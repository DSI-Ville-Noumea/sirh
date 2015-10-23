package nc.noumea.spring.service;

import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementContractuelsDao;
import nc.mairie.spring.dao.metier.avancement.AvancementConvColDao;
import nc.mairie.spring.dao.metier.avancement.AvancementDetachesDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
import nc.mairie.technique.Transaction;

public interface IAvancementService {

	// CONVENTIONS COLLECTIVES

	AvancementConvCol calculAvancementConventionCollective(Transaction aTransaction, Agent agent, String annee, IAdsService adsService, FichePosteDao ficheDao, AffectationDao affDao) throws Exception;

	ReturnMessageDto isAvancementConventionCollective(Transaction aTransaction, Agent agent) throws Exception;

	List<Agent> listAgentAvctConvCol(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception;

	boolean creerAvancementConventionCollective(AvancementConvCol avct, AvancementConvColDao convColDa);

	Prime getNewPrimeConventionCollective(Transaction aTransaction, Agent agent, AvancementConvCol avct) throws Exception;

	boolean isPrimeAvctConvColSimu(Transaction aTransaction, Agent agent, AvancementConvCol avct) throws Exception;

	// CONTRACTUELS

	ReturnMessageDto isAvancementContractuel(Transaction aTransaction, Agent agent) throws Exception;

	List<Agent> listAgentAvctContractuel(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception;

	boolean creerAvancementContractuel(AvancementContractuels avct, AvancementContractuelsDao avancementContractuelsDao);

	AvancementContractuels calculAvancementContractuel(Transaction aTransaction, Agent agent, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			boolean avctPrev) throws Exception;

	boolean isCarriereContractuelSimu(AvancementContractuels avct, Carriere carr) throws Exception;

	Carriere getNewCarriereContractuel(Transaction aTransaction, Agent agent, AvancementContractuels avct, Carriere carr);

	// DETACHES

	ReturnMessageDto isAvancementDetache(Transaction aTransaction, Agent agent) throws Exception;

	List<Agent> listAgentAvctDetache(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception;

	AvancementDetaches calculAvancementDetache(Transaction aTransaction, Agent a, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			AutreAdministrationAgentDao autreAdministrationAgentDao, boolean avctPrev) throws Exception;

	boolean creerAvancementDetache(AvancementDetaches avct, AvancementDetachesDao avancementDetachesDao);

	boolean isCarriereDetacheSimu(AvancementDetaches avct, Carriere carr);

	Carriere getNewCarriereDetache(Transaction aTransaction, Agent agent, AvancementDetaches avct, Carriere carr, String dateAvct) throws Exception;

	// FONCTIONNAIRES

	ReturnMessageDto isAvancementFonctionnaire(Transaction aTransaction, Agent agent) throws Exception;

	List<Agent> listAgentAvctFonctionnaire(Transaction aTransaction, String idServiceAds, String annee, IAdsService adsService, AgentDao agentDao) throws Exception;

	AvancementFonctionnaires calculAvancementFonctionnaire(Transaction aTransaction, Agent a, String annee, IAdsService adsService, FichePosteDao fichePosteDao, AffectationDao affectationDao,
			AutreAdministrationAgentDao autreAdministrationAgentDao, MotifAvancementDao motifAvancementDao, AvisCapDao avisCapDao, boolean avctPrev) throws Exception;

	boolean creerAvancementFonctionnaire(AvancementFonctionnaires avct, AvancementFonctionnairesDao avancementFonctionnairesDao);

	boolean isCarriereFonctionnaireSimu(Carriere carr);

	Carriere getNewCarriereFonctionnaire(Transaction aTransaction, Agent agent, AvancementFonctionnaires avct, Carriere carr, AvancementFonctionnairesDao avancementFonctionnairesDao,
			String idAvisEmp, Date dateAvctFinale) throws Exception;

	void calculAccBmFonctionnaire(Transaction aTransaction, AvancementFonctionnaires avct, Carriere ancienneCarriere, Carriere nouvelleCarriere, String libCourtAvisCap,
			AvancementFonctionnairesDao avancementFonctionnaireDao) throws Exception;

}
