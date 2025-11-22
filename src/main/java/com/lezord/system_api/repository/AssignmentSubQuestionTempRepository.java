package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentSubQuestionTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentSubQuestionTempRepository extends JpaRepository<AssignmentSubQuestionTemp,String> {
}
