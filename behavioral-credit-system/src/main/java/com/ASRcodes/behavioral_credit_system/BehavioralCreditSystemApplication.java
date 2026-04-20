package com.ASRcodes.behavioral_credit_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // ADD THIS

@SpringBootApplication
@EnableScheduling // ADD THIS - This is the "Master Switch"
public class BehavioralCreditSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BehavioralCreditSystemApplication.class, args);
    }
}