package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.FindManagerIdRequest;
import backend.stamp.auth.dto.response.FindIdResponse;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.entity.Manager;
import backend.stamp.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerFindService {
    private final AccountRepository accountRepository;
    private final ManagerRepository managerRepository;

    public FindIdResponse findManagerId(FindManagerIdRequest request) {

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Manager manager = managerRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!manager.getBusinessNum().equals(request.getBusinessNum())) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }

        return FindIdResponse.builder()
                .loginId(account.getLoginId())
                .createdAt(account.getCreatedAt().toLocalDate())
                .build();
    }
}
