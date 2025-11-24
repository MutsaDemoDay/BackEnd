package backend.stamp.account.entity;

import backend.stamp.manager.entity.Manager;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "login_id", unique = true, length = 255)
    private String loginId;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "user_type", length = 20)
    @Enumerated(EnumType.STRING) // USER, MANAGER 타입을 저장
    private UserType userType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean userOnboarded = false;

    private boolean managerOnboarded = false;

    public void completeUserOnboarding() {
        this.userOnboarded = true;
    }

    public void completeManagerOnboarding() {
        this.managerOnboarded = true;
    }



}
