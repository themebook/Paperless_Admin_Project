// src/main/java/kd/Paperless_Admin_Project/dto/document/PaperlessDocDetailDto.java
package kd.Paperless_Admin_Project.dto.document;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaperlessDocDetailDto {
  private Long plId;
  private String docType;
  private String status;
  private LocalDateTime submittedAt;
  private LocalDateTime processedAt;

  private Long userId;
  private String userName;

  private Long adminId;
  private String adminName;
}
