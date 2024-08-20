package services.impl;

import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.models.User;
import kz.projects.ams.services.impl.UserAdvisorySessionServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserAdvisorySessionServiceImplTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserAdvisorySessionServiceImpl advisorySessionService;

  @Test
  public void testOrderAdvisorySession_Success() {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    AdvisorySessionDTO request = new AdvisorySessionDTO(null, null, 10L, null, null);

    when(restTemplate.postForObject(
            anyString(),
            argThat((AdvisorySessionDTO dto) -> dto.advisoryId().equals(10L)),
            eq(AdvisorySessionDTO.class)))
            .thenReturn(request);

    AdvisorySessionDTO result = advisorySessionService.orderAdvisorySession(request);

    verify(userService).getCurrentSessionUser();
    verify(restTemplate).postForObject(
            anyString(),
            argThat((AdvisorySessionDTO dto) -> dto.advisoryId().equals(10L)),
            eq(AdvisorySessionDTO.class));

    assertNotNull(result);
    assertEquals(request, result);
    assertEquals(request.userId(), result.userId());
    assertEquals(request.advisoryId(), result.advisoryId());
    assertEquals(request.date(), result.date());
    assertEquals(request.time(), result.time());
  }

  @Test(expected = AdvisorySessionOrderException.class)
  public void testOrderAdvisorySession_RestClientException() {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    AdvisorySessionDTO request = new AdvisorySessionDTO(null, null, 10L, null, null);


    when(restTemplate.postForObject(anyString(), any(AdvisorySessionDTO.class), eq(AdvisorySessionDTO.class)))
            .thenThrow(new RestClientException("Simulated RestClientException"));

    advisorySessionService.orderAdvisorySession(request);
  }

  @Test
  public void testGetAdvisorySessionsPlanned_Success() {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    List<AdvisorySessionDTO> mockSessions = Arrays.asList(
            new AdvisorySessionDTO(1L, 1L, 2L, LocalDate.now(), LocalTime.of(9, 0)),
            new AdvisorySessionDTO(2L, 1L, 3L, LocalDate.now(), LocalTime.of(10, 0))
    );
    ResponseEntity<List<AdvisorySessionDTO>> mockResponseEntity = new ResponseEntity<>(mockSessions, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
            eq(new ParameterizedTypeReference<List<AdvisorySessionDTO>>() {})))
            .thenReturn(mockResponseEntity);

    List<AdvisorySessionDTO> result = advisorySessionService.getAdvisorySessionsPlanned();

    verify(restTemplate).exchange(
            eq("http://localhost:8092/advisory-sessions?userId=1"),
            eq(HttpMethod.GET),
            eq(null),
            eq(new ParameterizedTypeReference<List<AdvisorySessionDTO>>() {})
    );

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(mockSessions, result);
  }

  @Test
  public void testGetAdvisersSessions_Success() {
    User currentUser = new User();
    currentUser.setEmail("test@example.com");
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    List<AdvisorySessionDTO> mockSessions = Arrays.asList(
            new AdvisorySessionDTO(1L, 1L, 2L, LocalDate.now(), LocalTime.of(9, 0)),
            new AdvisorySessionDTO(2L, 1L, 3L, LocalDate.now(), LocalTime.of(10, 0))
    );
    ResponseEntity<List<AdvisorySessionDTO>> mockResponseEntity = new ResponseEntity<>(mockSessions, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
            eq(new ParameterizedTypeReference<List<AdvisorySessionDTO>>() {})))
            .thenReturn(mockResponseEntity);

    List<AdvisorySessionDTO> result = advisorySessionService.getAdvisersSessions();

    verify(restTemplate).exchange(
            eq("http://localhost:8092/advisory-sessions/advisers?email=test@example.com"),
            eq(HttpMethod.GET),
            eq(null),
            eq(new ParameterizedTypeReference<List<AdvisorySessionDTO>>() {})
    );

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(mockSessions, result);
  }

  @Test
  public void testRescheduleAdvisorySession_Success() {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    AdvisorySessionDTO request = new AdvisorySessionDTO(1L, 1L, 2L, LocalDate.now(), LocalTime.of(9, 0));
    advisorySessionService.rescheduleAdvisorySession(request.id(), request);

    verify(restTemplate).put(
            eq("http://localhost:8092/advisory-sessions"),
            eq(request),
            eq(AdvisorySessionDTO.class)
    );
  }

  @Test
  public void testDeleteAdvisorySession_Success() {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    Long advisorySessionId = 1L;

    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    when(restTemplate.exchange(
            eq("http://localhost:8092/advisory-sessions/{id}?userId={userId}"),
            eq(HttpMethod.DELETE),
            isNull(),
            eq(Void.class),
            eq(advisorySessionId),
            eq(currentUser.getId())
    )).thenReturn(responseEntity);

    advisorySessionService.deleteAdvisorySession(advisorySessionId);

    verify(restTemplate).exchange(
            eq("http://localhost:8092/advisory-sessions/{id}?userId={userId}"),
            eq(HttpMethod.DELETE),
            isNull(),
            eq(Void.class),
            eq(advisorySessionId),
            eq(currentUser.getId())
    );
  }
}
