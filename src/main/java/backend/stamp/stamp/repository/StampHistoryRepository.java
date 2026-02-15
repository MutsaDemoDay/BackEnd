package backend.stamp.stamp.repository;

import backend.stamp.stamp.entity.StampHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StampHistoryRepository extends JpaRepository<StampHistory, Long> {
    List<StampHistory> findByStoreIdAndCreatedAtBetween(Long storeId, LocalDateTime start, LocalDateTime end);
}