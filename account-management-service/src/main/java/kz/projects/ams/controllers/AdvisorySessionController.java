package kz.projects.ams.controllers;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.services.UserAdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/advisories")
public class AdvisorySessionController {

  private final UserAdvisorySessionService advisorySessionService;

  @PostMapping
  public ResponseEntity<AdvisorySessionDTO> orderAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    return new ResponseEntity<>(advisorySessionService.orderAdvisorySession(request), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<AdvisorySessionDTO>> getAdvisorySessionsPlanned() {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getAdvisorySessionsPlanned();
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @GetMapping("/advisers")
  public ResponseEntity<List<AdvisorySessionDTO>> getFinancialAdviserSessions() {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getAdvisersSessions();
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> rescheduleAdvisorySession(@PathVariable("id") Long id,
                                                        @RequestBody AdvisorySessionDTO request) {
    advisorySessionService.rescheduleAdvisorySession(id, request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAdvisorySession(@PathVariable("id") Long id) {
    advisorySessionService.deleteAdvisorySession(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
