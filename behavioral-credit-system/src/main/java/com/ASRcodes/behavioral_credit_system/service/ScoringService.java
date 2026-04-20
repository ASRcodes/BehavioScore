package com.ASRcodes.behavioral_credit_system.service;

import com.ASRcodes.behavioral_credit_system.entity.ConsentRequest;
import com.ASRcodes.behavioral_credit_system.repository.ConsentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    @Autowired
    private ConsentRequestRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelay = 3000)
    @Transactional // Added: Ensures a clean database session for each loop
    public void processApprovedRequests() {
        List<ConsentRequest> pending = repository.findByStatusAndScoreIsNull("APPROVED");

        for (ConsentRequest req : pending) {
            try {
                System.out.println("BehavioScore: Processing approved request for " + req.getUserPhone());

                Map<String, Object> requestPayload = Map.of(
                        "phone", req.getUserPhone(),
                        "behavioral_data", Map.of("rent_delay", 7, "salary_consistency", 0.6, "savings_ratio", 0.10)
                );

                String pythonUrl = "http://localhost:8000/predict";
                Map<String, Object> response = restTemplate.postForObject(pythonUrl, requestPayload, Map.class);

                if (response != null && response.containsKey("results")) {
                    Map<String, Object> results = (Map<String, Object>) response.get("results");

                    req.setScore((Integer) results.get("score"));
                    req.setAnalysisResults(response);

                    repository.save(req);
                    System.out.println("BehavioScore: Analysis successfully saved for " + req.getUserPhone());
                }
            } catch (Exception e) {
                // This will catch ML connection issues without killing the DB transaction
                System.err.println("ML Engine Connection Error: " + e.getMessage());
            }
        }
    }
}