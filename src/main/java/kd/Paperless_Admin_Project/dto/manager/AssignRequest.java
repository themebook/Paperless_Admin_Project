package kd.Paperless_Admin_Project.dto.manager;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRequest {
  // 단일배정
  @NotNull
  private Long smgId;
  @NotNull
  private Long adminId;
}
