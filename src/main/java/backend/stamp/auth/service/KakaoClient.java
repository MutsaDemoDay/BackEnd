package backend.stamp.auth.service;

import backend.stamp.auth.kakao.KakaoInfo;
import backend.stamp.auth.kakao.KakaoToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClient {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String kakaoGrantType;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    public KakaoToken getKakaoAccessToken(String code, String redirectUri) {
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = kakaoRedirectUri;
        }

        // 요청 보낼 객체 기본 생성
        WebClient webClient = WebClient.create(kakaoTokenUri);

        //요청 본문
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoGrantType);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        // 요청 보내기 및 응답 수신
        String response = webClient.post()
                .uri(kakaoTokenUri)
                .header("Content-type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(params))
                .retrieve() // 데이터 받는 방식
                .bodyToMono(String.class)
                .block(); // 비동기식

        // 수신된 응답 Mapping
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken kakaoToken;
        try {
            kakaoToken = objectMapper.readValue(response, KakaoToken.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kakaoToken;
    }

    public KakaoInfo getMemberInfo(KakaoToken kakaoToken) {
        // 요청 기본 객체 생성
        WebClient webClient = WebClient.create(kakaoUserInfoUri);

        // 요청 보내서 응답 받기
        String response = webClient.post()
                .uri(kakaoUserInfoUri)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", "Bearer " + kakaoToken.access_token())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 수신된 응답 Mapping
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoInfo kakaoinfo;
        try {
            kakaoinfo = objectMapper.readValue(response, KakaoInfo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kakaoinfo;
    }
}
