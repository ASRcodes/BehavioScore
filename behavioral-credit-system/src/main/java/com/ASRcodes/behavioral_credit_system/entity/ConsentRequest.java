package com.ASRcodes.behavioral_credit_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;
import java.util.Map;

@Entity
@Table(name = "consent_requests")
@Data
public class ConsentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Experiment 4: Bean Validation
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone format")
    @Column(name = "user_phone")
    private String userPhone;

    private String status = "PENDING";

    // New: Final Credit Score
    private Integer score;

    // New: Detailed Behavioral Metrics from Python (Stored as JSONB in Postgres)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_results", columnDefinition = "jsonb")
    private Map<String, Object> analysisResults;

    @Column(name = "created_at")
    private java.time.OffsetDateTime createdAt = java.time.OffsetDateTime.now();
}