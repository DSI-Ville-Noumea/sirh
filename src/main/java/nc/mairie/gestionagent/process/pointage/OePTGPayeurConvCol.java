package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGPayeurConvCol extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private Logger logger = LoggerFactory.getLogger(OePTGPayeurConvCol.class);

	public static final String STATUT = "CC";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private ArrayList<EtatsPayeurDto> listEtatsPayeurDto;

	private String libelleStatut = "conventions collectives";

	private String urlFichier;

	@Override
	public String getJSP() {
		return "OePTGPayeurConvCol.jsp";
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

		// liste des historiques des editions
		initialiseHistoriqueEditions();

	}

	// affichage ou non du bouton "lancer editions"
	public boolean isBoutonLancerEditionAffiche() throws Exception {

		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		try {
			return t.canStartExportEtatsPayeur(STATUT);
		} catch (Exception e) {
			logger.debug("Erreur OePTGPayeurConvCol.isBoutonLancerEditionAffiche() " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			// clic sur le bouton Lancer editions
			if (testerParametre(request, getNOM_PB_LANCER_EDITIONS())) {
				return performPB_LANCER_EDITIONS(request);
			}

			// Si clic sur le bouton PB_VISUALISER_DOC
			for (int i = 0; i < getListEtatsPayeurDto().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISER_DOC(i))) {
					return performPB_VISUALISER_DOC(request, i);
				}
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
	private void initialiseHistoriqueEditions() throws Exception {

		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		try {
			setListEtatsPayeurDto((ArrayList<EtatsPayeurDto>) t.getListEtatsPayeurByStatut(STATUT));
		} catch (Exception e) {
			logger.debug("Erreur OePTGPayeurConvCol.initialiseHistoriqueEditions() " + e.getMessage());
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR700", libelleStatut));
		}

		for (int i = 0; i < getListEtatsPayeurDto().size(); i++) {
			EtatsPayeurDto dto = getListEtatsPayeurDto().get(i);

			addZone(getNOM_ST_USER_DATE_EDITION(i),
					sdf.format(dto.getDateEdition()) + "<br />" + dto.getDisplayPrenom() + " " + dto.getDisplayNom());
			addZone(getNOM_ST_LIBELLE_EDITION(i), dto.getLabel());
		}

	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_LANCER_EDITIONS(HttpServletRequest request) throws Exception {

		try {
			// on recupere l'agent connecté
			UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
			AgentNW agentConnecte = null;

			if (!u.getUserName().equals("nicno85")) {
				// on recupere l'id de l'agent
				Siidma ag = Siidma.chercherSiidma(getTransaction(), u.getUserName().toUpperCase());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// "ERR183",
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return false;
				}
				agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), ag.getNomatr());
			} else {
				agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
			}
			SirhPtgWSConsumer ptg = new SirhPtgWSConsumer();

			ptg.startExportEtatsPayeur(agentConnecte.getIdAgent(), STATUT);
		} catch (Exception e) {
			logger.debug("Erreur OePTGPayeurConvCol.performPB_LANCER_EDITIONS() " + e.getMessage());
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR702", libelleStatut));
			return false;
		}

		return true;
	}

	/**
	 * @return the listEtatsPayeurDto
	 */
	public ArrayList<EtatsPayeurDto> getListEtatsPayeurDto() {
		return listEtatsPayeurDto == null ? new ArrayList<EtatsPayeurDto>() : listEtatsPayeurDto;
	}

	/**
	 * @param listEtatsPayeurDto
	 *            the listEtatsPayeurDto to set
	 */
	public void setListEtatsPayeurDto(ArrayList<EtatsPayeurDto> listEtatsPayeurDto) {
		this.listEtatsPayeurDto = listEtatsPayeurDto;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-PAY-CONV-COL";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_LANCER_EDITIONS() {
		return "NOM_PB_LANCER_EDITIONS";
	}

	public String getNOM_ST_USER_DATE_EDITION(int i) {
		return "NOM_ST_USER_DATE_EDITION_" + i;
	}

	public String getVAL_ST_USER_DATE_EDITION(int i) {
		return getZone(getNOM_ST_USER_DATE_EDITION(i));
	}

	public String getNOM_ST_LIBELLE_EDITION(int i) {
		return "NOM_ST_LIBELLE_EDITION_" + i;
	}

	public String getVAL_ST_LIBELLE_EDITION(int i) {
		return getZone(getNOM_ST_LIBELLE_EDITION(i));
	}

	public String getNOM_PB_VISUALISER_DOC(int i) {
		return "NOM_PB_VISUALISER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VISUALISER_DOC(HttpServletRequest request, int indiceEltAVisualiser) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");

		// Récup de l'Etat
		EtatsPayeurDto etat = getListEtatsPayeurDto().get(indiceEltAVisualiser);
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + "Pointages/" + etat.getFichier()));

		return true;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
	}

}
