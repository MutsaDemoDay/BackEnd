package backend.stamp.manager.dto.dashboard;

public record WeeklyCustomerCompareResponse(
        Long currentWeekCount,
        Long lastWeekCount,
        Double diffPercent,
        String diffText
) {}