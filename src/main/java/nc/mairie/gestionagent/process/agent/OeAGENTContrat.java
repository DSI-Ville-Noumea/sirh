package nc.mairie.gestionagent.process.agent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeContrat;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.referentiel.Motif;
import nc.mairie.metier.referentiel.TypeContrat;
import nc.mairie.spring.dao.metier.agent.ContratDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.referentiel.MotifDao;
import nc.mairie.spring.dao.metier.referentiel.TypeContratDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTContrat Date de création : (16/05/11 09:36:20)
 * 
 */
public class OeAGENTContrat extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OeAGENTContrat.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_MOTIF;
	private String[] LB_TYPE_CONTRAT;

	public String ACTION_SUPPRESSION = "Suppression d'un contrat.";
	public String ACTION_CONSULTATION = "Consultation d'un contrat.";
	public String ACTION_MODIFICATION = "Modification d'un contrat.";
	public String ACTION_CREATION = "Création d'un contrat.";
	public String ACTION_IMPRESSION = "Impression d'un contrat.";

	public String CHOIX_CONTRAT_O = "Oui";
	public String CHOIX_CONTRAT_N = "Non";

	private Agent agentCourant;
	private TypeContrat typeContratCourant;
	private Motif motifCourant;
	private ArrayList<Contrat> listeContrat;
	private ArrayList<TypeContrat> listeTypeContrat;
	private Hashtable<Integer, TypeContrat> hashTypeContrat;
	private ArrayList<Motif> listeMotif;
	private Hashtable<Integer, Motif> hashMotif;
	private Contrat contratCourant;
	private Contrat contratReference;
	private String focus = null;
	private String urlFichier;

	String messageInfo = null;

	private MotifDao motifDao;
	private TypeContratDao typeContratDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private ContratDao contratDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			initialiseListeContratsAgent(request);
		}
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AVENANT Date de création :
	 * (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_PB_AVENANT() {
		return "NOM_PB_AVENANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public boolean performPB_AVENANT(HttpServletRequest request) throws Exception {
		boolean res = true;

		if (getVAL_RG_AVENANT().equals(getNOM_RB_AVENANT_O())) {
			// Contrat c = Contrat.chercherContratCourant(getTransaction(),
			// getAgentCourant());
			Contrat c = getContratDao().chercherDernierContrat(getAgentCourant().getIdAgent());
			setContratReference(c);

			if (getContratReference() != null) {
				addZone(getNOM_ST_NUM_CONTRAT_REF(), getContratReference().getNumContrat());
				addZone(getNOM_LB_TYPE_CONTRAT_SELECT(),
						String.valueOf(getListeTypeContrat().indexOf(
								getHashTypeContrat().get(getContratReference().getIdTypeContrat()))));
				Motif m = (Motif) getHashMotif().get(getContratReference().getIdMotif());
				setMotifCourant(m);
				int ligneMotif = getListeMotif().indexOf(m);
				addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif));
				addZone(getNOM_EF_JUSTIFICATION(), getContratReference().getJustification());
				// on met la date de debut du contrat si CDD a datefin
				// contratRef +1
				if (getTypeContratDao().chercherTypeContrat(getContratReference().getIdTypeContrat())
						.getLibTypeContrat().equals("CDD")) {
					addZone(getNOM_EF_DATE_DEB(),
							Services.ajouteJours(sdf.format(getContratReference().getDateFin()), 1));
				}
			}
		}

		return res;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// init du contrat courant
		setContratCourant(new Contrat());
		setContratReference(null);

		// On vide la zone de saisie
		initialiseContratVide();

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		addZone(getNOM_ST_CHOIX_CONTRAT(), CHOIX_CONTRAT_O);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/05/11 09:36:20)
	 * 
	 * RG_AG_CON_C09
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
			getContratDao().supprimerContrat(getContratCourant().getIdContrat());
			if (getTransaction().isErreur())
				return false;

		} else {

			// Récup des zones saisies
			String newIndTypeContrat = getZone(getNOM_LB_TYPE_CONTRAT_SELECT());
			TypeContrat newTypeContrat = (TypeContrat) getListeTypeContrat().get(Integer.parseInt(newIndTypeContrat));
			String newIndMotif = getZone(getNOM_LB_MOTIF_SELECT());
			Motif newMotif = (Motif) getListeMotif().get(Integer.parseInt(newIndMotif));
			boolean newAvenant = getVAL_RG_AVENANT().equals(getNOM_RB_AVENANT_O()) ? true : false;
			String newDateDebut = Services.formateDate(getZone(getNOM_EF_DATE_DEB()));
			String newDateFinPeriodeEssai = Services.formateDate(getZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI()));
			String newDateFin = Services.formateDate(getZone(getNOM_EF_DATE_FIN()));
			String newJustification = getZone(getNOM_EF_JUSTIFICATION());

			// Affectation des attributs
			getContratCourant().setIdAgent(getAgentCourant().getIdAgent());
			getContratCourant().setAvenant(newAvenant);
			if (newAvenant)
				getContratCourant().setIdContratRef(getContratReference().getIdContrat());
			getContratCourant().setDatdeb(sdf.parse(newDateDebut));
			getContratCourant().setDateFinPeriodeEss(
					newDateFinPeriodeEssai == null ? null : sdf.parse(newDateFinPeriodeEssai));
			getContratCourant().setDateFin(newDateFin == null ? null : sdf.parse(newDateFin));
			getContratCourant().setIdMotif(newMotif.getIdMotif());
			getContratCourant().setIdTypeContrat(newTypeContrat.getIdTypeContrat());
			getContratCourant().setJustification(newJustification);

			if (!performControlerChamps(request)) {
				return false;
			}

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {

				// Modification
				getContratDao().modifierContrat(getContratCourant().getIdContrat(),
						getContratCourant().getIdTypeContrat(), getContratCourant().getIdMotif(),
						getContratCourant().getIdAgent(), getContratCourant().getIdDocument(),
						getContratCourant().getNumContrat(), getContratCourant().isAvenant(),
						getContratCourant().getIdContratRef(), getContratCourant().getDatdeb(),
						getContratCourant().getDateFinPeriodeEss(), getContratCourant().getDateFin(),
						getContratCourant().getJustification());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				// RG_AG_CON_C09
				String numSeq = getContratDao().getNumContratChrono();

				getContratCourant().setNumContrat(
						Services.dateDuJour().substring(6) + "/" + Services.lpad(numSeq, 5, "0"));
				getContratDao().creerContrat(getContratCourant().getIdTypeContrat(), getContratCourant().getIdMotif(),
						getContratCourant().getIdAgent(), getContratCourant().getIdDocument(),
						getContratCourant().getNumContrat(), getContratCourant().isAvenant(),
						getContratCourant().getIdContratRef(), getContratCourant().getDatdeb(),
						getContratCourant().getDateFinPeriodeEss(), getContratCourant().getDateFin(),
						getContratCourant().getJustification());
			}
			if (getTransaction().isErreur())
				return false;
		}

		// Tout s'est bien passé
		// Si message informatif déclarer message
		if (messageInfo != null)
			getTransaction().declarerErreur(messageInfo);
		messageInfo = null;

		commitTransaction();
		initialiseListeContratsAgent(request);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...)
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		TypeContrat tc = (TypeContrat) getListeTypeContrat().get(
				Integer.parseInt(getZone(getNOM_LB_TYPE_CONTRAT_SELECT())));
		messageInfo = null;

		// **********************************************************
		// RG_AG_CON_C01 : date de début obligatoire et bien formatée
		// **********************************************************
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DEB()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date de début"));
			return false;
		}

		if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEB()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
			return false;
		}

		// ****************************************************************************************************************
		// RG_AG_CON_C01 : Si le contrat n'est pas un avenant, date de fin de
		// période d'essai obligatoire et bien formatée.
		// ****************************************************************************************************************
		if (!getVAL_RG_AVENANT().equals(getNOM_RB_AVENANT_O())) {
			if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date de fin de période d'essai"));
				setFocus(getNOM_EF_DATE_FIN_PERIODE_ESSAI());
				return false;
			}
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin de période d'essai"));
				setFocus(getNOM_EF_DATE_FIN_PERIODE_ESSAI());
				return false;
			}

			// ******************************************************
			// RG_AG_CON_C07 : date de fin période essai > date début
			// ******************************************************
			if (Services.compareDates(getZone(getNOM_EF_DATE_DEB()), getZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI())) >= 0) {
				// ERR205 : La date @ doit être supérieure à la date @.
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR205", "de fin de période d'essai", "de début"));
				setFocus(getNOM_EF_DATE_FIN_PERIODE_ESSAI());
				return false;
			}
		}

		// *****************************************
		// RG_AG_CON_C01 : justification obligatoire
		// *****************************************
		if (!(getZone(getNOM_RG_AVENANT()).equals(getNOM_RB_AVENANT_O()) && tc.getLibTypeContrat().equals(
				EnumTypeContrat.CDD.getValue()))
				&& (Const.CHAINE_VIDE).equals(getZone(getNOM_EF_JUSTIFICATION()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Justification"));
			setFocus(getNOM_EF_JUSTIFICATION());
			return false;
		}

		// **************************************************
		// RG_AG_CON_C05 : date de fin > date début si saisie
		// **************************************************
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin"));
				setFocus(getNOM_EF_DATE_FIN());
				return false;
			}

			if (Services.compareDates(getZone(getNOM_EF_DATE_DEB()), getZone(getNOM_EF_DATE_FIN())) >= 0) {
				// ERR205 : La date @ doit être supérieure à la date @.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "de début"));
				setFocus(getNOM_EF_DATE_FIN());
				return false;
			}

		} else {
			// ************************************************************
			// RG_AG_CON_C11 : date de fin obligatoire dans le cas d'un CDD
			// ************************************************************
			if (tc.getIdTypeContrat() == EnumTypeContrat.CDD.getCode()) {
				// ERR033 : La date de fin est obligatoire dans le cas d'un CDD.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR033"));
				setFocus(getNOM_EF_DATE_FIN());
				return false;
			}
		}

		// ***************************************************************************
		// RG_AG_CON_C02 : Un agent ne peut avoir qu'un seul contrat a une date
		// donnée
		// ***************************************************************************
		if (!getContratCourant().isAvenant()) {

			boolean rgOK = true;
			ArrayList<Contrat> lc = getContratDao().listerContratAvecAgent(getAgentCourant().getIdAgent());
			for (int i = 0; i < lc.size(); i++) {
				Contrat c = (Contrat) lc.get(i);
				if (!c.getIdContrat().equals(getContratCourant().getIdContrat())) {
					if (Services.compareDates(getZone(getNOM_EF_DATE_DEB()), sdf.format(c.getDatdeb())) >= 0
							&& ((c.getDateFin() == null || Services.compareDates(sdf.format(c.getDateFin()),
									getZone(getNOM_EF_DATE_DEB())) >= 0))) {
						rgOK = false;
					} else if (Services.compareDates(getZone(getNOM_EF_DATE_FIN()), sdf.format(c.getDatdeb())) >= 0
							&& ((c.getDateFin() == null || Services.compareDates(sdf.format(c.getDateFin()),
									getZone(getNOM_EF_DATE_DEB())) >= 0))) {
						rgOK = false;
					}
				}
			}

			if (!rgOK) {
				// ERR032:Un agent ne peut avoir qu'un seul contrat a une date
				// donnée.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR032"));
				return false;
			}
		}

		/*
		 * Si le contrat est un avenant
		 */
		if (getContratCourant().isAvenant()) {
			if (getContratReference().getIdTypeContrat().toString().equals(EnumTypeContrat.CDI.getCode().toString())) {
				// le contrat reference est un CDI

				if (Services.compareDates(sdf.format(getContratCourant().getDatdeb()),
						sdf.format(getContratReference().getDatdeb())) <= 0) {
					// "ERR205", "La date @ doit être supérieure à la date @."
					getTransaction().declarerErreur(
							MessageUtils
									.getMessage("ERR205", "de début de contrat", "de début du contrat de référence"));
					return false;
				}

			} else {
				// le contrat reference est un CDD

				ArrayList<Contrat> listeAvenant = getContratDao().listerContratAvenantAvecContratReference(
						getContratReference().getIdContrat());
				if (listeAvenant.size() == 0) {
					// 1er Avenant pour ce CDD : on Vérifie la date de début
					// avec la date de fin du contrat reference
					if (Services.compareDates(sdf.format(getContratCourant().getDatdeb()),
							sdf.format(getContratReference().getDateFin())) <= 0) {
						// "ERR205",
						// "La date @ doit être supérieure à la date @."
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR205", "de début de contrat",
										"de fin du contrat de référence"));
						return false;
					}
				} else {
					// Un ou des evenants pour ce contrat exite déjà : on
					// verifie la date du debut avec la date de fin du dernier
					// avenant
					if (Services.compareDates(sdf.format(getContratCourant().getDatdeb()),
							sdf.format(listeAvenant.get(listeAvenant.size() - 1).getDateFin())) <= 0) {
						// "ERR205",
						// "La date @ doit être supérieure à la date @."
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR205", "de début de contrat", "de fin du dernier avenant"));
						return false;
					}
				}
				if (listeAvenant.size() >= 3) {
					// "INF007",
					// "Attention : ce contrat a déjà 3 avenants. Veuillez Vérifier son motif."
					messageInfo = MessageUtils.getMessage("INF007");
				}
			}
		}

		return true;
	}

	/**
	 * Vérifie les regles de gestion spécifiques à la modification
	 * 
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 */
	public boolean performControlerRGModification() throws Exception {

		// ******************************************************************************
		// RG_AG_CON_C03 : une modification ne peut se faire que sur un contrat
		// en cours.
		// ******************************************************************************
		boolean rgOK = true;
		TypeContrat tc = (TypeContrat) getListeTypeContrat().get(
				Integer.parseInt(getZone(getNOM_LB_TYPE_CONTRAT_SELECT())));
		if (tc.getLibTypeContrat().equals(EnumTypeContrat.CDD.getValue())) {
			if (Services.compareDates(Services.dateDuJour(), getZone(getNOM_EF_DATE_DEB())) < 0
					|| Services.compareDates(getZone(getNOM_EF_DATE_FIN()), Services.dateDuJour()) < 0) {
				rgOK = false;
			}
			if (Services.compareDates(Services.dateDuJour(), getZone(getNOM_EF_DATE_DEB())) <= 0) {
				rgOK = true;
			}
		} else if (tc.getLibTypeContrat().equals(EnumTypeContrat.CDI.getValue())) {
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
				rgOK = false;
			}
		}
		if (!rgOK) {
			// ERR030:Une @ ne peut se faire que sur un contrat en cours.
			// getTransaction().declarerErreur(MessageUtils.getMessage("ERR030","modification"));
			setFocus(getNOM_EF_DATE_FIN());
		}

		return rgOK;
	}

	/**
	 * Vérifie les regles de gestion spécifiques a l'impression
	 * 
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 */
	public boolean performControlerRGImpression() throws Exception {

		// ******************************************************************************
		// RG_AG_CON_C03 : une impression ne peut se faire que sur un contrat
		// CDD.
		// ******************************************************************************
		TypeContrat tc = (TypeContrat) getListeTypeContrat().get(
				Integer.parseInt(getZone(getNOM_LB_TYPE_CONTRAT_SELECT())));
		if (tc.getLibTypeContrat().equals(EnumTypeContrat.CDI.getValue())) {
			return false;
		}

		return true;
	}

	/**
	 * Vérifie si le contrat de référence est saisi dans le cas d'une création
	 * d'avenant.
	 * 
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 */
	public boolean performControlerRGContratRefObligatoire() throws Exception {

		// **********************************************************************
		// RG_AG_CON_C08 : Num de contrat référence obligatoire si Avenant =
		// Oui.
		// **********************************************************************
		boolean rgOK = true;

		if (getZone(getNOM_RG_AVENANT()).equals(getNOM_RB_AVENANT_O()) && getContratReference() == null) {
			// ERR031:Le numéro de contrat de référence est obligatoire dans le
			// cas d'un avenant.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR031"));
			return false;
		}

		return rgOK;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_CONTRAT Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_NUM_CONTRAT() {
		return "NOM_ST_NUM_CONTRAT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_CONTRAT
	 * Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_NUM_CONTRAT() {
		return getZone(getNOM_ST_NUM_CONTRAT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_CONTRAT_REF Date
	 * de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_NUM_CONTRAT_REF() {
		return "NOM_ST_NUM_CONTRAT_REF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_CONTRAT_REF Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_NUM_CONTRAT_REF() {
		return getZone(getNOM_ST_NUM_CONTRAT_REF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEB Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_EF_DATE_DEB() {
		return "NOM_EF_DATE_DEB";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEB Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_EF_DATE_DEB() {
		return getZone(getNOM_EF_DATE_DEB());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_FIN_PERIODE_ESSAI Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_EF_DATE_FIN_PERIODE_ESSAI() {
		return "NOM_EF_DATE_FIN_PERIODE_ESSAI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN_PERIODE_ESSAI Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_EF_DATE_FIN_PERIODE_ESSAI() {
		return getZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_JUSTIFICATION Date
	 * de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_EF_JUSTIFICATION() {
		return "NOM_EF_JUSTIFICATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_JUSTIFICATION Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_EF_JUSTIFICATION() {
		return getZone(getNOM_EF_JUSTIFICATION());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de création :
	 * (16/05/11 09:36:20)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de création : (16/05/11 09:36:20)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de création :
	 * (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MOTIF Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_CONTRAT Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	private String[] getLB_TYPE_CONTRAT() {
		if (LB_TYPE_CONTRAT == null)
			LB_TYPE_CONTRAT = initialiseLazyLB();
		return LB_TYPE_CONTRAT;
	}

	/**
	 * Setter de la liste: LB_TYPE_CONTRAT Date de création : (16/05/11
	 * 09:36:20)
	 * 
	 */
	private void setLB_TYPE_CONTRAT(String[] newLB_TYPE_CONTRAT) {
		LB_TYPE_CONTRAT = newLB_TYPE_CONTRAT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_CONTRAT Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_LB_TYPE_CONTRAT() {
		return "NOM_LB_TYPE_CONTRAT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_CONTRAT_SELECT Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_LB_TYPE_CONTRAT_SELECT() {
		return "NOM_LB_TYPE_CONTRAT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_CONTRAT Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String[] getVAL_LB_TYPE_CONTRAT() {
		return getLB_TYPE_CONTRAT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_CONTRAT Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_LB_TYPE_CONTRAT_SELECT() {
		return getZone(getNOM_LB_TYPE_CONTRAT_SELECT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_AVENANT
	 * Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_RG_AVENANT() {
		return "NOM_RG_AVENANT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_AVENANT
	 * Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_RG_AVENANT() {
		return getZone(getNOM_RG_AVENANT());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AVENANT_N Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_RB_AVENANT_N() {
		return "NOM_RB_AVENANT_N";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AVENANT_O Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_RB_AVENANT_O() {
		return "NOM_RB_AVENANT_O";
	}

	/**
	 * Retourne le focus de la JSP.
	 * 
	 * @return String
	 */
	public String getFocus() {
		return focus;
	}

	/**
	 * Met a jour le focus de la JSP.
	 * 
	 * @param focus
	 */
	private void setFocus(String focus) {
		this.focus = focus;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getMotifDao() == null) {
			setMotifDao(new MotifDao((SirhDao) context.getBean("sirhDao")));
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
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (16/05/11 10:43:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Motifs
		initialiseListeMotif(request);

		// Types de contrat
		initialiseListeTypeContrat(request);

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeContratsAgent(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	/**
	 * Initialisation de la liste des contrats de l'agent
	 * 
	 */
	private void initialiseListeContratsAgent(HttpServletRequest request) throws Exception {

		// Recherche des contrats de l'agent
		ArrayList<Contrat> lc = getContratDao().listerContratAvecAgent(getAgentCourant().getIdAgent());
		setListeContrat(lc);

		int indiceContrat = 0;
		if (getListeContrat() != null) {
			for (int i = 0; i < getListeContrat().size(); i++) {
				Contrat c = (Contrat) getListeContrat().get(i);
				TypeContrat t = (TypeContrat) getHashTypeContrat().get(c.getIdTypeContrat());
				Motif m = (Motif) getHashMotif().get(c.getIdMotif());

				addZone(getNOM_ST_NUM(indiceContrat),
						c.getNumContrat().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getNumContrat());
				addZone(getNOM_ST_TYPE(indiceContrat),
						t.getLibTypeContrat().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getLibTypeContrat());
				addZone(getNOM_ST_AVENANT(indiceContrat), c.isAvenant() ? "Oui" : "Non");
				addZone(getNOM_ST_DATE_DEBUT(indiceContrat), sdf.format(c.getDatdeb()));
				addZone(getNOM_ST_DATE_ESSAI(indiceContrat),
						c.getDateFinPeriodeEss() == null ? "&nbsp;" : sdf.format(c.getDateFinPeriodeEss()));
				addZone(getNOM_ST_DATE_FIN(indiceContrat),
						c.getDateFin() == null ? "&nbsp;" : sdf.format(c.getDateFin()));
				addZone(getNOM_ST_MOTIF(indiceContrat),
						m.getLibMotif().equals(Const.CHAINE_VIDE) ? "&nbsp;" : m.getLibMotif());
				addZone(getNOM_ST_JUSTIFICATION(indiceContrat),
						c.getJustification().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getJustification());

				indiceContrat++;
			}
		}

		initialiseContratVide();
	}

	/**
	 * Initialise a vide les zones du contrat.
	 */
	public void initialiseContratVide() {
		addZone(getNOM_ST_NUM_CONTRAT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_CONTRAT_SELECT(), "0");
		addZone(getNOM_RG_AVENANT(), getNOM_RB_AVENANT_N());
		addZone(getNOM_EF_DATE_DEB(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MOTIF_SELECT(), "0");
		addZone(getNOM_EF_JUSTIFICATION(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_NUM_CONTRAT_REF(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NUM_CONTRAT_REF(), Const.CHAINE_VIDE);
	}

	/**
	 * Initialise les zones du contrat courant
	 * 
	 */
	private boolean initialiseContratCourant(HttpServletRequest request) throws Exception {
		Contrat c = getContratCourant();
		Contrat reference = null;
		if (c.getIdContratRef() != null)
			reference = getContratDao().chercherContrat(c.getIdContratRef());
		setContratReference(reference);

		TypeContrat t = (TypeContrat) getHashTypeContrat().get(c.getIdTypeContrat());
		setTypeContratCourant(t);
		Motif m = (Motif) getHashMotif().get(c.getIdMotif());
		setMotifCourant(m);

		// Alim zones
		int ligneTypeContrat = getListeTypeContrat().indexOf(t);
		int ligneMotif = getListeMotif().indexOf(m);
		addZone(getNOM_ST_NUM_CONTRAT(), c.getNumContrat());
		addZone(getNOM_LB_TYPE_CONTRAT_SELECT(), String.valueOf(ligneTypeContrat));
		addZone(getNOM_ST_TYPE_CONTRAT(), t.getLibTypeContrat());
		addZone(getNOM_RG_AVENANT(), c.isAvenant() ? getNOM_RB_AVENANT_O() : getNOM_RB_AVENANT_N());
		addZone(getNOM_EF_DATE_DEB(), c.getDatdeb() == null ? null : sdf.format(c.getDatdeb()));
		addZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI(),
				c.getDateFinPeriodeEss() == null ? null : sdf.format(c.getDateFinPeriodeEss()));
		addZone(getNOM_EF_DATE_FIN(), c.getDateFin() == null ? null : sdf.format(c.getDateFin()));
		addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif));
		addZone(getNOM_ST_MOTIF(), m.getLibMotif());
		addZone(getNOM_EF_JUSTIFICATION(), c.getJustification());
		addZone(getNOM_ST_NUM_CONTRAT_REF(), reference != null ? reference.getNumContrat() : Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Initialise la liste des types de contrat.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void initialiseListeTypeContrat(HttpServletRequest request) throws Exception {
		// Si liste des type de contrat vide
		if (getLB_TYPE_CONTRAT() == LBVide) {
			ArrayList<TypeContrat> a = (ArrayList<TypeContrat>) getTypeContratDao().listerTypeContrat();
			setListeTypeContrat(a);

			int[] tailles = { 5 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<TypeContrat> list = getListeTypeContrat().listIterator(); list.hasNext();) {
				TypeContrat fili = (TypeContrat) list.next();
				String ligne[] = { fili.getLibTypeContrat() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_CONTRAT(aFormat.getListeFormatee());

			// Si hashtable des types contrat vide
			if (getHashTypeContrat().size() == 0) {
				// remplissage de la hashTable
				for (int i = 0; i < a.size(); i++) {
					TypeContrat aTypeContrat = (TypeContrat) a.get(i);
					getHashTypeContrat().put(aTypeContrat.getIdTypeContrat(), aTypeContrat);
				}
			}
			setTypeContratCourant(((TypeContrat) getListeTypeContrat().get(0)));
			addZone(getNOM_ST_TYPE_CONTRAT(), getTypeContratCourant().getLibTypeContrat());
		}
	}

	/**
	 * Initialise la liste des motifs.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		// Si liste des motifs vide
		if (getLB_MOTIF() == LBVide) {
			ArrayList<Motif> a = (ArrayList<Motif>) getMotifDao().listerMotif();
			setListeMotif(a);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Motif> list = getListeMotif().listIterator(); list.hasNext();) {
				Motif fili = (Motif) list.next();
				String ligne[] = { fili.getLibMotif() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
			addZone(getNOM_ST_MOTIF(), ((Motif) getListeMotif().get(0)).getLibMotif());

			// Si hashtable des motifs vide
			if (getHashMotif().size() == 0) {
				// remplissage de la hashTable
				for (int i = 0; i < a.size(); i++) {
					Motif aMotif = (Motif) a.get(i);
					getHashMotif().put(aMotif.getIdMotif(), aMotif);
				}
			}
			setMotifCourant(((Motif) getListeMotif().get(0)));
			addZone(getNOM_ST_MOTIF(), getMotifCourant().getLibMotif());
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * création : (16/05/11 10:43:25)
	 * 
	 */
	public String getNOM_ST_MOTIF() {
		return "NOM_ST_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de
	 * création : (16/05/11 10:43:25)
	 * 
	 */
	public String getVAL_ST_MOTIF() {
		return getZone(getNOM_ST_MOTIF());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_CONTRAT Date de
	 * création : (16/05/11 10:43:25)
	 * 
	 */
	public String getNOM_ST_TYPE_CONTRAT() {
		return "NOM_ST_TYPE_CONTRAT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_CONTRAT
	 * Date de création : (16/05/11 10:43:25)
	 * 
	 */
	public String getVAL_ST_TYPE_CONTRAT() {
		return getZone(getNOM_ST_TYPE_CONTRAT());
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return Agent
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

	/**
	 * Retourne la liste des contrats.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Contrat> getListeContrat() {
		return listeContrat;
	}

	/**
	 * Met a jour la liste des contrats
	 * 
	 * @param listeContrat
	 *            ArrayList
	 */
	private void setListeContrat(ArrayList<Contrat> listeContrat) {
		this.listeContrat = listeContrat;
	}

	/**
	 * Retourne le contrat courant.
	 * 
	 * @return Contrat
	 */
	private Contrat getContratCourant() {
		return contratCourant;
	}

	/**
	 * Met a jour le contrat courant.
	 * 
	 * @param contratCourant
	 *            Contrat
	 */
	private void setContratCourant(Contrat contratCourant) {
		this.contratCourant = contratCourant;
	}

	/**
	 * Retourne la liste de motifs.
	 * 
	 * @return ArrayList
	 */
	private ArrayList<Motif> getListeMotif() {
		return listeMotif;
	}

	/**
	 * Met a jour la liste de motifs
	 * 
	 * @param listeMotif
	 */
	private void setListeMotif(ArrayList<Motif> listeMotif) {
		this.listeMotif = listeMotif;
	}

	/**
	 * Retourne la liste des types de contrat
	 * 
	 * @return ArrayList
	 */
	private ArrayList<TypeContrat> getListeTypeContrat() {
		return listeTypeContrat;
	}

	/**
	 * Initialise la liste des types de contrat
	 * 
	 * @param listeTypeContrat
	 */
	private void setListeTypeContrat(ArrayList<TypeContrat> listeTypeContrat) {
		this.listeTypeContrat = listeTypeContrat;
	}

	/**
	 * Retourne la hashtable des types de contrat
	 * 
	 * @return Hashtable<String, TypeContrat>
	 */
	private Hashtable<Integer, TypeContrat> getHashTypeContrat() {
		if (hashTypeContrat == null) {
			hashTypeContrat = new Hashtable<Integer, TypeContrat>();
		}

		return hashTypeContrat;
	}

	/**
	 * Retourne la hashtable des motifs
	 * 
	 * @return Hashtable<String, Motif>
	 */
	private Hashtable<Integer, Motif> getHashMotif() {
		if (hashMotif == null) {
			hashMotif = new Hashtable<Integer, Motif>();
		}

		return hashMotif;
	}

	/**
	 * Retourne le motif courant.
	 * 
	 * @return Motif
	 */
	private Motif getMotifCourant() {
		return motifCourant;
	}

	/**
	 * Met a jour le motif courant.
	 * 
	 * @param motifCourant
	 *            Motif
	 */
	private void setMotifCourant(Motif motifCourant) {
		this.motifCourant = motifCourant;
	}

	/**
	 * Retourne le type de contrat courant.
	 * 
	 * @return TypeContrat
	 */
	private TypeContrat getTypeContratCourant() {
		return typeContratCourant;
	}

	/**
	 * Met a jour le type de contrat courant.
	 * 
	 * @param typeContratCourant
	 */
	private void setTypeContratCourant(TypeContrat typeContratCourant) {
		this.typeContratCourant = typeContratCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_INIT_FIN_PERIODE_ESSAI Date
	 * de création : (17/05/11 16:01:36)
	 * 
	 */
	public String getNOM_PB_INIT_FIN_PERIODE_ESSAI() {
		return "NOM_PB_INIT_FIN_PERIODE_ESSAI";
	}

	/**
	 * Initialise le champ de fin de période d'essai a partir de la date de
	 * début et du type de contrat. Date de création : (17/05/11 16:01:36)
	 * 
	 */
	public boolean performPB_INIT_FIN_PERIODE_ESSAI(HttpServletRequest request) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		if (!getVAL_RG_AVENANT().equals(getNOM_RB_AVENANT_O())
				&& Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI()))
				&& !Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEB()))
				&& Services.estUneDate(getZone(getNOM_EF_DATE_DEB()))) {
			String datePeriodeEssai = Const.CHAINE_VIDE;
			TypeContrat tc = (TypeContrat) getListeTypeContrat().get(
					Integer.parseInt(getZone(getNOM_LB_TYPE_CONTRAT_SELECT())));
			if (tc.getLibTypeContrat().equals(EnumTypeContrat.CDI.getValue())) {
				// si CDI 3mois
				datePeriodeEssai = Services.ajouteMois(Services.formateDate(getZone(getNOM_EF_DATE_DEB())), 3);
			} else {
				// si CDD et date fin saisie
				if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))
						&& Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
					// si + de 1an entre les dates alors dateFin = DateDeb+1mois
					// si entre 6 mois et 1an alors dateFin = DateDeb+14 jours
					// si inf a 6 mois alors dateFin = DateDeb+1 jours par
					// semaine dans limite des 14jours
					String datedebut = Services.formateDate(getZone(getNOM_EF_DATE_DEB()));
					String datefin = Services.formateDate(getZone(getNOM_EF_DATE_FIN()));
					String dateDebFinale = null;
					String dateFinFinale = null;
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
						Date dateDeb = sdf.parse(datedebut);
						Date dateFin = sdf.parse(datefin);
						SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
						dateDebFinale = sdf2.format(dateDeb);
						dateFinFinale = sdf2.format(dateFin);
					} catch (Exception e) {
						// on essaye un autre format
						try {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							Date dateDeb = sdf.parse(datedebut);
							Date dateFin = sdf.parse(datefin);
							SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
							dateDebFinale = sdf2.format(dateDeb);
							dateFinFinale = sdf2.format(dateFin);
						} catch (Exception e2) {
							// on ne fait rien
						}
					}
					int nbJours = Services.compteJoursEntreDates(dateDebFinale, dateFinFinale);
					if (nbJours >= 365) {
						datePeriodeEssai = Services.ajouteMois(Services.formateDate(dateDebFinale), 1);
					} else if (182 <= nbJours && nbJours < 365) {
						datePeriodeEssai = Services.ajouteJours(Services.formateDate(dateDebFinale), 14);
					} else if (nbJours < 182) {
						// on calcul le nombre de semaine
						int nbSemaines = nbJours / 7;
						if (nbSemaines > 14) {
							nbSemaines = 14;
						}
						datePeriodeEssai = Services.ajouteJours(Services.formateDate(dateDebFinale), nbSemaines);
					} else {
						datePeriodeEssai = Const.CHAINE_VIDE;
					}
				} else {
					datePeriodeEssai = Const.CHAINE_VIDE;
				}
			}
			addZone(getNOM_EF_DATE_FIN_PERIODE_ESSAI(), datePeriodeEssai);
			// On pose le statut
			setStatut(STATUT_MEME_PROCESS);
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHOIX_CONTRAT Date
	 * de création : (23/05/11 10:09:01)
	 * 
	 */
	public String getNOM_ST_CHOIX_CONTRAT() {
		return "NOM_ST_CHOIX_CONTRAT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CHOIX_CONTRAT
	 * Date de création : (23/05/11 10:09:01)
	 * 
	 */
	public String getVAL_ST_CHOIX_CONTRAT() {
		return getZone(getNOM_ST_CHOIX_CONTRAT());
	}

	/**
	 * Constructeur du process OeAGENTContrat. Date de création : (23/05/11
	 * 10:09:49)
	 * 
	 */
	public OeAGENTContrat() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de création :
	 * (23/05/11 10:09:49)
	 * 
	 */
	public String getNOM_PB_OK() {
		return "NOM_PB_OK";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/05/11 10:09:49)
	 * 
	 */
	public boolean performPB_OK(HttpServletRequest request) throws Exception {
		if (performControlerRGContratRefObligatoire()) {
			// Type de contrat déjà choisi. Sert a l'affichage des champs a
			// saisir dans la JSP
			addZone(getNOM_ST_CHOIX_CONTRAT(), CHOIX_CONTRAT_N);

			// Initialise le type de contrat choisi
			String newIndTypeContrat = getZone(getNOM_LB_TYPE_CONTRAT_SELECT());
			setTypeContratCourant((TypeContrat) getListeTypeContrat().get(Integer.parseInt(newIndTypeContrat)));
			addZone(getNOM_ST_TYPE_CONTRAT(), getTypeContratCourant().getLibTypeContrat());
		} else {
			return false;
		}
		return true;
	}

	private Contrat getContratReference() {
		return contratReference;
	}

	private void setContratReference(Contrat contratReferance) {
		this.contratReference = contratReferance;
	}

	public String getNomEcran() {
		return "ECR-AG-DP-CONTRAT";
	}

	private boolean verifieExistFichier(Integer idContrat) throws Exception {
		// on regarde si le fichier existe
		try {
			getDocumentDao().chercherDocumentByContainsNom("C_" + idContrat);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER
	 */
	public String getNOM_PB_VALIDER_IMPRIMER() {
		return "NOM_PB_VALIDER_IMPRIMER";
	}

	public boolean performPB_VALIDER_IMPRIMER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}
		imprimeModele(request);

		return true;
	}

	private boolean imprimeModele(HttpServletRequest request) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("C");

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destination = "C/C_" + getContratCourant().getIdContrat() + ".doc";

		// si le fichier existe alors on supprime l'entrée ou il y a le fichier
		if (verifieExistFichier(getContratCourant().getIdContrat())) {
			Document d = getDocumentDao().chercherDocumentByContainsNom("C_" + getContratCourant().getIdContrat());
			DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(),
					d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
			getDocumentDao().supprimerDocument(d.getIdDocument());
		}

		if (!getTypeContratDao().chercherTypeContrat(getContratCourant().getIdTypeContrat()).getLibTypeContrat()
				.equals("CDD")) {
			// "ERR034", "Une impression ne peut se faire que sur un CDD."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR034"));
			return false;
		}

		try {
			byte[] fileAsBytes = new SirhWSConsumer().downloadContrat(getAgentCourant().getIdAgent(),
					getContratCourant().getIdContrat());

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

			// Tout s'est bien passé
			// on crée le document en base de données
			Document d = new Document();
			d.setIdTypeDocument(2);
			d.setLienDocument(destination);
			d.setNomDocument("C_" + getContratCourant().getIdContrat() + ".doc");
			d.setDateDocument(new Date());
			d.setCommentaire("Document généré par l'application");
			Integer id = getDocumentDao().creerDocument(d.getClasseDocument(), d.getNomDocument(), d.getLienDocument(),
					d.getDateDocument(), d.getCommentaire(), d.getIdTypeDocument(), d.getNomOriginal());

			DocumentAgent lda = new DocumentAgent();
			lda.setIdAgent(getAgentCourant().getIdAgent());
			lda.setIdDocument(id);
			getLienDocumentAgentDao().creerDocumentAgent(lda.getIdAgent(), lda.getIdDocument());

			if (getTransaction().isErreur())
				return false;

			destination = destination.substring(destination.lastIndexOf("/"), destination.length());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
			setURLFichier(getScriptOuverture(repertoireStockage + "C" + destination));

			commitTransaction();

		} catch (Exception e) {
			// "ERR185",
			// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
			return false;
		}

		initialiseListeContratsAgent(request);
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		return true;
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
			logger.error(String.format("An error occured while writing the report file to the following path  : "
					+ chemin + filename + " : " + e));
			return false;
		}
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
		}
		return res;
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_WARNING Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_OK
			if (testerParametre(request, getNOM_PB_OK())) {
				return performPB_OK(request);
			}

			// Si clic sur le bouton PB_INIT_FIN_PERIODE_ESSAI
			if (testerParametre(request, getNOM_PB_INIT_FIN_PERIODE_ESSAI())) {
				return performPB_INIT_FIN_PERIODE_ESSAI(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_AVENANT
			if (testerParametre(request, getNOM_PB_AVENANT())) {
				return performPB_AVENANT(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_IMPRIMER
			if (testerParametre(request, getNOM_PB_VALIDER_IMPRIMER())) {
				return performPB_VALIDER_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeContrat().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeContrat().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeContrat().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_IMPRIMER
			for (int i = 0; i < getListeContrat().size(); i++) {
				if (testerParametre(request, getNOM_PB_IMPRIMER(i))) {
					return performPB_IMPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (18/10/11 16:08:49)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTContrat.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM(int i) {
		return "NOM_ST_NUM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM(int i) {
		return getZone(getNOM_ST_NUM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVENANT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AVENANT(int i) {
		return "NOM_ST_AVENANT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVENANT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AVENANT(int i) {
		return getZone(getNOM_ST_AVENANT(i));
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ESSAI Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_ESSAI(int i) {
		return "NOM_ST_DATE_ESSAI" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ESSAI
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_ESSAI(int i) {
		return getZone(getNOM_ST_DATE_ESSAI(i));
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_JUSTIFICATION Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_JUSTIFICATION(int i) {
		return "NOM_ST_JUSTIFICATION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_JUSTIFICATION
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_JUSTIFICATION(int i) {
		return getZone(getNOM_ST_JUSTIFICATION(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Contrat c = (Contrat) getListeContrat().get(indiceEltAModifier);
		setContratCourant(c);

		// init de l'extrait de casier judiciaire courant
		if (initialiseContratCourant(request)) {

			if (!performControlerRGModification()) {
				// ERR030:Une @ ne peut se faire que sur un contrat en cours.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR030", "modification"));
				setFocus(getNOM_EF_DATE_FIN());
				return false;
			}

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

			// On pose le statut
			setStatut(STATUT_MEME_PROCESS);
		}

		return true;
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

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Contrat c = (Contrat) getListeContrat().get(indiceEltAConsulter);
		setContratCourant(c);

		// init de l'extrait de casier judiciaire courant
		if (!initialiseContratCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
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

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Contrat c = (Contrat) getListeContrat().get(indiceEltASuprimer);
		setContratCourant(c);

		// init du contrat courant
		if (!initialiseContratCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		if (getContratCourant().isAvenant()) {
			addZone(getNOM_ST_WARNING(), "Veuillez valider votre choix.");
		} else {
			addZone(getNOM_ST_WARNING(),
					"Attention : les avenants du contrat seront aussi supprimés. Veuillez valider votre choix.");
		}

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER
	 */
	public String getNOM_PB_IMPRIMER(int i) {
		return "NOM_PB_IMPRIMER" + i;
	}

	public boolean performPB_IMPRIMER(HttpServletRequest request, int indiceEltAImprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// Récup du contrat courant
		Contrat c = (Contrat) getListeContrat().get(indiceEltAImprimer);
		setContratCourant(c);

		if (!initialiseContratCourant(request)) {
			return false;
		}
		if (!performControlerRGImpression()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR034"));
			return false;
		}

		// on verifie si il existe deja un fichier pour ce contrat dans la BD
		if (verifieExistFichier(getContratCourant().getIdContrat())) {
			// alors on affiche un message
			// :" Attention un fichier existe déjà pour ce contrat. Etes-vous sûr de vouloir écraser la version précédente ?"
			addZone(getNOM_ST_WARNING(),
					"Attention un fichier existe déjà pour ce contrat. Etes-vous sûr de vouloir écraser la version précédente ?");
			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
		} else {
			imprimeModele(request);
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public MotifDao getMotifDao() {
		return motifDao;
	}

	public void setMotifDao(MotifDao motifDao) {
		this.motifDao = motifDao;
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
}
