package nc.mairie.enums;

import java.util.ArrayList;

import nc.mairie.metier.Const;

public enum EnumEtatEAE {
	NON_AFFECTE("NA", "Sans évaluateur"), NON_DEBUTE("ND", "Non débuté"), CREE("C", "Crée"), EN_COURS("EC", "En cours"), FINALISE("F", "Finalisé"), CONTROLE(
			"CO", "Contrôlé"), SUPPRIME("S", "Supprimé");

	/** L'attribut qui contient le code associe a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe un code et une valeur a l'enum */
	private EnumEtatEAE(String code, String value) {
		this.code = code;
		this.value = value;
	}

	/** La methode accesseur qui renvoit le code de l'enum */
	public String getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static ArrayList<EnumEtatEAE> getValues() {
		ArrayList<EnumEtatEAE> listeVal = new ArrayList<EnumEtatEAE>();
		for (EnumEtatEAE elt : EnumEtatEAE.values()) {
			listeVal.add(elt);
		}
		return listeVal;
	}

	/** La methode qui renvoit la valeur de l'enum */
	public static String getValueEnumEtatEAE(String code) {
		ArrayList<EnumEtatEAE> listeEtat = EnumEtatEAE.getValues();
		for (int i = 0; i < listeEtat.size(); i++) {
			EnumEtatEAE etat = listeEtat.get(i);
			if (etat.getCode().equals(code)) {
				return etat.getValue();
			}
		}

		return Const.CHAINE_VIDE;
	}
}
