package backend.stamp.level.entity;


import backend.stamp.badge.entity.Badge;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="levels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Level {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String benefit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users users;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Badge> badges = new ArrayList<>();

}
