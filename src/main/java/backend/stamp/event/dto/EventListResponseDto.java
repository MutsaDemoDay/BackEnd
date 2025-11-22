package backend.stamp.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import backend.stamp.event.entity.EventType;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EventListResponseDto {

    private EventType eventType;
    private String buttonDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private String buttonImageUrl;

}
