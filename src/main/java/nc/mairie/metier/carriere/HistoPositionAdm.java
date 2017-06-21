package nc.mairie.metier.carriere;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.PositionAdmAgent;

/**
 * Objet metier HistoPositionAdm
 */
public class HistoPositionAdm {

	public Integer noMatricule;
	public Date dateDebut;
	public String codePosa;
	public Date dateFin;
	public String refArr;
	public String dateHisto;
	public String typeHisto;
	public String userHisto;

	/**
	 * Constructeur HistoPositionAdm.
	 */
	public HistoPositionAdm() {
		super();
	}

	/**
	 * Constructeur HistoPositionAdmin.
	 * 
	 * @throws ParseException
	 */
	public HistoPositionAdm(PositionAdmAgent posa) throws ParseException {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		this.setNoMatricule(Integer.valueOf(posa.getNomatr()));
		this.setDateDebut(sdf.parse(posa.getDatdeb()));
		this.setCodePosa(posa.getCdpadm());
		this.setDateFin(posa.getDatfin() == null || posa.getDatfin().equals(Const.ZERO)
				|| posa.getDatfin().equals(Const.CHAINE_VIDE) ? null : sdf.parse(posa.getDatfin()));
		this.setRefArr(posa.getRefarr());
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
	 * Getter de l'attribut codePosa.
	 */
	public String getCodePosa() {
		return codePosa;
	}

	/**
	 * Setter de l'attribut codePosa.
	 */
	public void setCodePosa(String newCodePosa) {
		codePosa = newCodePosa;
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
	 * Getter de l'attribut refArr.
	 */
	public String getRefArr() {
		return refArr;
	}

	/**
	 * Setter de l'attribut refArr.
	 */
	public void setRefArr(String newRefArr) {
		refArr = newRefArr;
	}

	/**
	 * Getter de l'attribut dateHisto.
	 */
	public String getDateHisto() {
		return dateHisto;
	}

	/**
	 * Setter de l'attribut dateHisto.
	 */
	public void setDateHisto(String newDateHisto) {
		dateHisto = newDateHisto;
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
