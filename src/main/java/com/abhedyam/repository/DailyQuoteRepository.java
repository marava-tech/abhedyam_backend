package com.abhedyam.repository;

import com.abhedyam.model.DailyQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DailyQuoteRepository extends JpaRepository<DailyQuote, UUID> {
    List<DailyQuote> findByIsActiveTrue();
    
    @Query("SELECT q FROM DailyQuote q WHERE q.isActive = true " +
           "ORDER BY q.lastUsedAt ASC NULLS FIRST")
    List<DailyQuote> findActiveQuotesOrderByLastUsedAsc();
    
    @Query("SELECT q FROM DailyQuote q WHERE q.isActive = true " +
           "AND q.lastUsedAt IS NULL")
    List<DailyQuote> findUnusedActiveQuotes();
    
    @Query("SELECT q FROM DailyQuote q WHERE q.isActive = true " +
           "AND q.lastUsedAt IS NOT NULL " +
           "ORDER BY q.lastUsedAt DESC")
    List<DailyQuote> findUsedActiveQuotesOrderByLastUsedDesc();
}

