package com.toy.project.studio.notice.repository;

import java.util.List;
import java.util.Optional;

import com.toy.project.studio.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("""
            select n
            from Notice n
            where n.isActive = true
            order by n.id desc
            """)
    List<Notice> findAllActive();

    @Query("""
            select n
            from Notice n
            where n.id = :noticeId
              and n.isActive = true
            """)
    Optional<Notice> findActiveById(@Param("noticeId") Long noticeId);
}
