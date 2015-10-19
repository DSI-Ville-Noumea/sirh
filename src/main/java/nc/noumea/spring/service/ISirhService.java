package nc.noumea.spring.service;

import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementConvColDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.technique.Transaction;

public interface ISirhService {

	byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee) throws Exception;

	byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee) throws Exception;

	byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception;

	byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception;

	byte[] downloadFichePoste(Integer idFichePoste) throws Exception;

	DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception;

	byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte) throws Exception;

	byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format) throws Exception;

	boolean miseAJourArbreFDP();

	List<AgentDto> getAgentsSubordonnes(Integer idAgent);

	ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent);

	BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateDebut);

	AvancementConvCol calculAvancementConventionCollective(Transaction aTransaction, Agent agent, String annee, IAdsService adsService, FichePosteDao ficheDao, AffectationDao affDao) throws Exception;

	ReturnMessageDto isAvancementConventionCollective(Transaction aTransaction, Agent agent) throws Exception;

	List<Agent> listAgentAvctConvCol(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception;

	boolean creerAvancementConventionCollective(AvancementConvCol avct, AvancementConvColDao convColDa);

	Prime getNewPrimeConventionCollective(Transaction aTransaction, Agent agent, AvancementConvCol avct) throws Exception;

	boolean isPrimeAvctConvColSimu(Transaction aTransaction, Agent agent, AvancementConvCol avct) throws Exception;
}
