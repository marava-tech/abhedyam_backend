package com.abhedyam.repository;

import com.abhedyam.model.FcmDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FcmDetailsRepository extends JpaRepository<FcmDetails, UUID> {
    List<FcmDetails> findByUserId(UUID userId);
    List<FcmDetails> findByUserIdAndPackageName(UUID userId, String packageName);
    Optional<FcmDetails> findByUserIdAndTokenAndPackageName(UUID userId, String token, String packageName);
    void deleteByUserIdAndTokenAndPackageName(UUID userId, String token, String packageName);
}

