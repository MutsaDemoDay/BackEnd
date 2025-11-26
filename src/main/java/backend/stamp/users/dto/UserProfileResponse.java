package backend.stamp.users.dto;

import backend.stamp.review.dto.ReviewerProfileResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserProfileResponse {
    private String nickname;

    private int totalStampCount;

    private Integer couponNum;

    private List<UserProfileResponse.StampInfo> stamps;
    private List<UserProfileResponse.ReviewInfo> reviews;

    @Getter
    @Builder
    public static class StampInfo {
        private String storeName;
        private LocalDateTime date;
        private String stampImageUrl;
        private String stampReward;
    }

    @Getter @Builder
    public static class ReviewInfo {
        private String storeName;
        private byte rate;
        private String content;
        private LocalDateTime reviewDate;
        private String reviewImageUrl;
    }
}
