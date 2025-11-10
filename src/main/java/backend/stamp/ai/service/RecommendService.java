package backend.stamp.ai.service;

import backend.stamp.ai.ai.AiRequest;
import backend.stamp.ai.ai.store.EventStore;
import backend.stamp.ai.ai.store.NewStore;
import backend.stamp.ai.ai.store.PopularStore;
import backend.stamp.order.repository.OrderRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    public AiRequest buildFullAiRequest(AiRequest request) {
        List<EventStore> eventStores = storeRepository.findByEventApplyIsNotNull()
                .stream()
                .map(EventStore::fromEntity)
                .toList();
        List<NewStore> newStores = storeRepository.findTop10ByOrderByJoinDateDesc()
                .stream()
                .map(NewStore::fromEntity)
                .toList();

        List<Store> topStores = orderRepository.findTop10StoresByOrderCount(PageRequest.of(0, 10));

        List<PopularStore> popularStores = topStores.stream()
                .map(store -> PopularStore.fromEntity(
                        store,
                        orderRepository.countByStoreId(store.getId()) // 주문 수 조회
                ))
                .toList();
        return new AiRequest(
                request.getUserId(),
                request.getLocation(),
                eventStores,
                newStores,
                popularStores
        );
    }


}
