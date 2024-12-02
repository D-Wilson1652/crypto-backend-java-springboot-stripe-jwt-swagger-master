package com.store.crypto.repository.membership;

import com.store.crypto.model.membership.UserMembership;
import com.store.crypto.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {

    Optional<UserMembership> findByStripeCustomerId(String stripeCustomerId);

    Optional<UserMembership> findBySubscriptionId(String subscriptionId);

    Optional<UserMembership> findByUser(User user);
}
