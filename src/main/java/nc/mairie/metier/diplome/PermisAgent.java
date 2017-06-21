package nc.mairie.metier.diplome;

import java.util.Date;

public class PermisAgent {

	private Integer idPermisAgent;
	private Integer idPermis;
	private Integer idAgent;
	private Integer dureePermis;
	private String uniteDuree;
	private Date dateObtention;

	public PermisAgent() {
		super();
	}

	public String toString() {
		return "Permis agent : [date : " + getDateObtention() + ", idAgent : " + getIdAgent() + "]";
	}

	public Integer getIdPermisAgent() {
		return idPermisAgent;
	}

	public void setIdPermisAgent(Integer idPermisAgent) {
		this.idPermisAgent = idPermisAgent;
	}

	public Integer getIdPermis() {
		return idPermis;
	}

	public void setIdPermis(Integer idPermis) {
		this.idPermis = idPermis;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getDureePermis() {
		return dureePermis;
	}

	public void setDureePermis(Integer dureePermis) {
		this.dureePermis = dureePermis;
	}

	public String getUniteDuree() {
		return uniteDuree;
	}

	public void setUniteDuree(String uniteDuree) {
		this.uniteDuree = uniteDuree;
	}

	public Date getDateObtention() {
		return dateObtention;
	}

	public void setDateObtention(Date dateObtention) {
		this.dateObtention = dateObtention;
	}

	@Override
	public boolean equals(Object object) {
		return idPermisAgent.toString().equals(((PermisAgent) object).getIdPermisAgent().toString());
	}
}
