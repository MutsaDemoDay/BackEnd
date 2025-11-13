package backend.stamp.global.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //100: Success
    SUCCESS(HttpStatus.OK, 100, "정상적으로 생성되었습니다."),

    LOGOUT_SUCCESS(HttpStatus.OK, 200, "로그아웃 되었습니다."),

    // 400: Bad Request
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 400, "사용자가 없습니다."),
    INVALID_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "제한된 접근입니다."),
    KAKAO_TOKEN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 404, "토큰 발급에서 오류가 발생했습니다."),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "요청하신 가게 정보를 찾을 수 없습니다."),

    STAMP_NOT_FOUND(HttpStatus.NOT_FOUND, 405, "요청하신 스탬프 정보를 찾을 수 없습니다."),

    ALREADY_FAVORITE_STAMP(HttpStatus.BAD_REQUEST, 406, "이미 즐겨찾기로 설정된 스탬프입니다."),

    ALREADY_FAVORITE_STORE(HttpStatus.BAD_REQUEST, 407, "이미 즐겨찾기로 설정된 가게입니다."),

    NOT_FAVORITE_STAMP(HttpStatus.BAD_REQUEST, 408, "해당 스탬프가 즐겨찾기 상태가 아닙니다."),

    FAVORITE_STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, 409, "해당 매장이 즐겨찾기 상태가 아닙니다."),

    DUPLICATE_STAMP(HttpStatus.BAD_REQUEST, 410, "해당 매장의 스탬프판이 이미 등록되어 있습니다."),

    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST,411,"요청하신 주문 정보를 찾을 수 없습니다."),

    ORDER_NOT_IN_STORE(HttpStatus.BAD_REQUEST, 412, "해당 주문은 선택한 매장의 주문이 아닙니다."),

    STAMP_ALREADY_ADDED_FOR_ORDER(HttpStatus.BAD_REQUEST, 413, "이미 이 주문에 대해 스탬프가 적립되었습니다."),
    STAMP_BOARD_NOT_FOUND_FOR_STORE(HttpStatus.NOT_FOUND, 414, "해당 매장의 스탬프판이 없습니다."),

    // 500: Internal Error
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 501, "예기치 못한 오류가 발생했습니다."),
    INVALIDE_QRCODE(HttpStatus.NOT_FOUND, 502, "잘못된 qr코드 입니다."),

    // 이메일 관련 추가 에러 코드 (기존 형식과 동일)
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 503, "이메일 전송에 실패했습니다."),
    EMAIL_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, 504, "요청하신 이메일의 인증번호가 존재하지 않습니다."),
    INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, 505, "이메일 인증번호가 일치하지 않습니다.");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
