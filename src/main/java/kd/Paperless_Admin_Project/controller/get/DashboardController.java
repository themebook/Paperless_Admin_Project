package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.repository.notice.NoticeRepository;
import kd.Paperless_Admin_Project.repository.sinmungo.SinmungoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

  private final SinmungoRepository sinmungoRepository;
  private final NoticeRepository noticeRepository;

  @GetMapping("/admin/dashboard")
  public String dashboard(
      @AuthenticationPrincipal(expression = "admin.adminId") Long adminId,
      Model model) {

    long totalCount = sinmungoRepository.count();

    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
    long todayCount = sinmungoRepository.countByCreatedAtBetween(startOfDay, endOfDay);

    long assignedCount = (adminId != null) ? sinmungoRepository.countByAdminId(adminId) : 0L;

    model.addAttribute("kpiTotal", totalCount);
    model.addAttribute("kpiTodayReceived", todayCount);
    model.addAttribute("kpiAssigned", assignedCount);

    var topNotices = noticeRepository
        .searchAdminNoticesWithName(null, PageRequest.of(0, 5))
        .getContent();
    model.addAttribute("noticesTop", topNotices);

    LocalDate startDate = today.minusDays(6);
    LocalDateTime start = startDate.atStartOfDay();
    var rows = sinmungoRepository.countByDaySince(start);

    Map<String, Long> map = new HashMap<>();
    for (Object[] r : rows) {
      String dayKey = (String) r[0];
      Long cnt = (r[1] instanceof Number) ? ((Number) r[1]).longValue() : 0L;
      map.put(dayKey, cnt);
    }

    List<Map<String, Object>> last7Days = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      LocalDate d = startDate.plusDays(i);
      String key = d.toString();
      String label = String.format("%02d-%02d", d.getMonthValue(), d.getDayOfMonth());
      last7Days.add(Map.of("label", label, "count", map.getOrDefault(key, 0L)));
    }
    model.addAttribute("recent7", last7Days);

    var statusRows = sinmungoRepository.countByStatus();
    Map<String, Long> statusMap = new HashMap<>();
    for (Object[] r : statusRows) {
      String s = (String) r[0];
      long c = ((Number) r[1]).longValue();
      statusMap.merge(s, c, Long::sum);
    }

    List<String> orderedLabels = List.of("접수", "승인", "보류", "반려");
    List<Long> orderedCounts = new ArrayList<>(orderedLabels.size());
    for (String k : orderedLabels) {
      orderedCounts.add(statusMap.getOrDefault(k, 0L));
    }
    model.addAttribute("statusLabels", orderedLabels);
    model.addAttribute("statusCounts", orderedCounts);

    return "/dashboard/dashboard";
  }
}
