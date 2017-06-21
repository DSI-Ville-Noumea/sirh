package nc.mairie.metier.specificites;

public class PrimePointageFP {

	private Integer numRubrique;
	private Integer idFichePoste;

	public PrimePointageFP() {
		super();
	}

	public String toString() {
		return "PrimePointage : [rubr : " + getNumRubrique() + ", idFP : " + getIdFichePoste() + "]";
	}

	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	public void setIdFichePoste(Integer idFichePoste) {
		this.idFichePoste = idFichePoste;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}

	@Override
	public boolean equals(Object object) {
		return numRubrique.toString().equals(((PrimePointageFP) object).getNumRubrique().toString())
				&& idFichePoste == null ? true : idFichePoste.toString().equals(
				((PrimePointageFP) object).getIdFichePoste().toString());
	}

}
