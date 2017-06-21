package nc.mairie.utils;

import java.util.Enumeration;
import java.util.Hashtable;

import nc.mairie.metier.Const;

/**
 * Cette classe permet de lister la liste des messages d'erreur et d'information.
 * Cette liste de messages doit etre renseignee dans le getter de la HashTable listeMessagesSIRH
 */
public class MessageUtils {
	/**
	 * Methode servant a compter le nombre de caracteres @
	 */
	private static int compterLesAt(String libelle) {
		// Si libelle vide ou null retourne 0
		if (libelle == null || libelle.length() == 0) {
			return 0;
		}

		// On compte les @
		int nbAt = 0;
		String s = libelle;
		while (s.length() != 0) {
			int posAt = s.indexOf("@");
			if (posAt < 0)
				break;
			s = s.substring(posAt + 1);
			nbAt++;
		}

		return nbAt;
	}

	/**
	 * Methode servant a compter le nombre de caracteres @
	 */
	private static String controleMessageParametres(String nomMessage, String[] param) {
		// Recuperation du libelle dans la HashTable
		String libelle = (String) getListeMessages().get(nomMessage);

		// Si message absent, retour d'un message par defaut.
		if (libelle == null || libelle.length() == 0)
			return getDefautMessage(nomMessage);

		// On compte les @
		int nbAt = compterLesAt(libelle);

		// Si nbAt <> nbParam alors erreur
		if (nbAt != param.length)
			return "Pour le message " + nomMessage + " il faut " + nbAt + " paramètres";

		return null;
	}

	/**
	 * Methode servant a compter le nombre de caracteres @
	 */
	private static String formateMessage(String nomMessage, String[] params) {
		// Recup le libelle avec les @
		String libelle = (String) getListeMessages().get(nomMessage);
		String result = Const.CHAINE_VIDE;
		int curAt = 0;

		// Remplacement
		while (libelle.indexOf("@") != -1) {
			int posAt = libelle.indexOf("@");
			result = result + libelle.substring(0, posAt) + params[curAt];
			curAt++;
			libelle = libelle.substring(posAt + 1);
		}
		return result + libelle;

	}

	/**
	 * Methode servant a retourner un message par defaut, indiquant que le message d'erreur demande n'est pas
	 * implemente.
	 */
	private static String getDefautMessage(String nomMessage) {
		return nomMessage + " : message non implémenté !";
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getLibelleCodeMessage(String codeMessage) {

		return (String) getListeMessages().get(codeMessage);

	}

	/**
	 * Methode qui construit la HashTable avec tous les messages d'erreur et d'information de
	 * 
	 * @VeRSe
	 */
	private static Hashtable<String, String> getListeMessages() {
		return ListeMessagesSIRH.getListeMessages();
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage) {

		String[] params = {};
		return getMessage(nomMessage, params);

	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	private static String getMessage(String nomMessage, String[] params) {

		// Controle des param
		String controle = controleMessageParametres(nomMessage, params);
		if (controle != null)
			return controle;

		String libelle = formateMessage(nomMessage, params);

		return nomMessage + " : " + libelle;
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage, String param1) {

		String[] params = { param1 };
		return getMessage(nomMessage, params);
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage, String param1, String param2) {

		String[] params = { param1, param2 };
		return getMessage(nomMessage, params);
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage, String param1, String param2, String param3) {

		String[] params = { param1, param2, param3 };
		return getMessage(nomMessage, params);
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage, String param1, String param2, String param3, String param4) {

		String[] params = { param1, param2, param3, param4 };
		return getMessage(nomMessage, params);
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage, String param1, String param2, String param3, String param4, String param5) {

		String[] params = { param1, param2, param3, param4, param5 };
		return getMessage(nomMessage, params);
	}

	/**
	 * Methode statique utilisee pour retourner le libelle d'un code message passe en parametre.
	 */
	public static String getMessage(String nomMessage, String param1, String param2, String param3, String param4, String param5, String param6) {

		String[] params = { param1, param2, param3, param4, param5, param6 };
		return getMessage(nomMessage, params);
	}

	/**
	 * Methode statique qui retourne les libelles contenant les chaines passees en param.
	 */
	public static String listerMessagesContenant(String[] chaines) {

		if (chaines == null || chaines.length == 0)
			return null;

		String res = Const.CHAINE_VIDE;
		Hashtable<?, ?> h = getListeMessages();
		Enumeration<?> enume = h.keys();
		while (enume.hasMoreElements()) {
			String cle = (String) enume.nextElement();
			String lib = (String) h.get(cle);
			boolean contient = true;
			for (int i = 0; i < chaines.length; i++) {
				if (lib.toUpperCase().indexOf(chaines[i].toUpperCase()) == -1) {
					contient = false;
					break;
				}
			}
			if (contient)
				res += cle + " : " + lib + "\n";
		}
		return res;

	}
}
