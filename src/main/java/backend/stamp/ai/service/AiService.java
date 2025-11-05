package backend.stamp.ai.service;
import backend.stamp.ai.ai.AiApiResponse;
import backend.stamp.ai.ai.AiRequest;
import backend.stamp.ai.ai.AiResponse;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AiService {

    private final WebClient webClient;
    public AiService(WebClient.Builder webClientBuilder, AiApiResponse props) {
        this.webClient = webClientBuilder
                .baseUrl(props.getUrl())
                .build();
    }
    public Mono<AiResponse> callAiServer(AiRequest request) {
        return webClient.post()
                .uri("/api/v1/recommendations")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiResponse.class);
    }
}
