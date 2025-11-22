package com.lezord.system_api.entity;

import com.lezord.system_api.entity.enums.GetInTouchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "get_in_touch")
public class GetInTouch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id", length = 80)
    private String propertyId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "phone", columnDefinition = "TEXT")
    private String phone;

    @Column(name = "active_status", columnDefinition = "TINYINT")
    private boolean activeStatus;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GetInTouchStatus status;

    @Column(name =  "created_at")
    private Instant createdAt;


}
