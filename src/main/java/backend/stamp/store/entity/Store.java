package backend.stamp.store.entity;

import backend.stamp.favstore.entity.FavStore;
import backend.stamp.order.entity.Order;
import backend.stamp.review.entity.Review;
import backend.stamp.storemenu.entity.StoreMenu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="stores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="store_id")
    private Long id;


    @Column(name = "store_name",nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String phone;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Column(length = 1000)
    private String storeUrl;

    @Column(length = 500)
    private String sns;

    @Column(length = 1000)
    private String stampImageUrl;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<FavStore> favStores = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreMenu> storeMenus = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    //ai 호출용
    private LocalDate joinDate; // 가게가 서비스에 가입한 날짜
    private Integer eventApply; // 이벤트: e.g. 경험치 2배 부여
    private Double longtitude; // 위경도: 가게 위경도
    private Double latitude;

}
