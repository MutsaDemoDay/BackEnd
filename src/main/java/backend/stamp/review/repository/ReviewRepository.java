package backend.stamp.review.repository;

import backend.stamp.review.entity.Review;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStoreId(Long storeId);
    boolean existsByUsersAndStore(Users users, Store store);
}
