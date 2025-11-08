package backend.stamp.favstore.entity;


import backend.stamp.store.entity.Store;
import backend.stamp.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name="fav_stores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="fav_store_id",nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
