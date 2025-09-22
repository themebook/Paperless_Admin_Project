package kd.Paperless_Admin_Project.repository.sinmungo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;

import java.time.LocalDateTime;
import java.util.List;

public interface SinmungoRepository extends JpaRepository<Sinmungo, Long> {

  // 전체 민원 검색
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

  // 내 배정 민원 검색
  @Query("""
        SELECT s FROM Sinmungo s
        WHERE s.adminId = :adminId
          AND (:status IS NULL OR s.status = :status)
          AND (
                :q IS NULL
             OR  LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
             OR  CAST(s.smgId AS string) LIKE CONCAT('%', :q, '%')
          )
          AND (
               :due IS NULL
            OR (:due = 'soon' AND s.answerDate IS NOT NULL AND s.answerDate BETWEEN :now AND :soonUntil)
            OR (:due = 'over' AND s.answerDate IS NOT NULL AND s.answerDate < :now)
          )
        ORDER BY s.smgId DESC
      """)
  Page<Sinmungo> adminAssignedSearch(@Param("adminId") Long adminId,
      @Param("q") String q,
      @Param("status") String status,
      @Param("due") String due,
      @Param("now") LocalDateTime now,
      @Param("soonUntil") LocalDateTime soonUntil,
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

  // 관리쪽... 미배정 신문고 목록
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

  // 관리쪽... 미배정 신문고 count
  @Query("SELECT COUNT(s) FROM Sinmungo s WHERE s.adminId IS NULL")
  long countUnassigned();

  // 관리쪽 미배정 신문고 단일 배정
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE Sinmungo s
      SET s.adminId = :adminId
       WHERE s.smgId = :smgId
       AND s.adminId IS NULL
       """)
  int assignOneIfUnassigned(@Param("smgId") Long smgId, @Param("adminId") Long adminId);

  // 관리쪽 미배정 신문고 일괄 배정
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE Sinmungo s
      SET s.adminId = :adminId
      WHERE s.smgId IN :ids
      AND s.adminId IS NULL
      """)
  int assignBulkIfUnassigned(@Param("ids") List<Long> ids, @Param("adminId") Long adminId);

  long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  long countByAdminId(Long adminId);
}
