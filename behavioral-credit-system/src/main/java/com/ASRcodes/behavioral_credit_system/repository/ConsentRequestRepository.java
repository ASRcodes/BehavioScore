package com.ASRcodes.behavioral_credit_system.repository;

import com.ASRcodes.behavioral_credit_system.entity.ConsentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ConsentRequestRepository extends JpaRepository<ConsentRequest, UUID> {

    // This naming convention tells Spring to generate:
    // SELECT * FROM consent_requests WHERE status = ? AND score IS NULL
    List<ConsentRequest> findByStatusAndScoreIsNull(String status);
}