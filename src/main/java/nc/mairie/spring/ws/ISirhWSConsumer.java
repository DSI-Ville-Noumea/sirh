package nc.mairie.spring.ws;

import java.util.Date;

import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
import nc.mairie.gestionagent.dto.DateAvctDto;

public interface ISirhWSConsumer {

	RefTypeSaisiCongeAnnuelDto getBaseHoraireAbsence(Integer idAgent, Date date);

	DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception;

	boolean miseAJourArbreFDP();

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
}
