package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.auth.dto.request.UserOnboardingRequest;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.favstore.repository.FavStoreRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserOnboardingService {
    private final UsersRepository usersRepository;
    private final FavStoreRepository favStoreRepository;
    private final StoreRepository storeRepository;

    public void completeOnboarding(UserOnboardingRequest request) {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Users user = usersRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        user.setNickname(request.getNickname());
        user.setGender(request.getGender());

        List<Long> safeFavStoreIds = Optional.ofNullable(request.getFavStoreId())
                .orElse(Collections.emptyList());

        if (!safeFavStoreIds.isEmpty()) {
            List<FavStore> newFavStores = safeFavStoreIds.stream()
                    .map(storeId -> {
                        Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

                        return FavStore.builder()
                                .users(user)
                                .store(store)
                                .build();
                    })
                    .toList();

            favStoreRepository.saveAll(newFavStores);
        }

        usersRepository.save(user);
    }

}
