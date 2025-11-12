package backend.stamp.ai.ai.store;

import backend.stamp.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PopularStore(

        @JsonProperty("store_address")
        String storeAddress,

        @JsonProperty("visit_count")
        int visitCount

) {
        public static PopularStore fromEntity(Store store, int visitCount) {
                return new PopularStore(
                        store.getAddress(),
                        visitCount
                );
        }
}
