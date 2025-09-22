package kd.Paperless_Admin_Project.entity.admin;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "ADMIN",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_ADMIN_LOGIN_ID", columnNames = "LOGIN_ID")
    }
)
@SequenceGenerator(
    name = "admin_seq",
    sequenceName = "SEQ_ADMIN",
    allocationSize = 1
)
public class Admin {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_seq")
  @Column(name = "ADMIN_ID")
  private Long adminId;

  @Column(name = "LOGIN_ID", length = 50, nullable = false)
  private String loginId;

  @Column(name = "PASSWORD_HASH", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "ADMIN_NAME", length = 100, nullable = false)
  private String adminName;

  @Column(name = "ROLE_CODE", precision = 2)
  private Integer roleCode;

  @Column(name = "EMAIL", length = 200)
  private String email;

  @Column(name = "PHONE_NUM", length = 20)
  private String phoneNum;

  @Column(name = "CREATED_AT", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "UPDATED_AT")
  private LocalDateTime updatedAt;

  @Column(name = "STATUS", length = 20)
  private String status;
}
