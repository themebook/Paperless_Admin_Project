package kd.Paperless_Admin_Project.dto.document;

import lombok.*;
import java.time.LocalDateTime;
import kd.Paperless_Admin_Project.entity.document.PaperlessDoc;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PaperlessDocListDto {
  private Long plId;
  private String docType;
  private String status;
  private LocalDateTime submittedAt;
  private LocalDateTime processedAt;
  private Long userId;
  private String userName;
  private Long adminId;
  private String adminName;

  public PaperlessDocListDto(Long plId, String docType, String status,
      LocalDateTime submittedAt, LocalDateTime processedAt,
      Long userId, String userName,
      Long adminId, String adminName) {
    this.plId = plId;
    this.docType = docType;
    this.status = status;
    this.submittedAt = submittedAt;
    this.processedAt = processedAt;
    this.userId = userId;
    this.userName = userName;
    this.adminId = adminId;
    this.adminName = adminName;
  }

  public static PaperlessDocListDto fromEntity(PaperlessDoc e) {
    if (e == null)
      return null;
    return PaperlessDocListDto.builder()
        .plId(e.getPlId())
        .docType(e.getDocType())
        .status(e.getStatus())
        .submittedAt(e.getSubmittedAt())
        .processedAt(e.getProcessedAt())
        .userId(e.getUserId())
        .adminId(e.getAdminId())
        .build();
  }
}
