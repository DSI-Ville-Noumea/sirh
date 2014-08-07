package nc.mairie.gestionagent.absence.dto;

import java.util.Date;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.IJSONDeserialize;
import flexjson.JSONDeserializer;

public class DemandeDto implements IJSONDeserialize<DemandeDto> {

	private AgentWithServiceDto agentWithServiceDto;

	private Integer idDemande;
	private Integer idTypeDemande;
	private RefGroupeAbsenceDto groupeAbsence;
	private Date dateDemande;
	private Date dateDebut;
	private boolean isDateDebutAM;
	private boolean isDateDebutPM;
	private Date dateFin;
	private boolean isDateFinAM;
	private boolean isDateFinPM;
	private Double duree;

	private Integer idRefEtat;
	private Date dateSaisie;
	private String motif;

	// permet d'afficher ou non les icones correspondants
	private boolean isAffichageBoutonModifier;
	private boolean isAffichageBoutonSupprimer;
	private boolean isAffichageBoutonImprimer;
	private boolean isAffichageBoutonAnnuler;
	private boolean isAffichageVisa;
	private boolean isAffichageApprobation;
	// permet de viser ou approuver
	private boolean isModifierVisa;
	private boolean isModifierApprobation;
	// valeur du visa et approbation de la demande
	private Boolean isValeurVisa = null;
	private Boolean isValeurApprobation = null;
	// depasement de droits
	private boolean isDepassementCompteur;

	private OrganisationSyndicaleDto organisationSyndicale;

	private String commentaire;

	private RefTypeSaisiDto typeSaisi;

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

	public boolean isAffichageVisa() {
		return isAffichageVisa;
	}

	public void setAffichageVisa(boolean isAffichageVisa) {
		this.isAffichageVisa = isAffichageVisa;
	}

	public boolean isAffichageApprobation() {
		return isAffichageApprobation;
	}

	public void setAffichageApprobation(boolean isAffichageApprobation) {
		this.isAffichageApprobation = isAffichageApprobation;
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

}
