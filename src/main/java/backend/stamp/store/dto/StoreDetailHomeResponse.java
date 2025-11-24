package backend.stamp.store.dto;

import backend.stamp.store.entity.Category;
import backend.stamp.store.entity.Store;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreDetailHomeResponse {
    private Long storeId;
    private String name;
    private Category category;
    private String address;
    private String storeImageUrl;
    private String phone;
    private String storeUrl;
    private String sns;

    private Double distanceMeters;

    // 영업시간 정보
    private String status;      // "영업중", "영업종료"
    private String message;     // "HH:mm까지", "금일 영업 종료"

    // 스탬프 정보
    private String reward;
    private String stampReward;
    private String stampImageUrl;
    private Integer maxCount;

    // 리뷰 작성 가능 여부
    private boolean isReviewAvailable;

    // 대표 메뉴 정보
    private List<StoreMenuInfo> signatureMenus;

    // --- Inner DTO Classes ---
    @Getter
    @Builder
    public static class StoreMenuInfo {
        private String menuName;
        private String content;
        private int price;
        private String menuImageUrl;
    }
}
