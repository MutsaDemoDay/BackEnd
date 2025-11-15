package backend.stamp.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPasswordResponse {
    private String resetToken;
}
