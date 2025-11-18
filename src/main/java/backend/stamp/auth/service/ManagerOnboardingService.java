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
import java.util.Random;


@Service
@RequiredArgsConstructor
@Transactional
public class ManagerOnboardingService {
    private final ManagerRepository managerRepository;
    private final StoreRepository storeRepository;
    private final BusinessHourRepository businessHourRepository;

    //랜덤코드 생성용
    private final Random random = new Random();

    public ManagerOnboardingResponse completeOnboarding(ManagerOnboardingRequest request) {
        Account currentAccount = SecurityUtil.getCurrentAccount();

        Manager manager = managerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));


        //4자리 고유코드 생성
        String verificationCode = generateUniqueCode();

        Store newStore = Store.builder()
                .name(request.getStoreName())
                .phone(request.getPhone())
                .storeImageUrl(request.getStoreImageUrl())
                .stampImageUrl(request.getStampImageUrl())
                .requiredAmount(request.getRequiredAmount())
                .reward(request.getReward())
                .category(request.getCategory())
                .verificationCode(verificationCode)
                .manager(manager)
                .build();

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
                .verificationCode(verificationCode)
                .build();
    }
    private String generateUniqueCode() {
        String code;
        do {
            code = String.format("%04d", random.nextInt(10000)); // 0000~9999
        } while (storeRepository.existsByVerificationCode(code));
        return code;
    }
}
