package nc.mairie.enums;

public enum EnumEtatAvancement {
	TRAVAIL("T"), SGC("C"), SEF("F"), ARRETE("A"), VALIDE("V"), AFFECTE("E");

	/** L'attribut qui contient la valeur associee a l'enum */
	private final String value;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumEtatAvancement(String value) {
		this.value = value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String et[] = new String[EnumEtatAvancement.values().length];
		int i = 0;
		for (EnumEtatAvancement elt : EnumEtatAvancement.values()) {
			et[i++] = elt.getValue();
		}
		return et;
	}
}
