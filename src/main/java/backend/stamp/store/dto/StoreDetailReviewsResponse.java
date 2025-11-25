package backend.stamp.store.dto;

import backend.stamp.review.dto.ReviewerProfileResponse.ReviewInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class StoreDetailReviewsResponse {
    private double averageRating;
    private int totalReviewCount;
    private Map<Integer, Long> ratingDistribution;
    private boolean isStampBoardCompleted;

    private boolean isUserReviewWritten;

    private List<StoreReviewDetail> reviews;

    @Getter
    @Builder
    public static class StoreReviewDetail {
        private Long reviewId;
        private Long reviewerId;
        private String reviewerNickname;
        private Integer totalStampSum;
        private String reviewContent;
        private byte rate;
        private LocalDateTime reviewDate;
        private String representativeBadgeName;
    }
}
