package kz.projects.ams.services.impl;

import kz.projects.ams.dto.LoginRequest;
import kz.projects.ams.model.Permissions;
import kz.projects.ams.model.User;
import kz.projects.ams.repositories.PermissionsRepository;
import kz.projects.ams.repositories.UserRepository;
import kz.projects.ams.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PermissionsRepository permissionsRepository;

  private final PasswordEncoder passwordEncoder;

  private final MyUserDetailsService userDetailsService;

  @Override
  public User register(User registerRequest) {
    Optional<User> checkUser = userRepository.findByEmail(registerRequest.getEmail());
    if (checkUser.isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }

    registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

    Permissions defaultPermission = permissionsRepository.findByRole("ROLE_USER");
    if (defaultPermission == null) {
      defaultPermission = new Permissions();
      defaultPermission.setRole("ROLE_USER");
      defaultPermission = permissionsRepository.save(defaultPermission);
    }
    registerRequest.setPermissionList(Collections.singletonList(defaultPermission));

    return userRepository.save(registerRequest);
  }

  @Override
  public UserDetails login(LoginRequest request) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
      UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                      userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      return userDetails;
    } else {
      throw new UsernameNotFoundException("Invalid credentials");
    }
  }
}
