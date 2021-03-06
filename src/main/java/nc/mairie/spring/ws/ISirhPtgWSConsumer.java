package nc.mairie.spring.ws;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.gestionagent.dto.DelegatorAndOperatorsDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteAnneeDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteChoixAgentDto;
import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.MotifHeureSupDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasDemandeDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurDto;
import nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurTaskDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;

public interface ISirhPtgWSConsumer {

	public ArrayList<Integer> getListeIdAgentPointage();

	// Droits
	List<ApprobateurDto> getApprobateurs(Integer idServiceADS, Integer idAgent);

	ReturnMessageDto setApprobateur(String json);

	ReturnMessageDto deleteApprobateur(String json);

	ReturnMessageDto setDelegataire(Integer idAgent, String json);

	List<AgentDto> getApprovedAgents(Integer idAgent);

	ReturnMessageDto saveApprovedAgents(Integer idAgent, List<AgentDto> listSelect);

	DelegatorAndOperatorsDto getDelegateAndOperator(Integer idAgent);

	ReturnMessageDto saveDelegateAndOperator(Integer idAgent, DelegatorAndOperatorsDto dto);

	List<AgentDto> getAgentsSaisisOperateur(Integer idAgent, Integer idOperateur);

	ReturnMessageDto saveAgentsSaisisOperateur(Integer idAgent, Integer idOperateur, List<AgentDto> listSelect);

	// Ventilation
	<T> List<T> getVentilations(Class<T> targetClass, Integer idDateVentil, Integer idRefTypePointage, String agentsJson, boolean allVentilation);

	<T> List<T> getVentilationsHistory(Class<T> targetClass, Integer mois, Integer annee, Integer idRefTypePointage, Integer idAgent, boolean allVentilation, Integer idVentilDate);

	boolean isVentilAvailable(String agentStatus);

	boolean isVentilEnCours(String agentStatus);

	VentilDateDto getVentilationEnCours(String statut);

	boolean startVentilation(Integer idAgent, Date dateVentilation, String agentsJson, String statut, String idRefTypePointage);

	boolean isValidAvailable(String agentStatus);

	boolean isValidEnCours(String agentStatus);

	ReturnMessageDto startDeversementPaie(Integer idAgent, String statut);

	boolean canStartExportEtatsPayeur(String statut);

	List<EtatsPayeurDto> getListEtatsPayeurByStatut(String statut);

	byte[] downloadFicheEtatsPayeur(Integer idEtatPayeur);

	boolean startExportEtatsPayeur(Integer idAgentExporting, String statutString);

	List<VentilErreurDto> getErreursVentilation(String type);

	List<Integer> getListeAgentsForShowVentilation(Integer idDateVentil, Integer idRefTypePointage, String statut, Date ventilationDate, String agentMin, String agentMax, boolean allVentilation);

	// Visualisation
	List<ConsultPointageDto> getVisualisationPointage(String fromDate, String toDate, List<String> idAgents, Integer idRefEtat, Integer idRefType, String typeHeureSup, String dateEtat);

	List<ConsultPointageDto> getVisualisationHistory(Integer idAgents);

	// Saisie
	FichePointageDto getSaisiePointage(Integer idAgent, String monday);

	ReturnMessageDto setSaisiePointage(Integer idAgent, FichePointageDto toSerialize);

	ReturnMessageDto setPtgState(ArrayList<Integer> idPtg, Integer idRefEtat, Integer idAgent);

	// Filtres
	List<RefEtatDto> getEtatsPointage();

	List<RefTypePointageDto> getTypesPointage();

	List<TypeAbsenceDto> getListeRefTypeAbsence();

	List<MotifHeureSupDto> getListeMotifHeureSup();

	ReturnMessageDto saveMotifHeureSup(String json);

	// Primes
	List<RefPrimeDto> getPrimes(String agentStatus);

	List<RefPrimeDto> getPrimes();

	RefPrimeDto getPrimeDetail(Integer numRubrique);

	RefPrimeDto getPrimeDetailFromRefPrime(Integer idRefPrime);

	boolean isPrimeUtilPointage(Integer numRubrique, Integer idAgent);

	List<TitreRepasDemandeDto> getVisualisationTitreRepasHistory(
			Integer idTrDemande);

	List<TitreRepasDemandeDto> getListTitreRepas(Integer idAgentConnecte,
			String fromDate, String toDate, Integer idRefEtat,
			Boolean commande, String dateMonth, Integer idServiceAds,
			Integer idAgent, List<Integer> listIdsAgent);

	ReturnMessageDto setTRState(List<TitreRepasDemandeDto> listTitreRepasDemandeDto, Integer idAgent);

	ReturnMessageDto enregistreTitreRepas(List<TitreRepasDemandeDto> dto,
			Integer idAgent);

	List<RefEtatDto> getEtatsTitreRepas();

	List<Date> getFiltreListeMois();

	ReturnMessageDto startEtatPayeurTitreRepas(Integer idAgent,InputStream fileInputStream);

	List<TitreRepasEtatPayeurDto> getListTitreRepasEtatPayeur(Integer idAgent);

	ReturnMessageDto dupliqueApprobateur(Integer idAgentConnecte,
			Integer idAgentSource, Integer idAgentDestinataire);

	ReturnMessageDto createDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto);

	ReturnMessageDto saveDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto);

	DpmIndemniteAnneeDto getDpmIndemAnneeByAnnee(Integer annee);

	List<DpmIndemniteAnneeDto> getListDpmIndemAnnee(Integer idAgent);

	List<DpmIndemniteAnneeDto> getListDpmIndemAnneeOuverte();

	List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(Integer idAgent, Integer annee, Boolean isChoixIndemnite,
			Boolean isChoixRecuperation, List<Integer> listIdsAgent);

	ReturnMessageDto saveIndemniteChoixAgent(Integer idAgentConnecte, DpmIndemniteChoixAgentDto listDto);

	ReturnMessageDto deleteIndemniteChoixAgent(Integer idAgentConnecte, Integer idDpmIndemChoixAgent);

	boolean isEtatPayeurTitreRepasEnCours();

	List<TitreRepasEtatPayeurTaskDto> getListErreurTitreRepasEtatPayeurTask();

}
