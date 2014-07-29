package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumCivilite;
import nc.mairie.enums.EnumCollectivite;
import nc.mairie.enums.EnumNationalite;
import nc.mairie.enums.EnumSexe;
import nc.mairie.enums.EnumTypeContact;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Contact;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.commun.BanqueGuichet;
import nc.mairie.metier.commun.Commune;
import nc.mairie.metier.commun.CommuneEtrangere;
import nc.mairie.metier.commun.CommunePostal;
import nc.mairie.metier.commun.Pays;
import nc.mairie.metier.commun.TypeContact;
import nc.mairie.metier.commun.VoieQuartier;
import nc.mairie.metier.referentiel.Collectivite;
import nc.mairie.metier.referentiel.EtatServiceMilitaire;
import nc.mairie.metier.referentiel.SituationFamiliale;
import nc.mairie.spring.dao.metier.agent.ContactDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.referentiel.CollectiviteDao;
import nc.mairie.spring.dao.metier.referentiel.EtatServiceMilitaireDao;
import nc.mairie.spring.dao.metier.referentiel.SituationFamilialeDao;
import nc.mairie.spring.dao.metier.referentiel.TypeContactDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTEtatCivil Date de création : (15/03/11 10:49:55)
 * 
 * 
 */
public class OeAGENTEtatCivil extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_AGT_HOMONYME = 8;
	public static final int STATUT_LIEU_NAISS = 7;
	public static final int STATUT_VOIE = 6;
	public static final int STATUT_CONTACT = 3;
	private boolean modeCreation;
	private String[] LB_BANQUE_GUICHET;
	private String[] LB_CIVILITE;
	private String[] LB_COLLECTIVITE;
	private String[] LB_COMMUNE_DOM;
	private String[] LB_NATIONALITE;
	private String[] LB_SITUATION;
	private String[] LB_TCONTACT;
	private String[] LB_TYPE_SERVICE;
	private ArrayList<Collectivite> listeCollectivite;
	private ArrayList<CommunePostal> listeCommuneDomNoumea;
	private ArrayList<CommunePostal> listeCommuneDomAutre;
	private ArrayList<CommunePostal> listeCommuneBP;
	private ArrayList<SituationFamiliale> listeSituation;
	private ArrayList<BanqueGuichet> listeBanqueGuichet;
	private ArrayList<EtatServiceMilitaire> listeTypeServiceMilitaire;
	private String[] listeNationalite;
	private AgentNW agentCourant;
	private String lieuNaissance;
	private Pays paysNaissanceCourant;
	private Object commNaissanceCourant;
	private VoieQuartier voieQuartierCourant;
	private CommunePostal communeDomCourant;
	private CommunePostal communeBPCourant;
	private String codePostal;
	public String message = Const.CHAINE_VIDE;
	public String focus = null;
	// Pour la gestion des contacts
	private Contact contactCourant;
	private ArrayList<Contact> listeContact;
	private ArrayList<Contact> listeContactAAjouter;
	private ArrayList<Contact> listeContactAModifier;
	private ArrayList<Contact> listeContactASupprimer;
	private Hashtable<Integer, TypeContact> hashTypeContact;
	public String ACTION_SUPPRESSION = "Suppression d'un contact.<FONT color='red'><div align='right'> Veuillez valider votre choix.</div></FONT>";
	private String ACTION_MODIFICATION = "Modification.";
	private String ACTION_CREATION = "Création.";
	private ArrayList<TypeContact> listeTypeContact;
	public String messageDesign = Const.CHAINE_VIDE;
	public boolean diffusableModifiable = true;
	public boolean prioritaireModifiable = true;

	private CollectiviteDao collectiviteDao;
	private EtatServiceMilitaireDao etatServiceMilitaireDao;
	private DocumentDao documentDao;
	private ContactDao contactDao;
	private TypeContactDao typeContactDao;
	private SituationFamilialeDao situationFamilialeDao;

	// Fin gestion des contacts
	public String getMessage() {
		return message;
	}

	public void setMessage(String newMessage) {
		message = newMessage;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_COMMUNE_DOM Date de création
	 * : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_COMMUNE_DOM() {
		return "NOM_PB_COMMUNE_DOM";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 * RG_AG_EC_C11
	 */
	public boolean performPB_COMMUNE_DOM(HttpServletRequest request) throws Exception {
		// RG_AG_EC_C11
		CommunePostal newCommune = null;
		if (getVAL_RG_VILLE_DOMICILE().equals(getNOM_RB_VILLE_DOMICILE_NOUMEA())) {
			newCommune = (CommunePostal) getListeCommuneDomNoumea().get(
					Integer.parseInt(getVAL_LB_COMMUNE_DOM_SELECT()));
		} else {
			newCommune = (CommunePostal) getListeCommuneDomAutre()
					.get(Integer.parseInt(getVAL_LB_COMMUNE_DOM_SELECT()));
		}
		setCommuneDomCourant(newCommune);
		addZone(getNOM_EF_CODE_POSTAL_DOM(), getCommuneDomCourant().getCodCodePostal());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process. - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_CREER_CONTACT(HttpServletRequest request) throws Exception {

		VariablesActivite.ajouter(this, "ACTION", "CREATION");
		setStatut(STATUT_CONTACT, true);

		addZone(getNOM_ST_ACTION_CONTACT(), ACTION_CREATION);
		addZone(getNOM_EF_LIBELLE_CONTACT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TCONTACT_SELECT(), "0");
		addZone(getNOM_RG_CONTACT_DIFF(), getNOM_RB_CONTACT_DIFF_NON());
		TypeContact typeContact = (TypeContact) getListeTypeContact().get(0);
		initialiseContactPrioritaire(typeContact);

		// init du contact courant
		setContactCourant(null);
		setFocus(getNOM_PB_VALIDER_CONTACT());
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_LIEU_NAISSANCE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_LIEU_NAISSANCE() {
		return "NOM_PB_LIEU_NAISSANCE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/03/11 10:54:00)
	 * 
	 * 
	 */
	public boolean performPB_LIEU_NAISSANCE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_LIEU_NAISS, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_TCONTACT Date de création :
	 * (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_TCONTACT() {
		return "NOM_PB_TCONTACT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_TCONTACT(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 * RG_AG_EC_C05
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Contrôle des champs

		if (!performControlerSaisie(request, true)) {
			return false;
		}

		boolean creation = getAgentCourant() == null;

		// Alimentation de l'agent
		alimenterAgent(request);

		// on verifie que la clé RIB correspond bien
		if (!performControlerSaisieRIB(request)) {
			return false;
		}

		// RG : verif agent homonyme (même nom, prénom, date de Naissance)
		if (creation) {
			String dateNaiss = Services.formateDateInternationale(getVAL_EF_DATE_NAISSANCE());

			ArrayList<AgentNW> listAgent = AgentNW.listerAgentHomonyme(getTransaction(), getAgentCourant()
					.getNomUsage(), getAgentCourant().getPrenomUsage(), dateNaiss);
			listAgent.remove(getAgentCourant());
			if (listAgent.size() > 0) {
				VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AGT_HOMONYME, listAgent);
				VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getAgentCourant());
				setStatut(STATUT_AGT_HOMONYME, true);
				return true;
			}
		}

		// Création de l'agent
		ArrayList<Contact> lContact = getContactDao().listerContactAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()));
		SituationFamiliale situFam = getSituationFamilialeDao().chercherSituationFamilialeById(
				Integer.valueOf(getAgentCourant().getIdSituationFamiliale()));
		getAgentCourant().creerAgentNW(getTransaction(), lContact, situFam);
		if (getTransaction().isErreur()) {
			return false;
		}

		VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getAgentCourant());

		for (Contact contact : getListeContactAAjouter()) {
			contact.setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			getContactDao().creerContact(contact.getIdAgent(), contact.getIdTypeContact(), contact.getDescription(),
					contact.isDiffusable(), contact.isPrioritaire());
		}

		for (Contact contact : getListeContactAModifier()) {
			getContactDao().modifierContact(contact.getIdContact(), contact.getIdAgent(), contact.getIdTypeContact(),
					contact.getDescription(), contact.isDiffusable(), contact.isPrioritaire());
		}

		for (Contact contact : getListeContactASupprimer()) {
			getContactDao().supprimerContact(contact.getIdContact());
		}

		getListeContactAAjouter().clear();
		getListeContactAModifier().clear();
		getListeContactASupprimer().clear();

		commitTransaction();

		// si date de premiere embauche sup à la date du jour alors on affiche
		// un message d'information
		// La date de première entrée ne doit pas être postérieure à la date du
		// jour
		// RG_AG_EC_C05
		String messageDateEmbauche = Const.CHAINE_VIDE;
		if (Services.compareDates(getAgentCourant().getDatePremiereEmbauche(), Services.dateDuJour()) > 0) {
			messageDateEmbauche = "Attention, vous avez saisi une date d'embauche supérieure à la date du jour.";
		}

		if (creation) // "INF001","Agent @ créé"
		{
			setStatut(STATUT_PROCESS_APPELANT, false,
					MessageUtils.getMessage("INF001", getAgentCourant().getNoMatricule()) + " " + message + " "
							+ messageDateEmbauche);
		} else // "INF001","Agent @ modifié"
		{
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF002", getAgentCourant().getNoMatricule())
					+ " " + messageDateEmbauche);
		}

		return true;
	}

	/**
	 * Alimente l'objet Agent avec les champs de saisie du formulaire. Retourne
	 * true ou false Date de création : (11/04/11 15:15:00)
	 * 
	 * RG_AG_EC_C06 RG_AG_EC_C01
	 */
	private boolean alimenterAgent(HttpServletRequest request) throws Exception {

		if (getAgentCourant() == null) {
			setAgentCourant(new AgentNW());
		}

		getAgentCourant().setCivilite(getVAL_LB_CIVILITE_SELECT());
		getAgentCourant().setSexe(getVAL_ST_SEXE().substring(0, 1));
		getAgentCourant().setNomPatronymique(getVAL_EF_NOM_PATRONYMIQUE());
		getAgentCourant().setPrenom(getVAL_EF_PRENOM());
		getAgentCourant().setPrenomUsage(
				getVAL_EF_PRENOM_USAGE().length() == 0 ? getVAL_EF_PRENOM() : getVAL_EF_PRENOM_USAGE());

		/**
		 * FIX CLV https://redmine.ville-noumea.nc/issues/2424 *
		 */
		if (getVAL_EF_NOM_USAGE().length() == 0) {
			getAgentCourant().setNomUsage(getVAL_EF_NOM_PATRONYMIQUE());
		} else {
			getAgentCourant().setNomUsage(getVAL_EF_NOM_USAGE());
		}
		if ((getVAL_EF_NOM_MARITAL().length() == 0) && getVAL_LB_SITUATION_SELECT().equals("1")) {
			getAgentCourant().setNomMarital(getVAL_EF_NOM_PATRONYMIQUE());
		} else {
			getAgentCourant().setNomMarital(getVAL_EF_NOM_MARITAL());
		}
		/**
		 * FIN FIX CLV https://redmine.ville-noumea.nc/issues/2424 *
		 */
		int indiceCol = (Services.estNumerique(getVAL_LB_COLLECTIVITE_SELECT()) ? Integer
				.parseInt(getVAL_LB_COLLECTIVITE_SELECT()) : -1);
		getAgentCourant().setIdCollectivite(
				((Collectivite) getListeCollectivite().get(indiceCol)).getIdCollectivite().toString());

		int indiceSitu = (Services.estNumerique(getVAL_LB_SITUATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_SITUATION_SELECT()) : -1);
		getAgentCourant().setIdSituationFamiliale(
				((SituationFamiliale) getListeSituation().get(indiceSitu)).getIdSituation().toString());

		int indiceNation = (Services.estNumerique(getVAL_LB_NATIONALITE_SELECT()) ? Integer
				.parseInt(getVAL_LB_NATIONALITE_SELECT()) : -1);
		getAgentCourant().setNationalite(
				getLB_NATIONALITE()[indiceNation].length() == 0 ? null : getLB_NATIONALITE()[indiceNation].substring(0,
						1));

		getAgentCourant().setDateNaissance(getVAL_EF_DATE_NAISSANCE());

		if (getPaysNaissanceCourant() != null) {
			getAgentCourant().setCodePaysNaissanceEt(getPaysNaissanceCourant().getCodPays());
			getAgentCourant().setCodeCommuneNaissanceEt(
					((CommuneEtrangere) getCommNaissanceCourant()).getCodCommuneEtrangere());
		} else {
			getAgentCourant().setCodeCommuneNaissanceFr(((Commune) getCommNaissanceCourant()).getCodCommune());
		}

		getAgentCourant().setDatePremiereEmbauche(getVAL_EF_DATE_PREM_EMB());
		// RG_AG_EC_C06
		getAgentCourant().setDateDerniereEmbauche(
				getVAL_EF_DATE_DERN_EMB().equals(Const.CHAINE_VIDE) ? getVAL_EF_DATE_PREM_EMB()
						: getVAL_EF_DATE_DERN_EMB());
		getAgentCourant().setNumCarteSejour(getVAL_EF_NUM_CARTE_SEJOUR());
		getAgentCourant().setDateValiditeCarteSejour(getVAL_EF_DATE_VALIDITE_CARTE_SEJOUR());

		// Adresse
		if (getVAL_RG_VILLE_DOMICILE().equals(getNOM_RB_VILLE_DOMICILE_NOUMEA())) {
			// Adresse nouméa, voie obligatoire
			getAgentCourant()
					.setIdVoie(getVoieQuartierCourant() == null ? null : getVoieQuartierCourant().getCodVoie());
			getAgentCourant().setRueNonNoumea(null);
		} else {
			getAgentCourant().setIdVoie(null);
			getAgentCourant().setRueNonNoumea(getVAL_EF_RUE_NON_NOUMEA());
		}

		getAgentCourant().setNumRue(getVAL_EF_NUM_RUE());
		getAgentCourant().setNumRueBisTer(getVAL_EF_BIS_TER());
		getAgentCourant().setAdresseComplementaire(getVAL_EF_ADRESSE_COMPLEMENTAIRE());
		getAgentCourant().setCodePostalVilleDom(getCommuneDomCourant().getCodCodePostal());
		getAgentCourant().setCodeComVilleDom(getCommuneDomCourant().getCodCommune());
		getAgentCourant().setBp(getVAL_EF_BP());
		getAgentCourant().setCodePostalVilleBp(getVAL_EF_CODE_POSTAL_BP());
		getAgentCourant().setCodeComVilleBp(
				getCommuneBPCourant() != null ? getCommuneBPCourant().getCodCommune() : null);

		/**
		 * FIX CLV https://redmine.ville-noumea.nc/issues/2424 *
		 */
		// Si nom d'usage pas saisi, le renseigner avec le nom Marital, sinon le
		// nom patro
		// RG_AG_EC_C01
		// if (getVAL_EF_NOM_USAGE().length() == 0) {
		// String nomUsage = (getVAL_EF_NOM_MARITAL().length() != 0 ?
		// getVAL_EF_NOM_MARITAL() : getVAL_EF_NOM_PATRONYMIQUE());
		// getAgentCourant().setNomUsage(nomUsage);
		// }
		/**
		 * FIN FIX CLV https://redmine.ville-noumea.nc/issues/2424 *
		 */
		// /////////////////////////////////////////////////////////////////////////
		// ////////////////////// ALIMENTATION DU COMPTE
		// ///////////////////////////
		// /////////////////////////////////////////////////////////////////////////
		int indiceBanqueGuichet = (Services.estNumerique(getVAL_LB_BANQUE_GUICHET_SELECT()) ? Integer
				.parseInt(getVAL_LB_BANQUE_GUICHET_SELECT()) : -1);
		if (indiceBanqueGuichet != 0) {
			BanqueGuichet bg = (BanqueGuichet) getListeBanqueGuichet().get(indiceBanqueGuichet);
			getAgentCourant().setCodeGuichet(bg.getCodGuichet());
			getAgentCourant().setCodeBanque(bg.getCodBanque());

			getAgentCourant().setNumCompte(Services.lpad(getVAL_EF_NUM_COMPTE(), 11, "0"));
			getAgentCourant().setRib(getVAL_EF_RIB());
			getAgentCourant().setIntituleCompte(getVAL_EF_INTITULE_COMPTE());
		} else {
			getAgentCourant().setCodeGuichet(null);
			getAgentCourant().setCodeBanque(null);
			getAgentCourant().setNumCompte(null);
			getAgentCourant().setRib(null);
			getAgentCourant().setIntituleCompte(null);
		}

		// /////////////////////////////////////////////////////////////////////////
		// ////////////////////// ALIMENTATION DU SERVICE NATIONAL
		// /////////////////
		// /////////////////////////////////////////////////////////////////////////
		int indiceEtatService = (Services.estNumerique(getVAL_LB_TYPE_SERVICE_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_SERVICE_SELECT()) : -1);
		EtatServiceMilitaire aEtatService;
		if (indiceEtatService == 0) {
			getAgentCourant().setIdEtatService(null);
		} else {
			aEtatService = (EtatServiceMilitaire) getListeTypeServiceMilitaire().get(indiceEtatService - 1);
			getAgentCourant().setIdEtatService(aEtatService.getIdEtatService().toString());
		}
		getAgentCourant().setVcat(getVAL_RG_VCAT().equals(getNOM_RB_VCAT_OUI()) ? "O" : "N");
		getAgentCourant().setDebutService(getVAL_EF_SERVICE_DEBUT());
		getAgentCourant().setFinService(getVAL_EF_SERVICE_FIN());

		// /////////////////////////////////////////////////////////////////////////
		// ////////////////////// ALIMENTATION DES CHARGES
		// /////////////////////////
		// /////////////////////////////////////////////////////////////////////////
		getAgentCourant().setNumCafat(getVAL_EF_NUM_CAFAT());
		getAgentCourant().setNumRuamm(getVAL_EF_NUM_RUAMM());
		getAgentCourant().setNumMutuelle(getVAL_EF_NUM_MUTUELLE());
		getAgentCourant().setNumCre(getVAL_EF_NUM_CRE());
		getAgentCourant().setNumIrcafex(getVAL_EF_NUM_IRCAFEX());
		getAgentCourant().setNumClr(getVAL_EF_NUM_CLR());

		return true;
	}

	/**
	 * Contrôle les zones saisies Date de création : (17/03/03 11:01:57)
	 * 
	 * RG_AG_EC_C06 RG_AG_EC_C04 RG_AG_EC_C05 RG_AG_EC_C03 RG_AG_EC_C09
	 * RG_AG_EC_C02 RG_AG_EC_C10
	 */
	private boolean performControlerSaisie(HttpServletRequest request, boolean aValider) throws Exception {

		boolean result = true;

		// ***********************
		// Verification Etat civil
		// ***********************
		// Prénom
		if (getZone(getNOM_EF_PRENOM()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "prénom"));
			setFocus(getNOM_EF_PRENOM());
			return false;
		}

		// Nom patronymique
		if (getZone(getNOM_EF_NOM_PATRONYMIQUE()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nom patronymique"));
			setFocus(getNOM_EF_NOM_PATRONYMIQUE());
			return false;
		}

		// Date de naissance
		if (getZone(getNOM_EF_DATE_NAISSANCE()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de naissance"));
			setFocus(getNOM_EF_DATE_NAISSANCE());
			return false;
		}

		// Lieu de naissance
		if (getLieuNaissance() == null) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "lieu de naissance"));
			return false;
		}

		// Contrôle format date de naissance
		if (!Services.estUneDate(getZone(getNOM_EF_DATE_NAISSANCE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de naissance"));
			setFocus(getNOM_EF_DATE_NAISSANCE());
			result &= false;
		} else {
			addZone(getNOM_EF_DATE_NAISSANCE(), Services.formateDate(getVAL_EF_DATE_NAISSANCE()));
		}

		// Situation familiale
		if (getZone(getVAL_LB_SITUATION_SELECT()).equals("-1")) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "situation familiale"));
			result &= false;
		}

		// Nationalité
		if (getVAL_LB_NATIONALITE_SELECT().length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nationalité"));
			result &= false;
		}

		// Date de première embauche
		if (getVAL_EF_DATE_PREM_EMB().length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de première embauche"));
			setFocus(getNOM_EF_DATE_PREM_EMB());
			result &= false;
		}

		// Contrôle format date première embauche
		if (!Services.estUneDate(getVAL_EF_DATE_PREM_EMB())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de première embauche"));
			setFocus(getNOM_EF_DATE_PREM_EMB());
			result &= false;
		}

		// Contrôle format date dernière embauche
		// RG_AG_EC_C06
		if (getVAL_EF_DATE_DERN_EMB().length() != 0 && !Services.estUneDate(getVAL_EF_DATE_DERN_EMB())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de dernière embauche"));
			setFocus(getNOM_EF_DATE_DERN_EMB());
			result &= false;
		}

		// Contrôle format date validite carte de sejour
		if (getVAL_EF_DATE_VALIDITE_CARTE_SEJOUR().length() != 0
				&& !Services.estUneDate(getVAL_EF_DATE_VALIDITE_CARTE_SEJOUR())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de validité de la carte de séjour"));
			setFocus(getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR());
			result &= false;
		}

		// La date de naissance doit être antérieure à la date du jour
		// RG_AG_EC_C04
		if (Services.compareDates(getVAL_EF_DATE_NAISSANCE(), Services.dateDuJour()) >= 0) {
			// "ERR013","La date de naissance doit être antérieure à la date du jour"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR013"));
			result &= false;
		}

		// La date de première entrée doit être postérieure à la date de
		// naissance + 16 ans
		// RG_AG_EC_C05
		if (Services.compareDates(getVAL_EF_DATE_PREM_EMB(),
				Services.ajouteAnnee(Services.formateDate(getVAL_EF_DATE_NAISSANCE()), 16)) <= 0) {
			// "ERR015","La date d'embauche doit être postérieure à la date de naissance"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR015"));
			result &= false;
		}

		/**
		 * FIX CLV https://redmine.ville-noumea.nc/issues/2424 *
		 */
		// Si la désignation est M. le nom marital doit être à blanc
		// if (getVAL_LB_CIVILITE_SELECT().equals(EnumCivilite.M.getCode()) &&
		// getVAL_EF_NOM_MARITAL().length() > 0) {
		// "ERR016","Si la désignation est M, le nom marital doit être à blanc"
		// getTransaction().declarerErreur(MessageUtils.getMessage("ERR016"));
		// result &= false;
		// }
		// Si la désignation est Mme le nom marital doit renseigné
		// RG_AG_EC_C03
		// if (getVAL_LB_CIVILITE_SELECT().equals(EnumCivilite.MME.getCode()) &&
		// getVAL_EF_NOM_MARITAL().equals(Const.CHAINE_VIDE)) {
		// getTransaction().declarerErreur(MessageUtils.getMessage("ERR002",
		// "nom marital"));
		// result &= false;
		// }
		/**
		 * FIN FIX CLV https://redmine.ville-noumea.nc/issues/2424 *
		 */
		// ***************************
		// FIN Verification Etat civil
		// ***************************
		// *************************
		// Verification de service national
		// *************************
		// Contrôle format date debut service
		if (getVAL_EF_SERVICE_DEBUT().length() != 0 && !Services.estUneDate(getVAL_EF_SERVICE_DEBUT())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début de service"));
			setFocus(getNOM_EF_SERVICE_DEBUT());
			result &= false;
		}
		// Contrôle format date fin service
		if (getVAL_EF_SERVICE_FIN().length() != 0 && !Services.estUneDate(getVAL_EF_SERVICE_FIN())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin de service"));
			setFocus(getNOM_EF_SERVICE_FIN());
			result &= false;
		}
		// La date de début de service ne doit pas être postérieure à la date de
		// fin de service
		// RG_AG_EC_C09
		if (Services.compareDates(getVAL_EF_SERVICE_DEBUT(), getVAL_EF_SERVICE_FIN()) > 0) {
			// "ERR204", "La date @ doit être inférieure à la date @.");
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR204", "de début de service", " de fin de service"));
			result &= false;
		}

		// ***************************
		// FIN Verification Service National
		// ***************************

		// *************************
		// Verification de l'adresse
		// *************************
		// RG_AG_EC_C02
		if (getVAL_RG_VILLE_DOMICILE().equals(getNOM_RB_VILLE_DOMICILE_NOUMEA())) {
			if (getVAL_ST_VOIE().length() == 0) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Nom voie / rue"));
				result &= false;
			}
		} else {
			if (getVAL_EF_ADRESSE_COMPLEMENTAIRE().trim().length() == 0) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Adresse complémentaire"));
				result &= false;
			}
		}
		if (getVAL_LB_COMMUNE_DOM_SELECT().equals("0")) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Ville du domicile"));
			result &= false;
		}
		if (getVAL_EF_CODE_POSTAL_DOM().length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Code postal du domicile"));
			result &= false;
		} else if (!Services.estNumerique(getVAL_EF_CODE_POSTAL_DOM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Code postal du domicile"));
			result &= false;
		}
		if (getVAL_EF_CODE_POSTAL_BP().length() != 0 && !Services.estNumerique(getVAL_EF_CODE_POSTAL_DOM())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Code postal de la BP"));
			result &= false;
		}

		if (getVAL_EF_CODE_POSTAL_BP().length() != 0 && Integer.parseInt(getVAL_LB_COMMUNE_BP_SELECT()) > 0
				&& getVAL_EF_BP().length() == 0) {
			// "ERR019",
			// "La BP est obligatoire si le code postal et la ville de la boîte postale sont renseignés."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR019"));
			result &= false;
		}

		if (getVAL_EF_NUM_RUE().length() != 0) {
			if (!Services.estNumerique(getVAL_EF_NUM_RUE())) {
				// "ERR992", "La zone @ doit être numérique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "numéro de rue"));
				result &= false;
			}
		}

		// *****************************
		// FIN Verification de l'adresse
		// *****************************

		// **************************************
		// Vérification des coordonnées bancaires
		// **************************************
		// si au moins 1 champ rempli les autres sont obligatoires
		if (getVAL_EF_NUM_COMPTE().length() != 0 || getVAL_EF_RIB().length() != 0
				|| getVAL_EF_INTITULE_COMPTE().length() != 0) {
			// Code banque/Guichet obligatoire
			if (getVAL_LB_BANQUE_GUICHET_SELECT().equals("0")) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Code banque / Guichet"));
				setFocus(getNOM_LB_BANQUE_GUICHET());
				result &= false;
			}

			// N° compte obligatoire
			if (getVAL_EF_NUM_COMPTE().length() == 0 || getVAL_EF_RIB().length() == 0
					|| getVAL_EF_INTITULE_COMPTE().length() == 0) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR002", "N° de compte, RIB et Intitulé de compte"));
				setFocus(getNOM_EF_NUM_COMPTE());
				result &= false;
			}

		}
		if (!getVAL_LB_BANQUE_GUICHET_SELECT().equals("0")) {
			if (getVAL_EF_NUM_COMPTE().length() == 0 || getVAL_EF_RIB().length() == 0
					|| getVAL_EF_INTITULE_COMPTE().length() == 0) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR002", "N° de compte, RIB et Intitulé de compte"));
				setFocus(getNOM_EF_NUM_COMPTE());
				result &= false;
			}
		}

		// ******************************************
		// FIN Vérification des coordonnées bancaires
		// ******************************************

		// **************************************
		// Vérification n° CAFAT
		// **************************************

		if (getVAL_EF_NUM_CAFAT().length() != 0) {
			if (!Services.estNumerique(getVAL_EF_NUM_CAFAT())) {
				// "ERR992", "La zone @ doit être numérique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "numéro de cafat"));
				result &= false;
			}
			// **************************************
			// Vérification unicité n° CAFAT
			// **************************************

			AgentNW cafatAg = AgentNW.chercherCafat(getTransaction(), getVAL_EF_NUM_CAFAT(), getAgentCourant());
			if (result == true) {
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
			}

			if (cafatAg != null && cafatAg.getIdAgent() != null) {
				// "ERR997",
				// "Ce numéro de @ est déjà utilisé, il doit être unique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR997", "cafat"));
				result &= false;

			}

		}

		// **************************************
		// Vérification n° RUAM
		// **************************************

		if (getVAL_EF_NUM_RUAMM().length() != 0) {
			if (!Services.estNumerique(getVAL_EF_NUM_RUAMM())) {
				// "ERR992", "La zone @ doit être numérique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "numéro de ruamm"));
				result &= false;
			}

			// **************************************
			// Vérification unicité n° RUAM
			// **************************************

			AgentNW ruamAg = AgentNW.chercherRuam(getTransaction(), getVAL_EF_NUM_RUAMM(), getAgentCourant());
			if (result == true) {
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
			}

			if (ruamAg != null && ruamAg.getIdAgent() != null) {
				// "ERR997",
				// "Ce numéro de @ est déjà utilisé, il doit être unique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR997", "ruamm"));
				result &= false;
			}

		}

		// **************************************
		// Vérification unicité n° MUTUELLE
		// **************************************

		if (getVAL_EF_NUM_MUTUELLE().length() != 0) {

			AgentNW mutuelleAg = AgentNW
					.chercherMutuelle(getTransaction(), getVAL_EF_NUM_MUTUELLE(), getAgentCourant());
			if (result == true) {
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
			}

			if (mutuelleAg != null && mutuelleAg.getIdAgent() != null) {
				// "ERR997",
				// "Ce numéro de @ est déjà utilisé, il doit être unique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR997", "mutuelle"));
				result &= false;
			}

		}

		// **************************************
		// Vérification unicité n° CLR
		// **************************************

		if (getVAL_EF_NUM_CLR().length() != 0) {

			AgentNW clrAg = AgentNW.chercherClr(getTransaction(), getVAL_EF_NUM_CLR(), getAgentCourant());
			if (result == true) {
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
			}

			if (clrAg != null && clrAg.getIdAgent() != null) {
				// "ERR997",
				// "Ce numéro de @ est déjà utilisé, il doit être unique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR997", "CLR"));
				result &= false;
			}

		}

		// **************************************
		// Vérification unicité n° CRE
		// **************************************

		if (getVAL_EF_NUM_CRE().length() != 0) {

			AgentNW creAg = AgentNW.chercherCre(getTransaction(), getVAL_EF_NUM_CRE(), getAgentCourant());
			if (result == true) {
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
			}

			if (creAg != null && creAg.getIdAgent() != null) {
				// "ERR997",
				// "Ce numéro de @ est déjà utilisé, il doit être unique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR997", "CRE"));
				result &= false;
			}
		}

		// **************************************
		// Vérification unicité n° IRCAFEX
		// **************************************

		if (getVAL_EF_NUM_IRCAFEX().length() != 0) {

			AgentNW ircafexAg = AgentNW.chercherIrcafex(getTransaction(), getVAL_EF_NUM_IRCAFEX(), getAgentCourant());
			if (result == true) {
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
			}

			if (ircafexAg != null && ircafexAg.getIdAgent() != null) {
				// "ERR997",
				// "Ce numéro de @ est déjà utilisé, il doit être unique."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR997", "Ircafex"));
				result &= false;
			}
		}

		return result;

	}

	/**
	 * Contrôle les zones saisies pour un contact
	 * 
	 * @param request
	 * @return boolean
	 * @throws Exception
	 */
	private boolean performControlerSaisieContact(HttpServletRequest request) throws Exception {
		// Si lib contact non saisit
		if (getVAL_EF_LIBELLE_CONTACT().length() == 0) {
			// "ERR001","Le libellé @ est obligatoire"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR001", "du contact"));
			return false;
		} // Tous les types de contact doivent faire 6 caractères sauf Email
			// (jusqu'à 50)
		else {
			int indiceTypeContact = (Services.estNumerique(getVAL_LB_TCONTACT_SELECT()) ? Integer
					.parseInt(getVAL_LB_TCONTACT_SELECT()) : -1);
			if (indiceTypeContact == -1 || getListeTypeContact().size() == 0
					|| indiceTypeContact > getListeTypeContact().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Type contact"));
				return false;
			}
			if (indiceTypeContact >= 0) {
				TypeContact tc = (TypeContact) getListeTypeContact().get(indiceTypeContact);
				if ((!EnumTypeContact.EMAIL.getCode().toString().equals(tc.getIdTypeContact().toString()))
						&& getVAL_EF_LIBELLE_CONTACT().length() > 6) {
					// "ERR018",
					// "Les contacts Tel, Fax, Mobile, Mobile pro et ligne directe ne doivent pas dépasser 6 caractères. Seul l'Email peut faire jusqu'à 50 caractères."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR018"));
					return false;
				}
				if (EnumTypeContact.EMAIL.getCode().toString().equals(tc.getIdTypeContact().toString())
						&& Services.estNumerique(getVAL_EF_LIBELLE_CONTACT())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "contact"));
					return false;
				}
				if ((!EnumTypeContact.EMAIL.getCode().toString().equals(tc.getIdTypeContact().toString()))
						&& !Services.estNumerique(getVAL_EF_LIBELLE_CONTACT())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "contact"));
					return false;
				}
				if (EnumTypeContact.LIGNE_DIRECTE.getCode().toString().equals(tc.getIdTypeContact().toString())) {
					// une seule entrée possible pour ligne directe
					boolean dejaLigneDirecte = false;
					if (getListeContactAAjouter() != null) {
						for (int i = 0; i < getListeContactAAjouter().size(); i++) {
							Contact contactAAjouter = getListeContactAAjouter().get(i);
							if (contactAAjouter.getIdTypeContact().toString()
									.equals(EnumTypeContact.LIGNE_DIRECTE.getCode().toString())) {
								dejaLigneDirecte = true;
								break;
							}
						}
					}
					if (!dejaLigneDirecte) {
						ArrayList<Contact> contactExist = getContactDao().listerContactAgentAvecTypeContact(
								Integer.valueOf(getAgentCourant().getIdAgent()),
								EnumTypeContact.LIGNE_DIRECTE.getCode());
						if (contactExist != null && contactExist.size() > 0) {
							dejaLigneDirecte = true;
						}
					}
					if (dejaLigneDirecte) {
						// "ERR969",
						// "Il ne peut y avoir qu'un seul contact de type 'Ligne directe'.");
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR969"));
						return false;
					}

				}
			}
		}

		return true;
	}

	/**
	 * 
	 * @param request
	 * @return boolean
	 * @throws Exception
	 *             RG_AG_EC_C08
	 */
	private boolean performControlerSaisieRIB(HttpServletRequest request) throws Exception {
		// RG_AG_EC_C08
		boolean result = true;
		AgentNW a = getAgentCourant();
		boolean compteNumerique = false;
		if (a.getNumCompte() != null && !getVAL_EF_RIB().equals(Const.CHAINE_VIDE)) {
			// on regarde si le numero de compte est numerique ou non
			if (Services.estNumerique(a.getNumCompte())) {
				compteNumerique = true;
			}
			int res = calculCleRIB(a, compteNumerique);

			if (Integer.parseInt(getVAL_EF_RIB()) != res) {
				// "ERR017","La clé RIB est éronnée."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR017"));
				setFocus(getNOM_EF_CLE_RIB());
				result &= false;
			}
		} else {
			return true;
		}

		return result;
	}

	private int calculCleRIB(AgentNW a, boolean compteNumerique) {
		String numCompte = a.getNumCompte();
		// si n° compte numerique
		if (!compteNumerique) {
			numCompte = transformeNumCompteEnNumerique(numCompte);
		}

		int res = 0;
		// Calcul de la clé rib d'apres http://fr.wikipedia.org/wiki/Clé_RIB
		int b = Integer.parseInt(a.getCodeBanque());
		int g = Integer.parseInt(a.getCodeGuichet());
		int d = Integer.parseInt(numCompte.substring(0, 6));
		int c = Integer.parseInt(numCompte.substring(6, numCompte.length()));
		int calc = 89 * b + 15 * g + 76 * d + 3 * c;
		int modulo = calc % 97;
		res = 97 - modulo;
		return res;
	}

	private String transformeNumCompteEnNumerique(String numCompte) {
		String res = numCompte.toUpperCase();
		// Calcul de la clé rib d'apres http://fr.wikipedia.org/wiki/Clé_RIB
		res = res.replace("A", "1");
		res = res.replace("J", "1");
		res = res.replace("B", "2");
		res = res.replace("K", "2");
		res = res.replace("S", "2");
		res = res.replace("C", "3");
		res = res.replace("L", "3");
		res = res.replace("T", "3");
		res = res.replace("D", "4");
		res = res.replace("M", "4");
		res = res.replace("U", "4");
		res = res.replace("E", "5");
		res = res.replace("N", "5");
		res = res.replace("V", "5");
		res = res.replace("F", "6");
		res = res.replace("O", "6");
		res = res.replace("W", "6");
		res = res.replace("G", "7");
		res = res.replace("P", "7");
		res = res.replace("X", "7");
		res = res.replace("H", "8");
		res = res.replace("Q", "8");
		res = res.replace("Y", "8");
		res = res.replace("I", "9");
		res = res.replace("R", "9");
		res = res.replace("Z", "9");
		return res;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CONTACT Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_VALIDER_CONTACT() {
		return "NOM_PB_VALIDER_CONTACT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_VALIDER_CONTACT(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION_CONTACT()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION_CONTACT()).equals(ACTION_SUPPRESSION)) {

			if (getListeContactAAjouter().contains(getContactCourant())) {
				getListeContactAAjouter().remove(getContactCourant());
			} else {
				if (getListeContactAModifier().contains(getContactCourant())) {
					getListeContactAModifier().remove(getContactCourant());
				}
				getListeContactASupprimer().add(getContactCourant());
			}

			getListeContact().remove(getContactCourant());

			// Si Action Modification
		} else if (getZone(getNOM_ST_ACTION_CONTACT()).equals(ACTION_MODIFICATION)) {

			// Récup des zones saisies
			TypeContact newType = (TypeContact) getListeTypeContact().get(
					Integer.parseInt(getZone(getNOM_LB_TCONTACT_SELECT())));
			String newLibContact = getZone(getNOM_EF_LIBELLE_CONTACT());
			boolean diffusable = getZone(getNOM_RG_CONTACT_DIFF()).equals(getNOM_RB_CONTACT_DIFF_OUI()) ? true : false;
			boolean prioritaire = getZone(getNOM_RG_CONTACT_PRIORITAIRE()).equals(getNOM_RB_CONTACT_PRIORITAIRE_OUI()) ? true
					: false;

			if (performControlerSaisieContact(request)) {
				// Affectation des attributs
				getContactCourant().setDescription(newLibContact);
				getContactCourant().setIdTypeContact(newType.getIdTypeContact());
				getContactCourant().setDiffusable(diffusable);
				getContactCourant().setPrioritaire(prioritaire);

				if (!getListeContactAAjouter().contains(getContactCourant())) {
					getListeContactAModifier().add(getContactCourant());
				}
			}

			// Si Action Creation
		} else if (getZone(getNOM_ST_ACTION_CONTACT()).equals(ACTION_CREATION)) {

			// Récup des zones saisies
			TypeContact newType = (TypeContact) getListeTypeContact().get(
					Integer.parseInt(getZone(getNOM_LB_TCONTACT_SELECT())));
			String newLibContact = getZone(getNOM_EF_LIBELLE_CONTACT());
			boolean diffusable = getZone(getNOM_RG_CONTACT_DIFF()).equals(getNOM_RB_CONTACT_DIFF_OUI()) ? true : false;
			boolean prioritaire = getZone(getNOM_RG_CONTACT_PRIORITAIRE()).equals(getNOM_RB_CONTACT_PRIORITAIRE_OUI()) ? true
					: false;

			if (performControlerSaisieContact(request)) {
				setContactCourant(new Contact());

				// Affectation des attributs
				getContactCourant().setDescription(newLibContact);
				getContactCourant().setIdTypeContact(newType.getIdTypeContact());
				getContactCourant().setDiffusable(diffusable);
				getContactCourant().setPrioritaire(prioritaire);

				getListeContactAAjouter().add(getContactCourant());
				getListeContact().add(getContactCourant());
			}
		}

		afficheListeContact();

		addZone(getNOM_ST_ACTION_CONTACT(), Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VOIE Date de création :
	 * (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_VOIE() {
		return "NOM_PB_VOIE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_VOIE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_VOIE, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_CONTACT Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_ACTION_CONTACT() {
		return "NOM_ST_ACTION_CONTACT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_CONTACT
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_ACTION_CONTACT() {
		return getZone(getNOM_ST_ACTION_CONTACT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DERN_EMB Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_DATE_DERN_EMB() {
		return "NOM_ST_DATE_DERN_EMB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DERN_EMB
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_DATE_DERN_EMB() {
		return getZone(getNOM_ST_DATE_DERN_EMB());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_PREM_EMB Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_DATE_PREM_EMB() {
		return "NOM_ST_DATE_PREM_EMB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_PREM_EMB
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_DATE_PREM_EMB() {
		return getZone(getNOM_ST_DATE_PREM_EMB());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIBELLE_CONTACT Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_LIBELLE_CONTACT() {
		return "NOM_ST_LIBELLE_CONTACT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIBELLE_CONTACT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_LIBELLE_CONTACT() {
		return getZone(getNOM_ST_LIBELLE_CONTACT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PAYS_NAISSANCE Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_PAYS_NAISSANCE() {
		return "NOM_ST_PAYS_NAISSANCE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PAYS_NAISSANCE
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_PAYS_NAISSANCE() {
		return getZone(getNOM_ST_PAYS_NAISSANCE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PHOTO Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_PHOTO() {
		return "NOM_ST_PHOTO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PHOTO Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_PHOTO() {
		return getZone(getNOM_ST_PHOTO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TCONTACT Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_TCONTACT() {
		return "NOM_ST_TCONTACT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TCONTACT Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_TCONTACT() {
		return getZone(getNOM_ST_TCONTACT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * getNOM_ST_LIEU_NAISSANCE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_ST_LIEU_NAISSANCE() {
		return "NOM_ST_LIEU_NAISSANCE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIEU_NAISSANCE
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_ST_LIEU_NAISSANCE() {
		return getZone(getNOM_ST_LIEU_NAISSANCE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_ADRESSE_COMPLEMENTAIRE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_ADRESSE_COMPLEMENTAIRE() {
		return "NOM_EF_ADRESSE_COMPLEMENTAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ADRESSE_COMPLEMENTAIRE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_ADRESSE_COMPLEMENTAIRE() {
		return getZone(getNOM_EF_ADRESSE_COMPLEMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RUE_NON_NOUMEA Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_RUE_NON_NOUMEA() {
		return "NOM_EF_RUE_NON_NOUMEA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_RUE_NON_NOUMEA Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_RUE_NON_NOUMEA() {
		return getZone(getNOM_EF_RUE_NON_NOUMEA());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BP Date de création
	 * : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_BP() {
		return "NOM_EF_BP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_BP
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_BP() {
		return getZone(getNOM_EF_BP());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CLE_RIB Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_CLE_RIB() {
		return "NOM_EF_CLE_RIB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CLE_RIB Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_CLE_RIB() {
		return getZone(getNOM_EF_CLE_RIB());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_NAISSANCE Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_DATE_NAISSANCE() {
		return "NOM_EF_DATE_NAISSANCE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_NAISSANCE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_DATE_NAISSANCE() {
		return getZone(getNOM_EF_DATE_NAISSANCE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_INTITULE_COMPTE
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_INTITULE_COMPTE() {
		return "NOM_EF_INTITULE_COMPTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_INTITULE_COMPTE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_INTITULE_COMPTE() {
		return getZone(getNOM_EF_INTITULE_COMPTE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIBELLE_CONTACT
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_LIBELLE_CONTACT() {
		return "NOM_EF_LIBELLE_CONTACT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIBELLE_CONTACT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_LIBELLE_CONTACT() {
		return getZone(getNOM_EF_LIBELLE_CONTACT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_MARITAL Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_NOM_MARITAL() {
		return "NOM_EF_NOM_MARITAL";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_MARITAL Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_NOM_MARITAL() {
		return getZone(getNOM_EF_NOM_MARITAL());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_PATRONYMIQUE
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_NOM_PATRONYMIQUE() {
		return "NOM_EF_NOM_PATRONYMIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_PATRONYMIQUE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_NOM_PATRONYMIQUE() {
		return getZone(getNOM_EF_NOM_PATRONYMIQUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_USAGE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_NOM_USAGE() {
		return "NOM_EF_NOM_USAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_USAGE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_NOM_USAGE() {
		return getZone(getNOM_EF_NOM_USAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_CARTE_SEJOUR
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_NUM_CARTE_SEJOUR() {
		return "NOM_EF_NUM_CARTE_SEJOUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_CARTE_SEJOUR Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_NUM_CARTE_SEJOUR() {
		return getZone(getNOM_EF_NUM_CARTE_SEJOUR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_COMPTE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_NUM_COMPTE() {
		return "NOM_EF_NUM_COMPTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_COMPTE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_NUM_COMPTE() {
		return getZone(getNOM_EF_NUM_COMPTE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_RUE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_NUM_RUE() {
		return "NOM_EF_NUM_RUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_RUE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_NUM_RUE() {
		return getZone(getNOM_EF_NUM_RUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_PRENOM() {
		return "NOM_EF_PRENOM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_PRENOM() {
		return getZone(getNOM_EF_PRENOM());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE_DEBUT Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_SERVICE_DEBUT() {
		return "NOM_EF_SERVICE_DEBUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE_DEBUT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_SERVICE_DEBUT() {
		return getZone(getNOM_EF_SERVICE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE_FIN Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_EF_SERVICE_FIN() {
		return "NOM_EF_SERVICE_FIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE_FIN Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_EF_SERVICE_FIN() {
		return getZone(getNOM_EF_SERVICE_FIN());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BANQUE_GUICHET Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private String[] getLB_BANQUE_GUICHET() {
		if (LB_BANQUE_GUICHET == null) {
			LB_BANQUE_GUICHET = initialiseLazyLB();
		}
		return LB_BANQUE_GUICHET;
	}

	/**
	 * Setter de la liste: LB_BANQUE_GUICHET Date de création : (15/03/11
	 * 10:49:55)
	 * 
	 * 
	 */
	private void setLB_BANQUE_GUICHET(String[] newLB_BANQUE_GUICHET) {
		LB_BANQUE_GUICHET = newLB_BANQUE_GUICHET;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BANQUE_GUICHET Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_BANQUE_GUICHET() {
		return "NOM_LB_BANQUE_GUICHET";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BANQUE_GUICHET_SELECT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_BANQUE_GUICHET_SELECT() {
		return "NOM_LB_BANQUE_GUICHET_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BANQUE_GUICHET Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String[] getVAL_LB_BANQUE_GUICHET() {
		return getLB_BANQUE_GUICHET();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_BANQUE_GUICHET Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_LB_BANQUE_GUICHET_SELECT() {
		return getZone(getNOM_LB_BANQUE_GUICHET_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATIONALITE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private String[] getLB_NATIONALITE() {
		if (LB_NATIONALITE == null) {
			LB_NATIONALITE = initialiseLazyLB();
		}
		return LB_NATIONALITE;
	}

	/**
	 * Setter de la liste: LB_NATIONALITE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private void setLB_NATIONALITE(String[] newLB_NATIONALITE) {
		LB_NATIONALITE = newLB_NATIONALITE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATIONALITE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_NATIONALITE() {
		return "NOM_LB_NATIONALITE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NATIONALITE_SELECT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_NATIONALITE_SELECT() {
		return "NOM_LB_NATIONALITE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NATIONALITE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String[] getVAL_LB_NATIONALITE() {
		return getLB_NATIONALITE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NATIONALITE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_LB_NATIONALITE_SELECT() {
		return getZone(getNOM_LB_NATIONALITE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_SITUATION Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private String[] getLB_SITUATION() {
		if (LB_SITUATION == null) {
			LB_SITUATION = initialiseLazyLB();
		}
		return LB_SITUATION;
	}

	/**
	 * Setter de la liste: LB_SITUATION Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private void setLB_SITUATION(String[] newLB_SITUATION) {
		LB_SITUATION = newLB_SITUATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_SITUATION Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_SITUATION() {
		return "NOM_LB_SITUATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_SITUATION_SELECT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_SITUATION_SELECT() {
		return "NOM_LB_SITUATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_SITUATION Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String[] getVAL_LB_SITUATION() {
		return getLB_SITUATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_SITUATION Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_LB_SITUATION_SELECT() {
		return getZone(getNOM_LB_SITUATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TCONTACT Date de création
	 * : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private String[] getLB_TCONTACT() {
		if (LB_TCONTACT == null) {
			LB_TCONTACT = initialiseLazyLB();
		}
		return LB_TCONTACT;
	}

	/**
	 * Setter de la liste: LB_TCONTACT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private void setLB_TCONTACT(String[] newLB_TCONTACT) {
		LB_TCONTACT = newLB_TCONTACT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TCONTACT Date de création
	 * : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_TCONTACT() {
		return "NOM_LB_TCONTACT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TCONTACT_SELECT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_TCONTACT_SELECT() {
		return "NOM_LB_TCONTACT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TCONTACT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String[] getVAL_LB_TCONTACT() {
		return getLB_TCONTACT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TCONTACT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_LB_TCONTACT_SELECT() {
		return getZone(getNOM_LB_TCONTACT_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_SERVICE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	private String[] getLB_TYPE_SERVICE() {
		if (LB_TYPE_SERVICE == null) {
			LB_TYPE_SERVICE = initialiseLazyLB();
		}
		return LB_TYPE_SERVICE;
	}

	/**
	 * Setter de la liste: LB_TYPE_SERVICE Date de création : (15/03/11
	 * 10:49:55)
	 * 
	 * 
	 */
	private void setLB_TYPE_SERVICE(String[] newLB_TYPE_SERVICE) {
		LB_TYPE_SERVICE = newLB_TYPE_SERVICE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_SERVICE Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_TYPE_SERVICE() {
		return "NOM_LB_TYPE_SERVICE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_SERVICE_SELECT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_LB_TYPE_SERVICE_SELECT() {
		return "NOM_LB_TYPE_SERVICE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_SERVICE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String[] getVAL_LB_TYPE_SERVICE() {
		return getLB_TYPE_SERVICE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_SERVICE Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_LB_TYPE_SERVICE_SELECT() {
		return getZone(getNOM_LB_TYPE_SERVICE_SELECT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_PAYS_NAISS Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_RG_PAYS_NAISS() {
		return "NOM_RG_PAYS_NAISS";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_PAYS_NAISS Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_RG_PAYS_NAISS() {
		return getZone(getNOM_RG_PAYS_NAISS());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TYPE_CONTRAT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_RG_TYPE_CONTRAT() {
		return "NOM_RG_TYPE_CONTRAT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TYPE_CONTRAT Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_RG_TYPE_CONTRAT() {
		return getZone(getNOM_RG_TYPE_CONTRAT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_VCAT
	 * Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_RG_VCAT() {
		return "NOM_RG_VCAT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_VCAT Date
	 * de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getVAL_RG_VCAT() {
		return getZone(getNOM_RG_VCAT());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VCAT_NON Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_RB_VCAT_NON() {
		return "NOM_RB_VCAT_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VCAT_OUI Date de
	 * création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_RB_VCAT_OUI() {
		return "NOM_RB_VCAT_OUI";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (15/03/11 15:30:49)
	 * 
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (15/03/11 15:30:49)
	 * 
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (16/03/11 09:31:43)
	 * 
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		initialiseListeDeroulante();

		setModeCreation(Const.MODE_CREATION_AGENT.equals(request.getParameter("ACTIVITE")));
		if (isModeCreation()) {
			VariableGlobale.enlever(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			videZonesDeSaisie(request);
			addZone(getNOM_RG_VILLE_DOMICILE(), getNOM_RB_VILLE_DOMICILE_NOUMEA());
			performPB_ADRESSE(request);
		}

		if (etatStatut() == STATUT_LIEU_NAISS) {
			setPaysNaissanceCourant((Pays) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_PAYS));
			if (getPaysNaissanceCourant() != null) {
				setCommNaissanceCourant((CommuneEtrangere) VariablesActivite.recuperer(this,
						VariablesActivite.ACTIVITE_COMMUNE_ET));
				setLieuNaissance(((CommuneEtrangere) getCommNaissanceCourant()).getLibCommuneEtrangere() + " - "
						+ getPaysNaissanceCourant().getLibPays());
			} else {
				setCommNaissanceCourant((Commune) VariablesActivite.recuperer(this,
						VariablesActivite.ACTIVITE_COMMUNE_FR));
				if (getCommNaissanceCourant() != null) {
					setLieuNaissance(((Commune) getCommNaissanceCourant()).getLibCommune() + " - "
							+ Const.COMMUNE_FRANCE);
				}
			}

			addZone(getNOM_ST_LIEU_NAISSANCE(), getLieuNaissance() == null ? Const.CHAINE_VIDE : getLieuNaissance());
			return;
		}

		if (etatStatut() == STATUT_VOIE) {
			setVoieQuartierCourant((VoieQuartier) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_VOIE_QUARTIER));
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_VOIE_QUARTIER);
			addZone(getNOM_ST_VOIE(), getVoieQuartierCourant() == null ? Const.CHAINE_VIDE : getVoieQuartierCourant()
					.getLibVoie());
			return;
		}

		if (etatStatut() == STATUT_AGT_HOMONYME) {
			setAgentCourant(null);
			setLieuNaissance(null);
			setPaysNaissanceCourant(null);
			setCommNaissanceCourant(null);
			setVoieQuartierCourant(null);
			addZone(getNOM_RG_VILLE_DOMICILE(), Const.CHAINE_VIDE);
			setCommuneDomCourant(null);
			setCommuneBPCourant(null);
		}

		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {

			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);

			setAgentCourant(aAgent);

			if (getAgentCourant() != null) {
				viderAdresse();
				// AGENT EN MODIFICATION
				if (getVAL_RG_VILLE_DOMICILE().equals(Const.CHAINE_VIDE)) {
					if (getAgentCourant().getIdVoie() != null) {
						addZone(getNOM_RG_VILLE_DOMICILE(), getNOM_RB_VILLE_DOMICILE_NOUMEA());
					} else {
						addZone(getNOM_RG_VILLE_DOMICILE(), getNOM_RB_VILLE_DOMICILE_AUTRE());
					}
				}

				initialiseListeContact(request);

				initialiseZonesAgentCourant(request);

				// Initialisation du lieu de naissance
				setLieuNaissance(null);
				initialiseZonesLieuNaissance(request);

				// Initialisation Voie/Quartier
				initialiseZonesVoieQuartier(request);

				initialiseZonesCommune(request);

			} else {
				// AGENT EN CREATION
				// Init de la liste des villes de l'adresse (par défaut, nouméa)
				if (getVAL_RG_VILLE_DOMICILE().equals(Const.CHAINE_VIDE)) {
					addZone(getNOM_RG_VILLE_DOMICILE(), getNOM_RB_VILLE_DOMICILE_NOUMEA());
					int[] tailles = { 5, 12 };
					String[] champs = { "codCodePostal", "libCommune" };
					setLB_COMMUNE_DOM(new FormateListe(tailles, getListeCommuneDomNoumea(), champs).getListeFormatee());
					// si pas de ville choisie on met Noumea par defaut
					if (getZone(getVAL_EF_CODE_POSTAL_DOM()).equals(Const.CHAINE_VIDE)) {
						CommunePostal newCommune = (CommunePostal) getListeCommuneDomNoumea().get(1);
						setCommuneDomCourant(newCommune);
						addZone(getNOM_EF_CODE_POSTAL_DOM(), getCommuneDomCourant().getCodCodePostal());
						addZone(getNOM_LB_COMMUNE_DOM_SELECT(), "1");
					}

				}
			}

		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getCollectiviteDao() == null) {
			setCollectiviteDao(new CollectiviteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getEtatServiceMilitaireDao() == null) {
			setEtatServiceMilitaireDao(new EtatServiceMilitaireDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getContactDao() == null) {
			setContactDao(new ContactDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeContactDao() == null) {
			setTypeContactDao(new TypeContactDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSituationFamilialeDao() == null) {
			setSituationFamilialeDao(new SituationFamilialeDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_PHOTO(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_CIVILITE_SELECT(), Const.ZERO);
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SEXE(), EnumSexe.MASCULIN.getValue());
		addZone(getNOM_EF_NOM_PATRONYMIQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOM_MARITAL(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOM_USAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PRENOM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PRENOM_USAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_SITUATION_SELECT(), Const.ZERO);
		// Init à "Mairie"
		int indiceCol = 0;
		for (int i = 0; i < getListeCollectivite().size(); i++) {
			if (((Collectivite) getListeCollectivite().get(i)).getCodeCollectivite().equals(
					EnumCollectivite.MAIRIE.getCode())) {
				indiceCol = i;
				break;
			}
		}
		addZone(getNOM_LB_COLLECTIVITE_SELECT(), String.valueOf(indiceCol));
		addZone(getNOM_LB_NATIONALITE_SELECT(), Const.ZERO);
		addZone(getNOM_ST_LIEU_NAISSANCE(), Const.CHAINE_VIDE);

		addZone(getNOM_EF_DATE_NAISSANCE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_PREM_EMB(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DERN_EMB(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_CARTE_SEJOUR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_RUE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BIS_TER(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ADRESSE_COMPLEMENTAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RUE_NON_NOUMEA(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BP(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_BANQUE_GUICHET_SELECT(), Const.ZERO);
		addZone(getNOM_ST_BANQUE_GUICHET(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_COMPTE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RIB(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_INTITULE_COMPTE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_SERVICE_SELECT(), Const.ZERO);
		addZone(getNOM_RG_VCAT(), getNOM_RB_VCAT_NON());
		addZone(getNOM_EF_SERVICE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_CAFAT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_RUAMM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_MUTUELLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_CRE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_IRCAFEX(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_CLR(), Const.CHAINE_VIDE);

	}

	/**
	 * Initialisation des liste déroulantes de l'écran Etat civil. RG_AG_EC_A02
	 * RG_AG_EC_A01 RG_AG_EC_A03 RG_AG_EC_A04
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste civilité vide alors affectation
		// RG_AG_EC_A02
		if (getLB_CIVILITE() == LBVide) {
			setLB_CIVILITE(EnumCivilite.getValues());
			addZone(getNOM_LB_CIVILITE_SELECT(), "0");
			addZone(getNOM_ST_SEXE(), EnumSexe.MASCULIN.getValue());
		}

		// Si liste collectivité vide alors affectation
		// RG_AG_EC_A01
		if (getLB_COLLECTIVITE() == LBVide) {
			ArrayList<Collectivite> col = (ArrayList<Collectivite>) getCollectiviteDao().listerCollectivite();
			setListeCollectivite(col);

			int[] tailles = { 40 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Collectivite> list = getListeCollectivite().listIterator(); list.hasNext();) {
				Collectivite fili = (Collectivite) list.next();
				String ligne[] = { fili.getLibLongCollectivite() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_COLLECTIVITE(aFormat.getListeFormatee());

			// Init à "Mairie"
			int indiceCol = 0;
			for (int i = 0; i < getListeCollectivite().size(); i++) {
				if (((Collectivite) getListeCollectivite().get(i)).getCodeCollectivite().equals(
						EnumCollectivite.MAIRIE.getCode())) {
					indiceCol = i;
					break;
				}
			}
			addZone(getNOM_LB_COLLECTIVITE_SELECT(), String.valueOf(indiceCol));
		}

		// Si liste situation vide alors affectation
		// RG_AG_EC_A03
		if (getLB_SITUATION() == LBVide) {
			ArrayList<SituationFamiliale> sf = (ArrayList<SituationFamiliale>) getSituationFamilialeDao()
					.listerSituationFamiliale();
			setListeSituation(sf);

			int[] tailles = { 12 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<SituationFamiliale> list = getListeSituation().listIterator(); list.hasNext();) {
				SituationFamiliale fili = (SituationFamiliale) list.next();
				String ligne[] = { fili.getLibSituation() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_SITUATION(aFormat.getListeFormatee());
		}

		// Si liste nationalité vide alors affectation
		// RG_AG_EC_A04
		if (getLB_NATIONALITE() == LBVide) {
			listeNationalite = new String[2];
			listeNationalite[0] = EnumNationalite.getValues()[0];
			listeNationalite[1] = EnumNationalite.getValues()[1];
			setLB_NATIONALITE(listeNationalite);
		}

		// Si liste commune domicile vide alors affectation
		if (getLB_COMMUNE_DOM() == LBVide) {
			ArrayList<CommunePostal> cpn = CommunePostal.listerCommunePostalNoumea(getTransaction());
			ArrayList<CommunePostal> cpa = CommunePostal.listerCommunePostalHorsNoumea(getTransaction());

			CommunePostal commVide = new CommunePostal();
			cpn.add(0, commVide);
			cpa.add(0, commVide);
			setListeCommuneDomNoumea(cpn);
			setListeCommuneDomAutre(cpa);
		}

		// Si liste commune BP vide alors affectation
		if (getLB_COMMUNE_BP() == LBVide) {
			ArrayList<CommunePostal> a = CommunePostal.listerCommunePostalAvecCodCommuneCommencant(getTransaction(),
					"988");
			CommunePostal commVide = new CommunePostal();
			a.add(0, commVide);
			setListeCommuneBP(a);

			int[] tailles = { 5, 12 };
			String[] champs = { "codCodePostal", "libCommune" };
			setLB_COMMUNE_BP(new FormateListe(tailles, a, champs).getListeFormatee());
		}

		// Si commune domicile vide
		if (getCommuneDomCourant() == null) {
			addZone(getNOM_LB_COMMUNE_DOM_SELECT(), "0");
			addZone(getNOM_EF_CODE_POSTAL_DOM(), null);
		}

		// Si commune BP vide
		if (getCommuneBPCourant() == null) {
			addZone(getNOM_LB_COMMUNE_BP_SELECT(), "0");
			addZone(getNOM_EF_CODE_POSTAL_BP(), null);
		}

		initialiseZonesBanque();
		initialiseZonesService();
		initialiseListeTypeContact();
	}

	/**
	 * Initialisation des zones en fonction de l'agent courant
	 */
	private void initialiseZonesAgentCourant(HttpServletRequest request) throws Exception {
		// Si pas d'agent, on se casse
		if (getAgentCourant() == null) {
			return;
		}

		// //////////////////////////////////////////////////////////////////////////////////////
		// zones de l'agent directement
		// //////////////////////////////////////////////////////////////////////////////////////
		addZone(getNOM_ST_PHOTO(), Const.CHAINE_VIDE);
		try {
			Document doc = getDocumentDao().chercherDocumentParTypeEtAgent("PHO",
					Integer.valueOf(getAgentCourant().getIdAgent()));
			String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			if (doc != null) {
				if (new File(repPartage + doc.getLienDocument()).exists()) {
					String repPartageLecture = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
					addZone(getNOM_ST_PHOTO(), repPartageLecture + doc.getLienDocument());
				} else {
					addZone(getNOM_ST_PHOTO(), Const.CHAINE_VIDE);
				}
			}
		} catch (Exception e) {
			addZone(getNOM_ST_PHOTO(), Const.CHAINE_VIDE);
		}

		addZone(getNOM_LB_CIVILITE_SELECT(), getAgentCourant().getCivilite());

		addZone(getNOM_ST_AGENT(), getAgentCourant().getNoMatricule() + " " + getAgentCourant().getNomAgent() + " "
				+ getAgentCourant().getPrenomAgent());
		addZone(getNOM_ST_SEXE(),
				(getAgentCourant().getCivilite().equals(EnumCivilite.M.getCode()) ? EnumSexe.MASCULIN.getValue()
						: EnumSexe.FEMININ.getValue()));
		addZone(getNOM_EF_NOM_PATRONYMIQUE(), getAgentCourant().getNomPatronymique());
		addZone(getNOM_EF_NOM_MARITAL(), getAgentCourant().getNomMarital());
		addZone(getNOM_EF_NOM_USAGE(), getAgentCourant().getNomUsage());
		addZone(getNOM_EF_PRENOM(), getAgentCourant().getPrenom());
		addZone(getNOM_EF_PRENOM_USAGE(), getAgentCourant().getPrenomUsage());

		// Situation familiale
		int indiceSitu = 0;
		for (int i = 0; i < getListeSituation().size(); i++) {
			if (((SituationFamiliale) getListeSituation().get(i)).getIdSituation().toString()
					.equals(getAgentCourant().getIdSituationFamiliale())) {
				indiceSitu = i;
				break;
			}
		}
		addZone(getNOM_LB_SITUATION_SELECT(), String.valueOf(indiceSitu));

		// Collectivite
		int indiceCol = 0;
		for (int i = 0; i < getListeCollectivite().size(); i++) {
			if (((Collectivite) getListeCollectivite().get(i)).getIdCollectivite().toString()
					.equals(getAgentCourant().getIdCollectivite())) {
				indiceCol = i;
				break;
			}
		}
		addZone(getNOM_LB_COLLECTIVITE_SELECT(), String.valueOf(indiceCol));

		// Nationalité
		int indiceNat = 0;
		for (int i = 0; i < getListeNationalite().length; i++) {
			if (getListeNationalite()[i].length() > 0) {
				if (getListeNationalite()[i].substring(0, 1).equals(getAgentCourant().getNationalite())) {
					indiceNat = i;
					break;
				}
			}
		}
		addZone(getNOM_LB_NATIONALITE_SELECT(), String.valueOf(indiceNat));

		addZone(getNOM_EF_DATE_NAISSANCE(), (getAgentCourant().getDateNaissance() == null || getAgentCourant()
				.getDateNaissance().equals(Const.DATE_NULL)) ? Const.CHAINE_VIDE : getAgentCourant().getDateNaissance());
		addZone(getNOM_EF_DATE_PREM_EMB(), (getAgentCourant().getDatePremiereEmbauche() == null || getAgentCourant()
				.getDatePremiereEmbauche().equals(Const.DATE_NULL)) ? Const.CHAINE_VIDE : getAgentCourant()
				.getDatePremiereEmbauche());
		addZone(getNOM_EF_DATE_DERN_EMB(), (getAgentCourant().getDateDerniereEmbauche() == null || getAgentCourant()
				.getDateDerniereEmbauche().equals(Const.DATE_NULL)) ? Const.CHAINE_VIDE : getAgentCourant()
				.getDateDerniereEmbauche());
		addZone(getNOM_EF_NUM_CARTE_SEJOUR(), getAgentCourant().getNumCarteSejour());
		addZone(getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR(), getAgentCourant().getDateValiditeCarteSejour());

		// //////////////////////////////////////////////////////////////////////////////////////
		// Zones de l'adresse
		// //////////////////////////////////////////////////////////////////////////////////////
		addZone(getNOM_EF_NUM_RUE(), getAgentCourant().getNumRue());
		addZone(getNOM_EF_BIS_TER(), getAgentCourant().getNumRueBisTer());
		addZone(getNOM_EF_ADRESSE_COMPLEMENTAIRE(), getAgentCourant().getAdresseComplementaire());
		addZone(getNOM_EF_RUE_NON_NOUMEA(), getAgentCourant().getRueNonNoumea());
		addZone(getNOM_EF_BP(), getAgentCourant().getBp());

		// //////////////////////////////////////////////////////////////////////////////////////
		// Zones de la banque
		// //////////////////////////////////////////////////////////////////////////////////////
		for (int i = 0; i < getListeBanqueGuichet().size(); i++) {
			BanqueGuichet bg = (BanqueGuichet) getListeBanqueGuichet().get(i);
			if (bg != null && bg.getCodBanque().equals(getAgentCourant().getCodeBanque())
					&& bg.getCodGuichet().equals(getAgentCourant().getCodeGuichet())) {
				addZone(getNOM_LB_BANQUE_GUICHET_SELECT(), String.valueOf(i));
				addZone(getNOM_ST_BANQUE_GUICHET(),
						bg == null ? Const.CHAINE_VIDE : bg.getLibBanque() + " - " + bg.getLibGuichet());
				break;
			}
		}

		addZone(getNOM_EF_NUM_COMPTE(), getAgentCourant().getNumCompte() == null ? Const.CHAINE_VIDE
				: getAgentCourant().getNumCompte());
		addZone(getNOM_EF_RIB(), getAgentCourant().getRib() == null ? Const.CHAINE_VIDE : getAgentCourant().getRib());
		addZone(getNOM_EF_INTITULE_COMPTE(), getAgentCourant().getIntituleCompte() == null ? Const.CHAINE_VIDE
				: getAgentCourant().getIntituleCompte());

		// //////////////////////////////////////////////////////////////////////////////////////
		// Zones du service militaire
		// //////////////////////////////////////////////////////////////////////////////////////
		if (getAgentCourant().getIdEtatService() == null) {
			addZone(getNOM_LB_TYPE_SERVICE_SELECT(), "0");
		} else {
			for (int i = 0; i < getListeTypeServiceMilitaire().size(); i++) {
				if (((EtatServiceMilitaire) getListeTypeServiceMilitaire().get(i)).getIdEtatService().toString()
						.equals(getAgentCourant().getIdEtatService())) {
					addZone(getNOM_LB_TYPE_SERVICE_SELECT(), String.valueOf(i + 1));
					break;
				}
			}
		}
		addZone(getNOM_RG_VCAT(),
				(getAgentCourant().getVcat() != null && getAgentCourant().getVcat().equals("O")) ? getNOM_RB_VCAT_OUI()
						: getNOM_RB_VCAT_NON());
		addZone(getNOM_EF_SERVICE_DEBUT(), getAgentCourant().getDebutService() == null ? Const.CHAINE_VIDE
				: getAgentCourant().getDebutService());
		addZone(getNOM_EF_SERVICE_FIN(), getAgentCourant().getFinService() == null ? Const.CHAINE_VIDE
				: getAgentCourant().getFinService());

		// //////////////////////////////////////////////////////////////////////////////////////
		// Zones Couverture
		// //////////////////////////////////////////////////////////////////////////////////////
		addZone(getNOM_EF_NUM_CAFAT(), getAgentCourant().getNumCafat() == null ? Const.CHAINE_VIDE : getAgentCourant()
				.getNumCafat());
		addZone(getNOM_EF_NUM_RUAMM(), getAgentCourant().getNumRuamm() == null ? Const.CHAINE_VIDE : getAgentCourant()
				.getNumRuamm());
		addZone(getNOM_EF_NUM_MUTUELLE(), getAgentCourant().getNumMutuelle() == null ? Const.CHAINE_VIDE
				: getAgentCourant().getNumMutuelle());
		addZone(getNOM_EF_NUM_CRE(), getAgentCourant().getNumCre() == null ? Const.CHAINE_VIDE : getAgentCourant()
				.getNumCre());
		addZone(getNOM_EF_NUM_IRCAFEX(), getAgentCourant().getNumIrcafex() == null ? Const.CHAINE_VIDE
				: getAgentCourant().getNumIrcafex());
		addZone(getNOM_EF_NUM_CLR(), getAgentCourant().getNumClr() == null ? Const.CHAINE_VIDE : getAgentCourant()
				.getNumClr());
	}

	/**
	 * Initialisation banque-guichet RG_AG_EC_A05
	 */
	private void initialiseZonesBanque() throws Exception {
		// si liste des banques/guichets vide alors
		// RG_AG_EC_A05
		if (getLB_BANQUE_GUICHET() == LBVide) {
			ArrayList<BanqueGuichet> a = BanqueGuichet.listerBanqueGuichet(getTransaction());
			a.add(0, null);
			setListeBanqueGuichet(a);
			int tailles[] = { 5, 1, 5 };
			FormateListe aListeFormatee = new FormateListe(tailles);
			for (int i = 0; i < a.size(); i++) {
				BanqueGuichet b = (BanqueGuichet) a.get(i);
				if (b == null) {
					String colonnes[] = { Const.CHAINE_VIDE, Const.CHAINE_VIDE, Const.CHAINE_VIDE };
					aListeFormatee.ajouteLigne(colonnes);
				} else {
					String colonnes[] = { Services.lpad(b.getCodBanque(), 5, "0"), "/",
							Services.lpad(b.getCodGuichet(), 5, "0") };
					aListeFormatee.ajouteLigne(colonnes);
				}
			}
			setLB_BANQUE_GUICHET(aListeFormatee.getListeFormatee());
			addZone(getNOM_ST_BANQUE_GUICHET(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Initialisation du lieu de naissance
	 */
	private void initialiseZonesLieuNaissance(HttpServletRequest request) throws Exception {
		// recup du lieu de naissance
		if (getLieuNaissance() == null && getAgentCourant() != null) {
			if (getAgentCourant().getCodePaysNaissanceEt() == null) {
				setCommNaissanceCourant(Commune.chercherCommune(getTransaction(), getAgentCourant()
						.getCodeCommuneNaissanceFr()));
				setLieuNaissance(((Commune) getCommNaissanceCourant()).getLibCommune() + " - " + Const.COMMUNE_FRANCE);
			} else {
				setPaysNaissanceCourant(Pays.chercherPays(getTransaction(), getAgentCourant().getCodePaysNaissanceEt()));
				setCommNaissanceCourant(CommuneEtrangere.chercherCommuneEtrangere(getTransaction(),
						getPaysNaissanceCourant().getCodPays(), getAgentCourant().getCodeCommuneNaissanceEt()));
				setLieuNaissance(((CommuneEtrangere) getCommNaissanceCourant()).getLibCommuneEtrangere() + " - "
						+ getPaysNaissanceCourant().getLibPays());
			}
		}
		addZone(getNOM_ST_LIEU_NAISSANCE(), getLieuNaissance() == null ? Const.CHAINE_VIDE : getLieuNaissance());
	}

	/**
	 * Initialisation de la commune
	 */
	private void initialiseZonesCommune(HttpServletRequest request) throws Exception {
		// Init de la liste des villes de l'adresse
		int[] tailles = { 5, 12 };
		String[] champs = { "codCodePostal", "libCommune" };
		ArrayList<CommunePostal> listeCommunes = null;
		if (getVAL_RG_VILLE_DOMICILE().equals(getNOM_RB_VILLE_DOMICILE_NOUMEA())) {
			listeCommunes = getListeCommuneDomNoumea();
		} else {
			listeCommunes = getListeCommuneDomAutre();
		}
		setLB_COMMUNE_DOM(new FormateListe(tailles, listeCommunes, champs).getListeFormatee());

		// La commune adresse courante
		if (getCommuneDomCourant() == null && getAgentCourant() != null
				&& getAgentCourant().getCodeComVilleDom() != null
				&& !getAgentCourant().getCodeComVilleDom().equals("0")) {
			setCommuneDomCourant(CommunePostal.chercherCommunePostal(getTransaction(), getAgentCourant()
					.getCodePostalVilleDom(), getAgentCourant().getCodeComVilleDom()));
		}

		if (getCommuneDomCourant() != null) {
			int indiceCommuneDom = 0;
			if (listeCommunes.size() > 0) {
				// La première commune de la liste est nulle. Début du parcours
				// à l'indice 1
				for (int i = 1; i < listeCommunes.size(); i++) {
					CommunePostal comPostDom = (CommunePostal) listeCommunes.get(i);
					if (comPostDom.getCodCodePostal() != null
							&& comPostDom.getCodCodePostal().equals(getCommuneDomCourant().getCodCodePostal())
							&& comPostDom.getCodCommune().equals(getCommuneDomCourant().getCodCommune())) {
						indiceCommuneDom = i;
						break;
					}
				}
			}
			addZone(getNOM_LB_COMMUNE_DOM_SELECT(), String.valueOf(indiceCommuneDom));
			if (indiceCommuneDom == 0) {
				addZone(getNOM_EF_CODE_POSTAL_DOM(), Const.CHAINE_VIDE);
			} else {
				addZone(getNOM_EF_CODE_POSTAL_DOM(), getCommuneDomCourant().getCodCodePostal());
			}
		}

		// La commune boîte postale
		if (getAgentCourant() != null && getAgentCourant().getCodeComVilleBp() != null
				&& !getAgentCourant().getCodeComVilleBp().equals("0")) {
			setCommuneBPCourant(CommunePostal.chercherCommunePostal(getTransaction(), getAgentCourant()
					.getCodePostalVilleBp(), getAgentCourant().getCodeComVilleBp()));
			if (getTransaction().isErreur()) {
				return;
			}
			int indiceCommuneBp = 0;
			for (int i = 0; i < getListeCommuneBP().size(); i++) {
				CommunePostal comPostBp = (CommunePostal) getListeCommuneBP().get(i);
				if (comPostBp.getCodCodePostal() != null
						&& comPostBp.getCodCodePostal().equals(getCommuneBPCourant().getCodCodePostal())
						&& comPostBp.getCodCommune().equals(getCommuneBPCourant().getCodCommune())) {
					indiceCommuneBp = i;
					break;
				}
			}
			addZone(getNOM_LB_COMMUNE_BP_SELECT(), String.valueOf(indiceCommuneBp));
			addZone(getNOM_EF_CODE_POSTAL_BP(), getCommuneBPCourant().getCodCodePostal());
		}
	}

	/**
	 * Initialisation des zones du servive militaire RG_AG_EC_A06
	 */
	private void initialiseZonesService() throws Exception {
		// Si la liste des Types de Service vide alors
		// RG_AG_EC_A06
		if (getLB_TYPE_SERVICE() == LBVide) {
			ArrayList<EtatServiceMilitaire> esm = (ArrayList<EtatServiceMilitaire>) getEtatServiceMilitaireDao()
					.listerEtatServiceMilitaire();
			setListeTypeServiceMilitaire(esm);

			int[] tailles = { 20 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<EtatServiceMilitaire> list = getListeTypeServiceMilitaire().listIterator(); list
					.hasNext();) {
				EtatServiceMilitaire fili = (EtatServiceMilitaire) list.next();
				String ligne[] = { fili.getLibEtatService() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_SERVICE(aFormat.getListeFormatee(true));
		}

		// Si aucune VCAT alors NON par défaut
		if (getZone(getNOM_RG_VCAT()).length() == 0) {
			addZone(getNOM_RG_VCAT(), getNOM_RB_VCAT_NON());
		}
	}

	/**
	 * Initialisation des zones de la voie
	 */
	private void initialiseZonesVoieQuartier(HttpServletRequest request) throws Exception {
		// recup du champ voie-quartier
		if (getVoieQuartierCourant() == null && getAgentCourant() != null) {
			if (getAgentCourant().getIdVoie() != null && !getAgentCourant().getIdVoie().equals("0")) {
				setVoieQuartierCourant(VoieQuartier.chercherVoieQuartierAvecCodVoie(getTransaction(), getAgentCourant()
						.getIdVoie()));
				addZone(getNOM_RG_VILLE_DOMICILE(), getNOM_RB_VILLE_DOMICILE_NOUMEA());
			} else if (getAgentCourant().getIdVoie() == null) {
				addZone(getNOM_RG_VILLE_DOMICILE(), getNOM_RB_VILLE_DOMICILE_AUTRE());
			}
		}

		addZone(getNOM_ST_VOIE(), getVoieQuartierCourant() == null ? Const.CHAINE_VIDE : getVoieQuartierCourant()
				.getLibVoie());
	}

	/**
	 * Initialise la liste des types de contact.
	 * 
	 * @throws Exception
	 *             RG_AG_EC_A07
	 */
	private void initialiseListeTypeContact() throws Exception {
		// Si liste des type de contact vide
		// RG_AG_EC_A07
		if (getLB_TCONTACT() == LBVide) {
			ArrayList<TypeContact> a = getTypeContactDao().listerTypeContact();
			setListeTypeContact(a);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<TypeContact> list = getListeTypeContact().listIterator(); list.hasNext();) {
				TypeContact fili = (TypeContact) list.next();
				String ligne[] = { fili.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TCONTACT(aFormat.getListeFormatee());

			// Remplissage de la hashtable des types de contacts.
			for (ListIterator<TypeContact> list = a.listIterator(); list.hasNext();) {
				TypeContact aTypeContact = (TypeContact) list.next();
				getHashTypeContact().put(aTypeContact.getIdTypeContact(), aTypeContact);
			}
		}
	}

	/**
	 * Initialisation des contacts de l'agent
	 */
	private void initialiseListeContact(HttpServletRequest request) throws Exception {

		ArrayList<Contact> a = getContactDao().listerContactAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
		setListeContact(a);

		afficheListeContact();
	}

	/**
	 * Affiche le contenu de la liste listeContact
	 * 
	 */
	private void afficheListeContact() throws Exception {
		int indiceContact = 0;
		if (getListeContact() != null) {
			for (int i = 0; i < getListeContact().size(); i++) {
				Contact aContact = (Contact) getListeContact().get(i);
				TypeContact aType = getTypeContactDao().chercherTypeContact(aContact.getIdTypeContact());

				addZone(getNOM_ST_TYPE_CONTACT(indiceContact), aType.getLibelle().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: aType.getLibelle());
				addZone(getNOM_ST_DIFFUSABLE_CONTACT(indiceContact), aContact.isDiffusable() ? "Diffusable"
						: "Non diffusable");
				addZone(getNOM_ST_DESCRIPTION_CONTACT(indiceContact),
						aContact.getDescription().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aContact.getDescription());
				addZone(getNOM_ST_PRIORITAIRE_CONTACT(indiceContact), aContact.isPrioritaire() ? "Prioritaire"
						: "Non prioritaire");

				indiceContact++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CIVILITE Date de création :
	 * (16/03/11 09:31:44)
	 * 
	 * 
	 */
	public String getNOM_PB_CIVILITE() {
		return "NOM_PB_CIVILITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/03/11 09:31:44)
	 * 
	 * 
	 * RG_AG_EC_C07
	 */
	public boolean performPB_CIVILITE(HttpServletRequest request) throws Exception {
		// RG_AG_EC_C07
		String civilite = getLB_CIVILITE()[Integer.parseInt(getVAL_LB_CIVILITE_SELECT())];
		addZone(getNOM_ST_SEXE(), (civilite.equals(EnumCivilite.M.getValue()) ? EnumSexe.MASCULIN.getValue()
				: EnumSexe.FEMININ.getValue()));
		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CIVILITE Date de création
	 * : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	private String[] getLB_CIVILITE() {
		if (LB_CIVILITE == null) {
			LB_CIVILITE = initialiseLazyLB();
		}
		return LB_CIVILITE;
	}

	/**
	 * Setter de la liste: LB_CIVILITE Date de création : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	private void setLB_CIVILITE(String[] newLB_CIVILITE) {
		LB_CIVILITE = newLB_CIVILITE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CIVILITE Date de création
	 * : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	public String getNOM_LB_CIVILITE() {
		return "NOM_LB_CIVILITE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CIVILITE_SELECT Date de création : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	public String getNOM_LB_CIVILITE_SELECT() {
		return "NOM_LB_CIVILITE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CIVILITE Date de création : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	public String[] getVAL_LB_CIVILITE() {
		return getLB_CIVILITE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CIVILITE Date de création : (16/03/11 09:31:44)
	 * 
	 * 
	 */
	public String getVAL_LB_CIVILITE_SELECT() {
		return getZone(getNOM_LB_CIVILITE_SELECT());
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (16/03/2011
	 * 10:29:03)
	 * 
	 * @return nc.mairie.metier.agent.AgentNW
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (16/03/2011
	 * 10:29:03)
	 * 
	 * @param agentCourant
	 */
	public void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SEXE Date de
	 * création : (17/03/11 09:44:22)
	 * 
	 * 
	 */
	public String getNOM_ST_SEXE() {
		return "NOM_ST_SEXE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SEXE Date de
	 * création : (17/03/11 09:44:22)
	 * 
	 * 
	 */
	public String getVAL_ST_SEXE() {
		return getZone(getNOM_ST_SEXE());
	}

	/**
	 * Récupère la liste des situations familiales. Date de création :
	 * (17/03/2011 15:26:00)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<SituationFamiliale> getListeSituation() {
		if (listeSituation == null) {
			listeSituation = new ArrayList<SituationFamiliale>();
		}
		return listeSituation;
	}

	/**
	 * Met à jour la liste des situations familiales. Date de création :
	 * (17/03/2011 15:26:00)
	 * 
	 * @param newListeSituation
	 *            ArrayList
	 */
	private void setListeSituation(ArrayList<SituationFamiliale> newListeSituation) {
		listeSituation = newListeSituation;
	}

	/**
	 * Récupère la liste des nationalites. Date de création : (17/03/2011
	 * 15:26:00)
	 * 
	 * @return String[]
	 */
	private String[] getListeNationalite() {
		if (listeNationalite == null) {
			listeNationalite = new String[3];
		}
		return listeNationalite;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (17/03/2011
	 * 15:26:00)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<BanqueGuichet> getListeBanqueGuichet() {
		if (listeBanqueGuichet == null) {
			listeBanqueGuichet = new ArrayList<BanqueGuichet>();
		}
		return listeBanqueGuichet;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (17/03/2011
	 * 15:26:00)
	 * 
	 * @param newListeBanqueGuichet
	 *            ArrayList
	 */
	private void setListeBanqueGuichet(ArrayList<BanqueGuichet> newListeBanqueGuichet) {
		listeBanqueGuichet = newListeBanqueGuichet;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SITUATION Date de création :
	 * (17/03/11 15:43:50)
	 * 
	 * 
	 */
	public String getNOM_PB_SITUATION() {
		return "NOM_PB_SITUATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/03/11 15:43:50)
	 * 
	 * 
	 */
	public boolean performPB_SITUATION(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_VALIDITE_CARTE_SEJOUR Date de création : (17/03/11 15:43:50)
	 * 
	 * 
	 */
	public String getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR() {
		return "NOM_EF_DATE_VALIDITE_CARTE_SEJOUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_VALIDITE_CARTE_SEJOUR Date de création : (17/03/11 15:43:50)
	 * 
	 * 
	 */
	public String getVAL_EF_DATE_VALIDITE_CARTE_SEJOUR() {
		return getZone(getNOM_EF_DATE_VALIDITE_CARTE_SEJOUR());
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public boolean performPB_ANNULER_CONTACT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_CONTACT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIBELLE_CONTACT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TCONTACT(), Const.CHAINE_VIDE);
		setContactCourant(null);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_BANQUE Date de création :
	 * (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getNOM_PB_BANQUE() {
		return "NOM_PB_BANQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public boolean performPB_BANQUE(HttpServletRequest request) throws Exception {
		// recup de l'indice sélectionné
		int indice = Integer.parseInt(getVAL_LB_BANQUE_GUICHET_SELECT());
		BanqueGuichet b = (BanqueGuichet) getListeBanqueGuichet().get(indice);

		addZone(getNOM_ST_BANQUE_GUICHET(),
				b == null ? Const.CHAINE_VIDE : b.getLibBanque() + " - " + b.getLibGuichet());

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BP Date de création
	 * : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getNOM_ST_BP() {
		return "NOM_ST_BP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BP Date de
	 * création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getVAL_ST_BP() {
		return getZone(getNOM_ST_BP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMUNE_DOM Date de
	 * création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getNOM_ST_COMMUNE_DOM() {
		return "NOM_ST_COMMUNE_DOM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMUNE_DOM
	 * Date de création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getVAL_ST_COMMUNE_DOM() {
		return getZone(getNOM_ST_COMMUNE_DOM());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_VOIE Date de
	 * création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getNOM_ST_VOIE() {
		return "NOM_ST_VOIE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_VOIE Date de
	 * création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getVAL_ST_VOIE() {
		return getZone(getNOM_ST_VOIE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RIB Date de
	 * création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getNOM_EF_RIB() {
		return "NOM_EF_RIB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_RIB
	 * Date de création : (18/03/11 11:04:52)
	 * 
	 * 
	 */
	public String getVAL_EF_RIB() {
		return getZone(getNOM_EF_RIB());
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (24/03/2011
	 * 10:38:00)
	 * 
	 * @return nc.mairie.metier.commun.VoieQuartier
	 */
	public VoieQuartier getVoieQuartierCourant() {
		return voieQuartierCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (24/03/2011
	 * 10:38:00)
	 * 
	 * @param voieQuartierCourant
	 *            nc.mairie.metier.commun.VoieQuartier
	 */
	public void setVoieQuartierCourant(VoieQuartier voieQuartierCourant) {
		this.voieQuartierCourant = voieQuartierCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (24/03/2011
	 * 10:37:00)
	 * 
	 * @return String
	 */
	public String getLieuNaissance() {
		return lieuNaissance;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (24/03/2011
	 * 10:37:00)
	 * 
	 * @param newLieuNaissance
	 *            String
	 */
	public void setLieuNaissance(String newLieuNaissance) {
		this.lieuNaissance = newLieuNaissance;
	}

	/**
	 * Retourne la commune courante du domicile. Date de création : (24/03/2011
	 * 10:37:00)
	 * 
	 * @return nc.mairie.metier.commun.CommunePostal
	 */
	private CommunePostal getCommuneDomCourant() {
		return communeDomCourant;
	}

	/**
	 * Met à jour la commune courante du domicile. Date de création :
	 * (24/03/2011 10:37:00)
	 * 
	 * @param newCommuneDomCourant
	 *            nc.mairie.metier.commun.Commune
	 */
	private void setCommuneDomCourant(CommunePostal newCommuneDomCourant) {
		this.communeDomCourant = newCommuneDomCourant;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_COMMUNE_DOM Date de
	 * création : (24/03/11 15:27:46)
	 * 
	 */
	private String[] getLB_COMMUNE_DOM() {
		if (LB_COMMUNE_DOM == null) {
			LB_COMMUNE_DOM = initialiseLazyLB();
		}
		return LB_COMMUNE_DOM;
	}

	/**
	 * Setter de la liste: LB_COMMUNE_DOM Date de création : (24/03/11 15:27:46)
	 * 
	 */
	private void setLB_COMMUNE_DOM(String[] newLB_COMMUNE_DOM) {
		LB_COMMUNE_DOM = newLB_COMMUNE_DOM;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_COMMUNE_DOM Date de
	 * création : (24/03/11 15:27:46)
	 * 
	 */
	public String getNOM_LB_COMMUNE_DOM() {
		return "NOM_LB_COMMUNE_DOM";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_COMMUNE_DOM_SELECT Date de création : (24/03/11 15:27:46)
	 * 
	 */
	public String getNOM_LB_COMMUNE_DOM_SELECT() {
		return "NOM_LB_COMMUNE_DOM_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_COMMUNE_DOM Date de création : (24/03/11 15:27:46)
	 * 
	 */
	public String[] getVAL_LB_COMMUNE_DOM() {
		return getLB_COMMUNE_DOM();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_COMMUNE_DOM Date de création : (24/03/11 15:27:46)
	 * 
	 */
	public String getVAL_LB_COMMUNE_DOM_SELECT() {
		return getZone(getNOM_LB_COMMUNE_DOM_SELECT());
	}

	/**
	 * Retourne la liste des codes postaux de Noumea. Date de création :
	 * (25/03/2011 08:30:00)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<CommunePostal> getListeCommuneDomNoumea() {
		if (listeCommuneDomNoumea == null) {
			listeCommuneDomNoumea = new ArrayList<CommunePostal>();
		}
		return listeCommuneDomNoumea;
	}

	/**
	 * Met à jour la liste des codes postaux de Noumea. Date de création :
	 * (25/03/2011 08:30:00)
	 * 
	 * @param newListeCommuneDomNoumea
	 *            ArrayList
	 */
	private void setListeCommuneDomNoumea(ArrayList<CommunePostal> newListeCommuneDomNoumea) {
		this.listeCommuneDomNoumea = newListeCommuneDomNoumea;
	}

	/**
	 * Retourne la liste des codes postaux hors Noumea. Date de création :
	 * (25/03/2011 08:30:00)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<CommunePostal> getListeCommuneDomAutre() {
		if (listeCommuneDomAutre == null) {
			listeCommuneDomAutre = new ArrayList<CommunePostal>();
		}
		return listeCommuneDomAutre;
	}

	/**
	 * Met à jour la liste des codes postaux hors Noumea. Date de création :
	 * (25/03/2011 08:30:00)
	 * 
	 * @param newListeCommuneDomAutre
	 *            ArrayList
	 */
	private void setListeCommuneDomAutre(ArrayList<CommunePostal> newListeCommuneDomAutre) {
		this.listeCommuneDomAutre = newListeCommuneDomAutre;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_POSTAL_DOM
	 * Date de création : (25/03/11 10:30:28)
	 * 
	 */
	public String getNOM_EF_CODE_POSTAL_DOM() {
		return "NOM_EF_CODE_POSTAL_DOM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_POSTAL_DOM Date de création : (25/03/11 10:30:28)
	 * 
	 */
	public String getVAL_EF_CODE_POSTAL_DOM() {
		return getZone(getNOM_EF_CODE_POSTAL_DOM());
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (25/03/2011
	 * 10:40:00)
	 * 
	 * @return String
	 */
	public String getCodePostal() {
		return codePostal;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (25/03/2011
	 * 10:40:00)
	 * 
	 * @param codePostal
	 *            String
	 */
	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BANQUE_GUICHET Date
	 * de création : (29/03/11 16:13:41)
	 * 
	 */
	public String getNOM_ST_BANQUE_GUICHET() {
		return "NOM_ST_BANQUE_GUICHET";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BANQUE_GUICHET
	 * Date de création : (29/03/11 16:13:41)
	 * 
	 */
	public String getVAL_ST_BANQUE_GUICHET() {
		return getZone(getNOM_ST_BANQUE_GUICHET());
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (30/03/2011
	 * 08:22:00)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<EtatServiceMilitaire> getListeTypeServiceMilitaire() {
		return listeTypeServiceMilitaire;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (17/03/2011
	 * 08:22:00)
	 * 
	 * @param newListeTypeServiceMilitaire
	 *            ArrayList
	 */
	private void setListeTypeServiceMilitaire(ArrayList<EtatServiceMilitaire> newListeTypeServiceMilitaire) {
		this.listeTypeServiceMilitaire = newListeTypeServiceMilitaire;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_CAFAT Date de
	 * création : (30/03/11 09:27:03)
	 * 
	 */
	public String getNOM_EF_NUM_CAFAT() {
		return "NOM_EF_NUM_CAFAT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_CAFAT Date de création : (30/03/11 09:27:03)
	 * 
	 */
	public String getVAL_EF_NUM_CAFAT() {
		return getZone(getNOM_EF_NUM_CAFAT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_CRE Date de
	 * création : (30/03/11 09:27:03)
	 * 
	 */
	public String getNOM_EF_NUM_CRE() {
		return "NOM_EF_NUM_CRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_CRE Date de création : (30/03/11 09:27:03)
	 * 
	 */
	public String getVAL_EF_NUM_CRE() {
		return getZone(getNOM_EF_NUM_CRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_IRCAFEX Date de
	 * création : (30/03/11 09:27:03)
	 * 
	 */
	public String getNOM_EF_NUM_IRCAFEX() {
		return "NOM_EF_NUM_IRCAFEX";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_IRCAFEX Date de création : (30/03/11 09:27:03)
	 * 
	 */
	public String getVAL_EF_NUM_IRCAFEX() {
		return getZone(getNOM_EF_NUM_IRCAFEX());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_CLR Date de
	 * création : (30/03/11 09:27:03)
	 * 
	 */
	public String getNOM_EF_NUM_CLR() {
		return "NOM_EF_NUM_CLR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_CLR Date de création : (08/09/11)
	 * 
	 */
	public String getVAL_EF_NUM_CLR() {
		return getZone(getNOM_EF_NUM_CLR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_MUTUELLE Date
	 * de création : (30/03/11 09:27:03)
	 * 
	 */
	public String getNOM_EF_NUM_MUTUELLE() {
		return "NOM_EF_NUM_MUTUELLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_MUTUELLE Date de création : (30/03/11 09:27:03)
	 * 
	 */
	public String getVAL_EF_NUM_MUTUELLE() {
		return getZone(getNOM_EF_NUM_MUTUELLE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_RUAMM Date de
	 * création : (30/03/11 09:27:03)
	 * 
	 */
	public String getNOM_EF_NUM_RUAMM() {
		return "NOM_EF_NUM_RUAMM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_RUAMM Date de création : (30/03/11 09:27:03)
	 * 
	 */
	public String getVAL_EF_NUM_RUAMM() {
		return getZone(getNOM_EF_NUM_RUAMM());
	}

	private void setContactCourant(Contact newContact) {
		contactCourant = newContact;
	}

	private Contact getContactCourant() {
		return contactCourant;
	}

	private void setListeContact(ArrayList<Contact> newListeContact) {
		listeContact = newListeContact;
	}

	public ArrayList<Contact> getListeContact() {
		if (listeContact == null) {
			listeContact = new ArrayList<Contact>();
		}
		return listeContact;
	}

	private ArrayList<TypeContact> getListeTypeContact() {
		if (listeTypeContact == null) {
			listeTypeContact = new ArrayList<TypeContact>();
		}
		return listeTypeContact;
	}

	private void setListeTypeContact(ArrayList<TypeContact> newListeTypeContact) {
		listeTypeContact = newListeTypeContact;
	}

	private Hashtable<Integer, TypeContact> getHashTypeContact() {
		if (hashTypeContact == null) {
			hashTypeContact = new Hashtable<Integer, TypeContact>();
		}
		return hashTypeContact;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CONTACT Date de
	 * création : (30/03/11 10:26:43)
	 * 
	 */
	public String getNOM_PB_ANNULER_CONTACT() {
		return "NOM_PB_ANNULER_CONTACT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CONTACT Date de
	 * création : (30/03/11 10:26:43)
	 * 
	 */
	public String getNOM_PB_CREER_CONTACT() {
		return "NOM_PB_CREER_CONTACT";
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
		return getNOM_EF_NOM_PATRONYMIQUE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DERN_EMB Date
	 * de création : (30/03/11 11:38:17)
	 * 
	 */
	public String getNOM_EF_DATE_DERN_EMB() {
		return "NOM_EF_DATE_DERN_EMB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DERN_EMB Date de création : (30/03/11 11:38:17)
	 * 
	 */
	public String getVAL_EF_DATE_DERN_EMB() {
		return getZone(getNOM_EF_DATE_DERN_EMB());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_PREM_EMB Date
	 * de création : (30/03/11 11:38:17)
	 * 
	 */
	public String getNOM_EF_DATE_PREM_EMB() {
		return "NOM_EF_DATE_PREM_EMB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_PREM_EMB Date de création : (30/03/11 11:38:17)
	 * 
	 */
	public String getVAL_EF_DATE_PREM_EMB() {
		return getZone(getNOM_EF_DATE_PREM_EMB());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ENREGISTRER Date de création
	 * : (11/04/11 14:50:48)
	 * 
	 */
	public String getNOM_PB_ENREGISTRER() {
		return "NOM_PB_ENREGISTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/04/11 14:50:48)
	 * 
	 */
	public boolean performPB_ENREGISTRER(HttpServletRequest request) throws Exception {

		// Contrôle des champs
		if (!performControlerSaisie(request, false)) {
			return false;
		}

		// Alimentation de l'agent
		alimenterAgent(request);

		// Création/Modification de l'agent
		ArrayList<Contact> lContact = getContactDao().listerContactAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()));
		SituationFamiliale situFam = getSituationFamilialeDao().chercherSituationFamilialeById(
				Integer.valueOf(getAgentCourant().getIdSituationFamiliale()));
		getAgentCourant().creerAgentNW(getTransaction(), lContact, situFam);
		if (!getTransaction().isErreur()) {
			VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getAgentCourant());
			commitTransaction();
		} else {
			return false;
		}

		// "INF001","Agent @ créé"
		/*
		 * setStatut(STATUT_SUIVI, false, MairieMessages.getMessage("INF001",
		 * getAgentCourant().getNoMatricule()) + " " + message);
		 */
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_CONTACT_DIFF Date de création : (14/04/11 15:28:01)
	 * 
	 */
	public String getNOM_RG_CONTACT_DIFF() {
		return "NOM_RG_CONTACT_DIFF";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_CONTACT_DIFF Date de création : (14/04/11 15:28:01)
	 * 
	 */
	public String getVAL_RG_CONTACT_DIFF() {
		return getZone(getNOM_RG_CONTACT_DIFF());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONTACT_DIFF_NON Date de
	 * création : (14/04/11 15:28:01)
	 * 
	 */
	public String getNOM_RB_CONTACT_DIFF_NON() {
		return "NOM_RB_CONTACT_DIFF_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONTACT_DIFF_OUI Date de
	 * création : (14/04/11 15:28:01)
	 * 
	 */
	public String getNOM_RB_CONTACT_DIFF_OUI() {
		return "NOM_RB_CONTACT_DIFF_OUI";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_INIT_DATE_DERNIERE_EMBAUCHE
	 * Date de création : (25/05/11 16:34:06)
	 * 
	 */
	public String getNOM_PB_INIT_DATE_DERNIERE_EMBAUCHE() {
		return "NOM_PB_INIT_DATE_DERNIERE_EMBAUCHE";
	}

	/**
	 * Initialise la date de dernière embauche à partir de la date de première
	 * embauche. Date de création : (25/05/11 16:34:06)
	 * 
	 */
	public boolean performPB_INIT_DATE_DERNIERE_EMBAUCHE(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_DATE_DERN_EMB(), getZone(getNOM_EF_DATE_PREM_EMB()));
		return true;
	}

	private String[] LB_COMMUNE_BP;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VILLE_DOMICILE Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_PB_VILLE_DOMICILE() {
		return "NOM_PB_VILLE_DOMICILE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BIS_TER Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_EF_BIS_TER() {
		return "NOM_EF_BIS_TER";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BIS_TER Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_EF_BIS_TER() {
		return getZone(getNOM_EF_BIS_TER());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_POSTAL_BP Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_EF_CODE_POSTAL_BP() {
		return "NOM_EF_CODE_POSTAL_BP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_POSTAL_BP Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_EF_CODE_POSTAL_BP() {
		return getZone(getNOM_EF_CODE_POSTAL_BP());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_COMMUNE_BP Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	private String[] getLB_COMMUNE_BP() {
		if (LB_COMMUNE_BP == null) {
			LB_COMMUNE_BP = initialiseLazyLB();
		}
		return LB_COMMUNE_BP;
	}

	/**
	 * Setter de la liste: LB_COMMUNE_BP Date de création : (26/05/11 11:31:12)
	 * 
	 */
	private void setLB_COMMUNE_BP(String[] newLB_COMMUNE_BP) {
		LB_COMMUNE_BP = newLB_COMMUNE_BP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_COMMUNE_BP Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_LB_COMMUNE_BP() {
		return "NOM_LB_COMMUNE_BP";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_COMMUNE_BP_SELECT Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_LB_COMMUNE_BP_SELECT() {
		return "NOM_LB_COMMUNE_BP_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_COMMUNE_BP Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String[] getVAL_LB_COMMUNE_BP() {
		return getLB_COMMUNE_BP();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_COMMUNE_BP Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_LB_COMMUNE_BP_SELECT() {
		return getZone(getNOM_LB_COMMUNE_BP_SELECT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_VILLE_DOMICILE Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_VILLE_DOMICILE() {
		return "NOM_RG_VILLE_DOMICILE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_VILLE_DOMICILE Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_VILLE_DOMICILE() {
		return getZone(getNOM_RG_VILLE_DOMICILE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VILLE_DOMICILE_AUTRE
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_VILLE_DOMICILE_AUTRE() {
		return "NOM_RB_VILLE_DOMICILE_AUTRE";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VILLE_DOMICILE_NOUMEA
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_VILLE_DOMICILE_NOUMEA() {
		return "NOM_RB_VILLE_DOMICILE_NOUMEA";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_COMMUNE_BP Date de création
	 * : (26/05/11 14:15:07)
	 * 
	 */
	public String getNOM_PB_COMMUNE_BP() {
		return "NOM_PB_COMMUNE_BP";
	}

	/**
	 * Met à jour le code postal de la boîte postale à partir du choix de la
	 * ville. Date de création : (26/05/11 14:15:07)
	 * 
	 */
	public boolean performPB_COMMUNE_BP(HttpServletRequest request) throws Exception {
		CommunePostal newCommune = (CommunePostal) getListeCommuneBP().get(
				Integer.parseInt(getVAL_LB_COMMUNE_BP_SELECT()));
		setCommuneBPCourant(newCommune);
		addZone(getNOM_EF_CODE_POSTAL_BP(), getCommuneBPCourant().getCodCodePostal());
		return true;
	}

	/**
	 * Retourne la liste des communes de la boîte postale.
	 * 
	 * @return ArrayList<CommunePostal>
	 */
	private ArrayList<CommunePostal> getListeCommuneBP() {
		return listeCommuneBP;
	}

	/**
	 * Met à jour la liste des communes de la boîte postale.
	 * 
	 * @param listeCommuneBP
	 */
	private void setListeCommuneBP(ArrayList<CommunePostal> listeCommuneBP) {
		this.listeCommuneBP = listeCommuneBP;
	}

	/**
	 * Retourne la commune courante de la boîte postale.
	 * 
	 * @return CommunePostal
	 */
	private CommunePostal getCommuneBPCourant() {
		return communeBPCourant;
	}

	/**
	 * Met à jour la commune courante de la boîte postale.
	 * 
	 * @param communeBPCourant
	 *            CommunePostal
	 */
	private void setCommuneBPCourant(CommunePostal communeBPCourant) {
		this.communeBPCourant = communeBPCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ADRESSE Date de création :
	 * (30/05/11 10:18:13)
	 * 
	 */
	public String getNOM_PB_ADRESSE() {
		return "NOM_PB_ADRESSE";
	}

	/**
	 * Actions générées par le changement d'adresse (Nouméa / Autre) Date de
	 * création : (30/05/11 10:18:13)
	 * 
	 * RG_AG_EC_A08 RG_AG_EC_A09
	 */
	public boolean performPB_ADRESSE(HttpServletRequest request) throws Exception {
		// RG_AG_EC_A08
		// RG_AG_EC_A09

		viderAdresse();
		if (getVAL_RG_VILLE_DOMICILE().equals(getNOM_RB_VILLE_DOMICILE_NOUMEA())) {
			int[] tailles = { 5, 12 };
			String[] champs = { "codCodePostal", "libCommune" };
			setLB_COMMUNE_DOM(new FormateListe(tailles, getListeCommuneDomNoumea(), champs).getListeFormatee());
			// si pas de ville choisie on met Noumea par defaut
			if (getZone(getVAL_EF_CODE_POSTAL_DOM()).equals(Const.CHAINE_VIDE)) {
				CommunePostal newCommune = (CommunePostal) getListeCommuneDomNoumea().get(1);
				setCommuneDomCourant(newCommune);
				addZone(getNOM_EF_CODE_POSTAL_DOM(), getCommuneDomCourant().getCodCodePostal());
				addZone(getNOM_LB_COMMUNE_DOM_SELECT(), "1");
			}
		} else {
			int[] tailles = { 5, 12 };
			String[] champs = { "codCodePostal", "libCommune" };
			setLB_COMMUNE_DOM(new FormateListe(tailles, getListeCommuneDomAutre(), champs).getListeFormatee());
		}
		return true;
	}

	/**
	 * Vide les champs de l'adresse.
	 */
	private void viderAdresse() {
		addZone(getNOM_EF_NUM_RUE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BIS_TER(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ADRESSE_COMPLEMENTAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RUE_NON_NOUMEA(), Const.CHAINE_VIDE);

		addZone(getNOM_LB_COMMUNE_BP_SELECT(), "0");
		addZone(getNOM_EF_CODE_POSTAL_BP(), Const.CHAINE_VIDE);

		setVoieQuartierCourant(null);
		setCommuneDomCourant(null);
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_NATIONALITE Date de création
	 * : (30/05/11 15:39:08)
	 * 
	 */
	public String getNOM_PB_NATIONALITE() {
		return "NOM_PB_NATIONALITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (30/05/11 15:39:08)
	 * 
	 */
	public boolean performPB_NATIONALITE(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COLLECTIVITE Date de
	 * création : (31/05/11 09:01:30)
	 * 
	 */
	public String getNOM_ST_COLLECTIVITE() {
		return "NOM_ST_COLLECTIVITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COLLECTIVITE
	 * Date de création : (31/05/11 09:01:30)
	 * 
	 */
	public String getVAL_ST_COLLECTIVITE() {
		return getZone(getNOM_ST_COLLECTIVITE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_COLLECTIVITE Date de
	 * création : (31/05/11 09:01:30)
	 * 
	 */
	private String[] getLB_COLLECTIVITE() {
		if (LB_COLLECTIVITE == null) {
			LB_COLLECTIVITE = initialiseLazyLB();
		}
		return LB_COLLECTIVITE;
	}

	/**
	 * Setter de la liste: LB_COLLECTIVITE Date de création : (31/05/11
	 * 09:01:30)
	 * 
	 */
	private void setLB_COLLECTIVITE(String[] newLB_COLLECTIVITE) {
		LB_COLLECTIVITE = newLB_COLLECTIVITE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_COLLECTIVITE Date de
	 * création : (31/05/11 09:01:30)
	 * 
	 */
	public String getNOM_LB_COLLECTIVITE() {
		return "NOM_LB_COLLECTIVITE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_COLLECTIVITE_SELECT Date de création : (31/05/11 09:01:30)
	 * 
	 */
	public String getNOM_LB_COLLECTIVITE_SELECT() {
		return "NOM_LB_COLLECTIVITE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_COLLECTIVITE Date de création : (31/05/11 09:01:30)
	 * 
	 */
	public String[] getVAL_LB_COLLECTIVITE() {
		return getLB_COLLECTIVITE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_COLLECTIVITE Date de création : (31/05/11 09:01:30)
	 * 
	 */
	public String getVAL_LB_COLLECTIVITE_SELECT() {
		return getZone(getNOM_LB_COLLECTIVITE_SELECT());
	}

	/**
	 * Retourne la liste des collectivités.
	 * 
	 * @return ArrayList
	 */
	private ArrayList<Collectivite> getListeCollectivite() {
		return listeCollectivite;
	}

	/**
	 * Met à jour la liste des collectivités.
	 * 
	 * @param listeCollectivite
	 */
	private void setListeCollectivite(ArrayList<Collectivite> listeCollectivite) {
		this.listeCollectivite = listeCollectivite;
	}

	/**
	 * Retourne vrai si l'utilisateur est en mode création.Faux sinon.
	 * 
	 * @return modeCreation boolean
	 */
	private boolean isModeCreation() {
		return modeCreation;
	}

	/**
	 * Met à jour le mode de création.
	 * 
	 * @param modeCreation
	 *            modeCreation à définir
	 */
	private void setModeCreation(boolean modeCreation) {
		this.modeCreation = modeCreation;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_NATIONALITE
			if (testerParametre(request, getNOM_PB_NATIONALITE())) {
				return performPB_NATIONALITE(request);
			}

			// Si clic sur le bouton PB_ADRESSE
			if (testerParametre(request, getNOM_PB_ADRESSE())) {
				return performPB_ADRESSE(request);
			}

			// Si clic sur le bouton PB_COMMUNE_BP
			if (testerParametre(request, getNOM_PB_COMMUNE_BP())) {
				return performPB_COMMUNE_BP(request);
			}

			// Si clic sur le bouton PB_INIT_DATE_DERNIERE_EMBAUCHE
			if (testerParametre(request, getNOM_PB_INIT_DATE_DERNIERE_EMBAUCHE())) {
				return performPB_INIT_DATE_DERNIERE_EMBAUCHE(request);
			}

			// Si clic sur le bouton PB_ENREGISTRER
			if (testerParametre(request, getNOM_PB_ENREGISTRER())) {
				return performPB_ENREGISTRER(request);
			}

			// Si clic sur le bouton PB_ANNULER_CONTACT
			if (testerParametre(request, getNOM_PB_ANNULER_CONTACT())) {
				return performPB_ANNULER_CONTACT(request);
			}

			// Si clic sur le bouton PB_BANQUE
			if (testerParametre(request, getNOM_PB_BANQUE())) {
				return performPB_BANQUE(request);
			}

			// Si clic sur le bouton PB_SITUATION
			if (testerParametre(request, getNOM_PB_SITUATION())) {
				return performPB_SITUATION(request);
			}

			// Si clic sur le bouton PB_CIVILITE
			if (testerParametre(request, getNOM_PB_CIVILITE())) {
				return performPB_CIVILITE(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_COMMUNE_DOM
			if (testerParametre(request, getNOM_PB_COMMUNE_DOM())) {
				return performPB_COMMUNE_DOM(request);
			}

			// Si clic sur le bouton PB_CREER_CONTACT
			if (testerParametre(request, getNOM_PB_CREER_CONTACT())) {
				return performPB_CREER_CONTACT(request);
			}

			// Si clic sur le bouton PB_LIEU_NAISSANCE
			if (testerParametre(request, getNOM_PB_LIEU_NAISSANCE())) {
				return performPB_LIEU_NAISSANCE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_CONTACT
			for (int i = 0; i < getListeContact().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_CONTACT(i))) {
					return performPB_MODIFIER_CONTACT(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_CONTACT
			for (int i = 0; i < getListeContact().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_CONTACT(i))) {
					return performPB_SUPPRIMER_CONTACT(request, i);
				}
			}

			// Si clic sur le bouton PB_TCONTACT
			if (testerParametre(request, getNOM_PB_TCONTACT())) {
				return performPB_TCONTACT(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_VALIDER_CONTACT
			if (testerParametre(request, getNOM_PB_VALIDER_CONTACT())) {
				return performPB_VALIDER_CONTACT(request);
			}

			// Si clic sur le bouton PB_VOIE
			if (testerParametre(request, getNOM_PB_VOIE())) {
				return performPB_VOIE(request);
			}

			// Si clic sur le bouton PB_SELECT_TCONTACT
			if (testerParametre(request, getNOM_PB_SELECT_TCONTACT())) {
				return performPB_SELECT_TCONTACT(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTEtatCivil. Date de création : (10/06/11
	 * 10:49:53)
	 * 
	 */
	public OeAGENTEtatCivil() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (10/06/11 10:49:53)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEtatCivil.jsp";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM_USAGE Date
	 * de création : (10/06/11 10:49:54)
	 * 
	 */
	public String getNOM_EF_PRENOM_USAGE() {
		return "NOM_EF_PRENOM_USAGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM_USAGE Date de création : (10/06/11 10:49:54)
	 * 
	 */
	public String getVAL_EF_PRENOM_USAGE() {
		return getZone(getNOM_EF_PRENOM_USAGE());
	}

	/**
	 * Getter de la commune de naissance courante.
	 * 
	 * @return commNaissanceCourant
	 */
	private java.lang.Object getCommNaissanceCourant() {
		return commNaissanceCourant;
	}

	/**
	 * Setter de la commune de naissance courante.
	 * 
	 * @param commNaissanceCourant
	 */
	private void setCommNaissanceCourant(java.lang.Object commNaissanceCourant) {
		this.commNaissanceCourant = commNaissanceCourant;
	}

	/**
	 * Getter du pays de naissance courant.
	 * 
	 * @return paysNaissanceCourant
	 */
	private Pays getPaysNaissanceCourant() {
		return paysNaissanceCourant;
	}

	/**
	 * Setter du pays de naissance courant.
	 * 
	 * @param paysNaissanceCourant
	 */
	private void setPaysNaissanceCourant(Pays paysNaissanceCourant) {
		this.paysNaissanceCourant = paysNaissanceCourant;
	}

	private ArrayList<Contact> getListeContactAAjouter() {
		if (listeContactAAjouter == null) {
			listeContactAAjouter = new ArrayList<Contact>();
		}
		return listeContactAAjouter;
	}

	private ArrayList<Contact> getListeContactAModifier() {
		if (listeContactAModifier == null) {
			listeContactAModifier = new ArrayList<Contact>();
		}
		return listeContactAModifier;
	}

	private ArrayList<Contact> getListeContactASupprimer() {
		if (listeContactASupprimer == null) {
			listeContactASupprimer = new ArrayList<Contact>();
		}
		return listeContactASupprimer;
	}

	public String getNomEcran() {
		return "ECR-AG-DP-ETATCIVIL";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_TCONTACT Date de
	 * création : (26/09/11 09:04:08)
	 * 
	 */
	public String getNOM_PB_SELECT_TCONTACT() {
		return "NOM_PB_SELECT_TCONTACT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un TCONTACT en fonction de ces
	 * règles : setTCONTACT(TCONTACT, boolean veutRetour) ou
	 * setTCONTACT(TCONTACT,Message d'erreur) Date de création : (26/09/11
	 * 09:04:08)
	 * 
	 */
	public boolean performPB_SELECT_TCONTACT(HttpServletRequest request) throws Exception {
		// si ligne directe choisie alors par defaut diffusable="OUI"
		TypeContact newType = (TypeContact) getListeTypeContact().get(
				Integer.parseInt(getZone(getNOM_LB_TCONTACT_SELECT())));
		setFocus(getNOM_PB_VALIDER_CONTACT());
		if (newType.getIdTypeContact() == 6) {
			addZone(getNOM_RG_CONTACT_DIFF(), getNOM_RB_CONTACT_DIFF_OUI());
			diffusableModifiable = false;
		} else {
			diffusableModifiable = true;
		}
		initialiseContactPrioritaire(newType);
		return true;
	}

	/**
	 * @param newType
	 */
	private void initialiseContactPrioritaire(TypeContact newType) {
		// on regarde si il y a dejà pour ce type un contact prioritaire
		boolean unEstDejaPrioritaire = false;
		for (int i = 0; i < getListeContact().size(); i++) {
			Contact c = (Contact) getListeContact().get(i);
			if (c.isPrioritaire() && c.getIdTypeContact() == newType.getIdTypeContact()) {
				unEstDejaPrioritaire = true;
				break;
			}
		}
		if (unEstDejaPrioritaire) {
			// si oui, alors on rend le champ non modifiable
			prioritaireModifiable = false;
			addZone(getNOM_RG_CONTACT_PRIORITAIRE(), getNOM_RB_CONTACT_PRIORITAIRE_NON());
		} else {
			prioritaireModifiable = true;
			addZone(getNOM_RG_CONTACT_PRIORITAIRE(), getNOM_RB_CONTACT_PRIORITAIRE_OUI());
		}

	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_TYPE_CONTACT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_CONTACT(int i) {
		return "NOM_ST_TYPE_CONTACT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_CONTACT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_CONTACT(int i) {
		return getZone(getNOM_ST_TYPE_CONTACT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_DIFFUSABLE_CONTACT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DIFFUSABLE_CONTACT(int i) {
		return "NOM_ST_DIFFUSABLE_CONTACT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DIFFUSABLE_CONTACT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DIFFUSABLE_CONTACT(int i) {
		return getZone(getNOM_ST_DIFFUSABLE_CONTACT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_DESCRIPTION_CONTACT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DESCRIPTION_CONTACT(int i) {
		return "NOM_ST_DESCRIPTION_CONTACT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DESCRIPTION_CONTACT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DESCRIPTION_CONTACT(int i) {
		return getZone(getNOM_ST_DESCRIPTION_CONTACT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_PRIORITAIRE_CONTACT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PRIORITAIRE_CONTACT(int i) {
		return "NOM_ST_PRIORITAIRE_CONTACT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_PRIORITAIRE_CONTACT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PRIORITAIRE_CONTACT(int i) {
		return getZone(getNOM_ST_PRIORITAIRE_CONTACT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_CONTACT Date de
	 * création : (30/03/11 10:26:44)
	 * 
	 */
	public String getNOM_PB_MODIFIER_CONTACT(int i) {
		return "NOM_PB_MODIFIER_CONTACT" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CONTACT Date de
	 * création : (30/03/11 10:26:44)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CONTACT(int i) {
		return "NOM_PB_SUPPRIMER_CONTACT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_MODIFIER_CONTACT(HttpServletRequest request, int elemAModifier) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_CONTACT(), ACTION_MODIFICATION);

		// Récup du contact courant
		Contact c = (Contact) getListeContact().get(elemAModifier);
		setContactCourant(c);
		TypeContact t = (TypeContact) getHashTypeContact().get(c.getIdTypeContact());

		// Alim zones
		int ligneType = getListeTypeContact().indexOf(t);
		addZone(getNOM_EF_LIBELLE_CONTACT(), getContactCourant().getDescription());
		addZone(getNOM_LB_TCONTACT_SELECT(), String.valueOf(ligneType));
		addZone(getNOM_RG_CONTACT_DIFF(), (c != null && c.isDiffusable()) ? getNOM_RB_CONTACT_DIFF_OUI()
				: getNOM_RB_CONTACT_DIFF_NON());
		addZone(getNOM_RG_CONTACT_PRIORITAIRE(), (c != null && c.isPrioritaire()) ? getNOM_RB_CONTACT_PRIORITAIRE_OUI()
				: getNOM_RB_CONTACT_PRIORITAIRE_NON());

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_VALIDER_CONTACT());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_CONTACT(HttpServletRequest request, int elemASupprimer) throws Exception {

		addZone(getNOM_ST_ACTION_CONTACT(), ACTION_SUPPRESSION);

		// Récup du contact courant
		Contact c = (Contact) getListeContact().get(elemASupprimer);
		setContactCourant(c);
		TypeContact t = (TypeContact) getHashTypeContact().get(c.getIdTypeContact());

		// Alim zones
		addZone(getNOM_ST_LIBELLE_CONTACT(), c.getDescription());
		addZone(getNOM_ST_TCONTACT(), t.getLibelle());
		addZone(getNOM_RG_CONTACT_DIFF(), (c != null && c.isDiffusable()) ? getNOM_RB_CONTACT_DIFF_OUI()
				: getNOM_RB_CONTACT_DIFF_NON());
		addZone(getNOM_RG_CONTACT_PRIORITAIRE(), (c != null && c.isPrioritaire()) ? getNOM_RB_CONTACT_PRIORITAIRE_OUI()
				: getNOM_RB_CONTACT_PRIORITAIRE_NON());

		setFocus(getNOM_PB_VALIDER_CONTACT());
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_CONTACT_PRIORITAIRE Date de création : (14/04/11 15:28:01)
	 * 
	 */
	public String getNOM_RG_CONTACT_PRIORITAIRE() {
		return "NOM_RG_CONTACT_PRIORITAIRE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_CONTACT_PRIORITAIRE Date de création : (14/04/11 15:28:01)
	 * 
	 */
	public String getVAL_RG_CONTACT_PRIORITAIRE() {
		return getZone(getNOM_RG_CONTACT_PRIORITAIRE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONTACT_PRIORITAIRE_NON
	 * Date de création : (14/04/11 15:28:01)
	 * 
	 */
	public String getNOM_RB_CONTACT_PRIORITAIRE_NON() {
		return "NOM_RB_CONTACT_PRIORITAIRE_NON";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONTACT_PRIORITAIRE_OUI
	 * Date de création : (14/04/11 15:28:01)
	 * 
	 */
	public String getNOM_RB_CONTACT_PRIORITAIRE_OUI() {
		return "NOM_RB_CONTACT_PRIORITAIRE_OUI";
	}

	public CollectiviteDao getCollectiviteDao() {
		return collectiviteDao;
	}

	public void setCollectiviteDao(CollectiviteDao collectiviteDao) {
		this.collectiviteDao = collectiviteDao;
	}

	public EtatServiceMilitaireDao getEtatServiceMilitaireDao() {
		return etatServiceMilitaireDao;
	}

	public void setEtatServiceMilitaireDao(EtatServiceMilitaireDao etatServiceMilitaireDao) {
		this.etatServiceMilitaireDao = etatServiceMilitaireDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public ContactDao getContactDao() {
		return contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}

	public TypeContactDao getTypeContactDao() {
		return typeContactDao;
	}

	public void setTypeContactDao(TypeContactDao typeContactDao) {
		this.typeContactDao = typeContactDao;
	}

	public SituationFamilialeDao getSituationFamilialeDao() {
		return situationFamilialeDao;
	}

	public void setSituationFamilialeDao(SituationFamilialeDao situationFamilialeDao) {
		this.situationFamilialeDao = situationFamilialeDao;
	}
}
