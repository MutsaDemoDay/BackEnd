package backend.stamp.store.controller;

import backend.stamp.store.dto.StoreSearchResponseDto;
import backend.stamp.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@Tag(name = "Store Map ( 매장 지도 및 검색 )", description = "지도 기반 매장 조회/검색 API")
public class StoreMapController {
    private final StoreService storeService;

    @Operation(summary = "내 주변 가까운 매장 조회", description = "사용자 위치에서 가장 가까운 5개 매장을 거리순으로 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<StoreSearchResponseDto>> getNearbyStores(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        List<StoreSearchResponseDto> response = storeService.getNearbyStores(latitude, longitude, 5);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매장 검색 및 거리순 정렬", description = "매장 이름으로 검색하고, 결과를 사용자 위치 기준 거리순으로 정렬합니다.")
    @GetMapping("/search-distance")
    public ResponseEntity<List<StoreSearchResponseDto>> searchStoresByDistance(
            @RequestParam String storeName,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        List<StoreSearchResponseDto> response = storeService.getSearchedStoresByDistance(storeName, latitude, longitude);
        return ResponseEntity.ok(response);
    }
}
