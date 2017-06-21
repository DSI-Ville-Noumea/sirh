package nc.mairie.enums;

public enum EnumStatutFichePoste {
	EN_CREATION("1", "En création"), VALIDEE("2", "Validée"), INACTIVE("4", "Inactive"), TRANSITOIRE("5", "Transitoire"), GELEE(
			"6", "Gelée");

	/** L'attribut qui contient le libelle long associe a l'enum */
	private final String libLong;
	private final String id;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumStatutFichePoste(String id, String libLong) {
		this.libLong = libLong;
		this.id = id;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getLibLong() {
		return this.libLong;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getId() {
		return this.id;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String fichePoste[] = new String[EnumStatutFichePoste.values().length];
		int i = 0;
		for (EnumStatutFichePoste elt : EnumStatutFichePoste.values()) {
			fichePoste[i++] = elt.getLibLong();
		}
		return fichePoste;
	}
}
