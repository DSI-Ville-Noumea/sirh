package nc.mairie.gestionagent.eae.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class EaeEvolutionDto {

	private int								idEae;
	private boolean							mobiliteGeo;
	private boolean							mobiliteFonctionnelle;
	private boolean							changementMetier;
	private EaeListeDto						delaiEnvisage;
	private boolean							mobiliteService;
	private boolean							mobiliteDirection;
	private boolean							mobiliteCollectivite;
	private String							nomCollectivite;
	private boolean							mobiliteAutre;
	private boolean							concours;
	private String							nomConcours;
	private boolean							vae;
	private String							nomVae;
	private boolean							tempsPartiel;
	private EaeListeDto						pourcentageTempsPartiel;
	private boolean							retraite;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date							dateRetraite;
	private boolean							autrePerspective;
	private String							libelleAutrePerspective;

	private EaeCommentaireDto				commentaireEvolution;
	private EaeCommentaireDto				commentaireEvaluateur;
	private EaeCommentaireDto				commentaireEvalue;

	private List<EaeEvolutionSouhaitDto>	souhaitsSuggestions;
	private List<EaeDeveloppementDto>		developpementConnaissances;
	private List<EaeDeveloppementDto>		developpementCompetences;
	private List<EaeDeveloppementDto>		developpementExamensConcours;
	private List<EaeDeveloppementDto>		developpementPersonnel;
	private List<EaeDeveloppementDto>		developpementComportement;
	private List<EaeDeveloppementDto>		developpementFormateur;

	public EaeEvolutionDto() {
		souhaitsSuggestions = new ArrayList<EaeEvolutionSouhaitDto>();
		developpementConnaissances = new ArrayList<EaeDeveloppementDto>();
		developpementCompetences = new ArrayList<EaeDeveloppementDto>();
		developpementExamensConcours = new ArrayList<EaeDeveloppementDto>();
		developpementPersonnel = new ArrayList<EaeDeveloppementDto>();
		developpementComportement = new ArrayList<EaeDeveloppementDto>();
		developpementFormateur = new ArrayList<EaeDeveloppementDto>();
	}

	public int getIdEae() {
		return idEae;
	}

	public void setIdEae(int idEae) {
		this.idEae = idEae;
	}

	public boolean isMobiliteGeo() {
		return mobiliteGeo;
	}

	public void setMobiliteGeo(boolean mobiliteGeo) {
		this.mobiliteGeo = mobiliteGeo;
	}

	public boolean isMobiliteFonctionnelle() {
		return mobiliteFonctionnelle;
	}

	public void setMobiliteFonctionnelle(boolean mobiliteFonctionnelle) {
		this.mobiliteFonctionnelle = mobiliteFonctionnelle;
	}

	public boolean isChangementMetier() {
		return changementMetier;
	}

	public void setChangementMetier(boolean changementMetier) {
		this.changementMetier = changementMetier;
	}

	public EaeListeDto getDelaiEnvisage() {
		return delaiEnvisage;
	}

	public void setDelaiEnvisage(EaeListeDto delaiEnvisage) {
		this.delaiEnvisage = delaiEnvisage;
	}

	public boolean isMobiliteService() {
		return mobiliteService;
	}

	public void setMobiliteService(boolean mobiliteService) {
		this.mobiliteService = mobiliteService;
	}

	public boolean isMobiliteDirection() {
		return mobiliteDirection;
	}

	public void setMobiliteDirection(boolean mobiliteDirection) {
		this.mobiliteDirection = mobiliteDirection;
	}

	public boolean isMobiliteCollectivite() {
		return mobiliteCollectivite;
	}

	public void setMobiliteCollectivite(boolean mobiliteCollectivite) {
		this.mobiliteCollectivite = mobiliteCollectivite;
	}

	public String getNomCollectivite() {
		return nomCollectivite;
	}

	public void setNomCollectivite(String nomCollectivite) {
		this.nomCollectivite = nomCollectivite;
	}

	public boolean isMobiliteAutre() {
		return mobiliteAutre;
	}

	public void setMobiliteAutre(boolean mobiliteAutre) {
		this.mobiliteAutre = mobiliteAutre;
	}

	public boolean isConcours() {
		return concours;
	}

	public void setConcours(boolean concours) {
		this.concours = concours;
	}

	public String getNomConcours() {
		return nomConcours;
	}

	public void setNomConcours(String nomConcours) {
		this.nomConcours = nomConcours;
	}

	public boolean isVae() {
		return vae;
	}

	public void setVae(boolean vae) {
		this.vae = vae;
	}

	public String getNomVae() {
		return nomVae;
	}

	public void setNomVae(String nomVae) {
		this.nomVae = nomVae;
	}

	public boolean isTempsPartiel() {
		return tempsPartiel;
	}

	public void setTempsPartiel(boolean tempsPartiel) {
		this.tempsPartiel = tempsPartiel;
	}

	public EaeListeDto getPourcentageTempsPartiel() {
		return pourcentageTempsPartiel;
	}

	public void setPourcentageTempsPartiel(EaeListeDto tpsPartiel) {
		this.pourcentageTempsPartiel = tpsPartiel;
	}

	public boolean isRetraite() {
		return retraite;
	}

	public void setRetraite(boolean retraite) {
		this.retraite = retraite;
	}

	public Date getDateRetraite() {
		return dateRetraite;
	}

	public void setDateRetraite(Date dateRetraite) {
		this.dateRetraite = dateRetraite;
	}

	public boolean isAutrePerspective() {
		return autrePerspective;
	}

	public void setAutrePerspective(boolean autrePerspective) {
		this.autrePerspective = autrePerspective;
	}

	public String getLibelleAutrePerspective() {
		return libelleAutrePerspective;
	}

	public void setLibelleAutrePerspective(String libelleAutrePerspective) {
		this.libelleAutrePerspective = libelleAutrePerspective;
	}

	public EaeCommentaireDto getCommentaireEvolution() {
		return commentaireEvolution;
	}

	public void setCommentaireEvolution(EaeCommentaireDto commentaireEvolution) {
		this.commentaireEvolution = commentaireEvolution;
	}

	public EaeCommentaireDto getCommentaireEvaluateur() {
		return commentaireEvaluateur;
	}

	public void setCommentaireEvaluateur(EaeCommentaireDto commentaireEvaluateur) {
		this.commentaireEvaluateur = commentaireEvaluateur;
	}

	public EaeCommentaireDto getCommentaireEvalue() {
		return commentaireEvalue;
	}

	public void setCommentaireEvalue(EaeCommentaireDto commentaireEvalue) {
		this.commentaireEvalue = commentaireEvalue;
	}

	public List<EaeEvolutionSouhaitDto> getSouhaitsSuggestions() {
		return souhaitsSuggestions;
	}

	public void setSouhaitsSuggestions(List<EaeEvolutionSouhaitDto> souhaitsSuggestions) {
		this.souhaitsSuggestions = souhaitsSuggestions;
	}

	public List<EaeDeveloppementDto> getDeveloppementConnaissances() {
		return developpementConnaissances;
	}

	public void setDeveloppementConnaissances(List<EaeDeveloppementDto> developpementConnaissances) {
		this.developpementConnaissances = developpementConnaissances;
	}

	public List<EaeDeveloppementDto> getDeveloppementCompetences() {
		return developpementCompetences;
	}

	public void setDeveloppementCompetences(List<EaeDeveloppementDto> developpementCompetences) {
		this.developpementCompetences = developpementCompetences;
	}

	public List<EaeDeveloppementDto> getDeveloppementExamensConcours() {
		return developpementExamensConcours;
	}

	public void setDeveloppementExamensConcours(List<EaeDeveloppementDto> developpementExamensConcours) {
		this.developpementExamensConcours = developpementExamensConcours;
	}

	public List<EaeDeveloppementDto> getDeveloppementPersonnel() {
		return developpementPersonnel;
	}

	public void setDeveloppementPersonnel(List<EaeDeveloppementDto> developpementPersonnel) {
		this.developpementPersonnel = developpementPersonnel;
	}

	public List<EaeDeveloppementDto> getDeveloppementComportement() {
		return developpementComportement;
	}

	public void setDeveloppementComportement(List<EaeDeveloppementDto> developpementComportement) {
		this.developpementComportement = developpementComportement;
	}

	public List<EaeDeveloppementDto> getDeveloppementFormateur() {
		return developpementFormateur;
	}

	public void setDeveloppementFormateur(List<EaeDeveloppementDto> developpementFormateur) {
		this.developpementFormateur = developpementFormateur;
	}

}
