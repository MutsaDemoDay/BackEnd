package backend.stamp.store.dto;

import backend.stamp.store.entity.Category;
import backend.stamp.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSearchResponseDto {
    private Long storeId;
    private String storeName;
    private String storeAddress;
    private Category category;
    private String phone;
    private String storeUrl;
    private String stampImageUrl;
    private String storeImageUrl;
    private String reward;
    private String stampReward;
    private String sns;


    public static StoreSearchResponseDto from(Store store) {
        return StoreSearchResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .storeAddress(store.getAddress())
                .category(store.getCategory())
                .phone(store.getPhone())
                .stampImageUrl(store.getStampImageUrl())
                .storeImageUrl(store.getStoreImageUrl())
                .reward(store.getReward())
                .stampReward(store.getStampReward())
                .sns(store.getSns())
                .build();
    }
}
