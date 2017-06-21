package nc.mairie.metier.poste;

import java.util.Date;

import nc.mairie.metier.hsct.VisiteMedicale;

/**
 * Objet metier HistoFichePoste
 */
public class HistoVisiteMedicale {

	public Integer	idVisite;
	public Integer	idAgent;
	public Integer	idMedecin;
	public Integer	idRecommandation;
	public Date		dateDerniereVisite;
	public Integer	dureeValidite;
	public Integer	idMotifVm;
	public Integer	idSuiviMed;
	public String	commentaire;

	public String	typeHisto;
	public String	userHisto;

	/**
	 * Constructeur HistoVisiteMedicale.
	 */
	public HistoVisiteMedicale() {
		super();
	}

	/**
	 * Constructeur HistoVisiteMedicale.
	 */
	public HistoVisiteMedicale(VisiteMedicale vm) {
		super();
		this.idVisite = vm.getIdVisite();
		this.idAgent = vm.getIdAgent();
		this.idMedecin = vm.getIdMedecin();
		this.idRecommandation = vm.getIdRecommandation();
		this.dateDerniereVisite = vm.getDateDerniereVisite();
		this.dureeValidite = vm.getDureeValidite();
		this.idMotifVm = vm.getIdMotifVm();
		this.idSuiviMed = vm.getIdSuiviMed();
		this.commentaire = vm.getCommentaire();

	}

	public Integer getIdVisite() {
		return idVisite;
	}

	public void setIdVisite(Integer idVisite) {
		this.idVisite = idVisite;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdMedecin() {
		return idMedecin;
	}

	public void setIdMedecin(Integer idMedecin) {
		this.idMedecin = idMedecin;
	}

	public Integer getIdRecommandation() {
		return idRecommandation;
	}

	public void setIdRecommandation(Integer idRecommandation) {
		this.idRecommandation = idRecommandation;
	}

	public Date getDateDerniereVisite() {
		return dateDerniereVisite;
	}

	public void setDateDerniereVisite(Date dateDerniereVisite) {
		this.dateDerniereVisite = dateDerniereVisite;
	}

	public Integer getDureeValidite() {
		return dureeValidite;
	}

	public void setDureeValidite(Integer dureeValidite) {
		this.dureeValidite = dureeValidite;
	}

	public Integer getIdMotifVm() {
		return idMotifVm;
	}

	public void setIdMotifVm(Integer idMotifVm) {
		this.idMotifVm = idMotifVm;
	}

	public Integer getIdSuiviMed() {
		return idSuiviMed;
	}

	public void setIdSuiviMed(Integer idSuiviMed) {
		this.idSuiviMed = idSuiviMed;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getTypeHisto() {
		return typeHisto;
	}

	public void setTypeHisto(String typeHisto) {
		this.typeHisto = typeHisto;
	}

	public String getUserHisto() {
		return userHisto;
	}

	public void setUserHisto(String userHisto) {
		this.userHisto = userHisto;
	}
}
