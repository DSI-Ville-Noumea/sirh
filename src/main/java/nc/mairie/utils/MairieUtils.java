package nc.mairie.utils;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeDroit;
import nc.mairie.metier.Const;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;

public class MairieUtils {

	//***********************************************************************************************************************************************
	// Fonctions pour la gestion des droits
	//***********************************************************************************************************************************************

	/**
	 * Retourne le nom de la classe css a utiliser selon les droits de l'utilisateur et le type de droit pour lequel l'element HTML doit etre affiche.
	 * @param request HttpServletRequest
	 * @param nomEcran String
	 * @param typeDroit EnumTypeDroit
	 * @return String
	 * @throws Exception
	 */
	public static String getNomClasseCSS(HttpServletRequest request, String nomEcran, EnumTypeDroit typeDroit, String nomClasseCSS) throws Exception {

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		if (user.getListeDroits().contains(nomEcran + "-E")) {
			if (typeDroit.equals(EnumTypeDroit.EDITION))
				// Pour afficher l'element au bon format
				return nomClasseCSS;
		} else if (user.getListeDroits().contains(nomEcran + "-C")) {
			if (typeDroit.equals(EnumTypeDroit.CONSULTATION))
				// Pour afficher l'element au bon format
				return nomClasseCSS;
		}

		// Pour cacher l'element
		return "sigp2-displayNone";
	}

	/**
	 * Retourne le type de droit de l'utilisateur sur l'element passe en parametre. Null si aucun droit.
	 * @param user UserAppli
	 * @param nomElement String
	 * @return EnumTypeDroit
	 * @throws Exception
	 */
	private static EnumTypeDroit getTypeDroitAvecElement(UserAppli user, String nomElement) throws Exception {

		if (user.getListeDroits().contains(nomElement + "-E"))
			return EnumTypeDroit.EDITION;
		else if (user.getListeDroits().contains(nomElement + "-C"))
			return EnumTypeDroit.CONSULTATION;
		return null;
	}

	/**
	 * Retourne true si l'utilisateur a les droits en Consultation. false sinon.
	 * @param request HttpServletRequest
	 * @param nomElement String
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean estConsultation(HttpServletRequest request, String nomElement) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		return EnumTypeDroit.CONSULTATION.equals(getTypeDroitAvecElement(user, nomElement));
	}

	/**
	 * Retourne true si l'utilisateur n'a aucun droit sur l'element passe en parametre. false sinon.
	 * @param request HttpServletRequest
	 * @param nomElement String
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean estInterdit(HttpServletRequest request, String nomElement) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		return getTypeDroitAvecElement(user, nomElement) == null;
	}

	/**
	 * Retourne 'disabled' si l'utilisateur a les droits en Consultation. 'enabled" sinon.
	 * @param request HttpServletRequest
	 * @param nomElement String
	 * @return String
	 * @throws Exception
	 */
	public static String getDisabled(HttpServletRequest request, String nomElement) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		if (EnumTypeDroit.CONSULTATION.equals(getTypeDroitAvecElement(user, nomElement)))
			return "disabled='disabled'";

		return Const.CHAINE_VIDE;
	}

	/**
	 * Retourne true si l'utilisateur a les droits en Consultation. false sinon.
	 * @param request HttpServletRequest
	 * @param nomElement String
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean estEdition(HttpServletRequest request, String nomElement) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		if(EnumTypeDroit.EDITION.equals(getTypeDroitAvecElement(user, nomElement))){
			return true; 
		}else{
			return false;
		}
	}
}
