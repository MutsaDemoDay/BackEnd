package backend.stamp.manager.controller;


import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.dto.StampCustomerResponse;
import backend.stamp.manager.dto.StampSettingRequest;
import backend.stamp.manager.dto.StampSettingResponse;
import backend.stamp.manager.dto.StampStatisticsResponse;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stamps/manager")
@Tag(name = "점주 페이지", description = "점주 페이지 API")
public class ManagerController {
    private final ManagerService managerService;
    private final QRCodeService qrCodeService;
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
    public ApplicationResponse<String> addStamp(@RequestParam String storeName, @RequestParam Long userId){
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALIDE_QRCODE));
        qrCodeService.addStamp(store.getId(), userId);
        return ApplicationResponse.ok("성공적으로 적립되었습니다.");
    }
    @GetMapping("/statics")
    public ApplicationResponse<StampStatisticsResponse> getStatics(@RequestParam String storeName, @RequestParam String type){
        StampStatisticsResponse response = managerService.getStampStatics(storeName, type);
        return ApplicationResponse.ok(response);
    }
    @GetMapping("/totals")
    public ApplicationResponse<StampStatisticsResponse> getTotals(@RequestParam String storeName, @RequestParam String type){
        StampStatisticsResponse response = managerService.getCustomerStatics(storeName, type);
        return ApplicationResponse.ok(response);
    }


}
