package backend.stamp.users.dto;

import backend.stamp.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocalStoreResponseDto {

    private Long storeId;
    private String storeName;
    private String address;
    private String category;
    private String storeImageUrl;

    public static UserLocalStoreResponseDto from(Store store) {
        return UserLocalStoreResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .address(store.getAddress())
                .category(store.getCategory().name())
                .storeImageUrl(store.getStoreImageUrl())
                .build();
    }
}
