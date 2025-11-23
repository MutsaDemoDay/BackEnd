package backend.stamp.manager.service;

import backend.stamp.account.entity.Account;
import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.businesshour.repository.BusinessHourRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import backend.stamp.manager.dto.StoreProfileDto;
import backend.stamp.manager.dto.StoreProfileDto.BusinessHourData;
import backend.stamp.manager.dto.StoreProfileDto.MenuData;
import backend.stamp.manager.dto.StoreProfileDto.StoreInfo;
import backend.stamp.manager.entity.Manager;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.manager.repository.ManagerRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.storemenu.entity.StoreMenu;
import backend.stamp.storemenu.repository.StoreMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerProfileService {

    private final ManagerRepository managerRepository;
    private final StoreRepository storeRepository;
    private final BusinessHourRepository businessHourRepository;
    private final StoreMenuRepository storeMenuRepository;
    private final ObjectStorageService objectStorageService;

    private static final int MAX_MENU_COUNT = 10;

    /**
     * 매장 프로필 조회
     */
    @Transactional(readOnly = true)
    public StoreProfileDto getStoreProfile() {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Manager manager = managerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByManager(manager)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        List<BusinessHour> hours = businessHourRepository.findByStore(store);
        List<StoreMenu> menus = storeMenuRepository.findByStore(store);

        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setStoreId(store.getId());
        storeInfo.setStoreName(store.getName());
        storeInfo.setPhone(store.getPhone());
        storeInfo.setCategory(store.getCategory());
        storeInfo.setStoreImageUrl(store.getStoreImageUrl());
        storeInfo.setStampImageUrl(store.getStampImageUrl());
        storeInfo.setStoreUrl(store.getStoreUrl());
        storeInfo.setSns(store.getSns());
        storeInfo.setRequiredAmount(store.getRequiredAmount());
        storeInfo.setReward(store.getReward());
        storeInfo.setMaxCount(store.getMaxCount());
        storeInfo.setVerificationCode(store.getVerificationCode());

        List<BusinessHourData> businessHourDtos = hours.stream()
                .map(h -> {
                    BusinessHourData dto = new BusinessHourData();
                    dto.setId(h.getId());
                    dto.setDay(h.getDay());
                    dto.setOpenTime(h.getOpenTime());
                    dto.setCloseTime(h.getCloseTime());
                    dto.setIsHoliday(h.isHoliday());
                    // 응답에서는 action 비워둠
                    return dto;
                })
                .toList();

        List<MenuData> menuDtos = menus.stream()
                .map(m -> {
                    MenuData dto = new MenuData();
                    dto.setId(m.getId());
                    dto.setName(m.getName());
                    dto.setPrice(m.getPrice());
                    dto.setContent(m.getContent());
                    dto.setImageUrl(m.getImageUrl());
                    // 응답에서는 action 비워둠
                    return dto;
                })
                .toList();

        storeInfo.setBusinessHours(businessHourDtos);
        storeInfo.setMenus(menuDtos);

        StoreProfileDto result = new StoreProfileDto();
        result.setStore(storeInfo);
        return result;
    }

    /**
     * 매장 프로필 수정
     * - 텍스트 정보는 null 이 아닌 것만 반영
     * - 영업시간/메뉴는 action + id 기반으로 부분 추가/수정/삭제
     * - storeImage: 단일 이미지 (null 이 아니고 비어있을 때만 교체)
     * - stampImage: 단일 이미지 (스탬프 이미지)
     * - menuImages: 각 MenuData 와 index 를 맞춘 리스트 (CREATE/UPDATE 시에만 사용)
     */
    public StoreProfileDto updateStoreProfile(StoreProfileDto requestDto,
                                              MultipartFile storeImage,
                                              MultipartFile stampImage,
                                              List<MultipartFile> menuImages) {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Manager manager = managerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByManager(manager)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        StoreInfo req = Optional.ofNullable(requestDto.getStore())
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_REQUEST));

        // 1) 매장 기본 정보
        if (req.getStoreName() != null) {
            store.setName(req.getStoreName());
        }
        if (req.getPhone() != null) {
            store.setPhone(req.getPhone());
        }
        if (req.getCategory() != null) {
            store.setCategory(req.getCategory());
        }
        if (req.getStoreUrl() != null) {
            store.setStoreUrl(req.getStoreUrl());
        }
        if (req.getSns() != null) {
            store.setSns(req.getSns());
        }
        if (req.getRequiredAmount() != null) {
            store.setRequiredAmount(req.getRequiredAmount());
        }
        if (req.getReward() != null) {
            store.setReward(req.getReward());
        }
        if (req.getMaxCount() != null) {
            store.setMaxCount(req.getMaxCount());
        }
        // verificationCode 는 온보딩에서만 세팅, 여기서는 절대 변경하지 않음

        if (storeImage != null && !storeImage.isEmpty()) {
            String url = objectStorageService.uploadFile(storeImage);
            store.setStoreImageUrl(url);
        }

        if (stampImage != null && !stampImage.isEmpty()) {
            String url = objectStorageService.uploadFile(stampImage);
            store.setStampImageUrl(url);
        }

        storeRepository.save(store);

        handleBusinessHoursPartialUpdate(store, req.getBusinessHours());

        handleMenusPartialUpdate(store, req.getMenus(), menuImages);

        return getStoreProfile();
    }

    private void handleBusinessHoursPartialUpdate(Store store, List<BusinessHourData> requests) {

        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<BusinessHour> existing = businessHourRepository.findByStore(store);
        Map<Long, BusinessHour> existingMap = existing.stream()
                .collect(Collectors.toMap(BusinessHour::getId, h -> h));

        for (BusinessHourData dto : requests) {

            String action = normalizeAction(dto.getAction(), dto.getId());

            switch (action) {
                case "DELETE" -> {
                    if (dto.getId() != null) {
                        BusinessHour target = existingMap.remove(dto.getId());
                        if (target != null) {
                            businessHourRepository.delete(target);
                        }
                    }
                }
                case "UPDATE" -> {
                    if (dto.getId() == null) {
                        throw new ApplicationException(ErrorCode.INVALID_REQUEST);
                    }
                    BusinessHour hour = existingMap.get(dto.getId());
                    if (hour == null) {
                        throw new ApplicationException(ErrorCode.BUSINESS_HOUR_NOT_FOUND);
                    }
                    if (dto.getDay() != null) {
                        hour.setDay(dto.getDay());
                    }
                    hour.setOpenTime(dto.getOpenTime());
                    hour.setCloseTime(dto.getCloseTime());
                    if (dto.getIsHoliday() != null) {
                        hour.setHoliday(dto.getIsHoliday());
                    }
                }
                case "CREATE" -> {
                    BusinessHour hour = new BusinessHour(
                            null,
                            dto.getDay(),
                            dto.getOpenTime(),
                            dto.getCloseTime(),
                            Boolean.TRUE.equals(dto.getIsHoliday()),
                            store
                    );
                    businessHourRepository.save(hour);
                }
                default -> throw new ApplicationException(ErrorCode.INVALID_REQUEST);
            }
        }
    }

    private void handleMenusPartialUpdate(Store store,
                                          List<MenuData> requests,
                                          List<MultipartFile> menuImages) {

        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<StoreMenu> existing = storeMenuRepository.findByStore(store);
        Map<Long, StoreMenu> existingMap = existing.stream()
                .collect(Collectors.toMap(StoreMenu::getId, m -> m));

        int currentCount = existing.size();
        int finalCount = currentCount;

        for (MenuData dto : requests) {
            String action = normalizeAction(dto.getAction(), dto.getId());

            if ("DELETE".equals(action) && dto.getId() != null && existingMap.containsKey(dto.getId())) {
                finalCount--;
            } else if ("CREATE".equals(action) && dto.getId() == null) {
                finalCount++;
            }
        }

        if (finalCount > MAX_MENU_COUNT) {
            throw new ApplicationException(ErrorCode.MENU_LIMIT_EXCEEDED);
        }

        for (int i = 0; i < requests.size(); i++) {
            MenuData dto = requests.get(i);

            MultipartFile imageFile = null;
            if (menuImages != null && menuImages.size() > i) {
                imageFile = menuImages.get(i);
            }

            String action = normalizeAction(dto.getAction(), dto.getId());

            switch (action) {
                case "DELETE" -> {
                    if (dto.getId() != null) {
                        StoreMenu target = existingMap.remove(dto.getId());
                        if (target != null) {
                            storeMenuRepository.delete(target);
                        }
                    }
                }
                case "UPDATE" -> {
                    if (dto.getId() == null) {
                        throw new ApplicationException(ErrorCode.INVALID_REQUEST);
                    }
                    StoreMenu menu = existingMap.get(dto.getId());
                    if (menu == null) {
                        throw new ApplicationException(ErrorCode.MENU_NOT_FOUND);
                    }

                    if (dto.getName() != null) {
                        menu.setName(dto.getName());
                    }
                    menu.setPrice(dto.getPrice());
                    menu.setContent(dto.getContent());

                    if (imageFile != null && !imageFile.isEmpty()) {
                        String url = objectStorageService.uploadFile(imageFile);
                        menu.setImageUrl(url);
                    }
                }
                case "CREATE" -> {
                    String imageUrl = null;
                    if (imageFile != null && !imageFile.isEmpty()) {
                        imageUrl = objectStorageService.uploadFile(imageFile);
                    }

                    StoreMenu menu = StoreMenu.create(
                            store,
                            dto.getName(),
                            dto.getPrice(),
                            dto.getContent(),
                            imageUrl
                    );

                    storeMenuRepository.save(menu);
                }
                default -> throw new ApplicationException(ErrorCode.INVALID_REQUEST);
            }
        }
    }

    /**
     * action 기본값 보정:
     * - action 이 null/공백이고 id 가 있으면 UPDATE
     * - action 이 null/공백이고 id 가 없으면 CREATE
     */
    private String normalizeAction(String action, Long id) {
        if (action == null || action.isBlank()) {
            return (id != null) ? "UPDATE" : "CREATE";
        }
        return action.toUpperCase(Locale.ROOT);
    }
}
