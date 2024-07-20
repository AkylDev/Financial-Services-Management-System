package kz.projects.ias.service.impl;

import kz.projects.ias.exceptions.CustomerServiceRequestNotFoundException;
import kz.projects.ias.models.CustomerServiceRequest;
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

  /**
   * Создает новый запрос на обслуживание и сохраняет его в базе данных.
   *
   * @param request объект {@link CustomerServiceRequest}, содержащий данные запроса.
   * @return сохраненный объект {@link CustomerServiceRequest}.
   */
  @Override
  public CustomerServiceRequest createRequest(CustomerServiceRequest request) {
    return customerRequestRepository.save(request);
  }

  /**
   * Возвращает список всех запросов на обслуживание.
   *
   * @return список объектов {@link CustomerServiceRequest}.
   */
  @Override
  public List<CustomerServiceRequest> getCustomerRequests() {
    return customerRequestRepository.findAll();
  }

  /**
   * Обновляет информацию о запросе на обслуживание.
   *
   * @param id идентификатор запроса на обслуживание, который нужно обновить.
   * @param request объект {@link CustomerServiceRequest} с обновленной информацией.
   * @return обновленный объект {@link CustomerServiceRequest}.
   * @throws CustomerServiceRequestNotFoundException если запрос с указанным ID не найден.
   */
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
