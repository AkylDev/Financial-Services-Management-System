package service.impl;


import kz.projects.ias.exceptions.CustomerServiceRequestNotFoundException;
import kz.projects.ias.models.CustomerServiceRequest;
import kz.projects.ias.models.enums.RequestStatus;
import kz.projects.ias.models.enums.RequestType;
import kz.projects.ias.repositories.CustomerServiceRequestRepository;
import kz.projects.ias.service.impl.CustomerRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerRequestServiceImplTest {

  @Mock
  private CustomerServiceRequestRepository customerRequestRepository;

  @InjectMocks
  private CustomerRequestServiceImpl customerRequestService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateRequest() {
    CustomerServiceRequest request = new CustomerServiceRequest();
    request.setUserId(1L);
    request.setRequestType(RequestType.INVESTMENT);
    request.setDescription("New investment request");

    when(customerRequestRepository.save(any(CustomerServiceRequest.class))).thenReturn(request);

    CustomerServiceRequest result = customerRequestService.createRequest(request);

    assertNotNull(result);
    assertEquals(request.getUserId(), result.getUserId());
    assertEquals(request.getRequestType(), result.getRequestType());
    assertEquals(request.getDescription(), result.getDescription());
  }

  @Test
  void testGetCustomerRequests() {
    List<CustomerServiceRequest> requests = Arrays.asList(
            new CustomerServiceRequest(1L, 1L, RequestType.INVESTMENT, "Investment request", RequestStatus.PENDING),
            new CustomerServiceRequest(2L, 2L, RequestType.ADVISORY, "Retirement planning request", RequestStatus.COMPLETED)
    );

    when(customerRequestRepository.findAll()).thenReturn(requests);

    List<CustomerServiceRequest> result = customerRequestService.getCustomerRequests();

    assertNotNull(result);
    assertEquals(requests.size(), result.size());
    assertEquals(requests.get(0).getDescription(), result.get(0).getDescription());
    assertEquals(requests.get(1).getRequestType(), result.get(1).getRequestType());
  }

  @Test
  void testUpdateRequests_Success() {
    Long requestId = 1L;
    CustomerServiceRequest existingRequest = new CustomerServiceRequest(requestId, 1L, RequestType.INVESTMENT,
            "Initial request", RequestStatus.CANCELLED);

    when(customerRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));

    when(customerRequestRepository.save(any(CustomerServiceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CustomerServiceRequest updatedRequest = new CustomerServiceRequest();
    updatedRequest.setId(requestId);
    updatedRequest.setUserId(1L);
    updatedRequest.setRequestType(RequestType.ADVISORY);
    updatedRequest.setDescription("Updated request");

    CustomerServiceRequest result = customerRequestService.updateRequests(requestId, updatedRequest);

    assertNotNull(result);
    assertEquals(requestId, result.getId());
    assertEquals(updatedRequest.getRequestType(), result.getRequestType());
    assertEquals(updatedRequest.getDescription(), result.getDescription());
  }

  @Test
  void testUpdateRequests_NotFound() {
    Long requestId = 1L;

    when(customerRequestRepository.findById(requestId)).thenReturn(Optional.empty());

    CustomerServiceRequest updatedRequest = new CustomerServiceRequest();
    updatedRequest.setId(requestId);
    updatedRequest.setUserId(1L);
    updatedRequest.setRequestType(RequestType.INVESTMENT);
    updatedRequest.setDescription("Updated request");

    CustomerServiceRequestNotFoundException exception = assertThrows(
            CustomerServiceRequestNotFoundException.class,
            () -> customerRequestService.updateRequests(requestId, updatedRequest)
    );

    assertEquals("Customer Request Not Found!", exception.getMessage());
  }
}

