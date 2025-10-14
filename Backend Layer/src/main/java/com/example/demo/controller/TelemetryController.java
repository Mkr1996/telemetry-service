package com.example.demo.controller;

import com.example.demo.entity.Telemetry;
import com.example.demo.service.TelemetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping("/{machineId}")
    public ResponseEntity<Telemetry> addTelemetry(
            @PathVariable Long machineId,
            @RequestBody @Valid Telemetry telemetry) {
        return ResponseEntity.ok(telemetryService.addTelemetry(machineId, telemetry));
    }

    @GetMapping("/{machineId}/recent")
    public List<Telemetry> getRecent(@PathVariable Long machineId) {
        return telemetryService.getRecent(machineId);
    }
}
