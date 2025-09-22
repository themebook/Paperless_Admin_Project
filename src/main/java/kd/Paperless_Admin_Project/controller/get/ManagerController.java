package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.dto.manager.SinmungoManagerDto;
import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import kd.Paperless_Admin_Project.repository.sinmungo.SinmungoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class ManagerController {

  private final SinmungoRepository sinmungoRepository;
  private final AdminRepository adminRepository;

  @GetMapping("/admin/manager")
  public String managerPage(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      Model model) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

    String keyword = (q == null || q.isBlank()) ? null : q.trim();

    Page<SinmungoManagerDto> items = sinmungoRepository
        .searchUnassigned(keyword, pageable)
        .map(SinmungoManagerDto::fromEntity);

    long unassignedCount = sinmungoRepository.countUnassigned();

    List<Admin> adminOptions;
    try {
      adminOptions = adminRepository.findAll();
      if (adminOptions == null || adminOptions.isEmpty()) {
        adminOptions = adminRepository.findAll();
      }
    } catch (Exception e) {
      adminOptions = adminRepository.findAll();
    }

    model.addAttribute("items", items.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", items.getTotalPages() == 0 ? 1 : items.getTotalPages());
    model.addAttribute("totalCount", items.getTotalElements());

    model.addAttribute("unassignedCount", unassignedCount);
    model.addAttribute("q", q);

    model.addAttribute("adminOptions", adminOptions);

    return "/manager/manager";
  }
}
