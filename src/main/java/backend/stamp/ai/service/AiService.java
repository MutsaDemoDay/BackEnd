package backend.stamp.ai.service;
import backend.stamp.ai.ai.AiApiResponse;
import backend.stamp.ai.ai.AiRequest;
import backend.stamp.ai.ai.AiResponse;
import backend.stamp.global.config.WebClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final WebClient.Builder webClientBuilder;
    private final AiApiResponse props;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(props.getUrl())
                .build();
    }

    public Mono<AiResponse> callAiServer(AiRequest request) {
        return webClient.post()
                .uri("/api/v1/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiResponse.class);
    }
}