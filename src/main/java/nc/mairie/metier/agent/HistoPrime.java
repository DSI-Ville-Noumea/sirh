package nc.mairie.metier.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier HistoPrime
 */
public class HistoPrime {

	public Integer noMatricule;
	public Integer noRubrique;
	public Integer refArrete;
	public Date dateDebut;
	public Date dateFin;
	public Date dateArrete;
	public Double montant;
	public String typeHisto;
	public String userHisto;

	/**
	 * Constructeur HistoPrime.
	 */
	public HistoPrime() {
		super();
	}

	/**
	 * Constructeur HistoPrime.
	 * 
	 * @throws Exception
	 */
	public HistoPrime(Prime prime) throws Exception {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		this.noMatricule = Integer.valueOf(prime.getNoMatr());
		this.noRubrique = Integer.valueOf(prime.getNoRubr());
		this.refArrete = Integer.valueOf(prime.getRefArr());
		this.dateDebut = sdf.parse(prime.getDatDeb());
		this.dateFin = prime.getDatFin() == null || prime.getDatFin().equals(Const.ZERO)
				|| prime.getDatFin().equals(Const.CHAINE_VIDE) ? null : sdf.parse(prime.getDatFin());
		this.dateArrete = prime.getDateArrete() == null || prime.getDateArrete().equals(Const.ZERO)
				|| prime.getDateArrete().equals(Const.CHAINE_VIDE) ? null : sdf.parse(prime.getDateArrete());
		this.montant = Double.valueOf(prime.getMtPri());
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
	 * Getter de l'attribut refArrete.
	 */
	public Integer getRefArrete() {
		return refArrete;
	}

	/**
	 * Setter de l'attribut refArrete.
	 */
	public void setRefArrete(Integer newRefArrete) {
		refArrete = newRefArrete;
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
	 * Getter de l'attribut date_arrete.
	 */
	public Date getDateArrete() {
		return dateArrete;
	}

	/**
	 * Setter de l'attribut date_arrete.
	 */
	public void setDateArrete(Date newDate_arrete) {
		dateArrete = newDate_arrete;
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
