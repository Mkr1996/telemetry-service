import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.demo.repository.TelemetryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    private final TelemetryRepository telemetryRepository;
    private final WebClient aiWebClient;  // Inject WebClient
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "telemetry-topic", groupId = "telemetry-group")
    public void consumeMessage(String message) {
        try {
            log.info("Received telemetry message: {}", message);
            Telemetry telemetry = objectMapper.readValue(message, Telemetry.class);
            
            // Save telemetry in TimescaleDB
            telemetryRepository.save(telemetry);
            log.info("Telemetry saved: {}", telemetry);

            // Send telemetry to AI Insights microservice for embedding
            aiWebClient.post()
                    .uri("/embed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(message)  // Same JSON format the AI service expects
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                        resp -> log.info("AI embed response: {}", resp),
                        err -> log.error("Error calling AI service", err)
                    );
        } catch (Exception e) {
            log.error("Error processing Kafka message", e);
        }
    }
}
