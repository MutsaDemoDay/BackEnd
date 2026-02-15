package backend.stamp.stamp.entity;

import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stamp_histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StampHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Column(nullable = false)
    private int amount; // 한 번에 적립된 스탬프 개수 (예: 1개, 3개 등)

    @Column(nullable = false)
    private LocalDateTime createdAt; // 실제 적립된 시간
}