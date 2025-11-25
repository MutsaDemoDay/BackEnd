package backend.stamp.stamp.service;

import backend.stamp.account.entity.Account;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.coupon.service.CouponService;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.order.entity.Order;
import backend.stamp.order.repository.OrderRepository;
import backend.stamp.stamp.dto.*;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StampService {

    private final StampRepository stampRepository;
    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;
    private final CouponService couponService;
    private final CouponRepository couponRepository;

    //스탬프판 새로 등록 ( by 가게 검색 )
    @Transactional
    public StampCreateResponseDto createStamp(Account account, StampCreateRequestDto requestDto) {
        //유저 조회

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        //매장 조회
        Store store =storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(()->  new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        //등록 이미 된거 있는지 조회 ( 중복 제거 )
        boolean exists = stampRepository.existsByUsersAndStore(users, store);
        if (exists) {
            throw  new ApplicationException(ErrorCode.DUPLICATE_STAMP);
        }

        //유저의 스탬프판 수 증가
        users.setStampSum(users.getStampSum() + 1);
        usersRepository.save(users);

        //new 스탬프 엔티티 생성

        Stamp stamp = Stamp.builder()
                .users(users)
                .store(store)
                .name(store.getName())
                .currentCount(0) // 처음 등록 시 0개
                .date(LocalDateTime.now())
                .build();

        Stamp savedStamp = stampRepository.save(stamp);


        return StampCreateResponseDto.from(savedStamp);

    }

    //스탬프 적립
    public StampAddResponseDto addStamp(Account account, Long storeId, Long orderId)
    {
        //추가 )) npe 오류 방지용 로그인 여부 체크 로직 추가
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        // 1) 유저 조회

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        // 2) 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        // 3️) 주문 내역 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_FOUND));

       // + 추가 ))주문이 해당 매장의 주문이 맞는지 검증
        if (order.getStore() == null ||
                order.getStore().getId() == null ||
                !order.getStore().getId().equals(store.getId())) {

            throw new ApplicationException(ErrorCode.ORDER_NOT_IN_STORE);
        }

        //+ 추가 ))이미 이주문으로 스탬프 적립된적 있는지 확인

        if (order.isUsed()) {
            throw new ApplicationException(ErrorCode.STAMP_ALREADY_ADDED_FOR_ORDER);
        }
        // 4)  총 주문 금액 확인
        Integer totalPrice = order.getTotalPrice();

        // 5) 매장 적립 기준 조회 ( 기준 금액 조회 )
        Integer requiredAmount= store.getRequiredAmount();

        // 6) 유저의 해당 매장 스탬프판 조회
        Stamp stamp = stampRepository.findByUsersAndStore(users, store)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STAMP_BOARD_NOT_FOUND_FOR_STORE));

        // 7️) 적립 개수 계산 (총 주문 금액 / 적립 기준)
        Integer addCount = totalPrice / requiredAmount;

        if (addCount == 0) {
            throw new IllegalArgumentException(requiredAmount + "원 이상 주문 시 스탬프가 적립됩니다.");
        }

        int currentCount = stamp.getCurrentCount() + addCount;
        int maxCount = store.getMaxCount();

        //유저 누적 스탬프 수 계산 로직
        users.setTotalStampSum(users.getTotalStampSum() + addCount);

        // 8)  maxCount 초과 시 쿠폰 발급 + 스탬프 초기화
        // 여러 판 넘어갈 수 있으므로 while 사용
        while (currentCount >= maxCount) {

            // 쿠폰 발급
            couponService.createCoupon(users, store);

            // 스탬프판 완료 개수 +1
            users.setCouponNum(users.getCouponNum() + 1);

            currentCount -= maxCount;
        }

        // 9) 스탬프 업데이트 및 저장
        stamp.setCurrentCount(currentCount);

        //해당 주문과 연결
        stamp.setOrder(order);
        stampRepository.save(stamp);

        //주문 사용처리
        order.setUsed(true);
        orderRepository.save(order);

        //누적 스탬프 수 반영한 유저 정보 저장
        usersRepository.save(users);

        // 10)  응답 DTO 반환
        return StampAddResponseDto.from(stamp);

    }


    @Transactional
    //스탬프 삭제
    public void deleteStamp(Account account, Long stampId)
    {
        //계정 체크
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //유저 확인

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        //스탬프 확인
        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //스탬프 소유자 확인 (npe 오류 수정버전)
        Users stampOwner = stamp.getUsers();

        if (stampOwner == null ||
                stampOwner.getAccount() == null ||
                !stampOwner.getAccount().getAccountId().equals(account.getAccountId())) {

            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }


        //유저 스탬프판 수 감소시키기 (유저 정보 업뎃)
        int currentStampSum = users.getStampSum();
        if (currentStampSum > 0) {
            users.setStampSum(currentStampSum - 1);
            usersRepository.save(users);
        }
        stampRepository.delete(stamp);


    }

}
