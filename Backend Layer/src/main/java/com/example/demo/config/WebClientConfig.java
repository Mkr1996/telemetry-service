import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient aiWebClient() {
        // Use service name from docker-compose for internal container DNS
        return WebClient.builder()
                .baseUrl(System.getenv().getOrDefault("AI_INSIGHTS_BASEURL", "http://ai-insights:8000"))
                .build();
    }
}
