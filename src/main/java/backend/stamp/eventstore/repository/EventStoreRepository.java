package backend.stamp.eventstore.repository;

import backend.stamp.event.entity.EventType;
import backend.stamp.eventstore.entity.EventStore;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import backend.stamp.store.entity.Store;
import backend.stamp.event.entity.Event;


import java.time.LocalDate;
import java.util.List;

public interface EventStoreRepository extends JpaRepository<EventStore, Long> {

    //특정 스토어가 특정 달에 이미 이 이벤트를 신청했는지 체크
    @Query("SELECT es FROM EventStore es " +
            "WHERE es.store = :store " +
            "AND es.event = :event " +
            "AND MONTH(es.startDate) = MONTH(:today) " +

            "AND YEAR(es.startDate) = YEAR(:today)")
    List<EventStore> findThisMonthAppliedEvent(
            @Param("store") Store store,
            @Param("event") Event event,
            @Param("today") LocalDate today
    );

    //현재 진행중인 애들 갖고와 !!
    List<EventStore> findByEvent_EventTypeAndActive(EventType type, boolean active);

    List<EventStore> findByActiveIsTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate start, LocalDate end);

}
