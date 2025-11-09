package backend.stamp.auth.dto.request;

import backend.stamp.store.entity.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ManagerOnboardingRequest {

    @NotBlank(message = "매장 이미지 URL은 필수입니다.")
    private String storeImageUrl;

    @NotBlank(message = "매장명은 필수입니다.")
    private String storeName;

    private Category category;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;

    private Double latitude;
    private Double longitude;

    @NotNull(message = "대표 번호 사용 여부는 필수입니다.")
    private Boolean useManagerPhoneNumber; // 휴대폰 번호와 동일 여부

    // useManagerPhoneNumber가 false일 때만 사용
    @Pattern(regexp = "^\\d{2,4}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식(XXX-XXXX-XXXX)이 아닙니다.")
    private String storeTel;

    @NotEmpty(message = "영업시간 정보는 최소 하나 이상 필요합니다.")
    @Valid
    private List<BusinessHourRequest> businessHours;

    @NotBlank(message = "스탬프 이미지 URL은 필수입니다.")
    private String stampImageUrl;

    @NotNull(message = "최소 적립 금액 조건은 필수입니다.")
    @Min(value = 0, message = "최소 적립 금액은 0 이상이어야 합니다.")
    private Integer requiredAmount;

    private String reward;
}
