package kd.Paperless_Admin_Project.dto.notice;

import kd.Paperless_Admin_Project.entity.notice.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeWriteDto {

  @NotBlank(message = "제목을 입력하세요.")
  private String title;

  @NotBlank(message = "내용을 입력하세요.")
  private String content;

  private String isPinned;
  private String targetAudience;

  public Notice toEntity(Long adminId) {
    return Notice.builder()
        .title(title != null ? title.trim() : null)
        .content(content)
        .adminId(adminId)
        .isPinned(isPinned != null && !isPinned.isBlank() ? isPinned.charAt(0) : 'N')
        .targetAudience(targetAudience != null ? targetAudience : "ADMIN")
        .build();
  }
}
