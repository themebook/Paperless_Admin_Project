package kd.Paperless_Admin_Project.controller.get;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerController {
  
  @GetMapping("/admin/manager")
  public String adminManager() {
    return "/manager/manager";
  }

}
