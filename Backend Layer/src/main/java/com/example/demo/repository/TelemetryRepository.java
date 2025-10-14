package com.example.demo.repository;

import com.example.demo.entity.Telemetry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {

    @Query("SELECT t FROM Telemetry t WHERE t.machine.id = :machineId ORDER BY t.ts DESC")
    List<Telemetry> findRecentByMachine(@Param("machineId") Long machineId, Pageable pageable);
}
