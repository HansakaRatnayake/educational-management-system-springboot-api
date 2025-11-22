package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestAddressDetailDTO;
import com.lezord.system_api.dto.request.RequestAdminDTO;
import com.lezord.system_api.dto.request.RequestEmploymentDetailDTO;
import com.lezord.system_api.dto.response.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.entity.core.Address;
import com.lezord.system_api.entity.Admin;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.entity.core.EmploymentDetails;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.AdminRepository;
import com.lezord.system_api.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private AdminRepository adminRepository;
    private ApplicationUserServiceImpl applicationUserService;


    @Override
    public void update(RequestAdminDTO adminDTO, String adminId) {
        Admin selectedAdmin = adminRepository.findById(adminId).orElseThrow(() -> new EntryNotFoundException("Admin not found"));

        Optional<Admin> adminByApplicationUserPhoneNumber = adminRepository.findAdminByApplicationUser_PhoneNumber(adminDTO.getPhoneNumber());
        Optional<Admin> adminByNic = adminRepository.findAdminByNic(adminDTO.getNic());

        if (adminByNic.isPresent() && (selectedAdmin.getNic() != null && !selectedAdmin.getNic().equals(adminByNic.get().getNic())))
            throw new DuplicateEntryException(String.format("Admin with nic (%s) is exists", adminDTO.getNic()));


        if (adminByApplicationUserPhoneNumber.isPresent() && (selectedAdmin.getApplicationUser().getPhoneNumber() != null && !selectedAdmin.getApplicationUser().getPhoneNumber().equals(adminDTO.getPhoneNumber())))
            throw new DuplicateEntryException(String.format("Admin with phone (%s) is exists", adminDTO.getPhoneNumber()));

        selectedAdmin.setDob(adminDTO.getDob());
        selectedAdmin.setNic(adminDTO.getNic());
        selectedAdmin.setDisplayName(adminDTO.getDisplayName());
        selectedAdmin.setGender(adminDTO.getGender());
        selectedAdmin.setAddress(mapToAddress(adminDTO.getAddress()));
        selectedAdmin.setEmploymentDetails(mapToEmploymentDetails(adminDTO.getEmployment()));
        selectedAdmin.getApplicationUser().setPhoneNumber(adminDTO.getPhoneNumber());
        selectedAdmin.getApplicationUser().setCountryCode(adminDTO.getCountryCode());
        selectedAdmin.getApplicationUser().setFullName(adminDTO.getFullName());


        adminRepository.save(selectedAdmin);

    }

    @Override
    public void changeStatus(String adminId) {
        Admin selectedAdmin = adminRepository.findById(adminId).orElseThrow(() -> new EntryNotFoundException("Admin not found"));

        selectedAdmin.getEmploymentDetails().setActiveStatus(!selectedAdmin.getEmploymentDetails().getActiveStatus());
        adminRepository.save(selectedAdmin);
    }

    @Override
    public ResponseAdminDTO getByApplicationUserId(String userId) {
        return mapToResponseAdminDTO(adminRepository.findAdminByApplicationUser_UserId(userId).orElseThrow(() -> new EntryNotFoundException("Admin not found")));
    }

    private Address mapToAddress(RequestAddressDetailDTO dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .province(dto.getProvince())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .build();
    }

    private EmploymentDetails mapToEmploymentDetails(RequestEmploymentDetailDTO dto) {
        return EmploymentDetails.builder()
                .employmentType(dto.getEmploymentType())
                .designation(dto.getDesignation())
                .activeStatus(true)
                .dateJoined(java.time.LocalDate.now())
                .build();
    }

    private ResponseAdminDTO mapToResponseAdminDTO(Admin admin) {
        return ResponseAdminDTO.builder()
                .propertyId(admin.getPropertyId())
                .userId(admin.getApplicationUser().getUserId())
                .displayName(admin.getDisplayName())
                .dob(admin.getDob())
                .nic(admin.getNic())
                .email(admin.getEmail())
                .gender(admin.getGender())
                .activeStatus(admin.getEmploymentDetails().getActiveStatus())
                .address(mapToResponseAddressDetailDTO(admin.getAddress()))
                .employment(mapToResponseEmploymentDetailDTO(admin.getEmploymentDetails()))
                .applicationUser(mapToResponseApplicationUserDTO(admin.getApplicationUser()))
                .build();
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

    private ResponseEmploymentDetailDTO mapToResponseEmploymentDetailDTO(EmploymentDetails employmentDetails) {
        if (employmentDetails == null) return null;
        return ResponseEmploymentDetailDTO.builder()
                .employmentType(employmentDetails.getEmploymentType())
                .designation(employmentDetails.getDesignation())
                .dateJoined(employmentDetails.getDateJoined())
                .activeStatus(employmentDetails.getActiveStatus())
                .build();
    }

    private ResponseAddressDetailDTO mapToResponseAddressDetailDTO(Address address) {
        if (address == null) return null;
        return ResponseAddressDetailDTO.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }


}
