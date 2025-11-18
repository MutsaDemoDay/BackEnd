package backend.stamp.eventstore.controller;

import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.eventstore.dto.EndedEventListResponseDto;
import backend.stamp.eventstore.service.EventStoreService;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/eventstores")

@Tag(name = "EventStore ( 이벤트 - 신청 매장 조회 관련 )", description = "이벤트와 신청 매장 관련 API")
public class EventStoreController {

    private final EventStoreService eventStoreService;

    //지난 이벤트 조회
    @Operation(summary = "지난 이벤트 목록 조회")
    @GetMapping("/ended")
    public ApplicationResponse<List<EndedEventListResponseDto>> getEndedCategories(@AuthenticationPrincipal PrincipalDetails userDetails) {
        Account account = userDetails.getAccount();
        List<EndedEventListResponseDto> response =eventStoreService.getEndedEventList(account);

        return ApplicationResponse.ok(response);
    }

}
