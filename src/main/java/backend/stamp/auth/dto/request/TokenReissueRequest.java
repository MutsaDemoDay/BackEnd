package backend.stamp.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenReissueRequest {
    @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
    private String refreshToken;
}
