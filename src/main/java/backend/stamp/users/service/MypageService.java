package backend.stamp.users.service;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.review.repository.ReviewRepository;
import backend.stamp.users.dto.UserAccountInfoResponse;
import backend.stamp.users.dto.UserProfileResponse;
import backend.stamp.users.dto.UserSettingsDto;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MypageService {

    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectStorageService objectStorageService;

    /**
     * 유저 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile() {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Users user = usersRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        List<UserProfileResponse.ReviewInfo> reviews = user.getReviews().stream()
                .map(review -> UserProfileResponse.ReviewInfo.builder()
                        .storeName(review.getStore() != null ? review.getStore().getName() : null)
                        .rate(review.getRate())
                        .content(review.getContent())
                        .reviewDate(review.getCreatedAt())
                        .reviewImageUrl(review.getReviewImageUrl())
                        .build())
                .collect(Collectors.toList());

        List<UserProfileResponse.StampInfo> stampInfos = user.getStamps().stream()
                .map(stamp -> UserProfileResponse.StampInfo.builder()
                        .storeName(stamp.getStore() != null ? stamp.getStore().getName() : null)
                        .date(stamp.getDate())
                        .stampImageUrl(stamp.getStore() != null ? stamp.getStore().getStampImageUrl() : null)
                        .stampReward(stamp.getStore() != null ? stamp.getStore().getStampReward() : null)
                        .build())
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .nickname(user.getNickname())
                .totalStampCount(user.getTotalStampSum() != null ? user.getTotalStampSum() : 0)
                .couponNum(user.getCouponNum())
                .stamps(stampInfos)
                .reviews(reviews)
                .build();
    }

    /**
     * 유저 계정 정보 조회
     */
    @Transactional(readOnly = true)
    public UserAccountInfoResponse getUserAccountInfo() {
        Account currentAccount = SecurityUtil.getCurrentAccount();

        Users user = usersRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        UserAccountInfoResponse response = new UserAccountInfoResponse();
        response.setEmail(currentAccount.getEmail());
        response.setLoginId(currentAccount.getLoginId());
        response.setJoinedAt(currentAccount.getCreatedAt());
        return response;
    }

    /**
     * 유저 설정 조회
     */
    @Transactional(readOnly = true)
    public UserSettingsDto getUserSettings() {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Users user = usersRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        UserSettingsDto dto = new UserSettingsDto();
        dto.setNickname(user.getNickname());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setRepresentativeBadgeName(user.getRepresentativeBadgeName());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setLatitude(user.getLatitude());
        dto.setLongitude(user.getLongitude());

        return dto;
    }

    /**
     * 유저 설정 부분 업데이트
     *
     * 프로필 이미지 처리 규칙:
     *  1) profileImage 파일이 넘어오면 → ObjectStorage 에 업로드 후 해당 URL 로 덮어씀
     *  2) 파일이 없고 requestDto.profileImageUrl 이
     *     - null        → 이미지 URL 변경하지 않음 (기존 값 유지)
     *     - 빈 문자열("") → 프로필 이미지 삭제 (null 로 세팅, 혹은 기본 이미지로 사용하도록)
     *     - 그 외 문자열 → 해당 문자열을 URL 로 사용
     *
     * 나머지 필드들은 부분 업데이트:
     *  - requestDto 의 각 필드가 null 이 아닐 때에만 엔티티 값을 변경
     */
    public UserSettingsDto updateUserSettings(UserSettingsDto requestDto, MultipartFile profileImage) {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Users user = usersRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = objectStorageService.uploadFile(profileImage);
            user.setProfileImageUrl(profileImageUrl);

        } else {
            String requestedUrl = requestDto.getProfileImageUrl();

            if (requestedUrl != null) {
                if (requestedUrl.isEmpty()) {
                    user.setProfileImageUrl(null);
                } else {
                    user.setProfileImageUrl(requestedUrl);
                }
            }
        }

        if (requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
        }

        if (requestDto.getRepresentativeBadgeName() != null) {
            user.setRepresentativeBadgeName(requestDto.getRepresentativeBadgeName());
        }
        if (requestDto.getGender() != null) {
            user.setGender(requestDto.getGender());
        }
        if (requestDto.getAddress() != null) {
            user.setAddress(requestDto.getAddress());
        }
        if (requestDto.getLatitude() != null) {
            user.setLatitude(requestDto.getLatitude());
        }
        if (requestDto.getLongitude() != null) {
            user.setLongitude(requestDto.getLongitude());
        }

        UserSettingsDto response = new UserSettingsDto();
        response.setNickname(user.getNickname());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setRepresentativeBadgeName(user.getRepresentativeBadgeName());
        response.setGender(user.getGender());
        response.setAddress(user.getAddress());
        response.setLatitude(user.getLatitude());
        response.setLongitude(user.getLongitude());

        return response;
    }

}
