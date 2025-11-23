package backend.stamp.reward.controller;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.reward.dto.RewardDashboardResponse;
import backend.stamp.reward.service.RewardDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "Reward (리워드/랭킹 대시보드)", description = "유저 리워드 레벨 및 랭킹 조회 API")
public class RewardDashboardController {

    private final RewardDashboardService rewardDashboardService;

    @Operation(summary = "리워드 대시보드 조회", description = "로그인한 유저의 레벨, 혜택, 랭킹 정보를 한 번에 조회합니다.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApplicationResponse<RewardDashboardResponse>> getRewardDashboard(
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        if (userDetails == null || userDetails.getAccount() == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Account account = userDetails.getAccount();

        RewardDashboardResponse responseDto = rewardDashboardService.getRewardDashboard(account);

        return ResponseEntity.ok(ApplicationResponse.ok(responseDto));
    }

}
