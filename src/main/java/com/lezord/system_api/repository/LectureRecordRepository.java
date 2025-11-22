package com.lezord.system_api.repository;

import com.lezord.system_api.entity.LectureRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface LectureRecordRepository extends JpaRepository<LectureRecord, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM lecture_record WHERE intake_property_id=?1")
    Page<LectureRecord> findAllWithIntakeId(String intakeId, Pageable page);

    @Query(nativeQuery = true, value = "SELECT * FROM lecture_record WHERE intake_property_id=?1 AND course_stage_conent=?2")
    List<LectureRecord> findAll(String intakeId, String contentId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM lecture_record WHERE intake_property_id=?1")
    long countAllByIntakeId(String intakeId);

    @Query(nativeQuery = true, value = "SELECT * FROM lecture_record WHERE intake_id=?1 AND course_stage_conent=?2")
    List<LectureRecord> findAllWithIntakeIdAndContent(String intakeId, String content);


    @Query(nativeQuery = true, value = "SELECT * FROM lecture_record WHERE intake_property_id IN (?1) ORDER BY date ASC LIMIT 20")
    List<LectureRecord> getLatestRecordsForStudents(List<String> intakeIds);

    @Query(nativeQuery = true, value = "SELECT * FROM lecture_recording WHERE intake_id=?1 AND course_stage_conent=?2")
    List<LectureRecord> findByIntakeAndContentId(String intakeId, String contentId);

}
