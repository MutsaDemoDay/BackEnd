package backend.stamp.stamp.service;


import backend.stamp.account.entity.Account;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.stamp.dto.MyStampResponseDto;
import backend.stamp.stamp.dto.StampHistoryListResponseDto;
import backend.stamp.stamp.dto.StampHistoryResponseDto;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StampDetailService {

    private final UsersRepository usersRepository;
    private final CouponRepository couponRepository;
    private final StampRepository stampRepository;
    //내가 현재 가진 스탬프 히스토리 조회 ( 과거 )

    @Transactional(readOnly = true)
    public StampHistoryListResponseDto getStampHistory(Account account) {

        //유저 계정 null 체크
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }


        // 유저 조회

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        // 유저의 쿠폰 목록 조회
        List<Coupon> coupons = couponRepository.findByUsers(users);

        if (coupons == null) {
            coupons = List.of(); // null이면 빈 리스트로 처리
        }

        List<StampHistoryResponseDto> stampList = coupons.stream()
                .map(StampHistoryResponseDto::from)
                .toList();

        // DTO 변환
        return StampHistoryListResponseDto.builder()
                .completedStampNum(stampList.size())
                .completedStamps(stampList)
                .build();
    }

    // 내가 현재 가진 스탬프 리스트 조회
    @Transactional(readOnly = true)
    public List<MyStampResponseDto> getMyStamps(Account account) {

        //계정 체크
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        // 유저 조회

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        //유저의 스탬프 목록 조회
        List<Stamp> stamps = stampRepository.findByUsers(users);


        if (stamps == null) {
            stamps = List.of(); // null이면 빈 리스트로 처리
        }
        return stamps.stream()
                .map(MyStampResponseDto::from)
                .collect(Collectors.toList());

    }


    @Transactional
    //스탬프 개별조회
    public MyStampResponseDto getStampDetail(Account account,Long stampId)
    {
        //계정체크
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //유저 확인

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        //스탬프 확인
        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //스탬프 소유자 확인

        Users stampOwner = stamp.getUsers();

        if (stampOwner == null ||
                stampOwner.getAccount() == null ||
                !stampOwner.getAccount().getAccountId().equals(account.getAccountId())) {

            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }



        //스탬프 DTO 변환
        return MyStampResponseDto.from(stamp);

    }


}
