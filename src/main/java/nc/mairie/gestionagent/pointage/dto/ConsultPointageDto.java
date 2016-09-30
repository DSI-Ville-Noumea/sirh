package nc.mairie.gestionagent.pointage.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class ConsultPointageDto {

	private Integer idPointage;
	private AgentDto agent;
	private String typePointage;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date date;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date debut;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date fin;
	private String quantite;
	private String motif;
	private String commentaire;
	private Integer idRefEtat;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateSaisie;
	private AgentDto operateur;
	private boolean heuresSupRecuperees;
	private boolean heuresSupRappelEnService;

	public ConsultPointageDto() {

	}

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public AgentDto getAgent() {
		return agent;
	}

	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}

	public String getTypePointage() {
		return typePointage;
	}

	public void setTypePointage(String typePointage) {
		this.typePointage = typePointage;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDebut() {
		return debut;
	}

	public void setDebut(Date debut) {
		this.debut = debut;
	}

	public Date getFin() {
		return fin;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
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

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}

	public AgentDto getOperateur() {
		return operateur;
	}

	public void setOperateur(AgentDto operateur) {
		this.operateur = operateur;
	}

	public boolean isHeuresSupRecuperees() {
		return heuresSupRecuperees;
	}

	public void setHeuresSupRecuperees(boolean heuresSupRecuperees) {
		this.heuresSupRecuperees = heuresSupRecuperees;
	}

	public boolean isHeuresSupRappelEnService() {
		return heuresSupRappelEnService;
	}

	public void setHeuresSupRappelEnService(boolean heuresSupRappelEnService) {
		this.heuresSupRappelEnService = heuresSupRappelEnService;
	}
	
}
