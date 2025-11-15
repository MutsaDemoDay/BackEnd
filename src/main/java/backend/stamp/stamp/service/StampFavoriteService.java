package backend.stamp.stamp.service;


import backend.stamp.account.entity.Account;
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
    public void createFavoriteStamp(Account account, Long stampId)
    {
        // 인증/계정 체크
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

//스탬프 소유자 체크
        Users stampOwner = stamp.getUsers();

        if (stampOwner == null ||
                stampOwner.getAccount() == null ||
                !stampOwner.getAccount().getAccountId().equals(account.getAccountId())) {

            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        // 이미 즐겨찾기인 경우
        if (stamp.isFavorite()) {
            throw new ApplicationException(ErrorCode.ALREADY_FAVORITE_STAMP);
        }

        stamp.setFavorite(true);
    }

    //스탬프 즐겨찾기 취소
    @Transactional
    public void deleteFavoriteStamp(Account account, Long stampId)
    {
        //계정 체크
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Stamp stamp =stampRepository.findById(stampId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STAMP_NOT_FOUND));

        //스탬프 소유자 체크
        Users stampOwner = stamp.getUsers();

        // 소유자 확인
        if (stampOwner == null ||
                stampOwner.getAccount() == null ||
                !stampOwner.getAccount().getAccountId().equals(account.getAccountId())) {

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
