package backend.stamp.ai.ai.store;

import backend.stamp.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record NewStore(

        @JsonProperty("store_address")
        String storeAddress,
        @JsonProperty("joined_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate joinedDate

) {
        public static NewStore fromEntity(Store store) {
                return new NewStore(
                        store.getAddress(),
                        store.getJoinDate()
                );
        }
}
