package kd.Paperless_Admin_Project.controller.get;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NoticeController {
  
  @GetMapping("/admin/notice")
  public String adminNotice() {
    return "/notices/notice";
  }

  @GetMapping("/admin/notice_detail/{id}")
  public String adminNoticeDetail(@PathVariable String id) {
    return "/notices/notice_detail";
  }

}
