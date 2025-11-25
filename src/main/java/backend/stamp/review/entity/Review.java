package backend.stamp.review.entity;

import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name="reviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="review_id")
    private Long id;


    //별점
    @Column(nullable = false)
    private byte rate;

    //리뷰 생성 날짜
    @CreatedDate
    private LocalDateTime createdAt;

    //리뷰 내용
    @Lob
    @Column(nullable = false)
    private String content;


    //가게별로 후기 작성
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    //유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Column
    private String reviewImageUrl;

}
