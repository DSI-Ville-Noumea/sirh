package nc.mairie.spring.ws;

import java.util.Date;
import java.util.List;

import nc.mairie.abs.dto.DemandeDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.SoldeDto;

public interface ISirhAbsWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	SoldeDto getSoldeAgent(String idAgent);

	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, Date dateDebut, Date dateFin,
			Date dateDemande, Integer idRefEtat, Integer idRefType);
}
