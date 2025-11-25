package backend.stamp.event.service;


import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventListResponseDto;
import backend.stamp.event.entity.Event;
import backend.stamp.event.repository.EventRepository;
import backend.stamp.eventstore.entity.EventStore;
import backend.stamp.eventstore.repository.EventStoreRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventBoardService {

    private final EventRepository eventRepository;
    private final EventStoreRepository eventStoreRepository;

    //현재 진행중인 이벤트 리스트 조회
    public List<EventListResponseDto> getOngoingEventLists(Account account)
    {
        //유저 아이디 조회
        if(account == null)
        {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //오늘 날짜 조회
        LocalDate today = LocalDate.now();

        //현재 진행중인 이벤트 있는지 조회
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(event -> {

                    // 2) 이 이벤트에 참여중인 모든 EventStore 조회
                    List<EventStore> eventStores = eventStoreRepository.findByEventAndActiveIsTrue(event);

                    // 3) 기간 내에 있는 참여 매장만 필터링
                    List<Long> participatingStoreIds = eventStores.stream()
                            .filter(es -> !es.getStartDate().isAfter(today) &&
                                    !es.getEndDate().isBefore(today))
                            .map(es -> es.getStore().getId())
                            .toList();

                    return EventListResponseDto.builder()
                            .eventType(event.getEventType())
                            .buttonDescription(event.getButtonDescription())
                            .buttonImageUrl(event.getButtonImageUrl())
                            .participatingStores(participatingStoreIds)
                            .build();
                })
                // 4) 참여 매장이 0개(eventStores 비었으면) 해당 이벤트 숨김
                .filter(dto -> !dto.getParticipatingStores().isEmpty())
                .toList();    }

}
