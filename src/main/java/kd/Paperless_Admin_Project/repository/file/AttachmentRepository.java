package kd.Paperless_Admin_Project.repository.file;

import kd.Paperless_Admin_Project.entity.file.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

  List<Attachment> findByTargetTypeAndTargetIdOrderByFileIdAsc(String targetType, Long targetId);

  long deleteByTargetTypeAndTargetId(String targetType, Long targetId);
} 