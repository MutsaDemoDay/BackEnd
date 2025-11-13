package backend.stamp.favstore.service;


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
    public void createFavStore(Long userId, Long storeId) {

        //유저 조회
        Users users = usersRepository.findByAccount_AccountId(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        //가게 조회
        Store store=storeRepository.findById(storeId)
                .orElseThrow(()->new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        //중복 즐겨찾기 방지
        if(favStoreRepository.existsByUsersAndStore(users,store))
        {
            throw new ApplicationException(ErrorCode.ALREADY_FAVORITE_STORE);
        }

        FavStore favstore=FavStore.builder()
                .users(users)
                .store(store).build();

        favStoreRepository.save(favstore);


    }

    //매장 즐겨찾기 취소
    public void deleteFavStore(Long userId, Long storeId) {

        Users users = usersRepository.findByAccount_AccountId(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        FavStore favStore = favStoreRepository.findByUsersAndStore(users, store)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FAVORITE_STORE_NOT_FOUND));

        favStoreRepository.delete(favStore);
    }


    //즐겨찾기 매장 리스트 조회

    public List<FavStoreListResponseDto> getMyFavStores(Long userId,double userLat,double userLon) {

        Users users = usersRepository.findByAccount_AccountId(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        List<Store> stores = storeRepository.findAll();
        //유저의 즐겨찾기 매장 확인
        List<Long> favStoreIds =favStoreRepository.findFavStoreIdsByUsers_UserId(userId);

        return stores.stream()
                .map(store->{
                    double distanceMeters = DistanceUtil.calculateDistance(userLat,userLon,
                            store.getLatitude(),store.getLongitude());
                    String formattedDistance = DistanceFormatter.formatDistance(distanceMeters);
                boolean isFavorite = favStoreIds.contains(store.getId());

                return FavStoreListResponseDto.builder()
                        .storeId(store.getId())
                        .storeName(store.getName())
                        .storeCategory(store.getCategory())
                        .storeAddress(store.getAddress())
                        .storeImageUrl(store.getStoreImageUrl())
                        .distance(formattedDistance)
                        .isFavorite(isFavorite)
                        .build();
                })

                //가까운순으로 정렬 !!
                .sorted(Comparator.comparing(dto -> dto.getDistance()))
                        .toList();

    }
}
