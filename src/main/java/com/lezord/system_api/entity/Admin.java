package com.lezord.system_api.entity;


import com.lezord.system_api.entity.core.Address;
import com.lezord.system_api.entity.core.EmploymentDetails;
import com.lezord.system_api.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "dob", columnDefinition = "DATE")
    private LocalDate dob;

    @Column(name = "nic", length = 20)
    private String nic;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 45)
    private Gender gender;

    @Embedded
    private Address address;

    @Embedded
    private EmploymentDetails employmentDetails;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private ApplicationUser applicationUser;

}
