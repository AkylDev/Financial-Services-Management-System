package kz.projects.ias.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ias.models.CustomerServiceRequest;
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

  @Operation(summary = "Create a new customer service request")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Customer service request created successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerServiceRequest.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<CustomerServiceRequest> createCustomerRequest(@RequestBody CustomerServiceRequest request) {
    return new ResponseEntity<>(customerRequestService.createRequest(request), HttpStatus.CREATED);
  }

  @Operation(summary = "Get all customer service requests")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Customer service requests retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerServiceRequest.class))),
          @ApiResponse(responseCode = "404", description = "Customer service requests not found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<CustomerServiceRequest>> getCustomerRequests() {
    return new ResponseEntity<>(customerRequestService.getCustomerRequests(), HttpStatus.OK);
  }

  @Operation(summary = "Update a customer service request")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Customer service request updated successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerServiceRequest.class))),
          @ApiResponse(responseCode = "404", description = "Customer service request not found", content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PutMapping("/{id}")
  public ResponseEntity<CustomerServiceRequest> updateCustomerRequest(@PathVariable("id") Long id,
                                                                      @RequestBody CustomerServiceRequest request) {
    return new ResponseEntity<>(customerRequestService.updateRequests(id, request), HttpStatus.OK);
  }
}
