package nc.mairie.metier.diplome;

public class FormationAgent {

	private Integer idFormation;
	private Integer idTitreFormation;
	private Integer idCentreFormation;
	private Integer idAgent;
	private Integer dureeFormation;
	private String uniteDuree;
	private Integer anneeFormation;

	public FormationAgent() {
		super();
	}

	public String toString() {
		return "Formation agent : [annee : " + getAnneeFormation() + ", idAgent : " + getIdAgent() + "]";
	}

	public Integer getIdFormation() {
		return idFormation;
	}

	public void setIdFormation(Integer idFormation) {
		this.idFormation = idFormation;
	}

	public Integer getIdTitreFormation() {
		return idTitreFormation;
	}

	public void setIdTitreFormation(Integer idTitreFormation) {
		this.idTitreFormation = idTitreFormation;
	}

	public Integer getIdCentreFormation() {
		return idCentreFormation;
	}

	public void setIdCentreFormation(Integer idCentreFormation) {
		this.idCentreFormation = idCentreFormation;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getDureeFormation() {
		return dureeFormation;
	}

	public void setDureeFormation(Integer dureeFormation) {
		this.dureeFormation = dureeFormation;
	}

	public Integer getAnneeFormation() {
		return anneeFormation;
	}

	public void setAnneeFormation(Integer anneeFormation) {
		this.anneeFormation = anneeFormation;
	}

	public String getUniteDuree() {
		return uniteDuree;
	}

	public void setUniteDuree(String uniteDuree) {
		this.uniteDuree = uniteDuree;
	}

	@Override
	public boolean equals(Object object) {
		return idFormation.toString().equals(((FormationAgent) object).getIdFormation().toString());
	}
}
