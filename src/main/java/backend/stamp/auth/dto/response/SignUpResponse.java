package backend.stamp.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignUpResponse {
    private String message;
    private Long userId;
    private Long managerId; // 유저 가입 시 null
    private String userType;
    private String accessToken;
    private String refreshToken;
    private boolean userOnboarded;
    private boolean managerOnboarded;
}
