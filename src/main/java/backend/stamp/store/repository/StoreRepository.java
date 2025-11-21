package backend.stamp.store.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.manager.entity.Manager;
import backend.stamp.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByNameContaining(String keyword);

    List<Store> findByEventApplyIsNotNull();
    List<Store> findTop10ByOrderByJoinDateDesc();
    Optional<Store> findByName(String name);
    Optional<Store> findById(Long id);
    boolean existsByVerificationCode(String code);
    //실제 매장 정보 조회
    List<Store> findByIdIn(List<Long> ids);
    Optional<Store> findByManager_Account(Account account);
    Optional<Store> findByManager(Manager manager);

}
