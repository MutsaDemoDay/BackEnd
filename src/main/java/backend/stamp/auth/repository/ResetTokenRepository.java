package backend.stamp.auth.repository;

import backend.stamp.auth.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
    Optional<ResetToken> findByToken(String token);

    @Transactional
    void deleteByExpiryDateBefore(LocalDateTime now);
}
