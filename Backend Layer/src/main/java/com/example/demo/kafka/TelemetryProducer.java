package com.example.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random random = new Random();

    @Scheduled(fixedRate = 5000)
    public void sendTelemetryEvent() {
        double temperature = 60 + random.nextDouble() * 40;
        double vibration = random.nextDouble() * 0.05;
        double pressure = 90 + random.nextDouble() * 20;

        String telemetryJson = String.format(
                "{\"machineId\":1,\"temperature\":%.2f,\"vibration\":%.3f,\"pressure\":%.2f,\"ts\":\"%s\"}",
                temperature, vibration, pressure, Instant.now().toString()
        );

        kafkaTemplate.send("telemetry-topic", telemetryJson);
        log.info("Sent telemetry message: {}", telemetryJson);
    }
}
