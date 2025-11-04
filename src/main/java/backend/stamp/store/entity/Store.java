package backend.stamp.store.entity;

import backend.stamp.favstore.entity.FavStore;
import backend.stamp.manager.entity.Manager;
import backend.stamp.order.entity.Order;
import backend.stamp.review.entity.Review;
import backend.stamp.storemenu.entity.StoreMenu;
import jakarta.persistence.*;
import lombok.*;

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
@Builder
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

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Manager> managers = new ArrayList<>();

}
