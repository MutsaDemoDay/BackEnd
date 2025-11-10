package backend.stamp.order.repository;

import backend.stamp.ai.ai.subdtos.VisitStatics;
import backend.stamp.order.entity.Order;
import backend.stamp.store.entity.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o.store FROM Order o GROUP BY o.store ORDER BY COUNT(o.id) DESC")
    List<Store> findTop10StoresByOrderCount(Pageable pageable);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId")
    int countByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT new backend.stamp.ai.ai.subdtos.VisitStatics(" +
            "o.users.id, o.store.id, COUNT(o)) " +
            "FROM Order o " +
            "WHERE o.users.id = :userId " +
            "GROUP BY o.users.id, o.store.id")
    List<VisitStatics> findVisitStaticsByUserId(@Param("userId") Long userId);
}
