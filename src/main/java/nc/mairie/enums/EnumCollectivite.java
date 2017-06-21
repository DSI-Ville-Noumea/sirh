package nc.mairie.enums;

public enum EnumCollectivite {
	CCAS("50", "CCAS", "Centre communal d'action sociale"), CDE("40", "CDE", "Caisse des écoles"), MAIRIE("90", "VDN", "Mairie");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient le libelle court associe a l'enum */
	private final String libCourt;

	/** L'attribut qui contient le libelle long associe a l'enum */
	private final String libLong;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumCollectivite(String code, String libCourt, String libLong) {
		this.code = code;
		this.libCourt = libCourt;
		this.libLong = libLong;
	}

	/** La methode accesseur qui renvoit le code de l'enum */
	public String getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getLibCourt() {
		return this.libCourt;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getLibLong() {
		return this.libLong;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String collectivite[] = new String[EnumCollectivite.values().length];
		int i = 0;
		for (EnumCollectivite elt : EnumCollectivite.values()) {
			collectivite[i++] = elt.getLibLong();
		}
		return collectivite;
	}
}
