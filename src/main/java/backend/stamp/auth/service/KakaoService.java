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

            String tempLoginId = "kakao_" + UUID.randomUUID().toString().substring(0, 8);
            String tempPassword = passwordEncoder.encode(UUID.randomUUID().toString());


            account = new Account(
                    null,
                    tempLoginId,
                    email,
                    tempPassword,
                    UserType.USER,
                    LocalDateTime.now()
            );
            account = accountRepository.save(account);

            user = Users.createSocialUser(nickname, account);
            user = usersRepository.save(user);

        } else {
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
