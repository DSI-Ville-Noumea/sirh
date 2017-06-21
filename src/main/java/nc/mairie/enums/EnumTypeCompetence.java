package nc.mairie.enums;

import java.util.ArrayList;

import nc.mairie.metier.Const;

public enum EnumTypeCompetence {
	SAVOIR(1, "Savoir", "SA"), SAVOIR_FAIRE(2, "Savoir-faire", "SF"), COMPORTEMENT(3, "Comportements professionnels",
			"CP");

	/** L'attribut qui contient le code associe a l'enum */
	private final Integer code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String valueCourte;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumTypeCompetence(Integer code, String value, String valueCourte) {
		this.code = code;
		this.value = value;
		this.valueCourte = valueCourte;
	}

	/** La methode accesseur qui renvoit le code de l'enum */
	public Integer getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValueCourte() {
		return this.valueCourte;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static ArrayList<EnumTypeCompetence> getValues() {
		ArrayList<EnumTypeCompetence> listeComp = new ArrayList<EnumTypeCompetence>();
		for (EnumTypeCompetence elt : EnumTypeCompetence.values()) {
			listeComp.add(elt);
		}
		return listeComp;
	}

	/** La methode qui renvoit la valeur de l'enum */
	public static String getValueEnumTypeCompetence(Integer code) {
		ArrayList<EnumTypeCompetence> listeType = EnumTypeCompetence.getValues();
		for (int i = 0; i < listeType.size(); i++) {
			EnumTypeCompetence type = listeType.get(i);
			if (type.getCode().toString().equals(code.toString())) {
				return type.getValueCourte();
			}
		}

		return Const.CHAINE_VIDE;
	}
}
