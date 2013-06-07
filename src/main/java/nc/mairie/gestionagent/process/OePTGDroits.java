package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGDroits extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OePTGDroits.class);

	public static final int STATUT_RECHERCHER_AGENT = 1;
	public String ACTION_CREATION = "Création d'un approbateur.";
	public String ACTION_SUPPRESSION = "Suppression d'un approbateur.";

	private ArrayList<AgentWithServiceDto> listeApprobateurs = new ArrayList<AgentWithServiceDto>();

	@Override
	public String getJSP() {
		return "OePTGDroits.jsp";
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// on recupere l'agent ajouté eventuellement
		AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null && !agt.getIdAgent().equals(Const.CHAINE_VIDE)) {

			Affectation affCourante = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agt.getIdAgent());
			if (getTransaction().isErreur()) {
				// "ERR400",
				// "L'agent @ n'est affecté à aucun poste. Il ne peut être ajouté en tant qu'approbateur."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR400", agt.getIdAgent()));
				throw new Exception();

			}
			FichePoste fpCourante = FichePoste.chercherFichePoste(getTransaction(), affCourante.getIdFichePoste());
			Service serv = Service.chercherService(getTransaction(), fpCourante.getIdServi());
			AgentWithServiceDto agentDto = new AgentWithServiceDto();
			agentDto.setIdAgent(Integer.valueOf(agt.getIdAgent()));
			agentDto.setNom(agt.getNomAgent());
			agentDto.setPrenom(agt.getPrenomAgent());
			agentDto.setCodeService(fpCourante.getIdServi());
			agentDto.setService(serv.getLibService());
			getListeApprobateurs().add(agentDto);
			afficheListeApprobateurs();
		}

		if (getListeApprobateurs().size() == 0) {
			initialiseApprobateurs(request);
			afficheListeApprobateurs();
		}

	}

	private void initialiseApprobateurs(HttpServletRequest request) throws Exception {
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		setListeApprobateurs((ArrayList<AgentWithServiceDto>) t.getApprobateurs());
	}

	private void afficheListeApprobateurs() throws Exception {
		for (int i = 0; i < getListeApprobateurs().size(); i++) {
			AgentWithServiceDto ag = getListeApprobateurs().get(i);

			addZone(getNOM_ST_AGENT(i),
					ag.getNom() + " " + ag.getPrenom() + " (" + ag.getIdAgent().toString().substring(3, ag.getIdAgent().toString().length()) + ")");
			addZone(getNOM_ST_SERVICE(i), ag.getService() + " (" + ag.getCodeService() + ")");
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeApprobateurs().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste déroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {

	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-DROITS";
	}

	public ArrayList<AgentWithServiceDto> getListeApprobateurs() {
		return listeApprobateurs;
	}

	public void setListeApprobateurs(ArrayList<AgentWithServiceDto> listeApprobateurs) {
		this.listeApprobateurs = listeApprobateurs;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		AgentWithServiceDto agentSelec = getListeApprobateurs().get(indiceEltASuprimer);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		if (getListeApprobateurs().contains(agentSelec)) {
			getListeApprobateurs().remove(agentSelec);
		}

		afficheListeApprobateurs();
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		List<AgentWithServiceDto> listeEnvoi = getListeApprobateurs();
		List<AgentWithServiceDto> listeAgentErreur = t.setApprobateurs(new JSONSerializer().serialize(listeEnvoi));
		if (listeAgentErreur.size() > 0) {
			String agents = Const.CHAINE_VIDE;
			for (AgentWithServiceDto agentDtoErreur : listeAgentErreur) {
				agents += " " + agentDtoErreur.getNom();
			}
			// "INF600",
			// "Les agents suivants n'ont pu être ajouté en tant qu'approbateurs car ils sont dejà opérateur : @"
			getTransaction().declarerErreur(MessageUtils.getMessage("INF600", agents));
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		getListeApprobateurs().clear();
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
}
