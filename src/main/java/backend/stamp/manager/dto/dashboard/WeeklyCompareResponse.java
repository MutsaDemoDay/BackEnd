package backend.stamp.manager.dto.dashboard;

public record WeeklyCompareResponse(
        String title,               // 예: "등록 고객 수", "이벤트 기간"
        Long currentWeekAvg,
        Long lastWeekAvg,
        Double percentageDiff,
        String diffText
) {}
