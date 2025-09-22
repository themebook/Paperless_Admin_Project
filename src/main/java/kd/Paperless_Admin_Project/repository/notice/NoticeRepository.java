package kd.Paperless_Admin_Project.repository.notice;

import kd.Paperless_Admin_Project.entity.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  // 공지사항 목록
  @Query("""
        SELECT n
        FROM Notice n
        WHERE n.targetAudience = 'ADMIN'
          AND (
                :q IS NULL
             OR LOWER(n.title) LIKE CONCAT('%', LOWER(cast(:q as string)), '%')
             OR function(
                  'INSTR',
                  LOWER(function('DBMS_LOB.SUBSTR', n.content, 4000, 1)),
                  LOWER(cast(:q as string))
                ) > 0
          )
        ORDER BY n.isPinned DESC, n.createdAt DESC
      """)
  Page<Notice> searchAdminNotices(@Param("q") String q, Pageable pageable);

  // 하나찾기
  @Query("""
        SELECT n FROM Notice n
        WHERE n.noticeId = :id AND n.targetAudience = 'ADMIN'
      """)
  Optional<Notice> findAdminById(@Param("id") Long id);

  // 이전글
  @Query("""
        SELECT n FROM Notice n
        WHERE n.targetAudience = 'ADMIN' AND n.noticeId < :id
        ORDER BY n.noticeId DESC
      """)
  List<Notice> findPrevAdmin(@Param("id") Long id, Pageable pageable);

  // 다음글
  @Query("""
        SELECT n FROM Notice n
        WHERE n.targetAudience = 'ADMIN' AND n.noticeId > :id
        ORDER BY n.noticeId ASC
      """)
  List<Notice> findNextAdmin(@Param("id") Long id, Pageable pageable);

  // 조회수
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
        UPDATE Notice n
        SET n.viewCount = COALESCE(n.viewCount, 0) + 1
        WHERE n.noticeId = :id AND n.targetAudience = 'ADMIN'
      """)
  int increaseViewCount(@Param("id") Long id);
}
