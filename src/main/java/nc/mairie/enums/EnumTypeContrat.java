package nc.mairie.enums;

public enum EnumTypeContrat {
	CDD(1, "CDD"), CDI(2, "CDI");

	/** L'attribut qui contient le code associe a l'enum */
	private final Integer code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumTypeContrat(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	/** La methode accesseur qui renvoit le code de l'enum */
	public Integer getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String typesContrat[] = new String[EnumTypeContrat.values().length];
		int i = 0;
		for (EnumTypeContrat elt : EnumTypeContrat.values()) {
			typesContrat[i++] = elt.getValue();
		}
		return typesContrat;
	}
}
