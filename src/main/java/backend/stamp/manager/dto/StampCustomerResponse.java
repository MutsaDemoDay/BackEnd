package backend.stamp.manager.dto;

import java.time.LocalDateTime;

public record StampCustomerResponse(
        Long userId,
        String nickname,
        LocalDateTime createdAt,
        Integer level
) {
}
