package backend.stamp.ai.ai.subdtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VisitStatics (
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("store_address")
        String storeAddress,
        @JsonProperty("visit_count")
        Long visitCount
)
{}
