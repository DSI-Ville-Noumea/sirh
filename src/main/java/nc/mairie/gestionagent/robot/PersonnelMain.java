package nc.mairie.gestionagent.robot;

import nc.mairie.metier.Const;
import nc.mairie.technique.VariableGlobale;

/**
 * Ins�rez la description du type ici.
 * Date de cr�ation : (10/01/2003 09:39:40)
 */
public class PersonnelMain extends nc.mairie.technique.BasicProcess {
	/**
	 * Commentaire relatif au constructeur DefaultProcess.
	 */
	public PersonnelMain() {
		super();
	}

	/**
	 Retourne le nom de la JSP du process
	 Zone � utiliser dans un champ cach� dans chaque formulaire de la JSP.
	 */
	public String getJSP() {
		return "PersonnelMain.jsp";
	}

	/**
	 Initialisation des zones � afficher dans le JSP
	 */
	public void initialiseZones(javax.servlet.http.HttpServletRequest request) throws Exception {
		//POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		if (Const.MODE_DESELECTION_AGENT.equals(request.getParameter("ACTIVITE"))) {
			VariableGlobale.enlever(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			setProcessAppelant(new PersonnelMain());
		}
	}

	/**
	 * Process incoming requests for information
	 * 
	 * @param request Object that encapsulates the request to the servlet 
	 */
	public boolean recupererStatut(javax.servlet.http.HttpServletRequest request) throws Exception {
		return false;
	}
}
