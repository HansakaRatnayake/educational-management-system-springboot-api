package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestionRecording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentQuestionAudioRepository extends JpaRepository<AssignmentQuestionRecording, String> {
}
