package backend.stamp.stamp.entity;


import backend.stamp.order.entity.Order;
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
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id",nullable = false)
    private Order order;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "created_date")
    private LocalDateTime date;

    @Column(name = "max_count", nullable = false)
    private Integer maxCount;

    @Column(name = "reward",nullable = false)
    private String reward;



}
