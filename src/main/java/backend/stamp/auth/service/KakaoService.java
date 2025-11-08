package backend.stamp.auth.service;
import backend.stamp.account.entity.Account;
import backend.stamp.account.entity.UserType;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.kakao.KakaoInfo;
import backend.stamp.auth.kakao.KakaoToken;
import backend.stamp.auth.kakao.KakaoUser;
import backend.stamp.global.security.TokenProvider;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Component
@RequiredArgsConstructor
public class KakaoService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    private final KakaoClient kakaoClient;
    private final UsersRepository usersRepository;
    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String getKakaoLoginUrl(String redirectUri) {
        if(redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = kakaoRedirectUri;
        }

        return "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
    }

    public KakaoInfo loginKakao(String authorizationCode, String redirectUri){
        System.out.println("카카오 인가 코드: " + authorizationCode);
        System.out.println("Redirect URI: " + redirectUri);
        KakaoToken token = kakaoClient.getKakaoAccessToken(authorizationCode, redirectUri);
        return kakaoClient.getMemberInfo(token);
    }
    @Transactional
    public KakaoUser register(String email, String nickname){

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }

        Optional<Account> existingAccount = accountRepository.findByEmail(email);

        Account account;
        Users user;

        if (existingAccount.isEmpty()) {
            //Account가 없는 경우: 소셜 회원가입 처리

            String tempLoginId = "kakao_" + UUID.randomUUID().toString().substring(0, 8);
            String tempPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            // 필수 필드인 전화번호를 알 수 없으므로 임시 값 설정 ("00000000000")

            account = new Account(
                    null, // accountId
                    tempLoginId,
                    email, // Account에 email 저장
                    tempPassword, // 임시 비밀번호
                    UserType.USER, // 유저 타입 설정
                    "00000000000", // 임시 전화번호
                    LocalDateTime.now()
            );
            account = accountRepository.save(account);

            // Users 엔티티 생성 및 Account 연결
            user = Users.createSocialUser(nickname, account);
            user = usersRepository.save(user);

        } else {
            //Account가 이미 있는 경우: 로그인 처리
            account = existingAccount.get();
            user = usersRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalStateException("Account exists but corresponding User not found."));
        }

        String accessToken = tokenProvider.createAccessToken(account);

        return KakaoUser.builder()
                .id(user.getUserId())
                .email(account.getEmail())
                .name(user.getNickname())
                .accessToken(accessToken)
                .build();
    }

}
