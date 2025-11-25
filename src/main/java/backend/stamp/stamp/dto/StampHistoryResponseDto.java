package backend.stamp.stamp.dto;

import backend.stamp.coupon.entity.Coupon;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StampHistoryResponseDto {

    private Long storeId;
    private String storeName;
    private String storeAddress;

    //날짜만 표시하도록
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime issuedDate;

    public static StampHistoryResponseDto from(Coupon coupon) {
        return StampHistoryResponseDto.builder()
                .storeId(coupon.getStore().getId())
                .storeName(coupon.getStore().getName())
                .storeAddress(coupon.getStore().getAddress())
                .issuedDate(coupon.getCreatedDate()) // BaseEntity에서 가지고 오기
                .build();
    }
}
