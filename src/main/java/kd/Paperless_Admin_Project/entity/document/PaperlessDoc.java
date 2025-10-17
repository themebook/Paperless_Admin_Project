package kd.Paperless_Admin_Project.entity.document;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PAPERLESS_DOC")
@SequenceGenerator(name = "paperless_pl_seq", sequenceName = "seq_paperless_pl_id",
    allocationSize = 1)
public class PaperlessDoc {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paperless_pl_seq")
  @Column(name = "PL_ID")
  private Long plId;

  @Column(name = "USER_ID", nullable = false)
  private Long userId;

  @Column(name = "CONSENT_YN", columnDefinition = "CHAR(1)", nullable = false)
  private Character consentYn;

  @Column(name = "STATUS", length = 20, nullable = false)
  private String status;

  @Column(name = "SUBMITTED_AT", insertable = false, updatable = false)
  private LocalDateTime submittedAt;

  @Column(name = "PROCESSED_AT")
  private LocalDateTime processedAt;

  @Column(name = "ADMIN_ID")
  private Long adminId;

  @Column(name = "DOC_TYPE", length = 50, nullable = false)
  private String docType;

  @Lob
  @Column(name = "EXTRA_JSON")
  private String extraJson;
}
