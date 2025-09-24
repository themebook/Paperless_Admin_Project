package kd.Paperless_Admin_Project.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USERS")
@SequenceGenerator(
    name = "seq_users",
    sequenceName = "SEQ_USERS",
    allocationSize = 1
)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_users")
  @Column(name = "USER_ID")
  private Long userId;

  @Column(name = "LOGIN_ID", length = 50, nullable = false)
  private String loginId;

  @Column(name = "PASSWORD_HASH", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "USER_NAME", length = 100, nullable = false)
  private String userName;

  @Column(name = "USER_BIRTH")
  private LocalDate userBirth;

  @Column(name = "PHONE_NUM", length = 20)
  private String phoneNum;

  @Column(name = "TEL_NUM", length = 20)
  private String telNum;

  @Column(name = "EMAIL", length = 200)
  private String email;

  @Column(name = "POSTCODE", length = 10)
  private String postcode;

  @Column(name = "ADDR1", length = 200)
  private String addr1;

  @Column(name = "ADDR2", length = 200)
  private String addr2;

  @Column(name = "CREATED_AT", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "UPDATED_AT")
  private LocalDateTime updatedAt;

  @Column(name = "STATUS", length = 20)
  private String status;
}