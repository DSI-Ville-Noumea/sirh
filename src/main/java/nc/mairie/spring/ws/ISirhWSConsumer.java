package nc.mairie.spring.ws;

import java.util.Date;

import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;

public interface ISirhWSConsumer {

	RefTypeSaisiCongeAnnuelDto getBaseHoraireAbsence(Integer idAgent, Date date);
}
