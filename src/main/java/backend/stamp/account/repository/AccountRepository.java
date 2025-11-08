package backend.stamp.account.repository;

import backend.stamp.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByLoginId(String loginId);
    Optional<Account> findByEmail(String email);
}
