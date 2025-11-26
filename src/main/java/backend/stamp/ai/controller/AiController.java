package backend.stamp.ai.controller;

import backend.stamp.ai.ai.AiRequest;
import backend.stamp.ai.ai.AiResponse;
import backend.stamp.ai.ai.subdtos.AiRequestForm;
import backend.stamp.ai.service.AiService;
import backend.stamp.ai.service.RecommendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")

@Tag(name = "AI", description = "AI 관련 api")
public class AiController {
    private final AiService aiService;
    private final RecommendService recommendService;
    private final ObjectMapper objectMapper;

    @PostMapping("/call")
    public Mono<AiResponse> callAi(@RequestBody AiRequest partialRequest) {
        AiRequestForm fullRequest = recommendService.buildFullAiRequest(partialRequest);
        try {
            System.out.println("AI 요청 JSON 직전 = " +
                    objectMapper.writeValueAsString(fullRequest));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aiService.callAiServer(fullRequest);
    }
}
