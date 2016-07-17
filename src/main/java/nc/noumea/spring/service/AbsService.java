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
import nc.mairie.spring.ws.ISirhAbsWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbsService implements IAbsService {

	@Autowired
	private ISirhAbsWSConsumer absConsumer;

	@Override
	public ReturnMessageDto saveAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur, List<AgentDto> list) {
		return absConsumer.saveAgentsViseur(idAgentApprobateur, idAgentViseur, list);
	}

	@Override
	public ReturnMessageDto saveAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> list) {
		return absConsumer.saveAgentsOperateur(idAgentApprobateur, idAgentOperateur, list);
	}

	@Override
	public ReturnMessageDto saveViseurApprobateur(Integer idAgent, AgentDto viseurAAjouter) {
		return absConsumer.saveViseurApprobateur(idAgent, viseurAAjouter);
	}

	@Override
	public ReturnMessageDto deleteViseurApprobateur(Integer idAgent, AgentDto viseurASupprimer) {
		return absConsumer.deleteViseurApprobateur(idAgent, viseurASupprimer);
	}

	@Override
	public ReturnMessageDto saveOperateurApprobateur(Integer idAgent, AgentDto ajoutOperateur) {
		return absConsumer.saveOperateurApprobateur(idAgent, ajoutOperateur);
	}

	@Override
	public List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent) {
		return absConsumer.getApprobateurs(idServiceADS, idAgent);
	}

	@Override
	public ReturnMessageDto deleteOperateurApprobateur(Integer idAgent, AgentDto deleteOperateur) {
		return absConsumer.deleteOperateurApprobateur(idAgent, deleteOperateur);
	}

	@Override
	public ReturnMessageDto saveAgentsApprobateur(Integer idAgent, List<AgentDto> listeAgentsApprobateurAbs) {
		return absConsumer.saveAgentsApprobateur(idAgent, listeAgentsApprobateurAbs);
	}

	@Override
	public ReturnMessageDto setApprobateur(String json) {
		return absConsumer.setApprobateur(json);
	}

	@Override
	public ReturnMessageDto deleteApprobateur(String json) {
		return absConsumer.deleteApprobateur(json);
	}

	@Override
	public ReturnMessageDto setDelegataire(Integer idAgent, String json) {
		return absConsumer.setDelegataire(idAgent, json);
	}

	@Override
	public List<AgentDto> getAgentsApprobateur(Integer idAgent) {
		return absConsumer.getAgentsApprobateur(idAgent);
	}

	@Override
	public InputterDto getOperateursDelegataireApprobateur(Integer idAgent) {
		return absConsumer.getOperateursDelegataireApprobateur(idAgent);
	}

	@Override
	public ReturnMessageDto dupliqueApprobateur(Integer idAgentConnecte, Integer idAgentSource, Integer idAgentDestinataire) {
		return absConsumer.dupliqueApprobateur(idAgentConnecte, idAgentSource, idAgentDestinataire);
	}

	@Override
	public ViseursDto getViseursApprobateur(Integer idAgent) {
		return absConsumer.getViseursApprobateur(idAgent);
	}

	@Override
	public Object getAgentsOperateur(Integer idAgentApprobateur, Integer idAgentOperateur) {
		return absConsumer.getAgentsOperateur(idAgentApprobateur, idAgentOperateur);
	}

	@Override
	public Object getAgentsViseur(Integer idAgentApprobateur, Integer idAgentViseur) {
		return absConsumer.getAgentsViseur(idAgentApprobateur, idAgentViseur);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getListeMoisALimAUtoCongeAnnuel() {
		return absConsumer.getListeMoisALimAUtoCongeAnnuel();
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getListeAlimAutoCongeAnnuel(MoisAlimAutoCongesAnnuelsDto moisChoisi, boolean onlyErreur) {
		return absConsumer.getListeAlimAutoCongeAnnuel(moisChoisi, onlyErreur);
	}

	@Override
	public List<RestitutionMassiveDto> getHistoRestitutionMassive(Integer idAgent) {
		return absConsumer.getHistoRestitutionMassive(idAgent);
	}

	@Override
	public ReturnMessageDto addRestitutionMassive(Integer idAgent, String json) {
		return absConsumer.addRestitutionMassive(idAgent, json);
	}

	@Override
	public RestitutionMassiveDto getDetailsHistoRestitutionMassive(Integer idAgent, RestitutionMassiveDto dto) {
		return absConsumer.getDetailsHistoRestitutionMassive(idAgent, dto);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		return absConsumer.getListeOrganisationSyndicale();
	}

	@Override
	public List<TypeAbsenceDto> getListeRefTypeAbsenceDto(Integer idRefGroupe) {
		return absConsumer.getListeRefTypeAbsenceDto(idRefGroupe);
	}

	@Override
	public List<RefGroupeAbsenceDto> getRefGroupeAbsence() {
		return absConsumer.getRefGroupeAbsence();
	}

	@Override
	public List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, String listIdRefEtat, Integer idRefType, Integer idAgentRecherche, Integer idRefGroupe, boolean aValider,
			List<String> idAgentsService) {
		return absConsumer.getListeDemandes(dateDebut, dateFin, listIdRefEtat, idRefType, idAgentRecherche, idRefGroupe, aValider, idAgentsService);
	}

	@Override
	public TypeAbsenceDto getTypeAbsence(Integer idBaseHoraireAbsence) {
		return absConsumer.getTypeAbsence(idBaseHoraireAbsence);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOrganisationSyndicaleActiveByAgent(Integer idAgent, Integer idRefTypeAbsence) {
		return absConsumer.getListeOrganisationSyndicaleActiveByAgent(idAgent, idRefTypeAbsence);
	}

	@Override
	public List<DemandeDto> getVisualisationHistory(Integer absId) {
		return absConsumer.getVisualisationHistory(absId);
	}

	@Override
	public ReturnMessageDto setAbsState(Integer idAgent, String json) {
		return absConsumer.setAbsState(idAgent, json);
	}

	@Override
	public ReturnMessageDto saveDemande(Integer idAgentConnecte, String json) {
		return absConsumer.saveDemande(idAgentConnecte, json);
	}

	@Override
	public DemandeDto getDureeCongeAnnuel(DemandeDto demandeDto) {
		return absConsumer.getDureeCongeAnnuel(demandeDto);
	}

	@Override
	public List<MotifCompteurDto> getListeMotifCompteur(Integer idRefTypeAbsence) {
		return absConsumer.getListeMotifCompteur(idRefTypeAbsence);
	}

	@Override
	public ReturnMessageDto addCompteurCongeAnnuel(Integer idAgent, String json) {
		return absConsumer.addCompteurCongeAnnuel(idAgent, json);
	}

	@Override
	public ReturnMessageDto addCompteurReposComp(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurReposComp(idAgentConnecte, json);
	}

	@Override
	public ReturnMessageDto addCompteurRecup(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurRecup(idAgentConnecte, json);
	}

	@Override
	public SoldeDto getSoldeAgent(Integer idAgent, String json) {
		return absConsumer.getSoldeAgent(idAgent, json);
	}

	@Override
	public List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin, String dateDemande, String listIdRefEtat, Integer idRefType,
			Integer idRefGroupeAbsence) {
		return absConsumer.getListeDemandesAgent(idAgent, onglet, dateDebut, dateFin, dateDemande, listIdRefEtat, idRefType, idRefGroupeAbsence);
	}

	@Override
	public List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json) {
		return absConsumer.getHistoriqueCompteurAgent(idAgent, codeTypeAbsence, json);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoCongeAnnuelAgent(Integer idAgent) {
		return absConsumer.getHistoriqueAlimAutoCongeAnnuelAgent(idAgent);
	}

	@Override
	public List<RestitutionMassiveDto> getHistoRestitutionMassiveByIdAgent(Integer idAgent) {
		return absConsumer.getHistoRestitutionMassiveByIdAgent(idAgent);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoReposCompAgent(Integer idAgent) {
		return absConsumer.getHistoriqueAlimAutoReposCompAgent(idAgent);
	}

	@Override
	public List<MoisAlimAutoCongesAnnuelsDto> getHistoriqueAlimAutoRecupAgent(Integer idAgent) {
		return absConsumer.getHistoriqueAlimAutoRecupAgent(idAgent);
	}

	@Override
	public ActeursDto getListeActeurs(Integer idAgent) {
		return absConsumer.getListeActeurs(idAgent);
	}

	@Override
	public ReturnMessageDto initialiseCompteurConge(Integer agentConnecte, Integer idAgentConcerne) {
		return absConsumer.initialiseCompteurConge(agentConnecte, idAgentConcerne);
	}

	@Override
	public List<CompteurDto> getListeCompteursA48(Integer annee) {
		return absConsumer.getListeCompteursA48(annee);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA48(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA48(idAgentConnecte, json);
	}

	@Override
	public ReturnMessageDto saveRepresentantAsaA52(Integer idOrganisation, String json) {
		return absConsumer.saveRepresentantAsaA52(idOrganisation, json);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA52(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA52(idAgentConnecte, json);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA53(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA53(idAgentConnecte, json);
	}

	@Override
	public List<CompteurDto> getListeCompteursA53() {
		return absConsumer.getListeCompteursA53();
	}

	@Override
	public ReturnMessageDto addCompteurAsaA54(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA54(idAgentConnecte, json);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA54ByList(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA54ByList(idAgentConnecte, json);
	}

	@Override
	public List<CompteurDto> getListeCompteursA54(Integer annee) {
		return absConsumer.getListeCompteursA54(annee);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA55(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA55(idAgentConnecte, json);
	}

	@Override
	public List<CompteurDto> getListeCompteursA55() {
		return absConsumer.getListeCompteursA55();
	}

	@Override
	public ReturnMessageDto saveMotifCompteur(String json) {
		return absConsumer.saveMotifCompteur(json);
	}

	@Override
	public List<MotifDto> getListeMotif() {
		return absConsumer.getListeMotif();
	}

	@Override
	public ReturnMessageDto saveMotif(String json) {
		return absConsumer.saveMotif(json);
	}

	@Override
	public ReturnMessageDto saveTypeAbsence(Integer idAgentConnecte, String json) {
		return absConsumer.saveTypeAbsence(idAgentConnecte, json);
	}

	@Override
	public List<RefAlimCongesAnnuelsDto> getListeRefAlimCongesAnnuels(Integer idRefTypeSaisiCongeAnnuel) {
		return absConsumer.getListeRefAlimCongesAnnuels(idRefTypeSaisiCongeAnnuel);
	}

	@Override
	public ReturnMessageDto saveRefAlimMensuelle(Integer idAgent, String json) {
		return absConsumer.saveRefAlimMensuelle(idAgent, json);
	}

	@Override
	public List<UnitePeriodeQuotaDto> getUnitePeriodeQuota() {
		return absConsumer.getUnitePeriodeQuota();
	}

	@Override
	public ReturnMessageDto deleteTypeAbsence(Integer idAgentConnecte, Integer idRefTypeAbsence) {
		return absConsumer.deleteTypeAbsence(idAgentConnecte, idRefTypeAbsence);
	}

	@Override
	public ReturnMessageDto saveOrganisationSyndicale(String json) {
		return absConsumer.saveOrganisationSyndicale(json);
	}

	@Override
	public ReturnMessageDto createNouvelleAnneeBaseConges(Integer anneeCreation) {
		return absConsumer.createNouvelleAnneeBaseConges(anneeCreation);
	}

	@Override
	public List<OrganisationSyndicaleDto> getListeOSCompteursA52() {
		return absConsumer.getListeOSCompteursA52();
	}

	@Override
	public List<CompteurDto> getListeCompteursA52(Integer idOrganisation) {
		return absConsumer.getListeCompteursA52(idOrganisation);
	}

	@Override
	public List<AgentOrganisationSyndicaleDto> getListeRepresentantA52(Integer idOrganisation) {
		return absConsumer.getListeRepresentantA52(idOrganisation);
	}

	@Override
	public ReturnMessageDto addCompteurAsaAmicale(Integer idAgent, String json) {
		return absConsumer.addCompteurAsaAmicale(idAgent, json);
	}

	@Override
	public List<CompteurDto> getListeCompteursAmicale() {
		return absConsumer.getListeCompteursAmicale();
	}

	@Override
	public List<DemandeDto> getListeDemandeCAWhichAddOrRemoveOnCounterAgent(Integer idAgent, Integer idAgentConcerne) {
		return absConsumer.getListeDemandeCAWhichAddOrRemoveOnCounterAgent(idAgent, idAgentConcerne);
	}

	@Override
	public ReturnMessageDto addCompteurAsaA48ByList(Integer idAgentConnecte, String json) {
		return absConsumer.addCompteurAsaA48ByList(idAgentConnecte, json);
	}

}
