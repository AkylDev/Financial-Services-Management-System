package kz.projects.ams.services.impl;

import com.sun.jdi.InternalException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

/**
 * Реализация {@link UserService} для управления пользователями.
 * Обрабатывает регистрацию пользователей, вход в систему и регистрацию пользователей как советников.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PermissionsRepository permissionsRepository;

  private final PasswordEncoder passwordEncoder;

  private final MyUserDetailsService userDetailsService;

  private final UserMapper userMapper;

  private final RestTemplate restTemplate;

  /**
   * Регистрирует нового пользователя.
   * Проверяет наличие пользователя с указанным email и создаёт нового, если такой не найден.
   *
   * @param registerRequest запрос на регистрацию пользователя
   * @return зарегистрированный пользователь в виде {@link UserDTO}
   * @throws IllegalArgumentException если пользователь с указанным email уже существует
   */
  @Override
  public UserDTO register(UserDTO registerRequest) {
    Optional<User> checkUser = userRepository.findByEmail(registerRequest.email());
    if (checkUser.isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }

    User newUser = new User();
    newUser.setName(registerRequest.name());
    newUser.setEmail(registerRequest.email());
    newUser.setPassword(passwordEncoder.encode(registerRequest.password()));

    Permissions defaultPermission = permissionsRepository.findByRole("ROLE_USER");
    if (defaultPermission == null) {
      defaultPermission = new Permissions();
      defaultPermission.setRole("ROLE_USER");
      defaultPermission = permissionsRepository.save(defaultPermission);
    }
    newUser.setPermissionList(Collections.singletonList(defaultPermission));

    return userMapper.toDto(userRepository.save(newUser));
  }

  /**
   * Выполняет вход пользователя в систему.
   * Проверяет предоставленные учетные данные и устанавливает аутентификацию в Security контекст.
   *
   * @param request запрос на вход в систему
   * @return {@link UserDetails} объект, представляющий аутентифицированного пользователя
   * @throws UsernameNotFoundException если предоставленные учетные данные неверны
   */
  @Override
  public UserDetails login(LoginRequest request) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
    if (passwordEncoder.matches(request.password(), userDetails.getPassword())) {
      UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                      userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      return userDetails;
    } else {
      throw new BadCredentialsException("Invalid credentials");
    }
  }

  /**
   * Регистрирует нового советника.
   * Проверяет наличие пользователя с указанным email, создает нового советника и отправляет запрос на внешний сервис.
   *
   * @param adviser запрос на регистрацию советника
   * @return зарегистрированный советник в виде {@link UserDTO}
   * @throws IllegalArgumentException если пользователь с указанным email уже существует
   * @throws InternalException если не удалось отправить запрос на внешний сервис
   */
  @Override
  public UserDTO registerAsAdvisor(AdviserDTO adviser) {
    Optional<User> checkUser = userRepository.findByEmail(adviser.email());
    if (checkUser.isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }

    User newUser = new User();
    newUser.setName(adviser.name());
    newUser.setEmail(adviser.email());
    newUser.setPassword(passwordEncoder.encode(adviser.password()));

    Permissions defaultPermission = permissionsRepository.findByRole("ROLE_ADVISOR");
    if (defaultPermission == null) {
      defaultPermission = new Permissions();
      defaultPermission.setRole("ROLE_ADVISOR");
      defaultPermission = permissionsRepository.save(defaultPermission);
    }
    newUser.setPermissionList(Collections.singletonList(defaultPermission));

    try {
      AdviserDTO response = restTemplate.postForObject(
              "http://localhost:8092/financial-advisors",
              adviser,
              AdviserDTO.class
      );
      if (response == null) {
        throw new RestClientException("Failed to get advisory sessions");
      }
    } catch (RestClientException e) {
      throw new InternalException("Failed to get advisory sessions");
    }

    return userMapper.toDto(userRepository.save(newUser));
  }
}
