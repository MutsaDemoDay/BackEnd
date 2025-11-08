package backend.stamp.store.controller;


import backend.stamp.store.dto.StoreSearchResponseDto;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {
    private final StoreService storeService;

    //매장 검색 ( 부분 검색 )
    @GetMapping("/search")
    public List<StoreSearchResponseDto> searchStores(@RequestParam String storeName) {
        return storeService.getSearchedStores(storeName);
    }


}

