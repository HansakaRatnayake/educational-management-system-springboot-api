package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponsePurchaseDetailDTO;
import com.lezord.system_api.dto.response.ResponseStudentCourseEnrollmentEligibilityDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedEnrollmentDTO;

import java.util.List;

public interface EnrollmentService {
    boolean createEnrollment(String studentId, String intakeId);
    PaginatedEnrollmentDTO getAllEnrollments(int page, int size);

    void changeActiveStatus(String enrollmentId);

    void changeCourseAccessStatus(String studentId, String intakeId);

    void changeAllNonPaidStudentsCourseAccessStatus(boolean access);

    List<ResponsePurchaseDetailDTO> getEnrollmentPurchaseDetails(String enrollmentId);

    ResponseStudentCourseEnrollmentEligibilityDTO checkEnrollmentEligibility(String intakeId, String studentId);

}
