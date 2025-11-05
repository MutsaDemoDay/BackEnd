package backend.stamp.ai.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiResponse(

        @JsonProperty("success")
        boolean success,

        @JsonProperty("user_id")
        String userId,

        @JsonProperty("recommendations")
        List<RecommendationCategory> recommendations

) {}
