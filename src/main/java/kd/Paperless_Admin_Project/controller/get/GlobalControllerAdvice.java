package kd.Paperless_Admin_Project.controller.get;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import kd.Paperless_Admin_Project.repository.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "kd.Paperless_Admin_Project.controller")
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final AdminRepository adminRepository;

    @ModelAttribute("loginAdmin")
    public Admin loginAdmin() {
        // TODO: 로그인 연동 시 세션이나 SecurityContext에서 loginId 가져오기
        return adminRepository.findById(1L).orElse(null);
    }
}