package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestStudentDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserRoleDTO;
import com.lezord.system_api.dto.response.ResponseNonPaidStudentDetailsDTO;
import com.lezord.system_api.dto.response.ResponseStudentDTO;
import com.lezord.system_api.dto.response.paginate.PaginateNonPaidStudentDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginateStudentDTO;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.entity.Enrollment;
import com.lezord.system_api.entity.Student;
import com.lezord.system_api.entity.StudentInstallmentPlan;
import com.lezord.system_api.entity.enums.PaymentStatus;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.ApplicationUserRepository;
import com.lezord.system_api.repository.EnrollmentRepository;
import com.lezord.system_api.repository.StudentInstallmentPlanRepository;
import com.lezord.system_api.repository.StudentRepository;
import com.lezord.system_api.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentInstallmentPlanRepository studentInstallmentPlanRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserServiceImpl applicationUserService;

    @Override
    public void create(RequestStudentDTO studentDTO) {
        if (studentRepository.findStudentByApplicationUserUserId(studentDTO.getUserId()).isPresent()) throw new DuplicateEntryException("Student already exists");
        if (studentRepository.findStudentByNic(studentDTO.getNic()).isPresent()) throw new DuplicateEntryException(String.format("student with nic (%s) is exists", studentDTO.getNic()));
        studentRepository.save(createStudent(studentDTO));
    }

    @Override
    public void update(RequestStudentDTO studentDTO, String studentId) {
        Student selectedStudent = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException(String.format("student with id (%s) not found", studentId)));
        Optional<Student> studentByNic = studentRepository.findStudentByNic(studentDTO.getNic());
        Optional<Student> adminByApplicationUserPhoneNumber = studentRepository.findStudentByApplicationUser_PhoneNumber(studentDTO.getPhoneNumber());


        if (studentByNic.isPresent() && (selectedStudent.getNic() != null && !selectedStudent.getNic().equals(studentDTO.getNic()))) throw new DuplicateEntryException(String.format("student with nic (%s) is exists", studentDTO.getNic()));

        if (adminByApplicationUserPhoneNumber.isPresent() && (selectedStudent.getApplicationUser().getPhoneNumber() != null && !selectedStudent.getApplicationUser().getPhoneNumber().equals(studentDTO.getPhoneNumber())))
            throw new DuplicateEntryException(String.format("Admin with phone (%s) is exists", studentDTO.getPhoneNumber()));

        selectedStudent.getApplicationUser().setPhoneNumber(studentDTO.getPhoneNumber());
        selectedStudent.getApplicationUser().setCountryCode(studentDTO.getCountryCode());
        selectedStudent.getApplicationUser().setFullName(studentDTO.getFullName());
        selectedStudent.setNic(studentDTO.getNic());
        selectedStudent.setDob(studentDTO.getDob());
        selectedStudent.setAddress(studentDTO.getAddress());
        selectedStudent.setCity(studentDTO.getCity());
        selectedStudent.setCountry(studentDTO.getCountry());
        selectedStudent.setDisplayName(studentDTO.getDisplayName());
        selectedStudent.setGender(studentDTO.getGender());
        selectedStudent.setAddress(studentDTO.getAddress());
        selectedStudent.setFirstName(studentDTO.getFirstName());
        selectedStudent.setLastName(studentDTO.getLastName());


    }

    @Override
    public void delete(String studentId) {
        Student selectedStudent = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException(String.format("Student %s not found", studentId)));
        studentRepository.deleteById(studentId);
//        applicationUserService.delete(selectedStudent.getPropertyId());
    }

    @Override
    public void changeStatus(String studentId) {
        Student selectedStudent = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException(String.format("Student %s not found", studentId)));

        selectedStudent.setActiveState(!selectedStudent.getActiveState());
        studentRepository.save(selectedStudent);
    }

    @Override
    public Long count(String searchText) {
        return studentRepository.countStudents(searchText);
    }

    @Override
    public long countStudentByActiveState(boolean active) {
        return studentRepository.countStudentByActiveState(active);
    }

    @Override
    public ResponseStudentDTO findByUserId(String userId) {
        if (userId.isEmpty()) throw new EntryNotFoundException("User id not found");

        Student selectedStudent = studentRepository.findStudentByApplicationUserUserId(userId)
                .orElseThrow(() -> new EntryNotFoundException("Student data not found"));

        return convertToResponseStudentDTO(selectedStudent);
    }

    @Override
    public ResponseStudentDTO findById(String studentId) {

        return convertToResponseStudentDTO(studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException(String.format("Student %s not found", studentId))));
    }

    @Override
    public PaginateStudentDTO findByIntakeId(String intakeId, int pageNumber, int pageSize) {
        return PaginateStudentDTO.builder()
                .dataList(studentRepository.findStudentsByIntake(intakeId,PageRequest.of(pageNumber,pageSize)).stream().map(this::convertToResponseStudentDTO).toList())
                .count(studentRepository.findStudentsCountByIntake(intakeId))
                .build();
    }

    @Override
    public PaginateStudentDTO findAll(String searchText, String courseId, String intakeId, int pageNumber, int pageSize) {

        return PaginateStudentDTO.builder()
                .dataList(studentRepository.searchStudentsWithFilters(searchText, courseId, intakeId, PageRequest.of(pageNumber,pageSize)).stream().map(this::convertToResponseStudentDTO).collect(Collectors.toList()))
                .count(studentRepository.countStudents(searchText))
                .build();
    }

    @Override
    public PaginateNonPaidStudentDetailDTO findNonPaidStudents(String searchText, int pageNumber, int pageSize) {
        Page<StudentInstallmentPlan> page = studentInstallmentPlanRepository
                .findStudentInstallmentPlansByStatusAndNextAndInstallmentEndDateBefore(
                        PaymentStatus.PENDING,
                        true,
                        Instant.now().atZone(ZoneId.systemDefault()).toLocalDate(),
                        searchText,
                        PageRequest.of(pageNumber, pageSize)
                );

        List<StudentInstallmentPlan> plans = page.getContent();

        // Build a list of studentId + courseId pairs
        List<Pair<String, String>> studentCoursePairs = plans.stream()
                .map(p -> Pair.of(
                        p.getStudent().getPropertyId(),
                        p.getIntake().getCourse().getPropertyId()))
                .toList();

        // Fetch enrollments for all student+course combinations in a single batch query
        Map<String, Boolean> accessMap = enrollmentRepository
                .findByStudentPropertyIdInAndIntakeCoursePropertyIdIn(
                        studentCoursePairs.stream().map(Pair::getLeft).collect(Collectors.toSet()),
                        studentCoursePairs.stream().map(Pair::getRight).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getStudent().getPropertyId() + "-" + e.getIntake().getCourse().getPropertyId(),
                        Enrollment::getCanAccessCourse
                ));

        // Build DTO list
        List<ResponseNonPaidStudentDetailsDTO> dtoList = plans.stream()
                .map(p -> {
                    String key = p.getStudent().getPropertyId() + "-" + p.getIntake().getCourse().getPropertyId();
                    Boolean canAccess = accessMap.getOrDefault(key, false);

                    return ResponseNonPaidStudentDetailsDTO.builder()
                            .student(convertToResponseStudentDTO(p.getStudent()))
                            .courseName(p.getIntake().getCourse().getName())
                            .courseId(p.getIntake().getCourse().getPropertyId())
                            .intakeName(p.getIntake().getName())
                            .intakeId(p.getIntake().getPropertyId())
                            .amount(p.getInstallment().getAmount())
                            .installmentStartDate(p.getInstallment().getStartDate())
                            .installmentEndDate(p.getInstallment().getEndDate())
                            .installmentNumber(p.getInstallment().getInstallmentNumber())
                            .paymentStatus(p.getStatus())
                            .canAccessCourse(canAccess)
                            .build();
                })
                .toList();

        // Determine if any student has course access
        boolean access = dtoList.stream().anyMatch(ResponseNonPaidStudentDetailsDTO::isCanAccessCourse);

        return PaginateNonPaidStudentDetailDTO.builder()
                .access(access)
                .count(page.getTotalElements())
                .dataList(dtoList)
                .build();
    }



    private Student createStudent(RequestStudentDTO studentDTO) {
        return Student.builder()
                .propertyId(UUID.randomUUID().toString())
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .displayName(studentDTO.getDisplayName())
                .nic(studentDTO.getNic())
                .email(studentDTO.getEmail())
                .dob(studentDTO.getDob())
                .activeState(true)
                .address(studentDTO.getAddress())
                .city(studentDTO.getCity())
                .country(studentDTO.getCountry())
                .applicationUser(applicationUserService.findById(studentDTO.getUserId()))
                .gender(studentDTO.getGender())
                .build();
    }

    private ResponseStudentDTO convertToResponseStudentDTO(Student student) {
        ApplicationUser applicationUser = applicationUserRepository.findById(student.getApplicationUser().getUserId()).orElseThrow(() -> new EntryNotFoundException("Application user not found: " + student.getApplicationUser().getUserId()));

        return ResponseStudentDTO.builder()
                .propertyId(student.getPropertyId())

                .email(student.getEmail() == null ? "" : student.getEmail())
                .firstName(student.getFirstName() == null ? "" : student.getFirstName())
                .lastName(student.getLastName() == null ? "" : student.getLastName())
                .displayName(student.getDisplayName() == null ? "" : student.getDisplayName())
                .nic(student.getNic() == null ? "" : student.getNic())
                .gender(student.getGender() == null ? null : student.getGender())
                .userId(student.getApplicationUser().getUserId())
                .dob(student.getDob() == null ? null : student.getDob())
                .address(student.getAddress() == null ? "" : student.getAddress())
                .country(student.getCountry() == null ? "" : student.getCountry())
                .city(student.getCity() == null ? "" : student.getCity())
                .activeStatus(student.getActiveState() == null)
                .applicationUser(mapToResponseApplicationUserDTO(applicationUser))
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


}
