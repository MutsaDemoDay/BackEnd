package backend.stamp.auth.kakao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record KakaoUser(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        String nickname,
        String accessToken
) {
}
