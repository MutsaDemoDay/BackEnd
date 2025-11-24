package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.UserOnboardingRequest;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.favstore.repository.FavStoreRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ObjectStorageService objectStorageService;
    private final AccountRepository accountRepository;

    public void completeOnboarding(UserOnboardingRequest request, MultipartFile profileImage) {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Account account = accountRepository.findById(currentAccount.getAccountId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Users user = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        // 이미지 업로드
        String profileImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = objectStorageService.uploadFile(profileImage);
        }

        user.setAddress(request.getAddress());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setGender(request.getGender());
        user.setProfileImageUrl(profileImageUrl);

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

        account.completeUserOnboarding();

        usersRepository.save(user);
        accountRepository.save(account);
    }

}
