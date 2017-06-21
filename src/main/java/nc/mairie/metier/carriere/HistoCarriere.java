package nc.mairie.metier.carriere;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier HistoCarriere
 */
public class HistoCarriere {

	public Integer noMatricule;
	public Integer codeCategorie;
	public String codeGrade;
	public Integer refArrete;
	public Date dateDebut;
	public Date dateFin;
	public String modeReg;
	public Double montantForfait;
	public Integer codeEmploi;
	public String codeBase;
	public Integer codeTypeEmploi;
	public Double codeBaseHor2;
	public String iban;
	public String codeMotifPromo;
	public Double accJour;
	public Double accMois;
	public Double accAnnee;
	public Double bmJour;
	public Double bmMois;
	public Double bmAnnee;
	public Date dateArrete;
	public String cddcdica;

	public String typeHisto;
	public String userHisto;

	/**
	 * Constructeur HistoCarriere.
	 */
	public HistoCarriere() {
		super();
	}

	/**
	 * Constructeur HistoCarriere.
	 * 
	 * @throws ParseException
	 */
	public HistoCarriere(Carriere carriere) throws ParseException {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		this.noMatricule = Integer.valueOf(carriere.getNoMatricule());
		this.codeCategorie = Integer.valueOf(carriere.getCodeCategorie());
		this.codeGrade = carriere.getCodeGrade();
		this.refArrete = carriere.getReferenceArrete() == null || carriere.getReferenceArrete().equals(Const.CHAINE_VIDE) ? 0 : Integer.valueOf(carriere.getReferenceArrete());
		this.dateDebut = sdf.parse(carriere.getDateDebut());
		this.dateFin = carriere.getDateFin() == null || carriere.getDateFin().equals(Const.ZERO) || carriere.getDateFin().equals(Const.CHAINE_VIDE) ? null : sdf.parse(carriere.getDateFin());
		this.modeReg = carriere.getModeReglement();
		this.montantForfait = carriere.getMonantForfait() == null ? null : Double.valueOf(carriere.getMonantForfait());
		this.codeEmploi = carriere.getCodeEmploi() == null ? null : Integer.valueOf(carriere.getCodeEmploi());
		this.codeBase = carriere.getCodeBase();
		this.codeTypeEmploi = carriere.getCodeTypeEmploi() == null ? null : Integer.valueOf(carriere.getCodeTypeEmploi());
		this.codeBaseHor2 = Double.valueOf(carriere.getCodeBaseHoraire2());
		this.iban = carriere.getIban();
		this.codeMotifPromo = carriere.getIdMotif();
		this.accJour = carriere.getACCJour() == null ? null : Double.valueOf(carriere.getACCJour());
		this.accMois = carriere.getACCMois() == null ? null : Double.valueOf(carriere.getACCMois());
		this.accAnnee = carriere.getACCAnnee() == null ? null : Double.valueOf(carriere.getACCAnnee());
		this.bmJour = carriere.getBMJour() == null ? null : Double.valueOf(carriere.getBMJour());
		this.bmMois = carriere.getBMMois() == null ? null : Double.valueOf(carriere.getBMMois());
		this.bmAnnee = carriere.getBMAnnee() == null ? null : Double.valueOf(carriere.getBMAnnee());
		this.dateArrete = carriere.getDateArrete() == null || carriere.getDateArrete().equals(Const.ZERO) || carriere.getDateArrete().equals(Const.CHAINE_VIDE) ? null : sdf.parse(carriere
				.getDateArrete());
		this.cddcdica = carriere.getTypeContrat();
	}

	public Integer getNoMatricule() {
		return noMatricule;
	}

	public void setNoMatricule(Integer noMatricule) {
		this.noMatricule = noMatricule;
	}

	public Integer getCodeCategorie() {
		return codeCategorie;
	}

	public void setCodeCategorie(Integer codeCategorie) {
		this.codeCategorie = codeCategorie;
	}

	public String getCodeGrade() {
		return codeGrade;
	}

	public void setCodeGrade(String codeGrade) {
		this.codeGrade = codeGrade;
	}

	public Integer getRefArrete() {
		return refArrete;
	}

	public void setRefArrete(Integer refArrete) {
		this.refArrete = refArrete;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public String getModeReg() {
		return modeReg;
	}

	public void setModeReg(String modeReg) {
		this.modeReg = modeReg;
	}

	public Double getMontantForfait() {
		return montantForfait;
	}

	public void setMontantForfait(Double montantForfait) {
		this.montantForfait = montantForfait;
	}

	public Integer getCodeEmploi() {
		return codeEmploi;
	}

	public void setCodeEmploi(Integer codeEmploi) {
		this.codeEmploi = codeEmploi;
	}

	public String getCodeBase() {
		return codeBase;
	}

	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}

	public Integer getCodeTypeEmploi() {
		return codeTypeEmploi;
	}

	public void setCodeTypeEmploi(Integer codeTypeEmploi) {
		this.codeTypeEmploi = codeTypeEmploi;
	}

	public Double getCodeBaseHor2() {
		return codeBaseHor2;
	}

	public void setCodeBaseHor2(Double codeBaseHor2) {
		this.codeBaseHor2 = codeBaseHor2;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getCodeMotifPromo() {
		return codeMotifPromo;
	}

	public void setCodeMotifPromo(String codeMotifPromo) {
		this.codeMotifPromo = codeMotifPromo;
	}

	public Double getAccJour() {
		return accJour;
	}

	public void setAccJour(Double accJour) {
		this.accJour = accJour;
	}

	public Double getAccMois() {
		return accMois;
	}

	public void setAccMois(Double accMois) {
		this.accMois = accMois;
	}

	public Double getAccAnnee() {
		return accAnnee;
	}

	public void setAccAnnee(Double accAnnee) {
		this.accAnnee = accAnnee;
	}

	public Double getBmJour() {
		return bmJour;
	}

	public void setBmJour(Double bmJour) {
		this.bmJour = bmJour;
	}

	public Double getBmMois() {
		return bmMois;
	}

	public void setBmMois(Double bmMois) {
		this.bmMois = bmMois;
	}

	public Double getBmAnnee() {
		return bmAnnee;
	}

	public void setBmAnnee(Double bmAnnee) {
		this.bmAnnee = bmAnnee;
	}

	public Date getDateArrete() {
		return dateArrete;
	}

	public void setDateArrete(Date dateArrete) {
		this.dateArrete = dateArrete;
	}

	public String getCddcdica() {
		return cddcdica;
	}

	public void setCddcdica(String cddcdica) {
		this.cddcdica = cddcdica;
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
