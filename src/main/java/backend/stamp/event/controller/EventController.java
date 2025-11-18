package backend.stamp.event.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.service.EventService;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    //이벤트 신청






}
