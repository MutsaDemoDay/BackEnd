package backend.stamp.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponseListDto {
    private Integer myCouponNum;
    private List<CouponResponseDto> couponLists;

}
