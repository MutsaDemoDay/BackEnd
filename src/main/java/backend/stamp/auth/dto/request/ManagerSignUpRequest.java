package backend.stamp.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManagerSignUpRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 최소 8자, 영문과 숫자가 모두 포함되어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
    private String passwordConfirm;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^010[0-9]{8}$", message = "유효한 휴대폰 번호 형식이 아닙니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "사업자 등록 번호는 필수 입력값입니다.")
    @Size(max = 20, message = "사업자 등록 번호는 20자 이내로 입력해주세요.")
    private String businessNum;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String address;

    private Double latitude;
    private Double longitude;
}
