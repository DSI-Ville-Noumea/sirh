package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.HistoAffectation;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.HistoAffectationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTEmploisAffHisto Date de création : (16/08/11 10:08:01)
 * 
 */
public class OeAGENTEmploisAffHisto extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentNW agentCourant;
	private ArrayList<HistoAffectation> listeHistoAffectation;
	private HistoAffectationDao histoAffectationDao;
	private FichePosteDao fichePosteDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (16/08/11 10:08:01)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {

		initialiseDao();

		// Si agentCourant vide
		if (getAgentCourant() == null) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeHistoAffectation(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getHistoAffectationDao() == null) {
			setHistoAffectationDao(new HistoAffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeHistoAffectation(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ArrayList<HistoAffectation> affHisto = getHistoAffectationDao().listerAffectationHistoAvecAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()));
		setListeHistoAffectation(affHisto);

		int indiceHistoAff = 0;
		if (getListeHistoAffectation() != null) {
			for (int i = 0; i < getListeHistoAffectation().size(); i++) {
				HistoAffectation ah = (HistoAffectation) getListeHistoAffectation().get(i);
				FichePoste fp = getFichePosteDao().chercherFichePoste(ah.getIdFichePoste());
				Service service = Service.chercherService(getTransaction(), fp.getIdServi());

				addZone(getNOM_ST_MATR(indiceHistoAff),
						ah.getIdAgent().toString().substring(3, ah.getIdAgent().toString().length()));
				addZone(getNOM_ST_SERV(indiceHistoAff), service.getLibService().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: service.getLibService());
				addZone(getNOM_ST_REF_ARR(indiceHistoAff),
						ah.getRefArreteAff() == null ? "&nbsp;" : ah.getRefArreteAff());
				addZone(getNOM_ST_DATE_DEBUT(indiceHistoAff), sdf.format(ah.getDateDebutAff()));
				addZone(getNOM_ST_DATE_FIN(indiceHistoAff),
						ah.getDateFinAff() == null ? "&nbsp;" : sdf.format(ah.getDateFinAff()));
				addZone(getNOM_ST_CODE_ECOLE(indiceHistoAff), ah.getCodeEcole().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: ah.getCodeEcole());

				indiceHistoAff++;
			}
		}

	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (16/08/11 10:08:01)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_OK
			if (testerParametre(request, getNOM_PB_OK())) {
				return performPB_OK(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTEmploisAffHisto. Date de création :
	 * (16/08/11 10:08:01)
	 * 
	 */
	public OeAGENTEmploisAffHisto() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (16/08/11 10:08:01)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEmploisAffHisto.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de création :
	 * (16/08/11 10:08:01)
	 * 
	 */
	public String getNOM_PB_OK() {
		return "NOM_PB_OK";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 10:08:01)
	 * 
	 */
	public boolean performPB_OK(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met à jour l'agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	public ArrayList<HistoAffectation> getListeHistoAffectation() {
		return listeHistoAffectation;
	}

	private void setListeHistoAffectation(ArrayList<HistoAffectation> listeHistoAffectation) {
		this.listeHistoAffectation = listeHistoAffectation;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MATR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MATR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERV Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SERV(int i) {
		return "NOM_ST_SERV" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERV Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERV(int i) {
		return getZone(getNOM_ST_SERV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REF_ARR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REF_ARR(int i) {
		return "NOM_ST_REF_ARR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REF_ARR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REF_ARR(int i) {
		return getZone(getNOM_ST_REF_ARR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_ECOLE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE_ECOLE(int i) {
		return "NOM_ST_CODE_ECOLE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_ECOLE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE_ECOLE(int i) {
		return getZone(getNOM_ST_CODE_ECOLE(i));
	}

	public HistoAffectationDao getHistoAffectationDao() {
		return histoAffectationDao;
	}

	public void setHistoAffectationDao(HistoAffectationDao histoAffectationDao) {
		this.histoAffectationDao = histoAffectationDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}
}
