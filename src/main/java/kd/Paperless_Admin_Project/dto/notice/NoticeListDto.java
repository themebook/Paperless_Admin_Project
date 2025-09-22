package kd.Paperless_Admin_Project.dto.notice;

import kd.Paperless_Admin_Project.entity.notice.Notice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListDto {

  private Long noticeId;
  private String title;
  private LocalDateTime createdAt;
  private Long adminId;
  private String category;
  private String isPinned;
  private Long viewCount;
  private String targetAudience;

  @Builder.Default
  private Integer attachmentCount = 0;

  public static NoticeListDto fromEntity(Notice e) {
    if (e == null) {
      return null;
    }
    return NoticeListDto.builder()
        .noticeId(e.getNoticeId())
        .title(e.getTitle())
        .createdAt(e.getCreatedAt())
        .adminId(e.getAdminId())
        .category(e.getCategory())
        .isPinned(String.valueOf(e.getIsPinned()))
        .viewCount(e.getViewCount())
        .targetAudience(e.getTargetAudience())
        .build();
  }
}