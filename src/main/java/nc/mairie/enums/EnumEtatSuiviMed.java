package nc.mairie.enums;

import java.util.ArrayList;

public enum EnumEtatSuiviMed {
	TRAVAIL("T", "Non traité"), PLANIFIE("P", "Planifié"), EFFECTUE("E", "Effectuée");

	/** L'attribut qui contient la valeur associee a l'enum */
	private final String code;

	/** L'attribut qui contient la valeur associe a l'enum */
	private final String value;

	/** Le constructeur qui associe une valeur a l'enum */
	private EnumEtatSuiviMed(String code, String value) {
		this.code = code;
		this.value = value;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getCode() {
		return this.code;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}

	/** La methode accesseur qui renvoit la liste des valeurs de l'enum */
	public static ArrayList<EnumEtatSuiviMed> getValues() {
		ArrayList<EnumEtatSuiviMed> listeVal = new ArrayList<EnumEtatSuiviMed>();
		for (EnumEtatSuiviMed elt : EnumEtatSuiviMed.values()) {
			listeVal.add(elt);
		}
		return listeVal;
	}
}
