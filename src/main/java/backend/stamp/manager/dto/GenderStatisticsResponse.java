package backend.stamp.manager.dto;

public record GenderStatisticsResponse(
        Long total,
        Long womanCount,
        Long manCount,
        Double womanRatio,
        Double manRatio
) {}