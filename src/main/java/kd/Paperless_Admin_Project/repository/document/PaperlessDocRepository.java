// src/main/java/kd/Paperless_Admin_Project/repository/document/PaperlessDocRepository.java
package kd.Paperless_Admin_Project.repository.document;

import kd.Paperless_Admin_Project.dto.document.PaperlessDocDetailDto;
import kd.Paperless_Admin_Project.dto.document.PaperlessDocListDto;
import kd.Paperless_Admin_Project.entity.document.PaperlessDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PaperlessDocRepository extends JpaRepository<PaperlessDoc, Long> {

  // ✅ 기존: 전체 목록 (상태/문서유형)
  @Query(value = """
      SELECT new kd.Paperless_Admin_Project.dto.document.PaperlessDocListDto(
          d.plId,
          d.docType,
          d.status,
          d.submittedAt,
          d.processedAt,
          u.userId,
          u.userName,
          d.adminId,
          a.adminName
      )
      FROM PaperlessDoc d
      LEFT JOIN kd.Paperless_Admin_Project.entity.user.User u
        ON u.userId = d.userId
      LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a
        ON a.adminId = d.adminId
      WHERE (:status IS NULL OR d.status = :status)
        AND (:docType IS NULL OR d.docType = :docType)
      ORDER BY d.submittedAt DESC
      """,
      countQuery = """
      SELECT COUNT(d)
      FROM PaperlessDoc d
      WHERE (:status IS NULL OR d.status = :status)
        AND (:docType IS NULL OR d.docType = :docType)
      """)
  Page<PaperlessDocListDto> adminSearchWithUserName(
      @Param("status") String status,
      @Param("docType") String docType,
      Pageable pageable);

  // ✅ 추가: 내 담당 목록 (adminId 필터)
  @Query(value = """
      SELECT new kd.Paperless_Admin_Project.dto.document.PaperlessDocListDto(
          d.plId,
          d.docType,
          d.status,
          d.submittedAt,
          d.processedAt,
          u.userId,
          u.userName,
          d.adminId,
          a.adminName
      )
      FROM PaperlessDoc d
      LEFT JOIN kd.Paperless_Admin_Project.entity.user.User u
        ON u.userId = d.userId
      LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a
        ON a.adminId = d.adminId
      WHERE d.adminId = :adminId
        AND (:status IS NULL OR d.status = :status)
        AND (:docType IS NULL OR d.docType = :docType)
      ORDER BY d.submittedAt DESC
      """,
      countQuery = """
      SELECT COUNT(d)
      FROM PaperlessDoc d
      WHERE d.adminId = :adminId
        AND (:status IS NULL OR d.status = :status)
        AND (:docType IS NULL OR d.docType = :docType)
      """)
  Page<PaperlessDocListDto> adminAssignedSearchWithUserName(
      @Param("adminId") Long adminId,
      @Param("status") String status,
      @Param("docType") String docType,
      Pageable pageable);

  // ✅ 상세 (실제 필드명으로 수정)
  @Query("""
      SELECT new kd.Paperless_Admin_Project.dto.document.PaperlessDocDetailDto(
          d.plId,
          d.docType,
          d.status,
          d.submittedAt,
          d.processedAt,
          u.userId,
          u.userName,
          d.adminId,
          a.adminName
      )
      FROM PaperlessDoc d
      LEFT JOIN kd.Paperless_Admin_Project.entity.user.User u
        ON u.userId = d.userId
      LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a
        ON a.adminId = d.adminId
      WHERE d.plId = :plId
      """)
  PaperlessDocDetailDto findDetailWithNames(@Param("plId") Long plId);
}
