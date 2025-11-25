package backend.stamp.coupon.service;


import backend.stamp.account.entity.Account;
import backend.stamp.coupon.dto.CouponResponseDto;
import backend.stamp.coupon.dto.CouponResponseListDto;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;

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
    public CouponResponseListDto getUserCoupons(Account account) {

        if(account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //로그인한 사용자 조회
        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        //사용되지 않은 쿠폰만 조회
        List<Coupon> coupons = couponRepository.findByUsersAndUsedFalse(users);

        //쿠폰 리스트 반환
        List<CouponResponseDto> couponListDtos = coupons.stream()
                .map(CouponResponseDto::from)
                .toList();
        return CouponResponseListDto.builder()
                .myCouponNum(users.getCouponNum())
                .couponLists(couponListDtos)
                .build();
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
    public long countTodayUsedCoupons(String storeName) {
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        return couponRepository.countByStoreIdAndUsedAndUsedDateBetween(
                store.getId(),
                true,
                start,
                end
        );
    }

}


