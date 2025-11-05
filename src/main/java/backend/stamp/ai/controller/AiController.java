package backend.stamp.ai.controller;

import backend.stamp.ai.ai.AiRequest;
import backend.stamp.ai.ai.AiResponse;
import backend.stamp.ai.service.AiService;
import backend.stamp.ai.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class AiController {
    private final AiService aiService;
    private final RecommendService recommendService;
    @PostMapping("/call")
    public Mono<AiResponse> callAi(@RequestBody AiRequest partialRequest) {
        AiRequest fullRequest = recommendService.buildFullAiRequest(partialRequest);
        return aiService.callAiServer(fullRequest);
    }
}
