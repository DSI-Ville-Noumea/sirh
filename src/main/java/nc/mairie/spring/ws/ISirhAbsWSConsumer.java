package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.absence.dto.CompteurAsaDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;

public interface ISirhAbsWSConsumer {

	// Approbateurs
	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	// OS
	List<OrganisationSyndicaleDto> getListeOrganisationSyndicale();

	ReturnMessageDto saveOrganisationSyndicale(String json);

	// Type d'absences
	List<TypeAbsenceDto> getListeRefTypeAbsenceDto();

	// Motifs
	List<MotifDto> getListeMotif();

	ReturnMessageDto saveMotif(String json);

	// Motif alim compteur
	List<MotifCompteurDto> getListeMotifCompteur(Integer idRefType);

	ReturnMessageDto saveMotifCompteur(String json);

	// solde
	SoldeDto getSoldeAgent(String idAgent, String json);

	// compteurs
	ReturnMessageDto addCompteurRecup(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurReposComp(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA48(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA54(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA55(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA52(String idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA53(String idAgentConnecte, String json);

	List<CompteurAsaDto> getListeCompteursA48();

	List<CompteurAsaDto> getListeCompteursA54();

	List<CompteurAsaDto> getListeCompteursA55();

	List<CompteurAsaDto> getListeCompteursA52();

	List<CompteurAsaDto> getListeCompteursA53();

	List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json);

	// demandes
	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefType);

	List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, Integer idRefEtat, Integer idRefType,
			Integer idAgentRecherche);

	List<DemandeDto> getVisualisationHistory(int absId);

	ReturnMessageDto saveDemande(String idAgentConnecte, String json);

	ReturnMessageDto setAbsState(Integer idAgent, String json);

}
