package backend.stamp.event.repository;

import backend.stamp.event.entity.Event;
import backend.stamp.event.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventType(EventType eventType);

}
