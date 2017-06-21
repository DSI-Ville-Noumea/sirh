package nc.mairie.utils;

import nc.mairie.technique.BasicProcess;

/**
 * Inserez la description du type ici.
 * Date de creation : (24/03/2011 11:30:00)
 */
public class VariablesActivite implements ListeVariablesActivite {
	/**
	 * Methode qui ajoute dans la session une variable globale
	 * Date de creation : (08/11/2002 14:53:28)
	 */
	public static void ajouter(BasicProcess aProcess, String nomVariable, Object valeurVariable) {
		aProcess.getTransaction().ajouteVariable(nomVariable, valeurVariable);
	}

	/**
	 * Methode qui ajoute dans la session une variable globale
	 * Date de creation : (08/11/2002 14:53:28)
	 */
	public static void enlever(BasicProcess aProcess, String nomVariable) {
		aProcess.getTransaction().enleveVariable(nomVariable);
	}

	/**
	 * Methode qui ajoute dans la session une variable globale
	 * Date de creation : (08/11/2002 14:53:28)
	 */
	public static Object recuperer(BasicProcess aProcess, String nomVariable) {
		return aProcess.getTransaction().recupereVariable(nomVariable);
	}
}
