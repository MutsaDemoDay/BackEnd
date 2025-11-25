package backend.stamp.users.dto;

import backend.stamp.users.entity.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsDto {

    private String profileImageUrl;
    private String representativeBadgeName;
    private Gender gender;

    private String address;

    private Double latitude;
    private Double longitude;

}
