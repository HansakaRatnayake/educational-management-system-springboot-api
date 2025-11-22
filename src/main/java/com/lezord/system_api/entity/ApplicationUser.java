package com.lezord.system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_user")
public class ApplicationUser {

    @Id
    @Column(name = "user_id", length = 80)
    private String userId;

    @Column(name = "username", length = 100, unique = true)
    private String username;

    @Column(name = "full_name", length = 50, nullable = false)
    private String fullName;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "otp", length = 4)
    private String otp;

    @Column(name = "password", length = 750, nullable = false)
    private String password;

    @Column(name = "oauth_user",columnDefinition = "TINYINT", updatable = false)
    private boolean oauthUser;

    @Column(name = "created_date", updatable = false)
    private Instant createdDate;

    @Column(name = "is_account_non_expired",columnDefinition = "TINYINT")
    private boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked",columnDefinition = "TINYINT")
    private boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired",columnDefinition = "TINYINT")
    private boolean isCredentialsNonExpired;

    @Column(name = "is_enabled",columnDefinition = "TINYINT")
    private boolean isEnabled;

    @OneToOne(mappedBy = "applicationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationUserAvatar applicationUserAvatar;

    @OneToOne(mappedBy = "applicationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Student student;

    @OneToOne(mappedBy = "applicationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Instructor instructor;

    @OneToOne(mappedBy = "applicationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Admin admin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_has_user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles;

    @OneToOne(mappedBy = "applicationUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private SuccessStory successStory;


}
