package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumImpressionAffectation;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.TypeContrat;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.ContratDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.metier.referentiel.TypeContratDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.cmis.AlfrescoCMISService;
import nc.noumea.spring.service.cmis.IAlfrescoCMISService;

import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAGENTActesDonneesPerso Date de création : (11/10/11 08:38:48)
 * 
 */
public class OeAGENTActesDonneesPerso extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	public String focus = null;
	private Agent agentCourant;
	private Document documentCourant;
	private DocumentAgent lienDocumentAgentCourant;

	private ArrayList<Document> listeDocuments;
	private String[] LB_TYPE_DOCUMENT;
	private ArrayList<TypeDocument> listeTypeDocument;
	private String[] LB_CONTRAT;
	private ArrayList<Contrat> listeContrat;
	private String[] LB_AFFECTATION;
	private ArrayList<Affectation> listeAffectation;
	private String[] LB_TYPE_FICHIER_AFFECTATION;
	private String[] LB_FICHE_POSTE;
	private ArrayList<FichePoste> listeFichePoste;

	public String ACTION_SUPPRESSION = "Suppression d'un document";
	public String ACTION_CREATION = "Choix du fichier a ajouter";

	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;
	private String vueCourant = null;


	private TypeDocumentDao typeDocumentDao;
	private TypeContratDao typeContratDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private ContratDao contratDao;
	private TitrePosteDao titrePosteDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	private IAlfrescoCMISService alfrescoCMISService;
	private IRadiService radiService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
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
			setLB_AFFECTATION(LBVide);
			setLB_CONTRAT(LBVide);
			setLB_TYPE_DOCUMENT(LBVide);
			setLB_FICHE_POSTE(LBVide);
			setLB_TYPE_FICHIER_AFFECTATION(LBVide);
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

		if (getVueCourant() == null) {
			setVueCourant("Autre");
			addZone(getNOM_RG_VUE(), getNOM_RB_VUE_AUTRE());
		}

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
		if (getTypeContratDao() == null) {
			setTypeContratDao(new TypeContratDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getContratDao() == null) {
			setContratDao(new ContratDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (alfrescoCMISService == null) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
	}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeDeroulante() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (getLB_TYPE_DOCUMENT() == LBVide) {
			ArrayList<TypeDocument> td = getTypeDocumentDao().listerTypeDocumentAvecModule("DONNEES PERSONNELLES");
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
		if (getLB_CONTRAT() == LBVide) {
			if (null != getAgentCourant()) {
				ArrayList<Contrat> c = getContratDao().listerContratAvecAgent(
						getAgentCourant().getIdAgent());
				if (c.size() > 0) {
					int[] tailles = { 14, 8, 12 };
					FormateListe aFormat = new FormateListe(tailles);
					String ligneVide[] = { Const.CHAINE_VIDE, Const.CHAINE_VIDE, Const.CHAINE_VIDE };
					aFormat.ajouteLigne(ligneVide);
					for (ListIterator<Contrat> list = c.listIterator(); list.hasNext();) {
						Contrat contrat = (Contrat) list.next();
						TypeContrat tc = getTypeContratDao().chercherTypeContrat(contrat.getIdTypeContrat());
						String ligne[] = { contrat.getNumContrat(), tc.getLibTypeContrat(),
								sdf.format(contrat.getDatdeb()) };
						aFormat.ajouteLigne(ligne);
					}
					setLB_CONTRAT(aFormat.getListeFormatee());
				} else {
					setLB_CONTRAT(null);
				}
				Contrat contratVide = new Contrat();
				c.add(0, contratVide);
				setListeContrat(c);
			}
		}
		if (getLB_AFFECTATION() == LBVide) {
			if (null != getAgentCourant()) {
				ArrayList<Affectation> aff = getAffectationDao().listerAffectationAvecAgent(
						getAgentCourant().getIdAgent());
				if (aff.size() > 0) {
					int[] tailles = { 15, 50 };
					FormateListe aFormat = new FormateListe(tailles);
					String ligneVide[] = { Const.CHAINE_VIDE, Const.CHAINE_VIDE, Const.CHAINE_VIDE };
					aFormat.ajouteLigne(ligneVide);
					for (ListIterator<Affectation> list = aff.listIterator(); list.hasNext();) {
						Affectation a = (Affectation) list.next();
						FichePoste fp = getFichePosteDao().chercherFichePoste(a.getIdFichePoste());
						TitrePoste tp = getTitrePosteDao().chercherTitrePoste(fp.getIdTitrePoste());
						String ligne[] = { sdf.format(a.getDateDebutAff()), tp.getLibTitrePoste() };
						aFormat.ajouteLigne(ligne);
					}
					setLB_AFFECTATION(aFormat.getListeFormatee());
				} else {
					setLB_AFFECTATION(null);
				}
				Affectation affVide = new Affectation();
				aff.add(0, affVide);
				setListeAffectation(aff);
			}
		}
		if (getLB_FICHE_POSTE() == LBVide) {
			if (null != getAgentCourant()) {
				// Recherche de tous les liens Agent / FichePoste
				ArrayList<Affectation> liens = getAffectationDao().listerAffectationActiveAvecAgent(
						getAgentCourant().getIdAgent());
				ArrayList<FichePoste> listeFp = getFichePosteDao().listerFichePosteAvecAgent(liens);
				if (listeFp.size() > 0) {
					int[] tailles = { 15, 50 };
					FormateListe aFormat = new FormateListe(tailles);
					String ligneVide[] = { Const.CHAINE_VIDE, Const.CHAINE_VIDE, Const.CHAINE_VIDE };
					aFormat.ajouteLigne(ligneVide);
					for (ListIterator<FichePoste> list = listeFp.listIterator(); list.hasNext();) {
						FichePoste fiche = (FichePoste) list.next();
						TitrePoste tp = getTitrePosteDao().chercherTitrePoste(fiche.getIdTitrePoste());
						String ligne[] = { "FP: " + fiche.getNumFp(), tp.getLibTitrePoste() };
						aFormat.ajouteLigne(ligne);
					}
					setLB_FICHE_POSTE(aFormat.getListeFormatee());
				} else {
					setLB_FICHE_POSTE(null);
				}
				FichePoste fpVide = new FichePoste();
				listeFp.add(0, fpVide);
				setListeFichePoste(listeFp);
			}
		}
		if (getLB_TYPE_FICHIER_AFFECTATION() == LBVide) {
			setLB_TYPE_FICHIER_AFFECTATION(EnumImpressionAffectation.getValues());
			addZone(getNOM_LB_TYPE_FICHIER_AFFECTATION_SELECT(), "0");
		}
	}

	/**
	 * Constructeur du process OeAGENTActesDonneesPerso. Date de création :
	 * (11/10/11 08:38:48)
	 * 
	 */
	public OeAGENTActesDonneesPerso() {
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
		addZone(getNOM_LB_CONTRAT_SELECT(), multi.getParameter(getNOM_LB_CONTRAT()));
		addZone(getNOM_LB_FICHE_POSTE_SELECT(), multi.getParameter(getNOM_LB_FICHE_POSTE()));
		addZone(getNOM_LB_AFFECTATION_SELECT(), multi.getParameter(getNOM_LB_AFFECTATION()));
		addZone(getNOM_LB_TYPE_FICHIER_AFFECTATION_SELECT(), multi.getParameter(getNOM_LB_TYPE_FICHIER_AFFECTATION()));
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
		if (nomType.toUpperCase().equals("CONTRAT")) {
			if (multi.getParameter(getNOM_LB_CONTRAT()).equals("0")) {
				// ERR002:La zone contrat est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "contrat"));
				result &= false;
			}
		} else if (nomType.toUpperCase().equals("NOTE DE SERVICE")) {
			if (multi.getParameter(getNOM_LB_AFFECTATION()).equals("0")) {
				// ERR002:La zone @ est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "affectation"));
				result &= false;
			}
			if (multi.getParameter(getNOM_LB_TYPE_FICHIER_AFFECTATION()).equals("0")) {
				// ERR002:La zone @ est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type de fichier"));
				result &= false;
			}
		} else if (nomType.toUpperCase().equals("FICHE DE POSTE")) {
			if (multi.getParameter(getNOM_LB_FICHE_POSTE()).equals("0")) {
				// ERR002:La zone @ est obligatoire.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "fiche de poste"));
				result &= false;
			}
		} else if (nomType.toUpperCase().equals("PHOTO")) {
			// on verifie que l'extension soit bien .jpg
			String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
					fichierUpload.getName().length());
			if (!extension.toUpperCase().equals(".JPG")) {
				// alors on affiche un message qu'il faut que l'extension
				// soit.jpg
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR150"));
				fichierUpload.delete();
				fichierUpload = null;
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
		boolean ajoutContrat = false;
		boolean ajoutAffectation = false;
		boolean ajoutFichePoste = false;
		boolean ajoutPhoto = false;
		Contrat c = new Contrat();
		Affectation aff = new Affectation();
		FichePoste fichePoste = new FichePoste();
		String nomDocumentNS = null;
		if (nomType.toUpperCase().equals("CONTRAT")) {
			if (!multi.getParameter(getNOM_LB_CONTRAT()).equals("0")) {
				int indiceContrat = (Services.estNumerique(multi.getParameter(getNOM_LB_CONTRAT())) ? Integer
						.parseInt(multi.getParameter(getNOM_LB_CONTRAT())) : -1);
				c = ((Contrat) getListeContrat().get(indiceContrat));
				ajoutContrat = true;
			}
		}
		if (nomType.toUpperCase().equals("NOTE DE SERVICE")) {
			if (!multi.getParameter(getNOM_LB_AFFECTATION()).equals("0")
					&& !multi.getParameter(getNOM_LB_TYPE_FICHIER_AFFECTATION()).equals("0")) {
				int indiceAffectation = (Services.estNumerique(multi.getParameter(getNOM_LB_AFFECTATION())) ? Integer
						.parseInt(multi.getParameter(getNOM_LB_AFFECTATION())) : -1);
				// recup du document a imprimer
				nomDocumentNS = EnumImpressionAffectation.getCodeImpressionAffectation(Integer.parseInt(multi
						.getParameter(getNOM_LB_TYPE_FICHIER_AFFECTATION())));
				aff = ((Affectation) getListeAffectation().get(indiceAffectation));
				ajoutAffectation = true;
			}
		}
		if (nomType.toUpperCase().equals("FICHE DE POSTE")) {
			if (!multi.getParameter(getNOM_LB_FICHE_POSTE()).equals("0")) {
				int indiceFichePoste = (Services.estNumerique(multi.getParameter(getNOM_LB_FICHE_POSTE())) ? Integer
						.parseInt(multi.getParameter(getNOM_LB_FICHE_POSTE())) : -1);
				fichePoste = ((FichePoste) getListeFichePoste().get(indiceFichePoste));
				ajoutFichePoste = true;
			}
		}
		if (nomType.toUpperCase().equals("PHOTO")) {
			ajoutPhoto = true;
		}
		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {

			if (!creeDocument(request, ajoutContrat, c, ajoutAffectation, aff, ajoutFichePoste,
					fichePoste, ajoutPhoto)) {
				return false;
			}

		} else {
			if (ajoutContrat) {
				// on supprime le document existant dans la base de données
				Document d = getDocumentDao().chercherDocumentByContainsNom("C_" + c.getIdContrat());
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				
				ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
				if (declarerErreurFromReturnMessageDto(rmd))
					return false;
				
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());
			} else if (ajoutAffectation) {
				// on supprime le document existant dans la base de données
				String nomSansExtension = nomDocumentNS.substring(0, nomDocumentNS.indexOf("."));
				Document d = getDocumentDao()
						.chercherDocumentByContainsNom(
								"NS_" + aff.getIdAffectation() + "_"
										+ nomSansExtension.substring(3, nomSansExtension.length()));
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				
				ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
				if (declarerErreurFromReturnMessageDto(rmd))
					return false;
				
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());
			} else if (ajoutFichePoste) {
				// on supprime le document existant dans la base de données
				Document d = getDocumentDao().chercherDocumentByContainsNom("FP_" + fichePoste.getIdFichePoste());
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				
				ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
				if (declarerErreurFromReturnMessageDto(rmd))
					return false;
				
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());
			} else if (ajoutPhoto) {
				// on supprime le document existant dans la base de données
				Document d = getDocumentDao().chercherDocumentByContainsNom("PHO_" + getAgentCourant().getIdAgent());
				DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
						getAgentCourant().getIdAgent(), d.getIdDocument());
				
				ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
				if (declarerErreurFromReturnMessageDto(rmd))
					return false;
				
				getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
				getDocumentDao().supprimerDocument(d.getIdDocument());

			}
			if (!creeDocument(request, ajoutContrat, c, ajoutAffectation, aff, ajoutFichePoste,
					fichePoste, ajoutPhoto)) {
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	private boolean creeDocument(HttpServletRequest request, boolean ajoutContrat, Contrat c, boolean ajoutAffectation,
			Affectation aff, boolean ajoutFichePoste, FichePoste fp, boolean ajoutPhoto)
			throws Exception {
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

		// on crée le document en base de données
		getDocumentCourant().setIdTypeDocument(
				((TypeDocument) getListeTypeDocument().get(indiceTypeDoc - 1)).getIdTypeDocument());
		getDocumentCourant().setNomOriginal(fichierUpload.getName());
		getDocumentCourant().setDateDocument(new Date());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		
		Integer reference = null;
		if(null != c.getIdContrat()) {
			reference = c.getIdContrat();
		} else if(null != aff) {
			reference = aff.getIdAffectation();
		} else if(null != fp) {
			reference = fp.getIdFichePoste();
		}

		getDocumentCourant().setReference(reference);
		
		// on upload le fichier
		ReturnMessageDto rmd = alfrescoCMISService.uploadDocument(getAgentConnecte(request).getIdAgent(), getAgentCourant(), getDocumentCourant(), 
				fichierUpload, codTypeDoc);

		if (declarerErreurFromReturnMessageDto(rmd))
			return false;

		// on crée le document en base de données
		Integer id = getDocumentDao().creerDocument(getDocumentCourant().getClasseDocument(),
				getDocumentCourant().getNomDocument(), getDocumentCourant().getLienDocument(),
				getDocumentCourant().getDateDocument(), getDocumentCourant().getCommentaire(),
				getDocumentCourant().getIdTypeDocument(), getDocumentCourant().getNomOriginal(),
				getDocumentCourant().getNodeRefAlfresco(), getDocumentCourant().getCommentaireAlfresco(),
				getDocumentCourant().getReference());

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
		addZone(getNOM_LB_AFFECTATION_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_FICHE_POSTE_SELECT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_FICHIER_AFFECTATION_SELECT(), Const.CHAINE_VIDE);
		initialiseListeDocuments(request);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

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
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT Date
	 * de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 *            focus à  définir.
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
				getAgentCourant().getIdAgent(), getVueCourant(), "DONNEES PERSONNELLES");
		setListeDocuments(listeDocAgent);

		int indiceActe = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) getTypeDocumentDao().chercherTypeDocument(doc.getIdTypeDocument());

				addZone(getNOM_ST_NOM_DOC(indiceActe),
						doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(indiceActe),
						doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_TYPE_DOC(indiceActe), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActe), sdf.format(doc.getDateDocument()));
				addZone(getNOM_ST_COMMENTAIRE(indiceActe), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());
				addZone(getNOM_ST_URL_DOC(indiceActe),
						(null == doc.getNodeRefAlfresco()
							|| doc.getNodeRefAlfresco().equals(Const.CHAINE_VIDE))
							? "&nbsp;" 
							: AlfrescoCMISService.getUrlOfDocument(doc.getNodeRefAlfresco()));

				indiceActe++;
			}
		}

		// addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

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
		
		// on supprime le fichier physiquement sur alfresco
		ReturnMessageDto rmd = alfrescoCMISService.removeDocument(getDocumentCourant());
		if (declarerErreurFromReturnMessageDto(rmd))
			return false;
		
		// suppression dans table DOCUMENT_AGENT
		getLienDocumentAgentDao().supprimerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(),
				getLienDocumentAgentCourant().getIdDocument());
		// Suppression dans la table DOCUMENT_ASSOCIE
		getDocumentDao().supprimerDocument(getDocumentCourant().getIdDocument());

		if (getTransaction().isErreur())
			return false;

		// tout s'est bien passé
		commitTransaction();
		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TYPE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		return true;
	}

	private boolean declarerErreurFromReturnMessageDto(ReturnMessageDto rmd) {
		
		if(!rmd.getErrors().isEmpty()) {
			String errors = "";
			for(String error : rmd.getErrors()) {
				errors += error;
			}
			
			getTransaction().declarerErreur("Err : " + errors);
			return true;
		}
		return false;
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

			// Si clic sur le bouton PB_CHANGER_VUE
			if (testerParametre(request, getNOM_PB_CHANGER_VUE())) {
				return performPB_CHANGER_VUE(request);
			}

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
		return "OeAGENTActesDonneesPerso.jsp";
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
		addZone(getNOM_LB_CONTRAT_SELECT(), Const.CHAINE_VIDE);

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
		return "ECR-AG-DP-ACTES";
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
			if (nomType.toUpperCase().equals("CONTRAT")) {
				// on affiche une liste deroulante des differents contrats
				// disponibles
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), "CONTRAT");
			} else if (nomType.toUpperCase().equals("NOTE DE SERVICE")) {
				// on affiche une liste deroulante des differents affectations
				// disponibles
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), "NOTE DE SERVICE");
			} else if (nomType.toUpperCase().equals("FICHE DE POSTE")) {
				// on affiche une liste deroulante des differents FP disponibles
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), "FICHE DE POSTE");
			} else {
				addZone(getNOM_ST_CHOIX_TYPE_DOC(), Const.CHAINE_VIDE);
			}
			addZone(getNOM_LB_TYPE_DOCUMENT_SELECT(), multi.getParameter(getNOM_LB_TYPE_DOCUMENT()));
			return true;

		} else {
			return false;
		}
	}

	public String getVAL_ST_CHOIX_TYPE_DOC() {
		return getZone(getNOM_ST_CHOIX_TYPE_DOC());
	}

	public String getNOM_ST_CHOIX_TYPE_DOC() {
		return "NOM_ST_CHOIX_TYPE_DOC";
	}

	public String getNOM_LB_CONTRAT() {
		return "NOM_LB_CONTRAT";
	}

	public String[] getVAL_LB_CONTRAT() {
		return getLB_CONTRAT();
	}

	private String[] getLB_CONTRAT() {
		if (LB_CONTRAT == null)
			LB_CONTRAT = initialiseLazyLB();
		return LB_CONTRAT;
	}

	private void setLB_CONTRAT(String[] newLB_CONTRAT) {
		LB_CONTRAT = newLB_CONTRAT;
	}

	public String getVAL_LB_CONTRAT_SELECT() {
		return getZone(getNOM_LB_CONTRAT_SELECT());
	}

	public String getNOM_LB_CONTRAT_SELECT() {
		return "NOM_LB_CONTRAT_SELECT";
	}

	private ArrayList<Contrat> getListeContrat() {
		if (listeContrat == null) {
			listeContrat = new ArrayList<Contrat>();
		}
		return listeContrat;
	}

	private void setListeContrat(ArrayList<Contrat> newListeContrat) {
		listeContrat = newListeContrat;
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	public String getNOM_LB_FICHE_POSTE() {
		return "NOM_LB_FICHE_POSTE";
	}

	public String[] getVAL_LB_FICHE_POSTE() {
		return getLB_FICHE_POSTE();
	}

	private String[] getLB_FICHE_POSTE() {
		if (LB_FICHE_POSTE == null)
			LB_FICHE_POSTE = initialiseLazyLB();
		return LB_FICHE_POSTE;
	}

	private void setLB_FICHE_POSTE(String[] newLB_FICHE_POSTE) {
		LB_FICHE_POSTE = newLB_FICHE_POSTE;
	}

	public String getVAL_LB_FICHE_POSTE_SELECT() {
		return getZone(getNOM_LB_FICHE_POSTE_SELECT());
	}

	public String getNOM_LB_FICHE_POSTE_SELECT() {
		return "NOM_LB_FICHE_POSTE_SELECT";
	}

	private ArrayList<FichePoste> getListeFichePoste() {
		if (listeFichePoste == null) {
			listeFichePoste = new ArrayList<FichePoste>();
		}
		return listeFichePoste;
	}

	private void setListeFichePoste(ArrayList<FichePoste> newListeFichePoste) {
		listeFichePoste = newListeFichePoste;
	}

	public String getNOM_LB_AFFECTATION() {
		return "NOM_LB_AFFECTATION";
	}

	public String[] getVAL_LB_AFFECTATION() {
		return getLB_AFFECTATION();
	}

	private String[] getLB_AFFECTATION() {
		if (LB_AFFECTATION == null)
			LB_AFFECTATION = initialiseLazyLB();
		return LB_AFFECTATION;
	}

	private void setLB_AFFECTATION(String[] newLB_AFFECTATION) {
		LB_AFFECTATION = newLB_AFFECTATION;
	}

	public String getVAL_LB_AFFECTATION_SELECT() {
		return getZone(getNOM_LB_AFFECTATION_SELECT());
	}

	public String getNOM_LB_AFFECTATION_SELECT() {
		return "NOM_LB_AFFECTATION_SELECT";
	}

	private ArrayList<Affectation> getListeAffectation() {
		if (listeAffectation == null) {
			listeAffectation = new ArrayList<Affectation>();
		}
		return listeAffectation;
	}

	private void setListeAffectation(ArrayList<Affectation> newListeAffectation) {
		listeAffectation = newListeAffectation;
	}

	public String getNOM_LB_TYPE_FICHIER_AFFECTATION() {
		return "NOM_LB_TYPE_FICHIER_AFFECTATION";
	}

	public String[] getVAL_LB_TYPE_FICHIER_AFFECTATION() {
		return getLB_TYPE_FICHIER_AFFECTATION();
	}

	private String[] getLB_TYPE_FICHIER_AFFECTATION() {
		if (LB_TYPE_FICHIER_AFFECTATION == null)
			LB_TYPE_FICHIER_AFFECTATION = initialiseLazyLB();
		return LB_TYPE_FICHIER_AFFECTATION;
	}

	private void setLB_TYPE_FICHIER_AFFECTATION(String[] newLB_TYPE_FICHIER_AFFECTATION) {
		LB_TYPE_FICHIER_AFFECTATION = newLB_TYPE_FICHIER_AFFECTATION;
	}

	public String getVAL_LB_TYPE_FICHIER_AFFECTATION_SELECT() {
		return getZone(getNOM_LB_TYPE_FICHIER_AFFECTATION_SELECT());
	}

	public String getNOM_LB_TYPE_FICHIER_AFFECTATION_SELECT() {
		return "NOM_LB_TYPE_FICHIER_AFFECTATION_SELECT";
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
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_VUE Date de création
	 * : (04/07/11 13:57:35)
	 * 
	 */
	public String getNOM_PB_CHANGER_VUE() {
		return "NOM_PB_CHANGER_VUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/07/11 13:57:35)
	 * 
	 */
	public boolean performPB_CHANGER_VUE(HttpServletRequest request) throws Exception {
		// Mise à  jour de la liste des compétences
		if (getVAL_RG_VUE().equals(getNOM_RB_VUE_AUTRE()))
			setVueCourant("Autre");
		if (getVAL_RG_VUE().equals(getNOM_RB_VUE_SAUVEGARDE()))
			setVueCourant("Sauvegarde");
		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(getTransaction().traiterErreur());
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_VUE
	 * Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RG_VUE() {
		return "NOM_RG_VUE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_VUE Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getVAL_RG_VUE() {
		return getZone(getNOM_RG_VUE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VUE_AUTRE Date de
	 * création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_VUE_AUTRE() {
		return "NOM_RB_VUE_AUTRE";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VUE_SAUVEGARDE Date de
	 * création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_VUE_SAUVEGARDE() {
		return "NOM_RB_VUE_SAUVEGARDE";
	}

	public String getVueCourant() {
		return vueCourant;
	}

	public void setVueCourant(String vueCourant) {
		this.vueCourant = vueCourant;
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NOM_DOC Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TYPE_DOC Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_DOC Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
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

	public TypeContratDao getTypeContratDao() {
		return typeContratDao;
	}

	public void setTypeContratDao(TypeContratDao typeContratDao) {
		this.typeContratDao = typeContratDao;
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

	public ContratDao getContratDao() {
		return contratDao;
	}

	public void setContratDao(ContratDao contratDao) {
		this.contratDao = contratDao;
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

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
	
	public String getNOM_ST_URL_DOC(int i) {
		return "NOM_ST_URL_DOC" + i;
	}

	public String getVAL_ST_URL_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC(i));
	}

}
