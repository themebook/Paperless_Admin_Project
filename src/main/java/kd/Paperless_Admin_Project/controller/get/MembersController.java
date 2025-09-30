package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.entity.user.User;
import kd.Paperless_Admin_Project.repository.user.UserRepository;
import kd.Paperless_Admin_Project.dto.user.UserDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class MembersController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @GetMapping
  public String members(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "createdAt") String sort,
      @RequestParam(defaultValue = "desc") String dir,
      Model model) {

    String kw = (q == null || q.isBlank()) ? null : q.trim();
    String st = (status == null || status.isBlank()) ? null : status.trim();

    Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    String sortProp = switch (sort) {
      case "loginId" -> "loginId";
      case "userName" -> "userName";
      default -> "createdAt";
    };

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(direction, sortProp));

    Page<User> pageResult = userRepository.search(kw, st, pageable);
    Page<UserDto> dtoPage = pageResult.map(UserDto::from);

    long total = dtoPage.getTotalElements();
    int totalPages = dtoPage.getTotalPages();

    int blockSize = 10;
    int start = ((page - 1) / blockSize) * blockSize + 1;
    int end = Math.min(start + blockSize - 1, Math.max(totalPages, 1));

    long kpiTotal = userRepository.count();
    long kpiActive = userRepository.countByStatus("ACTIVE");
    long kpiDisabled = userRepository.countByStatus("DISABLED");

    model.addAttribute("items", dtoPage.getContent());
    model.addAttribute("totalCount", total);

    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("startPage", start);
    model.addAttribute("endPage", end);

    model.addAttribute("q", kw == null ? "" : kw);
    model.addAttribute("status", st == null ? "" : st);

    model.addAttribute("sort", sortProp);
    model.addAttribute("dir", direction.isAscending() ? "asc" : "desc");

    model.addAttribute("kpiTotal", kpiTotal);
    model.addAttribute("kpiActive", kpiActive);
    model.addAttribute("kpiDisabled", kpiDisabled);

    return "/members/members";
  }

  @PostMapping(consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<?> create(@RequestBody CreateReq req) {
    if (req.getLoginId() == null || req.getLoginId().isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("message", "로그인ID를 입력하세요."));
    }
    if (userRepository.existsByLoginId(req.getLoginId().trim())) {
      return ResponseEntity.badRequest().body(Map.of("message", "이미 존재하는 로그인ID입니다."));
    }

    final String RAW_TEMP = "qwer1234";
    String hash = passwordEncoder.encode(RAW_TEMP);

    User u = User.builder()
        .loginId(req.getLoginId().trim())
        .passwordHash(hash)
        .userName(req.getUserName() == null ? "" : req.getUserName().trim())
        .email(req.getEmail() == null ? null : req.getEmail().trim())
        .phoneNum(req.getPhoneNum() == null ? null : req.getPhoneNum().trim())
        .postcode(req.getPostcode())
        .addr1(req.getAddr1())
        .addr2(req.getAddr2())
        .status(req.getStatus() == null || req.getStatus().isBlank() ? "ACTIVE" : req.getStatus().trim())
        .updatedAt(LocalDateTime.now())
        .build();

    userRepository.save(u);

    return ResponseEntity.ok(Map.of(
        "message", "생성되었습니다.",
        "userId", u.getUserId(),
        "tempPassword", RAW_TEMP));
  }

  @PostMapping(value = "/{id}/update", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UpdateReq req) {
    User u = userRepository.findById(id)
        .orElse(null);
    if (u == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "대상 회원을 찾을 수 없습니다."));
    }

    if (req.getStatus() != null && !req.getStatus().isBlank())
      u.setStatus(req.getStatus().trim());
    if (req.getUserName() != null)
      u.setUserName(req.getUserName().trim());
    if (req.getEmail() != null)
      u.setEmail(req.getEmail().trim());
    if (req.getPhoneNum() != null)
      u.setPhoneNum(req.getPhoneNum().trim());
    if (req.getPostcode() != null)
      u.setPostcode(req.getPostcode());
    if (req.getAddr1() != null)
      u.setAddr1(req.getAddr1());
    if (req.getAddr2() != null)
      u.setAddr2(req.getAddr2());

    u.setUpdatedAt(LocalDateTime.now());
    userRepository.save(u);

    return ResponseEntity.ok(Map.of("message", "저장되었습니다."));
  }

  @PostMapping(value = "/{id}/reset-password", produces = "application/json")
  @ResponseBody
  public ResponseEntity<?> resetPassword(@PathVariable("id") Long id) {
    User u = userRepository.findById(id).orElse(null);
    if (u == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "대상 회원을 찾을 수 없습니다."));
    }
    final String RAW_TEMP = "qwer1234";
    u.setPasswordHash(passwordEncoder.encode(RAW_TEMP));
    u.setUpdatedAt(LocalDateTime.now());
    userRepository.save(u);

    return ResponseEntity.ok(Map.of(
        "message", "임시 비밀번호가 발급되었습니다.",
        "tempPassword", RAW_TEMP));
  }

  @PostMapping(value = "/{id}/delete", produces = "application/json")
  @ResponseBody
  public ResponseEntity<?> delete(@PathVariable("id") Long id) {
    User u = userRepository.findById(id).orElse(null);
    if (u == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "대상 회원을 찾을 수 없습니다."));
    }
    try {
      userRepository.delete(u);
      return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("message", "삭제할 수 없습니다. 관련 데이터가 존재할 수 있습니다."));
    }
  }

  @Data
  public static class CreateReq {
    private String loginId;
    private String userName;
    private String email;
    private String phoneNum;
    private String postcode;
    private String addr1;
    private String addr2;
    private String status;
  }

  @Data
  public static class UpdateReq {
    private String status;
    private String userName;
    private String email;
    private String phoneNum;
    private String postcode;
    private String addr1;
    private String addr2;
  }
}
