package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TelemetryRequest {
    @NotNull @Positive
    private Double temperature;

    @NotNull @Positive
    private Double vibration;

    private Double pressure;
}
