package backend.stamp.event.entity;


import backend.stamp.eventstore.entity.EventStore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String title;

    //이벤트 선택 버튼에 들어가는 설명
    @Column(length=1000, nullable = false)
    private String description;

    //유저 페이지에서 이벤트 버튼에서 보여지는 설명
    @Column(length=1000, nullable = false)
    private String buttonDescription;

    //이벤트 페이지 안에 들어갈 설명
    @Column(length=1000, nullable = false)
    private String inPageDescription;


    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventStore> EventStores = new ArrayList<>();

    public void addStore(EventStore eventStore) {
        EventStores.add(eventStore);
    }

}
