package com.lezord.system_api.repository;

import com.lezord.system_api.entity.GetInTouch;
import com.lezord.system_api.entity.enums.GetInTouchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GetInTouchRepository extends JpaRepository<GetInTouch, String> {

    long countByStatus(GetInTouchStatus status);

    Page<GetInTouch> findGetInTouchByStatus(GetInTouchStatus status, Pageable pageable);

    @Query("""
    SELECT g FROM GetInTouch g 
    WHERE LOWER(g.email) LIKE LOWER(CONCAT('%', :email, '%'))
    """)
    Page<GetInTouch> findGetInTouchByEmail(@Param("email") String email, Pageable pageable);


}

