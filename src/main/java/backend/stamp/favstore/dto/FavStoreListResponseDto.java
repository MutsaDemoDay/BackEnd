package backend.stamp.favstore.dto;


import backend.stamp.store.entity.Category;
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

    private String distance; //m 단위로 !!
    private boolean isFavorite; // 즐겨찾기 여부 표시



}
