package kz.projects.ias.controllers;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.models.AdvisorySession;
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
    return new ResponseEntity<>(advisorySessionService.createAdvisorySession(request), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<AdvisorySession>> getAdvisorySessions() {
    return new ResponseEntity<>(advisorySessionService.getAdvisorySessions(), HttpStatus.OK);
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
