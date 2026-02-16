package backend.stamp.stamp.repository;

import backend.stamp.stamp.entity.StampHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StampHistoryRepository extends JpaRepository<StampHistory, Long> {
    List<StampHistory> findByStoreIdAndCreatedAtBetween(Long storeId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT CAST(h.createdAt AS date), COUNT(DISTINCT h.users.userId) " +
           "FROM StampHistory h " +
           "WHERE h.store.id = :storeId AND h.createdAt BETWEEN :start AND :end " +
           "GROUP BY CAST(h.createdAt AS date)")
    List<Object[]> countDailyUniqueUsers(@Param("storeId") Long storeId, 
                                         @Param("start") LocalDateTime start, 
                                         @Param("end") LocalDateTime end);
}

