package com.store.crypto.service.membership;

import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.dto.membership.UserMembershipDTO;
import com.store.crypto.model.membership.MembershipDetails;
import com.store.crypto.model.membership.UserMembership;
import com.store.crypto.model.user.User;
import com.store.crypto.repository.membership.MembershipDetailsRepository;
import com.store.crypto.repository.membership.UserMembershipRepository;
import com.store.crypto.repository.user.UserRepository;
import com.store.crypto.utils.SessionUtils;
import com.store.crypto.utils.StripeUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipDetailsService {
    private final MembershipDetailsRepository membershipDetailsRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final UserRepository userRepository;
    private final SessionUtils sessionUtils;

    public ResponseEntity<Object> cancelSubscription() {
        GenericResponse response = new GenericResponse();
        try {
            User loggedInUser = sessionUtils.getLoggedInUser();
            Optional<UserMembership> userMembership = userMembershipRepository.findByUser(loggedInUser);
            if (userMembership.isPresent()) {

                UserMembership membership = userMembership.get();
                if (membership.getSubscriptionId() != null) {
                    // Retrieve customer subscriptions
                    SubscriptionListParams params = SubscriptionListParams.builder()
                            .setCustomer(membership.getStripeCustomerId())
                            .build();
                    List<Subscription> subscriptions = Subscription.list(params).getData();
                    // Find and cancel the previous active/trialing subscription
                    for (Subscription subscription : subscriptions) {
                        if (("active".equals(subscription.getStatus()) || "trialing".equals(subscription.getStatus()))) {
                            subscription.cancel();
                            log.info("Previous active/trialing subscription canceled: {}", subscription.getId());
                            break;
                        }
                    }
//                    membership.setEndDate(Date.from(Instant.now()));
                    membership.setStatus("Cancelled");
                    membership.setLastUpdated(Date.from(Instant.now()));
                    userMembershipRepository.save(membership);
                    response.setData(null);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Subscription cancelled successfully");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setData(null);
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    response.setMessage("No subscription found on stripe.");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                response.setData(null);
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("No membership found in the database.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (StripeException e) {
            log.error("Error cancelling subscription: {}", e.getMessage());
            response.setData(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> listAll() {
        GenericResponse response = new GenericResponse();
        try {
            List<MembershipDetails> membershipDetails = membershipDetailsRepository.findAll();
            if (membershipDetails.isEmpty()) {
                response.setData(null);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("No membership records found");
            } else {
                response.setData(membershipDetails);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Success");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void handleSubscriptionUpgrade(Event event) {
        try {
            if (event.getDataObjectDeserializer().getObject().isEmpty()) {
                return;
            }

            Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().get();
            String customerId = (invoice.getCustomer() != null) ? invoice.getCustomer() : "unknown_customer";
            String newSubscriptionId = (invoice.getSubscription() != null) ? invoice.getSubscription() : "unknown_subscription";
            InvoiceLineItemCollection lineItems = (invoice.getLines() != null) ? invoice.getLines() : new InvoiceLineItemCollection();

            // Default values
            String productId = "unknown_product";
            Long amount = 0L;
            String currency = "unknown_currency";
            String planName = "unknown_plan";
            String interval = "unknown_interval";

            // Fetch the product ID and price details from the invoice's line items
            for (InvoiceLineItem lineItem : lineItems.getData()) {
                if (lineItem.getPrice() != null && lineItem.getPrice().getProduct() != null) {
                    productId = lineItem.getPrice().getProduct();
                    amount = (lineItem.getPrice().getUnitAmount() != null) ? lineItem.getPrice().getUnitAmount() : 0L;
                    currency = (lineItem.getPrice().getCurrency() != null) ? lineItem.getPrice().getCurrency() : "unknown_currency";
                    String priceId = lineItem.getPrice().getId();  // Get Price ID as well

                    try {
                        Product product = Product.retrieve(productId);
                        if (product != null) {
                            planName = (product.getName() != null) ? product.getName() : "unknown_plan";
                        }
                    } catch (StripeException e) {
                        log.error("Failed to retrieve product details for product ID {}: {}", productId, e.getMessage());
                    }

                    interval = (lineItem.getPrice().getRecurring() != null && lineItem.getPrice().getRecurring().getInterval() != null)
                            ? lineItem.getPrice().getRecurring().getInterval()
                            : "unknown_interval";

                    log.info("Product ID from invoice: {}", productId);
                    log.info("Price ID from invoice: {}", priceId);  // Log Price ID to check association
                    log.info("Product Name from invoice: {}", lineItem.getPrice().getProduct());
                    log.info("Subscription: {}", lineItem.getSubscription());
                }
            }

            // Retrieve customer subscriptions
            SubscriptionListParams params = SubscriptionListParams.builder()
                    .setCustomer(customerId)
                    .build();
            List<Subscription> subscriptions = Subscription.list(params).getData();

            // Find and cancel the previous active/trialing subscription
            for (Subscription subscription : subscriptions) {
                if (!subscription.getId().equals(newSubscriptionId) &&
                        ("active".equals(subscription.getStatus()) || "trialing".equals(subscription.getStatus()))) {

                    subscription.cancel();
                    log.info("Previous active/trialing subscription canceled: {}", subscription.getId());
                    break;
                }

                // Get subscription start and end dates
                Long periodStart = subscription.getCurrentPeriodStart();
                Long periodEnd = subscription.getCurrentPeriodEnd();

                if (periodStart != null && periodEnd != null) {
                    log.info("Subscription Start Date: {}", Instant.ofEpochSecond(periodStart));
                    log.info("Subscription End Date: {}", Instant.ofEpochSecond(periodEnd));

                    // Save or update the membership details in the database
                    Optional<UserMembership> existingMembership = userMembershipRepository.findByStripeCustomerId(customerId);
                    UserMembership userMembership;

                    // to find the listing limit of the membership, so we can add that to the user limit.
                    Optional<MembershipDetails> membershipDetails = membershipDetailsRepository.findAllByNameEqualsIgnoreCaseAndPlanIntervalEqualsIgnoreCase(planName, interval);
                    if (existingMembership.isPresent()) {
                        userMembership = existingMembership.get();
                        //Increase the limit accordingly.
                        if (membershipDetails.isPresent()) {
                            userMembership.setListingLimit(userMembership.getListingLimit() + membershipDetails.get().getListingLimit());
                        } else {
                            userMembership.setListingLimit(userMembership.getListingLimit() + 1);
                            log.error("Membership details not found in the database for product ID: {}, and for the customer ID: {}", productId, customerId);
                        }
                    } else {
                        userMembership = new UserMembership();
                        userMembership.setStripeCustomerId(customerId);
                        if (membershipDetails.isPresent()) {
                            userMembership.setListingLimit(membershipDetails.get().getListingLimit());
                        } else {
                            userMembership.setListingLimit(1);
                            log.error("Membership details not found in the database for product ID: {}, and for the customer ID: {}", productId, customerId);
                        }
                        Optional<User> user = userRepository.findByEmail(StripeUtil.getCustomerEmail(customerId));
                        if (user.isPresent()) {
                            userMembership.setUser(user.get());
                        } else {
                            log.error("User not found for customer: {}", customerId);
                            log.error("Stripe Invoice ID is: {}", invoice.getId());
//                            continue; // Skip saving the membership details if the user isn't found
                        }
                    }
                    userMembership.setLastUpdated(new Date());
                    // Set or update membership details
                    userMembership.setMembershipName(planName);
                    userMembership.setPrice(amount);
                    userMembership.setCurrency(currency);
                    userMembership.setStartDate(Date.from(Instant.ofEpochSecond(periodStart)));
                    userMembership.setEndDate(Date.from(Instant.ofEpochSecond(periodEnd)));
                    userMembership.setStatus(subscription.getStatus());
                    userMembership.setSubscriptionId(newSubscriptionId);
                    userMembership.setMembershipInterval(interval);

                    // Save or update the membership in the database
                    userMembershipRepository.save(userMembership);

                    log.info("Membership details updated for customer: {}", customerId);
                }
            }
        } catch (StripeException e) {
            log.error("Failed to handle subscription upgrade: {}", e.getMessage());
        }
    }

    public void handleSubscriptionUpdated(Event event) {
        try {
            if (event.getDataObjectDeserializer().getObject().isEmpty()) {
                return;
            }

            Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().get();
            String customerId = subscription.getCustomer();

            // Get the current period details from the updated subscription

            UserMembership existingUserMembership = userMembershipRepository.findByStripeCustomerId(customerId).orElse(null);
            if (existingUserMembership != null) {

                //TODO: NEED TO HANDLE THE RENEWAL CASE.
                // ALL THE 3 OTHER CASES ARE HANDLED.
                // 1. NEW SUBSCRIPTION, 2. CANCELLED, 3. UPDATING PLAN,
                String membershipStatus = existingUserMembership.getStatus();

                if (membershipStatus.equalsIgnoreCase("canceled")) {
                    //If membership was in cancelled state that means it's new subscription or a renewal.
                }
            }


            // If the subscription is canceled
            if (subscription.getCancellationDetails() != null
                    && subscription.getCancellationDetails().getReason() != null
                    && subscription.getCancellationDetails().getReason().equals("cancellation_requested")) {
                log.info("Subscription canceled for customer: {}", customerId);

                // Retrieve all subscriptions for the customer
                SubscriptionListParams params = SubscriptionListParams.builder()
                        .setCustomer(customerId)
                        .build();
                List<Subscription> subscriptions = Subscription.list(params).getData();

                if (subscriptions.size() == 1 && subscriptions.get(0).getCanceledAt() != null) {
                    log.info("Subscription already canceled from stripe end: {}", subscriptions.get(0).getCanceledAt());
                    // Mark the user as having no active membership in the database
                    Optional<UserMembership> membership = userMembershipRepository.findByStripeCustomerId(customerId);
                    if (membership.isPresent()) {
                        UserMembership userMembership = membership.get();
                        userMembership.setStatus("canceled");
                        userMembershipRepository.save(userMembership);
                        log.info("Updated membership status to canceled for customer: {}", customerId);
                    } else {
                        log.error("User membership not found for customer: {}", customerId);
                    }
                }
            }
        } catch (StripeException e) {
            log.error("Error handling subscription updated event: {}", e.getMessage());
        }
    }

    public void handleSubscriptionDeleted(Event event) {
        try {
            if (event.getDataObjectDeserializer().getObject().isEmpty()) {
                return;
            }
            Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().get();
            String customerId = subscription.getCustomer();
            log.info("Handling subscription deletion for customer: {}", customerId);
            Optional<UserMembership> membership = userMembershipRepository.findByStripeCustomerId(customerId);
            if (membership.isPresent()) {
                UserMembership userMembership = membership.get();
                userMembership.setStatus("deleted");
                userMembershipRepository.save(userMembership);
                log.info("Updated membership status to deleted for customer: {}", customerId);
            } else {
                log.error("User membership not found for customer: {}", customerId);
            }
        } catch (Exception e) {
            log.error("Error handling subscription deleted event: {}", e.getMessage());
        }
    }

    public SessionCreateParams.LineItem.PriceData.Recurring.Interval getInterval(List<Price> prices) {
        Price.Recurring recurring = prices.get(0).getRecurring();
        // Map Stripe's recurring interval to the corresponding enum used by SessionCreateParams
        SessionCreateParams.LineItem.PriceData.Recurring.Interval interval;

        if (recurring.getInterval().equalsIgnoreCase("month")) {
            interval = SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH;
        } else if (recurring.getInterval().equalsIgnoreCase("year")) {
            interval = SessionCreateParams.LineItem.PriceData.Recurring.Interval.YEAR;
        } else {
            throw new IllegalArgumentException("Unsupported interval: " + recurring.getInterval());
        }
        return interval;
    }

    public ResponseEntity<Object> getUserMembershipDetails() {
        GenericResponse response = new GenericResponse();
        try {
            UserMembershipDTO userMembershipDTO = new UserMembershipDTO();
            User loggedInUser = sessionUtils.getLoggedInUser();

            // Step 1: Check if membership exists in the database
            Optional<UserMembership> userMembership = userMembershipRepository.findByUser(loggedInUser);

            if (userMembership.isPresent()) {
                // Membership found in the database
                userMembershipDTO.setMembershipName(userMembership.get().getMembershipName());
                userMembershipDTO.setEndDate(userMembership.get().getEndDate());
                userMembershipDTO.setActive(userMembership.get().getStatus().equalsIgnoreCase("Active"));
                response.setData(userMembershipDTO);
                response.setMessage("Success");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // Step 2: Fetch from Stripe if not found in the database
                String stripeCustomerId = Objects.requireNonNull(StripeUtil.findCustomerByEmail(loggedInUser.getEmail())).getId();
                if (stripeCustomerId != null && !stripeCustomerId.isEmpty()) {
                    SubscriptionListParams params = SubscriptionListParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setLimit(1L)
                            .build();
                    List<Subscription> subscriptions = Subscription.list(params).getData();

                    if (!subscriptions.isEmpty()) {
                        Subscription subscription = subscriptions.get(0); // Assuming one subscription per customer
                        String planName = "unknown_plan";
                        String interval = "unknown_interval";
                        Long amount = 0L;
                        String currency = "unknown_currency";

                        // Extract product, plan, price, etc. from the subscription
                        if (subscription.getItems() != null && !subscription.getItems().getData().isEmpty()) {
                            SubscriptionItem item = subscription.getItems().getData().get(0);
                            String productId = item.getPrice().getProduct();
                            amount = item.getPrice().getUnitAmount();
                            currency = item.getPrice().getCurrency();
                            interval = item.getPrice().getRecurring().getInterval();

                            try {
                                Product product = Product.retrieve(productId);
                                planName = product.getName();
                            } catch (StripeException e) {
                                log.error("Failed to retrieve product details from Stripe: {}", e.getMessage());
                            }
                        }

                        // Step 3: Insert new membership details into the database
                        UserMembership newMembership = new UserMembership();
                        newMembership.setUser(loggedInUser);
                        newMembership.setStripeCustomerId(stripeCustomerId);
                        newMembership.setMembershipName(planName);
                        newMembership.setPrice(amount);
                        newMembership.setCurrency(currency);
                        newMembership.setStartDate(Date.from(Instant.ofEpochSecond(subscription.getCurrentPeriodStart())));
                        newMembership.setEndDate(Date.from(Instant.ofEpochSecond(subscription.getCurrentPeriodEnd())));
                        newMembership.setStatus(subscription.getStatus());
                        newMembership.setSubscriptionId(subscription.getId());
                        newMembership.setMembershipInterval(interval);

                        Optional<MembershipDetails> membershipDetails = membershipDetailsRepository.findAllByNameEqualsIgnoreCaseAndPlanIntervalEqualsIgnoreCase(planName, interval);
                        membershipDetails.ifPresent(details -> newMembership.setListingLimit(details.getListingLimit()));
                        userMembershipRepository.save(newMembership);
                        // Return the response with membership details
                        userMembershipDTO.setMembershipName(planName);
                        userMembershipDTO.setEndDate(newMembership.getEndDate());
                        userMembershipDTO.setActive(newMembership.getStatus().equalsIgnoreCase("Active"));
                        response.setData(userMembershipDTO);
                        response.setMessage("Membership fetched from Stripe and saved to the database.");
                        response.setStatusCode(HttpStatus.OK.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setData(null);
                        response.setMessage("User membership not found on Stripe.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                    }
                } else {
                    response.setData(null);
                    response.setMessage("User Stripe customer ID not found.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            }
        } catch (StripeException e) {
            log.error("Stripe API error: {}", e.getMessage());
            response.setData(null);
            response.setMessage("Error retrieving membership from Stripe.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
