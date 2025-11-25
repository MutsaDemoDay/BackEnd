package backend.stamp.store.controller;


import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.store.dto.StoreSearchResponseDto;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.store.service.StoreService;
import backend.stamp.store.service.StoreShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")

@Tag(name = "Store ( 매장 관련 )", description = "store( 매장 관련 ) API")

public class StoreController {
    private final StoreService storeService;
    private final StoreShareService storeShareService;

    //매장 검색 ( 부분 검색 )
    @Operation(summary = "매장검색 api", description = "유저가 매장을 검색합니다.")

    @GetMapping("/search")
    public List<StoreSearchResponseDto> searchStores(@RequestParam String storeName) {
        return storeService.getSearchedStores(storeName);
    }


    //매장 전체 조회
    @Operation(summary = "매장 전체 조회 api", description = "DB에 있는 전체 매장을 조회합니다.")

    @GetMapping
    public ApplicationResponse<List<StoreSearchResponseDto>> getAllStores() {
        List<StoreSearchResponseDto> response = storeService.getAllStores();
        return ApplicationResponse.ok(response);
    }

    //스토어 공유 url 생성

    @Operation(summary = "매장 공유 링크 생성", description = "매장 상세 정보를 공유할 수 있는 URL을 생성합니다.")
    @GetMapping("/{storeId}/share")
    public ApplicationResponse<String> getStoreShareUrl(@PathVariable Long storeId) {
        String shareUrl = storeShareService.createShareUrl(storeId);
        return ApplicationResponse.ok(shareUrl);
    }


}

