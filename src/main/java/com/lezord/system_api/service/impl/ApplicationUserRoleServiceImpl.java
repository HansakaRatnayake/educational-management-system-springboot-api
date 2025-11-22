package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestApplicationRoleDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserRoleDTO;
import com.lezord.system_api.entity.UserRole;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.UserRoleRepository;
import com.lezord.system_api.service.ApplicationUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationUserRoleServiceImpl implements ApplicationUserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public void initializeRoles() {
        List<UserRole> applicationUserRoles = userRoleRepository.findAll();

        if(applicationUserRoles.isEmpty()){

            UserRole admin = UserRole.builder()
                    .roleId("ROLE_001")
                    .roleName("ADMIN")
                    .build();

            UserRole trainer = UserRole.builder()
                    .roleId("ROLE_002")
                    .roleName("TRAINER")
                    .build();

            UserRole student = UserRole.builder()
                    .roleId("ROLE_003")
                    .roleName("STUDENT")
                    .build();



            userRoleRepository.saveAll(List.of(admin,student,trainer));
        }
    }

    @Override
    public void create(RequestApplicationRoleDTO requestApplicationRoleDTO) {
        userRoleRepository.findUserRoleByRoleName(requestApplicationRoleDTO.getRoleName()).ifPresent(userRole -> {throw new DuplicateEntryException("Role already exists");});

        userRoleRepository.save(
                UserRole.builder()
                        .roleId(generateRoleId())
                        .roleName(requestApplicationRoleDTO.getRoleName())
                .build()
        );
    }

    @Override
    public void update(RequestApplicationRoleDTO requestApplicationRoleDTO, String roleId) {
        UserRole selectedUserRole = userRoleRepository.findById(roleId).orElseThrow(() -> new EntryNotFoundException("Role does not exist"));
        Optional<UserRole> userRoleByRoleName = userRoleRepository.findUserRoleByRoleName(requestApplicationRoleDTO.getRoleName());

        if (userRoleByRoleName.isPresent() && !requestApplicationRoleDTO.getRoleName().equals(userRoleByRoleName.get().getRoleName())) throw new DuplicateEntryException("Role already exists");

        selectedUserRole.setRoleName(requestApplicationRoleDTO.getRoleName());
        userRoleRepository.save(selectedUserRole);

    }

    @Override
    public void delete(String roleId) {
        UserRole selectedUserRole = userRoleRepository.findById(roleId).orElseThrow(() -> new EntryNotFoundException("Role does not exist"));
        userRoleRepository.delete(selectedUserRole);

    }

    @Override
    public List<ResponseApplicationUserRoleDTO> findAll() {
        return userRoleRepository.findAll(Sort.by(Sort.Direction.ASC,"roleId")).stream().map(
                userRole -> ResponseApplicationUserRoleDTO.builder()
                        .propertyId(userRole.getRoleId())
                        .role(userRole.getRoleName())
                        .build()
                ).toList();
    }

    private String generateRoleId() {
        Optional<UserRole> lastRole = userRoleRepository.findTopByOrderByRoleIdDesc();

        if (lastRole.isEmpty()) {
            return "ROLE_001";
        } else {
            String lastId = lastRole.get().getRoleId();
            int number = Integer.parseInt(lastId.substring(5));
            int nextNumber = number + 1;
            return String.format("ROLE_%03d", nextNumber);
        }
    }

}
