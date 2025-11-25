package backend.stamp.manager.dto.dashboard;

import java.util.List;

public record StampStatisticsTotalResponse(
        String type,
        Long avgCount,
        int total,
        String periodText,
        List<StampChartData> chartData
) {
}
