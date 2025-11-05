package backend.stamp.store.repository;

import backend.stamp.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByEventApplyIsNotNull();
    List<Store> findTop10ByOrderByJoinDateDesc();
}
