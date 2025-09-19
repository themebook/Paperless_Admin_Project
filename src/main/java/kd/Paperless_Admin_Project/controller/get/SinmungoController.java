package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.dto.SinmungoDetailDto;
import kd.Paperless_Admin_Project.dto.SinmungoListDto;
import kd.Paperless_Admin_Project.entity.Sinmungo;
import kd.Paperless_Admin_Project.repository.SinmungoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SinmungoController {

  private final SinmungoRepository sinmungoRepository;

  @GetMapping("/admin/sinmungo_list")
  public String adminSinmungoList(@SessionAttribute(name = "adminId", required = false) Long adminId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      Model model) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "smgId"));

    String kw = (q == null || q.isBlank()) ? null : q.trim();
    String st = (status == null || status.isBlank()) ? null : status.trim();

    Page<Sinmungo> result = sinmungoRepository.adminSearch(kw, st, pageable);
    Page<SinmungoListDto> dtoPage = result.map(SinmungoListDto::fromEntity);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());
    model.addAttribute("q", kw == null ? "" : kw);
    model.addAttribute("status", st == null ? "" : st);

    model.addAttribute("adminId", adminId);
    return "/sinmungo/sinmungo_list";
  }

  @GetMapping("/admin/sinmungo_list/{adminId}")
  public String adminSinmungoAssigned(@PathVariable Long adminId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String due,
      Model model) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "smgId"));

    String kw = (q == null || q.isBlank()) ? null : q.trim();
    String st = (status == null || status.isBlank()) ? null : status.trim();
    String dueFilter = (due == null || due.isBlank()) ? null : due.trim();

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime soonUntil = now.plusHours(48);

    Page<Sinmungo> result = sinmungoRepository.adminAssignedSearch(
        adminId, kw, st, dueFilter, now, soonUntil, pageable);
    Page<SinmungoListDto> dtoPage = result.map(SinmungoListDto::fromEntity);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());

    model.addAttribute("q", kw == null ? "" : kw);
    model.addAttribute("status", st == null ? "" : st);
    model.addAttribute("due", dueFilter == null ? "" : dueFilter);
    model.addAttribute("adminId", adminId);

    return "/sinmungo/sinmungo_my";
  }

  @GetMapping("/admin/sinmungo_detail/{id}")
  public String detail(@PathVariable Long id, Model model) {
    Sinmungo entity = sinmungoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("민원 없음: " + id));

    SinmungoDetailDto dto = SinmungoDetailDto.fromEntity(entity);
    model.addAttribute("dto", dto);

    // TODO: DB 연결하기
    List<AssigneeOption> adminOptions = List.of(
        new AssigneeOption(23L, "김담당"),
        new AssigneeOption(24L, "박담당"),
        new AssigneeOption(25L, "이담당"),
        new AssigneeOption(26L, "최담당"));
    model.addAttribute("adminOptions", adminOptions);

    return "/sinmungo/sinmungo_detail";
  }

  @PostMapping("/admin/sinmungo_detail/{id}/assign")
  public String assign(@PathVariable Long id,
      @RequestParam("adminId") Long adminId,
      RedirectAttributes ra) {

    Sinmungo entity = sinmungoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("민원 없음: " + id));

    entity.setAdminId(adminId);
    sinmungoRepository.save(entity);

    ra.addFlashAttribute("msg", "담당자가 변경되었습니다.");
    return "redirect:/admin/sinmungo_detail/" + id;
  }

  @PostMapping("/admin/sinmungo_detail/{id}/status")
  @Transactional
  public String changeStatus(@PathVariable Long id,
      @RequestParam("action") String action,
      @RequestParam(value = "rejectReason", required = false) String rejectReason,
      RedirectAttributes ra) {
    Sinmungo e = sinmungoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("민원 없음: " + id));

    switch (action) {
      case "approve" -> {
        e.setStatus("완료");
        e.setRejectReason(null);
        ra.addFlashAttribute("msg", "승인(완료) 처리되었습니다.");
      }
      case "reject" -> {
        if (rejectReason == null || rejectReason.isBlank()) {
          throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }
        e.setStatus("반려");
        e.setRejectReason(rejectReason.trim());
        ra.addFlashAttribute("msg", "반려 처리되었습니다.");
      }
      case "delete" -> {
        e.setStatus("삭제");
        e.setRejectReason(null);
        ra.addFlashAttribute("msg", "삭제 처리되었습니다.");
      }
      case "receive" -> {
        e.setStatus("접수");
        e.setRejectReason(null);
        ra.addFlashAttribute("msg", "접수 상태로 변경되었습니다.");
      }
      default -> throw new IllegalArgumentException("알 수 없는 action: " + action);
    }

    return "redirect:/admin/sinmungo_detail/" + id;
  }

  @Getter
  @AllArgsConstructor
  public static class AssigneeOption {
    private Long id;
    private String name;
  }
}
