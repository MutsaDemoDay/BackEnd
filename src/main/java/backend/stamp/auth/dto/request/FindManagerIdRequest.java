package backend.stamp.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindManagerIdRequest {
    private String businessNum;
    private String email;
}
