package kd.Paperless_Admin_Project.controller.get;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SinmungoController {

  @GetMapping("/admin/sinmungo_list")
  public String adminSinmungoList() {
    return "/sinmungos/sinmungo_list";
  }

  @GetMapping("/admin/sinmungo_list/{admin_id}")
  public String adminSinmungoMyList(@PathVariable String admin_id) {
    return "/sinmungos/sinmungo_my";
  }

  @GetMapping("/admin/sinmungo_detail/{id}")
  public String adminSinmungoDetail(@PathVariable String id) {
    return "/sinmungos/sinmungo_detail";
  }

}
