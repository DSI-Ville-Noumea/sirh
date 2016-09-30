package nc.noumea.spring.service;

import java.util.List;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.BirtDto;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeCampagneTaskDto;
import nc.mairie.gestionagent.eae.dto.EaeDashboardItemDto;
import nc.mairie.gestionagent.eae.dto.EaeDocumentDto;
import nc.mairie.gestionagent.eae.dto.EaeDto;
import nc.mairie.gestionagent.eae.dto.EaeEvaluationDto;
import nc.mairie.gestionagent.eae.dto.EaeEvolutionDto;
import nc.mairie.gestionagent.eae.dto.EaePlanActionDto;
import nc.mairie.gestionagent.eae.dto.FormRehercheGestionEae;
import nc.mairie.gestionagent.eae.dto.ListItemDto;

public interface IEaeService {

	EaeDto getEaeDtoByIdAgent(List<EaeDto> listEae, Integer idAgent);
	
	List<EaeDto> getListeEaeDto(Integer idAgentSirh, FormRehercheGestionEae form);
	
	CampagneEaeDto getCampagneAnneePrecedente(Integer idAgentSirh, Integer anneePrecedente);
	
	List<ListItemDto> getListeTypeDeveloppement();
	
	Integer chercherEaeNumIncrement(Integer idAgentSirh);
	
	ReturnMessageDto saveEvolution(Integer idAgentSirh, Integer idEae, EaeEvolutionDto eaeEvolutionDto);
	
	ReturnMessageDto savePlanAction(Integer idAgentSirh, Integer idEae, EaePlanActionDto eaePlanActionDto);
	
	ReturnMessageDto saveEvaluation(Integer idAgentSirh, Integer idEae, EaeEvaluationDto eaeEvaluationDto);
	
	EaeDto getDetailsEae(Integer idAgentSirh, Integer idEae);
	
	List<EaeDto> getListEaesByidAgent(Integer idAgentSirh, Integer idAgent);
	
	List<CampagneEaeDto> getListeCampagnesEae(Integer idAgentSirh);
	
	ReturnMessageDto createOrModifyCampagneEae(Integer idAgentSirh, CampagneEaeDto dto);
	
	EaeDocumentDto getDocumentEaeByIdDocument(Integer idAgentSirh, Integer idDocument);
	
	ReturnMessageDto deleteDocumentEae(Integer idAgentSirh, Integer idEaeDocument);
	
	ReturnMessageDto setEae(Integer idAgentSirh, EaeDto eaeDto);
	
	EaeCampagneTaskDto findEaeCampagneTaskByIdCampagneEae(Integer idAgentSirh, Integer idEaeCampagne);
	
	ReturnMessageDto saveEaeCampagneTask(Integer idAgentSirh, EaeCampagneTaskDto campagneTaskDto);
	
	public ReturnMessageDto updateEae(Integer idAgentSirh, Integer idEae);
	
	String getLastDocumentEaeFinalise(Integer idAgentSirh, Integer idEae);

	ReturnMessageDto updateCapEae(Integer idAgentSirh, Integer idEae, Boolean cap);

	List<EaeDashboardItemDto> getEaesDashboard(Integer idAgentSirh, Integer annee);

	ReturnMessageDto saveDateEvaluateurFromSirh(Integer idAgentSirh, Integer idEae, BirtDto evaluateur);

	ReturnMessageDto saveDateEvalueFromSirh(Integer idAgentSirh, Integer idEae, BirtDto evalue);
}
