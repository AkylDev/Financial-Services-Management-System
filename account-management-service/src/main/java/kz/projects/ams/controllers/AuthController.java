package kz.projects.ams.controllers;

import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody UserDTO user){
    return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      UserDetails userDetails = userService.login(request);
      return ResponseEntity.ok(userDetails);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }
}
