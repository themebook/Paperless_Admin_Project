package kd.Paperless_Admin_Project.repository.user;

import kd.Paperless_Admin_Project.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("""
      SELECT u
        FROM User u
       WHERE (:status IS NULL OR u.status = :status)
         AND (
               :q IS NULL
            OR LOWER(u.loginId)  LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%'))
         )
      """)
  Page<User> search(@Param("q") String q,
      @Param("status") String status,
      Pageable pageable);

  Optional<User> findByLoginId(String loginId);

  boolean existsByLoginId(String loginId);

  Optional<User> findByEmail(String email);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE User u SET u.status = :status WHERE u.userId = :id")
  int updateStatus(@Param("id") Long userId, @Param("status") String status);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE User u SET u.passwordHash = :passwordHash WHERE u.userId = :id")
  int updatePassword(@Param("id") Long userId, @Param("passwordHash") String passwordHash);

  long countByStatus(String status);
}