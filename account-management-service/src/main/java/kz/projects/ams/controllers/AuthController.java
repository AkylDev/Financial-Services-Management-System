package kz.projects.ams.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ams/auth")
public class AuthController {

  private final UserService userService;

  @Operation(summary = "Register a new user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "User registered successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/register")
  public ResponseEntity<UserDTO> register(@RequestBody UserDTO user) {
    return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
  }

  @Operation(summary = "Login a user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Login successful",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
          @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      UserDTO userDTO = userService.login(request);
      return ResponseEntity.ok(userDTO);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }

  @Operation(summary = "Register a new advisor")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Advisor registered successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/register-advisor")
  public ResponseEntity<UserDTO> registerAsAdvisor(@RequestBody AdviserDTO adviser) {
    return new ResponseEntity<>(userService.registerAsAdvisor(adviser), HttpStatus.CREATED);
  }
}
