package backend.stamp.users.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByAccount(Account account);

    @Query("SELECT u.userId FROM Users u WHERE u.account.email = :email")
    Optional<Long> findUserIdByEmail(@Param("email") String email);
    @Query("SELECT u.userId FROM Users u WHERE u.account.loginId = :loginId")
    Optional<Long> findUserIdByLoginId(@Param("email") String email);

    Optional<Users>findByAccount_AccountId(Long accountId);

    @Query("SELECT u FROM Users u " +
            "LEFT JOIN FETCH u.account " +
            "LEFT JOIN FETCH u.level " +
            "WHERE u.userId = :userId")
    Optional<Users> findUserWithAccountAndLevel(@Param("userId") Long userId);
    @Query("SELECT u.userId, u.gender FROM Users u WHERE u.userId IN :userIds")
    List<Object[]> findGenderByUserIds(List<Long> userIds);


}
