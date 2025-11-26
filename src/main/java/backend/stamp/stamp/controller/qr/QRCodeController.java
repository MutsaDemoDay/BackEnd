package backend.stamp.stamp.controller.qr;

import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.stamp.service.qr.QRCodeService;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/v1/qr")

@Tag(name = "QrCode( Qr코드 관련 )", description = "QrCode( Qr코드 관련 ) API")
public class QRCodeController {
    private final QRCodeService qrservice;
    private final UsersRepository usersRepository;
    @PostMapping("/scan")
    public ApplicationResponse<String> scanQrCode(
            @RequestParam Long storeId,
            @RequestParam String email,
            @RequestParam int stampCount)
    {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        qrservice.addStamp(storeId, users.getUserId(), stampCount);
        return ApplicationResponse.ok("스탬프가 정상적으로 적립되었습니다.");
    }
    @GetMapping("/generate")
    public ApplicationResponse<String> generateQRCode(@RequestParam("email") String email) {
        try {
            String base64Image = qrservice.generateQRCode(email);
            return ApplicationResponse.ok(base64Image);
        } catch (Exception e) {
            return ApplicationResponse.error("QR 생성 실패: " + e.getMessage());
        }
    }

}
