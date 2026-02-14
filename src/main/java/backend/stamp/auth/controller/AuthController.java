package backend.stamp.auth.controller;
import backend.stamp.auth.dto.request.*;
import backend.stamp.auth.dto.response.*;
import backend.stamp.auth.kakao.KakaoInfo;
import backend.stamp.auth.kakao.KakaoUser;
import backend.stamp.auth.service.*;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
    private final UserFindService userFindService;
    private final ManagerFindService managerFindService;
    private final UserPasswordService userPasswordService;
    private final ManagerPasswordService managerPasswordService;
    private final ResetPasswordService resetPasswordService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "아이디 중복 확인 api", description = "true: 중복됨(사용불가), false: 중복안됨(사용가능)")
    @GetMapping("/check-id")
    public ApplicationResponse<Boolean> checkIdDuplicate(@RequestParam(name = "loginId") String loginId) {
        boolean isDuplicate = userSignUpService.isLoginIdDuplicate(loginId);
        return ApplicationResponse.ok(isDuplicate);
    }

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
    public ApplicationResponse<KakaoUser> loginKakao(@RequestParam(name="code") String authorizationCode,
                                                     @RequestParam(name="redirectUri", required = false) String redirectUri){
        KakaoInfo info = kakaoService.loginKakao(authorizationCode, redirectUri);

String email = null;
String nickname = null;

if (info.kakao_account() != null) {
    email = info.kakao_account().email();
}
if (info.properties() != null) {
    nickname = info.properties().nickname();
}

if (email == null) {
    email = "kakao_" + info.id() + "@kakao.com";
}
if (nickname == null) {
    nickname = "사용자" + info.id();
}

return ApplicationResponse.ok(kakaoService.register(email, nickname));
    }

    @Operation(summary = "유저 회원가입 api", description = "일반 유저로 회원가입을 진행합니다.")
    @PostMapping("/user/join")
    public ApplicationResponse<SignUpResponse> signUpUser(
            @Valid @RequestBody UserSignUpRequest request) {

        SignUpResponse response = userSignUpService.signUp(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "유저 온보딩 api", description = "유저가 온보딩에서 추가 정보를 입력합니다.")
    @PostMapping(value="/user/onboarding", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<Void> completeUserOnboarding(
            @RequestPart("data") @Valid String data,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws JsonProcessingException {

        UserOnboardingRequest request = objectMapper.readValue(data, UserOnboardingRequest.class);

        userOnboardingService.completeOnboarding(request, profileImage);
        return ApplicationResponse.ok(null);
    }

    @Operation(summary = "점주 회원가입 api", description = "점주로 회원가입을 진행합니다.")
    @PostMapping("/manager/join")
    public ApplicationResponse<SignUpResponse> signUpManager(
            @Valid @RequestBody ManagerSignUpRequest request) {

        SignUpResponse response = managerSignUpService.signUp(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "로그인 api", description = "유저와 점주가 로그인을 진행합니다.")
    @PostMapping("/login")
    public ApplicationResponse<SignUpResponse> login(
            @Valid @RequestBody LoginRequest request) {

        SignUpResponse response = loginService.login(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "로그아웃 api", description = "유저와 점주가 로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ApplicationResponse<Void> logout() {
        logoutService.logout();

        return ApplicationResponse.<Void>builder()
                .code(ErrorCode.LOGOUT_SUCCESS.getCode())
                .message(ErrorCode.LOGOUT_SUCCESS.getMessage())
                .data(null)
                .build();
    }

    @Operation(summary = "점주 온보딩 api", description = "점주가 온보딩에서 자신의 매장 정보를 입력합니다.")
    @PostMapping(value = "/manager/onboarding", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<ManagerOnboardingResponse> completeManagerOnboarding(
            @RequestPart("data") @Valid String data,
            @RequestPart(value = "storeImage", required = false) MultipartFile storeImage,
            @RequestPart(value = "stampImage", required = false) MultipartFile stampImage
    ) throws JsonProcessingException {

        ManagerOnboardingRequest request =
                objectMapper.readValue(data, ManagerOnboardingRequest.class);

        ManagerOnboardingResponse response =
                managerOnboardingService.completeOnboarding(request, storeImage, stampImage);

        return ApplicationResponse.<ManagerOnboardingResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("매장 등록이 완료되었습니다.")
                .data(response)
                .build();
    }

    @Operation(summary = "토큰 재발급 api", description = "이전에 발급받았던 리프레시토큰을 통해 토큰을 재발급합니다.")
    @PostMapping("/token")
    public ApplicationResponse<TokenReissueResponse> reissueToken(
            @Valid @RequestBody TokenReissueRequest request) {

        TokenReissueResponse response = tokenReissueService.reissueToken(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "이메일 인증번호 발송 api", description = "이메일로 인증번호를 발송합니다.")
    @PostMapping("/email/send")
    public ApplicationResponse<String> sendEmail(@Valid @RequestBody EmailSendRequest request) {
        emailService.sendVerificationCode(request.getEmail());
        return ApplicationResponse.ok("인증번호가 전송되었습니다.");
    }

    @Operation(summary = "이메일 인증번호 검증 api", description = "입력한 인증번호가 맞는지 검증합니다.")
    @PostMapping("/email/verify")
    public ApplicationResponse<EmailVerifyResponse> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request) {
        String token = emailService.verifyCodeAndIssueToken(request.getEmail(), request.getCode());
        EmailVerifyResponse response = new EmailVerifyResponse(token);

        return ApplicationResponse.<EmailVerifyResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("이메일 인증이 완료되었습니다.")
                .data(response)
                .build();
    }

    @Operation(summary = "유저 아이디 찾기 api", description = "유저가 자신의 아이디를 찾습니다.")
    @PostMapping("/user/findId")
    public ApplicationResponse<FindIdResponse> findUserId(@Valid @RequestBody FindUserIdRequest request) {
        FindIdResponse response = userFindService.findUserId(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "유저 비밀번호 찾기 api", description = "유저가 자신의 비밀번호를 찾습니다.")
    @PostMapping("/user/findPassword")
    public ApplicationResponse<FindPasswordResponse> findUserPassword(@Valid @RequestBody FindUserPasswordRequest request) {
        FindPasswordResponse response = userPasswordService.findUserPassword(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "점주 아이디 찾기 api", description = "점주가 자신의 아이디를 찾습니다.")
    @PostMapping("/manager/findId")
    public ApplicationResponse<FindIdResponse> findManagerId(@Valid @RequestBody FindManagerIdRequest request) {
        FindIdResponse response = managerFindService.findManagerId(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "점주 비밀번호 찾기 api", description = "점주가 자신의 비밀번호를 찾습니다.")
    @PostMapping("/manager/findPassword")
    public ApplicationResponse<FindPasswordResponse> findManagerPassword(@Valid @RequestBody FindManagerPasswordRequest request) {
        FindPasswordResponse response = managerPasswordService.findManagerPassword(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "비밀번호 재설정 api", description = "자신이 사용할 새 비밀번호를 설정합니다.")
    @PatchMapping("/resetPassword")
    public ApplicationResponse<Void> resetPassword(
            @RequestHeader("resetToken") String resetToken,
            @Valid @RequestBody ResetPasswordRequest request) {

        resetPasswordService.resetPassword(resetToken, request);
        return ApplicationResponse.ok(null);
    }

}
