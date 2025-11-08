package backend.stamp.coupon.repository;

import backend.stamp.coupon.entity.Coupon;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
      List<Coupon> findByUsers_UserId(Long userId);
      List<Coupon> findByUsers(Users users);
}
