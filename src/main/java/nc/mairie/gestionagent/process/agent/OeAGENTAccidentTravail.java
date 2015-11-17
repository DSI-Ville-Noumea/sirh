package nc.mairie.gestionagent.process.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.gestionagent.vo.VoAccidentTravailMaladiePro;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.hsct.AccidentTravail;
import nc.mairie.metier.hsct.MaladiePro;
import nc.mairie.metier.hsct.SiegeLesion;
import nc.mairie.metier.hsct.TypeAT;
import nc.mairie.metier.hsct.TypeMaladiePro;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.hsct.AccidentTravailDao;
import nc.mairie.spring.dao.metier.hsct.MaladieProDao;
import nc.mairie.spring.dao.metier.hsct.SiegeLesionDao;
import nc.mairie.spring.dao.metier.hsct.TypeATDao;
import nc.mairie.spring.dao.metier.hsct.TypeMaladieProDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeAGENTAccidentTravail extends BasicProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	
	private Logger logger = LoggerFactory.getLogger(OeAGENTAccidentTravail.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private String[] LB_SIEGE_LESION;
	private String[] LB_AVIS_COMMISSION;
	private String[] LB_TYPE_AT;
	private String[] LB_TYPE_MP;
	private String[] LB_AT_REFERENCE;

	private Agent agentCourant;
	private VoAccidentTravailMaladiePro voAccidentTravailMaladieProCourant;

	private ArrayList<AccidentTravail> listeAT;
	private ArrayList<MaladiePro> listeMP;
	private ArrayList<TypeAT> listeTypeAT;
	private ArrayList<TypeMaladiePro> listeTypeMP;
	private ArrayList<SiegeLesion> listeSiegeLesion;
	private ArrayList<AccidentTravail> listeATReference;
	private ArrayList<VoAccidentTravailMaladiePro> listeAT_MP;

	private Hashtable<Integer, TypeAT> hashTypeAT;
	private Hashtable<Integer, SiegeLesion> hashSiegeLesion;
	private Hashtable<Integer, TypeMaladiePro> hashTypeMP;
	private Hashtable<Integer, AccidentTravail> hashATReference;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche AT.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche AT.";
	private String ACTION_MODIFICATION = "Modification d'une fiche AT.";
	private String ACTION_CREATION = "Création d'une fiche AT.";

	public String ACTION_DOCUMENT = "Documents d'une fiche AT.";
	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document d'une fiche AT.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document d'une fiche AT.";
	private ArrayList<Document> listeDocuments;
	private Document documentCourant;
	private DocumentAgent lienDocumentAgentCourant;
	private String urlFichier;
	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

	private TypeDocumentDao typeDocumentDao;
	private AccidentTravailDao accidentTravailDao;
	private MaladieProDao maladieProDao;
	private SiegeLesionDao siegeLesionDao;
	private TypeATDao typeATDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private TypeMaladieProDao typeMPDao;

	/**
	 * Constructeur du process OeAGENTAccidentTravail. Date de création :
	 * (30/06/11 13:56:32)
	 * 
	 */
	public OeAGENTAccidentTravail() {
		super();
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (30/06/11 14:19:10)
	 * 
	 * RG_AG_AT_C01 RG_AG_AT_C02
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setVoAccidentTravailMaladieProCourant(null);
			multi = null;
			isImporting = false;
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Si hashtable des types d'accident de travail vide
		// RG_AG_AT_C01
		if (getHashTypeAT().size() == 0) {
			ArrayList<TypeAT> listeTypeAT = getTypeATDao().listerTypeAT();
			setListeTypeAT(listeTypeAT);

			if (getListeTypeAT().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<TypeAT> list = getListeTypeAT().listIterator(); list.hasNext();) {
					TypeAT m = (TypeAT) list.next();
					String ligne[] = { m.getDescTypeAt() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_AT(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE_AT(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeTypeAT.size(); i++) {
				TypeAT t = (TypeAT) listeTypeAT.get(i);
				getHashTypeAT().put(t.getIdTypeAt(), t);
			}
		}

		// Si hashtable des types de maladie pro vide
		// RG_AG_AT_C01
		if (getHashTypeMP().size() == 0) {
			ArrayList<TypeMaladiePro> listeTypeMP = getTypeMPDao().listerMaladiePro();
			setListeTypeMP(listeTypeMP);

			if (getListeTypeMP().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<TypeMaladiePro> list = getListeTypeMP().listIterator(); list.hasNext();) {
					TypeMaladiePro m = (TypeMaladiePro) list.next();
					String ligne[] = { m.getLibMaladiePro() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_MP(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE_MP(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeTypeMP.size(); i++) {
				TypeMaladiePro t = (TypeMaladiePro) listeTypeMP.get(i);
				getHashTypeMP().put(t.getIdMaladiePro(), t);
			}
		}

		// Si hashtable des sieges de lésion vide
		// RG_AG_AT_C02
		if (getHashSiegeLesion().size() == 0) {
			ArrayList<SiegeLesion> listeSiegeLesion = getSiegeLesionDao().listerSiegeLesion();
			setListeSiegeLesion(listeSiegeLesion);

			if (getListeSiegeLesion().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<SiegeLesion> list = getListeSiegeLesion().listIterator(); list.hasNext();) {
					SiegeLesion m = (SiegeLesion) list.next();
					String ligne[] = { m.getDescSiege() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_SIEGE_LESION(aFormat.getListeFormatee(true));
			} else {
				setLB_SIEGE_LESION(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeSiegeLesion.size(); i++) {
				SiegeLesion s = (SiegeLesion) listeSiegeLesion.get(i);
				getHashSiegeLesion().put(s.getIdSiege(), s);
			}
		}
		
		// avis commission
		if(null == getLB_AVIS_COMMISSION()
				|| 2 > getLB_AVIS_COMMISSION().length) {
			int tailles[] = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			String ligneAccepte[] = { "Accepté" };
			aFormat.ajouteLigne(ligneAccepte);
			String ligneRefus[] = { "Refus" };
			aFormat.ajouteLigne(ligneRefus);
			setLB_AVIS_COMMISSION(aFormat.getListeFormatee(true));
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeAT_MP(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
		
		// AT de référence
		if (getHashATReference().size() == 0) {
			ArrayList<AccidentTravail> listATReference = getAccidentTravailDao().listerAccidentTravailAgent(getAgentCourant().getIdAgent());
			if(null != listATReference) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for(AccidentTravail atReference : listATReference) {
					if(!atReference.getRechute()) {
						SiegeLesion s = getHashSiegeLesion().get(atReference.getIdSiege());
						String ligne[] = { sdf.format(atReference.getDateAt()) + " - " + s.getDescSiege() };
						aFormat.ajouteLigne(ligne);
						getHashATReference().put(atReference.getIdAt(), atReference);
					}
				}
				setLB_AT_REFERENCE(aFormat.getListeFormatee(true));
				setListeATReference(listATReference);
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAccidentTravailDao() == null) {
			setAccidentTravailDao(new AccidentTravailDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSiegeLesionDao() == null) {
			setSiegeLesionDao(new SiegeLesionDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeATDao() == null) {
			setTypeATDao(new TypeATDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMaladieProDao() == null) {
			setMaladieProDao(new MaladieProDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeMPDao() == null) {
			setTypeMPDao(new TypeMaladieProDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation de la liste des accidents du travail et maladie pro de l'agent courant
	 * Date de création : 12/11/2015
	 */
	private void initialiseListeAT_MP(HttpServletRequest request) throws Exception {
		// Recherche des accidents du travail de l'agent
		ArrayList<AccidentTravail> listeAT = getAccidentTravailDao().listerAccidentTravailAgent(
				getAgentCourant().getIdAgent());
		setListeAT(listeAT);

		// Recherche des maladies pro de l'agent
		ArrayList<MaladiePro> listeMP = getMaladieProDao().listerMaladieProAgent(
				getAgentCourant().getIdAgent());
		setListeMP(listeMP);
		
		ArrayList<VoAccidentTravailMaladiePro> listeAT_MP = new ArrayList<VoAccidentTravailMaladiePro>();
		if (getListeAT() != null) {
			for (int i = 0; i < getListeAT().size(); i++) {
				AccidentTravail at = (AccidentTravail) getListeAT().get(i);
				VoAccidentTravailMaladiePro vo = new VoAccidentTravailMaladiePro(at);
				listeAT_MP.add(vo);
			}
		}
		
		if (getListeAT() != null) {
			for (int i = 0; i < getListeMP().size(); i++) {
				MaladiePro mp = (MaladiePro) getListeMP().get(i);
				VoAccidentTravailMaladiePro vo = new VoAccidentTravailMaladiePro(mp);
				listeAT_MP.add(vo);
			}
		}
		
		Collections.sort(listeAT_MP);
		setListeAT_MP(listeAT_MP);
		
		int indiceAcc = 0;
		if (getListeAT_MP() != null) {
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				VoAccidentTravailMaladiePro vo = (VoAccidentTravailMaladiePro) getListeAT_MP().get(i);
				
				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgentTYPE(getLienDocumentAgentDao(),
						getAgentCourant().getIdAgent(), "HSCT", vo.getType(), vo.getId());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}
				
				if(null != vo.getIdAT()) {
					TypeAT t = (TypeAT) getHashTypeAT().get(vo.getIdTypeAt());
					SiegeLesion s = (SiegeLesion) getHashSiegeLesion().get(vo.getIdSiege());
					addZone(getNOM_ST_TYPE(indiceAcc),
							t.getDescTypeAt().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getDescTypeAt());
					addZone(getNOM_ST_SIEGE(indiceAcc),
							s.getDescSiege().equals(Const.CHAINE_VIDE) ? "&nbsp;" : s.getDescSiege());
				}else{
					TypeMaladiePro t = (TypeMaladiePro) getHashTypeMP().get(vo.getIdTypeMp());
					addZone(getNOM_ST_TYPE(indiceAcc),
							t.getLibMaladiePro().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getLibMaladiePro());
					addZone(getNOM_ST_SIEGE(indiceAcc), "&nbsp;");
				}

				addZone(getNOM_ST_AT_MP(indiceAcc), vo.getType());
				addZone(getNOM_ST_DATE(indiceAcc), sdf.format(vo.getDateDeclaration()));
				addZone(getNOM_ST_RECHUTE(indiceAcc), vo.getRechute() ? "X" : "&nbsp;");
				addZone(getNOM_ST_NB_JOURS(indiceAcc), vo.getNbJoursItt() == null ? "&nbsp;" : vo.getNbJoursItt()
						.toString());
				addZone(getNOM_ST_NB_DOC(indiceAcc), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceAcc++;
			}
		}
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de la visite courante
		setVoAccidentTravailMaladieProCourant(new VoAccidentTravailMaladiePro());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'un
	 * accident de travail
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_CK_RECHUTE(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_TYPE_AT_MP(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TAUX_CAFAT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_COMMISSION_APTITUDE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DECISION_CAFAT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_TRANSMISSION_CAFAT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_AT_REFERENCE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NB_JOUR_ITT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_SELECT(), "0");
		addZone(getNOM_LB_SIEGE_LESION_SELECT(), "0");
		addZone(getNOM_LB_AVIS_COMMISSION(), "0");
		addZone(getNOM_LB_AT_REFERENCE_SELECT(), "0");
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'un accident
	 * de travail Date de création : 11/07/01
	 */
	private boolean initialiseATCourant(HttpServletRequest request) throws Exception {

		if(getVoAccidentTravailMaladieProCourant().isTypeAT()) {
			addZone(getNOM_RG_TYPE_AT_MP(), getNOM_RB_AT());
			
			TypeAT type = (TypeAT) getHashTypeAT().get(getVoAccidentTravailMaladieProCourant().getIdTypeAt());
			SiegeLesion siege = (SiegeLesion) getHashSiegeLesion().get(getVoAccidentTravailMaladieProCourant().getIdSiege());

			int ligneType = getListeTypeAT().indexOf(type);
			addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(ligneType + 1));
			addZone(getNOM_ST_TYPE(), type.getDescTypeAt());

			int ligneSiege = getListeSiegeLesion().indexOf(siege);
			addZone(getNOM_LB_SIEGE_LESION_SELECT(), String.valueOf(ligneSiege + 1));
			addZone(getNOM_ST_SIEGE_LESION(), siege.getDescSiege());
			
			if(null != getVoAccidentTravailMaladieProCourant().getIdAtReference()) {
				AccidentTravail atReference = (AccidentTravail) getHashATReference().get(getVoAccidentTravailMaladieProCourant().getIdAtReference());
				int ligneAtReference = getListeATReference().indexOf(atReference);
				addZone(getNOM_LB_AT_REFERENCE_SELECT(), String.valueOf(ligneAtReference + 1));
				SiegeLesion s = getHashSiegeLesion().get(atReference.getIdSiege());
				addZone(getNOM_LB_AT_REFERENCE(), sdf.format(atReference.getDateAt()) + " - " + s.getDescSiege());
			}
		}
		
		if(getVoAccidentTravailMaladieProCourant().isTypeMP()) {
			addZone(getNOM_RG_TYPE_AT_MP(), getNOM_RB_MP());
			
			TypeMaladiePro type = (TypeMaladiePro) getHashTypeMP().get(getVoAccidentTravailMaladieProCourant().getIdTypeMp());
			
			int ligneType = getListeTypeMP().indexOf(type);
			addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(ligneType + 1));
			addZone(getNOM_ST_TYPE(), type.getLibMaladiePro());

			addZone(getNOM_EF_DATE_COMMISSION_APTITUDE(), null == getVoAccidentTravailMaladieProCourant().getDateTransmissionAptitude() ? "" : sdf.format(getVoAccidentTravailMaladieProCourant().getDateTransmissionAptitude()));
			addZone(getNOM_EF_DATE_TRANSMISSION_CAFAT(), null == getVoAccidentTravailMaladieProCourant().getDateTransmissionCafat() ? "" : sdf.format(getVoAccidentTravailMaladieProCourant().getDateTransmissionCafat()));
			addZone(getNOM_EF_DATE_DECISION_CAFAT(), null == getVoAccidentTravailMaladieProCourant().getDateDecisionCafat() ? "" : sdf.format(getVoAccidentTravailMaladieProCourant().getDateDecisionCafat()));
			addZone(getNOM_EF_TAUX_CAFAT(), null != getVoAccidentTravailMaladieProCourant().getTauxPrisEnChargeCafat()
					? getVoAccidentTravailMaladieProCourant().getTauxPrisEnChargeCafat().toString()
						: "");
		}

		// Alim zones
		addZone(getNOM_EF_DATE(), sdf.format(getVoAccidentTravailMaladieProCourant().getDateDeclaration()));
		addZone(getNOM_EF_DATE_FIN(), getVoAccidentTravailMaladieProCourant().getDateFin() == null ? Const.CHAINE_VIDE
				: sdf.format(getVoAccidentTravailMaladieProCourant().getDateFin()));
		addZone(getNOM_EF_NB_JOUR_ITT(), getVoAccidentTravailMaladieProCourant().getNbJoursItt() == null ? Const.CHAINE_VIDE
				: getVoAccidentTravailMaladieProCourant().getNbJoursItt().toString());
		addZone(getNOM_LB_AVIS_COMMISSION(), (null != getVoAccidentTravailMaladieProCourant().getAvisCommission() 
				&& getVoAccidentTravailMaladieProCourant().getAvisCommission().equals(1))
				? "Accepté" : 
					(null != getVoAccidentTravailMaladieProCourant().getAvisCommission() 
						&& getVoAccidentTravailMaladieProCourant().getAvisCommission().equals(0) 
							? "Refus" : ""));
		
		Integer avisCommission = getVoAccidentTravailMaladieProCourant().getAvisCommission();
		String avisCommissionLB = null;
		if(null == avisCommission) {
			avisCommissionLB = "-1";
		}else if(1 == avisCommission) {
			avisCommissionLB = "1";
		}else{
			avisCommissionLB = "2";
		}
		
		addZone(getNOM_LB_AVIS_COMMISSION_SELECT(), avisCommissionLB);
		
		addZone(getNOM_CK_RECHUTE(), getVoAccidentTravailMaladieProCourant().getRechute() ?  getCHECKED_ON() : getCHECKED_OFF());

		return true;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/*
	 * RG_AG_AT_A02
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {

			// Suppression
			if(null != getVoAccidentTravailMaladieProCourant().getIdAT()) {
				getAccidentTravailDao().supprimerAccidentTravail(getVoAccidentTravailMaladieProCourant().getIdAT());
				if (getTransaction().isErreur())
					return false;
			}else{
				getMaladieProDao().supprimerMaladiePro(getVoAccidentTravailMaladieProCourant().getIdMaladiePro());
				if (getTransaction().isErreur())
					return false;
			}
			
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {
			
			String type = null;
			if(getZone(getNOM_RG_TYPE_AT_MP()).equals(getNOM_RB_AT())) {
				type = "AT";
			}
			if(getZone(getNOM_RG_TYPE_AT_MP()).equals(getNOM_RB_MP())) {
				type = "MP";
			}
			getVoAccidentTravailMaladieProCourant().setType(type);
			
			String rechute = getZone(getNOM_CK_RECHUTE());
			getVoAccidentTravailMaladieProCourant().setRechute(rechute.equals(getCHECKED_ON()));

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			// récupération des informations remplies dans les zones de saisie
			String date = Services.formateDate(getZone(getNOM_EF_DATE()));
			String dateFin = getZone(getNOM_EF_DATE_FIN()).equals(Const.CHAINE_VIDE) ? null : Services
					.formateDate(getZone(getNOM_EF_DATE_FIN()));
			String duree = getZone(getNOM_EF_NB_JOUR_ITT());

			// RG_AG_AT_A02
			PositionAdmAgent dernPosAdmn = PositionAdmAgent.chercherPositionAdmAgentDateComprise(getTransaction(),
					getAgentCourant().getNomatr(),
					Services.convertitDate(Services.formateDate(getZone(getNOM_EF_DATE())), "dd/MM/yyyy", "yyyyMMdd"));
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR132"));
				return false;
			} else {
				if (!dernPosAdmn.permetAT()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR090", "accidents du travail"));
					return false;
				}
			}

			// Vérification si la PA de l'agent donne le droit a accidents du
			// travail.
			// RG_AG_AT_A02
			ArrayList<PositionAdmAgent> listePA = PositionAdmAgent.listerPositionAdmAgentAvecAgent(getTransaction(),
					getAgentCourant());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			for (PositionAdmAgent pa : listePA) {
				if (Services.compareDates(pa.getDatdeb(), date) <= 0
						&& (pa.getDatfin() == null || Services.compareDates(pa.getDatfin(), date) >= 0)) {
					if (!pa.permetAT()) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR090", "accidents du travail"));
						return false;
					}
					break;
				}
			}

			if(getVoAccidentTravailMaladieProCourant().isTypeAT()) {
				int numLigneType = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer
						.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);
	
				if (numLigneType == -1 || getListeTypeAT().size() == 0 || numLigneType > getListeTypeAT().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types"));
					return false;
				}
				TypeAT typeAt = (TypeAT) getListeTypeAT().get(numLigneType - 1);
	
				int numLigneSiege = (Services.estNumerique(getZone(getNOM_LB_SIEGE_LESION_SELECT())) ? Integer
						.parseInt(getZone(getNOM_LB_SIEGE_LESION_SELECT())) : -1);
	
				if (numLigneSiege == -1 || getListeSiegeLesion().size() == 0
						|| numLigneSiege > getListeSiegeLesion().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "sièges de lésion"));
					return false;
				}
	
				SiegeLesion siege = (SiegeLesion) getListeSiegeLesion().get(numLigneSiege - 1);

				getVoAccidentTravailMaladieProCourant().setIdTypeAt(typeAt.getIdTypeAt());
				getVoAccidentTravailMaladieProCourant().setIdSiege(siege.getIdSiege());
				
				if(getVoAccidentTravailMaladieProCourant().getRechute()) {
					
					int numLigneATReference = (Services.estNumerique(getZone(getNOM_LB_AT_REFERENCE_SELECT())) ? Integer
							.parseInt(getZone(getNOM_LB_AT_REFERENCE_SELECT())) : -1);
		
					if (numLigneATReference == -1 || getListeATReference().size() == 0 || numLigneATReference > getListeATReference().size()) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "AT de référence"));
						return false;
					}
					AccidentTravail typeMP = (AccidentTravail) getListeATReference().get(numLigneATReference - 1);
					
					getVoAccidentTravailMaladieProCourant().setIdAtReference(typeMP.getIdAt());
				}
			}
			
			if(getVoAccidentTravailMaladieProCourant().isTypeMP()) {
				int numLigneType = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer
						.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);
	
				if (numLigneType == -1 || getListeTypeMP().size() == 0 || numLigneType > getListeTypeMP().size()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types"));
					return false;
				}
				TypeMaladiePro typeMP = (TypeMaladiePro) getListeTypeMP().get(numLigneType - 1);
				
				getVoAccidentTravailMaladieProCourant().setIdTypeMp(typeMP.getIdMaladiePro());
				

				String dateTransmissionCafat = getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()).equals(Const.CHAINE_VIDE) ? null : Services
						.formateDate(getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()));

				String dateDecisionCafat = getZone(getNOM_EF_DATE_DECISION_CAFAT()).equals(Const.CHAINE_VIDE) ? null : Services
						.formateDate(getZone(getNOM_EF_DATE_DECISION_CAFAT()));

				String dateTransmissionAptitude = getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()).equals(Const.CHAINE_VIDE) ? null : Services
						.formateDate(getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()));

				String tauxPrisEnChargeCafat = getZone(getNOM_EF_TAUX_CAFAT());
				
				if(getVoAccidentTravailMaladieProCourant().getRechute()) {
					dateFin = null;
				}
				
				getVoAccidentTravailMaladieProCourant().setDateTransmissionCafat(null != dateTransmissionCafat && !"".equals(dateTransmissionCafat) ? sdf.parse(dateTransmissionCafat) : null);
				getVoAccidentTravailMaladieProCourant().setDateDecisionCafat(null != dateDecisionCafat && !"".equals(dateDecisionCafat) ? sdf.parse(dateDecisionCafat) : null);
				getVoAccidentTravailMaladieProCourant().setDateTransmissionAptitude(null != dateTransmissionAptitude && !"".equals(dateTransmissionAptitude) ? sdf.parse(dateTransmissionAptitude) : null );
				getVoAccidentTravailMaladieProCourant().setTauxPrisEnChargeCafat(null != tauxPrisEnChargeCafat && !"".equals(tauxPrisEnChargeCafat) ? new Integer(tauxPrisEnChargeCafat) : null);
			}

			// Création de l'objet VisiteMedicale a créer/modifier
			Agent agentCourant = getAgentCourant();
			getVoAccidentTravailMaladieProCourant().setIdAgent(agentCourant.getIdAgent());
			getVoAccidentTravailMaladieProCourant().setDateDeclaration(sdf.parse(date));
			getVoAccidentTravailMaladieProCourant().setDateFin(dateFin == null ? null : sdf.parse(dateFin));
			getVoAccidentTravailMaladieProCourant().setNbJoursItt(duree.equals(Const.CHAINE_VIDE) ? null : Integer.valueOf(duree));
			
			// avis commission
			Integer numLigneAvisCommission = (Services.estNumerique(getZone(getNOM_LB_AVIS_COMMISSION_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_AVIS_COMMISSION_SELECT())) : -1);
			Integer avisCommission = numLigneAvisCommission == 2 ? new Integer(0) : (numLigneAvisCommission == 1 ? new Integer(1) : null);
			getVoAccidentTravailMaladieProCourant().setAvisCommission(avisCommission);
			
			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				
				// si changement de type, il faut supprimer d une part pour recreer d une autre part
				if(getVoAccidentTravailMaladieProCourant().isTypeMP()
						&& null != getVoAccidentTravailMaladieProCourant().getIdAT()) {
					// on supprime l AT
					getAccidentTravailDao().supprimerAccidentTravail(getVoAccidentTravailMaladieProCourant().getIdAT());
					// on cree la maladie pro
					getMaladieProDao().creerMaladiePro(
							getVoAccidentTravailMaladieProCourant().getIdAgent(), 
							getVoAccidentTravailMaladieProCourant().getRechute(), 
							getVoAccidentTravailMaladieProCourant().getDateDeclaration(), 
							getVoAccidentTravailMaladieProCourant().getDateFin(), 
							getVoAccidentTravailMaladieProCourant().getNbJoursItt(), 
							getVoAccidentTravailMaladieProCourant().getAvisCommission(), 
							getVoAccidentTravailMaladieProCourant().getIdTypeMp(), 
							getVoAccidentTravailMaladieProCourant().getDateTransmissionCafat(), 
							getVoAccidentTravailMaladieProCourant().getDateDecisionCafat(), 
							getVoAccidentTravailMaladieProCourant().getTauxPrisEnChargeCafat(), 
							getVoAccidentTravailMaladieProCourant().getDateTransmissionAptitude()
						);
				}
				
				if(getVoAccidentTravailMaladieProCourant().isTypeAT()
						&& null != getVoAccidentTravailMaladieProCourant().getIdMaladiePro()) {
					// on supprime la maladie pro
					getMaladieProDao().supprimerMaladiePro(getVoAccidentTravailMaladieProCourant().getIdMaladiePro());
					// on cree l AT
					getAccidentTravailDao().creerAccidentTravail(
							getVoAccidentTravailMaladieProCourant().getIdTypeAt(), 
							getVoAccidentTravailMaladieProCourant().getIdSiege(), 
							getVoAccidentTravailMaladieProCourant().getIdAgent(), 
							getVoAccidentTravailMaladieProCourant().getDateDeclaration(), 
							null, 
							getVoAccidentTravailMaladieProCourant().getNbJoursItt(), 
							getVoAccidentTravailMaladieProCourant().getDateFin(), 
							getVoAccidentTravailMaladieProCourant().getAvisCommission(), 
							getVoAccidentTravailMaladieProCourant().getIdAtReference(),
							getVoAccidentTravailMaladieProCourant().getRechute());
				}
				
				// Modification
				if(getVoAccidentTravailMaladieProCourant().isTypeMP()
						&& null != getVoAccidentTravailMaladieProCourant().getIdMaladiePro()) {
					getMaladieProDao().modifierMaladiePro(
							getVoAccidentTravailMaladieProCourant().getIdMaladiePro(),
							getVoAccidentTravailMaladieProCourant().getIdAgent(), 
							getVoAccidentTravailMaladieProCourant().getRechute(), 
							getVoAccidentTravailMaladieProCourant().getDateDeclaration(), 
							getVoAccidentTravailMaladieProCourant().getDateFin(), 
							getVoAccidentTravailMaladieProCourant().getNbJoursItt(), 
							getVoAccidentTravailMaladieProCourant().getAvisCommission(), 
							getVoAccidentTravailMaladieProCourant().getIdTypeMp(), 
							getVoAccidentTravailMaladieProCourant().getDateTransmissionCafat(), 
							getVoAccidentTravailMaladieProCourant().getDateDecisionCafat(), 
							getVoAccidentTravailMaladieProCourant().getTauxPrisEnChargeCafat(), 
							getVoAccidentTravailMaladieProCourant().getDateTransmissionAptitude()
						);
				}
				if(getVoAccidentTravailMaladieProCourant().isTypeAT()
						&& null != getVoAccidentTravailMaladieProCourant().getIdAT()) {
					getAccidentTravailDao().modifierAccidentTravail(
							getVoAccidentTravailMaladieProCourant().getIdAT(), 
							getVoAccidentTravailMaladieProCourant().getIdTypeAt(), 
							getVoAccidentTravailMaladieProCourant().getIdSiege(), 
							getVoAccidentTravailMaladieProCourant().getIdAgent(), 
							getVoAccidentTravailMaladieProCourant().getDateDeclaration(), 
							null, 
							getVoAccidentTravailMaladieProCourant().getNbJoursItt(), 
							getVoAccidentTravailMaladieProCourant().getDateFin(), 
							getVoAccidentTravailMaladieProCourant().getAvisCommission(), 
							getVoAccidentTravailMaladieProCourant().getIdAtReference(),
							getVoAccidentTravailMaladieProCourant().getRechute());
				}
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				if(getVoAccidentTravailMaladieProCourant().isTypeMP()) {
					getMaladieProDao().creerMaladiePro(
							getVoAccidentTravailMaladieProCourant().getIdAgent(), 
							getVoAccidentTravailMaladieProCourant().getRechute(), 
							getVoAccidentTravailMaladieProCourant().getDateDeclaration(), 
							getVoAccidentTravailMaladieProCourant().getDateFin(), 
							getVoAccidentTravailMaladieProCourant().getNbJoursItt(), 
							getVoAccidentTravailMaladieProCourant().getAvisCommission(), 
							getVoAccidentTravailMaladieProCourant().getIdTypeMp(), 
							getVoAccidentTravailMaladieProCourant().getDateTransmissionCafat(), 
							getVoAccidentTravailMaladieProCourant().getDateDecisionCafat(), 
							getVoAccidentTravailMaladieProCourant().getTauxPrisEnChargeCafat(), 
							getVoAccidentTravailMaladieProCourant().getDateTransmissionAptitude()
						);
				}
				if(getVoAccidentTravailMaladieProCourant().isTypeAT()) {
					getAccidentTravailDao().creerAccidentTravail(
							getVoAccidentTravailMaladieProCourant().getIdTypeAt(), 
							getVoAccidentTravailMaladieProCourant().getIdSiege(), 
							getVoAccidentTravailMaladieProCourant().getIdAgent(), 
							getVoAccidentTravailMaladieProCourant().getDateDeclaration(), 
							null, 
							getVoAccidentTravailMaladieProCourant().getNbJoursItt(), 
							getVoAccidentTravailMaladieProCourant().getDateFin(), 
							getVoAccidentTravailMaladieProCourant().getAvisCommission(), 
							getVoAccidentTravailMaladieProCourant().getIdAtReference(),
							getVoAccidentTravailMaladieProCourant().getRechute());
				}
			}
			if (getTransaction().isErreur())
				return false;
		}

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien passé
		commitTransaction();
		initialiseListeAT_MP(request);

		return true;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 *             RG_AG_AT_A01
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		// date de l'accident du travail
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de déclaration"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_EF_DATE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de déclaration"));
			return false;
		} else if (!Const.CHAINE_VIDE.equals(getVAL_EF_NB_JOUR_ITT())) {

			// verification des regles de gestions
			// RG_AG_AT_A01
			ArrayList<AccidentTravail> listeAT = getListeAT();
			for (int i = 0; i < listeAT.size(); i++) {
				AccidentTravail at = (AccidentTravail) listeAT.get(i);
				
				if(null != getVoAccidentTravailMaladieProCourant().getIdAT()
						&& getVoAccidentTravailMaladieProCourant().getIdAT().equals(at.getIdAt())) {
					continue;
				}

				if (Services.compareDates(getZone(getNOM_EF_DATE()), sdf.format(at.getDateAt())) == -1) {
					int resultat = Services.compareDates(
							Services.ajouteJours(Services.formateDate(getZone(getNOM_EF_DATE())),
									Integer.parseInt(getZone(getNOM_EF_NB_JOUR_ITT()))), sdf.format(at.getDateAt()));
					if (resultat == 1) {
						// erreur
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR050"));
						return false;
					}
				} else if (Services.compareDates(getZone(getNOM_EF_DATE()), sdf.format(at.getDateAt())) == 1) {
					int resultat = Services.compareDates(Services.ajouteJours(
							Services.formateDate(sdf.format(at.getDateAt())),
							at.getNbJoursItt() == null ? 0 : at.getNbJoursItt()), getZone(getNOM_EF_DATE()));
					if (resultat == 1) {
						// erreur
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR050"));
						return false;
					}
				}
			}
		}
		
		// AT ou MP
		if(getVoAccidentTravailMaladieProCourant().getType().equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR093"));
			return false;
		}

		// date de fin
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN()))
				&& !Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de fin"));
			return false;
		}

		// duree ITT
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_NB_JOUR_ITT()))
				&& !Services.estNumerique(getZone(getNOM_EF_NB_JOUR_ITT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "durée ITT"));
			return false;
		}

		// type AT/MP obligatoire
		int indiceType = (Services.estNumerique(getVAL_LB_TYPE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_SELECT())
				: -1);
		if (indiceType < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type"));
			return false;
		}

		// siege des lésions obligatoire
		if(getVoAccidentTravailMaladieProCourant().isTypeAT()) {
			int indiceRecommandation = (Services.estNumerique(getVAL_LB_SIEGE_LESION_SELECT()) ? Integer
					.parseInt(getVAL_LB_SIEGE_LESION_SELECT()) : -1);
			if (indiceRecommandation < 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "siège des lésions"));
				return false;
			}
		}

		// AT de référence
		if(getVoAccidentTravailMaladieProCourant().getRechute()
				&& getVoAccidentTravailMaladieProCourant().isTypeAT()) {
			int indiceATReference = (Services.estNumerique(getVAL_LB_AT_REFERENCE_SELECT()) ? Integer
					.parseInt(getVAL_LB_AT_REFERENCE_SELECT()) : -1);
			if (indiceATReference < 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "AT de référence"));
				return false;
			}
		}
		
		// taux Cafat
		if(getVoAccidentTravailMaladieProCourant().isTypeMP()) {
			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_TAUX_CAFAT()))
					&& !Services.estNumerique(getZone(getNOM_EF_TAUX_CAFAT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "taux de pris en charge Cafat"));
				return false;
			}

			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()))
					&& !Services.estUneDate(getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de transmission Cafat"));
				return false;
			}

			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DECISION_CAFAT()))
					&& !Services.estUneDate(getZone(getNOM_EF_DATE_DECISION_CAFAT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de décision Cafat"));
				return false;
			}

			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()))
					&& !Services.estUneDate(getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de transmission à la commission d'aptitude"));
				return false;
			}
		}

		return true;
	}

	public String getNomEcran() {
		return "ECR-AG-HSCT-ACCTRAVAIL";
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		VoAccidentTravailMaladiePro voCourant = (VoAccidentTravailMaladiePro) getListeAT_MP().get(indiceEltAModifier);
		setVoAccidentTravailMaladieProCourant(voCourant);

		// init du diplome courant
		if (!initialiseATCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		VoAccidentTravailMaladiePro voCourant = (VoAccidentTravailMaladiePro) getListeAT_MP().get(indiceEltAConsulter);
		setVoAccidentTravailMaladieProCourant(voCourant);

		// init du diplome courant
		if (!initialiseATCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		VoAccidentTravailMaladiePro voCourant = (VoAccidentTravailMaladiePro) getListeAT_MP().get(indiceEltDocument);
		setVoAccidentTravailMaladieProCourant(voCourant);

		// init des documents AT de l'agent
		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void setListeDocuments(ArrayList<Document> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<Document>();
		}
		return listeDocuments;
	}

	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {
		// Recherche des documents de l'agent
		ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgentTYPE(getLienDocumentAgentDao(),
				getAgentCourant().getIdAgent(), "HSCT", getVoAccidentTravailMaladieProCourant().getType(), getVoAccidentTravailMaladieProCourant().getId());
		setListeDocuments(listeDocAgent);

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) getTypeDocumentDao().chercherTypeDocument(doc.getIdTypeDocument());

				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(indiceActeVM),
						doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_TYPE_DOC(indiceActeVM), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM),
						doc.getDateDocument() == null ? "&nbsp;" : sdf.format(doc.getDateDocument()));
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());

				indiceActeVM++;
			}
		}
	}

	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_CONSULTER_DOC(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");

		// Récup du document courant
		Document d = (Document) getListeDocuments().get(indiceEltAConsulter);
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + d.getLienDocument()));

		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

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

	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		Document d = (Document) getListeDocuments().get(indiceEltASuprimer);
		setDocumentCourant(d);

		// init des documents courant
		if (!initialiseDocumentSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request) throws Exception {
		// Récup du Diplome courant
		Document d = getDocumentCourant();

		DocumentAgent lda = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(),
				getDocumentCourant().getIdDocument());
		setLienDocumentAgentCourant(lda);

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_NOM_ORI_DOC(), d.getNomOriginal());
		addZone(getNOM_ST_DATE_DOC(), d.getDateDocument() == null ? Const.CHAINE_VIDE : sdf.format(d.getDateDocument()));
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	private boolean performPB_VALIDER_DOCUMENT_SUPPRESSION(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}
		// suppression dans table DOCUMENT_AGENT
		getLienDocumentAgentDao().supprimerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(),
				getLienDocumentAgentCourant().getIdDocument());
		// Suppression dans la table DOCUMENT_ASSOCIE
		getDocumentDao().supprimerDocument(getDocumentCourant().getIdDocument());

		// on supprime le fichier physiquement sur le serveur
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		String cheminDoc = getDocumentCourant().getLienDocument();
		File fichierASupp = new File(repertoireStockage + cheminDoc);
		try {
			fichierASupp.delete();
		} catch (Exception e) {
			logger.error("Erreur suppression physique du fichier : " + e.toString());
		}

		// tout s'est bien passé
		commitTransaction();
		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		initialiseListeDocuments(request);
		return true;
	}

	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Controle des champs
		if (!performControlerSaisieDocument(request))
			return false;

		VoAccidentTravailMaladiePro vo = getVoAccidentTravailMaladieProCourant();

		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {
			String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();

			// on controle si il y a deja un fichier pour cet AT
			if (!performControlerFichier(request, vo.getType() + "_" + vo.getId() + "_" + dateJour)) {
				// alors on affiche un message pour prevenir que l'on va ecraser
				// le fichier precedent
				addZone(getNOM_ST_WARNING(),
						"Attention un fichier du même type existe déjà pour cette accident du travail. Etes-vous sûr de vouloir écraser la version précédente ?");
				return true;
			}

			if (!creeDocument(request, vo)) {
				return false;
			}

		} else {
			// on supprime le document existant dans la base de données
			Document d = getDocumentDao().chercherDocumentByContainsNom(vo.getType() + "_" + vo.getId());
			DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(),
					d.getIdDocument());

			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
			getDocumentDao().supprimerDocument(d.getIdDocument());

			if (!creeDocument(request, vo)) {
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met a jour le tableau des AT pour avoir le nombre de documents
		initialiseListeAT_MP(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, VoAccidentTravailMaladiePro vo) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupere le type de document
		String codTypeDoc = vo.getType();
		TypeDocument td = getTypeDocumentDao().chercherTypeDocumentByCod(codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + vo.getId() + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf") || extension.equals(".tiff")) {
			upload = uploadFichierPDF(fichierUpload, nom, codTypeDoc);
		} else {
			upload = uploadFichier(fichierUpload, nom, codTypeDoc);
		}

		if (!upload)
			return false;

		// on crée le document en base de données
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomOriginal(fichierUpload.getName());
		getDocumentCourant().setNomDocument(nom);
		getDocumentCourant().setDateDocument(new Date());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		Integer id = getDocumentDao().creerDocument(getDocumentCourant().getClasseDocument(),
				getDocumentCourant().getNomDocument(), getDocumentCourant().getLienDocument(),
				getDocumentCourant().getDateDocument(), getDocumentCourant().getCommentaire(),
				getDocumentCourant().getIdTypeDocument(), getDocumentCourant().getNomOriginal());

		setLienDocumentAgentCourant(new DocumentAgent());
		getLienDocumentAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocumentAgentCourant().setIdDocument(id);
		getLienDocumentAgentDao().creerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(),
				getLienDocumentAgentCourant().getIdDocument());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private boolean uploadFichierPDF(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		File newFile = new File(repPartage + codTypeDoc + "/" + nomFichier);

		FileInputStream in = new FileInputStream(f);

		try {
			FileOutputStream out = new FileOutputStream(newFile);
			try {
				byte[] byteBuffer = new byte[in.available()];
				@SuppressWarnings("unused")
				int s = in.read(byteBuffer);
				out.write(byteBuffer);
				out.flush();
				resultat = true;
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

		return resultat;
	}

	private boolean uploadFichier(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.toFileObject(f);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(repPartage + codTypeDoc + "/" + nomFichier);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		try {
			while ((ligne = in.readLine()) != null) {
				out.write(ligne);
			}
			resultat = true;
		} catch (Exception e) {
			logger.error("erreur d'execution " + e.toString());
		}

		// FERMETURE DES FLUX
		in.close();
		inR.close();
		is.close();
		fo.close();

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();

		return resultat;
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

	private boolean performControlerSaisieDocument(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), multi.getParameter(getNOM_EF_COMMENTAIRE()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	private boolean performControlerFichier(HttpServletRequest request, String nomFichier) {
		boolean result = true;
		// on regarde dans la liste des document si il y a une entrée avec ce
		// nom de contrat
		for (Iterator<Document> iter = getListeDocuments().iterator(); iter.hasNext();) {
			Document doc = (Document) iter.next();
			// on supprime l'extension
			String nomDocSansExtension = doc.getNomDocument().substring(0, doc.getNomDocument().indexOf("."));
			if (nomFichier.equals(nomDocSansExtension)) {
				result = false;
			}
		}
		return result;
	}

	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null || (multi != null && multi
				.getParameter(param) != null));
	}

	public boolean recupererPreControles(HttpServletRequest request) throws Exception {
		String type = request.getHeader("Content-Type");
		String repTemp = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");
		@SuppressWarnings("unused")
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			request.setCharacterEncoding("UTF-8");
			multi = new MultipartRequest(request, repTemp, 10 * 1024 * 1024, "UTF-8");
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}
	
	public boolean performPB_SELECT_TYPE_AT(HttpServletRequest request) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}
		
		getVoAccidentTravailMaladieProCourant().setType("AT");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
	
	public boolean performPB_SELECT_TYPE_MP(HttpServletRequest request) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}
		
		getVoAccidentTravailMaladieProCourant().setType("MP");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
	
	public boolean performPB_SELECT_RECHUTE(HttpServletRequest request) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}
		
		String rechute = getZone(getNOM_CK_RECHUTE()); 
		getVoAccidentTravailMaladieProCourant().setRechute(rechute.equals(getCHECKED_ON()));

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		String JSP = null;
		if (null == request.getParameter("JSP")) {
			if (multi != null) {
				JSP = multi.getParameter("JSP");
			}
		} else {
			JSP = request.getParameter("JSP");
		}

		// Si on arrive de la JSP alors on traite le get
		if (JSP != null && JSP.equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				if (testerParametre(request, getNOM_PB_DOCUMENT(i))) {
					return performPB_DOCUMENT(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_CREER_DOC())) {
				return performPB_CREER_DOC(request);
			}

			// Si clic sur le bouton PB_CONSULTER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_DOC(i))) {
					return performPB_CONSULTER_DOC(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(i))) {
					return performPB_SUPPRIMER_DOC(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_SUPPRESSION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION())) {
				return performPB_VALIDER_DOCUMENT_SUPPRESSION(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_CREATION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION())) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}

			// Si clic sur le bouton NOM_PB_SELECT_AT
			if (testerParametre(request, getNOM_PB_SELECT_AT())) {
				return performPB_SELECT_TYPE_AT(request);
			}

			// Si clic sur le bouton NOM_PB_SELECT_MP
			if (testerParametre(request, getNOM_PB_SELECT_MP())) {
				return performPB_SELECT_TYPE_MP(request);
			}

			// Si clic sur le bouton NOM_PB_SELECT_RECHUTE
			if (testerParametre(request, getNOM_PB_SELECT_RECHUTE())) {
				return performPB_SELECT_RECHUTE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getJSP() {
		return "OeAGENTAccidentTravail.jsp";
	}

	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE" + i;
	}

	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	public String getNOM_ST_AT_MP(int i) {
		return "NOM_ST_AT_MP" + i;
	}

	public String getVAL_ST_AT_MP(int i) {
		return getZone(getNOM_ST_AT_MP(i));
	}

	public String getNOM_ST_RECHUTE(int i) {
		return "NOM_ST_RECHUTE" + i;
	}

	public String getVAL_ST_RECHUTE(int i) {
		return getZone(getNOM_ST_RECHUTE(i));
	}

	public String getNOM_ST_NB_JOURS(int i) {
		return "NOM_ST_NB_JOURS" + i;
	}

	public String getVAL_ST_NB_JOURS(int i) {
		return getZone(getNOM_ST_NB_JOURS(i));
	}

	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE" + i;
	}

	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	public String getNOM_ST_SIEGE(int i) {
		return "NOM_ST_SIEGE" + i;
	}

	public String getVAL_ST_SIEGE(int i) {
		return getZone(getNOM_ST_SIEGE(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
	}

	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	public String getNOM_ST_TYPE_DOC(int i) {
		return "NOM_ST_TYPE_DOC" + i;
	}

	public String getVAL_ST_TYPE_DOC(int i) {
		return getZone(getNOM_ST_TYPE_DOC(i));
	}

	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	public String getNOM_ST_NB_DOC(int i) {
		return "NOM_ST_NB_DOC" + i;
	}

	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
	}

	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	private Document getDocumentCourant() {
		return documentCourant;
	}

	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	private DocumentAgent getLienDocumentAgentCourant() {
		return lienDocumentAgentCourant;
	}

	private void setLienDocumentAgentCourant(DocumentAgent lienDocumentAgentCourant) {
		this.lienDocumentAgentCourant = lienDocumentAgentCourant;
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getVAL_ST_DATE_DOC() {
		return getZone(getNOM_ST_DATE_DOC());
	}

	public String getNOM_ST_DATE_DOC() {
		return "NOM_ST_DATE_DOC";
	}

	public String getVAL_ST_COMMENTAIRE_DOC() {
		return getZone(getNOM_ST_COMMENTAIRE_DOC());
	}

	public String getNOM_ST_COMMENTAIRE_DOC() {
		return "NOM_ST_COMMENTAIRE_DOC";
	}

	public String getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION() {
		return "NOM_PB_VALIDER_DOCUMENT_SUPPRESSION";
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	public String getVAL_ST_NOM_ORI_DOC() {
		return getZone(getNOM_ST_NOM_ORI_DOC());
	}

	public String getNOM_ST_NOM_ORI_DOC() {
		return "NOM_ST_NOM_ORI_DOC";
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}

	public AccidentTravailDao getAccidentTravailDao() {
		return accidentTravailDao;
	}

	public void setAccidentTravailDao(AccidentTravailDao accidentTravailDao) {
		this.accidentTravailDao = accidentTravailDao;
	}

	public MaladieProDao getMaladieProDao() {
		return maladieProDao;
	}

	public void setMaladieProDao(MaladieProDao maladieProDao) {
		this.maladieProDao = maladieProDao;
	}

	public SiegeLesionDao getSiegeLesionDao() {
		return siegeLesionDao;
	}

	public void setSiegeLesionDao(SiegeLesionDao siegeLesionDao) {
		this.siegeLesionDao = siegeLesionDao;
	}

	public TypeATDao getTypeATDao() {
		return typeATDao;
	}

	public void setTypeATDao(TypeATDao typeATDao) {
		this.typeATDao = typeATDao;
	}

	public TypeMaladieProDao getTypeMPDao() {
		return typeMPDao;
	}

	public void setTypeMPDao(TypeMaladieProDao typeMPDao) {
		this.typeMPDao = typeMPDao;
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

	public ArrayList<MaladiePro> getListeMP() {
		return listeMP;
	}

	public void setListeMP(ArrayList<MaladiePro> listeMP) {
		this.listeMP = listeMP;
	}

	public ArrayList<TypeMaladiePro> getListeTypeMP() {
		return listeTypeMP;
	}

	public void setListeTypeMP(ArrayList<TypeMaladiePro> listeTypeMP) {
		this.listeTypeMP = listeTypeMP;
	}

	public ArrayList<VoAccidentTravailMaladiePro> getListeAT_MP() {
		return listeAT_MP;
	}

	public void setListeAT_MP(ArrayList<VoAccidentTravailMaladiePro> listeAT_MP) {
		this.listeAT_MP = listeAT_MP;
	}

	public String getNOM_ST_DATE() {
		return "NOM_ST_DATE";
	}

	public String getVAL_ST_DATE() {
		return getZone(getNOM_ST_DATE());
	}

	public String getNOM_ST_NB_JOUR_ITT() {
		return "NOM_ST_NB_JOUR_ITT";
	}

	public String getVAL_ST_NB_JOUR_ITT() {
		return getZone(getNOM_ST_NB_JOUR_ITT());
	}

	public String getNOM_ST_SIEGE_LESION() {
		return "NOM_ST_SIEGE_LESION";
	}

	public String getVAL_ST_SIEGE_LESION() {
		return getZone(getNOM_ST_SIEGE_LESION());
	}

	public String getNOM_ST_TYPE() {
		return "NOM_ST_TYPE";
	}

	public String getVAL_ST_TYPE() {
		return getZone(getNOM_ST_TYPE());
	}

	public String getNOM_EF_DATE() {
		return "NOM_EF_DATE";
	}

	public String getVAL_EF_DATE() {
		return getZone(getNOM_EF_DATE());
	}

	public String getNOM_EF_NB_JOUR_ITT() {
		return "NOM_EF_NB_JOUR_ITT";
	}

	public String getVAL_EF_NB_JOUR_ITT() {
		return getZone(getNOM_EF_NB_JOUR_ITT());
	}

	public String getNOM_EF_TAUX_CAFAT() {
		return "NOM_EF_TAUX_CAFAT";
	}

	public String getVAL_EF_TAUX_CAFAT() {
		return getZone(getNOM_EF_TAUX_CAFAT());
	}

	private String[] getLB_SIEGE_LESION() {
		if (LB_SIEGE_LESION == null)
			LB_SIEGE_LESION = initialiseLazyLB();
		return LB_SIEGE_LESION;
	}

	private void setLB_SIEGE_LESION(String[] newLB_SIEGE_LESION) {
		LB_SIEGE_LESION = newLB_SIEGE_LESION;
	}

	public String getNOM_LB_SIEGE_LESION() {
		return "NOM_LB_SIEGE_LESION";
	}

	public String getNOM_LB_SIEGE_LESION_SELECT() {
		return "NOM_LB_SIEGE_LESION_SELECT";
	}

	public String[] getVAL_LB_SIEGE_LESION() {
		return getLB_SIEGE_LESION();
	}

	public String getVAL_LB_SIEGE_LESION_SELECT() {
		return getZone(getNOM_LB_SIEGE_LESION_SELECT());
	}

	public String[] getLB_AVIS_COMMISSION() {
		if (LB_AVIS_COMMISSION == null)
			LB_AVIS_COMMISSION = initialiseLazyLB();
		return LB_AVIS_COMMISSION;
	}

	private void setLB_AVIS_COMMISSION(String[] newLB_AVIS_COMMISSION) {
		LB_AVIS_COMMISSION = newLB_AVIS_COMMISSION;
	}

	public String getNOM_LB_AVIS_COMMISSION() {
		return "NOM_LB_AVIS_COMMISSION";
	}

	public String getNOM_LB_AVIS_COMMISSION_SELECT() {
		return "NOM_LB_AVIS_COMMISSION_SELECT";
	}

	public String getVAL_LB_AVIS_COMMISSION() {
		return getZone(getNOM_LB_AVIS_COMMISSION());
	}

	public String getVAL_LB_AVIS_COMMISSION_SELECT() {
		return getZone(getNOM_LB_AVIS_COMMISSION_SELECT());
	}

	private String[] getLB_TYPE_AT() {
		if (LB_TYPE_AT == null)
			LB_TYPE_AT = initialiseLazyLB();
		return LB_TYPE_AT;
	}

	private void setLB_TYPE_AT(String[] newLB_TYPE_AT) {
		LB_TYPE_AT = newLB_TYPE_AT;
	}

	public String[] getVAL_LB_TYPE_AT() {
		return getLB_TYPE_AT();
	}

	private String[] getLB_TYPE_MP() {
		if (LB_TYPE_MP == null)
			LB_TYPE_MP = initialiseLazyLB();
		return LB_TYPE_MP;
	}

	private void setLB_TYPE_MP(String[] newLB_TYPE_MP) {
		LB_TYPE_MP = newLB_TYPE_MP;
	}

	public String getNOM_LB_TYPE() {
		return "NOM_LB_TYPE";
	}

	public String[] getVAL_LB_TYPE_MP() {
		return getLB_TYPE_MP();
	}

	public String getNOM_LB_TYPE_SELECT() {
		return "NOM_LB_TYPE_SELECT";
	}

	public String getVAL_LB_TYPE_SELECT() {
		return getZone(getNOM_LB_TYPE_SELECT());
	}

	public String[] getLB_AT_REFERENCE() {
		if (LB_AT_REFERENCE == null)
			LB_AT_REFERENCE = initialiseLazyLB();
		return LB_AT_REFERENCE;
	}

	private void setLB_AT_REFERENCE(String[] newLB_AT_REFERENCE) {
		LB_AT_REFERENCE = newLB_AT_REFERENCE;
	}

	public String getNOM_LB_AT_REFERENCE() {
		return "NOM_LB_AT_REFERENCE";
	}

	public String getVAL_LB_AT_REFERENCE() {
		return getZone(getNOM_LB_AT_REFERENCE());
	}

	public String getNOM_LB_AT_REFERENCE_SELECT() {
		return "NOM_LB_AT_REFERENCE_SELECT";
	}

	public String getVAL_LB_AT_REFERENCE_SELECT() {
		return getZone(getNOM_LB_AT_REFERENCE_SELECT());
	}

	public String getNOM_RG_TYPE_AT_MP() {
		return "NOM_RG_TYPE_AT_MP";
	}

	public String getNOM_RB_AT() {
		return "NOM_RB_AT";
	}

	public String getNOM_RB_MP() {
		return "NOM_RB_MP";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}
	
	public String getNOM_PB_SELECT_AT() {
		return "NOM_PB_SELECT_AT";
	}
	
	public String getNOM_PB_SELECT_RECHUTE() {
		return "NOM_PB_SELECT_RECHUTE";
	}
	
	public String getNOM_PB_SELECT_MP() {
		return "NOM_PB_SELECT_MP";
	}
	
	public String getNOM_CK_RECHUTE() {
		return "NOM_CK_RECHUTE";
	}

	public String getVAL_CK_RECHUTE() {
		return getZone(getNOM_CK_RECHUTE());
	}

	public VoAccidentTravailMaladiePro getVoAccidentTravailMaladieProCourant() {
		return voAccidentTravailMaladieProCourant;
	}

	public void setVoAccidentTravailMaladieProCourant(
			VoAccidentTravailMaladiePro voAccidentTravailMaladieProCourant) {
		this.voAccidentTravailMaladieProCourant = voAccidentTravailMaladieProCourant;
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	private Hashtable<Integer, SiegeLesion> getHashSiegeLesion() {
		if (hashSiegeLesion == null) {
			hashSiegeLesion = new Hashtable<Integer, SiegeLesion>();
		}
		return hashSiegeLesion;
	}

	private Hashtable<Integer, TypeAT> getHashTypeAT() {
		if (hashTypeAT == null) {
			hashTypeAT = new Hashtable<Integer, TypeAT>();
		}
		return hashTypeAT;
	}

	private Hashtable<Integer, AccidentTravail> getHashATReference() {
		if (hashATReference == null) {
			hashATReference = new Hashtable<Integer, AccidentTravail>();
		}
		return hashATReference;
	}

	public ArrayList<AccidentTravail> getListeAT() {
		return listeAT;
	}

	private void setListeAT(ArrayList<AccidentTravail> listeAT) {
		this.listeAT = listeAT;
	}

	public ArrayList<AccidentTravail> getListeATReference() {
		return listeATReference;
	}

	private void setListeATReference(ArrayList<AccidentTravail> listeATReference) {
		this.listeATReference = listeATReference;
	}

	public Hashtable<Integer, TypeMaladiePro> getHashTypeMP() {
		if (hashTypeMP == null) {
			hashTypeMP = new Hashtable<Integer, TypeMaladiePro>();
		}
		return hashTypeMP;
	}

	public void setHashTypeMP(Hashtable<Integer, TypeMaladiePro> hashTypeMP) {
		this.hashTypeMP = hashTypeMP;
	}

	private ArrayList<SiegeLesion> getListeSiegeLesion() {
		return listeSiegeLesion;
	}

	private void setListeSiegeLesion(ArrayList<SiegeLesion> listeSiegeLesion) {
		this.listeSiegeLesion = listeSiegeLesion;
	}

	private ArrayList<TypeAT> getListeTypeAT() {
		return listeTypeAT;
	}

	private void setListeTypeAT(ArrayList<TypeAT> listeTypeAT) {
		this.listeTypeAT = listeTypeAT;
	}

	public String getNOM_ST_DATE_INITIALE() {
		return "NOM_ST_DATE_INITIALE";
	}

	public String getVAL_ST_DATE_INITIALE() {
		return getZone(getNOM_ST_DATE_INITIALE());
	}

	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	public String getNOM_EF_DATE_TRANSMISSION_CAFAT() {
		return "NOM_EF_DATE_TRANSMISSION_CAFAT";
	}

	public String getVAL_EF_DATE_TRANSMISSION_CAFAT() {
		return getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT());
	}

	public String getNOM_EF_DATE_DECISION_CAFAT() {
		return "NOM_EF_DATE_DECISION_CAFAT";
	}

	public String getVAL_EF_DATE_DECISION_CAFAT() {
		return getZone(getNOM_EF_DATE_DECISION_CAFAT());
	}

	public String getNOM_EF_DATE_COMMISSION_APTITUDE() {
		return "NOM_EF_DATE_COMMISSION_APTITUDE";
	}

	public String getVAL_EF_DATE_COMMISSION_APTITUDE() {
		return getZone(getNOM_EF_DATE_COMMISSION_APTITUDE());
	}
	
}
