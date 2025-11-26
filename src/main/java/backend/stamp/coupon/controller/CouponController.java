package backend.stamp.coupon.controller;


import backend.stamp.account.entity.Account;
import backend.stamp.coupon.dto.CouponResponseDto;
import backend.stamp.coupon.dto.CouponUseRequestDto;
import backend.stamp.coupon.service.CouponService;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")

@Tag(name = "Coupon ", description = "Coupon 관련 API")
public class CouponController {

    private final CouponService couponService;

    //쿠폰 사용처리


    @Operation(
            summary = "쿠폰 사용 확인",
            description = "로그인한 유저가 쿠폰을 제시하고, 매장 직원이 verificationCode를 입력해 사용완료 처리합니다."
    )
    @PostMapping("/{couponId}/confirm")
    public ApplicationResponse<Void> useCoupon( @PathVariable Long couponId,@AuthenticationPrincipal PrincipalDetails user, @RequestParam String verificationCode) {

        Account account =user.getAccount();
        couponService.useCoupon(couponId,account,verificationCode);
        return ApplicationResponse.ok(null);

    }

    // 쿠폰 개별조회

    @Operation(
            summary = "쿠폰 개별조회",
            description = "로그인한 유저의 쿠폰을 개별 조회합니다."
    )

    @GetMapping("/{couponId}")
    public ApplicationResponse<CouponResponseDto> getCoupon(@PathVariable Long couponId,@AuthenticationPrincipal PrincipalDetails user) {

        Account account = user.getAccount();
        CouponResponseDto response = couponService.getCoupon(couponId,account);

        return ApplicationResponse.ok(response);


    }

}
