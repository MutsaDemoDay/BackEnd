package backend.stamp.global.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //100: Success
    SUCCESS(HttpStatus.OK, 100, "정상적으로 생성되었습니다."),

    // 400: Bad Request
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 400, "잘못된 사용자 입니다."),
    INVALID_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "제한된 접근입니다."),
    KAKAO_TOKEN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 404, "토큰 발급에서 오류가 발생했습니다."),


    // 500: Internal Error
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 501, "예기치 못한 오류가 발생했습니다.");



    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
