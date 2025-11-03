package backend.stamp.storemenu.entity;

import backend.stamp.order.entity.Order;
import backend.stamp.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_menus")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class StoreMenu {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="store_menu_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Lob
    private String content;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

}
