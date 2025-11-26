package backend.stamp.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyResponse {
    private String emailVerificationToken;
}
