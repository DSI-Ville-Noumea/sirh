package nc.noumea.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.BirtDto;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeCampagneTaskDto;
import nc.mairie.gestionagent.eae.dto.EaeDashboardItemDto;
import nc.mairie.gestionagent.eae.dto.EaeDocumentDto;
import nc.mairie.gestionagent.eae.dto.EaeDto;
import nc.mairie.gestionagent.eae.dto.EaeEvaluationDto;
import nc.mairie.gestionagent.eae.dto.EaeEvolutionDto;
import nc.mairie.gestionagent.eae.dto.EaeFinalizationDto;
import nc.mairie.gestionagent.eae.dto.EaePlanActionDto;
import nc.mairie.gestionagent.eae.dto.FormRehercheGestionEae;
import nc.mairie.gestionagent.eae.dto.ListItemDto;
import nc.mairie.spring.ws.ISirhEaeWSConsumer;

@Service
public class EaeService implements IEaeService {

	@Autowired
	private ISirhEaeWSConsumer eaeConsumer;

	@Override
	public EaeDto getEaeDtoByIdAgent(List<EaeDto> listEae, Integer idAgent) {
		if (null == listEae || listEae.isEmpty() || null == idAgent) {
			return null;
		}

		for (EaeDto eae : listEae) {
			if (null != eae.getEvalue() && null != eae.getEvalue().getIdAgent() && eae.getEvalue().getIdAgent().equals(idAgent)) {
				return eae;
			}
		}

		return null;
	}

	@Override
	public List<EaeDto> getListeEaeDto(Integer idAgentSirh, FormRehercheGestionEae form, Integer pageSize, Integer pageNumber) {
		return eaeConsumer.getListeEaeDto(idAgentSirh, form, pageSize, pageNumber);
	}

	@Override
	public CampagneEaeDto getCampagneAnneePrecedente(Integer idAgentSirh, Integer anneePrecedente) {
		return eaeConsumer.getCampagneAnneePrecedente(idAgentSirh, anneePrecedente);
	}

	@Override
	public List<ListItemDto> getListeTypeDeveloppement() {
		return eaeConsumer.getListeTypeDeveloppement();
	}

	@Override
	public ReturnMessageDto saveEvolution(Integer idAgentSirh, Integer idEae, EaeEvolutionDto eaeEvolutionDto) {
		return eaeConsumer.saveEvolution(idAgentSirh, idEae, eaeEvolutionDto);
	}

	@Override
	public ReturnMessageDto savePlanAction(Integer idAgentSirh, Integer idEae, EaePlanActionDto eaePlanActionDto) {
		return eaeConsumer.savePlanAction(idAgentSirh, idEae, eaePlanActionDto);
	}

	@Override
	public ReturnMessageDto saveEvaluation(Integer idAgentSirh, Integer idEae, EaeEvaluationDto eaeEvaluationDto) {
		return eaeConsumer.saveEvaluation(idAgentSirh, idEae, eaeEvaluationDto);
	}

	@Override
	public EaeDto getDetailsEae(Integer idAgentSirh, Integer idEae) {
		return eaeConsumer.getDetailsEae(idAgentSirh, idEae);
	}

	@Override
	public List<EaeDto> getListEaesByidAgent(Integer idAgentSirh, Integer idAgent) {
		return eaeConsumer.getListEaesByidAgent(idAgentSirh, idAgent);
	}

	@Override
	public List<CampagneEaeDto> getListeCampagnesEae(Integer idAgentSirh) {
		return eaeConsumer.getListeCampagnesEae(idAgentSirh);
	}

	@Override
	public ReturnMessageDto createOrModifyCampagneEae(Integer idAgentSirh, CampagneEaeDto dto) {
		return eaeConsumer.createOrModifyCampagneEae(idAgentSirh, dto);
	}

	@Override
	public EaeDocumentDto getDocumentEaeByIdDocument(Integer idAgentSirh, Integer idDocument) {
		return eaeConsumer.getDocumentEaeByIdDocument(idAgentSirh, idDocument);
	}

	@Override
	public ReturnMessageDto deleteDocumentEae(Integer idAgentSirh, Integer idEaeDocument) {
		return eaeConsumer.deleteDocumentEae(idAgentSirh, idEaeDocument);
	}

	@Override
	public ReturnMessageDto setEae(Integer idAgentSirh, EaeDto eaeDto) {
		return eaeConsumer.setEae(idAgentSirh, eaeDto);
	}

	@Override
	public EaeCampagneTaskDto findEaeCampagneTaskByIdCampagneEae(Integer idAgentSirh, Integer idEaeCampagne) {
		return eaeConsumer.findEaeCampagneTaskByIdCampagneEae(idAgentSirh, idEaeCampagne);
	}

	@Override
	public ReturnMessageDto saveEaeCampagneTask(Integer idAgentSirh, EaeCampagneTaskDto campagneTaskDto) {
		return eaeConsumer.saveEaeCampagneTask(idAgentSirh, campagneTaskDto);
	}

	@Override
	public ReturnMessageDto updateEae(Integer idAgentSirh, Integer idEae) {
		return eaeConsumer.updateEae(idAgentSirh, idEae);
	}

	@Override
	public String getLastDocumentEaeFinalise(Integer idAgentSirh, Integer idEae) {
		return eaeConsumer.getLastDocumentEaeFinalise(idAgentSirh, idEae);
	}

	@Override
	public List<EaeDashboardItemDto> getEaesDashboard(Integer idAgentSirh, Integer annee) {
		return eaeConsumer.getEaesDashboard(idAgentSirh, annee);
	}

	@Override
	public ReturnMessageDto saveDateEvaluateurFromSirh(Integer idAgentSirh, Integer idEae, BirtDto evaluateur) {
		return eaeConsumer.saveDateEvaluateurFromSirh(idAgentSirh, idEae, evaluateur);
	}

	@Override
	public ReturnMessageDto saveDateEvalueFromSirh(Integer idAgentSirh, Integer idEae, BirtDto evalue) {
		return eaeConsumer.saveDateEvalueFromSirh(idAgentSirh, idEae, evalue);
	}

	@Override
	public ReturnMessageDto finalizeEae(Integer idEae, Integer idAgent, EaeFinalizationDto eaeFinalisationDto) {
		return eaeConsumer.finalizeEae(idEae, idAgent, eaeFinalisationDto);
	}

	@Override
	public List<EaeDto> getListeEaeDtoLight(Integer idAgentSirh, FormRehercheGestionEae form) {
		return eaeConsumer.getListeEaeDtoLight(idAgentSirh, form);
	}

	@Override
	public CampagneEaeDto getCampagneAnneePrecedenteLight(Integer idAgentSirh, Integer anneePrecedente) {
		return eaeConsumer.getCampagneAnneePrecedenteLight(idAgentSirh, anneePrecedente);
	}

	@Override
	public Integer getCountList(FormRehercheGestionEae form) {
		return eaeConsumer.getCountList(form);
	}

}
