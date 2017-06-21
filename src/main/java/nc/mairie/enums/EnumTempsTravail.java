package nc.mairie.enums;

public enum EnumTempsTravail {
	P100("100"), P90("90"), P80("80"), P75("75"), P70("70"), P66("66"), P60("60"), P50("50"), P40("40"), P33("33"), P25("25"), P20("20");

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumTempsTravail(String value) {
		this.value = value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static String[] getValues() {
		String tpsTravail[] = new String[EnumTempsTravail.values().length];
		int i = 0;
		for (EnumTempsTravail elt : EnumTempsTravail.values()) {
			tpsTravail[i++] = elt.getValue();
		}
		return tpsTravail;
	}
}
