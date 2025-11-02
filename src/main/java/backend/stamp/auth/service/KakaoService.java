package backend.stamp.auth.service;
import backend.stamp.auth.kakao.KakaoInfo;
import backend.stamp.auth.kakao.KakaoToken;
import backend.stamp.auth.kakao.KakaoUser;
import backend.stamp.global.security.TokenProvider;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
    private final TokenProvider tokenProvider;
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
    public KakaoUser register(String email, String name){

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }

        Users users = usersRepository.findByEmail(email)
                .orElseGet(() -> {
                    Users newUser = new Users(name, email);
                    return usersRepository.save(newUser);
                });

        String accessToken = tokenProvider.createAccessToken(users);

        return KakaoUser.builder()
                .id(users.getUserId())
                .email(users.getEmail())
                .name(users.getName())
                .accessToken(accessToken)
                .build();
    }

}
