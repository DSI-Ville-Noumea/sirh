package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Enfant;
import nc.mairie.metier.agent.LienEnfantAgent;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.agent.EnfantDao;
import nc.mairie.spring.dao.metier.agent.LienEnfantAgentDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTEnfantHomonyme Date de cr�ation : (03/10/11 14:00:29)
 * 
 */
public class OeAGENTEnfantHomonyme extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_ENFANT_HOMONYME;

	private ArrayList<Enfant> listeEnfantHomonyme;

	private AgentNW agentCourant;
	private Enfant enfantCourant;
	private LienEnfantAgentDao lienEnfantAgentDao;
	private EnfantDao enfantDao;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void initialiseZones(HttpServletRequest request) throws Exception {

		if (getAgentCourant() == null) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		initialiseDao();
		if (getEnfantCourant() == null) {
			Enfant aEnfant = (Enfant) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_ENFANT_COURANT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_ENFANT_COURANT);
			if (aEnfant != null) {
				setEnfantCourant(aEnfant);
			} else {
				// "ERR005", "Aucun @ trouv�."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR005", "enfant"));
				return;
			}
		}

		if (getLB_ENFANT_HOMONYME() == LBVide) {
			setListeEnfantHomonyme((ArrayList<Enfant>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_ENFANT_HOMONYME));

			int taillesEnfHomonyme[] = { 10, 95, 30 };
			FormateListe aListeEnfantFormatee = new FormateListe(taillesEnfHomonyme);
			for (int i = 0; i < getListeEnfantHomonyme().size(); i++) {
				Enfant enfant = (Enfant) getListeEnfantHomonyme().get(i);
				ArrayList<AgentNW> parents = AgentNW.listerAgentNWAvecEnfant(getTransaction(), enfant);
				if (parents.size() < 2 && parents.size() > 0) {
					String colonnes[] = { parents.get(0).getNoMatricule(),
							parents.get(0).getNomAgent() + " " + parents.get(0).getPrenomAgent(),
							enfant.getCommentaire() };
					aListeEnfantFormatee.ajouteLigne(colonnes);
				}
			}
			setLB_ENFANT_HOMONYME(aListeEnfantFormatee.getListeFormatee());
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getLienEnfantAgentDao() == null) {
			setLienEnfantAgentDao(new LienEnfantAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getEnfantDao() == null) {
			setEnfantDao(new EnfantDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de cr�ation :
	 * (03/10/11 14:00:29)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		if (getEnfantCourant() != null) {
			Integer id = getEnfantDao().creerEnfant(getEnfantCourant().getIdDocument(), getEnfantCourant().getNom(),
					getEnfantCourant().getPrenom(), getEnfantCourant().getSexe(),
					getEnfantCourant().getDateNaissance(), getEnfantCourant().getCodePaysNaissEt(),
					getEnfantCourant().getCodeCommuneNaissEt(), getEnfantCourant().getCodeCommuneNaissFr(),
					getEnfantCourant().getDateDeces(), getEnfantCourant().getNationalite(),
					getEnfantCourant().getCommentaire());

			// Creation du lien
			LienEnfantAgent aLien = new LienEnfantAgent(Integer.valueOf(getAgentCourant().getIdAgent()), id, false);
			getLienEnfantAgentDao().creerLienEnfantAgent(aLien.getIdAgent(), aLien.getIdEnfant(),
					aLien.isEnfantACharge());
			commitTransaction();
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ENFANT_COURANT, getEnfantCourant());
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECTIONNER Date de
	 * cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public String getNOM_PB_SELECTIONNER() {
		return "NOM_PB_SELECTIONNER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public boolean performPB_SELECTIONNER(HttpServletRequest request) throws Exception {
		// Test si ligne s�lectionn�e
		int numligne = (Services.estNumerique(getZone(getNOM_LB_ENFANT_HOMONYME_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ENFANT_HOMONYME_SELECT())) : -1);
		if (numligne == -1 || getListeEnfantHomonyme().size() == 0 || numligne > getListeEnfantHomonyme().size() - 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Enfants homonymes"));
			return false;
		}

		// Recuperation Enfant homonyme s�lectionn�
		setEnfantCourant((Enfant) getListeEnfantHomonyme().get(numligne));

		// Creation du lien
		LienEnfantAgent aLien = new LienEnfantAgent(Integer.valueOf(getAgentCourant().getIdAgent()), getEnfantCourant()
				.getIdEnfant(), false);
		getLienEnfantAgentDao().creerLienEnfantAgent(aLien.getIdAgent(), aLien.getIdEnfant(), aLien.isEnfantACharge());
		commitTransaction();

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ENFANT_COURANT, getEnfantCourant());
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ENFANT_HOMONYME Date de
	 * cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	private String[] getLB_ENFANT_HOMONYME() {
		if (LB_ENFANT_HOMONYME == null)
			LB_ENFANT_HOMONYME = initialiseLazyLB();
		return LB_ENFANT_HOMONYME;
	}

	/**
	 * Setter de la liste: LB_ENFANT_HOMONYME Date de cr�ation : (03/10/11
	 * 14:00:29)
	 * 
	 */
	private void setLB_ENFANT_HOMONYME(String[] newLB_ENFANT_HOMONYME) {
		LB_ENFANT_HOMONYME = newLB_ENFANT_HOMONYME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ENFANT_HOMONYME Date de
	 * cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public String getNOM_LB_ENFANT_HOMONYME() {
		return "NOM_LB_ENFANT_HOMONYME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_ENFANT_HOMONYME_SELECT Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public String getNOM_LB_ENFANT_HOMONYME_SELECT() {
		return "NOM_LB_ENFANT_HOMONYME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_ENFANT_HOMONYME Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public String[] getVAL_LB_ENFANT_HOMONYME() {
		return getLB_ENFANT_HOMONYME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_ENFANT_HOMONYME Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public String getVAL_LB_ENFANT_HOMONYME_SELECT() {
		return getZone(getNOM_LB_ENFANT_HOMONYME_SELECT());
	}

	/**
	 * Getter de l'enfant courant.
	 * 
	 * @return enfantCourant
	 */
	public Enfant getEnfantCourant() {
		return enfantCourant;
	}

	/**
	 * Setter de l'enfant courant.
	 * 
	 * @param enfantCourant
	 */
	private void setEnfantCourant(Enfant enfantCourant) {
		this.enfantCourant = enfantCourant;
	}

	/**
	 * Getter de l'agent courant.
	 * 
	 * @return AgentNW
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Setter de l'agent courant.
	 * 
	 * @param agentCourant
	 */
	public void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (03/10/11 14:00:29)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_SELECTIONNER
			if (testerParametre(request, getNOM_PB_SELECTIONNER())) {
				return performPB_SELECTIONNER(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTEnfantHomonyme. Date de cr�ation :
	 * (06/10/11 14:27:55)
	 * 
	 */
	public OeAGENTEnfantHomonyme() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (06/10/11 14:27:55)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEnfantHomonyme.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (06/10/11 14:27:55)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (06/10/11 14:27:55)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Getter de la liste des enfants homonymes.
	 * 
	 * @return listeEnfantHomonyme
	 */
	private ArrayList<Enfant> getListeEnfantHomonyme() {
		return listeEnfantHomonyme;
	}

	/**
	 * Setter de la liste des enfants homonymes.
	 * 
	 * @param listeEnfantHomonyme
	 */
	private void setListeEnfantHomonyme(ArrayList<Enfant> listeEnfantHomonyme) {
		this.listeEnfantHomonyme = listeEnfantHomonyme;
	}

	public LienEnfantAgentDao getLienEnfantAgentDao() {
		return lienEnfantAgentDao;
	}

	public void setLienEnfantAgentDao(LienEnfantAgentDao lienEnfantAgentDao) {
		this.lienEnfantAgentDao = lienEnfantAgentDao;
	}

	public EnfantDao getEnfantDao() {
		return enfantDao;
	}

	public void setEnfantDao(EnfantDao enfantDao) {
		this.enfantDao = enfantDao;
	}
}
