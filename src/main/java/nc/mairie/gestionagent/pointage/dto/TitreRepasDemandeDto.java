package nc.mairie.gestionagent.pointage.dto;

import java.util.Date;

import nc.mairie.gestionagent.dto.AgentDto;

public class TitreRepasDemandeDto {

	private Integer idTrDemande;
	private AgentDto agent;
	private Integer idRefEtat;
	private Date dateSaisie;
	private Date dateMonth;
	private Boolean commande;
	private String commentaire;
	private AgentDto operateur;

	public TitreRepasDemandeDto() {
		super();
	}

	public Integer getIdTrDemande() {
		return idTrDemande;
	}

	public void setIdTrDemande(Integer idTrDemande) {
		this.idTrDemande = idTrDemande;
	}

	public Date getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}

	public Boolean getCommande() {
		return commande;
	}

	public void setCommande(Boolean commande) {
		this.commande = commande;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public AgentDto getAgent() {
		return agent;
	}

	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}

	public AgentDto getOperateur() {
		return operateur;
	}

	public void setOperateur(AgentDto operateur) {
		this.operateur = operateur;
	}

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}

}
