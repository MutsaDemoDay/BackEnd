package backend.stamp.auth.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NtsStatusResponse {
    @JsonProperty("request_cnt")
    private int requestCnt;

    @JsonProperty("match_cnt")
    private int matchCnt;

    @JsonProperty("status_code")
    private String statusCode;

    private List<NtsStatusData> data;
}
