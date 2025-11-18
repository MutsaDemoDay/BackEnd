package backend.stamp.eventstore.repository;

import backend.stamp.eventstore.entity.EventStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStoreRepository extends JpaRepository<EventStore, Long> {
}
