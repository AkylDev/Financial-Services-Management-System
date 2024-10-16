package kz.projects.ams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  private final UserDetailsService userDetailsService;

  public SecurityConfig(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    AuthenticationManagerBuilder builder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

    http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                    authorizationManagerRequestMatcherRegistry.requestMatchers(HttpMethod.DELETE).permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/api/v1/ams/auth/**").permitAll()
                            .requestMatchers("/api/v1/ams/check-balance").permitAll()
                            .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .formLogin(AbstractAuthenticationFilterConfigurer::disable)
            .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                    .logoutUrl("/api/v1/ams/auth/logout")
                    .permitAll());

    return http.build();
  }

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
