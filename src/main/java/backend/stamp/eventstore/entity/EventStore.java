package backend.stamp.eventstore.entity;


import backend.stamp.event.entity.Event;
import backend.stamp.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name="event_stores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

//어떤 가게가 어떤 이벤트에 참여 신청했는지 기록됨
public class EventStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_store_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    @Column(nullable = false)
    private LocalDateTime appliedAt; // 참여 신청 날짜

    @Column(nullable = false)
    private LocalDate startDate;//이벤트 시작 날짜

    @Column(nullable = false)
    private LocalDate endDate;//이벤트 끝난 날짜


    @Column(nullable = false)
    private boolean active; // 참여중인지 여부

    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
    }

}
