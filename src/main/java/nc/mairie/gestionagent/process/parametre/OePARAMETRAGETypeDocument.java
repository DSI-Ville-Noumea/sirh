package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.PathAlfresco;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.parametrage.PathAlfrescoDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

public class OePARAMETRAGETypeDocument extends BasicProcess {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4947520932844167792L;

	private TypeDocumentDao typeDocumentDao;
	private DocumentDao documentDao;
	private PathAlfrescoDao pathAlfrescoDao;

	private ArrayList<String> listeModuleDocument;
	private ArrayList<PathAlfresco> listePathAlfresco;
	private ArrayList<TypeDocument> listeTypeDocument;
	private TypeDocument typeDocumentCourant;
	
	private String[] LB_TYPE_DOCUMENT;
	private String[] LB_MODULE;
	private String[] LB_PATH_ALFRESCO;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	
	@Override
	public String getJSP() {
		return "OePARAMETRAGETypeDocument.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-TYPE-DOCUMENT";
	}

	@Override
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

		if (getListeTypeDocument() == null) {
			// Recherche des types de documents
			initialiseListeTypeDocument(request);
		}
		if(null == getListeModuleDocument())  {
			initialiseListeModuleDocument(request);
		}
		if(null == getListePathAlfresco())  {
			initialiseListePathAlfresco(request);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if(null == getPathAlfrescoDao()) {
			setPathAlfrescoDao(new PathAlfrescoDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_DOCUMENT())) {
				return performPB_VALIDER_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_DOCUMENT())) {
				return performPB_ANNULER_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_CREER_TYPE_DOCUMENT())) {
				return performPB_CREER_TYPE_DOCUMENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_DOCUMENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_DOCUMENT())) {
				return performPB_SUPPRIMER_TYPE_DOCUMENT(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation de la listes des types de documents Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeTypeDocument(HttpServletRequest request) throws Exception {
		setListeTypeDocument(getTypeDocumentDao().listerTypeDocument());
		if (getListeTypeDocument().size() != 0) {
			int tailles[] = { 30, 30, 30, 30 };
			String padding[] = { "G", "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeDocument> list = getListeTypeDocument().listIterator(); list.hasNext();) {
				TypeDocument type = (TypeDocument) list.next();
				
				PathAlfresco path = getPathAlfrescoDao().chercherPathAlfresco(type.getIdPathAlfresco());
				String[] ligne = { type.getLibTypeDocument() , type.getCodTypeDocument() , type.getModuleTypeDocument() , (null != path ? path.getPathAlfresco() : Const.CHAINE_VIDE)};

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DOCUMENT(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_DOCUMENT(null);
		}
	}

	/**
	 * Initialisation de la listes des types de documents Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListeModuleDocument(HttpServletRequest request) throws Exception {
		setListeModuleDocument(getTypeDocumentDao().listerModuleDocument());
		if (getListeModuleDocument().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<String> list = getListeModuleDocument().listIterator(); list.hasNext();) {
				String module = (String) list.next();
				String ligne[] = { module };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MODULE(aFormat.getListeFormatee());
		} else {
			setLB_MODULE(null);
		}
	}

	/**
	 * Initialisation de la listes des types de documents Date de création :
	 * (15/09/11)
	 * 
	 */
	private void initialiseListePathAlfresco(HttpServletRequest request) throws Exception {
		setListePathAlfresco(getPathAlfrescoDao().listerPathAlfresco());
		if (getListePathAlfresco().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<PathAlfresco> list = getListePathAlfresco().listIterator(); list.hasNext();) {
				PathAlfresco path = (PathAlfresco) list.next();
				String ligne[] = { path.getPathAlfresco() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_PATH_ALFRESCO(aFormat.getListeFormatee());
		} else {
			setLB_PATH_ALFRESCO(null);
		}
	}
	
	/**
	 * Controle les zones saisies d'un type de document Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTypeDocument(HttpServletRequest request) throws Exception {

		// Verification libellé type document not null
		if (getZone(getNOM_EF_TYPE_DOCUMENT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification libellé du code du type document not null
		if (getZone(getNOM_EF_CODE_TYPE_DOCUMENT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		if (getZone(getNOM_LB_MODULE_SELECT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "module"));
			return false;
		}
		if (getZone(getNOM_LB_PATH_ALFRESCO_SELECT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "chemin alfresco"));
			return false;
		}

		return true;
	}
	

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_CREER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MODULE_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PATH_ALFRESCO(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_DOCUMENT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_DOCUMENT_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeDocument().size()) {
			TypeDocument type = getListeTypeDocument().get(indice);
			setTypeDocumentCourant(type);
			addZone(getNOM_EF_TYPE_DOCUMENT(), type.getLibTypeDocument());
			addZone(getNOM_EF_CODE_TYPE_DOCUMENT(), type.getCodTypeDocument());
			addZone(getNOM_EF_MODULE_TYPE_DOCUMENT(), type.getModuleTypeDocument());
			
			PathAlfresco path = getPathAlfrescoDao().chercherPathAlfresco(type.getIdPathAlfresco());
			addZone(getNOM_EF_PATH_ALFRESCO(), null != path ? path.getPathAlfresco() : Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types de documents"));
		}

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		
		if (getVAL_ST_ACTION_TYPE_DOCUMENT() != null && getVAL_ST_ACTION_TYPE_DOCUMENT() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_CREATION)) {
				
				if (!performControlerSaisieTypeDocument(request))
					return false;

				if (!performControlerRegleGestionTypeDocument(request))
					return false;
				
				setTypeDocumentCourant(new TypeDocument());
				getTypeDocumentCourant().setLibTypeDocument(getVAL_EF_TYPE_DOCUMENT());
				getTypeDocumentCourant().setCodTypeDocument(getVAL_EF_CODE_TYPE_DOCUMENT());
				
				String module = getListeModuleDocument().get(new Integer(getVAL_LB_MODULE_SELECT()));
				getTypeDocumentCourant().setModuleTypeDocument(module);
				
				PathAlfresco path = getListePathAlfresco().get(new Integer(getVAL_LB_PATH_ALFRESCO_SELECT()));
				
				getTypeDocumentCourant().setIdPathAlfresco(path.getIdPathAlfresco());
				getTypeDocumentDao()
						.creerTypeDocument(getTypeDocumentCourant().getLibTypeDocument(),
								getTypeDocumentCourant().getCodTypeDocument(),
								getTypeDocumentCourant().getModuleTypeDocument(),
								getTypeDocumentCourant().getIdPathAlfresco());
				if (!getTransaction().isErreur())
					getListeTypeDocument().add(getTypeDocumentCourant());
				else
					return false;
			} else if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_SUPPRESSION)) {
				getTypeDocumentDao().supprimerTypeDocument(getTypeDocumentCourant().getIdTypeDocument());
				if (!getTransaction().isErreur())
					getListeTypeDocument().remove(getTypeDocumentCourant());
				else
					return false;
				setTypeDocumentCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeDocument(request);
			addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Controle les regles de gestion d'une autre administration Date de
	 * création : (14/09/11)
	 */
	private boolean performControlerRegleGestionTypeDocument(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type de document utilise sur
		// document agent

		if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_SUPPRESSION)
				&& getDocumentDao().listerDocumentAvecType(getTypeDocumentCourant().getIdTypeDocument()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché a @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un document", "ce type"));
			return false;
		}

		// Vérification des contraintes d'unicité du type de document
		if (getVAL_ST_ACTION_TYPE_DOCUMENT().equals(ACTION_CREATION)) {

			for (TypeDocument typeDoc : getListeTypeDocument()) {
				if (typeDoc.getLibTypeDocument().equals(getVAL_EF_TYPE_DOCUMENT().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un type de document", "ce libellé"));
					return false;
				}
				if (typeDoc.getCodTypeDocument().equals(getVAL_EF_CODE_TYPE_DOCUMENT().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un code type de document", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_DOCUMENT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_DOCUMENT(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DOCUMENT
	 * 
	 */
	public String getNOM_LB_TYPE_DOCUMENT() {
		return "NOM_LB_TYPE_DOCUMENT";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_DOCUMENT
	 * 
	 */
	public String getVAL_LB_TYPE_DOCUMENT_SELECT() {
		return getZone(getNOM_LB_TYPE_DOCUMENT_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DOCUMENT_SELECT
	 * 
	 */
	public String getNOM_LB_TYPE_DOCUMENT_SELECT() {
		return "NOM_LB_TYPE_DOCUMENT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DOCUMENT
	 * 
	 */
	public String[] getVAL_LB_TYPE_DOCUMENT() {
		return getLB_TYPE_DOCUMENT();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DOCUMENT
	 * 
	 */
	private String[] getLB_TYPE_DOCUMENT() {
		if (LB_TYPE_DOCUMENT == null)
			LB_TYPE_DOCUMENT = initialiseLazyLB();
		return LB_TYPE_DOCUMENT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MODULE_DOCUMENT
	 * 
	 */
	public String getNOM_LB_MODULE() {
		return "NOM_LB_MODULE";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MODULE_DOCUMENT
	 * 
	 */
	public String getVAL_LB_MODULE_SELECT() {
		return getZone(getNOM_LB_MODULE_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MODULE_DOCUMENT
	 * 
	 */
	public String getNOM_LB_MODULE_SELECT() {
		return "NOM_LB_MODULE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MODULE
	 * 
	 */
	public String[] getVAL_LB_MODULE() {
		return getLB_MODULE();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MODULE
	 * 
	 */
	private String[] getLB_MODULE() {
		if (LB_MODULE == null)
			LB_MODULE = initialiseLazyLB();
		return LB_MODULE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_PATH_ALFRESCO
	 * 
	 */
	public String getNOM_LB_PATH_ALFRESCO() {
		return "NOM_LB_PATH_ALFRESCO";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : PATH_ALFRESCO
	 * 
	 */
	public String getVAL_LB_PATH_ALFRESCO_SELECT() {
		return getZone(getNOM_LB_PATH_ALFRESCO_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_PATH_ALFRESCO_SELECT
	 * 
	 */
	public String getNOM_LB_PATH_ALFRESCO_SELECT() {
		return "NOM_LB_PATH_ALFRESCO_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_PATH_ALFRESCO
	 * 
	 */
	public String[] getVAL_LB_PATH_ALFRESCO() {
		return getLB_PATH_ALFRESCO();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_PATH_ALFRESCO
	 * 
	 */
	private String[] getLB_PATH_ALFRESCO() {
		if (LB_PATH_ALFRESCO == null)
			LB_PATH_ALFRESCO = initialiseLazyLB();
		return LB_PATH_ALFRESCO;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_DOUMENT Date de
	 * création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_DOCUMENT() {
		return "NOM_PB_CREER_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_DOUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_DOCUMENT() {
		return "NOM_PB_SUPPRIMER_TYPE_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_DOUMENT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_DOCUMENT() {
		return getZone(getNOM_ST_ACTION_TYPE_DOCUMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TYPE_DOUMENT
	 * Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_DOCUMENT() {
		return "NOM_ST_ACTION_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_DOCUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_EF_TYPE_DOCUMENT() {
		return "NOM_EF_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_TYPE_DOCUMENT
	 * Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_EF_CODE_TYPE_DOCUMENT() {
		return "NOM_EF_CODE_TYPE_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_DOCUMENT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_EF_TYPE_DOCUMENT() {
		return getZone(getNOM_EF_TYPE_DOCUMENT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_TYPE_DOCUMENT Date de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getVAL_EF_CODE_TYPE_DOCUMENT() {
		return getZone(getNOM_EF_CODE_TYPE_DOCUMENT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : NOM_EF_MODULE_TYPE_DOCUMENT
	 * 
	 */
	public String getNOM_EF_MODULE_TYPE_DOCUMENT() {
		return "NOM_EF_MODULE_TYPE_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * NOM_EF_MODULE_TYPE_DOCUMENT
	 * 
	 */
	public String getVAL_EF_MODULE_TYPE_DOCUMENT() {
		return getZone(getNOM_EF_MODULE_TYPE_DOCUMENT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : NOM_EF_PATH_ALFRESCO
	 * 
	 */
	public String getNOM_EF_PATH_ALFRESCO() {
		return "NOM_EF_PATH_ALFRESCO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * NOM_EF_PATH_ALFRESCO
	 * 
	 */
	public String getVAL_EF_PATH_ALFRESCO() {
		return getZone(getNOM_EF_PATH_ALFRESCO());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_DOCUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_DOCUMENT() {
		return "NOM_PB_VALIDER_TYPE_DOCUMENT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_DOCUMENT Date
	 * de création : (15/09/11 14:14:43)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_DOCUMENT() {
		return "NOM_PB_ANNULER_TYPE_DOCUMENT";
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public ArrayList<TypeDocument> getListeTypeDocument() {
		return listeTypeDocument;
	}

	public void setListeTypeDocument(ArrayList<TypeDocument> listeTypeDocument) {
		this.listeTypeDocument = listeTypeDocument;
	}

	public TypeDocument getTypeDocumentCourant() {
		return typeDocumentCourant;
	}

	public void setTypeDocumentCourant(TypeDocument typeDocumentCourant) {
		this.typeDocumentCourant = typeDocumentCourant;
	}

	/**
	 * Setter de la liste: LB_TYPE_DOCUMENT 
	 * 
	 */
	private void setLB_TYPE_DOCUMENT(String[] newLB_TYPE_DOCUMENT) {
		LB_TYPE_DOCUMENT = newLB_TYPE_DOCUMENT;
	}

	public void setLB_MODULE(String[] lB_MODULE) {
		LB_MODULE = lB_MODULE;
	}

	public void setLB_PATH_ALFRESCO(String[] lB_PATH_ALFRESCO) {
		LB_PATH_ALFRESCO = lB_PATH_ALFRESCO;
	}

	public ArrayList<String> getListeModuleDocument() {
		return listeModuleDocument;
	}

	public void setListeModuleDocument(ArrayList<String> listeModuleDocument) {
		this.listeModuleDocument = listeModuleDocument;
	}

	public ArrayList<PathAlfresco> getListePathAlfresco() {
		return listePathAlfresco;
	}

	public void setListePathAlfresco(ArrayList<PathAlfresco> listePathAlfresco) {
		this.listePathAlfresco = listePathAlfresco;
	}

	public PathAlfrescoDao getPathAlfrescoDao() {
		return pathAlfrescoDao;
	}

	public void setPathAlfrescoDao(PathAlfrescoDao pathAlfrescoDao) {
		this.pathAlfrescoDao = pathAlfrescoDao;
	}
	
}
