package nc.noumea.spring.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.EntiteWithAgentWithServiceDto;
import nc.mairie.gestionagent.dto.FichePosteTreeNodeDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.AutreAdministrationAgentDto;
import nc.mairie.gestionagent.eae.dto.CalculEaeInfosDto;

public interface ISirhService {


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

	CalculEaeInfosDto getDetailAffectationActiveByAgent(Integer idAgent, Integer anneeFormation);

	List<AutreAdministrationAgentDto> getListeAutreAdministrationAgent(Integer idAgent);

	EntiteWithAgentWithServiceDto getListeEntiteWithAgentWithServiceDtoByIdServiceAdsWithoutAgentConnecte(
			Integer idServiceAds, Integer idAgent, List<AgentDto> listAgentsAInclureDansArbre);

	List<AgentWithServiceDto> getListAgentsWithService(
			List<Integer> listAgentDto, Date date);

	List<AgentWithServiceDto> getListeAgentWithIndemniteForfaitTravailDPM(Set<Integer> listIdsAgent);

	/**
	 * Retourne l arbre des fiches de poste par rapport a l entite
	 * 
	 * @param idEntite Integer ID entite
	 * @param withFichesPosteNonReglemente boolean Avec FP non reglementaire ou non
	 * @return Arbre fiches de poste
	 */
	List<FichePosteTreeNodeDto> getFichePosteTreeNodeDto(Integer idEntite, boolean withFichesPosteNonReglemente);

	/**
	 * Retourne vrai si une FP est Valide Gelee ou Transitoire :
	 * parcours tout l'arbre
	 * 
	 * @param listFP List<FichePosteTreeNodeDto
	 * @param idFichePosteConcerne Integer FP que l on va passer a inactif
	 * @return boolean
	 */
	boolean isFPEnfantValideGeleeTransitoire(List<FichePosteTreeNodeDto> listFP, Integer idFichePosteConcerne);

	FichePosteTreeNodeDto getFichePosteTreeNodeDtoByIdFichePoste(List<FichePosteTreeNodeDto> listFP, Integer idFichePosteConcerne);
}
