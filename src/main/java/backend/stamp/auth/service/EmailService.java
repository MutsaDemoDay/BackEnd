package backend.stamp.auth.service;

import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    private final JavaMailSender mailSender;

    // 임시 저장소
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, String> verificationTokens = new ConcurrentHashMap<>();

    /**
     * 인증번호 이메일 발송
     * 실패 시 ApplicationException(ErrorCode.EMAIL_SEND_FAILED) 발생
     */
    public void sendVerificationCode(String email) {
        String code = generateCode();
        verificationCodes.put(email, code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[DANGO] 이메일 인증번호 안내");
            message.setText("인증번호는 " + code + " 입니다.");
            mailSender.send(message);
        } catch (Exception e) {
            // 필요 시 로깅 추가
            e.printStackTrace();
            throw new ApplicationException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 사용자 입력 코드 검증 (성공 시 아무것도 반환하지 않음)
     * - 코드가 없으면 EMAIL_CODE_NOT_FOUND
     * - 코드가 불일치하면 INVALID_EMAIL_CODE
     */
    public String verifyCodeAndIssueToken(String email, String inputCode) {
        String savedCode = verificationCodes.get(email);

        if (savedCode == null) {
            throw new ApplicationException(ErrorCode.EMAIL_CODE_NOT_FOUND);
        }

        if (!savedCode.equals(inputCode)) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL_CODE);
        }

        verificationCodes.remove(email);

        String token = UUID.randomUUID().toString();
        verificationTokens.put(token, email);

        return token;
    }

    public void validateVerificationTokenOrThrow(String email, String token) {
        if (token == null || token.isBlank()) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        String emailFromToken = verificationTokens.get(token);

        if (emailFromToken == null || !emailFromToken.equals(email)) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        verificationTokens.remove(token);
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    public void sendBusinessVerifiedMail(String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[DANGO] 사업자 등록 인증이 완료되었습니다.");
            message.setText(
                    "안녕하세요.\n\n" +
                            "요청하신 사업자 등록 인증이 정상적으로 완료되었습니다.\n" +
                            "이제 STAMP에서 매장 정보를 등록하고, 서비스를 이용하실 수 있습니다.\n\n" +
                            "감사합니다."
            );
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
