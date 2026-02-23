package com.smartiadev.auth_service.repository;

import com.smartiadev.auth_service.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    Long countActiveUsers();

   /* @Modifying
    @Transactional
    @Query("update User u set u.enabled = true where u.enabled = false")
    void enableAllDisabledUsers();*/

}

