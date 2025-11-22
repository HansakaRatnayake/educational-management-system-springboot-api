package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestionAnswerTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentQuestionAnswerTempRepository extends JpaRepository<AssignmentQuestionAnswerTemp,String> {
}
