package com.store.crypto.repository.membership;

import com.store.crypto.model.membership.MembershipDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipDetailsRepository extends JpaRepository<MembershipDetails, Long> {

    Optional<MembershipDetails> findByNameEqualsIgnoreCase(String name);

    Optional<MembershipDetails> findByStripeProductId(String stripeProductId);

    boolean existsByName(String name);

    Optional<MembershipDetails> findAllByNameEqualsIgnoreCaseAndPlanIntervalEqualsIgnoreCase(String name, String interval);
}
