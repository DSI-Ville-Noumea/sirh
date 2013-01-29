package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.avancement.Avancement;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.metier.parametrage.CapDao;
import nc.mairie.spring.dao.metier.parametrage.DeliberationDao;
import nc.mairie.spring.dao.metier.parametrage.EmployeurDao;
import nc.mairie.spring.dao.metier.parametrage.RepresentantDao;
import nc.mairie.spring.dao.metier.referentiel.TypeRepresentantDao;
import nc.mairie.spring.domain.metier.parametrage.Cap;
import nc.mairie.spring.domain.metier.parametrage.Deliberation;
import nc.mairie.spring.domain.metier.parametrage.Employeur;
import nc.mairie.spring.domain.metier.parametrage.Representant;
import nc.mairie.spring.domain.metier.referentiel.TypeRepresentant;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEAvancement extends nc.mairie.technique.BasicProcess {
	private String[] LB_MOTIF;
	private String[] LB_EMPLOYEUR;
	private String[] LB_REPRESENTANT;
	private String[] LB_TYPE_REPRESENTANT;
	private String[] LB_DELIBERATION;
	private String[] LB_TYPE_DELIBERATION;
	private String[] LB_CAP;

	private ArrayList<MotifAvancement> listeMotif;
	private MotifAvancement motifCourant;

	private ArrayList<Employeur> listeEmployeur;
	private Employeur employeurCourant;
	private EmployeurDao employeurDao;

	private ArrayList<Representant> listeRepresentant;
	private Representant representantCourant;
	private RepresentantDao representantDao;

	private ArrayList<TypeRepresentant> listeTypeRepresentant;
	private TypeRepresentantDao typeRepresentantDao;
	private Hashtable<Integer, TypeRepresentant> hashTypeRepresentant;

	private ArrayList<Deliberation> listeDeliberation;
	private Deliberation deliberationCourant;
	private DeliberationDao deliberationDao;

	private ArrayList<String> listeTypeDeliberation;

	private ArrayList<Cap> listeCap;
	private Cap capCourant;
	private CapDao capDao;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
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
		if (getListeMotif().size() == 0) {
			initialiseListeMotif(request);
		}
		if (getListeEmployeur().size() == 0) {
			initialiseListeEmployeur(request);
		}
		if (getListeRepresentant().size() == 0) {
			initialiseListeRepresentant(request);
		}
		if (getListeTypeRepresentant().size() == 0) {
			initialiseListeTypeRepresentant(request);
		}
		if (getListeDeliberation().size() == 0) {
			initialiseListeDeliberation(request);
		}
		if (getListeTypeDeliberation().size() == 0) {
			initialiseListeTypeDeliberation(request);
		}
		if (getListeCap().size() == 0) {
			initialiseListeCap(request);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getEmployeurDao() == null) {
			setEmployeurDao((EmployeurDao) context.getBean("employeurDao"));
		}
		if (getRepresentantDao() == null) {
			setRepresentantDao((RepresentantDao) context.getBean("representantDao"));
		}
		if (getTypeRepresentantDao() == null) {
			setTypeRepresentantDao((TypeRepresentantDao) context.getBean("typeRepresentantDao"));
		}
		if (getDeliberationDao() == null) {
			setDeliberationDao((DeliberationDao) context.getBean("deliberationDao"));
		}
		if (getCapDao() == null) {
			setCapDao((CapDao) context.getBean("capDao"));
		}
	}

	/**
	 * Initialisation de la listes des cap Date de création : (14/09/11)
	 * 
	 */
	private void initialiseListeCap(HttpServletRequest request) throws Exception {
		setListeCap(getCapDao().listerCap());
		if (getListeCap().size() != 0) {
			int tailles[] = { 10, 10 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeCap().listIterator(); list.hasNext();) {
				Cap cap = (Cap) list.next();
				String ligne[] = { cap.getCodeCap(), cap.getRefCap() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CAP(aFormat.getListeFormatee());
		} else {
			setLB_CAP(null);
		}
	}

	/**
	 * Initialisation de la listes des motifs d'avancement Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		setListeMotif(MotifAvancement.listerMotifAvancement(getTransaction()));
		if (getListeMotif().size() != 0) {
			int tailles[] = { 40, 10 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeMotif().listIterator(); list.hasNext();) {
				MotifAvancement ma = (MotifAvancement) list.next();
				String ligne[] = { ma.getLibMotifAvct(), ma.getCodeMotifAvct() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
		}
	}

	/**
	 * Initialisation de la listes des employeurs Date de création : (14/09/11)
	 * 
	 */
	private void initialiseListeEmployeur(HttpServletRequest request) throws Exception {
		setListeEmployeur(getEmployeurDao().listerEmployeur());
		if (getListeEmployeur().size() != 0) {
			int tailles[] = { 50, 90 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeEmployeur().listIterator(); list.hasNext();) {
				Employeur emp = (Employeur) list.next();
				String ligne[] = { emp.getLibEmployeur(), emp.getTitreEmployeur() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_EMPLOYEUR(aFormat.getListeFormatee());
		} else {
			setLB_EMPLOYEUR(null);
		}
	}

	/**
	 * Initialisation de la listes des représentants Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeRepresentant(HttpServletRequest request) throws Exception {
		setListeRepresentant(getRepresentantDao().listerRepresentant());
		if (getListeRepresentant().size() != 0) {
			int tailles[] = { 20, 20, 10 };
			String padding[] = { "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeRepresentant().listIterator(); list.hasNext();) {
				Representant repre = (Representant) list.next();
				TypeRepresentant typeRepre = getTypeRepresentantDao().chercherTypeRepresentant(repre.getIdTypeRepresentant());
				String ligne[] = { repre.getNomRepresentant(), repre.getPrenomRepresentant(), typeRepre.getLibTypeRepresentant() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_REPRESENTANT(aFormat.getListeFormatee());
		} else {
			setLB_REPRESENTANT(null);
		}
	}

	/**
	 * Initialisation de la listes des types de représentants Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTypeRepresentant(HttpServletRequest request) throws Exception {
		setListeTypeRepresentant(getTypeRepresentantDao().listerTypeRepresentant());
		if (getListeTypeRepresentant().size() != 0) {
			setHashTypeRepresentant(new Hashtable<Integer, TypeRepresentant>());
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeTypeRepresentant().listIterator(); list.hasNext();) {
				TypeRepresentant type = (TypeRepresentant) list.next();
				String ligne[] = { type.getLibTypeRepresentant() };

				aFormat.ajouteLigne(ligne);
				getHashTypeRepresentant().put(type.getIdTypeRepresentant(), type);
			}
			setLB_TYPE_REPRESENTANT(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_REPRESENTANT(null);
		}
	}

	/**
	 * Initialisation de la listes des types de délibération Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTypeDeliberation(HttpServletRequest request) throws Exception {
		ArrayList<String> listeTypeDelib = new ArrayList<String>();
		listeTypeDelib.add("COMMUNAL");
		listeTypeDelib.add("TERRITORIAL");
		setListeTypeDeliberation(listeTypeDelib);

		if (getListeTypeDeliberation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeTypeDeliberation().listIterator(); list.hasNext();) {
				String type = (String) list.next();
				String ligne[] = { type };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DELIBERATION(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_DELIBERATION(null);
		}

	}

	/**
	 * Initialisation de la listes des délibérations Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeDeliberation(HttpServletRequest request) throws Exception {
		setListeDeliberation(getDeliberationDao().listerDeliberation());
		if (getListeDeliberation().size() != 0) {
			int tailles[] = { 10, 20, 70 };
			String padding[] = { "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeDeliberation().listIterator(); list.hasNext();) {
				Deliberation delib = (Deliberation) list.next();
				String ligne[] = { delib.getCodeDeliberation(), delib.getTypeDeliberation(), delib.getLibDeliberation() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_DELIBERATION(aFormat.getListeFormatee());
		} else {
			setLB_DELIBERATION(null);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAvancement. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAvancement() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_MOTIF() {
		return "NOM_PB_ANNULER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_MOTIF(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MOTIF(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MOTIF Date de création
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_MOTIF() {
		return "NOM_PB_CREER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_MOTIF(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF(), ACTION_CREATION);
		addZone(getNOM_EF_LIB_MOTIF(), "");
		addZone(getNOM_EF_CODE_MOTIF(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MOTIF() {
		return "NOM_PB_SUPPRIMER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MOTIF(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotif().size()) {
			MotifAvancement ma = getListeMotif().get(indice);
			setMotifCourant(ma);
			addZone(getNOM_EF_LIB_MOTIF(), ma.getLibMotifAvct());
			addZone(getNOM_EF_CODE_MOTIF(), ma.getCodeMotifAvct());
			addZone(getNOM_ST_ACTION_MOTIF(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs d'avancement"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_MOTIF() {
		return "NOM_PB_VALIDER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_MOTIF(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMotif(request))
			return false;

		if (!performControlerRegleGestionMotif(request))
			return false;

		if (getVAL_ST_ACTION_MOTIF() != null && getVAL_ST_ACTION_MOTIF() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {
				setMotifCourant(new MotifAvancement());
				getMotifCourant().setLibMotifAvct(getVAL_EF_LIB_MOTIF());
				getMotifCourant().setCodeMotifAvct(getVAL_EF_CODE_MOTIF());
				getMotifCourant().creerMotifAvancement(getTransaction());
				if (!getTransaction().isErreur())
					getListeMotif().add(getMotifCourant());
			} else if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)) {
				getMotifCourant().supprimerMotifAvancement(getTransaction());
				if (!getTransaction().isErreur())
					getListeMotif().remove(getMotifCourant());
				setMotifCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeMotif(request);
			addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un motif de recrutement Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieMotif(HttpServletRequest request) throws Exception {
		// Verification libellé motif not null
		if (getZone(getNOM_EF_LIB_MOTIF()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification code motif not null
		if (getZone(getNOM_EF_CODE_MOTIF()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une entité géographique Date de création
	 * : (14/09/11)
	 */
	private boolean performControlerRegleGestionMotif(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un motif avancement utilisée sur un
		// avancement
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)
				&& Avancement.listerAvancementAvecMotif(getTransaction(), getMotifCourant()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un avancement", "ce motif d'avancement"));
			return false;
		}

		// Vérification des contraintes d'unicité de l'entité géographique
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {

			for (MotifAvancement motif : getListeMotif()) {
				if (motif.getLibMotifAvct().equals(getVAL_EF_LIB_MOTIF().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un motif d'avancement", "ce libellé"));
					return false;
				}
				if (motif.getCodeMotifAvct().equals(getVAL_EF_CODE_MOTIF().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un motif d'avancement", "ce code"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_MOTIF
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_MOTIF() {
		return "NOM_EF_LIB_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_MOTIF() {
		return getZone(getNOM_EF_LIB_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_MOTIF() {
		return "NOM_EF_CODE_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_MOTIF() {
		return getZone(getNOM_EF_CODE_MOTIF());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	private ArrayList<MotifAvancement> getListeMotif() {
		if (listeMotif == null)
			return new ArrayList<MotifAvancement>();
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifAvancement> listeMotif) {
		this.listeMotif = listeMotif;
	}

	private MotifAvancement getMotifCourant() {
		return motifCourant;
	}

	private void setMotifCourant(MotifAvancement motifCourant) {
		this.motifCourant = motifCourant;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_MOTIF
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF())) {
				return performPB_ANNULER_MOTIF(request);
			}

			// Si clic sur le bouton PB_CREER_MOTIF
			if (testerParametre(request, getNOM_PB_CREER_MOTIF())) {
				return performPB_CREER_MOTIF(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MOTIF
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MOTIF())) {
				return performPB_SUPPRIMER_MOTIF(request);
			}

			// Si clic sur le bouton PB_VALIDER_MOTIF
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF())) {
				return performPB_VALIDER_MOTIF(request);
			}

			// Si clic sur le bouton PB_ANNULER_EMPLOYEUR
			if (testerParametre(request, getNOM_PB_ANNULER_EMPLOYEUR())) {
				return performPB_ANNULER_EMPLOYEUR(request);
			}

			// Si clic sur le bouton PB_CREER_EMPLOYEUR
			if (testerParametre(request, getNOM_PB_CREER_EMPLOYEUR())) {
				return performPB_CREER_EMPLOYEUR(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_EMPLOYEUR
			if (testerParametre(request, getNOM_PB_SUPPRIMER_EMPLOYEUR())) {
				return performPB_SUPPRIMER_EMPLOYEUR(request);
			}

			// Si clic sur le bouton PB_VALIDER_EMPLOYEUR
			if (testerParametre(request, getNOM_PB_VALIDER_EMPLOYEUR())) {
				return performPB_VALIDER_EMPLOYEUR(request);
			}

			// Si clic sur le bouton PB_ANNULER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_ANNULER_REPRESENTANT())) {
				return performPB_ANNULER_REPRESENTANT(request);
			}

			// Si clic sur le bouton PB_CREER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_CREER_REPRESENTANT())) {
				return performPB_CREER_REPRESENTANT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_REPRESENTANT())) {
				return performPB_SUPPRIMER_REPRESENTANT(request);
			}

			// Si clic sur le bouton PB_VALIDER_REPRESENTANT
			if (testerParametre(request, getNOM_PB_VALIDER_REPRESENTANT())) {
				return performPB_VALIDER_REPRESENTANT(request);
			}

			// Si clic sur le bouton PB_ANNULER_DELIBERATION
			if (testerParametre(request, getNOM_PB_ANNULER_DELIBERATION())) {
				return performPB_ANNULER_DELIBERATION(request);
			}

			// Si clic sur le bouton PB_CREER_DELIBERATION
			if (testerParametre(request, getNOM_PB_CREER_DELIBERATION())) {
				return performPB_CREER_DELIBERATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DELIBERATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DELIBERATION())) {
				return performPB_SUPPRIMER_DELIBERATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_DELIBERATION
			if (testerParametre(request, getNOM_PB_VALIDER_DELIBERATION())) {
				return performPB_VALIDER_DELIBERATION(request);
			}

			// Si clic sur le bouton PB_ANNULER_CAP
			if (testerParametre(request, getNOM_PB_ANNULER_CAP())) {
				return performPB_ANNULER_CAP(request);
			}

			// Si clic sur le bouton PB_CREER_CAP
			if (testerParametre(request, getNOM_PB_CREER_CAP())) {
				return performPB_CREER_CAP(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_CAP
			if (testerParametre(request, getNOM_PB_SUPPRIMER_CAP())) {
				return performPB_SUPPRIMER_CAP(request);
			}

			// Si clic sur le bouton PB_VALIDER_CAP
			if (testerParametre(request, getNOM_PB_VALIDER_CAP())) {
				return performPB_VALIDER_CAP(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEAvancement.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-AVANCEMENT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_EMPLOYEUR
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_EMPLOYEUR() {
		return "NOM_ST_ACTION_EMPLOYEUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_EMPLOYEUR Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_EMPLOYEUR() {
		return getZone(getNOM_ST_ACTION_EMPLOYEUR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_EMPLOYEUR() {
		return "NOM_EF_EMPLOYEUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_EMPLOYEUR Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_EMPLOYEUR() {
		return getZone(getNOM_EF_EMPLOYEUR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_EMPLOYEUR
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_TITRE_EMPLOYEUR() {
		return "NOM_EF_TITRE_EMPLOYEUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_EMPLOYEUR Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_TITRE_EMPLOYEUR() {
		return getZone(getNOM_EF_TITRE_EMPLOYEUR());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_EMPLOYEUR() {
		if (LB_EMPLOYEUR == null)
			LB_EMPLOYEUR = initialiseLazyLB();
		return LB_EMPLOYEUR;
	}

	/**
	 * Setter de la liste: LB_EMPLOYEUR Date de création : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_EMPLOYEUR(String[] newLB_EMPLOYEUR) {
		LB_EMPLOYEUR = newLB_EMPLOYEUR;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_EMPLOYEUR() {
		return "NOM_LB_EMPLOYEUR";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_EMPLOYEUR_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_EMPLOYEUR_SELECT() {
		return "NOM_LB_EMPLOYEUR_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_EMPLOYEUR Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_EMPLOYEUR() {
		return getLB_EMPLOYEUR();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_EMPLOYEUR Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_EMPLOYEUR_SELECT() {
		return getZone(getNOM_LB_EMPLOYEUR_SELECT());
	}

	public ArrayList<Employeur> getListeEmployeur() {
		if (listeEmployeur == null)
			return new ArrayList<Employeur>();
		return listeEmployeur;
	}

	private void setListeEmployeur(ArrayList<Employeur> listeEmployeur) {
		this.listeEmployeur = listeEmployeur;
	}

	private Employeur getEmployeurCourant() {
		return employeurCourant;
	}

	private void setEmployeurCourant(Employeur employeurCourant) {
		this.employeurCourant = employeurCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_EMPLOYEUR() {
		return "NOM_PB_ANNULER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_EMPLOYEUR(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_EMPLOYEUR(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_EMPLOYEUR() {
		return "NOM_PB_CREER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_EMPLOYEUR(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_EMPLOYEUR(), ACTION_CREATION);
		addZone(getNOM_EF_EMPLOYEUR(), "");
		addZone(getNOM_EF_TITRE_EMPLOYEUR(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_EMPLOYEUR() {
		return "NOM_PB_SUPPRIMER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_EMPLOYEUR(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_EMPLOYEUR_SELECT()) ? Integer.parseInt(getVAL_LB_EMPLOYEUR_SELECT()) : -1);
		if (indice != -1 && indice < getListeEmployeur().size()) {
			Employeur emp = getListeEmployeur().get(indice);
			setEmployeurCourant(emp);
			addZone(getNOM_EF_EMPLOYEUR(), emp.getLibEmployeur());
			addZone(getNOM_EF_TITRE_EMPLOYEUR(), emp.getTitreEmployeur());
			addZone(getNOM_ST_ACTION_EMPLOYEUR(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "employeurs"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_EMPLOYEUR Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_EMPLOYEUR() {
		return "NOM_PB_VALIDER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_EMPLOYEUR(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieEmployeur(request))
			return false;

		if (!performControlerRegleGestionEmployeur(request))
			return false;

		if (getVAL_ST_ACTION_EMPLOYEUR() != null && getVAL_ST_ACTION_EMPLOYEUR() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_CREATION)) {
				setEmployeurCourant(new Employeur());
				getEmployeurCourant().setLibEmployeur(getVAL_EF_EMPLOYEUR());
				getEmployeurCourant().setTitreEmployeur(getVAL_EF_TITRE_EMPLOYEUR());
				getEmployeurDao().creerEmployeur(getEmployeurCourant().getLibEmployeur(), getEmployeurCourant().getTitreEmployeur());
				getListeEmployeur().add(getEmployeurCourant());
			} else if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_SUPPRESSION)) {
				getEmployeurDao().supprimerEmployeur(getEmployeurCourant().getIdEmployeur());
				getListeEmployeur().remove(getEmployeurCourant());
				setEmployeurCourant(null);
			}
			initialiseListeEmployeur(request);
			addZone(getNOM_ST_ACTION_EMPLOYEUR(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un employeur Date de création : (14/09/11)
	 */
	private boolean performControlerSaisieEmployeur(HttpServletRequest request) throws Exception {
		// Verification libellé employeur not null
		if (getZone(getNOM_EF_EMPLOYEUR()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification titre employeur not null
		if (getZone(getNOM_EF_TITRE_EMPLOYEUR()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "titre"));
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un employeur Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionEmployeur(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un employeur utilisée sur un
		// avancement
		/*
		 * if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_SUPPRESSION) &&
		 * getEmployeurDao().listerEmployeur() &&
		 * Avancement.listerAvancementAvecMotif(getTransaction(),
		 * getMotifCourant()).size() > 0) { // TODO // "ERR989", //
		 * "Suppression impossible. Il existe au moins @ rattaché à @."
		 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR989",
		 * "un avancement", "ce motif d'avancement")); return false; }
		 */

		// Vérification des contraintes d'unicité de l'employeur
		if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_CREATION)) {

			for (Employeur emp : getListeEmployeur()) {
				if (emp.getLibEmployeur().equals(getVAL_EF_EMPLOYEUR())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un employeur", "ce libellé"));
					return false;
				}
				if (emp.getTitreEmployeur().equals(getVAL_EF_TITRE_EMPLOYEUR())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un employeur", "ce titre"));
					return false;
				}
			}
		}

		return true;
	}

	public EmployeurDao getEmployeurDao() {
		return employeurDao;
	}

	public void setEmployeurDao(EmployeurDao employeurDao) {
		this.employeurDao = employeurDao;
	}

	public RepresentantDao getRepresentantDao() {
		return representantDao;
	}

	public void setRepresentantDao(RepresentantDao representantDao) {
		this.representantDao = representantDao;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_REPRESENTANT() {
		if (LB_REPRESENTANT == null)
			LB_REPRESENTANT = initialiseLazyLB();
		return LB_REPRESENTANT;
	}

	/**
	 * Setter de la liste: LB_REPRESENTANT Date de création : (14/09/11
	 * 13:52:54)
	 * 
	 */
	private void setLB_REPRESENTANT(String[] newLB_REPRESENTANT) {
		LB_REPRESENTANT = newLB_REPRESENTANT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_REPRESENTANT() {
		return "NOM_LB_REPRESENTANT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_REPRESENTANT_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_REPRESENTANT_SELECT() {
		return "NOM_LB_REPRESENTANT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_REPRESENTANT() {
		return getLB_REPRESENTANT();
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_REPRESENTANT
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_REPRESENTANT() {
		return "NOM_ST_ACTION_REPRESENTANT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_REPRESENTANT() {
		return getZone(getNOM_ST_ACTION_REPRESENTANT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_REPRESENTANT_SELECT() {
		return getZone(getNOM_LB_REPRESENTANT_SELECT());
	}

	public ArrayList<Representant> getListeRepresentant() {
		if (listeRepresentant == null)
			return new ArrayList<Representant>();
		return listeRepresentant;
	}

	private void setListeRepresentant(ArrayList<Representant> listeRepresentant) {
		this.listeRepresentant = listeRepresentant;
	}

	private Representant getRepresentantCourant() {
		return representantCourant;
	}

	private void setRepresentantCourant(Representant representantCourant) {
		this.representantCourant = representantCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_REPRESENTANT() {
		return "NOM_PB_ANNULER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_REPRESENTANT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_REPRESENTANT(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_REPRESENTANT() {
		return "NOM_PB_CREER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_REPRESENTANT(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_REPRESENTANT(), ACTION_CREATION);
		addZone(getNOM_LB_TYPE_REPRESENTANT_SELECT(), Const.ZERO);
		addZone(getNOM_EF_NOM_REPRESENTANT(), "");
		addZone(getNOM_EF_PRENOM_REPRESENTANT(), "");

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REPRESENTANT Date
	 * de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REPRESENTANT() {
		return "NOM_PB_SUPPRIMER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_REPRESENTANT(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_REPRESENTANT_SELECT()) ? Integer.parseInt(getVAL_LB_REPRESENTANT_SELECT()) : -1);
		if (indice != -1 && indice < getListeRepresentant().size()) {
			Representant repre = getListeRepresentant().get(indice);
			setRepresentantCourant(repre);

			TypeRepresentant typeRe = (TypeRepresentant) getHashTypeRepresentant().get(repre.getIdTypeRepresentant());
			int ligneTypeRepresentant = getListeTypeRepresentant().indexOf(typeRe);

			addZone(getNOM_LB_TYPE_REPRESENTANT_SELECT(), String.valueOf(ligneTypeRepresentant));
			addZone(getNOM_EF_NOM_REPRESENTANT(), repre.getNomRepresentant());
			addZone(getNOM_EF_PRENOM_REPRESENTANT(), repre.getPrenomRepresentant());

			addZone(getNOM_ST_ACTION_REPRESENTANT(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "représentants"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_REPRESENTANT() {
		return "NOM_PB_VALIDER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_REPRESENTANT(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieRepresentant(request))
			return false;

		if (!performControlerRegleGestionRepresentant(request))
			return false;

		if (getVAL_ST_ACTION_REPRESENTANT() != null && getVAL_ST_ACTION_REPRESENTANT() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_CREATION)) {
				setRepresentantCourant(new Representant());
				// on recupere le type de representant
				int indice = (Services.estNumerique(getVAL_LB_TYPE_REPRESENTANT_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_REPRESENTANT_SELECT())
						: -1);
				Integer idTypeRepre = ((TypeRepresentant) getListeTypeRepresentant().get(indice)).getIdTypeRepresentant();
				getRepresentantCourant().setIdTypeRepresentant(idTypeRepre);
				getRepresentantCourant().setNomRepresentant(getVAL_EF_NOM_REPRESENTANT());
				getRepresentantCourant().setPrenomRepresentant(getVAL_EF_PRENOM_REPRESENTANT());
				getRepresentantDao().creerRepresentant(getRepresentantCourant().getIdTypeRepresentant(),
						getRepresentantCourant().getNomRepresentant(), getRepresentantCourant().getPrenomRepresentant());
				getListeRepresentant().add(getRepresentantCourant());
			} else if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_SUPPRESSION)) {
				getRepresentantDao().supprimerRepresentant(getRepresentantCourant().getIdRepresentant());
				getListeRepresentant().remove(getRepresentantCourant());
				setRepresentantCourant(null);
			}
			initialiseListeRepresentant(request);
			addZone(getNOM_ST_ACTION_REPRESENTANT(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un employeur Date de création : (14/09/11)
	 */
	private boolean performControlerSaisieRepresentant(HttpServletRequest request) throws Exception {
		// Verification nom representant not null
		if (getZone(getNOM_EF_NOM_REPRESENTANT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nom"));
			return false;
		}
		// Verification prenom representant not null
		if (getZone(getNOM_EF_PRENOM_REPRESENTANT()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "prénom"));
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les règles de gestion d'un employeur Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionRepresentant(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un employeur utilisée sur un
		// avancement

		/*
		 * if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_SUPPRESSION) &&
		 * getRepresentantDao().listerRepresentant() &&
		 * Avancement.listerAvancementAvecMotif(getTransaction(),
		 * getMotifCourant()).size() > 0) { // TODO // "ERR989", //
		 * "Suppression impossible. Il existe au moins @ rattaché à @."
		 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR989",
		 * "un avancement", "ce motif d'avancement")); return false; }
		 */

		// Vérification des contraintes d'unicité de l'employeur
		if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_CREATION)) {

			for (Representant repre : getListeRepresentant()) {
				if (repre.getNomRepresentant().equals(getVAL_EF_NOM_REPRESENTANT())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un représentant", "ce nom"));
					return false;
				}
				if (repre.getPrenomRepresentant().equals(getVAL_EF_PRENOM_REPRESENTANT())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un représentant", "ce prénom"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_TYPE_REPRESENTANT() {
		if (LB_TYPE_REPRESENTANT == null)
			LB_TYPE_REPRESENTANT = initialiseLazyLB();
		return LB_TYPE_REPRESENTANT;
	}

	/**
	 * Setter de la liste: LB_TYPE_REPRESENTANT Date de création : (14/09/11
	 * 13:52:54)
	 * 
	 */
	private void setLB_TYPE_REPRESENTANT(String[] newLB_TYPE_REPRESENTANT) {
		LB_TYPE_REPRESENTANT = newLB_TYPE_REPRESENTANT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REPRESENTANT Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_REPRESENTANT() {
		return "NOM_LB_TYPE_REPRESENTANT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_REPRESENTANT_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_REPRESENTANT_SELECT() {
		return "NOM_LB_TYPE_REPRESENTANT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REPRESENTANT() {
		return getLB_TYPE_REPRESENTANT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_TYPE_REPRESENTANT_SELECT() {
		return getZone(getNOM_LB_TYPE_REPRESENTANT_SELECT());
	}

	private ArrayList<TypeRepresentant> getListeTypeRepresentant() {
		if (listeTypeRepresentant == null)
			return new ArrayList<TypeRepresentant>();
		return listeTypeRepresentant;
	}

	private void setListeTypeRepresentant(ArrayList<TypeRepresentant> listeTypeRepresentant) {
		this.listeTypeRepresentant = listeTypeRepresentant;
	}

	public TypeRepresentantDao getTypeRepresentantDao() {
		return typeRepresentantDao;
	}

	public void setTypeRepresentantDao(TypeRepresentantDao typeRepresentantDao) {
		this.typeRepresentantDao = typeRepresentantDao;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_REPRESENTANT
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_NOM_REPRESENTANT() {
		return "NOM_EF_NOM_REPRESENTANT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_NOM_REPRESENTANT() {
		return getZone(getNOM_EF_NOM_REPRESENTANT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM_REPRESENTANT
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_PRENOM_REPRESENTANT() {
		return "NOM_EF_PRENOM_REPRESENTANT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM_REPRESENTANT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_PRENOM_REPRESENTANT() {
		return getZone(getNOM_EF_PRENOM_REPRESENTANT());
	}

	public Hashtable<Integer, TypeRepresentant> getHashTypeRepresentant() {
		if (hashTypeRepresentant == null)
			return new Hashtable<Integer, TypeRepresentant>();
		return hashTypeRepresentant;
	}

	public void setHashTypeRepresentant(Hashtable<Integer, TypeRepresentant> hashTypeRepresentant) {
		this.hashTypeRepresentant = hashTypeRepresentant;
	}

	public String[] getLB_DELIBERATION() {
		if (LB_DELIBERATION == null)
			LB_DELIBERATION = initialiseLazyLB();
		return LB_DELIBERATION;
	}

	public void setLB_DELIBERATION(String[] lB_DELIBERATION) {
		LB_DELIBERATION = lB_DELIBERATION;
	}

	public ArrayList<Deliberation> getListeDeliberation() {
		return listeDeliberation == null ? new ArrayList<Deliberation>() : listeDeliberation;
	}

	public void setListeDeliberation(ArrayList<Deliberation> listeDeliberation) {
		this.listeDeliberation = listeDeliberation;
	}

	public Deliberation getDeliberationCourant() {
		return deliberationCourant;
	}

	public void setDeliberationCourant(Deliberation deliberationCourant) {
		this.deliberationCourant = deliberationCourant;
	}

	public DeliberationDao getDeliberationDao() {
		return deliberationDao;
	}

	public void setDeliberationDao(DeliberationDao deliberationDao) {
		this.deliberationDao = deliberationDao;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DELIBERATION Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_DELIBERATION() {
		return "NOM_LB_DELIBERATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DELIBERATION_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_DELIBERATION_SELECT() {
		return "NOM_LB_DELIBERATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_DELIBERATION() {
		return getLB_DELIBERATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_DELIBERATION_SELECT() {
		return getZone(getNOM_LB_DELIBERATION_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DELIBERATION
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_DELIBERATION() {
		return "NOM_ST_ACTION_DELIBERATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_DELIBERATION() {
		return getZone(getNOM_ST_ACTION_DELIBERATION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_DELIBERATION Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_DELIBERATION() {
		return "NOM_PB_ANNULER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_DELIBERATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_DELIBERATION(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DELIBERATION Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_DELIBERATION() {
		return "NOM_PB_CREER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_DELIBERATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_DELIBERATION(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_DELIBERATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_DELIBERATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TEXTE_CAP_DELIBERATION(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_DELIBERATION_SELECT(), Const.ZERO);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DELIBERATION Date
	 * de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELIBERATION() {
		return "NOM_PB_SUPPRIMER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DELIBERATION(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_DELIBERATION_SELECT()) ? Integer.parseInt(getVAL_LB_DELIBERATION_SELECT()) : -1);
		if (indice != -1 && indice < getListeDeliberation().size()) {
			Deliberation delib = getListeDeliberation().get(indice);
			setDeliberationCourant(delib);

			int ligneTypeDelib = getListeTypeDeliberation().indexOf(delib.getTypeDeliberation());

			addZone(getNOM_LB_TYPE_DELIBERATION_SELECT(), String.valueOf(ligneTypeDelib));
			addZone(getNOM_EF_CODE_DELIBERATION(), delib.getCodeDeliberation());
			addZone(getNOM_EF_LIB_DELIBERATION(), delib.getLibDeliberation());
			addZone(getNOM_EF_TEXTE_CAP_DELIBERATION(), delib.getTexteCAP());

			addZone(getNOM_ST_ACTION_DELIBERATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "délibérations"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DELIBERATION Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_DELIBERATION() {
		return "NOM_PB_VALIDER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_DELIBERATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieDeliberation(request))
			return false;

		if (!performControlerRegleGestionDeliberation(request))
			return false;

		if (getVAL_ST_ACTION_DELIBERATION() != null && getVAL_ST_ACTION_DELIBERATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_DELIBERATION().equals(ACTION_CREATION)) {
				setDeliberationCourant(new Deliberation());

				// on recupere le type de déliberation
				int indiceTypeDelib = (Services.estNumerique(getVAL_LB_TYPE_DELIBERATION_SELECT()) ? Integer
						.parseInt(getVAL_LB_TYPE_DELIBERATION_SELECT()) : -1);
				String typeDelib = Const.CHAINE_VIDE;
				if (indiceTypeDelib > -1) {
					typeDelib = getListeTypeDeliberation().get(indiceTypeDelib);
				}

				getDeliberationCourant().setCodeDeliberation(getVAL_EF_CODE_DELIBERATION());
				getDeliberationCourant().setLibDeliberation(getVAL_EF_LIB_DELIBERATION());
				getDeliberationCourant().setTexteCAP(getVAL_EF_TEXTE_CAP_DELIBERATION());
				getDeliberationCourant().setTypeDeliberation(typeDelib);
				getDeliberationDao().creerDeliberation(getDeliberationCourant().getCodeDeliberation(), getDeliberationCourant().getLibDeliberation(),
						getDeliberationCourant().getTypeDeliberation(), getDeliberationCourant().getTexteCAP());
				getListeDeliberation().add(getDeliberationCourant());

			} else if (getVAL_ST_ACTION_DELIBERATION().equals(ACTION_SUPPRESSION)) {
				getDeliberationDao().supprimerDeliberation(getDeliberationCourant().getIdDeliberation());
				getListeDeliberation().remove(getDeliberationCourant());
				setDeliberationCourant(null);
			}
			initialiseListeDeliberation(request);
			addZone(getNOM_ST_ACTION_DELIBERATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_DELIBERATION
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_DELIBERATION() {
		return "NOM_EF_CODE_DELIBERATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_DELIBERATION() {
		return getZone(getNOM_EF_CODE_DELIBERATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_DELIBERATION
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_DELIBERATION() {
		return "NOM_EF_LIB_DELIBERATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_DELIBERATION() {
		return getZone(getNOM_EF_LIB_DELIBERATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_TEXTE_CAP_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_TEXTE_CAP_DELIBERATION() {
		return "NOM_EF_TEXTE_CAP_DELIBERATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TEXTE_CAP_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_TEXTE_CAP_DELIBERATION() {
		return getZone(getNOM_EF_TEXTE_CAP_DELIBERATION());
	}

	/**
	 * Contrôle les zones saisies d'une délibération Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieDeliberation(HttpServletRequest request) throws Exception {
		// Verification code délibération not null
		if (getZone(getNOM_EF_CODE_DELIBERATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		// Verification libellé délibération not null
		if (getZone(getNOM_EF_LIB_DELIBERATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification texte CAP not null
		if (getZone(getNOM_EF_TEXTE_CAP_DELIBERATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "texte CAP"));
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une délibération Date de création :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionDeliberation(HttpServletRequest request) throws Exception {
		// Verification si suppression d'une délibération utilisée sur un
		// avancement
		// TODO

		/*
		 * if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION) &&
		 * Avancement.listerAvancementAvecMotif(getTransaction(),
		 * getMotifCourant()).size() > 0) { // "ERR989", //
		 * "Suppression impossible. Il existe au moins @ rattaché à @."
		 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR989",
		 * "un avancement", "ce motif d'avancement")); return false; }
		 */

		// Vérification des contraintes d'unicité de la délibération
		if (getVAL_ST_ACTION_DELIBERATION().equals(ACTION_CREATION)) {

			for (Deliberation delib : getListeDeliberation()) {
				if (delib.getCodeDeliberation().equals(getVAL_EF_CODE_DELIBERATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une délibération", "ce code"));
					return false;
				}
				if (delib.getLibDeliberation().equals(getVAL_EF_LIB_DELIBERATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une délibération", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	public ArrayList<String> getListeTypeDeliberation() {
		return listeTypeDeliberation == null ? new ArrayList<String>() : listeTypeDeliberation;
	}

	public void setListeTypeDeliberation(ArrayList<String> listeTypeDeliberation) {
		this.listeTypeDeliberation = listeTypeDeliberation;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELIBERATION Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_TYPE_DELIBERATION() {
		if (LB_TYPE_DELIBERATION == null)
			LB_TYPE_DELIBERATION = initialiseLazyLB();
		return LB_TYPE_DELIBERATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELIBERATION Date de création : (14/09/11
	 * 13:52:54)
	 * 
	 */
	private void setLB_TYPE_DELIBERATION(String[] newLB_TYPE_DELIBERATION) {
		LB_TYPE_DELIBERATION = newLB_TYPE_DELIBERATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELIBERATION Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_DELIBERATION() {
		return "NOM_LB_TYPE_DELIBERATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DELIBERATION_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_DELIBERATION_SELECT() {
		return "NOM_LB_TYPE_DELIBERATION_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELIBERATION() {
		return getLB_TYPE_DELIBERATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_DELIBERATION Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_TYPE_DELIBERATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELIBERATION_SELECT());
	}

	public String[] getLB_CAP() {
		if (LB_CAP == null)
			LB_CAP = initialiseLazyLB();
		return LB_CAP;
	}

	public void setLB_CAP(String[] lB_CAP) {
		LB_CAP = lB_CAP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CAP Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_CAP() {
		return "NOM_LB_CAP";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CAP_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_CAP_SELECT() {
		return "NOM_LB_CAP_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CAP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_CAP() {
		return getLB_CAP();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CAP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_CAP_SELECT() {
		return getZone(getNOM_LB_CAP_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_CAP Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_CAP() {
		return "NOM_ST_ACTION_CAP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_CAP
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_CAP() {
		return getZone(getNOM_ST_ACTION_CAP());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CAP Date de création
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_CAP() {
		return "NOM_PB_ANNULER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_CAP(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_CAP(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CAP Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_CAP() {
		return "NOM_PB_CREER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_CAP(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_CAP(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_CAP(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_CAP(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CAP Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CAP() {
		return "NOM_PB_SUPPRIMER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CAP(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_CAP_SELECT()) : -1);
		if (indice != -1 && indice < getListeCap().size()) {
			Cap cap = getListeCap().get(indice);
			setCapCourant(cap);

			addZone(getNOM_EF_CODE_CAP(), cap.getCodeCap());
			addZone(getNOM_EF_REF_CAP(), cap.getRefCap());

			addZone(getNOM_ST_ACTION_CAP(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "cap"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CAP Date de création
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_CAP() {
		return "NOM_PB_VALIDER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_CAP(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieCap(request))
			return false;

		if (!performControlerRegleGestionCap(request))
			return false;

		if (getVAL_ST_ACTION_CAP() != null && getVAL_ST_ACTION_CAP() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_CAP().equals(ACTION_CREATION)) {
				setCapCourant(new Cap());

				getCapCourant().setCodeCap(getVAL_EF_CODE_CAP());
				getCapCourant().setRefCap(getVAL_EF_REF_CAP());
				getCapDao().creerCap(getCapCourant().getCodeCap(), getCapCourant().getRefCap());
				getListeCap().add(getCapCourant());

			} else if (getVAL_ST_ACTION_CAP().equals(ACTION_SUPPRESSION)) {
				getCapDao().supprimerCap(getCapCourant().getIdCap());
				getListeCap().remove(getCapCourant());
				setCapCourant(null);
			}
			initialiseListeCap(request);
			addZone(getNOM_ST_ACTION_CAP(), Const.CHAINE_VIDE);
		}

		return true;
	}

	public ArrayList<Cap> getListeCap() {
		return listeCap == null ? new ArrayList<Cap>() : listeCap;
	}

	public void setListeCap(ArrayList<Cap> listeCap) {
		this.listeCap = listeCap;
	}

	public Cap getCapCourant() {
		return capCourant;
	}

	public void setCapCourant(Cap capCourant) {
		this.capCourant = capCourant;
	}

	public CapDao getCapDao() {
		return capDao;
	}

	public void setCapDao(CapDao capDao) {
		this.capDao = capDao;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_CAP Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_CAP() {
		return "NOM_EF_CODE_CAP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_CAP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_CAP() {
		return getZone(getNOM_EF_CODE_CAP());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_CAP Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_REF_CAP() {
		return "NOM_EF_REF_CAP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_CAP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_REF_CAP() {
		return getZone(getNOM_EF_REF_CAP());
	}

	/**
	 * Contrôle les zones saisies d'une cap Date de création : (14/09/11)
	 */
	private boolean performControlerSaisieCap(HttpServletRequest request) throws Exception {
		// Verification reference cap not null
		if (getZone(getNOM_EF_REF_CAP()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "référence"));
			return false;
		}
		// Verification code cap not null
		if (getZone(getNOM_EF_CODE_CAP()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les règles de gestion d'une cap Date de création : (14/09/11)
	 */
	private boolean performControlerRegleGestionCap(HttpServletRequest request) throws Exception {
		// Verification si suppression d'une cap utilisée sur un
		// avancement
		// TODO

		/*
		 * if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION) &&
		 * Avancement.listerAvancementAvecMotif(getTransaction(),
		 * getMotifCourant()).size() > 0) { // "ERR989", //
		 * "Suppression impossible. Il existe au moins @ rattaché à @."
		 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR989",
		 * "un avancement", "ce motif d'avancement")); return false; }
		 */

		// Vérification des contraintes d'unicité de la cap
		if (getVAL_ST_ACTION_CAP().equals(ACTION_CREATION)) {

			for (Cap cap : getListeCap()) {
				if (cap.getCodeCap().equals(getVAL_EF_CODE_CAP().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une CAP", "ce code"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_EMPLOYEUR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_EMPLOYEUR(int i) {
		return "NOM_CK_SELECT_LIGNE_EMPLOYEUR_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE_EMPLOYEUR Date de création : (21/11/11
	 * 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_EMPLOYEUR(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_EMPLOYEUR(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_REPRESENTANT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_REPRESENTANT(int i) {
		return "NOM_CK_SELECT_LIGNE_REPRESENTANT_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE_REPRESENTANT Date de création : (21/11/11
	 * 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_REPRESENTANT(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_REPRESENTANT(i));
	}
}
