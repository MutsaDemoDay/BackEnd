package backend.stamp.ai.ai;

import backend.stamp.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendationCategory(

        @JsonProperty("category")
        String category,

        @JsonProperty("stores")
        List<Store> stores

) {}
