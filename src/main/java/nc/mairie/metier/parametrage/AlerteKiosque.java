package nc.mairie.metier.parametrage;

import java.util.Date;

public class AlerteKiosque {

	public Integer idAlerteKiosque;
	public String texteAlerteKiosque;
	public String titre;
	public Date dateDebut;
	public Date dateFin;
	public boolean agent;
	public boolean approbateurABS;
	public boolean approbateurPTG;
	public boolean operateurABS;
	public boolean operateurPTG;
	public boolean viseurABS;

	public AlerteKiosque() {
		super();
	}

	public String toString() {
		return "AlerteKiosque : [texteAlerteKiosque : " + getTexteAlerteKiosque() + "]";
	}

	public Integer getIdAlerteKiosque() {
		return idAlerteKiosque;
	}

	public void setIdAlerteKiosque(Integer idAlerteKiosque) {
		this.idAlerteKiosque = idAlerteKiosque;
	}

	public String getTexteAlerteKiosque() {
		return texteAlerteKiosque;
	}

	public void setTexteAlerteKiosque(String texteAlerteKiosque) {
		this.texteAlerteKiosque = texteAlerteKiosque;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public boolean isAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}

	public boolean isApprobateurABS() {
		return approbateurABS;
	}

	public void setApprobateurABS(boolean approbateurABS) {
		this.approbateurABS = approbateurABS;
	}

	public boolean isApprobateurPTG() {
		return approbateurPTG;
	}

	public void setApprobateurPTG(boolean approbateurPTG) {
		this.approbateurPTG = approbateurPTG;
	}

	public boolean isOperateurABS() {
		return operateurABS;
	}

	public void setOperateurABS(boolean operateurABS) {
		this.operateurABS = operateurABS;
	}

	public boolean isOperateurPTG() {
		return operateurPTG;
	}

	public void setOperateurPTG(boolean operateurPTG) {
		this.operateurPTG = operateurPTG;
	}

	public boolean isViseurABS() {
		return viseurABS;
	}

	public void setViseurABS(boolean viseurABS) {
		this.viseurABS = viseurABS;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}
}
