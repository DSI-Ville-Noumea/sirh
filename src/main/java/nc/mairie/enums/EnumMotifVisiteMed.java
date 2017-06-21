package nc.mairie.enums;

public enum EnumMotifVisiteMed {
	VM_REGULIERE(2, "VISITE REGULIERE"), VM_NOUVEAU(1, "NOUVEL ARRIVANT"), VM_CHANGEMENT_PA(3, "CHANGEMENT DE PA"), VM_DEMANDE_AGENT(
			4, "A LA DEMANDE DE L'AGENT"), VM_DEMANDE_SERVICE(5, "A LA DEMANDE DU SERVICE"), VM_AT_ITT_15JOURS(6,
			"AT avec ITT > 15jours"), VM_MA_1MOIS(7, "MALADIE > 30jours"), VM_CONGE_LONGUE_MALADIE(8,
			"CONGE LONGUE MALADIE"), VM_AGENT_SANS_VM(9, "SANS VISITE MEDICALE");

	/** L'attribut qui contient le code associe a l'enum */
	private final Integer code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumMotifVisiteMed(Integer code, String value) {
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
		String motifs[] = new String[EnumMotifVisiteMed.values().length];
		int i = 0;
		for (EnumMotifVisiteMed elt : EnumMotifVisiteMed.values()) {
			motifs[i++] = elt.getValue();
		}
		return motifs;
	}
}
