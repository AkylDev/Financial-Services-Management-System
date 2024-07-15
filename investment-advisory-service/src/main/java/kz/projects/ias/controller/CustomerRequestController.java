package kz.projects.ias.controller;

import kz.projects.ias.module.CustomerServiceRequest;
import kz.projects.ias.service.CustomerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service-requests")
public class CustomerRequestController {

  private final CustomerRequestService customerRequestService;

  @PostMapping
  public ResponseEntity<CustomerServiceRequest> createCustomerRequest(@RequestBody CustomerServiceRequest request) {
    return new ResponseEntity<>(customerRequestService.createRequest(request), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CustomerServiceRequest>> getCustomerRequests() {
    return new ResponseEntity<>(customerRequestService.getCustomerRequests(), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerServiceRequest> updateCustomerRequest(@PathVariable("id") Long id,
                                                                      @RequestBody CustomerServiceRequest request){
    return new ResponseEntity<>(customerRequestService.updateRequests(id, request), HttpStatus.OK);
  }

}
