package backend.stamp.event.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventApplyResponseDto;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.entity.EventType;
import backend.stamp.event.service.EventService;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")

@Tag(name = "Event ( 점주 : 이벤트 신청 관련 )", description = "점주와 이벤트 관련 API")
public class EventController {
    private final EventService eventService;



    //현재 신청 가능한 이벤트 카테고리 조회

    @Operation(summary = "신청 가능한 이벤트 카테고리 조회")
    @GetMapping("/categories")
    public ApplicationResponse<EventCategoryListResponseDto> getAvailableCategories(
            @AuthenticationPrincipal PrincipalDetails userDetails
            ) {

        Account account = userDetails.getAccount();
        EventCategoryListResponseDto response = eventService.getAvailableCategories(account);

        return ApplicationResponse.ok(response);
    }


    //선택한 이벤트 신청
    @Operation(summary = "현재 열려있는 이벤트 신청")
    @PostMapping("{eventType}/apply")
    public ApplicationResponse<EventApplyResponseDto> applyEvent(@AuthenticationPrincipal PrincipalDetails userDetails, @PathVariable("eventType") EventType eventType) {
        Account account = userDetails.getAccount();
        EventApplyResponseDto response = eventService.applyEvent(account,eventType);
        return ApplicationResponse.ok(response);
    }



}
