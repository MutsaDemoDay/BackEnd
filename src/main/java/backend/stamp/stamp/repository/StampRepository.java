package backend.stamp.stamp.repository;

import backend.stamp.order.entity.Order;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    // 유저의 스탬프판 중복 조회
    boolean existsByUsersAndStore(Users users, Store store);

    //유저의 스탬프판 조회
    Optional<Stamp> findByUsersAndStore(Users users, Store store);

    Optional<Stamp> findByStoreAndUsers(Store store, Users users);
    //주문으로 스탬프 적립 여부 확인용
    boolean existsByOrder(Order order);

    //유저로 조회
    List<Stamp> findByUsers(Users users);
    @Query("SELECT s.users.userId FROM Stamp s WHERE s.store.name = :storeName")
    List<Long> findUserIdsByStoreName(@Param("storeName") String storeName);
    List<Stamp> findByStoreIdAndDateBetween(Long storeId, LocalDateTime start, LocalDateTime end);
    @Query(value = """
        SELECT DATE(s.created_date) AS d,
               COUNT(DISTINCT s.user_id) AS cnt
        FROM stamps s
        WHERE s.store_id = :storeId
          AND s.created_date BETWEEN :start AND :end
        GROUP BY DATE(s.created_date)
        ORDER BY d
    """, nativeQuery = true)
    List<Object[]> countDailyUniqueUsers(Long storeId,
                                         LocalDateTime start,
                                         LocalDateTime end);
}
