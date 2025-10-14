package com.example.demo.controller;

import com.example.demo.entity.Machine;
import com.example.demo.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machine")
@RequiredArgsConstructor
public class MachineController {

    private final MachineRepository machineRepository;

    @PostMapping
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
        return ResponseEntity.ok(machineRepository.save(machine));
    }

    @GetMapping
    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }
}
