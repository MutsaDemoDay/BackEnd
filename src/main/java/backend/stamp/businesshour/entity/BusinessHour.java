package backend.stamp.businesshour.entity;

import backend.stamp.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "business_hours")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BusinessHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="business_hours_id")
    private Long id;

    @Column(nullable = false, length = 3)
    private String day;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Column(nullable = false)
    private boolean isHoliday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public void setStore(Store store) {
        this.store = store;
    }


}
