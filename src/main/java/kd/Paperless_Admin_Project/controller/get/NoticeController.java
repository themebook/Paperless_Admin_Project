package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.dto.notice.NoticeDetailDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeListDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeUpdateDto;
import kd.Paperless_Admin_Project.dto.notice.NoticeWriteDto;
import kd.Paperless_Admin_Project.entity.notice.Notice;
import kd.Paperless_Admin_Project.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeRepository noticeRepository;

  @GetMapping("/admin/notice")
  public String adminNotice(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      Model model) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
    String keyword = (q == null || q.isBlank()) ? null : q;

    Page<Notice> result = noticeRepository.searchAdminNotices(keyword, pageable);

    model.addAttribute("items", result.map(NoticeListDto::fromEntity).getContent());
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

    Notice n = noticeRepository.findAdminById(id)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. id=" + id));

    model.addAttribute("dto", NoticeDetailDto.fromEntity(n));

    Pageable one = PageRequest.of(0, 1);
    List<Notice> prev = noticeRepository.findPrevAdmin(id, one);
    List<Notice> next = noticeRepository.findNextAdmin(id, one);

    model.addAttribute("prev", prev.isEmpty() ? null : NoticeListDto.fromEntity(prev.get(0)));
    model.addAttribute("next", next.isEmpty() ? null : NoticeListDto.fromEntity(next.get(0)));

    return "/notice/notice_detail";
  }

  @GetMapping("/admin/notice_write")
  public String adminNoticeWrite(Model model) {
    if (!model.containsAttribute("form")) {
      model.addAttribute("form", new NoticeWriteDto());
    }
    return "/notice/notice_write";
  }

  @PostMapping("/admin/notice_write")
  public String adminNoticeWriteSubmit(
      @Valid @ModelAttribute("form") NoticeWriteDto form,
      BindingResult binding,
      RedirectAttributes ra) {
    if (binding.hasErrors()) {
      ra.addFlashAttribute("org.springframework.validation.BindingResult.form", binding);
      ra.addFlashAttribute("form", form);
      return "redirect:/admin/notice_write";
    }

    // TODO: 실제 로그인 사용자 ID를 주입하세요.
    Long adminId = 1L;

    noticeRepository.save(form.toEntity(adminId));
    return "redirect:/admin/notice";
  }

  @GetMapping("/admin/notice_edit/{id}")
  public String adminNoticeEdit(@PathVariable Long id, Model model) {
    Notice n = noticeRepository.findAdminById(id)
        .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. id=" + id));

    if (!model.containsAttribute("form")) {
      model.addAttribute("form", NoticeUpdateDto.fromEntity(n));
    }
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
}

