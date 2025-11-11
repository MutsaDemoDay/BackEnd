package backend.stamp.store.repository;

import backend.stamp.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByNameContaining(String keyword);

    List<Store> findByEventApplyIsNotNull();
    List<Store> findTop10ByOrderByJoinDateDesc();
    Optional<Store> findById(Long storeId);
}
