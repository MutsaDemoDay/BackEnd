package backend.stamp.favstore.service;


import backend.stamp.account.entity.Account;
import backend.stamp.favstore.dto.FavStoreListResponseDto;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.favstore.repository.FavStoreRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.util.DistanceFormatter;
import backend.stamp.global.util.DistanceUtil;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavStoreService {
    private final FavStoreRepository favStoreRepository;
    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;

    // 매장 즐겨찾기 등록

    @Transactional
    public void createFavStore(Account account, Long storeId) {

        //유저 조회
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        if (storeId == null) {
            throw new ApplicationException(ErrorCode.STORE_NOT_FOUND);
        }
        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        //가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        //중복 즐겨찾기 방지
        if (favStoreRepository.existsByUsersAndStore(users, store)) {
            throw new ApplicationException(ErrorCode.ALREADY_FAVORITE_STORE);
        }

        FavStore favstore = FavStore.builder()
                .users(users)
                .store(store).build();

        favStoreRepository.save(favstore);


    }

    //매장 즐겨찾기 취소
    public void deleteFavStore(Account account, Long storeId) {

        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        if (storeId == null) {
            throw new ApplicationException(ErrorCode.STORE_NOT_FOUND);
        }

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        FavStore favStore = favStoreRepository.findByUsersAndStore(users, store)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FAVORITE_STORE_NOT_FOUND));

        favStoreRepository.delete(favStore);
    }


    //즐겨찾기 매장 리스트 조회

    public List<FavStoreListResponseDto> getMyFavStores(Account account) {
    //계정 체크 (npe 방지)
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //유저 조회
        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        //유저의 즐겨찾기 매장 확인

        List<FavStore> favStores = favStoreRepository.findByUsers(users);

        //유저의 즐겨찾기 매장이 없는경우
        if (favStores == null || favStores.isEmpty()) {
            return List.of();
        }



        //DTO 변환

        return favStores.stream()
                .map(store->FavStoreListResponseDto.from(store.getStore(),true)) // DTO 변환
                .collect(Collectors.toList());

    }
}