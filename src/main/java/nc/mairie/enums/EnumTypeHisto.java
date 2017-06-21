package nc.mairie.enums;

public enum EnumTypeHisto {
	CREATION("Création"), SUPPRESSION("Suppression"), MODIFICATION("Modification"), GENERATION_APTITUDE_VM("Impr certifApti");

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumTypeHisto(String value) {
		this.value = value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String typeHisto[] = new String[EnumTypeHisto.values().length];
		int i = 0;
		for (EnumTypeHisto elt : EnumTypeHisto.values()) {
			typeHisto[i++] = elt.getValue();
		}
		return typeHisto;
	}
}
