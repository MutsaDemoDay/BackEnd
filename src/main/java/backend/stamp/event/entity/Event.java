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

    @Column(length=1000, nullable = false)
    private String description;


    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventStore> EventStores = new ArrayList<>();



    public void addStore(EventStore eventStore) {
        EventStores.add(eventStore);
    }

}
