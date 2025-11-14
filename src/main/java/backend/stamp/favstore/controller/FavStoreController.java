package backend.stamp.favstore.controller;

import backend.stamp.favstore.dto.FavStoreListResponseDto;
import backend.stamp.favstore.service.FavStoreService;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favstores")

@Tag(name = "FavStore (즐겨찾는 매장)", description = "매장 즐겨찾기 관련 API")
public class FavStoreController {

    private final FavStoreService favStoreService;


    // 매장 즐겨찾기 등록

    @Operation(summary = "매장 즐겨찾기 등록 api", description = "로그인한 유저가 매장 즐겨찾기를 등록합니다.")
    @PostMapping("/{storeId}")
    public ResponseEntity<ApplicationResponse<Void>> createFavStore(@AuthenticationPrincipal PrincipalDetails userDetail,  @PathVariable Long storeId) {

        //유저 조회
        Long userId =userDetail.getAccount().getAccountId();

        //매장 조회
        favStoreService.createFavStore(userId,storeId);
        return ResponseEntity.ok(ApplicationResponse.ok(null));

    }
    //매장 즐겨찾기 취소
    @Operation(summary = "매장 즐겨찾기 취소 api", description = "로그인한 유저가 매장 즐겨찾기를 취소합니다.")

    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApplicationResponse<Void>> deleteFavStore(@AuthenticationPrincipal PrincipalDetails userDetail, @PathVariable Long storeId) {

        Long userId = userDetail.getAccount().getAccountId();
        favStoreService.deleteFavStore(userId, storeId);

        return ResponseEntity.ok(ApplicationResponse.ok(null));
    }

    //즐겨찾기 매장 리스트 조회
    @Operation(summary = "매장 즐겨찾기 리스트 조회 api", description = "즐겨찾기 매장 리스트를 조회합니다.")


    @GetMapping
    public ResponseEntity<ApplicationResponse<List<FavStoreListResponseDto>>> getMyFavStores(@AuthenticationPrincipal PrincipalDetails userDetail, @RequestParam double userLat,@RequestParam double userLon)
    {
        Long userId = userDetail.getAccount().getAccountId();

        List<FavStoreListResponseDto> response = favStoreService.getMyFavStores(userId,userLat,userLon);
        return ResponseEntity.ok(
                ApplicationResponse.ok(response));
    }

}
