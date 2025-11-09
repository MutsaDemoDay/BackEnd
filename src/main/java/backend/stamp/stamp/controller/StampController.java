package backend.stamp.stamp.controller;


import backend.stamp.global.security.PrincipalDetails;
import backend.stamp.stamp.dto.StampAddRequestDto;
import backend.stamp.stamp.dto.StampAddResponseDto;
import backend.stamp.stamp.dto.StampCreateRequestDto;
import backend.stamp.stamp.dto.StampCreateResponseDto;
import backend.stamp.stamp.service.StampService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stamps")
public class StampController {

    private final StampService stampService;

    //스탬프 등록 -> by 가게 검색
    @PostMapping
    public ResponseEntity<StampCreateResponseDto> createStamp(@AuthenticationPrincipal PrincipalDetails userDetail, @RequestBody StampCreateRequestDto requestDto) {

        //로그인한 유저 식별용 갖고 오기
        Long userId = userDetail.getAccount().getAccountId();

        return ResponseEntity.ok(stampService.createStamp(userId,requestDto));

    }

    //스탬프 적립
    @PostMapping("/add")
    public ResponseEntity<StampAddResponseDto> addStamp(@AuthenticationPrincipal PrincipalDetails userDetail,@RequestBody StampAddRequestDto requestDto) {
       //유저
        Long userId = userDetail.getAccount().getAccountId();
        Long storeId = requestDto.getStoreId();
        Long orderId = requestDto.getOrderId();

        StampAddResponseDto response = stampService.addStamp(userId, storeId, orderId);
    //서비스 호출

        return ResponseEntity.ok(response);


    }


}
