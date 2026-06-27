package com.example.analytics.repository;

import com.example.analytics.model.PageCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PageCountRepository extends JpaRepository<PageCount, String> {

    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO page_counts (url, count) VALUES (:url, 1) " +
                "ON DUPLICATE KEY UPDATE count = count + 1",
        nativeQuery = true
    )
    void upsertCount(@Param("url") String url);
}
