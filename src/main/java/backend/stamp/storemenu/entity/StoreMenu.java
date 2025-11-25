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

    private String menuImageUrl;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    public static StoreMenu create(Store store,
                                   String name,
                                   int price,
                                   String content,
                                   String menuImageUrl) {

        StoreMenu menu = new StoreMenu(); // 같은 클래스 안이라 protected 생성자 사용 가능
        menu.store = store;
        menu.name = name;
        menu.price = price;
        menu.content = content;
        menu.menuImageUrl = menuImageUrl;
        return menu;
    }

}
