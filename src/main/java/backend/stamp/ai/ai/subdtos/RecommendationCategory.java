package backend.stamp.ai.ai.subdtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendationCategory(

        @JsonProperty("category")
        String category,

        @JsonProperty("stores")
        List<StoreName> stores

) {}
