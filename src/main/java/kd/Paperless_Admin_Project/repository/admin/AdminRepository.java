package kd.Paperless_Admin_Project.repository.admin;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
  Optional<Admin> findByLoginId(String loginId);
}
