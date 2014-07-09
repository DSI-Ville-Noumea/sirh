package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.EnfantNW;
import nc.mairie.metier.agent.LienEnfantNWAgentNW;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OeAGENTEnfantHomonyme
 * Date de création : (03/10/11 14:00:29)
     *
 */
public class OeAGENTEnfantHomonyme extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_ENFANT_HOMONYME;

	private ArrayList<EnfantNW> listeEnfantHomonyme;

	private AgentNW agentCourant;
	private EnfantNW enfantCourant;

	/**
	 * Initialisation des zones à afficher dans la JSP
	 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
	 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
	 * Utilisation de la méthode addZone(getNOMxxx, String);
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	@SuppressWarnings("unchecked")
	public void initialiseZones(HttpServletRequest request) throws Exception {

		if (getAgentCourant() == null) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
			} else {
				//ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		if (getEnfantCourant() == null) {
			EnfantNW aEnfant = (EnfantNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_ENFANT_COURANT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_ENFANT_COURANT);
			if (aEnfant != null) {
				setEnfantCourant(aEnfant);
			} else {
				//"ERR005", "Aucun @ trouvé."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR005", "enfant"));
				return;
			}
		}

		if (getLB_ENFANT_HOMONYME() == LBVide) {
			setListeEnfantHomonyme((ArrayList<EnfantNW>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_ENFANT_HOMONYME));

			int taillesEnfHomonyme[] = { 10, 95, 30 };
			FormateListe aListeEnfantFormatee = new FormateListe(taillesEnfHomonyme);
			for (int i = 0; i < getListeEnfantHomonyme().size(); i++) {
				EnfantNW enfant = (EnfantNW) getListeEnfantHomonyme().get(i);
				ArrayList<AgentNW> parents = AgentNW.listerAgentNWAvecEnfant(getTransaction(), enfant);
				if (parents.size() < 2 && parents.size()>0) {
					String colonnes[] = { parents.get(0).getNoMatricule(), parents.get(0).getNomAgent() + " " + parents.get(0).getPrenomAgent(), enfant.getCommentaire() };
					aListeEnfantFormatee.ajouteLigne(colonnes);
				}
			}
			setLB_ENFANT_HOMONYME(aListeEnfantFormatee.getListeFormatee());
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_CREER
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		if (getEnfantCourant() != null) {
			getEnfantCourant().creerEnfantNW(getTransaction());

			//Creation du lien
			LienEnfantNWAgentNW aLien = new LienEnfantNWAgentNW(getAgentCourant().getIdAgent(), getEnfantCourant().getId_enfant(), false);
			aLien.creerLienEnfantNWAgentNW(getTransaction());
			commitTransaction();
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ENFANT_COURANT, getEnfantCourant());
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SELECTIONNER
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public String getNOM_PB_SELECTIONNER() {
		return "NOM_PB_SELECTIONNER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public boolean performPB_SELECTIONNER(HttpServletRequest request) throws Exception {
		//Test si ligne sélectionnée
		int numligne = (Services.estNumerique(getZone(getNOM_LB_ENFANT_HOMONYME_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ENFANT_HOMONYME_SELECT())) : -1);
		if (numligne == -1 || getListeEnfantHomonyme().size() == 0 || numligne > getListeEnfantHomonyme().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Enfants homonymes"));
			return false;
		}

		//Recuperation Enfant homonyme sélectionné
		setEnfantCourant((EnfantNW) getListeEnfantHomonyme().get(numligne));

		//Creation du lien
		LienEnfantNWAgentNW aLien = new LienEnfantNWAgentNW(getAgentCourant().getIdAgent(), getEnfantCourant().getId_enfant(), false);
		aLien.creerLienEnfantNWAgentNW(getTransaction());
		commitTransaction();

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ENFANT_COURANT, getEnfantCourant());
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize :
	 * LB_ENFANT_HOMONYME
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	private String[] getLB_ENFANT_HOMONYME() {
		if (LB_ENFANT_HOMONYME == null)
			LB_ENFANT_HOMONYME = initialiseLazyLB();
		return LB_ENFANT_HOMONYME;
	}

	/**
	 * Setter de la liste:
	 * LB_ENFANT_HOMONYME
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	private void setLB_ENFANT_HOMONYME(String[] newLB_ENFANT_HOMONYME) {
		LB_ENFANT_HOMONYME = newLB_ENFANT_HOMONYME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP :
	 * NOM_LB_ENFANT_HOMONYME
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public String getNOM_LB_ENFANT_HOMONYME() {
		return "NOM_LB_ENFANT_HOMONYME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ENFANT_HOMONYME_SELECT
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public String getNOM_LB_ENFANT_HOMONYME_SELECT() {
		return "NOM_LB_ENFANT_HOMONYME_SELECT";
	}

	/**
	 * Méthode à personnaliser
	 * Retourne la valeur à afficher pour la zone de la JSP :
	 * LB_ENFANT_HOMONYME
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public String[] getVAL_LB_ENFANT_HOMONYME() {
		return getLB_ENFANT_HOMONYME();
	}

	/**
	 * Méthode à personnaliser
	 * Retourne l'indice à sélectionner pour la zone de la JSP :
	 * LB_ENFANT_HOMONYME
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public String getVAL_LB_ENFANT_HOMONYME_SELECT() {
		return getZone(getNOM_LB_ENFANT_HOMONYME_SELECT());
	}

	/**
	 * Getter de l'enfant courant.
	 * @return enfantCourant
	 */
	public EnfantNW getEnfantCourant() {
		return enfantCourant;
	}

	/**
	 * Setter de l'enfant courant.
	 * @param enfantCourant
	 */
	private void setEnfantCourant(EnfantNW enfantCourant) {
		this.enfantCourant = enfantCourant;
	}

	/**
	 * Getter de l'agent courant.
	 * @return AgentNW
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Setter de l'agent courant.
	 * @param agentCourant
	 */
	public void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : 
	 * en fonction du bouton de la JSP 
	 * Date de création : (03/10/11 14:00:29)
     *
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		//Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			//Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			//Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			//Si clic sur le bouton PB_SELECTIONNER
			if (testerParametre(request, getNOM_PB_SELECTIONNER())) {
				return performPB_SELECTIONNER(request);
			}

		}
		//Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTEnfantHomonyme.
	 * Date de création : (06/10/11 14:27:55)
     *
	 */
	public OeAGENTEnfantHomonyme() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process
	 * Zone à utiliser dans un champ caché dans chaque formulaire de la JSP.
	 * Date de création : (06/10/11 14:27:55)
     *
	 */
	public String getJSP() {
		return "OeAGENTEnfantHomonyme.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_ANNULER
	 * Date de création : (06/10/11 14:27:55)
     *
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (06/10/11 14:27:55)
     *
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Getter de la liste des enfants homonymes.
	 * @return listeEnfantHomonyme
	 */
	private ArrayList<EnfantNW> getListeEnfantHomonyme() {
		return listeEnfantHomonyme;
	}

	/**
	 * Setter de la liste des enfants homonymes.
	 * @param listeEnfantHomonyme
	 */
	private void setListeEnfantHomonyme(ArrayList<EnfantNW> listeEnfantHomonyme) {
		this.listeEnfantHomonyme = listeEnfantHomonyme;
	}
}
