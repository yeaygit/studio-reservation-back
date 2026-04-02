package com.toy.project.studio.setting.repository;

import java.util.List;
import java.util.Optional;

import com.toy.project.studio.setting.entity.ShootingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShootingTypeRepository extends JpaRepository<ShootingType, Long> {

    @Query("""
            select coalesce(max(s.sortOrder), 0)
            from ShootingType s
            where s.isActive = true
            """)
    Long findMaxSortOrder();

    @Query("""
            select s
            from ShootingType s
            where s.isActive = true
            order by s.sortOrder asc, s.id asc
            """)
    List<ShootingType> findAllActive();

    @Query("""
            select s
            from ShootingType s
            where s.id = :shootingTypeId
              and s.isActive = true
            """)
    Optional<ShootingType> findActiveById(@Param("shootingTypeId") Long shootingTypeId);

    @Query("""
            select case when count(s) > 0 then true else false end
            from ShootingType s
            where lower(s.code) = lower(:code)
              and s.isActive = true
            """)
    boolean existsActiveByCode(@Param("code") String code);
}
