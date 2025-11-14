package backend.stamp.stamp.service;


import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.order.repository.OrderRepository;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StampFavoriteService {

    private final StampRepository stampRepository;
    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final OrderRepository orderRepository;

    // 스탬프 즐겨찾기 설정

    @Transactional
    public void createFavoriteStamp(Long userId, Long stampId)
    {
        Users users = usersRepository.findByAccount_AccountId(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //소유자 확인
        if(!stamp.getUsers().getAccount().getAccountId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        if(stamp.isFavorite()) {
            throw new ApplicationException(ErrorCode.ALREADY_FAVORITE_STAMP);
        }

        stamp.setFavorite(true);
    }

    //스탬프 즐겨찾기 취소
    @Transactional
    public void deleteFavoriteStamp(Long userId, Long stampId)
    {

        Users users = usersRepository.findByAccount_AccountId(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        // 소유자 확인
        if (!stamp.getUsers().getAccount().getAccountId().equals(userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        // 이미 즐겨찾기가 아닌 경우
        if (!stamp.isFavorite()) {
            throw new ApplicationException(ErrorCode.NOT_FAVORITE_STAMP);
        }

        // 즐겨찾기 취소
        stamp.setFavorite(false);
    }
}
