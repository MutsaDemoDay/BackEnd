package backend.stamp.manager.service;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.level.entity.Level;
import backend.stamp.manager.dto.StampCustomerResponse;
import backend.stamp.manager.dto.StampSettingRequest;
import backend.stamp.manager.dto.StampSettingResponse;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class ManagerService {
    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final StampRepository stampRepository;
    private final ObjectStorageService objectStorageService;
    public String setStamp(StampSettingRequest request, MultipartFile image){
        Store store = storeRepository.findByName(request.storeName())
                .orElseThrow(()-> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        //이미지
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = objectStorageService.uploadFile(image);
        }
        //세팅 값
        store.setRequiredAmount(request.requiredAmount());
        store.setReward(request.reward());
        store.setMaxCount(request.maxCnt());
        if (imageUrl != null) {
            store.setStampImageUrl(imageUrl);
        }
        storeRepository.save(store);
        return imageUrl;
    }

    public StampSettingResponse getStamp(String name){
        Store store = storeRepository.findByName(name)
                .orElseThrow(()-> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        return new StampSettingResponse(
                store.getName(),
                store.getRequiredAmount(),
                store.getReward(),
                store.getMaxCount(),
                store.getStampImageUrl()
        );
    }


    public List<StampCustomerResponse> getCustomers(String storeName) {
        // 1. 스토어 존재하는지 체크
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
        // 2. 해당 store에 찍은 모든 userId 조회
        List<Long> userIds = stampRepository.findUserIdsByStoreName(storeName);
        // 3. userId 각각의 상세 정보 조회
        List<StampCustomerResponse> list = new ArrayList<>();

        for (Long userId : userIds) {
            Users user = usersRepository.findUserWithAccountAndLevel(userId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
            Account account = user.getAccount();
            Level level = user.getLevel();
            list.add(
                    new StampCustomerResponse(
                            user.getUserId(),
                            user.getNickname(),
                            account != null ? account.getCreatedAt() : null,
                            level != null ? level.getLevel() : null
                    )
            );
        }
        return list;
    }
}
