package kz.projects.ias.mapper;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.models.AdvisorySession;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.models.enums.RequestStatus;
import org.springframework.stereotype.Component;

@Component
public class AdvisorySessionMapper {

  public static AdvisorySessionDTO toDto(AdvisorySession session) {
    if (session == null) {
      return null;
    }

    AdvisorySessionDTO dto = new AdvisorySessionDTO();
    dto.setId(session.getId());
    dto.setAdvisoryId(session.getFinancialAdvisor() != null ? session.getFinancialAdvisor().getId() : null);
    dto.setUserId(session.getUserId());
    dto.setDate(session.getDate());
    dto.setTime(session.getTime());

    return dto;
  }

  public static AdvisorySession toEntity(AdvisorySessionDTO dto, FinancialAdvisor advisor) {
    AdvisorySession session = new AdvisorySession();
    session.setId(dto.getId());
    session.setUserId(dto.getUserId());
    session.setFinancialAdvisor(advisor);
    session.setDate(dto.getDate());
    session.setTime(dto.getTime());
    session.setStatus(RequestStatus.PENDING);
    return session;
  }

}
