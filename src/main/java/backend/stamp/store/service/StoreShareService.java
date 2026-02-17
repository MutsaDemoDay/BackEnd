package backend.stamp.store.service;

import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreShareService {

    private final StoreRepository storeRepository;

    //공유 url 생성
    public String createShareUrl(Long storeId) {

        //스토어 있는지 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        // 2. 프론트에서 접근 가능한 공유용 URL 생성
        return "https://dango.co.kr/store/info/" + storeId;

    }
}
