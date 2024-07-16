package services.impl;

import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.mapper.UserMapper;
import kz.projects.ams.models.Permissions;
import kz.projects.ams.models.User;
import kz.projects.ams.models.enums.AdvisorSpecialization;
import kz.projects.ams.repositories.PermissionsRepository;
import kz.projects.ams.repositories.UserRepository;
import kz.projects.ams.services.impl.MyUserDetailsService;
import kz.projects.ams.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PermissionsRepository permissionsRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private MyUserDetailsService userDetailsService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private UserServiceImpl userService;

  private UserDTO userDTO;
  private User user;

  @BeforeEach
  void setUp() {
    userDTO = new UserDTO();
    userDTO.setName("Test User");
    userDTO.setEmail("test@example.com");
    userDTO.setPassword("password");

    user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");
  }

  @Test
  void testRegisterSuccess() {
    when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
    when(permissionsRepository.findByRole("ROLE_USER")).thenReturn(null);
    when(permissionsRepository.save(any(Permissions.class))).thenReturn(new Permissions(1L,"ROLE_USER"));
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

    UserDTO registeredUser = userService.register(userDTO);

    assertNotNull(registeredUser);
    assertEquals("Test User", registeredUser.getName());
    assertEquals("test@example.com", registeredUser.getEmail());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterExistingEmail() {
    when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.register(userDTO);
    });

    assertEquals("User with this email already exists.", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testLoginSuccess() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getPassword()).thenReturn("encodedPassword");
    when(userDetailsService.loadUserByUsername(loginRequest.getEmail())).thenReturn(userDetails);
    when(passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())).thenReturn(true);

    UserDetails loggedInUser = userService.login(loginRequest);

    assertNotNull(loggedInUser);
    verify(userDetailsService, times(1)).loadUserByUsername(loginRequest.getEmail());
  }

  @Test
  void testLoginInvalidCredentials() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("wrongPassword");

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetailsService.loadUserByUsername(loginRequest.getEmail())).thenReturn(userDetails);
    when(passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())).thenReturn(false);

    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
      userService.login(loginRequest);
    });

    assertEquals("Invalid credentials", exception.getMessage());
    verify(userDetailsService, times(1)).loadUserByUsername(loginRequest.getEmail());
  }

  @Test
  void testRegisterAsAdvisorSuccess() {
    AdviserDTO adviserDTO = new AdviserDTO();
    adviserDTO.setName("Advisor");
    adviserDTO.setEmail("advisor@example.com");
    adviserDTO.setPassword("password");
    adviserDTO.setSpecialization(AdvisorSpecialization.ACCOUNTANT);

    User savedUser = new User();
    savedUser.setName(adviserDTO.getName());
    savedUser.setEmail(adviserDTO.getEmail());
    savedUser.setPassword("encodedPassword");
    savedUser.setPermissionList(Collections.singletonList(new Permissions(1L, "ROLE_ADVISOR")));

    UserDTO userDTO = new UserDTO();
    userDTO.setName(adviserDTO.getName());
    userDTO.setEmail(adviserDTO.getEmail());

    when(userRepository.findByEmail(adviserDTO.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(adviserDTO.getPassword())).thenReturn("encodedPassword");
    when(permissionsRepository.findByRole("ROLE_ADVISOR")).thenReturn(null);
    when(permissionsRepository.save(any(Permissions.class))).thenReturn(new Permissions(1L, "ROLE_ADVISOR"));
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);
    when(restTemplate.postForObject(
            eq("http://localhost:8092/financial-advisors"),
            eq(adviserDTO),
            eq(AdviserDTO.class)
    )).thenReturn(adviserDTO);

    UserDTO registeredAdvisor = userService.registerAsAdvisor(adviserDTO);

    assertNotNull(registeredAdvisor);
    assertEquals("Advisor", registeredAdvisor.getName());
    assertEquals("advisor@example.com", registeredAdvisor.getEmail());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterAsAdvisorExistingEmail() {
    AdviserDTO adviserDTO = new AdviserDTO();
    adviserDTO.setName("Advisor");
    adviserDTO.setEmail("advisor@example.com");
    adviserDTO.setPassword("password");
    adviserDTO.setSpecialization(AdvisorSpecialization.ACCOUNTANT);

    when(userRepository.findByEmail(adviserDTO.getEmail())).thenReturn(Optional.of(user));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.registerAsAdvisor(adviserDTO);
    });

    assertEquals("User with this email already exists.", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }
}
