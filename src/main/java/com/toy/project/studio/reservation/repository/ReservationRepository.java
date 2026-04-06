package com.toy.project.studio.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import com.toy.project.studio.reservation.entity.Reservation;
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
}
