package backend.stamp.manager.controller;


import backend.stamp.coupon.service.CouponService;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.dto.dashboard.*;
import backend.stamp.manager.service.ManagerService;
import backend.stamp.stamp.service.qr.QRCodeService;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manager")
@Tag(name = "점주 페이지", description = "점주 페이지 API")
public class ManagerController {
    private final ManagerService managerService;
    private final QRCodeService qrCodeService;
    private final CouponService couponService;
    private final StoreRepository storeRepository;

    /**
     * 점주가 stamp 설정값 세팅
     * @param data
     * @param image
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/settings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setStamp(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        StampSettingRequest request = mapper.readValue(data, StampSettingRequest.class);
        String imgUrl = managerService.setStamp(request, image);
        return ResponseEntity.ok(ApplicationResponse.ok(imgUrl));
    }

    /**
     * 스탬프 세팅값 조회
     * @param storeName
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity<?> getSetting(@RequestParam String storeName) {
        StampSettingResponse response = managerService.getStamp(storeName);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/customers")
    public ApplicationResponse<List<StampCustomerResponse>> getCustomers(@RequestParam String storeName){
        List<StampCustomerResponse> response = managerService.getCustomers(storeName);
        return ApplicationResponse.ok(response);
    }
    @PostMapping("/addByNum")
    public ApplicationResponse<String> addStamp(@RequestParam String storeName, @RequestParam Long userId, @RequestParam int stampCount){
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALIDE_QRCODE));
        qrCodeService.addStamp(store.getId(), userId, stampCount);
        return ApplicationResponse.ok("성공적으로 적립되었습니다.");
    }
    @GetMapping("/stamps/statics")
    public ApplicationResponse<StampStatisticsTotalResponse> getStatics(
            @RequestParam String storeName,
            @RequestParam String type
    ) {
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        StampStatisticsTotalResponse response = switch (type.toLowerCase()) {
            case "weekly" -> managerService.getWeeklyStats(store.getId());
            case "monthly" -> managerService.getMonthlyStats(store.getId());
            default -> throw new ApplicationException(ErrorCode.INVALID_REQUEST);
        };
        return ApplicationResponse.ok(response);
    }
    @GetMapping("/stamps/statics/daily")
    public ApplicationResponse<?> getDailyStatics(@RequestParam String storeName){
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        StampStatisticsResponse response =managerService.getDailyStats(store.getId());
        return ApplicationResponse.ok(response);
    }

    @GetMapping("/customers/statics")
    public ApplicationResponse<StampStatisticsResponse> getTotals(@RequestParam String storeName, @RequestParam String type){
        StampStatisticsResponse response = managerService.getCustomerStatics(storeName, type);
        return ApplicationResponse.ok(response);
    }
    @GetMapping("/gender/weekly")
    public ApplicationResponse<GenderStatisticsResponse> getWeeklyGender(
            @RequestParam String storeName,
            @RequestParam(required = false) String baseDate
    ){
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        LocalDate date = baseDate != null
                ? LocalDate.parse(baseDate)
                : LocalDate.now();
        GenderStatisticsResponse response =
                managerService.getWeeklyGenderStatistics(store.getId(), date);
        return ApplicationResponse.ok(response);
    }

    /**
     * 기간 내 스탬프 적립 수
     * @param storeName
     * @return
     */
    @GetMapping("/weekly/event")
    public ApplicationResponse<WeeklyCompareResponse> getWeeklyEventCompare(
            @RequestParam String storeName
    ){
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        WeeklyCompareResponse resp =
                managerService.getEventWeeklyCompare(store.getId());
        return ApplicationResponse.ok(resp);
    }
    @GetMapping("/weekly/customers")
    public ApplicationResponse<WeeklyCustomerCompareResponse> getWeeklyCustomerCompare(
            @RequestParam String storeName
    ){
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        WeeklyCustomerCompareResponse resp = managerService.getWeeklyCustomerCompare(store.getId());
        return ApplicationResponse.ok(resp);
    }
    @GetMapping("/reward")
    public ApplicationResponse<Long> getTodayUsedCount(@RequestParam String storeName) {
        long count = couponService.countTodayUsedCoupons(storeName);
        return ApplicationResponse.ok(count);
    }


}
