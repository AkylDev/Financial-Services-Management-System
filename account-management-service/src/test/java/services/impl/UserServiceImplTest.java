package services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.mapper.UserMapper;
import kz.projects.ams.models.Permissions;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.PermissionsRepository;
import kz.projects.ams.repositories.UserRepository;
import kz.projects.ams.services.impl.CustomUserDetailsService;
import kz.projects.ams.services.impl.UserServiceImpl;
import kz.projects.commonlib.dto.enums.AdvisorSpecialization;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PermissionsRepository permissionsRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private CustomUserDetailsService userDetailsService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private MockWebServer mockWebServer;
  private UserDTO userDTO;
  private User user;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    userDTO = new UserDTO(
            null,
            "Test User",
            "test@example.com",
            "password"
    );

    user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void testRegisterSuccess() {
    when(userRepository.findByEmail(userDTO.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
    when(permissionsRepository.findByRole("ROLE_USER")).thenReturn(null);
    when(permissionsRepository.save(any(Permissions.class))).thenReturn(new Permissions(1L, "ROLE_USER"));
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

    UserDTO registeredUser = userService.register(userDTO);

    assertNotNull(registeredUser);
    assertEquals("Test User", registeredUser.name());
    assertEquals("test@example.com", registeredUser.email());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterExistingEmail() {
    when(userRepository.findByEmail(userDTO.email())).thenReturn(Optional.of(user));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.register(userDTO));

    assertEquals("User with this email already exists.", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }


  @Test
  void testLoginSuccess() {
    LoginRequest request = new LoginRequest("test@example.com", "password123");
    User user = new User();
    user.setEmail(request.email());
    user.setPassword("encodedPassword");

    when(userDetailsService.loadUserByUsername(request.email())).thenReturn(user);
    when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

    UserDTO loggedInUser = userService.login(request);

    assertEquals(request.email(), loggedInUser.email());
    verify(userDetailsService).loadUserByUsername(request.email());
    verify(passwordEncoder).matches(request.password(), user.getPassword());
  }

  @Test
  void testLoginInvalidCredentials() {
    LoginRequest loginRequest = new LoginRequest(
            "test@example.com",
            "wrongPassword"
    );

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetailsService.loadUserByUsername(loginRequest.email())).thenReturn(userDetails);
    when(passwordEncoder.matches(loginRequest.password(), userDetails.getPassword())).thenReturn(false);

    BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> userService.login(loginRequest));

    assertEquals("Invalid credentials", exception.getMessage());
    verify(userDetailsService, times(1)).loadUserByUsername(loginRequest.email());
  }

  @Test
  void testRegisterAsAdvisorSuccess() throws Exception {
    AdviserDTO adviserDTO = new AdviserDTO(
            "Advisor",
            "advisor@example.com",
            "password",
            AdvisorSpecialization.ACCOUNTANT
    );

    User savedUser = new User();
    savedUser.setName(adviserDTO.name());
    savedUser.setEmail(adviserDTO.email());
    savedUser.setPassword("encodedPassword");
    savedUser.setPermissionList(Collections.singletonList(new Permissions(1L, "ROLE_ADVISOR")));

    UserDTO userDTO = new UserDTO(null, adviserDTO.name(), adviserDTO.email(), "password");

    when(userRepository.findByEmail(adviserDTO.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(adviserDTO.password())).thenReturn("encodedPassword");
    when(permissionsRepository.findByRole("ROLE_ADVISOR")).thenReturn(null);
    when(permissionsRepository.save(any(Permissions.class))).thenReturn(new Permissions(1L, "ROLE_ADVISOR"));
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

    ObjectMapper objectMapper = new ObjectMapper();
    String mockWebAdviser = objectMapper.writeValueAsString(adviserDTO);

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(201)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mockWebAdviser));

    WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(mockWebServer.url("/").toString());
    userService = new UserServiceImpl(userRepository, permissionsRepository, passwordEncoder, userDetailsService, userMapper, webClientBuilder);

    UserDTO registeredAdvisor = userService.registerAsAdvisor(adviserDTO);

    assertNotNull(registeredAdvisor);
    assertEquals("Advisor", registeredAdvisor.name());
    assertEquals("advisor@example.com", registeredAdvisor.email());
    verify(userRepository, times(1)).save(any(User.class));

    var recordedRequest = mockWebServer.takeRequest(10, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/financial-advisors", recordedRequest.getPath());
    assertEquals("POST", recordedRequest.getMethod());

  }

  @Test
  void testRegisterAsAdvisorExistingEmail() {
    AdviserDTO adviserDTO = new AdviserDTO(
            "Advisor",
            "advisor@example.com",
            "password",
            AdvisorSpecialization.ACCOUNTANT
    );

    when(userRepository.findByEmail(adviserDTO.email())).thenReturn(Optional.of(user));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.registerAsAdvisor(adviserDTO));

    assertEquals("User with this email already exists.", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }
}
