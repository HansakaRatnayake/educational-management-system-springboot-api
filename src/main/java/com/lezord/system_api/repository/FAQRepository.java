package com.lezord.system_api.repository;

import com.lezord.system_api.entity.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FAQRepository extends JpaRepository<FAQ, String> {

    Page<FAQ> findByQuestionContainingIgnoreCase(String searchText, Pageable pageable);

    @Query("SELECT MAX(f.orderId) FROM FAQ f")
    Optional<Integer> findMaxOrOrderId();
}
