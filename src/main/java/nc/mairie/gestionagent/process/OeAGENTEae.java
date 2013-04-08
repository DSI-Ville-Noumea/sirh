package nc.mairie.gestionagent.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeCommentaireDao;
import nc.mairie.spring.dao.metier.EAE.EaeDeveloppementDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvolutionDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFinalisationDao;
import nc.mairie.spring.dao.metier.EAE.EaePlanActionDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeCommentaire;
import nc.mairie.spring.domain.metier.EAE.EaeDeveloppement;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluateur;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;
import nc.mairie.spring.domain.metier.EAE.EaePlanAction;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTDIPLOMEGestion Date de cr�ation : (11/02/03 14:20:31)
 * 
 */
public class OeAGENTEae extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHER_AGENT = 1;

	public String ACTION_MODIFICATION = "Modification d'un EAE.";
	public String ACTION_CONSULTATION = "Consultation d'un EAE.";

	private String[] LB_BASE_HORAIRE;
	private ArrayList<Horaire> listeHoraire;
	private Hashtable<String, Horaire> hashHoraire;

	private AgentNW AgentCourant;
	private ArrayList<EAE> listeEae;
	private ArrayList<EaeEvaluateur> listeEvaluateurEae;
	private ArrayList<EaePlanAction> listeObjectifPro;
	private ArrayList<EaePlanAction> listeObjectifIndi;
	private ArrayList<EaeDeveloppement> listeDeveloppement;
	private EAE eaeCourant;

	private EAEDao eaeDao;
	private CampagneEAEDao campagneEaeDao;
	private EaeEvaluateurDao eaeEvaluateurDao;
	private EaeFichePosteDao eaeFichePosteDao;
	private EaeEvalueDao eaeEvalueDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private EaeCommentaireDao eaeCommentaireDao;
	private EaeFinalisationDao eaeFinalisationDao;
	private EaePlanActionDao eaePlanActionDao;
	private EaeEvolutionDao eaeEvolutionDao;
	private EaeDeveloppementDao eaeDeveloppementDao;

	private String urlFichier;

	public String focus = null;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// V�rification des droits d'acc�s.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		initialiseListeDeroulante();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeEae(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	/**
	 * Initialisation de la liste des primes de l'agent courant Date de cr�ation
	 * : (04/08/11)
	 * 
	 */
	private void initialiseListeEae(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des EAE de l'agent
		ArrayList<EaeEvalue> listeEAEEvalue = getEaeEvalueDao().listerEaeEvalue(Integer.valueOf(getAgentCourant().getIdAgent()));

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		int indiceEae = 0;
		if (listeEAEEvalue != null) {
			for (int i = 0; i < listeEAEEvalue.size(); i++) {
				EaeEvalue evalue = (EaeEvalue) listeEAEEvalue.get(i);
				EAE eae = getEaeDao().chercherEAE(evalue.getIdEae());
				listeEAE.add(eae);
				EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(evalue.getIdEae(), true);
				CampagneEAE camp = getCampagneEaeDao().chercherCampagneEAE(eae.getIdCampagneEAE());
				ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(evalue.getIdEae());
				String evaluateur = Const.CHAINE_VIDE;
				for (int j = 0; j < listeEvaluateur.size(); j++) {
					EaeEvaluateur eval = listeEvaluateur.get(j);
					AgentNW agentEvaluateur = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
					evaluateur += agentEvaluateur.getNomAgent() + " " + agentEvaluateur.getPrenomAgent() + " (" + agentEvaluateur.getNoMatricule()
							+ ") <br/> ";
				}

				addZone(getNOM_ST_ANNEE(indiceEae), camp.getAnnee().toString());
				addZone(getNOM_ST_EVALUATEUR(indiceEae), evaluateur.equals(Const.CHAINE_VIDE) ? "&nbsp;" : evaluateur);
				addZone(getNOM_ST_DATE_ENTRETIEN(indiceEae), eae.getDateEntretien() == null ? "&nbsp;" : sdf.format(eae.getDateEntretien()));
				addZone(getNOM_ST_SERVICE(indiceEae), eaeFDP.getServiceServ() == null ? "&nbsp;" : eaeFDP.getServiceServ());
				addZone(getNOM_ST_STATUT(indiceEae), EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()));

				indiceEae++;
			}
		}
		setListeEae(listeEAE);
	}

	private void initialiseListeDeroulante() throws Exception {

		// Si liste base horaire vide alors affectation
		if (getLB_BASE_HORAIRE() == LBVide) {
			// ArrayList<Horaire> liste =
			// Horaire.listerHoraire(getTransaction());
			ArrayList<Horaire> liste = Horaire.listerHoraireSansNulSansComplet(getTransaction());
			setListeHoraire(liste);

			int[] tailles = { 30 };
			String[] champs = { "libHor" };
			setLB_BASE_HORAIRE(new FormateListe(tailles, liste, champs).getListeFormatee(true));

			for (Horaire h : liste)
				getHashHoraire().put(h.getCdtHor(), h);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getEaeDao() == null) {
			setEaeDao((EAEDao) context.getBean("eaeDao"));
		}
		if (getCampagneEaeDao() == null) {
			setCampagneEaeDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}
		if (getEaeEvaluateurDao() == null) {
			setEaeEvaluateurDao((EaeEvaluateurDao) context.getBean("eaeEvaluateurDao"));
		}
		if (getEaeFichePosteDao() == null) {
			setEaeFichePosteDao((EaeFichePosteDao) context.getBean("eaeFichePosteDao"));
		}
		if (getEaeEvalueDao() == null) {
			setEaeEvalueDao((EaeEvalueDao) context.getBean("eaeEvalueDao"));
		}
		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao((EaeEvaluationDao) context.getBean("eaeEvaluationDao"));
		}
		if (getEaeCommentaireDao() == null) {
			setEaeCommentaireDao((EaeCommentaireDao) context.getBean("eaeCommentaireDao"));
		}
		if (getEaeFinalisationDao() == null) {
			setEaeFinalisationDao((EaeFinalisationDao) context.getBean("eaeFinalisationDao"));
		}
		if (getEaePlanActionDao() == null) {
			setEaePlanActionDao((EaePlanActionDao) context.getBean("eaePlanActionDao"));
		}
		if (getEaeEvolutionDao() == null) {
			setEaeEvolutionDao((EaeEvolutionDao) context.getBean("eaeEvolutionDao"));
		}
		if (getEaeDeveloppementDao() == null) {
			setEaeDeveloppementDao((EaeDeveloppementDao) context.getBean("eaeDeveloppementDao"));
		}
	}

	/**
	 * @return Agent
	 */
	public AgentNW getAgentCourant() {
		return AgentCourant;
	}

	/**
	 * @param newAgentCourant
	 *            Agent
	 */
	private void setAgentCourant(AgentNW newAgentCourant) {
		AgentCourant = newAgentCourant;
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		// return getNOM_EF_DATE_OBTENTION();
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}
			// Si clic sur le bouton PB_VISUALISER_DOC
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISER_DOC(i))) {
					return performPB_VISUALISER_DOC(request, i);
				}
			}
			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}

		}
		// Si pas de retour d�finit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non g�r� par le process");
		return false;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (17/10/11 10:36:22)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEae.jsp";
	}

	/**
	 * Retourne le nom de l'�cran (notamment pour d�terminer les droits
	 * associ�s).
	 */
	public String getNomEcran() {
		return "ECR-AG-EAE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ANNEE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR(int i) {
		return "NOM_ST_EVALUATEUR" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_EVALUATEUR
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR(int i) {
		return getZone(getNOM_ST_EVALUATEUR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ENTRETIEN Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_ENTRETIEN(int i) {
		return "NOM_ST_DATE_ENTRETIEN" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_ENTRETIEN(int i) {
		return getZone(getNOM_ST_DATE_ENTRETIEN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_SERVICE Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_STATUT Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// R�cup de l'eae courant
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltAConsulter);
		setEaeCourant(eaeCourant);

		initialiseEae();
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseEae() throws Exception {

		// R�cup de l'EAE courant
		EAE eae = getEaeCourant();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Alim zone Informations
		addZone(getNOM_ST_DATE_ENTRETIEN(), eae.getDateEntretien() == null ? "non renseign�" : sdf.format(eae.getDateEntretien()));
		ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(eae.getIdEAE());
		setListeEvaluateurEae(listeEvaluateur);
		for (int j = 0; j < listeEvaluateur.size(); j++) {
			EaeEvaluateur eval = listeEvaluateur.get(j);
			AgentNW agentEvaluateur = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
			String evaluateur = agentEvaluateur.getNomAgent() + " " + agentEvaluateur.getPrenomAgent() + " (" + agentEvaluateur.getNoMatricule()
					+ ") ";

			addZone(getNOM_ST_EVALUATEUR_NOM(j), evaluateur.equals(Const.CHAINE_VIDE) ? "non renseign�" : evaluateur);
			addZone(getNOM_ST_EVALUATEUR_FONCTION(j), eval.getFonction().equals(Const.CHAINE_VIDE) ? "non renseign�" : eval.getFonction());
		}
		EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEAE(), true);
		String direction = eaeFDP.getDirectionServ() == null ? Const.CHAINE_VIDE : eaeFDP.getDirectionServ();
		String serv = eaeFDP.getServiceServ() == null ? Const.CHAINE_VIDE : eaeFDP.getServiceServ();
		addZone(getNOM_ST_SERVICE(), direction.equals(Const.CHAINE_VIDE) ? serv.equals(Const.CHAINE_VIDE) ? "&nbsp;" : serv : direction + " / "
				+ serv);

		// Alim zone evaluation
		EaeEvaluation evaluation = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEAE());
		if (evaluation == null) {
			addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_NIVEAU(), "non renseign�");
			addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
			addZone(getNOM_ST_NOTE(), "non renseign�");
			addZone(getNOM_ST_AVIS_SHD(), "non renseign�");
			addZone(getNOM_ST_AVCT_DIFF(), "non renseign�");
			addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
			addZone(getNOM_ST_CHANGEMENT_CLASSE(), "non renseign�");
			addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
			addZone(getNOM_ST_AVIS_REVALO(), "non renseign�");
			addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
			addZone(getNOM_ST_RAPPORT_CIRCON(), Const.CHAINE_VIDE);
		} else {
			// commentaire de l'evaluateur
			if (evaluation.getIdCommEvaluateur() != null) {
				EaeCommentaire commEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(evaluation.getIdCommEvaluateur());
				addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), commEvaluateur == null ? Const.CHAINE_VIDE : commEvaluateur.getCommentaire());
			} else {
				addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), Const.CHAINE_VIDE);
			}
			// commentaire de l'evaluateur sur le rapport circonstanci�
			if (evaluation.getIdCommAvctEvaluateur() != null) {
				EaeCommentaire commAvctEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(evaluation.getIdCommAvctEvaluateur());
				addZone(getNOM_ST_RAPPORT_CIRCON(), commAvctEvaluateur == null ? Const.CHAINE_VIDE : commAvctEvaluateur.getCommentaire());
			} else {
				addZone(getNOM_ST_RAPPORT_CIRCON(), Const.CHAINE_VIDE);
			}
			addZone(getNOM_ST_NIVEAU(), evaluation.getNiveau() == null ? "non renseign�" : evaluation.getNiveau());
			// pour la modif
			if (evaluation.getNiveau() == null) {
				addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
			} else {
				String niveau = evaluation.getNiveau();
				if (niveau.equals("NECESSITANT_DES_PROGRES")) {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_PROGR());
				} else if (niveau.equals("INSUFFISANT")) {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_INSU());
				} else if (niveau.equals("EXCELLENT")) {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_EXCEL());
				} else {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
				}
			}

			addZone(getNOM_ST_NOTE(), evaluation.getNoteAnnee() == null ? "non renseign�" : evaluation.getNoteAnnee().toString());
			addZone(getNOM_ST_AVIS_SHD(), evaluation.getAvis_shd() == null ? "non renseign� " : evaluation.getAvis_shd());
			addZone(getNOM_ST_AVCT_DIFF(), evaluation.getPropositionAvancement() == null ? "non renseign�" : evaluation.getPropositionAvancement());
			// pour la modif
			if (evaluation.getPropositionAvancement() == null) {
				addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
			} else {
				if (evaluation.getPropositionAvancement().equals("MINI")) {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MIN());
				} else if (evaluation.getPropositionAvancement().equals("MAXI")) {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MAX());
				} else {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
				}
			}
			addZone(getNOM_ST_CHANGEMENT_CLASSE(),
					evaluation.getAvisChangementClasse() == null ? "non renseign�" : evaluation.getAvisChangementClasse() == 1 ? "favorable"
							: "d�favorable");
			// pour la modif
			if (evaluation.getAvisChangementClasse() == null) {
				addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
			} else {
				if (evaluation.getAvisRevalorisation() == 0) {
					addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_DEF());
				} else {
					addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
				}
			}
			addZone(getNOM_ST_AVIS_REVALO(), evaluation.getAvisRevalorisation() == null ? "non renseign�"
					: evaluation.getAvisRevalorisation() == 1 ? "favorable" : "d�favorable");
			// pour la modif
			if (evaluation.getAvisRevalorisation() == null) {
				addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
			} else {
				if (evaluation.getAvisRevalorisation() == 0) {
					addZone(getNOM_RG_REVA(), getNOM_RB_REVA_DEF());
				} else {
					addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
				}
			}
		}

		// alim zone plan action
		ArrayList<EaePlanAction> listeObjectifPro = getEaePlanActionDao().listerPlanActionParType(eae.getIdEAE(), 1);
		setListeObjectifPro(listeObjectifPro);
		for (int j = 0; j < listeObjectifPro.size(); j++) {
			EaePlanAction plan = listeObjectifPro.get(j);

			addZone(getNOM_ST_LIB_OBJ_PRO(j), plan.getObjectif());
			addZone(getNOM_ST_LIB_MESURE_PRO(j), plan.getMesure());
		}

		ArrayList<EaePlanAction> listeObjectifIndi = getEaePlanActionDao().listerPlanActionParType(eae.getIdEAE(), 2);
		setListeObjectifIndi(listeObjectifIndi);
		for (int j = 0; j < listeObjectifIndi.size(); j++) {
			EaePlanAction plan = listeObjectifIndi.get(j);

			addZone(getNOM_ST_LIB_OBJ_INDI(j), plan.getObjectif());
		}

		// TODO
		// Alim zone Evolution
		EaeEvolution evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEAE());
		if (evolution == null) {
			addZone(getNOM_ST_MOB_GEO(), "non renseign�");
			addZone(getNOM_RG_MOB_GEO(), getNOM_RB_MOB_GEO_NON());
			addZone(getNOM_ST_MOB_FONCT(), "non renseign�");
			addZone(getNOM_RG_MOB_FONCT(), getNOM_RB_MOB_FONCT_NON());
			addZone(getNOM_ST_CHANGEMENT_METIER(), "non renseign�");
			addZone(getNOM_RG_METIER(), getNOM_RB_METIER_NON());
			addZone(getNOM_ST_DELAI(), "non renseign�");
			addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
			addZone(getNOM_ST_MOB_SERV(), "non renseign�");
			addZone(getNOM_RG_MOB_SERV(), getNOM_RB_MOB_SERV_NON());
			addZone(getNOM_ST_MOB_DIR(), "non renseign�");
			addZone(getNOM_RG_MOB_DIR(), getNOM_RB_MOB_DIR_NON());
			addZone(getNOM_ST_MOB_COLL(), "non renseign�");
			addZone(getNOM_RG_MOB_COLL(), getNOM_RB_MOB_COLL_NON());
			addZone(getNOM_ST_NOM_COLL(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_MOB_AUTRE(), "non renseign�");
			addZone(getNOM_RG_MOB_AUTRE(), getNOM_RB_MOB_AUTRE_NON());
			addZone(getNOM_ST_CONCOURS(), "non renseign�");
			addZone(getNOM_RG_CONCOURS(), getNOM_RB_CONCOURS_NON());
			addZone(getNOM_ST_NOM_CONCOURS(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_VAE(), "non renseign�");
			addZone(getNOM_RG_VAE(), getNOM_RB_VAE_NON());
			addZone(getNOM_ST_NOM_VAE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_TPS_PARTIEL(), "non renseign�");
			addZone(getNOM_RG_TPS_PARTIEL(), getNOM_RB_TPS_PARTIEL_NON());
			addZone(getNOM_ST_POURC_TPS_PARTIEL(), "non renseign�");
			addZone(getNOM_ST_RETRAITE(), "non renseign�");
			addZone(getNOM_RG_RETRAITE(), getNOM_RB_RETRAITE_NON());
			addZone(getNOM_ST_DATE_RETRAITE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_AUTRE_PERSP(), "non renseign�");
			addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_NON());
			addZone(getNOM_ST_LIB_AUTRE_PERSP(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_COM_EVOLUTION(), Const.CHAINE_VIDE);
		} else {
			if (evolution.getIdComEvolution() != null) {
				EaeCommentaire commEvolution = getEaeCommentaireDao().chercherEaeCommentaire(evolution.getIdComEvolution());
				addZone(getNOM_ST_COM_EVOLUTION(), commEvolution == null ? Const.CHAINE_VIDE : commEvolution.getCommentaire());
			} else {
				addZone(getNOM_ST_COM_EVOLUTION(), Const.CHAINE_VIDE);
			}
			addZone(getNOM_ST_MOB_GEO(), evolution.isMobiliteGeo() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteGeo()) {
				addZone(getNOM_RG_MOB_GEO(), getNOM_RB_MOB_GEO_NON());
			} else {
				addZone(getNOM_RG_MOB_GEO(), getNOM_RB_MOB_GEO_OUI());
			}
			addZone(getNOM_ST_MOB_FONCT(), evolution.isMobiliteFonct() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteFonct()) {
				addZone(getNOM_RG_MOB_FONCT(), getNOM_RB_MOB_FONCT_NON());
			} else {
				addZone(getNOM_RG_MOB_FONCT(), getNOM_RB_MOB_FONCT_OUI());
			}
			addZone(getNOM_ST_CHANGEMENT_METIER(), evolution.isChangementMetier() ? "oui" : "non");
			// pour la modif
			if (!evolution.isChangementMetier()) {
				addZone(getNOM_RG_METIER(), getNOM_RB_METIER_NON());
			} else {
				addZone(getNOM_RG_METIER(), getNOM_RB_METIER_OUI());
			}
			addZone(getNOM_ST_DELAI(), evolution.getDelaiEnvisage() == null ? "non renseign�" : evolution.getDelaiEnvisage());
			// pour la modif
			if (evolution.getDelaiEnvisage() == null) {
				addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
			} else {
				if (evolution.getDelaiEnvisage().equals("ENTRE1ET2ANS")) {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_2());
				} else if (evolution.getDelaiEnvisage().equals("MOINS1AN")) {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_1());
				} else {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
				}
			}
			addZone(getNOM_ST_MOB_SERV(), evolution.isMobiliteService() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteService()) {
				addZone(getNOM_RG_MOB_SERV(), getNOM_RB_MOB_SERV_NON());
			} else {
				addZone(getNOM_RG_MOB_SERV(), getNOM_RB_MOB_SERV_OUI());
			}
			addZone(getNOM_ST_MOB_DIR(), evolution.isMobiliteDirection() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteDirection()) {
				addZone(getNOM_RG_MOB_DIR(), getNOM_RB_MOB_DIR_NON());
			} else {
				addZone(getNOM_RG_MOB_DIR(), getNOM_RB_MOB_DIR_OUI());
			}
			addZone(getNOM_ST_MOB_COLL(), evolution.isMobiliteCollectivite() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteCollectivite()) {
				addZone(getNOM_RG_MOB_COLL(), getNOM_RB_MOB_COLL_NON());
			} else {
				addZone(getNOM_RG_MOB_COLL(), getNOM_RB_MOB_COLL_OUI());
			}
			addZone(getNOM_ST_NOM_COLL(), evolution.getNomCollectivite() == null ? Const.CHAINE_VIDE : evolution.getNomCollectivite());
			addZone(getNOM_ST_MOB_AUTRE(), evolution.isMobiliteAutre() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteAutre()) {
				addZone(getNOM_RG_MOB_AUTRE(), getNOM_RB_MOB_AUTRE_NON());
			} else {
				addZone(getNOM_RG_MOB_AUTRE(), getNOM_RB_MOB_AUTRE_OUI());
			}
			addZone(getNOM_ST_CONCOURS(), evolution.isConcours() ? "oui" : "non");
			// pour la modif
			if (!evolution.isConcours()) {
				addZone(getNOM_RG_CONCOURS(), getNOM_RB_CONCOURS_NON());
			} else {
				addZone(getNOM_RG_CONCOURS(), getNOM_RB_CONCOURS_OUI());
			}
			addZone(getNOM_ST_NOM_CONCOURS(), evolution.getNomConcours() == null ? Const.CHAINE_VIDE : evolution.getNomConcours());
			addZone(getNOM_ST_VAE(), evolution.isVae() ? "oui" : "non");
			// pour la modif
			if (!evolution.isVae()) {
				addZone(getNOM_RG_VAE(), getNOM_RB_VAE_NON());
			} else {
				addZone(getNOM_RG_VAE(), getNOM_RB_VAE_OUI());
			}
			addZone(getNOM_ST_NOM_VAE(), evolution.getNomVae() == null ? Const.CHAINE_VIDE : evolution.getNomVae());
			addZone(getNOM_ST_TPS_PARTIEL(), evolution.isTempsPartiel() ? "oui" : "non");
			// pour la modif
			if (!evolution.isTempsPartiel()) {
				addZone(getNOM_RG_TPS_PARTIEL(), getNOM_RB_TPS_PARTIEL_NON());
			} else {
				addZone(getNOM_RG_TPS_PARTIEL(), getNOM_RB_TPS_PARTIEL_OUI());
			}
			// Horaire tempsPart = Horaire.chercherHoraire(getTransaction(),
			// evolution.getIdSpbhorTpsPartiel().toString());
			Horaire tempsPart = (Horaire) getHashHoraire().get(evolution.getIdSpbhorTpsPartiel().toString());
			if (tempsPart != null) {
				Float taux = Float.parseFloat(tempsPart.getCdTaux()) * 100;
				int ligneHoraire = getListeHoraire().indexOf(tempsPart);
				addZone(getNOM_LB_BASE_HORAIRE_SELECT(), String.valueOf(ligneHoraire + 1));
				addZone(getNOM_ST_POURC_TPS_PARTIEL(), tempsPart == null || tempsPart.getCdtHor() == null ? "non renseign�" : tempsPart.getLibHor()
						+ " - " + String.valueOf(taux.intValue()) + "%");
			} else {
				addZone(getNOM_LB_BASE_HORAIRE_SELECT(), Const.ZERO);
				addZone(getNOM_ST_POURC_TPS_PARTIEL(), "non renseign�");
			}

			addZone(getNOM_ST_RETRAITE(), evolution.isRetraite() ? "oui" : "non");
			// pour la modif
			if (!evolution.isRetraite()) {
				addZone(getNOM_RG_RETRAITE(), getNOM_RB_RETRAITE_NON());
			} else {
				addZone(getNOM_RG_RETRAITE(), getNOM_RB_RETRAITE_OUI());
			}
			addZone(getNOM_ST_DATE_RETRAITE(), evolution.getDateRetraite() == null ? Const.CHAINE_VIDE : sdf.format(evolution.getDateRetraite()));
			addZone(getNOM_ST_AUTRE_PERSP(), evolution.isAutrePerspective() ? "oui" : "non");
			// pour la modif
			if (!evolution.isAutrePerspective()) {
				addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_NON());
			} else {
				addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_OUI());
			}
			addZone(getNOM_ST_LIB_AUTRE_PERSP(), evolution.getLibAutrePerspective() == null ? Const.CHAINE_VIDE : evolution.getLibAutrePerspective());
		}

		if (evolution != null) {
			// Alim zones developpement
			ArrayList<EaeDeveloppement> listeDeveloppement = getEaeDeveloppementDao().listerEaeDeveloppementParEvolution(
					evolution.getIdEaeEvolution());
			setListeDeveloppement(listeDeveloppement);
			for (int j = 0; j < listeDeveloppement.size(); j++) {
				EaeDeveloppement dev = listeDeveloppement.get(j);
				addZone(getNOM_ST_TYPE_DEV(j), dev.getTypeDeveloppement());
				addZone(getNOM_ST_LIB_DEV(j), dev.getLibelleDeveloppement());
				addZone(getNOM_ST_ECHEANCE_DEV(j), new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheanceDeveloppement()));
				addZone(getNOM_ST_PRIORISATION_DEV(j), dev.getPriorisation().toString());
			}
		}

	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup de l'eae courant
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltAModifier);
		setEaeCourant(eaeCourant);

		initialiseEae();
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<EAE> getListeEae() {
		if (listeEae == null)
			return new ArrayList<EAE>();
		return listeEae;
	}

	public void setListeEae(ArrayList<EAE> listeEae) {
		this.listeEae = listeEae;
	}

	public EAE getEaeCourant() {
		return eaeCourant;
	}

	public void setEaeCourant(EAE eaeCourant) {
		this.eaeCourant = eaeCourant;
	}

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public CampagneEAEDao getCampagneEaeDao() {
		return campagneEaeDao;
	}

	public void setCampagneEaeDao(CampagneEAEDao campagneEaeDao) {
		this.campagneEaeDao = campagneEaeDao;
	}

	public EaeEvaluateurDao getEaeEvaluateurDao() {
		return eaeEvaluateurDao;
	}

	public void setEaeEvaluateurDao(EaeEvaluateurDao eaeEvaluateurDao) {
		this.eaeEvaluateurDao = eaeEvaluateurDao;
	}

	public EaeFichePosteDao getEaeFichePosteDao() {
		return eaeFichePosteDao;
	}

	public void setEaeFichePosteDao(EaeFichePosteDao eaeFichePosteDao) {
		this.eaeFichePosteDao = eaeFichePosteDao;
	}

	public EaeEvalueDao getEaeEvalueDao() {
		return eaeEvalueDao;
	}

	public void setEaeEvalueDao(EaeEvalueDao eaeEvalueDao) {
		this.eaeEvalueDao = eaeEvalueDao;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_PERMIS Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		// addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ENTRETIEN Date
	 * de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_DATE_ENTRETIEN() {
		return "NOM_ST_DATE_ENTRETIEN";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
	 * Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_DATE_ENTRETIEN() {
		return getZone(getNOM_ST_DATE_ENTRETIEN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR_NOM Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR_NOM(int i) {
		return "NOM_ST_EVALUATEUR_NOM" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_EVALUATEUR_NOM
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_NOM(int i) {
		return getZone(getNOM_ST_EVALUATEUR_NOM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR_FONCTION
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR_FONCTION(int i) {
		return "NOM_ST_EVALUATEUR_FONCTION" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_EVALUATEUR_FONCTION Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_FONCTION(int i) {
		return getZone(getNOM_ST_EVALUATEUR_FONCTION(i));
	}

	public ArrayList<EaeEvaluateur> getListeEvaluateurEae() {
		return listeEvaluateurEae == null ? new ArrayList<EaeEvaluateur>() : listeEvaluateurEae;
	}

	public void setListeEvaluateurEae(ArrayList<EaeEvaluateur> listeEvaluateurEae) {
		this.listeEvaluateurEae = listeEvaluateurEae;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_SERVICE Date
	 * de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NIVEAU Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_NIVEAU() {
		return "NOM_ST_NIVEAU";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NIVEAU Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NIVEAU() {
		return getZone(getNOM_ST_NIVEAU());
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOTE Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_NOTE() {
		return "NOM_ST_NOTE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOTE Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NOTE() {
		return getZone(getNOM_ST_NOTE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_COMMENTAIRE_EVALUATEUR Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE_EVALUATEUR() {
		return "NOM_ST_COMMENTAIRE_EVALUATEUR";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_COMMENTAIRE_EVALUATEUR Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE_EVALUATEUR() {
		return getZone(getNOM_ST_COMMENTAIRE_EVALUATEUR());
	}

	public EaeCommentaireDao getEaeCommentaireDao() {
		return eaeCommentaireDao;
	}

	public void setEaeCommentaireDao(EaeCommentaireDao eaeCommentaireDao) {
		this.eaeCommentaireDao = eaeCommentaireDao;
	}

	public String getNOM_PB_VISUALISER_DOC(int i) {
		return "NOM_PB_VISUALISER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VISUALISER_DOC(HttpServletRequest request, int indiceEltAVisualiser) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("URL_SHAREPOINT_GED");

		// R�cup de l'EAE courant
		EAE eae = (EAE) getListeEae().get(indiceEltAVisualiser);
		String finalisation = getEaeFinalisationDao().chercherDernierDocumentFinalise(eae.getIdEAE());
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + finalisation));

		return true;
	}

	public EaeFinalisationDao getEaeFinalisationDao() {
		return eaeFinalisationDao;
	}

	public void setEaeFinalisationDao(EaeFinalisationDao eaeFinalisationDao) {
		this.eaeFinalisationDao = eaeFinalisationDao;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
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

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_SHD Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVIS_SHD() {
		return "NOM_ST_AVIS_SHD";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AVIS_SHD Date
	 * de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVIS_SHD() {
		return getZone(getNOM_ST_AVIS_SHD());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVCT_DIFF Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVCT_DIFF() {
		return "NOM_ST_AVCT_DIFF";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AVCT_DIFF Date
	 * de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVCT_DIFF() {
		return getZone(getNOM_ST_AVCT_DIFF());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_CLASSE
	 * Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_CLASSE() {
		return "NOM_ST_CHANGEMENT_CLASSE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_CLASSE Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_CLASSE() {
		return getZone(getNOM_ST_CHANGEMENT_CLASSE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_REVALO Date de
	 * cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVIS_REVALO() {
		return "NOM_ST_AVIS_REVALO";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AVIS_REVALO
	 * Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVIS_REVALO() {
		return getZone(getNOM_ST_AVIS_REVALO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RAPPORT_CIRCON Date
	 * de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_RAPPORT_CIRCON() {
		return "NOM_ST_RAPPORT_CIRCON";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RAPPORT_CIRCON
	 * Date de cr�ation : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_RAPPORT_CIRCON() {
		return getZone(getNOM_ST_RAPPORT_CIRCON());
	}

	public EaePlanActionDao getEaePlanActionDao() {
		return eaePlanActionDao;
	}

	public void setEaePlanActionDao(EaePlanActionDao eaePlanActionDao) {
		this.eaePlanActionDao = eaePlanActionDao;
	}

	public ArrayList<EaePlanAction> getListeObjectifPro() {
		return listeObjectifPro == null ? new ArrayList<EaePlanAction>() : listeObjectifPro;
	}

	public void setListeObjectifPro(ArrayList<EaePlanAction> listeObjectifPro) {
		this.listeObjectifPro = listeObjectifPro;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_PRO Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_PRO(int i) {
		return "NOM_ST_LIB_OBJ_PRO" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB_OBJ_PRO
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_PRO(int i) {
		return getZone(getNOM_ST_LIB_OBJ_PRO(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_MESURE_PRO Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_MESURE_PRO(int i) {
		return "NOM_ST_LIB_MESURE_PRO" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB_MESURE_PRO
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_MESURE_PRO(int i) {
		return getZone(getNOM_ST_LIB_MESURE_PRO(i));
	}

	public ArrayList<EaePlanAction> getListeObjectifIndi() {
		return listeObjectifIndi == null ? new ArrayList<EaePlanAction>() : listeObjectifIndi;
	}

	public void setListeObjectifIndi(ArrayList<EaePlanAction> listeObjectifIndi) {
		this.listeObjectifIndi = listeObjectifIndi;
	}

	public ArrayList<EaeDeveloppement> getListeDeveloppement() {
		return listeDeveloppement == null ? new ArrayList<EaeDeveloppement>() : listeDeveloppement;
	}

	public void setListeDeveloppement(ArrayList<EaeDeveloppement> listeDeveloppement) {
		this.listeDeveloppement = listeDeveloppement;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_INDI Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_INDI(int i) {
		return "NOM_ST_LIB_OBJ_INDI" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB_OBJ_INDI
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_INDI(int i) {
		return getZone(getNOM_ST_LIB_OBJ_INDI(i));
	}

	public EaeEvolutionDao getEaeEvolutionDao() {
		return eaeEvolutionDao;
	}

	public void setEaeEvolutionDao(EaeEvolutionDao eaeEvolutionDao) {
		this.eaeEvolutionDao = eaeEvolutionDao;
	}

	public EaeDeveloppementDao getEaeDeveloppementDao() {
		return eaeDeveloppementDao;
	}

	public void setEaeDeveloppementDao(EaeDeveloppementDao eaeDeveloppementDao) {
		this.eaeDeveloppementDao = eaeDeveloppementDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_GEO Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_GEO() {
		return "NOM_ST_MOB_GEO";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOB_GEO Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_GEO() {
		return getZone(getNOM_ST_MOB_GEO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_FONCT Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_FONCT() {
		return "NOM_ST_MOB_FONCT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOB_FONCT Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_FONCT() {
		return getZone(getNOM_ST_MOB_FONCT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_METIER
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_METIER() {
		return "NOM_ST_CHANGEMENT_METIER";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_METIER Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_METIER() {
		return getZone(getNOM_ST_CHANGEMENT_METIER());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DELAI Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DELAI() {
		return "NOM_ST_DELAI";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DELAI Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DELAI() {
		return getZone(getNOM_ST_DELAI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_SERV Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_SERV() {
		return "NOM_ST_MOB_SERV";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOB_SERV Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_SERV() {
		return getZone(getNOM_ST_MOB_SERV());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_DIR Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_DIR() {
		return "NOM_ST_MOB_DIR";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOB_DIR Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_DIR() {
		return getZone(getNOM_ST_MOB_DIR());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_COLL Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_COLL() {
		return "NOM_ST_MOB_COLL";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOB_COLL Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_COLL() {
		return getZone(getNOM_ST_MOB_COLL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_COLL Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_COLL() {
		return "NOM_ST_NOM_COLL";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM_COLL Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_COLL() {
		return getZone(getNOM_ST_NOM_COLL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_AUTRE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_AUTRE() {
		return "NOM_ST_MOB_AUTRE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOB_AUTRE Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_AUTRE() {
		return getZone(getNOM_ST_MOB_AUTRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CONCOURS Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CONCOURS() {
		return "NOM_ST_CONCOURS";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CONCOURS Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CONCOURS() {
		return getZone(getNOM_ST_CONCOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_CONCOURS Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_CONCOURS() {
		return "NOM_ST_NOM_CONCOURS";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM_CONCOURS
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_CONCOURS() {
		return getZone(getNOM_ST_NOM_CONCOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_VAE Date de cr�ation
	 * : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_VAE() {
		return "NOM_ST_VAE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_VAE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_VAE() {
		return getZone(getNOM_ST_VAE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_VAE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_VAE() {
		return "NOM_ST_NOM_VAE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM_VAE Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_VAE() {
		return getZone(getNOM_ST_NOM_VAE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_PARTIEL Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_TPS_PARTIEL() {
		return "NOM_ST_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TPS_PARTIEL
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_TPS_PARTIEL() {
		return getZone(getNOM_ST_TPS_PARTIEL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POURC_TPS_PARTIEL
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_POURC_TPS_PARTIEL() {
		return "NOM_ST_POURC_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_POURC_TPS_PARTIEL Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_POURC_TPS_PARTIEL() {
		return getZone(getNOM_ST_POURC_TPS_PARTIEL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RETRAITE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_RETRAITE() {
		return "NOM_ST_RETRAITE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RETRAITE Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_RETRAITE() {
		return getZone(getNOM_ST_RETRAITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_RETRAITE Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_RETRAITE() {
		return "NOM_ST_DATE_RETRAITE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_RETRAITE
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_RETRAITE() {
		return getZone(getNOM_ST_DATE_RETRAITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AUTRE_PERSP Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_AUTRE_PERSP() {
		return "NOM_ST_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AUTRE_PERSP
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_AUTRE_PERSP() {
		return getZone(getNOM_ST_AUTRE_PERSP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AUTRE_PERSP Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_AUTRE_PERSP() {
		return "NOM_ST_LIB_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LIB_AUTRE_PERSP Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_AUTRE_PERSP() {
		return getZone(getNOM_ST_LIB_AUTRE_PERSP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COM_EVOLUTION Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_COM_EVOLUTION() {
		return "NOM_ST_COM_EVOLUTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_COM_EVOLUTION
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_COM_EVOLUTION() {
		return getZone(getNOM_ST_COM_EVOLUTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_DEV Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_TYPE_DEV(int i) {
		return "NOM_ST_TYPE_DEV" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TYPE_DEV Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_TYPE_DEV(int i) {
		return getZone(getNOM_ST_TYPE_DEV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_DEV Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_DEV(int i) {
		return "NOM_ST_LIB_DEV" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB_DEV Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_DEV(int i) {
		return getZone(getNOM_ST_LIB_DEV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ECHEANCE_DEV Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ECHEANCE_DEV(int i) {
		return "NOM_ST_ECHEANCE_DEV" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ECHEANCE_DEV
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ECHEANCE_DEV(int i) {
		return getZone(getNOM_ST_ECHEANCE_DEV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PRIORISATION_DEV
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_PRIORISATION_DEV(int i) {
		return "NOM_ST_PRIORISATION_DEV" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_PRIORISATION_DEV Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_PRIORISATION_DEV(int i) {
		return getZone(getNOM_ST_PRIORISATION_DEV(i));
	}

	public boolean isCampagneOuverte(Integer idCampagneEAE) throws Exception {
		return getCampagneEaeDao().chercherCampagneEAE(idCampagneEAE).estOuverte();
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_NIVEAU
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_NIVEAU() {
		return "NOM_RG_NIVEAU";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_NIVEAU
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_NIVEAU() {
		return getZone(getNOM_RG_NIVEAU());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_EXCEL Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_EXCEL() {
		return "NOM_RB_NIVEAU_EXCEL";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_SATIS Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_SATIS() {
		return "NOM_RB_NIVEAU_SATIS";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_PROGR Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_PROGR() {
		return "NOM_RB_NIVEAU_PROGR";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_INSU Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_INSU() {
		return "NOM_RB_NIVEAU_INSU";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_REVA
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_REVA() {
		return "NOM_RG_REVA";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_REVA Date
	 * de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_REVA() {
		return getZone(getNOM_RG_REVA());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_REVA_FAV Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_REVA_FAV() {
		return "NOM_RB_REVA_FAV";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_REVA_DEF Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_REVA_DEF() {
		return "NOM_RB_REVA_DEF";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_CHGT
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_CHGT() {
		return "NOM_RG_CHGT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_CHGT Date
	 * de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_CHGT() {
		return getZone(getNOM_RG_CHGT());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CHGT_FAV Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CHGT_FAV() {
		return "NOM_RB_CHGT_FAV";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CHGT_DEF Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CHGT_DEF() {
		return "NOM_RB_CHGT_DEF";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_AD Date
	 * de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_AD() {
		return "NOM_RG_AD";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_AD Date
	 * de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_AD() {
		return getZone(getNOM_RG_AD());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AD_MIN Date de cr�ation
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AD_MIN() {
		return "NOM_RB_AD_MIN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AD_MOY Date de cr�ation
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AD_MOY() {
		return "NOM_RB_AD_MOY";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AD_MAX Date de cr�ation
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AD_MAX() {
		return "NOM_RB_AD_MAX";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			// v�rification de la validit� du formulaire
			if (!performControlerChamps(request))
				return false;

			EAE eae = getEaeCourant();
			if (eae != null && eae.getIdEAE() != null) {
				performSauvegardeEvaluation(request, eae);

				/************* PARTIE PLAN ACTION **********************/

				performSauvegardeEvolution(request, eae);

			} else {
				// TODO on traite l'erreur
			}

		}
		return true;
	}

	private void performSauvegardeEvolution(HttpServletRequest request, EAE eae) throws Exception {
		/************* PARTIE EVOLUTION **********************/
		EaeEvolution evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEAE());
		// Mobilit�s
		String mobGeo = getVAL_RG_MOB_GEO();
		if (mobGeo.equals(getNOM_RB_MOB_GEO_NON())) {
			evolution.setMobiliteGeo(false);
		} else {
			evolution.setMobiliteGeo(true);
		}
		String mobFonct = getVAL_RG_MOB_FONCT();
		if (mobFonct.equals(getNOM_RB_MOB_FONCT_NON())) {
			evolution.setMobiliteFonct(false);
		} else {
			evolution.setMobiliteFonct(true);
		}
		String mobServ = getVAL_RG_MOB_SERV();
		if (mobServ.equals(getNOM_RB_MOB_SERV_NON())) {
			evolution.setMobiliteService(false);
		} else {
			evolution.setMobiliteService(true);
		}
		String mobDir = getVAL_RG_MOB_DIR();
		if (mobDir.equals(getNOM_RB_MOB_DIR_NON())) {
			evolution.setMobiliteDirection(false);
		} else {
			evolution.setMobiliteDirection(true);
		}
		String mobColl = getVAL_RG_MOB_COLL();
		if (mobColl.equals(getNOM_RB_MOB_COLL_NON())) {
			evolution.setMobiliteCollectivite(false);
		} else {
			evolution.setMobiliteCollectivite(true);
		}
		String mobAutre = getVAL_RG_MOB_AUTRE();
		if (mobAutre.equals(getNOM_RB_MOB_AUTRE_NON())) {
			evolution.setMobiliteAutre(false);
		} else {
			evolution.setMobiliteAutre(true);
		}
		getEaeEvolutionDao().modifierMobiliteEaeEvolution(evolution.getIdEaeEvolution(), evolution.isMobiliteGeo(), evolution.isMobiliteFonct(),
				evolution.isMobiliteService(), evolution.isMobiliteDirection(), evolution.isMobiliteCollectivite(), evolution.isMobiliteAutre());

		// Changement de metier
		String metier = getVAL_RG_METIER();
		if (metier.equals(getNOM_RB_METIER_NON())) {
			evolution.setChangementMetier(false);
		} else {
			evolution.setChangementMetier(true);
		}
		getEaeEvolutionDao().modifierChangementMetierEaeEvolution(evolution.getIdEaeEvolution(), evolution.isChangementMetier());

		// Delai
		String delai = getVAL_RG_DELAI();
		if (delai.equals(getNOM_RB_DELAI_1())) {
			evolution.setDelaiEnvisage("MOINS1AN");
		} else if (delai.equals(getNOM_RB_DELAI_2())) {
			evolution.setDelaiEnvisage("ENTRE1ET2ANS");
		} else {
			evolution.setDelaiEnvisage("ENTRE2ET4ANS");
		}
		getEaeEvolutionDao().modifierDelaiEaeEvolution(evolution.getIdEaeEvolution(), evolution.getDelaiEnvisage());

		// concours
		String concours = getVAL_RG_CONCOURS();
		if (concours.equals(getNOM_RB_CONCOURS_NON())) {
			evolution.setConcours(false);
		} else {
			evolution.setConcours(true);
		}
		// vae
		String vae = getVAL_RG_VAE();
		if (vae.equals(getNOM_RB_VAE_NON())) {
			evolution.setVae(false);
		} else {
			evolution.setVae(true);
		}
		// temps partiel
		String tpsPartiel = getVAL_RG_TPS_PARTIEL();
		if (tpsPartiel.equals(getNOM_RB_TPS_PARTIEL_NON())) {
			evolution.setTempsPartiel(false);
		} else {
			evolution.setTempsPartiel(true);
		}
		// retraite
		String retraite = getVAL_RG_RETRAITE();
		if (retraite.equals(getNOM_RB_RETRAITE_NON())) {
			evolution.setRetraite(false);
		} else {
			evolution.setRetraite(true);
		}
		// autres persp
		String autrePersp = getVAL_RG_AUTRE_PERSP();
		if (autrePersp.equals(getNOM_RB_AUTRE_PERSP_NON())) {
			evolution.setAutrePerspective(false);
		} else {
			evolution.setAutrePerspective(true);
		}
		getEaeEvolutionDao().modifierAutresInfosEaeEvolution(evolution.getIdEaeEvolution(), evolution.isConcours(), evolution.isVae(),
				evolution.isTempsPartiel(), evolution.isRetraite(), evolution.isAutrePerspective());

		// pour les libelles
		// collectivite
		String nomColl = getVAL_ST_NOM_COLL().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NOM_COLL();
		evolution.setNomCollectivite(nomColl);
		// concours
		String nomConcours = getVAL_ST_NOM_CONCOURS().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NOM_CONCOURS();
		evolution.setNomConcours(nomConcours);
		// vae
		String nomVae = getVAL_ST_NOM_VAE().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NOM_VAE();
		evolution.setNomVae(nomVae);
		// autre persp
		String nomAutrePersp = getVAL_ST_LIB_AUTRE_PERSP().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_LIB_AUTRE_PERSP();
		evolution.setLibAutrePerspective(nomAutrePersp);
		getEaeEvolutionDao().modifierLibelleEaeEvolution(evolution.getIdEaeEvolution(), evolution.getNomCollectivite(), evolution.getNomConcours(),
				evolution.getNomVae(), evolution.getLibAutrePerspective());

		// date de la retraite
		String dateRetraire = getVAL_ST_DATE_RETRAITE().equals(Const.CHAINE_VIDE) ? null : Services.formateDate(getVAL_ST_DATE_RETRAITE());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		evolution.setDateRetraite(dateRetraire != null ? sdf.parse(dateRetraire) : null);
		getEaeEvolutionDao().modifierDateRetraiteEaeEvolution(evolution.getIdEaeEvolution(), evolution.getDateRetraite());

		// commentaire de l'evolution
		if (evolution.getIdComEvolution() != null && evolution.getIdComEvolution() != 0) {
			EaeCommentaire commEvolution = getEaeCommentaireDao().chercherEaeCommentaire(evolution.getIdComEvolution());
			commEvolution.setCommentaire(getVAL_ST_COM_EVOLUTION());
			getEaeCommentaireDao().modifierEaeCommentaire(commEvolution.getIdEaeCommenatire(), commEvolution.getCommentaire());
		} else {
			if (!getVAL_ST_COM_EVOLUTION().equals(Const.CHAINE_VIDE)) {
				EaeCommentaire comm = new EaeCommentaire();
				comm.setCommentaire(getVAL_ST_COM_EVOLUTION());
				Integer idCree = getEaeCommentaireDao().creerEaeCommentaire(comm.getCommentaire());
				getEaeEvolutionDao().modifierCommentaireEaeEvaluation(evolution.getIdEaeEvolution(), idCree);
			}
		}

		// pourcentage temps partiel
		int numLigneBH = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) : -1);
		Horaire horaire = numLigneBH > 0 ? (Horaire) getListeHoraire().get(numLigneBH - 1) : null;
		evolution.setIdSpbhorTpsPartiel(horaire == null ? null : Integer.valueOf(horaire.getCdtHor()));
		getEaeEvolutionDao().modifierPourcTpsPartielEaeEvolution(evolution.getIdEaeEvolution(), evolution.getIdSpbhorTpsPartiel());
		// TODO
	}

	private void performSauvegardeEvaluation(HttpServletRequest request, EAE eae) throws Exception {
		/************* PARTIE EVALUATION **********************/
		EaeEvaluation eval = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEAE());
		// commentaire de l'evaluateur
		if (eval.getIdCommEvaluateur() != null && eval.getIdCommEvaluateur() != 0) {
			EaeCommentaire commEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(eval.getIdCommEvaluateur());
			commEvaluateur.setCommentaire(getVAL_ST_COMMENTAIRE_EVALUATEUR());
			getEaeCommentaireDao().modifierEaeCommentaire(commEvaluateur.getIdEaeCommenatire(), commEvaluateur.getCommentaire());
		} else {
			if (!getVAL_ST_COMMENTAIRE_EVALUATEUR().equals(Const.CHAINE_VIDE)) {
				EaeCommentaire comm = new EaeCommentaire();
				comm.setCommentaire(getVAL_ST_COMMENTAIRE_EVALUATEUR());
				Integer idCree = getEaeCommentaireDao().creerEaeCommentaire(comm.getCommentaire());
				getEaeEvaluationDao().modifierCommentaireEvaluateurEaeEvaluation(eval.getIdEaeEvaluation(), idCree);
			}
		}

		// Niveau
		String niveau = getVAL_RG_NIVEAU();
		if (niveau.equals(getNOM_RB_NIVEAU_EXCEL())) {
			eval.setNiveau("EXCELLENT");
		} else if (niveau.equals(getNOM_RB_NIVEAU_SATIS())) {
			eval.setNiveau("SATISFAISANT");
		} else if (niveau.equals(getNOM_RB_NIVEAU_PROGR())) {
			eval.setNiveau("NECESSITANT_DES_PROGRES");
		} else {
			eval.setNiveau("INSUFFISANT");
		}
		getEaeEvaluationDao().modifierNiveauEaeEvaluation(eval.getIdEaeEvaluation(), eval.getNiveau());

		// note
		Float note = Float.parseFloat(getVAL_ST_NOTE().replace(',', '.'));
		eval.setNoteAnnee(note.doubleValue());
		getEaeEvaluationDao().modifierNoteEaeEvaluation(eval.getIdEaeEvaluation(), eval.getNoteAnnee());

		// Avancement Diff
		String ad = getVAL_RG_AD();
		if (ad.equals(getNOM_RB_AD_MIN())) {
			eval.setPropositionAvancement("MINI");
		} else if (ad.equals(getNOM_RB_AD_MAX())) {
			eval.setPropositionAvancement("MAXI");
		} else {
			eval.setPropositionAvancement("MOY");
		}
		getEaeEvaluationDao().modifierADEaeEvaluation(eval.getIdEaeEvaluation(), eval.getPropositionAvancement());

		// Changement classe
		String chgt = getVAL_RG_CHGT();
		if (chgt.equals(getNOM_RB_CHGT_DEF())) {
			eval.setAvisChangementClasse(0);
		} else {
			eval.setAvisChangementClasse(1);
		}
		getEaeEvaluationDao().modifierChgtClasseEaeEvaluation(eval.getIdEaeEvaluation(), eval.getAvisChangementClasse());

		// Revalorisation
		String reva = getVAL_RG_REVA();
		if (reva.equals(getNOM_RB_REVA_DEF())) {
			eval.setAvisRevalorisation(0);
		} else {
			eval.setAvisRevalorisation(1);
		}
		getEaeEvaluationDao().modifierRevaloEaeEvaluation(eval.getIdEaeEvaluation(), eval.getAvisRevalorisation());

		// rapport circonstanci� de l'evaluateur
		if (eval.getIdCommAvctEvaluateur() != null && eval.getIdCommAvctEvaluateur() != 0) {
			EaeCommentaire commAvctEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(eval.getIdCommAvctEvaluateur());
			commAvctEvaluateur.setCommentaire(getVAL_ST_RAPPORT_CIRCON());
			getEaeCommentaireDao().modifierEaeCommentaire(commAvctEvaluateur.getIdEaeCommenatire(), commAvctEvaluateur.getCommentaire());
		} else {
			if (!getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
				EaeCommentaire comm = new EaeCommentaire();
				comm.setCommentaire(getVAL_ST_RAPPORT_CIRCON());
				Integer idCree = getEaeCommentaireDao().creerEaeCommentaire(comm.getCommentaire());
				getEaeEvaluationDao().modifierRapportCirconstancieEaeEvaluation(eval.getIdEaeEvaluation(), idCree);
			}
		}

	}

	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// ********************************************
		// ///////////////////NOTE/////////////////////
		// ********************************************
		if (!Services.estFloat(getVAL_ST_NOTE())) {
			// "ERR992", "La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "note"));
			return false;
		}
		Float note = Float.parseFloat(getVAL_ST_NOTE().replace(',', '.'));
		if (getVAL_ST_NOTE().equals(Const.CHAINE_VIDE) || note == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "note"));
			return false;
		}
		// si la note n'est pas comprise entre 0 et 20
		if (0 > note || 20 < note) {
			// "ERR160", "La note doit �tre comprise entre 0 et 20.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR160"));
			return false;

		}
		// ********************************************
		// /////////RAPPORT CIRCONSTANCIE//////////////
		// ********************************************
		// si min ou max alors rapport circonstanci� obligatoire
		if ((getVAL_RG_AD().equals(getNOM_RB_AD_MIN()) || getVAL_RG_AD().equals(getNOM_RB_AD_MAX()))
				&& getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			// "ERR162",
			// "Le contenu du rapport circonstanci� ne doit pas �tre vide pour une dur�e d'avancement minimale ou maximale.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR162"));
			return false;
		}
		if (getVAL_RG_AD().equals(getNOM_RB_AD_MOY()) && !getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			// "ERR163",
			// "Le contenu du rapport circonstanci� ne doit pas �tre rempli pour une dur�e d'avancement moyenne.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR163"));
			return false;
		}
		// ********************************************
		// /////////DATE RETRAITE//////////////
		// ********************************************
		// format date de retraite
		if (!getVAL_ST_DATE_RETRAITE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_RETRAITE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit �tre au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de retraite"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_MOB_GEO
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_GEO() {
		return "NOM_RG_MOB_GEO";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_MOB_GEO
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_GEO() {
		return getZone(getNOM_RG_MOB_GEO());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_GEO_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_GEO_OUI() {
		return "NOM_RB_MOB_GEO_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_GEO_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_GEO_NON() {
		return "NOM_RB_MOB_GEO_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_MOB_FONCT Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_FONCT() {
		return "NOM_RG_MOB_FONCT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_MOB_FONCT
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_FONCT() {
		return getZone(getNOM_RG_MOB_FONCT());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_FONCT_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_FONCT_OUI() {
		return "NOM_RB_MOB_FONCT_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_FONCT_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_FONCT_NON() {
		return "NOM_RB_MOB_FONCT_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_MOB_SERV Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_SERV() {
		return "NOM_RG_MOB_SERV";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_MOB_SERV
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_SERV() {
		return getZone(getNOM_RG_MOB_SERV());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_SERV_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_SERV_OUI() {
		return "NOM_RB_MOB_SERV_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_SERV_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_SERV_NON() {
		return "NOM_RB_MOB_SERV_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_MOB_DIR
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_DIR() {
		return "NOM_RG_MOB_DIR";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_MOB_DIR
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_DIR() {
		return getZone(getNOM_RG_MOB_DIR());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_DIR_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_DIR_OUI() {
		return "NOM_RB_MOB_DIR_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_DIR_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_DIR_NON() {
		return "NOM_RB_MOB_DIR_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_MOB_COLL Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_COLL() {
		return "NOM_RG_MOB_COLL";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_MOB_COLL
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_COLL() {
		return getZone(getNOM_RG_MOB_COLL());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_COLL_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_COLL_OUI() {
		return "NOM_RB_MOB_COLL_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_COLL_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_COLL_NON() {
		return "NOM_RB_MOB_COLL_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_MOB_AUTRE Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_AUTRE() {
		return "NOM_RG_MOB_AUTRE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_MOB_AUTRE
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_AUTRE() {
		return getZone(getNOM_RG_MOB_AUTRE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_AUTRE_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_AUTRE_OUI() {
		return "NOM_RB_MOB_AUTRE_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_AUTRE_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_AUTRE_NON() {
		return "NOM_RB_MOB_AUTRE_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_METIER
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_METIER() {
		return "NOM_RG_METIER";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_METIER
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_METIER() {
		return getZone(getNOM_RG_METIER());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_METIER_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_METIER_OUI() {
		return "NOM_RB_METIER_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_METIER_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_METIER_NON() {
		return "NOM_RB_METIER_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_DELAI
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_DELAI() {
		return "NOM_RG_DELAI";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_DELAI
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_DELAI() {
		return getZone(getNOM_RG_DELAI());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_DELAI_1 Date de cr�ation
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_DELAI_1() {
		return "NOM_RB_DELAI_1";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_DELAI_2 Date de cr�ation
	 * : (26/05/22 22:32:22)
	 * 
	 */
	public String getNOM_RB_DELAI_2() {
		return "NOM_RB_DELAI_2";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_DELAI_4 Date de cr�ation
	 * : (26/05/44 44:34:42)
	 * 
	 */
	public String getNOM_RB_DELAI_4() {
		return "NOM_RB_DELAI_4";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_CONCOURS Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_CONCOURS() {
		return "NOM_RG_CONCOURS";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_CONCOURS
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_CONCOURS() {
		return getZone(getNOM_RG_CONCOURS());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONCOURS_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CONCOURS_OUI() {
		return "NOM_RB_CONCOURS_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONCOURS_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CONCOURS_NON() {
		return "NOM_RB_CONCOURS_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP : RG_VAE
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_VAE() {
		return "NOM_RG_VAE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_VAE Date
	 * de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_VAE() {
		return getZone(getNOM_RG_VAE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VAE_OUI Date de cr�ation
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_VAE_OUI() {
		return "NOM_RB_VAE_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VAE_NON Date de cr�ation
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_VAE_NON() {
		return "NOM_RB_VAE_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_TPS_PARTIEL Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_TPS_PARTIEL() {
		return "NOM_RG_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_TPS_PARTIEL Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_TPS_PARTIEL() {
		return getZone(getNOM_RG_TPS_PARTIEL());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TPS_PARTIEL_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_TPS_PARTIEL_OUI() {
		return "NOM_RB_TPS_PARTIEL_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TPS_PARTIEL_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_TPS_PARTIEL_NON() {
		return "NOM_RB_TPS_PARTIEL_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_AUTRE_PERSP Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_AUTRE_PERSP() {
		return "NOM_RG_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_AUTRE_PERSP Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_AUTRE_PERSP() {
		return getZone(getNOM_RG_AUTRE_PERSP());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AUTRE_PERSP_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AUTRE_PERSP_OUI() {
		return "NOM_RB_AUTRE_PERSP_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AUTRE_PERSP_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AUTRE_PERSP_NON() {
		return "NOM_RB_AUTRE_PERSP_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_RETRAITE Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_RETRAITE() {
		return "NOM_RG_RETRAITE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP : RG_RETRAITE
	 * Date de cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_RETRAITE() {
		return getZone(getNOM_RG_RETRAITE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RETRAITE_OUI Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_RETRAITE_OUI() {
		return "NOM_RB_RETRAITE_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RETRAITE_NON Date de
	 * cr�ation : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_RETRAITE_NON() {
		return "NOM_RB_RETRAITE_NON";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BASE_HORAIRE Date de
	 * cr�ation : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_BASE_HORAIRE() {
		if (LB_BASE_HORAIRE == null)
			LB_BASE_HORAIRE = initialiseLazyLB();
		return LB_BASE_HORAIRE;
	}

	/**
	 * Setter de la liste: LB_BASE_HORAIRE Date de cr�ation : (05/09/11
	 * 14:28:25)
	 * 
	 */
	private void setLB_BASE_HORAIRE(String[] newLB_BASE_HORAIRE) {
		LB_BASE_HORAIRE = newLB_BASE_HORAIRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BASE_HORAIRE Date de
	 * cr�ation : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_HORAIRE() {
		return "NOM_LB_BASE_HORAIRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_BASE_HORAIRE_SELECT Date de cr�ation : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_HORAIRE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_BASE_HORAIRE Date de cr�ation : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_BASE_HORAIRE() {
		return getLB_BASE_HORAIRE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_BASE_HORAIRE Date de cr�ation : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_BASE_HORAIRE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_SELECT());
	}

	private ArrayList<Horaire> getListeHoraire() {
		return listeHoraire;
	}

	private void setListeHoraire(ArrayList<Horaire> listeHoraire) {
		this.listeHoraire = listeHoraire;
	}

	private Hashtable<String, Horaire> getHashHoraire() {
		if (hashHoraire == null)
			hashHoraire = new Hashtable<String, Horaire>();
		return hashHoraire;
	}
}
