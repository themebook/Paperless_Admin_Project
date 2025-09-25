package kd.Paperless_Admin_Project.controller.post;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class PasswordChangeController {

  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping(path = "/account/change-password", consumes = "application/json", produces = "application/json")
  @PreAuthorize("isAuthenticated()")
  @Transactional
  public ResponseEntity<Map<String, Object>> changePassword(@RequestBody PwChangeReq req,
                                                            Principal principal) {
    if (req.curPw == null || req.curPw.isBlank()
        || req.newPw == null || req.newPw.isBlank()
        || req.newPw2 == null || req.newPw2.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "모든 항목을 입력하세요."));
    }
    if (!req.newPw.equals(req.newPw2)) {
      return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "새 비밀번호가 일치하지 않습니다."));
    }

    if (req.newPw.length() < 4) {
      return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "새 비밀번호는 4자 이상이어야 합니다."));
    }

    String loginId = principal.getName();
    Admin me = adminRepository.findByLoginId(loginId)
        .orElseThrow(() -> new NoSuchElementException("로그인 정보를 찾을 수 없습니다."));

    if (!passwordEncoder.matches(req.curPw, me.getPasswordHash())) {
      return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "현재 비밀번호가 올바르지 않습니다."));
    }
    if (passwordEncoder.matches(req.newPw, me.getPasswordHash())) {
      return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "현재 비밀번호와 동일합니다."));
    }

    me.setPasswordHash(passwordEncoder.encode(req.newPw));
    adminRepository.save(me);

    return ResponseEntity.ok(Map.of("ok", true, "message", "비밀번호가 변경되었습니다."));
  }

  @Data
  public static class PwChangeReq {
    private String curPw;
    private String newPw;
    private String newPw2;
  }
}
