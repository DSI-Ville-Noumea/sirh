package nc.mairie.metier.specificites;

public class PrimePointageAff {

	private Integer numRubrique;
	private Integer idAffectation;

	public PrimePointageAff() {
		super();
	}

	public PrimePointageAff(PrimePointageFP p, Integer idAff) {
		super();
		numRubrique = p.getNumRubrique();
		if (idAff != null)
			idAffectation = idAff;
	}

	public String toString() {
		return "PrimePointage : [rubr : " + getNumRubrique() + ", idAff : " + getIdAffectation() + "]";
	}

	public Integer getIdAffectation() {
		return idAffectation;
	}

	public void setIdAffectation(Integer idAffectation) {
		this.idAffectation = idAffectation;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}

	@Override
	public boolean equals(Object object) {
		return numRubrique.toString().equals(((PrimePointageAff) object).getNumRubrique().toString())
				&& idAffectation.toString().equals(((PrimePointageAff) object).getIdAffectation().toString());
	}

}
