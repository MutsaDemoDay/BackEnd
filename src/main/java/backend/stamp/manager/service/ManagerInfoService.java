package backend.stamp.manager.service;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import backend.stamp.manager.dto.ManagerAccountInfoResponse;
import backend.stamp.manager.entity.Manager;
import backend.stamp.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerInfoService {

    private final ManagerRepository managerRepository;

    public ManagerAccountInfoResponse getMyAccountInfo() {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        Manager manager = managerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        ManagerAccountInfoResponse response = new ManagerAccountInfoResponse();
        response.setEmail(currentAccount.getEmail());
        response.setLoginId(currentAccount.getLoginId());
        response.setJoinedAt(currentAccount.getCreatedAt());
        return response;
    }
}
