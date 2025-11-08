package backend.stamp.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class BusinessHourRequest {
    @NotBlank(message = "요일은 필수 항목입니다. (예: MON)")
    private String day; // MON, TUE, WED...

    private LocalTime openTime;
    private LocalTime closeTime;

    @NotNull(message = "휴무 여부는 필수 항목입니다.")
    private Boolean isHoliday;
}
