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

    private String phone;

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
