package com.example.demo.kafka;

import com.example.demo.entity.Telemetry;
import com.example.demo.repository.TelemetryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    private final TelemetryRepository telemetryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "telemetry-topic", groupId = "telemetry-group")
    public void consumeMessage(String message) {
        try {
            log.info("Received telemetry message: {}", message);
            Telemetry telemetry = objectMapper.readValue(message, Telemetry.class);
            telemetryRepository.save(telemetry);
            log.info("Telemetry record saved: {}", telemetry);
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }
}
