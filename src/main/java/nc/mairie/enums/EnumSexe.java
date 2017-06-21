package nc.mairie.enums;

public enum EnumSexe {
	MASCULIN("M", "Masculin"), FEMININ("F", "Féminin");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumSexe(String code, String value) {
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
}
