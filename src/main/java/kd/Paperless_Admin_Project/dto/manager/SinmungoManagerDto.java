package kd.Paperless_Admin_Project.dto.manager;

import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinmungoManagerDto {
  private Long smgId;
  private String title;
  private String content;

  public static SinmungoManagerDto fromEntity(Sinmungo e) {
    if (e == null)
      return null;
    return SinmungoManagerDto.builder()
        .smgId(e.getSmgId())
        .title(e.getTitle())
        .content(e.getContent())
        .build();
  }
}
