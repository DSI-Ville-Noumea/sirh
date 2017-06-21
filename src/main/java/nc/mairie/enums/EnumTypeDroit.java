package nc.mairie.enums;

public enum EnumTypeDroit {
	CONSULTATION("Consultation"), EDITION("Edition");

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumTypeDroit(String value) {
		this.value = value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String typeRubrique[] = new String[EnumTypeDroit.values().length];
		int i = 0;
		for (EnumTypeDroit elt : EnumTypeDroit.values()) {
			typeRubrique[i++] = elt.getValue();
		}
		return typeRubrique;
	}
}
