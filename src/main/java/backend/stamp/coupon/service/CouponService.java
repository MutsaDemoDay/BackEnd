package backend.stamp.coupon.service;


import backend.stamp.coupon.dto.CouponResponseDto;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    //max Count 달성시 쿠폰 발급
    @Transactional
    public void createCoupon(Users user, Store store) {

        //쿠폰 이름 생성
        String couponName = store.getName() + " 스탬프 리워드 쿠폰";

        //쿠폰 유효기간 생성
        LocalDateTime expiredDate = LocalDateTime.now().plusDays(30);

        //쿠폰 엔티티 생성
        Coupon coupon = Coupon.builder()
                .users(user)
                .store(store)
                .name(couponName)
                .expiredDate(expiredDate)
                .used(false)
                .build();

        couponRepository.save(coupon);



    }


    //나의 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponResponseDto> getUserCoupons(Long userId) {
        List<Coupon> coupons = couponRepository.findByUsers_Account_AccountId(userId);
        return coupons.stream()
                .map(CouponResponseDto::from)
                .toList();
    }

    //쿠폰 사용완료 처리


}


