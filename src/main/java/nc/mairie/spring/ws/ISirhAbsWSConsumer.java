package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.absence.dto.ActeursDto;
import nc.mairie.gestionagent.absence.dto.AgentOrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.MoisAlimAutoCongesAnnuelsDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.RefAlimCongesAnnuelsDto;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.RestitutionMassiveDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.UnitePeriodeQuotaDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.InputterDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.dto.ViseursDto;

public interface ISirhAbsWSConsumer {

	// Filtres
	List<UnitePeriodeQuotaDto> getUnitePeriodeQuota();

	// Approbateurs / Droits
	List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent);

	ReturnMessageDto setApprobateur(String json);

	ReturnMessageDto deleteApprobateur(String json);

	ReturnMessageDto setDelegataire(Integer idAgent, String json);

	List<AgentDto> getAgentsApprobateur(Integer idAgentApprobateur);

	ReturnMessageDto saveAgentsApprobateur(Integer idAgent, List<AgentDto> listSelect);

	InputterDto getOperateursDelegataireApprobateur(Integer idAgentApprobateur);

	ReturnMessageDto saveOperateurApprobateur(Integer idAgent, AgentDto dto);

	ReturnMessageDto deleteOperateurApprobateur(Integer idAgent, AgentDto dto);

	ViseursDto getViseursApprobateur(Integer idAgentApprobateur);

	ReturnMessageDto saveViseurApprobateur(Integer idAgent, AgentDto dto);

	ReturnMessageDto deleteViseurApprobateur(Integer idAgent, AgentDto dto);

	List<AgentDto> getAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur);

	ReturnMessageDto saveAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> listSelect);

	// OS
	List<OrganisationSyndicaleDto> getListeOrganisationSyndicale();

	ReturnMessageDto saveOrganisationSyndicale(String json);

	ReturnMessageDto saveRepresentantAsaA52(Integer idOrganisationSyndicale, String json);

	List<OrganisationSyndicaleDto> getListeOSCompteursA52();

	List<AgentOrganisationSyndicaleDto> getListeRepresentantA52(Integer idOrganisation);

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

	ReturnMessageDto addCompteurAsaAmicale(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA52(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurAsaA53(Integer idAgentConnecte, String json);

	ReturnMessageDto addCompteurCongeAnnuel(Integer idAgent, String json);

	List<CompteurDto> getListeCompteursA48();

	List<CompteurDto> getListeCompteursA54();

	List<CompteurDto> getListeCompteursA55();

	List<CompteurDto> getListeCompteursAmicale();

	List<CompteurDto> getListeCompteursA52(Integer idOrganisation);

	List<CompteurDto> getListeCompteursA53();

	List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json);

	ReturnMessageDto initialiseCompteurConge(Integer agentConnecte, Integer idAgentConcerne);

	// demandes
	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin, String dateDemande, String listIdRefEtat, Integer idRefType, Integer idRefGroupeAbsence);

	List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, String listIdRefEtat, Integer idRefType, Integer idAgentRecherche, Integer idRefGroupe, boolean aValider,
			List<String> idAgentsService);

	List<DemandeDto> getVisualisationHistory(Integer absId);

	ReturnMessageDto saveDemande(Integer idAgentConnecte, String json);

	ReturnMessageDto setAbsState(Integer idAgent, String json);

	DemandeDto getDureeCongeAnnuel(DemandeDto demandeDto);

	ReturnMessageDto addRestitutionMassive(Integer idAgent, String json);

	// alimentation auto des congés annuels
	List<MoisAlimAutoCongesAnnuelsDto> getListeMoisALimAUtoCongeAnnuel();

	List<MoisAlimAutoCongesAnnuelsDto> getListeAlimAutoCongeAnnuel(MoisAlimAutoCongesAnnuelsDto moisChoisi, boolean onlyErreur);

	List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoCongeAnnuelAgent(Integer idAgent);

	List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoRecupAgent(Integer idAgent);

	List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoReposCompAgent(Integer idAgent);

	List<RefAlimCongesAnnuelsDto> getListeRefAlimCongesAnnuels(Integer idRefTypeSaisiCongeAnnuel);

	List<RestitutionMassiveDto> getHistoRestitutionMassive(Integer idAgent);

	RestitutionMassiveDto getDetailsHistoRestitutionMassive(Integer idAgent, RestitutionMassiveDto dto);

	ReturnMessageDto saveRefAlimMensuelle(Integer idAgent, String json);

	ActeursDto getListeActeurs(Integer idAgent);

	List<OrganisationSyndicaleDto> getListeOrganisationSyndicaleActiveByAgent(Integer idAgent, Integer idRefTypeAbsence);

	// bases congés
	ReturnMessageDto createNouvelleAnneeBaseConges(Integer anneeCreation);

	List<RestitutionMassiveDto> getHistoRestitutionMassiveByIdAgent(Integer idAgent);

	List<AgentDto> getAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur);

	ReturnMessageDto saveAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur, List<AgentDto> listSelect);

	ReturnMessageDto dupliqueApprobateur(Integer idAgentConnecte,
			Integer idAgentSource, Integer idAgentDestinataire);

	List<DemandeDto> getListeDemandeCAWhichAddOrRemoveOnCounterAgent(Integer idAgentConnecte, Integer idAgentConcerne);

}
