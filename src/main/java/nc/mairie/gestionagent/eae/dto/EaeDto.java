package nc.mairie.gestionagent.eae.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EaeDto {

	private Integer						idEae;
	private String						etat;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateEntretien;

	private CampagneEaeDto				campagne;
	private BirtDto						evalue;

	private EaeFichePosteDto			fichePoste;
	private List<BirtDto>				evaluateurs;

	private EaeEvaluationDto			evaluation;
	private EaePlanActionDto			planAction;
	private EaeEvolutionDto				evolution;
	private List<EaeFinalizationDto>	finalisation;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateCreation;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateFinalisation;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date						dateControle;

	private String						userControle;
	private Integer						idAgentDelegataire;

	private boolean						cap;
	private boolean						docAttache;

	public EaeDto() {
		this.evaluateurs = new ArrayList<BirtDto>();
		this.finalisation = new ArrayList<EaeFinalizationDto>();
	}

	public Integer getIdEae() {
		return idEae;
	}

	public void setIdEae(Integer idEae) {
		this.idEae = idEae;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	public Date getDateEntretien() {
		return dateEntretien;
	}

	public void setDateEntretien(Date dateEntretien) {
		this.dateEntretien = dateEntretien;
	}

	public CampagneEaeDto getCampagne() {
		return campagne;
	}

	public void setCampagne(CampagneEaeDto campagne) {
		this.campagne = campagne;
	}

	public BirtDto getEvalue() {
		return evalue;
	}

	public void setEvalue(BirtDto evalue) {
		this.evalue = evalue;
	}

	public EaeFichePosteDto getFichePoste() {
		return fichePoste;
	}

	public void setFichePoste(EaeFichePosteDto fichePoste) {
		this.fichePoste = fichePoste;
	}

	public List<BirtDto> getEvaluateurs() {
		return evaluateurs;
	}

	public void setEvaluateurs(List<BirtDto> evaluateurs) {
		this.evaluateurs = evaluateurs;
	}

	public EaeEvaluationDto getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(EaeEvaluationDto evaluation) {
		this.evaluation = evaluation;
	}

	public EaePlanActionDto getPlanAction() {
		return planAction;
	}

	public void setPlanAction(EaePlanActionDto planAction) {
		this.planAction = planAction;
	}

	public EaeEvolutionDto getEvolution() {
		return evolution;
	}

	public void setEvolution(EaeEvolutionDto evolution) {
		this.evolution = evolution;
	}

	public List<EaeFinalizationDto> getFinalisation() {
		return finalisation;
	}

	public void setFinalisation(List<EaeFinalizationDto> finalisation) {
		this.finalisation = finalisation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateFinalisation() {
		return dateFinalisation;
	}

	public void setDateFinalisation(Date dateFinalisation) {
		this.dateFinalisation = dateFinalisation;
	}

	public Date getDateControle() {
		return dateControle;
	}

	public void setDateControle(Date dateControle) {
		this.dateControle = dateControle;
	}

	public String getUserControle() {
		return userControle;
	}

	public void setUserControle(String userControle) {
		this.userControle = userControle;
	}

	public Integer getIdAgentDelegataire() {
		return idAgentDelegataire;
	}

	public void setIdAgentDelegataire(Integer idAgentDelegataire) {
		this.idAgentDelegataire = idAgentDelegataire;
	}

	public boolean isCap() {
		return cap;
	}

	public void setCap(boolean cap) {
		this.cap = cap;
	}

	public boolean isDocAttache() {
		return docAttache;
	}

	public void setDocAttache(boolean docAttache) {
		this.docAttache = docAttache;
	}

}
