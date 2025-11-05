package backend.stamp.store.entity;

import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.manager.entity.Manager;
import backend.stamp.order.entity.Order;
import backend.stamp.review.entity.Review;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.storemenu.entity.StoreMenu;
import backend.stamp.users.entity.Gender;
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

    private Double latitude;
    private Double longitude;

    private String phone;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusinessHour> businessHours = new ArrayList<>();

    @Column(length = 1000)
    private String storeUrl;

    @Column(length = 500)
    private String sns;


    @Column(length = 1000)
    private String storeImageUrl;

    @Column(length = 1000)
    private String stampImageUrl;

    //적립 금액 기준

    @Column(name = "required_amount",nullable = false)
    private Integer requiredAmount;

    @Column(name = "reward",nullable = false)
    private String reward;

    //스탬프 템플릿 내에 보여지는 리워드
    @Column(name = "stamp_reward",nullable = false)
    private String stampReward;

    //스탬프 maxCount 가게별로 정하니까
    @Column(name = "max_count", nullable = false)
    private Integer maxCount;


    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<FavStore> favStores = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreMenu> storeMenus = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Stamp> stamps = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Coupon> coupons = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;
    //ai 호출용
    private LocalDate joinDate; // 가게가 서비스에 가입한 날짜
    private Integer eventApply; // 이벤트: e.g. 경험치 2배 부여

}
