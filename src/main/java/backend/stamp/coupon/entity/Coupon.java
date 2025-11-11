package backend.stamp.coupon.entity;

import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="coupons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="coupon_id")
    private Long id;

    @Column(length=500)
    private String name;


    private LocalDateTime expiredDate;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users users;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    //사용완료 처리
    @Builder.Default
    private boolean used = false;
}
