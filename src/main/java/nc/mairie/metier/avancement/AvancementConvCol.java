package nc.mairie.metier.avancement;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier Avancement
 */
public class AvancementConvCol {

	public Integer idAvct;
	public Integer idAgent;
	public Integer annee;
	public String etat;
	public String numArrete;
	public Date dateArrete;
	public Date dateEmbauche;
	public String grade;
	public String libGrade;
	public String directionService;
	public String sectionService;
	public String carriereSimu;
	public String montantPrime1200;
	public String codePa;

	/**
	 * Constructeur Avancement.
	 */
	public AvancementConvCol() {
		super();
	}

	/**
	 * Getter de l'attribut idAvct.
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
	 * Getter de l'attribut directionService.
	 */
	public String getDirectionService() {
		return directionService;
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
		return sectionService;
	}

	/**
	 * Setter de l'attribut sectionService.
	 */
	public void setSectionService(String newSectionService) {
		sectionService = newSectionService;
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
	 * Getter de l'attribut numArrete.
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
	 * Getter de l'attribut dateEmbauche.
	 */
	public Date getDateEmbauche() {
		return dateEmbauche;
	}

	/**
	 * Setter de l'attribut dateEmbauche.
	 */
	public void setDateEmbauche(Date newDateEmbauche) {
		dateEmbauche = newDateEmbauche;
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

	public String getMontantPrime1200() {
		return montantPrime1200;
	}

	public void setMontantPrime1200(String montantPrime1200) {
		this.montantPrime1200 = montantPrime1200;
	}

	public String getCarriereSimu() {
		return carriereSimu;
	}

	public void setCarriereSimu(String carriereSimu) {
		this.carriereSimu = carriereSimu;
	}

	public String getLibGrade() {
		return libGrade == null ? Const.CHAINE_VIDE : libGrade.trim();
	}

	public void setLibGrade(String libGrade) {
		this.libGrade = libGrade;
	}

	public String getCodePa() {
		return codePa;
	}

	public void setCodePa(String codePa) {
		this.codePa = codePa;
	}

	@Override
	public boolean equals(Object object) {
		return idAvct.toString().equals(((AvancementConvCol) object).getIdAvct().toString());
	}
}
