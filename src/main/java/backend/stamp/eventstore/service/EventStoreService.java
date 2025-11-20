package backend.stamp.eventstore.service;

import backend.stamp.account.entity.Account;
import backend.stamp.event.dto.EventCategoryListResponseDto;
import backend.stamp.event.entity.Event;
import backend.stamp.eventstore.dto.EndedEventListResponseDto;
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

}
