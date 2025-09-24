package kd.Paperless_Admin_Project.repository.admin;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByLoginId(String loginId);

  boolean existsByRoleCode(Integer roleCode);

  @Query("""
        SELECT a FROM Admin a
         WHERE (:q IS NULL OR
                LOWER(a.loginId)   LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(a.adminName) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(a.email)     LIKE LOWER(CONCAT('%', :q, '%')))
           AND (:roleCode IS NULL OR a.roleCode = :roleCode)
           AND (:status IS NULL OR a.status = :status)
         ORDER BY a.createdAt DESC NULLS LAST, a.adminId DESC
      """)
  Page<Admin> search(@Param("q") String q,
      @Param("roleCode") Integer roleCode,
      @Param("status") String status,
      Pageable pageable);
}
