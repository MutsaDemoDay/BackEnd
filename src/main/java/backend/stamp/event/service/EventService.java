package backend.stamp.event.service;


import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventApplyResponseDto;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.dto.EventCategoryResponseDto;
import backend.stamp.event.dto.EventMenuRequestDto;
import backend.stamp.event.entity.Event;
import backend.stamp.event.entity.EventType;
import backend.stamp.event.repository.EventRepository;
import backend.stamp.eventstore.entity.EventStore;
import backend.stamp.eventstore.repository.EventStoreRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final StoreRepository storeRepository;
    private final EventStoreRepository eventStoreRepository;

    // 이벤트 신청가능한 카테고리 조회
    @Transactional
    public EventCategoryListResponseDto getAvailableCategories(Account account)
    {

        //점주의 계정 조회 ( npe 방지 )
        if(account == null)
        {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //점주가 가진 매장 조회
        Store store = storeRepository.findByManager_Account(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        //매장 등록 날짜 null 방지
        if (store.getCreatedDate() == null) {
            throw new ApplicationException(ErrorCode.STORE_CREATED_DATE_NOT_FOUND);
        }

        //오늘 날짜 조회
        LocalDate today = LocalDate.now();


        //신규 매장 여부 체크
        boolean isNewStore= store.getCreatedDate().getYear() == today.getYear()
                && store.getCreatedDate().getMonth() ==today.getMonth();

        //현재 달의 시작 날 / 끝나는 날 체크
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);


        //DB에 3개 있는거
        List<Event> events = eventRepository.findAll();

        //등록된 DB 없는 경우
        if (events.isEmpty()) {
            throw new ApplicationException(ErrorCode.EVENT_NOT_FOUND);
        }

        //있는 이벤트 리스트 불러오기
        List<EventCategoryResponseDto> dtoList = new ArrayList<>();


        for (Event event : events) {

            boolean available;

            if (event.getEventType() == EventType.OPEN_EVENT) {
                available = isNewStore; // 신규 매장만
            } else {
                available = true; // 상시 가능
            }

            dtoList.add(EventCategoryResponseDto.builder()
                    .eventType(event.getEventType())
                    .description(event.getDescription())
                    .available(available)
                    .startDate(startOfMonth)
                    .endDate(endOfMonth)
                    .build());
        }

        return EventCategoryListResponseDto.builder()
                .availableCategories(dtoList)
                .build();
    }


    //이벤트 신청
    @Transactional
    public EventApplyResponseDto applyEvent(Account account, EventType eventType, EventMenuRequestDto request)
    {
        if (account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        //점주가 가진 매장 조회
        Store store = storeRepository.findByManager_Account(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));


        // 이벤트 엔티티 조회
        Event event = eventRepository.findByEventType(eventType)
                .orElseThrow(() -> new ApplicationException(ErrorCode.EVENT_NOT_FOUND));


        //오늘 날짜 조회
        LocalDate today = LocalDate.now();

        //이미 해당 달에 해당 이벤트를 신청했으면 예외 처리
        List<EventStore> existingEvent = eventStoreRepository
                .findThisMonthAppliedEvent(store, event, today);
        if (!existingEvent.isEmpty()) {
            throw new ApplicationException(ErrorCode.EVENT_ALREADY_APPLIED);
        }

        //이벤트 대표메뉴 설정( 메뉴 3개 검증 )
        if(request.getMenuNames()==null || request.getMenuNames().size()!=3){
            throw new ApplicationException(ErrorCode.INVALID_EVENT_MENUS);
        }

        //이달의 끝나는 날짜 조회

        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());



        // EventStore 생성
        EventStore eventStore = EventStore.builder()
                .store(store)
                .event(event)
                .startDate(LocalDate.now())
                .endDate(endDate)     //항상 매달의 마지막날
                .active(true)
                .menu1(request.getMenuNames().get(0))
                .menu2(request.getMenuNames().get(1))
                .menu3(request.getMenuNames().get(2))
                .build();

        eventStoreRepository.save(eventStore);

        return EventApplyResponseDto.builder()
                .eventStoreId(eventStore.getId())
                .eventType(eventType)
                .title(event.getTitle())
                .message(eventType + " 이벤트가 성공적으로 신청되었습니다.")
                .build();
    }


    }

