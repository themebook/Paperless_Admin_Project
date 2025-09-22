package kd.Paperless_Admin_Project.controller.get;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
  
  @GetMapping("/admin/login")
  public String adminLogin() {
    return "/login/login";
  }

  @PostMapping("/admin/login")
  public String adminLogin(@RequestParam String username,
                           @RequestParam String password) {
    
    System.out.println(username);
    System.out.println(password);

    return "redirect:/admin/dashboard";
  }

}
