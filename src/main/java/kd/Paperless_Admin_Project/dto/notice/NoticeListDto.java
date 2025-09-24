package kd.Paperless_Admin_Project.dto.notice;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NoticeListDto {

  private Long noticeId;
  private String title;
  private LocalDateTime createdAt;
  private Long adminId;
  private String adminName;
  private String category;
  private String isPinned;
  private Long viewCount;
  private String targetAudience;
  private Integer attachmentCount;

  public NoticeListDto(Long noticeId,
      String title,
      LocalDateTime createdAt,
      Long adminId,
      String adminName,
      String category,
      Character isPinned,
      Long viewCount,
      String targetAudience,
      Integer attachmentCount) {
    this.noticeId = noticeId;
    this.title = title;
    this.createdAt = createdAt;
    this.adminId = adminId;
    this.adminName = adminName;
    this.category = category;
    this.isPinned = normalizeYN(isPinned);
    this.viewCount = viewCount;
    this.targetAudience = targetAudience;
    this.attachmentCount = (attachmentCount != null ? attachmentCount : 0);
  }

  private static String normalizeYN(Character yn) {
    if (yn == null)
      return null;
    if (yn == 'Y' || yn == 'y')
      return "Y";
    if (yn == 'N' || yn == 'n')
      return "N";
    return String.valueOf(yn);
  }
}