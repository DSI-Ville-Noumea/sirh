package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.IJSONDeserialize;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import flexjson.JSONDeserializer;

public class DemandeDto implements IJSONDeserialize<DemandeDto> {

	private AgentWithServiceDto agentWithServiceDto;

	private Integer idDemande;
	private Integer idTypeDemande;
	private String libelleTypeDemande;
	private RefGroupeAbsenceDto groupeAbsence;
	private Date dateDemande;
	private Date dateDebut;
	private boolean isDateDebutAM;
	private boolean isDateDebutPM;
	private Date dateFin;
	private boolean isDateFinAM;
	private boolean isDateFinPM;
	private Date dateReprise;
	private Double duree;
	private boolean forceSaisieManuelleDuree;
	private boolean isSamediOffert;

	private Integer idRefEtat;
	private RefEtatDto etatDto;
	private Date dateSaisie;
	private String motif;
	private AgentWithServiceDto agentEtat;

	// permet d'afficher ou non les icones correspondants
	private boolean isAffichageBoutonModifier;
	private boolean isAffichageBoutonSupprimer;
	private boolean isAffichageBoutonImprimer;
	private boolean isAffichageBoutonAnnuler;
	private boolean isAffichageValidation;
	private boolean isAffichageEnAttente;
	private boolean isAffichageBoutonDupliquer;
	// permet de viser ou approuver
	private boolean isModifierVisa;
	private boolean isModifierApprobation;
	// valeur du visa et approbation de la demande
	private Boolean isValeurVisa = null;
	private Boolean isValeurApprobation = null;
	// depasement de droits
	private boolean isDepassementCompteur;
	private boolean isDepassementMultiple;

	private OrganisationSyndicaleDto organisationSyndicale;

	private String commentaire;

	private RefTypeSaisiDto typeSaisi;
	private RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuel;

	// Pour les soldes des demandes
	private Double totalJoursNew;
	private Double totalJoursOld;
	private Double totalJoursAnneeN1New;
	private Double totalJoursAnneeN1Old;
	private Integer totalMinutesNew;
	private Integer totalMinutesOld;
	private Integer totalMinutesAnneeN1New;
	private Integer totalMinutesAnneeN1Old;
	
	// #15586 restitution massive
	private boolean affichageBoutonHistorique;

	public DemandeDto() {
	}

	public AgentWithServiceDto getAgentWithServiceDto() {
		return agentWithServiceDto;
	}

	public void setAgentWithServiceDto(AgentWithServiceDto agentWithServiceDto) {
		this.agentWithServiceDto = agentWithServiceDto;
	}

	public Integer getIdDemande() {
		return idDemande;
	}

	public void setIdDemande(Integer idDemande) {
		this.idDemande = idDemande;
	}

	public Integer getIdTypeDemande() {
		return idTypeDemande;
	}

	public void setIdTypeDemande(Integer idTypeDemande) {
		this.idTypeDemande = idTypeDemande;
	}

	public Date getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(Date dateDemande) {
		this.dateDemande = dateDemande;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public boolean isDateDebutAM() {
		return isDateDebutAM;
	}

	public void setDateDebutAM(boolean isDateDebutAM) {
		this.isDateDebutAM = isDateDebutAM;
	}

	public boolean isDateDebutPM() {
		return isDateDebutPM;
	}

	public void setDateDebutPM(boolean isDateDebutPM) {
		this.isDateDebutPM = isDateDebutPM;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public boolean isDateFinAM() {
		return isDateFinAM;
	}

	public void setDateFinAM(boolean isDateFinAM) {
		this.isDateFinAM = isDateFinAM;
	}

	public boolean isDateFinPM() {
		return isDateFinPM;
	}

	public void setDateFinPM(boolean isDateFinPM) {
		this.isDateFinPM = isDateFinPM;
	}

	public Double getDuree() {
		return duree;
	}

	public void setDuree(Double duree) {
		this.duree = duree;
	}

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public boolean isAffichageBoutonModifier() {
		return isAffichageBoutonModifier;
	}

	public void setAffichageBoutonModifier(boolean isAffichageBoutonModifier) {
		this.isAffichageBoutonModifier = isAffichageBoutonModifier;
	}

	public boolean isAffichageBoutonSupprimer() {
		return isAffichageBoutonSupprimer;
	}

	public void setAffichageBoutonSupprimer(boolean isAffichageBoutonSupprimer) {
		this.isAffichageBoutonSupprimer = isAffichageBoutonSupprimer;
	}

	public boolean isAffichageBoutonImprimer() {
		return isAffichageBoutonImprimer;
	}

	public void setAffichageBoutonImprimer(boolean isAffichageBoutonImprimer) {
		this.isAffichageBoutonImprimer = isAffichageBoutonImprimer;
	}

	public boolean isAffichageBoutonAnnuler() {
		return isAffichageBoutonAnnuler;
	}

	public void setAffichageBoutonAnnuler(boolean isAffichageBoutonAnnuler) {
		this.isAffichageBoutonAnnuler = isAffichageBoutonAnnuler;
	}

	public boolean isModifierVisa() {
		return isModifierVisa;
	}

	public void setModifierVisa(boolean isModifierVisa) {
		this.isModifierVisa = isModifierVisa;
	}

	public boolean isModifierApprobation() {
		return isModifierApprobation;
	}

	public void setModifierApprobation(boolean isModifierApprobation) {
		this.isModifierApprobation = isModifierApprobation;
	}

	public Boolean getIsValeurVisa() {
		return isValeurVisa;
	}

	public void setIsValeurVisa(Boolean isValeurVisa) {
		this.isValeurVisa = isValeurVisa;
	}

	public Boolean getIsValeurApprobation() {
		return isValeurApprobation;
	}

	public void setIsValeurApprobation(Boolean isValeurApprobation) {
		this.isValeurApprobation = isValeurApprobation;
	}

	@Override
	public DemandeDto deserializeFromJSON(String json) {
		return new JSONDeserializer<DemandeDto>().deserializeInto(json, this);
	}

	public boolean isDepassementCompteur() {
		return isDepassementCompteur;
	}

	public void setDepassementCompteur(boolean isDepassementCompteur) {
		this.isDepassementCompteur = isDepassementCompteur;
	}

	public OrganisationSyndicaleDto getOrganisationSyndicale() {
		return organisationSyndicale;
	}

	public void setOrganisationSyndicale(OrganisationSyndicaleDto organisationSyndicale) {
		this.organisationSyndicale = organisationSyndicale;
	}

	public RefGroupeAbsenceDto getGroupeAbsence() {
		return groupeAbsence;
	}

	public void setGroupeAbsence(RefGroupeAbsenceDto groupeAbsence) {
		this.groupeAbsence = groupeAbsence;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public RefTypeSaisiDto getTypeSaisi() {
		return typeSaisi;
	}

	public void setTypeSaisi(RefTypeSaisiDto typeSaisi) {
		this.typeSaisi = typeSaisi;
	}

	public String getLibelleTypeDemande() {
		return libelleTypeDemande;
	}

	public void setLibelleTypeDemande(String libelleTypeDemande) {
		this.libelleTypeDemande = libelleTypeDemande;
	}

	public Date getDateReprise() {
		return dateReprise;
	}

	public void setDateReprise(Date dateReprise) {
		this.dateReprise = dateReprise;
	}

	public RefTypeSaisiCongeAnnuelDto getTypeSaisiCongeAnnuel() {
		return typeSaisiCongeAnnuel;
	}

	public void setTypeSaisiCongeAnnuel(RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuel) {
		this.typeSaisiCongeAnnuel = typeSaisiCongeAnnuel;
	}

	public boolean isDepassementMultiple() {
		return isDepassementMultiple;
	}

	public void setDepassementMultiple(boolean isDepassementMultiple) {
		this.isDepassementMultiple = isDepassementMultiple;
	}

	public boolean isSamediOffert() {
		return isSamediOffert;
	}

	public void setSamediOffert(boolean isSamediOffert) {
		this.isSamediOffert = isSamediOffert;
	}

	public boolean isAffichageBoutonHistorique() {
		return affichageBoutonHistorique;
	}

	public void setAffichageBoutonHistorique(boolean affichageBoutonHistorique) {
		this.affichageBoutonHistorique = affichageBoutonHistorique;
	}

	public RefEtatDto getEtatDto() {
		return etatDto;
	}

	public void setEtatDto(RefEtatDto etatDto) {
		this.etatDto = etatDto;
	}

	public boolean isAffichageBoutonDupliquer() {
		return isAffichageBoutonDupliquer;
	}

	public void setAffichageBoutonDupliquer(boolean isAffichageBoutonDupliquer) {
		this.isAffichageBoutonDupliquer = isAffichageBoutonDupliquer;
	}

	public boolean isAffichageValidation() {
		return isAffichageValidation;
	}

	public void setAffichageValidation(boolean isAffichageValidation) {
		this.isAffichageValidation = isAffichageValidation;
	}

	public boolean isAffichageEnAttente() {
		return isAffichageEnAttente;
	}

	public void setAffichageEnAttente(boolean isAffichageEnAttente) {
		this.isAffichageEnAttente = isAffichageEnAttente;
	}

	public AgentWithServiceDto getAgentEtat() {
		return agentEtat;
	}

	public void setAgentEtat(AgentWithServiceDto agentEtat) {
		this.agentEtat = agentEtat;
	}

	public Double getTotalJoursNew() {
		return totalJoursNew;
	}

	public void setTotalJoursNew(Double totalJoursNew) {
		this.totalJoursNew = totalJoursNew;
	}

	public Double getTotalJoursOld() {
		return totalJoursOld;
	}

	public void setTotalJoursOld(Double totalJoursOld) {
		this.totalJoursOld = totalJoursOld;
	}

	public Double getTotalJoursAnneeN1New() {
		return totalJoursAnneeN1New;
	}

	public void setTotalJoursAnneeN1New(Double totalJoursAnneeN1New) {
		this.totalJoursAnneeN1New = totalJoursAnneeN1New;
	}

	public Double getTotalJoursAnneeN1Old() {
		return totalJoursAnneeN1Old;
	}

	public void setTotalJoursAnneeN1Old(Double totalJoursAnneeN1Old) {
		this.totalJoursAnneeN1Old = totalJoursAnneeN1Old;
	}

	public Integer getTotalMinutesNew() {
		return totalMinutesNew;
	}

	public void setTotalMinutesNew(Integer totalMinutesNew) {
		this.totalMinutesNew = totalMinutesNew;
	}

	public Integer getTotalMinutesOld() {
		return totalMinutesOld;
	}

	public void setTotalMinutesOld(Integer totalMinutesOld) {
		this.totalMinutesOld = totalMinutesOld;
	}

	public Integer getTotalMinutesAnneeN1New() {
		return totalMinutesAnneeN1New;
	}

	public void setTotalMinutesAnneeN1New(Integer totalMinutesAnneeN1New) {
		this.totalMinutesAnneeN1New = totalMinutesAnneeN1New;
	}

	public Integer getTotalMinutesAnneeN1Old() {
		return totalMinutesAnneeN1Old;
	}

	public void setTotalMinutesAnneeN1Old(Integer totalMinutesAnneeN1Old) {
		this.totalMinutesAnneeN1Old = totalMinutesAnneeN1Old;
	}

	public boolean isForceSaisieManuelleDuree() {
		return forceSaisieManuelleDuree;
	}

	public void setForceSaisieManuelleDuree(boolean forceSaisieManuelleDuree) {
		this.forceSaisieManuelleDuree = forceSaisieManuelleDuree;
	}
	
}
