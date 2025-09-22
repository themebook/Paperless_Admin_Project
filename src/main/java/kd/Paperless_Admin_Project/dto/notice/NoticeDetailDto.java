package kd.Paperless_Admin_Project.dto.notice;

import kd.Paperless_Admin_Project.entity.notice.Notice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NoticeDetailDto {

  private Long noticeId;
  private String title;
  private String content;

  private Long adminId;
  private String isPinned;
  private String targetAudience;

  private LocalDateTime createdAt;
  private Long viewCount;
  private String status;

  @Builder.Default
  private Integer attachmentCount = 0;

  public static NoticeDetailDto fromEntity(Notice e) {
    if (e == null)
      return null;
    return NoticeDetailDto.builder()
        .noticeId(e.getNoticeId())
        .title(e.getTitle())
        .content(e.getContent())
        .adminId(e.getAdminId())
        .isPinned(e.getIsPinned() == null ? null : String.valueOf(e.getIsPinned()))
        .targetAudience(e.getTargetAudience())
        .createdAt(e.getCreatedAt())
        .viewCount(e.getViewCount())
        .status(e.getStatus())
        .build();
  }
}
