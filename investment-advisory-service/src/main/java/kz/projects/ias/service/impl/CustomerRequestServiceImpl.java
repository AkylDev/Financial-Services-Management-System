package kz.projects.ias.service.impl;

import kz.projects.ias.exceptions.CustomerServiceRequestNotFoundException;
import kz.projects.ias.module.CustomerServiceRequest;
import kz.projects.ias.module.enums.RequestStatus;
import kz.projects.ias.repositories.CustomerServiceRequestRepository;
import kz.projects.ias.service.CustomerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerRequestServiceImpl implements CustomerRequestService {

  private final CustomerServiceRequestRepository customerRequestRepository;


  @Override
  public CustomerServiceRequest createRequest(CustomerServiceRequest request) {
    request.setStatus(RequestStatus.PENDING);
    return customerRequestRepository.save(request);
  }

  @Override
  public List<CustomerServiceRequest> getCustomerRequests() {
    return customerRequestRepository.findAll();
  }

  @Override
  public CustomerServiceRequest updateRequests(Long id, CustomerServiceRequest request) {

    Optional<CustomerServiceRequest> customerRequestOptional = customerRequestRepository.findById(id);

    if (customerRequestOptional.isEmpty()){
      throw new CustomerServiceRequestNotFoundException("Customer Request Not Found!");
    }

    CustomerServiceRequest customerRequest = customerRequestOptional.get();
    customerRequest.setStatus(request.getStatus());
    customerRequest.setRequestType(request.getRequestType());
    customerRequest.setUserId(request.getUserId());
    customerRequest.setDescription(request.getDescription());

    return customerRequestRepository.save(customerRequest);
  }
}
