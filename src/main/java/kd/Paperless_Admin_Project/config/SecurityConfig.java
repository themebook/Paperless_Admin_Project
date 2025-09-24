package kd.Paperless_Admin_Project.config;

import kd.Paperless_Admin_Project.security.AdminUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AdminUserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers("/", "/admin/login", "/error",
                "/admin/css/**", "/login/css/**",
                "/css/**", "/js/**", "/images/**")
            .permitAll()
            .requestMatchers("/admin/manager/**", "/admin/accounts/**")
            .hasAnyRole("MANAGER", "DIRECTOR", "ADMIN")
            .requestMatchers("/admin/**")
            .hasAnyRole("EMPLOYEE", "MANAGER", "DIRECTOR", "ADMIN")
            .anyRequest().authenticated())
        .userDetailsService(userDetailsService)
        .formLogin(form -> form
            .loginPage("/admin/login")
            .loginProcessingUrl("/admin/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .defaultSuccessUrl("/admin/dashboard", true)
            .failureUrl("/admin/login?error"))
        .logout(logout -> logout
            .logoutUrl("/admin/logout")
            .logoutSuccessUrl("/admin/login?logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID"))
        .sessionManagement(sm -> sm
            .sessionFixation(sess -> sess.migrateSession())
            .maximumSessions(1)
            .expiredUrl("/admin/login?expired"))
        .headers(headers -> headers.cacheControl(cache -> cache.disable()));

    return http.build();
  }

}
