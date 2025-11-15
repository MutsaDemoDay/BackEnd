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

    public List<FavStoreListResponseDto> getMyFavStores(Account account, double userLat, double userLon) {

        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Users users = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        List<Store> stores = storeRepository.findAll();
        //유저의 즐겨찾기 매장 확인
        List<Long> favStoreIds = favStoreRepository.findFavStoreIdsByUsers_Account(account);

        return stores.stream()
                .map(store -> {

                    Double lat = store.getLatitude();
                    Double lon = store.getLongitude();

                    double distanceMeters;
                    String formattedDistance;
                    if (lat == null || lon == null) {
                        distanceMeters = Double.MAX_VALUE;
                        formattedDistance = "위치 정보 없음";
                    } else {
                        distanceMeters = DistanceUtil.calculateDistance(userLat, userLon, lat, lon);
                        formattedDistance = DistanceFormatter.formatDistance(distanceMeters);
                    }

                    boolean isFavorite = favStoreIds.contains(store.getId());
                    return new Object[]{
                            distanceMeters,
                            FavStoreListResponseDto.builder()
                                    .storeId(store.getId())
                                    .storeName(store.getName())
                                    .storeCategory(store.getCategory())
                                    .storeAddress(store.getAddress())
                                    .storeImageUrl(store.getStoreImageUrl())
                                    .distance(formattedDistance)
                                    .isFavorite(isFavorite)
                                    .build()
                    };

                })
                .sorted(Comparator.comparing(obj -> (Double) obj[0]))

                // ✔ 마지막에 DTO만 추출
                .map(obj -> (FavStoreListResponseDto) obj[1])

                .toList();

    }
}