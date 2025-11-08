package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.auth.dto.request.ManagerOnboardingRequest;
import backend.stamp.auth.dto.response.ManagerOnboardingResponse;
import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.businesshour.repository.BusinessHourRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import backend.stamp.manager.entity.Manager;
import backend.stamp.manager.repository.ManagerRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ManagerOnboardingService {
    private final ManagerRepository managerRepository;
    private final StoreRepository storeRepository;
    private final BusinessHourRepository businessHourRepository;

    public ManagerOnboardingResponse completeOnboarding(ManagerOnboardingRequest request) {
        Account currentAccount = SecurityUtil.getCurrentAccount();

        Manager manager = managerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        String finalStorePhone = request.getUseManagerPhoneNumber()
                ? currentAccount.getPhone()
                : request.getStoreTel();

        Store newStore = new Store(
                null, // id
                request.getStoreName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                finalStorePhone,
                null, // businessHours
                null, // storeUrl <- DTO에서 제외됨
                null, // sns <- DTO에서 제외됨
                request.getStoreImageUrl(),
                request.getStampImageUrl(),
                request.getRequiredAmount(),
                request.getReward(),
                "10개 적립 시 무료 커피", // stampReward <- DTO에 없으므로 임시값 할당
                10, // maxCount <- DTO에 없으므로 임시값 할당
                request.getCategory(),
                // 나머지 List 필드는 null로 초기화
                null, null, null, null, null, null,
                manager // Manager 연결
        );
        Store savedStore = storeRepository.save(newStore);

        businessHourRepository.deleteByStore(savedStore);

        List<BusinessHour> newBusinessHours = request.getBusinessHours().stream()
                .map(hourRequest -> {
                    return new BusinessHour(
                            null,
                            hourRequest.getDay(),
                            hourRequest.getOpenTime(),
                            hourRequest.getCloseTime(),
                            hourRequest.getIsHoliday(),
                            savedStore
                    );
                })
                .toList();

        businessHourRepository.saveAll(newBusinessHours);

        return ManagerOnboardingResponse.builder()
                .storeId(savedStore.getId())
                .build();
    }
}
