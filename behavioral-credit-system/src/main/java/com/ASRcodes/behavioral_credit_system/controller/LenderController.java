package com.ASRcodes.behavioral_credit_system.controller;

import com.ASRcodes.behavioral_credit_system.entity.ConsentRequest;
import com.ASRcodes.behavioral_credit_system.repository.ConsentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/lender")
@CrossOrigin(origins = "*") // Allows your website to talk to this API
public class LenderController {

    @Autowired
    private ConsentRequestRepository repository;

    @PostMapping("/request-score")
    public ConsentRequest createRequest(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");

        ConsentRequest newRequest = new ConsentRequest();
        newRequest.setUserPhone(phone);
        newRequest.setStatus("PENDING");

        return repository.save(newRequest);
    }
}