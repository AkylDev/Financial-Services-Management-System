package kz.projects.ias.controllers;

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

  @PostMapping
  public ResponseEntity<AdvisorySessionDTO> createAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    AdvisorySessionDTO createdSession = advisorySessionService.createAdvisorySession(request);
    return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<AdvisorySessionDTO>> getAdvisorySessions(@RequestParam("userId") Long userId) {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getAdvisorySessions(userId);
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @GetMapping("/advisers")
  public ResponseEntity<List<AdvisorySessionDTO>> getFinancialAdviserSessions(@RequestParam("email") String email) {
    List<AdvisorySessionDTO> advisorySessions = advisorySessionService.getFinancialAdviserSessions(email);
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @PutMapping
  public void updateAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    advisorySessionService.updateAdvisorySession(request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAdvisorySession(@PathVariable("id") Long id,
                                                    @RequestParam("userId") Long userId) {
    advisorySessionService.deleteAdvisorySession(id, userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
