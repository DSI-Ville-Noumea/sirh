package nc.mairie.gestionagent.process.pointage;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.technique.BasicProcess;

public class OePTGSelectionApprobateur extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public OePTGSelectionApprobateur() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (24/08/11 09:15:05)
	 * 
	 */
	public String getJSP() {
		return "OePTGSelectionApprobateur.jsp";
	}
}
