/*
 * Created on 6 fevr. 2009
 *
 */
package nc.mairie.metier;

import java.util.ArrayList;

/**
 * Methodes generiques utilitaires
 * 
 */
public class Utils {

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe l1 ayant elemine de la liste l1 les elements en communs avec l2
	 * fonctionne uniquement avec une liste l1 n'ayant pas 2 elements identiques
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList Elim_doubure(ArrayList l1, ArrayList l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if (l2.get(i) == l1.get(j))
						l1.remove(j);
				}
			}
		}
		return l1;

	}

}
