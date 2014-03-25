package nc.mairie.gestionagent.pointage.dto;

import java.util.Date;

public abstract class PointageDto {

	private Integer idPointage;
	private Date heureDebut;
	private Date heureFin;
	private String motif;
	private String commentaire;
	private Integer idRefEtat;
	private boolean aSupprimer;

	public PointageDto() {

	}

	public PointageDto(PointageDto pointageDto) {
		this.idPointage = pointageDto.idPointage;
		this.heureDebut = pointageDto.heureDebut;
		this.heureFin = pointageDto.heureFin;
		this.motif = pointageDto.motif;
		this.commentaire = pointageDto.commentaire;
		this.idRefEtat = pointageDto.idRefEtat;
		this.aSupprimer = false;
	}

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public Date getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(Date heureDebut) {
		this.heureDebut = heureDebut;
	}

	public Date getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(Date heureFin) {
		this.heureFin = heureFin;
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

	public boolean isaSupprimer() {
		return aSupprimer;
	}

	public void setaSupprimer(boolean aSupprimer) {
		this.aSupprimer = aSupprimer;
	}
}
