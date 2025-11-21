package backend.stamp.event.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventApplyResponseDto;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.dto.EventMenuRequestDto;
import backend.stamp.event.entity.EventType;
import backend.stamp.event.service.EventService;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다."),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "요청하신 가게 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "421", description = "매장의 등록 날짜가 생성되지 않았습니다."),
            @ApiResponse(responseCode = "422", description = "등록된 이벤트가 없습니다.")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이벤트 신청 성공"),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "요청하신 가게 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "422", description = "등록된 이벤트가 없습니다."),
            @ApiResponse(responseCode = "423", description = "이미 이번 달에 해당 이벤트를 신청했습니다."),
            @ApiResponse(responseCode = "424",description = "이벤트 신청을 위해서는 대표 메뉴 3개가 필요합니다.")
    })
    @PostMapping("{eventType}/apply")

    public ApplicationResponse<EventApplyResponseDto> applyEvent(@AuthenticationPrincipal PrincipalDetails userDetails, @PathVariable("eventType") EventType eventType , @RequestBody EventMenuRequestDto request) {
        Account account = userDetails.getAccount();
        EventApplyResponseDto response = eventService.applyEvent(account,eventType,request);
        return ApplicationResponse.ok(response);
    }



}
