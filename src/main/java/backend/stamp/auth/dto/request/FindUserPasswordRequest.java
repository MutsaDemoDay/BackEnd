package backend.stamp.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindUserPasswordRequest {
    private String loginId;
    private String email;
}
