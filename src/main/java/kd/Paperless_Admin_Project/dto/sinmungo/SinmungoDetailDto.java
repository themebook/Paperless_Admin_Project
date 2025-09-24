package kd.Paperless_Admin_Project.dto.sinmungo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;

@Getter
@Setter
@NoArgsConstructor
public class SinmungoDetailDto {

  private Long smgId;
  private String title;
  private String content;

  private Long writerId;
  private String writerName;
  private String telNum;
  private String noticeEmail;
  private String noticeSms;

  private String postcode;
  private String addr1;
  private String addr2;

  private Long adminId;
  private String adminName;
  private String adminAnswer;
  private LocalDateTime answerDate;

  private String status;
  private String rejectReason;
  private Long viewCount;

  private LocalDateTime createdAt;

  public SinmungoDetailDto(
      Long smgId, String title, String content,
      Long writerId, String writerName, String telNum, String noticeEmail, Character noticeSms,
      String postcode, String addr1, String addr2,
      Long adminId, String adminName, String adminAnswer, LocalDateTime answerDate,
      String status, String rejectReason, Long viewCount,
      LocalDateTime createdAt) {
    this.smgId = smgId;
    this.title = title;
    this.content = content;

    this.writerId = writerId;
    this.writerName = writerName;
    this.telNum = telNum;
    this.noticeEmail = noticeEmail;
    this.noticeSms = normalizeYN(noticeSms);

    this.postcode = postcode;
    this.addr1 = addr1;
    this.addr2 = addr2;

    this.adminId = adminId;
    this.adminName = adminName;
    this.adminAnswer = adminAnswer;
    this.answerDate = answerDate;

    this.status = status;
    this.rejectReason = rejectReason;
    this.viewCount = viewCount;

    this.createdAt = createdAt;
  }

  public static SinmungoDetailDto fromEntity(Sinmungo e) {
    if (e == null)
      return null;

    SinmungoDetailDto dto = new SinmungoDetailDto();
    dto.setSmgId(e.getSmgId());
    dto.setTitle(e.getTitle());
    dto.setContent(e.getContent());

    dto.setWriterId(e.getWriterId());
    dto.setTelNum(e.getTelNum());
    dto.setNoticeEmail(e.getNoticeEmail());
    dto.setNoticeSms(normalizeYN(e.getNoticeSms()));

    dto.setPostcode(e.getPostcode());
    dto.setAddr1(e.getAddr1());
    dto.setAddr2(e.getAddr2());

    dto.setAdminId(e.getAdminId());
    dto.setAdminAnswer(e.getAdminAnswer());
    dto.setAnswerDate(e.getAnswerDate());

    dto.setStatus(e.getStatus());
    dto.setRejectReason(e.getRejectReason());
    dto.setViewCount(e.getViewCount());

    dto.setCreatedAt(e.getCreatedAt());
    return dto;
  }

  public void applyAssigneeToEntity(Sinmungo e) {
    e.setAdminId(this.adminId);
  }

  private static String normalizeYN(Character yn) {
    if (yn == null)
      return null;
    if (yn == 'Y' || yn == 'y')
      return "Y";
    if (yn == 'N' || yn == 'n')
      return "N";
    return String.valueOf(yn);
  }
}
