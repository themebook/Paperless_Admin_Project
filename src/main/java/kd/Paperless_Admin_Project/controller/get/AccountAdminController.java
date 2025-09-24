// src/main/java/kd/Paperless_Admin_Project/controller/get/AccountAdminController.java
package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/accounts")
@PreAuthorize("hasAnyRole('MANAGER','DIRECTOR','ADMIN')")
public class AccountAdminController {

  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  private static boolean canManage(int meRole, int targetRole) {
    return meRole > targetRole;
  }

  private static void assertManageable(int meRole, Admin target, Integer newRoleIfAny, Long meId) {
    if (target.getRoleCode() == null) {
      throw new AccessDeniedException("대상 계정의 권한 정보가 올바르지 않습니다.");
    }
    if (meId != null && meId.equals(target.getAdminId())) {
      throw new AccessDeniedException("자기 자신의 계정은 이 작업을 할 수 없습니다.");
    }
    if (!canManage(meRole, target.getRoleCode())) {
      throw new AccessDeniedException("본인과 같거나 높은 직급의 계정은 관리할 수 없습니다.");
    }
    if (newRoleIfAny != null && meRole <= newRoleIfAny) {
      throw new AccessDeniedException("본인과 같거나 높은 직급으로 변경할 수 없습니다.");
    }
  }

  @GetMapping
  @Transactional(readOnly = true)
  public String list(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer roleCode,
      @RequestParam(required = false) String status,
      Model model,
      @AuthenticationPrincipal UserDetails me) {

    Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.max(1, size));
    String keyword = (q == null || q.isBlank()) ? null : q.trim();
    String normStatus = normalizeStatus(status);

    Page<Admin> result = adminRepository.search(keyword, roleCode, normStatus, pageable);

    model.addAttribute("items", result.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", Math.max(1, result.getTotalPages()));
    model.addAttribute("totalCount", result.getTotalElements());
    model.addAttribute("q", q);
    model.addAttribute("roleCode", roleCode);
    model.addAttribute("status", status);
    model.addAttribute("adminExists", adminRepository.existsByRoleCode(10));

    Admin meAdmin = adminRepository.findByLoginId(me.getUsername())
        .orElseThrow(() -> new IllegalStateException("현재 사용자 정보를 찾을 수 없습니다."));
    model.addAttribute("meId", meAdmin.getAdminId());
    model.addAttribute("meRoleCode", meAdmin.getRoleCode());

    return "accounts/accounts";
  }

  @PostMapping(consumes = "application/json", produces = "application/json")
  @ResponseBody
  @Transactional
  public Map<String, Object> create(@RequestBody CreateReq req,
      @AuthenticationPrincipal UserDetails me) {
    if (req.loginId == null || req.loginId.isBlank()) {
      return Map.of("ok", false, "message", "로그인ID를 입력하세요.");
    }
    String loginId = req.loginId.trim();

    adminRepository.findByLoginId(loginId).ifPresent(a -> {
      throw new IllegalArgumentException("이미 존재하는 로그인ID입니다.");
    });

    int roleCode = Optional.ofNullable(req.roleCode).orElse(1);
    validateRole(roleCode);
    String normStatus = Optional.ofNullable(normalizeStatus(req.status)).orElse("ACTIVE");

    Admin meAdmin = adminRepository.findByLoginId(me.getUsername())
        .orElseThrow(() -> new IllegalStateException("현재 사용자 정보를 찾을 수 없습니다."));

    if (!canManage(meAdmin.getRoleCode(), roleCode)) {
      throw new AccessDeniedException("본인과 같거나 높은 직급의 계정은 생성할 수 없습니다.");
    }

    if (roleCode == 10 && adminRepository.existsByRoleCode(10)) {
      throw new IllegalStateException("최고관리자는 이미 존재합니다.");
    }

    String rawPw = (req.tempPassword == null || req.tempPassword.isBlank())
        ? randomPassword(10)
        : req.tempPassword.trim();

    Admin entity = Admin.builder()
        .loginId(loginId)
        .passwordHash(passwordEncoder.encode(rawPw))
        .adminName(Optional.ofNullable(req.adminName).orElse("이름없음"))
        .roleCode(roleCode)
        .email(emptyToNull(req.email))
        .phoneNum(emptyToNull(req.phoneNum))
        .status(normStatus)
        .build();

    entity = adminRepository.save(entity);
    return Map.of(
        "ok", true,
        "message", "계정이 생성되었습니다.",
        "adminId", entity.getAdminId(),
        "tempPassword", rawPw);
  }

  @PostMapping(path = "/{adminId}/update", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @Transactional
  public Map<String, Object> update(@PathVariable Long adminId,
      @RequestBody UpdateReq req,
      @AuthenticationPrincipal UserDetails me) {
    Admin meAdmin = adminRepository.findByLoginId(me.getUsername())
        .orElseThrow(() -> new IllegalStateException("현재 사용자 정보를 찾을 수 없습니다."));
    Admin target = adminRepository.findById(adminId)
        .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

    if (req.roleCode != null) {
      validateRole(req.roleCode);
      if (req.roleCode == 10 && !Objects.equals(target.getRoleCode(), 10) && adminRepository.existsByRoleCode(10)) {
        throw new IllegalStateException("최고관리자는 1명만 가능합니다.");
      }
      if (Objects.equals(target.getRoleCode(), 10) && req.roleCode != 10) {
        throw new IllegalStateException("최고관리자의 직급은 변경할 수 없습니다.");
      }
    }

    assertManageable(meAdmin.getRoleCode(), target, req.roleCode, meAdmin.getAdminId());

    if (req.roleCode != null) {
      target.setRoleCode(req.roleCode);
    }
    if (req.status != null && !req.status.isBlank()) {
      target.setStatus(normalizeStatus(req.status));
    }

    adminRepository.save(target);
    return Map.of("ok", true, "message", "저장되었습니다.");
  }

  @PostMapping(path = "/{adminId}/reset-password", produces = "application/json")
  @ResponseBody
  @Transactional
  public Map<String, Object> resetPassword(@PathVariable Long adminId,
      @AuthenticationPrincipal UserDetails me) {
    final String DEFAULT_RESET_PASSWORD = "1234";

    Admin meAdmin = adminRepository.findByLoginId(me.getUsername())
        .orElseThrow(() -> new IllegalStateException("현재 사용자 정보를 찾을 수 없습니다."));
    Admin target = adminRepository.findById(adminId)
        .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

    assertManageable(meAdmin.getRoleCode(), target, null, meAdmin.getAdminId());

    target.setPasswordHash(passwordEncoder.encode(DEFAULT_RESET_PASSWORD));
    adminRepository.save(target);

    return Map.of(
        "ok", true,
        "message", "임시 비밀번호가 1234로 초기화되었습니다.",
        "tempPassword", DEFAULT_RESET_PASSWORD);
  }

  @PostMapping(path = "/{adminId}/delete", produces = "application/json")
  @ResponseBody
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public Map<String, Object> delete(@PathVariable Long adminId,
      @AuthenticationPrincipal UserDetails me) {
    Admin meAdmin = adminRepository.findByLoginId(me.getUsername())
        .orElseThrow(() -> new IllegalStateException("현재 사용자 정보를 찾을 수 없습니다."));
    Admin target = adminRepository.findById(adminId)
        .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

    if (Objects.equals(target.getRoleCode(), 10)) {
      throw new IllegalStateException("최고관리자 계정은 삭제할 수 없습니다.");
    }

    assertManageable(meAdmin.getRoleCode(), target, null, meAdmin.getAdminId());

    adminRepository.delete(target);
    return Map.of("ok", true, "message", "계정이 삭제되었습니다.");
  }

  @Data
  public static class CreateReq {
    private String loginId;
    private String adminName;
    private Integer roleCode;
    private String email;
    private String phoneNum;
    private String status;
    private String tempPassword;
  }

  @Data
  public static class UpdateReq {
    private Integer roleCode;
    private String status;
  }

  private static final SecureRandom RND = new SecureRandom();
  private static final char[] PW_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*"
      .toCharArray();

  private static String randomPassword(int len) {
    char[] buf = new char[len];
    for (int i = 0; i < len; i++)
      buf[i] = PW_CHARS[RND.nextInt(PW_CHARS.length)];
    return new String(buf);
  }

  private static String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s.trim();
  }

  private static String normalizeStatus(String s) {
    if (s == null)
      return null;
    String u = s.trim().toUpperCase(Locale.ROOT);
    return switch (u) {
      case "ACTIVE" -> "ACTIVE";
      case "DISABLED" -> "DISABLED";
      default -> "ACTIVE";
    };
  }

  private static void validateRole(Integer roleCode) {
    if (roleCode == null)
      throw new IllegalArgumentException("직급(roleCode)은 필수입니다.");
    if (roleCode != 1 && roleCode != 2 && roleCode != 3 && roleCode != 10) {
      throw new IllegalArgumentException("직급(roleCode)은 1,2,3,10 중 하나여야 합니다.");
    }
  }
}
