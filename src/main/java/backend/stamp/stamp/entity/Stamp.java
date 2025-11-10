package backend.stamp.stamp.entity;


import backend.stamp.order.entity.Order;
import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "stamps")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="stamp_id", nullable = false)
    private Long id;

    //주문
    //nullable =true  -> 빈 스탬프판일때 예외 방지
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id",nullable = true)
    private Order order;
    //유저
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private Users users;

    //가게
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id",nullable = false)
    private Store store;



    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "created_date")
    private LocalDateTime date;

    //현재 스탬프 개수
    @Column(name = "current_count", nullable = false)
    private Integer currentCount = 0;//초깃값 초기화

}
