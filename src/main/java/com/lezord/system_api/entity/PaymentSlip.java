package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_slip")
public class PaymentSlip {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Lob
    @Column(name = "directory", nullable = false)
    private byte[] directory;

    @Lob
    @Column(name = "file_name", nullable = false)
    private byte[] fileName;

    @Lob
    @Column(name = "hash", nullable = false)
    private byte[] hash;

    @Lob
    @Column(name = "resource_url")
    private byte[] resourceUrl;

    private boolean isVerified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "property_id")
    private Payment payment;
}
