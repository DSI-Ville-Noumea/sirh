package nc.noumea.spring.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.BaseHorairePointageDto;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.EntiteWithAgentWithServiceDto;
import nc.mairie.gestionagent.dto.FichePosteTreeNodeDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.AutreAdministrationAgentDto;
import nc.mairie.gestionagent.eae.dto.CalculEaeInfosDto;
import nc.mairie.spring.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SirhService implements ISirhService {

	@Autowired
	private ISirhWSConsumer sirhConsumer;

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
	public byte[] downloadArrete(String csvAgents, boolean isChangementClasse, int anneeAvct, boolean isAffecte) throws Exception {
		return sirhConsumer.downloadArrete(csvAgents, isChangementClasse, anneeAvct, isAffecte);
	}

	@Override
	public byte[] downloadTableauAvancement(int idCap, int idCadreEmploi, boolean isAvisShd, String format,Integer idAgentConnecte) throws Exception {
		return sirhConsumer.downloadTableauAvancement(idCap, idCadreEmploi, isAvisShd, format,idAgentConnecte);
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

	@Override
	public BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateDebut) {

		List<BaseHorairePointageDto> listBaseHorairePointageDto = sirhConsumer.getListBaseHorairePointageAgent(idAgent, dateDebut, dateDebut);

		if (null != listBaseHorairePointageDto && !listBaseHorairePointageDto.isEmpty()) {
			return listBaseHorairePointageDto.get(0);
		}
		return null;
	}

	@Override
	public CalculEaeInfosDto getDetailAffectationActiveByAgent(Integer idAgent, Integer anneeFormation) {
		return sirhConsumer.getDetailAffectationActiveByAgent(idAgent, anneeFormation);
	}

	@Override
	public List<AutreAdministrationAgentDto> getListeAutreAdministrationAgent(Integer idAgent) {
		return sirhConsumer.getListeAutreAdministrationAgent(idAgent);
	}

	@Override
	public EntiteWithAgentWithServiceDto getListeEntiteWithAgentWithServiceDtoByIdServiceAdsWithoutAgentConnecte(Integer idServiceAds,
			Integer idAgent, List<AgentDto> listAgentsAInclureDansArbre) {
		return sirhConsumer.getListeEntiteWithAgentWithServiceDtoByIdServiceAds(idServiceAds, idAgent, listAgentsAInclureDansArbre);
	}

	@Override
	public List<AgentWithServiceDto> getListAgentsWithService(List<Integer> listAgentDto, Date date) {
		return sirhConsumer.getListAgentsWithService(listAgentDto, date);
	}

	@Override
	public List<AgentWithServiceDto> getListeAgentWithIndemniteForfaitTravailDPM(Set<Integer> listIdsAgent) {
		return sirhConsumer.getListeAgentWithIndemniteForfaitTravailDPM(listIdsAgent);
	}

	@Override
	public List<FichePosteTreeNodeDto> getFichePosteTreeNodeDto(Integer idEntite, boolean withFichesPosteNonReglemente) {
		return sirhConsumer.getFichePosteTreeNodeDto(idEntite, withFichesPosteNonReglemente);
	}

	@Override
	public FichePosteTreeNodeDto getFichePosteTreeNodeDtoByIdFichePoste(List<FichePosteTreeNodeDto> listFP, Integer idFichePosteConcerne) {

		FichePosteTreeNodeDto result = null;
		if (null != listFP && !listFP.isEmpty()) {
			for (FichePosteTreeNodeDto fp : listFP) {
				if (fp.getIdFichePoste().equals(idFichePosteConcerne)) {
					return fp;
				}
				result = getFichePosteTreeNodeDtoByIdFichePoste(fp.getFichePostesEnfant(), idFichePosteConcerne);
				if (null != result) {
					return result;
				}
			}
		}

		return result;
	}

	@Override
	public boolean isFPEnfantValideGeleeTransitoire(List<FichePosteTreeNodeDto> listFP, Integer idFichePosteConcerne) {

		boolean result = false;

		if (null != listFP && !listFP.isEmpty()) {
			for (FichePosteTreeNodeDto fp : listFP) {
				if (!fp.getIdFichePoste().equals(idFichePosteConcerne) && (EnumStatutFichePoste.VALIDEE.getId().equals(fp.getIdStatutFDP().toString())
						|| EnumStatutFichePoste.GELEE.getId().equals(fp.getIdStatutFDP().toString())
						|| EnumStatutFichePoste.TRANSITOIRE.getId().equals(fp.getIdStatutFDP().toString()))) {

					return true;
				}

				result = isFPEnfantValideGeleeTransitoire(fp.getFichePostesEnfant(), idFichePosteConcerne);
				if (result)
					return result;
			}
		}

		return result;
	}

	@Override
	public byte[] downloadCertificatAptitude(Integer idVisite) throws Exception {
		return sirhConsumer.downloadCertificatAptitude(idVisite);
	}

}
