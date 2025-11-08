package backend.stamp.stamp.dto;

import backend.stamp.stamp.entity.Stamp;

import java.time.LocalDateTime;


import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StampAddResponseDto {
    private Long userId;
    private Long storeId;
    private Long stampId;
    private Long orderId;
    private LocalDateTime orderDate;
    private String storeName;
    //스탬프에 보여지는 리워드
    private String stampReward;
    private Integer currentCount;
    private Integer maxCount;

    public static StampAddResponseDto from(Stamp stamp) {
        return StampAddResponseDto.builder()
                .userId(stamp.getUsers().getUserId())
                .storeId(stamp.getStore().getId())
                .stampId(stamp.getId())
                .orderId(stamp.getOrder().getId())
                //주문 날짜
                .orderDate(stamp.getOrder().getOrderDate())
                .storeName(stamp.getStore().getName())
                .stampReward(stamp.getStore().getStampReward())
                .currentCount(stamp.getCurrentCount())
                .maxCount(stamp.getStore().getMaxCount())
                .build();
    }
}
