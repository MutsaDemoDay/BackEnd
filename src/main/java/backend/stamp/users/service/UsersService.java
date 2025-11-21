package backend.stamp.users.service;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.dto.UserLocalStoreResponseDto;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsersService {

    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;
    // 내 동네 매장 리스트 조회
    public List<UserLocalStoreResponseDto> getMyLocalStores(Account account) {

        if(account == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Users user = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        // 유저 주소에서 구 단위 추출
        String address = user.getAddress();
        String gu = extractGu(address);

        if (gu == null) {
            throw new IllegalArgumentException("주소가 잘못 입력되었습니다.");
        }


        //  매장 조회
        List<Store> stores = storeRepository.findByAddressContaining(gu);

        //없으면 ..
        if(stores.isEmpty()) {
            return List.of();
        }

        //  DTO 변환
        return stores.stream()
                .map(UserLocalStoreResponseDto::from)
                .toList();
    }

    private String extractGu(String address) {
        String[] parts = address.split(" ");
        for (String part : parts) {
            if (part.endsWith("구")) {
                return part;
            }
        }
        return null;
    }
}
