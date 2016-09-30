package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.connecteur.metier.Spmtsr;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MessageUtils;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAdsService;

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
	private Agent agentCourant;
	private ArrayList<Spmtsr> listeHistoAffectation;
	private FichePosteDao fichePosteDao;

	private IAdsService adsService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (16/08/11 10:08:01)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {

		initialiseDao();

		// Si agentCourant vide
		if (getAgentCourant() == null) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
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
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
	}

	private void initialiseListeHistoAffectation(HttpServletRequest request) throws Exception {
		ArrayList<Spmtsr> affHisto = Spmtsr.listerSpmtsrAvecAgentOrderDateDeb(getTransaction(), getAgentCourant());
		setListeHistoAffectation(affHisto);

		int indiceHistoAff = 0;
		if (getListeHistoAffectation() != null) {
			for (int i = 0; i < getListeHistoAffectation().size(); i++) {
				Spmtsr ah = (Spmtsr) getListeHistoAffectation().get(i);
				EntiteDto service = adsService.getEntiteByCodeServiceSISERV(ah.getServi());

				addZone(getNOM_ST_MATR(indiceHistoAff), ah.getNomatr());
				addZone(getNOM_ST_SERV(indiceHistoAff),service==null ? Const.CHAINE_VIDE :  service.getLabel());
				addZone(getNOM_ST_REF_ARR(indiceHistoAff),
						ah.getRefarr().equals(Const.CHAINE_VIDE) ? "&nbsp;" : ah.getRefarr());
				addZone(getNOM_ST_DATE_DEBUT(indiceHistoAff), ah.getDatdeb());
				addZone(getNOM_ST_DATE_FIN(indiceHistoAff), ah.getDatfin());
				addZone(getNOM_ST_CODE_ECOLE(indiceHistoAff),
						ah.getCdecol().equals(Const.CHAINE_VIDE) ? "&nbsp;" : ah.getCdecol());

				indiceHistoAff++;
			}
		}

	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	public Agent getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met a jour l'agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	public ArrayList<Spmtsr> getListeHistoAffectation() {
		return listeHistoAffectation;
	}

	private void setListeHistoAffectation(ArrayList<Spmtsr> listeHistoAffectation) {
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MATR Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_SERV Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_REF_ARR Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_DEBUT
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_FIN Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_ECOLE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE_ECOLE(int i) {
		return getZone(getNOM_ST_CODE_ECOLE(i));
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}
}
