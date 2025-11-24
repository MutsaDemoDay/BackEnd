package backend.stamp.manager.dto.dashboard;

public record StampSettingResponse(
        String storeName,
        int requiredAmount,
        String reward,
        int maxCnt,
        String imgurl

) {
}