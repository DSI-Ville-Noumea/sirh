package nc.mairie.spring.ws;

import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;

public interface ISirhWSConsumer {

	DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception;

	boolean miseAJourArbreFDP();

	ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent);

	// BIRT
	byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format) throws Exception;

	byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte)
			throws Exception;

	byte[] downloadFichePoste(Integer idFichePoste) throws Exception;

	byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception;

	byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception;

	byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception;

	byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception;

	// pour la gestion des droits

	List<AgentDto> getAgentsSubordonnes(Integer idAgent);

	List<BaseHorairePointageDto> getListBaseHorairePointageAgent(
			Integer idAgent, Date dateDebut, Date dateFin);
}
