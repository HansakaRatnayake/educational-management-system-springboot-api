package com.lezord.system_api.entity;


import com.lezord.system_api.entity.enums.SuccessStoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "success_story")
public class SuccessStory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id", length = 80)
    private String propertyId;

    @Column(name = "title")
    private String title;

    @Column(name = "rating")
    private int rating;

    @Column(name = "story", columnDefinition = "TEXT")
    private String story;

    @Column(name = "active_status", columnDefinition = "TINYINT")
    private boolean activeStatus;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SuccessStoryStatus status;

    @Column(name =  "created_at")
    private Instant createdAt;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private ApplicationUser applicationUser;

}
