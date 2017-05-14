package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.RefTypeDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import org.springframework.context.ApplicationContext;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.metier.hsct.TypeInaptitude;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.hsct.HandicapDao;
import nc.mairie.spring.dao.metier.hsct.InaptitudeDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.RecommandationDao;
import nc.mairie.spring.dao.metier.hsct.TypeInaptitudeDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IRadiService;

/**
 * Process OePARAMETRAGEHSCT Date de création : (15/09/11 08:57:49)
 * 
 */
public class OePARAMETRAGEHSCT extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_AT;
	private String[] LB_INAPTITUDE;
	private String[] LB_LESION;
	private String[] LB_MALADIE;
	private String[] LB_MEDECIN;
	private String[] LB_RECOMMANDATION;

	private ArrayList<Medecin> listeMedecin;
	private Medecin medecinCourant;

	private ArrayList<Recommandation> listeRecommandation;
	private Recommandation recommandationCourante;

	private ArrayList<TypeInaptitude> listeInaptitude;
	private TypeInaptitude inaptitudeCourante;

	private ArrayList<RefTypeDto> listeAT;
	private RefTypeDto atCourant;

	private ArrayList<RefTypeDto> listeLesion;
	private RefTypeDto lesionCourant;

	private ArrayList<RefTypeDto> listeMaladie;
	private RefTypeDto maladieCourante;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";

	private HandicapDao handicapDao;
	private MedecinDao medecinDao;
	private RecommandationDao recommandationDao;
	private TypeInaptitudeDao typeInaptitudeDao;
	private VisiteMedicaleDao visiteMedicaleDao;
	private InaptitudeDao inaptitudeDao;

	private IAbsService absService;

	private AgentDao agentDao;

	private IRadiService radiService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//

		if (getListeMedecin() == null) {
			// Recherche des médecins
			ArrayList<Medecin> listeMedecin = getMedecinDao().listerMedecin();
			setListeMedecin(listeMedecin);
			initialiseListeMedecin(request);
		}

		if (getListeRecommandation() == null) {
			// Recherche des recommandations
			ArrayList<Recommandation> listeRecommandation = getRecommandationDao().listerRecommandation();
			setListeRecommandation(listeRecommandation);
			initialiseListeRecommandation(request);
		}

		if (getListeInaptitude() == null) {
			// Recherche des types d'inaptitude
			ArrayList<TypeInaptitude> listeInaptitude = getTypeInaptitudeDao().listerTypeInaptitude();
			setListeInaptitude(listeInaptitude);
			initialiseListeInaptitude(request);
		}

		if (getListeAT() == null) {
			// Recherche des types d'AT
			ArrayList<RefTypeDto> listeAT = (ArrayList<RefTypeDto>) getAbsService().getRefTypeAccidentTravail();
			setListeAT(listeAT);
			initialiseListeAT(request);
		}

		if (getListeLesion() == null) {
			// Recherche des des sieges de lésions
			ArrayList<RefTypeDto> listeLesion = (ArrayList<RefTypeDto>) getAbsService().getRefTypeSiegeLesion();
			setListeLesion(listeLesion);
			initialiseListeLesion(request);
		}

		if (getListeMaladie() == null) {
			// Recherche des des maladies professionnelles
			ArrayList<RefTypeDto> listeMaladie = (ArrayList<RefTypeDto>) getAbsService().getRefTypeMaladiePro();
			setListeMaladie(listeMaladie);
			initialiseListeMaladie(request);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (null == getAbsService()) {
			setAbsService((AbsService) context.getBean("absService"));
		}
		if (getHandicapDao() == null) {
			setHandicapDao(new HandicapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRecommandationDao() == null) {
			setRecommandationDao(new RecommandationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeInaptitudeDao() == null) {
			setTypeInaptitudeDao(new TypeInaptitudeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getVisiteMedicaleDao() == null) {
			setVisiteMedicaleDao(new VisiteMedicaleDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getInaptitudeDao() == null) {
			setInaptitudeDao(new InaptitudeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == getRadiService()) {
			setRadiService((IRadiService) context.getBean("radiService"));
		}
	}

	/**
	 * Initialisation de la listes des médecins Date de création : (15/09/11)
	 * 
	 */
	private void initialiseListeMedecin(HttpServletRequest request) throws Exception {
		setListeMedecin(getMedecinDao().listerMedecin());
		if (getListeMedecin().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Medecin> list = getListeMedecin().listIterator(); list.hasNext();) {
				Medecin m = (Medecin) list.next();
				String ligne[] = { m.getTitreMedecin() + " " + m.getPrenomMedecin() + " " + m.getNomMedecin() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MEDECIN(aFormat.getListeFormatee());
		} else {
			setLB_MEDECIN(null);
		}
	}

	/**
	 * Initialisation de la listes des recommandations Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeRecommandation(HttpServletRequest request) throws Exception {
		setListeRecommandation(getRecommandationDao().listerRecommandation());
		if (getListeRecommandation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Recommandation> list = getListeRecommandation().listIterator(); list.hasNext();) {
				Recommandation r = (Recommandation) list.next();
				String ligne[] = { r.getDescRecommandation() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_RECOMMANDATION(aFormat.getListeFormatee());
		} else {
			setLB_RECOMMANDATION(null);
		}
	}

	/**
	 * Initialisation de la listes des types d'inaptitude Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeInaptitude(HttpServletRequest request) throws Exception {
		setListeInaptitude(getTypeInaptitudeDao().listerTypeInaptitude());
		if (getListeInaptitude().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeInaptitude> list = getListeInaptitude().listIterator(); list.hasNext();) {
				TypeInaptitude ti = (TypeInaptitude) list.next();
				String ligne[] = { ti.getDescTypeInaptitude() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_INAPTITUDE(aFormat.getListeFormatee());
		} else {
			setLB_INAPTITUDE(null);
		}
	}

	/**
	 * Initialisation de la listes des types d'AT Date de création : (15/09/11)
	 * 
	 */
	private void initialiseListeAT(HttpServletRequest request) throws Exception {
		setListeAT((ArrayList<RefTypeDto>)getAbsService().getRefTypeAccidentTravail());
		if (getListeAT().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<RefTypeDto> list = getListeAT().listIterator(); list.hasNext();) {
				RefTypeDto td = (RefTypeDto) list.next();
				String ligne[] = { td.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_AT(aFormat.getListeFormatee());
		} else {
			setLB_AT(null);
		}
	}

	/**
	 * Initialisation de la listes des sieges de lésions Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeLesion(HttpServletRequest request) throws Exception {
		setListeLesion((ArrayList<RefTypeDto>)getAbsService().getRefTypeSiegeLesion());
		if (getListeLesion().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<RefTypeDto> list = getListeLesion().listIterator(); list.hasNext();) {
				RefTypeDto sl = (RefTypeDto) list.next();
				String ligne[] = { sl.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_LESION(aFormat.getListeFormatee());
		} else {
			setLB_LESION(null);
		}
	}

	/**
	 * Initialisation de la listes des maladies professionnelles Date de
	 * création : (15/09/11)
	 * 
	 */
	private void initialiseListeMaladie(HttpServletRequest request) throws Exception {
		setListeMaladie((ArrayList<RefTypeDto>)getAbsService().getRefTypeMaladiePro());
		if (getListeMaladie().size() != 0) {
			int tailles[] = { 20, 50 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<RefTypeDto> list = getListeMaladie().listIterator(); list.hasNext();) {
				RefTypeDto mp = (RefTypeDto) list.next();
				String ligne[] = { mp.getCode(), mp.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MALADIE(aFormat.getListeFormatee());
		} else {
			setLB_MALADIE(null);
		}
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_AT
			if (testerParametre(request, getNOM_PB_ANNULER_AT())) {
				return performPB_ANNULER_AT(request);
			}

			// Si clic sur le bouton PB_ANNULER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_ANNULER_INAPTITUDE())) {
				return performPB_ANNULER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_ANNULER_LESION
			if (testerParametre(request, getNOM_PB_ANNULER_LESION())) {
				return performPB_ANNULER_LESION(request);
			}

			// Si clic sur le bouton PB_ANNULER_MALADIE
			if (testerParametre(request, getNOM_PB_ANNULER_MALADIE())) {
				return performPB_ANNULER_MALADIE(request);
			}

			// Si clic sur le bouton PB_ANNULER_MEDECIN
			if (testerParametre(request, getNOM_PB_ANNULER_MEDECIN())) {
				return performPB_ANNULER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_ANNULER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_ANNULER_RECOMMANDATION())) {
				return performPB_ANNULER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_CREER_AT
			if (testerParametre(request, getNOM_PB_CREER_AT())) {
				return performPB_CREER_AT(request);
			}

			// Si clic sur le bouton PB_CREER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_CREER_INAPTITUDE())) {
				return performPB_CREER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_CREER_LESION
			if (testerParametre(request, getNOM_PB_CREER_LESION())) {
				return performPB_CREER_LESION(request);
			}

			// Si clic sur le bouton PB_CREER_MALADIE
			if (testerParametre(request, getNOM_PB_CREER_MALADIE())) {
				return performPB_CREER_MALADIE(request);
			}

			// Si clic sur le bouton PB_CREER_MEDECIN
			if (testerParametre(request, getNOM_PB_CREER_MEDECIN())) {
				return performPB_CREER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_CREER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_CREER_RECOMMANDATION())) {
				return performPB_CREER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_AT())) {
				return performPB_SUPPRIMER_AT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_INAPTITUDE())) {
				return performPB_SUPPRIMER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_LESION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_LESION())) {
				return performPB_SUPPRIMER_LESION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MALADIE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MALADIE())) {
				return performPB_SUPPRIMER_MALADIE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MEDECIN
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MEDECIN())) {
				return performPB_SUPPRIMER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECOMMANDATION())) {
				return performPB_SUPPRIMER_RECOMMANDATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_AT
			if (testerParametre(request, getNOM_PB_VALIDER_AT())) {
				return performPB_VALIDER_AT(request);
			}

			// Si clic sur le bouton PB_VALIDER_INAPTITUDE
			if (testerParametre(request, getNOM_PB_VALIDER_INAPTITUDE())) {
				return performPB_VALIDER_INAPTITUDE(request);
			}

			// Si clic sur le bouton PB_VALIDER_LESION
			if (testerParametre(request, getNOM_PB_VALIDER_LESION())) {
				return performPB_VALIDER_LESION(request);
			}

			// Si clic sur le bouton PB_VALIDER_MALADIE
			if (testerParametre(request, getNOM_PB_VALIDER_MALADIE())) {
				return performPB_VALIDER_MALADIE(request);
			}

			// Si clic sur le bouton PB_VALIDER_MEDECIN
			if (testerParametre(request, getNOM_PB_VALIDER_MEDECIN())) {
				return performPB_VALIDER_MEDECIN(request);
			}

			// Si clic sur le bouton PB_VALIDER_RECOMMANDATION
			if (testerParametre(request, getNOM_PB_VALIDER_RECOMMANDATION())) {
				return performPB_VALIDER_RECOMMANDATION(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePARAMETRAGEHSCT. Date de création : (15/09/11
	 * 08:57:49)
	 * 
	 */
	public OePARAMETRAGEHSCT() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEHSCT.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-AG-HSCT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_AT Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_AT() {
		return "NOM_PB_ANNULER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_AT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_AT(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_INAPTITUDE() {
		return "NOM_PB_ANNULER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_INAPTITUDE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_LESION() {
		return "NOM_PB_ANNULER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_LESION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_LESION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_MALADIE() {
		return "NOM_PB_ANNULER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_MALADIE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MALADIE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_MEDECIN() {
		return "NOM_PB_ANNULER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_MEDECIN(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MEDECIN(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_RECOMMANDATION Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_ANNULER_RECOMMANDATION() {
		return "NOM_PB_ANNULER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_ANNULER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_RECOMMANDATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_AT Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_AT() {
		return "NOM_PB_CREER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_AT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_AT(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_AT(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_INAPTITUDE() {
		return "NOM_PB_CREER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_INAPTITUDE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_INAPTITUDE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_LESION() {
		return "NOM_PB_CREER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_LESION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_LESION(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_LESION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_MALADIE() {
		return "NOM_PB_CREER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_MALADIE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MALADIE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_MALADIE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIBELLE_MALADIE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_MEDECIN() {
		return "NOM_PB_CREER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_MEDECIN(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MEDECIN(), ACTION_CREATION);
		addZone(getNOM_EF_NOM_MEDECIN(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_RECOMMANDATION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_CREER_RECOMMANDATION() {
		return "NOM_PB_CREER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_CREER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_RECOMMANDATION(), ACTION_CREATION);
		addZone(getNOM_EF_DESC_RECOMMANDATION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AT Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AT() {
		return "NOM_PB_SUPPRIMER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AT(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_AT_SELECT()) ? Integer.parseInt(getVAL_LB_AT_SELECT()) : -1);

		if (indice != -1 && indice < getListeAT().size()) {
			RefTypeDto at = getListeAT().get(indice);
			setAtCourant(at);
			addZone(getNOM_EF_DESC_AT(), at.getLibelle());
			addZone(getNOM_ST_ACTION_AT(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'AT"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_INAPTITUDE() {
		return "NOM_PB_SUPPRIMER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_INAPTITUDE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_INAPTITUDE_SELECT()) ? Integer
				.parseInt(getVAL_LB_INAPTITUDE_SELECT()) : -1);

		if (indice != -1 && indice < getListeInaptitude().size()) {
			TypeInaptitude ti = getListeInaptitude().get(indice);
			setInaptitudeCourante(ti);
			addZone(getNOM_EF_DESC_INAPTITUDE(), ti.getDescTypeInaptitude());
			addZone(getNOM_ST_ACTION_INAPTITUDE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'inaptitude"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_LESION() {
		return "NOM_PB_SUPPRIMER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_LESION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_LESION_SELECT()) ? Integer.parseInt(getVAL_LB_LESION_SELECT())
				: -1);

		if (indice != -1 && indice < getListeLesion().size()) {
			RefTypeDto sl = getListeLesion().get(indice);
			setLesionCourant(sl);
			addZone(getNOM_EF_DESC_LESION(), sl.getLibelle());
			addZone(getNOM_ST_ACTION_LESION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "sièges de lésion"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MALADIE() {
		return "NOM_PB_SUPPRIMER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MALADIE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MALADIE_SELECT()) ? Integer.parseInt(getVAL_LB_MALADIE_SELECT())
				: -1);

		if (indice != -1 && indice < getListeMaladie().size()) {
			RefTypeDto mp = getListeMaladie().get(indice);
			setMaladieCourante(mp);
			addZone(getNOM_EF_CODE_MALADIE(), mp.getCode());
			addZone(getNOM_EF_LIBELLE_MALADIE(), mp.getLibelle());
			addZone(getNOM_ST_ACTION_MALADIE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "maladie professionelles"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MEDECIN() {
		return "NOM_PB_SUPPRIMER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MEDECIN(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MEDECIN_SELECT()) ? Integer.parseInt(getVAL_LB_MEDECIN_SELECT())
				: -1);

		if (indice != -1 && indice < getListeMedecin().size()) {
			Medecin m = getListeMedecin().get(indice);
			setMedecinCourant(m);
			addZone(getNOM_EF_NOM_MEDECIN(), m.getNomMedecin());
			addZone(getNOM_EF_PRENOM_MEDECIN(), m.getPrenomMedecin());
			addZone(getNOM_EF_TITRE_MEDECIN(), m.getTitreMedecin());
			addZone(getNOM_ST_ACTION_MEDECIN(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "médecins"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECOMMANDATION
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECOMMANDATION() {
		return "NOM_PB_SUPPRIMER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_RECOMMANDATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_RECOMMANDATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeRecommandation().size()) {
			Recommandation r = getListeRecommandation().get(indice);
			setRecommandationCourante(r);
			addZone(getNOM_EF_DESC_RECOMMANDATION(), r.getDescRecommandation());
			addZone(getNOM_ST_ACTION_RECOMMANDATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "recommandations"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AT Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_AT() {
		return "NOM_PB_VALIDER_AT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_AT(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieAT(request))
			return false;

		if (getVAL_ST_ACTION_AT() != null && getVAL_ST_ACTION_AT() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_AT().equals(ACTION_CREATION)) {
				setAtCourant(new RefTypeDto());
				getAtCourant().setLibelle(getVAL_EF_DESC_AT().toUpperCase());
				
				ReturnMessageDto result = getAbsService().setRefTypeAccidentTravail(getAgentConnecte(request).getIdAgent(), getAtCourant());
				
				if (result.getErrors().isEmpty())
					getListeAT().add(getAtCourant());

				if (!declarerErreurFromReturnMessageDto(result))
					return false;
				
			} else if (getVAL_ST_ACTION_AT().equals(ACTION_SUPPRESSION)) {
				
				ReturnMessageDto result = getAbsService().deleteRefTypeAccidentTravail(getAgentConnecte(request).getIdAgent(), getAtCourant());
				
				if (result.getErrors().isEmpty())
					getListeAT().remove(getAtCourant());

				if (!declarerErreurFromReturnMessageDto(result))
					return false;
				
				setAtCourant(null);
			}
			
			initialiseListeAT(request);
			addZone(getNOM_ST_ACTION_AT(), Const.CHAINE_VIDE);
		}

		return true;
	}
	
	private boolean declarerErreurFromReturnMessageDto(ReturnMessageDto result) {
		if (result.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : result.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (result.getInfos().size() > 0) {
			String inf = Const.CHAINE_VIDE;
			for (String info : result.getInfos()) {
				inf += " " + info;
			}
			getTransaction().declarerErreur(inf);
		}
		return true;
	}

	/**
	 * Controle les zones saisies d'un type d'AT Date de création : (15/09/11)
	 */
	private boolean performControlerSaisieAT(HttpServletRequest request) throws Exception {

		// Verification description type d'AT not null
		if (getZone(getNOM_EF_DESC_AT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_INAPTITUDE() {
		return "NOM_PB_VALIDER_INAPTITUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_INAPTITUDE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieInaptitude(request))
			return false;

		if (!performControlerRegleGestionInaptitude(request))
			return false;

		if (getVAL_ST_ACTION_INAPTITUDE() != null && getVAL_ST_ACTION_INAPTITUDE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_CREATION)) {
				setInaptitudeCourante(new TypeInaptitude());
				getInaptitudeCourante().setDescTypeInaptitude(getVAL_EF_DESC_INAPTITUDE());
				getTypeInaptitudeDao().creerTypeInaptitude(getInaptitudeCourante().getDescTypeInaptitude());
				if (!getTransaction().isErreur())
					getListeInaptitude().add(getInaptitudeCourante());
			} else if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_SUPPRESSION)) {
				getTypeInaptitudeDao().supprimerTypeInaptitude(getInaptitudeCourante().getIdTypeInaptitude());
				if (!getTransaction().isErreur())
					getListeInaptitude().remove(getInaptitudeCourante());
				setInaptitudeCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeInaptitude(request);
			addZone(getNOM_ST_ACTION_INAPTITUDE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un type d'inaptitude Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerSaisieInaptitude(HttpServletRequest request) throws Exception {

		// Verification description type d'inaptitude not null
		if (getZone(getNOM_EF_DESC_INAPTITUDE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'un type d'inaptitude Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerRegleGestionInaptitude(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type d'inaptitude utilise sur une
		// inaptitude
		if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_SUPPRESSION)
				&& getInaptitudeDao().listerInaptitudeAvecTypeInaptitude(getInaptitudeCourante().getIdTypeInaptitude())
						.size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché a @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "inaptitude", "ce type d'inaptitude"));
			return false;
		}

		// Vérification des contraintes d'unicité du type d'inaptitude
		if (getVAL_ST_ACTION_INAPTITUDE().equals(ACTION_CREATION)) {

			for (TypeInaptitude titre : getListeInaptitude()) {
				if (titre.getDescTypeInaptitude().equals(getVAL_EF_DESC_INAPTITUDE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà  @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type d'inaptitude", "cette description"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_LESION() {
		return "NOM_PB_VALIDER_LESION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_LESION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieLesion(request))
			return false;

		if (getVAL_ST_ACTION_LESION() != null && getVAL_ST_ACTION_LESION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_LESION().equals(ACTION_CREATION)) {
				setLesionCourant(new RefTypeDto());
				getLesionCourant().setLibelle(getVAL_EF_DESC_LESION().toUpperCase());
				
				ReturnMessageDto result = getAbsService().setRefTypeSiegeLesion(getAgentConnecte(request).getIdAgent(), getLesionCourant());
				
				if (result.getErrors().isEmpty())
					getListeLesion().add(getLesionCourant());
				
				if(!declarerErreurFromReturnMessageDto(result))
					return false;
				
			} else if (getVAL_ST_ACTION_LESION().equals(ACTION_SUPPRESSION)) {
				
				ReturnMessageDto result = getAbsService().deleteRefTypeSiegeLesion(getAgentConnecte(request).getIdAgent(), getLesionCourant());
				
				if (result.getErrors().isEmpty())
					getListeLesion().remove(getLesionCourant());
				
				if(!declarerErreurFromReturnMessageDto(result))
					return false;
				
				setLesionCourant(null);
			}

			initialiseListeLesion(request);
			addZone(getNOM_ST_ACTION_LESION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un siege de lésion Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerSaisieLesion(HttpServletRequest request) throws Exception {

		// Verification desription siege de lesion not null
		if (getZone(getNOM_EF_DESC_LESION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MALADIE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_MALADIE() {
		return "NOM_PB_VALIDER_MALADIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_MALADIE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMaladie(request))
			return false;

		if (getVAL_ST_ACTION_MALADIE() != null && getVAL_ST_ACTION_MALADIE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MALADIE().equals(ACTION_CREATION)) {
				
				setMaladieCourante(new RefTypeDto());
				getMaladieCourante().setLibelle(getVAL_EF_LIBELLE_MALADIE().toUpperCase());
				getMaladieCourante().setCode(null != getVAL_EF_CODE_MALADIE() ? getVAL_EF_CODE_MALADIE().toUpperCase() : Const.CHAINE_VIDE);
				
				ReturnMessageDto result = getAbsService().setRefTypeMaladiePro(getAgentConnecte(request).getIdAgent(), getMaladieCourante());
				
				if (result.getErrors().isEmpty())
					getListeMaladie().add(getMaladieCourante());
				
				if(!declarerErreurFromReturnMessageDto(result)) 
					return false;
				
			} else if (getVAL_ST_ACTION_MALADIE().equals(ACTION_SUPPRESSION)) {
				
				ReturnMessageDto result = getAbsService().deleteRefTypeMaladiePro(getAgentConnecte(request).getIdAgent(), getMaladieCourante());
				
				if (result.getErrors().isEmpty())
					getListeMaladie().remove(getMaladieCourante());
				
				if(!declarerErreurFromReturnMessageDto(result)) 
					return false;
				
				setMaladieCourante(null);
			}
			
			initialiseListeMaladie(request);
			addZone(getNOM_ST_ACTION_MALADIE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'une maladie professionnelle Date de création
	 * : (15/09/11)
	 */
	private boolean performControlerSaisieMaladie(HttpServletRequest request) throws Exception {

		// Verification libellé maladie not null
		if (getZone(getNOM_EF_LIBELLE_MALADIE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}
	
	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_MEDECIN() {
		return "NOM_PB_VALIDER_MEDECIN";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_MEDECIN(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMedecin(request))
			return false;

		if (!performControlerRegleGestionMedecin(request))
			return false;

		if (getVAL_ST_ACTION_MEDECIN() != null && getVAL_ST_ACTION_MEDECIN() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_CREATION)) {
				setMedecinCourant(new Medecin());
				getMedecinCourant().setNomMedecin(getVAL_EF_NOM_MEDECIN());
				getMedecinCourant().setPrenomMedecin(getVAL_EF_PRENOM_MEDECIN());
				getMedecinCourant().setTitreMedecin(getVAL_EF_TITRE_MEDECIN());
				try {
					getMedecinDao().creerMedecin(getMedecinCourant().getTitreMedecin(),
							getMedecinCourant().getPrenomMedecin(), getMedecinCourant().getNomMedecin());
					getListeMedecin().add(getMedecinCourant());
				} catch (Exception e) {

				}
			} else if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_SUPPRESSION)) {
				getMedecinDao().supprimerMedecin(getMedecinCourant().getIdMedecin());
				if (!getTransaction().isErreur())
					getListeMedecin().remove(getMedecinCourant());
				setMedecinCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeMedecin(request);
			addZone(getNOM_ST_ACTION_MEDECIN(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un medecin Date de création : (15/09/11)
	 */
	private boolean performControlerSaisieMedecin(HttpServletRequest request) throws Exception {

		// Verification nom medecin not null
		if (getZone(getNOM_EF_NOM_MEDECIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nom"));
			return false;
		}

		// Verification prenom medecin not null
		if (getZone(getNOM_EF_PRENOM_MEDECIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "prénom"));
			return false;
		}

		// Verification titre medecin not null
		if (getZone(getNOM_EF_TITRE_MEDECIN()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "titre"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'un medecin Date de création : (15/09/11)
	 */
	private boolean performControlerRegleGestionMedecin(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un medecin utilise sur une visite
		// médicale
		if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_SUPPRESSION)
				&& getVisiteMedicaleDao().listerVisiteMedicaleAvecMedecin(getMedecinCourant().getIdMedecin()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché a @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une visite médicale", "ce médecin"));
			return false;
		}

		// Vérification des contraintes d'unicité du medecin
		if (getVAL_ST_ACTION_MEDECIN().equals(ACTION_CREATION)) {

			for (Medecin medecin : getListeMedecin()) {
				if (medecin.getNomMedecin().equals(getVAL_EF_NOM_MEDECIN().toUpperCase())
						&& medecin.getPrenomMedecin().equals(getVAL_EF_PRENOM_MEDECIN().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà  @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un médecin", "ce nom et ce prénom"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_RECOMMANDATION Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_PB_VALIDER_RECOMMANDATION() {
		return "NOM_PB_VALIDER_RECOMMANDATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public boolean performPB_VALIDER_RECOMMANDATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieRecommandation(request))
			return false;

		if (!performControlerRegleGestionRecommandation(request))
			return false;

		if (getVAL_ST_ACTION_RECOMMANDATION() != null && getVAL_ST_ACTION_RECOMMANDATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_CREATION)) {
				setRecommandationCourante(new Recommandation());
				getRecommandationCourante().setDescRecommandation(getVAL_EF_DESC_RECOMMANDATION());
				getRecommandationDao().creerRecommandation(getRecommandationCourante().getDescRecommandation());
				if (!getTransaction().isErreur())
					getListeRecommandation().add(getRecommandationCourante());
			} else if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_SUPPRESSION)) {
				getRecommandationDao().supprimerRecommandation(getRecommandationCourante().getIdRecommandation());
				if (!getTransaction().isErreur())
					getListeRecommandation().remove(getRecommandationCourante());
				setRecommandationCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeRecommandation(request);
			addZone(getNOM_ST_ACTION_RECOMMANDATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'une recommandation Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerSaisieRecommandation(HttpServletRequest request) throws Exception {

		// Verification description recomandation not null
		if (getZone(getNOM_EF_DESC_RECOMMANDATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'une recommandation Date de création :
	 * (15/09/11)
	 */
	private boolean performControlerRegleGestionRecommandation(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une recommandation utilise sur une
		// visite médicale
		if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_SUPPRESSION)
				&& getVisiteMedicaleDao().listerVisiteMedicaleAvecRecommandation(
						getRecommandationCourante().getIdRecommandation()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché a @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "une visite médicale", "cette recommandation"));
			return false;
		}

		// Vérification des contraintes d'unicité de la recommandation
		if (getVAL_ST_ACTION_RECOMMANDATION().equals(ACTION_CREATION)) {

			for (Recommandation rec : getListeRecommandation()) {
				if (rec.getDescRecommandation().equals(getVAL_EF_DESC_RECOMMANDATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà  @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une recommandation", "cette description"));
					return false;
				}
			}
		}

		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_AT Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_AT() {
		return "NOM_ST_ACTION_AT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION_AT Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_AT() {
		return getZone(getNOM_ST_ACTION_AT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_INAPTITUDE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_INAPTITUDE() {
		return "NOM_ST_ACTION_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_ACTION_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_INAPTITUDE() {
		return getZone(getNOM_ST_ACTION_INAPTITUDE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_LESION Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_LESION() {
		return "NOM_ST_ACTION_LESION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION_LESION
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_LESION() {
		return getZone(getNOM_ST_ACTION_LESION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MALADIE Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_MALADIE() {
		return "NOM_ST_ACTION_MALADIE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION_MALADIE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_MALADIE() {
		return getZone(getNOM_ST_ACTION_MALADIE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MEDECIN Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_MEDECIN() {
		return "NOM_ST_ACTION_MEDECIN";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION_MEDECIN
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_MEDECIN() {
		return getZone(getNOM_ST_ACTION_MEDECIN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_ST_ACTION_RECOMMANDATION() {
		return "NOM_ST_ACTION_RECOMMANDATION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_ACTION_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_ST_ACTION_RECOMMANDATION() {
		return getZone(getNOM_ST_ACTION_RECOMMANDATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_AT Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_AT() {
		return "NOM_EF_DESC_AT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DESC_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_AT() {
		return getZone(getNOM_EF_DESC_AT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_INAPTITUDE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_INAPTITUDE() {
		return "NOM_EF_DESC_INAPTITUDE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DESC_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_INAPTITUDE() {
		return getZone(getNOM_EF_DESC_INAPTITUDE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_LESION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_LESION() {
		return "NOM_EF_DESC_LESION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DESC_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_LESION() {
		return getZone(getNOM_EF_DESC_LESION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESC_RECOMMANDATION
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_DESC_RECOMMANDATION() {
		return "NOM_EF_DESC_RECOMMANDATION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_DESC_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_DESC_RECOMMANDATION() {
		return getZone(getNOM_EF_DESC_RECOMMANDATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIBELLE_MALADIE
	 * Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_LIBELLE_MALADIE() {
		return "NOM_EF_LIBELLE_MALADIE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_LIBELLE_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_LIBELLE_MALADIE() {
		return getZone(getNOM_EF_LIBELLE_MALADIE());
	}
	
	public String getNOM_EF_CODE_MALADIE() {
		return "NOM_EF_CODE_MALADIE";
	}
	public String getVAL_EF_CODE_MALADIE() {
		return getZone(getNOM_EF_CODE_MALADIE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_MEDECIN Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_NOM_MEDECIN() {
		return "NOM_EF_NOM_MEDECIN";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_NOM_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_NOM_MEDECIN() {
		return getZone(getNOM_EF_NOM_MEDECIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM_MEDECIN Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_PRENOM_MEDECIN() {
		return "NOM_EF_PRENOM_MEDECIN";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_PRENOM_MEDECIN() {
		return getZone(getNOM_EF_PRENOM_MEDECIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_MEDECIN Date
	 * de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_EF_TITRE_MEDECIN() {
		return "NOM_EF_TITRE_MEDECIN";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_EF_TITRE_MEDECIN() {
		return getZone(getNOM_EF_TITRE_MEDECIN());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AT Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_AT() {
		if (LB_AT == null)
			LB_AT = initialiseLazyLB();
		return LB_AT;
	}

	/**
	 * Setter de la liste: LB_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_AT(String[] newLB_AT) {
		LB_AT = newLB_AT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AT Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_AT() {
		return "NOM_LB_AT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AT_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_AT_SELECT() {
		return "NOM_LB_AT_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_AT() {
		return getLB_AT();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_AT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_AT_SELECT() {
		return getZone(getNOM_LB_AT_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_INAPTITUDE() {
		if (LB_INAPTITUDE == null)
			LB_INAPTITUDE = initialiseLazyLB();
		return LB_INAPTITUDE;
	}

	/**
	 * Setter de la liste: LB_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_INAPTITUDE(String[] newLB_INAPTITUDE) {
		LB_INAPTITUDE = newLB_INAPTITUDE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_INAPTITUDE Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_INAPTITUDE() {
		return "NOM_LB_INAPTITUDE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_INAPTITUDE_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_INAPTITUDE_SELECT() {
		return "NOM_LB_INAPTITUDE_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_INAPTITUDE() {
		return getLB_INAPTITUDE();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_INAPTITUDE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_INAPTITUDE_SELECT() {
		return getZone(getNOM_LB_INAPTITUDE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_LESION Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_LESION() {
		if (LB_LESION == null)
			LB_LESION = initialiseLazyLB();
		return LB_LESION;
	}

	/**
	 * Setter de la liste: LB_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_LESION(String[] newLB_LESION) {
		LB_LESION = newLB_LESION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_LESION Date de création :
	 * (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_LESION() {
		return "NOM_LB_LESION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_LESION_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_LESION_SELECT() {
		return "NOM_LB_LESION_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_LESION() {
		return getLB_LESION();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_LESION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_LESION_SELECT() {
		return getZone(getNOM_LB_LESION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MALADIE Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_MALADIE() {
		if (LB_MALADIE == null)
			LB_MALADIE = initialiseLazyLB();
		return LB_MALADIE;
	}

	/**
	 * Setter de la liste: LB_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_MALADIE(String[] newLB_MALADIE) {
		LB_MALADIE = newLB_MALADIE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MALADIE Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MALADIE() {
		return "NOM_LB_MALADIE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MALADIE_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MALADIE_SELECT() {
		return "NOM_LB_MALADIE_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_MALADIE() {
		return getLB_MALADIE();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MALADIE Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_MALADIE_SELECT() {
		return getZone(getNOM_LB_MALADIE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MEDECIN Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_MEDECIN() {
		if (LB_MEDECIN == null)
			LB_MEDECIN = initialiseLazyLB();
		return LB_MEDECIN;
	}

	/**
	 * Setter de la liste: LB_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	private void setLB_MEDECIN(String[] newLB_MEDECIN) {
		LB_MEDECIN = newLB_MEDECIN;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MEDECIN Date de création
	 * : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MEDECIN() {
		return "NOM_LB_MEDECIN";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MEDECIN_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_MEDECIN_SELECT() {
		return "NOM_LB_MEDECIN_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_MEDECIN() {
		return getLB_MEDECIN();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MEDECIN Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_MEDECIN_SELECT() {
		return getZone(getNOM_LB_MEDECIN_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RECOMMANDATION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	private String[] getLB_RECOMMANDATION() {
		if (LB_RECOMMANDATION == null)
			LB_RECOMMANDATION = initialiseLazyLB();
		return LB_RECOMMANDATION;
	}

	/**
	 * Setter de la liste: LB_RECOMMANDATION Date de création : (15/09/11
	 * 08:57:49)
	 * 
	 */
	private void setLB_RECOMMANDATION(String[] newLB_RECOMMANDATION) {
		LB_RECOMMANDATION = newLB_RECOMMANDATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RECOMMANDATION Date de
	 * création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_RECOMMANDATION() {
		return "NOM_LB_RECOMMANDATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RECOMMANDATION_SELECT Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getNOM_LB_RECOMMANDATION_SELECT() {
		return "NOM_LB_RECOMMANDATION_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String[] getVAL_LB_RECOMMANDATION() {
		return getLB_RECOMMANDATION();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RECOMMANDATION Date de création : (15/09/11 08:57:49)
	 * 
	 */
	public String getVAL_LB_RECOMMANDATION_SELECT() {
		return getZone(getNOM_LB_RECOMMANDATION_SELECT());
	}

	private TypeInaptitude getInaptitudeCourante() {
		return inaptitudeCourante;
	}

	private void setInaptitudeCourante(TypeInaptitude inaptitudeCourante) {
		this.inaptitudeCourante = inaptitudeCourante;
	}

	private ArrayList<TypeInaptitude> getListeInaptitude() {
		return listeInaptitude;
	}

	private void setListeInaptitude(ArrayList<TypeInaptitude> listeInaptitude) {
		this.listeInaptitude = listeInaptitude;
	}

	private ArrayList<Medecin> getListeMedecin() {
		return listeMedecin;
	}

	private void setListeMedecin(ArrayList<Medecin> listeMedecin) {
		this.listeMedecin = listeMedecin;
	}

	private ArrayList<Recommandation> getListeRecommandation() {
		return listeRecommandation;
	}

	private void setListeRecommandation(ArrayList<Recommandation> listeRecommandation) {
		this.listeRecommandation = listeRecommandation;
	}

	private Medecin getMedecinCourant() {
		return medecinCourant;
	}

	private void setMedecinCourant(Medecin medecinCourant) {
		this.medecinCourant = medecinCourant;
	}

	private Recommandation getRecommandationCourante() {
		return recommandationCourante;
	}

	private void setRecommandationCourante(Recommandation recommandationCourante) {
		this.recommandationCourante = recommandationCourante;
	}

	public HandicapDao getHandicapDao() {
		return handicapDao;
	}

	public void setHandicapDao(HandicapDao handicapDao) {
		this.handicapDao = handicapDao;
	}

	public MedecinDao getMedecinDao() {
		return medecinDao;
	}

	public void setMedecinDao(MedecinDao medecinDao) {
		this.medecinDao = medecinDao;
	}

	public RecommandationDao getRecommandationDao() {
		return recommandationDao;
	}

	public void setRecommandationDao(RecommandationDao recommandationDao) {
		this.recommandationDao = recommandationDao;
	}

	public TypeInaptitudeDao getTypeInaptitudeDao() {
		return typeInaptitudeDao;
	}

	public void setTypeInaptitudeDao(TypeInaptitudeDao typeInaptitudeDao) {
		this.typeInaptitudeDao = typeInaptitudeDao;
	}

	public VisiteMedicaleDao getVisiteMedicaleDao() {
		return visiteMedicaleDao;
	}

	public void setVisiteMedicaleDao(VisiteMedicaleDao visiteMedicaleDao) {
		this.visiteMedicaleDao = visiteMedicaleDao;
	}

	public InaptitudeDao getInaptitudeDao() {
		return inaptitudeDao;
	}

	public void setInaptitudeDao(InaptitudeDao inaptitudeDao) {
		this.inaptitudeDao = inaptitudeDao;
	}

	public IAbsService getAbsService() {
		return absService;
	}

	public void setAbsService(IAbsService absService) {
		this.absService = absService;
	}

	public ArrayList<RefTypeDto> getListeAT() {
		return listeAT;
	}

	public void setListeAT(ArrayList<RefTypeDto> listeAT) {
		this.listeAT = listeAT;
	}

	public RefTypeDto getAtCourant() {
		return atCourant;
	}

	public void setAtCourant(RefTypeDto atCourant) {
		this.atCourant = atCourant;
	}

	public ArrayList<RefTypeDto> getListeLesion() {
		return listeLesion;
	}

	public void setListeLesion(ArrayList<RefTypeDto> listeLesion) {
		this.listeLesion = listeLesion;
	}

	public RefTypeDto getLesionCourant() {
		return lesionCourant;
	}

	public void setLesionCourant(RefTypeDto lesionCourant) {
		this.lesionCourant = lesionCourant;
	}

	public ArrayList<RefTypeDto> getListeMaladie() {
		return listeMaladie;
	}

	public void setListeMaladie(ArrayList<RefTypeDto> listeMaladie) {
		this.listeMaladie = listeMaladie;
	}

	public RefTypeDto getMaladieCourante() {
		return maladieCourante;
	}

	public void setMaladieCourante(RefTypeDto maladieCourante) {
		this.maladieCourante = maladieCourante;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public IRadiService getRadiService() {
		return radiService;
	}

	public void setRadiService(IRadiService radiService) {
		this.radiService = radiService;
	}
	
}
