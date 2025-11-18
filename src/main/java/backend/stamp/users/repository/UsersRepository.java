package backend.stamp.users.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByAccount(Account account);
    Optional<Users>findByAccount_AccountId(Long accountId);

//    @Query("SELECT u.nickname FROM User u WHERE u.id IN :userIds")
//    List<String> findNicknamesByUserIds(@Param("userIds") List<Long> userIds);

}
