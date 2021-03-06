package nc.mairie.enums;

public enum EnumNationalite {
	FRANCAISE("F", "Française"), ETRANGERE("E", "Etrangère");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumNationalite(String code, String value) {
		this.code = code;
		this.value = value;
	}

	/** La methode accesseur qui renvoit le code de l'enum */
	public String getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String nationalites[] = new String[EnumNationalite.values().length];
		int i = 0;
		for (EnumNationalite elt : EnumNationalite.values()) {
			nationalites[i++] = elt.getValue();
		}
		return nationalites;
	}
}
