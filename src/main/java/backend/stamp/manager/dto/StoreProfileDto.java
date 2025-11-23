package backend.stamp.manager.dto;

import backend.stamp.store.entity.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class StoreProfileDto {
    /**
     * 실제 프로필 데이터
     * - GET 응답 시: 서버에서 채워서 내려줌
     * - PATCH 요청 시: 클라이언트가 채워서 보냄 (null 필드는 무시 가능)
     */
    private StoreInfo store;

    @Getter
    @Setter
    public static class StoreInfo {
        private Long storeId;          // 조회용
        private String storeName;
        private String phone;
        private Category category;

        // 매장 대표 이미지 (1개)
        private String storeImageUrl;

        // 스탬프 이미지 (1개, 온보딩에서 이미 세팅되었을 수 있음)
        private String stampImageUrl;

        private String storeUrl;

        private String sns;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String verificationCode;


        private Integer requiredAmount;
        private String reward;
        private Integer maxCount;

        private List<BusinessHourData> businessHours;
        private List<MenuData> menus;
    }

    @Getter
    @Setter
    public static class BusinessHourData {
        private Long id;               // 기존 영업시간 id (없으면 신규)
        private String day;
        private LocalTime openTime;
        private LocalTime closeTime;
        private Boolean isHoliday;

        /**
         * "CREATE" | "UPDATE" | "DELETE"
         * - 응답(GET)에서는 null 로 내려줘도 됨
         * - 요청(PATCH)에서는 비워둘 경우 기본 동작:
         *   - id != null  → UPDATE
         *   - id == null  → CREATE
         */
        private String action;
    }

    @Getter
    @Setter
    public static class MenuData {
        private Long id;               // 기존 메뉴 id (없으면 신규)
        private String name;
        private int price;
        private String content;
        private String imageUrl;       // 조회용 (PATCH 에서 새 이미지 안 보내면 기존 유지)

        /**
         * "CREATE" | "UPDATE" | "DELETE"
         * 기본 동작은 BusinessHourData 와 동일
         */
        private String action;
    }
}
