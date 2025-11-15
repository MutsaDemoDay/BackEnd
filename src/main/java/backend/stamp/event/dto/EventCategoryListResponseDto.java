package backend.stamp.event.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EventCategoryListResponseDto {
    private List<EventCategoryResponseDto> availableCategories;
}
