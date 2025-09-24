package kd.Paperless_Admin_Project.security;

import kd.Paperless_Admin_Project.entity.admin.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Collection;

public class AdminUserDetails implements UserDetails {
  private final Admin admin;

  public AdminUserDetails(Admin admin) {
    this.admin = admin;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String role = switch (admin.getRoleCode() == null ? 0 : admin.getRoleCode()) {
      case 10 -> "ROLE_ADMIN";
      case 3 -> "ROLE_DIRECTOR";
      case 2 -> "ROLE_MANAGER";
      case 1 -> "ROLE_EMPLOYEE";
      default -> "ROLE_EMPLOYEE";
    };
    return List.of(new SimpleGrantedAuthority(role));
  }

  @Override
  public String getPassword() {
    return admin.getPasswordHash();
  }

  @Override
  public String getUsername() {
    return admin.getLoginId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return !"DISABLED".equalsIgnoreCase(admin.getStatus());
  }

  public Admin getAdmin() {
    return admin;
  }
}
