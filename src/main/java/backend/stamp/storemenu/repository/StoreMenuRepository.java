package backend.stamp.storemenu.repository;

import backend.stamp.store.entity.Store;
import backend.stamp.storemenu.entity.StoreMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreMenuRepository extends JpaRepository<StoreMenu, Long> {

    List<StoreMenu> findByStore(Store store);
}
