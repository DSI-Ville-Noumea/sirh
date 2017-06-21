package nc.mairie.enums;

import java.util.ArrayList;

import nc.mairie.metier.Const;

public enum EnumEtatAbsence {
	PROVISOIRE(0, "Provisoire"), SAISIE(1, "Saisie"), VISEE_FAV(2, "Visée favorable"), VISEE_DEFAV(3,
			"Visée défavorable"), APPROUVE(4, "Approuvé"), REFUSE(5, "Refusé"), PRISE(6, "Prise"), ANNULEE(7, "Annulée par l'agent"), VALIDEE(
			8, "Validée par la DRH"), REJETE(9, "Rejetée par la DRH"), EN_ATTENTE(10, "En attente"), A_VALIDER(11, "En attente de validation par la DRH");

	/** L'attribut qui contient le code associe a l'enum */
	private final Integer code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumEtatAbsence(Integer code, String value) {
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
	public static ArrayList<EnumEtatAbsence> getValues() {
		ArrayList<EnumEtatAbsence> listeVal = new ArrayList<EnumEtatAbsence>();
		for (EnumEtatAbsence elt : EnumEtatAbsence.values()) {
			listeVal.add(elt);
		}
		return listeVal;
	}

	/** La methode qui renvoit la valeur de l'enum */
	public static String getValueEnumEtatAbsence(Integer code) {
		ArrayList<EnumEtatAbsence> listeEtat = EnumEtatAbsence.getValues();
		for (int i = 0; i < listeEtat.size(); i++) {
			EnumEtatAbsence etat = listeEtat.get(i);
			if (etat.getCode() == code) {
				return etat.getValue();
			}
		}

		return Const.CHAINE_VIDE;
	}
}
