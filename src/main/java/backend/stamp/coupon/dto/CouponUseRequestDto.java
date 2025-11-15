package backend.stamp.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CouponUseRequestDto {

    @Schema(description = "매장 검증 코드", example = "5732")

    @NotBlank(message = "매장 인증 코드는 필수입니다.")
    @Size(min = 4, max = 4, message = "인증 코드는 4자리여야 합니다.")
    private String verificationCode;
}
