package com.toy.project.studio.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import com.toy.project.studio.reservation.entity.Reservation;
import com.toy.project.studio.reservation.enumeration.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r.id as reservationId,
                   r.startTime as startTime,
                   r.endTime as endTime
            from Reservation r
            where r.date = :date
            order by r.startTime asc
            """)
    List<ReservedTimeProjection> findBookedTimesByDate(@Param("date") LocalDate date);

    @Query("""
            select count(r) > 0
            from Reservation r
            where r.date = :date
              and r.status in :statuses
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsOverlappingReservation(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("statuses") Collection<ReservationStatus> statuses
    );

    @Query("""
            select r
            from Reservation r
            join fetch r.shootingType st
            where r.date = :date
            order by r.date desc, r.startTime asc , r.createdAt desc
            """)
    List<Reservation> findAllWithShootingTypeByDateOrderByStartTimeAsc(
            @Param("date") LocalDate date
    );

    @Query("""
            select r
            from Reservation r
            join fetch r.shootingType st
            where r.date = :date
              and lower(r.name) like :namePattern
            order by r.date desc, r.startTime asc, r.createdAt desc
            """)
    List<Reservation> findAllWithShootingTypeByDateAndNamePatternOrderByStartTimeAsc(
            @Param("date") LocalDate date,
            @Param("namePattern") String namePattern
    );
}
