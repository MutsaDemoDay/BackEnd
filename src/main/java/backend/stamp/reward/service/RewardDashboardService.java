package backend.stamp.reward.service;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.reward.dto.RewardDashboardResponse;
import backend.stamp.reward.repository.RankingRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardDashboardService {

    private final UsersRepository usersRepository;
    private final RankingRepository rankingRepository;

    /**
     * 리워드 대시보드 전체 정보를 Account 기준으로 조회합니다.
     */
    @Transactional(readOnly = true)
    public RewardDashboardResponse getRewardDashboard(Account account) {

        // 1. Account → Users 조회
        Users currentUser = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        RewardDashboardResponse response = new RewardDashboardResponse();

        // 2. 내 정보
        RewardDashboardResponse.UserRewardInfo myInfo = composeMyRewardInfo(currentUser);
        response.setMyInfo(myInfo);

        // 3. 탑 랭커 리스트
        List<RewardDashboardResponse.StamperRank> topStampers = composeTopStampers(6);
        response.setTopStampers(topStampers);

        return response;
    }

    /**
     * 내 리워드 정보 DTO 생성
     */
    private RewardDashboardResponse.UserRewardInfo composeMyRewardInfo(Users user) {

        Long userRank = rankingRepository.getRankByTotalStampSum(user.getUserId());
        long totalUsers = rankingRepository.count();
        String topPercent = calculateTopPercentage(userRank, totalUsers);

        RewardDashboardResponse.UserRewardInfo myInfo = new RewardDashboardResponse.UserRewardInfo();
        myInfo.setNickname(user.getNickname());
        myInfo.setTotalStampSum(user.getTotalStampSum());
        myInfo.setTopPercent(topPercent);
        myInfo.setProfileImageUrl(user.getProfileImageUrl());

        return myInfo;
    }

    /**
     * 탑 N 스탬퍼 리스트 DTO 생성
     */
    private List<RewardDashboardResponse.StamperRank> composeTopStampers(int limit) {

        List<Users> rankerUsers = rankingRepository.findByOrderByTotalStampSumDesc(
                PageRequest.of(0, limit)
        );

        List<RewardDashboardResponse.StamperRank> stamperRanks = new ArrayList<>();

        for (Users ranker : rankerUsers) {
            RewardDashboardResponse.StamperRank dto = new RewardDashboardResponse.StamperRank();
            dto.setNickname(ranker.getNickname());
            dto.setProfileImageUrl(ranker.getProfileImageUrl());
            dto.setTotalStampSum(ranker.getTotalStampSum());
            dto.setRepresentativeBadgeName(getRepresentativeBadgeName(ranker));

            stamperRanks.add(dto);
        }

        return stamperRanks;
    }

    /**
     * 상위 몇 % 인지 계산
     * rank: 1부터 시작, totalUsers: 전체 유저 수
     */
    private String calculateTopPercentage(Long rank, long totalUsers) {
        if (totalUsers == 0 || rank == null) {
            return "0%";
        }

        double percentage = (rank.doubleValue() / totalUsers) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        String result = df.format(percentage) + "%";

        return result;
    }

    /**
     * 유저의 대표 뱃지 이름 조회
     * - 대표 뱃지가 없으면 null 리턴
     */
    private String getRepresentativeBadgeName(Users user) {
        String name = user.getRepresentativeBadgeName();

        if (name == null || name.isBlank()) {
            return null;
        }

        return name;

    }
}
