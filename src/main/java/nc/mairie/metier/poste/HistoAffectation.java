package nc.mairie.metier.poste;

import java.text.ParseException;
import java.util.Date;

/**
 * Objet metier HistoAffectation
 */
public class HistoAffectation {

	public Integer idAffectation;
	public Integer idMotifAffectation;
	public Integer idFichePoste;
	public Integer idAgent;
	public String refArreteAff;
	public Date dateArrete;
	public Date dateDebutAff;
	public Date dateFinAff;
	public String tempsTravail;
	public String typeHisto;
	public Date dateHisto;
	public String userHisto;
	public String codeEcole;
	public Integer idFichePosteSecondaire;
	public String commentaire;
	public Integer idBaseHorairePointage;
	public Integer idBaseHoraireAbsence;

	/**
	 * Constructeur HistoAffectation.
	 */
	public HistoAffectation() {
		super();
	}

	/**
	 * Constructeur HistoAffectation.
	 * 
	 * @throws ParseException
	 */
	public HistoAffectation(Affectation affectation) throws ParseException {
		super();

		this.idAffectation = affectation.getIdAffectation();
		this.idMotifAffectation = affectation.getIdMotifAffectation();
		this.idFichePoste = affectation.getIdFichePoste();
		this.idAgent = affectation.getIdAgent();
		this.refArreteAff = affectation.getRefArreteAff();
		this.dateArrete = affectation.getDateArrete();
		this.dateDebutAff = affectation.getDateDebutAff();
		this.dateFinAff = affectation.getDateFinAff();
		this.tempsTravail = affectation.getTempsTravail();
		this.codeEcole = affectation.getCodeEcole();
		this.idFichePosteSecondaire = affectation.getIdFichePosteSecondaire();
		this.commentaire = affectation.getCommentaire();
		this.idBaseHorairePointage = affectation.getIdBaseHorairePointage();
		this.idBaseHoraireAbsence = affectation.getIdBaseHoraireAbsence();
	}

	/**
	 * Getter de l'attribut idAffectation.
	 */
	public Integer getIdAffectation() {
		return idAffectation;
	}

	/**
	 * Setter de l'attribut idAffectation.
	 */
	public void setIdAffectation(Integer newIdAffectation) {
		idAffectation = newIdAffectation;
	}

	/**
	 * Getter de l'attribut idMotifAffectation.
	 */
	public Integer getIdMotifAffectation() {
		return idMotifAffectation;
	}

	/**
	 * Setter de l'attribut idMotifAffectation.
	 */
	public void setIdMotifAffectation(Integer newIdMotifAffectation) {
		idMotifAffectation = newIdMotifAffectation;
	}

	/**
	 * Getter de l'attribut idFichePoste.
	 */
	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	/**
	 * Setter de l'attribut idFichePoste.
	 */
	public void setIdFichePoste(Integer newIdFichePoste) {
		idFichePoste = newIdFichePoste;
	}

	/**
	 * Getter de l'attribut idAgent.
	 */
	public Integer getIdAgent() {
		return idAgent;
	}

	/**
	 * Setter de l'attribut idAgent.
	 */
	public void setIdAgent(Integer newIdAgent) {
		idAgent = newIdAgent;
	}

	/**
	 * Getter de l'attribut refArreteAff.
	 */
	public String getRefArreteAff() {
		return refArreteAff;
	}

	/**
	 * Setter de l'attribut refArreteAff.
	 */
	public void setRefArreteAff(String newRefArreteAff) {
		refArreteAff = newRefArreteAff;
	}

	/**
	 * Getter de l'attribut dateArrete.
	 */
	public Date getDateArrete() {
		return dateArrete;
	}

	/**
	 * Setter de l'attribut dateArrete.
	 */
	public void setDateArrete(Date newDateArrete) {
		dateArrete = newDateArrete;
	}

	/**
	 * Getter de l'attribut dateDebutAff.
	 */
	public Date getDateDebutAff() {
		return dateDebutAff;
	}

	/**
	 * Setter de l'attribut dateDebutAff.
	 */
	public void setDateDebutAff(Date newDateDebutAff) {
		dateDebutAff = newDateDebutAff;
	}

	/**
	 * Getter de l'attribut dateFinAff.
	 */
	public Date getDateFinAff() {
		return dateFinAff;
	}

	/**
	 * Setter de l'attribut dateFinAff.
	 */
	public void setDateFinAff(Date newDateFinAff) {
		dateFinAff = newDateFinAff;
	}

	/**
	 * Getter de l'attribut temps_travail.
	 */
	public String getTempsTravail() {
		return tempsTravail;
	}

	/**
	 * Setter de l'attribut temps_travail.
	 */
	public void setTempsTravail(String newTemps_travail) {
		tempsTravail = newTemps_travail;
	}

	/**
	 * Getter de l'attribut typeHisto.
	 */
	public String getTypeHisto() {
		return typeHisto;
	}

	/**
	 * Setter de l'attribut typeHisto.
	 */
	public void setTypeHisto(String newTypeHisto) {
		typeHisto = newTypeHisto;
	}

	/**
	 * Getter de l'attribut userHisto.
	 */
	public String getUserHisto() {
		return userHisto;
	}

	/**
	 * Setter de l'attribut userHisto.
	 */
	public void setUserHisto(String newUserHisto) {
		userHisto = newUserHisto;
	}

	/**
	 * @return codeEcole
	 */
	public String getCodeEcole() {
		return codeEcole;
	}

	/**
	 * @param codeEcole
	 */
	public void setCodeEcole(String codeEcole) {
		this.codeEcole = codeEcole;
	}

	/**
	 * Getter de l'attribut idFichePosteSecondaire.
	 */
	public Integer getIdFichePosteSecondaire() {
		return idFichePosteSecondaire;
	}

	/**
	 * Setter de l'attribut idFichePosteSecondaire.
	 */
	public void setIdFichePosteSecondaire(Integer newIdFichePosteSecondaire) {
		idFichePosteSecondaire = newIdFichePosteSecondaire;
	}

	/**
	 * Getter de l'attribut commentaire.
	 */
	public String getCommentaire() {
		return commentaire;
	}

	/**
	 * Setter de l'attribut commentaire.
	 */
	public void setCommentaire(String newCommentaire) {
		commentaire = newCommentaire;
	}

	public Integer getIdBaseHorairePointage() {
		return idBaseHorairePointage;
	}

	public void setIdBaseHorairePointage(Integer idBaseHorairePointage) {
		this.idBaseHorairePointage = idBaseHorairePointage;
	}

	public Integer getIdBaseHoraireAbsence() {
		return idBaseHoraireAbsence;
	}

	public void setIdBaseHoraireAbsence(Integer idBaseHoraireAbsence) {
		this.idBaseHoraireAbsence = idBaseHoraireAbsence;
	}

	public Date getDateHisto() {
		return dateHisto;
	}

	public void setDateHisto(Date dateHisto) {
		this.dateHisto = dateHisto;
	}
}
