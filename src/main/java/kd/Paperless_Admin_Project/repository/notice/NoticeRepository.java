package kd.Paperless_Admin_Project.repository.notice;

import kd.Paperless_Admin_Project.dto.notice.NoticeDetailDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeListDto;
import kd.Paperless_Admin_Project.entity.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  @Query("""
        SELECT new kd.Paperless_Admin_Project.dto.notice.NoticeListDto(
          n.noticeId, n.title, n.createdAt, n.adminId, a.adminName,
          n.category, n.isPinned, n.viewCount, n.targetAudience, 0
        )
        FROM Notice n
        LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a ON a.adminId = n.adminId
        WHERE n.targetAudience = 'ADMIN' OR n.targetAudience = 'ALL' OR n.targetAudience = 'USER' 
          AND (
                :q IS NULL
             OR  LOWER(n.title) LIKE CONCAT('%', LOWER(cast(:q as string)), '%')
             OR  function(
                    'INSTR',
                    LOWER(function('DBMS_LOB.SUBSTR', n.content, 4000, 1)),
                    LOWER(cast(:q as string))
                 ) > 0
          )
        ORDER BY n.isPinned DESC, n.createdAt DESC
      """)
  Page<NoticeListDto> searchAdminNoticesWithName(@Param("q") String q, Pageable pageable);

  @Query("""
        SELECT new kd.Paperless_Admin_Project.dto.notice.NoticeDetailDto(
          n.noticeId, n.title, n.content,
          n.adminId, a.adminName,
          n.category, n.isPinned,
          n.targetAudience,
          n.createdAt, n.viewCount, n.status
        )
        FROM Notice n
        LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a ON a.adminId = n.adminId
        WHERE n.noticeId = :id
      """)
  Optional<NoticeDetailDto> findAdminByIdWithName(@Param("id") Long id);

  @Query("""
        SELECT new kd.Paperless_Admin_Project.dto.notice.NoticeListDto(
          n.noticeId, n.title, n.createdAt, n.adminId, a.adminName,
          n.category, n.isPinned, n.viewCount, n.targetAudience, 0
        )
        FROM Notice n
        LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a ON a.adminId = n.adminId
        WHERE n.noticeId < :id
        ORDER BY n.noticeId DESC
      """)
  List<NoticeListDto> findPrevAdminWithName(@Param("id") Long id, Pageable pageable);

  @Query("""
        SELECT new kd.Paperless_Admin_Project.dto.notice.NoticeListDto(
          n.noticeId, n.title, n.createdAt, n.adminId, a.adminName,
          n.category, n.isPinned, n.viewCount, n.targetAudience, 0
        )
        FROM Notice n
        LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a ON a.adminId = n.adminId
        WHERE n.noticeId > :id
        ORDER BY n.noticeId ASC
      """)
  List<NoticeListDto> findNextAdminWithName(@Param("id") Long id, Pageable pageable);

  @Query("""
        SELECT n FROM Notice n
        WHERE n.noticeId = :id
      """)
  Optional<Notice> findAdminById(@Param("id") Long id);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        UPDATE Notice n
        SET n.viewCount = COALESCE(n.viewCount, 0) + 1
        WHERE n.noticeId = :id
      """)
  int increaseViewCount(@Param("id") Long id);
}
