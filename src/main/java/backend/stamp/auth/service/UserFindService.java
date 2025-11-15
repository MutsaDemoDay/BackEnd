package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.FindUserIdRequest;
import backend.stamp.auth.dto.response.FindIdResponse;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.users.repository.UsersRepository;
import backend.stamp.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFindService {
    private final AccountRepository accountRepository;
    private final UsersRepository usersRepository;

    public FindIdResponse findUserId(FindUserIdRequest request) {

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Users user = usersRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!user.getNickname().equals(request.getNickname())) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }

        return FindIdResponse.builder()
                .loginId(account.getLoginId())
                .createdAt(account.getCreatedAt().toLocalDate())
                .build();
    }
}
