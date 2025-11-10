package backend.stamp.ai.ai.store;

import backend.stamp.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EventStore(

        @JsonProperty("store_address")
        String storeAddress,

        @JsonProperty("exp_multiplier")
        double expMultiplier
) {
        public static EventStore fromEntity(Store store) {
                return new EventStore(
                        store.getAddress(),
                        store.getEventApply() != null ? store.getEventApply().doubleValue() : 1.0
                );
        }
}
