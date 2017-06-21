package nc.mairie.metier.poste;

/**
 * Objet metier ActiviteFE
 */
public class ActiviteFE {

	public Integer idActivite;
	public Integer idFicheEmploi;

	/**
	 * Constructeur ActiviteFE.
	 */
	public ActiviteFE() {
		super();
	}

	/**
	 * Constructeur ActiviteFE.
	 */
	public ActiviteFE(Integer newIdFicheEmploi, Integer newIdActivite) {
		super();
		setIdFicheEmploi(newIdFicheEmploi);
		setIdActivite(newIdActivite);
	}

	/**
	 * Getter de l'attribut idActivite.
	 */
	public Integer getIdActivite() {
		return idActivite;
	}

	/**
	 * Setter de l'attribut idActivite.
	 */
	public void setIdActivite(Integer newIdActivite) {
		idActivite = newIdActivite;
	}

	/**
	 * Getter de l'attribut idFicheEmploi.
	 */
	public Integer getIdFicheEmploi() {
		return idFicheEmploi;
	}

	/**
	 * Setter de l'attribut idFicheEmploi.
	 */
	public void setIdFicheEmploi(Integer newIdFicheEmploi) {
		idFicheEmploi = newIdFicheEmploi;
	}

	@Override
	public boolean equals(Object object) {
		return idActivite.toString().equals(((ActiviteFE) object).getIdActivite().toString())
				&& idFicheEmploi.toString().equals(((ActiviteFE) object).getIdFicheEmploi().toString());
	}
}
