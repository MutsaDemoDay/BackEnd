package backend.stamp.stamp.dto;

import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StampAddRequestDto {

    private Long storeId;
    private Long orderId;
}
