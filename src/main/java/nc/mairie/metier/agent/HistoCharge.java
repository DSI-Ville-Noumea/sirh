package nc.mairie.metier.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier HistoCharge
 */
public class HistoCharge {

	public Integer noMatricule;
	public Integer noRubrique;
	public Integer codeCreancier;
	public String noMate;
	public Integer codeCharge;
	public Double taux;
	public Double montant;
	public Date dateFin;
	public Date dateDebut;
	public String typeHisto;
	public String userHisto;

	/**
	 * Constructeur HistoCharge.
	 */
	public HistoCharge() {
		super();
	}

	/**
	 * Constructeur HistoCharge.
	 * 
	 * @throws Exception
	 */
	public HistoCharge(Charge charge) throws Exception {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		setNoMatricule(Integer.valueOf(charge.getNoMatr()));
		setNoRubrique(Integer.valueOf(charge.getNoRubr()));
		setCodeCreancier(Integer.valueOf(charge.getCdCrea()));
		setNoMate(charge.getNoMate());
		setCodeCharge(Integer.valueOf(charge.getCdChar()));
		setTaux(Double.valueOf(charge.getTxSal()));
		setMontant(Double.valueOf(charge.getMttreg()));
		setDateFin(charge.getDatFin() == null || charge.getDatFin().equals(Const.ZERO)
				|| charge.getDatFin().equals(Const.CHAINE_VIDE) ? null : sdf.parse(charge.getDatFin()));
		setDateDebut(sdf.parse(charge.getDatDeb()));
	}

	/**
	 * Getter de l'attribut noMatricule.
	 */
	public Integer getNoMatricule() {
		return noMatricule;
	}

	/**
	 * Setter de l'attribut noMatricule.
	 */
	public void setNoMatricule(Integer newNoMatricule) {
		noMatricule = newNoMatricule;
	}

	/**
	 * Getter de l'attribut noRubrique.
	 */
	public Integer getNoRubrique() {
		return noRubrique;
	}

	/**
	 * Setter de l'attribut noRubrique.
	 */
	public void setNoRubrique(Integer newNoRubrique) {
		noRubrique = newNoRubrique;
	}

	/**
	 * Getter de l'attribut codeCreancier.
	 */
	public Integer getCodeCreancier() {
		return codeCreancier;
	}

	/**
	 * Setter de l'attribut codeCreancier.
	 */
	public void setCodeCreancier(Integer newCodeCreancier) {
		codeCreancier = newCodeCreancier;
	}

	/**
	 * Getter de l'attribut noMate.
	 */
	public String getNoMate() {
		return noMate;
	}

	/**
	 * Setter de l'attribut noMate.
	 */
	public void setNoMate(String newNoMate) {
		noMate = newNoMate;
	}

	/**
	 * Getter de l'attribut codeCharge.
	 */
	public Integer getCodeCharge() {
		return codeCharge;
	}

	/**
	 * Setter de l'attribut codeCharge.
	 */
	public void setCodeCharge(Integer newCodeCharge) {
		codeCharge = newCodeCharge;
	}

	/**
	 * Getter de l'attribut taux.
	 */
	public Double getTaux() {
		return taux;
	}

	/**
	 * Setter de l'attribut taux.
	 */
	public void setTaux(Double newTaux) {
		taux = newTaux;
	}

	/**
	 * Getter de l'attribut montant.
	 */
	public Double getMontant() {
		return montant;
	}

	/**
	 * Setter de l'attribut montant.
	 */
	public void setMontant(Double newMontant) {
		montant = newMontant;

	}

	/**
	 * Getter de l'attribut dateFin.
	 */
	public Date getDateFin() {
		return dateFin;
	}

	/**
	 * Setter de l'attribut dateFin.
	 */
	public void setDateFin(Date newDateFin) {
		dateFin = newDateFin;
	}

	/**
	 * Getter de l'attribut dateDebut.
	 */
	public Date getDateDebut() {
		return dateDebut;
	}

	/**
	 * Setter de l'attribut dateDebut.
	 */
	public void setDateDebut(Date newDateDebut) {
		dateDebut = newDateDebut;
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
}
