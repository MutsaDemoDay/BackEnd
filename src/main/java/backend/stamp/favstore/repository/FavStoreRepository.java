package backend.stamp.favstore.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavStoreRepository extends JpaRepository<FavStore, Long> {

    boolean existsByUsersAndStore(Users users, Store store);


    Optional<FavStore> findByUsersAndStore(Users users, Store store);

    List<FavStore> findByUsers_UserId(Long userId);

    List<Long> findFavStoreIdsByUsers_UserId(Long userId);


    List<FavStore> findByUsers(Users users);
    List<Long> findFavStoreIdsByUsers_Account(Account account);
}
