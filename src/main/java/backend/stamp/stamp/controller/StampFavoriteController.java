package backend.stamp.stamp.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.service.StampFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stamps")

@Tag(name = "Stamp (스탬프 즐겨찾기 관련)", description = "스탬프 즐겨찾기 관련 API")
public class StampFavoriteController {

    private final StampFavoriteService stampFavoriteService;

    //스탬프 즐겨찾기 설정
    @Operation(summary = "스탬프 즐겨찾기 설정 api", description = "유저가 자신의 개별 스탬프를 즐겨찾기로 설정합니다.")

    @PostMapping("/{stampId}/favorite")
    public ResponseEntity<ApplicationResponse<Void>> createFavoriteStamp(@AuthenticationPrincipal PrincipalDetails userDetail, @PathVariable Long stampId) {
        Account account = userDetail.getAccount();
        stampFavoriteService.createFavoriteStamp(account, stampId);
        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }

    // 스탬프 즐겨찾기 취소
    @Operation(summary = "스탬프 즐겨찾기 취소 api", description = "유저가 자신의 스탬프 즐겨찾기를 취소합니다.")

    @DeleteMapping("/{stampId}/favorite")
    public ResponseEntity<ApplicationResponse<Void>> deleteFavoriteStamp(
            @AuthenticationPrincipal PrincipalDetails userDetail,
            @PathVariable Long stampId
    ) { Account account = userDetail.getAccount();
        stampFavoriteService.deleteFavoriteStamp(account, stampId);
        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }

}
