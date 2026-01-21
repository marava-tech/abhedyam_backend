package com.abhedyam.repository;

import com.abhedyam.model.Feedback;
import com.abhedyam.model.enums.FeedbackCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT f FROM Feedback f WHERE " +
           "(:userId IS NULL OR f.userId = :userId) " +
           "AND (:category IS NULL OR f.category = :category) " +
           "AND (:searchText IS NULL OR LOWER(f.issueDescription) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Feedback> searchFeedbacks(@Param("userId") UUID userId,
                                    @Param("category") FeedbackCategory category,
                                    @Param("searchText") String searchText,
                                    Pageable pageable);
}

