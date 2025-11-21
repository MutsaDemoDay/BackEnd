package backend.stamp.event.dto;

import backend.stamp.event.entity.EventType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventApplyResponseDto {
    private Long eventStoreId; //   이벤트신청목록
    private EventType eventType;    // 신청한 이벤트 타입
    private String title;           // 이벤트 제목
    private String message;         // 응답 메시지
}