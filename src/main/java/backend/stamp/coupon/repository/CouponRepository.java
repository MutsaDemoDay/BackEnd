package backend.stamp.coupon.repository;

import backend.stamp.coupon.entity.Coupon;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
      List<Coupon> findByUsers_UserId(Long userId);
      List<Coupon> findByUsers(Users users);
      List<Coupon> findByUsers_Account_AccountId(Long accountId);
      List<Coupon> findByUsers_Account_AccountIdAndUsedFalse(Long userId);
      List<Coupon> findByUsersAndUsedFalse(Users users);

      boolean existsByUsersAndStore(Users users, Store store);

      long countByStoreIdAndUsedAndUsedDateBetween(
              Long storeId,
              boolean used,
              LocalDateTime start,
              LocalDateTime end
      );

}
