package com.toy.project.studio.terms.repository;

import java.util.List;
import java.util.Optional;

import com.toy.project.studio.terms.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    @Query("""
            select t
            from Terms t
            where t.isActive = true
            order by t.id desc
            """)
    List<Terms> findAllActive();

    @Query("""
            select t
            from Terms t
            where t.id = :termsId
              and t.isActive = true
            """)
    Optional<Terms> findActiveById(@Param("termsId") Long termsId);
}
