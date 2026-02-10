package backend.stamp.manager.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByAccount(Account account);
}