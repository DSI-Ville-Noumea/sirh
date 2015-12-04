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
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.hsct.AccidentTravail;
import nc.mairie.metier.hsct.Handicap;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.NomHandicap;
import nc.mairie.metier.hsct.Recommandation;
import nc.mairie.metier.hsct.TypeAT;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.hsct.AccidentTravailDao;
import nc.mairie.spring.dao.metier.hsct.HandicapDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.NomHandicapDao;
import nc.mairie.spring.dao.metier.hsct.RecommandationDao;
import nc.mairie.spring.dao.metier.hsct.TypeATDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
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
 * Process OeAGENTActesDonneesPerso Date de création : (11/10/11 08:38:48)
 * 
 */
public class OeAGENTActesHSCT extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	public String focus = null;
	private Agent agentCourant;
	private Document documentCourant;
	private DocumentAgent lienDocumentAgentCourant;
	private String urlFichier;

	private ArrayList<Document> listeDocuments;
	private String[] LB_TYPE_DOCUMENT;
	private ArrayList<TypeDocument> listeTypeDocument;
	private String[] LB_AT;
	private ArrayList<AccidentTravail> listeAT;
	private String[] LB_VM;
	private ArrayList<VisiteMedicale> listeVM;
	private String[] LB_HANDI;
	private ArrayList<Handicap> listeHANDI;

	public String ACTION_SUPPRESSION = "Suppression d'un document";
	public String ACTION_CREATION = "Choix du fichier a ajouter";

	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

	private Logger logger = LoggerFactory.getLogger(OeAGENTActesHSCT.class);

	private TypeDocumentDao typeDocumentDao;
	private AccidentTravailDao accidentTravailDao;
	private HandicapDao handicapDao;
	private MedecinDao medecinDao;
	private NomHandicapDao nomHandicapDao;
	private RecommandationDao recommandationDao;
	private TypeATDao typeATDao;
	private VisiteMedicaleDao visiteMedicaleDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setLB_AT(LBVide);
			setLB_HANDI(LBVide);
			setLB_TYPE_DOCUMENT(LBVide);
			setLB_VM(LBVide);
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

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || etatStatut() == STATUT_RECHERCHER_AGENT
				|| MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeDocuments(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		} else {
			initialiseListeDocuments(request);
		}

		initialiseListeDeroulante();
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
		if (getHandicapDao() == null) {
			setHandicapDao(new HandicapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNomHandicapDao() == null) {
			setNomHandicapDao(new NomHandicapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRecommandationDao() == null) {
			setRecommandationDao(new RecommandationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeATDao() == null) {
			setTypeATDao(new TypeATDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getVisiteMedicaleDao() == null) {
			setVisiteMedicaleDao(new VisiteMedicaleDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeDeroulante() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (getLB_TYPE_DOCUMENT() == LBVide) {
			ArrayList<TypeDocument> td = getTypeDocumentDao().listerTypeDocumentAvecModule("HSCT");
			setListeTypeDocument(td);
			if (getListeTypeDocument().size() != 0) {
				int[] tailles = { 25 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeDocument> list = getListeTypeDocument().listIterator(); list.hasNext();) {
					TypeDocument de = (TypeDocument) list.next();
					String ligne[] = { de.getLibTypeDocument(), de.getIdTypeDocument().toString() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_DOCUMENT(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE_DOCUMENT(null);
			}
		}
		if (getLB_VM() == LBVide) {
			if (null != getAgentCourant()) {
				ArrayList<VisiteMedicale> c = getVisiteMedicaleDao().listerVisiteMedicaleAgent(
						getAgentCourant().getIdAgent());
				if (c.size() > 0) {
					int[] tailles = { 14, 30, 30 };
					FormateListe aFormat = new FormateListe(tailles);
					for (ListIterator<VisiteMedicale> list = c.listIterator(); list.hasNext();) {
						VisiteMedicale vm = (VisiteMedicale) list.next();
						Medecin medecin = getMedecinDao().chercherMedecin(vm.getIdMedecin());
						Recommandation recom = null;
						if (vm != null && vm.getIdRecommandation() != null) {
							recom = getRecommandationDao().chercherRecommandation(vm.getIdRecommandation());
						}
						String ligne[] = { sdf.format(vm.getDateDerniereVisite()), medecin.getNomMedecin(),
								recom == null ? Const.CHAINE_VIDE : recom.getDescRecommandation() };
						aFormat.ajouteLigne(ligne);
					}
					setLB_VM(aFormat.getListeFormatee(true));
				} else {
					setLB_VM(null);
				}
				setListeVM(c);
			}
		}
		if (getLB_HANDI() == LBVide) {
			if (null != getAgentCourant()) {
				ArrayList<Handicap> c = getHandicapDao().listerHandicapAgent(
						getAgentCourant().getIdAgent());
				if (c.size() > 0) {
					int[] tailles = { 14, 60 };
					FormateListe aFormat = new FormateListe(tailles);
					for (ListIterator<Handicap> list = c.listIterator(); list.hasNext();) {
						Handicap handi = (Handicap) list.next();
						NomHandicap nomHandi = getNomHandicapDao().chercherNomHandicap(handi.getIdTypeHandicap());
						String ligne[] = { sdf.format(handi.getDateDebutHandicap()), nomHandi.getNomTypeHandicap() };
						aFormat.ajouteLigne(ligne);
					}
					setLB_HANDI(aFormat.getListeFormatee(true));
				} else {
					setLB_HANDI(null);
				}
				setListeHANDI(c);
			}
		}
		if (getLB_AT() == LBVide) {
			if (null != getAgentCourant()) {
				ArrayList<AccidentTravail> c = getAccidentTravailDao().listerAccidentTravailAgent(
						getAgentCourant().getIdAgent());
				if (c.size() > 0) {
					int[] tailles = { 14, 60 };
					FormateListe aFormat = new FormateListe(tailles);
					for (ListIterator<AccidentTravail> list = c.listIterator(); list.hasNext();) {
						AccidentTravail acci = (AccidentTravail) list.next();
						TypeAT tAt = getTypeATDao().chercherTypeAT(acci.getIdTypeAt());
						String ligne[] = { sdf.format(acci.getDateAt()), tAt.getDescTypeAt() };
						aFormat.ajouteLigne(ligne);
					}
					setLB_AT(aFormat.getListeFormatee(true));
				} else {
					setLB_AT(null);
				}
				setListeAT(c);
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTActesHSCT. Date de création : (11/10/11
	 * 08:38:48)
	 * 
	 */
	public OeAGENTActesHSCT() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_UPLOADER Date de création :
	 * (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	private boolean performControlerSaisie(HttpServletRequest request, boolean aValider) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_LB_AT_SELECT(), multi.getParameter(getNOM_LB_AT()));
		addZone(getNOM_LB_VM_SELECT(), multi.getParameter(getNOM_LB_VM()));
		addZone(getNOM_LB_HANDI_SELECT(), multi.getParameter(getNOM_LB_HANDI()));
		addZone(getNOM_EF_COMMENTAIRE(), multi.getParameter(getNOM_EF_COMMENTAIRE()));

		boolean result = true;
		// Type Document
		if (getVAL_LB_TYPE_DOCUMENT_SELECT().equals("0")) {
			// ERR002:La zone type de document est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type de document"));
			result &= false;
		}
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}

		// si le type de document est contrat alors le contrat concerné est
		// obligatoire
		int indiceTypeDoc = (Services.estNumerique(getVAL_LB_TYPE_DOCUMENT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_DOCUMENT_SELECT()) : -1);
		String nomType = ((TypeDocument) getListeTypeDocument().get(indiceTypeDoc - 1)).getLibTypeDocument();
		if (nomType.toUpperCase().equals("ACCIDENT TRAVAIL")) {
			if (multi.getParameter(getNOM_LB_AT()).equals("0")) {
				// ERR002:La zone accident travail est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "accident travail"));
				result &= false;
			}
		} else if (nomType.toUpperCase().equals("VISITE MEDICALE")) {
			if (multi.getParameter(getNOM_LB_VM()).equals("0")) {
				// ERR002:La zone @ est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "visite médicale"));
				result &= false;
			}
		} else if (nomType.toUpperCase().equals("HANDICAP")) {
			if (multi.getParameter(getNOM_LB_HANDI()).equals("0")) {
				// ERR002:La zone @ est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "handicap"));
				result &= false;
			}
		}
		return result;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Controle des champs
		if (!performControlerSaisie(request, true))
			return false;
		// on recupere le contrat concerné par l'ajout
		int indice = (Services.estNumerique(getVAL_LB_TYPE_DOCUMENT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_DOCUMENT_SELECT()) : -1);
		String nomType = ((TypeDocument) getListeTypeDocument().get(indice - 1)).getLibTypeDocument();
		boolean ajoutAT = false;
		boolean ajoutVM = false;
		boolean ajoutHandi = false;
		AccidentTravail at = new AccidentTravail();
		VisiteMedicale vm = new VisiteMedicale();
		Handicap handi = new Handicap();
		if (nomType.toUpperCase().equals("ACCIDENT TRAVAIL")) {
			if (!multi.getParameter(getNOM_LB_AT()).equals("0")) {
				int indiceAT = (Services.estNumerique(multi.getParameter(getNOM_LB_AT())) ? Integer.parseInt(multi
						.getParameter(getNOM_LB_AT())) : -1);
				at = ((AccidentTravail) getListeAT().get(indiceAT - 1));
				ajoutAT = true;
			}
		}
		if (nomType.toUpperCase().equals("VISITE MEDICALE")) {
			if (!multi.getParameter(getNOM_LB_VM()).equals("0")) {
				int indiceVM = (Services.estNumerique(multi.getParameter(getNOM_LB_VM())) ? Integer.parseInt(multi
						.getParameter(getNOM_LB_VM())) : -1);
				vm = ((VisiteMedicale) getListeVM().get(indiceVM - 1));
				ajoutVM = true;
			}
		}
		if (nomType.toUpperCase().equals("HANDICAP")) {
			if (!multi.getParameter(getNOM_LB_HANDI()).equals("0")) {
				int indiceHandi = (Services.estNumerique(multi.getParameter(getNOM_LB_HANDI())) ? Integer
						.parseInt(multi.getParameter(getNOM_LB_HANDI())) : -1);
				handi = ((Handicap) getListeHANDI().get(indiceHandi - 1));
				ajoutHandi = true;
			}
		}
		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {
			String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();

			// on controle si il y a deja un fichier pour ce contrat
			if (ajoutAT) {
				if (!performControlerFichier(request, "AT_" + at.getIdAt() + "_" + dateJour)) {
					// alors on affiche un message pour prevenir que l'on va
					// ecraser le fichier precedent
					addZone(getNOM_ST_WARNING(),
							"Attention un fichier existe déjà pour cet accident du travail. Etes-vous sûr de vouloir écraser la version précédente ?");
					return true;
				}
			} else if (ajoutVM) {
				if (!performControlerFichier(request, "VM_" + vm.getIdVisite() + "_" + dateJour)) {
					// alors on affiche un message pour prevenir que l'on va
					// ecraser le fichier precedent
					addZone(getNOM_ST_WARNING(),
							"Attention un fichier du même type existe déjà pour cette visite médicale. Etes-vous sûr de vouloir écraser la version précédente ?");
					return true;
				}
			} else if (ajoutHandi) {
				if (!performControlerFichier(request, "HANDI_" + handi.getIdHandicap() + "_" + dateJour)) {
					// alors on affiche un message pour prevenir que l'on va
					// ecraser le fichier precedent
					addZone(getNOM_ST_WARNING(),
							"Attention un fichier du même type existe déjà pour ce handicap. Etes-vous sûr de vouloir écraser la version précédente ?");
					return true;
				}
			}
			if (!creeDocument(request, ajoutAT, at, ajoutVM, vm, ajoutHandi, handi)) {
				addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				// "ERR092", "Vous ne pouvez ajouter de document pour ce type."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR092"));
				return false;
			}

		} else {
			if (ajoutAT) {
				// on supprime le document existant dans la base de données
				Document d = getDocumentDao().chercherDocumentByContainsNom("AT_" + at.getIdAt());
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				File f = new File(repertoireStockage + d.getLienDocument());
				if (f.exists()) {
					f.delete();
				}
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());
			} else if (ajoutVM) {
				// on supprime le document existant dans la base de données
				Document d = getDocumentDao().chercherDocumentByContainsNom("VM_" + vm.getIdVisite());
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				File f = new File(repertoireStockage + d.getLienDocument());
				if (f.exists()) {
					f.delete();
				}
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());
			} else if (ajoutHandi) {
				// on supprime le document existant dans la base de données
				Document d = getDocumentDao().chercherDocumentByContainsNom("HANDI_" + handi.getIdHandicap());
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				File f = new File(repertoireStockage + d.getLienDocument());
				if (f.exists()) {
					f.delete();
				}
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());
			}
			if (!creeDocument(request, ajoutAT, at, ajoutVM, vm, ajoutHandi, handi)) {
				addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				// "ERR092", "Vous ne pouvez ajouter de document pour ce type."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR092"));
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;

	}

	private boolean creeDocument(HttpServletRequest request, boolean ajoutAT, AccidentTravail at, boolean ajoutVM,
			VisiteMedicale vm, boolean ajoutHandi, Handicap handi) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupere le type de document
		int indiceTypeDoc = (Services.estNumerique(getVAL_LB_TYPE_DOCUMENT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_DOCUMENT_SELECT()) : -1);
		String codTypeDoc = ((TypeDocument) getListeTypeDocument().get(indiceTypeDoc - 1)).getCodTypeDocument();
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = Const.CHAINE_VIDE;
		if (ajoutAT) {
			nom = codTypeDoc.toUpperCase() + "_" + at.getIdAt() + "_" + dateJour + extension;
		} else if (ajoutVM) {
			nom = codTypeDoc.toUpperCase() + "_" + vm.getIdVisite() + "_" + dateJour + extension;
		} else if (ajoutHandi) {
			nom = codTypeDoc.toUpperCase() + "_" + handi.getIdHandicap() + "_" + dateJour + extension;
		}
		if (nom.equals(Const.CHAINE_VIDE)) {
			return false;
		}

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
		// String repPartage = (String)
		// ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(
				((TypeDocument) getListeTypeDocument().get(indiceTypeDoc - 1)).getIdTypeDocument());
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
		addZone(getNOM_LB_TYPE_DOCUMENT_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_AT_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_VM_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_HANDI_SELECT(), Const.CHAINE_VIDE);
		initialiseListeDocuments(request);

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

	private boolean uploadFichier(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		// InputStream fis = new FileInputStream(f);
		/*
		 * File newFile = new File(repPartage + codTypeDoc + "/" + nomFichier);
		 * FileOutputStream fos = new FileOutputStream(newFile);
		 */

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

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT Date
	 * de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
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
		return getNOM_EF_LIENDOCUMENT();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
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
	 * Met a jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne le doc en cours.
	 * 
	 * @return documentCourant
	 */
	private Document getDocumentCourant() {
		return documentCourant;
	}

	/**
	 * Met a jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @param newListeDocuments
	 *            ArrayList
	 */
	private void setListeDocuments(ArrayList<Document> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<Document>();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des contacts
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des documents de l'agent
		ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgent(getLienDocumentAgentDao(),
				getAgentCourant().getIdAgent(), Const.CHAINE_VIDE, "HSCT");
		setListeDocuments(listeDocAgent);

		int indiceActe = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) getTypeDocumentDao().chercherTypeDocument(doc.getIdTypeDocument());
				String info = "&nbsp;";
				if (td.getCodTypeDocument().equals("VM")) {
					String nomDoc = doc.getNomDocument();
					// on recupere l'id du document
					nomDoc = nomDoc.substring(nomDoc.indexOf("_") + 1, nomDoc.length());
					String id = nomDoc.substring(0, nomDoc.indexOf("_"));
					VisiteMedicale vm = null;
					try {
						vm = getVisiteMedicaleDao().chercherVisiteMedicale(Integer.valueOf(id));
					} catch(org.springframework.dao.EmptyResultDataAccessException e) {
						
					}
					if (vm != null && vm.getDateDerniereVisite() != null) {
						info = "VM du : " + sdf.format(vm.getDateDerniereVisite());
					}
				} else if (td.getCodTypeDocument().equals("AT")) {
					String nomDoc = doc.getNomDocument();
					// on recupere l'id du document
					nomDoc = nomDoc.substring(nomDoc.indexOf("_") + 1, nomDoc.length());
					String id = nomDoc.substring(0, nomDoc.indexOf("_"));
					AccidentTravail at = getAccidentTravailDao().chercherAccidentTravail(Integer.valueOf(id));
					if (at != null && at.getDateAt() != null) {
						info = "AT du : " + sdf.format(at.getDateAt());
					}
				} else if (td.getCodTypeDocument().equals("HANDI")) {
					String nomDoc = doc.getNomDocument();
					// on recupere l'id du document
					nomDoc = nomDoc.substring(nomDoc.indexOf("_") + 1, nomDoc.length());
					String id = nomDoc.substring(0, nomDoc.indexOf("_"));
					try {
						Handicap handi = getHandicapDao().chercherHandicap(Integer.valueOf(id));
						if (handi != null && handi.getDateDebutHandicap() != null) {
							info = "Handicap du : " + sdf.format(handi.getDateDebutHandicap());
						}
					} catch (Exception e) {
						info = "Handicap manquant";
					}
				}

				addZone(getNOM_ST_NOM_DOC(indiceActe),
						doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(indiceActe),
						doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_TYPE_DOC(indiceActe), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActe), sdf.format(doc.getDateDocument()));
				addZone(getNOM_ST_COMMENTAIRE(indiceActe), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());
				addZone(getNOM_ST_INFO(indiceActe), info);

				indiceActe++;
			}
		}
	}

	public String getNOM_PB_TYPE_DOCUMENT() {
		return "NOM_PB_TYPE_DOCUMENT";
	}

	public String getNOM_LB_TYPE_DOCUMENT() {
		return "NOM_LB_TYPE_DOCUMENT";
	}

	public String[] getVAL_LB_TYPE_DOCUMENT() {
		return getLB_TYPE_DOCUMENT();
	}

	private String[] getLB_TYPE_DOCUMENT() {
		if (LB_TYPE_DOCUMENT == null)
			LB_TYPE_DOCUMENT = initialiseLazyLB();
		return LB_TYPE_DOCUMENT;
	}

	private void setLB_TYPE_DOCUMENT(String[] newLB_TYPE_DOCUMENT) {
		LB_TYPE_DOCUMENT = newLB_TYPE_DOCUMENT;
	}

	public String getVAL_LB_TYPE_DOCUMENT_SELECT() {
		return getZone(getNOM_LB_TYPE_DOCUMENT_SELECT());
	}

	public String getNOM_LB_TYPE_DOCUMENT_SELECT() {
		return "NOM_LB_TYPE_DOCUMENT_SELECT";
	}

	private ArrayList<TypeDocument> getListeTypeDocument() {
		if (listeTypeDocument == null) {
			listeTypeDocument = new ArrayList<TypeDocument>();
		}
		return listeTypeDocument;
	}

	private void setListeTypeDocument(ArrayList<TypeDocument> newListeTypeDocument) {
		listeTypeDocument = newListeTypeDocument;
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Récup du Diplome courant
		Document d = getDocumentCourant();

		TypeDocument td = (TypeDocument) getTypeDocumentDao().chercherTypeDocument(d.getIdTypeDocument());
		DocumentAgent lda = getLienDocumentAgentDao().chercherDocumentAgent(
				getAgentCourant().getIdAgent(), getDocumentCourant().getIdDocument());
		setLienDocumentAgentCourant(lda);

		if (getTransaction().isErreur())
			return false;

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_NOM_ORI_DOC(), d.getNomOriginal());
		addZone(getNOM_ST_TYPE_DOC(), td.getLibTypeDocument());
		addZone(getNOM_ST_DATE_DOC(), sdf.format(d.getDateDocument()));
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	private DocumentAgent getLienDocumentAgentCourant() {
		return lienDocumentAgentCourant;
	}

	/**
	 * Met a jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setLienDocumentAgentCourant(DocumentAgent lienDocumentAgentCourant) {
		this.lienDocumentAgentCourant = lienDocumentAgentCourant;
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
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

	public String getNOM_PB_VALIDER_SUPPRESSION() {
		return "NOM_PB_VALIDER_SUPPRESSION";
	}

	private boolean performPB_VALIDER_SUPPRESSION(HttpServletRequest request) throws Exception {
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

		if (getTransaction().isErreur())
			return false;

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
		addZone(getNOM_ST_NOM_ORI_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TYPE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		fichierUpload = null;

		return true;
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getVAL_ST_TYPE_DOC() {
		return getZone(getNOM_ST_TYPE_DOC());
	}

	public String getNOM_ST_TYPE_DOC() {
		return "NOM_ST_TYPE_DOC";
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

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (11/10/11 08:38:48)
	 * 
	 */
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

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_VALIDER_SUPPRESSION
			if (testerParametre(request, getNOM_PB_VALIDER_SUPPRESSION())) {
				return performPB_VALIDER_SUPPRESSION(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_TYPE_DOCUMENT())) {
				return performPB_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 13:46:24)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTActesHSCT.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_DOCUMENT_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_CHOIX_TYPE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_AT_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_VM_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_HANDI_SELECT(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de l'ecran utilise par la gestion des droits
	 */
	public String getNomEcran() {
		return "ECR-AG-HSCT-ACTES";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	public boolean performPB_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		int indiceTypeDoc = (Services.estNumerique(multi.getParameter(getNOM_LB_TYPE_DOCUMENT())) ? Integer
				.parseInt(multi.getParameter(getNOM_LB_TYPE_DOCUMENT())) : -1);
		if (indiceTypeDoc != -1 && indiceTypeDoc != 0) {
			String nomType = ((TypeDocument) getListeTypeDocument().get(indiceTypeDoc - 1)).getLibTypeDocument();
			if (nomType.toUpperCase().equals("ACCIDENT TRAVAIL")) {
				// on affiche une liste deroulante des differents AT disponibles
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), "ACCIDENT TRAVAIL");
			} else if (nomType.toUpperCase().equals("VISITE MEDICALE")) {
				// on affiche une liste deroulante des differents VM disponibles
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), "VISITE MEDICALE");
			} else if (nomType.toUpperCase().equals("HANDICAP")) {
				// on affiche une liste deroulante des differents Handicaps
				// disponibles
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), "HANDICAP");
			} else {
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), Const.CHAINE_VIDE);
			}
			addZone(getNOM_LB_TYPE_DOCUMENT_SELECT(), multi.getParameter(getNOM_LB_TYPE_DOCUMENT()));
			return true;

		} else {
			return false;
		}
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	/**
	 * méthode qui teste si un parametre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null || (multi != null && multi
				.getParameter(param) != null));
	}

	/**
	 * Process incoming requests for information*
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 */
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

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_DOC(int i) {
		return "NOM_ST_TYPE_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_DOC(int i) {
		return getZone(getNOM_ST_TYPE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_INFO(int i) {
		return "NOM_ST_INFO" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_INFO(int i) {
		return getZone(getNOM_ST_INFO(i));
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
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		// Récup du document courant
		Document d = (Document) getListeDocuments().get(indiceEltAConsulter);
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + d.getLienDocument()));

		return true;
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		Document d = (Document) getListeDocuments().get(indiceEltASuprimer);
		setDocumentCourant(d);

		// init des documents courant
		if (!initialiseDocumentSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getVAL_ST_CHOIX_TYPE_DOC() {
		return getZone(getNOM_ST_CHOIX_TYPE_DOC());
	}

	public String getNOM_ST_CHOIX_TYPE_DOC() {
		return "NOM_ST_CHOIX_TYPE_DOC";
	}

	public String getNOM_LB_AT() {
		return "NOM_LB_AT";
	}

	public String[] getVAL_LB_AT() {
		return getLB_AT();
	}

	private String[] getLB_AT() {
		if (LB_AT == null)
			LB_AT = initialiseLazyLB();
		return LB_AT;
	}

	private void setLB_AT(String[] newLB_AT) {
		LB_AT = newLB_AT;
	}

	public String getVAL_LB_AT_SELECT() {
		return getZone(getNOM_LB_AT_SELECT());
	}

	public String getNOM_LB_AT_SELECT() {
		return "NOM_LB_AT_SELECT";
	}

	private ArrayList<AccidentTravail> getListeAT() {
		if (listeAT == null) {
			listeAT = new ArrayList<AccidentTravail>();
		}
		return listeAT;
	}

	private void setListeAT(ArrayList<AccidentTravail> newListeAT) {
		listeAT = newListeAT;
	}

	public String getNOM_LB_VM() {
		return "NOM_LB_VM";
	}

	public String[] getVAL_LB_VM() {
		return getLB_VM();
	}

	private String[] getLB_VM() {
		if (LB_VM == null)
			LB_VM = initialiseLazyLB();
		return LB_VM;
	}

	private void setLB_VM(String[] newLB_VM) {
		LB_VM = newLB_VM;
	}

	public String getVAL_LB_VM_SELECT() {
		return getZone(getNOM_LB_VM_SELECT());
	}

	public String getNOM_LB_VM_SELECT() {
		return "NOM_LB_VM_SELECT";
	}

	private ArrayList<VisiteMedicale> getListeVM() {
		if (listeVM == null) {
			listeVM = new ArrayList<VisiteMedicale>();
		}
		return listeVM;
	}

	private void setListeVM(ArrayList<VisiteMedicale> newListeVM) {
		listeVM = newListeVM;
	}

	public String getNOM_LB_HANDI() {
		return "NOM_LB_HANDI";
	}

	public String[] getVAL_LB_HANDI() {
		return getLB_HANDI();
	}

	private String[] getLB_HANDI() {
		if (LB_HANDI == null)
			LB_HANDI = initialiseLazyLB();
		return LB_HANDI;
	}

	private void setLB_HANDI(String[] newLB_HANDI) {
		LB_HANDI = newLB_HANDI;
	}

	public String getVAL_LB_HANDI_SELECT() {
		return getZone(getNOM_LB_HANDI_SELECT());
	}

	public String getNOM_LB_HANDI_SELECT() {
		return "NOM_LB_HANDI_SELECT";
	}

	private ArrayList<Handicap> getListeHANDI() {
		if (listeHANDI == null) {
			listeHANDI = new ArrayList<Handicap>();
		}
		return listeHANDI;
	}

	private void setListeHANDI(ArrayList<Handicap> newListeHANDI) {
		listeHANDI = newListeHANDI;
	}

	public String getVAL_ST_NOM_ORI_DOC() {
		return getZone(getNOM_ST_NOM_ORI_DOC());
	}

	public String getNOM_ST_NOM_ORI_DOC() {
		return "NOM_ST_NOM_ORI_DOC";
	}

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
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

	public NomHandicapDao getNomHandicapDao() {
		return nomHandicapDao;
	}

	public void setNomHandicapDao(NomHandicapDao nomHandicapDao) {
		this.nomHandicapDao = nomHandicapDao;
	}

	public RecommandationDao getRecommandationDao() {
		return recommandationDao;
	}

	public void setRecommandationDao(RecommandationDao recommandationDao) {
		this.recommandationDao = recommandationDao;
	}

	public TypeATDao getTypeATDao() {
		return typeATDao;
	}

	public void setTypeATDao(TypeATDao typeATDao) {
		this.typeATDao = typeATDao;
	}

	public VisiteMedicaleDao getVisiteMedicaleDao() {
		return visiteMedicaleDao;
	}

	public void setVisiteMedicaleDao(VisiteMedicaleDao visiteMedicaleDao) {
		this.visiteMedicaleDao = visiteMedicaleDao;
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

}
