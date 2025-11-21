package backend.stamp.eventstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class JoinStoreResponseDto {
    private Long StoreId;
    private String storeName;
    private String storeAddress;
    private List<String> menuNames;
}
