package backend.stamp.store.service;

import backend.stamp.account.entity.Account;
import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.review.repository.ReviewRepository;
import backend.stamp.store.dto.StoreDetailHomeResponse;
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
    private final ReviewRepository reviewRepository;

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

    // 4) 가게 상세 - 홈
    @Transactional(readOnly = true)
    public StoreDetailHomeResponse getStoreDetailHome(Long storeId, Account account, Double userLatitude, Double userLongitude) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        Users user = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        boolean stampCompleted = couponRepository.existsByUsersAndStore(user, store);
        boolean reviewAlreadyWritten = reviewRepository.existsByUsersAndStore(user, store);
        boolean isReviewAvailable = stampCompleted && !reviewAlreadyWritten;

        List<StoreDetailHomeResponse.StoreMenuInfo> menus = store.getStoreMenus().stream()
                .map(menu -> StoreDetailHomeResponse.StoreMenuInfo.builder()
                        .menuName(menu.getName())
                        .content(menu.getContent())
                        .price(menu.getPrice())
                        .menuImageUrl(menu.getMenuImageUrl())
                        .build())
                .collect(Collectors.toList());

        OperationStatusResponse operationStatus = getOperationStatus(store);

        Double distanceMeters = null;
        if (userLatitude != null && userLongitude != null && store.getLatitude() != null && store.getLongitude() != null) {
            distanceMeters = calculateDistance(userLatitude, userLongitude, store.getLatitude(), store.getLongitude());
        }

        String stampRewardStr = store.getStampReward();

        return StoreDetailHomeResponse.builder()
                .storeId(store.getId())
                .name(store.getName())
                .category(store.getCategory())
                .address(store.getAddress())
                .storeImageUrl(store.getStoreImageUrl())
                .phone(store.getPhone())
                .storeUrl(store.getStoreUrl())
                .sns(store.getSns())
                .distanceMeters(distanceMeters)
                .status(operationStatus.status())
                .message(operationStatus.message())
                .reward(store.getReward())
                .stampReward(stampRewardStr)
                .stampImageUrl(store.getStampImageUrl())
                .maxCount(store.getMaxCount())
                .isReviewAvailable(isReviewAvailable)
                .signatureMenus(menus)
                .build();
    }

    // 헬퍼: 영업 상태 계산
    private OperationStatusResponse getOperationStatus(Store store) {
        if (store.getBusinessHours() == null || store.getBusinessHours().isEmpty()) {
            return new OperationStatusResponse("정보 없음", null);
        }

        LocalDateTime now = LocalDateTime.now();
        DayOfWeek todayOfWeek = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        // BusinessHour#day 컬럼이 "MON", "TUE" 같은 3글자 포맷
        String todayString = todayOfWeek.toString().substring(0, 3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        List<BusinessHour> todayHours = store.getBusinessHours().stream()
                .filter(bh -> bh.getDay() != null && bh.getDay().equalsIgnoreCase(todayString))
                .collect(Collectors.toList());

        if (todayHours.isEmpty()) {
            return new OperationStatusResponse("정보 없음", null);
        }

        boolean isHoliday = todayHours.stream().anyMatch(BusinessHour::isHoliday);
        if (isHoliday) {
            return new OperationStatusResponse("영업 종료", "오늘은 휴무일입니다.");
        }

        Optional<BusinessHour> currentStatus = todayHours.stream()
                .filter(bh -> {
                    LocalTime open = bh.getOpenTime();
                    LocalTime close = bh.getCloseTime();
                    if (open == null || close == null) return false;
                    // 영업 시작 시각 포함, 종료 시각은 미포함 (open <= now < close)
                    return !currentTime.isBefore(open) && currentTime.isBefore(close);
                })
                .findFirst();

        if (currentStatus.isPresent()) {
            LocalTime closeTime = currentStatus.get().getCloseTime();
            return new OperationStatusResponse("영업중", closeTime.format(formatter) + "까지");
        } else {
            Optional<BusinessHour> nextOpen = todayHours.stream()
                    .filter(bh -> {
                        LocalTime open = bh.getOpenTime();
                        return open != null && currentTime.isBefore(open);
                    })
                    .sorted(Comparator.comparing(BusinessHour::getOpenTime))
                    .findFirst();

            if (nextOpen.isPresent()) {
                LocalTime openTime = nextOpen.get().getOpenTime();
                return new OperationStatusResponse("영업 종료", openTime.format(formatter) + "에 시작");
            } else {
                return new OperationStatusResponse("영업 종료", "금일 영업이 종료되었습니다.");
            }
        }
    }

}
