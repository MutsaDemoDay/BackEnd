package backend.stamp.reward.repository;

import backend.stamp.users.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RankingRepository extends JpaRepository<Users, Long> {

    // 1. 스탬프 많은 순으로 Top N 유저 조회
    List<Users> findByOrderByTotalStampSumDesc(Pageable pageable);

    // 2. 특정 유저의 순위 (자기보다 스탬프 더 많은 사람 수 + 1)
    @Query("SELECT COUNT(u) + 1 FROM Users u " +
            "WHERE u.totalStampSum > (" +
            "   SELECT target.totalStampSum FROM Users target WHERE target.userId = :userId" +
            ")")
    Long getRankByTotalStampSum(@Param("userId") Long userId);

    // 3. 전체 유저 수
    long count();
}
