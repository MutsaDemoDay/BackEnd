package backend.stamp.users.controller;

import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.users.dto.UserAccountInfoResponse;
import backend.stamp.users.dto.UserProfileResponse;
import backend.stamp.users.dto.UserSettingsDto;
import backend.stamp.users.service.MypageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
@Tag(name = "Mypage", description = "유저의 프로필, 계정 정보 조회 및 설정 관련 API")
public class MypageController {

    private final MypageService mypageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "유저 프로필 조회", description = "유저의 프로필을 조회합니다.")
    @GetMapping("/profile")
    public ApplicationResponse<UserProfileResponse> getUserProfile() {

        UserProfileResponse response = mypageService.getUserProfile();

        return ApplicationResponse.<UserProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("유저 프로필 조회가 완료되었습니다.")
                .data(response)
                .build();
    }

    @Operation(summary = "유저 계정 정보 조회", description = "유저의 계정 정보를 조회합니다.")
    @GetMapping("/account")
    public ApplicationResponse<UserAccountInfoResponse> getUserAccountInfo() {

        UserAccountInfoResponse response = mypageService.getUserAccountInfo();

        return ApplicationResponse.<UserAccountInfoResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("유저 계정 정보 조회가 완료되었습니다.")
                .data(response)
                .build();
    }

    @Operation(summary = "유저 설정 조회", description = "유저의 설정 정보를 조회합니다.")
    @GetMapping("/settings")
    public ApplicationResponse<UserSettingsDto> getUserSettings() {

        UserSettingsDto response = mypageService.getUserSettings();

        return ApplicationResponse.<UserSettingsDto>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("유저 설정 조회가 완료되었습니다.")
                .data(response)
                .build();
    }

    @Operation(summary = "유저 설정 수정", description = "유저의 설정 정보를 수정합니다.")
    @PatchMapping(value="/settings",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<UserSettingsDto> updateUserSettings(
            @RequestPart("data") String data,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws JsonProcessingException {

        UserSettingsDto requestDto =
                objectMapper.readValue(data, UserSettingsDto.class);

        UserSettingsDto response =
                mypageService.updateUserSettings(requestDto, profileImage);

        return ApplicationResponse.<UserSettingsDto>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("유저 설정 수정이 완료되었습니다.")
                .data(response)
                .build();
    }

}
