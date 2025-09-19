package kd.Paperless_Admin_Project.repository;

import kd.Paperless_Admin_Project.entity.Sinmungo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
}
