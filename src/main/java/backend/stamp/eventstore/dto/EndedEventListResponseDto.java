package backend.stamp.eventstore.dto;

import backend.stamp.event.entity.Event;
import backend.stamp.event.entity.EventType;
import backend.stamp.eventstore.entity.EventStore;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class EndedEventListResponseDto {
    private EventType eventType;
    private String EventTitle;
    private LocalDate EventStartDate;
    private LocalDate EventEndDate;

    public static EndedEventListResponseDto from (EventStore eventStore)
    {
        return EndedEventListResponseDto.builder()
                .eventType(eventStore.getEvent().getEventType())
                .EventTitle(eventStore.getEvent().getTitle())
                .EventStartDate(eventStore.getStartDate())
                .EventEndDate(eventStore.getEndDate())
                .build();
    }
}
