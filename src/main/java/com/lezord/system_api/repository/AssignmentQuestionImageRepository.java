package com.lezord.system_api.repository;

import com.lezord.system_api.entity.AssignmentQuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentQuestionImageRepository extends JpaRepository<AssignmentQuestionImage,String> {
}
