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
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (03/02/09 14:56:59)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
	}

	/**
	 * @param focus
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (03/02/09 14:56:59)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public OePTGSelectionApprobateur() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (24/08/11 09:15:05)
	 * 
	 */
	public String getJSP() {
		return "OePTGSelectionApprobateur.jsp";
	}
}
