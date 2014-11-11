package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.BaseHorairePointage;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Ecole;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.NFA;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.spring.dao.metier.parametrage.BaseHorairePointageDao;
import nc.mairie.spring.dao.metier.parametrage.NatureAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDelegationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeRegIndemnDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.NFADao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureDao;
import nc.mairie.spring.dao.metier.specificites.DelegationDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGEFichePoste Date de création : (13/09/11 15:49:10)
 * 
 */
public class OePARAMETRAGEFichePoste extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	
	private String[] LB_ENTITE_GEO;
	private String[] LB_ENTITE_ECOLE;
	private String[] LB_NATURE_AVANTAGE;
	private String[] LB_TITRE;
	private String[] LB_TYPE_AVANTAGE;
	private String[] LB_TYPE_DELEGATION;
	private String[] LB_TYPE_REGIME;
	private String[] LB_ECOLE;
	private String[] LB_NFA;
	private String[] LB_BASE_HORAIRE_POINTAGE;

	private ArrayList<BaseHorairePointage> listeBaseHorairePointage;
	private BaseHorairePointage baseHorairePointageCourant;

	private ArrayList<EntiteGeo> listeEntite;
	private EntiteGeo entiteGeoCourante;

	private ArrayList<Ecole> listeEntiteEcole;
	private Hashtable<String, Ecole> hashEntiteEcole;

	private ArrayList<TitrePoste> listeTitrePoste;
	private TitrePoste titrePosteCourante;

	private ArrayList<TypeAvantage> listeTypeAvantage;
	private TypeAvantage typeAvantageCourant;

	private ArrayList<NatureAvantage> listeNatureAvantage;
	private NatureAvantage natureAvantageCourant;

	private ArrayList<TypeDelegation> listeTypeDelegation;
	private TypeDelegation typeDelegationCourant;

	private ArrayList<TypeRegIndemn> listeTypeRegime;
	private TypeRegIndemn typeRegimeCourant;

	private ArrayList<NFA> listeNFA;
	private NFA NFACourant;

	private ArrayList<Ecole> listeEcole;
	private Ecole EcoleCourante;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	private NatureAvantageDao natureAvantageDao;
	private TypeAvantageDao typeAvantageDao;
	private TypeDelegationDao typeDelegationDao;
	private TypeRegIndemnDao typeRegIndemnDao;
	private AvantageNatureDao avantageNatureDao;
	private DelegationDao delegationDao;
	private RegIndemnDao regIndemnDao;
	private TitrePosteDao titrePosteDao;
	private NFADao nfaDao;
	private FichePosteDao fichePosteDao;
	private BaseHorairePointageDao baseHorairePointageDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (13/09/11 15:49:10)
	 * 
	 */
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

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		initialiseListes(request);

		// Si hashtable des écoles vide
		if (getHashEntiteEcole().size() == 0) {
			ArrayList<Ecole> a = Ecole.listerEcole(getTransaction());
			setListeEntiteEcole(a);
			initialiseListeEntiteGeoEcole(request);
			// remplissage de la hashTable
			for (int i = 0; i < a.size(); i++) {
				Ecole aEcole = (Ecole) a.get(i);
				getHashEntiteEcole().put(aEcole.getCdecol(), aEcole);
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getNatureAvantageDao() == null) {
			setNatureAvantageDao(new NatureAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeAvantageDao() == null) {
			setTypeAvantageDao(new TypeAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeDelegationDao() == null) {
			setTypeDelegationDao(new TypeDelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeRegIndemnDao() == null) {
			setTypeRegIndemnDao(new TypeRegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvantageNatureDao() == null) {
			setAvantageNatureDao(new AvantageNatureDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDelegationDao() == null) {
			setDelegationDao(new DelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnDao() == null) {
			setRegIndemnDao(new RegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNfaDao() == null) {
			setNfaDao(new NFADao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getBaseHorairePointageDao() == null) {
			setBaseHorairePointageDao(new BaseHorairePointageDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Retourne les ecoles dans une table de hashage Date de création :
	 * (11/06/2003 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, Ecole> getHashEntiteEcole() {
		if (hashEntiteEcole == null) {
			hashEntiteEcole = new Hashtable<String, Ecole>();
		}
		return hashEntiteEcole;
	}

	/**
	 * Initialisation de la listes des entités géographiques Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeEntiteGeo(HttpServletRequest request) throws Exception {
		setListeEntite(EntiteGeo.listerEntiteGeo(getTransaction()));
		if (getListeEntite().size() != 0) {
			int tailles[] = { 5, 70 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EntiteGeo> list = getListeEntite().listIterator(); list.hasNext();) {
				EntiteGeo eg = (EntiteGeo) list.next();
				String ligne[] = { eg.getCdEcol(), eg.getLibEntiteGeo() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ENTITE_GEO(aFormat.getListeFormatee());
		} else {
			setLB_ENTITE_GEO(null);
		}
	}

	/**
	 * Initialisation de la listes des ecoles pour les entités géographiques
	 * Date de création : (14/09/11)
	 */
	private void initialiseListeEntiteGeoEcole(HttpServletRequest request) throws Exception {
		if (getListeEntiteEcole().size() != 0) {
			int tailles[] = { 6, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Ecole> list = getListeEntiteEcole().listIterator(); list.hasNext();) {
				Ecole ecole = (Ecole) list.next();
				String ligne[] = { ecole.getCdecol(), ecole.getLiecol() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ENTITE_GEO_ECOLE(aFormat.getListeFormatee(true));
		} else {
			setLB_ENTITE_GEO_ECOLE(null);
		}
	}

	/**
	 * Initialisation dede la liste des titres de poste Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTitre(HttpServletRequest request) throws Exception {
		setListeTitrePoste(getTitrePosteDao().listerTitrePoste());
		if (getListeTitrePoste().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TitrePoste> list = getListeTitrePoste().listIterator(); list.hasNext();) {
				TitrePoste tp = (TitrePoste) list.next();
				String ligne[] = { tp.getLibTitrePoste() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TITRE(aFormat.getListeFormatee());
		} else {
			setLB_TITRE(null);
		}
	}

	/**
	 * Initialisation dede la liste des types d'avantage en nature Date de
	 * création : (14/09/11)
	 * 
	 */
	private void initialiseListeTypeAvantage(HttpServletRequest request) throws Exception {
		setListeTypeAvantage(getTypeAvantageDao().listerTypeAvantage());
		if (getListeTypeAvantage().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeAvantage> list = getListeTypeAvantage().listIterator(); list.hasNext();) {
				TypeAvantage ta = (TypeAvantage) list.next();
				String ligne[] = { ta.getLibTypeAvantage() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_AVANTAGE(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_AVANTAGE(null);
		}
	}

	/**
	 * Initialisation dede la liste des natures d'avantage en nature Date de
	 * création : (14/09/11)
	 * 
	 */
	private void initialiseListeNatureAvantage(HttpServletRequest request) throws Exception {
		setListeNatureAvantage(getNatureAvantageDao().listerNatureAvantage());
		if (getListeNatureAvantage().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<NatureAvantage> list = getListeNatureAvantage().listIterator(); list.hasNext();) {
				NatureAvantage na = (NatureAvantage) list.next();
				String ligne[] = { na.getLibNatureAvantage() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_NATURE_AVANTAGE(aFormat.getListeFormatee());
		} else {
			setLB_NATURE_AVANTAGE(null);
		}
	}

	/**
	 * Initialisation dede la liste des types de délégation Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTypeDelegation(HttpServletRequest request) throws Exception {
		setListeTypeDelegation(getTypeDelegationDao().listerTypeDelegation());
		if (getListeTypeDelegation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeDelegation> list = getListeTypeDelegation().listIterator(); list.hasNext();) {
				TypeDelegation td = (TypeDelegation) list.next();
				String ligne[] = { td.getLibTypeDelegation() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DELEGATION(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_DELEGATION(null);
		}
	}

	/**
	 * Initialisation dede la liste des types de régime indemnitaire Date de
	 * création : (14/09/11)
	 * 
	 */
	private void initialiseListeTypeRegime(HttpServletRequest request) throws Exception {
		setListeTypeRegime(getTypeRegIndemnDao().listerTypeRegIndemn());
		if (getListeTypeRegime().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeRegIndemn> list = getListeTypeRegime().listIterator(); list.hasNext();) {
				TypeRegIndemn tr = (TypeRegIndemn) list.next();
				String ligne[] = { tr.getLibTypeRegIndemn() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_REGIME(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_REGIME(null);
		}
	}

	/**
	 * Initialisation dede la liste des NFAs Date de création : (04/11/11)
	 * 
	 */
	private void initialiseListeNFA(HttpServletRequest request) throws Exception {
		setListeNFA((ArrayList<NFA>) getNfaDao().listerNFA());
		if (getListeNFA().size() != 0) {
			int tailles[] = { 6, 5 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<NFA> list = getListeNFA().listIterator(); list.hasNext();) {
				NFA nfa = (NFA) list.next();
				String ligne[] = { nfa.getCodeService(), nfa.getNFA() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_NFA(aFormat.getListeFormatee());
		} else {
			setLB_NFA(null);
		}
	}

	/**
	 * Initialisation dede la liste des ecoles Date de création : (04/11/11)
	 */
	private void initialiseListeEcole(HttpServletRequest request) throws Exception {
		setListeEcole(Ecole.listerEcole(getTransaction()));
		if (getListeEcole().size() != 0) {
			int tailles[] = { 6, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Ecole> list = getListeEcole().listIterator(); list.hasNext();) {
				Ecole ecole = (Ecole) list.next();
				String ligne[] = { ecole.getCdecol(), ecole.getLiecol() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ECOLE(aFormat.getListeFormatee());
		} else {
			setLB_ECOLE(null);
		}
	}

	/**
	 * Initialisation des listes de paramètres Date de création : (13/09/11)
	 * 
	 */
	private void initialiseListes(HttpServletRequest request) throws Exception {

		if (getListeEntite() == null) {
			// Recherche des entités géographiques
			setListeEntite(EntiteGeo.listerEntiteGeo(getTransaction()));
			initialiseListeEntiteGeo(request);
		}

		if (getListeTitrePoste() == null) {
			// Recherche des titres de poste
			setListeTitrePoste(getTitrePosteDao().listerTitrePoste());
			initialiseListeTitre(request);
		}

		if (getListeTypeAvantage() == null) {
			// Recherche des types d'avantage en nature
			setListeTypeAvantage(getTypeAvantageDao().listerTypeAvantage());
			initialiseListeTypeAvantage(request);
		}

		if (getListeNatureAvantage() == null) {
			// Recherche des natures d'avantage en nature
			setListeNatureAvantage(getNatureAvantageDao().listerNatureAvantage());
			initialiseListeNatureAvantage(request);
		}

		if (getListeTypeDelegation() == null) {
			// Recherche des types de délégation
			setListeTypeDelegation(getTypeDelegationDao().listerTypeDelegation());
			initialiseListeTypeDelegation(request);
		}

		if (getListeTypeRegime() == null) {
			// Recherche des types de régime indemnitaires
			setListeTypeRegime(getTypeRegIndemnDao().listerTypeRegIndemn());
			initialiseListeTypeRegime(request);
		}

		if (getListeNFA() == null) {
			// Recherche des NFAs
			setListeNFA((ArrayList<NFA>) getNfaDao().listerNFA());
			initialiseListeNFA(request);
		}

		if (getListeEcole() == null) {
			// Recherche des Ecoles
			setListeEcole(Ecole.listerEcole(getTransaction()));
			initialiseListeEcole(request);
		}

		if (getListeBaseHorairePointage() == null) {
			// Recherche des bases horaires de pointage
			setListeBaseHorairePointage((ArrayList<BaseHorairePointage>) getBaseHorairePointageDao()
					.listerBaseHorairePointage());
			initialiseListeBaseHorairePointage(request);
		}
	}

	private void initialiseListeBaseHorairePointage(HttpServletRequest request) throws Exception {
		setListeBaseHorairePointage((ArrayList<BaseHorairePointage>) getBaseHorairePointageDao()
				.listerBaseHorairePointage());
		if (getListeBaseHorairePointage().size() != 0) {
			int tailles[] = { 6, 5 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<BaseHorairePointage> list = getListeBaseHorairePointage().listIterator(); list.hasNext();) {
				BaseHorairePointage base = (BaseHorairePointage) list.next();
				String ligne[] = { base.getCodeBaseHorairePointage(), base.getLibelleBaseHorairePointage() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_BASE_HORAIRE_POINTAGE(aFormat.getListeFormatee());
		} else {
			setLB_BASE_HORAIRE_POINTAGE(null);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEFichePoste. Date de création :
	 * (13/09/11 15:49:10)
	 * 
	 */
	public OePARAMETRAGEFichePoste() {
		super();
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-PE-FICHEPOSTE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_ENTITE_GEO() {
		return "NOM_PB_ANNULER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_ENTITE_GEO(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ENTITE_GEO(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_NATURE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_NATURE_AVANTAGE() {
		return "NOM_PB_ANNULER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TITRE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TITRE() {
		return "NOM_PB_ANNULER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TITRE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TITRE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_AVANTAGE() {
		return "NOM_PB_ANNULER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_DELEGATION Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_DELEGATION() {
		return "NOM_PB_ANNULER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_REGIME Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_REGIME() {
		return "NOM_PB_ANNULER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_REGIME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_REGIME(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_ENTITE_GEO() {
		return "NOM_PB_CREER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_ENTITE_GEO(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_ENTITE_GEO(), ACTION_CREATION);
		addZone(getNOM_EF_ENTITE_GEO(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT(), "0");

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_MODIFIER_ENTITE_GEO() {
		return "NOM_PB_MODIFIER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_ENTITE_GEO(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_SELECT()) ? Integer
				.parseInt(getVAL_LB_ENTITE_GEO_SELECT()) : -1);

		if (indice != -1 && indice < getListeEntite().size()) {
			EntiteGeo entiteGeo = getListeEntite().get(indice);
			setEntiteGeoCourante(entiteGeo);

			Ecole s = (Ecole) getHashEntiteEcole().get(entiteGeo.getCdEcol());
			int ligneEcoleEntitee = getListeEntiteEcole().indexOf(s);
			addZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT(), String.valueOf(ligneEcoleEntitee + 1));

			addZone(getNOM_EF_ENTITE_GEO(), entiteGeo.getLibEntiteGeo());
			addZone(getNOM_ST_ACTION_ENTITE_GEO(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "entités géographiques"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_NATURE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_NATURE_AVANTAGE() {
		return "NOM_PB_CREER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), ACTION_CREATION);
		addZone(getNOM_EF_NATURE_AVANTAGE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TITRE Date de création
	 * : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TITRE() {
		return "NOM_PB_CREER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TITRE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TITRE(), ACTION_CREATION);
		addZone(getNOM_EF_TITRE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_AVANTAGE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_AVANTAGE() {
		return "NOM_PB_CREER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_AVANTAGE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_DELEGATION Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_DELEGATION() {
		return "NOM_PB_CREER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_DELEGATION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_REGIME Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_REGIME() {
		return "NOM_PB_CREER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TYPE_REGIME(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_REGIME(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_REGIME(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_ENTITE_GEO() {
		return "NOM_PB_SUPPRIMER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_ENTITE_GEO(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_SELECT()) ? Integer
				.parseInt(getVAL_LB_ENTITE_GEO_SELECT()) : -1);

		if (indice != -1 && indice < getListeEntite().size()) {
			EntiteGeo eg = getListeEntite().get(indice);
			setEntiteGeoCourante(eg);

			Ecole s = (Ecole) getHashEntiteEcole().get(eg.getCdEcol());
			int ligneEcoleEntitee = getListeEntiteEcole().indexOf(s);
			addZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT(), String.valueOf(ligneEcoleEntitee + 1));

			addZone(getNOM_EF_ENTITE_GEO(), eg.getLibEntiteGeo());
			addZone(getNOM_ST_ACTION_ENTITE_GEO(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "entités géographiques"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NATURE_AVANTAGE
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NATURE_AVANTAGE() {
		return "NOM_PB_SUPPRIMER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()) : -1);

		if (indice != -1 && indice < getListeNatureAvantage().size()) {
			NatureAvantage na = getListeNatureAvantage().get(indice);
			setNatureAvantageCourant(na);
			addZone(getNOM_EF_NATURE_AVANTAGE(), na.getLibNatureAvantage());
			addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "natures d'avantage en nature"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TITRE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TITRE() {
		return "NOM_PB_SUPPRIMER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TITRE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TITRE_SELECT()) ? Integer.parseInt(getVAL_LB_TITRE_SELECT()) : -1);

		if (indice != -1 && indice < getListeTitrePoste().size()) {
			TitrePoste tp = getListeTitrePoste().get(indice);
			setTitrePosteCourante(tp);
			addZone(getNOM_EF_TITRE(), tp.getLibTitrePoste());
			addZone(getNOM_ST_ACTION_TITRE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de poste"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_AVANTAGE() {
		return "NOM_PB_SUPPRIMER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeAvantage().size()) {
			TypeAvantage ta = getListeTypeAvantage().get(indice);
			setTypeAvantageCourant(ta);
			addZone(getNOM_EF_TYPE_AVANTAGE(), ta.getLibTypeAvantage());
			addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'avantage en nature"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_DELEGATION
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_DELEGATION() {
		return "NOM_PB_SUPPRIMER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeDelegation().size()) {
			TypeDelegation tg = getListeTypeDelegation().get(indice);
			setTypeDelegationCourant(tg);
			addZone(getNOM_EF_TYPE_DELEGATION(), tg.getLibTypeDelegation());
			addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types de délégation"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_REGIME Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_REGIME() {
		return "NOM_PB_SUPPRIMER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_REGIME(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeRegime().size()) {
			TypeRegIndemn tr = getListeTypeRegime().get(indice);
			setTypeRegimeCourant(tr);
			addZone(getNOM_EF_TYPE_REGIME(), tr.getLibTypeRegIndemn());
			addZone(getNOM_ST_ACTION_TYPE_REGIME(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types de régime"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_ENTITE_GEO() {
		return "NOM_PB_VALIDER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_ENTITE_GEO(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieEntiteGeo(request))
			return false;

		if (!performControlerRegleGestionEntiteGeo(request))
			return false;

		if (getVAL_ST_ACTION_ENTITE_GEO() != null && getVAL_ST_ACTION_ENTITE_GEO() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_CREATION)) {
				// on recupere l'école (si elle est saisie !)
				setEntiteGeoCourante(new EntiteGeo());
				int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) ? Integer
						.parseInt(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) : -1);
				if (indice != 0) {
					String codeEcole = ((Ecole) getListeEntiteEcole().get(indice - 1)).getCdecol();
					getEntiteGeoCourante().setCdEcol(codeEcole);
				} else {
					getEntiteGeoCourante().setCdEcol(Const.ZERO);
				}

				getEntiteGeoCourante().setLibEntiteGeo(getVAL_EF_ENTITE_GEO());
				getEntiteGeoCourante().setRidet(Const.ZERO);
				getEntiteGeoCourante().creerEntiteGeo(getTransaction());

				if (!getTransaction().isErreur())
					getListeEntite().add(getEntiteGeoCourante());
			} else if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_SUPPRESSION)) {
				getEntiteGeoCourante().supprimerEntiteGeo(getTransaction());
				if (!getTransaction().isErreur())
					getListeEntite().remove(getEntiteGeoCourante());
				setEntiteGeoCourante(null);
			} else if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_MODIFICATION)) {
				// on recupere l'école (si elle est saisie !)
				int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) ? Integer
						.parseInt(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) : -1);
				if (indice != 0) {
					String codeEcole = ((Ecole) getListeEntiteEcole().get(indice - 1)).getCdecol();
					getEntiteGeoCourante().setCdEcol(codeEcole);

				} else {
					getEntiteGeoCourante().setCdEcol(Const.ZERO);
				}

				getEntiteGeoCourante().setLibEntiteGeo(getVAL_EF_ENTITE_GEO());
				getEntiteGeoCourante().modifierEntiteGeo(getTransaction());

			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeEntiteGeo(request);
			addZone(getNOM_ST_ACTION_ENTITE_GEO(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une entité géographique Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieEntiteGeo(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_ENTITE_GEO()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une entité géographique Date de création
	 * : (09/09/11)
	 */
	private boolean performControlerRegleGestionEntiteGeo(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une entité géographique utilisée sur
		// une fiche de poste
		if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_SUPPRESSION)
				&& getFichePosteDao().listerFichePosteAvecEntiteGeo(
						Integer.valueOf(getEntiteGeoCourante().getIdEntiteGeo())).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "une fiche de poste", "cette entité géographique"));
			return false;
		}

		// Vérification des contraintes d'unicité de l'entité géographique
		if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_CREATION)) {

			for (EntiteGeo entite : getListeEntite()) {
				if (entite.getLibEntiteGeo().equals(getVAL_EF_ENTITE_GEO().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une entité géographique", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_NATURE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_NATURE_AVANTAGE() {
		return "NOM_PB_VALIDER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieNatureAvantage(request))
			return false;

		if (!performControlerRegleGestionNatureAvantage(request))
			return false;

		if (getVAL_ST_ACTION_NATURE_AVANTAGE() != null && getVAL_ST_ACTION_NATURE_AVANTAGE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_CREATION)) {
				setNatureAvantageCourant(new NatureAvantage());
				getNatureAvantageCourant().setLibNatureAvantage(getVAL_EF_NATURE_AVANTAGE());
				getNatureAvantageDao().creerNatureAvantage(getNatureAvantageCourant().getLibNatureAvantage());
				if (!getTransaction().isErreur())
					getListeNatureAvantage().add(getNatureAvantageCourant());
			} else if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_SUPPRESSION)) {
				getNatureAvantageDao().supprimerNatureAvantage(getNatureAvantageCourant().getIdNatureAvantage());
				if (!getTransaction().isErreur())
					getListeNatureAvantage().remove(getNatureAvantageCourant());
				setNatureAvantageCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeNatureAvantage(request);
			addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une nature d'avantage en nature Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerSaisieNatureAvantage(HttpServletRequest request) throws Exception {

		// Verification lib nature avantage not null
		if (getZone(getNOM_EF_NATURE_AVANTAGE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une nature d'avantage en nature Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionNatureAvantage(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une nature d'avantage en nature
		// utilisée sur un avantage en nature
		if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_SUPPRESSION)
				&& getAvantageNatureDao().listerAvantageNatureAvecNatureAvantage(
						getNatureAvantageCourant().getIdNatureAvantage()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "un avantage en nature", "cette nature d'avantage en nature"));
			return false;
		}

		// Vérification des contraintes d'unicité de l'entité géographique
		if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_CREATION)) {

			for (NatureAvantage nature : getListeNatureAvantage()) {
				if (nature.getLibNatureAvantage().equals(getVAL_EF_NATURE_AVANTAGE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une nature d'avantage en nature", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TITRE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TITRE() {
		return "NOM_PB_VALIDER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TITRE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTitre(request))
			return false;

		if (!performControlerRegleGestionTitre(request))
			return false;

		if (getVAL_ST_ACTION_TITRE() != null && getVAL_ST_ACTION_TITRE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TITRE().equals(ACTION_CREATION)) {
				setTitrePosteCourante(new TitrePoste());
				getTitrePosteCourante().setLibTitrePoste(getVAL_EF_TITRE());
				getTitrePosteDao().creerTitrePoste(getTitrePosteCourante().getLibTitrePoste());
				if (!getTransaction().isErreur())
					getListeTitrePoste().add(getTitrePosteCourante());
			} else if (getVAL_ST_ACTION_TITRE().equals(ACTION_SUPPRESSION)) {
				getTitrePosteDao().supprimerTitrePoste(getTitrePosteCourante().getIdTitrePoste());
				if (!getTransaction().isErreur())
					getListeTitrePoste().remove(getTitrePosteCourante());
				setTitrePosteCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTitre(request);
			addZone(getNOM_ST_ACTION_TITRE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un titre de poste Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTitre(HttpServletRequest request) throws Exception {

		// Verification libellé titre de poste not null
		if (getZone(getNOM_EF_TITRE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un titre de poste Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionTitre(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un titre de poste utilisé sur une fiche
		// de poste
		if (getVAL_ST_ACTION_TITRE().equals(ACTION_SUPPRESSION)
				&& getFichePosteDao().listerFichePosteAvecTitrePoste(getTitrePosteCourante().getIdTitrePoste()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "une fiche de poste", "ce titre de poste"));
			return false;
		}

		// Vérification des contraintes d'unicité du titre de poste
		if (getVAL_ST_ACTION_TITRE().equals(ACTION_CREATION)) {

			for (TitrePoste titre : getListeTitrePoste()) {
				if (titre.getLibTitrePoste().equals(getVAL_EF_TITRE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un titre de poste", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_AVANTAGE() {
		return "NOM_PB_VALIDER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeAvantage(request))
			return false;

		if (!performControlerRegleGestionTypeAvantage(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_AVANTAGE() != null && getVAL_ST_ACTION_TYPE_AVANTAGE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_CREATION)) {
				setTypeAvantageCourant(new TypeAvantage());
				getTypeAvantageCourant().setLibTypeAvantage(getVAL_EF_TYPE_AVANTAGE());
				getTypeAvantageDao().creerTypeAvantage(getTypeAvantageCourant().getLibTypeAvantage());
				if (!getTransaction().isErreur())
					getListeTypeAvantage().add(getTypeAvantageCourant());
			} else if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_SUPPRESSION)) {
				getTypeAvantageDao().supprimerTypeAvantage(getTypeAvantageCourant().getIdTypeAvantage());
				if (!getTransaction().isErreur())
					getListeTypeAvantage().remove(getTypeAvantageCourant());
				setTypeAvantageCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeAvantage(request);
			addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type d'avantage en nature Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerSaisieTypeAvantage(HttpServletRequest request) throws Exception {

		// Verification libellé type d'avantage not null
		if (getZone(getNOM_EF_TYPE_AVANTAGE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type d'avantage en nature Date de
	 * création : (09/09/11)
	 */
	private boolean performControlerRegleGestionTypeAvantage(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type d'avantage utilisé sur un
		// avantage en nature
		if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_SUPPRESSION)
				&& getAvantageNatureDao().listerAvantageNatureAvecTypeAvantage(
						getTypeAvantageCourant().getIdTypeAvantage()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "un avantage en nature", "ce type d'avantage en nature"));
			return false;
		}

		// Vérification des contraintes d'unicité du type d'avantage en nature
		if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_CREATION)) {

			for (TypeAvantage type : getListeTypeAvantage()) {
				if (type.getLibTypeAvantage().equals(getVAL_EF_TYPE_AVANTAGE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type d'avantage en nature", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_DELEGATION Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_DELEGATION() {
		return "NOM_PB_VALIDER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeDelegation(request))
			return false;

		if (!performControlerRegleGestionTypeDelegation(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_DELEGATION() != null && getVAL_ST_ACTION_TYPE_DELEGATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_CREATION)) {
				setTypeDelegationCourant(new TypeDelegation());
				getTypeDelegationCourant().setLibTypeDelegation(getVAL_EF_TYPE_DELEGATION());
				getTypeDelegationDao().creerTypeDelegation(getTypeDelegationCourant().getLibTypeDelegation());
				if (!getTransaction().isErreur())
					getListeTypeDelegation().add(getTypeDelegationCourant());
			} else if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_SUPPRESSION)) {
				getTypeDelegationDao().supprimerTypeDelegation(getTypeDelegationCourant().getIdTypeDelegation());
				if (!getTransaction().isErreur())
					getListeTypeDelegation().remove(getTypeDelegationCourant());
				setTypeDelegationCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeDelegation(request);
			addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type de délégation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTypeDelegation(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_TYPE_DELEGATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type de delegation Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionTypeDelegation(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type de délégation sur une
		// délégation
		if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_SUPPRESSION)
				&& getDelegationDao().listerDelegationAvecTypeDelegation(
						getTypeDelegationCourant().getIdTypeDelegation()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "une délégation", "ce type de délégation"));
			return false;
		}

		// Vérification des contraintes d'unicité de l'entité géographique
		if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_CREATION)) {

			for (TypeDelegation type : getListeTypeDelegation()) {
				if (type.getLibTypeDelegation().equals(getVAL_EF_TYPE_DELEGATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type de délégation", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_REGIME Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_REGIME() {
		return "NOM_PB_VALIDER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_REGIME(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeRegime(request))
			return false;

		if (!performControlerRegleGestionTypeRegime(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_REGIME() != null && getVAL_ST_ACTION_TYPE_REGIME() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_CREATION)) {
				setTypeRegimeCourant(new TypeRegIndemn());
				getTypeRegimeCourant().setLibTypeRegIndemn(getVAL_EF_TYPE_REGIME());
				getTypeRegIndemnDao().creerTypeRegIndemn(getTypeRegimeCourant().getLibTypeRegIndemn());
				if (!getTransaction().isErreur())
					getListeTypeRegime().add(getTypeRegimeCourant());
			} else if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_SUPPRESSION)) {
				getTypeRegIndemnDao().supprimerTypeRegIndemn(getTypeRegimeCourant().getIdTypeRegIndemn());
				if (!getTransaction().isErreur())
					getListeTypeRegime().remove(getTypeRegimeCourant());
				setTypeRegimeCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeRegime(request);
			addZone(getNOM_ST_ACTION_TYPE_REGIME(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type de regime indemnitaire Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerSaisieTypeRegime(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_TYPE_REGIME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type de régime idemnitaire Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionTypeRegime(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type de régime idemnitaire utilisée
		// sur un régime idemnitaire
		if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_SUPPRESSION)
				&& getRegIndemnDao()
						.listerRegimeIndemnitaireAvecTypeRegime(getTypeRegimeCourant().getIdTypeRegIndemn()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR989", "un régime indemnitaire", "ce type de régime idemnitaire"));
			return false;
		}

		// Vérification des contraintes d'unicité du type de régime idemnitaire
		if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_CREATION)) {

			for (TypeRegIndemn type : getListeTypeRegime()) {
				if (type.getLibTypeRegIndemn().equals(getVAL_EF_TYPE_REGIME().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type de régime idemnitaire", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_ENTITE_GEO
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_ENTITE_GEO() {
		return "NOM_ST_ACTION_ENTITE_GEO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_ENTITE_GEO Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_ENTITE_GEO() {
		return getZone(getNOM_ST_ACTION_ENTITE_GEO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_NATURE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_NATURE_AVANTAGE() {
		return "NOM_ST_ACTION_NATURE_AVANTAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_NATURE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_NATURE_AVANTAGE() {
		return getZone(getNOM_ST_ACTION_NATURE_AVANTAGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TITRE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TITRE() {
		return "NOM_ST_ACTION_TITRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_TITRE
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TITRE() {
		return getZone(getNOM_ST_ACTION_TITRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TYPE_AVANTAGE
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_AVANTAGE() {
		return "NOM_ST_ACTION_TYPE_AVANTAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_AVANTAGE() {
		return getZone(getNOM_ST_ACTION_TYPE_AVANTAGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_TYPE_DELEGATION Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_DELEGATION() {
		return "NOM_ST_ACTION_TYPE_DELEGATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_DELEGATION Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_DELEGATION() {
		return getZone(getNOM_ST_ACTION_TYPE_DELEGATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TYPE_REGIME
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_REGIME() {
		return "NOM_ST_ACTION_TYPE_REGIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_REGIME Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_REGIME() {
		return getZone(getNOM_ST_ACTION_TYPE_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_ENTITE_GEO() {
		return "NOM_EF_ENTITE_GEO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ENTITE_GEO Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_ENTITE_GEO() {
		return getZone(getNOM_EF_ENTITE_GEO());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NATURE_AVANTAGE
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_NATURE_AVANTAGE() {
		return "NOM_EF_NATURE_AVANTAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NATURE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_NATURE_AVANTAGE() {
		return getZone(getNOM_EF_NATURE_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TITRE() {
		return "NOM_EF_TITRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TITRE() {
		return getZone(getNOM_EF_TITRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_AVANTAGE Date
	 * de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TYPE_AVANTAGE() {
		return "NOM_EF_TYPE_AVANTAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TYPE_AVANTAGE() {
		return getZone(getNOM_EF_TYPE_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_DELEGATION
	 * Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TYPE_DELEGATION() {
		return "NOM_EF_TYPE_DELEGATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_DELEGATION Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TYPE_DELEGATION() {
		return getZone(getNOM_EF_TYPE_DELEGATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_REGIME Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TYPE_REGIME() {
		return "NOM_EF_TYPE_REGIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_REGIME Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TYPE_REGIME() {
		return getZone(getNOM_EF_TYPE_REGIME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_ENTITE_GEO() {
		if (LB_ENTITE_GEO == null)
			LB_ENTITE_GEO = initialiseLazyLB();
		return LB_ENTITE_GEO;
	}

	/**
	 * Setter de la liste: LB_ENTITE_GEO Date de création : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_ENTITE_GEO(String[] newLB_ENTITE_GEO) {
		LB_ENTITE_GEO = newLB_ENTITE_GEO;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ENTITE_GEO Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO() {
		return "NOM_LB_ENTITE_GEO";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ENTITE_GEO_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO_SELECT() {
		return "NOM_LB_ENTITE_GEO_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ENTITE_GEO Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_ENTITE_GEO() {
		return getLB_ENTITE_GEO();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ENTITE_GEO Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_ENTITE_GEO_SELECT() {
		return getZone(getNOM_LB_ENTITE_GEO_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ENTITE_ECOLE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_ENTITE_ECOLE() {
		if (LB_ENTITE_ECOLE == null)
			LB_ENTITE_ECOLE = initialiseLazyLB();
		return LB_ENTITE_ECOLE;
	}

	/**
	 * Setter de la liste: LB_ENTITE_ECOLE Date de création : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_ENTITE_GEO_ECOLE(String[] newLB_ENTITE_ECOLE) {
		LB_ENTITE_ECOLE = newLB_ENTITE_ECOLE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ENTITE_GEO_ECOLE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO_ECOLE() {
		return "NOM_LB_ENTITE_GEO_ECOLE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ENTITE_GEO_ECOLE_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO_ECOLE_SELECT() {
		return "NOM_LB_ENTITE_GEO_ECOLE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ENTITE_GEO_ECOLE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_ENTITE_GEO_ECOLE() {
		return getLB_ENTITE_ECOLE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ENTITE_GEO_ECOLE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_ENTITE_GEO_ECOLE_SELECT() {
		return getZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de création : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE Date de création :
	 * (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TITRE() {
		if (LB_TITRE == null)
			LB_TITRE = initialiseLazyLB();
		return LB_TITRE;
	}

	/**
	 * Setter de la liste: LB_TITRE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_TITRE(String[] newLB_TITRE) {
		LB_TITRE = newLB_TITRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE Date de création :
	 * (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TITRE() {
		return "NOM_LB_TITRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TITRE_SELECT() {
		return "NOM_LB_TITRE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TITRE() {
		return getLB_TITRE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TITRE_SELECT() {
		return getZone(getNOM_LB_TITRE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de création : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE() {
		return "NOM_LB_TYPE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de création : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_TYPE_DELEGATION(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION() {
		return "NOM_LB_TYPE_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT() {
		return "NOM_LB_TYPE_DELEGATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION() {
		return getLB_TYPE_DELEGATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TYPE_REGIME() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de création : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_TYPE_REGIME(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME() {
		return "NOM_LB_TYPE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT() {
		return "NOM_LB_TYPE_REGIME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME() {
		return getLB_TYPE_REGIME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TYPE_REGIME_SELECT() {
		return getZone(getNOM_LB_TYPE_REGIME_SELECT());
	}

	private EntiteGeo getEntiteGeoCourante() {
		return entiteGeoCourante;
	}

	private void setEntiteGeoCourante(EntiteGeo entiteGeoCourante) {
		this.entiteGeoCourante = entiteGeoCourante;
	}

	private ArrayList<EntiteGeo> getListeEntite() {
		return listeEntite;
	}

	private void setListeEntite(ArrayList<EntiteGeo> listeEntite) {
		this.listeEntite = listeEntite;
	}

	private ArrayList<Ecole> getListeEntiteEcole() {
		return listeEntiteEcole;
	}

	private void setListeEntiteEcole(ArrayList<Ecole> listeEntiteEcole) {
		this.listeEntiteEcole = listeEntiteEcole;
	}

	private ArrayList<NatureAvantage> getListeNatureAvantage() {
		return listeNatureAvantage;
	}

	private void setListeNatureAvantage(ArrayList<NatureAvantage> listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	private ArrayList<TitrePoste> getListeTitrePoste() {
		return listeTitrePoste;
	}

	private void setListeTitrePoste(ArrayList<TitrePoste> listeTitrePoste) {
		this.listeTitrePoste = listeTitrePoste;
	}

	private ArrayList<TypeAvantage> getListeTypeAvantage() {
		return listeTypeAvantage;
	}

	private void setListeTypeAvantage(ArrayList<TypeAvantage> listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	private ArrayList<TypeDelegation> getListeTypeDelegation() {
		return listeTypeDelegation;
	}

	private void setListeTypeDelegation(ArrayList<TypeDelegation> listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
	}

	private ArrayList<TypeRegIndemn> getListeTypeRegime() {
		return listeTypeRegime;
	}

	private void setListeTypeRegime(ArrayList<TypeRegIndemn> listeTypeRegime) {
		this.listeTypeRegime = listeTypeRegime;
	}

	private NatureAvantage getNatureAvantageCourant() {
		return natureAvantageCourant;
	}

	private void setNatureAvantageCourant(NatureAvantage natureAvantageCourant) {
		this.natureAvantageCourant = natureAvantageCourant;
	}

	private TitrePoste getTitrePosteCourante() {
		return titrePosteCourante;
	}

	private void setTitrePosteCourante(TitrePoste titrePosteCourante) {
		this.titrePosteCourante = titrePosteCourante;
	}

	private TypeAvantage getTypeAvantageCourant() {
		return typeAvantageCourant;
	}

	private void setTypeAvantageCourant(TypeAvantage typeAvantageCourant) {
		this.typeAvantageCourant = typeAvantageCourant;
	}

	private TypeDelegation getTypeDelegationCourant() {
		return typeDelegationCourant;
	}

	private void setTypeDelegationCourant(TypeDelegation typeDelegationCourant) {
		this.typeDelegationCourant = typeDelegationCourant;
	}

	private TypeRegIndemn getTypeRegimeCourant() {
		return typeRegimeCourant;
	}

	private void setTypeRegimeCourant(TypeRegIndemn typeRegimeCourant) {
		this.typeRegimeCourant = typeRegimeCourant;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (13/09/11 15:49:10)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_ECOLE
			if (testerParametre(request, getNOM_PB_ANNULER_ECOLE())) {
				return performPB_ANNULER_ECOLE(request);
			}

			// Si clic sur le bouton PB_CREER_ECOLE
			if (testerParametre(request, getNOM_PB_CREER_ECOLE())) {
				return performPB_CREER_ECOLE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_ECOLE
			if (testerParametre(request, getNOM_PB_MODIFIER_ECOLE())) {
				return performPB_MODIFIER_ECOLE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ECOLE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ECOLE())) {
				return performPB_SUPPRIMER_ECOLE(request);
			}

			// Si clic sur le bouton PB_VALIDER_ECOLE
			if (testerParametre(request, getNOM_PB_VALIDER_ECOLE())) {
				return performPB_VALIDER_ECOLE(request);
			}

			// Si clic sur le bouton PB_ANNULER_NFA
			if (testerParametre(request, getNOM_PB_ANNULER_NFA())) {
				return performPB_ANNULER_NFA(request);
			}

			// Si clic sur le bouton PB_CREER_NFA
			if (testerParametre(request, getNOM_PB_CREER_NFA())) {
				return performPB_CREER_NFA(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NFA
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NFA())) {
				return performPB_SUPPRIMER_NFA(request);
			}

			// Si clic sur le bouton PB_VALIDER_NFA
			if (testerParametre(request, getNOM_PB_VALIDER_NFA())) {
				return performPB_VALIDER_NFA(request);
			}

			// Si clic sur le bouton PB_ANNULER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_ANNULER_ENTITE_GEO())) {
				return performPB_ANNULER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_ANNULER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_ANNULER_NATURE_AVANTAGE())) {
				return performPB_ANNULER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_ANNULER_TITRE
			if (testerParametre(request, getNOM_PB_ANNULER_TITRE())) {
				return performPB_ANNULER_TITRE(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_AVANTAGE())) {
				return performPB_ANNULER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_DELEGATION())) {
				return performPB_ANNULER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_REGIME())) {
				return performPB_ANNULER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_CREER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_CREER_ENTITE_GEO())) {
				return performPB_CREER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_MODIFIER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_MODIFIER_ENTITE_GEO())) {
				return performPB_MODIFIER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_CREER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_CREER_NATURE_AVANTAGE())) {
				return performPB_CREER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_CREER_TITRE
			if (testerParametre(request, getNOM_PB_CREER_TITRE())) {
				return performPB_CREER_TITRE(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_CREER_TYPE_AVANTAGE())) {
				return performPB_CREER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_CREER_TYPE_DELEGATION())) {
				return performPB_CREER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_CREER_TYPE_REGIME())) {
				return performPB_CREER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ENTITE_GEO())) {
				return performPB_SUPPRIMER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NATURE_AVANTAGE())) {
				return performPB_SUPPRIMER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TITRE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TITRE())) {
				return performPB_SUPPRIMER_TITRE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_AVANTAGE())) {
				return performPB_SUPPRIMER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_DELEGATION())) {
				return performPB_SUPPRIMER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_REGIME())) {
				return performPB_SUPPRIMER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_VALIDER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_VALIDER_ENTITE_GEO())) {
				return performPB_VALIDER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_VALIDER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_VALIDER_NATURE_AVANTAGE())) {
				return performPB_VALIDER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TITRE
			if (testerParametre(request, getNOM_PB_VALIDER_TITRE())) {
				return performPB_VALIDER_TITRE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_AVANTAGE())) {
				return performPB_VALIDER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_DELEGATION())) {
				return performPB_VALIDER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_REGIME())) {
				return performPB_VALIDER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_ANNULER_BASE_HORAIRE_POINTAGE
			if (testerParametre(request, getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE())) {
				return performPB_ANNULER_BASE_HORAIRE_POINTAGE(request);
			}

			// Si clic sur le bouton PB_CREER_BASE_HORAIRE_POINTAGE
			if (testerParametre(request, getNOM_PB_CREER_BASE_HORAIRE_POINTAGE())) {
				return performPB_CREER_BASE_HORAIRE_POINTAGE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_BASE_HORAIRE_POINTAGE
			if (testerParametre(request, getNOM_PB_MODIFIER_BASE_HORAIRE_POINTAGE())) {
				return performPB_MODIFIER_BASE_HORAIRE_POINTAGE(request);
			}

			// Si clic sur le bouton PB_VALIDER_BASE_HORAIRE_POINTAGE
			if (testerParametre(request, getNOM_PB_VALIDER_BASE_HORAIRE_POINTAGE())) {
				return performPB_VALIDER_BASE_HORAIRE_POINTAGE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (04/11/11 11:33:54)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEFichePoste.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ECOLE Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_ANNULER_ECOLE() {
		return "NOM_PB_ANNULER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_ANNULER_ECOLE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ECOLE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_ECOLE Date de création
	 * : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_CREER_ECOLE() {
		return "NOM_PB_CREER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_CREER_ECOLE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_ECOLE(), ACTION_CREATION);
		addZone(getNOM_EF_ECOLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ECOLE_CODE_ECOLE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_ECOLE Date de
	 * création : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_ECOLE() {
		return "NOM_PB_MODIFIER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_ECOLE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ECOLE_SELECT()) ? Integer.parseInt(getVAL_LB_ECOLE_SELECT()) : -1);

		if (indice != -1 && indice < getListeEcole().size()) {
			Ecole ecole = getListeEcole().get(indice);
			setEcoleCourante(ecole);
			addZone(getNOM_EF_ECOLE_CODE_ECOLE(), ecole.getCdecol());
			addZone(getNOM_EF_ECOLE(), ecole.getLiecol());
			addZone(getNOM_ST_ACTION_ECOLE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "écoles"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ECOLE Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_ECOLE() {
		return "NOM_PB_SUPPRIMER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_SUPPRIMER_ECOLE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ECOLE_SELECT()) ? Integer.parseInt(getVAL_LB_ECOLE_SELECT()) : -1);

		if (indice != -1 && indice < getListeEcole().size()) {
			Ecole ecole = getListeEcole().get(indice);
			setEcoleCourante(ecole);
			addZone(getNOM_EF_ECOLE_CODE_ECOLE(), ecole.getCdecol());
			addZone(getNOM_EF_ECOLE(), ecole.getLiecol());
			addZone(getNOM_ST_ACTION_ECOLE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Ecoles"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ECOLE Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_VALIDER_ECOLE() {
		return "NOM_PB_VALIDER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_VALIDER_ECOLE(HttpServletRequest request) throws Exception {

		if (!performControlerSaisieEcole(request))
			return false;

		if (!performControlerRegleGestionEcole(request))
			return false;

		if (getVAL_ST_ACTION_ECOLE() != null && getVAL_ST_ACTION_ECOLE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_ECOLE().equals(ACTION_CREATION)) {
				setEcoleCourante(new Ecole());
				getEcoleCourante().setCdecol(getVAL_EF_ECOLE_CODE_ECOLE());
				getEcoleCourante().setLiecol(getVAL_EF_ECOLE());
				getEcoleCourante().setQuecol(Const.CHAINE_VIDE);
				getEcoleCourante().setSecter(Const.CHAINE_VIDE);
				getEcoleCourante().creerEcole(getTransaction());
				if (!getTransaction().isErreur())
					getListeEcole().add(getEcoleCourante());
			} else if (getVAL_ST_ACTION_ECOLE().equals(ACTION_SUPPRESSION)) {
				getEcoleCourante().supprimerEcole(getTransaction());
				if (!getTransaction().isErreur())
					getListeEcole().remove(getEcoleCourante());
				setEcoleCourante(null);
			} else if (getVAL_ST_ACTION_ECOLE().equals(ACTION_MODIFICATION)) {
				getEcoleCourante().setLiecol(getVAL_EF_ECOLE());
				getEcoleCourante().modifierEcole(getTransaction());
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeEcole(request);
			addZone(getNOM_ST_ACTION_ECOLE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type de regime indemnitaire Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerSaisieEcole(HttpServletRequest request) throws Exception {
		// Verification code ecole numerique
		if (!Services.estNumerique(getVAL_EF_ECOLE_CODE_ECOLE())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "code école"));
			return false;
		}
		// Verification code ecole not null
		if (getZone(getNOM_EF_ECOLE_CODE_ECOLE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellée"));
			return false;
		}
		// Verification lib ecole not null
		if (getZone(getNOM_EF_ECOLE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code ecole"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type de régime idemnitaire Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionEcole(HttpServletRequest request) throws Exception {
		// Vérification des contraintes d'unicité du code ecole
		if (getVAL_ST_ACTION_ECOLE().equals(ACTION_CREATION)) {
			for (Ecole ecole : getListeEcole()) {
				if (ecole.getCdecol().equals(getVAL_EF_ECOLE_CODE_ECOLE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une école", "ce code école"));
					return false;
				}
			}
		}
		// Vérification des contraintes d'unicité du libellé ecole
		if (getVAL_ST_ACTION_ECOLE().equals(ACTION_CREATION)) {
			for (Ecole ecole : getListeEcole()) {
				if (ecole.getLiecol().equals(getVAL_EF_ECOLE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une école", "ce libellé"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_ECOLE Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_ST_ACTION_ECOLE() {
		return "NOM_ST_ACTION_ECOLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_ECOLE
	 * Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_ST_ACTION_ECOLE() {
		return getZone(getNOM_ST_ACTION_ECOLE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ECOLE Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_ECOLE() {
		return "NOM_EF_ECOLE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ECOLE_CODE_ECOLE
	 * Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_ECOLE_CODE_ECOLE() {
		return "NOM_EF_ECOLE_CODE_ECOLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ECOLE_CODE_ECOLE Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_ECOLE_CODE_ECOLE() {
		return getZone(getNOM_EF_ECOLE_CODE_ECOLE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ECOLE Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_ECOLE() {
		return getZone(getNOM_EF_ECOLE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_NFA Date de création
	 * : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_ANNULER_NFA() {
		return "NOM_PB_ANNULER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_ANNULER_NFA(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_NFA(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_NFA Date de création :
	 * (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_CREER_NFA() {
		return "NOM_PB_CREER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_CREER_NFA(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_NFA(), ACTION_CREATION);
		addZone(getNOM_EF_NFA(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NFA_CODE_SERVICE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NFA Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NFA() {
		return "NOM_PB_SUPPRIMER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_SUPPRIMER_NFA(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_NFA_SELECT()) ? Integer.parseInt(getVAL_LB_NFA_SELECT()) : -1);

		if (indice != -1 && indice < getListeNFA().size()) {
			NFA nfa = getListeNFA().get(indice);
			setNFACourant(nfa);
			addZone(getNOM_EF_NFA_CODE_SERVICE(), nfa.getCodeService());
			addZone(getNOM_EF_NFA(), nfa.getNFA());
			addZone(getNOM_ST_ACTION_NFA(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "NFAs"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_NFA Date de création
	 * : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_VALIDER_NFA() {
		return "NOM_PB_VALIDER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_VALIDER_NFA(HttpServletRequest request) throws Exception {

		if (!performControlerSaisieNFA(request))
			return false;

		if (!performControlerRegleGestionNFA(request))
			return false;

		if (getVAL_ST_ACTION_NFA() != null && getVAL_ST_ACTION_NFA() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_NFA().equals(ACTION_CREATION)) {
				setNFACourant(new NFA());
				getNFACourant().setCodeService(getVAL_EF_NFA_CODE_SERVICE());
				getNFACourant().setNFA(getVAL_EF_NFA());
				getNfaDao().creerNFA(getNFACourant().getCodeService(), getNFACourant().getNFA());
				if (!getTransaction().isErreur())
					getListeNFA().add(getNFACourant());
			} else if (getVAL_ST_ACTION_NFA().equals(ACTION_SUPPRESSION)) {
				getNfaDao().supprimerNFA(getNFACourant().getCodeService(), getNFACourant().getNFA());
				if (!getTransaction().isErreur())
					getListeNFA().remove(getNFACourant());
				setNFACourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeNFA(request);
			addZone(getNOM_ST_ACTION_NFA(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un type de regime indemnitaire Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerSaisieNFA(HttpServletRequest request) throws Exception {

		// Verification code service not null
		if (getZone(getNOM_EF_NFA_CODE_SERVICE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code service"));
			return false;
		}

		// Verification nfa not null
		if (getZone(getNOM_EF_NFA()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "NFA"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un type de régime idemnitaire Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionNFA(HttpServletRequest request) throws Exception {

		// Vérification des contraintes d'unicité du NFA
		if (getVAL_ST_ACTION_NFA().equals(ACTION_CREATION)) {

			for (NFA nfa : getListeNFA()) {
				if (nfa.getCodeService().equals(getVAL_EF_NFA_CODE_SERVICE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un NFA", "ce code service"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_NFA Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_ST_ACTION_NFA() {
		return "NOM_ST_ACTION_NFA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_NFA
	 * Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_ST_ACTION_NFA() {
		return getZone(getNOM_ST_ACTION_NFA());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NFA Date de
	 * création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_NFA() {
		return "NOM_EF_NFA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_NFA
	 * Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_NFA() {
		return getZone(getNOM_EF_NFA());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NFA_CODE_SERVICE
	 * Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_NFA_CODE_SERVICE() {
		return "NOM_EF_NFA_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NFA_CODE_SERVICE Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_NFA_CODE_SERVICE() {
		return getZone(getNOM_EF_NFA_CODE_SERVICE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NFA Date de création :
	 * (04/11/11 11:33:55)
	 * 
	 */
	private String[] getLB_NFA() {
		if (LB_NFA == null)
			LB_NFA = initialiseLazyLB();
		return LB_NFA;
	}

	/**
	 * Setter de la liste: LB_NFA Date de création : (04/11/11 11:33:55)
	 * 
	 */
	private void setLB_NFA(String[] newLB_NFA) {
		LB_NFA = newLB_NFA;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NFA Date de création :
	 * (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_NFA() {
		return "NOM_LB_NFA";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NFA_SELECT Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_NFA_SELECT() {
		return "NOM_LB_NFA_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NFA Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String[] getVAL_LB_NFA() {
		return getLB_NFA();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NFA Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_LB_NFA_SELECT() {
		return getZone(getNOM_LB_NFA_SELECT());
	}

	private NFA getNFACourant() {
		return NFACourant;
	}

	private void setNFACourant(NFA courant) {
		NFACourant = courant;
	}

	private ArrayList<NFA> getListeNFA() {
		return listeNFA;
	}

	private void setListeNFA(ArrayList<NFA> listeNFA) {
		this.listeNFA = listeNFA;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ECOLE Date de création :
	 * (04/11/11 11:33:55)
	 * 
	 */
	private String[] getLB_ECOLE() {
		if (LB_ECOLE == null)
			LB_ECOLE = initialiseLazyLB();
		return LB_ECOLE;
	}

	/**
	 * Setter de la liste: LB_ECOLE Date de création : (04/11/11 11:33:55)
	 * 
	 */
	private void setLB_ECOLE(String[] newLB_ECOLE) {
		LB_ECOLE = newLB_ECOLE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ECOLE Date de création :
	 * (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_ECOLE() {
		return "NOM_LB_ECOLE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ECOLE_SELECT Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_ECOLE_SELECT() {
		return "NOM_LB_ECOLE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ECOLE Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String[] getVAL_LB_ECOLE() {
		return getLB_ECOLE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ECOLE Date de création : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_LB_ECOLE_SELECT() {
		return getZone(getNOM_LB_ECOLE_SELECT());
	}

	private Ecole getEcoleCourante() {
		return EcoleCourante;
	}

	private void setEcoleCourante(Ecole courant) {
		EcoleCourante = courant;
	}

	private ArrayList<Ecole> getListeEcole() {
		return listeEcole;
	}

	private void setListeEcole(ArrayList<Ecole> listeEcole) {
		this.listeEcole = listeEcole;
	}

	public NatureAvantageDao getNatureAvantageDao() {
		return natureAvantageDao;
	}

	public void setNatureAvantageDao(NatureAvantageDao natureAvantageDao) {
		this.natureAvantageDao = natureAvantageDao;
	}

	public TypeAvantageDao getTypeAvantageDao() {
		return typeAvantageDao;
	}

	public void setTypeAvantageDao(TypeAvantageDao typeAvantageDao) {
		this.typeAvantageDao = typeAvantageDao;
	}

	public TypeDelegationDao getTypeDelegationDao() {
		return typeDelegationDao;
	}

	public void setTypeDelegationDao(TypeDelegationDao typeDelegationDao) {
		this.typeDelegationDao = typeDelegationDao;
	}

	public TypeRegIndemnDao getTypeRegIndemnDao() {
		return typeRegIndemnDao;
	}

	public void setTypeRegIndemnDao(TypeRegIndemnDao typeRegIndemnDao) {
		this.typeRegIndemnDao = typeRegIndemnDao;
	}

	public AvantageNatureDao getAvantageNatureDao() {
		return avantageNatureDao;
	}

	public void setAvantageNatureDao(AvantageNatureDao avantageNatureDao) {
		this.avantageNatureDao = avantageNatureDao;
	}

	public DelegationDao getDelegationDao() {
		return delegationDao;
	}

	public void setDelegationDao(DelegationDao delegationDao) {
		this.delegationDao = delegationDao;
	}

	public RegIndemnDao getRegIndemnDao() {
		return regIndemnDao;
	}

	public void setRegIndemnDao(RegIndemnDao regIndemnDao) {
		this.regIndemnDao = regIndemnDao;
	}

	public TitrePosteDao getTitrePosteDao() {
		return titrePosteDao;
	}

	public void setTitrePosteDao(TitrePosteDao titrePosteDao) {
		this.titrePosteDao = titrePosteDao;
	}

	public NFADao getNfaDao() {
		return nfaDao;
	}

	public void setNfaDao(NFADao nfaDao) {
		this.nfaDao = nfaDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public BaseHorairePointageDao getBaseHorairePointageDao() {
		return baseHorairePointageDao;
	}

	public void setBaseHorairePointageDao(BaseHorairePointageDao baseHorairePointageDao) {
		this.baseHorairePointageDao = baseHorairePointageDao;
	}

	private String[] getLB_BASE_HORAIRE_POINTAGE() {
		if (LB_BASE_HORAIRE_POINTAGE == null)
			LB_BASE_HORAIRE_POINTAGE = initialiseLazyLB();
		return LB_BASE_HORAIRE_POINTAGE;
	}

	private void setLB_BASE_HORAIRE_POINTAGE(String[] newLB_BASE_HORAIRE_POINTAGE) {
		LB_BASE_HORAIRE_POINTAGE = newLB_BASE_HORAIRE_POINTAGE;
	}

	public String getNOM_LB_BASE_HORAIRE_POINTAGE() {
		return "NOM_LB_BASE_HORAIRE_POINTAGE";
	}

	public String getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_POINTAGE_SELECT";
	}

	public String[] getVAL_LB_BASE_HORAIRE_POINTAGE() {
		return getLB_BASE_HORAIRE_POINTAGE();
	}

	public String getVAL_LB_BASE_HORAIRE_POINTAGE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT());
	}

	public ArrayList<BaseHorairePointage> getListeBaseHorairePointage() {
		return listeBaseHorairePointage;
	}

	public void setListeBaseHorairePointage(ArrayList<BaseHorairePointage> listeBaseHorairePointage) {
		this.listeBaseHorairePointage = listeBaseHorairePointage;
	}

	public BaseHorairePointage getBaseHorairePointageCourant() {
		return baseHorairePointageCourant;
	}

	public void setBaseHorairePointageCourant(BaseHorairePointage baseHorairePointageCourant) {
		this.baseHorairePointageCourant = baseHorairePointageCourant;
	}

	public String getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE() {
		return "NOM_PB_ANNULER_BASE_HORAIRE_POINTAGE";
	}

	public boolean performPB_ANNULER_BASE_HORAIRE_POINTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_BASE_HORAIRE_POINTAGE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE());
		return true;
	}

	public String getNOM_ST_ACTION_BASE_HORAIRE_POINTAGE() {
		return "NOM_ST_ACTION_BASE_HORAIRE_POINTAGE";
	}

	public String getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE() {
		return getZone(getNOM_ST_ACTION_BASE_HORAIRE_POINTAGE());
	}

	public String getNOM_PB_CREER_BASE_HORAIRE_POINTAGE() {
		return "NOM_PB_CREER_BASE_HORAIRE_POINTAGE";
	}

	public boolean performPB_CREER_BASE_HORAIRE_POINTAGE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_BASE_HORAIRE_POINTAGE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_BASE_HORAIRE_POINTAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_BASE_HORAIRE_POINTAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DESC_BASE_HORAIRE_POINTAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_LEG_H(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_LEG_M(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_H(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_M(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_LUNDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_MARDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_MERCREDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_JEUDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_VENDREDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_SAMEDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_DIMANCHE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE());
		return true;
	}

	public String getNOM_PB_MODIFIER_BASE_HORAIRE_POINTAGE() {
		return "NOM_PB_MODIFIER_BASE_HORAIRE_POINTAGE";
	}

	public boolean performPB_MODIFIER_BASE_HORAIRE_POINTAGE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_BASE_HORAIRE_POINTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_BASE_HORAIRE_POINTAGE_SELECT()) : -1);
		if (indice != -1 && indice < getListeBaseHorairePointage().size()) {
			BaseHorairePointage base = getListeBaseHorairePointage().get(indice);
			setBaseHorairePointageCourant(base);
			addZone(getNOM_EF_LIB_BASE_HORAIRE_POINTAGE(), base.getLibelleBaseHorairePointage());
			addZone(getNOM_EF_DESC_BASE_HORAIRE_POINTAGE(), base.getDescriptionBaseHorairePointage());
			addZone(getNOM_EF_CODE_BASE_HORAIRE_POINTAGE(), base.getCodeBaseHorairePointage());
			addZone(getNOM_EF_HEURE_LUNDI(), base.getHeureLundi().toString());
			addZone(getNOM_EF_HEURE_MARDI(), base.getHeureMardi().toString());
			addZone(getNOM_EF_HEURE_MERCREDI(), base.getHeureMercredi().toString());
			addZone(getNOM_EF_HEURE_JEUDI(), base.getHeureJeudi().toString());
			addZone(getNOM_EF_HEURE_VENDREDI(), base.getHeureVendredi().toString());
			addZone(getNOM_EF_HEURE_SAMEDI(), base.getHeureSamedi().toString());
			addZone(getNOM_EF_HEURE_DIMANCHE(), base.getHeureDimanche().toString());

			if (base.getBaseLegale() != 0) {
				String avantPoint = base.getBaseLegale().toString()
						.substring(0, base.getBaseLegale().toString().indexOf("."));
				String apresPoint = base
						.getBaseLegale()
						.toString()
						.substring(base.getBaseLegale().toString().indexOf(".") + 1,
								base.getBaseLegale().toString().length());

				addZone(getNOM_EF_BASE_HEBDO_LEG_H(), avantPoint.equals("0") ? Const.CHAINE_VIDE : avantPoint);
				addZone(getNOM_EF_BASE_HEBDO_LEG_M(), apresPoint.equals("0") ? Const.CHAINE_VIDE
						: apresPoint.length() == 1 ? apresPoint + "0" : apresPoint);
			} else {
				addZone(getNOM_EF_BASE_HEBDO_LEG_H(), Const.CHAINE_VIDE);
				addZone(getNOM_EF_BASE_HEBDO_LEG_M(), Const.CHAINE_VIDE);

			}

			if (base.getBaseCalculee() != 0) {
				String avantPoint = base.getBaseCalculee().toString()
						.substring(0, base.getBaseCalculee().toString().indexOf("."));
				String apresPoint = base
						.getBaseCalculee()
						.toString()
						.substring(base.getBaseCalculee().toString().indexOf(".") + 1,
								base.getBaseCalculee().toString().length());

				addZone(getNOM_EF_BASE_HEBDO_H(), avantPoint.equals("0") ? Const.CHAINE_VIDE : avantPoint);
				addZone(getNOM_EF_BASE_HEBDO_M(), apresPoint.equals("0") ? Const.CHAINE_VIDE
						: apresPoint.length() == 1 ? apresPoint + "0" : apresPoint);
			} else {
				addZone(getNOM_EF_BASE_HEBDO_H(), Const.CHAINE_VIDE);
				addZone(getNOM_EF_BASE_HEBDO_M(), Const.CHAINE_VIDE);

			}

			addZone(getNOM_ST_ACTION_BASE_HORAIRE_POINTAGE(), ACTION_MODIFICATION);
			setFocus(getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE());
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "bases horaires de pointage"));
			setFocus(getDefaultFocus());
		}

		return true;
	}

	public String getNOM_PB_VALIDER_BASE_HORAIRE_POINTAGE() {
		return "NOM_PB_VALIDER_BASE_HORAIRE_POINTAGE";
	}

	public boolean performPB_VALIDER_BASE_HORAIRE_POINTAGE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieBaseHorairePointage(request))
			return false;

		if (!performControlerRegleGestionBaseHorairePointage(request))
			return false;

		if (getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE() != null
				&& getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE().equals(ACTION_CREATION)) {
				setBaseHorairePointageCourant(new BaseHorairePointage());
				getBaseHorairePointageCourant().setCodeBaseHorairePointage(getVAL_EF_CODE_BASE_HORAIRE_POINTAGE());
				getBaseHorairePointageCourant().setLibelleBaseHorairePointage(getVAL_EF_LIB_BASE_HORAIRE_POINTAGE());
				getBaseHorairePointageCourant().setDescriptionBaseHorairePointage(getVAL_EF_DESC_BASE_HORAIRE_POINTAGE());
				getBaseHorairePointageCourant().setHeureLundi(Double.valueOf(getVAL_EF_HEURE_LUNDI()));
				getBaseHorairePointageCourant().setHeureMardi(Double.valueOf(getVAL_EF_HEURE_MARDI()));
				getBaseHorairePointageCourant().setHeureMercredi(Double.valueOf(getVAL_EF_HEURE_MERCREDI()));
				getBaseHorairePointageCourant().setHeureJeudi(Double.valueOf(getVAL_EF_HEURE_JEUDI()));
				getBaseHorairePointageCourant().setHeureVendredi(Double.valueOf(getVAL_EF_HEURE_VENDREDI()));
				getBaseHorairePointageCourant().setHeureSamedi(Double.valueOf(getVAL_EF_HEURE_SAMEDI()));
				getBaseHorairePointageCourant().setHeureDimanche(Double.valueOf(getVAL_EF_HEURE_DIMANCHE()));

				String heureBaseLegale = getVAL_EF_BASE_HEBDO_LEG_H().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_H();
				String minuteBaseLegale = getVAL_EF_BASE_HEBDO_LEG_M().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_M();
				String totalBaseLegale = heureBaseLegale + ":" + minuteBaseLegale;
				getBaseHorairePointageCourant().setBaseLegale(getHeureDouble(totalBaseLegale));

				// on fait le calcul de la base legale
				Double nbHeureCalc = transformeHeure(getBaseHorairePointageCourant().getHeureLundi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureMardi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureMercredi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureJeudi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureVendredi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureSamedi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureDimanche());
				getBaseHorairePointageCourant().setBaseCalculee(transformeResultatHeure(nbHeureCalc));

				getBaseHorairePointageDao().creerBaseHorairePointage(getBaseHorairePointageCourant());

			} else if (getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE().equals(ACTION_MODIFICATION)) {
				getBaseHorairePointageCourant().setLibelleBaseHorairePointage(getVAL_EF_LIB_BASE_HORAIRE_POINTAGE());
				getBaseHorairePointageCourant().setDescriptionBaseHorairePointage(getVAL_EF_DESC_BASE_HORAIRE_POINTAGE());
				getBaseHorairePointageCourant().setHeureLundi(Double.valueOf(getVAL_EF_HEURE_LUNDI()));
				getBaseHorairePointageCourant().setHeureMardi(Double.valueOf(getVAL_EF_HEURE_MARDI()));
				getBaseHorairePointageCourant().setHeureMercredi(Double.valueOf(getVAL_EF_HEURE_MERCREDI()));
				getBaseHorairePointageCourant().setHeureJeudi(Double.valueOf(getVAL_EF_HEURE_JEUDI()));
				getBaseHorairePointageCourant().setHeureVendredi(Double.valueOf(getVAL_EF_HEURE_VENDREDI()));
				getBaseHorairePointageCourant().setHeureSamedi(Double.valueOf(getVAL_EF_HEURE_SAMEDI()));
				getBaseHorairePointageCourant().setHeureDimanche(Double.valueOf(getVAL_EF_HEURE_DIMANCHE()));

				String heureBaseLegale = getVAL_EF_BASE_HEBDO_LEG_H().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_H();
				String minuteBaseLegale = getVAL_EF_BASE_HEBDO_LEG_M().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_M();
				String totalBaseLegale = heureBaseLegale + ":" + minuteBaseLegale;
				getBaseHorairePointageCourant().setBaseLegale(getHeureDouble(totalBaseLegale));

				// on fait le calcul de la base legale
				Double nbHeureCalc = transformeHeure(getBaseHorairePointageCourant().getHeureLundi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureMardi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureMercredi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureJeudi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureVendredi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureSamedi())
						+ transformeHeure(getBaseHorairePointageCourant().getHeureDimanche());
				getBaseHorairePointageCourant().setBaseCalculee(transformeResultatHeure(nbHeureCalc));

				getBaseHorairePointageDao().modifierBaseHorairePointage(getBaseHorairePointageCourant());
				setBaseHorairePointageCourant(null);
			}

			initialiseListeBaseHorairePointage(request);
			addZone(getNOM_ST_ACTION_BASE_HORAIRE_POINTAGE(), Const.CHAINE_VIDE);
			setFocus(getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE());
		}

		return true;
	}

	private Double transformeResultatHeure(Double nbHeureCalc) {
		String nbHeure = nbHeureCalc.toString();
		String partieEntiere = nbHeure.substring(0, nbHeure.indexOf("."));
		String partieDecimale = nbHeure.substring(nbHeure.indexOf(".") + 1, nbHeure.length());
		if (partieDecimale.equals("25")) {
			partieDecimale = "15";
		} else if (partieDecimale.equals("5")) {
			partieDecimale = "3";
		} else if (partieDecimale.equals("75")) {
			partieDecimale = "45";
		}
		return Double.valueOf(partieEntiere + "." + partieDecimale);
	}

	private Double transformeHeure(Double nbheure) {
		// on transforme les 7.48 en heure
		String nbHeure = nbheure.toString();
		String partieEntiere = nbHeure.substring(0, nbHeure.indexOf("."));
		String partieDecimale = nbHeure.substring(nbHeure.indexOf(".") + 1, nbHeure.length());
		if (partieDecimale.length() == 1) {
			partieDecimale = partieDecimale + "0";
		}
		Integer res = (Integer.valueOf(partieEntiere) * 60) + Integer.valueOf(partieDecimale);
		return Double.valueOf(res) / 60;
	}

	private Double getHeureDouble(String heure) {
		Double res = Double.valueOf(heure.replace(":", "."));

		return res;
	}

	private boolean performControlerRegleGestionBaseHorairePointage(HttpServletRequest request) {
		// Vérification des contraintes d'unicité de la base horaire de pointage
		if (getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE().equals(ACTION_CREATION)) {

			for (BaseHorairePointage base : getListeBaseHorairePointage()) {
				if (base.getLibelleBaseHorairePointage().trim()
						.equals(getVAL_EF_LIB_BASE_HORAIRE_POINTAGE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une base horaire de pointage", "ce libellé"));
					return false;
				}
				if (base.getCodeBaseHorairePointage().trim()
						.equals(getVAL_EF_CODE_BASE_HORAIRE_POINTAGE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "une base horaire de pointage", "ce code"));
					return false;
				}
			}
		}
		return true;
	}

	private boolean performControlerSaisieBaseHorairePointage(HttpServletRequest request) {
		// Verification libellé not null
		if (getZone(getNOM_EF_LIB_BASE_HORAIRE_POINTAGE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification code not null
		if (getZone(getNOM_EF_CODE_BASE_HORAIRE_POINTAGE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		// Verification nb heure legale not null
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_H()).length() == 0 && getZone(getNOM_EF_BASE_HEBDO_LEG_M()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base légale hebdomadaire"));
			return false;
		}
		// Verification nb heure legale numerique
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_H()).length() != 0
				&& !Services.estNumerique(getZone(getNOM_EF_BASE_HEBDO_LEG_H()))) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "heures légale"));
			return false;
		}
		// Verification nb heure legale numerique
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_M()).length() != 0
				&& !Services.estNumerique(getZone(getNOM_EF_BASE_HEBDO_LEG_M()))) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "minutes légale"));
			return false;
		}

		return true;
	}

	public String getNOM_EF_CODE_BASE_HORAIRE_POINTAGE() {
		return "NOM_EF_CODE_BASE_HORAIRE_POINTAGE";
	}

	public String getVAL_EF_CODE_BASE_HORAIRE_POINTAGE() {
		return getZone(getNOM_EF_CODE_BASE_HORAIRE_POINTAGE());
	}

	public String getNOM_EF_LIB_BASE_HORAIRE_POINTAGE() {
		return "NOM_EF_LIB_BASE_HORAIRE_POINTAGE";
	}

	public String getVAL_EF_LIB_BASE_HORAIRE_POINTAGE() {
		return getZone(getNOM_EF_LIB_BASE_HORAIRE_POINTAGE());
	}

	public String getNOM_EF_DESC_BASE_HORAIRE_POINTAGE() {
		return "NOM_EF_DESC_BASE_HORAIRE_POINTAGE";
	}

	public String getVAL_EF_DESC_BASE_HORAIRE_POINTAGE() {
		return getZone(getNOM_EF_DESC_BASE_HORAIRE_POINTAGE());
	}

	public String getNOM_EF_HEURE_LUNDI() {
		return "NOM_EF_HEURE_LUNDI";
	}

	public String getVAL_EF_HEURE_LUNDI() {
		return getZone(getNOM_EF_HEURE_LUNDI());
	}

	public String getNOM_EF_HEURE_MARDI() {
		return "NOM_EF_HEURE_MARDI";
	}

	public String getVAL_EF_HEURE_MARDI() {
		return getZone(getNOM_EF_HEURE_MARDI());
	}

	public String getNOM_EF_HEURE_MERCREDI() {
		return "NOM_EF_HEURE_MERCREDI";
	}

	public String getVAL_EF_HEURE_MERCREDI() {
		return getZone(getNOM_EF_HEURE_MERCREDI());
	}

	public String getNOM_EF_HEURE_JEUDI() {
		return "NOM_EF_HEURE_JEUDI";
	}

	public String getVAL_EF_HEURE_JEUDI() {
		return getZone(getNOM_EF_HEURE_JEUDI());
	}

	public String getNOM_EF_HEURE_VENDREDI() {
		return "NOM_EF_HEURE_VENDREDI";
	}

	public String getVAL_EF_HEURE_VENDREDI() {
		return getZone(getNOM_EF_HEURE_VENDREDI());
	}

	public String getNOM_EF_HEURE_SAMEDI() {
		return "NOM_EF_HEURE_SAMEDI";
	}

	public String getVAL_EF_HEURE_SAMEDI() {
		return getZone(getNOM_EF_HEURE_SAMEDI());
	}

	public String getNOM_EF_HEURE_DIMANCHE() {
		return "NOM_EF_HEURE_DIMANCHE";
	}

	public String getVAL_EF_HEURE_DIMANCHE() {
		return getZone(getNOM_EF_HEURE_DIMANCHE());
	}

	public String getNOM_EF_BASE_HEBDO_LEG_H() {
		return "NOM_EF_BASE_HEBDO_LEG_H";
	}

	public String getVAL_EF_BASE_HEBDO_LEG_H() {
		return getZone(getNOM_EF_BASE_HEBDO_LEG_H());
	}

	public String getNOM_EF_BASE_HEBDO_LEG_M() {
		return "NOM_EF_BASE_HEBDO_LEG_M";
	}

	public String getVAL_EF_BASE_HEBDO_LEG_M() {
		return getZone(getNOM_EF_BASE_HEBDO_LEG_M());
	}

	public String getNOM_EF_BASE_HEBDO_H() {
		return "NOM_EF_BASE_HEBDO_H";
	}

	public String getVAL_EF_BASE_HEBDO_H() {
		return getZone(getNOM_EF_BASE_HEBDO_H());
	}

	public String getNOM_EF_BASE_HEBDO_M() {
		return "NOM_EF_BASE_HEBDO_M";
	}

	public String getVAL_EF_BASE_HEBDO_M() {
		return getZone(getNOM_EF_BASE_HEBDO_M());
	}

	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_PB_ANNULER_ENTITE_GEO();
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}
}
