package backend.stamp.users.controller;

import backend.stamp.coupon.dto.CouponResponseDto;
import backend.stamp.coupon.service.CouponService;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.dto.MyStampResponseDto;
import backend.stamp.stamp.dto.StampHistoryResponseDto;
import backend.stamp.stamp.service.StampService;
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

public class UsersController {

    private final CouponService couponService;
    private final StampService stampService;

    // 내 쿠폰 조회
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponseDto>> getMyCoupons(
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(couponService.getUserCoupons(userId));
    }

    //내 현재 쿠폰 목록 조회

    @GetMapping("/stamps")
    public ResponseEntity<List<MyStampResponseDto>> getMyStamps(
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        Long userId = userDetails.getUser().getUserId();
        List<MyStampResponseDto> myStamps = stampService.getMyStamps(userId);
        return ResponseEntity.ok(myStamps);
    }

    // 내 스탬프 히스토리 조회
    @GetMapping("/stamps/history")
    public ResponseEntity<List<StampHistoryResponseDto>> getMyStampHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUser().getUserId();
        return ResponseEntity.ok(stampService.getStampHistory(userId));
    }
}
