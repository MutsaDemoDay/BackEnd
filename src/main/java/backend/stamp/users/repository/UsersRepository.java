package backend.stamp.users.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByAccount(Account account);
    Optional<Users>findByAccount_AccountId(Long accountId);

}
