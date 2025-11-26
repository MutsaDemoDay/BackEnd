package backend.stamp.manager.controller;

import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.manager.dto.StoreProfileDto;
import backend.stamp.manager.service.ManagerProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/managers/profile")
@RequiredArgsConstructor
@Tag(name = "Manager (매장 프로필)", description = "점주가 매장 프로필 조회/수정하는 api")
public class ManagerProfileController {

    private final ManagerProfileService managerProfileService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "매장 프로필 조회", description = "점주의 매장 프로필 정보를 조회합니다.")
    @GetMapping
    public ApplicationResponse<StoreProfileDto> getStoreProfile() {

        StoreProfileDto response = managerProfileService.getStoreProfile();

        return ApplicationResponse.<StoreProfileDto>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("매장 프로필 조회가 완료되었습니다.")
                .data(response)
                .build();
    }

    @Operation(summary = "매장 프로필 수정", description = "점주의 매장 프로필 및 대표 메뉴를 수정합니다.")
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<StoreProfileDto> updateStoreProfile(
            @RequestPart("data") String data,
            @RequestPart(value = "storeImage", required = false) MultipartFile storeImage,
            @RequestPart(value = "stampImage", required = false) MultipartFile stampImage,
            @RequestPart(value = "menuImages", required = false) List<MultipartFile> menuImages
    ) throws JsonProcessingException {

        StoreProfileDto requestDto =
                objectMapper.readValue(data, StoreProfileDto.class);

        StoreProfileDto response =
                managerProfileService.updateStoreProfile(requestDto, storeImage, stampImage, menuImages);

        return ApplicationResponse.<StoreProfileDto>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("매장 프로필 수정이 완료되었습니다.")
                .data(response)
                .build();
    }
}
