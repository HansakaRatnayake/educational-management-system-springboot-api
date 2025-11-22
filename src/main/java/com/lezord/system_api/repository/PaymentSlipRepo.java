package com.lezord.system_api.repository;

import com.lezord.system_api.entity.PaymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentSlipRepo extends JpaRepository<PaymentSlip, String> {
}
