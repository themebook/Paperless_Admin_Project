package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.dto.document.PaperlessDocDetailDto;
import kd.Paperless_Admin_Project.dto.document.PaperlessDocListDto;
import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.entity.document.PaperlessDoc;
import kd.Paperless_Admin_Project.entity.file.Attachment;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import kd.Paperless_Admin_Project.repository.document.PaperlessDocRepository;
import kd.Paperless_Admin_Project.repository.file.AttachmentRepository;
import kd.Paperless_Admin_Project.security.AdminUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PaperlessDocController {

  private final PaperlessDocRepository docRepository;
  private final AdminRepository adminRepository;
  private final AttachmentRepository attachmentRepository;

  private static final String ATTACH_TARGET = "PAPERLESS_DOC";

  @GetMapping("admin/document_list")
  public String adminPaperlessList(
      Authentication auth,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String docType,
      Model model) {

    Long me = resolveAdminId(auth);
    model.addAttribute("adminId", me);

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "plId"));
    String st = (status == null || status.isBlank()) ? null : status.trim();
    String dt = (docType == null || docType.isBlank()) ? null : docType.trim();

    Page<PaperlessDocListDto> dtoPage = docRepository.adminSearchWithUserName(st, dt, pageable);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());
    model.addAttribute("status", st == null ? "" : st);
    model.addAttribute("docType", dt == null ? "" : dt);

    return "document/document_list";
  }

  @GetMapping("/admin/document_detail/{plId}")
  public String detail(@PathVariable Long plId, Model model) {
    PaperlessDocDetailDto dto = docRepository.findDetailWithNames(plId);
    if (dto == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "문서를 찾을 수 없습니다.");
    model.addAttribute("dto", dto);

    List<Attachment> files = attachmentRepository
        .findByTargetTypeAndTargetIdOrderByFileIdAsc(ATTACH_TARGET, plId);
    model.addAttribute("files", files);

    return "document/document_detail";
  }

  @PostMapping("/admin/paperless_doc_detail/{plId}/assign")
  public String assign(@PathVariable Long plId,
                       @RequestParam("adminId") Long adminId,
                       RedirectAttributes ra) {

    PaperlessDoc entity = docRepository.findById(plId)
        .orElseThrow(() -> new IllegalArgumentException("문서 없음: " + plId));

    entity.setAdminId(adminId);
    docRepository.save(entity);

    ra.addFlashAttribute("msg", "담당자가 변경되었습니다.");
    return "redirect:admin/paperless_doc_detail/" + plId;
  }

  @PostMapping("admin/paperless_doc_detail/{plId}/status")
  @Transactional
  public String changeStatus(@PathVariable Long plId,
                             @RequestParam("action") String action,
                             @RequestParam(value = "rejectReason", required = false) String rejectReason,
                             RedirectAttributes ra) {
    PaperlessDoc e = docRepository.findById(plId)
        .orElseThrow(() -> new IllegalArgumentException("문서 없음: " + plId));

    switch (action) {
  case "receive" -> { e.setStatus("WAITING"); }
  case "start"   -> { e.setStatus("IN_PROGRESS"); }
  case "complete"-> { e.setStatus("ANSWERED"); }
  case "reject"  -> {
    if (rejectReason == null || rejectReason.isBlank())
      throw new IllegalArgumentException("반려 사유는 필수입니다.");
    e.setStatus("REJECTED");
  }
  default -> throw new IllegalArgumentException("알 수 없는 action: " + action);
}
    return "redirect:documet/document_detail/" + plId;
  }

  private Long resolveAdminId(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) return null;
    Object p = auth.getPrincipal();
    if (p instanceof AdminUserDetails aud && aud.getAdmin() != null) return aud.getAdmin().getAdminId();
    if (p instanceof Admin a) return a.getAdminId();
    if (p instanceof UserDetails ud) return adminRepository.findByLoginId(ud.getUsername()).map(Admin::getAdminId).orElse(null);
    if (p instanceof String s && !"anonymousUser".equals(s)) return adminRepository.findByLoginId(s).map(Admin::getAdminId).orElse(null);
    return null;
  }

  @Getter @AllArgsConstructor
  public static class AssigneeOption { private Long id; private String name; }
}
