package backend.stamp.event.dto;

import backend.stamp.event.entity.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventCategoryResponseDto {
    private EventType eventType;
    private boolean available;// 점주가 선택 가능한지
    private String description;

    //이벤트 시작 시간
    private LocalDateTime startDate;
    //이벤트 끝나는 시간
    private LocalDateTime endDate;


}

