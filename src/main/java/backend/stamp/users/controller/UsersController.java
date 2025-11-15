package backend.stamp.users.controller;

import backend.stamp.account.entity.Account;
import backend.stamp.coupon.dto.CouponResponseDto;
import backend.stamp.coupon.service.CouponService;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.dto.MyStampResponseDto;
import backend.stamp.stamp.dto.StampHistoryResponseDto;
import backend.stamp.stamp.service.StampDetailService;
import backend.stamp.stamp.service.StampService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor

@Tag(name = "User ( 유저 정보 관련 )", description = "User(유저 정보 관련) API")
public class UsersController {

    private final CouponService couponService;
    private final StampDetailService stampDetailService;

    // 내 쿠폰 조회
    @Operation(summary = "내 쿠폰 조회 api", description = "유저가 자신의 쿠폰 목록을 조회합니다.")

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponseDto>> getMyCoupons(
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        Long userId = userDetails.getAccount().getAccountId();
        return ResponseEntity.ok(couponService.getUserCoupons(userId));
    }

    //내 현재 스탬프 목록 조회

    @Operation(summary = "유저의 현재 스탬프 목록 조회 api", description = "유저가 자신의 스탬프 목록을 조회합니다.")
    @GetMapping("/stamps")
    public ResponseEntity<List<MyStampResponseDto>> getMyStamps(
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        Account account = userDetails.getAccount();
        List<MyStampResponseDto> myStamps = stampDetailService.getMyStamps(account);
        return ResponseEntity.ok(myStamps);
    }

    // 내 스탬프 히스토리 조회

    @Operation(summary = "내 스탬프 히스토리 조회 api", description = "유저가 자신의 스탬프 히스토리를 조회합니다.")
    @GetMapping("/stamps/history")
    public ResponseEntity<List<StampHistoryResponseDto>> getMyStampHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Account account = principalDetails.getAccount();
        return ResponseEntity.ok(stampDetailService.getStampHistory(account));
    }
}
