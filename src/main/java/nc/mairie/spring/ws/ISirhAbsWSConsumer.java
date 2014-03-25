package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.absence.dto.CompteurAsaDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifRefusDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;

public interface ISirhAbsWSConsumer {

	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	SoldeDto getSoldeAgent(String idAgent);

	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefType);

	List<MotifRefusDto> getListeMotifRefus(Integer idRefType);

	List<MotifCompteurDto> getListeMotifCompteur(Integer idRefType);

	ReturnMessageDto saveMotifRefus(String json);

	ReturnMessageDto saveMotifCompteur(String json);

	ReturnMessageDto addCompteurRecup(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurReposComp(String idAgentConnecte, String json);

	List<CompteurAsaDto> getListeCompteurs();

	ReturnMessageDto addCompteurAsaA48(String idAgentConnecte, String json);
}
