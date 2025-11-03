package backend.stamp.badge.entity;


import backend.stamp.level.entity.Level;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="badges")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="level_id",nullable = false)
    private Level level;

}
