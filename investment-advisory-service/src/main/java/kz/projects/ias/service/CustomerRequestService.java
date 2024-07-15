package kz.projects.ias.service;

import kz.projects.ias.models.CustomerServiceRequest;

import java.util.List;

public interface CustomerRequestService {
  CustomerServiceRequest createRequest(CustomerServiceRequest request);

  List<CustomerServiceRequest> getCustomerRequests();

  CustomerServiceRequest updateRequests(Long id, CustomerServiceRequest request);

}
