package nc.noumea.spring.service;

import java.util.Date;
import java.util.List;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.spring.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SirhService implements ISirhService {

	@Autowired
	private ISirhWSConsumer sirhConsumer;

	@Override
	public byte[] downloadAccompagnement(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception {
		return sirhConsumer.downloadAccompagnement(csvIdSuiviMedical, typePopulation, mois, annee);
	}

	@Override
	public byte[] downloadConvocation(String csvIdSuiviMedical, String typePopulation, String mois, String annee)
			throws Exception {
		return sirhConsumer.downloadConvocation(csvIdSuiviMedical, typePopulation, mois, annee);
	}

	@Override
	public byte[] downloadContrat(Integer idAgent, Integer idContrat) throws Exception {
		return sirhConsumer.downloadContrat(idAgent, idContrat);
	}

	@Override
	public byte[] downloadNoteService(Integer idAffectation, String typeDocument) throws Exception {
		return sirhConsumer.downloadNoteService(idAffectation, typeDocument);
	}

	@Override
	public byte[] downloadFichePoste(Integer idFichePoste) throws Exception {
		return sirhConsumer.downloadFichePoste(idFichePoste);
	}

	@Override
	public DateAvctDto getCalculDateAvct(Integer idAgent) throws Exception {
		return sirhConsumer.getCalculDateAvct(idAgent);
	}

	@Override
	public byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte)
			throws Exception {
		return sirhConsumer.downloadArrete(csvAgents, isChangementClasse, anneeAvct, isAffecte);
	}

	@Override
	public byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean avisEAE, String format)
			throws Exception {
		return sirhConsumer.downloadTableauAvancement(idCap, idCadreEmploi, avisEAE, format);
	}

	@Override
	public BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateLundi) {
		return sirhConsumer.getBaseHorairePointageAgent(idAgent, dateLundi);
	}

	@Override
	public boolean miseAJourArbreFDP() {
		return sirhConsumer.miseAJourArbreFDP();
	}

	@Override
	public List<AgentDto> getAgentsSubordonnes(Integer idAgent) {
		return sirhConsumer.getAgentsSubordonnes(idAgent);
	}

	@Override
	public ReturnMessageDto deleteFDP(Integer idFichePoste, Integer idAgent) {
		return sirhConsumer.deleteFDP(idFichePoste, idAgent);
	}

}