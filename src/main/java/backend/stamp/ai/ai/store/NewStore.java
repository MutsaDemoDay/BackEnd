package backend.stamp.ai.ai.store;

import backend.stamp.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record NewStore(

        @JsonProperty("store_id")
        String storeId,
        @JsonProperty("joined_date")
        LocalDate joinedDate

) {
        public static NewStore fromEntity(Store store) {
                return new NewStore(
                        store.getId().toString(),
                        store.getJoinDate()
                );
        }
}
