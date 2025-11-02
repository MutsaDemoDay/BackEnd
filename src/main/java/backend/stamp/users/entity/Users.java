package backend.stamp.users.entity;

import backend.stamp.coupon.entity.Coupon;
import backend.stamp.favstore.entity.FavStore;
import backend.stamp.level.entity.Level;
import backend.stamp.order.entity.Order;
import backend.stamp.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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



    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;


    @Column(name = "nickname", nullable = false, unique = true, length = 255)
    private String nickname;


    @Column(name = "password", nullable = false, unique = true, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;


    @Column(nullable = false)
    @Builder.Default
    private Integer stampSum = 0;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Coupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<FavStore> favStores = new ArrayList<>();

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Level level;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();


    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();


    public Users(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }


}