package backend.stamp.favstore.dto;


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
public class FavStoreListResponseDto {

    private Long storeId;
    private String storeName;
    private Category storeCategory;
    private String storeAddress;
    private String storeImageUrl;

    private boolean isFavorite; // 즐겨찾기 여부 표시



    public static FavStoreListResponseDto from(Store store,boolean isFavorite) {
        return FavStoreListResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .storeCategory(store.getCategory())
                .storeAddress(store.getAddress())
                .storeImageUrl(store.getStoreImageUrl())
                .isFavorite(isFavorite)
                .build();

    }
}
