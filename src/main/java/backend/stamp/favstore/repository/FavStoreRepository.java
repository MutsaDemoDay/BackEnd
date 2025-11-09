package backend.stamp.favstore.repository;

import backend.stamp.favstore.entity.FavStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavStoreRepository extends JpaRepository<FavStore, Long> {
}
