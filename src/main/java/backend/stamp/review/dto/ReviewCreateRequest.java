package backend.stamp.review.dto;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequest {
    private Long storeId;
    private byte rate;
    private String content;
}
