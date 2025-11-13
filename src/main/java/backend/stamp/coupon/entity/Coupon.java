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
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="coupon_id",nullable = false)
    private Long id;

    @Column(length=500,nullable = false)
    private String name;

    @Column(nullable = false)

    private LocalDateTime expiredDate;




    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private Users users;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id",nullable = false)
    private Store store;

    private LocalDateTime usedDate;

    //사용완료 처리
    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;
}
