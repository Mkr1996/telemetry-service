package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MachineRequest {
    @NotBlank(message = "Machine name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;
}
