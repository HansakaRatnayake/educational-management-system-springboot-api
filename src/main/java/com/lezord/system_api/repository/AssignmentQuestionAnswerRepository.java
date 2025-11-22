package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentQuestionAnswerRepository extends JpaRepository<AssignmentQuestionAnswer, String> {
}
