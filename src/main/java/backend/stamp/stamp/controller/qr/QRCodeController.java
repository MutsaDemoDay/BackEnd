package backend.stamp.stamp.controller.qr;

import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.stamp.service.qr.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/qr")
public class QRCodeController {
    private final QRCodeService qrservice;
    @PostMapping("/scan")
    public ApplicationResponse<String> scanQrCode(
            @RequestParam Long storeId,
            @RequestParam Long userId)
    {
        qrservice.addStamp(storeId, userId);
        return ApplicationResponse.ok("스탬프가 정상적으로 적립되었습니다.");
    }
    @GetMapping("/generate")
    public ApplicationResponse<String> generateQRCode(@RequestParam("storeCode") String storeCode) {
        try {
            String base64Image = qrservice.generateQRCode(storeCode);
            return ApplicationResponse.ok(base64Image);
        } catch (Exception e) {
            return ApplicationResponse.error("QR 생성 실패: " + e.getMessage());
        }
    }

}
