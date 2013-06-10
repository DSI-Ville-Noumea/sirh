package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTEFEActiviteSelection Date de création : (03/02/09 14:56:59)
 * 
 */
public class OeAVCTSelectionEvaluateur extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<AgentNW> listeEvaluateurs;
	private ArrayList<AgentNW> listeEvaluateursExistant;
	private ArrayList<AgentNW> listeEvaluateursPossible;
	private ArrayList<AgentNW> listeDepart = new ArrayList<AgentNW>();

	public String focus = null;
	private boolean first = true;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// on recupere les evaluateurs deja present
		ArrayList<AgentNW> listDep = new ArrayList<AgentNW>();
		if (isFirst()) {
			listDep = (ArrayList<AgentNW>) VariablesActivite.recuperer(this, "LISTEEVALUATEUR");
			VariablesActivite.enlever(this, "LISTEEVALUATEUR");
			setListeDepart(null);
			ArrayList<AgentNW> listeBis = (ArrayList<AgentNW>) listDep.clone();
			setListeDepart(listeBis);
			setFirst(false);
		}
		recupereEvaluateur(request, listDep);
		afficheListe(request);
		addZone(getNOM_RG_RECHERCHE(), getNOM_RB_RECH_NOM());

	}

	private void recupereEvaluateur(HttpServletRequest request, ArrayList<AgentNW> listDep) {
		if (getListeEvaluateursExistant().size() == 0) {

			// Affectation de la liste
			setListeEvaluateursExistant(listDep);
			setListeEvaluateurs(listDep);
		}
	}

	private void afficheListe(HttpServletRequest request) {
		for (int j = 0; j < getListeEvaluateurs().size(); j++) {
			AgentNW agent = (AgentNW) getListeEvaluateurs().get(j);
			Integer i = Integer.valueOf(agent.getIdAgent());
			addZone(getNOM_ST_ID_AGENT(i), agent.getIdAgent());
			addZone(getNOM_ST_LIB_AGENT(i), agent.getNomAgent() + " " + agent.getPrenomAgent());
			if (getListeEvaluateursExistant().contains(agent))
				addZone(getNOM_CK_SELECT_LIGNE(i), getCHECKED_ON());
		}
	}

	private void afficheListeEvalPossible(HttpServletRequest request) {
		for (int j = 0; j < getListeEvaluateursPossible().size(); j++) {
			AgentNW agent = (AgentNW) getListeEvaluateursPossible().get(j);
			Integer i = Integer.valueOf(agent.getIdAgent());
			addZone(getNOM_ST_ID_AGENT_POSSIBLE(i), agent.getIdAgent());
			addZone(getNOM_ST_LIB_AGENT_POSSIBLE(i), agent.getNomAgent() + " " + agent.getPrenomAgent());
			addZone(getNOM_CK_SELECT_LIGNE_POSSIBLE(i), getCHECKED_OFF());
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (03/02/09 14:56:59)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		VariablesActivite.ajouter(this, "EVALUATEURS", getListeDepart());
		setListeDepart(null);
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (19/07/11 16:22:13)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (19/07/11 16:22:13)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		ArrayList<AgentNW> listAgentSelect = new ArrayList<AgentNW>();
		for (int j = 0; j < getListeEvaluateurs().size(); j++) {
			// on recupère la ligne concernée
			AgentNW ag = (AgentNW) getListeEvaluateurs().get(j);
			Integer i = Integer.valueOf(ag.getIdAgent());
			// si la colonne selection est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				listAgentSelect.add(ag);
			}
		}
		// on verifie que la liste ne depasse pas 2 personnes
		if (listAgentSelect.size() > 2) {
			// "ERR211",
			// "Vous ne pouvez pas sélectionner plus de 2 évaluateurs."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR211"));
			return false;
		}

		VariablesActivite.ajouter(this, "EVALUATEURS", listAgentSelect);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_OK
			for (int i = 0; i < getListeEvaluateursPossible().size(); i++) {
				AgentNW ag = getListeEvaluateursPossible().get(i);
				if (testerParametre(request, getNOM_PB_OK(Integer.valueOf(ag.getIdAgent())))) {
					return performPB_OK(request, Integer.valueOf(ag.getIdAgent()));
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFEActiviteSelection. Date de création :
	 * (24/08/11 09:15:05)
	 * 
	 */
	public OeAVCTSelectionEvaluateur() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (24/08/11 09:15:05)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTSelectionEvaluateur.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ID_AGENT(int i) {
		return "NOM_ST_ID_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_AGENT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ID_AGENT(int i) {
		return getZone(getNOM_ST_ID_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_AGENT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_RECHERCHE Date de création : (15/09/11 10:51:20)
	 * 
	 */
	public String getNOM_RG_RECHERCHE() {
		return "NOM_RG_RECHERCHE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_RECHERCHE
	 * Date de création : (15/09/11 10:51:20)
	 * 
	 */
	public String getVAL_RG_RECHERCHE() {
		return getZone(getNOM_RG_RECHERCHE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_NOM Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_NOM() {
		return "NOM_RB_RECH_NOM";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RECH_PRENOM Date de
	 * création : (08/10/08 13:07:23)
	 * 
	 */
	public String getNOM_RB_RECH_PRENOM() {
		return "NOM_RB_RECH_PRENOM";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ZONE Date de
	 * création : (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_EF_ZONE() {
		return "NOM_EF_ZONE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_ZONE
	 * Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public String getVAL_EF_ZONE() {
		return getZone(getNOM_EF_ZONE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		String zone = getVAL_EF_ZONE();

		ArrayList<AgentNW> aListe = new ArrayList<AgentNW>();
		// Si rien de saisi, recherche de tous les agents
		if (zone.length() == 0) {
			ArrayList<PositionAdmAgent> listeNomatrActif = PositionAdmAgent.listerPositionAdmAgentEnActivite(getTransaction());
			String liste = Const.CHAINE_VIDE;
			for (PositionAdmAgent pa : listeNomatrActif) {
				liste += pa.getNomatr() + ",";
			}
			if (!liste.equals(Const.CHAINE_VIDE)) {
				liste = liste.substring(0, liste.length() - 1);
			}
			aListe = AgentNW.listerAgentWithListNomatr(getTransaction(), liste);
			// Sinon, si numérique on cherche l'agent
		} else if (Services.estNumerique(zone)) {
			PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), zone);
			if (getTransaction().isErreur()) {
				return false;
			}
			AgentNW aAgent = AgentNW.chercherAgentParMatricule(getTransaction(), paAgent.getNomatr());
			// Si erreur alors pas trouvé. On traite
			if (getTransaction().isErreur())
				return false;

			aListe.add(aAgent);

			// Sinon, les agents dont le nom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_NOM())) {
			// on recupere une liste d'agent avec le nom commencant par...
			ArrayList<AgentNW> listeAgentWithNom = AgentNW.listerAgentAvecNomCommencant(getTransaction(), zone);
			ArrayList<AgentNW> listeAgentEnActivite = new ArrayList<AgentNW>();
			// on parcours cette liste pour ne mettre que les agents en activite
			for (AgentNW ag : listeAgentWithNom) {
				PositionAdmAgent paActive = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), ag.getNoMatricule());
				if (paActive == null || getTransaction().isErreur()) {
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					continue;
				} else {
					listeAgentEnActivite.add(ag);
				}
			}
			aListe = listeAgentEnActivite;
			// sinon les agents dont le prénom commence par
		} else if (getVAL_RG_RECHERCHE().equals(getNOM_RB_RECH_PRENOM())) {
			// on recupere une liste d'agent avec le prénom commencant par...
			ArrayList<AgentNW> listeAgentWithPrenom = AgentNW.listerAgentAvecPrenomCommencant(getTransaction(), zone);
			ArrayList<AgentNW> listeAgentEnActivite = new ArrayList<AgentNW>();
			// on parcours cette liste pour ne mettre que les agents en activite
			for (AgentNW ag : listeAgentWithPrenom) {
				PositionAdmAgent paActive = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), ag.getNoMatricule());
				if (paActive == null || getTransaction().isErreur()) {
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					continue;
				} else {
					listeAgentEnActivite.add(ag);
				}
			}
			aListe = listeAgentEnActivite;
		}

		// Si la liste est vide alors erreur
		if (aListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}
		ArrayList<AgentNW> xcludeListe = getListeEvaluateurs();
		aListe = elim_doublure_evaluateur(aListe, xcludeListe);

		setListeEvaluateursPossible(null);
		if (aListe.size() == 1) {
			getListeEvaluateursExistant().addAll(aListe);
		} else {
			setListeEvaluateursPossible(aListe);
			afficheListeEvalPossible(request);
		}
		addZone(getNOM_EF_ZONE(), Const.CHAINE_VIDE);
		setFocus(getNOM_EF_ZONE());

		return true;
	}

	private ArrayList<AgentNW> elim_doublure_evaluateur(ArrayList<AgentNW> l1, ArrayList<AgentNW> l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((AgentNW) l2.get(i)).getIdAgent()).equals(((AgentNW) l1.get(j)).getIdAgent()))
						l1.remove(j);

				}
			}
		}
		return l1;
	}

	public ArrayList<AgentNW> getListeEvaluateursExistant() {
		if (listeEvaluateursExistant == null)
			return new ArrayList<AgentNW>();
		return listeEvaluateursExistant;
	}

	public void setListeEvaluateursExistant(ArrayList<AgentNW> listeEvaluateursExistant) {
		this.listeEvaluateursExistant = listeEvaluateursExistant;
	}

	public ArrayList<AgentNW> getListeEvaluateurs() {
		if (listeEvaluateurs == null)
			return new ArrayList<AgentNW>();
		return listeEvaluateurs;
	}

	public void setListeEvaluateurs(ArrayList<AgentNW> listeEvaluateurs) {
		this.listeEvaluateurs = listeEvaluateurs;
	}

	public ArrayList<AgentNW> getListeEvaluateursPossible() {
		if (listeEvaluateursPossible == null)
			return new ArrayList<AgentNW>();
		return listeEvaluateursPossible;
	}

	public void setListeEvaluateursPossible(ArrayList<AgentNW> listeEvaluateursPossible) {
		this.listeEvaluateursPossible = listeEvaluateursPossible;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_AGENT_POSSIBLE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ID_AGENT_POSSIBLE(int i) {
		return "NOM_ST_ID_AGENT_POSSIBLE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ID_AGENT_POSSIBLE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ID_AGENT_POSSIBLE(int i) {
		return getZone(getNOM_ST_ID_AGENT_POSSIBLE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AGENT_POSSIBLE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_LIB_AGENT_POSSIBLE(int i) {
		return "NOM_ST_LIB_AGENT_POSSIBLE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_AGENT_POSSIBLE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_LIB_AGENT_POSSIBLE(int i) {
		return getZone(getNOM_ST_LIB_AGENT_POSSIBLE(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_POSSIBLE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_POSSIBLE(int i) {
		return "NOM_CK_SELECT_LIGNE_POSSIBLE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_POSSIBLE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_POSSIBLE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de création :
	 * (01/01/03 09:35:10)
	 * 
	 */
	public String getNOM_PB_OK(int i) {
		return "NOM_PB_OK" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/01/03 09:35:10)
	 * 
	 */
	public boolean performPB_OK(HttpServletRequest request, Integer idAgent) throws Exception {

		AgentNW agent = AgentNW.chercherAgent(getTransaction(), idAgent.toString());
		if (getVAL_CK_SELECT_LIGNE_POSSIBLE(idAgent).equals(getCHECKED_ON())) {
			getListeEvaluateursPossible().remove(agent);
			getListeEvaluateurs().add(agent);
			afficheListeEvalPossible(request);
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getFocus() {
		return focus;
	}

	public ArrayList<AgentNW> getListeDepart() {
		return listeDepart == null ? new ArrayList<AgentNW>() : listeDepart;
	}

	public void setListeDepart(ArrayList<AgentNW> listeDepart) {
		this.listeDepart = listeDepart;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

}
