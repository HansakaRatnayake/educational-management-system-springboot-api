package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.converters.MonthlyRevenue;
import com.lezord.system_api.dto.response.converters.YearlyRevenue;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {

    List<ResponseStudentDashboardViewEnrolledCourseDTO> getStudentEnrolledCourses(String studentId);
    ResponseStudentDashboardDetailDTO getStudentDashboardDetail(String studentId, String intakeId);

    List<ResponseInstructorDashboardViewAssignedCourseDTO> getInstructorAssignedCourses(String instructorId);
    ResponseAdminDashboardStatCardDetailDTO getAdminDashboardStatCardDetails();
    ResponseInstructorDashboardDetailDTO getInstructorDashboardDetail(String instructorId, String intakeId);

    List<MonthlyRevenue> calculateMonthlyRevenue(String year);
    List<YearlyRevenue> calculateYearlyRevenue(String year);
    BigDecimal totalRevenue();



}
