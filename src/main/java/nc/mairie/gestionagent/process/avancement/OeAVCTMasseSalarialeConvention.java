package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de cr�ation : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTMasseSalarialeConvention extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OeAVCTMasseSalarialeConvention.class);

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// V�rification des droits d'acc�s. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (21/11/11 09:55:36)
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

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTMasseSalarialeConvention() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTMasseSalarialeConvention.jsp";
	}
	/**
	 * Getter du nom de l'�cran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SIMU-MASSE-CONV";
	}
}