package nc.mairie.metier.poste;

import java.util.Date;

/**
 * Objet metier HistoFichePoste
 */
public class HistoFichePoste {

	public Integer idFichePoste;
	public Integer idTitrePoste;
	public Integer idEntiteGeo;
	public Integer idBudget;
	public Integer idStatutFp;
	public Integer idResponsable;
	public Integer idRemplacement;
	public Integer idCdthorBud;
	public Integer idCdthorReg;
	public String idServi;
	public Date dateFinValiditeFp;
	public String opi;
	public String nfa;
	public String missions;
	public Integer anneeCreation;
	public String codeGrade;
	public String numFp;
	public String typeHisto;
	public String userHisto;
	public Date dateDebutValiditeFp;
	public Date dateDebAppliServ;
	public Date dateFinAppliServ;
	public Integer idNatureCredit;
	public String numDeliberation;
	public Integer idBaseHorairePointage;
	public Integer idBaseHoraireAbsence;
	public Integer idServiceAds;

	/**
	 * Constructeur HistoFichePoste.
	 */
	public HistoFichePoste() {
		super();
	}

	/**
	 * Constructeur HistoAffectation.
	 */
	public HistoFichePoste(FichePoste fichePoste) {
		super();

		this.idFichePoste = fichePoste.getIdFichePoste();
		this.idTitrePoste = fichePoste.getIdTitrePoste();
		this.idEntiteGeo = fichePoste.getIdEntiteGeo();
		this.idBudget = fichePoste.getIdBudget();
		this.idStatutFp = fichePoste.getIdStatutFp();
		this.idResponsable = fichePoste.getIdResponsable();
		this.idRemplacement = fichePoste.getIdRemplacement();
		this.idCdthorBud = fichePoste.getIdCdthorBud();
		this.idCdthorReg = fichePoste.getIdCdthorReg();
		this.idServi = fichePoste.getIdServi();
		this.dateFinValiditeFp = fichePoste.getDateFinValiditeFp();
		this.dateDebutValiditeFp = fichePoste.getDateDebutValiditeFp();
		this.opi = fichePoste.getOpi();
		this.nfa = fichePoste.getNfa();
		this.missions = fichePoste.getMissions();
		this.anneeCreation = fichePoste.getAnneeCreation();
		this.codeGrade = fichePoste.getCodeGrade();
		this.numFp = fichePoste.getNumFp();
		this.dateDebAppliServ = fichePoste.getDateDebAppliServ();
		this.idNatureCredit = fichePoste.getIdNatureCredit();
		this.numDeliberation = fichePoste.getNumDeliberation();
		this.idBaseHorairePointage = fichePoste.getIdBaseHorairePointage();
		this.idBaseHoraireAbsence = fichePoste.getIdBaseHoraireAbsence();
		this.idServiceAds = fichePoste.getIdServiceAds();

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
	 * Getter de l'attribut idStatutFp.
	 */
	public Integer getIdStatutFp() {
		return idStatutFp;
	}

	/**
	 * Setter de l'attribut idStatutFp.
	 */
	public void setIdStatutFp(Integer newIdStatutFp) {
		idStatutFp = newIdStatutFp;
	}

	/**
	 * Getter de l'attribut idResponsable.
	 */
	public Integer getIdResponsable() {
		return idResponsable;
	}

	/**
	 * Setter de l'attribut idResponsable.
	 */
	public void setIdResponsable(Integer newIdResponsable) {
		idResponsable = newIdResponsable;
	}

	/**
	 * Getter de l'attribut idRemplacement.
	 */
	public Integer getIdRemplacement() {
		return idRemplacement;
	}

	/**
	 * Setter de l'attribut idRemplacement.
	 */
	public void setIdRemplacement(Integer newIdRemplacement) {
		idRemplacement = newIdRemplacement;
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
	 * Getter de l'attribut dateDebutValiditeFp.
	 */
	public Date getDateDebutValiditeFp() {
		return dateDebutValiditeFp;
	}

	/**
	 * Setter de l'attribut dateDebutValiditeFp.
	 */
	public void setDateDebutValiditeFp(Date newDateDebutValiditeFp) {
		dateDebutValiditeFp = newDateDebutValiditeFp;
	}

	/**
	 * Getter de l'attribut opi.
	 */
	public String getOpi() {
		return opi;
	}

	/**
	 * Setter de l'attribut opi.
	 */
	public void setOpi(String newOpi) {
		opi = newOpi;
	}

	/**
	 * Getter de l'attribut nfa.
	 */
	public String getNfa() {
		return nfa;
	}

	/**
	 * Setter de l'attribut nfa.
	 */
	public void setNfa(String newNfa) {
		nfa = newNfa;
	}

	/**
	 * Getter de l'attribut missions.
	 */
	public String getMissions() {
		return missions;
	}

	/**
	 * Setter de l'attribut missions.
	 */
	public void setMissions(String newMissions) {
		missions = newMissions;
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
	 * Getter de l'attribut numFp.
	 */
	public String getNumFp() {
		return numFp;
	}

	/**
	 * Setter de l'attribut numFp.
	 */
	public void setNumFp(String newNumFp) {
		numFp = newNumFp;
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

	public Date getDateDebAppliServ() {
		return dateDebAppliServ;
	}

	public void setDateDebAppliServ(Date newDateDebAppliService) {
		dateDebAppliServ = newDateDebAppliService;
	}

	public Date getDateFinAppliServ() {
		return dateFinAppliServ;
	}

	public void setDateFinAppliServ(Date newDateFinAppliService) {
		dateFinAppliServ = newDateFinAppliService;
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
