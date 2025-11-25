package backend.stamp.review.service;

import backend.stamp.account.entity.Account;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.object.ObjectStorageService;
import backend.stamp.review.dto.ReviewCreateRequest;
import backend.stamp.review.dto.ReviewerProfileResponse;
import backend.stamp.review.dto.ReviewerProfileResponse.ReviewInfo;
import backend.stamp.review.dto.ReviewerProfileResponse.StampInfo;
import backend.stamp.review.entity.Review;
import backend.stamp.review.repository.ReviewRepository;
import backend.stamp.store.dto.StoreDetailReviewsResponse;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final ObjectStorageService objectStorageService;

    private static final int MAX_REVIEW_LENGTH = 1000;

    @Transactional(readOnly = true)
    public ReviewerProfileResponse getReviewerProfile(Long targetUserId) {

        Users user = usersRepository.findUserWithAccountAndLevel(targetUserId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        List<ReviewerProfileResponse.ReviewInfo> reviews = user.getReviews().stream()
                .map(review -> ReviewerProfileResponse.ReviewInfo.builder()
                        .storeName(review.getStore() != null ? review.getStore().getName() : null)
                        .rate(review.getRate())
                        .content(review.getContent())
                        .reviewDate(review.getCreatedAt())
                        .reviewImageUrl(review.getReviewImageUrl())
                        .build())
                .collect(Collectors.toList());

        List<ReviewerProfileResponse.StampInfo> stampInfos = user.getStamps().stream()
                .map(stamp -> ReviewerProfileResponse.StampInfo.builder()
                        .storeName(stamp.getStore() != null ? stamp.getStore().getName() : null)
                        .date(stamp.getDate())
                        .stampImageUrl(stamp.getStore() != null ? stamp.getStore().getStampImageUrl() : null)
                        .stampReward(stamp.getStore() != null ? stamp.getStore().getStampReward() : null)
                        .build())
                .collect(Collectors.toList());

        return ReviewerProfileResponse.builder()
                .nickname(user.getNickname())
                .totalStampCount(user.getTotalStampSum() != null ? user.getTotalStampSum() : 0)
                .reviewCount(reviews.size())
                .stamps(stampInfos)
                .reviews(reviews)
                .build();
    }

    @Transactional
    public Long createReview(Account account, ReviewCreateRequest request, MultipartFile reviewImage) {

        Users user = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        if (reviewRepository.existsByUsersAndStore(user, store)) {
            throw new ApplicationException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
        if (!couponRepository.existsByUsersAndStore(user, store)) {
            throw new ApplicationException(ErrorCode.STAMP_NOT_COMPLETED);
        }

        String reviewImageUrl = null;

        if (reviewImage != null && !reviewImage.isEmpty()) {
            reviewImageUrl = objectStorageService.uploadFile(reviewImage);
        }

        Review review = new Review();
        review.setUsers(user);
        review.setStore(store);
        review.setRate(request.getRate());
        review.setContent(request.getContent());
        review.setCreatedAt(LocalDateTime.now());
        review.setReviewImageUrl(reviewImageUrl);

        Review savedReview = reviewRepository.save(review);

        return savedReview.getId();
    }

    @Transactional(readOnly = true)
    public StoreDetailReviewsResponse getStoreDetailReviews(Long storeId, Account account) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        List<Review> allReviews = reviewRepository.findByStoreId(storeId);

        DoubleSummaryStatistics stats = allReviews.stream()
                .collect(Collectors.summarizingDouble(Review::getRate));
        double average = allReviews.isEmpty() ? 0.0 : stats.getAverage();
        int totalCount = (int) stats.getCount();

        Map<Integer, Long> distribution = allReviews.stream()
                .collect(Collectors.groupingBy(r -> (int) r.getRate(), Collectors.counting()));
        for (int i = 1; i <= 5; i++) distribution.putIfAbsent(i, 0L);

        boolean isStampBoardCompleted = false;
        boolean isUserReviewWritten = false;
        Users currentUser = null;
        if (account != null) {
            currentUser = usersRepository.findByAccount(account).orElse(null);
            if (currentUser != null) {
                isStampBoardCompleted = couponRepository.existsByUsersAndStore(currentUser, store);
                isUserReviewWritten = reviewRepository.existsByUsersAndStore(currentUser, store);
            }
        }


        List<StoreDetailReviewsResponse.StoreReviewDetail> reviewDetails = allReviews.stream()
                .map(review -> {
                    Users reviewer = review.getUsers();

                    Long reviewerId = reviewer != null ? reviewer.getUserId() : null;
                    String nickname = reviewer != null ? reviewer.getNickname() : "알 수 없음";
                    Integer totalStampSum = reviewer != null ? reviewer.getTotalStampSum() : 0;
                    String representativeBadge = reviewer != null ? reviewer.getRepresentativeBadgeName() : null;


                    return StoreDetailReviewsResponse.StoreReviewDetail.builder()
                            .reviewId(review.getId())
                            .reviewerId(reviewerId)
                            .reviewerNickname(nickname)
                            .totalStampSum(totalStampSum)
                            .reviewContent(review.getContent())
                            .rate(review.getRate())
                            .reviewDate(review.getCreatedAt())
                            .representativeBadgeName(representativeBadge)
                            .build();
                })
                .collect(Collectors.toList());

        return StoreDetailReviewsResponse.builder()
                .averageRating(average)
                .totalReviewCount(totalCount)
                .ratingDistribution(distribution)
                .isStampBoardCompleted(isStampBoardCompleted)
                .isUserReviewWritten(isUserReviewWritten)
                .reviews(reviewDetails)
                .build();
    }
}
