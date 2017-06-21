package nc.mairie.metier.hsct;

import java.util.Date;

/**
 * Objet metier VisiteMedicale
 */
public class VisiteMedicale {

	public Integer idVisite;
	public Integer idAgent;
	public Integer idMedecin;
	public Integer idRecommandation;
	public Date dateDerniereVisite;
	public Integer dureeValidite;
	public Integer idMotifVm;
	public Integer idSuiviMed;
	public String commentaire;

	/**
	 * Constructeur VisiteMedicale.
	 */
	public VisiteMedicale() {
		super();
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

	public Integer getIdMotifVm() {
		return idMotifVm;
	}

	public void setIdMotifVm(Integer idMotifVm) {
		this.idMotifVm = idMotifVm;
	}

	public void setDureeValidite(Integer dureeValidite) {
		this.dureeValidite = dureeValidite;
	}

	public Integer getDureeValidite() {
		return dureeValidite;
	}

	public void setIdSuiviMed(Integer idSuiviMed) {
		this.idSuiviMed = idSuiviMed;
	}

	@Override
	public boolean equals(Object object) {
		return idVisite.toString().equals(((VisiteMedicale) object).getIdVisite().toString());
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Integer getIdSuiviMed() {
		return idSuiviMed;
	}
}
