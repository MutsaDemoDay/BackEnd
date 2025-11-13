package backend.stamp.stamp.service;

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
    public StampCreateResponseDto createStamp(Long userId, StampCreateRequestDto requestDto) {
        //유저 조회
    Users users = usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        //매장 조회
        Store store =storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 매장입니다. "));

        //등록 이미 된거 있는지 조회 ( 중복 제거 )
        boolean exists = stampRepository.existsByUsersAndStore(users, store);
        if (exists) {
            throw new IllegalArgumentException("이미 이 매장의 스탬프가 등록되어 있습니다.");
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
    public StampAddResponseDto addStamp(Long userId, Long storeId, Long orderId)
    { // 1) 유저 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2) 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));

        // 3️) 주문 내역 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

       // + 추가 ))주문이 해당 매장의 주문이 맞는지 검증
        if (!order.getStore().getId().equals(store.getId())) {
            throw new IllegalArgumentException("해당 주문은 선택한 매장의 주문이 아닙니다.");
        }

        //+ 추가 ))이미 이주문으로 스탬프 적립된적 있는지 확인
        boolean alreadyAdded = stampRepository.existsByOrder(order);
        if (alreadyAdded) {
            throw new IllegalArgumentException("이미 이 주문에 대해 스탬프가 적립되었습니다.");
        }

        // 4)  총 주문 금액 확인
        Integer totalPrice = order.getTotalPrice();

        // 5) 매장 적립 기준 조회 ( 기준 금액 조회 )
        Integer requiredAmount= store.getRequiredAmount();

        // 6) 유저의 해당 매장 스탬프판 조회
        Stamp stamp = stampRepository.findByUsersAndStore(user, store)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장의 스탬프판이 없습니다."));

        // 7️) 적립 개수 계산 (총 주문 금액 / 적립 기준)
        Integer addCount = totalPrice / requiredAmount;

        if (addCount == 0) {
            throw new IllegalArgumentException(requiredAmount + "원 이상 주문 시 스탬프가 적립됩니다.");
        }

        int currentCount = stamp.getCurrentCount() + addCount;
        int maxCount = store.getMaxCount();

        //유저 누적 스탬프 수 계산 로직
        user.setTotalStampSum(user.getTotalStampSum() + addCount);

        // 8)  maxCount 초과 시 쿠폰 발급 + 스탬프 초기화
        if (currentCount >= maxCount) {
            // 쿠폰 발급 로직 -> couponService에서 별도 구현 !
            couponService.createCoupon(user, store);


            //유저의 스탬프판 수 증가 ( 수정 필요)
            user.setCouponNum(user.getCouponNum() + 1);
            usersRepository.save(user);

            // 초과된 스탬프는 다음 판으로 넘김
            currentCount = currentCount - maxCount;


        }

        // 9) 스탬프 업데이트 및 저장
        stamp.setCurrentCount(currentCount);

        //해당 주문과 연결
        stamp.setOrder(order);
        stampRepository.save(stamp);

        //누적 스탬프 수 반영한 유저 정보 저장
        usersRepository.save(user);

        // 10)  응답 DTO 반환
        return StampAddResponseDto.from(stamp);

    }


    //내가 현재 가진 스탬프 히스토리 조회 ( 과거 )

    @Transactional(readOnly = true)
    public List<StampHistoryResponseDto> getStampHistory(Long userId) {
        // 유저 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 유저의 쿠폰 목록 조회
        List<Coupon> coupons = couponRepository.findByUsers(user);

        // DTO 변환
        return coupons.stream()
                .map(StampHistoryResponseDto::from)
                .collect(Collectors.toList());
    }

    // 내가 현재 가진 스탬프 리스트 조회
    @Transactional(readOnly = true)
    public List<MyStampResponseDto> getMyStamps(Long userId) {

        // 유저 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        //유저의 스탬프 목록 조회
        List<Stamp> stamps = stampRepository.findByUsers(user);

        return stamps.stream()
                .map(MyStampResponseDto::from)
                .collect(Collectors.toList());

    }

    // 스탬프 즐겨찾기 설정

    @Transactional
    public void createFavoriteStamp(Long userId, Long stampId)
    {
        Users users =usersRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //소유자 확인
        if(!stamp.getUsers().equals(users)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        if(stamp.isFavorite()) {
            throw new ApplicationException(ErrorCode.ALREADY_FAVORITE);
        }

        stamp.setFavorite(true);
    }

    @Transactional
    //스탬프 삭제
    public void deleteStamp(Long userId, Long stampId)
    {

        //유저 확인
        Users users =usersRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        //스탬프 확인
        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //스탬프 소유자 확인
        if (!stamp.getUsers().getUserId().equals(userId)) {
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

    @Transactional
    //스탬프 개별조회
    public MyStampResponseDto getStampDetail(Long userId,Long stampId)
    {

        //유저 확인
        Users users =usersRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        //스탬프 확인
        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //스탬프 소유자 확인
        if (!stamp.getUsers().getUserId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }



        //스탬프 DTO 변환
        return MyStampResponseDto.from(stamp);

    }

}
