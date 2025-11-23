package backend.stamp.reward.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RewardDashboardResponse {

    private UserRewardInfo myInfo;

    private List<StamperRank> topStampers;

    @Getter
    @Setter
    public static class UserRewardInfo {
        private String nickname;
        private Integer totalStampSum;
        private String topPercent;
        private String profileImageUrl;
    }

    @Getter
    @Setter
    public static class StamperRank {
        private String nickname;
        private String profileImageUrl;
        private Integer totalStampSum;
        private String representativeBadgeName;
    }
}
