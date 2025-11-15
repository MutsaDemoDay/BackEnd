package backend.stamp.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_tokens")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetToken {

    @Id
    @Column(name = "reset_token", nullable = false, length = 255)
    private String token;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}
