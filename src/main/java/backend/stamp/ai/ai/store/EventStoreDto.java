package backend.stamp.ai.ai.store;

import backend.stamp.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EventStoreDto(

        @JsonProperty("store_address")
        String storeAddress,

        @JsonProperty("exp_multiplier")
        double expMultiplier
) {
        public static EventStoreDto fromEntity(Store store) {
                return new EventStoreDto(
                        store.getAddress(),
                        store.getEventApply() != null ? store.getEventApply().doubleValue() : 1.0
                );
        }
}
