package backend.stamp.eventstore.dto;

import backend.stamp.event.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OngoingEventResponseDto {

    private Long eventId;
    private EventType eventType;
    private String buttonDescription;
    private String inPageDescription;
    private LocalDate startDate;
    private LocalDate endDate;

    List<JoinStoreResponseDto> JoinStoreLists;

}
