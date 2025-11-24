package backend.stamp.manager.service;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.level.entity.Level;
import backend.stamp.manager.dto.*;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Component
@RequiredArgsConstructor
public class ManagerService {
    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final StampRepository stampRepository;
    private final ObjectStorageService objectStorageService;
    public String setStamp(StampSettingRequest request, MultipartFile image){
        Store store = storeRepository.findByName(request.storeName())
                .orElseThrow(()-> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        //이미지
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = objectStorageService.uploadFile(image);
        }
        //세팅 값
        store.setRequiredAmount(request.requiredAmount());
        store.setReward(request.reward());
        store.setMaxCount(request.maxCnt());
        if (imageUrl != null) {
            store.setStampImageUrl(imageUrl);
        }
        storeRepository.save(store);
        return imageUrl;
    }

    public StampSettingResponse getStamp(String name){
        Store store = storeRepository.findByName(name)
                .orElseThrow(()-> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        return new StampSettingResponse(
                store.getName(),
                store.getRequiredAmount(),
                store.getReward(),
                store.getMaxCount(),
                store.getStampImageUrl()
        );
    }


    public List<StampCustomerResponse> getCustomers(String storeName) {
        List<Long> userIds = stampRepository.findUserIdsByStoreName(storeName);
        List<StampCustomerResponse> list = new ArrayList<>();
        for (Long userId : userIds) {
            Users user = usersRepository.findUserWithAccountAndLevel(userId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
            Account account = user.getAccount();
            Level level = user.getLevel();
            list.add(
                    new StampCustomerResponse(
                            user.getUserId(),
                            user.getNickname(),
                            account != null ? account.getCreatedAt() : null,
                            level != null ? level.getLevel() : null
                    )
            );
        }
        return list;
    }
    // 가게 스탬프 통계
    public StampStatisticsResponse getStampStatics(String storeName, String type) {
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        Long storeId = store.getId();
        return switch (type.toLowerCase()) {
            case "daily" -> getDailyStats(storeId);
            case "weekly" -> getWeeklyStats(storeId);
            case "monthly" -> getMonthlyStats(storeId);
            default -> throw new ApplicationException(ErrorCode.INVALID_REQUEST);
        };
    }

    private StampStatisticsResponse getDailyStats(Long storeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Stamp> stamps = stampRepository.findByStoreIdAndDateBetween(storeId, start, end);
        Map<Integer, Long> hourlyCount = new HashMap<>();
        for (int i = 0; i < 24; i++) hourlyCount.put(i, 0L);

        for (Stamp s : stamps) {
            int hour = s.getDate().getHour();
            hourlyCount.put(hour, hourlyCount.get(hour) + 1);
        }

        List<StampChartData> chartData = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            chartData.add(new StampChartData(i + "시", hourlyCount.get(i)));
        }

        return new StampStatisticsResponse(
                "daily",
                (long) stamps.size(),
                today.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")),
                chartData
        );
    }

    private StampStatisticsResponse getWeeklyStats(Long storeId) {

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.atTime(23, 59, 59);

        List<Stamp> stamps = stampRepository.findByStoreIdAndDateBetween(storeId, start, end);

        Map<DayOfWeek, Long> weekdayCount = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) weekdayCount.put(day, 0L);

        for (Stamp s : stamps) {
            DayOfWeek day = s.getDate().getDayOfWeek();
            weekdayCount.put(day, weekdayCount.get(day) + 1);
        }

        // 차트 데이터 (월~일 순)
        List<String> labels = List.of("월", "화", "수", "목", "금", "토", "일");

        List<StampChartData> chartData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DayOfWeek day = DayOfWeek.MONDAY.plus(i);
            chartData.add(new StampChartData(labels.get(i), weekdayCount.get(day)));
        }

        long avg = stamps.size() / 7;

        String periodText = weekStart.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
                + " - "
                + weekEnd.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return new StampStatisticsResponse(
                "weekly",
                avg,
                periodText,
                chartData
        );
    }

    private StampStatisticsResponse getMonthlyStats(Long storeId) {

        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);

        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        LocalDateTime start = monthStart.atStartOfDay();
        LocalDateTime end = monthEnd.atTime(23, 59, 59);

        List<Stamp> stamps = stampRepository.findByStoreIdAndDateBetween(storeId, start, end);

        // 일자별 카운트
        int days = ym.lengthOfMonth();
        Map<Integer, Long> dailyCount = new HashMap<>();
        for (int i = 1; i <= days; i++) dailyCount.put(i, 0L);
        for (Stamp s : stamps) {
            int dayOfMonth = s.getDate().getDayOfMonth();
            dailyCount.put(dayOfMonth, dailyCount.get(dayOfMonth) + 1);
        }

        List<StampChartData> chartData = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            chartData.add(new StampChartData(i + "일", dailyCount.get(i)));
        }

        long avg = (days == 0) ? 0 : stamps.size() / days;

        String periodText = today.format(DateTimeFormatter.ofPattern("yyyy년 MM월"));

        return new StampStatisticsResponse(
                "monthly",
                avg,
                periodText,
                chartData
        );
    }

    /**
     * 가계별 고객 통계
     * @param storeName
     * @param type
     * @return
     */
    public StampStatisticsResponse getCustomerStatics(String storeName, String type) {

        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        Long storeId = store.getId();

        return switch (type.toLowerCase()) {
            case "weekly" -> getWeeklyCustomerStats(storeId);
            case "monthly" -> getMonthlyCustomerStats(storeId);
            default -> throw new ApplicationException(ErrorCode.INVALID_REQUEST);
        };
    }
    private StampStatisticsResponse getWeeklyCustomerStats(Long storeId) {

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.atTime(23, 59, 59);

        List<Object[]> data = stampRepository.countDailyUniqueUsers(storeId, start, end);
        Map<DayOfWeek, Long> map = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) map.put(d, 0L);

        // 결과 매핑
        for (Object[] row : data) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = ((Number) row[1]).longValue();

            map.put(date.getDayOfWeek(), count);
        }

        List<String> labels = List.of("월","화","수","목","금","토","일");
        List<StampChartData> chart = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            DayOfWeek d = DayOfWeek.MONDAY.plus(i);
            chart.add(new StampChartData(labels.get(i), map.get(d)));
        }

        long total = map.values().stream().mapToLong(Long::longValue).sum();
        long avg = total / 7;

        String periodText = weekStart.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
                + " - " +
                weekEnd.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return new StampStatisticsResponse(
                "weekly_customer",
                avg,
                periodText,
                chart
        );
    }
    private StampStatisticsResponse getMonthlyCustomerStats(Long storeId) {

        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);

        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        LocalDateTime start = monthStart.atStartOfDay();
        LocalDateTime end = monthEnd.atTime(23, 59, 59);

        // DB 조회 (1일~31일별 unique user count)
        List<Object[]> data = stampRepository.countDailyUniqueUsers(storeId, start, end);

        int days = ym.lengthOfMonth();
        Map<Integer, Long> map = new HashMap<>();

        for (int i = 1; i <= days; i++) map.put(i, 0L);

        for (Object[] row : data) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Long count = ((Number) row[1]).longValue();

            map.put(date.getDayOfMonth(), count);
        }

        List<StampChartData> chart = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            chart.add(new StampChartData(i + "일", map.get(i)));
        }

        long total = map.values().stream().mapToLong(Long::longValue).sum();
        long avg = total / days;

        String periodText = today.format(DateTimeFormatter.ofPattern("yyyy년 MM월"));

        return new StampStatisticsResponse(
                "monthly_customer",
                avg,
                periodText,
                chart
        );
    }

}
