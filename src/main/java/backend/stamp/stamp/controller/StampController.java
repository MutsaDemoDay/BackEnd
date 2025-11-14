package backend.stamp.stamp.controller;


import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.dto.*;
import backend.stamp.stamp.service.StampDetailService;
import backend.stamp.stamp.service.StampFavoriteService;
import backend.stamp.stamp.service.StampService;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stamps")

@Tag(name = "Stamp ( 등록 ,적립 ,삭제 관련 )", description = "스탬프 등록, 적립 , 삭제 관련 API")
public class StampController {

    private final StampService stampService;
    private final StampDetailService stampDetailService;
    private final StampFavoriteService stampFavoriteService;

    //스탬프 등록 -> by 가게 검색

    @Operation(summary = "스탬프 등록 api", description = "유저가 원하는 매장의 스탬프판을 새로 등록합니다.")
    @PostMapping
    public ResponseEntity<StampCreateResponseDto> createStamp(@AuthenticationPrincipal PrincipalDetails userDetail, @RequestBody StampCreateRequestDto requestDto) {

        //로그인한 유저 식별용 갖고 오기
        Long userId = userDetail.getAccount().getAccountId();

        return ResponseEntity.ok(stampService.createStamp(userId,requestDto));

    }

    //스탬프 적립

    @Operation(summary = "스탬프 적립 api", description = "유저가 원하는 매장의 스탬프판에 스탬프를 적립합니다.")
    @PostMapping("/add")
    public ResponseEntity<StampAddResponseDto> addStamp(@AuthenticationPrincipal PrincipalDetails userDetail,@RequestBody StampAddRequestDto requestDto) {
       //유저
        Long userId = userDetail.getAccount().getAccountId();
        Long storeId = requestDto.getStoreId();
        Long orderId = requestDto.getOrderId();

        StampAddResponseDto response = stampService.addStamp(userId, storeId, orderId);
    //서비스 호출

        return ResponseEntity.ok(response);


    }
    
    //스탬프 삭제

    @Operation(summary = "스탬프 삭제 api", description = "유저가 자신의 스탬프판을 삭제합니다.")
    @DeleteMapping("/{stampId}")
    public ResponseEntity<ApplicationResponse<Void>> deleteStamp(
            @AuthenticationPrincipal PrincipalDetails userDetail,
            @PathVariable Long stampId) {

        Long userId = userDetail.getAccount().getAccountId();
        stampService.deleteStamp(userId, stampId);

        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }

    //스탬프 개별조회

    @Operation(summary = "스탬프 개별조회 api", description = "유저가 자신의 스탬프를 개별적으로 조회합니다.")
    @GetMapping("/{stampId}")
    public ResponseEntity<ApplicationResponse<MyStampResponseDto>> getStampDetail(@AuthenticationPrincipal PrincipalDetails userDetail, @PathVariable Long stampId) {
        Long userId = userDetail.getAccount().getAccountId();

        //값 받아오기
        MyStampResponseDto responseDto = stampDetailService.getStampDetail(userId, stampId);
        return ResponseEntity.ok(ApplicationResponse.ok(responseDto));
    }


}
