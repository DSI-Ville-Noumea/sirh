package nc.mairie.metier.suiviMedical;

import java.util.Date;

public class SuiviMedical {

	private Integer idSuiviMed;
	private Integer idAgent;
	private Integer nomatr;
	private String agent;
	private String statut;
	private String idServi;
	private Date dateDerniereVisite;
	private Date datePrevisionVisite;
	private Integer idMotifVm;
	private Integer nbVisitesRatees;
	private Integer idMedecin;
	private Date dateProchaineVisite;
	private String heureProchaineVisite;
	private String etat;
	private Integer mois;
	private Integer annee;
	private Integer relance;
	private Integer idServiceAds;
	private Integer idRecommandationDerniereVisite;
	private String commentaireDerniereViste;

	public String toString() {
		return "SUIVI MEDICAL : [Nomatr : " + getNomatr() + ", Statut : " + getStatut() + "]";
	}

	public SuiviMedical() {
		super();
	}

	public SuiviMedical(Integer idSuiviMed, Integer idAgent, Integer nomatr, String agent, String statut,
			Date dateDerniereVisite, Date datePrevisionVisite, Integer idMotifVM, Integer nbVisitesRatees,
			Integer idMedecin, Date dateProchaineVisite, String heureProchaineVisite, String etat,
			Integer idServiceADS, String idServi,Integer idRecommandationDerniereVisite,String commentaireDerniereViste) {
		super();
		this.idSuiviMed = idSuiviMed;
		this.idAgent = idAgent;
		this.nomatr = nomatr;
		this.agent = agent;
		this.statut = statut;
		this.dateDerniereVisite = dateDerniereVisite;
		this.datePrevisionVisite = datePrevisionVisite;
		this.idMotifVm = idMotifVM;
		this.nbVisitesRatees = nbVisitesRatees;
		this.idMedecin = idMedecin;
		this.dateProchaineVisite = dateProchaineVisite;
		this.heureProchaineVisite = heureProchaineVisite;
		this.etat = etat;
		this.idServiceAds = idServiceADS;
		this.idServi = idServi;
		this.idRecommandationDerniereVisite = idRecommandationDerniereVisite;
		this.commentaireDerniereViste = commentaireDerniereViste;
	}

	public Integer getIdSuiviMed() {
		return idSuiviMed;
	}

	public void setIdSuiviMed(Integer idSuiviMed) {
		this.idSuiviMed = idSuiviMed;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Date getDateDerniereVisite() {
		return dateDerniereVisite;
	}

	public void setDateDerniereVisite(Date dateDerniereVisite) {
		this.dateDerniereVisite = dateDerniereVisite;
	}

	public Date getDatePrevisionVisite() {
		return datePrevisionVisite;
	}

	public void setDatePrevisionVisite(Date datePrevisionVisite) {
		this.datePrevisionVisite = datePrevisionVisite;
	}

	public Integer getIdMotifVm() {
		return idMotifVm;
	}

	public void setIdMotifVm(Integer idMotifVM) {
		this.idMotifVm = idMotifVM;
	}

	public Integer getNbVisitesRatees() {
		return nbVisitesRatees;
	}

	public void setNbVisitesRatees(Integer nbVisitesRatees) {
		this.nbVisitesRatees = nbVisitesRatees;
	}

	public Integer getIdMedecin() {
		return idMedecin;
	}

	public void setIdMedecin(Integer idMedecin) {
		this.idMedecin = idMedecin;
	}

	public Date getDateProchaineVisite() {
		return dateProchaineVisite;
	}

	public void setDateProchaineVisite(Date dateProchaineVisite) {
		this.dateProchaineVisite = dateProchaineVisite;
	}

	public String getHeureProchaineVisite() {
		return heureProchaineVisite;
	}

	public void setHeureProchaineVisite(String heureProchaineVisite) {
		this.heureProchaineVisite = heureProchaineVisite;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	public Integer getMois() {
		return mois;
	}

	public void setMois(Integer mois) {
		this.mois = mois;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Integer getRelance() {
		return relance;
	}

	public void setRelance(Integer relance) {
		this.relance = relance;
	}

	@Override
	public boolean equals(Object object) {
		return idSuiviMed.toString().equals(((SuiviMedical) object).getIdSuiviMed().toString());
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

	public Integer getIdRecommandationDerniereVisite() {
		return idRecommandationDerniereVisite;
	}

	public void setIdRecommandationDerniereVisite(
			Integer idRecommandationDerniereVisite) {
		this.idRecommandationDerniereVisite = idRecommandationDerniereVisite;
	}

	public String getCommentaireDerniereViste() {
		return commentaireDerniereViste;
	}

	public void setCommentaireDerniereViste(String commentaireDerniereViste) {
		this.commentaireDerniereViste = commentaireDerniereViste;
	}
}
