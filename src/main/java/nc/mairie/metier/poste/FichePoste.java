package nc.mairie.metier.poste;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier FichePoste
 */
public class FichePoste implements Cloneable {

	public Integer idFichePoste;
	public Integer idTitrePoste;
	public String codeGrade;
	public Integer idEntiteGeo;
	public Integer idBudget;
	public Integer idStatutFp;
	public Integer idResponsable;
	public Integer idRemplacement;
	public Integer idCdthorBud;
	public Integer idCdthorReg;
	public String idServi;
	public Integer anneeCreation;
	public String numFp;
	public Date dateFinValiditeFp;
	public String opi;
	public String nfa;
	public String observation;
	public String missions;
	public Date dateDebutValiditeFp;
	public Date dateDebAppliServ;
	public Integer idNatureCredit;
	public String numDeliberation;
	public Integer idBaseHorairePointage;
	public Integer idBaseHoraireAbsence;
	public Integer idServiceAds;
	public String specialisation;
	public String informations_complementaires;

	/**
	 * Constructeur FichePoste.
	 */
	public FichePoste() {
		super();
	}

	/**
	 * Constructeur HistoAffectation.
	 */
	public FichePoste(HistoFichePoste histoFichePoste) {
		super();
		this.idTitrePoste = histoFichePoste.getIdTitrePoste();
		this.codeGrade = histoFichePoste.getCodeGrade();
		this.idEntiteGeo = histoFichePoste.getIdEntiteGeo();
		this.idBudget = histoFichePoste.getIdBudget();
		this.idFichePoste = histoFichePoste.getIdFichePoste();
		this.idStatutFp = histoFichePoste.getIdStatutFp();
		this.idResponsable = histoFichePoste.getIdResponsable();
		this.idRemplacement = histoFichePoste.getIdRemplacement();
		this.idCdthorBud = histoFichePoste.getIdCdthorBud();
		this.idCdthorReg = histoFichePoste.getIdCdthorReg();
		this.idServi = histoFichePoste.getIdServi();
		this.anneeCreation = histoFichePoste.getAnneeCreation();
		this.numFp = histoFichePoste.getNumFp();
		this.dateFinValiditeFp = histoFichePoste.getDateFinValiditeFp();
		this.dateDebutValiditeFp = histoFichePoste.getDateDebutValiditeFp();
		this.opi = histoFichePoste.getOpi();
		this.nfa = histoFichePoste.getNfa();
		this.missions = histoFichePoste.getMissions();
		this.dateDebAppliServ = histoFichePoste.getDateDebAppliServ();
		this.idNatureCredit = histoFichePoste.getIdNatureCredit();
		this.numDeliberation = histoFichePoste.getNumDeliberation();
		this.idBaseHorairePointage = histoFichePoste.getIdBaseHorairePointage();
		this.idBaseHoraireAbsence = histoFichePoste.getIdBaseHoraireAbsence();
		this.idServiceAds = histoFichePoste.getIdServiceAds();
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
	 * Getter de l'attribut idTitrePoste.
	 */
	public Integer getIdTitrePoste() {
		return idTitrePoste;
	}

	/**
	 * Setter de l'attribut idTitrePoste.
	 */
	public void setIdTitrePoste(Integer newIdTitrePoste) {
		idTitrePoste = newIdTitrePoste;
	}

	/**
	 * Getter de l'attribut codeGrade.
	 */
	public String getCodeGrade() {
		return codeGrade;
	}

	/**
	 * Setter de l'attribut codeGrade.
	 */
	public void setCodeGrade(String newCodeGrade) {
		codeGrade = newCodeGrade;
	}

	/**
	 * Getter de l'attribut idEntiteGeo.
	 */
	public Integer getIdEntiteGeo() {
		return idEntiteGeo;
	}

	/**
	 * Setter de l'attribut idEntiteGeo.
	 */
	public void setIdEntiteGeo(Integer newIdEntiteGeo) {
		idEntiteGeo = newIdEntiteGeo;
	}

	/**
	 * Getter de l'attribut idBudget.
	 */
	public Integer getIdBudget() {
		return idBudget;
	}

	/**
	 * Setter de l'attribut idBudget.
	 */
	public void setIdBudget(Integer newIdBudget) {
		idBudget = newIdBudget;
	}

	/**
	 * Getter de l'attribut idStatutFP.
	 */
	public Integer getIdStatutFp() {
		return idStatutFp;
	}

	/**
	 * Setter de l'attribut idStatutFP.
	 */
	public void setIdStatutFp(Integer newIdStatutFP) {
		idStatutFp = newIdStatutFP;
	}

	/**
	 * Getter de l'attribut ficIdFichePoste.
	 */
	public Integer getIdResponsable() {
		return idResponsable;
	}

	/**
	 * Setter de l'attribut ficIdFichePoste.
	 */
	public void setIdResponsable(Integer newIdResponsable) {
		idResponsable = newIdResponsable;
	}

	/**
	 * Getter de l'attribut idCdthorBud.
	 */
	public Integer getIdCdthorBud() {
		return idCdthorBud;
	}

	/**
	 * Setter de l'attribut idCdthorBud.
	 */
	public void setIdCdthorBud(Integer newIdCdthorBud) {
		idCdthorBud = newIdCdthorBud;
	}

	/**
	 * Getter de l'attribut idCdthorReg.
	 */
	public Integer getIdCdthorReg() {
		return idCdthorReg;
	}

	/**
	 * Setter de l'attribut idCdthorReg.
	 */
	public void setIdCdthorReg(Integer newIdCdthorReg) {
		idCdthorReg = newIdCdthorReg;
	}

	/**
	 * Getter de l'attribut anneeCreation.
	 */
	public Integer getAnneeCreation() {
		return anneeCreation;
	}

	/**
	 * Setter de l'attribut anneeCreation.
	 */
	public void setAnneeCreation(Integer newAnneeCreation) {
		anneeCreation = newAnneeCreation;
	}

	/**
	 * Getter de l'attribut numero_fp.
	 */
	public String getNumFp() {
		return numFp;
	}

	/**
	 * Setter de l'attribut numero_fp.
	 */
	public void setNumFp(String newNumFP) {
		numFp = newNumFP;
	}

	/**
	 * Getter de l'attribut dateFinValiditeFP.
	 */
	public Date getDateFinValiditeFp() {
		return dateFinValiditeFp;
	}

	/**
	 * Setter de l'attribut dateFinValiditeFP.
	 */
	public void setDateFinValiditeFp(Date newDateFinValiditeFP) {
		dateFinValiditeFp = newDateFinValiditeFP;
	}

	/**
	 * Getter de l'attribut dateDebutValiditeFP.
	 */
	public Date getDateDebutValiditeFp() {
		return dateDebutValiditeFp;
	}

	/**
	 * Setter de l'attribut dateDebutValiditeFP.
	 */
	public void setDateDebutValiditeFp(Date newDateDebutValiditeFP) {
		dateDebutValiditeFp = newDateDebutValiditeFP;
	}

	public Date getDateDebAppliServ() {
		return dateDebAppliServ;
	}

	public void setDateDebAppliServ(Date newDateDebAppliService) {
		dateDebAppliServ = newDateDebAppliService;
	}

	/**
	 * Getter de l'attribut OPI.
	 */
	public String getOpi() {
		return opi;
	}

	/**
	 * Setter de l'attribut OPI.
	 */
	public void setOpi(String newOPI) {
		opi = newOPI;
	}

	/**
	 * Getter de l'attribut NFA.
	 */
	public String getNfa() {
		return nfa;
	}

	/**
	 * Setter de l'attribut NFA.
	 */
	public void setNfa(String newNFA) {
		nfa = newNFA;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}

	public String getInformations_complementaires() {
		return informations_complementaires;
	}

	public void setInformations_complementaires(String informations_complementaires) {
		this.informations_complementaires = informations_complementaires;
	}

	/**
	 * Getter de l'attribut missions.
	 */
	public String getMissions() {
		return missions == null ? Const.CHAINE_VIDE : missions.trim();
	}

	/**
	 * Setter de l'attribut missions.
	 */
	public void setMissions(String newMissions) {
		missions = newMissions;
	}

	public Integer getIdRemplacement() {
		return idRemplacement;
	}

	public void setIdRemplacement(Integer idRemplacement) {
		this.idRemplacement = idRemplacement;
	}

	public Integer getIdNatureCredit() {
		return idNatureCredit;
	}

	public void setIdNatureCredit(Integer idNatureCredit) {
		this.idNatureCredit = idNatureCredit;
	}

	public String getNumDeliberation() {
		return numDeliberation;
	}

	public void setNumDeliberation(String numDeliberation) {
		this.numDeliberation = numDeliberation;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object object) {
		return idFichePoste.toString().equals(((FichePoste) object).getIdFichePoste().toString());
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

	public Integer getIdServiceAds() {
		return idServiceAds;
	}

	public void setIdServiceAds(Integer idServiceADS) {
		this.idServiceAds = idServiceADS;
	}

	public String getIdServi() {
		return idServi;
	}

	public void setIdServi(String idServi) {
		this.idServi = idServi;
	}
}
