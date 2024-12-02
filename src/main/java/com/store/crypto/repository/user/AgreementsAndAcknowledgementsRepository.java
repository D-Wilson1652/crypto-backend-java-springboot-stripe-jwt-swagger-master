package com.store.crypto.repository.user;

import com.store.crypto.model.user.AgreementsAndAcknowledgements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementsAndAcknowledgementsRepository extends JpaRepository<AgreementsAndAcknowledgements, Long> {
}
