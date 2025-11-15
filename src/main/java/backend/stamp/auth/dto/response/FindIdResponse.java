package backend.stamp.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@Builder
public class FindIdResponse {
    private String loginId;
    private LocalDate createdAt;

}
