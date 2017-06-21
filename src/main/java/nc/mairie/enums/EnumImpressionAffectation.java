package nc.mairie.enums;

import nc.mairie.metier.Const;

public enum EnumImpressionAffectation {
	NS("0", Const.CHAINE_VIDE), NS_INTERNE("Interne", "Note de service interne"), NS_AFFECTATION("Affectation",
			"Note de service affectation"), NS_NOMINATION("Nomination", "Note de service nomination chef de section");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumImpressionAffectation(String code, String value) {
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
		String impressionsAff[] = new String[EnumImpressionAffectation.values().length];
		int i = 0;
		for (EnumImpressionAffectation elt : EnumImpressionAffectation.values()) {
			impressionsAff[i++] = elt.getValue();
		}
		return impressionsAff;
	}

	/** La methode qui renvoit la valeur de l'enum */
	public static String getCodeImpressionAffectation(int cle) {
		String impressionsAff[] = new String[EnumImpressionAffectation.values().length];
		int i = 0;
		for (EnumImpressionAffectation elt : EnumImpressionAffectation.values()) {
			impressionsAff[i++] = elt.getCode();
		}
		return impressionsAff[cle];
	}
}
