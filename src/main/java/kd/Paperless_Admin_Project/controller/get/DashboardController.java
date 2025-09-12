package kd.Paperless_Admin_Project.controller.get;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
  
  @GetMapping("/admin/dashboard")
  public String dashboard() {
    return "/dashboard/dashboard";
  }

}
