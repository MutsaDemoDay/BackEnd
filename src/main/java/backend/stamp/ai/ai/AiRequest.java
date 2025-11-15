package backend.stamp.ai.ai;
import backend.stamp.ai.ai.store.EventStoreDto;
import backend.stamp.ai.ai.store.NewStore;
import backend.stamp.ai.ai.store.PopularStore;
import backend.stamp.ai.ai.subdtos.UserLocation;
import backend.stamp.ai.ai.subdtos.VisitStatics;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("location")
    private UserLocation location;

    @JsonProperty("event_stores")
    private List<EventStoreDto> eventStoreDtos;

    @JsonProperty("new_stores")
    private List<NewStore> newStores;

    @JsonProperty("popular_stores")
    private List<PopularStore> popularStores;

    @JsonProperty("visit_statics")
    private List<VisitStatics> visitStatics;


}