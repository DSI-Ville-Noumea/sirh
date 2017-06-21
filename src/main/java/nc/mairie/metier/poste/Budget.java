package nc.mairie.metier.poste;

/**
 * Objet metier Budget
 */
public class Budget {

	public Integer idBudget;
	public String libBudget;

	/**
	 * Constructeur Budget.
	 */
	public Budget() {
		super();
	}

	/**
	 * Getter de l'attribut idBudget.
	 */
	public Integer getIdBudget() {
		return idBudget;
	}

	/**
	 * Setter de l'attribut idBudget.
	 */
	public void setIdBudget(Integer newIdBudget) {
		idBudget = newIdBudget;
	}

	/**
	 * Getter de l'attribut libBudget.
	 */
	public String getLibBudget() {
		return libBudget;
	}

	/**
	 * Setter de l'attribut libBudget.
	 */
	public void setLibBudget(String newLibBudget) {
		libBudget = newLibBudget;
	}

	@Override
	public boolean equals(Object object) {
		return idBudget.toString().equals(((Budget) object).getIdBudget().toString());
	}
}
