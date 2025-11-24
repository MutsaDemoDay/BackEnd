package backend.stamp.manager.dto.dashboard;

import java.util.List;

public record StampStatisticsResponse(
        String type,              // "daily", "weekly", "monthly"
        Long totalOrAvgCount,     // 총합 또는 평균
        String periodText,        // "2025년 01월 01일" 또는 "2025년 01월 01일 - 2025년 01월 07일"
        List<StampChartData> chartData
) {}