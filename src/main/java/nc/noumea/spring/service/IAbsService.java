package nc.noumea.spring.service;

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

public interface IAbsService {

	ReturnMessageDto saveAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur, List<AgentDto> list);

	ReturnMessageDto saveAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> list);

	ReturnMessageDto saveViseurApprobateur(Integer idAgent, AgentDto viseurAAjouter);

	ReturnMessageDto deleteViseurApprobateur(Integer idAgent, AgentDto viseurASupprimer);

	ReturnMessageDto saveOperateurApprobateur(Integer idAgent, AgentDto ajoutOperateur);

	List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent);

	ReturnMessageDto deleteOperateurApprobateur(Integer idAgent, AgentDto deleteOperateur);

	ReturnMessageDto saveAgentsApprobateur(Integer idAgent, List<AgentDto> listeAgentsApprobateurAbs);

	ReturnMessageDto setApprobateur(String json);

	ReturnMessageDto deleteApprobateur(String json);

	ReturnMessageDto setDelegataire(Integer idAgent, String json);

	List<AgentDto> getAgentsApprobateur(Integer idAgent);

	InputterDto getOperateursDelegataireApprobateur(Integer idAgent);

	ViseursDto getViseursApprobateur(Integer idAgent);

	Object getAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur);

	Object getAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur);

	List<MoisAlimAutoCongesAnnuelsDto> getListeMoisALimAUtoCongeAnnuel();

	List<MoisAlimAutoCongesAnnuelsDto> getListeAlimAutoCongeAnnuel(MoisAlimAutoCongesAnnuelsDto moisChoisi, boolean onlyErreur);

	List<RestitutionMassiveDto> getHistoRestitutionMassive(Integer idAgent);

	ReturnMessageDto addRestitutionMassive(Integer idAgent, String json);

	RestitutionMassiveDto getDetailsHistoRestitutionMassive(Integer idAgent, RestitutionMassiveDto restitutionMassiveById);

	List<OrganisationSyndicaleDto> getListeOrganisationSyndicale();

	List<TypeAbsenceDto> getListeRefTypeAbsenceDto(Integer idRefGroupe);

	List<RefGroupeAbsenceDto> getRefGroupeAbsence();

	List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, String listIdRefEtat, Integer idRefType, Integer idAgentRecherche, Integer idRefGroupe, boolean aValider,
			List<String> idAgentsService);

	TypeAbsenceDto getTypeAbsence(Integer idBaseHoraireAbsence);

	List<OrganisationSyndicaleDto> getListeOrganisationSyndicaleActiveByAgent(Integer idAgent, Integer idRefTypeAbsence);

	List<DemandeDto> getVisualisationHistory(Integer absId);

	ReturnMessageDto setAbsState(Integer idAgent, String json);

	ReturnMessageDto saveDemande(Integer idAgentConnecte, String json);

	DemandeDto getDureeCongeAnnuel(DemandeDto demandeDto);

	List<MotifCompteurDto> getListeMotifCompteur(Integer idRefTypeAbsence);

	ReturnMessageDto addCompteurCongeAnnuel(Integer idAgent, String json);

	ReturnMessageDto addCompteurReposComp(Integer idAgent, String json);

	ReturnMessageDto addCompteurRecup(Integer idAgent, String json);

	SoldeDto getSoldeAgent(Integer idAgent, String json);

	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin, String dateDemande, String listIdRefEtat, Integer idRefType, Integer idRefGroupeAbsence);

	List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json);

	List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoCongeAnnuelAgent(Integer idAgent);

	List<RestitutionMassiveDto> getHistoRestitutionMassiveByIdAgent(Integer idAgent);

	List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoReposCompAgent(Integer idAgent);

	List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoRecupAgent(Integer idAgent);

	ActeursDto getListeActeurs(Integer idAgent);

	ReturnMessageDto initialiseCompteurConge(Integer agentConnecte, Integer agentConcerne);

	List<CompteurDto> getListeCompteursA48();

	ReturnMessageDto addCompteurAsaA48(Integer idAgent, String json);

	ReturnMessageDto saveRepresentantAsaA52(Integer idOrganisation, String json);

	ReturnMessageDto addCompteurAsaA52(Integer idAgent, String json);

	ReturnMessageDto addCompteurAsaA53(Integer idAgent, String json);

	List<CompteurDto> getListeCompteursA53();

	ReturnMessageDto addCompteurAsaA54(Integer idAgent, String json);

	List<CompteurDto> getListeCompteursA54(Integer annee);

	ReturnMessageDto addCompteurAsaA55(Integer idAgent, String json);

	List<CompteurDto> getListeCompteursA55();

	ReturnMessageDto addCompteurAsaAmicale(Integer idAgent, String json);

	List<CompteurDto> getListeCompteursAmicale();

	ReturnMessageDto saveMotifCompteur(String json);

	List<MotifDto> getListeMotif();

	ReturnMessageDto saveMotif(String json);

	ReturnMessageDto saveTypeAbsence(Integer idAgent, String json);

	List<RefAlimCongesAnnuelsDto> getListeRefAlimCongesAnnuels(Integer idRefTypeSaisiCongeAnnuel);

	ReturnMessageDto saveRefAlimMensuelle(Integer idAgent, String json);

	List<UnitePeriodeQuotaDto> getUnitePeriodeQuota();

	ReturnMessageDto deleteTypeAbsence(Integer idAgent, Integer idRefTypeAbsence);

	ReturnMessageDto saveOrganisationSyndicale(String json);

	ReturnMessageDto createNouvelleAnneeBaseConges(Integer anneeCreation);

	List<OrganisationSyndicaleDto> getListeOSCompteursA52();

	List<CompteurDto> getListeCompteursA52(Integer idOrganisation);

	List<AgentOrganisationSyndicaleDto> getListeRepresentantA52(Integer idOrganisation);

	ReturnMessageDto dupliqueApprobateur(Integer idAgentConnecte,
			Integer idAgentSource, Integer idAgentDestinataire);

	List<DemandeDto> getListeDemandeCAWhichAddOrRemoveOnCounterAgent(Integer idAgent, Integer idAgentConcerne);
}
