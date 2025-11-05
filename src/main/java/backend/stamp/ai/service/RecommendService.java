package backend.stamp.ai.service;

import backend.stamp.ai.ai.AiRequest;
import backend.stamp.ai.ai.store.EventStore;
import backend.stamp.ai.ai.store.NewStore;
import backend.stamp.ai.ai.store.PopularStore;
import backend.stamp.order.OrderRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
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
