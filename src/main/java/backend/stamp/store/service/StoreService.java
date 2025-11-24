package backend.stamp.store.service;

import backend.stamp.account.entity.Account;
import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.store.dto.StoreSearchResponseDto;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final CouponRepository couponRepository;

    private record OperationStatusResponse(String status, String message) {
    }

    // -----------------------
    // 1) DB 내에서 매장 검색
    // -----------------------
    @Transactional(readOnly = true)
    public List<StoreSearchResponseDto> getSearchedStores(String storeName) {

        // 이름으로 키워드 검색 진행
        List<Store> stores = storeRepository.findByNameContaining(storeName);

        return stores.stream()
                .map(StoreSearchResponseDto::from)
                .collect(Collectors.toList());
    }

    // 2) 내 주변 가게 조회
    @Transactional(readOnly = true)
    public List<StoreSearchResponseDto> getNearbyStores(Double latitude, Double longitude, int limit) {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
                .map(store -> {
                    Double distance = computeDistanceSafe(latitude, longitude, store.getLatitude(), store.getLongitude());
                    return StoreSearchResponseDto.from(store, distance);
                })
                .sorted(Comparator.comparing(
                        StoreSearchResponseDto::getDistanceMeters,
                        Comparator.nullsLast(Double::compareTo)))
                .limit(limit)
                .collect(Collectors.toList());
    }


    //DB에 존재하는 전체 매장 조회

    @Transactional
    public List<StoreSearchResponseDto> getAllStores() {
        List<Store> stores = storeRepository.findAll();

        return stores.stream().map(StoreSearchResponseDto::from)
                .collect(Collectors.toList());

    }

    // 3) 이름 기반 + 거리순 검색
    @Transactional(readOnly = true)
    public List<StoreSearchResponseDto> getSearchedStoresByDistance(String storeName, Double latitude, Double longitude) {
        List<Store> stores = storeRepository.findByNameContaining(storeName);

        return stores.stream()
                .map(store -> {
                    Double distance = computeDistanceSafe(latitude, longitude, store.getLatitude(), store.getLongitude());
                    return StoreSearchResponseDto.from(store, distance);
                })
                .sorted(Comparator.comparing(
                        StoreSearchResponseDto::getDistanceMeters,
                        Comparator.nullsLast(Double::compareTo)))
                .collect(Collectors.toList());
    }

    private Double computeDistanceSafe(Double userLat, Double userLon, Double storeLat, Double storeLon) {

        if (userLat == null || userLon == null) {
            return null;
        }

        if (storeLat == null || storeLon == null) {
            return null;
        }
        return calculateDistance(userLat, userLon, storeLat, storeLon);
    }


private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
    final int R = 6_371_000;

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

}
