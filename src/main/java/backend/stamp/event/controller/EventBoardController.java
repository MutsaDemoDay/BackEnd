package backend.stamp.event.controller;

import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventListResponseDto;
import backend.stamp.event.service.EventBoardService;
import backend.stamp.event.service.EventService;
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
@RequestMapping("/api/v1/events/board")

@Tag(name = "EventBoard ( 유저 : 이벤트 조회 관련 )", description = "유저 이벤트 조회 관련 API")
public class EventBoardController {

    private final EventBoardService eventBoardService;

    //이벤트 홍보 게시판 리스트 조회

    @Operation(summary = "스탬프 페이지 내 진행중 이벤트 리스트 조회")
    @GetMapping
    public ApplicationResponse<List<EventListResponseDto>> getOngoingEventLists(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Account account = principalDetails.getAccount();

        List<EventListResponseDto> response = eventBoardService.getOngoingEventLists(account);

        return ApplicationResponse.ok(response);
    }
}

