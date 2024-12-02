package com.store.crypto.controller.membership;

import com.store.crypto.config.stripe.StripeConfig;
import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.dto.payment.PaymentRequest;
import com.store.crypto.model.user.User;
import com.store.crypto.service.membership.MembershipDetailsService;
import com.store.crypto.utils.SessionUtils;
import com.store.crypto.utils.StripeUtil;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PriceListParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/membership")
public class MembershipController {

    private final StripeConfig stripeConfig;

    private final SessionUtils sessionUtils;

    private final MembershipDetailsService membershipDetailsService;

    @GetMapping("/list")
    public ResponseEntity<Object> list() {
        return membershipDetailsService.listAll();
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/user/details")
    public ResponseEntity<Object> getUserMembershipDetails() {
        return membershipDetailsService.getUserMembershipDetails();
    }

    @GetMapping("/payment/success")
    public ResponseEntity<Object> success(@RequestParam("session_id") String sessionId) {
        try {
            // Initialize Stripe API key
            Stripe.apiKey = stripeConfig.getSecretKey();

            Session session = Session.retrieve(sessionId);
            // Retrieve subscription details from the session
            String subscriptionId = session.getSubscription();
            Subscription subscription = Subscription.retrieve(subscriptionId);

            // Retrieve customer details
            String customerId = session.getCustomer();
            Customer customer = Customer.retrieve(customerId);

            // Extract required customer details
            String customerEmail = customer.getEmail();
            String customerName = customer.getName();

            // Payment details can be retrieved from subscription
            String paymentMethodId = subscription.getDefaultPaymentMethod();
            String currency = subscription.getCurrency();
//            long amount = subscription.eg().getAmount();
            // Log relevant details
            log.info("Payment successful for session: {}", session.toJson());
            log.info("Subscription details: {}", subscription.toJson());

            return ResponseEntity.ok("Payment was successful.");
        } catch (StripeException e) {
            log.error("Error retrieving session: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the payment.");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/payment/status")
    public ResponseEntity<Object> handlePaymentFailure(@RequestParam("session_id") String sessionId) {
        GenericResponse response = new GenericResponse();
        try {
            // Retrieve the session to get payment details
            Session session = Session.retrieve(sessionId);
            log.error("Checking payment status for session: {}, with customer: {}", session.getId(), session.getCustomer());

            // Check the payment status of the session
            String paymentStatus = session.getPaymentStatus(); // Returns "paid", "unpaid", or "no_payment_required"

            if ("paid".equals(paymentStatus)) {
                // If payment was completed successfully, don't allow access to the cancel route
                log.error("Payment was already successful. You can't cancel this payment.");
                response.setMessage("Payment was already successful. You can't cancel this payment.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ("unpaid".equals(paymentStatus)) {
                // If payment was not completed, it is safe to allow cancellation
                log.error("Payment was not successful. You can try again or contact support for assistance.");
                response.setMessage("Payment was not successful. You can try again or contact support for assistance.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if ("no_payment_required".equals(paymentStatus)) {
                // No payment was needed for this session, treat accordingly
                log.error("No payment was required for this session.");
                response.setMessage("No payment was required for this session.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // Handle other cases or statuses
                response.setMessage("An error occurred. Please contact support.");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (StripeException e) {
            // Log and handle Stripe exception
            log.error("Error retrieving session in handlePaymentFailure: {}", e.getMessage());
            response.setMessage("An error occurred. Please contact support.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/subscriptions/new")
    ResponseEntity<Object> newSubscription(@RequestBody PaymentRequest requestDTO) {
        GenericResponse response = new GenericResponse();
        try {
            // Initialize Stripe API key
            Stripe.apiKey = stripeConfig.getSecretKey();
            // Fetch the user from session.
            User loggedInUser = sessionUtils.getLoggedInUser();
            // Start by finding existing customer record from Stripe or creating a new one if needed
            Customer customer = StripeUtil.findOrCreateCustomer(loggedInUser.getEmail(), loggedInUser.getFullName());
            log.info("Customer ID: {}", customer.getId());
            // Retrieve the membership
            Product membership = Product.retrieve(requestDTO.getProductId());
            log.info("Product Name: {}", membership.getName());

            // Get the membership images from the Stripe catalog (if any exist)
            List<String> productImages = membership.getImages();
            String productImageUrl = !productImages.isEmpty() ? productImages.get(0) : "http://13.50.169.161:3000/images/pricing/pricing-card/gold-plan.png"; // Get the first image if available

            // Retrieve the associated price(s) for the membership
            PriceListParams priceListParams = PriceListParams.builder()
                    .setProduct(membership.getId())
                    .build();

            List<Price> prices = Price.list(priceListParams).getData();

            // Next, create a checkout session by adding the details of the checkout
            SessionCreateParams.Builder paramsBuilder =
                    SessionCreateParams.builder()
                            // For subscriptions, you need to set the mode as subscription
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .setCustomer(customer.getId())
                            .setSuccessUrl(stripeConfig.getClientBaseFrontendUrl() + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(stripeConfig.getClientBaseFrontendUrl() + "/payment/cancel?session_id={CHECKOUT_SESSION_ID}");

            SessionCreateParams.LineItem.PriceData.Recurring.Interval interval = membershipDetailsService.getInterval(prices);

            // Add the line item to the checkout session
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .putMetadata("app_id", membership.getId())
                                                            .setName(membership.getName())
                                                            .setDescription(membership.getDescription())
                                                            .addImage(productImageUrl)
                                                            .build()
                                            )
                                            .setCurrency(prices.get(0).getCurrency())
                                            .setUnitAmountDecimal(prices.get(0).getUnitAmountDecimal())
                                            // For subscriptions, you need to provide the details on how often they would recur
                                            .setRecurring(
                                                    SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                                            .setInterval(interval).build())
                                            .build())
                            .build());
            Session session = Session.create(paramsBuilder.build());
            response.setData(session.getUrl());
            response.setMessage("Success");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Unexpected error while subscription of membership: {}", e.getMessage());
            response.setData(null);
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/billing-portal")
    public ResponseEntity<Object> customerPortalVerification(@RequestParam String email) throws StripeException {
        GenericResponse response = new GenericResponse();
        String loggedInUser = sessionUtils.getLoggedInUserUserName();

        if (!loggedInUser.equals(email)) {
            response.setData(null);
            response.setMessage("Customer portal verification failed. This is not the logged-in user.");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            // Fetch the Stripe customer associated with the logged-in user
            User user = sessionUtils.getLoggedInUser(); // Assuming you have a method to fetch user details
            Customer stripeCustomer = StripeUtil.findOrCreateCustomer(user.getEmail(), user.getFullName());

            // Create billing portal session
            com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
                    .setCustomer(stripeCustomer.getId())  // Use the customer ID
                    .setReturnUrl(stripeConfig.getClientBaseUrl())  // URL where the customer will be redirected after leaving the portal
                    .build();
            com.stripe.model.billingportal.Session billingPortalSession = com.stripe.model.billingportal.Session.create(params);

            // Return the billing portal URL to the frontend
            String billingPortalUrl = billingPortalSession.getUrl();
            response.setData(billingPortalUrl);
            response.setMessage("Customer portal verification successful. Redirect to billing portal.");
            response.setStatusCode(HttpStatus.OK.value());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/cancel")
    public ResponseEntity<Object> cancelSubscription() {
        return membershipDetailsService.cancelSubscription();
    }

    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String
                                                              payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received Stripe webhook with sign header: {}", sigHeader);
        String endpointSecret = stripeConfig.getWebhookSecretKey(); // Get from Stripe dashboard
        Event event;

        try {
            // Verify the signature of the webhook event
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            log.error("Invalid signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_method.attached":
                log.warn("Payment method attached for customer: {}", event.getId());
//                handleSubscriptionDeleted(event);
                break;
            case "customer.subscription.created":
                log.warn("Subscription created for customer: {}", event.getId());
//                handleSubscriptionDeleted(event);
                break;
            case "customer.subscription.updated":
                log.warn("Subscription updated for customer: {}", event.getId());
//                membershipDetailsService.handleSubscriptionUpdated(event);
                break;
            case "invoice.payment_succeeded":
                log.warn("Payment succeeded for event: {}", event.getId());
                membershipDetailsService.handleSubscriptionUpgrade(event);
                break;
            case "customer.subscription.deleted":
            case "customer.subscription.cancelled":
                log.warn("Subscription cancelled or deleted for customer: {}", event.getId());
//                membershipDetailsService.handleSubscriptionDeleted(event);
                break;

            default:
                // Unhandled event type
                log.warn("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook received");
    }


}
