package backend.stamp.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import backend.stamp.event.entity.EventType;

import java.time.LocalDate;
import java.util.List;

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

    private List<Long> participatingStores;  // 이 이벤트에 참여중인 storeId 목록


}
