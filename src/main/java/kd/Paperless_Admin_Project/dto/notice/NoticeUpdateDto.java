package kd.Paperless_Admin_Project.dto.notice;

import jakarta.validation.constraints.NotBlank;
import kd.Paperless_Admin_Project.entity.notice.Notice;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateDto {

  private Long noticeId;

  @NotBlank(message = "제목을 입력하세요.")
  private String title;

  @NotBlank(message = "내용을 입력하세요.")
  private String content;

  @Builder.Default
  private String isPinned = "N";

  @Builder.Default
  private String targetAudience = "ADMIN";

  public static NoticeUpdateDto fromEntity(Notice e) {
    if (e == null)
      return null;
    return NoticeUpdateDto.builder()
        .noticeId(e.getNoticeId())
        .title(e.getTitle())
        .content(e.getContent())
        .isPinned(e.getIsPinned() == null ? "N" : String.valueOf(e.getIsPinned()))
        .targetAudience(e.getTargetAudience())
        .build();
  }

  public void applyToEntity(Notice e) {
    e.setTitle(title != null ? title.trim() : null);
    e.setContent(content);
    e.setIsPinned(isPinned != null && !isPinned.isBlank() ? isPinned.charAt(0) : 'N');
    e.setTargetAudience(targetAudience != null ? targetAudience : "ADMIN");
  }
}
