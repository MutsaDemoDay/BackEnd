package backend.stamp.manager.dto.dashboard;

public record GenderStatisticsResponse(
        Long total,
        Long womanCount,
        Long manCount,
        Double womanRatio,
        Double manRatio
) {}