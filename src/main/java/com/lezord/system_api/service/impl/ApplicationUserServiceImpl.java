package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestApplicationUserByAdminDTO;
import com.lezord.system_api.dto.request.RequestApplicationUserDTO;
import com.lezord.system_api.dto.request.RequestApplicationUserPasswordResetDTO;
import com.lezord.system_api.dto.request.RequestUpdateApplicationUserDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserRoleDTO;
import com.lezord.system_api.dto.response.paginate.PaginateApplicationUserDTO;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.core.EmploymentDetails;
import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.config.JwtConfig;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.security.SupportSpringApplicationUser;
import com.lezord.system_api.service.ApplicationUserService;
import com.lezord.system_api.service.EmailService;
import com.lezord.system_api.util.FileDataHandler;
import com.lezord.system_api.util.OTPGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;

import static com.lezord.system_api.security.ApplicationUserRole.ADMIN;
import static com.lezord.system_api.security.ApplicationUserRole.STUDENT;
import static com.lezord.system_api.security.ApplicationUserRole.TRAINER;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserAvatarRepository applicationUserAvatarRepository;
    private final InstructorRepository instructorRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final OTPGenerator otpGenerator;
    private final EmailService emailService;
    private final FileDataHandler fileDataHandler;

    @Value("${admin.app.origin}")
    private String adminOrigin;


    @Override
    public void create(RequestApplicationUserDTO dto) {
        if ( applicationUserRepository.findByUsername(dto.getUsername()).isPresent()) throw new DuplicateEntryException(String.format("user with email (%s) is exists", dto.getUsername()));
        if ( applicationUserRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) throw new DuplicateEntryException(String.format("user with phone (%s) is exists", dto.getPhoneNumber()));
        ApplicationUser savedUser = applicationUserRepository.save(createApplicationUser(dto));

        if (dto.getRole().equals("STUDENT")) {
            studentRepository.save(
                    Student.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .applicationUser(savedUser)
                            .displayName(dto.getFullName())
                            .activeState(true)
                            .email(dto.getUsername())
                            .build()
            );
        }



    }

    @Override
    public void update(RequestUpdateApplicationUserDTO dto, String userId) {
        ApplicationUser selectedUser = applicationUserRepository.findById(userId).orElseThrow(() -> new EntryNotFoundException("User not found"));

        Optional<ApplicationUser> applicationUser = applicationUserRepository.findByPhoneNumber(dto.getPhoneNumber());

        if (applicationUser.isPresent() && !applicationUser.get().getUserId().equals(userId) )
            throw new DuplicateEntryException(String.format("user with phone (%s) is exists", dto.getPhoneNumber()));


        selectedUser.setFullName(dto.getFullName());
        selectedUser.setPhoneNumber(dto.getPhoneNumber());
        selectedUser.setCountryCode(dto.getCountryCode());

        applicationUserRepository.save(selectedUser);
    }

    @Override
    public void initializeAdmin() throws IOException {

        Set<UserRole> selectedRoles = new HashSet<>();
        selectedRoles.add(userRoleRepository.findByRoleName("ADMIN").orElseThrow(() -> new EntryNotFoundException("admin role not found")));

        List<ApplicationUser> users = new ArrayList<>();

        if (applicationUserRepository.findByUsername("info@nozomi.lk").isPresent() && applicationUserRepository.findByUsername("ceo@emedi.lk").isPresent()) return;

        if (applicationUserRepository.findByUsername("info@nozomi.lk").isEmpty()) {
            users.add(
                    ApplicationUser.builder()
                            .userId(UUID.randomUUID().toString())
                            .username("info@nozomi.lk")
                            .password(passwordEncoder.encode("Nozomi@1234"))
                            .fullName("Nozomi International")
                            .phoneNumber("0715325640")
                            .countryCode("+94715325640")
                            .createdDate(Instant.now())
                            .roles(selectedRoles)
                            .isAccountNonExpired(true)
                            .isAccountNonLocked(true)
                            .isCredentialsNonExpired(true)
                            .isEnabled(true)
                            .build()
            );
        }
        if (applicationUserRepository.findByUsername("ceo@emedi.lk").isEmpty()) {
            users.add(
                    ApplicationUser.builder()
                            .userId(UUID.randomUUID().toString())
                            .username("ceo@emedi.lk")
                            .password(passwordEncoder.encode("Dilan@1234"))
                            .fullName("Dilan Sandaruwan")
                            .phoneNumber("09073491733")
                            .countryCode("+819073491733")
                            .createdDate(Instant.now())
                            .roles(selectedRoles)
                            .isAccountNonExpired(true)
                            .isAccountNonLocked(true)
                            .isCredentialsNonExpired(true)
                            .isEnabled(true)
                            .build()
            );
        }

        List<ApplicationUser> savedUsers = applicationUserRepository.saveAll(users);


        savedUsers.forEach(user -> {
            adminRepository.save(
                    Admin.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .displayName(user.getFullName())
                            .applicationUser(user)
                            .email(user.getUsername())
                            .employmentDetails(EmploymentDetails.builder().activeStatus(true).build())
                            .build()
            );
        });



    }

    @Override
    public ApplicationUser findById(String userId) {
        return applicationUserRepository.findById(userId).orElseThrow(() -> new EntryNotFoundException(String.format("user %s not found", userId)));

    }

    @Override
    public PaginateApplicationUserDTO findAll(String searchText, int pageNumber, int pageSize) {
        Page<ApplicationUser> applicationUsers = applicationUserRepository.searchUsers(searchText, PageRequest.of(pageNumber, pageSize));

        return PaginateApplicationUserDTO.builder()
                .count(applicationUsers.getTotalElements())
                .dataList(applicationUsers.getContent().stream().map(this::mapToResponseApplicationUserDTO).collect(Collectors.toList()))
                .build();
    }


    @Override
    public void changeStatus(boolean status, String username, String tokenHeader) {

    }

    @Override
    public ResponseApplicationUserDTO findData(String tokenHeader) {
        String realToken = tokenHeader.replace(jwtConfig.getTokenPrefix(), "");

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(realToken);

        String username = claims.getBody().getSubject();

        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username).orElseThrow(() -> new EntryNotFoundException(String.format("username %s not found", username)));

        return ResponseApplicationUserDTO.builder()
                .username(applicationUser.getUsername())
                .fullName(applicationUser.getFullName())
                .roles(applicationUser.getRoles().stream().map(userRole -> ResponseApplicationUserRoleDTO.builder().role(userRole.getRoleName()).build()).toList())
                .build();
    }

    @Override
    public void forgotPasswordSendVerificationCode(String email) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(email).orElseThrow(() -> new EntryNotFoundException(String.format("Unable to find any users associated with the provided email address %s.  Please check your credentials and try again.", email)));

        String code = otpGenerator.generateOTP(4);

        applicationUser.setOtp(code);

        applicationUserRepository.save(applicationUser);

        emailService.sendPasswordResetVerificationCode(applicationUser.getUsername(), "Verify Your Email Address for System Access", code);


    }

    @Override
    public boolean verifyReset(String otp, String email) {
            if (email == null || otp == null || otp.isEmpty()) return false;
            ApplicationUser selectedApplicationUser = applicationUserRepository.findByUsername(email)
                    .orElseThrow(() -> new EntryNotFoundException("Unable to find any users associated with the provided email address."));

            return selectedApplicationUser.getOtp() != null && selectedApplicationUser.getOtp().equals(otp);
    }


    @Override
    public boolean passwordReset(RequestApplicationUserPasswordResetDTO dto) {
        Optional<ApplicationUser> selectedUserObj = applicationUserRepository.findByUsername(dto.getEmail());
        if (selectedUserObj.isPresent()) {
            ApplicationUser systemUser = selectedUserObj.get();
            if (selectedUserObj.get().getOtp().equals(dto.getCode())) {
                systemUser.setPassword(passwordEncoder.encode(dto.getPassword()));
                applicationUserRepository.save(systemUser);
                return true;
            }

            throw new BadRequestException("Something went wrong with the OTP, Please try again");

        }
        throw new EntryNotFoundException("Unable to find any users associated with the provided email address.");
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username).orElseThrow(() -> new EntryNotFoundException(String.format("username %s not found", username)));

        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();

        for (UserRole u : applicationUser.getRoles()) {
            if (u.getRoleName().equals("ADMIN")) {
                grantedAuthorities.addAll(ADMIN.grantedAuthorities());
            }
            if (u.getRoleName().equals("TRAINER")) {
                grantedAuthorities.addAll(TRAINER.grantedAuthorities());
            }
            if (u.getRoleName().equals("STUDENT")) {
                grantedAuthorities.addAll(STUDENT.grantedAuthorities());
            }
        }

        return SupportSpringApplicationUser.builder()
                .userId(applicationUser.getUserId())
                .username(applicationUser.getUsername())
                .password(applicationUser.getPassword())
                .isAccountNonLocked(applicationUser.isAccountNonLocked())
                .isAccountNonExpired(applicationUser.isAccountNonExpired())
                .isCredentialsNonExpired(applicationUser.isCredentialsNonExpired())
                .isEnabled(applicationUser.isEnabled())
                .authorities(grantedAuthorities)
                .build();
    }

    @Override
    public void delete(String userId) {
        ApplicationUser applicationUser = applicationUserRepository.findById(userId).orElseThrow(() -> new EntryNotFoundException(String.format("username %s not found", userId)));
        applicationUserRepository.delete(applicationUser);
    }

    @Override
    public ResponseApplicationUserDTO getApplicationUserByUsername(String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username).orElseThrow(() -> new EntryNotFoundException(String.format("username %s not found", username)));
        Optional<ApplicationUserAvatar> applicationUserAvatar = applicationUserAvatarRepository.findApplicationUserAvatarByApplicationUserUserId(applicationUser.getUserId());
        return ResponseApplicationUserDTO.builder()
                .userId(applicationUser.getUserId())
                .username(applicationUser.getUsername())
                .fullName(applicationUser.getFullName())
                .avatarUrl(applicationUserAvatar.map(userAvatar -> fileDataHandler.byteArrayToString(userAvatar.getResourceUrl())).orElse(null))
                .roles(applicationUser.getRoles().stream().map(userRole -> ResponseApplicationUserRoleDTO.builder().role(userRole.getRoleName()).build()).toList())
                .build();
    }

    @Override
    public ResponseApplicationUserDTO getApplicationUserByUsername(OAuth2User oAuth2User) {

        String username = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");

        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username).orElseThrow(() -> new EntryNotFoundException(String.format("username %s not found", username)));
        return ResponseApplicationUserDTO.builder()
                .username(applicationUser.getUsername())
                .fullName(applicationUser.getFullName())
                .avatarUrl(picture)
                .roles(applicationUser.getRoles().stream().map(userRole -> ResponseApplicationUserRoleDTO.builder().role(userRole.getRoleName()).build()).toList())
                .build();
    }

    @Override
    public ApplicationUser processOAuthPostLogin(OAuth2User oAuth2User) {

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRoleRepository.findByRoleName("STUDENT").orElseThrow(() -> new EntryNotFoundException("role %STUDENT not found")));


        return applicationUserRepository.findByUsername(email)
                .orElseGet(() -> {
                    ApplicationUser newUser = ApplicationUser.builder()
                            .userId(UUID.randomUUID().toString())
                            .username(email)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .fullName(name)
                            .oauthUser(true)
                            .createdDate(Instant.now())
                            .roles(userRoles)
                            .isAccountNonExpired(true)
                            .isAccountNonLocked(true)
                            .isCredentialsNonExpired(true)
                            .isEnabled(true)
                            .build();

                    return applicationUserRepository.save(newUser);

                });
    }

    @Override
    public List<ResponseApplicationUserRoleDTO> findUserRoleByUsername(String username) {
        applicationUserRepository.findByUsername(username).orElseThrow(() -> new EntryNotFoundException("User not found"));

        return applicationUserRepository.findRolesByUsername(username).stream().map(
                role -> ResponseApplicationUserRoleDTO.builder()
                .propertyId(role.getRoleId())
                .role(role.getRoleName())
                .build()
        ).collect(Collectors.toList());


    }

    @Override
    public void createUserByAdmin(RequestApplicationUserByAdminDTO dto) {
        if ( applicationUserRepository.findByUsername(dto.getUsername()).isPresent()) throw new DuplicateEntryException(String.format("user with email (%s) is exists", dto.getUsername()));
        if ( applicationUserRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) throw new DuplicateEntryException(String.format("user with phone (%s) is exists", dto.getPhoneNumber()));

        ApplicationUser savedUser = applicationUserRepository.save(createApplicationUser(dto));

        if (dto.getRole().equals("STUDENT")) {
            studentRepository.save(
                    Student.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .applicationUser(savedUser)
                            .displayName(savedUser.getFullName())
                            .activeState(true)
                            .email(savedUser.getUsername())
                            .build()
            );
        }
        if (dto.getRole().equals("TRAINER")) {
            instructorRepository.save(
                    Instructor.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .displayName(savedUser.getFullName())
                            .applicationUser(savedUser)
                            .email(savedUser.getUsername())
                            .employmentDetails(EmploymentDetails.builder().activeStatus(true).dateJoined(java.time.LocalDate.now()).build())
                            .build()
            );
        }

        if (dto.getRole().equals("ADMIN")) {
            adminRepository.save(
                    Admin.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .displayName(savedUser.getFullName())
                            .applicationUser(savedUser)
                            .email(savedUser.getUsername())
                            .employmentDetails(EmploymentDetails.builder().activeStatus(true).build())
                            .build()
            );
        }
//        if (dto.getRole().equals("ADMIN")) {}
    }

    @Override
    public List<ResponseApplicationUserDTO> findAllByRole(String role) {
        UserRole userRole = userRoleRepository.findUserRoleByRoleName(role).orElseThrow(() -> new EntryNotFoundException("role ADMIN not found"));
        return applicationUserRepository.findApplicationUserByRoles(Set.of(userRole)).stream().map(this::mapToResponseApplicationUserDTO).toList();
    }

    @Override
    public void changeRoleForApplicationUser(String roleId, String userId, boolean active) {
        ApplicationUser user = applicationUserRepository.findById(userId)
                .orElseThrow(() -> new EntryNotFoundException("User not found with id: " + userId));

        UserRole role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntryNotFoundException("Role not found with id: " + roleId));


            if (active) {
                user.getRoles().add(role);
                applicationUserRepository.save(user);
            }else {
                user.getRoles().remove(role);
                applicationUserRepository.save(user);
            }

    }

    private ApplicationUser createApplicationUserBase(String username, String password, String fullName, String countryCode, String phoneNumber, String role) {
        if (username == null || password == null || fullName == null || countryCode == null || phoneNumber == null || role == null) {
            throw new BadRequestException("Missing required user fields");
        }

        UserRole userRole = userRoleRepository.findByRoleName(role)
                .orElseThrow(() -> new EntryNotFoundException(String.format("Role %s not found", role)));

        return ApplicationUser.builder()
                .userId(UUID.randomUUID().toString())
                .username(username.trim())
                .password(passwordEncoder.encode(password))
                .fullName(fullName.trim())
                .oauthUser(false)
                .countryCode(countryCode.trim())
                .phoneNumber(phoneNumber.trim())
                .createdDate(Instant.now())
                .roles(Set.of(userRole))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }

    private ApplicationUser createApplicationUser(RequestApplicationUserDTO dto) {
        if (dto == null) throw new BadRequestException("Invalid request data");
        return createApplicationUserBase(
                dto.getUsername(),
                dto.getPassword(),
                dto.getFullName(),
                dto.getCountryCode(),
                dto.getPhoneNumber(),
                dto.getRole()
        );
    }

    private ApplicationUser createApplicationUser(RequestApplicationUserByAdminDTO dto) {
        if (dto == null) throw new BadRequestException("Invalid request data");
        String generatedPassword = generatePassword();

        emailService.sendUserAccountCredentialsEmail(
                dto.getUsername(),
                "Account Credentials",
                dto.getFullName(),
                dto.getRole(),
                dto.getUsername(),
                generatedPassword,
                adminOrigin
        );
        // You can email this password to the user here if needed.
        return createApplicationUserBase(
                dto.getUsername(),
                generatedPassword,
                dto.getFullName(),
                dto.getCountryCode(),
                dto.getPhoneNumber(),
                dto.getRole()
        );
    }


    private ResponseApplicationUserDTO mapToResponseApplicationUserDTO(ApplicationUser applicationUser) {


        return ResponseApplicationUserDTO.builder()
                .userId(applicationUser.getUserId())
                .username(applicationUser.getUsername())
                .fullName(applicationUser.getFullName())
                .phoneNumber(applicationUser.getCountryCode())
                .phoneNumberWithCountryCode(applicationUser.getPhoneNumber())
                .roles(applicationUser.getRoles().stream().map(
                        userRole -> ResponseApplicationUserRoleDTO.builder()
                                .propertyId(userRole.getRoleId())
                                .role(userRole.getRoleName())
                                .active(true)
                                .build()).toList())
                .build();
    }

    private String generatePassword() {
        int length = 12; // You can adjust this as needed
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%&*!";

        String combined = upper + lower + digits + special;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill remaining characters randomly
        for (int i = 4; i < length; i++) {
            password.append(combined.charAt(random.nextInt(combined.length())));
        }

        // Shuffle to avoid predictable order
        return shuffleString(password.toString(), random);
    }

    private String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int j = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

}
