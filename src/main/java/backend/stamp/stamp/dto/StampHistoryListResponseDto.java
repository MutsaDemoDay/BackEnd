package backend.stamp.stamp.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StampHistoryListResponseDto {


    private Integer totalStampSum;
    private Integer completedStampNum;
    List<StampHistoryResponseDto> completedStamps;

}
