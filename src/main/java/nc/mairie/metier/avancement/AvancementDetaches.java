package nc.mairie.metier.avancement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.technique.Services;

/**
 * Objet metier Avancement
 */
public class AvancementDetaches {

	public Integer idAvct;
	public Integer idAgent;
	public Integer idMotifAvct;
	public String directionService;
	public String sectionService;
	public String filiere;
	public String grade;
	public String idNouvGrade;
	public Integer annee;
	public String cdcadr;
	public Integer bmAnnee;
	public Integer bmMois;
	public Integer bmJour;
	public Integer accAnnee;
	public Integer accMois;
	public Integer accJour;
	public Integer nouvBmAnnee;
	public Integer nouvBmMois;
	public Integer nouvBmJour;
	public Integer nouvAccAnnee;
	public Integer nouvAccMois;
	public Integer nouvAccJour;
	public String iban;
	public Integer inm;
	public Integer ina;
	public String nouvIban;
	public Integer nouvInm;
	public Integer nouvIna;
	public Date dateGrade;
	public Integer periodeStandard;
	public Date dateAvctMoy;
	public String numArrete;
	public Date dateArrete;
	public String etat;
	public Integer codeCategorie;
	public String carriereSimu;
	public String userVerifSgc;
	public Date dateVerifSgc;
	public String heureVerifSgc;
	public String userVerifSef;
	public Date dateVerifSef;
	public String heureVerifSef;
	public String userVerifArr;
	public Date dateVerifArr;
	public String heureVerifArr;
	public String observationArr;
	public String userVerifArrImpr;
	public Date dateVerifArrImpr;
	public String heureVerifArrImpr;
	public boolean regularisation;
	public boolean agentVdn;
	public String codePa;

	/**
	 * Constructeur Avancement.
	 */
	public AvancementDetaches() {
		super();
	}/*
	 * 
	 * /** Getter de l'attribut idAvct.
	 */

	public Integer getIdAvct() {
		return idAvct;
	}

	/**
	 * Setter de l'attribut idAvct.
	 */
	public void setIdAvct(Integer newIdAvct) {
		idAvct = newIdAvct;
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
	 * Getter de l'attribut idMotifAvct.
	 */
	public Integer getIdMotifAvct() {
		return idMotifAvct;
	}

	/**
	 * Setter de l'attribut idMotifAvct.
	 */
	public void setIdMotifAvct(Integer newIdMotifAvct) {
		idMotifAvct = newIdMotifAvct;
	}

	/**
	 * Getter de l'attribut directionService.
	 */
	public String getDirectionService() {
		return directionService == null ? Const.CHAINE_VIDE : directionService;
	}

	/**
	 * Setter de l'attribut directionService.
	 */
	public void setDirectionService(String newDirectionService) {
		directionService = newDirectionService;
	}

	/**
	 * Getter de l'attribut sectionService.
	 */
	public String getSectionService() {
		return sectionService == null ? Const.CHAINE_VIDE : sectionService;
	}

	/**
	 * Setter de l'attribut sectionService.
	 */
	public void setSectionService(String newSectionService) {
		sectionService = newSectionService;
	}

	/**
	 * Getter de l'attribut filiere.
	 */
	public String getFiliere() {
		return filiere;
	}

	/**
	 * Setter de l'attribut filiere.
	 */
	public void setFiliere(String newFiliere) {
		filiere = newFiliere;
	}

	/**
	 * Getter de l'attribut grade.
	 */
	public String getGrade() {
		return grade;
	}

	/**
	 * Setter de l'attribut grade.
	 */
	public void setGrade(String newGrade) {
		grade = newGrade;
	}

	/**
	 * Getter de l'attribut idNouvGrade.
	 */
	public String getIdNouvGrade() {
		return idNouvGrade;
	}

	/**
	 * Setter de l'attribut idNouvGrade.
	 */
	public void setIdNouvGrade(String newIdNouvGrade) {
		idNouvGrade = newIdNouvGrade;
	}

	/**
	 * Getter de l'attribut annee.
	 */
	public Integer getAnnee() {
		return annee;
	}

	/**
	 * Setter de l'attribut annee.
	 */
	public void setAnnee(Integer newAnnee) {
		annee = newAnnee;
	}

	/**
	 * Getter de l'attribut nouvBMAnnee.
	 */
	public Integer getNouvBmAnnee() {
		return nouvBmAnnee;
	}

	/**
	 * Setter de l'attribut nouvBMAnnee.
	 */
	public void setNouvBmAnnee(Integer newNouvBmAnnee) {
		nouvBmAnnee = newNouvBmAnnee;
	}

	/**
	 * Getter de l'attribut nouvBMMois.
	 */
	public Integer getNouvBmMois() {
		return nouvBmMois;
	}

	/**
	 * Setter de l'attribut nouvBMMois.
	 */
	public void setNouvBmMois(Integer newNouvBmMois) {
		nouvBmMois = newNouvBmMois;
	}

	/**
	 * Getter de l'attribut nouvBMJour.
	 */
	public Integer getNouvBmJour() {
		return nouvBmJour;
	}

	/**
	 * Setter de l'attribut nouvBMJour.
	 */
	public void setNouvBmJour(Integer newNouvBmJour) {
		nouvBmJour = newNouvBmJour;
	}

	/**
	 * Getter de l'attribut nouvACCAnnee.
	 */
	public Integer getNouvAccAnnee() {
		return nouvAccAnnee;
	}

	/**
	 * Setter de l'attribut nouvACCAnnee.
	 */
	public void setNouvAccAnnee(Integer newNouvAccAnnee) {
		nouvAccAnnee = newNouvAccAnnee;
	}

	/**
	 * Getter de l'attribut nouvACCMois.
	 */
	public Integer getNouvAccMois() {
		return nouvAccMois;
	}

	/**
	 * Setter de l'attribut nouvACCMois.
	 */
	public void setNouvAccMois(Integer newNouvAccMois) {
		nouvAccMois = newNouvAccMois;
	}

	/**
	 * Getter de l'attribut nouvACCJour.
	 */
	public Integer getNouvAccJour() {
		return nouvAccJour;
	}

	/**
	 * Setter de l'attribut nouvACCJour.
	 */
	public void setNouvAccJour(Integer newNouvAccJour) {
		nouvAccJour = newNouvAccJour;
	}

	/**
	 * Getter de l'attribut iba.
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * Setter de l'attribut iban.
	 */
	public void setIban(String newIban) {
		iban = newIban;
	}

	/**
	 * Getter de l'attribut inm.
	 */
	public Integer getInm() {
		return inm;
	}

	/**
	 * Setter de l'attribut inm.
	 */
	public void setInm(Integer newInm) {
		inm = newInm;
	}

	/**
	 * Getter de l'attribut ina.
	 */
	public Integer getIna() {
		return ina;
	}

	/**
	 * Setter de l'attribut ina.
	 */
	public void setIna(Integer newIna) {
		ina = newIna;
	}

	/**
	 * Getter de l'attribut nouvIba.
	 */
	public String getNouvIban() {
		return nouvIban;
	}

	/**
	 * Setter de l'attribut nouvIba.
	 */
	public void setNouvIban(String newNouvIBAN) {
		nouvIban = newNouvIBAN;
	}

	/**
	 * Getter de l'attribut nouvInm.
	 */
	public Integer getNouvInm() {
		return nouvInm;
	}

	/**
	 * Setter de l'attribut nouvInm.
	 */
	public void setNouvInm(Integer newNouvINM) {
		nouvInm = newNouvINM;
	}

	/**
	 * Getter de l'attribut nouvIna.
	 */
	public Integer getNouvIna() {
		return nouvIna;
	}

	/**
	 * Setter de l'attribut nouvIna.
	 */
	public void setNouvIna(Integer newNouvINA) {
		nouvIna = newNouvINA;
	}

	/**
	 * Getter de l'attribut dateGrade.
	 */
	public Date getDateGrade() {
		return dateGrade;
	}

	/**
	 * Setter de l'attribut dateGrade.
	 */
	public void setDateGrade(Date newDateGrade) {
		dateGrade = newDateGrade;
	}

	/**
	 * Getter de l'attribut dureeStandard.
	 */
	public Integer getPeriodeStandard() {
		return periodeStandard;
	}

	/**
	 * Setter de l'attribut dureeStandard.
	 */
	public void setPeriodeStandard(Integer newPeriodeStandard) {
		periodeStandard = newPeriodeStandard;
	}

	/**
	 * Getter de l'attribut dateAvctMoy.
	 */
	public Date getDateAvctMoy() {
		return dateAvctMoy;
	}

	/**
	 * Setter de l'attribut dateAvctMoy.
	 */
	public void setDateAvctMoy(Date newDateAvctMoy) {
		dateAvctMoy = newDateAvctMoy;
	}

	/*
	 * /** Getter de l'attribut numArrete.
	 */
	public String getNumArrete() {
		return numArrete;
	}

	/**
	 * Setter de l'attribut numArrete.
	 */
	public void setNumArrete(String newNumArrete) {
		numArrete = newNumArrete;
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
	 * Getter de l'attribut etat.
	 */
	public String getEtat() {
		return etat;
	}

	/**
	 * Setter de l'attribut etat.
	 */
	public void setEtat(String newEtat) {
		etat = newEtat;
	}

	public Integer getAccAnnee() {
		return accAnnee;
	}

	public void setAccAnnee(Integer annee) {
		accAnnee = annee;
	}

	public Integer getAccJour() {
		return accJour;
	}

	public void setAccJour(Integer jour) {
		accJour = jour;
	}

	public Integer getAccMois() {
		return accMois;
	}

	public void setAccMois(Integer mois) {
		accMois = mois;
	}

	public Integer getBmAnnee() {
		return bmAnnee;
	}

	public void setBmAnnee(Integer annee) {
		bmAnnee = annee;
	}

	public Integer getBmJour() {
		return bmJour;
	}

	public void setBmJour(Integer jour) {
		bmJour = jour;
	}

	public Integer getBmMois() {
		return bmMois;
	}

	public void setBmMois(Integer mois) {
		bmMois = mois;
	}

	public String getCarriereSimu() {
		return carriereSimu;
	}

	public void setCarriereSimu(String carriereSimu) {
		this.carriereSimu = carriereSimu;
	}

	public String getCdcadr() {
		return cdcadr;
	}

	public void setCdcadr(String codeCadre) {
		this.cdcadr = codeCadre;
	}

	public String getUserVerifSgc() {
		return userVerifSgc;
	}

	public void setUserVerifSgc(String userVerifSGC) {
		this.userVerifSgc = userVerifSGC;
	}

	public Date getDateVerifSgc() {
		return dateVerifSgc;
	}

	public void setDateVerifSgc(Date dateVerifSGC) {
		this.dateVerifSgc = dateVerifSGC;
	}

	public String getHeureVerifSgc() {
		return heureVerifSgc;
	}

	public void setHeureVerifSgc(String heureVerifSGC) {
		this.heureVerifSgc = heureVerifSGC;
	}

	public String getUserVerifSef() {
		return userVerifSef;
	}

	public void setUserVerifSef(String userVerifSEF) {
		this.userVerifSef = userVerifSEF;
	}

	public Date getDateVerifSef() {
		return dateVerifSef;
	}

	public void setDateVerifSef(Date dateVerifSEF) {
		this.dateVerifSef = dateVerifSEF;
	}

	public String getHeureVerifSef() {
		return heureVerifSef;
	}

	public void setHeureVerifSef(String heureVerifSEF) {
		this.heureVerifSef = heureVerifSEF;
	}

	public Integer getCodeCategorie() {
		return codeCategorie;
	}

	public void setCodeCategorie(Integer codeCategorie) {
		this.codeCategorie = codeCategorie;
	}

	public String getUserVerifArr() {
		return userVerifArr;
	}

	public void setUserVerifArr(String userVerifArr) {
		this.userVerifArr = userVerifArr;
	}

	public Date getDateVerifArr() {
		return dateVerifArr;
	}

	public void setDateVerifArr(Date dateVerifArr) {
		this.dateVerifArr = dateVerifArr;
	}

	public String getHeureVerifArr() {
		return heureVerifArr;
	}

	public void setHeureVerifArr(String heureVerifArr) {
		this.heureVerifArr = heureVerifArr;
	}

	public String getObservationArr() {
		return observationArr;
	}

	public void setObservationArr(String observationArr) {
		this.observationArr = observationArr;
	}

	public String getUserVerifArrImpr() {
		return userVerifArrImpr;
	}

	public void setUserVerifArrImpr(String userVerifArrImpr) {
		this.userVerifArrImpr = userVerifArrImpr;
	}

	public Date getDateVerifArrImpr() {
		return dateVerifArrImpr;
	}

	public void setDateVerifArrImpr(Date dateVerifArrImpr) {
		this.dateVerifArrImpr = dateVerifArrImpr;
	}

	public String getHeureVerifArrImpr() {
		return heureVerifArrImpr;
	}

	public void setHeureVerifArrImpr(String heureVerifArrImpr) {
		this.heureVerifArrImpr = heureVerifArrImpr;
	}

	public boolean isRegularisation() {
		return regularisation;
	}

	public void setRegularisation(boolean regularisation) {
		this.regularisation = regularisation;
	}

	public static Integer calculJourBM(Grade gradeActuel, Carriere carr) {
		Integer nbJoursBM = 0;
		if (gradeActuel.getBm().equals(Const.OUI)) {
			nbJoursBM = (Integer.parseInt(carr.getBMAnnee()) * 360) + (Integer.parseInt(carr.getBMMois()) * 30)
					+ Integer.parseInt(carr.getBMJour());
		}
		return nbJoursBM;

	}

	public static Integer calculJourACC(Grade gradeActuel, Carriere carr) {
		Integer nbJoursACC = 0;
		if (gradeActuel.getAcc().equals(Const.OUI)) {
			nbJoursACC += (Integer.parseInt(carr.getACCAnnee()) * 360) + (Integer.parseInt(carr.getACCMois()) * 30)
					+ Integer.parseInt(carr.getACCJour());
		}
		return nbJoursACC;
	}

	public static Date calculDateAvctMoy(Grade gradeActuel, Carriere carr) throws ParseException {
		String dateSansAccBm = Services.ajouteMois(Services.formateDate(carr.getDateDebut()),
				Integer.parseInt(gradeActuel.getDureeMoy()));
		String dateAvcAnne = Services.enleveAnnee(dateSansAccBm,
				Integer.parseInt(carr.getACCAnnee()) + Integer.parseInt(carr.getBMAnnee()));
		String dateAvcMois = Services.enleveMois(dateAvcAnne,
				Integer.parseInt(carr.getACCMois()) + Integer.parseInt(carr.getBMMois()));
		String dateAvcJour = Services.enleveJours(dateAvcMois,
				Integer.parseInt(carr.getACCJour()) + Integer.parseInt(carr.getBMJour()));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.parse(dateAvcJour);

	}

	public boolean isAgentVdn() {
		return agentVdn;
	}

	public void setAgentVdn(boolean agentVDN) {
		this.agentVdn = agentVDN;
	}

	public String getCodePa() {
		return codePa;
	}

	public void setCodePa(String codePA) {
		this.codePa = codePA;
	}

	@Override
	public boolean equals(Object object) {
		return idAvct.toString().equals(((AvancementDetaches) object).getIdAvct().toString());
	}
}
