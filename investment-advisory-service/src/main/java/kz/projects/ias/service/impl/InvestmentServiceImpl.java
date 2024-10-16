package kz.projects.ias.service.impl;

import kz.projects.ias.dto.BalanceCheckRequest;
import kz.projects.ias.dto.BalanceCheckResponse;
import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.exceptions.CheckBalanceException;
import kz.projects.ias.exceptions.InvestmentNotFoundException;
import kz.projects.ias.exceptions.NotSufficientFundsException;
import kz.projects.ias.mapper.InvestmentsMapper;
import kz.projects.ias.models.CustomerServiceRequest;
import kz.projects.ias.models.Investment;
import kz.projects.ias.models.enums.RequestStatus;
import kz.projects.ias.models.enums.RequestType;
import kz.projects.ias.repositories.InvestmentRepository;
import kz.projects.ias.service.CustomerRequestService;
import kz.projects.ias.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

  private final InvestmentRepository investmentRepository;

  private final WebClient.Builder webClientBuilder;

  private final CustomerRequestService customerRequestService;

  private CustomerServiceRequest customerInvestmentRequest(InvestmentDTO investment){
    CustomerServiceRequest customerServiceRequest = new CustomerServiceRequest();
    customerServiceRequest.setUserId(investment.userId());
    customerServiceRequest.setRequestType(RequestType.INVESTMENT);
    customerServiceRequest.setDescription("Customer invested " + investment.amount()
            + "$ to " + investment.investmentType());

    return customerServiceRequest;
  }

  /**
   * Создает инвестицию, проверяя наличие достаточных средств на счете.
   *
   * @param request объект {@link InvestmentDTO}, содержащий информацию об инвестиции.
   * @return объект {@link InvestmentDTO}, который был сохранен в базе данных.
   * @throws NotSufficientFundsException если на счете недостаточно средств для инвестиции.
   * @throws CheckBalanceException если произошла ошибка при проверке баланса или создании инвестиции.
   */
  @Override
  public InvestmentDTO createInvestment(InvestmentDTO request) {

    BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest(
            request.accountId(),
            request.amount()
    );

    try {
      BalanceCheckResponse response = webClientBuilder.build()
              .post()
              .uri("/api/v1/ams/check-balance")
              .bodyValue(balanceCheckRequest)
              .retrieve()
              .bodyToMono(BalanceCheckResponse.class)
              .block();

      if (response == null || !response.sufficientFunds()) {
        throw new NotSufficientFundsException("Insufficient funds");
      }

      Investment investment = InvestmentsMapper.toEntity(request);
      investment.setDate(new Date());

      CustomerServiceRequest serviceRequest = customerInvestmentRequest(request);
      serviceRequest.setStatus(RequestStatus.PENDING);
      customerRequestService.createRequest(serviceRequest);

      Investment savedInvestment = investmentRepository.save(investment);

      return InvestmentsMapper.toDto(savedInvestment);
    } catch (WebClientResponseException e) {
      throw new CheckBalanceException("Failed to check balance or create investment", e);
    }
  }

  /**
   * Возвращает список всех инвестиций для указанного пользователя.
   *
   * @param userId идентификатор пользователя, для которого нужно получить инвестиции.
   * @return список объектов {@link Investment}, связанных с указанным пользователем.
   */
  @Override
  public List<Investment> getAllInvestments(Long userId) {
    return investmentRepository.findAllByUserId(userId);
  }

  /**
   * Обновляет информацию об инвестиции.
   *
   * @param request объект {@link InvestmentDTO}, содержащий обновленную информацию об инвестиции.
   * @throws InvestmentNotFoundException если инвестиция с указанным ID не найдена.
   * @throws IllegalArgumentException если пользователь не имеет прав для обновления этой инвестиции.
   */
  @Override
  public void updateInvestment(InvestmentDTO request) {
    Investment investment = investmentRepository.findById(request.id())
            .orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));

    if (!investment.getUserId().equals(request.userId())) {
      throw new IllegalArgumentException("You are not allowed");
    }

    investment.setInvestmentType(request.investmentType());
    investment.setDate(new Date());
    investment.setAmount(request.amount());
    investment.setUserId(request.userId());

    CustomerServiceRequest serviceRequest = customerInvestmentRequest(request);
    serviceRequest.setStatus(RequestStatus.CHANGED);
    customerRequestService.createRequest(serviceRequest);

    investmentRepository.save(investment);
  }

  /**
   * Удаляет инвестицию и создает запрос на отмену инвестиции.
   *
   * @param id идентификатор инвестиции, которую нужно удалить.
   * @param userId идентификатор пользователя, который запрашивает удаление.
   * @throws InvestmentNotFoundException если инвестиция с указанным ID не найдена.
   * @throws IllegalArgumentException если пользователь не имеет прав для удаления этой инвестиции.
   */
  @Override
  public void deleteInvestment(Long id, Long userId) {
    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));

    if (!investment.getUserId().equals(userId)) {
      throw new IllegalArgumentException("You are not allowed");
    }

    InvestmentDTO request = InvestmentsMapper.toDto(investment);

    CustomerServiceRequest serviceRequest = customerInvestmentRequest(request);
    serviceRequest.setStatus(RequestStatus.CANCELLED);
    customerRequestService.createRequest(serviceRequest);

    investmentRepository.deleteById(id);
  }
}
