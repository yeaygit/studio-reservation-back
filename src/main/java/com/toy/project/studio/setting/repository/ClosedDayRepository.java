package com.toy.project.studio.setting.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.toy.project.studio.setting.entity.ClosedDay;
import com.toy.project.studio.setting.enumeration.ClosedDayType;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, Long> {

    @Query("""
            select c
            from ClosedDay c
            where c.isActive = true
              and (
                    (c.type = :specificType
                     and c.specificDate >= :startDate
                     and c.specificDate < :endDate)
                 or c.type = :annualType
              )
            """)
    List<ClosedDay> findAllActiveByYear(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("specificType") ClosedDayType specificType,
            @Param("annualType") ClosedDayType annualType
    );

    @Query("""
            select c
            from ClosedDay c
            where c.isActive = true
              and (
                    c.type = :annualType
                 or (c.type = :specificType
                     and c.specificDate >= :startDate
                     and c.specificDate <= :endDate)
              )
            """)
    List<ClosedDay> findAllActiveByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("specificType") ClosedDayType specificType,
            @Param("annualType") ClosedDayType annualType
    );

    @Query("""
            select c
            from ClosedDay c
            where c.id = :closedDayId
              and c.isActive = true
            """)
    Optional<ClosedDay> findActiveById(@Param("closedDayId") Long closedDayId);
}
