package backend.stamp.auth.controller;
import backend.stamp.auth.kakao.KakaoInfo;
import backend.stamp.auth.kakao.KakaoUser;
import backend.stamp.auth.service.KakaoService;
import backend.stamp.global.exception.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final KakaoService kakaoService;


    @GetMapping("/login")
    public ResponseEntity<?> redirectLoginPage(
            @RequestParam(name="redirectUri", required = false) String redirectUri){
        String authUrl = kakaoService.getKakaoLoginUrl(redirectUri);
        if(redirectUri == null || redirectUri.isEmpty()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", authUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            return ResponseEntity.ok(ApplicationResponse.ok(authUrl));
        }
    }

    @GetMapping("/kakao")
    public ApplicationResponse<KakaoUser> loginKakao(
            @RequestParam(name="code") String authorizationCode,
            @RequestParam(name="redirectUri", required = false) String redirecUri){
        KakaoInfo info = kakaoService.loginKakao(authorizationCode, redirecUri);
        return ApplicationResponse.ok(kakaoService.register(info.kakao_account().email(), info.properties().nickname()));
    }


}
