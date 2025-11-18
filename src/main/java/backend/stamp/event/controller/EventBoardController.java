package backend.stamp.event.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/board")

@Tag(name = "EventBoard ( 유저 : 이벤트 조회 관련 )", description = "유저 이벤트 조회 관련 API")
public class EventBoardController {


    //이벤트 홍보 게시판 리스트 조회

    //이벤트 홍보 게시판 개별조회
}

