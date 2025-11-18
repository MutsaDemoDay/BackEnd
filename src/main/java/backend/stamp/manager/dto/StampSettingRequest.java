package backend.stamp.manager.dto;

public record StampSettingRequest(
        String storeName,
        int requiredAmount, // e.g. 5000원
        String reward,
        int maxCnt // 1판에 10개

) {
}
