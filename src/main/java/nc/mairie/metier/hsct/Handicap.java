package nc.mairie.metier.hsct;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier Handicap
 */
public class Handicap {

	public Integer idHandicap;
	public Integer idAgent;
	public Integer idTypeHandicap;
	public Integer idMaladiePro;
	public Integer pourcentIncapacite;
	public boolean reconnaissanceMp;
	public Date dateDebutHandicap;
	public Date dateFinHandicap;
	public boolean handicapCRDHNC;
	public String numCarteCrdhnc;
	public boolean amenagementPoste;
	public String commentaireHandicap;
	public boolean renouvellement;

	/**
	 * Constructeur Handicap.
	 */
	public Handicap() {
		super();
	}

	public Integer getIdHandicap() {
		return idHandicap;
	}

	public void setIdHandicap(Integer idHandicap) {
		this.idHandicap = idHandicap;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdTypeHandicap() {
		return idTypeHandicap;
	}

	public void setIdTypeHandicap(Integer idTypeHandicap) {
		this.idTypeHandicap = idTypeHandicap;
	}

	public Integer getIdMaladiePro() {
		return idMaladiePro;
	}

	public void setIdMaladiePro(Integer idMaladiePro) {
		this.idMaladiePro = idMaladiePro;
	}

	public Integer getPourcentIncapacite() {
		return pourcentIncapacite;
	}

	public void setPourcentIncapacite(Integer pourcentIncapacite) {
		if (pourcentIncapacite == null)
			this.pourcentIncapacite = 0;
		else
			this.pourcentIncapacite = pourcentIncapacite;
	}

	public boolean isReconnaissanceMp() {
		return reconnaissanceMp;
	}

	public void setReconnaissanceMp(boolean reconnaissanceMp) {
		this.reconnaissanceMp = reconnaissanceMp;
	}

	public Date getDateDebutHandicap() {
		return dateDebutHandicap;
	}

	public void setDateDebutHandicap(Date dateDebutHandicap) {
		this.dateDebutHandicap = dateDebutHandicap;
	}

	public Date getDateFinHandicap() {
		return dateFinHandicap;
	}

	public void setDateFinHandicap(Date dateFinHandicap) {
		this.dateFinHandicap = dateFinHandicap;
	}

	public boolean isHandicapCRDHNC() {
		return handicapCRDHNC;
	}

	public void setHandicapCRDHNC(boolean handicapCRDHNC) {
		this.handicapCRDHNC = handicapCRDHNC;
	}

	public String getNumCarteCrdhnc() {
		return numCarteCrdhnc == null ? Const.CHAINE_VIDE : numCarteCrdhnc.trim();
	}

	public void setNumCarteCrdhnc(String numCarteCrdhnc) {
		this.numCarteCrdhnc = numCarteCrdhnc;
	}

	public boolean isAmenagementPoste() {
		return amenagementPoste;
	}

	public void setAmenagementPoste(boolean amenagementPoste) {
		this.amenagementPoste = amenagementPoste;
	}

	public String getCommentaireHandicap() {
		return commentaireHandicap;
	}

	public void setCommentaireHandicap(String commentaireHandicap) {
		this.commentaireHandicap = commentaireHandicap;
	}

	public boolean isRenouvellement() {
		return renouvellement;
	}

	public void setRenouvellement(boolean renouvellement) {
		this.renouvellement = renouvellement;
	}

	@Override
	public boolean equals(Object object) {
		return idHandicap.toString().equals(((Handicap) object).getIdHandicap().toString());
	}
}
