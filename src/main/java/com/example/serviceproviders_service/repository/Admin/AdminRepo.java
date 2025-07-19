// repository/AdminRepo.java
package com.example.serviceproviders_service.repository.Admin;

import com.example.serviceproviders_service.entity.Admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}