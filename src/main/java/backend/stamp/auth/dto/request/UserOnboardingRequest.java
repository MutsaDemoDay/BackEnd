package backend.stamp.auth.dto.request;

import backend.stamp.users.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserOnboardingRequest {

    @NotNull(message = "성별은 필수 입력값입니다.")
    private Gender gender;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;

    private Double latitude;
    private Double longitude;

    private List<Long> favStoreId;
}
