package kz.projects.ams.services.impl;

import com.sun.jdi.InternalException;
import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.mapper.UserMapper;
import kz.projects.ams.models.Permissions;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.PermissionsRepository;
import kz.projects.ams.repositories.UserRepository;
import kz.projects.ams.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;

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
  private final CustomUserDetailsService userDetailsService;
  private final UserMapper userMapper;
  private final WebClient.Builder webClientBuilder;

  private static final String FINANCIAL_ADVISOR_URI = "/financial-advisors";

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
    checkIfUserExists(registerRequest.email());
    User newUser = createUser(registerRequest.email(), registerRequest.name(), registerRequest.password(), "ROLE_USER");
    return userMapper.toDto(userRepository.save(newUser));
  }

  /**
   * Выполняет вход пользователя в систему.
   * Проверяет предоставленные учетные данные и устанавливает аутентификацию в Security контекст.
   *
   * @param request запрос на вход в систему
   * @return {@link UserDTO} объект, представляющий аутентифицированного пользователя
   * @throws UsernameNotFoundException если предоставленные учетные данные неверны
   */
  @Override
  public UserDTO login(LoginRequest request) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
    if (passwordEncoder.matches(request.password(), userDetails.getPassword())) {
      UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                      userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      return userMapper.toDto((User) userDetails);
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
   * @throws InternalException        если не удалось отправить запрос на внешний сервис
   */
  @Override
  public UserDTO registerAsAdvisor(AdviserDTO adviser) {
    checkIfUserExists(adviser.email());
    User newUser = createUser(adviser.email(), adviser.name(), adviser.password(), "ROLE_ADVISOR");

    try {
      sendAdviserToExternalService(adviser);
    } catch (WebClientResponseException e) {
      throw new InternalException("Failed to get advisory sessions");
    }

    return userMapper.toDto(userRepository.save(newUser));
  }

  /**
   * Получает текущего авторизованного пользователя из контекста безопасности.
   *
   * @return {@link User} текущий авторизованный пользователь, или {@code null}, если нет активной авторизации
   */
  @Override
  public User getCurrentSessionUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      return (User) authentication.getPrincipal();
    }
    return null;
  }

  private void checkIfUserExists(String email) {
    userRepository.findByEmail(email)
            .ifPresent(user -> {
              throw new IllegalArgumentException("User with this email already exists.");
            });
  }

  private User createUser(String email, String name, String password, String role) {
    User newUser = new User();
    newUser.setName(name);
    newUser.setEmail(email);
    newUser.setPassword(passwordEncoder.encode(password));

    Permissions permission = permissionsRepository.findByRole(role);
    if (permission == null) {
      permission = new Permissions();
      permission.setRole(role);
      permission = permissionsRepository.save(permission);
    }
    newUser.setPermissionList(Collections.singletonList(permission));
    return newUser;
  }

  private void sendAdviserToExternalService(AdviserDTO adviser) {
    AdviserDTO response = webClientBuilder.build()
            .post()
            .uri(FINANCIAL_ADVISOR_URI)
            .bodyValue(adviser)
            .retrieve()
            .bodyToMono(AdviserDTO.class)
            .block();

    if (response == null) {
      throw new WebClientResponseException("Failed to get advisory sessions", HttpStatus.INTERNAL_SERVER_ERROR.value(),
              "", null, null, null);
    }
  }
}

