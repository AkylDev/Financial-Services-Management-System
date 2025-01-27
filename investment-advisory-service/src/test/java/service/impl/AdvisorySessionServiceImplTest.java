package service.impl;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.exceptions.AdvisorySessionNotFoundException;
import kz.projects.ias.exceptions.UnauthorizedAccessException;
import kz.projects.ias.models.AdvisorySession;
import kz.projects.ias.models.CustomerServiceRequest;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.models.enums.RequestStatus;
import kz.projects.ias.repositories.AdvisorySessionRepository;
import kz.projects.ias.repositories.FinancialAdvisorRepository;
import kz.projects.ias.service.CustomerRequestService;
import kz.projects.ias.service.impl.AdvisorySessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdvisorySessionServiceImplTest {

  @Mock
  private FinancialAdvisorRepository financialAdvisorRepository;

  @Mock
  private AdvisorySessionRepository advisorySessionRepository;

  @Mock
  private CustomerRequestService customerRequestService;

  @InjectMocks
  private AdvisorySessionServiceImpl advisorySessionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateAdvisorySession() {
    // Mocking the input advisory session request
    AdvisorySessionDTO request =
            new AdvisorySessionDTO(1L, 1L, 1L, LocalDate.of(2024, 7, 20), LocalTime.of(10, 0));

    // Mocking the financial advisor repository response
    FinancialAdvisor advisor = new FinancialAdvisor();
    advisor.setId(1L);
    advisor.setName("John Doe");
    when(financialAdvisorRepository.findById(request.advisoryId())).thenReturn(Optional.of(advisor));

    // Mocking the advisory session repository save method
    AdvisorySession savedSession = new AdvisorySession();
    savedSession.setId(1L);
    savedSession.setUserId(request.userId());
    savedSession.setFinancialAdvisor(advisor);
    savedSession.setDate(request.date());
    savedSession.setTime(request.time());
    savedSession.setStatus(RequestStatus.PENDING);
    when(advisorySessionRepository.save(any(AdvisorySession.class))).thenReturn(savedSession);

    // Mocking customer service request creation
    when(customerRequestService.createRequest(any())).thenReturn(new CustomerServiceRequest());

    // Test the method
    AdvisorySessionDTO result = advisorySessionService.createAdvisorySession(request);

    // Assertions
    assertNotNull(result);
    assertEquals(request.userId(), result.userId());
    assertEquals(advisor.getId(), result.advisoryId());
  }

  @Test
  void testGetAdvisorySessions() {
    Long userId = 1L;

    // Mocking the advisory session repository response
    List<AdvisorySession> sessions = Arrays.asList(
            createAdvisorySession(1L, userId, LocalDate.of(2024, 7, 20), LocalTime.of(10, 0), RequestStatus.PENDING),
            createAdvisorySession(2L, userId, LocalDate.of(2024, 7, 21), LocalTime.of(11, 0), RequestStatus.CANCELLED)
    );
    when(advisorySessionRepository.findAllByUserId(userId)).thenReturn(sessions);

    // Test the method
    List<AdvisorySessionDTO> result = advisorySessionService.getAdvisorySessions(userId);

    // Assertions
    assertNotNull(result);
    assertEquals(sessions.size(), result.size());
    assertEquals(sessions.get(0).getDate(), result.get(0).date());
//    assertEquals(sessions.get(1).get(), result.get(1).getStatus());
  }

  @Test
  void testGetFinancialAdviserSessions() {
    String advisorEmail = "john.doe@example.com";

    // Mocking the financial advisor repository response
    FinancialAdvisor advisor = new FinancialAdvisor();
    advisor.setEmail(advisorEmail);
    when(financialAdvisorRepository.findByEmail(advisorEmail)).thenReturn(Optional.of(advisor));

    // Mocking the advisory session repository response
    List<AdvisorySession> sessions = Arrays.asList(
            createAdvisorySession(1L, 1L, LocalDate.of(2024, 7, 20), LocalTime.of(10, 0), RequestStatus.PENDING),
            createAdvisorySession(2L, 1L, LocalDate.of(2024, 7, 21), LocalTime.of(11, 0), RequestStatus.CANCELLED)
    );
    when(advisorySessionRepository.findAllByFinancialAdvisor(advisor)).thenReturn(sessions);

    // Test the method
    List<AdvisorySessionDTO> result = advisorySessionService.getFinancialAdviserSessions(advisorEmail);

    // Assertions
    assertNotNull(result);
    assertEquals(sessions.size(), result.size());
    assertEquals(sessions.get(0).getDate(), result.get(0).date());
//    assertEquals(sessions.get(1).getStatus(), result.get(1).getStatus());
  }

  @Test
  void testUpdateAdvisorySession_Success() {
    Long sessionId = 1L;
    Long userId = 1L;
    FinancialAdvisor advisor = new FinancialAdvisor();
    advisor.setId(1L);
    advisor.setName("Jon Doe");

    AdvisorySession existingSession = createAdvisorySession(sessionId, userId,
            LocalDate.of(2024, 7, 20), LocalTime.of(10, 0), RequestStatus.PENDING);
    existingSession.setFinancialAdvisor(advisor);

    // Mocking the advisory session repository findById method
    when(advisorySessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));

    // Mocking the advisory session repository save method
    when(advisorySessionRepository.save(any(AdvisorySession.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Updated advisory session request
    AdvisorySessionDTO updatedSession =
            new AdvisorySessionDTO(sessionId, userId, null, LocalDate.of(2024, 7, 22), LocalTime.of(12, 0));


    // Test the method
    advisorySessionService.updateAdvisorySession(updatedSession);

    // Assertions
    assertEquals(updatedSession.date(), existingSession.getDate());
    assertEquals(updatedSession.time(), existingSession.getTime());
    assertEquals(RequestStatus.RESCHEDULED, existingSession.getStatus());
  }

  @Test
  void testUpdateAdvisorySession_NotFound() {
    Long sessionId = 1L;

    // Mocking the advisory session repository findById method
    when(advisorySessionRepository.findById(sessionId)).thenReturn(Optional.empty());

    // Updated advisory session request
    AdvisorySessionDTO updatedSession =
            new AdvisorySessionDTO(sessionId, 1L, 1L, LocalDate.of(2024, 7, 22), LocalTime.of(12, 0));

    // Test and assert AdvisorySessionNotFoundException
    AdvisorySessionNotFoundException exception = assertThrows(
            AdvisorySessionNotFoundException.class,
            () -> advisorySessionService.updateAdvisorySession(updatedSession)
    );

    assertEquals("AdvisorySession with this ID not found", exception.getMessage());
  }

  @Test
  void testDeleteAdvisorySession_Success() {
    Long sessionId = 1L;
    Long userId = 1L;
    FinancialAdvisor advisor = new FinancialAdvisor();
    advisor.setId(1L);
    advisor.setName("Jon Doe");

    AdvisorySession existingSession = createAdvisorySession(sessionId, userId,
            LocalDate.of(2024, 7, 20), LocalTime.of(10, 0), RequestStatus.PENDING);
    existingSession.setFinancialAdvisor(advisor);

    // Mocking the advisory session repository findById method
    when(advisorySessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));

    // Mocking customer service request creation
    when(customerRequestService.createRequest(any(CustomerServiceRequest.class))).thenReturn(new CustomerServiceRequest());

    // Test the method
    advisorySessionService.deleteAdvisorySession(sessionId, userId);

    // Assertions
    verify(customerRequestService, times(1)).createRequest(any(CustomerServiceRequest.class));
    verify(advisorySessionRepository, times(1)).deleteById(sessionId);
  }

  @Test
  void testDeleteAdvisorySession_NotAllowed() {
    Long sessionId = 1L;
    Long userId = 2L; // Different user ID

    // Mocking the advisory session repository findById method
    when(advisorySessionRepository.findById(sessionId)).thenReturn(Optional.of(createAdvisorySession(sessionId, 1L, LocalDate.of(2024, 7, 20), LocalTime.of(10, 0), RequestStatus.PENDING)));

    // Test and assert IllegalArgumentException
    UnauthorizedAccessException exception = assertThrows(
            UnauthorizedAccessException.class,
            () -> advisorySessionService.deleteAdvisorySession(sessionId, userId)
    );

    assertEquals("You are not allowed", exception.getMessage());
  }

  private AdvisorySession createAdvisorySession(Long id, Long userId, LocalDate date, LocalTime time, RequestStatus status) {
    AdvisorySession session = new AdvisorySession();
    session.setId(id);
    session.setUserId(userId);
    session.setDate(date);
    session.setTime(time);
    session.setStatus(status);
    return session;
  }
}
