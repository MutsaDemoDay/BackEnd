package backend.stamp.order.entity;

import backend.stamp.stamp.entity.Stamp;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id", nullable = false)
    private Long id;

    //총 주문금액
    @Column(length = 1000, nullable = false)
    private Integer totalPrice;

    @Column(length=1000,nullable = false)
    private Long menuCount;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    //stamp
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Stamp stamp;

    //user

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Users users;

    //stamp
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    //사용여부
    @Column(nullable = false)
    private boolean used = false;

}
