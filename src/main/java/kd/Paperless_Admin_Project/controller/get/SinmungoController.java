package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.dto.sinmungo.SinmungoDetailDto;
import kd.Paperless_Admin_Project.dto.sinmungo.SinmungoListDto;
import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.entity.file.Attachment;
import kd.Paperless_Admin_Project.entity.sinmungo.Sinmungo;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import kd.Paperless_Admin_Project.repository.file.AttachmentRepository;
import kd.Paperless_Admin_Project.repository.sinmungo.SinmungoRepository;
import kd.Paperless_Admin_Project.security.AdminUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SinmungoController {

  private final SinmungoRepository sinmungoRepository;
  private final AdminRepository adminRepository;
  private final AttachmentRepository attachmentRepository;

  private static final String ATTACH_TARGET = "SINMUNGO";

  @GetMapping("/admin/sinmungo_list")
  public String adminSinmungoList(
      Authentication auth,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      Model model) {

    Long me = null;
    if (auth != null && auth.isAuthenticated()) {
      Object p = auth.getPrincipal();
      if (p instanceof AdminUserDetails aud && aud.getAdmin() != null)
        me = aud.getAdmin().getAdminId();
      else if (p instanceof Admin a)
        me = a.getAdminId();
      else if (p instanceof UserDetails ud)
        me = adminRepository.findByLoginId(ud.getUsername()).map(Admin::getAdminId).orElse(null);
      else if (p instanceof String s && !"anonymousUser".equals(s))
        me = adminRepository.findByLoginId(s).map(Admin::getAdminId).orElse(null);
    }
    model.addAttribute("adminId", me);

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "smgId"));
    String kw = (q == null || q.isBlank()) ? null : q.trim();
    String st = (status == null || status.isBlank()) ? null : status.trim();

    Page<SinmungoListDto> dtoPage = sinmungoRepository.adminSearchWithName(kw, st, pageable);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());
    model.addAttribute("q", kw == null ? "" : kw);
    model.addAttribute("status", st == null ? "" : st);

    return "sinmungo/sinmungo_list";
  }

  @GetMapping("/admin/sinmungo_list/{adminId}")
  public String adminSinmungoAssigned(
      @PathVariable Long adminId,
      Authentication auth,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      Model model) {

    model.addAttribute("adminId", adminId);

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "smgId"));
    String kw = (q == null || q.isBlank()) ? null : q.trim();
    String st = (status == null || status.isBlank()) ? null : status.trim();

    Page<SinmungoListDto> dtoPage = sinmungoRepository.adminAssignedSearchWithName(adminId, kw, st, pageable);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());
    model.addAttribute("q", kw == null ? "" : kw);
    model.addAttribute("status", st == null ? "" : st);

    return "sinmungo/sinmungo_my";
  }

  @GetMapping("/admin/sinmungo_detail/{id}")
  public String detail(@PathVariable Long id, Model model) {
    SinmungoDetailDto dto = sinmungoRepository.findDetailWithNames(id);
    if (dto == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "민원을 찾을 수 없습니다.");
    }
    model.addAttribute("dto", dto);

    List<Attachment> files = attachmentRepository
        .findByTargetTypeAndTargetIdOrderByFileIdAsc(ATTACH_TARGET, id);
    model.addAttribute("files", files);

    return "sinmungo/sinmungo_detail";
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
        e.setStatus("승인");
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
      case "hold" -> {
        e.setStatus("보류");
        e.setRejectReason(null);
        ra.addFlashAttribute("msg", "보류 처리되었습니다.");
      }
      case "receive" -> {
        e.setStatus("접수");
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

  private Long currentAdminId(Authentication auth) {
    if (auth == null || !auth.isAuthenticated())
      return null;
    Object p = auth.getPrincipal();
    if (p instanceof AdminUserDetails aud && aud.getAdmin() != null)
      return aud.getAdmin().getAdminId();
    if (p instanceof Admin a)
      return a.getAdminId();
    if (p instanceof UserDetails ud)
      return adminRepository.findByLoginId(ud.getUsername()).map(Admin::getAdminId).orElse(null);
    if (p instanceof String s && !"anonymousUser".equals(s))
      return adminRepository.findByLoginId(s).map(Admin::getAdminId).orElse(null);
    return null;
  }

  private String formatPhone(String raw) {
    if (raw == null)
      return null;
    String d = raw.replaceAll("\\D+", "");
    if (d.startsWith("02")) {
      if (d.length() == 9)
        return d.replaceFirst("^(02)(\\d{3})(\\d{4})$", "$1-$2-$3");
      if (d.length() == 10)
        return d.replaceFirst("^(02)(\\d{4})(\\d{4})$", "$1-$2-$3");
    }
    if (d.length() == 10)
      return d.replaceFirst("^(\\d{3})(\\d{3})(\\d{4})$", "$1-$2-$3");
    if (d.length() == 11)
      return d.replaceFirst("^(\\d{3})(\\d{4})(\\d{4})$", "$1-$2-$3");
    return raw;
  }

  @GetMapping("/admin/rpa_sinmungo_list")
  public String adminRpaSinmungoList(
      Authentication auth,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      Model model) {

    Long me = null;
    if (auth != null && auth.isAuthenticated()) {
      Object p = auth.getPrincipal();
      if (p instanceof AdminUserDetails aud && aud.getAdmin() != null)
        me = aud.getAdmin().getAdminId();
      else if (p instanceof Admin a)
        me = a.getAdminId();
      else if (p instanceof UserDetails ud)
        me = adminRepository.findByLoginId(ud.getUsername()).map(Admin::getAdminId).orElse(null);
      else if (p instanceof String s && !"anonymousUser".equals(s))
        me = adminRepository.findByLoginId(s).map(Admin::getAdminId).orElse(null);
    }
    model.addAttribute("adminId", me);

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
    String kw = (q == null || q.isBlank()) ? null : q.trim();
    Page<SinmungoListDto> dtoPage = sinmungoRepository.adminSearchReceivedWithoutAttachmentByCreatedAtAsc(kw, pageable);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());
    model.addAttribute("q", kw == null ? "" : kw);
    model.addAttribute("status", "접수");
    return "rpa/rpa_sinmungo_list";
  }
}
