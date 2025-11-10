package backend.stamp.stamp.controller.qr;

import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.stamp.service.qr.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr")
public class QRCodeController {
    private final QRCodeService qrservice;
    @PostMapping("/scan")
    public ApplicationResponse<String> scanQrCode(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("userId") Long userId) {
        try {
            String decodedText = qrservice.decodeQRCode(file);
            if (decodedText == null || decodedText.isBlank()) {
                return ApplicationResponse.error("QR 코드에서 데이터를 읽지 못했습니다.");
            }
            qrservice.addStamp(decodedText, userId);
            return ApplicationResponse.ok("스탬프가 정상적으로 적립되었습니다.");
        } catch (IOException e) {
            return ApplicationResponse.error("QR 코드를 해독하는 중 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApplicationResponse.error("잘못된 QR 코드입니다: " + e.getMessage());
        } catch (Exception e) {
            return ApplicationResponse.error("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
