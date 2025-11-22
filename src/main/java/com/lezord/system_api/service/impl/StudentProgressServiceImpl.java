package com.lezord.system_api.service.impl;

import com.lezord.system_api.entity.Intake;
import com.lezord.system_api.entity.Student;
import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentProgressServiceImpl implements StudentProgressService {
    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final CourseStageRepository courseStageRepository;
    private final StudentRepository studentRepository;
    private final IntakeRepository intakeRepository;
    private final StudentHasAssignmentRepository studentHasAssignmentRepository;

    @Override
    public boolean verifyEligibilityForNextCourseStage(String studentId) {
        return false;
    }

    @Override
    public int calculateStudentProgress(String studentId, String intakeId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student not found"));
        Intake intake = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake not found"));

        long studentCompletedAndPassedAssigmentCount = studentHasAssignmentRepository.countStudentHasAssignmentsByAssignment_IntakeAndStudentAndStatusTypeAndMarksType(intake, student, StudentHasAssignmentTypes.COMPLETED, StudentHasAssignmentMarksTypes.PASSED);

        return  studentCompletedAndPassedAssigmentCount == 0 ? 0 : Math.round(((float) (int) studentCompletedAndPassedAssigmentCount /intake.getCourse().getAssigmentCount()) * 100);
    }


}
