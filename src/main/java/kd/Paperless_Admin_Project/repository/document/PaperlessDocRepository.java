package kd.Paperless_Admin_Project.repository.document;

import kd.Paperless_Admin_Project.entity.document.PaperlessDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PaperlessDocRepository extends JpaRepository<PaperlessDoc, Long> {

  @Query("""
      SELECT d
      FROM PaperlessDoc d
      WHERE (:status  IS NULL OR d.status  = :status)
        AND (:docType IS NULL OR d.docType = :docType)
      ORDER BY d.plId DESC
      """)
  Page<PaperlessDoc> adminSearch(@Param("status") String status,
      @Param("docType") String docType,
      Pageable pageable);
}