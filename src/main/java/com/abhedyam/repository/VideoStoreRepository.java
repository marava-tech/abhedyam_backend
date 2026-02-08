package com.abhedyam.repository;

import com.abhedyam.model.VideoStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoStoreRepository extends JpaRepository<VideoStore, UUID> {

    Page<VideoStore> findByIsActiveTrue(Pageable pageable);

    List<VideoStore> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT v FROM VideoStore v JOIN v.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :tag, '%'))")
    List<VideoStore> findByTagContainingIgnoreCase(@Param("tag") String tag, Pageable pageable);
}
