package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.mapper.UserMapper;
import kz.projects.ams.models.Permissions;
import kz.projects.ams.models.User;
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
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PermissionsRepository permissionsRepository;

  private final PasswordEncoder passwordEncoder;

  private final MyUserDetailsService userDetailsService;

  private final UserMapper userMapper;

  private final RestTemplate restTemplate;

  @Override
  public UserDTO register(UserDTO registerRequest) {
    Optional<User> checkUser = userRepository.findByEmail(registerRequest.getEmail());
    if (checkUser.isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }

    User newUser = new User();
    newUser.setName(registerRequest.getName());
    newUser.setEmail(registerRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

    Permissions defaultPermission = permissionsRepository.findByRole("ROLE_USER");
    if (defaultPermission == null) {
      defaultPermission = new Permissions();
      defaultPermission.setRole("ROLE_USER");
      defaultPermission = permissionsRepository.save(defaultPermission);
    }
    newUser.setPermissionList(Collections.singletonList(defaultPermission));

    return userMapper.toDto(userRepository.save(newUser));
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

  @Override
  public UserDTO registerAsAdvisor(AdviserDTO adviser) {
    Optional<User> checkUser = userRepository.findByEmail(adviser.getEmail());
    if (checkUser.isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }

    User newUser = new User();
    newUser.setName(adviser.getName());
    newUser.setEmail(adviser.getEmail());
    newUser.setPassword(passwordEncoder.encode(adviser.getPassword()));

    Permissions defaultPermission = permissionsRepository.findByRole("ROLE_ADVISOR");
    if (defaultPermission == null) {
      defaultPermission = new Permissions();
      defaultPermission.setRole("ROLE_ADVISOR");
      defaultPermission = permissionsRepository.save(defaultPermission);
    }
    newUser.setPermissionList(Collections.singletonList(defaultPermission));

    restTemplate.postForObject(
            "http://localhost:8092/financial-advisors",
            adviser,
            AdviserDTO.class
    );

    return userMapper.toDto(userRepository.save(newUser));
  }
}
