package backend.stamp.store.service;


import backend.stamp.store.dto.StoreSearchResponseDto;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    //DB 내에서 매장 검색
    public List<StoreSearchResponseDto> getSearchedStores(String storeName) {

        //이름으로 키워드 검색 진행

        List<Store> stores = storeRepository.findByNameContaining(storeName);

        return stores.stream().map(StoreSearchResponseDto::from)
                .collect(Collectors.toList());
    }


    //지도에서 매장 검색 ( 현재 위치 기반 검색 )-> 프론트가 해준다고 함


}
