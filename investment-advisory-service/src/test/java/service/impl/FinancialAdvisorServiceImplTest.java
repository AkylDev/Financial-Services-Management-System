package service.impl;

import kz.projects.ias.dto.FinancialAdvisorDTO;
import kz.projects.ias.exceptions.FinancialAdvisorNotFoundException;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.models.enums.AdvisorSpecialization;
import kz.projects.ias.repositories.FinancialAdvisorRepository;
import kz.projects.ias.service.impl.FinancialAdvisorServiceImpl;
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


public class FinancialAdvisorServiceImplTest {

  @Mock
  private FinancialAdvisorRepository financialAdvisorRepository;

  @InjectMocks
  private FinancialAdvisorServiceImpl financialAdvisorService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAddFinancialAdvisor() {
    FinancialAdvisorDTO advisorDTO = new FinancialAdvisorDTO();
    advisorDTO.setName("John Doe");
    advisorDTO.setEmail("john.doe@example.com");
    advisorDTO.setSpecialization(AdvisorSpecialization.ACCOUNTANT);

    FinancialAdvisor savedAdvisor = new FinancialAdvisor();
    savedAdvisor.setId(1L);
    savedAdvisor.setName(advisorDTO.getName());
    savedAdvisor.setEmail(advisorDTO.getEmail());
    savedAdvisor.setSpecialization(advisorDTO.getSpecialization());

    when(financialAdvisorRepository.save(any(FinancialAdvisor.class))).thenReturn(savedAdvisor);

    FinancialAdvisor result = financialAdvisorService.addFinancialAdvisor(advisorDTO);

    assertNotNull(result);
    assertEquals(savedAdvisor.getId(), result.getId());
    assertEquals(savedAdvisor.getName(), result.getName());
    assertEquals(savedAdvisor.getEmail(), result.getEmail());
    assertEquals(savedAdvisor.getSpecialization(), result.getSpecialization());
  }

  @Test
  void testGetAllAdvisors() {
    List<FinancialAdvisor> advisors = Arrays.asList(
            new FinancialAdvisor(1L, "Jane Smith", "jane.smith@example.com", AdvisorSpecialization.ACCOUNTANT),
            new FinancialAdvisor(2L, "Michael Brown", "michael.brown@example.com", AdvisorSpecialization.COACH)
    );

    when(financialAdvisorRepository.findAll()).thenReturn(advisors);

    List<FinancialAdvisor> result = financialAdvisorService.getAllAdvisors();

    assertNotNull(result);
    assertEquals(advisors.size(), result.size());
    assertEquals(advisors.get(0).getName(), result.get(0).getName());
    assertEquals(advisors.get(1).getEmail(), result.get(1).getEmail());
  }

  @Test
  void testDeleteFinancialAdvisor_Success() {
    Long advisorId = 1L;
    FinancialAdvisor advisorToDelete = new FinancialAdvisor(advisorId, "John Doe", "john.doe@example.com",
            AdvisorSpecialization.ACCOUNTANT);

    when(financialAdvisorRepository.findById(advisorId)).thenReturn(Optional.of(advisorToDelete));

    assertDoesNotThrow(() -> financialAdvisorService.deleteFinancialAdvisor(advisorId));

    verify(financialAdvisorRepository, times(1)).deleteById(advisorId);
  }

  @Test
  void testDeleteFinancialAdvisor_NotFound() {
    Long advisorId = 1L;

    when(financialAdvisorRepository.findById(advisorId)).thenReturn(Optional.empty());

    FinancialAdvisorNotFoundException exception = assertThrows(
            FinancialAdvisorNotFoundException.class,
            () -> financialAdvisorService.deleteFinancialAdvisor(advisorId)
    );

    assertEquals("Financial Advisor not found", exception.getMessage());
  }
}

