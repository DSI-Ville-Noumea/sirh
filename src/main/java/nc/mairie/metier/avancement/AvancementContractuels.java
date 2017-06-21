package nc.mairie.metier.avancement;

import java.util.Date;

/**
 * Objet metier Avancement
 */
public class AvancementContractuels {

	public Integer idAvct;
	public Integer idAgent;
	public Date dateEmbauche;
	public String numFp;
	public String pa;
	public Date dateGrade;
	public Date dateProchainGrade;
	public String grade;
	public String idNouvGrade;
	public String iban;
	public Integer inm;
	public Integer ina;
	public String nouvIban;
	public Integer nouvInm;
	public Integer nouvIna;
	public String etat;
	public Date dateArrete;
	public String numArrete;
	public String carriereSimu;
	public Integer annee;
	public String directionService;
	public String sectionService;
	// code cadre
	public String cdcadr;

	/**
	 * Constructeur Avancement.
	 */
	public AvancementContractuels() {
		super();
	}

	public Integer getIdAvct() {
		return idAvct;
	}

	public void setIdAvct(Integer idAvct) {
		this.idAvct = idAvct;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateEmbauche() {
		return dateEmbauche;
	}

	public void setDateEmbauche(Date dateEmbauche) {
		this.dateEmbauche = dateEmbauche;
	}

	public String getNumFp() {
		return numFp;
	}

	public void setNumFp(String numFp) {
		this.numFp = numFp;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public Date getDateGrade() {
		return dateGrade;
	}

	public void setDateGrade(Date dateGrade) {
		this.dateGrade = dateGrade;
	}

	public Date getDateProchainGrade() {
		return dateProchainGrade;
	}

	public void setDateProchainGrade(Date dateProchainGrade) {
		this.dateProchainGrade = dateProchainGrade;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public Integer getInm() {
		return inm;
	}

	public void setInm(Integer inm) {
		this.inm = inm;
	}

	public Integer getIna() {
		return ina;
	}

	public void setIna(Integer ina) {
		this.ina = ina;
	}

	public String getNouvIban() {
		return nouvIban;
	}

	public void setNouvIban(String nouvIban) {
		this.nouvIban = nouvIban;
	}

	public Integer getNouvInm() {
		return nouvInm;
	}

	public void setNouvInm(Integer nouvInm) {
		this.nouvInm = nouvInm;
	}

	public Integer getNouvIna() {
		return nouvIna;
	}

	public void setNouvIna(Integer nouvIna) {
		this.nouvIna = nouvIna;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	public Date getDateArrete() {
		return dateArrete;
	}

	public void setDateArrete(Date dateArrete) {
		this.dateArrete = dateArrete;
	}

	public String getNumArrete() {
		return numArrete;
	}

	public void setNumArrete(String numArrete) {
		this.numArrete = numArrete;
	}

	public String getCarriereSimu() {
		return carriereSimu;
	}

	public void setCarriereSimu(String carriereSimu) {
		this.carriereSimu = carriereSimu;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public String getDirectionService() {
		return directionService;
	}

	public void setDirectionService(String directionService) {
		this.directionService = directionService;
	}

	public String getSectionService() {
		return sectionService;
	}

	public void setSectionService(String sectionService) {
		this.sectionService = sectionService;
	}

	public String getCdcadr() {
		return cdcadr;
	}

	public void setCdcadr(String cdcadr) {
		this.cdcadr = cdcadr;
	}

	@Override
	public boolean equals(Object object) {
		return idAvct.toString().equals(((AvancementContractuels) object).getIdAvct().toString());
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getIdNouvGrade() {
		return idNouvGrade;
	}

	public void setIdNouvGrade(String idNouvGrade) {
		this.idNouvGrade = idNouvGrade;
	}
}
