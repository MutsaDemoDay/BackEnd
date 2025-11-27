package backend.stamp.users.entity;
import backend.stamp.account.entity.Account;
import backend.stamp.stamp.entity.Stamp;
import jakarta.persistence.*;
import backend.stamp.coupon.entity.Coupon;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.level.entity.Level;
import backend.stamp.order.entity.Order;
import backend.stamp.review.entity.Review;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id",nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", unique = true) // accountId 대신 명확히 account_fk로 명시
    private Account account;

    @Column(name = "nickname", length = 255)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    //유저의 stamp 개수
    @Builder.Default
    private Integer stampSum = 0;


    //유저의 쿠폰 개수
    @Builder.Default
    private Integer couponNum = 0;

    //유저의 총 스탬프 개수 ( 모든 가게 스탬프 총합 )
    @Builder.Default
    private Integer totalStampSum = 0;

    private String profileImageUrl;

    @Column(name = "representative_badge_name")
    private String representativeBadgeName;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Coupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<FavStore> favStores = new ArrayList<>();

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Level level;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Stamp> stamps = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();


    public static Users createSocialUser(String nickname, Account account) {
        return Users.builder()
                .nickname(nickname)
                .account(account)
                .stampSum(0)
                .build();
    }
    public void decreaseCouponCount() {
        this.couponNum -= 1;
    }


}
