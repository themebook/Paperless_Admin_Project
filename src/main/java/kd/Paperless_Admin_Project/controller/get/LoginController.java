package kd.Paperless_Admin_Project.controller.get;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
  
  @GetMapping("/admin/login")
  public String adminLogin() {
    return "login/login";
  }

}
