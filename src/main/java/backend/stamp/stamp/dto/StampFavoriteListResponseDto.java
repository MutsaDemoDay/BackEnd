package backend.stamp.stamp.dto;

import backend.stamp.stamp.entity.Stamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StampFavoriteListResponseDto {

    private Long storeId;
    private String storeName;
    private String stampImageUrl;
    private Integer currentCount;
    private Integer maxCount;
    private boolean isFavorite;

    public static StampFavoriteListResponseDto from(Stamp stamp) {
        return StampFavoriteListResponseDto.builder()
                .storeId(stamp.getStore().getId())
                .storeName(stamp.getStore().getName())
                .stampImageUrl(stamp.getStore().getStampImageUrl())
                .currentCount(stamp.getCurrentCount())
                .maxCount(stamp.getStore().getMaxCount())
                .isFavorite(stamp.isFavorite())
                .build();
    }
}
