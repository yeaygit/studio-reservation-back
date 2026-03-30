package com.toy.project.studio.faq.repository;

import java.util.List;
import java.util.Optional;

import com.toy.project.studio.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    @Query("""
            select coalesce(max(f.sortOrder), 0)
            from Faq f
            """)
    Integer findMaxSortOrder();

    @Query("""
            select f
            from Faq f
            where f.isActive = true
            order by f.sortOrder asc, f.id asc
            """)
    List<Faq> findAllActive();

    @Query("""
            select f
            from Faq f
            where f.id = :faqId
              and f.isActive = true
            """)
    Optional<Faq> findActiveById(@Param("faqId") Long faqId);
}
