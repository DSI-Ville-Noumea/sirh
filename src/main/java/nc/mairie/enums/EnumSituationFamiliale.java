package nc.mairie.enums;

public enum EnumSituationFamiliale {
	CELIBATAIRE("C", "Célibataire"), MARIE("M", "Marié(e)"), DIVORCE("D", "Divorcé(e)"), SEPARE("S", "Séparé(e)"), VEUF("V", "Veuf(ve)"), UNION_LIBRE("U", "Union libre"), PACS("P", "PACS");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumSituationFamiliale(String code, String value) {
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
		String situations[] = new String[EnumSituationFamiliale.values().length];
		int i = 0;
		for (EnumSituationFamiliale elt : EnumSituationFamiliale.values()) {
			situations[i++] = elt.getValue();
		}
		return situations;
	}
}
