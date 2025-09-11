package com.example.serviceproviders_service.repository.ServiceProvider;

import com.example.serviceproviders_service.entity.serviceProvider.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {

    Optional<BankDetails> findByServiceProviderId(Long serviceProviderId);

    boolean existsByServiceProviderId(Long serviceProviderId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByAccountNumberAndServiceProviderIdNot(String accountNumber, Long serviceProviderId);

    List<BankDetails> findByVerificationStatus(BankDetails.VerificationStatus verificationStatus);

    @Query("SELECT bd FROM BankDetails bd WHERE bd.serviceProviderId = :serviceProviderId AND bd.isVerified = :isVerified")
    Optional<BankDetails> findByServiceProviderIdAndIsVerified(@Param("serviceProviderId") Long serviceProviderId, @Param("isVerified") Boolean isVerified);

    @Query("SELECT COUNT(bd) FROM BankDetails bd WHERE bd.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") BankDetails.VerificationStatus status);

    @Query("SELECT bd FROM BankDetails bd WHERE bd.ifscCode = :ifscCode")
    List<BankDetails> findByIfscCode(@Param("ifscCode") String ifscCode);
}
