package nc.mairie.enums;

public enum EnumTypeContrat {
	AUCUN(0, ""), CDD(1, "CDD"), CDI(2, "CDI");

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
	/** La methode accesseur qui renvoit le code à partir de la valeur */
	public static int getCodeForValue ( String value ) {
		for ( EnumTypeContrat etc : EnumTypeContrat.values() )
			if ( value.equals( etc.getValue() ) ) return etc.getCode();
		
		return -1;
	}
	/** La methode accesseur qui renvoit la valeur à partir du code */
	public static String getValueForCode ( int code ) {
		for ( EnumTypeContrat etc : EnumTypeContrat.values() )
			if ( code == etc.getCode() ) return etc.getValue();
		
		return null;
	}
}
