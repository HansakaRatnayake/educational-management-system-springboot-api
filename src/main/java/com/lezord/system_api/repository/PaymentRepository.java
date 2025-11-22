package com.lezord.system_api.repository;

import com.lezord.system_api.dto.response.converters.MonthlyRevenue;
import com.lezord.system_api.dto.response.converters.TotalRevenue;
import com.lezord.system_api.dto.response.converters.YearlyRevenue;
import com.lezord.system_api.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    @Query(value = """
    SELECT DISTINCT p.* FROM payment p
    JOIN payment_slip ps ON ps.payment_id = p.property_id
    JOIN student s ON s.property_id = p.student_id
    WHERE ps.is_verified = false AND (
        LOWER(s.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.last_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.nic) LIKE LOWER(CONCAT('%', :search, '%'))
    )
    """,
            countQuery = """
    SELECT COUNT(DISTINCT p.property_id) FROM payment p
    JOIN payment_slip ps ON ps.payment_id = p.property_id
    JOIN student s ON s.property_id = p.student_id
    WHERE ps.is_verified = false AND (
        LOWER(s.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.last_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.nic) LIKE LOWER(CONCAT('%', :search, '%'))
    )
    """,
            nativeQuery = true)
    Page<Payment> findUnverifiedPaymentSlipsByStudentInfo(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(value = """
    SELECT COUNT(*) FROM payment p
    JOIN payment_slip ps ON ps.payment_id = p.property_id
    JOIN student s ON s.property_id = p.student_id
    WHERE ps.is_verified = false AND (
        LOWER(s.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.last_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(s.nic) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""", nativeQuery = true)
    long countUnverifiedPaymentSlips(@Param("search") String search);


    // Monthly revenue
    @Query(
            value = """
                SELECT 
                    YEAR(p.paid_at) AS year,
                    MONTH(p.paid_at) AS month,
                    SUM(p.amount) AS totalRevenue
                FROM payment p
                WHERE p.status = :status AND YEAR(p.paid_at) = :byYear
                GROUP BY YEAR(p.paid_at), MONTH(p.paid_at)
                ORDER BY year DESC, month DESC
            """,
            nativeQuery = true
    )
    List<MonthlyRevenue> findMonthlyRevenue(@Param("status") String status, @Param("byYear")int byYear);

    // Yearly revenue
    @Query(
            value = """
                SELECT 
                    CAST(YEAR(p.paid_at) AS SIGNED) AS year,
                    SUM(p.amount) AS totalRevenue
                FROM payment p
                WHERE p.status = :status AND YEAR(p.paid_at) = :byYear
                GROUP BY CAST(YEAR(p.paid_at) AS SIGNED)
                ORDER BY year DESC
            """,
            nativeQuery = true
    )
    List<YearlyRevenue> findYearlyRevenue(@Param("status") String status, @Param("byYear")int byYear);

    // Total revenue
    @Query(
            value = """
                SELECT 
                    COALESCE(SUM(p.amount), 0) AS totalRevenue
                FROM payment p
                WHERE p.status = :status
            """,
            nativeQuery = true
    )
    TotalRevenue findTotalRevenue(@Param("status") String status);
}
