package backend.stamp.auth.controller;
import backend.stamp.auth.dto.request.*;
import backend.stamp.auth.dto.response.ManagerOnboardingResponse;
import backend.stamp.auth.dto.response.SignUpResponse;
import backend.stamp.auth.dto.response.TokenReissueResponse;
import backend.stamp.auth.kakao.KakaoInfo;
import backend.stamp.auth.kakao.KakaoUser;
import backend.stamp.auth.service.*;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")

@Tag(name = "Auth", description = "Auth 관련 API")
public class AuthController {
    private final KakaoService kakaoService;
    private final UserSignUpService userSignUpService;
    private final UserOnboardingService userOnboardingService;
    private final ManagerSignUpService managerSignUpService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ManagerOnboardingService managerOnboardingService;
    private final TokenReissueService tokenReissueService;
    private final EmailService emailService;

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
    public ApplicationResponse<KakaoUser> loginKakao(@RequestParam(name="code") String authorizationCode){
        KakaoInfo info = kakaoService.loginKakao(authorizationCode);
        return ApplicationResponse.ok(kakaoService.register(info.kakao_account().email(), info.properties().nickname()));
    }

    @PostMapping("/user/join")
    public ApplicationResponse<SignUpResponse> signUpUser(
            @Valid @RequestBody UserSignUpRequest request) {

        SignUpResponse response = userSignUpService.signUp(request);
        return ApplicationResponse.ok(response);
    }

    @PostMapping("/user/onboarding")
    public ApplicationResponse<Void> completeUserOnboarding(
            @Valid @RequestBody UserOnboardingRequest request) {

        userOnboardingService.completeOnboarding(request);
        return ApplicationResponse.ok(null);
    }

    @PostMapping("/manager/join")
    public ApplicationResponse<SignUpResponse> signUpManager(
            @Valid @RequestBody ManagerSignUpRequest request) {

        SignUpResponse response = managerSignUpService.signUp(request);
        return ApplicationResponse.ok(response);
    }

    @PostMapping("/login")
    public ApplicationResponse<SignUpResponse> login(
            @Valid @RequestBody LoginRequest request) {

        SignUpResponse response = loginService.login(request);
        return ApplicationResponse.ok(response);
    }

    /**
     * 로그아웃 API
     * Access Token을 포함하여 요청하며, 서버에서 Refresh Token을 무효화합니다.
     */
    @PostMapping("/logout")
    public ApplicationResponse<Void> logout() {
        logoutService.logout();

        return ApplicationResponse.<Void>builder()
                .code(ErrorCode.LOGOUT_SUCCESS.getCode())
                .message(ErrorCode.LOGOUT_SUCCESS.getMessage())
                .data(null)
                .build();
    }

    @PostMapping("/manager/onboarding")
    public ApplicationResponse<ManagerOnboardingResponse> completeManagerOnboarding(
            @Valid @RequestBody ManagerOnboardingRequest request) {

        ManagerOnboardingResponse response = managerOnboardingService.completeOnboarding(request);
        return ApplicationResponse.<ManagerOnboardingResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("매장 등록이 완료되었습니다.")
                .data(response)
                .build();
    }

    @PostMapping("/token")
    public ApplicationResponse<TokenReissueResponse> reissueToken(
            @Valid @RequestBody TokenReissueRequest request) {

        TokenReissueResponse response = tokenReissueService.reissueToken(request);
        return ApplicationResponse.ok(response);
    }

    @PostMapping("/email/send")
    public ApplicationResponse<String> sendEmail(@Valid @RequestBody EmailSendRequest request) {
        emailService.sendVerificationCode(request.getEmail());
        return ApplicationResponse.ok("인증번호가 전송되었습니다.");
    }

    @PostMapping("/email/verify")
    public ApplicationResponse<String> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request) {
        emailService.verifyCodeOrThrow(request.getEmail(), request.getCode());
        return ApplicationResponse.ok("이메일 인증이 완료되었습니다.");
    }


}
