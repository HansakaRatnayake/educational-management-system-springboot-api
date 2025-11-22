package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.ResponseClientViewBasicStatisticsDTO;
import com.lezord.system_api.service.*;
import com.nozomi.system_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StudentService studentService;
    private final CourseService courseService;
    private final InstructorService instructorService;
    private final SuccessStoryService successStoryService;
//    private final StatisticService statisticService;

    @Override
    public ResponseClientViewBasicStatisticsDTO getBasicStatistics() {
        return ResponseClientViewBasicStatisticsDTO.builder()
                .activeStudentCount(studentService.countStudentByActiveState(true))
                .totalInstructorsCount(instructorService.totalInstructorCount())
                .totalProgramCount(courseService.count(""))
                .totalSuccessStoriesCount(successStoryService.totalSuccessStory())
                .build();
    }
}
