package backend.stamp.coupon.controller;


import backend.stamp.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")

@Tag(name = "Coupon ", description = "Coupon 관련 API")
public class CouponController {

    private final CouponService couponService;

}
