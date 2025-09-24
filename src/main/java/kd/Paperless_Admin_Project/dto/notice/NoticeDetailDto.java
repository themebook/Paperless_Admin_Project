package kd.Paperless_Admin_Project.dto.notice;

import kd.Paperless_Admin_Project.entity.notice.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDetailDto {

  private Long noticeId;
  private String title;
  private String content;

  private Long adminId;
  private String adminName;
  private String category;
  private String isPinned;
  private String targetAudience;

  private LocalDateTime createdAt;
  private Long viewCount;
  private String status;

  public NoticeDetailDto(Long noticeId,
      String title,
      String content,
      Long adminId,
      String adminName,
      String category,
      Character isPinned,
      String targetAudience,
      LocalDateTime createdAt,
      Long viewCount,
      String status) {
    this.noticeId = noticeId;
    this.title = title;
    this.content = content;
    this.adminId = adminId;
    this.adminName = adminName;
    this.category = category;
    this.isPinned = normalizeYN(isPinned);
    this.targetAudience = targetAudience;
    this.createdAt = createdAt;
    this.viewCount = viewCount;
    this.status = status;
  }

  public static NoticeDetailDto fromEntity(Notice e) {
    if (e == null)
      return null;
    NoticeDetailDto dto = new NoticeDetailDto();
    dto.setNoticeId(e.getNoticeId());
    dto.setTitle(e.getTitle());
    dto.setContent(e.getContent());
    dto.setAdminId(e.getAdminId());
    dto.setCategory(e.getCategory());
    dto.setIsPinned(normalizeYN(e.getIsPinned()));
    dto.setTargetAudience(e.getTargetAudience());
    dto.setCreatedAt(e.getCreatedAt());
    dto.setViewCount(e.getViewCount());
    dto.setStatus(e.getStatus());
    return dto;
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
