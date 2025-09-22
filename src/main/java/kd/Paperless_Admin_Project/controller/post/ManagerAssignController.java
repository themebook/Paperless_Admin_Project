package kd.Paperless_Admin_Project.controller.post;

import kd.Paperless_Admin_Project.dto.manager.AssignRequest;
import kd.Paperless_Admin_Project.dto.manager.BulkAssignRequest;
import kd.Paperless_Admin_Project.dto.manager.AssignResponse;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import kd.Paperless_Admin_Project.repository.sinmungo.SinmungoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/manager")
public class ManagerAssignController {

  private final SinmungoRepository sinmungoRepository;
  private final AdminRepository adminRepository;

  /** 단건 배정 */
  @PostMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<AssignResponse> assignOne(@Validated @RequestBody AssignRequest req) {
    if (!adminRepository.existsById(req.getAdminId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(AssignResponse.builder()
              .success(false).assignedCount(0)
              .message("존재하지 않는 관리자입니다: " + req.getAdminId())
              .build());
    }

    int updated = sinmungoRepository.assignOneIfUnassigned(req.getSmgId(), req.getAdminId());
    if (updated == 0) {
      return ResponseEntity.ok(
          AssignResponse.builder()
              .success(false)
              .assignedCount(0)
              .message("이미 배정된 건이거나 존재하지 않는 접수번호입니다.")
              .build());
    }

    return ResponseEntity.ok(
        AssignResponse.builder()
            .success(true)
            .assignedCount(updated)
            .message("배정 완료")
            .build());
  }

  /** 일괄 배정 */
  @PostMapping(value = "/assign/bulk", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<AssignResponse> assignBulk(@Validated @RequestBody BulkAssignRequest req) {
    if (!adminRepository.existsById(req.getAdminId())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(AssignResponse.builder()
              .success(false).assignedCount(0)
              .message("존재하지 않는 관리자입니다: " + req.getAdminId())
              .build());
    }

    int updated = sinmungoRepository.assignBulkIfUnassigned(req.getIds(), req.getAdminId());
    String msg = (updated == req.getIds().size())
        ? "일괄 배정 완료"
        : "일부만 배정되었습니다. (요청 " + req.getIds().size() + "건 중 " + updated + "건)";

    return ResponseEntity.ok(
        AssignResponse.builder()
            .success(updated > 0)
            .assignedCount(updated)
            .message(msg)
            .build());
  }
}
