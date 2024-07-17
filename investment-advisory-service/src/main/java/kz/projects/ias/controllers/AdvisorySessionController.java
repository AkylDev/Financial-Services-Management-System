package kz.projects.ias.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.service.AdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/advisory-sessions")
public class AdvisorySessionController {

  private final AdvisorySessionService advisorySessionService;

  @Operation(summary = "Create a new advisory session")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Advisory session created successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvisorySessionDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<AdvisorySessionDTO> createAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    AdvisorySessionDTO createdSession = advisorySessionService.createAdvisorySession(request);
    return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
  }

  @Operation(summary = "Get advisory sessions for a user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Advisory sessions retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvisorySessionDTO.class))),
          @ApiResponse(responseCode = "404", description = "Advisory sessions not found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<AdvisorySessionDTO>> getAdvisorySessions(@RequestParam("userId") Long userId) {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getAdvisorySessions(userId);
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @Operation(summary = "Get advisory sessions for a financial adviser")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Advisory sessions retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvisorySessionDTO.class))),
          @ApiResponse(responseCode = "404", description = "Advisory sessions not found", content = @Content)
  })
  @GetMapping("/advisers")
  public ResponseEntity<List<AdvisorySessionDTO>> getFinancialAdviserSessions(@RequestParam("email") String email) {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getFinancialAdviserSessions(email);
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @Operation(summary = "Update an advisory session")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Advisory session updated successfully",
                  content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PutMapping
  public void updateAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    advisorySessionService.updateAdvisorySession(request);
  }

  @Operation(summary = "Delete an advisory session")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Advisory session deleted successfully",
                  content = @Content),
          @ApiResponse(responseCode = "404", description = "Advisory session not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAdvisorySession(@PathVariable("id") Long id,
                                                    @RequestParam("userId") Long userId) {
    advisorySessionService.deleteAdvisorySession(id, userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
