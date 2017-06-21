package nc.mairie.metier.poste;

/**
 * Objet metier ActiviteFP
 */
public class ActiviteFP {

	public Integer idActivite;
	public Integer idFichePoste;
	public boolean activitePrincipale;

	/**
	 * Constructeur ActiviteFP.
	 */
	public ActiviteFP(Integer idFichePoste, Integer idActivite, boolean activitePrincipale) {
		super();
		this.idActivite = idActivite;
		this.idFichePoste = idFichePoste;
		this.activitePrincipale = activitePrincipale;
	}

	/**
	 * Constructeur ActiviteFP.
	 */
	public ActiviteFP() {
		super();
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
	 * Getter de l'attribut idFichePoste.
	 */
	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	/**
	 * Setter de l'attribut idFichePoste.
	 */
	public void setIdFichePoste(Integer newIdFichePoste) {
		idFichePoste = newIdFichePoste;
	}

	/**
	 * Getter de l'attribut activitePrincipale.
	 */
	public boolean isActivitePrincipale() {
		return activitePrincipale;
	}

	/**
	 * Setter de l'attribut activitePrincipale.
	 */
	public void setActivitePrincipale(boolean newActivitePrincipale) {
		activitePrincipale = newActivitePrincipale;
	}

	@Override
	public boolean equals(Object object) {
		return idActivite.toString().equals(((ActiviteFP) object).getIdActivite().toString())
				&& idFichePoste.toString().equals(((ActiviteFP) object).getIdFichePoste().toString());
	}
}
