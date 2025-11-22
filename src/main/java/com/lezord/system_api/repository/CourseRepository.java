package com.lezord.system_api.repository;


import com.lezord.system_api.entity.Course;
import com.lezord.system_api.entity.enums.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT c FROM Course c WHERE c.name LIKE %:searchText% OR c.description LIKE %:searchText%")
    List<Course> findAllCourses(@Param("searchText") String searchText);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.name LIKE %:searchText% OR c.description LIKE :searchText")
    int countCourses(@Param("searchText") String searchText);

    List<Course> findByCourseLevel(CourseLevel courseLevel);



}
