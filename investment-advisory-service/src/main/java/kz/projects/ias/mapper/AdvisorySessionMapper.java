package kz.projects.ias.mapper;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.module.AdvisorySession;
import org.springframework.stereotype.Component;

@Component
public class AdvisorySessionMapper {

  public AdvisorySessionDTO toDto(AdvisorySession advisorySession){
    if (advisorySession == null) {
      return null;
    }

    AdvisorySessionDTO advisorySessionDTO = new AdvisorySessionDTO();
    advisorySessionDTO.setAdvisoryId(advisorySession.getFinancialAdvisor().getId());
    advisorySessionDTO.setUserId(advisorySession.getUserId());
    advisorySessionDTO.setDate(advisorySession.getDate());
    advisorySessionDTO.setTime(advisorySession.getTime());

    return advisorySessionDTO;
  }

}
