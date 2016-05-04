package nc.mairie.gestionagent.process.agent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.connecteur.Connecteur;
import nc.mairie.enums.EnumImpressionAffectation;
import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.enums.EnumTempsTravail;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.agent.SISERV;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.parametrage.BaseHorairePointage;
import nc.mairie.metier.parametrage.MotifAffectation;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.HistoAffectation;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.AvantageNatureAFF;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.DelegationAFF;
import nc.mairie.metier.specificites.PrimePointageAff;
import nc.mairie.metier.specificites.PrimePointageFP;
import nc.mairie.metier.specificites.RegIndemnAFF;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.agent.SISERVDao;
import nc.mairie.spring.dao.metier.parametrage.BaseHorairePointageDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAffectationDao;
import nc.mairie.spring.dao.metier.parametrage.NatureAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDelegationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeRegIndemnDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.HistoAffectationDao;
import nc.mairie.spring.dao.metier.poste.HistoFichePosteDao;
import nc.mairie.spring.dao.metier.poste.StatutFPDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureAffDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureDao;
import nc.mairie.spring.dao.metier.specificites.DelegationAffDao;
import nc.mairie.spring.dao.metier.specificites.DelegationDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageAffDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnAffDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnDao;
import nc.mairie.spring.dao.metier.specificites.RubriqueDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;
import nc.noumea.spring.service.PtgService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTEmploisAffectation Date de création : (04/08/11 15:20:56)
 * 
 */
public class OeAGENTEmploisAffectation extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OeAGENTEmploisAffectation.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_HISTORIQUE = 3;
	public static final int STATUT_RECHERCHE_FP = 1;
	public static final int STATUT_RECHERCHE_FP_SECONDAIRE = 4;

	private final String ACTION_AJOUTER_SPEC = "Ajouter";
	public String ACTION_CONSULTATION = "Consultation d'une affectation";
	public String ACTION_CREATION = "Création d'une affectation";

	public String ACTION_IMPRESSION = "Impression des documents liés a une affectation";
	public String ACTION_MODIFICATION = "Modification d'une affectation";
	public String ACTION_SUPPRESSION = "Suppression d'une affectation";

	public final String ACTION_SUPPRIMER_SPEC = "Supprimer";
	private Affectation affectationCourant;
	private Agent agentCourant;
	private FichePoste fichePosteCourant;
	private FichePoste fichePosteSecondaireCourant;

	public String focus = null;
	private String[] LB_LISTE_IMPRESSION;
	private String[] LB_MOTIF_AFFECTATION;
	private String[] LB_NATURE_AVANTAGE;
	private String[] LB_RUBRIQUE_AVANTAGE;
	private String[] LB_RUBRIQUE_PRIME_POINTAGE;
	private String[] LB_RUBRIQUE_REGIME;
	private String[] LB_TEMPS_TRAVAIL;
	private String[] LB_TYPE_AVANTAGE;
	private String[] LB_TYPE_DELEGATION;
	private String[] LB_BASE_HORAIRE_POINTAGE;
	private String[] LB_BASE_HORAIRE_ABSENCE;
	private String[] LB_TYPE_REGIME;

	private ArrayList<BaseHorairePointage> listeBaseHorairePointage = new ArrayList<>();
	private ArrayList<RefTypeSaisiCongeAnnuelDto> listeBaseHoraireAbsence = new ArrayList<>();

	private ArrayList<Affectation> listeAffectation = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageAAjouter = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageAFF = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageASupprimer = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageFP = new ArrayList<>();
	private ArrayList<Delegation> listeDelegationAAjouter = new ArrayList<>();

	private ArrayList<Delegation> listeDelegationAFF = new ArrayList<>();
	private ArrayList<Delegation> listeDelegationASupprimer = new ArrayList<>();
	private ArrayList<Delegation> listeDelegationFP = new ArrayList<>();
	private ArrayList<MotifAffectation> listeMotifAffectation = new ArrayList<>();
	private ArrayList<NatureAvantage> listeNatureAvantage = new ArrayList<>();

	private ArrayList<PrimePointageAff> listePrimePointageAFF;
	private ArrayList<PrimePointageAff> listePrimePointageAffAAjouter = new ArrayList<>();
	private ArrayList<PrimePointageAff> listePrimePointageAffASupprimer = new ArrayList<>();
	private ArrayList<PrimePointageFP> listePrimePointageFP = new ArrayList<>();

	private List<RefPrimeDto> listePrimes = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeAAjouter = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeAFF = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeASupprimer = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeFP = new ArrayList<>();
	private List<Rubrique> listeRubrique = new ArrayList<>();
	private String[] listeTempsTravail;

	private ArrayList<TypeAvantage> listeTypeAvantage = new ArrayList<>();
	private ArrayList<TypeDelegation> listeTypeDelegation = new ArrayList<>();
	private ArrayList<TypeRegIndemn> listeTypeRegIndemn = new ArrayList<>();
	private PrimePointageAffDao primePointageAffDao;
	private PrimePointageFPDao primePointageFPDao;
	public final String SPEC_AVANTAGE_NATURE_SPEC = "avantage en nature";

	public final String SPEC_DELEGATION_SPEC = "délégation";
	public final String SPEC_PRIME_POINTAGE_SPEC = "prime pointage";

	public final String SPEC_REG_INDEMN_SPEC = "régime indemnitaire";
	private String urlFichier;

	private MotifAffectationDao motifAffectationDao;
	private NatureAvantageDao natureAvantageDao;
	private TypeAvantageDao typeAvantageDao;
	private TypeDelegationDao typeDelegationDao;
	private TypeRegIndemnDao typeRegIndemnDao;
	private AvantageNatureAffDao avantageNatureAffDao;
	private AvantageNatureDao avantageNatureDao;
	private DelegationDao delegationDao;
	private DelegationAffDao delegationAffDao;
	private RegIndemnAffDao regIndemnAffDao;
	private RegIndemnDao regIndemnDao;
	private RubriqueDao rubriqueDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private TitrePosteDao titrePosteDao;
	private FichePosteDao fichePosteDao;
	private StatutFPDao statutFPDao;
	private HistoFichePosteDao histoFichePosteDao;
	private HistoAffectationDao histoAffectationDao;
	private AffectationDao affectationDao;
	private BaseHorairePointageDao baseHorairePointageDao;
	private AgentDao agentDao;
	private SISERVDao siservDao;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private IAdsService adsService;

	private IRadiService radiService;

	private IAbsService absService;

	private IPtgService ptgService;

	private ISirhService sirhService;

	/**
	 * Constructeur du process OeAGENTEmploisAffectation. Date de création :
	 * (11/08/11 16:10:24)
	 * 
	 */
	public OeAGENTEmploisAffectation() {
		super();
	}

	/**
	 * Retourne l'affectation en cours.
	 * 
	 * @return affectationCourant
	 */
	public Affectation getAffectationCourant() {
		return affectationCourant;
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public Agent getAgentCourant() {
		return agentCourant;
	}

	public String getDefaultFocus() {
		return getNOM_EF_REF_ARRETE();
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return fichePosteCourant
	 */
	private FichePoste getFichePosteCourant() {
		return fichePosteCourant;
	}

	/**
	 * Retourne la fiche de poste secondaire courante.
	 * 
	 * @return fichePosteSecondaireCourant
	 */
	private FichePoste getFichePosteSecondaireCourant() {
		return fichePosteSecondaireCourant;
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

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 16:45:31)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEmploisAffectation.jsp";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_LISTE_IMPRESSION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	private String[] getLB_LISTE_IMPRESSION() {
		if (LB_LISTE_IMPRESSION == null)
			LB_LISTE_IMPRESSION = initialiseLazyLB();
		return LB_LISTE_IMPRESSION;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF_AFFECTATION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	private String[] getLB_MOTIF_AFFECTATION() {
		if (LB_MOTIF_AFFECTATION == null)
			LB_MOTIF_AFFECTATION = initialiseLazyLB();
		return LB_MOTIF_AFFECTATION;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_AVANTAGE() {
		if (LB_RUBRIQUE_AVANTAGE == null)
			LB_RUBRIQUE_AVANTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_PRIME_POINTAGE Date
	 * de création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_PRIME_POINTAGE() {
		if (LB_RUBRIQUE_PRIME_POINTAGE == null)
			LB_RUBRIQUE_PRIME_POINTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_REGIME() {
		if (LB_RUBRIQUE_REGIME == null)
			LB_RUBRIQUE_REGIME = initialiseLazyLB();
		return LB_RUBRIQUE_REGIME;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TEMPS_TRAVAIL Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	private String[] getLB_TEMPS_TRAVAIL() {
		if (LB_TEMPS_TRAVAIL == null)
			LB_TEMPS_TRAVAIL = initialiseLazyLB();
		return LB_TEMPS_TRAVAIL;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_REGIME() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Retourne la liste des affectations de l'agent
	 * 
	 * @return listeAffectation
	 */
	public ArrayList<Affectation> getListeAffectation() {
		return listeAffectation;
	}

	/**
	 * Retourne la liste des avantages en nature a ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	public ArrayList<AvantageNature> getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null)
			listeAvantageAAjouter = new ArrayList<AvantageNature>();
		return listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des AvantageNature de l'affectation.
	 * 
	 * @return listeAvantageAFF
	 */
	public ArrayList<AvantageNature> getListeAvantageAFF() {
		return listeAvantageAFF;
	}

	/**
	 * Retourne la liste des avantages en nature a supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList<AvantageNature> getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null)
			listeAvantageASupprimer = new ArrayList<AvantageNature>();
		return listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des AvantageNature de la fiche de poste.
	 * 
	 * @return listeAvantageFP
	 */
	public ArrayList<AvantageNature> getListeAvantageFP() {
		return listeAvantageFP;
	}

	/**
	 * Retourne la liste des délégations a ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	public ArrayList<Delegation> getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null)
			listeDelegationAAjouter = new ArrayList<Delegation>();
		return listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des Delegation de l'affectation.
	 * 
	 * @return listeDelegationAFF
	 */
	public ArrayList<Delegation> getListeDelegationAFF() {
		return listeDelegationAFF;
	}

	/**
	 * Retourne la liste des délégations a supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList<Delegation> getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null)
			listeDelegationASupprimer = new ArrayList<Delegation>();
		return listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des Delegation de la fiche de poste.
	 * 
	 * @return listeDelegationFP
	 */
	public ArrayList<Delegation> getListeDelegationFP() {
		return listeDelegationFP;
	}

	/**
	 * Retourne la liste des motifs d'affectation.
	 * 
	 * @return listeMotifAffectation
	 */
	private ArrayList<MotifAffectation> getListeMotifAffectation() {
		return listeMotifAffectation;
	}

	/**
	 * Retourne la liste des natures d'avantage en nature.
	 * 
	 * @return listeNatureAvantage
	 */
	private ArrayList<NatureAvantage> getListeNatureAvantage() {
		return listeNatureAvantage;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire de l'affectation.
	 * 
	 * @return listePrimePointageAFF
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAFF() {
		return listePrimePointageAFF;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a ajouter.
	 * 
	 * @return listePrimePointageAAjouter
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAffAAjouter() {
		if (listePrimePointageAffAAjouter == null)
			listePrimePointageAffAAjouter = new ArrayList<PrimePointageAff>();
		return listePrimePointageAffAAjouter;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a supprimer.
	 * 
	 * @return listePrimePointageASupprimer
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAffASupprimer() {
		if (listePrimePointageAffASupprimer == null)
			listePrimePointageAffASupprimer = new ArrayList<PrimePointageAff>();
		return listePrimePointageAffASupprimer;
	}

	/**
	 * Retourne la liste des PrimePointage de la fiche de poste.
	 * 
	 * @return listePrimePointageFP
	 */
	public ArrayList<PrimePointageFP> getListePrimePointageFP() {
		return listePrimePointageFP == null ? new ArrayList<PrimePointageFP>() : listePrimePointageFP;
	}

	/**
	 * Retourne la liste des primes.
	 * 
	 * @return listeRubrique
	 */
	private List<RefPrimeDto> getListePrimes() {
		return listePrimes;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null)
			listeRegimeAAjouter = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire de l'affectation.
	 * 
	 * @return listeRegimeAFF
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegimeAFF() {
		return listeRegimeAFF;
	}

	/**
	 * Retourne la liste des régimes indemnitaires a supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null)
			listeRegimeASupprimer = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeASupprimer;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire de la fiche de poste.
	 * 
	 * @return listeRegimeFP
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegimeFP() {
		return listeRegimeFP;
	}

	/**
	 * Retourne la liste des rubriques.
	 * 
	 * @return listeRubrique
	 */
	private List<Rubrique> getListeRubrique() {
		return listeRubrique;
	}

	public ArrayList<Integer> getListeRubs() {
		ArrayList<Integer> ret = new ArrayList<>();

		if (getListePrimePointageAFF() != null) {
			for (PrimePointageAff p : getListePrimePointageAFF()) {
				ret.add(p.getNumRubrique());
			}
		}
		if (getListePrimePointageAffAAjouter() != null) {
			for (PrimePointageAff p : getListePrimePointageAffAAjouter()) {
				ret.add(p.getNumRubrique());

			}
		}
		return ret;
	}

	/**
	 * Retourne la liste des temps de travail (exprimés en pourcentage).
	 * 
	 * @return listeTempsTravail
	 */
	private String[] getListeTempsTravail() {
		return listeTempsTravail;
	}

	/**
	 * Retourne la liste des types d'avantage en nature.
	 * 
	 * @return listeTypeAvantage
	 */
	private ArrayList<TypeAvantage> getListeTypeAvantage() {
		return listeTypeAvantage;
	}

	/**
	 * Retourne la liste des TypeDelegation.
	 * 
	 * @return listeTypeDelegation
	 */
	private ArrayList<TypeDelegation> getListeTypeDelegation() {
		return listeTypeDelegation;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeTypeRegIndemn
	 */
	private ArrayList<TypeRegIndemn> getListeTypeRegIndemn() {
		return listeTypeRegIndemn;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENT_DELEGATION
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_COMMENT_DELEGATION() {
		return "NOM_EF_COMMENT_DELEGATION";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENTAIRE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE() {
		return "NOM_EF_DATE_ARRETE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_FORFAIT_REGIME Date
	 * de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_FORFAIT_REGIME() {
		return "NOM_EF_FORFAIT_REGIME";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_AVANTAGE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_MONTANT_AVANTAGE() {
		return "NOM_EF_MONTANT_AVANTAGE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_POINTS_REGIME
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_NB_POINTS_REGIME() {
		return "NOM_EF_NB_POINTS_REGIME";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_ARRETE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_REF_ARRETE() {
		return "NOM_EF_REF_ARRETE";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_LISTE_IMPRESSION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_LISTE_IMPRESSION() {
		return "NOM_LB_LISTE_IMPRESSION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_LISTE_IMPRESSION_SELECT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_LISTE_IMPRESSION_SELECT() {
		return "NOM_LB_LISTE_IMPRESSION_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF_AFFECTATION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_MOTIF_AFFECTATION() {
		return "NOM_LB_MOTIF_AFFECTATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_AFFECTATION_SELECT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_MOTIF_AFFECTATION_SELECT() {
		return "NOM_LB_MOTIF_AFFECTATION_SELECT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE() {
		return "NOM_LB_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_AVANTAGE_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_AVANTAGE_SELECT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_PRIME_POINTAGE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME() {
		return "NOM_LB_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_REGIME_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME_SELECT() {
		return "NOM_LB_RUBRIQUE_REGIME_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TEMPS_TRAVAIL Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_TEMPS_TRAVAIL() {
		return "NOM_LB_TEMPS_TRAVAIL";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TEMPS_TRAVAIL_SELECT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_TEMPS_TRAVAIL_SELECT() {
		return "NOM_LB_TEMPS_TRAVAIL_SELECT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE() {
		return "NOM_LB_TYPE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT() {
		return "NOM_LB_TYPE_DELEGATION_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION() {
		return "NOM_LB_TYPE_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT() {
		return "NOM_LB_TYPE_REGIME_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME() {
		return "NOM_LB_TYPE_REGIME";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_AVANTAGE() {
		return "NOM_PB_AJOUTER_AVANTAGE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DELEGATION() {
		return "NOM_PB_AJOUTER_DELEGATION";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_PRIME_POINTAGE Date
	 * de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_PRIME_POINTAGE() {
		return "NOM_PB_AJOUTER_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_REGIME() {
		return "NOM_PB_AJOUTER_REGIME";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_SPECIFICITE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_CHANGER_SPECIFICITE() {
		return "NOM_PB_CHANGER_SPECIFICITE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_HISTORIQUE Date de création
	 * : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_HISTORIQUE() {
		return "NOM_PB_HISTORIQUE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER
	 */
	public String getNOM_PB_IMPRIMER(int i) {
		return "NOM_PB_IMPRIMER" + i;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP Date de
	 * création : (05/08/11 13:35:40)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP() {
		return "NOM_PB_RECHERCHER_FP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP_SECONDAIRE
	 * Date de création : (05/08/11 13:35:40)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP_SECONDAIRE() {
		return "NOM_PB_RECHERCHER_FP_SECONDAIRE";
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
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AVANTAGE(int i) {
		return "NOM_PB_SUPPRIMER_AVANTAGE" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELEGATION(int i) {
		return "NOM_PB_SUPPRIMER_DELEGATION" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_PRIME_POINTAGE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_PRIME_POINTAGE(int i) {
		return "NOM_PB_SUPPRIMER_PRIME_POINTAGE" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REGIME(int i) {
		return "NOM_PB_SUPPRIMER_REGIME" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AJOUT Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_VALIDER_AJOUT() {
		return "NOM_PB_VALIDER_AJOUT";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_AN Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_AN() {
		return "NOM_RB_SPECIFICITE_AN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_D Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_D() {
		return "NOM_RB_SPECIFICITE_D";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_PP Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_PP() {
		return "NOM_RB_SPECIFICITE_PP";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_RI Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_RI() {
		return "NOM_RB_SPECIFICITE_RI";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_SPECIFICITE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RG_SPECIFICITE() {
		return "NOM_RG_SPECIFICITE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (04/08/11 15:42:46)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_ST_ACTION_spec() {
		return "NOM_ST_ACTION_spec";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIR Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DIR(int i) {
		return "NOM_ST_DIR" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_DIRECTION() {
		return "NOM_ST_DIRECTION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_DIRECTION_SECONDAIRE() {
		return "NOM_ST_DIRECTION_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIEU_FP Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_LIEU_FP() {
		return "NOM_ST_LIEU_FP";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIEU_FP_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_LIEU_FP_SECONDAIRE() {
		return "NOM_ST_LIEU_FP_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_MONTANT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_MONTANT(int i) {
		return "NOM_ST_LST_AVANTAGE_MONTANT" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_NATURE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_NATURE(int i) {
		return "NOM_ST_LST_AVANTAGE_NATURE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_AVANTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_RUBRIQUE(int i) {
		return "NOM_ST_LST_AVANTAGE_RUBRIQUE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_TYPE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_TYPE(int i) {
		return "NOM_ST_LST_AVANTAGE_TYPE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_DELEGATION_COMMENT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_DELEGATION_COMMENT(int i) {
		return "NOM_ST_LST_DELEGATION_COMMENT" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_DELEGATION_TYPE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_DELEGATION_TYPE(int i) {
		return "NOM_ST_LST_DELEGATION_TYPE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_PRIME_POINTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE(int i) {
		return "NOM_ST_LST_PRIME_POINTAGE_RUBRIQUE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_FORFAIT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_FORFAIT(int i) {
		return "NOM_ST_LST_REGINDEMN_FORFAIT" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_NB_POINTS Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_NB_POINTS(int i) {
		return "NOM_ST_LST_REGINDEMN_NB_POINTS" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_RUBRIQUE(int i) {
		return "NOM_ST_LST_REGINDEMN_RUBRIQUE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_REGINDEMN_TYPE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_TYPE(int i) {
		return "NOM_ST_LST_REGINDEMN_TYPE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FICHE_POSTE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_NUM_FICHE_POSTE() {
		return "NOM_ST_NUM_FICHE_POSTE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FICHE_POSTE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE() {
		return "NOM_ST_NUM_FICHE_POSTE_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FP Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM_FP(int i) {
		return "NOM_ST_NUM_FP" + i;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SERVICE_SECONDAIRE() {
		return "NOM_ST_SERVICE_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SPECIFICITE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_ST_SPECIFICITE() {
		return "NOM_ST_SPECIFICITE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SUBDIVISION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SUBDIVISION() {
		return "NOM_ST_SUBDIVISION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_SUBDIVISION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SUBDIVISION_SECONDAIRE() {
		return "NOM_ST_SUBDIVISION_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TITRE(int i) {
		return "NOM_ST_TITRE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_FP Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TITRE_FP() {
		return "NOM_ST_TITRE_FP";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_FP_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TITRE_FP_SECONDAIRE() {
		return "NOM_ST_TITRE_FP_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_REG Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TPS_REG() {
		return "NOM_ST_TPS_REG";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_REG_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TPS_REG_SECONDAIRE() {
		return "NOM_ST_TPS_REG_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_WARNING Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	public String getNomEcran() {
		return "ECR-AG-EMPLOIS-AFFECTATIONS";
	}

	public String getVAL_PB_SET_PRIME_POINTAGE(int i) {
		return getZone(getNOM_PB_SET_PRIME_POINTAGE(i));
	}

	public String getNOM_PB_SET_PRIME_POINTAGE(int i) {
		return "NOM_PB_SET_PRIME_POINTAGE_" + i;
	}

	/***
	 * methods called by jsp
	 * 
	 * @return String
	 */
	public String getPosteCourantTitle() {
		return fichePosteCourant.getNumFp();
	}

	public PrimePointageAffDao getPrimePointageAffDao() {
		return primePointageAffDao;
	}

	public PrimePointageFPDao getPrimePointageFPDao() {
		return primePointageFPDao;
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

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENT_DELEGATION Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_COMMENT_DELEGATION() {
		return getZone(getNOM_EF_COMMENT_DELEGATION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENTAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE() {
		return getZone(getNOM_EF_DATE_ARRETE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_FORFAIT_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_FORFAIT_REGIME() {
		return getZone(getNOM_EF_FORFAIT_REGIME());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_MONTANT_AVANTAGE() {
		return getZone(getNOM_EF_MONTANT_AVANTAGE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NB_POINTS_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_NB_POINTS_REGIME() {
		return getZone(getNOM_EF_NB_POINTS_REGIME());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_ARRETE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_REF_ARRETE() {
		return getZone(getNOM_EF_REF_ARRETE());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_LISTE_IMPRESSION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_LISTE_IMPRESSION() {
		return getLB_LISTE_IMPRESSION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_LISTE_IMPRESSION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_LISTE_IMPRESSION_SELECT() {
		return getZone(getNOM_LB_LISTE_IMPRESSION_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF_AFFECTATION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_MOTIF_AFFECTATION() {
		return getLB_MOTIF_AFFECTATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MOTIF_AFFECTATION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_MOTIF_AFFECTATION_SELECT() {
		return getZone(getNOM_LB_MOTIF_AFFECTATION_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_AVANTAGE() {
		return getLB_RUBRIQUE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_PRIME_POINTAGE() {
		return getLB_RUBRIQUE_PRIME_POINTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_REGIME() {
		return getLB_RUBRIQUE_REGIME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_REGIME_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_REGIME_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TEMPS_TRAVAIL Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_TEMPS_TRAVAIL() {
		return getLB_TEMPS_TRAVAIL();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TEMPS_TRAVAIL Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_TEMPS_TRAVAIL_SELECT() {
		return getZone(getNOM_LB_TEMPS_TRAVAIL_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION() {
		return getLB_TYPE_DELEGATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_REGIME_SELECT() {
		return getZone(getNOM_LB_TYPE_REGIME_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME() {
		return getLB_TYPE_REGIME();
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_SPECIFICITE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_RG_SPECIFICITE() {
		return getZone(getNOM_RG_SPECIFICITE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (04/08/11 15:42:46)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_ST_ACTION_spec() {
		return getZone(getNOM_ST_ACTION_spec());
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DIR(int i) {
		return getZone(getNOM_ST_DIR(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_DIRECTION() {
		return getZone(getNOM_ST_DIRECTION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DIRECTION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_DIRECTION_SECONDAIRE() {
		return getZone(getNOM_ST_DIRECTION_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIEU_FP Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_LIEU_FP() {
		return getZone(getNOM_ST_LIEU_FP());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIEU_FP_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_LIEU_FP_SECONDAIRE() {
		return getZone(getNOM_ST_LIEU_FP_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_MONTANT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_MONTANT(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_MONTANT(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_NATURE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_NATURE(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_NATURE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_RUBRIQUE(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_TYPE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_TYPE(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_TYPE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_DELEGATION_COMMENT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_DELEGATION_COMMENT(int i) {
		return getZone(getNOM_ST_LST_DELEGATION_COMMENT(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_DELEGATION_TYPE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_DELEGATION_TYPE(int i) {
		return getZone(getNOM_ST_LST_DELEGATION_TYPE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_PRIME_POINTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE(int i) {
		return getZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_FORFAIT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_FORFAIT(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_FORFAIT(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_NB_POINTS Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_NB_POINTS(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_RUBRIQUE(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_TYPE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_TYPE(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_TYPE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_FICHE_POSTE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_NUM_FICHE_POSTE() {
		return getZone(getNOM_ST_NUM_FICHE_POSTE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_FICHE_POSTE_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE() {
		return getZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_FP Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM_FP(int i) {
		return getZone(getNOM_ST_NUM_FP(i));
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_SERVICE_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SERVICE_SECONDAIRE() {
		return getZone(getNOM_ST_SERVICE_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SPECIFICITE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_ST_SPECIFICITE() {
		return getZone(getNOM_ST_SPECIFICITE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SUBDIVISION
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SUBDIVISION() {
		return getZone(getNOM_ST_SUBDIVISION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_SUBDIVISION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SUBDIVISION_SECONDAIRE() {
		return getZone(getNOM_ST_SUBDIVISION_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TITRE(int i) {
		return getZone(getNOM_ST_TITRE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE_FP Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TITRE_FP() {
		return getZone(getNOM_ST_TITRE_FP());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TITRE_FP_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TITRE_FP_SECONDAIRE() {
		return getZone(getNOM_ST_TITRE_FP_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TPS_REG Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TPS_REG() {
		return getZone(getNOM_ST_TPS_REG());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TPS_REG_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TPS_REG_SECONDAIRE() {
		return getZone(getNOM_ST_TPS_REG_SECONDAIRE());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	private boolean imprimeModele(HttpServletRequest request, String typeDocument) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("NS");

		Affectation aff = getAffectationCourant();
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destination = "NS/NS_" + aff.getIdAffectation() + "_" + typeDocument + ".doc";

		// si le fichier existe alors on supprime l'entrée ou il y a le fichier
		if (verifieExistFichier(aff.getIdAffectation(), typeDocument)) {
			Document d = getDocumentDao().chercherDocumentByContainsNom("NS_" + aff.getIdAffectation() + "_" + typeDocument);
			DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(), d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
			getDocumentDao().supprimerDocument(d.getIdDocument());
		}

		try {
			byte[] fileAsBytes = null;
			if (typeDocument.equals("Interne")) {
				fileAsBytes = sirhService.downloadNoteService(getAffectationCourant().getIdAffectation(), null);
			} else {
				fileAsBytes = sirhService.downloadNoteService(getAffectationCourant().getIdAffectation(), typeDocument);
			}

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

			// Tout s'est bien passé
			// on crée le document en base de données
			Document d = new Document();
			d.setIdTypeDocument(3);
			d.setLienDocument(destination);
			d.setNomDocument("NS_" + aff.getIdAffectation() + "_" + typeDocument + ".doc");
			d.setDateDocument(new Date());
			d.setCommentaire("Document généré par l'application");
			Integer id = getDocumentDao().creerDocument(d.getClasseDocument(), d.getNomDocument(), d.getLienDocument(), d.getDateDocument(), d.getCommentaire(), d.getIdTypeDocument(),
					d.getNomOriginal());

			DocumentAgent lda = new DocumentAgent();
			lda.setIdAgent(getAgentCourant().getIdAgent());
			lda.setIdDocument(id);
			getLienDocumentAgentDao().creerDocumentAgent(lda.getIdAgent(), lda.getIdDocument());

			if (getTransaction().isErreur())
				return false;

			destination = destination.substring(destination.lastIndexOf("/"), destination.length());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
			setURLFichier(getScriptOuverture(repertoireStockage + "NS" + destination));

			commitTransaction();

		} catch (Exception e) {
			// "ERR185",
			// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
			return false;
		}

		return true;
	}

	/**
	 * Initialise les zones de l'affectation courant RG_AG_AF_A04 RG_AG_AF_A12
	 */
	private boolean initialiseAffectationCourante(HttpServletRequest request) throws Exception {

		if (getAffectationCourant() == null || getAffectationCourant().getIdAffectation() == null) {
			if (getFichePosteCourant() == null || getFichePosteCourant().getIdFichePoste() == null) {
				initialiseAffectationVide();
			}
			// Init Fiche de poste
			// RG_AG_AF_A04
			// RG_AG_AF_A12
			if (etatStatut() == STATUT_RECHERCHE_FP) {
				FichePoste fp = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fp != null) {
					setFichePosteCourant(fp);
					initialiserFichePoste();
				} else if (getFichePosteSecondaireCourant() == null && getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste()));
					initialiserFichePoste();
				}
			}
			// Init Fiche de poste secondaire
			if (etatStatut() == STATUT_RECHERCHE_FP_SECONDAIRE) {
				FichePoste fpSecondaire = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fpSecondaire != null) {
					setFichePosteSecondaireCourant(fpSecondaire);
					initialiserFichePosteSecondaire();
				} else if (getFichePosteSecondaireCourant() == null && getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			}
		} else {
			// Init Fiche de poste
			// RG_AG_AF_A04
			// RG_AG_AF_A12
			if (etatStatut() == STATUT_RECHERCHE_FP) {
				FichePoste fp = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fp != null) {
					setFichePosteCourant(fp);
					initialiserFichePoste();
				} else if (getFichePosteSecondaireCourant() == null && getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste()));
					initialiserFichePoste();
				}
			} else {
				if (getAffectationCourant().getIdFichePoste() != null) {
					FichePoste fp = getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste());

					setFichePosteCourant(fp);
					initialiserFichePoste();
				}
			}
			// Init Fiche de poste secondaire
			if (etatStatut() == STATUT_RECHERCHE_FP_SECONDAIRE) {
				FichePoste fpSecondaire = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fpSecondaire != null) {
					setFichePosteSecondaireCourant(fpSecondaire);
					initialiserFichePosteSecondaire();
				} else if (getFichePosteSecondaireCourant() == null && getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			} else {
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			}
		}

		if (getAffectationCourant() != null) {
			// Récup du motif d'affectation et temps de travail
			if (getAffectationCourant().getIdMotifAffectation() != null) {
				MotifAffectation ma = getMotifAffectationDao().chercherMotifAffectation(getAffectationCourant().getIdMotifAffectation());
				addZone(getNOM_LB_MOTIF_AFFECTATION_SELECT(), String.valueOf(getListeMotifAffectation().indexOf(ma)));
				for (int i = 0; i < getListeTempsTravail().length; i++) {
					String tpsT = getListeTempsTravail()[i];
					if (tpsT.equals(getAffectationCourant().getTempsTravail()))
						addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), String.valueOf(i));
				}
			}

			// Récup base horaire pointage
			if (getAffectationCourant().getIdBaseHorairePointage() != null) {
				BaseHorairePointage ma = getBaseHorairePointageDao().chercherBaseHorairePointage(getAffectationCourant().getIdBaseHorairePointage());
				addZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT(), String.valueOf(getListeBaseHorairePointage().indexOf(ma) + 1));
			} else {
				addZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT(), Const.ZERO);
			}
			// on affiche la base horaire de la FDP
			if (getFichePosteCourant() != null && getFichePosteCourant().getIdBaseHorairePointage() != null) {
				BaseHorairePointage ma = getBaseHorairePointageDao().chercherBaseHorairePointage(getFichePosteCourant().getIdBaseHorairePointage());
				addZone(getNOM_EF_INFO_POINTAGE_FDP(), "FDP : " + ma.getCodeBaseHorairePointage());
			} else {
				addZone(getNOM_EF_INFO_POINTAGE_FDP(), "FDP : non renseigné");
			}

			// Récup base horaire absence
			if (getAffectationCourant().getIdBaseHoraireAbsence() != null) {
				RefTypeSaisiCongeAnnuelDto dto = new RefTypeSaisiCongeAnnuelDto();
				dto.setIdRefTypeSaisiCongeAnnuel(getAffectationCourant().getIdBaseHoraireAbsence());
				addZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT(), String.valueOf(getListeBaseHoraireAbsence().indexOf(dto) + 1));
			} else {
				addZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT(), Const.ZERO);
			}
			// on affiche la base horaire de la FDP
			if (getFichePosteCourant() != null && getFichePosteCourant().getIdBaseHoraireAbsence() != null) {
				RefTypeSaisiCongeAnnuelDto dto = new RefTypeSaisiCongeAnnuelDto();
				dto.setIdRefTypeSaisiCongeAnnuel(getFichePosteCourant().getIdBaseHoraireAbsence());
				RefTypeSaisiCongeAnnuelDto base = getListeBaseHoraireAbsence().get(getListeBaseHoraireAbsence().indexOf(dto));
				addZone(getNOM_EF_INFO_ABSENCE_FDP(), "FDP : " + base.getCodeBaseHoraireAbsence());
			} else {
				addZone(getNOM_EF_INFO_ABSENCE_FDP(), "FDP : non renseigné");
			}

		}
		return true;
	}

	/**
	 * Initialise a vide les zones de l'affectation.
	 */
	private void initialiseAffectationVide() {
		addZone(getNOM_ST_DIRECTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SUBDIVISION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TPS_REG(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_NUM_FICHE_POSTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TITRE_FP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIEU_FP(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_DIRECTION_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SERVICE_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SUBDIVISION_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TPS_REG_SECONDAIRE(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TITRE_FP_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIEU_FP_SECONDAIRE(), Const.CHAINE_VIDE);

		addZone(getNOM_EF_REF_ARRETE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_ARRETE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		addZone(getNOM_LB_MOTIF_AFFECTATION_SELECT(), Const.ZERO);
		addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), Const.ZERO);
		addZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT(), Const.ZERO);
		addZone(getNOM_EF_INFO_POINTAGE_FDP(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_INFO_ABSENCE_FDP(), Const.CHAINE_VIDE);

	}

	private void initialiseAvantageNature_spec() throws Exception {
		// Avantages en nature
		if (getListeAvantageFP() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeAvantageFP(getAvantageNatureDao().listerAvantageNatureAvecFP(getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeAvantageFP().addAll(getAvantageNatureDao().listerAvantageNatureAvecFP(getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeAvantageAFF() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null && getAffectationCourant().getIdAffectation() != null) {
			setListeAvantageAFF(getAvantageNatureDao().listerAvantageNatureAvecAFF(getAffectationCourant().getIdAffectation()));
		}
		int indiceAvNat = 0;
		if (getListeAvantageFP() != null && getListeAvantageFP().size() != 0) {
			for (int i = 0; i < getListeAvantageFP().size(); i++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageFP().get(i);
				if (aAvNat != null) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : getNatureAvantageDao().chercherNatureAvantage(aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : getRubriqueDao().chercherRubrique(aAvNat.getNumRubrique());

					addZone(getNOM_ST_LST_AVANTAGE_TYPE(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT(indiceAvNat), aAvNat.getMontant().toString());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNorubr() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat), rubr.getLirubr());
					indiceAvNat++;
				}
			}
		}
		if (getListeAvantageAFF() != null && getListeAvantageAFF().size() != 0) {
			for (int j = 0; j < getListeAvantageAFF().size(); j++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageAFF().get(j);
				if (aAvNat != null && !getListeAvantageFP().contains(aAvNat)) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : getNatureAvantageDao().chercherNatureAvantage(aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : getRubriqueDao().chercherRubrique(aAvNat.getNumRubrique());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT(indiceAvNat), aAvNat.getMontant().toString());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNorubr() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat), rubr.getLirubr());
					indiceAvNat++;
				}
			}
		}

		if (getListeAvantageAAjouter() != null && getListeAvantageAAjouter().size() != 0) {
			for (int k = 0; k < getListeAvantageAAjouter().size(); k++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageAAjouter().get(k);
				if (aAvNat != null) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : getNatureAvantageDao().chercherNatureAvantage(aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : getRubriqueDao().chercherRubrique(aAvNat.getNumRubrique());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT(indiceAvNat), aAvNat.getMontant().toString());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNorubr() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat), rubr.getLirubr());
					indiceAvNat++;
				}
			}
		}
	}

	private void initialiseDelegation_spec() throws Exception {
		// délégations
		if (getListeDelegationFP() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeDelegationFP(getDelegationDao().listerDelegationAvecFP(getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeDelegationFP().addAll(getDelegationDao().listerDelegationAvecFP(getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeDelegationAFF() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null && getAffectationCourant() != null
				&& getAffectationCourant().getIdAffectation() != null) {
			setListeDelegationAFF(getDelegationDao().listerDelegationAvecAFF(getAffectationCourant().getIdAffectation()));
		}
		int indiceDel = 0;
		if (getListeDelegationFP() != null && getListeDelegationFP().size() != 0) {
			for (int i = 0; i < getListeDelegationFP().size(); i++) {
				Delegation aDel = (Delegation) getListeDelegationFP().get(i);
				if (aDel != null) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}
		if (getListeDelegationAFF() != null && getListeDelegationAFF().size() != 0) {
			for (int j = 0; j < getListeDelegationAFF().size(); j++) {
				Delegation aDel = (Delegation) getListeDelegationAFF().get(j);
				if (aDel != null && !getListeDelegationFP().contains(aDel)) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}
		if (getListeDelegationAAjouter() != null && getListeDelegationAAjouter().size() != 0) {
			for (int k = 0; k < getListeDelegationAAjouter().size(); k++) {
				Delegation aDel = (Delegation) getListeDelegationAAjouter().get(k);
				if (aDel != null) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}

	}

	/**
	 * Initialisation de la liste des affectations. RG_AG_AF_A09
	 */
	private void initialiseListeAffectation(HttpServletRequest request) throws Exception {

		// Recherche des affectations de l'agent
		ArrayList<Affectation> aff = getAffectationDao().listerAffectationAvecAgent(getAgentCourant().getIdAgent());
		setListeAffectation(aff);

		boolean affectationActive = false;

		int indiceAff = 0;
		if (getListeAffectation() != null) {
			for (int i = 0; i < getListeAffectation().size(); i++) {
				Affectation a = (Affectation) getListeAffectation().get(i);
				affectationActive = affectationActive || a.isActive();
				FichePoste fp = getFichePosteDao().chercherFichePoste(a.getIdFichePoste());
				HistoFichePoste hfp = null;
				ArrayList<HistoFichePoste> listeHistoFP = new ArrayList<HistoFichePoste>();
				if (a.getDateFinAff() != null) {
					// on cherche la FDP dans histo_fiche_poste
					listeHistoFP = getHistoFichePosteDao().listerHistoFichePosteDansDate(a.getIdFichePoste(), a.getDateDebutAff(), a.getDateFinAff());
				}
				// si il n'y en a pas on prend les infos dans la table
				// FICHE_POSTE
				if (listeHistoFP == null || listeHistoFP.size() == 0) {
					fp = getFichePosteDao().chercherFichePoste(a.getIdFichePoste());
				} else {
					// si il y en a plusieurs on prend la date la plus recente
					hfp = (HistoFichePoste) listeHistoFP.get(0);
				}
				String titreFichePoste = Const.CHAINE_VIDE;
				String numFP = Const.CHAINE_VIDE;
				EntiteDto direction = null;
				EntiteDto service = null;
				if (hfp != null) {
					titreFichePoste = hfp.getIdTitrePoste() == null ? "&nbsp;" : getTitrePosteDao().chercherTitrePoste(hfp.getIdTitrePoste()).getLibTitrePoste();
					// Service
					direction = adsService.getAffichageDirection(hfp.getIdServiceAds());
					service = adsService.getAffichageSection(hfp.getIdServiceAds());
					if (service == null)
						service = adsService.getAffichageService(hfp.getIdServiceAds());
					if (service == null)
						service = adsService.getAffichageDirection(hfp.getIdServiceAds());
					if (service == null)
						service = adsService.getEntiteByIdEntite(hfp.getIdServiceAds());

					// #184002 : on cherche les infos antérieures dans la table
					// mairie.siserv
					if (service == null && hfp.getIdServiceAds() == null && hfp.getIdServi() != null) {
						try {
							SISERV siserv = getSiservDao().chercherSiserv(hfp.getIdServi());
							if (siserv != null && siserv.getServi() != null) {
								service = new EntiteDto();
								service.setLabel(siserv.getLiserv());
								service.setSigle(siserv.getSigle());
							}
						} catch (Exception e) {
							// on ne fait rien
						}
					}

					numFP = hfp.getNumFp();
					if (a.getIdFichePosteSecondaire() != null)
						numFP = hfp.getNumFp() + " *";

				} else {
					titreFichePoste = fp.getIdTitrePoste() == null ? "&nbsp;" : getTitrePosteDao().chercherTitrePoste(fp.getIdTitrePoste()).getLibTitrePoste();
					// Service
					direction = adsService.getAffichageDirection(fp.getIdServiceAds());
					service = adsService.getAffichageSection(fp.getIdServiceAds());
					if (service == null)
						service = adsService.getAffichageService(fp.getIdServiceAds());
					if (service == null)
						service = adsService.getAffichageDirection(fp.getIdServiceAds());
					if (service == null)
						service = adsService.getEntiteByIdEntite(fp.getIdServiceAds());

					// #184002 : on cherche les infos antérieures dans la table
					// mairie.siserv
					if (service == null && fp.getIdServiceAds() == null && fp.getIdServi() != null) {
						try {
							SISERV siserv = getSiservDao().chercherSiserv(fp.getIdServi());
							if (siserv != null && siserv.getServi() != null) {
								service = new EntiteDto();
								service.setLabel(siserv.getLiserv());
								service.setSigle(siserv.getSigle());
							}
						} catch (Exception e) {
							// on ne fait rien
						}
					}

					numFP = fp.getNumFp();
					if (a.getIdFichePosteSecondaire() != null)
						numFP = fp.getNumFp() + " *";

				}

				addZone(getNOM_ST_DIR(indiceAff), direction != null ? direction.getSigle() : "&nbsp;");
				addZone(getNOM_ST_SERV(indiceAff), service != null ? service.getLabel() + " ( " + service.getSigle() + " )" : "&nbsp;");
				addZone(getNOM_ST_DATE_DEBUT(indiceAff), sdf.format(a.getDateDebutAff()));
				addZone(getNOM_ST_DATE_FIN(indiceAff), a.getDateFinAff() == null ? "&nbsp;" : sdf.format(a.getDateFinAff()));
				addZone(getNOM_ST_NUM_FP(indiceAff), numFP.equals(Const.CHAINE_VIDE) ? "&nbsp;" : numFP);
				addZone(getNOM_ST_TITRE(indiceAff), titreFichePoste.equals(Const.CHAINE_VIDE) ? "&nbsp;" : titreFichePoste);

				indiceAff++;
			}
		}

		if (!affectationActive) {
			// Messages informatifs
			// RG_AG_AF_A09
			if (getTransaction().isErreur())
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "<BR/>" + MessageUtils.getMessage("INF003", getAgentCourant().getNomatr().toString()));
			else
				getTransaction().declarerErreur(MessageUtils.getMessage("INF003", getAgentCourant().getNomatr().toString()));

			setFocus(getNOM_PB_AJOUTER());
		}
	}

	/**
	 * Initialise les listes deroulantes de l'écran.
	 * 
	 * @throws Exception
	 *             RG_AG_AF_C06 RG_AG_AF_C02
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste motif affectation vide alors affectation
		// RG_AG_AF_C06
		if (getLB_MOTIF_AFFECTATION() == LBVide) {
			ArrayList<MotifAffectation> motifAff = (ArrayList<MotifAffectation>) getMotifAffectationDao().listerMotifAffectation();
			setListeMotifAffectation(motifAff);

			if (getListeMotifAffectation().size() != 0) {
				int[] tailles = { 30 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<MotifAffectation> list = getListeMotifAffectation().listIterator(); list.hasNext();) {
					MotifAffectation de = (MotifAffectation) list.next();
					String ligne[] = { de.getLibMotifAffectation() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_MOTIF_AFFECTATION(aFormat.getListeFormatee());
			} else {
				setLB_MOTIF_AFFECTATION(null);
			}
		}

		// Si liste pourcentages Temps de travail alors affectation
		// RG_AG_AF_C02
		if (getLB_TEMPS_TRAVAIL() == LBVide) {
			setListeTempsTravail(EnumTempsTravail.getValues());
			setLB_TEMPS_TRAVAIL(getListeTempsTravail());
			addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), Const.ZERO);
		}

		// Si liste base horaire absence vide alors affectation
		if (getLB_BASE_HORAIRE_ABSENCE() == LBVide) {
			List<TypeAbsenceDto> listeTypeAbsence = absService.getListeRefTypeAbsenceDto(EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue());

			for (TypeAbsenceDto abs : listeTypeAbsence) {
				getListeBaseHoraireAbsence().add(abs.getTypeSaisiCongeAnnuelDto());
			}
			if (getListeBaseHoraireAbsence().size() != 0) {
				int tailles[] = { 5 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<RefTypeSaisiCongeAnnuelDto> list = getListeBaseHoraireAbsence().listIterator(); list.hasNext();) {
					RefTypeSaisiCongeAnnuelDto base = (RefTypeSaisiCongeAnnuelDto) list.next();
					String ligne[] = { base.getCodeBaseHoraireAbsence() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_BASE_HORAIRE_ABSENCE(aFormat.getListeFormatee(true));
			} else {
				setLB_BASE_HORAIRE_ABSENCE(null);
			}
		}

		// Si liste base horaire pointage vide alors affectation
		if (getLB_BASE_HORAIRE_POINTAGE() == LBVide) {
			ArrayList<BaseHorairePointage> listeBaseHorairePointage = (ArrayList<BaseHorairePointage>) getBaseHorairePointageDao().listerBaseHorairePointageOrderByCode();
			setListeBaseHorairePointage(listeBaseHorairePointage);
			if (getListeBaseHorairePointage().size() != 0) {
				int tailles[] = { 5, 50 };
				String padding[] = { "G", "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<BaseHorairePointage> list = getListeBaseHorairePointage().listIterator(); list.hasNext();) {
					BaseHorairePointage base = (BaseHorairePointage) list.next();
					String ligne[] = { base.getCodeBaseHorairePointage(), base.getLibelleBaseHorairePointage() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_BASE_HORAIRE_POINTAGE(aFormat.getListeFormatee(true));
			} else {
				setLB_BASE_HORAIRE_POINTAGE(null);
			}
		}
	}

	/**
	 * Initialise les listes deroulantes de l'écran. Date de création :
	 * (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante_spec() throws Exception {
		// Si liste type avantage vide alors affectation
		if (getLB_TYPE_AVANTAGE() == LBVide) {
			ArrayList<TypeAvantage> typeAvantage = getTypeAvantageDao().listerTypeAvantage();
			setListeTypeAvantage(typeAvantage);

			if (getListeTypeAvantage().size() != 0) {
				int[] tailles = { 50 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeAvantage> list = getListeTypeAvantage().listIterator(); list.hasNext();) {
					TypeAvantage de = (TypeAvantage) list.next();
					String ligne[] = { de.getLibTypeAvantage() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_AVANTAGE(aFormat.getListeFormatee());
			} else {
				setLB_TYPE_AVANTAGE(null);
			}
		}

		// Si liste nature avantage vide alors affectation
		if (getLB_NATURE_AVANTAGE() == LBVide) {
			ArrayList<NatureAvantage> natureAvantage = getNatureAvantageDao().listerNatureAvantage();
			NatureAvantage natAvVide = new NatureAvantage();
			natureAvantage.add(0, natAvVide);
			setListeNatureAvantage(natureAvantage);

			if (getListeNatureAvantage().size() != 0) {
				int[] tailles = { 50 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<NatureAvantage> list = getListeNatureAvantage().listIterator(); list.hasNext();) {
					NatureAvantage de = (NatureAvantage) list.next();
					String ligne[] = { de.getLibNatureAvantage() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_NATURE_AVANTAGE_spec(aFormat.getListeFormatee());
			} else {
				setLB_NATURE_AVANTAGE_spec(null);
			}
		}

		// Si liste rubrique vide alors affectation
		if (getLB_RUBRIQUE_AVANTAGE() == LBVide || getLB_RUBRIQUE_REGIME() == LBVide) {
			ArrayList<Rubrique> rubrique = getRubriqueDao().listerRubrique7000();
			setListeRubrique(rubrique);

			if (getListeRubrique() != null && getListeRubrique().size() != 0) {
				int taillesRub[] = { 68 };
				FormateListe aFormatRub = new FormateListe(taillesRub);
				for (ListIterator<Rubrique> list = getListeRubrique().listIterator(); list.hasNext();) {
					Rubrique aRub = (Rubrique) list.next();
					if (aRub != null) {
						String ligne[] = { aRub.getNorubr() + " - " + aRub.getLirubr() };
						aFormatRub.ajouteLigne(ligne);
					}
				}
				setLB_RUBRIQUE_AVANTAGE(aFormatRub.getListeFormatee(true));
				setLB_RUBRIQUE_REGIME(aFormatRub.getListeFormatee(true));
			} else {
				setLB_RUBRIQUE_AVANTAGE(null);
				setLB_RUBRIQUE_REGIME(null);
			}
		}

		if (getLB_RUBRIQUE_PRIME_POINTAGE() == LBVide) {
			setListePrimes(initialiseListeDeroulantePrimes_spec());
			if (getListePrimes() != null) {
				String[] content = new String[getListePrimes().size()];
				for (int i = 0; i < getListePrimes().size(); i++) {
					content[i] = getListePrimes().get(i).getNumRubrique() + " - " + getListePrimes().get(i).getLibelle();
				}
				setLB_RUBRIQUE_PRIME_POINTAGE_spec(content);
			}
		}

		// Si liste type délégation vide alors affectation
		if (getLB_TYPE_DELEGATION() == LBVide) {
			ArrayList<TypeDelegation> typeDelegation = getTypeDelegationDao().listerTypeDelegation();
			setListeTypeDelegation(typeDelegation);

			if (getListeTypeDelegation().size() != 0) {
				int[] tailles = { 30 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeDelegation> list = getListeTypeDelegation().listIterator(); list.hasNext();) {
					TypeDelegation de = (TypeDelegation) list.next();
					String ligne[] = { de.getLibTypeDelegation() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_DELEGATION(aFormat.getListeFormatee());
			} else {
				setLB_TYPE_DELEGATION(null);
			}
		}

		// Si liste type régime vide alors affectation
		if (getLB_TYPE_REGIME() == LBVide) {
			ArrayList<TypeRegIndemn> typeRegime = getTypeRegIndemnDao().listerTypeRegIndemn();
			setListeTypeRegIndemn(typeRegime);

			if (getListeTypeRegIndemn().size() != 0) {
				int[] tailles = { 20 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeRegIndemn> list = getListeTypeRegIndemn().listIterator(); list.hasNext();) {
					TypeRegIndemn de = (TypeRegIndemn) list.next();
					String ligne[] = { de.getLibTypeRegIndemn() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_REGIME(aFormat.getListeFormatee());
			} else {
				setLB_TYPE_REGIME(null);
			}
		}
	}

	/**
	 * CLV #3264 Initialisation de la liste deroulantes des primes.
	 * 
	 * @throws Exception
	 */
	private List<RefPrimeDto> initialiseListeDeroulantePrimes_spec() throws Exception {
		List<RefPrimeDto> primes = new ArrayList<RefPrimeDto>();
		if (agentCourant != null) {
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agentCourant);
			if (carr != null && !carr.getCodeCategorie().equals(Const.CHAINE_VIDE)) {
				String statut = Carriere.getStatutCarriere(carr.getCodeCategorie());
				if (!statut.equals(Const.CHAINE_VIDE))
					primes = ptgService.getPrimes();
			}
		}
		return primes;
	}

	/**
	 * Initialise la liste deroulantes des impressions.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeImpression() throws Exception {
		// Si liste impressions vide alors affectation
		if (getLB_LISTE_IMPRESSION() == LBVide) {
			setLB_LISTE_IMPRESSION(EnumImpressionAffectation.getValues());
			addZone(getNOM_LB_LISTE_IMPRESSION_SELECT(), "0");
		}
	}

	/**
	 * Initialise les listes de spécificités. Date de création : (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeSpecificites_spec() throws Exception {
		initialiseAvantageNature_spec();
		initialiseDelegation_spec();
		initialiseRegime_spec();
		initialisePrimePointage_spec();
	}

	/**
	 * fin CLV #3264
	 */

	private void initialisePrimePointage_spec() throws Exception {
		// Primes pointages
		if (getListePrimePointageFP().size() == 0 && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListePrimePointageFP(getPrimePointageFPDao().listerPrimePointageFP(getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListePrimePointageFP().addAll(getPrimePointageFPDao().listerPrimePointageFP(getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}

		if (getListePrimePointageAFF() == null && getFichePosteCourant() != null && getAffectationCourant().getIdAffectation() != null) {
			setListePrimePointageAFF(getPrimePointageAffDao().listerPrimePointageAff(getAffectationCourant().getIdAffectation()));
		}
		int indicePrime = 0;
		if (getListePrimePointageFP() != null && getListePrimePointageFP().size() != 0) {
			for (int i = 0; i < getListePrimePointageFP().size(); i++) {
				PrimePointageFP prime = (PrimePointageFP) getListePrimePointageFP().get(i);
				if (prime != null) {
					RefPrimeDto rubr = ptgService.getPrimeDetail(prime.getNumRubrique());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE(indicePrime), rubr.getNumRubrique() + " : " + rubr.getLibelle());

					indicePrime++;
				}
			}
		}
		if (getListePrimePointageAFF() != null && getListePrimePointageAFF().size() != 0) {
			for (int j = 0; j < getListePrimePointageAFF().size(); j++) {
				PrimePointageAff prime = (PrimePointageAff) getListePrimePointageAFF().get(j);
				if (prime != null) {
					RefPrimeDto rubr = ptgService.getPrimeDetail(prime.getNumRubrique());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE(indicePrime), rubr.getNumRubrique() + " : " + rubr.getLibelle());

					indicePrime++;
				}
			}
		}
		if (getListePrimePointageAffAAjouter() != null && getListePrimePointageAffAAjouter().size() != 0) {
			for (PrimePointageAff prime : getListePrimePointageAffAAjouter()) {
				if (prime != null) {
					RefPrimeDto rubr = ptgService.getPrimeDetail(prime.getNumRubrique());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE(indicePrime), rubr.getNumRubrique() + " : " + rubr.getLibelle());

					indicePrime++;
				}
			}
		}
	}

	private void initialiseRegime_spec() throws Exception {
		// Régimes indemnitaires
		if (getListeRegimeFP() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeRegimeFP(getRegIndemnDao().listerRegimeIndemnitaireAvecFP(getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeRegimeFP().addAll(getRegIndemnDao().listerRegimeIndemnitaireAvecFP(getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeRegimeAFF() == null && getAffectationCourant() != null && getAffectationCourant().getIdAffectation() != null) {
			setListeRegimeAFF(getRegIndemnDao().listerRegimeIndemnitaireAvecAFF(getAffectationCourant().getIdAffectation()));
		}
		int indiceReg = 0;
		if (getListeRegimeFP() != null && getListeRegimeFP().size() != 0) {
			for (int i = 0; i < getListeRegimeFP().size(); i++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeFP().get(i);
				if (aReg != null) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : getRubriqueDao().chercherRubrique(aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT(indiceReg), aReg.getForfait().toString());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(indiceReg), aReg.getNombrePoints().toString());
					if (rubr != null && rubr.getNorubr() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(indiceReg), rubr.getLirubr());
					indiceReg++;
				}
			}
		}
		if (getListeRegimeAFF() != null && getListeRegimeAFF().size() != 0) {
			for (int j = 0; j < getListeRegimeAFF().size(); j++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeAFF().get(j);
				if (aReg != null && !getListeRegimeFP().contains(aReg)) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : getRubriqueDao().chercherRubrique(aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT(indiceReg), aReg.getForfait().toString());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(indiceReg), aReg.getNombrePoints().toString());
					if (rubr != null && rubr.getNorubr() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(indiceReg), rubr.getLirubr());
					indiceReg++;
				}
			}
		}
		if (getListeRegimeAAjouter() != null && getListeRegimeAAjouter().size() != 0) {
			for (int k = 0; k < getListeRegimeAAjouter().size(); k++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeAAjouter().get(k);
				if (aReg != null) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : getRubriqueDao().chercherRubrique(aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT(indiceReg), aReg.getForfait().toString());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(indiceReg), aReg.getNombrePoints().toString());
					if (rubr != null && rubr.getNorubr() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(indiceReg), rubr.getLirubr());
					indiceReg++;
				}
			}
		}

	}

	/**
	 * Initialise les champs de la fiche de poste courante liee a l'affectation.
	 * 
	 * @throws Exception
	 */
	private void initialiserFichePoste() throws Exception {
		// Titre
		String titreFichePoste = getFichePosteCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : getTitrePosteDao().chercherTitrePoste(getFichePosteCourant().getIdTitrePoste())
				.getLibTitrePoste();

		// Service
		EntiteDto srv = adsService.getEntiteByIdEntite(getFichePosteCourant().getIdServiceAds());
		EntiteDto direction = adsService.getAffichageDirection(getFichePosteCourant().getIdServiceAds());
		EntiteDto division = adsService.getAffichageService(getFichePosteCourant().getIdServiceAds());
		EntiteDto section = adsService.getAffichageSection(getFichePosteCourant().getIdServiceAds());

		// temps reglementaire de travail
		Horaire hor = Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg().toString());
		// Lieu
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo().toString());

		addZone(getNOM_ST_DIRECTION(), direction == null ? Const.CHAINE_VIDE : direction.getLabel());
		addZone(getNOM_ST_SERVICE(), division == null ? srv == null ? Const.CHAINE_VIDE : srv.getLabel() : division.getLabel());
		addZone(getNOM_ST_SUBDIVISION(), section == null ? Const.CHAINE_VIDE : section.getLabel());
		addZone(getNOM_ST_NUM_FICHE_POSTE(), getFichePosteCourant().getNumFp());
		addZone(getNOM_ST_TITRE_FP(), titreFichePoste);
		addZone(getNOM_ST_TPS_REG(), hor.getLibHor());
		addZone(getNOM_ST_LIEU_FP(), eg.getLibEntiteGeo());
	}

	/**
	 * Initialise les champs de la fiche de poste secondaire courante liee a
	 * l'affectation.
	 * 
	 * @throws Exception
	 */
	private void initialiserFichePosteSecondaire() throws Exception {
		// Titre
		String titreFichePoste = getFichePosteSecondaireCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : getTitrePosteDao().chercherTitrePoste(
				getFichePosteSecondaireCourant().getIdTitrePoste()).getLibTitrePoste();
		// Service
		EntiteDto srv = adsService.getEntiteByIdEntite(getFichePosteCourant().getIdServiceAds());
		EntiteDto direction = adsService.getAffichageDirection(getFichePosteCourant().getIdServiceAds());
		EntiteDto division = adsService.getAffichageService(getFichePosteCourant().getIdServiceAds());
		EntiteDto section = adsService.getAffichageSection(getFichePosteCourant().getIdServiceAds());

		// temps reglementaire de travail
		Horaire hor = Horaire.chercherHoraire(getTransaction(), getFichePosteSecondaireCourant().getIdCdthorReg().toString());
		// Lieu
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteSecondaireCourant().getIdEntiteGeo().toString());

		addZone(getNOM_ST_DIRECTION_SECONDAIRE(), direction == null ? Const.CHAINE_VIDE : direction.getLabel());
		addZone(getNOM_ST_SERVICE_SECONDAIRE(), division == null ? srv.getLabel() : division.getLabel());
		addZone(getNOM_ST_SUBDIVISION_SECONDAIRE(), section == null ? Const.CHAINE_VIDE : section.getLabel());
		addZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE(), getFichePosteSecondaireCourant().getNumFp());
		addZone(getNOM_ST_TITRE_FP_SECONDAIRE(), titreFichePoste);
		addZone(getNOM_ST_TPS_REG_SECONDAIRE(), hor.getLibHor());
		addZone(getNOM_ST_LIEU_FP_SECONDAIRE(), eg.getLibEntiteGeo());
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (04/08/11 15:42:45)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeAffectation(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		initialiseAffectationCourante(request);

		// Init Motifs affectation et Tps de travail
		initialiseListeDeroulante();

		initialiseListeDeroulante_spec();

		initialiseListeSpecificites_spec();

		// Si pas d'affectation en cours
		if (!getVAL_ST_ACTION().equals(ACTION_CREATION)) {
			if (getFichePosteCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
				ArrayList<Affectation> affActives = getAffectationDao().listerAffectationActiveAvecAgent(getAgentCourant().getIdAgent());
				if (affActives.size() == 1) {
					setAffectationCourant((Affectation) affActives.get(0));
					// Recherche des informations à afficher
					setFichePosteCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste()));
					if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
						setFichePosteSecondaireCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePosteSecondaire()));
					}
				} else if (affActives.size() == 0) {
					/*
					 * getTransaction().declarerErreur(MessageUtils.getMessage(
					 * "ERR083" )); return;
					 */
				} else if (affActives.size() > 1) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR082"));
					return;
				}
			}
		}
		if (getVAL_RG_SPECIFICITE() == null || getVAL_RG_SPECIFICITE().length() == 0) {
			addZone(getNOM_RG_SPECIFICITE(), getNOM_RB_SPECIFICITE_PP());
			addZone(getNOM_ST_SPECIFICITE(), SPEC_PRIME_POINTAGE_SPEC);
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getMotifAffectationDao() == null) {
			setMotifAffectationDao(new MotifAffectationDao((SirhDao) context.getBean("sirhDao")));
		}
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
		if (getAvantageNatureAffDao() == null) {
			setAvantageNatureAffDao(new AvantageNatureAffDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvantageNatureDao() == null) {
			setAvantageNatureDao(new AvantageNatureDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDelegationDao() == null) {
			setDelegationDao(new DelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDelegationAffDao() == null) {
			setDelegationAffDao(new DelegationAffDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnAffDao() == null) {
			setRegIndemnAffDao(new RegIndemnAffDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnDao() == null) {
			setRegIndemnDao(new RegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getPrimePointageAffDao() == null) {
			setPrimePointageAffDao(new PrimePointageAffDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getPrimePointageFPDao() == null) {
			setPrimePointageFPDao(new PrimePointageFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRubriqueDao() == null) {
			setRubriqueDao(new RubriqueDao((MairieDao) context.getBean("mairieDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoFichePosteDao() == null) {
			setHistoFichePosteDao(new HistoFichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoAffectationDao() == null) {
			setHistoAffectationDao(new HistoAffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getBaseHorairePointageDao() == null) {
			setBaseHorairePointageDao(new BaseHorairePointageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getStatutFPDao() == null) {
			setStatutFPDao(new StatutFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (getSiservDao() == null) {
			setSiservDao(new SISERVDao((MairieDao) context.getBean("mairieDao")));
		}
	}

	/**
	 * Vérifie les regles de gestion metier
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean performControlerChoixImpression() throws Exception {
		// Si pas de document sélectionné alors erreur
		if (Integer.parseInt(getZone(getNOM_LB_LISTE_IMPRESSION_SELECT())) == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "documents a imprimer"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Vérifie les regles de gestion metier
	 * 
	 * @return boolean
	 * @throws Exception
	 *             RG_AG_AF_A11
	 */
	public boolean performControlerRG() throws Exception {
		// Vérification du non-chevauchement des dates des affectations
		for (ListIterator<Affectation> list = getListeAffectation().listIterator(); list.hasNext();) {
			Affectation aAff = (Affectation) list.next();
			if (getAffectationCourant() == null || getAffectationCourant().getIdAffectation() == null) {
				if (!testRG1(aAff)) {
					return false;
				}
			} else {
				if (!aAff.getIdAffectation().toString().equals(getAffectationCourant().getIdAffectation().toString())) {
					if (!testRG1(aAff)) {
						return false;
					}
				}
			}
		}
		// verification pas 2 fois la même fiche de poste mise
		if (getFichePosteSecondaireCourant() != null) {
			if (getFichePosteCourant().getIdFichePoste().toString().equals(getFichePosteSecondaireCourant().getIdFichePoste().toString())) {
				// "ERR117",
				// "La fiche de poste @ doit être differente de la fiche courante."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR117", "secondaire"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		// Vérification de la non-affectation de la Fiche de poste choisie dans
		// les dates choisies
		ArrayList<Affectation> listeAffFP = getAffectationDao().listerAffectationAvecFP(getFichePosteCourant().getIdFichePoste());
		for (Affectation aff : listeAffFP) {

			if (getAffectationCourant() == null || getAffectationCourant().getIdAffectation() == null) {
				if (!testRG2(aff)) {
					return false;
				}
			} else {
				if (!aff.getIdAffectation().toString().equals(getAffectationCourant().getIdAffectation().toString())) {
					if (!testRG2(aff)) {
						return false;
					}
				}
			}
		}

		// Vérification de la non-affectation de la Fiche de poste secondaire
		// choisie dans les dates choisies
		if (getFichePosteSecondaireCourant() != null) {
			ArrayList<Affectation> listeAffFPSecondaire = getAffectationDao().listerAffectationAvecFP(getFichePosteSecondaireCourant().getIdFichePoste());
			for (Affectation aff : listeAffFPSecondaire) {

				if (getAffectationCourant() == null || getAffectationCourant().getIdAffectation() == null) {
					if (!testRG3(aff)) {
						return false;
					}
				} else {
					if (!aff.getIdAffectation().toString().equals(getAffectationCourant().getIdAffectation().toString())) {
						if (!testRG3(aff)) {
							return false;
						}
					}
				}
			}
		}

		// Verification des temps reglementaires des 2 fiches de postes < 100%
		// RG_AG_AF_A11
		if (getFichePosteSecondaireCourant() != null) {
			Horaire horFDP1 = Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg().toString());
			Horaire horFDP2 = Horaire.chercherHoraire(getTransaction(), getFichePosteSecondaireCourant().getIdCdthorReg().toString());
			// calcul du taux que ca donne
			Float res = Float.valueOf(horFDP1.getCdTaux()) + Float.valueOf(horFDP2.getCdTaux());
			if (res > 1) {
				// "ERR104",
				// "Le temps de travail réglementaire des deux fiche de poste dépasse 100%."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR080"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		return true;
	}

	private boolean testRG3(Affectation aff) {
		if (aff.getDateFinAff() != null) {
			if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
				if (Services.compareDates(getVAL_EF_DATE_DEBUT(), sdf.format(aff.getDateFinAff())) <= 0) {
					// "ERR085",
					// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			} else {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aff.getDateDebutAff())) >= 0 && Services.compareDates(getVAL_EF_DATE_DEBUT(), sdf.format(aff.getDateFinAff())) <= 0) {
					// "ERR085",
					// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		} else {
			if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
				// "ERR085",
				// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			} else {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aff.getDateDebutAff())) >= 0) {
					// "ERR085",
					// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		}
		return true;
	}

	private boolean testRG2(Affectation aff) {
		if (aff.getDateFinAff() != null) {
			if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
				if (Services.compareDates(getVAL_EF_DATE_DEBUT(), sdf.format(aff.getDateFinAff())) <= 0) {
					// "ERR085",
					// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			} else {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aff.getDateDebutAff())) >= 0 && Services.compareDates(getVAL_EF_DATE_DEBUT(), sdf.format(aff.getDateFinAff())) <= 0) {
					// "ERR085",
					// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		} else {
			if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
				// "ERR085",
				// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			} else {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aff.getDateDebutAff())) >= 0) {
					// "ERR085",
					// "Cette Fiche de poste est déjà affectée a un autre agent aux dates données."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		}
		return true;
	}

	private boolean testRG1(Affectation aAff) {
		if (aAff.getDateFinAff() != null) {
			if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
				if (Services.compareDates(getVAL_EF_DATE_DEBUT(), sdf.format(aAff.getDateFinAff())) <= 0) {
					// "ERR201",
					// "Operation impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			} else {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aAff.getDateDebutAff())) >= 0 && Services.compareDates(getVAL_EF_DATE_DEBUT(), sdf.format(aAff.getDateFinAff())) <= 0) {
					// "ERR201",
					// "Operation impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		} else {
			if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
				// "ERR201",
				// "Operation impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			} else {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aAff.getDateDebutAff())) >= 0) {
					// "ERR201",
					// "Operation impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, dates bien
	 * formatées, ...)
	 * 
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 *             RG_AG_AF_A06
	 */
	public boolean performControlerSaisie() throws Exception {
		// RG_AG_AF_A06

		// **********************************************************
		// RG_AG_AFF_C01 : Vérification des champs obligatoires
		// **********************************************************
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date arrêté"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		if (!Services.estUneDate(getVAL_EF_DATE_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "arrêté"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// Vérification Date début et date fin (non null et dans le bon ordre.
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			// format de date
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
				// ERR007 : La date @ est incorrecte. Elle doit être au format
				// date.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "début"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
				if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
					// ERR007 : La date @ est incorrecte. Elle doit être au
					// format date.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "fin"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				} else if (Services.compareDates(getZone(getNOM_EF_DATE_DEBUT()), getZone(getNOM_EF_DATE_FIN())) > 0) {
					// ERR200 : La date @ doit être supérieure ou egale à la
					// date @.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR200", "fin", "début"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		} else {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date début"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		if ((Const.CHAINE_VIDE).equals(getVAL_ST_NUM_FICHE_POSTE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Fiche de poste"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// **********************************************************
		// Vérification Formats
		// **********************************************************
		// "ERR992", "La zone @ doit être numérique."
		if (!Const.CHAINE_VIDE.equals(getVAL_EF_REF_ARRETE()) && !Services.estNumerique(getVAL_EF_REF_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Réf. arrêté"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// **********************
		// Verification Base horaire de pointage
		// **********************
		// # 15685 : pour les élus, non obligatoire
		// on cherche la carriere en cours à la date de début de l'affectation
		// bug #16097
		SimpleDateFormat sdfMairie = new SimpleDateFormat("yyyyMMdd");
		Carriere carrEnCours = Carriere.chercherCarriereEnCoursAvecAgentEtDate(getTransaction(), new Integer(sdfMairie.format(sdf.parse(Services.formateDate(getVAL_EF_DATE_DEBUT())))),
				getAgentCourant());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		}
		if (carrEnCours != null && carrEnCours.getCodeCategorie() != null) {
			if (!Carriere.isCarriereConseilMunicipal(carrEnCours.getCodeCategorie())) {
				int numLigneBaseHorairePointage = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) : -1);

				if (numLigneBaseHorairePointage == 0 || getListeBaseHorairePointage().isEmpty() || numLigneBaseHorairePointage > getListeBaseHorairePointage().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire de pointage"));
					return false;
				}
			}
		} else {
			int numLigneBaseHorairePointage = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) : -1);

			if (numLigneBaseHorairePointage == 0 || getListeBaseHorairePointage().isEmpty() || numLigneBaseHorairePointage > getListeBaseHorairePointage().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire de pointage"));
				return false;
			}
		}

		// **********************
		// Verification Base horaire d'absence
		// **********************
		// # 15685 : pour les élus, non obligatoire
		// on cherche la carriere en cours à la date de début de l'affectation
		if (carrEnCours != null && carrEnCours.getCodeCategorie() != null) {
			if (!Carriere.isCarriereConseilMunicipal(carrEnCours.getCodeCategorie())) {
				int numLigneBaseHoraireAbsence = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) : -1);

				if (numLigneBaseHoraireAbsence == 0 || getListeBaseHoraireAbsence().isEmpty() || numLigneBaseHoraireAbsence > getListeBaseHoraireAbsence().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base de congé"));
					return false;
				}
			}
		} else {
			int numLigneBaseHoraireAbsence = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) : -1);

			if (numLigneBaseHoraireAbsence == 0 || getListeBaseHoraireAbsence().isEmpty() || numLigneBaseHoraireAbsence > getListeBaseHoraireAbsence().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base de congé"));
				return false;
			}
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un avantage en nature. Date de création :
	 * (28/07/11)
	 */
	private boolean performControlerSaisieAvNat_spec(HttpServletRequest request) throws Exception {

		// type avantage obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type avantage"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// ****************************************
		// Verification Montant OU Nature renseigné
		// ****************************************
		if (getVAL_EF_MONTANT_AVANTAGE().length() == 0 && ((NatureAvantage) getListeNatureAvantage().get(Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()))).getIdNatureAvantage() == null) {
			// "ERR979","Au moins une des 2 zones suivantes doit être renseignée : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Nature avantage", "Montant"));

			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// ********************
		// Verification Montant
		// ********************
		if (getVAL_EF_MONTANT_AVANTAGE().length() != 0 && !Services.estNumerique(getVAL_EF_MONTANT_AVANTAGE())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Montant"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'une délégation. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieDel_spec(HttpServletRequest request) throws Exception {
		// type obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		return true;
	}

	/**
	 * Controle les zones saisies d'un régime indemnitaire. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisiePrimePointage_spec(HttpServletRequest request) throws Exception {

		// rubrique obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "rubrique"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Controle les zones saisies d'un régime indemnitaire. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieRegIndemn_spec(HttpServletRequest request) throws Exception {
		// type obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// *******************************************
		// Verification Forfait OU Nb points renseigné
		// *******************************************
		if (getVAL_EF_FORFAIT_REGIME().length() == 0 && getVAL_EF_NB_POINTS_REGIME().length() == 0) {
			// "ERR979","Au moins une des 2 zones suivantes doit être renseignée : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Forfait", "Nb points"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// ********************
		// Verification Forfait
		// ********************
		if (getVAL_EF_FORFAIT_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_FORFAIT_REGIME())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Forfait"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// **********************
		// Verification Nb points
		// **********************
		if (getVAL_EF_NB_POINTS_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_NB_POINTS_REGIME())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Nb points"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {

		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de l'affectation courante
		setAffectationCourant(new Affectation());

		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		setFichePosteSecondaireCourant(null);

		// on vide les informations des specificites
		initialiseSpecificitesVide();

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseSpecificitesVide() {
		setListeAvantageAFF(null);
		setListeAvantageFP(null);
		setListeDelegationAFF(null);
		setListeDelegationFP(null);
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);
		setListeRegimeAFF(null);
		setListeRegimeFP(null);

		setListeAvantageAAjouter(null);
		setListeAvantageASupprimer(null);
		setListeDelegationAAjouter(null);
		setListeDelegationASupprimer(null);
		setListePrimePointageAffAAjouter(null);
		setListePrimePointageAffASupprimer(null);
		setListeRegimeAAjouter(null);
		setListeRegimeASupprimer(null);

	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_AVANTAGE_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_DELEGATION_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_DELEGATION_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_PRIME_POINTAGE_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_PRIME_POINTAGE_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_REGIME_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_REG_INDEMN_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

		// on vide les informations des specificites
		initialiseSpecificitesVide();
		initialiseListeAffectation(request);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_CHANGER_SPECIFICITE_spec(HttpServletRequest request) throws Exception {
		if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_AN()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_AVANTAGE_NATURE_SPEC);
		else if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_D()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_DELEGATION_SPEC);
		else if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_RI()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_REG_INDEMN_SPEC);
		else if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_PP()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_PRIME_POINTAGE_SPEC);

		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		// On supprime la fiche de poste secondaire
		setFichePosteSecondaireCourant(null);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAConsulter);
		setAffectationCourant(aff);

		// on vide les informations des specificites
		initialiseSpecificitesVide();

		if (initialiseAffectationCourante(request)) {

			// Alim zones
			addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
			addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete() == null ? Const.CHAINE_VIDE : sdf.format(getAffectationCourant().getDateArrete()));
			addZone(getNOM_EF_DATE_DEBUT(), sdf.format(getAffectationCourant().getDateDebutAff()));
			addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff() == null ? Const.CHAINE_VIDE : sdf.format(getAffectationCourant().getDateFinAff()));
			addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_HISTORIQUE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_HISTORIQUE, true);
		return true;
	}

	public boolean performPB_IMPRIMER(HttpServletRequest request, int indiceEltAImprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAImprimer);
		setAffectationCourant(aff);

		// on vide les informations des specificites
		initialiseSpecificitesVide();

		if (getAffectationCourant().isActive()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

			if (initialiseAffectationCourante(request)) {
				initialiseListeImpression();
				// On nomme l'action
				addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
			}
		} else {
			// "ERR081",
			// "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimee.")
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR081"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du processs - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		setFichePosteSecondaireCourant(null);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAModifier);
		setAffectationCourant(aff);

		// on vide les informations des specificites
		initialiseSpecificitesVide();

		if (initialiseAffectationCourante(request)) {

			// Alim zones
			addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
			addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete() == null ? Const.CHAINE_VIDE : sdf.format(getAffectationCourant().getDateArrete()));
			addZone(getNOM_EF_DATE_DEBUT(), sdf.format(getAffectationCourant().getDateDebutAff()));
			addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff() == null ? Const.CHAINE_VIDE : sdf.format(getAffectationCourant().getDateFinAff()));
			addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 13:35:40)
	 * 
	 * RG_AG_AF_C05 RG_AG_AF_C07
	 */
	public boolean performPB_RECHERCHER_FP(HttpServletRequest request) throws Exception {
		// RG_AG_AF_C05
		// RG_AG_AF_C07
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE, Boolean.TRUE);
		setStatut(STATUT_RECHERCHE_FP, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 13:35:40)
	 * 
	 * RG_AG_AF_C05 RG_AG_AF_C07
	 */
	public boolean performPB_RECHERCHER_FP_SECONDAIRE(HttpServletRequest request) throws Exception {
		// RG_AG_AF_C05
		// RG_AG_AF_C07
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE, Boolean.TRUE);
		setStatut(STATUT_RECHERCHE_FP_SECONDAIRE, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_AF_A02
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltASuprimer);
		setAffectationCourant(aff);

		// on vide les informations des specificites
		initialiseSpecificitesVide();

		if (getAffectationCourant().isActive()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

			if (initialiseAffectationCourante(request)) {

				// Alim zones
				addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
				addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete() == null ? Const.CHAINE_VIDE : sdf.format(getAffectationCourant().getDateArrete()));
				addZone(getNOM_EF_DATE_DEBUT(), sdf.format(getAffectationCourant().getDateDebutAff()));
				addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff() == null ? Const.CHAINE_VIDE : sdf.format(getAffectationCourant().getDateFinAff()));
				addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

				// On nomme l'action
				addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
			}
		} else {
			// RG_AG_AF_A02
			// "ERR081",
			// "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimee.")
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR081"));
			setFocus(getNOM_PB_AJOUTER());
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
			setAffectationCourant(null);
			return false;
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AVANTAGE_spec(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre d'Avantages en nature sélectionnés par l'utilisateur
		// parmi ceux issus de la fiche de poste
		int nbAvNatFPSelected = 0;
		for (int i = 0; i < getListeAvantageAFF().size(); i++) {
			if (getListeAvantageFP().contains(getListeAvantageAFF().get(i)))
				nbAvNatFPSelected++;
		}
		// Si la spécificité a supprimer est déjà en base
		if (indiceEltASupprimer - getListeAvantageFP().size() + nbAvNatFPSelected < getListeAvantageAFF().size()) {
			AvantageNature avNatASupprimer = (AvantageNature) getListeAvantageAFF().get(indiceEltASupprimer - getListeAvantageFP().size() + nbAvNatFPSelected);
			if (avNatASupprimer != null) {
				getListeAvantageAFF().remove(avNatASupprimer);
				getListeAvantageASupprimer().add(avNatASupprimer);
			}

		}
		// Si la spécificité a supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			AvantageNature avNatASupprimer = (AvantageNature) getListeAvantageAAjouter().get(indiceEltASupprimer - getListeAvantageFP().size() - getListeAvantageAFF().size() + nbAvNatFPSelected);
			if (avNatASupprimer != null) {
				getListeAvantageAAjouter().remove(avNatASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DELEGATION_spec(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre de Delegation sélectionnées par l'utilisateur parmi
		// ceux issus de la fiche de poste
		int nbDelFPSelected = 0;
		for (int i = 0; i < getListeDelegationAFF().size(); i++) {
			if (getListeDelegationFP().contains(getListeDelegationAFF().get(i)))
				nbDelFPSelected++;
		}
		// Si la spécificité a supprimer est déjà en base
		if (indiceEltASupprimer - getListeDelegationFP().size() + nbDelFPSelected < getListeDelegationAFF().size()) {
			Delegation delASupprimer = (Delegation) getListeDelegationAFF().get(indiceEltASupprimer - getListeDelegationFP().size() + nbDelFPSelected);
			if (delASupprimer != null) {
				getListeDelegationAFF().remove(delASupprimer);
				getListeDelegationASupprimer().add(delASupprimer);
			}

		}
		// Si la spécificité a supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			Delegation delASupprimer = (Delegation) getListeDelegationAAjouter().get(indiceEltASupprimer - getListeDelegationFP().size() - getListeDelegationAFF().size() + nbDelFPSelected);
			if (delASupprimer != null) {
				getListeDelegationAAjouter().remove(delASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_PRIME_POINTAGE_spec(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre de Prime Pointage sélectionnés par l'utilisateur
		// parmi ceux issus de la fiche de poste

		int ppAffSize = 0;
		if (getListePrimePointageAFF() != null) {
			ppAffSize = getListePrimePointageAFF().size();
		}
		// Si la spécificité a supprimer est déjà en base
		if (indiceEltASupprimer - getListePrimePointageFP().size() < ppAffSize) {
			PrimePointageAff primePointageASupprimer = (PrimePointageAff) getListePrimePointageAFF().get(indiceEltASupprimer - getListePrimePointageFP().size());
			if (primePointageASupprimer != null) {
				getListePrimePointageAFF().remove(primePointageASupprimer);
				getListePrimePointageAffASupprimer().add(primePointageASupprimer);
			}

		}
		// Si la spécificité a supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			PrimePointageAff primePointageASupprimer = (PrimePointageAff) getListePrimePointageAffAAjouter().get(indiceEltASupprimer - getListePrimePointageFP().size() - ppAffSize);
			if (primePointageASupprimer != null) {
				getListePrimePointageAffAAjouter().remove(primePointageASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_REGIME_spec(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre de RegimeIndemnitaire sélectionnés par l'utilisateur
		// parmi ceux issus de la fiche de poste
		int nbRegIndemnFPSelected = 0;
		for (int i = 0; i < getListeRegimeAFF().size(); i++) {
			if (getListeRegimeFP().contains(getListeRegimeAFF().get(i)))
				nbRegIndemnFPSelected++;
		}
		// Si la spécificité a supprimer est déjà en base
		if (indiceEltASupprimer - getListeRegimeFP().size() + nbRegIndemnFPSelected < getListeRegimeAFF().size()) {
			RegimeIndemnitaire regIndemnASupprimer = (RegimeIndemnitaire) getListeRegimeAFF().get(indiceEltASupprimer - getListeRegimeFP().size() + nbRegIndemnFPSelected);
			if (regIndemnASupprimer != null) {
				getListeRegimeAFF().remove(regIndemnASupprimer);
				getListeRegimeASupprimer().add(regIndemnASupprimer);
			}

		}
		// Si la spécificité a supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			RegimeIndemnitaire regIndemnASupprimer = (RegimeIndemnitaire) getListeRegimeAAjouter().get(
					indiceEltASupprimer - getListeRegimeFP().size() - getListeRegimeAFF().size() + nbRegIndemnFPSelected);
			if (regIndemnASupprimer != null) {
				getListeRegimeAAjouter().remove(regIndemnASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 * RG_AG_AF_A10 RG_AG_AF_A07
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Si Action Suppression
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
			// ON SUPPRIME LES SPECIFICITES DE L'AFFECTATION LIE
			getPrimePointageAffDao().supprimerToutesPrimePointageAff(getAffectationCourant().getIdAffectation());
			// Suppression
			FichePoste fichePoste = getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste());
			// RG_AG_AF_A01
			HistoAffectation histo = new HistoAffectation(getAffectationCourant());
			getHistoAffectationDao().creerHistoAffectation(histo, user, EnumTypeHisto.SUPPRESSION);

			if (!Connecteur.supprimerSPMTSR(getTransaction(), getAffectationCourant(), getAgentCourant(), fichePoste))
				return false;
			getAffectationDao().supprimerAffectation(getAffectationCourant().getIdAffectation());

			setFocus(getNOM_PB_AJOUTER());
		} else if (getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
			if (performControlerChoixImpression()) {
				// recup du document a imprimer
				String typeDocument = EnumImpressionAffectation.getCodeImpressionAffectation(Integer.parseInt(getZone(getNOM_LB_LISTE_IMPRESSION_SELECT())));
				// Récup affectation courante
				Affectation aff = getAffectationCourant();
				if (getVAL_ST_WARNING().equals(Const.CHAINE_VIDE)) {
					// on verifie si il existe deja un fichier pour cette
					// affectation dans la BD
					if (verifieExistFichier(aff.getIdAffectation(), typeDocument)) {
						// alors on affiche un message
						// :"Attention un fichier du même type existe déjà pour cette affectation. Etes-vous sûr de vouloir écraser la version précédente ?"
						addZone(getNOM_ST_WARNING(), "Attention un fichier du même type existe déjà pour cette affectation. Etes-vous sûr de vouloir écraser la version précédente ?");
					} else {
						imprimeModele(request, typeDocument);
						addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
						addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
						addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
					}
				} else {
					imprimeModele(request, typeDocument);
					addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
					addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
					addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
				}
			}
		} else {
			if (!performControlerSaisie()) {
				return false;
			}

			// #29145 : controle affectation pas sur FDP inactive
			if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				if (!performControleStatutFDP()) {
					return false;
				}
			}

			if (!performControlerRG()) {
				return false;
			}

			// Récup des zones saisies
			String newIndMotifAffectation = getZone(getNOM_LB_MOTIF_AFFECTATION_SELECT());
			MotifAffectation newMotifAffectation = (MotifAffectation) getListeMotifAffectation().get(Integer.parseInt(newIndMotifAffectation));

			// pour recupere le codeEcole
			EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo().toString());

			// Affectation des attributs
			// on sauvegarde l'ancienne date de debut
			Date oldDateDeb = getAffectationCourant().getDateDebutAff();
			getAffectationCourant().setIdAgent(getAgentCourant().getIdAgent());
			getAffectationCourant().setIdFichePoste(getFichePosteCourant().getIdFichePoste());
			getAffectationCourant().setRefArreteAff(getVAL_EF_REF_ARRETE().length() == 0 ? null : getVAL_EF_REF_ARRETE());
			getAffectationCourant().setDateArrete(getVAL_EF_DATE_ARRETE().equals(Const.CHAINE_VIDE) ? null : sdf.parse(Services.formateDate(getVAL_EF_DATE_ARRETE())));
			getAffectationCourant().setDateDebutAff(sdf.parse(Services.formateDate(getVAL_EF_DATE_DEBUT())));
			getAffectationCourant().setDateFinAff(getVAL_EF_DATE_FIN().equals(Const.CHAINE_VIDE) ? null : sdf.parse(Services.formateDate(getVAL_EF_DATE_FIN())));
			getAffectationCourant().setIdMotifAffectation(newMotifAffectation.getIdMotifAffectation());
			getAffectationCourant().setTempsTravail(getListeTempsTravail()[Integer.parseInt(getVAL_LB_TEMPS_TRAVAIL_SELECT())]);
			getAffectationCourant().setCodeEcole(eg.getCdEcol());
			getAffectationCourant().setIdFichePosteSecondaire(getFichePosteSecondaireCourant() != null ? getFichePosteSecondaireCourant().getIdFichePoste() : null);
			getAffectationCourant().setCommentaire(getVAL_EF_COMMENTAIRE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_COMMENTAIRE());

			// Base horaire de congé
			int numLigneBaseHoraireAbsence = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) : -1);

			if (numLigneBaseHoraireAbsence == 0 || getListeBaseHoraireAbsence().isEmpty() || numLigneBaseHoraireAbsence > getListeBaseHoraireAbsence().size()) {

				// bug #16097
				SimpleDateFormat sdfMairie = new SimpleDateFormat("yyyyMMdd");
				Carriere carrEnCours = Carriere.chercherCarriereEnCoursAvecAgentEtDate(getTransaction(), new Integer(sdfMairie.format(getAffectationCourant().getDateDebutAff())), getAgentCourant());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				if (carrEnCours != null && carrEnCours.getCodeCategorie() != null) {
					if (!Carriere.isCarriereConseilMunicipal(carrEnCours.getCodeCategorie())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base de congé"));
						return false;
					}
				} else {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base de congé"));
					return false;
				}
			}
			if (numLigneBaseHoraireAbsence != 0) {
				RefTypeSaisiCongeAnnuelDto baseHoraireAbsence = (RefTypeSaisiCongeAnnuelDto) getListeBaseHoraireAbsence().get(numLigneBaseHoraireAbsence - 1);

				getAffectationCourant().setIdBaseHoraireAbsence(baseHoraireAbsence.getIdRefTypeSaisiCongeAnnuel());
			}

			// Base horaire de pointage
			int numLigneBaseHorairePointage = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) : -1);

			if (numLigneBaseHorairePointage == 0 || getListeBaseHorairePointage().isEmpty() || numLigneBaseHorairePointage > getListeBaseHorairePointage().size()) {

				// bug #16097
				SimpleDateFormat sdfMairie = new SimpleDateFormat("yyyyMMdd");
				Carriere carrEnCours = Carriere.chercherCarriereEnCoursAvecAgentEtDate(getTransaction(), new Integer(sdfMairie.format(getAffectationCourant().getDateDebutAff())), getAgentCourant());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				if (carrEnCours != null && carrEnCours.getCodeCategorie() != null) {
					if (!Carriere.isCarriereConseilMunicipal(carrEnCours.getCodeCategorie())) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire de pointage"));
						return false;
					}
				} else {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire de pointage"));
					return false;
				}
			}
			if (numLigneBaseHorairePointage != 0) {
				BaseHorairePointage baseHorairePointage = (BaseHorairePointage) getListeBaseHorairePointage().get(numLigneBaseHorairePointage - 1);

				getAffectationCourant().setIdBaseHorairePointage(baseHorairePointage.getIdBaseHorairePointage());
			}

			if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION)) {
				// Modification
				// RG_AG_AF_A01
				// bug #29887 sur affectation inactive
				if (!Connecteur.modifierSPMTSR(getTransaction(), getAffectationCourant(), getAgentCourant().getNomatr(), getFichePosteCourant(), oldDateDeb, getAffectationCourant().isActive()))
					return false;

				HistoAffectation histo = new HistoAffectation(getAffectationCourant());
				getHistoAffectationDao().creerHistoAffectation(histo, user, EnumTypeHisto.MODIFICATION);
				getAffectationDao().modifierAffectation(getAffectationCourant());

				// mise à jour du champ primaire de sppost
				Connecteur.modifierSPPOST_Primaire(getTransaction(), getFichePosteCourant().getNumFp(), true);

				if (getFichePosteSecondaireCourant() != null) {
					Connecteur.modifierSPPOST_Primaire(getTransaction(), getFichePosteSecondaireCourant().getNumFp(), false);
				}

			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// mise à jour du champ primaire de sppost
				Connecteur.modifierSPPOST_Primaire(getTransaction(), getFichePosteCourant().getNumFp(), true);
				if (getFichePosteSecondaireCourant() != null) {
					Connecteur.modifierSPPOST_Primaire(getTransaction(), getFichePosteSecondaireCourant().getNumFp(), false);
				}
				// Création Affectation
				FichePoste fichePoste = getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste());

				// #13805 bug depuis que le schema SIRH en JDBC Template
				// si une erreur survient, on peut avoir un dephasage entre
				// SPMTSR et AFFECTATION : comme gerer par 2 transactions
				// disctinctes
				// si erreur => rollback sur une seule transaction
				// solution : on cree en 1er SPMTSR, car si doublon, l AS400
				// retourne une erreur contrairement a AFFECTATION
				try {
					if (!Connecteur.creerSPMTSR(getTransaction(), getAffectationCourant(), getAgentCourant(), fichePoste)) {
						getTransaction().declarerErreur("L'affectation est déjà créée.");
						return false;
					}
				} catch (Exception e) {
					getTransaction().declarerErreur("L'affectation est déjà créée.");
					return false;
				}
				// dans un 2e temps on verifie qu il n existe pas deja une
				// affeca tion pour l agent au meme date
				Affectation affectationExistante = getAffectationDao().chercherAffectationAgentPourDate(getAffectationCourant().getIdAgent(), getAffectationCourant().getDateDebutAff());
				if (null != affectationExistante && affectationExistante.getDateDebutAff().equals(getAffectationCourant().getDateDebutAff())) {
					return false;
				}
				// RG_AG_AF_A01
				Integer idCreer = getAffectationDao().creerAffectation(getAffectationCourant());
				getAffectationCourant().setIdAffectation(idCreer);

				HistoAffectation histo = new HistoAffectation(getAffectationCourant());
				getHistoAffectationDao().creerHistoAffectation(histo, user, EnumTypeHisto.CREATION);

				commitTransaction();

				// on initialise le compteur de congé
				@SuppressWarnings("unused")
				ReturnMessageDto erreurDto = absService.initialiseCompteurConge(getAgentConnecte(request).getIdAgent(), getAgentCourant().getIdAgent());

				// on sauvegarde les FDP au moment de la creation d'une
				// affectation
				// RG_AG_AF_A07
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					if (!sauvegardeFDP(getAffectationCourant().getIdFichePosteSecondaire())) {
						return false;
					}
				}

				if (!sauvegardeFDP(getAffectationCourant().getIdFichePoste())) {
					return false;
				}

			}
			if (getTransaction().isErreur())
				return false;
		}

		commitTransaction();

		if (getAffectationCourant().getIdAffectation() == null)
			return false;

		// Sauvegarde des nouveaux avantages nature et suppression des anciens
		for (int i = 0; i < getListeAvantageAAjouter().size(); i++) {
			AvantageNature avNat = (AvantageNature) getListeAvantageAAjouter().get(i);
			Integer idCreer = getAvantageNatureDao().creerAvantageNature(avNat.getNumRubrique(), avNat.getIdTypeAvantage(), avNat.getIdNatureAvantage(), avNat.getMontant());
			AvantageNatureAFF avNatAFF = new AvantageNatureAFF(getAffectationCourant().getIdAffectation(), idCreer);
			getAvantageNatureAffDao().creerAvantageNatureAff(avNatAFF.getIdAvantage(), avNatAFF.getIdAffectation());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu être créé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (int i = 0; i < getListeAvantageASupprimer().size(); i++) {
			AvantageNature avNat = (AvantageNature) getListeAvantageASupprimer().get(i);
			try {
				AvantageNatureAFF avNatAFF = getAvantageNatureAffDao().chercherAvantageNatureAFF(avNat.getIdAvantage(), getAffectationCourant().getIdAffectation());
				getAvantageNatureAffDao().supprimerAvantageNatureAff(avNatAFF.getIdAvantage(), avNatAFF.getIdAffectation());
				getAvantageNatureDao().supprimerAvantageNature(avNat.getIdAvantage());
			} catch (Exception e) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu être supprimé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		// Sauvegarde des nouvelles Delegation et suppression des anciennes
		for (int i = 0; i < getListeDelegationAAjouter().size(); i++) {
			Delegation deleg = (Delegation) getListeDelegationAAjouter().get(i);
			Integer idCreer = getDelegationDao().creerDelegation(deleg.getIdTypeDelegation(), deleg.getLibDelegation());
			DelegationAFF delAFF = new DelegationAFF(getAffectationCourant().getIdAffectation(), idCreer);
			getDelegationAffDao().creerDelegationAFF(delAFF.getIdDelegation(), delAFF.getIdAffectation());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une Delegation n'a pu être créée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (int i = 0; i < getListeDelegationASupprimer().size(); i++) {
			Delegation deleg = (Delegation) getListeDelegationASupprimer().get(i);
			try {
				DelegationAFF delAFF = getDelegationAffDao().chercherDelegationAFF(getAffectationCourant().getIdAffectation(), deleg.getIdDelegation());
				getDelegationAffDao().supprimerDelegationAFF(delAFF.getIdDelegation(), delAFF.getIdAffectation());
				getDelegationDao().supprimerDelegation(deleg.getIdDelegation());
			} catch (Exception e) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une Delegation n'a pu être supprimée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		// Sauvegarde des nouveaux RegimeIndemnitaire et suppression des anciens
		for (int i = 0; i < getListeRegimeAAjouter().size(); i++) {
			RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeAAjouter().get(i);
			Integer idCreer = getRegIndemnDao().creerRegimeIndemnitaire(regIndemn.getIdTypeRegIndemn(), regIndemn.getNumRubrique(), regIndemn.getForfait(), regIndemn.getNombrePoints());
			RegIndemnAFF riAFF = new RegIndemnAFF(getAffectationCourant().getIdAffectation(), idCreer);
			getRegIndemnAffDao().creerRegIndemnAFF(riAFF.getIdRegime(), riAFF.getIdAffectation());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu être créé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (int i = 0; i < getListeRegimeASupprimer().size(); i++) {
			RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeASupprimer().get(i);
			try {
				RegIndemnAFF riAFF = getRegIndemnAffDao().chercherRegIndemnAFF(getAffectationCourant().getIdAffectation(), regIndemn.getIdRegIndemn());
				getRegIndemnAffDao().supprimerRegIndemnAFF(riAFF.getIdRegime(), riAFF.getIdAffectation());
				getRegIndemnDao().supprimerRegimeIndemnitaire(regIndemn.getIdRegIndemn());
			} catch (Exception e) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu être supprimé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		for (PrimePointageAff prime : getListePrimePointageAffAAjouter()) {
			try {
				getPrimePointageAffDao().creerPrimePointageAff(prime.getNumRubrique(), getAffectationCourant().getIdAffectation());

			} catch (Exception e) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une prime de pointage n'a pu être créée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (PrimePointageAff prime : getListePrimePointageAffASupprimer()) {
			try {
				getPrimePointageAffDao().supprimerPrimePointageAff(getAffectationCourant().getIdAffectation(), prime.getNumRubrique());
			} catch (Exception e) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une prime de pointage n'a pu être supprimée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		if (getListePrimePointageAFF() == null && getListePrimePointageAffAAjouter().size() > 0) {
			setListePrimePointageAFF(new ArrayList<PrimePointageAff>());
		}
		for (PrimePointageAff prime : getListePrimePointageAffAAjouter()) {
			getListePrimePointageAFF().add(prime);
		}
		getListePrimePointageAffAAjouter().clear();
		getListePrimePointageAffASupprimer().clear();

		if (!getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
			// Tout s'est bien passé
			commitTransaction();

			if (!getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
				// #30794 : si l'affectation à une date de fin
				if (getAffectationCourant().getDateFinAff() != null) {
					// si date de fin < dateJour et que FDP non affectée à qqn
					// d'autre --> on passe la FDP en inactive
					Date dateJour = new SimpleDateFormat("dd/MM/yyyy").parse(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
					if (getAffectationCourant().getDateFinAff().compareTo(dateJour) < 0) {
						if (getAffectationDao().listerAffectationActiveOuFuturAvecFP(getFichePosteCourant().getIdFichePoste()).size() == 0) {
							getFichePosteCourant().setIdStatutFp(new Integer(EnumStatutFichePoste.INACTIVE.getId()));
							getFichePosteCourant().setIdResponsable(null);
							getFichePosteDao().modifierFichePoste(getFichePosteCourant(), getHistoFichePosteDao(), user, getTransaction(), getAffectationDao());
							commitTransaction();
							// "INF013", "Attention : la FDP @ a été inactivée."
							getTransaction().declarerErreur(MessageUtils.getMessage("INF013", getFichePosteCourant().getNumFp()));

						}
					} else {
						if (getFichePosteCourant().getIdStatutFp().toString().equals(EnumStatutFichePoste.INACTIVE.getId())) {
							// "INF011",
							// "Attention : la fiche de poste @ est @."
							getTransaction().declarerErreur(MessageUtils.getMessage("INF011", getFichePosteCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
						} else {
							// sinon, on informe l'utilisateur qu'il faudra
							// passer
							// la
							// FDP en inactive.
							// "INF012",
							// "Attention : il faudra penser à passer la FDP en @ à partir du @."
							getTransaction().declarerErreur(
									MessageUtils.getMessage("INF012", EnumStatutFichePoste.INACTIVE.getLibLong(), new SimpleDateFormat("dd/MM/yyyy").format(getAffectationCourant().getDateFinAff())));
						}
					}
				}
			}

			// Réinitialisation
			initialiseListeAffectation(request);
			// init de l'affectation courante
			setAffectationCourant(null);
			// On vide les zones de saisie
			initialiseAffectationVide();

			// #29145 : on regarde si la FDP est en statut transitoire --> alors
			// on informe
			StatutFP statutFDPPrincipale = getStatutFPDao().chercherStatutFP(getFichePosteCourant().getIdStatutFp());
			if (statutFDPPrincipale.getLibStatutFp().equals(EnumStatutFichePoste.TRANSITOIRE.getLibLong())) {
				// "INF011", "Attention : la fiche de poste @ est @."O
				getTransaction().declarerErreur(MessageUtils.getMessage("INF011", getFichePosteCourant().getNumFp(), EnumStatutFichePoste.TRANSITOIRE.getLibLong()));
			} else if (statutFDPPrincipale.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
				// "INF011", "Attention : la fiche de poste @ est @."O
				getTransaction().declarerErreur(MessageUtils.getMessage("INF011", getFichePosteCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
			}
			if (getFichePosteSecondaireCourant() != null) {
				StatutFP statutFDPSecondaire = getStatutFPDao().chercherStatutFP(getFichePosteSecondaireCourant().getIdStatutFp());
				if (statutFDPSecondaire.getLibStatutFp().equals(EnumStatutFichePoste.TRANSITOIRE.getLibLong())) {
					// "INF011", "Attention : la fiche de poste @ est @."
					getTransaction().declarerErreur(MessageUtils.getMessage("INF011", "secondaire " + getFichePosteSecondaireCourant().getNumFp(), EnumStatutFichePoste.TRANSITOIRE.getLibLong()));
				} else if (statutFDPSecondaire.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
					// "INF011", "Attention : la fiche de poste @ est @."
					getTransaction().declarerErreur(MessageUtils.getMessage("INF011", "secondaire " + getFichePosteSecondaireCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
				}
			}
			// On supprime la fiche de poste
			setFichePosteCourant(null);

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	private boolean performControleStatutFDP() throws Exception {
		// #29145 : controle affectation pas sur FDP inactive
		StatutFP statutFDPPrincipale = getStatutFPDao().chercherStatutFP(getFichePosteCourant().getIdStatutFp());
		if (statutFDPPrincipale.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
			// "ERR089",
			// "La fiche de poste @ est @. Vous ne pouvez pas créer d'affectation"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR089", getFichePosteCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		} else if (statutFDPPrincipale.getLibStatutFp().equals(EnumStatutFichePoste.EN_CREATION.getLibLong())) {
			// "ERR089",
			// "La fiche de poste @ est @. Vous ne pouvez pas créer d'affectation"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR089", getFichePosteCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		if (getFichePosteSecondaireCourant() != null) {
			StatutFP statutFDPSecondaire = getStatutFPDao().chercherStatutFP(getFichePosteSecondaireCourant().getIdStatutFp());
			if (statutFDPSecondaire.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
				// "ERR089",
				// "La fiche de poste @ est @. Vous ne pouvez pas créer d'affectation"
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR089", "secondaire " + getFichePosteSecondaireCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			} else if (statutFDPSecondaire.getLibStatutFp().equals(EnumStatutFichePoste.EN_CREATION.getLibLong())) {
				// "ERR089",
				// "La fiche de poste @ est @. Vous ne pouvez pas créer d'affectation"
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR089", "secondaire " + getFichePosteSecondaireCourant().getNumFp(), EnumStatutFichePoste.INACTIVE.getLibLong()));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VALIDER_AJOUT_spec(HttpServletRequest request) throws Exception {

		if (getVAL_ST_SPECIFICITE().equals(SPEC_AVANTAGE_NATURE_SPEC)) {
			// Controle des champs
			if (!performControlerSaisieAvNat_spec(request))
				return false;

			// Alimentation de l'objet
			AvantageNature avNat = new AvantageNature();

			avNat.setMontant(Double.valueOf(getVAL_EF_MONTANT_AVANTAGE()));

			int indiceTypeAvantage = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT()) : -1);
			avNat.setIdTypeAvantage(((TypeAvantage) getListeTypeAvantage().get(indiceTypeAvantage)).getIdTypeAvantage());
			int indiceNatAvantage = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()) : -1);
			avNat.setIdNatureAvantage(((NatureAvantage) getListeNatureAvantage().get(indiceNatAvantage)).getIdNatureAvantage());
			int indiceRubAvantage = (Services.estNumerique(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) : -1);
			if (indiceRubAvantage > 0)
				avNat.setNumRubrique(getListeRubrique().get(indiceRubAvantage - 1).getNorubr());

			if (getListeAvantageAFF() == null)
				setListeAvantageAFF(new ArrayList<AvantageNature>());

			if (!getListeAvantageAFF().contains(avNat) && !getListeAvantageFP().contains(avNat) && !getListeAvantageAAjouter().contains(avNat)) {
				if (getListeAvantageASupprimer().contains(avNat)) {
					getListeAvantageASupprimer().remove(avNat);
					getListeAvantageAFF().add(avNat);
				} else {
					getListeAvantageAAjouter().add(avNat);
				}
			}
			// Réinitialisation des champs de saisie
			viderAvantageNature_spec();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_DELEGATION_SPEC)) {
			// Controle des champs
			if (!performControlerSaisieDel_spec(request))
				return false;

			// Alimentation de l'objet
			Delegation deleg = new Delegation();

			deleg.setLibDelegation(getVAL_EF_COMMENT_DELEGATION());

			int indiceTypeDelegation = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);
			deleg.setIdTypeDelegation(((TypeDelegation) getListeTypeDelegation().get(indiceTypeDelegation)).getIdTypeDelegation());

			if (getListeDelegationAFF() == null)
				setListeDelegationAFF(new ArrayList<Delegation>());

			if (!getListeDelegationAFF().contains(deleg) && !getListeDelegationFP().contains(deleg) && !getListeDelegationAAjouter().contains(deleg)) {
				if (getListeDelegationASupprimer().contains(deleg)) {
					getListeDelegationASupprimer().remove(deleg);
					getListeDelegationAFF().add(deleg);
				} else {
					getListeDelegationAAjouter().add(deleg);
				}
			}
			// Réinitialisation des champs de saisie
			viderDelegation_spec();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_REG_INDEMN_SPEC)) {
			// Controle des champs
			if (!performControlerSaisieRegIndemn_spec(request))
				return false;

			// Alimentation de l'objet
			RegimeIndemnitaire regIndemn = new RegimeIndemnitaire();

			regIndemn.setForfait(Double.valueOf(getVAL_EF_FORFAIT_REGIME()));
			regIndemn.setNombrePoints(Integer.valueOf(getVAL_EF_NB_POINTS_REGIME()));

			int indiceRegIndemn = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);
			regIndemn.setIdTypeRegIndemn(((TypeRegIndemn) getListeTypeRegIndemn().get(indiceRegIndemn)).getIdTypeRegIndemn());
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_RUBRIQUE_REGIME_SELECT()) : -1);
			if (indiceRub > 0)
				regIndemn.setNumRubrique(getListeRubrique().get(indiceRub - 1).getNorubr());

			if (getListeRegimeAFF() == null)
				setListeRegimeAFF(new ArrayList<RegimeIndemnitaire>());

			if (!getListeRegimeAFF().contains(regIndemn) && !getListeRegimeFP().contains(regIndemn) && !getListeRegimeAAjouter().contains(regIndemn)) {
				if (getListeRegimeASupprimer().contains(regIndemn)) {
					getListeRegimeASupprimer().remove(regIndemn);
					getListeRegimeAFF().add(regIndemn);
				} else {
					getListeRegimeAAjouter().add(regIndemn);
				}
			}

			// Réinitialisation des champs de saisie
			viderRegIndemn_spec();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_PRIME_POINTAGE_SPEC)) {
			// Controle des champs
			if (!performControlerSaisiePrimePointage_spec(request))
				return false;

			int indiceRub = getListePrimes().get(Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1)
					.getNumRubrique();
			// Alimentation de l'objet
			if (!getListeRubs().contains(indiceRub)) {
				PrimePointageAff prime = new PrimePointageAff();
				prime.setNumRubrique(indiceRub);

				if (getListePrimePointageAFF() == null)
					setListePrimePointageAFF(new ArrayList<PrimePointageAff>());

				if (getListePrimePointageAffASupprimer().contains(prime)) {
					getListePrimePointageAffASupprimer().remove(prime);
					getListePrimePointageAFF().add(prime);
				} else {
					getListePrimePointageAffAAjouter().add(prime);
				}
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR088"));
				setFocus(getNOM_PB_AJOUTER());
			}

			// Réinitialisation des champs de saisie
			viderPrimePointage();
		}

		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER_FP
			if (testerParametre(request, getNOM_PB_RECHERCHER_FP())) {
				return performPB_RECHERCHER_FP(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_FP_SECONDAIRE
			if (testerParametre(request, getNOM_PB_RECHERCHER_FP_SECONDAIRE())) {
				return performPB_RECHERCHER_FP_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_HISTORIQUE
			if (testerParametre(request, getNOM_PB_HISTORIQUE())) {
				return performPB_HISTORIQUE(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
				// Si clic sur le bouton PB_CONSULTER
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
				// Si clic sur le bouton PB_SUPPRIMER
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
				// Si clic sur le bouton PB_IMPRIMER
				if (testerParametre(request, getNOM_PB_IMPRIMER(i))) {
					return performPB_IMPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_AVANTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_AVANTAGE())) {
				return performPB_AJOUTER_AVANTAGE_spec(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DELEGATION
			if (testerParametre(request, getNOM_PB_AJOUTER_DELEGATION())) {
				return performPB_AJOUTER_DELEGATION_spec(request);
			}

			// Si clic sur le bouton PB_AJOUTER_REGIME
			if (testerParametre(request, getNOM_PB_AJOUTER_REGIME())) {
				return performPB_AJOUTER_REGIME_spec(request);
			}

			// Si clic sur le bouton PB_AJOUTER_PRIME_POINTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_PRIME_POINTAGE())) {
				return performPB_AJOUTER_PRIME_POINTAGE_spec(request);
			}

			// Si clic sur le bouton AJOUTER PRIME POINTAGE FP
			for (int i = 0; i < getListePrimePointageFP().size(); i++) {
				if (testerParametre(request, getNOM_PB_SET_PRIME_POINTAGE(i))) {
					getListePrimePointageAffAAjouter().add(new PrimePointageAff(getListePrimePointageFP().get(i), getAffectationCourant().getIdAffectation()));
					return true;
				}
			}

			// Si clic sur le bouton PB_CHANGER_SPECIFICITE
			if (testerParametre(request, getNOM_PB_CHANGER_SPECIFICITE())) {
				return performPB_CHANGER_SPECIFICITE_spec(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AVANTAGE
			for (int i = getListeAvantageFP().size(); i < getListeAvantageFP().size() + getListeAvantageAFF().size() + getListeAvantageAAjouter().size() - getListeAvantageASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AVANTAGE(i))) {
					return performPB_SUPPRIMER_AVANTAGE_spec(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DELEGATION
			for (int i = getListeDelegationFP().size(); i < getListeDelegationFP().size() + getListeDelegationAFF().size() + getListeDelegationAAjouter().size()
					- getListeDelegationASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DELEGATION(i))) {
					return performPB_SUPPRIMER_DELEGATION_spec(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_REGIME
			for (int i = getListeRegimeFP().size(); i < getListeRegimeFP().size() + getListeRegimeAFF().size() + getListeRegimeAAjouter().size() - getListeRegimeASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_REGIME(i))) {
					return performPB_SUPPRIMER_REGIME_spec(request, i);
				}
			}

			if (getListePrimePointageAFF() == null)
				setListePrimePointageAFF(new ArrayList<PrimePointageAff>());

			// Si clic sur le bouton PB_SUPPRIMER_PRIME_POINTAGE
			for (int i = 0; i < getListePrimePointageFP().size() + getListePrimePointageAFF().size() + getListePrimePointageAffAAjouter().size(); i++) {

				if (testerParametre(request, getNOM_PB_SUPPRIMER_PRIME_POINTAGE(i))) {
					return performPB_SUPPRIMER_PRIME_POINTAGE_spec(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_AJOUT
			if (testerParametre(request, getNOM_PB_VALIDER_AJOUT())) {
				return performPB_VALIDER_AJOUT_spec(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean sauvegardeFDP(Integer idFichePoste) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("SauvegardeFDP");

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String destinationFDP = "SauvegardeFDP/SauvFP_" + idFichePoste + "_" + dateJour + ".doc";

		try {
			byte[] fileAsBytes = sirhService.downloadFichePoste(idFichePoste);

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destinationFDP)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

			// Tout s'est bien passé
			// on crée le document en base de données
			Document d = new Document();
			d.setIdTypeDocument(1);
			d.setLienDocument(destinationFDP);
			d.setNomDocument("SauvFP_" + idFichePoste + "_" + dateJour + ".doc");
			d.setDateDocument(new Date());
			d.setCommentaire("Sauvegarde automatique lors création affectation.");
			Integer id = getDocumentDao().creerDocument(d.getClasseDocument(), d.getNomDocument(), d.getLienDocument(), d.getDateDocument(), d.getCommentaire(), d.getIdTypeDocument(),
					d.getNomOriginal());

			DocumentAgent lda = new DocumentAgent();
			lda.setIdAgent(getAgentCourant().getIdAgent());
			lda.setIdDocument(id);
			getLienDocumentAgentDao().creerDocumentAgent(lda.getIdAgent(), lda.getIdDocument());

			if (getTransaction().isErreur())
				return false;

			commitTransaction();

		} catch (Exception e) {
			// "ERR185",
			// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
			return false;
		}

		return true;
	}

	/**
	 * Met a jour l'affectation en cours.
	 * 
	 * @param affectationCourant
	 *            Nouvelle affectation en cours
	 */
	private void setAffectationCourant(Affectation affectationCourant) {
		this.affectationCourant = affectationCourant;
	}

	/**
	 * Met a jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Met a jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteCourant(FichePoste fichePosteCourant) {
		this.fichePosteCourant = fichePosteCourant;
	}

	/**
	 * Met a jour la fiche de poste secondaire courante.
	 * 
	 * @param fichePosteSecondaireCourant
	 */
	private void setFichePosteSecondaireCourant(FichePoste fichePosteSecondaireCourant) {
		this.fichePosteSecondaireCourant = fichePosteSecondaireCourant;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Setter de la liste: LB_LISTE_IMPRESSION Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_LISTE_IMPRESSION(String[] newLB_LISTE_IMPRESSION) {
		LB_LISTE_IMPRESSION = newLB_LISTE_IMPRESSION;
	}

	/**
	 * Setter de la liste: LB_MOTIF_AFFECTATION Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_MOTIF_AFFECTATION(String[] newLB_MOTIF_AFFECTATION) {
		LB_MOTIF_AFFECTATION = newLB_MOTIF_AFFECTATION;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE_spec(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_AVANTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_AVANTAGE(String[] newLB_RUBRIQUE_AVANTAGE) {
		LB_RUBRIQUE_AVANTAGE = newLB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_PRIME_POINTAGE Date de création :
	 * (16/08/11 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_PRIME_POINTAGE_spec(String[] newLB_RUBRIQUE_PRIME_POINTAGE) {
		LB_RUBRIQUE_PRIME_POINTAGE = newLB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_REGIME Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_REGIME(String[] newLB_RUBRIQUE_REGIME) {
		LB_RUBRIQUE_REGIME = newLB_RUBRIQUE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TEMPS_TRAVAIL Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_TEMPS_TRAVAIL(String[] newLB_TEMPS_TRAVAIL) {
		LB_TEMPS_TRAVAIL = newLB_TEMPS_TRAVAIL;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_TYPE_DELEGATION(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	private void setLB_TYPE_REGIME(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * @param listeAffectation
	 *            listeAffectation à définir
	 */
	private void setListeAffectation(ArrayList<Affectation> listeAffectation) {
		this.listeAffectation = listeAffectation;
	}

	/**
	 * Met a jour la liste des AvantageNature de l'affectation.
	 * 
	 * @param listeAvantageAFF
	 */
	private void setListeAvantageAFF(ArrayList<AvantageNature> listeAvantageAFF) {
		this.listeAvantageAFF = listeAvantageAFF;
	}

	/**
	 * Met a jour la liste des AvantageNature de la fiche de poste.
	 * 
	 * @param listeAvantageFP
	 *            listeAvantageFP à définir
	 */
	private void setListeAvantageFP(ArrayList<AvantageNature> listeAvantageFP) {
		this.listeAvantageFP = listeAvantageFP;
	}

	/**
	 * Met a jour la liste des Delegation de l'affectation.
	 * 
	 * @param listeDelegationAFF
	 */
	private void setListeDelegationAFF(ArrayList<Delegation> listeDelegationAFF) {
		this.listeDelegationAFF = listeDelegationAFF;
	}

	/**
	 * Met a jour la liste des Delegation de la fiche de poste.
	 * 
	 * @param listeDelegationFP
	 *            listeDelegationFP à définir
	 */
	private void setListeDelegationFP(ArrayList<Delegation> listeDelegationFP) {
		this.listeDelegationFP = listeDelegationFP;
	}

	/**
	 * Met a jour la liste des motifs d'affectation.
	 * 
	 * @param listeMotifAffectation
	 *            Nouvelle liste des motifs d'affectations
	 */
	private void setListeMotifAffectation(ArrayList<MotifAffectation> listeMotifAffectation) {
		this.listeMotifAffectation = listeMotifAffectation;
	}

	/**
	 * Met a jour la liste des natures d'avantage en nature.
	 * 
	 * @param listeNatureAvantage
	 */
	private void setListeNatureAvantage(ArrayList<NatureAvantage> listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	/**
	 * Met a jour la liste des PrimePointageIndemnitaire de l'affectation.
	 * 
	 * @param listePrimePointageAFF
	 */
	private void setListePrimePointageAFF(ArrayList<PrimePointageAff> listePrimePointageAFF) {
		this.listePrimePointageAFF = listePrimePointageAFF;
	}

	/**
	 * Met a jour la liste des PrimePointage de la fiche de poste.
	 * 
	 * @param listePrimePointageFP
	 */
	private void setListePrimePointageFP(ArrayList<PrimePointageFP> listePrimePointageFP) {
		this.listePrimePointageFP = listePrimePointageFP;
	}

	/**
	 * Met a jour la liste des primes.
	 * 
	 * @param listeRubrique
	 */
	private void setListePrimes(List<RefPrimeDto> listePrimes) {
		this.listePrimes = listePrimes;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire de l'affectation.
	 * 
	 * @param listeRegimeAFF
	 */
	private void setListeRegimeAFF(ArrayList<RegimeIndemnitaire> listeRegimeAFF) {
		this.listeRegimeAFF = listeRegimeAFF;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire de la fiche de poste.
	 * 
	 * @param listeRegimeFP
	 *            listeRegimeFP à définir
	 */
	private void setListeRegimeFP(ArrayList<RegimeIndemnitaire> listeRegimeFP) {
		this.listeRegimeFP = listeRegimeFP;
	}

	/**
	 * Met a jour la liste des rubriques.
	 * 
	 * @param listeRubrique
	 */
	private void setListeRubrique(List<Rubrique> listeRubrique) {
		this.listeRubrique = listeRubrique;
	}

	/**
	 * Met a jour la liste des temps de travail.
	 * 
	 * @param listeTempsTravail
	 *            Liste des temps de travail
	 */
	private void setListeTempsTravail(String[] listeTempsTravail) {
		this.listeTempsTravail = listeTempsTravail;
	}

	/**
	 * Met a jour la liste des types d'avantage en nature.
	 * 
	 * @param listeTypeAvantage
	 */
	private void setListeTypeAvantage(ArrayList<TypeAvantage> listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	/**
	 * Met a jour la liste des TypeDelegation.
	 * 
	 * @param listeTypeDelegation
	 */
	private void setListeTypeDelegation(ArrayList<TypeDelegation> listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeTypeRegIndemn
	 */
	private void setListeTypeRegIndemn(ArrayList<TypeRegIndemn> listeTypeRegIndemn) {
		this.listeTypeRegIndemn = listeTypeRegIndemn;
	}

	public void setPrimeFP_AFF(int indiceRub) {

		// Alimentation de l'objet
		PrimePointageAff prime = new PrimePointageAff();
		prime.setNumRubrique(getListePrimePointageFP().get(indiceRub).getNumRubrique());

		if (getListePrimePointageAFF() == null)
			setListePrimePointageAFF(new ArrayList<PrimePointageAff>());

		if (!getListePrimePointageAFF().contains(prime) && !getListePrimePointageFP().contains(prime) && !getListePrimePointageAffAAjouter().contains(prime)) {
			if (getListePrimePointageAffASupprimer().contains(prime)) {
				getListePrimePointageAffASupprimer().remove(prime);
				getListePrimePointageAFF().add(prime);
			} else {
				getListePrimePointageAffAAjouter().add(prime);
			}
		}

	}

	/**
	 * public int getListPosteCourant_IdRub(int index) { return
	 * getPrimePointageFPDao
	 * ().listerPrimePointageFP(Integer.parseInt(fichePosteCourant
	 * .getIdFichePoste())).get(index).getNumRubrique(); }
	 * 
	 * public int getListAffCourant_IdRub(int index) { return
	 * getPrimePointageAffDao
	 * ().listerPrimePointageAff(Integer.parseInt(affectationCourant
	 * .getIdAffectation())).get(index).getNumRubrique(); }
	 **/

	public void setPrimePointageAffDao(PrimePointageAffDao primePointageAffDao) {
		this.primePointageAffDao = primePointageAffDao;
	}

	public void setPrimePointageFPDao(PrimePointageFPDao primePointageFPDao) {
		this.primePointageFPDao = primePointageFPDao;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	// public String getNomEcran() {
	// return "ECR-AG-EMPLOIS-SPECIFICITES";
	// }

	private boolean verifieExistFichier(Integer idAffectation, String typeDocument) throws Exception {
		// on regarde si le fichier existe
		try {
			getDocumentDao().chercherDocumentByContainsNom("NS_" + idAffectation + "_" + typeDocument);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie déjà que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderAvantageNature_spec() throws Exception {
		addZone(getNOM_LB_TYPE_AVANTAGE_SELECT(), "0");
		addZone(getNOM_LB_NATURE_AVANTAGE_SELECT(), "0");
		addZone(getNOM_EF_MONTANT_AVANTAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT(), "0");
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderDelegation_spec() throws Exception {
		addZone(getNOM_LB_TYPE_DELEGATION_SELECT(), "0");
		addZone(getNOM_EF_COMMENT_DELEGATION(), Const.CHAINE_VIDE);
	}

	/**
	 * Vide les champs de saisie des primes pointage.
	 * 
	 * @throws Exception
	 */
	private void viderPrimePointage() throws Exception {
		addZone(getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT(), Const.ZERO);
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderRegIndemn_spec() throws Exception {
		addZone(getNOM_LB_TYPE_REGIME_SELECT(), "0");
		addZone(getNOM_EF_FORFAIT_REGIME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NB_POINTS_REGIME(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT(), "0");
	}

	public boolean isPrimeSupprimable(int indiceEltASupprimer) throws Exception {

		PrimePointageAff primePointage = null;
		// Si la spécificité a supprimer est déjà en base
		if (indiceEltASupprimer - getListePrimePointageFP().size() < getListePrimePointageAFF().size()) {
			primePointage = (PrimePointageAff) getListePrimePointageAFF().get(indiceEltASupprimer - getListePrimePointageFP().size());
		}
		// Si la spécificité a supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			primePointage = (PrimePointageAff) getListePrimePointageAffAAjouter().get(indiceEltASupprimer - getListePrimePointageFP().size() - getListePrimePointageAFF().size());
		}
		if (primePointage == null) {
			return false;
		}
		// on interroge le WS pour savoir si la prime est utilisee sur un
		// pointage donné.
		return ptgService.isPrimeUtilPointage(primePointage.getNumRubrique(), getAgentCourant().getIdAgent());

	}

	public void setListeAvantageAAjouter(ArrayList<AvantageNature> listeAvantageAAjouter) {
		this.listeAvantageAAjouter = listeAvantageAAjouter;
	}

	public void setListeAvantageASupprimer(ArrayList<AvantageNature> listeAvantageASupprimer) {
		this.listeAvantageASupprimer = listeAvantageASupprimer;
	}

	public void setListeDelegationAAjouter(ArrayList<Delegation> listeDelegationAAjouter) {
		this.listeDelegationAAjouter = listeDelegationAAjouter;
	}

	public void setListeDelegationASupprimer(ArrayList<Delegation> listeDelegationASupprimer) {
		this.listeDelegationASupprimer = listeDelegationASupprimer;
	}

	public void setListePrimePointageAffAAjouter(ArrayList<PrimePointageAff> listePrimePointageAffAAjouter) {
		this.listePrimePointageAffAAjouter = listePrimePointageAffAAjouter;
	}

	public void setListePrimePointageAffASupprimer(ArrayList<PrimePointageAff> listePrimePointageAffASupprimer) {
		this.listePrimePointageAffASupprimer = listePrimePointageAffASupprimer;
	}

	public void setListeRegimeAAjouter(ArrayList<RegimeIndemnitaire> listeRegimeAAjouter) {
		this.listeRegimeAAjouter = listeRegimeAAjouter;
	}

	public void setListeRegimeASupprimer(ArrayList<RegimeIndemnitaire> listeRegimeASupprimer) {
		this.listeRegimeASupprimer = listeRegimeASupprimer;
	}

	public boolean saveFileToRemoteFileSystem(byte[] fileAsBytes, String chemin, String filename) throws Exception {

		BufferedOutputStream bos = null;
		FileObject docFile = null;

		try {
			FileSystemManager fsManager = VFS.getManager();
			docFile = fsManager.resolveFile(String.format("%s", chemin + filename));
			bos = new BufferedOutputStream(docFile.getContent().getOutputStream());
			IOUtils.write(fileAsBytes, bos);
			IOUtils.closeQuietly(bos);

			if (docFile != null) {
				try {
					docFile.close();
				} catch (FileSystemException e) {
					// ignore the exception
				}
			}
		} catch (Exception e) {
			logger.error(String.format("An error occured while writing the report file to the following path  : " + chemin + filename + " : " + e));
			return false;
		}
		return true;
	}

	public MotifAffectationDao getMotifAffectationDao() {
		return motifAffectationDao;
	}

	public void setMotifAffectationDao(MotifAffectationDao motifAffectationDao) {
		this.motifAffectationDao = motifAffectationDao;
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

	public AvantageNatureAffDao getAvantageNatureAffDao() {
		return avantageNatureAffDao;
	}

	public void setAvantageNatureAffDao(AvantageNatureAffDao avantageNatureAffDao) {
		this.avantageNatureAffDao = avantageNatureAffDao;
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

	public DelegationAffDao getDelegationAffDao() {
		return delegationAffDao;
	}

	public void setDelegationAffDao(DelegationAffDao delegationAffDao) {
		this.delegationAffDao = delegationAffDao;
	}

	public RegIndemnAffDao getRegIndemnAffDao() {
		return regIndemnAffDao;
	}

	public void setRegIndemnAffDao(RegIndemnAffDao regIndemnAffDao) {
		this.regIndemnAffDao = regIndemnAffDao;
	}

	public RegIndemnDao getRegIndemnDao() {
		return regIndemnDao;
	}

	public void setRegIndemnDao(RegIndemnDao regIndemnDao) {
		this.regIndemnDao = regIndemnDao;
	}

	public RubriqueDao getRubriqueDao() {
		return rubriqueDao;
	}

	public void setRubriqueDao(RubriqueDao rubriqueDao) {
		this.rubriqueDao = rubriqueDao;
	}

	public DocumentAgentDao getLienDocumentAgentDao() {
		return lienDocumentAgentDao;
	}

	public void setLienDocumentAgentDao(DocumentAgentDao lienDocumentAgentDao) {
		this.lienDocumentAgentDao = lienDocumentAgentDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public TitrePosteDao getTitrePosteDao() {
		return titrePosteDao;
	}

	public void setTitrePosteDao(TitrePosteDao titrePosteDao) {
		this.titrePosteDao = titrePosteDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public StatutFPDao getStatutFPDao() {
		return statutFPDao;
	}

	public void setStatutFPDao(StatutFPDao statutFPDao) {
		this.statutFPDao = statutFPDao;
	}

	public HistoFichePosteDao getHistoFichePosteDao() {
		return histoFichePosteDao;
	}

	public void setHistoFichePosteDao(HistoFichePosteDao histoFichePosteDao) {
		this.histoFichePosteDao = histoFichePosteDao;
	}

	public HistoAffectationDao getHistoAffectationDao() {
		return histoAffectationDao;
	}

	public void setHistoAffectationDao(HistoAffectationDao histoAffectationDao) {
		this.histoAffectationDao = histoAffectationDao;
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
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

	public BaseHorairePointageDao getBaseHorairePointageDao() {
		return baseHorairePointageDao;
	}

	public void setBaseHorairePointageDao(BaseHorairePointageDao baseHorairePointageDao) {
		this.baseHorairePointageDao = baseHorairePointageDao;
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

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	private String[] getLB_BASE_HORAIRE_ABSENCE() {
		if (LB_BASE_HORAIRE_ABSENCE == null)
			LB_BASE_HORAIRE_ABSENCE = initialiseLazyLB();
		return LB_BASE_HORAIRE_ABSENCE;
	}

	private void setLB_BASE_HORAIRE_ABSENCE(String[] newLB_BASE_HORAIRE_ABSENCE) {
		LB_BASE_HORAIRE_ABSENCE = newLB_BASE_HORAIRE_ABSENCE;
	}

	public String getNOM_LB_BASE_HORAIRE_ABSENCE() {
		return "NOM_LB_BASE_HORAIRE_ABSENCE";
	}

	public String getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_ABSENCE_SELECT";
	}

	public String[] getVAL_LB_BASE_HORAIRE_ABSENCE() {
		return getLB_BASE_HORAIRE_ABSENCE();
	}

	public String getVAL_LB_BASE_HORAIRE_ABSENCE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT());
	}

	public ArrayList<RefTypeSaisiCongeAnnuelDto> getListeBaseHoraireAbsence() {
		return listeBaseHoraireAbsence;
	}

	public void setListeBaseHoraireAbsence(ArrayList<RefTypeSaisiCongeAnnuelDto> listeBaseHoraireAbsence) {
		this.listeBaseHoraireAbsence = listeBaseHoraireAbsence;
	}

	public String getNOM_EF_INFO_POINTAGE_FDP() {
		return "NOM_EF_INFO_POINTAGE_FDP";
	}

	public String getVAL_EF_INFO_POINTAGE_FDP() {
		return getZone(getNOM_EF_INFO_POINTAGE_FDP());
	}

	public String getNOM_EF_INFO_ABSENCE_FDP() {
		return "NOM_EF_INFO_ABSENCE_FDP";
	}

	public String getVAL_EF_INFO_ABSENCE_FDP() {
		return getZone(getNOM_EF_INFO_ABSENCE_FDP());
	}

	public SISERVDao getSiservDao() {
		return siservDao;
	}

	public void setSiservDao(SISERVDao siservDao) {
		this.siservDao = siservDao;
	}
}