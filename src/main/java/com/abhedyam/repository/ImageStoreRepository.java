package com.abhedyam.repository;

import com.abhedyam.model.ImageStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageStoreRepository extends JpaRepository<ImageStore, UUID> {

    Page<ImageStore> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT i FROM ImageStore i WHERE i.isActive = true " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    List<ImageStore> findByNameContainingIgnoreCase(@Param("searchKey") String searchKey, Pageable pageable);

    @Query("SELECT DISTINCT i FROM ImageStore i JOIN i.tags t WHERE i.isActive = true " +
            "AND LOWER(t) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    List<ImageStore> findByTagContainingIgnoreCase(@Param("searchKey") String searchKey, Pageable pageable);
}
