package kd.Paperless_Admin_Project.dto.sinmungo;

import lombok.*;

import java.time.LocalDateTime;

import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class SinmungoListDto {
  private Long smgId;
  private String title;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime answerDate;
  private Long adminId;
  private String adminName;

  public SinmungoListDto(Long smgId, String title, String status,
      LocalDateTime createdAt, LocalDateTime answerDate,
      Long adminId, String adminName) {
    this.smgId = smgId;
    this.title = title;
    this.status = status;
    this.createdAt = createdAt;
    this.answerDate = answerDate;
    this.adminId = adminId;
    this.adminName = adminName;
  }

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
