package services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.projects.ams.models.User;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.UserService;
import kz.projects.ams.services.impl.UserAdvisorySessionServiceImpl;
import kz.projects.commonlib.dto.AdvisorySessionDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAdvisorySessionServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private NotificationEventProducer notificationEventProducer;

  @InjectMocks
  private UserAdvisorySessionServiceImpl advisorySessionService;

  private MockWebServer mockWebServer;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(mockWebServer.url("/").toString());
    advisorySessionService = new UserAdvisorySessionServiceImpl(webClientBuilder, userService, notificationEventProducer);

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @AfterEach
  public void tearDown() throws Exception {
    mockWebServer.shutdown();
  }

  @Test
  public void testOrderAdvisorySession_Success() throws Exception {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    AdvisorySessionDTO request = new AdvisorySessionDTO(null, null, 10L, null, null);

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(201)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(new ObjectMapper().writeValueAsString(request))
    );

    AdvisorySessionDTO result = advisorySessionService.orderAdvisorySession(request);

    assertNotNull(result);
    assertEquals(request.advisoryId(), result.advisoryId());

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);

    assertNotNull(recordedRequest);
    assertEquals("/advisory-sessions", recordedRequest.getPath());
  }

  @Test
  public void testGetAdvisorySessionsPlanned_Success() throws Exception {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    List<AdvisorySessionDTO> mockSessions = Arrays.asList(
            new AdvisorySessionDTO(1L, 1L, 2L, LocalDate.now(), LocalTime.of(9, 0)),
            new AdvisorySessionDTO(2L, 1L, 3L, LocalDate.now(), LocalTime.of(10, 0))
    );

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(objectMapper.writeValueAsString(mockSessions))
    );

    List<AdvisorySessionDTO> result = advisorySessionService.getAdvisorySessionsPlanned();

    verify(userService).getCurrentSessionUser();
    assertNotNull(result);
    assertEquals(2, result.size());

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/advisory-sessions?userId=1", recordedRequest.getPath());
  }

  @Test
  public void testGetAdvisersSessions_Success() throws Exception {
    User currentUser = new User();
    currentUser.setEmail("test@example.com");
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    List<AdvisorySessionDTO> mockSessions = Arrays.asList(
            new AdvisorySessionDTO(1L, 1L, 2L, LocalDate.now(), LocalTime.of(9, 0)),
            new AdvisorySessionDTO(2L, 1L, 3L, LocalDate.now(), LocalTime.of(10, 0))
    );

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(objectMapper.writeValueAsString(mockSessions))
    );

    List<AdvisorySessionDTO> result = advisorySessionService.getAdvisersSessions();

    verify(userService).getCurrentSessionUser();
    assertNotNull(result);
    assertEquals(2, result.size());

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/advisory-sessions/advisers?email=test@example.com", recordedRequest.getPath());
  }

  @Test
  public void testRescheduleAdvisorySession_Success() throws Exception {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    AdvisorySessionDTO request = new AdvisorySessionDTO(1L, 1L, 2L, LocalDate.now(), LocalTime.of(9, 0));

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
    );

    advisorySessionService.rescheduleAdvisorySession(request.id(), request);

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/advisory-sessions", recordedRequest.getPath());
  }

  @Test
  public void testDeleteAdvisorySession_Success() throws Exception {
    User currentUser = new User();
    currentUser.setId(1L);
    when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    Long advisorySessionId = 1L;

    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(204)
    );

    advisorySessionService.deleteAdvisorySession(advisorySessionId);

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/advisory-sessions/" + advisorySessionId + "?userId=1", recordedRequest.getPath());
  }
}
