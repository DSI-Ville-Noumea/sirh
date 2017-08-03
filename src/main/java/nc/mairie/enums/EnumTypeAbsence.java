package nc.mairie.enums;

public enum EnumTypeAbsence {
	//// @formatter:off
	CONGE(1, "Congé annuel"), 
	REPOS_COMP(2, "Repos compensateur"), 
	RECUP(3, "Récupération"), 
	ASA_A48(7, "Réunion des membres du bureau directeur"), 
	ASA_A54(8, "Congrès et conseil syndical"), 
	ASA_A55(9, "Délégation DP"), 
	ASA_A52(10, "Décharge de service CTP"), 
	ASA_A53(11, "Formation syndicale"), 
	ASA_A49(12, "Participation à une réunion syndicale"), 
	ASA_A50(13, "Activité institutionnelle"), 
	ASA_AMICALE(69, "Membre du bureau de l'Amicale de la ville"),
	MALADIES_MALADIE(81, "Maladie"), 
	MALADIES_MALADIE_ENFANT(74, "Maladie - Enfant malade"), 
	MALADIES_HOSPITALISATION(71, "Hospitalisation"), 
	MALADIES_CONVALESCENCE(72, "Convalescence"), 
	MALADIES_EVASAN(73, "Evasan"), 
	MALADIES_LONGUE_MALADIE(76, "Congé longue maladie"),
	MALADIES_ACCIDENT_TRAVAIL(77, "Accident  de travail"), 
	MALADIES_RECHUTE(78, "Rechute AT"), 
	MALADIES_ENFANT_MALADE(80, "Enfant malade"), 
	MALADIES_PROFESSIONNELLE(79, "Maladie professionnelle");
	// @formatter:on
	/** L'attribut qui contient le code associe a l'enum */
	private final Integer	code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String	value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumTypeAbsence(Integer code, String value) {
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

	public static EnumTypeAbsence getRefTypeAbsenceEnum(Integer type) {

		if (type == null)
			return null;

		switch (type) {
			case 1:
				return CONGE;
			case 2:
				return REPOS_COMP;
			case 3:
				return RECUP;
			case 7:
				return ASA_A48;
			case 8:
				return ASA_A54;
			case 9:
				return ASA_A55;
			case 10:
				return ASA_A52;
			case 11:
				return ASA_A53;
			case 12:
				return ASA_A49;
			case 13:
				return ASA_A50;
			case 69:
				return ASA_AMICALE;
			case 81:
				return MALADIES_MALADIE;
			case 74:
				return MALADIES_MALADIE_ENFANT;
			case 71:
				return MALADIES_HOSPITALISATION;
			case 72:
				return MALADIES_CONVALESCENCE;
			case 73:
				return MALADIES_EVASAN;
			case 77:
				return MALADIES_ACCIDENT_TRAVAIL;
			case 78:
				return MALADIES_RECHUTE;
			case 80:
				return MALADIES_ENFANT_MALADE;
			default:
				return null;
		}
	}
}
