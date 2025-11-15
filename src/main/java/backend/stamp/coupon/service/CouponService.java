package backend.stamp.coupon.service;


import backend.stamp.account.entity.Account;
import backend.stamp.coupon.dto.CouponResponseDto;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
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
        //사용되지 않은 쿠폰만 조회
        List<Coupon> coupons = couponRepository.findByUsers_Account_AccountIdAndUsedFalse(userId);
        return coupons.stream()
                .map(CouponResponseDto::from)
                .toList();
    }

    //쿠폰 사용완료 처리
    @Transactional
    public void useCoupon(Long couponId, Account account, String verificationCode) {
        //1. 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()->new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        //쿠폰 주인 체크
        Users couponOwner = coupon.getUsers();

        if (couponOwner == null || couponOwner.getAccount() == null || account == null) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        if (!couponOwner.getAccount().getAccountId().equals(account.getAccountId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        //이미 사용된 쿠폰인지 체크
        if (coupon.isUsed()) {
            throw new ApplicationException(ErrorCode.COUPON_ALREADY_USED);
        }
        //점주의 verificationCode 체크
        Store store = coupon.getStore();
        if (store == null || store.getVerificationCode() == null) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        if (!store.getVerificationCode().equals(verificationCode)) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE);
        }


        // 5. 사용 처리
        coupon.use();

    }


}


