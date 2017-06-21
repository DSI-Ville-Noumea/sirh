package nc.mairie.enums;

public enum EnumTypeContact {
	TEL(1, "Tel"), FAX(2, "Fax"), MOBILE(3, "Mobile"), MOBILE_PRO(5, "Mobile pro"), EMAIL(4, "Email"), LIGNE_DIRECTE(6,
			"Ligne Directe");

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** L'attribut qui contient le code associe a l'enum */
	private final Integer code;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumTypeContact(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit le code de l'enum */
	public Integer getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String typesContrat[] = new String[EnumTypeContact.values().length];
		int i = 0;
		for (EnumTypeContact elt : EnumTypeContact.values()) {
			typesContrat[i++] = elt.getValue();
		}
		return typesContrat;
	}
}
