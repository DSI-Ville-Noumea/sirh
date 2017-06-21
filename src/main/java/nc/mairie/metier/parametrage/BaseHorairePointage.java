package nc.mairie.metier.parametrage;

/**
 * Objet metier MotifAffectation
 */
public class BaseHorairePointage {

	public Integer idBaseHorairePointage;
	public String codeBaseHorairePointage;
	public String libelleBaseHorairePointage;
	public String descriptionBaseHorairePointage;
	public Double heureLundi;
	public Double heureMardi;
	public Double heureMercredi;
	public Double heureJeudi;
	public Double heureVendredi;
	public Double heureSamedi;
	public Double heureDimanche;
	public Double baseLegale;
	public Double baseCalculee;

	public BaseHorairePointage() {
		super();
	}

	public Integer getIdBaseHorairePointage() {
		return idBaseHorairePointage;
	}

	public void setIdBaseHorairePointage(Integer idBaseHorairePointage) {
		this.idBaseHorairePointage = idBaseHorairePointage;
	}

	public String getCodeBaseHorairePointage() {
		return codeBaseHorairePointage;
	}

	public void setCodeBaseHorairePointage(String codeBaseHorairePointage) {
		this.codeBaseHorairePointage = codeBaseHorairePointage;
	}

	public String getLibelleBaseHorairePointage() {
		return libelleBaseHorairePointage;
	}

	public void setLibelleBaseHorairePointage(String libelleBaseHorairePointage) {
		this.libelleBaseHorairePointage = libelleBaseHorairePointage;
	}

	public String getDescriptionBaseHorairePointage() {
		return descriptionBaseHorairePointage;
	}

	public void setDescriptionBaseHorairePointage(String descriptionBaseHorairePointage) {
		this.descriptionBaseHorairePointage = descriptionBaseHorairePointage;
	}

	public Double getHeureLundi() {
		return heureLundi;
	}

	public void setHeureLundi(Double heureLundi) {
		this.heureLundi = heureLundi;
	}

	public Double getHeureMardi() {
		return heureMardi;
	}

	public void setHeureMardi(Double heureMardi) {
		this.heureMardi = heureMardi;
	}

	public Double getHeureMercredi() {
		return heureMercredi;
	}

	public void setHeureMercredi(Double heureMercredi) {
		this.heureMercredi = heureMercredi;
	}

	public Double getHeureJeudi() {
		return heureJeudi;
	}

	public void setHeureJeudi(Double heureJeudi) {
		this.heureJeudi = heureJeudi;
	}

	public Double getHeureVendredi() {
		return heureVendredi;
	}

	public void setHeureVendredi(Double heureVendredi) {
		this.heureVendredi = heureVendredi;
	}

	public Double getHeureSamedi() {
		return heureSamedi;
	}

	public void setHeureSamedi(Double heureSamedi) {
		this.heureSamedi = heureSamedi;
	}

	public Double getHeureDimanche() {
		return heureDimanche;
	}

	public void setHeureDimanche(Double heureDimanche) {
		this.heureDimanche = heureDimanche;
	}

	public Double getBaseLegale() {
		return baseLegale;
	}

	public void setBaseLegale(Double baseLegale) {
		this.baseLegale = baseLegale;
	}

	public Double getBaseCalculee() {
		return baseCalculee;
	}

	public void setBaseCalculee(Double baseCalculee) {
		this.baseCalculee = baseCalculee;
	}

	@Override
	public boolean equals(Object obj) {
		return idBaseHorairePointage.toString().equals(
				((BaseHorairePointage) obj).getIdBaseHorairePointage().toString());
	}
}
