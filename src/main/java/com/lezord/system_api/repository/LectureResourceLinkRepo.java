package com.lezord.system_api.repository;

import com.lezord.system_api.entity.LectureResourceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface LectureResourceLinkRepo extends JpaRepository<LectureResourceLink, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM lecture_resource_link WHERE record_property_id=?1")
    List<LectureResourceLink> findByRecordId(String recordId);
}
