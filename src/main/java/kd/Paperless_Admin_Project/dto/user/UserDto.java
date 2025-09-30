package kd.Paperless_Admin_Project.dto.user;

import kd.Paperless_Admin_Project.entity.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private Long userId;
  private String loginId;
  private String userName;
  private String email;
  private String status;
  private LocalDateTime createdAt;

  public static UserDto from(User e) {
    if (e == null)
      return null;
    return UserDto.builder()
        .userId(e.getUserId())
        .loginId(e.getLoginId())
        .userName(e.getUserName())
        .email(e.getEmail())
        .status(e.getStatus())
        .createdAt(e.getCreatedAt())
        .build();
  }
}
