package kd.Paperless_Admin_Project.repository.sinmungo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import kd.Paperless_Admin_Project.dto.sinmungo.SinmungoDetailDto;
import kd.Paperless_Admin_Project.dto.sinmungo.SinmungoListDto;
import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;

import java.time.LocalDateTime;
import java.util.List;

public interface SinmungoRepository extends JpaRepository<Sinmungo, Long> {

  @Query("""
        SELECT s FROM Sinmungo s
        WHERE (:status IS NULL OR s.status = :status)
          AND (
                :q IS NULL
             OR  LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
             OR  CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%')
          )
        ORDER BY s.smgId DESC
      """)
  Page<Sinmungo> adminSearch(@Param("q") String q,
      @Param("status") String status,
      Pageable pageable);

  @Query("""
        SELECT s FROM Sinmungo s
        WHERE s.adminId = :adminId
          AND (:status IS NULL OR s.status = :status)
          AND (
                :q IS NULL
             OR  LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
             OR  CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%')
          )
        ORDER BY s.smgId DESC
      """)
  Page<Sinmungo> adminAssignedSearch(@Param("adminId") Long adminId,
      @Param("q") String q,
      @Param("status") String status,
      Pageable pageable);

  @Query("""
        SELECT function('TO_CHAR', s.createdAt, 'YYYY-MM-DD') AS dayKey, COUNT(s)
        FROM Sinmungo s
        WHERE s.createdAt >= :start
        GROUP BY function('TO_CHAR', s.createdAt, 'YYYY-MM-DD')
        ORDER BY function('TO_CHAR', s.createdAt, 'YYYY-MM-DD')
      """)
  List<Object[]> countByDaySince(@Param("start") java.time.LocalDateTime start);

  @Query("""
        SELECT s.status, COUNT(s)
        FROM Sinmungo s
        GROUP BY s.status
      """)
  List<Object[]> countByStatus();

  @Query("""
        SELECT s.status, COUNT(s)
        FROM Sinmungo s
        WHERE s.adminId = :adminId
        GROUP BY s.status
      """)
  List<Object[]> countByStatusForAdmin(@Param("adminId") Long adminId);

  @Query("""
        SELECT s FROM Sinmungo s
        WHERE s.adminId IS NULL
          AND (
               :q IS NULL
            OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
            OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%')
          )
        ORDER BY s.smgId DESC
      """)
  Page<Sinmungo> searchUnassigned(@Param("q") String q, Pageable pageable);

  @Query("SELECT COUNT(s) FROM Sinmungo s WHERE s.adminId IS NULL")
  long countUnassigned();

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE Sinmungo s
      SET s.adminId = :adminId
       WHERE s.smgId = :smgId
       AND s.adminId IS NULL
       """)
  int assignOneIfUnassigned(@Param("smgId") Long smgId, @Param("adminId") Long adminId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE Sinmungo s
      SET s.adminId = :adminId
      WHERE s.smgId IN :ids
      AND s.adminId IS NULL
      """)
  int assignBulkIfUnassigned(@Param("ids") List<Long> ids, @Param("adminId") Long adminId);

  @Query(value = """
      SELECT new kd.Paperless_Admin_Project.dto.sinmungo.SinmungoListDto(
               s.smgId, s.title, s.status, s.createdAt, s.answerDate, s.adminId, a.adminName)
      FROM Sinmungo s
      LEFT JOIN Admin a ON a.adminId = s.adminId
      WHERE (:q IS NULL
              OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
              OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%'))
        AND (:status IS NULL OR s.status = :status)
      ORDER BY s.smgId DESC
      """, countQuery = """
      SELECT COUNT(s)
      FROM Sinmungo s
      WHERE (:q IS NULL
              OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
              OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%'))
        AND (:status IS NULL OR s.status = :status)
      """)
  Page<SinmungoListDto> adminSearchWithName(@Param("q") String q,
      @Param("status") String status,
      Pageable pageable);

  @Query(value = """
      SELECT new kd.Paperless_Admin_Project.dto.sinmungo.SinmungoListDto(
               s.smgId, s.title, s.status, s.createdAt, s.answerDate, s.adminId, a.adminName)
      FROM Sinmungo s
      LEFT JOIN Admin a ON a.adminId = s.adminId
      WHERE s.adminId = :adminId
        AND (:q IS NULL
              OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
              OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%'))
        AND (:status IS NULL OR s.status = :status)
      ORDER BY s.smgId DESC
      """, countQuery = """
      SELECT COUNT(s)
      FROM Sinmungo s
      WHERE s.adminId = :adminId
        AND (:q IS NULL
              OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
              OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%'))
        AND (:status IS NULL OR s.status = :status)
      """)
  Page<SinmungoListDto> adminAssignedSearchWithName(@Param("adminId") Long adminId,
      @Param("q") String q,
      @Param("status") String status,
      Pageable pageable);

  @Query("""
        SELECT new kd.Paperless_Admin_Project.dto.sinmungo.SinmungoDetailDto(
          s.smgId, s.title, s.content,
          s.writerId, u.userName, s.telNum, s.noticeEmail, s.noticeSms,
          s.postcode, s.addr1, s.addr2,
          s.adminId, a.adminName, s.adminAnswer, s.answerDate,
          s.status, s.rejectReason, s.viewCount,
          s.createdAt
        )
        FROM Sinmungo s
        LEFT JOIN kd.Paperless_Admin_Project.entity.user.User  u ON u.userId  = s.writerId
        LEFT JOIN kd.Paperless_Admin_Project.entity.admin.Admin a ON a.adminId = s.adminId
        WHERE s.smgId = :id
      """)
  SinmungoDetailDto findDetailWithNames(@Param("id") Long id);

  long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  long countByAdminId(Long adminId);

  long countByAdminIdAndStatus(Long adminId, String status);

  @Query(value = """
      SELECT new kd.Paperless_Admin_Project.dto.sinmungo.SinmungoListDto(
               s.smgId, s.title, s.status, s.createdAt, s.answerDate, s.adminId, a.adminName)
      FROM Sinmungo s
      LEFT JOIN Admin a ON a.adminId = s.adminId
      WHERE s.status = '접수'
        AND ( :q IS NULL
              OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
              OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%') )
        AND NOT EXISTS (
              SELECT 1 FROM Attachment at
              WHERE at.targetType = 'SINMUNGO'
                AND at.targetId = s.smgId
        )
      ORDER BY s.createdAt ASC
      """, countQuery = """
      SELECT COUNT(s)
      FROM Sinmungo s
      WHERE s.status = '접수'
        AND ( :q IS NULL
              OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
              OR CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%') )
        AND NOT EXISTS (
              SELECT 1 FROM Attachment at
              WHERE at.targetType = 'SINMUNGO'
                AND at.targetId = s.smgId
        )
      """)
  Page<SinmungoListDto> adminSearchReceivedWithoutAttachmentByCreatedAtAsc(
      @Param("q") String q, Pageable pageable);
}
