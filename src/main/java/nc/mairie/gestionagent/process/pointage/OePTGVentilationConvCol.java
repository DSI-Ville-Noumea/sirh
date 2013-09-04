package nc.mairie.gestionagent.process.pointage;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGVentilationConvCol extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private Logger logger = org.slf4j.LoggerFactory.getLogger(OePTGVentilationConvCol.class);

	@Override
	public String getJSP() {
		return "OePTGVentilationConvCol.jsp";
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

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste déroulantes de l'écran convocation du suivi médical.
	 */
	private void initialiseListeDeroulante() throws Exception {
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-VENT-CONV-COL";
	}

	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_VENTILATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_HS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_PRIMES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_ABS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_VALIDATION(), Const.CHAINE_VIDE);

		return true;
	}

	public String getNOM_ST_ACTION_VENTILATION() {
		return "NOM_ST_ACTION_VENTILATION";
	}

	public String getNOM_ST_ACTION_HS() {
		return "NOM_ST_ACTION_HS";
	}

	public String getNOM_ST_ACTION_PRIMES() {
		return "NOM_ST_ACTION_PRIMES";
	}

	public String getNOM_ST_ACTION_ABS() {
		return "NOM_ST_ACTION_ABS";
	}

	public String getNOM_ST_ACTION_VALIDATION() {
		return "NOM_ST_ACTION_VALIDATION";
	}

	public String getTab(int typePointage) {
		Map<Integer, AgentNW> agents = new HashMap<>();
		try {
			agents.put(9004634, AgentNW.chercherAgent(getTransaction(), "9004634"));
		} catch (Exception ex) {
			logger.debug("agent non trouvé.");
		}
		return OePTGVentilationUtils.getTabVisu(agents, 51, typePointage, false);
	}

	public String getValid() {
		return OePTGVentilationUtils.getTabValid("CC");
	}

	public String getVentil() {
		return OePTGVentilationUtils.getTabVentil("CC");
	}
}
