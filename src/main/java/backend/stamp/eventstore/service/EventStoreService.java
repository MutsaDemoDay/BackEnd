package backend.stamp.eventstore.service;

import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.entity.Event;
import backend.stamp.event.entity.EventType;
import backend.stamp.event.repository.EventRepository;
import backend.stamp.eventstore.dto.EndedEventListResponseDto;
import backend.stamp.eventstore.dto.JoinStoreResponseDto;
import backend.stamp.eventstore.dto.OngoingEventResponseDto;
import backend.stamp.eventstore.entity.EventStore;
import backend.stamp.eventstore.repository.EventStoreRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class EventStoreService {

    private final EventStoreRepository eventStoreRepository;
    private final EventRepository eventRepository;

    //지난 이벤트 조회
    public List<EndedEventListResponseDto> getEndedEventList(Account account) {

        //점주 아이디 조회
        if(account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //오늘날짜 조회
        LocalDate today = LocalDate.now();


        //DB 리스트 조회
        List<EventStore> eventStores = eventStoreRepository.findAll();


        //endDate <today인 경우만 조회해옴 !
        List<EventStore> endedEvents = eventStores.stream()
                .filter(es -> es.getEndDate() != null)
                .filter(es -> es.getEndDate().isBefore(today))
                .toList();



        return endedEvents.stream()
                .map(EndedEventListResponseDto::from)
                .toList();
    }


    //현재 진행중인 이벤트 글 개별조회 ( eventType으로 분류하기 )
    public OngoingEventResponseDto getOngoingEvents(Account account, EventType eventType)
    {
        //점주 / 유저 동시 조회
        if(account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //현재 진행중인 이벤트 리스트 조회
        List <EventStore> stores = eventStoreRepository
                .findByEvent_EventTypeAndActive(eventType, true);

        List<JoinStoreResponseDto> joinStores = stores.stream()
                .map(es -> JoinStoreResponseDto.builder()
                        .StoreId(es.getStore().getId())
                        .storeName(es.getStore().getName())
                        .storeAddress(es.getStore().getAddress())
                        .menuNames(List.of(es.getMenu1(), es.getMenu2(), es.getMenu3()))
                        .build())
                .toList();

        //이벤트 엔티티 자체 조회
        Event event = eventRepository.findByEventType(eventType)
                .orElseThrow(() -> new ApplicationException(ErrorCode.EVENT_NOT_FOUND));

        //이벤트 시작/ 끝 기간
        LocalDate start = stores.get(0).getStartDate();
        LocalDate end = stores.get(0).getEndDate();

        return OngoingEventResponseDto.builder()
                .eventId(event.getId())
                .eventType(eventType)
                .buttonDescription(event.getButtonDescription())
                .inPageDescription(event.getInPageDescription())
                .startDate(start)
                .endDate(end)
                .JoinStoreLists(joinStores)
                .build();

    }


}
