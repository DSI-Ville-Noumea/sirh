package nc.noumea.spring.service;

import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;

public interface ISirhService {

	byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception;

	byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception;

	byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception;

	byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception;

	byte[] downloadFichePoste(Integer idFichePoste) throws Exception;

	DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception;

	byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte)
			throws Exception;

	byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format) throws Exception;

	BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateLundi);

	boolean miseAJourArbreFDP();

	List<AgentDto> getAgentsSubordonnes(Integer idAgent);

	ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent);
}