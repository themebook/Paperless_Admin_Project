package kd.Paperless_Admin_Project.controller.get;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kd.Paperless_Admin_Project.dto.notice.NoticeDetailDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeListDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeUpdateDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeWriteDto;
import kd.Paperless_Admin_Project.entity.file.Attachment;
import kd.Paperless_Admin_Project.entity.notice.Notice;
import kd.Paperless_Admin_Project.repository.file.AttachmentRepository;
import kd.Paperless_Admin_Project.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeRepository noticeRepository;

  private final MinioClient minioClient;
  private final AttachmentRepository attachmentRepository;

  @Value("${storage.minio.bucket}")
  private String bucket;

  @GetMapping("/admin/notice")
  public String adminNotice(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      Model model) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
    String keyword = (q == null || q.isBlank()) ? null : q;

    Page<NoticeListDto> result = noticeRepository.searchAdminNoticesWithName(keyword, pageable);

    model.addAttribute("items", result.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", result.getTotalPages());
    model.addAttribute("totalCount", result.getTotalElements());
    model.addAttribute("q", q);

    return "/notice/notice_list";
  }

  @Transactional
  @GetMapping("/admin/notice_detail/{id}")
  public String adminNoticeDetail(@PathVariable Long id, Model model) {
    noticeRepository.increaseViewCount(id);

    NoticeDetailDto dto = noticeRepository.findAdminByIdWithName(id)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. id=" + id));
    model.addAttribute("dto", dto);

    List<Attachment> files = attachmentRepository
        .findByTargetTypeAndTargetIdOrderByFileIdAsc("NOTICE", id);
    model.addAttribute("files", files);

    Pageable one = PageRequest.of(0, 1);
    List<NoticeListDto> prev = noticeRepository.findPrevAdminWithName(id, one);
    List<NoticeListDto> next = noticeRepository.findNextAdminWithName(id, one);
    model.addAttribute("prev", prev.isEmpty() ? null : prev.get(0));
    model.addAttribute("next", next.isEmpty() ? null : next.get(0));

    return "/notice/notice_detail";
  }

  @GetMapping("/admin/notice_write")
  public String adminNoticeWrite(Model model) {
    if (!model.containsAttribute("form")) {
      NoticeWriteDto form = new NoticeWriteDto();
      form.setIsPinned("N");
      form.setTargetAudience("ADMIN");
      model.addAttribute("form", form);
    }
    return "/notice/notice_write";
  }

  @PostMapping(value = "/admin/notice_write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Transactional
  public String adminNoticeWriteSubmit(
      @AuthenticationPrincipal(expression = "admin.adminId") Long adminId,
      @Valid @ModelAttribute("form") NoticeWriteDto form,
      BindingResult binding,
      RedirectAttributes ra) {

    if (binding.hasErrors()) {
      ra.addFlashAttribute("org.springframework.validation.BindingResult.form", binding);
      ra.addFlashAttribute("form", form);
      return "redirect:/admin/notice_write";
    }

    Notice saved = noticeRepository.save(form.toEntity(adminId));
    Long noticeId = saved.getNoticeId();

    if (form.getFiles() != null) {
      for (var file : form.getFiles()) {
        if (file == null || file.isEmpty())
          continue;

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        String safeName = original.isBlank() ? "file" : original;
        String contentType = (file.getContentType() != null) ? file.getContentType() : "application/octet-stream";

        String objectKey = buildObjectKey(safeName);

        try (InputStream in = file.getInputStream()) {
          minioClient.putObject(
              PutObjectArgs.builder()
                  .bucket(bucket)
                  .object(objectKey)
                  .contentType(contentType)
                  .stream(in, file.getSize(), -1)
                  .build());
        } catch (Exception e) {
          throw new RuntimeException("파일 업로드 실패: " + safeName, e);
        }

        attachmentRepository.save(
            Attachment.builder()
                .targetType("NOTICE")
                .targetId(noticeId)
                .fileUri(objectKey)
                .fileName(safeName)
                .mimeType(contentType)
                .fileSize(file.getSize())
                .build());
      }
    }

    ra.addFlashAttribute("msg", "등록되었습니다.");
    return "redirect:/admin/notice_detail/" + noticeId;
  }

  @Transactional(readOnly = true)
  @GetMapping("/admin/notice_edit/{id}")
  public String adminNoticeEdit(@PathVariable Long id, Model model) {
    Notice n = noticeRepository.findAdminById(id)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. id=" + id));
    if (!model.containsAttribute("form")) {
      model.addAttribute("form", NoticeUpdateDto.fromEntity(n));
    }
    List<Attachment> files = attachmentRepository
        .findByTargetTypeAndTargetIdOrderByFileIdAsc("NOTICE", id);
    model.addAttribute("files", files);

    return "/notice/notice_edit";
  }

  @PostMapping("/admin/notice_edit/{id}")
  @Transactional
  public String adminNoticeEditSubmit(@PathVariable Long id,
      @Valid @ModelAttribute("form") NoticeUpdateDto form,
      BindingResult binding,
      RedirectAttributes ra) {
    if (binding.hasErrors()) {
      ra.addFlashAttribute("org.springframework.validation.BindingResult.form", binding);
      ra.addFlashAttribute("form", form);
      return "redirect:/admin/notice_edit/" + id;
    }

    Notice n = noticeRepository.findAdminById(id)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. id=" + id));

    form.setNoticeId(id);
    form.applyToEntity(n);

    ra.addFlashAttribute("msg", "수정되었습니다.");
    return "redirect:/admin/notice_detail/" + id;
  }

  /** 객체 키 규칙: yyyy/MM/dd/uuid__원본파일명 */
  private static String buildObjectKey(String filename) {
    LocalDate d = LocalDate.now();
    return "%04d/%02d/%02d/%s__%s".formatted(
        d.getYear(), d.getMonthValue(), d.getDayOfMonth(), UUID.randomUUID(), filename);
  }
}
