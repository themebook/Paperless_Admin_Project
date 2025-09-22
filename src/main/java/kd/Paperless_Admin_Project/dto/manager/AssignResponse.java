package kd.Paperless_Admin_Project.dto.manager;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignResponse {
  // 실제 배정된 건수
  private boolean success;
  private int assignedCount;
  private String message;
}
