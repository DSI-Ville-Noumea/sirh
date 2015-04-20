package nc.mairie.spring.ws;

import java.util.List;

import nc.mairie.gestionagent.absence.dto.ActeursDto;
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
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;

public interface ISirhAbsWSConsumer {

	// Filtres
	List<UnitePeriodeQuotaDto> getUnitePeriodeQuota();

	// Approbateurs / Droits
	List<ApprobateurDto> getApprobateurs(String codeService, Integer idAgent);

	ReturnMessageDto setApprobateur(String json);

	ReturnMessageDto deleteApprobateur(String json);

	ReturnMessageDto setDelegataire(Integer idAgent, String json);

	// OS
	List<OrganisationSyndicaleDto> getListeOrganisationSyndicale();

	ReturnMessageDto saveOrganisationSyndicale(String json);

	ReturnMessageDto saveRepresentantAsaA52(Integer idOrganisationSyndicale, String json);

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

	ReturnMessageDto addCompteurCongeAnnuel(Integer idAgent, String json);

	List<CompteurDto> getListeCompteursA48();

	List<CompteurDto> getListeCompteursA54();

	List<CompteurDto> getListeCompteursA55();

	List<CompteurDto> getListeCompteursA52();

	List<CompteurDto> getListeCompteursA53();

	List<HistoriqueSoldeDto> getHistoriqueCompteurAgent(Integer idAgent, Integer codeTypeAbsence, String json);

	ReturnMessageDto initialiseCompteurConge(Integer agentConnecte, Integer idAgentConcerne);

	// demandes
	List<DemandeDto> getListeDemandesAgent(Integer idAgent, String onglet, String dateDebut, String dateFin,
			String dateDemande, String listIdRefEtat, Integer idRefType, Integer idRefGroupeAbsence);

	List<DemandeDto> getListeDemandes(String dateDebut, String dateFin, String listIdRefEtat, Integer idRefType,
			Integer idAgentRecherche, Integer idRefGroupe, boolean aValider, List<String> idAgentsService);

	List<DemandeDto> getVisualisationHistory(Integer absId);

	ReturnMessageDto saveDemande(Integer idAgentConnecte, String json);

	ReturnMessageDto setAbsState(Integer idAgent, String json);

	DemandeDto getDureeCongeAnnuel(DemandeDto demandeDto);

	ReturnMessageDto addRestitutionMassive(Integer idAgent, String json);

	// alimentation auto des congés annuels
	List<MoisAlimAutoCongesAnnuelsDto> getListeMoisALimAUtoCongeAnnuel();

	List<MoisAlimAutoCongesAnnuelsDto> getListeAlimAutoCongeAnnuel(MoisAlimAutoCongesAnnuelsDto moisChoisi);

	List<RefAlimCongesAnnuelsDto> getListeRefAlimCongesAnnuels(Integer idRefTypeSaisiCongeAnnuel);

	List<RestitutionMassiveDto> getHistoRestitutionMassive(Integer idAgent);

	RestitutionMassiveDto getDetailsHistoRestitutionMassive(Integer idAgent, RestitutionMassiveDto dto);

	ReturnMessageDto saveRefAlimMensuelle(Integer idAgent, String json);

	ActeursDto getListeActeurs(Integer idAgent);

	List<OrganisationSyndicaleDto> getListeOrganisationSyndicaleActiveByAgent(
			Integer idAgent, Integer idRefTypeAbsence);

}
