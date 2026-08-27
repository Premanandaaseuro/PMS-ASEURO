package com.aseuro.pms.manager.repository;

import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.manager.entity.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, Long> {
    List<Kpi> findByStatus(RecordStatus status);
}
