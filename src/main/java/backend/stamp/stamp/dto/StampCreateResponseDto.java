package backend.stamp.stamp.dto;


import backend.stamp.stamp.entity.Stamp;
import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StampCreateResponseDto {
    private Long userId;
    private Long storeId;
    private Long stampId;
    private String storeName;
    private String reward;
    private Integer currentCount;
    private Integer maxCount;

    public static StampCreateResponseDto from(Stamp stamp) {
        return StampCreateResponseDto.builder()
                .userId(stamp.getUsers().getUserId())
                .storeId(stamp.getStore().getId())
                .stampId(stamp.getId())
                .storeName(stamp.getStore().getName())
                .reward(stamp.getStore().getReward())
                .currentCount(stamp.getCurrentCount())
                .maxCount(stamp.getStore().getMaxCount())
                .build();
    }




}
