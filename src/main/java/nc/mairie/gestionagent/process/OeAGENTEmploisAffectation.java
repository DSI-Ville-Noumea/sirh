package nc.mairie.gestionagent.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

import nc.mairie.enums.EnumImpressionAffectation;
import nc.mairie.enums.EnumTempsTravail;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.agent.PAAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.parametrage.MotifAffectation;
import nc.mairie.metier.parametrage.SpecialiteDiplomeNW;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Budget;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

/**
 * Process OeAGENTEmploisAffectation Date de création : (04/08/11 15:20:56)
 * 
 */
public class OeAGENTEmploisAffectation extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHE_FP_SECONDAIRE = 4;
	public static final int STATUT_HISTORIQUE = 3;
	public static final int STATUT_VISU_FP = 2;
	public static final int STATUT_RECHERCHE_FP = 1;

	private String[] LB_MOTIF_AFFECTATION;
	private String[] LB_TEMPS_TRAVAIL;
	private String[] LB_LISTE_IMPRESSION;

	private ArrayList listeAffectation;
	private ArrayList listeMotifAffectation;
	private String[] listeTempsTravail;

	public String ACTION_SUPPRESSION = "Suppression d'une affectation";
	public String ACTION_CONSULTATION = "Consultation d'une affectation";
	public String ACTION_MODIFICATION = "Modification d'une affectation";
	public String ACTION_CREATION = "Création d'une affectation";
	public String ACTION_IMPRESSION = "Impression des documents liés à une affectation";

	private AgentNW agentCourant;
	private FichePoste fichePosteCourant;
	private FichePoste fichePosteSecondaireCourant;
	private Affectation affectationCourant;

	public String focus = null;
	private String urlFichier;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialise à vide les zones de l'affectation.
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		return true;
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_HISTORIQUE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_HISTORIQUE, true);
		return true;
	}

	/**
	 * Initialise les zones de l'affectation courant RG_AG_AF_A04 RG_AG_AF_A12
	 */
	private boolean initialiseAffectationCourante(HttpServletRequest request) throws Exception {

		if (getAffectationCourant() == null) {
			initialiseAffectationVide();
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
					setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant().getIdFichePoste()));
					initialiserFichePoste();
				}
			} else {
				if (getAffectationCourant().getIdFichePoste() != null) {
					FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant().getIdFichePoste());

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
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			} else {
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			}

			// Récup du motif d'affectation et temps de travail
			if (getAffectationCourant().getIdMotifAffectation() != null) {
				MotifAffectation ma = (MotifAffectation) MotifAffectation.chercherMotifAffectation(getTransaction(), getAffectationCourant()
						.getIdMotifAffectation());
				addZone(getNOM_LB_MOTIF_AFFECTATION_SELECT(), String.valueOf(getListeMotifAffectation().indexOf(ma)));
				for (int i = 0; i < getListeTempsTravail().length; i++) {
					String tpsT = getListeTempsTravail()[i];
					if (tpsT.equals(getAffectationCourant().getTempsTravail()))
						addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), String.valueOf(i));
				}
			}

		}
		return true;
	}

	/**
	 * Initialise les champs de la fiche de poste courante liée à l'affectation.
	 * 
	 * @throws Exception
	 */
	private void initialiserFichePoste() throws Exception {
		// Titre
		String titreFichePoste = getFichePosteCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : TitrePoste.chercherTitrePoste(getTransaction(),
				getFichePosteCourant().getIdTitrePoste()).getLibTitrePoste();
		// Service
		Service srv = Service.chercherService(getTransaction(), getFichePosteCourant().getIdServi());
		String direction = Const.CHAINE_VIDE;
		String division = Const.CHAINE_VIDE;
		String section = Const.CHAINE_VIDE;
		if (Services.estAlphabetique(srv.getCodService())) {
			if (Service.isSection(srv.getCodService()))
				section = srv.getLibService();
			if (!srv.getCodService().substring(2, 3).equals("A"))
				division = Service.getDivision(getTransaction(), srv.getCodService().substring(0, 3) + "A").getLibService();
			if (!srv.getCodService().substring(1, 2).equals("A"))
				direction = Service.getDirection(getTransaction(), srv.getCodService().substring(0, 2) + "AA").getLibService();
		} else {
			division = srv.getLibService();
		}
		if (division == null) {
			Service serv = Service.chercherService(getTransaction(), getFichePosteCourant().getIdServi());
			division = serv.getLibService();
		}
		// temps reglementaire de travail
		Horaire hor = Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg());
		// Lieu
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo());

		addZone(getNOM_ST_DIRECTION(), direction);
		addZone(getNOM_ST_SERVICE(), division);
		addZone(getNOM_ST_SUBDIVISION(), section);
		addZone(getNOM_ST_NUM_FICHE_POSTE(), getFichePosteCourant().getNumFP());
		addZone(getNOM_ST_TITRE_FP(), titreFichePoste);
		addZone(getNOM_ST_TPS_REG(), hor.getLibHor());
		addZone(getNOM_ST_LIEU_FP(), eg.getLibEntiteGeo());
	}

	/**
	 * Initialise les champs de la fiche de poste secondaire courante liée à
	 * l'affectation.
	 * 
	 * @throws Exception
	 */
	private void initialiserFichePosteSecondaire() throws Exception {
		// Titre
		String titreFichePoste = getFichePosteSecondaireCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : TitrePoste.chercherTitrePoste(getTransaction(),
				getFichePosteSecondaireCourant().getIdTitrePoste()).getLibTitrePoste();
		// Service
		Service srv = Service.chercherService(getTransaction(), getFichePosteSecondaireCourant().getIdServi());
		String direction = Const.CHAINE_VIDE;
		String division = Const.CHAINE_VIDE;
		String section = Const.CHAINE_VIDE;
		if (Services.estAlphabetique(srv.getCodService())) {
			if (Service.isSection(srv.getCodService()))
				section = srv.getLibService();
			if (!srv.getCodService().substring(2, 3).equals("A"))
				division = Service.getDivision(getTransaction(), srv.getCodService().substring(0, 3) + "A").getLibService();
			if (!srv.getCodService().substring(1, 2).equals("A"))
				direction = Service.getDirection(getTransaction(), srv.getCodService().substring(0, 2) + "AA").getLibService();
		} else {
			division = srv.getLibService();
		}
		// temps reglementaire de travail
		Horaire hor = Horaire.chercherHoraire(getTransaction(), getFichePosteSecondaireCourant().getIdCdthorReg());
		// Lieu
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteSecondaireCourant().getIdEntiteGeo());

		addZone(getNOM_ST_DIRECTION_SECONDAIRE(), direction);
		addZone(getNOM_ST_SERVICE_SECONDAIRE(), division);
		addZone(getNOM_ST_SUBDIVISION_SECONDAIRE(), section);
		addZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE(), getFichePosteSecondaireCourant().getNumFP());
		addZone(getNOM_ST_TITRE_FP_SECONDAIRE(), titreFichePoste);
		addZone(getNOM_ST_TPS_REG_SECONDAIRE(), hor.getLibHor());
		addZone(getNOM_ST_LIEU_FP_SECONDAIRE(), eg.getLibEntiteGeo());
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
			// Suppression
			getAffectationCourant().supprimerAffectation(getTransaction(), user, getAgentCourant());
			if (getTransaction().isErreur())
				return false;
			// Message informatif
			// RG_AG_AF_A10
			if (getTransaction().isErreur())
				getTransaction().declarerErreur(getTransaction().traiterErreur() + "<BR/>" + MessageUtils.getMessage("INF004"));
			else
				getTransaction().declarerErreur(MessageUtils.getMessage("INF004"));
		} else if (getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
			if (performControlerChoixImpression()) {
				// recup du document à imprimer
				String nomDocument = EnumImpressionAffectation.getCodeImpressionAffectation(Integer
						.parseInt(getZone(getNOM_LB_LISTE_IMPRESSION_SELECT())));
				// Récup affectation courante
				Affectation aff = getAffectationCourant();
				if (getVAL_ST_WARNING().equals(Const.CHAINE_VIDE)) {
					// on verifie si il existe dejà un fichier pour cette
					// affectation dans la BD
					if (verifieExistFichier(aff.getIdAffectation(), nomDocument)) {
						// alors on affiche un message
						// :" Attention un fichier existe déjà pour ce contrat. Etes-vous sûr de vouloir écraser la version précédente ?"
						addZone(getNOM_ST_WARNING(),
								"Attention un fichier du même type existe déjà pour cette affectation. Etes-vous sûr de vouloir écraser la version précédente ?");
					} else {
						imprimeModele(request, nomDocument);
						addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
						addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
					}
				} else {
					imprimeModele(request, nomDocument);
					addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
					addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				}
			}
		} else {
			if (!performControlerSaisie()) {
				return false;
			}

			if (!performControlerRG()) {
				return false;
			}

			// Récup des zones saisies
			String newIndMotifAffectation = getZone(getNOM_LB_MOTIF_AFFECTATION_SELECT());
			MotifAffectation newMotifAffectation = (MotifAffectation) getListeMotifAffectation().get(Integer.parseInt(newIndMotifAffectation));

			// pour recupere le codeEcole
			EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo());

			// Affectation des attributs
			getAffectationCourant().setIdAgent(getAgentCourant().getIdAgent());
			getAffectationCourant().setIdFichePoste(getFichePosteCourant().getIdFichePoste());
			getAffectationCourant().setRefArreteAff(getVAL_EF_REF_ARRETE().length() == 0 ? null : getVAL_EF_REF_ARRETE());
			getAffectationCourant().setDateArrete(Services.formateDate(getVAL_EF_DATE_ARRETE()));
			getAffectationCourant().setDateDebutAff(Services.formateDate(getVAL_EF_DATE_DEBUT()));
			getAffectationCourant().setDateFinAff(
					getVAL_EF_DATE_FIN().equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : Services.formateDate(getVAL_EF_DATE_FIN()));
			getAffectationCourant().setIdMotifAffectation(newMotifAffectation.getIdMotifAffectation());
			getAffectationCourant().setTempsTravail(getListeTempsTravail()[Integer.parseInt(getVAL_LB_TEMPS_TRAVAIL_SELECT())]);
			getAffectationCourant().setCodeEcole(eg.getCdEcol());
			getAffectationCourant().setIdFichePosteSecondaire(
					getFichePosteSecondaireCourant() != null ? getFichePosteSecondaireCourant().getIdFichePoste() : null);
			getAffectationCourant().setCommentaire(getVAL_EF_COMMENTAIRE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_COMMENTAIRE());

			if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION)) {
				// Modification
				getAffectationCourant().modifierAffectation(getTransaction(), user, getAgentCourant(), getFichePosteCourant());

			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création PA
				PositionAdmAgent posAdmAgt = null;
				if (getAffectationCourant().getDateFinAff().equals(Const.CHAINE_VIDE)) {
					posAdmAgt = PositionAdmAgent.chercherDernierePositionAdmAgentAvecAgent(getTransaction(), getAgentCourant());
				} else {
					posAdmAgt = PositionAdmAgent.chercherPositionAdmAgentDateDebutFinComprise(getTransaction(), getAgentCourant().getNoMatricule(),
							Services.convertitDate(Services.formateDate(getAffectationCourant().getDateDebutAff()), "dd/MM/yyyy", "yyyyMMdd"),
							Services.convertitDate(Services.formateDate(getAffectationCourant().getDateFinAff()), "dd/MM/yyyy", "yyyyMMdd"));
				}
				// Si aucune PA trouvée alors agent nouveau --> on crée sa PA
				// RG_AG_AF_A08
				if (posAdmAgt == null || posAdmAgt.getNomatr() == null) {
					getTransaction().traiterErreur();
					// Création d'une nouvelle PA
					PositionAdmAgent newPosAdmAgt = new PositionAdmAgent();
					newPosAdmAgt.setDatdeb(getAffectationCourant().getDateDebutAff());
					newPosAdmAgt.setCdpadm(Const.CODE_PA_ACTIVITE_NORMALE);
					newPosAdmAgt.setDatfin(Const.ZERO);
					newPosAdmAgt.setNomatr(getAgentCourant().getNoMatricule());
					newPosAdmAgt.setDateArrete(getAffectationCourant().getDateArrete());
					newPosAdmAgt
							.setRefarr(getAffectationCourant().getRefArreteAff() == null ? Const.ZERO : getAffectationCourant().getRefArreteAff());
					newPosAdmAgt.creerPositionAdmAgent(getTransaction(), user);
					PAAgent paAgent = new PAAgent(getAgentCourant().getIdAgent(), getAgentCourant().getNoMatricule(), getAffectationCourant()
							.getDateDebutAff());
					paAgent.creerPAAgent(getTransaction());
					// si derniere PA = PA inactive
				} else if (posAdmAgt.estPAInactive(getTransaction())) {
					// si dateDebAFF<=DateDebPA
					if (Services.compareDates(getAffectationCourant().getDateDebutAff(), posAdmAgt.getDatdeb()) <= 0) {
						// si aff sans date fin
						if (getAffectationCourant().getDateFinAff().equals(Const.CHAINE_VIDE)) {
							// on bloque
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
							return false;
						} else {
							// on verifie qu'il existe une PA comprise dans
							// dateDebAFF et dateFinAFF
							PositionAdmAgent posAdmAgtActiveAUneDate = PositionAdmAgent
									.chercherPositionAdmAgentDateComprise(getTransaction(), getAgentCourant().getNoMatricule(), Services
											.convertitDate(Services.formateDate(getAffectationCourant().getDateDebutAff()), "dd/MM/yyyy", "yyyyMMdd"));
							// si PA recupérée est ACTIVE
							if (!posAdmAgtActiveAUneDate.estPAInactive(getTransaction())) {
								// on regarde que la date de fin AFF > dateFin
								// PA recupérée
								if (Services.compareDates(getAffectationCourant().getDateFinAff(), posAdmAgtActiveAUneDate.getDatfin()) > 0) {
									getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
									return false;
								}
							} else {
								// si PA recuperre est inactive
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
								return false;
							}
						}
					} else {
						// on ferme la derniere PA
						posAdmAgt.setDatfin(getAffectationCourant().getDateDebutAff());
						posAdmAgt.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user);
						// on crée une nouvelle PA active
						PositionAdmAgent newPosAdmAgt = new PositionAdmAgent();
						newPosAdmAgt.setDatdeb(getAffectationCourant().getDateDebutAff());
						newPosAdmAgt.setCdpadm(Const.CODE_PA_ACTIVITE_NORMALE);
						newPosAdmAgt.setDatfin(Const.ZERO);
						newPosAdmAgt.setNomatr(getAgentCourant().getNoMatricule());
						newPosAdmAgt.setDateArrete(getAffectationCourant().getDateArrete());
						newPosAdmAgt.setRefarr(getAffectationCourant().getRefArreteAff() == null ? Const.ZERO : getAffectationCourant()
								.getRefArreteAff());
						newPosAdmAgt.creerPositionAdmAgent(getTransaction(), user);
						if (getTransaction().isErreur()) {
							return false;
						}
						PAAgent paAgent = new PAAgent(getAgentCourant().getIdAgent(), getAgentCourant().getNoMatricule(), getAffectationCourant()
								.getDateDebutAff());
						paAgent.creerPAAgent(getTransaction());
					}
					// PA ACTIVE
				} else {
					// si dateDebAFF<=DateDebPA
					if (Services.compareDates(getAffectationCourant().getDateDebutAff(), posAdmAgt.getDatdeb()) < 0) {
						// si aff sans date fin
						if (getAffectationCourant().getDateFinAff().equals(Const.CHAINE_VIDE)) {
							// on cherche la PA precedente
							PositionAdmAgent posAdmPrec = PositionAdmAgent.chercherPositionAdmAgentPrec(getTransaction(), posAdmAgt.getNomatr(),
									Services.convertitDate(Services.formateDate(posAdmAgt.getDatdeb()), "dd/MM/yyyy", "yyyyMMdd"));
							// si pas de PA precedente
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
								return false;
							} else {
								if (Services.compareDates(getAffectationCourant().getDateDebutAff(), posAdmPrec.getDatdeb()) > 0
										&& Services.compareDates(getAffectationCourant().getDateDebutAff(), posAdmPrec.getDatfin()) < 0) {
									// on modifie la date de fin de la PA
									// precedente
									posAdmPrec.setDatfin(getAffectationCourant().getDateDebutAff());
									posAdmPrec.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user);
									// on modifie la date de debut de la PA
									// courante
									posAdmAgt.setDatdeb(getAffectationCourant().getDateDebutAff());
									posAdmAgt.modifierPositionAdmAgent(getTransaction(), getAgentCourant(), user);
								} else {
									getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
									return false;
								}

							}
						} else {
							// on verifie qu'il existe une PA comprise dans
							// dateDebAFF et dateFinAFF
							PositionAdmAgent posAdmAgtActiveAUneDate = PositionAdmAgent
									.chercherPositionAdmAgentDateComprise(getTransaction(), getAgentCourant().getNoMatricule(), Services
											.convertitDate(Services.formateDate(getAffectationCourant().getDateDebutAff()), "dd/MM/yyyy", "yyyyMMdd"));
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
								return false;
							}
							// si PA recupérée est ACTIVE
							if (!posAdmAgtActiveAUneDate.estPAInactive(getTransaction())) {
								// on regarde que la date de fin AFF > dateFin
								// PA recupérée
								if (Services.compareDates(getAffectationCourant().getDateFinAff(), posAdmAgtActiveAUneDate.getDatfin()) > 0) {
									getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
									return false;
								}
							} else {
								// si PA recuperre est inactive
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR133"));
								return false;
							}
						}
					}
				}

				// Création Affectation
				if (!getAffectationCourant().creerAffectation(getTransaction(), user, getAgentCourant())) {
					return false;
				}

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

		if (!getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
			// Tout s'est bien passé
			commitTransaction();

			// Réinitialisation
			initialiseListeAffectation(request);
			// init de l'affectation courante
			setAffectationCourant(null);
			// On vide les zones de saisie
			initialiseAffectationVide();
			// On supprime la fiche de poste
			setFichePosteCourant(null);

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}

		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, dates bien
	 * formatées, ...)
	 * 
	 * @return true si les règles de gestion sont respectées. false sinon.
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
			setFocus(getNOM_EF_DATE_ARRETE());
			return false;
		}

		if (!Services.estUneDate(getVAL_EF_DATE_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "arrêté"));
			setFocus(getNOM_EF_DATE_ARRETE());
			return false;
		}

		// Vérification Date début et date fin (non null et dans le bon ordre.
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			// format de date
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
				// ERR007 : La date @ est incorrecte. Elle doit être au format
				// date.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "début"));
				setFocus(getNOM_EF_DATE_DEBUT());
				return false;
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
				if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
					// ERR007 : La date @ est incorrecte. Elle doit être au
					// format date.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "fin"));
					setFocus(getNOM_EF_DATE_FIN());
					return false;
				} else if (Services.compareDates(getZone(getNOM_EF_DATE_DEBUT()), getZone(getNOM_EF_DATE_FIN())) > 0) {
					// ERR200 : La date @ doit être supérieure ou égale à la
					// date @.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR200", "fin", "début"));
					setFocus(getNOM_EF_DATE_FIN());
					return false;
				}
			}
		} else {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date début"));
			setFocus(getNOM_EF_DATE_DEBUT());
			return false;
		}

		if ((Const.CHAINE_VIDE).equals(getVAL_ST_NUM_FICHE_POSTE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Fiche de poste"));
			setFocus(getNOM_PB_RECHERCHER_FP());
			return false;
		}

		// **********************************************************
		// Vérification Formats
		// **********************************************************
		// "ERR992", "La zone @ doit être numérique."
		if (!Const.CHAINE_VIDE.equals(getVAL_EF_REF_ARRETE()) && !Services.estNumerique(getVAL_EF_REF_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Réf. arrêté"));
			setFocus(getNOM_EF_REF_ARRETE());
			return false;
		}

		return true;
	}

	/**
	 * Vérifie les règles de gestion métier
	 * 
	 * @return boolean
	 * @throws Exception
	 *             RG_AG_AF_A11
	 */
	public boolean performControlerRG() throws Exception {

		// Vérification du non-chevauchement des dates des affectations
		for (ListIterator list = getListeAffectation().listIterator(); list.hasNext();) {
			Affectation aAff = (Affectation) list.next();
			if (!aAff.getIdAffectation().equals(getAffectationCourant().getIdAffectation())) {
				if (aAff.getDateFinAff() != null) {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), aAff.getDateFinAff()) <= 0) {
							// "ERR201",
							// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						}
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aAff.getDateDebutAff()) >= 0
								&& Services.compareDates(getVAL_EF_DATE_DEBUT(), aAff.getDateFinAff()) <= 0) {
							// "ERR201",
							// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						}
					}
				} else {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						// "ERR201",
						// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
						setFocus(getNOM_EF_DATE_DEBUT());
						return false;
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aAff.getDateDebutAff()) >= 0) {
							// "ERR201",
							// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						}
					}
				}
			}
		}
		// verification pas 2 fois la même fiche de poste mise
		if (getFichePosteSecondaireCourant() != null) {
			if (getFichePosteCourant().getIdFichePoste().equals(getFichePosteSecondaireCourant().getIdFichePoste())) {
				// "ERR117",
				// "La fiche de poste @ doit être différente de la fiche courante."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR117", "secondaire"));
				return false;
			}
		}

		// Vérification de la non-affectation de la Fiche de poste choisie dans
		// les dates choisies
		ArrayList<Affectation> listeAffFP = Affectation.listerAffectationAvecFP(getTransaction(), getFichePosteCourant());
		for (Affectation aff : listeAffFP) {
			if (!aff.getIdAffectation().equals(getAffectationCourant().getIdAffectation())) {
				if (aff.getDateFinAff() != null) {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						}
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0
								&& Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						}
					}
				} else {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						// "ERR085",
						// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
						setFocus(getNOM_EF_DATE_DEBUT());
						return false;
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						}
					}
				}
			}
		}

		// Vérification de la non-affectation de la Fiche de poste secondaire
		// choisie dans les dates choisies
		if (getFichePosteSecondaireCourant() != null) {
			ArrayList<Affectation> listeAffFPSecondaire = Affectation.listerAffectationAvecFP(getTransaction(), getFichePosteSecondaireCourant());
			for (Affectation aff : listeAffFPSecondaire) {
				if (!aff.getIdAffectation().equals(getAffectationCourant().getIdAffectation())) {
					if (aff.getDateFinAff() != null) {
						if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
							if (Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
								// "ERR085",
								// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
								setFocus(getNOM_EF_DATE_DEBUT());
								return false;
							}
						} else {
							if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0
									&& Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
								// "ERR085",
								// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
								setFocus(getNOM_EF_DATE_DEBUT());
								return false;
							}
						}
					} else {
						if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_EF_DATE_DEBUT());
							return false;
						} else {
							if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0) {
								// "ERR085",
								// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
								setFocus(getNOM_EF_DATE_DEBUT());
								return false;
							}
						}
					}
				}
			}
		}

		// Verification des temps reglementaires des 2 fiches de postes < 100%
		// RG_AG_AF_A11
		if (getFichePosteSecondaireCourant() != null) {
			Horaire horFDP1 = Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg());
			Horaire horFDP2 = Horaire.chercherHoraire(getTransaction(), getFichePosteSecondaireCourant().getIdCdthorReg());
			// calcul du taux que ca donne
			Float res = Float.valueOf(horFDP1.getCdTaux()) + Float.valueOf(horFDP2.getCdTaux());
			if (res > 1) {
				// "ERR104",
				// "Le temps de travail réglementaire des deux fiche de poste dépasse 100%."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR080"));
				return false;
			}
		}
		return true;
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_DIRECTION() {
		return getZone(getNOM_ST_DIRECTION());
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_FICHE_POSTE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_NUM_FICHE_POSTE() {
		return getZone(getNOM_ST_NUM_FICHE_POSTE());
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SUBDIVISION
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SUBDIVISION() {
		return getZone(getNOM_ST_SUBDIVISION());
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE_FP Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TITRE_FP() {
		return getZone(getNOM_ST_TITRE_FP());
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIEU_FP Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_LIEU_FP() {
		return getZone(getNOM_ST_LIEU_FP());
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE() {
		return getZone(getNOM_EF_DATE_ARRETE());
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_ARRETE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_REF_ARRETE() {
		return getZone(getNOM_EF_REF_ARRETE());
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
	 * Setter de la liste: LB_MOTIF_AFFECTATION Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_MOTIF_AFFECTATION(String[] newLB_MOTIF_AFFECTATION) {
		LB_MOTIF_AFFECTATION = newLB_MOTIF_AFFECTATION;
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF_AFFECTATION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_MOTIF_AFFECTATION() {
		return getLB_MOTIF_AFFECTATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF_AFFECTATION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_MOTIF_AFFECTATION_SELECT() {
		return getZone(getNOM_LB_MOTIF_AFFECTATION_SELECT());
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
	 * Setter de la liste: LB_TEMPS_TRAVAIL Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_TEMPS_TRAVAIL(String[] newLB_TEMPS_TRAVAIL) {
		LB_TEMPS_TRAVAIL = newLB_TEMPS_TRAVAIL;
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TEMPS_TRAVAIL Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_TEMPS_TRAVAIL() {
		return getLB_TEMPS_TRAVAIL();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TEMPS_TRAVAIL Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_TEMPS_TRAVAIL_SELECT() {
		return getZone(getNOM_LB_TEMPS_TRAVAIL_SELECT());
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
		return getNOM_EF_REF_ARRETE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
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
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Init Motifs affectation et Tps de travail
		initialiseListeDeroulante();
		initialiseAffectationCourante(request);

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeAffectation(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	/**
	 * Initialisation de la liste des affectations. RG_AG_AF_A09
	 */
	private void initialiseListeAffectation(HttpServletRequest request) throws Exception {

		// Recherche des affectations de l'agent
		ArrayList aff = Affectation.listerAffectationAvecAgent(getTransaction(), getAgentCourant());
		setListeAffectation(aff);

		boolean affectationActive = false;

		int indiceAff = 0;
		if (getListeAffectation() != null) {
			for (int i = 0; i < getListeAffectation().size(); i++) {
				Affectation a = (Affectation) getListeAffectation().get(i);
				affectationActive = affectationActive || a.isActive();
				FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), a.getIdFichePoste());
				HistoFichePoste hfp = null;
				ArrayList listeHistoFP = new ArrayList();
				if (a.getDateFinAff() != null && !a.getDateFinAff().equals(Const.CHAINE_VIDE)) {
					// on cherche la FDP dans histo_fiche_poste
					listeHistoFP = HistoFichePoste.listerHistoFichePosteDansDate(getTransaction(), a.getIdFichePoste(),
							Services.convertitDate(Services.formateDate(a.getDateDebutAff()), "dd/MM/yyyy", "yyyy-MM-dd"),
							Services.convertitDate(Services.formateDate(a.getDateFinAff()), "dd/MM/yyyy", "yyyy-MM-dd"));
				}
				// si il n'y en a pas on prend les infos dans la table
				// FICHE_POSTE
				if (listeHistoFP == null || listeHistoFP.size() == 0) {
					fp = FichePoste.chercherFichePoste(getTransaction(), a.getIdFichePoste());
				} else {
					// si il y en a plusieurs on prend la date la plus recente
					hfp = (HistoFichePoste) listeHistoFP.get(0);
				}
				String titreFichePoste = Const.CHAINE_VIDE;
				String numFP = Const.CHAINE_VIDE;
				Service direction = null;
				Service service = null;
				if (hfp != null) {
					titreFichePoste = hfp.getIdTitrePoste() == null ? "&nbsp;" : TitrePoste.chercherTitrePoste(getTransaction(),
							hfp.getIdTitrePoste()).getLibTitrePoste();
					// Service
					direction = Service.getDirection(getTransaction(), hfp.getIdServi());
					service = Service.getSection(getTransaction(), hfp.getIdServi());
					if (service == null)
						service = Service.getDivision(getTransaction(), hfp.getIdServi());
					if (service == null)
						service = Service.getDirection(getTransaction(), hfp.getIdServi());
					if (service == null)
						service = Service.chercherService(getTransaction(), hfp.getIdServi());

					numFP = hfp.getNumFp();
					if (a.getIdFichePosteSecondaire() != null)
						numFP = hfp.getNumFp() + " *";

				} else {
					titreFichePoste = fp.getIdTitrePoste() == null ? "&nbsp;" : TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste())
							.getLibTitrePoste();
					// Service
					direction = Service.getDirection(getTransaction(), fp.getIdServi());
					service = Service.getSection(getTransaction(), fp.getIdServi());
					if (service == null)
						service = Service.getDivision(getTransaction(), fp.getIdServi());
					if (service == null)
						service = Service.getDirection(getTransaction(), fp.getIdServi());
					if (service == null)
						service = Service.chercherService(getTransaction(), fp.getIdServi());

					numFP = fp.getNumFP();
					if (a.getIdFichePosteSecondaire() != null)
						numFP = fp.getNumFP() + " *";

				}

				addZone(getNOM_ST_DIR(indiceAff), direction != null ? direction.getCodService() : "&nbsp;");
				addZone(getNOM_ST_SERV(indiceAff), service != null ? service.getLibService() : "&nbsp;");
				addZone(getNOM_ST_DATE_DEBUT(indiceAff), a.getDateDebutAff());
				addZone(getNOM_ST_DATE_FIN(indiceAff),
						a.getDateFinAff() == null || a.getDateFinAff().equals(Const.CHAINE_VIDE) ? "&nbsp;" : a.getDateFinAff());
				addZone(getNOM_ST_NUM_FP(indiceAff), numFP.equals(Const.CHAINE_VIDE) ? "&nbsp;" : numFP);
				addZone(getNOM_ST_TITRE(indiceAff), titreFichePoste.equals(Const.CHAINE_VIDE) ? "&nbsp;" : titreFichePoste);

				indiceAff++;
			}
		}

		if (!affectationActive) {
			// Messages informatifs
			// RG_AG_AF_A09
			if (getTransaction().isErreur())
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + "<BR/>" + MessageUtils.getMessage("INF003", getAgentCourant().getNoMatricule()));
			else
				getTransaction().declarerErreur(MessageUtils.getMessage("INF003", getAgentCourant().getNoMatricule()));
		}
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * 
	 * @throws Exception
	 *             RG_AG_AF_C06 RG_AG_AF_C02
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste motif affectation vide alors affectation
		// RG_AG_AF_C06
		if (getLB_MOTIF_AFFECTATION() == LBVide) {
			ArrayList motifAff = MotifAffectation.listerMotifAffectation(getTransaction());
			setListeMotifAffectation(motifAff);

			int[] tailles = { 30 };
			String[] champs = { "libMotifAffectation" };
			setLB_MOTIF_AFFECTATION(new FormateListe(tailles, motifAff, champs).getListeFormatee());
		}

		// Si liste pourcentages Temps de travail alors affectation
		// RG_AG_AF_C02
		if (getLB_TEMPS_TRAVAIL() == LBVide) {
			setListeTempsTravail(EnumTempsTravail.getValues());
			setLB_TEMPS_TRAVAIL(getListeTempsTravail());
			addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), Const.ZERO);
		}
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (04/08/11 15:42:46)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met à jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Met à jour la liste des motifs d'affectation.
	 * 
	 * @param listeMotifAffectation
	 *            Nouvelle liste des motifs d'affectations
	 */
	private void setListeMotifAffectation(ArrayList listeMotifAffectation) {
		this.listeMotifAffectation = listeMotifAffectation;
	}

	/**
	 * Retourne la liste des affectations de l'agent
	 * 
	 * @return listeAffectation
	 */
	public ArrayList getListeAffectation() {
		return listeAffectation;
	}

	/**
	 * @param listeAffectation
	 *            listeAffectation à définir
	 */
	private void setListeAffectation(ArrayList listeAffectation) {
		this.listeAffectation = listeAffectation;
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
	 * Met à jour l'affectation en cours.
	 * 
	 * @param affectationCourant
	 *            Nouvelle affectation en cours
	 */
	private void setAffectationCourant(Affectation affectationCourant) {
		this.affectationCourant = affectationCourant;
	}

	/**
	 * Retourne la liste des motifs d'affectation.
	 * 
	 * @return listeMotifAffectation
	 */
	private ArrayList getListeMotifAffectation() {
		return listeMotifAffectation;
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
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP Date de
	 * création : (05/08/11 13:35:40)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP() {
		return "NOM_PB_RECHERCHER_FP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 13:35:40)
	 * 
	 * RG_AG_AF_C05 RG_AG_AF_C07
	 */
	public boolean performPB_RECHERCHER_FP(HttpServletRequest request) throws Exception {
		// RG_AG_AF_C05
		// RG_AG_AF_C07
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_AFFECTATION, Boolean.TRUE);
		setStatut(STATUT_RECHERCHE_FP, true);
		return true;
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
	 * Met à jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteCourant(FichePoste fichePosteCourant) {
		this.fichePosteCourant = fichePosteCourant;
	}

	/**
	 * Met à jour la liste des temps de travail.
	 * 
	 * @param listeTempsTravail
	 *            Liste des temps de travail
	 */
	private void setListeTempsTravail(String[] listeTempsTravail) {
		this.listeTempsTravail = listeTempsTravail;
	}

	/**
	 * Constructeur du process OeAGENTEmploisAffectation. Date de création :
	 * (11/08/11 16:10:24)
	 * 
	 */
	public OeAGENTEmploisAffectation() {
		super();
	}

	public String getNomEcran() {
		return "ECR-AG-EMPLOIS-AFFECTATIONS";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_IMPRIMER
			for (int i = 0; i < getListeAffectation().size(); i++) {
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
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
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
	 * Setter de la liste: LB_LISTE_IMPRESSION Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_LISTE_IMPRESSION(String[] newLB_LISTE_IMPRESSION) {
		LB_LISTE_IMPRESSION = newLB_LISTE_IMPRESSION;
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_LISTE_IMPRESSION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_LISTE_IMPRESSION() {
		return getLB_LISTE_IMPRESSION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_LISTE_IMPRESSION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_LISTE_IMPRESSION_SELECT() {
		return getZone(getNOM_LB_LISTE_IMPRESSION_SELECT());
	}

	/**
	 * Initialise la liste déroulantes des impressions.
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
	 * Vérifie les règles de gestion métier
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean performControlerChoixImpression() throws Exception {
		// Si pas de document sélectionné alors erreur
		if (Integer.parseInt(getZone(getNOM_LB_LISTE_IMPRESSION_SELECT())) == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "documents à imprimer"));
			return false;
		}

		return true;
	}

	private boolean verifieExistFichier(String idAffectation, String nomDoc) throws Exception {
		// on regarde si le fichier existe
		String nomSansExtension = nomDoc.substring(0, nomDoc.indexOf("."));
		Document.chercherDocumentByContainsNom(getTransaction(),
				"NS_" + idAffectation + "_" + nomSansExtension.substring(3, nomSansExtension.length()));
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			return false;
		}

		return true;
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

	private boolean imprimeModele(HttpServletRequest request, String nomDoc) throws Exception {
		Affectation aff = getAffectationCourant();
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destination = "NS/NS_" + aff.getIdAffectation() + "_" + nomDoc.substring(3, nomDoc.length());
		// si le fichier existe alors on supprime l'entrée où il y a le fichier
		// f
		if (verifieExistFichier(aff.getIdAffectation(), nomDoc)) {
			String nomSansExtension = nomDoc.substring(0, nomDoc.indexOf("."));
			Document d = Document.chercherDocumentByContainsNom(getTransaction(),
					"NS_" + aff.getIdAffectation() + "_" + nomSansExtension.substring(3, nomSansExtension.length()));
			LienDocumentAgent l = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant().getIdAgent(), d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			l.supprimerLienDocumentAgent(getTransaction());
			d.supprimerDocument(getTransaction());
		}

		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_AFFECTATIONS");
		String modele = repModeles + nomDoc;

		// Tout s'est bien passé
		// on crée le document en base de données
		Document d = new Document();
		d.setIdTypeDocument("3");
		d.setLienDocument(destination);
		d.setNomDocument("NS_" + aff.getIdAffectation() + "_" + nomDoc.substring(3, nomDoc.length()));
		d.setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		d.setCommentaire("Document généré par l'application");
		d.creerDocument(getTransaction());

		LienDocumentAgent lda = new LienDocumentAgent();
		lda.setIdAgent(getAgentCourant().getIdAgent());
		lda.setIdDocument(d.getIdDocument());
		lda.creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		creerModeleDocument(modele, repPartage + destination);
		commitTransaction();

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

	private void creerModeleDocument(String modele, String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("NS");

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.resolveFile(modele);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		AgentNW a = getAgentCourant();
		Affectation aff = getAffectationCourant();
		FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
		TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
		Contrat c = Contrat.chercherContratAgentDateComprise(getTransaction(), a.getIdAgent(), Services.dateDuJour());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		}
		Service s = Service.chercherService(getTransaction(), fp.getIdServi());
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), fp.getIdEntiteGeo());

		// on recupere les champs qui nous interessent
		String prenom = a.getPrenomAgent().toLowerCase();
		String premLettre = prenom.substring(0, 1).toUpperCase();
		String restePrenom = prenom.substring(1, prenom.length()).toLowerCase();
		prenom = premLettre + restePrenom;
		String nom = a.getNomAgent().toUpperCase();
		String civilite = a.getCivilite().equals("0") ? "Monsieur" : a.getCivilite().equals("1") ? "Madame" : "Mademoiselle";
		String dateDebAffectation = aff.getDateDebutAff();
		String dateFinAffectation = aff.getDateFinAff() == null ? "Il n'y a pas de date de fin pour ce contrat !" : aff.getDateFinAff();
		String titrePoste = tp.getLibTitrePoste();
		String dureePeriodeEssai = Const.CHAINE_VIDE;
		String dateFinEssai = Const.CHAINE_VIDE;
		if (c != null) {
			if (c.getDateFinPeriodeEssai() == null) {
				dureePeriodeEssai = "Il n'y a pas de date fin de periode d'essai pour ce contrat !";
			} else {
				dureePeriodeEssai = String.valueOf(Services.compteJoursEntreDates(c.getDateDebut(), c.getDateFinPeriodeEssai()));
			}
			dateFinEssai = c.getDateFinPeriodeEssai() == null ? "Il n'y a pas de date de fin de periode d'essai pour ce contrat !" : c
					.getDateFinPeriodeEssai();
		}
		String interesse = a.getCivilite().equals("0") ? "interesse" : "interessee";
		String nomme = a.getCivilite().equals("0") ? "nomme" : "nommee";
		String affecte = a.getCivilite().equals("0") ? "affecte" : "affectee";
		String libService = s.getLibService();
		String lieuPoste = eg.getLibEntiteGeo();

		// tant qu'il y a des lignes
		while ((ligne = in.readLine()) != null) {
			// je fais mon traitement
			ligne = StringUtils.replace(ligne, "$_DATE_JOUR", new Date().toString());
			ligne = StringUtils.replace(ligne, "$_NOM", nom);
			ligne = StringUtils.replace(ligne, "$_PRENOM", prenom);
			ligne = StringUtils.replace(ligne, "$_CIVILITE", civilite);
			ligne = StringUtils.replace(ligne, "$_DATEDEBAFFECTATION", dateDebAffectation);
			ligne = StringUtils.replace(ligne, "$_DATEFINAFFECTATION", dateFinAffectation);
			ligne = StringUtils.replace(ligne, "$_TITRE_POSTE", titrePoste);
			ligne = StringUtils.replace(ligne, "$_DUREEESSAI", dureePeriodeEssai);
			ligne = StringUtils.replace(ligne, "$_DATEFINESSAI", dateFinEssai);
			ligne = StringUtils.replace(ligne, "$_INTERESSE", interesse);
			ligne = StringUtils.replace(ligne, "$_NOMME", nomme);
			ligne = StringUtils.replace(ligne, "$_AFFECTE", affecte);
			ligne = StringUtils.replace(ligne, "$_LIB_SERVICE", libService);
			ligne = StringUtils.replace(ligne, "$_LIEU_POSTE", lieuPoste);
			out.write(ligne);
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

		destination = destination.substring(destination.lastIndexOf("/"), destination.length());
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		setURLFichier(getScriptOuverture(repertoireStockage + "NS" + destination));
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_FICHE_POSTE_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE() {
		return getZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE());
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
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_DIRECTION_SECONDAIRE() {
		return "NOM_ST_DIRECTION_SECONDAIRE";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SERVICE_SECONDAIRE() {
		return "NOM_ST_SERVICE_SECONDAIRE";
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
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_SUBDIVISION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SUBDIVISION_SECONDAIRE() {
		return "NOM_ST_SUBDIVISION_SECONDAIRE";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_FP_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TITRE_FP_SECONDAIRE() {
		return "NOM_ST_TITRE_FP_SECONDAIRE";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_LIEU_FP_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_LIEU_FP_SECONDAIRE() {
		return "NOM_ST_LIEU_FP_SECONDAIRE";
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
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP_SECONDAIRE
	 * Date de création : (05/08/11 13:35:40)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP_SECONDAIRE() {
		return "NOM_PB_RECHERCHER_FP_SECONDAIRE";
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENTAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TPS_REG_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TPS_REG_SECONDAIRE() {
		return getZone(getNOM_ST_TPS_REG_SECONDAIRE());
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TPS_REG Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TPS_REG() {
		return getZone(getNOM_ST_TPS_REG());
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 13:35:40)
	 * 
	 * RG_AG_AF_C05 RG_AG_AF_C07
	 */
	public boolean performPB_RECHERCHER_FP_SECONDAIRE(HttpServletRequest request) throws Exception {
		// RG_AG_AF_C05
		// RG_AG_AF_C07
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_SECONDAIRE_AFFECTATION, Boolean.TRUE);
		setStatut(STATUT_RECHERCHE_FP_SECONDAIRE, true);
		return true;
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
	 * Met à jour la fiche de poste secondaire courante.
	 * 
	 * @param fichePosteSecondaireCourant
	 */
	private void setFichePosteSecondaireCourant(FichePoste fichePosteSecondaireCourant) {
		this.fichePosteSecondaireCourant = fichePosteSecondaireCourant;
	}

	private boolean sauvegardeFDP(String idFichePoste) throws Exception {

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String destination = "SauvegardeFDP/SauvFP_" + idFichePoste + "_" + dateJour + ".xml";

		String modele = "ModeleFP.xml";
		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_FICHEPOSTE");

		creerModeleDocumentFP("SauvegardeFDP", repModeles + modele, repPartage + destination, idFichePoste);

		// Tout s'est bien passé
		// on crée le document en base de données
		Document d = new Document();
		d.setIdTypeDocument("1");
		d.setLienDocument(destination);
		d.setNomDocument("SauvFP_" + idFichePoste + "_" + dateJour + ".xml");
		d.setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		d.setCommentaire("Sauvegarde automatique lors création affectation.");
		d.creerDocument(getTransaction());

		LienDocumentAgent lda = new LienDocumentAgent();
		lda.setIdAgent(getAgentCourant().getIdAgent());
		lda.setIdDocument(d.getIdDocument());
		lda.creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		commitTransaction();
		return true;
	}

	private void creerModeleDocumentFP(String repertoire, String modele, String destination, String idFichePoste) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire(repertoire);

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.resolveFile(modele);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		AgentNW a = getAgentCourant();
		FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), idFichePoste);
		// Carriere
		String gradeTitulaire = Const.CHAINE_VIDE;
		ArrayList<Carriere> carrieres = Carriere.listerCarriereAvecAgent(getTransaction(), a);
		if (carrieres.size() > 0) {
			Grade grade = Grade.chercherGrade(getTransaction(), carrieres.get(carrieres.size() - 1).getCodeGrade());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			gradeTitulaire += grade.getGrade().replace("°", "eme");

		}
		// on recupère les champs liés à la FP
		FichePoste fpResponsable = FichePoste.chercherFichePoste(getTransaction(), fp.getIdResponsable());
		TitrePoste tpResponsable = TitrePoste.chercherTitrePoste(getTransaction(), fpResponsable.getIdTitrePoste());
		TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), fp.getIdEntiteGeo());
		Service s = Service.chercherService(getTransaction(), fp.getIdServi());
		Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
		String titrePoste = tp.getLibTitrePoste();
		String lieuPoste = eg.getLibEntiteGeo();
		String libService = s.getLibService();
		String missions = fp.getMissions();
		String grade = g.getGrade();
		String responsable = tpResponsable.getLibTitrePoste();
		// Liste Diplomes FP
		String formationRequise = Const.CHAINE_VIDE;
		ArrayList<DiplomeGenerique> dg = DiplomeGenerique.listerDiplomeGeneriqueAvecFP(getTransaction(), fp);
		for (DiplomeGenerique d : dg) {
			formationRequise += d.getLibDiplomeGenerique();
		}
		String tachesPrincipales = Const.CHAINE_VIDE;
		// activites principales
		ArrayList<Activite> lActi = Activite.listerActiviteAvecFP(getTransaction(), fp);
		for (Activite acti : lActi) {
			tachesPrincipales += acti.getNomActivite();
		}
		String budget = fp.getIdBudget() == null ? Const.CHAINE_VIDE : Budget.chercherBudget(getTransaction(), fp.getIdBudget()).getLibBudget();
		String reglementaire = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorReg()).getLibHor();
		String budgete = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorBud()).getLibHor();

		// on recupère les champs liés à l'agent
		String prenom = a.getPrenomAgent().toLowerCase();
		String premLettre = prenom.substring(0, 1).toUpperCase();
		String restePrenom = prenom.substring(1, prenom.length()).toLowerCase();
		prenom = premLettre + restePrenom;
		String nom = a.getNomAgent().toUpperCase();
		String civilite = a.getCivilite().equals("0") ? "Monsieur" : a.getCivilite().equals("1") ? "Madame" : "Mademoiselle";
		String dateNaiss = a.getCivilite().equals("0") ? "ne le " + a.getDateNaissance() : "nee le " + a.getDateNaissance();
		String embauche = a.getCivilite().equals("0") ? "embauche le " + a.getDateDerniereEmbauche() : "embauchee le " + a.getDateDerniereEmbauche()
				+ ".";
		String titulaire = civilite + " " + prenom + " " + nom + " (" + a.getNoMatricule() + ") " + dateNaiss + " " + embauche;

		// on récupère les diplomes de l'agent
		ArrayList diplomesAgent = DiplomeAgent.listerDiplomeAgentAvecAgent(getTransaction(), a);
		String listeDiplome = Const.CHAINE_VIDE;
		for (Iterator iter = diplomesAgent.iterator(); iter.hasNext();) {
			DiplomeAgent da = (DiplomeAgent) iter.next();
			TitreDiplome td = TitreDiplome.chercherTitreDiplome(getTransaction(), da.getIdTitreDiplome());
			SpecialiteDiplomeNW sd = SpecialiteDiplomeNW.chercherSpecialiteDiplomeNW(getTransaction(), da.getIdSpecialiteDiplome());
			listeDiplome += td.getLibTitreDiplome() + " " + sd.getLibSpeDiplome() + ",";
		}
		if (!listeDiplome.equals(Const.CHAINE_VIDE)) {
			listeDiplome = listeDiplome.substring(0, listeDiplome.length() - 1);
		}

		// tant qu'il y a des lignes
		while ((ligne = in.readLine()) != null) {
			// je fais mon traitement
			ligne = StringUtils.replace(ligne, "$_NUMERO_FP", fp.getNumFP());
			ligne = StringUtils.replace(ligne, "$_BUDGET_POSTE", budget);
			ligne = StringUtils.replace(ligne, "$_ANNEE", fp.getAnneeCreation());
			ligne = StringUtils.replace(ligne, "$_NFA", fp.getNFA());
			ligne = StringUtils.replace(ligne, "$_OPI", fp.getOPI() == null ? Const.CHAINE_VIDE : fp.getOPI());
			ligne = StringUtils.replace(ligne, "$_REGLEMENTAIRE", reglementaire);
			ligne = StringUtils.replace(ligne, "$_BUDGETE", budgete);
			ligne = StringUtils.replace(ligne, "$_TITRE_POSTE", titrePoste);
			ligne = StringUtils.replace(ligne, "$_LIEU_POSTE", lieuPoste);
			ligne = StringUtils.replace(ligne, "$_SERVICE", libService.replace("&", "et"));
			ligne = StringUtils.replace(ligne, "$_MISSION", missions);
			ligne = StringUtils.replace(ligne, "$_GRADE_POSTE", grade);
			ligne = StringUtils.replace(ligne, "$_RESPONSABLE", responsable);
			ligne = StringUtils.replace(ligne, "$_TITULAIRE", titulaire);
			ligne = StringUtils.replace(ligne, "$_DIPLOMES", listeDiplome);
			ligne = StringUtils.replace(ligne, "$_FORMATION_REQUISE", formationRequise);
			ligne = StringUtils.replace(ligne, "$_TACHES_PRINCIPALES", tachesPrincipales);
			ligne = StringUtils.replace(ligne, "$_GRADE_TITULAIRE", gradeTitulaire);
			ligne = StringUtils.replace(ligne, "$_DATE_JOUR", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

			out.write(ligne);
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

		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		}
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DIR(int i) {
		return getZone(getNOM_ST_DIR(i));
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERV Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERV(int i) {
		return getZone(getNOM_ST_SERV(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FP Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM_FP(int i) {
		return "NOM_ST_NUM_FP" + i;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TITRE(int i) {
		return "NOM_ST_TITRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TITRE(int i) {
		return getZone(getNOM_ST_TITRE(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du processs - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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

		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		setFichePosteSecondaireCourant(null);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAModifier);
		setAffectationCourant(aff);

		if (initialiseAffectationCourante(request)) {

			// Alim zones
			addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
			addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete());
			addZone(getNOM_EF_DATE_DEBUT(), getAffectationCourant().getDateDebutAff());
			addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff());
			addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		// On supprime la fiche de poste secondaire
		setFichePosteSecondaireCourant(null);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAConsulter);
		setAffectationCourant(aff);

		if (initialiseAffectationCourante(request)) {

			// Alim zones
			addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
			addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete());
			addZone(getNOM_EF_DATE_DEBUT(), getAffectationCourant().getDateDebutAff());
			addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff());
			addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);
		}
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_AF_A02
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltASuprimer);
		setAffectationCourant(aff);

		if (getAffectationCourant().isActive()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			if (initialiseAffectationCourante(request)) {

				// Alim zones
				addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
				addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete());
				addZone(getNOM_EF_DATE_DEBUT(), getAffectationCourant().getDateDebutAff());
				addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff());
				addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

				// On nomme l'action
				addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
			}
		} else {
			// RG_AG_AF_A02
			// "ERR081",
			// "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimée.")
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR081"));
			setFocus(getNOM_EF_DATE_ARRETE());
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setAffectationCourant(null);
			return false;
		}
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
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAImprimer);
		setAffectationCourant(aff);

		if (getAffectationCourant().isActive()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			if (initialiseAffectationCourante(request)) {
				initialiseListeImpression();
				// On nomme l'action
				addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
			}
		} else {
			// "ERR081",
			// "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimée.")
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR081"));
			setFocus(getNOM_EF_DATE_ARRETE());
			return false;
		}
		return true;
	}

}
