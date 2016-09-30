package nc.mairie.gestionagent.eae.dto;

public class EaeEvaluationDto {
	private Integer				dureeEntretien;
	private EaeCommentaireDto	commentaireEvaluateur;
	private EaeCommentaireDto	commentaireEvalue;
	private EaeListeDto			niveau;
	private Double				noteAnnee;
	private Double				noteAnneeN1;
	private Double				noteAnneeN2;
	private Double				noteAnneeN3;
	private Boolean				avisRevalorisation;
	private Boolean				avisChangementClasse;
	private EaeListeDto			propositionAvancement;
	private int					anneeAvancement;
	private EaeCommentaireDto	commentaireAvctEvaluateur;
	private EaeCommentaireDto	commentaireAvctEvalue;
	private String				statut;
	private String				typeAvct;
	private boolean				cap;
	private int					idEae;
	private String				avisShd;

	public Integer getDureeEntretien() {
		return dureeEntretien;
	}

	public void setDureeEntretien(Integer dureeEntretien) {
		this.dureeEntretien = dureeEntretien;
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

	public EaeListeDto getNiveau() {
		return niveau;
	}

	public void setNiveau(EaeListeDto niveau) {
		this.niveau = niveau;
	}

	public Double getNoteAnnee() {
		return noteAnnee;
	}

	public void setNoteAnnee(Double noteAnnee) {
		this.noteAnnee = noteAnnee;
	}

	public Double getNoteAnneeN1() {
		return noteAnneeN1;
	}

	public void setNoteAnneeN1(Double noteAnneeN1) {
		this.noteAnneeN1 = noteAnneeN1;
	}

	public Double getNoteAnneeN2() {
		return noteAnneeN2;
	}

	public void setNoteAnneeN2(Double noteAnneeN2) {
		this.noteAnneeN2 = noteAnneeN2;
	}

	public Double getNoteAnneeN3() {
		return noteAnneeN3;
	}

	public void setNoteAnneeN3(Double noteAnneeN3) {
		this.noteAnneeN3 = noteAnneeN3;
	}

	public Boolean getAvisRevalorisation() {
		return avisRevalorisation;
	}

	public void setAvisRevalorisation(Boolean avisRevalorisation) {
		this.avisRevalorisation = avisRevalorisation;
	}

	public Boolean getAvisChangementClasse() {
		return avisChangementClasse;
	}

	public void setAvisChangementClasse(Boolean avisChangementClasse) {
		this.avisChangementClasse = avisChangementClasse;
	}

	public EaeListeDto getPropositionAvancement() {
		return propositionAvancement;
	}

	public void setPropositionAvancement(EaeListeDto propositionAvancement) {
		this.propositionAvancement = propositionAvancement;
	}

	public int getAnneeAvancement() {
		return anneeAvancement;
	}

	public void setAnneeAvancement(int anneeAvancement) {
		this.anneeAvancement = anneeAvancement;
	}

	public EaeCommentaireDto getCommentaireAvctEvaluateur() {
		return commentaireAvctEvaluateur;
	}

	public void setCommentaireAvctEvaluateur(EaeCommentaireDto commentaireAvctEvaluateur) {
		this.commentaireAvctEvaluateur = commentaireAvctEvaluateur;
	}

	public EaeCommentaireDto getCommentaireAvctEvalue() {
		return commentaireAvctEvalue;
	}

	public void setCommentaireAvctEvalue(EaeCommentaireDto commentaireAvctEvalue) {
		this.commentaireAvctEvalue = commentaireAvctEvalue;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getTypeAvct() {
		return typeAvct;
	}

	public void setTypeAvct(String typeAvct) {
		this.typeAvct = typeAvct;
	}

	public boolean isCap() {
		return cap;
	}

	public void setCap(boolean cap) {
		this.cap = cap;
	}

	public int getIdEae() {
		return idEae;
	}

	public void setIdEae(int idEae) {
		this.idEae = idEae;
	}

	public String getAvisShd() {
		return avisShd;
	}

	public void setAvisShd(String avisShd) {
		this.avisShd = avisShd;
	}

}
