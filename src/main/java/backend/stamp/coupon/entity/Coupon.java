package backend.stamp.coupon.entity;

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
    @Column(name="coupon_id",nullable = false)
    private Long id;

    @Column(length=500,nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime expiredDate;

    @Column(nullable = false)
    private String qrCodeUrl;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private Users user;

}
