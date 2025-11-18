package backend.stamp.manager.service;

import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.dto.StampCustomerResponse;
import backend.stamp.manager.dto.StampSettingRequest;
import backend.stamp.manager.dto.StampSettingResponse;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class ManagerService {
    private final StoreRepository storeRepository;
    private final StampRepository stampRepository;
    private final UsersRepository usersRepository;
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

//    public StampCustomerResponse getCustomers(String name){
//        Store store = storeRepository.findByName(name)
//                .orElseThrow(()-> new ApplicationException(ErrorCode.STORE_NOT_FOUND));
//        // id
//        List<Long> userIds = stampRepository.findDistinctUserIdsByStoreId(store.getId());
//        if (userIds.isEmpty()) {
//            return List.of();
//        }
//        // 닉네임
//        List<String> nicknames = usersRepository.findNicknamesByUserIds(userIds);
//        // 가입일
//        // 레벨
//    }
}
