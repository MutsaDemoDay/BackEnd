package backend.stamp.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewerProfileResponse {
    private String nickname;

    private int totalStampCount;
    private int reviewCount;

    private List<StampInfo> stamps;
    private List<ReviewInfo> reviews;

    @Getter @Builder
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
