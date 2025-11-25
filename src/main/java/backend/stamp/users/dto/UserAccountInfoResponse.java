package backend.stamp.users.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserAccountInfoResponse {
    private String email;
    private String loginId;
    private LocalDateTime joinedAt;
}
