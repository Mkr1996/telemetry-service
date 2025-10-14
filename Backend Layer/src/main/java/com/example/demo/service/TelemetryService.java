package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import com.example.demo.entity.Machine;
import com.example.demo.entity.Telemetry;
import com.example.demo.repository.MachineRepository;
import com.example.demo.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final MachineRepository machineRepository;

    @Transactional
    public Telemetry addTelemetry(Long machineId, Telemetry telemetry) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found"));
        telemetry.setMachine(machine);
        Telemetry savedTelemetry = telemetryRepository.save(telemetry);
        log.info("Added telemetry for machine {}: {}", machineId, savedTelemetry);  
        return savedTelemetry;
    }

    public List<Telemetry> getRecent(Long machineId) {
        return telemetryRepository.findRecentByMachine(machineId, PageRequest.of(0, 10));
    }
}
