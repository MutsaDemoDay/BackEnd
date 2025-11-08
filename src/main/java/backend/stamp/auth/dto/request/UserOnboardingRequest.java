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
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하로 입력해주세요.")
    private String nickname;

    @NotNull(message = "성별은 필수 입력값입니다.")
    private Gender gender;

    private List<Long> favStoreId;
}
