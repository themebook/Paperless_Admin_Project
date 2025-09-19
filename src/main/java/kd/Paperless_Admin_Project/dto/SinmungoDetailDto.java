package kd.Paperless_Admin_Project.dto;

import kd.Paperless_Admin_Project.entity.Sinmungo;
import lombok.*;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinmungoDetailDto {

  private Long smgId;
  private String title;
  private String content;

  private Long writerId;
  private String telNum;
  private String noticeEmail;
  private String noticeSms;

  private String postcode;
  private String addr1;
  private String addr2;

  private Long adminId;
  private String adminAnswer;
  private LocalDateTime answerDate;

  private String status;
  private String rejectReason;
  private Long viewCount;

  private LocalDateTime createdAt;

  public static SinmungoDetailDto fromEntity(Sinmungo e) {
    if (e == null)
      return null;

    return SinmungoDetailDto.builder()
        .smgId(e.getSmgId())
        .title(e.getTitle())
        .content(e.getContent())

        .writerId(e.getWriterId())
        .telNum(e.getTelNum())
        .noticeEmail(e.getNoticeEmail())
        .noticeSms(e.getNoticeSms() == '\0' ? null : String.valueOf(e.getNoticeSms()))

        .postcode(e.getPostcode())
        .addr1(e.getAddr1())
        .addr2(e.getAddr2())

        .adminId(e.getAdminId())
        .adminAnswer(e.getAdminAnswer())
        .answerDate(e.getAnswerDate())

        .status(e.getStatus())
        .rejectReason(e.getRejectReason())
        .viewCount(e.getViewCount())

        .createdAt(e.getCreatedAt())
        .build();
  }

  public void applyAssigneeToEntity(Sinmungo e) {
    e.setAdminId(this.adminId);
  }
}
