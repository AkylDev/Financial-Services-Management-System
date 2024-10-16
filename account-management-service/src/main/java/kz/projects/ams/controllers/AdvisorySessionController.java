package kz.projects.ams.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.services.UserAdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ams/advisories")
public class AdvisorySessionController {

  private final UserAdvisorySessionService advisorySessionService;

  @Operation(summary = "Order a new advisory session")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Advisory session ordered successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvisorySessionDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<AdvisorySessionDTO> orderAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    return new ResponseEntity<>(advisorySessionService.orderAdvisorySession(request), HttpStatus.CREATED);
  }

  @Operation(summary = "Get all planned advisory sessions")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found planned advisory sessions",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvisorySessionDTO.class))),
          @ApiResponse(responseCode = "404", description = "No advisory sessions found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<AdvisorySessionDTO>> getAdvisorySessionsPlanned() {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getAdvisorySessionsPlanned();
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @Operation(summary = "Get all sessions of financial advisers")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found financial adviser sessions",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdvisorySessionDTO.class))),
          @ApiResponse(responseCode = "404", description = "No adviser sessions found", content = @Content)
  })
  @GetMapping("/advisers")
  public ResponseEntity<List<AdvisorySessionDTO>> getFinancialAdviserSessions() {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getAdvisersSessions();
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @Operation(summary = "Reschedule an advisory session")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Advisory session rescheduled successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Advisory session not found", content = @Content)
  })
  @PutMapping("/{id}")
  public ResponseEntity<Void> rescheduleAdvisorySession(@PathVariable("id") Long id,
                                                        @RequestBody AdvisorySessionDTO request) {
    advisorySessionService.rescheduleAdvisorySession(id, request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Delete an advisory session")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Advisory session deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Advisory session not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAdvisorySession(@PathVariable("id") Long id) {
    advisorySessionService.deleteAdvisorySession(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
