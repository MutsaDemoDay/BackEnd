package backend.stamp.coupon.dto;

import backend.stamp.coupon.entity.Coupon;

import java.time.LocalDateTime;


import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CouponResponseDto {

    private Long userId;
    private Long storeId;
    private Long couponId;
    private String couponName;
    private LocalDateTime expiredDate;
    private boolean used;

    public static CouponResponseDto from(Coupon coupon) {
        return CouponResponseDto.builder()
                .userId(coupon.getUsers().getUserId())
                .storeId(coupon.getStore().getId())
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .expiredDate(coupon.getExpiredDate())
                .used(coupon.isUsed())
                .build();
    }
}
