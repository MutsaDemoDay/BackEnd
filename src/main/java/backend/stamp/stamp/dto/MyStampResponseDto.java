package backend.stamp.stamp.dto;


import backend.stamp.stamp.entity.Stamp;
import jakarta.persistence.Column;
import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//내 스탬프 (현재) 관리 목록 조회
public class MyStampResponseDto {
    private Long stampId;
private String storeName;
private String StampImageUrl;
private Integer currentCount;
private Integer maxCount;
private boolean isFavorite;

public static MyStampResponseDto from(Stamp stamp) {
    return MyStampResponseDto.builder()
            .stampId(stamp.getId())
            .storeName(stamp.getStore().getName())
            .StampImageUrl(stamp.getStore().getStampImageUrl())
            .currentCount(stamp.getCurrentCount())
            .maxCount(stamp.getStore().getMaxCount())
            .isFavorite(stamp.isFavorite())
            .build();
}

}
