package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import kd.Paperless_Admin_Project.security.AdminUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice(basePackages = "kd.Paperless_Admin_Project.controller")
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final AdminRepository adminRepository;

    @ModelAttribute
    public void injectLoginAdmin(Model model, Authentication auth) {
        Admin admin = null;

        if (auth != null && auth.isAuthenticated()) {
            Object p = auth.getPrincipal();
            if (p instanceof AdminUserDetails aud) {
                admin = aud.getAdmin();
            } else if (p instanceof Admin a) {
                admin = a;
            } else if (p instanceof UserDetails ud) {
                admin = adminRepository.findByLoginId(ud.getUsername()).orElse(null);
            } else if (p instanceof String s && !"anonymousUser".equals(s)) {
                admin = adminRepository.findByLoginId(s).orElse(null);
            }
        }

        if (admin != null) {
            model.addAttribute("loginAdmin", admin);
            model.addAttribute("adminId", admin.getAdminId());
            model.addAttribute("adminName", admin.getAdminName());
        }
    }
}
