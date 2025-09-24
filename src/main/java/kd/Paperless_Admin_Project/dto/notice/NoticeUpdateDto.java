package kd.Paperless_Admin_Project.dto.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "^[YNyn]$", message = "상단 고정은 Y 또는 N 이어야 합니다.")
  private String isPinned = "N";

  @Builder.Default
  @Pattern(regexp = "^(ADMIN|USER)$", message = "대상은 ADMIN 또는 USER 입니다.")
  private String targetAudience = "ADMIN";

  public static NoticeUpdateDto fromEntity(Notice e) {
    if (e == null)
      return null;
    return NoticeUpdateDto.builder()
        .noticeId(e.getNoticeId())
        .title(e.getTitle())
        .content(e.getContent())
        .isPinned(e.getIsPinned() == null ? "N" : String.valueOf(e.getIsPinned()))
        .targetAudience(e.getTargetAudience() == null ? "ADMIN" : e.getTargetAudience())
        .build();
  }

  public void applyToEntity(Notice e) {
    e.setTitle(normalizeTitle(title));
    e.setContent(content);
    e.setIsPinned(toYNChar(isPinned));
    e.setTargetAudience(normalizeAudience(targetAudience));
  }

  private static String normalizeTitle(String t) {
    if (t == null)
      return null;
    String v = t.trim();
    return v.isEmpty() ? null : v;
  }

  private static Character toYNChar(String s) {
    if (s == null || s.isBlank())
      return 'N';
    char c = Character.toUpperCase(s.trim().charAt(0));
    return (c == 'Y' || c == 'N') ? c : 'N';
  }

  private static String normalizeAudience(String s) {
    if (s == null || s.isBlank())
      return "ADMIN";
    String v = s.trim().toUpperCase();
    return ("USER".equals(v) || "ADMIN".equals(v)) ? v : "ADMIN";
  }
}
