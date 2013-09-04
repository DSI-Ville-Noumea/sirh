package nc.mairie.gestionagent.dto;

public class RefPrimeDto {

	private Integer idRefPrime;
	private Integer numRubrique;
	private String libelle;
	private String description;
	private String typeSaisie;
	private boolean calculee;
	private String statut;

	public RefPrimeDto() {
	}

	@Override
	public String toString() {
		return "Prime:" + idRefPrime + "," + numRubrique + "," + libelle + "," + description + "," + typeSaisie + "," + calculee + "," + statut;
	}

	public Integer getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(Integer idRefPrime) {
		this.idRefPrime = idRefPrime;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCalculee() {
		return calculee;
	}

	public void setCalculee(boolean calculee) {
		this.calculee = calculee;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getTypeSaisie() {
		return typeSaisie;
	}

	public void setTypeSaisie(String typeSaisie) {
		this.typeSaisie = typeSaisie;
	}

}
