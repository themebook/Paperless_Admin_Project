package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.dto.document.PaperlessDocListDto;
import kd.Paperless_Admin_Project.repository.document.PaperlessDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DocumentController {

  private final PaperlessDocRepository paperlessDocRepository;

  @GetMapping("/admin/document_list")
  public String adminDocumentList(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String docType,
      Model model) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "submittedAt"));
    String st = (status == null || status.isBlank()) ? null : status.trim();
    String dt = (docType == null || docType.isBlank()) ? null : docType.trim();

    Page<PaperlessDocListDto> dtoPage = paperlessDocRepository.adminSearchWithUserName(st, dt, pageable);

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", dtoPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", dtoPage.getTotalPages());

    model.addAttribute("status", st == null ? "" : st);
    model.addAttribute("docType", dt == null ? "" : dt);

    return "document/document_list";
  }
}
