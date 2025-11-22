package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_user_avatar")
public class ApplicationUserAvatar {

    @Id
    @Column(name = "property_id", length = 75, nullable = false, unique = true)
    private String propertyId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Lob
    @Column(name = "directory", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] directory;

    @Lob
    @Column(name = "file_name", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] fileName;

    @Lob
    @Column(name = "hash", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] hash;

    @Lob
    @Column(name = "resource_url",columnDefinition = "LONGBLOB")
    private byte[] resourceUrl;

    @OneToOne
    @JoinColumn(name = "application_user_id", referencedColumnName = "user_id")
    private ApplicationUser applicationUser;
}
