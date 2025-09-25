package kd.Paperless_Admin_Project.dto.notice;

import kd.Paperless_Admin_Project.entity.notice.Notice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NoticeWriteDto {

  @NotBlank(message = "제목을 입력하세요.")
  private String title;

  @NotBlank(message = "내용을 입력하세요.")
  private String content;

  @Pattern(regexp = "^[YNyn]?$", message = "상단 고정은 Y 또는 N 이어야 합니다.")
  private String isPinned;

  @Pattern(regexp = "^(ADMIN|USER)?$", message = "대상은 ADMIN 또는 USER 입니다.")
  private String targetAudience;

  private List<MultipartFile> files;

  public Notice toEntity(Long adminId) {
    return Notice.builder()
        .title(normalizeTitle(title))
        .content(content)
        .adminId(adminId)
        .isPinned(toYNChar(isPinned))
        .targetAudience(normalizeAudience(targetAudience))
        .status("POSTED")
        .build();
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
