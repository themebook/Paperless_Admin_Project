package kd.Paperless_Admin_Project.entity.notice;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NOTICE")
public class Notice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "NOTICE_ID")
  private Long noticeId;

  @Column(name = "TITLE", length = 200, nullable = false)
  private String title;

  @Lob
  @Column(name = "CONTENT", nullable = false)
  private String content;

  @Column(name = "ADMIN_ID", nullable = false)
  private Long adminId;

  @Column(name = "CATEGORY", length = 30)
  private String category;

  @Column(name = "IS_PINNED", length = 1)
  private Character isPinned;

  @Column(name = "TARGET_AUDIENCE", length = 10)
  private String targetAudience;

  @Column(name = "CREATED_AT", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "VIEW_COUNT")
  private Long viewCount;

  @Column(name = "STATUS", length = 20)
  private String status;
}
