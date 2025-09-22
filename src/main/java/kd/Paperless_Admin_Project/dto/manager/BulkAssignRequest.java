package kd.Paperless_Admin_Project.dto.manager;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAssignRequest {
  // 일괄배정
  @NotEmpty
  private List<@NotNull Long> ids;
  @NotNull
  private Long adminId;
}
