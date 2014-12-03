package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.absence.dto.CompteurAsaDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.UnitePeriodeQuotaDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;

public interface ISirhAbsWSConsumer {

	// Filtres
	List<UnitePeriodeQuotaDto> getUnitePeriodeQuota();

	// Approbateurs
	List<AgentWithServiceDto> getApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(String json);

	// OS
	List<OrganisationSyndicaleDto> getListeOrganisationSyndicale();

	ReturnMessageDto saveOrganisationSyndicale(String json);

	// Type d'absences
	List<RefGroupeAbsenceDto> getRefGroupeAbsence();

	List<TypeAbsenceDto> getListeRefTypeAbsenceDto(Integer idRefGroupe);

	ReturnMessageDto saveTypeAbsence(Integer idAgentConnecte, String json);

	ReturnMessageDto deleteTypeAbsence(Integer idAgentConnecte, Integer idRefTypeAbsence);

	TypeAbsenceDto getTypeAbsence(Integer idBaseHoraireAbsence);

	// Motifs
	List<MotifDto> getListeMotif();

	ReturnMessageDto saveMotif(String json);

	// Motif alim compteur
	List<MotifCompteurDto> getListeMotifCompteur(Integer idRefType);

	ReturnMessageDto saveMotifCompteur(String json);

	// solde
	SoldeDto getSoldeAgent(Integer idAgent, String json);

	// compteurs
	ReturnMessageDto addCompteurRecup(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurReposComp(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA48(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA54(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA55(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA52(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA53(Integer idAgentConnecte, String json);

	List<CompteurAsaDto> getListeCompteursA48();

	List<CompteurAsaDto> getListeCompteursA54();

	List<CompteurAsaDto> getListeCompteursA55();

	List<CompteurAsaDto> getListeCompteursA52();

	List<CompteurAsaDto> getListeCompteursA53();

	List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json);

	ReturnMessageDto initialiseCompteurConge(Integer agentConnecte, Integer idAgentConcerne);

	// demandes
	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefType);

	List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, Integer idRefEtat, Integer idRefType,
			Integer idAgentRecherche, Integer idRefGroupe, boolean aValider);

	List<DemandeDto> getVisualisationHistory(Integer absId);

	ReturnMessageDto saveDemande(Integer idAgentConnecte, String json);

	ReturnMessageDto setAbsState(Integer idAgent, String json);

	DemandeDto getDureeCongeAnnuel(DemandeDto demandeDto);

}
