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

    return new AdvisorySessionDTO(
            session.getId(),
            session.getUserId(),
            session.getFinancialAdvisor() != null ? session.getFinancialAdvisor().getId() : null,
            session.getDate(),
            session.getTime()
    );
  }

  public static AdvisorySession toEntity(AdvisorySessionDTO dto, FinancialAdvisor advisor) {
    AdvisorySession session = new AdvisorySession();
    session.setId(dto.id());
    session.setUserId(dto.userId());
    session.setFinancialAdvisor(advisor);
    session.setDate(dto.date());
    session.setTime(dto.time());
    session.setStatus(RequestStatus.PENDING);
    return session;
  }

}
