package backend.stamp.stamp.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.dto.*;
import backend.stamp.stamp.service.StampDetailService;
import backend.stamp.stamp.service.StampFavoriteService;
import backend.stamp.stamp.service.StampService;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스탬프 등록 성공",
                    content = @Content(schema = @Schema(implementation = StampCreateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청하신 가게 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "410", description = "해당 매장의 스탬프판이 이미 등록되어 있습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class)))
    })
    @PostMapping
    public ResponseEntity<StampCreateResponseDto> createStamp(@AuthenticationPrincipal PrincipalDetails userDetail, @RequestBody StampCreateRequestDto requestDto) {

        //로그인한 유저 식별용 갖고 오기
        Account account = userDetail.getAccount();

        return ResponseEntity.ok(stampService.createStamp(account,requestDto));

    }

    //스탬프 적립

    @Operation(summary = "스탬프 적립 api", description = "유저가 원하는 매장의 스탬프판에 스탬프를 적립합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스탬프 적립 성공",
                    content = @Content(schema = @Schema(implementation = StampAddResponseDto.class))),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청하신 가게 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "411", description = "요청하신 주문 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "412", description = "해당 주문은 선택한 매장의 주문이 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "413", description = "이미 이 주문에 대해 스탬프가 적립되었습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "414", description = "해당 매장의 스탬프판이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class)))
    })
    @PostMapping("/add")
    public ResponseEntity<StampAddResponseDto> addStamp(@AuthenticationPrincipal PrincipalDetails userDetail,@RequestBody StampAddRequestDto requestDto) {
       //유저
        Account account = userDetail.getAccount();
        Long storeId = requestDto.getStoreId();
        Long orderId = requestDto.getOrderId();

        StampAddResponseDto response = stampService.addStamp(account, storeId, orderId);
    //서비스 호출

        return ResponseEntity.ok(response);


    }
    
    //스탬프 삭제

    @Operation(summary = "스탬프 삭제 api", description = "유저가 자신의 스탬프판을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스탬프 삭제 성공",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "405", description = "요청하신 스탬프 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "403", description = "제한된 접근입니다. (본인 스탬프 아님)",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class)))
    })
    @DeleteMapping("/{stampId}")
    public ResponseEntity<ApplicationResponse<Void>> deleteStamp(
            @AuthenticationPrincipal PrincipalDetails userDetail,
            @PathVariable Long stampId) {

        Account account = userDetail.getAccount();
        stampService.deleteStamp(account, stampId);

        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }

    //스탬프 개별조회

    @Operation(summary = "스탬프 개별조회 api", description = "유저가 자신의 스탬프를 개별적으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스탬프 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyStampResponseDto.class))),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "405", description = "요청하신 스탬프 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "403", description = "제한된 접근입니다. (본인 스탬프 아님)",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class)))
    })
    @GetMapping("/{stampId}")
    public ResponseEntity<ApplicationResponse<MyStampResponseDto>> getStampDetail(@AuthenticationPrincipal PrincipalDetails userDetail, @PathVariable Long stampId) {
        Account account = userDetail.getAccount();

        //값 받아오기
        MyStampResponseDto responseDto = stampDetailService.getStampDetail(account, stampId);
        return ResponseEntity.ok(ApplicationResponse.ok(responseDto));
    }


}
