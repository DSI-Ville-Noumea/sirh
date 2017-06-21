package nc.mairie.enums;

public enum EnumCivilite {
	M("0", "M."), MME("1", "Mme"), MLLE("2", "Mlle");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumCivilite(String code, String value) {
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
		String civilites[] = new String[EnumCivilite.values().length];
		int i = 0;
		for (EnumCivilite elt : EnumCivilite.values()) {
			civilites[i++] = elt.getValue();
		}
		return civilites;
	}
}
