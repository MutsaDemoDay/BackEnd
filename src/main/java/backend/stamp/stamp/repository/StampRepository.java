package backend.stamp.stamp.repository;

import backend.stamp.order.entity.Order;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    // 유저의 스탬프판 중복 조회
    boolean existsByUsersAndStore(Users users, Store store);

    //유저의 스탬프판 조회
    Optional<Stamp> findByUsersAndStore(Users users, Store store);

    //주문으로 스탬프 적립 여부 확인용
    boolean existsByOrder(Order order);

    //유저로 조회
    List<Stamp> findByUsers(Users users);
}
