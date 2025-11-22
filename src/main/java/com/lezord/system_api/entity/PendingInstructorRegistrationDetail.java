package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pending_instructor_registration_detail")
public class PendingInstructorRegistrationDetail {

    @Id
    private String propertyId;

    @Column(name = "username", length = 100, unique = true)
    private String username;

    @Column(name = "full_name", length = 50, nullable = false)
    private String fullName;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "password", length = 750, nullable = false)
    private String password;

    @Column(name = "request_date", updatable = false)
    private Instant requestDate;

}
