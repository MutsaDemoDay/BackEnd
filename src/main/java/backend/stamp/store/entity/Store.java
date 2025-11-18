package backend.stamp.store.entity;

import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.eventstore.entity.EventStore;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.global.entity.BaseEntity;
import backend.stamp.manager.entity.Manager;
import backend.stamp.order.entity.Order;
import backend.stamp.review.entity.Review;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.storemenu.entity.StoreMenu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="stores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="store_id")
    private Long id;


    @Column(name = "store_name")
    private String name;

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

    @Column(name = "required_amount")
    private Integer requiredAmount;

    @Column(name = "reward")
    private String reward;

    //스탬프 템플릿 내에 보여지는 리워드
    @Column(name = "stamp_reward")
    private String stampReward;

    //스탬프 maxCount 가게별로 정하니까
    @Column(name = "max_count")
    private Integer maxCount;

    @Column(name = "verification_code", unique = true, length = 4)
    private String verificationCode;

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
    @JoinColumn(name = "manager_id")
    private Manager manager;


    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<EventStore> EventStores = new ArrayList<>();


    //ai 호출용
    private LocalDate joinDate; // 가게가 서비스에 가입한 날짜
    private Integer eventApply; // 이벤트: e.g. 경험치 2배 부여

}
