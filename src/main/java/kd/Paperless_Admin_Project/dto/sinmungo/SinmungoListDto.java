package kd.Paperless_Admin_Project.dto.sinmungo;

import lombok.*;

import java.time.LocalDateTime;

import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinmungoListDto {

  private Long smgId;
  private String title;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime answerDate;
  private Long adminId;

  public static SinmungoListDto fromEntity(Sinmungo e) {
    if (e == null)
      return null;
    return SinmungoListDto.builder()
        .smgId(e.getSmgId())
        .title(e.getTitle())
        .status(e.getStatus())
        .createdAt(e.getCreatedAt())
        .answerDate(e.getAnswerDate())
        .adminId(e.getAdminId())
        .build();
  }
}
