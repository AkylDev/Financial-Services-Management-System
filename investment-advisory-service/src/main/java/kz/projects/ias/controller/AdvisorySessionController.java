package kz.projects.ias.controller;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.service.AdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/advisory-sessions")
public class AdvisorySessionController {

  private final AdvisorySessionService advisorySessionService;

  @PostMapping
  public ResponseEntity<AdvisorySessionDTO> createAdvisorySession(@RequestBody AdvisorySessionDTO request){
    return new ResponseEntity<>(advisorySessionService.createAdvisorySession(request), HttpStatus.CREATED);
  }

}
