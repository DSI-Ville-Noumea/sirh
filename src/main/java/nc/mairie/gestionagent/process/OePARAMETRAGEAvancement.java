package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.avancement.Avancement;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.metier.parametrage.EmployeurDao;
import nc.mairie.spring.dao.metier.parametrage.RepresentantDao;
import nc.mairie.spring.dao.metier.referentiel.TypeRepresentantDao;
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
 * Process OePARAMETRAGERecrutement Date de cr�ation : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEAvancement extends nc.mairie.technique.BasicProcess {
	private String[] LB_MOTIF;
	private String[] LB_EMPLOYEUR;
	private String[] LB_REPRESENTANT;
	private String[] LB_TYPE_REPRESENTANT;

	private ArrayList<MotifAvancement> listeMotif;
	private MotifAvancement motifCourant;

	private ArrayList<Employeur> listeEmployeur;
	private Employeur employeurCourant;
	private EmployeurDao employeurDao;

	private ArrayList<Representant> listeRepresentant;
	private Representant representantCourant;
	private RepresentantDao representantDao;

	private ArrayList<TypeRepresentant> listeTypeRepresentant;
	private TypeRepresentant typeRepresentantCourant;
	private TypeRepresentantDao typeRepresentantDao;
	private Hashtable<Integer, TypeRepresentant> hashTypeRepresentant;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// V�rification des droits d'acc�s. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
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
	}

	/**
	 * Initialisation de la listes des motifs d'avancement Date de cr�ation :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		setListeMotif(MotifAvancement.listerMotifAvancement(getTransaction()));
		if (getListeMotif().size() != 0) {
			int tailles[] = { 40,10 };
			String padding[] = { "G","G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeMotif().listIterator(); list.hasNext();) {
				MotifAvancement ma = (MotifAvancement) list.next();
				String ligne[] = { ma.getLibMotifAvct(),ma.getCodeMotifAvct() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
		}
	}

	/**
	 * Initialisation de la listes des employeurs Date de cr�ation : (14/09/11)
	 * 
	 */
	private void initialiseListeEmployeur(HttpServletRequest request) throws Exception {
		setListeEmployeur(getEmployeurDao().listerEmployeur());
		if (getListeEmployeur().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeEmployeur().listIterator(); list.hasNext();) {
				Employeur emp = (Employeur) list.next();
				String ligne[] = { emp.getLibEmployeur() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_EMPLOYEUR(aFormat.getListeFormatee());
		} else {
			setLB_EMPLOYEUR(null);
		}
	}

	/**
	 * Initialisation de la listes des repr�sentants Date de cr�ation :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeRepresentant(HttpServletRequest request) throws Exception {
		setListeRepresentant(getRepresentantDao().listerRepresentant());
		if (getListeRepresentant().size() != 0) {
			int tailles[] = { 20,20,10 };
			String padding[] = { "G","G","G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeRepresentant().listIterator(); list.hasNext();) {
				Representant repre = (Representant) list.next();
				TypeRepresentant typeRepre = getTypeRepresentantDao().chercherTypeRepresentant(repre.getIdTypeRepresentant());
				String ligne[] = { repre.getNomRepresentant(),repre.getPrenomRepresentant(),typeRepre.getLibTypeRepresentant() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_REPRESENTANT(aFormat.getListeFormatee());
		} else {
			setLB_REPRESENTANT(null);
		}
	}

	/**
	 * Initialisation de la listes des types de repr�sentants Date de cr�ation :
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
	 * Constructeur du process OePARAMETRAGEAvancement. Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAvancement() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MOTIF Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_MOTIF() {
		return "NOM_PB_ANNULER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_MOTIF(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MOTIF(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MOTIF Date de cr�ation
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_MOTIF() {
		return "NOM_PB_CREER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MOTIF() {
		return "NOM_PB_SUPPRIMER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_MOTIF() {
		return "NOM_PB_VALIDER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * Contr�le les zones saisies d'un motif de recrutement Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieMotif(HttpServletRequest request) throws Exception {
		// Verification libell� motif not null
		if (getZone(getNOM_EF_LIB_MOTIF()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
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
	 * Contr�le les r�gles de gestion d'une entit� g�ographique Date de cr�ation
	 * : (14/09/11)
	 */
	private boolean performControlerRegleGestionMotif(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un motif avancement utilis�e sur un
		// avancement
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)
				&& Avancement.listerAvancementAvecMotif(getTransaction(), getMotifCourant()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un avancement", "ce motif d'avancement"));
			return false;
		}

		// V�rification des contraintes d'unicit� de l'entit� g�ographique
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {

			for (MotifAvancement motif : getListeMotif()) {
				if (motif.getLibMotifAvct().equals(getVAL_EF_LIB_MOTIF().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un motif d'avancement", "ce libell�"));
					return false;
				}
				if (motif.getCodeMotifAvct().equals(getVAL_EF_CODE_MOTIF().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un motif d'avancement", "ce code"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MOTIF Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION_MOTIF
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_MOTIF Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_MOTIF() {
		return "NOM_EF_LIB_MOTIF";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_LIB_MOTIF Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_MOTIF() {
		return getZone(getNOM_EF_LIB_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_MOTIF Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_MOTIF() {
		return "NOM_EF_CODE_MOTIF";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_CODE_MOTIF Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_MOTIF() {
		return getZone(getNOM_EF_CODE_MOTIF());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_MOTIF Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_MOTIF Date de cr�ation : (14/09/11 13:52:54)
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
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (14/09/11 13:52:54)
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

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEAvancement.jsp";
	}

	/**
	 * Retourne le nom de l'�cran (notamment pour d�terminer les droits
	 * associ�s).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-AVANCEMENT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_EMPLOYEUR
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_EMPLOYEUR() {
		return "NOM_ST_ACTION_EMPLOYEUR";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_EMPLOYEUR Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_EMPLOYEUR() {
		return getZone(getNOM_ST_ACTION_EMPLOYEUR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_EMPLOYEUR Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_EMPLOYEUR() {
		return "NOM_EF_EMPLOYEUR";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_EMPLOYEUR Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_EMPLOYEUR() {
		return getZone(getNOM_EF_EMPLOYEUR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_EMPLOYEUR
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_TITRE_EMPLOYEUR() {
		return "NOM_EF_TITRE_EMPLOYEUR";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_EMPLOYEUR Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_TITRE_EMPLOYEUR() {
		return getZone(getNOM_EF_TITRE_EMPLOYEUR());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_EMPLOYEUR Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_EMPLOYEUR() {
		if (LB_EMPLOYEUR == null)
			LB_EMPLOYEUR = initialiseLazyLB();
		return LB_EMPLOYEUR;
	}

	/**
	 * Setter de la liste: LB_EMPLOYEUR Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_EMPLOYEUR(String[] newLB_EMPLOYEUR) {
		LB_EMPLOYEUR = newLB_EMPLOYEUR;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_EMPLOYEUR Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_EMPLOYEUR() {
		return "NOM_LB_EMPLOYEUR";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_EMPLOYEUR_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_EMPLOYEUR_SELECT() {
		return "NOM_LB_EMPLOYEUR_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_EMPLOYEUR Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_EMPLOYEUR() {
		return getLB_EMPLOYEUR();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_EMPLOYEUR Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_EMPLOYEUR_SELECT() {
		return getZone(getNOM_LB_EMPLOYEUR_SELECT());
	}

	private ArrayList<Employeur> getListeEmployeur() {
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_EMPLOYEUR() {
		return "NOM_PB_ANNULER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_EMPLOYEUR(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_EMPLOYEUR(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_EMPLOYEUR Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_EMPLOYEUR() {
		return "NOM_PB_CREER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_EMPLOYEUR() {
		return "NOM_PB_SUPPRIMER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_EMPLOYEUR() {
		return "NOM_PB_VALIDER_EMPLOYEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * Contr�le les zones saisies d'un employeur Date de cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieEmployeur(HttpServletRequest request) throws Exception {
		// Verification libell� employeur not null
		if (getZone(getNOM_EF_EMPLOYEUR()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
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
	 * Contr�le les r�gles de gestion d'un employeur Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionEmployeur(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un employeur utilis�e sur un
		// avancement
		/*
		 * if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_SUPPRESSION) &&
		 * getEmployeurDao().listerEmployeur() &&
		 * Avancement.listerAvancementAvecMotif(getTransaction(),
		 * getMotifCourant()).size() > 0) { // TODO // "ERR989", //
		 * "Suppression impossible. Il existe au moins @ rattach� � @."
		 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR989",
		 * "un avancement", "ce motif d'avancement")); return false; }
		 */

		// V�rification des contraintes d'unicit� de l'employeur
		if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_CREATION)) {

			for (Employeur emp : getListeEmployeur()) {
				if (emp.getLibEmployeur().equals(getVAL_EF_EMPLOYEUR())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un employeur", "ce libell�"));
					return false;
				}
				if (emp.getTitreEmployeur().equals(getVAL_EF_TITRE_EMPLOYEUR())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_REPRESENTANT() {
		if (LB_REPRESENTANT == null)
			LB_REPRESENTANT = initialiseLazyLB();
		return LB_REPRESENTANT;
	}

	/**
	 * Setter de la liste: LB_REPRESENTANT Date de cr�ation : (14/09/11
	 * 13:52:54)
	 * 
	 */
	private void setLB_REPRESENTANT(String[] newLB_REPRESENTANT) {
		LB_REPRESENTANT = newLB_REPRESENTANT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REPRESENTANT Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_REPRESENTANT() {
		return "NOM_LB_REPRESENTANT";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_REPRESENTANT_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_REPRESENTANT_SELECT() {
		return "NOM_LB_REPRESENTANT_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_REPRESENTANT() {
		return getLB_REPRESENTANT();
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_REPRESENTANT
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_REPRESENTANT() {
		return "NOM_ST_ACTION_REPRESENTANT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_REPRESENTANT() {
		return getZone(getNOM_ST_ACTION_REPRESENTANT());
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_REPRESENTANT_SELECT() {
		return getZone(getNOM_LB_REPRESENTANT_SELECT());
	}

	private ArrayList<Representant> getListeRepresentant() {
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_REPRESENTANT() {
		return "NOM_PB_ANNULER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_REPRESENTANT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_REPRESENTANT(), "");
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_REPRESENTANT Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_REPRESENTANT() {
		return "NOM_PB_CREER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REPRESENTANT() {
		return "NOM_PB_SUPPRIMER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "repr�sentants"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_REPRESENTANT Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_REPRESENTANT() {
		return "NOM_PB_VALIDER_REPRESENTANT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * Contr�le les zones saisies d'un employeur Date de cr�ation : (14/09/11)
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
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "pr�nom"));
			return false;
		}
		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un employeur Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionRepresentant(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un employeur utilis�e sur un
		// avancement

		/*
		 * if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_SUPPRESSION) &&
		 * getRepresentantDao().listerRepresentant() &&
		 * Avancement.listerAvancementAvecMotif(getTransaction(),
		 * getMotifCourant()).size() > 0) { // TODO // "ERR989", //
		 * "Suppression impossible. Il existe au moins @ rattach� � @."
		 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR989",
		 * "un avancement", "ce motif d'avancement")); return false; }
		 */

		// V�rification des contraintes d'unicit� de l'employeur
		if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_CREATION)) {

			for (Representant repre : getListeRepresentant()) {
				if (repre.getNomRepresentant().equals(getVAL_EF_NOM_REPRESENTANT())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un repr�sentant", "ce nom"));
					return false;
				}
				if (repre.getPrenomRepresentant().equals(getVAL_EF_PRENOM_REPRESENTANT())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un repr�sentant", "ce pr�nom"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REPRESENTANT Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_TYPE_REPRESENTANT() {
		if (LB_TYPE_REPRESENTANT == null)
			LB_TYPE_REPRESENTANT = initialiseLazyLB();
		return LB_TYPE_REPRESENTANT;
	}

	/**
	 * Setter de la liste: LB_TYPE_REPRESENTANT Date de cr�ation : (14/09/11
	 * 13:52:54)
	 * 
	 */
	private void setLB_TYPE_REPRESENTANT(String[] newLB_TYPE_REPRESENTANT) {
		LB_TYPE_REPRESENTANT = newLB_TYPE_REPRESENTANT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REPRESENTANT Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_REPRESENTANT() {
		return "NOM_LB_TYPE_REPRESENTANT";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_REPRESENTANT_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_REPRESENTANT_SELECT() {
		return "NOM_LB_TYPE_REPRESENTANT_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REPRESENTANT() {
		return getLB_TYPE_REPRESENTANT();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
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

	private TypeRepresentant getTypeRepresentantCourant() {
		return typeRepresentantCourant;
	}

	private void setTypeRepresentantCourant(TypeRepresentant typeRepresentantCourant) {
		this.typeRepresentantCourant = typeRepresentantCourant;
	}

	public TypeRepresentantDao getTypeRepresentantDao() {
		return typeRepresentantDao;
	}

	public void setTypeRepresentantDao(TypeRepresentantDao typeRepresentantDao) {
		this.typeRepresentantDao = typeRepresentantDao;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_REPRESENTANT
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_NOM_REPRESENTANT() {
		return "NOM_EF_NOM_REPRESENTANT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NOM_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_NOM_REPRESENTANT() {
		return getZone(getNOM_EF_NOM_REPRESENTANT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM_REPRESENTANT
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_PRENOM_REPRESENTANT() {
		return "NOM_EF_PRENOM_REPRESENTANT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM_REPRESENTANT Date de cr�ation : (14/09/11 13:52:54)
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
}
