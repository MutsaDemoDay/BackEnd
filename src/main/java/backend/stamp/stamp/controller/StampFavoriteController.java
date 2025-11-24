package backend.stamp.stamp.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.dto.StampFavoriteListResponseDto;
import backend.stamp.stamp.service.StampFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stamps")

@Tag(name = "Stamp (스탬프 즐겨찾기 관련)", description = "스탬프 즐겨찾기 관련 API")
public class StampFavoriteController {

    private final StampFavoriteService stampFavoriteService;

    //스탬프 즐겨찾기 설정
    @Operation(summary = "스탬프 즐겨찾기 설정 api", description = "유저가 자신의 개별 스탬프를 즐겨찾기로 설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "즐겨찾기 설정 성공",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "405", description = "요청하신 스탬프 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근이 제한되었습니다. (본인 스탬프 아님)",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "406", description = "이미 즐겨찾기로 설정된 스탬프입니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class)))
    })
    @PostMapping("/{stampId}/favorite")
    public ResponseEntity<ApplicationResponse<Void>> createFavoriteStamp(@AuthenticationPrincipal PrincipalDetails userDetail, @PathVariable Long stampId) {
        Account account = userDetail.getAccount();
        stampFavoriteService.createFavoriteStamp(account, stampId);
        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }

    // 스탬프 즐겨찾기 취소
    @Operation(summary = "스탬프 즐겨찾기 취소 api", description = "유저가 자신의 스탬프 즐겨찾기를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "즐겨찾기 취소 성공",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "402", description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "405", description = "요청하신 스탬프 정보를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근이 제한되었습니다. (본인 스탬프 아님)",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "408", description = "해당 스탬프가 즐겨찾기 상태가 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class)))
    })
    @DeleteMapping("/{stampId}/favorite")
    public ResponseEntity<ApplicationResponse<Void>> deleteFavoriteStamp(
            @AuthenticationPrincipal PrincipalDetails userDetail,
            @PathVariable Long stampId
    ) { Account account = userDetail.getAccount();
        stampFavoriteService.deleteFavoriteStamp(account, stampId);
        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }


    //스탬프 즐겨찾기 리스트 조회
    @Operation(summary = "스탬프 즐겨찾기 리스트 조회 api", description = "유저의 스탬프 즐겨찾기 리스트를 조회합니다. ")
    @GetMapping
    public ApplicationResponse<List<StampFavoriteListResponseDto>> getFavoriteStampLists(@AuthenticationPrincipal PrincipalDetails userDetails)
    {
        Account account = userDetails.getAccount();
        List<StampFavoriteListResponseDto> response =stampFavoriteService.getFavoriteStampLists(account);
        return ApplicationResponse.ok(response);
    }

}
