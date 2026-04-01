package com.toy.project.studio.setting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toy.project.studio.setting.entity.ClosedDay;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, Long> {
}
