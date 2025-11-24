package backend.stamp.manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ManagerAccountInfoResponse {
    private String email;
    private String loginId;
    private LocalDateTime joinedAt;
}
