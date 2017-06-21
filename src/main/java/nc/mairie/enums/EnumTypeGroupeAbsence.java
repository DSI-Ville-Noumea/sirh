package nc.mairie.enums;

public enum EnumTypeGroupeAbsence {

	RECUP(1), REPOS_COMP(2), AS(3), CONGES_EXCEP(4), CONGES_ANNUELS(5), MALADIES(6);

	private int type;

	private EnumTypeGroupeAbsence(int _type) {
		type = _type;
	}

	public int getValue() {
		return type;
	}

	public static EnumTypeGroupeAbsence getRefTypeGroupeAbsenceEnum(Integer type) {

		if (type == null)
			return null;

		switch (type) {
			case 1:
				return RECUP;
			case 2:
				return REPOS_COMP;
			case 3:
				return AS;
			case 4:
				return CONGES_EXCEP;
			case 5:
				return CONGES_ANNUELS;
			default:
				return null;
		}
	}
}
