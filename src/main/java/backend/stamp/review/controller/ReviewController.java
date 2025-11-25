package backend.stamp.review.controller;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ApplicationResponse;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.manager.dto.StoreProfileDto;
import backend.stamp.review.dto.ReviewCreateRequest;
import backend.stamp.review.dto.ReviewerProfileResponse;
import backend.stamp.review.service.ReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Review (리뷰 관련)", description = "리뷰 작성 및 리뷰어 프로필 조회 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "리뷰어 프로필 조회", description = "다른 유저의 레벨, 스탬프, 뱃지, 리뷰 목록을 조회합니다.")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApplicationResponse<ReviewerProfileResponse>> getReviewerProfile(
            @PathVariable Long userId) {

        ReviewerProfileResponse response = reviewService.getReviewerProfile(userId);
        return ResponseEntity.ok(ApplicationResponse.ok(response));
    }

    @Operation(summary = "리뷰 작성", description = "특정 가게에 별점 및 내용, 사진/영상을 첨부하여 리뷰를 작성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<Long> createReview(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestPart("data") String data,
            @RequestPart(value = "reviewImage", required = false) MultipartFile reviewImage
    ) throws ApplicationException, JsonProcessingException
    {

        if (userDetails == null || userDetails.getAccount() == null) {
            throw new ApplicationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        final Account account = userDetails.getAccount();

        ReviewCreateRequest requestDto =
                objectMapper.readValue(data, ReviewCreateRequest.class);

        Long reviewId = reviewService.createReview(account, requestDto, reviewImage);

        return ApplicationResponse.<Long>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("리뷰 작성이 완료되었습니다.")
                .data(reviewId)
                .build();
    }
}
