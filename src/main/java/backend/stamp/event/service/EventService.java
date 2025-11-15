package backend.stamp.event.service;


import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.dto.EventCategoryResponseDto;
import backend.stamp.event.entity.Event;
import backend.stamp.event.entity.EventType;
import backend.stamp.event.repository.EventRepository;
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

    // 이벤트 신청가능한 카테고리 조회
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

    }

