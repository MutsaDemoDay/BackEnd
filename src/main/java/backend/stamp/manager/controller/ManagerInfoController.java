package backend.stamp.manager.controller;

import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.dto.ManagerAccountInfoResponse;
import backend.stamp.manager.service.ManagerInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
@Tag(name = "점주 계정 정보", description = "점주 계정 정보 조회 API")
public class ManagerInfoController {

    private final ManagerInfoService managerInfoService;

    @Operation(summary = "점주 계정 정보 조회", description = "점주의 이메일, 아이디, 가입일을 조회합니다.")
    @GetMapping("/account")
    public ApplicationResponse<ManagerAccountInfoResponse> getMyAccountInfo() {

        ManagerAccountInfoResponse response = managerInfoService.getMyAccountInfo();

        return ApplicationResponse.<ManagerAccountInfoResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("점주 계정 정보 조회가 완료되었습니다.")
                .data(response)
                .build();
    }
}
