package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.metier.parametrage.CapDao;
import nc.mairie.spring.dao.metier.parametrage.CorpsCapDao;
import nc.mairie.spring.dao.metier.parametrage.DeliberationDao;
import nc.mairie.spring.dao.metier.parametrage.EmployeurCapDao;
import nc.mairie.spring.dao.metier.parametrage.EmployeurDao;
import nc.mairie.spring.dao.metier.parametrage.RepresentantCapDao;
import nc.mairie.spring.dao.metier.parametrage.RepresentantDao;
import nc.mairie.spring.dao.metier.referentiel.TypeRepresentantDao;
import nc.mairie.spring.domain.metier.parametrage.Cap;
import nc.mairie.spring.domain.metier.parametrage.CorpsCap;
import nc.mairie.spring.domain.metier.parametrage.Deliberation;
import nc.mairie.spring.domain.metier.parametrage.Employeur;
import nc.mairie.spring.domain.metier.parametrage.EmployeurCap;
import nc.mairie.spring.domain.metier.parametrage.Representant;
import nc.mairie.spring.domain.metier.parametrage.RepresentantCap;
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
	private String[] LB_DELIBERATION;
	private String[] LB_TYPE_DELIBERATION;
	private String[] LB_CAP;
	private String[] LB_CORPS;
	private String[] LB_TYPE_CAP;
	private String[] LB_REPRE_CAP;
	private String[] LB_REPRE_CAP_MULTI;
	private String[] LB_EMP_CAP;
	private String[] LB_EMP_CAP_MULTI;

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
	private ArrayList<String> listeTypeCap;

	private ArrayList<Cap> listeCap;
	private Cap capCourant;
	private CapDao capDao;

	private EmployeurCapDao employeurCapDao;
	private RepresentantCapDao representantCapDao;
	private CorpsCapDao corpsCapDao;
	private ArrayList<Employeur> listeEmployeurCap;
	private ArrayList<Representant> listeRepresentantCap;
	private ArrayList<GradeGenerique> listeCorpsCap;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	private ArrayList<GradeGenerique> listeCorps;
	public String focus = null;

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
		if (getListeDeliberation().size() == 0) {
			initialiseListeDeliberation(request);
		}
		if (getListeTypeDeliberation().size() == 0) {
			initialiseListeTypeDeliberation(request);
		}
		if (getListeTypeCap().size() == 0) {
			initialiseListeTypeCap(request);
		}
		if (getListeCap().size() == 0) {
			initialiseListeCap(request);
		}
		if (getListeCorps().size() == 0) {
			initialiseListeCorps(request);
		}
		if (getListeCorpsCap().size() == 0) {
			initialiseListeCorpsCap(request);
		}
	}

	private void initialiseListeCorpsCap(HttpServletRequest request) {
		// Afiichage des corps deja pr�sent
		if (getListeCorpsCap().size() > 0) {
			for (int i = 0; i < getListeCorps().size(); i++) {
				GradeGenerique gg = getListeCorps().get(i);
				addZone(getNOM_CK_SELECT_LIGNE_CORPS(i), getCHECKED_OFF());
				for (int j = 0; j < getListeCorpsCap().size(); j++) {
					GradeGenerique ggCap = getListeCorpsCap().get(j);
					if (gg.getCdgeng().toString().equals(ggCap.getCdgeng().toString())) {
						addZone(getNOM_CK_SELECT_LIGNE_CORPS(i), getCHECKED_ON());
						break;
					}
				}
			}
		} else {
			for (int i = 0; i < getListeCorps().size(); i++) {
				addZone(getNOM_CK_SELECT_LIGNE_CORPS(i), getCHECKED_OFF());
			}

		}

	}

	private void initialiseListeTypeCap(HttpServletRequest request) {
		ArrayList<String> listeTypeCap = new ArrayList<String>();
		listeTypeCap.add("COMMUNAL");
		listeTypeCap.add("TERRITORIAL");
		setListeTypeCap(listeTypeCap);

		if (getListeTypeCap().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeTypeCap().listIterator(); list.hasNext();) {
				String type = (String) list.next();
				String ligne[] = { type };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_CAP(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_CAP(null);
		}
	}

	private void initialiseListeCorps(HttpServletRequest request) throws Exception {
		setListeCorps(GradeGenerique.listerGradeGeneriqueActif(getTransaction()));
		if (getListeCorps().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeCorps().listIterator(); list.hasNext();) {
				GradeGenerique gg = (GradeGenerique) list.next();
				String ligne[] = { gg.getLibGradeGenerique() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CORPS(aFormat.getListeFormatee());
		} else {
			setLB_CORPS(null);
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
		if (getEmployeurCapDao() == null) {
			setEmployeurCapDao((EmployeurCapDao) context.getBean("employeurCapDao"));
		}
		if (getRepresentantCapDao() == null) {
			setRepresentantCapDao((RepresentantCapDao) context.getBean("representantCapDao"));
		}
		if (getCorpsCapDao() == null) {
			setCorpsCapDao((CorpsCapDao) context.getBean("corpsCapDao"));
		}
	}

	/**
	 * Initialisation de la listes des cap Date de cr�ation : (14/09/11)
	 * 
	 */
	private void initialiseListeCap(HttpServletRequest request) throws Exception {
		setListeCap(getCapDao().listerCap());
		if (getListeCap().size() != 0) {
			int tailles[] = { 10, 15, 10 };
			String padding[] = { "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeCap().listIterator(); list.hasNext();) {
				Cap cap = (Cap) list.next();
				String ligne[] = { cap.getCodeCap(), cap.getTypeCap(), cap.getRefCap() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CAP(aFormat.getListeFormatee());
		} else {
			setLB_CAP(null);
		}

		setListeRepresentantCap(getRepresentantDao().listerRepresentantOrderByNom());
		if (getListeRepresentantCap().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeRepresentant().listIterator(); list.hasNext();) {
				Representant repre = (Representant) list.next();
				TypeRepresentant typeRepre = getTypeRepresentantDao().chercherTypeRepresentant(repre.getIdTypeRepresentant());
				String ligne[] = { repre.getNomRepresentant() + " " + repre.getPrenomRepresentant() + "( " + typeRepre.getLibTypeRepresentant()
						+ " )" };

				aFormat.ajouteLigne(ligne);
			}
			setLB_REPRE_CAP(aFormat.getListeFormatee(true));
		} else {
			setLB_REPRE_CAP(null);
		}

		setListeEmployeurCap(getEmployeurDao().listerEmployeur());
		if (getListeEmployeurCap().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeEmployeurCap().listIterator(); list.hasNext();) {
				Employeur emp = (Employeur) list.next();
				String ligne[] = { emp.getLibEmployeur() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_EMP_CAP(aFormat.getListeFormatee(true));
		} else {
			setLB_EMP_CAP(null);
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
	 * Initialisation de la listes des employeurs Date de cr�ation : (14/09/11)
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
	 * Initialisation de la listes des repr�sentants Date de cr�ation :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeRepresentant(HttpServletRequest request) throws Exception {
		setListeRepresentant(getRepresentantDao().listerRepresentantOrderByNom());
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
	 * Initialisation de la listes des types de d�lib�ration Date de cr�ation :
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
	 * Initialisation de la listes des d�lib�rations Date de cr�ation :
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
		addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
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
		addZone(getNOM_EF_LIB_MOTIF(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_MOTIF(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
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

		setFocus(getNOM_PB_ANNULER_MOTIF());
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

		setFocus(getNOM_PB_ANNULER_MOTIF());
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
				&& AvancementFonctionnaires.listerAvancementAvecMotif(getTransaction(), getMotifCourant()).size() > 0) {
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

			// Si clic sur le bouton PB_MODIFIER_CAP
			if (testerParametre(request, getNOM_PB_MODIFIER_CAP())) {
				return performPB_MODIFIER_CAP(request);
			}

			// Si clic sur le bouton PB_AJOUTER_REPRESENTANT_CAP
			if (testerParametre(request, getNOM_PB_AJOUTER_REPRESENTANT_CAP())) {
				return performPB_AJOUTER_REPRESENTANT_CAP(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_REPRESENTANT_CAP
			if (testerParametre(request, getNOM_PB_SUPPRIMER_REPRESENTANT_CAP())) {
				return performPB_SUPPRIMER_REPRESENTANT_CAP(request);
			}

			// Si clic sur le bouton PB_AJOUTER_EMPLOYEUR_CAP
			if (testerParametre(request, getNOM_PB_AJOUTER_EMPLOYEUR_CAP())) {
				return performPB_AJOUTER_EMPLOYEUR_CAP(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_EMPLOYEUR_CAP
			if (testerParametre(request, getNOM_PB_SUPPRIMER_EMPLOYEUR_CAP())) {
				return performPB_SUPPRIMER_EMPLOYEUR_CAP(request);
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
		addZone(getNOM_ST_ACTION_EMPLOYEUR(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_EMPLOYEUR());
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
		addZone(getNOM_EF_EMPLOYEUR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TITRE_EMPLOYEUR(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_EMPLOYEUR());
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

		setFocus(getNOM_PB_ANNULER_EMPLOYEUR());
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

		setFocus(getNOM_PB_ANNULER_EMPLOYEUR());
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
		// Verification si suppression d'un employeur utilis�e sur une CAP
		if (getVAL_ST_ACTION_EMPLOYEUR().equals(ACTION_SUPPRESSION)
				&& getEmployeurCapDao().listerEmployeurCapParEmployeur(getEmployeurCourant().getIdEmployeur()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une CAP", "cet employeur"));
			return false;
		}

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
		addZone(getNOM_ST_ACTION_REPRESENTANT(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_REPRESENTANT());
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
		addZone(getNOM_EF_NOM_REPRESENTANT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PRENOM_REPRESENTANT(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_REPRESENTANT());
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

		setFocus(getNOM_PB_ANNULER_REPRESENTANT());
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

		setFocus(getNOM_PB_ANNULER_REPRESENTANT());
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
		// Verification si suppression d'un employeur utilis�e sur une
		// cap
		if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_SUPPRESSION)
				&& getRepresentantCapDao().listerRepresentantCapParRepresentant(getRepresentantCourant().getIdRepresentant()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une CAP", "ce repr�sentant"));
			return false;
		}

		// V�rification des contraintes d'unicit� de l'employeur
		if (getVAL_ST_ACTION_REPRESENTANT().equals(ACTION_CREATION)) {

			for (Representant repre : getListeRepresentant()) {
				if (repre.getNomRepresentant().trim().equals(getVAL_EF_NOM_REPRESENTANT().toUpperCase().trim()) && repre.getPrenomRepresentant().trim().equals(getVAL_EF_PRENOM_REPRESENTANT().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un repr�sentant", "ce nom et ce pr�nom"));
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_DELIBERATION() {
		return "NOM_LB_DELIBERATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_DELIBERATION_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_DELIBERATION_SELECT() {
		return "NOM_LB_DELIBERATION_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_DELIBERATION() {
		return getLB_DELIBERATION();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_DELIBERATION_SELECT() {
		return getZone(getNOM_LB_DELIBERATION_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DELIBERATION
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_DELIBERATION() {
		return "NOM_ST_ACTION_DELIBERATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_DELIBERATION() {
		return getZone(getNOM_ST_ACTION_DELIBERATION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_DELIBERATION Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_DELIBERATION() {
		return "NOM_PB_ANNULER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_DELIBERATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_DELIBERATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_DELIBERATION());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DELIBERATION Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_DELIBERATION() {
		return "NOM_PB_CREER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
		setFocus(getNOM_PB_ANNULER_DELIBERATION());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DELIBERATION Date
	 * de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELIBERATION() {
		return "NOM_PB_SUPPRIMER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "d�lib�rations"));
		}

		setFocus(getNOM_PB_ANNULER_DELIBERATION());
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DELIBERATION Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_DELIBERATION() {
		return "NOM_PB_VALIDER_DELIBERATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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

				// on recupere le type de d�liberation
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

		setFocus(getNOM_PB_ANNULER_DELIBERATION());
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_DELIBERATION
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_DELIBERATION() {
		return "NOM_EF_CODE_DELIBERATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_CODE_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_DELIBERATION() {
		return getZone(getNOM_EF_CODE_DELIBERATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_DELIBERATION
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_DELIBERATION() {
		return "NOM_EF_LIB_DELIBERATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_LIB_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_DELIBERATION() {
		return getZone(getNOM_EF_LIB_DELIBERATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_TEXTE_CAP_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_TEXTE_CAP_DELIBERATION() {
		return "NOM_EF_TEXTE_CAP_DELIBERATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_TEXTE_CAP_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_TEXTE_CAP_DELIBERATION() {
		return getZone(getNOM_EF_TEXTE_CAP_DELIBERATION());
	}

	/**
	 * Contr�le les zones saisies d'une d�lib�ration Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieDeliberation(HttpServletRequest request) throws Exception {
		// Verification code d�lib�ration not null
		if (getZone(getNOM_EF_CODE_DELIBERATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		// Verification libell� d�lib�ration not null
		if (getZone(getNOM_EF_LIB_DELIBERATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
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
	 * Contr�le les r�gles de gestion d'une d�lib�ration Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionDeliberation(HttpServletRequest request) throws Exception {
		// Verification si suppression d'une d�lib�ration utilis�e sur un
		// grade g�n�rique

		if (getVAL_ST_ACTION_DELIBERATION().equals(ACTION_SUPPRESSION)
				&& GradeGenerique.listerGradeGeneriqueAvecDeliberation(getTransaction(), getDeliberationCourant().getIdDeliberation()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une d�lib�ration", "un grade g�n�rique"));
			return false;
		}

		// V�rification des contraintes d'unicit� de la d�lib�ration
		if (getVAL_ST_ACTION_DELIBERATION().equals(ACTION_CREATION)) {

			for (Deliberation delib : getListeDeliberation()) {
				if (delib.getCodeDeliberation().equals(getVAL_EF_CODE_DELIBERATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une d�lib�ration", "ce code"));
					return false;
				}
				if (delib.getLibDeliberation().equals(getVAL_EF_LIB_DELIBERATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une d�lib�ration", "ce libell�"));
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_TYPE_DELIBERATION() {
		if (LB_TYPE_DELIBERATION == null)
			LB_TYPE_DELIBERATION = initialiseLazyLB();
		return LB_TYPE_DELIBERATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELIBERATION Date de cr�ation : (14/09/11
	 * 13:52:54)
	 * 
	 */
	private void setLB_TYPE_DELIBERATION(String[] newLB_TYPE_DELIBERATION) {
		LB_TYPE_DELIBERATION = newLB_TYPE_DELIBERATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELIBERATION Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_DELIBERATION() {
		return "NOM_LB_TYPE_DELIBERATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_DELIBERATION_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_DELIBERATION_SELECT() {
		return "NOM_LB_TYPE_DELIBERATION_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELIBERATION() {
		return getLB_TYPE_DELIBERATION();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_DELIBERATION Date de cr�ation : (14/09/11 13:52:54)
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
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CAP Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_CAP() {
		return "NOM_LB_CAP";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_CAP_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_CAP_SELECT() {
		return "NOM_LB_CAP_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_CAP() {
		return getLB_CAP();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_CAP_SELECT() {
		return getZone(getNOM_LB_CAP_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_CAP Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_CAP() {
		return "NOM_ST_ACTION_CAP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION_CAP
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_CAP() {
		return getZone(getNOM_ST_ACTION_CAP());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_CAP Date de cr�ation
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_CAP() {
		return "NOM_PB_ANNULER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_CAP(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_CAP(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_CAP Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_CAP() {
		return "NOM_PB_CREER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_CAP(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_CAP(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_CAP(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_CAP(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DESCRIPTION_CAP(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_CAP_SELECT(), Const.ZERO);
		setLB_REPRE_CAP_MULTI(null);
		setLB_EMP_CAP_MULTI(null);
		addZone(getNOM_LB_REPRE_CAP_SELECT(), Const.ZERO);
		addZone(getNOM_LB_EMP_CAP_SELECT(), Const.ZERO);

		setListeEmployeurCap(null);
		setListeRepresentantCap(null);
		setListeCorpsCap(null);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_CAP Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_CAP() {
		return "NOM_PB_SUPPRIMER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_CAP(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_CAP_SELECT()) : -1);
		if (indice != -1 && indice < getListeCap().size()) {
			Cap cap = getListeCap().get(indice);
			setCapCourant(cap);

			int ligneTypeCap = getListeTypeCap().indexOf(cap.getTypeCap());

			addZone(getNOM_LB_TYPE_CAP_SELECT(), String.valueOf(ligneTypeCap));
			addZone(getNOM_EF_CODE_CAP(), cap.getCodeCap());
			addZone(getNOM_EF_REF_CAP(), cap.getRefCap());
			addZone(getNOM_EF_DESCRIPTION_CAP(), cap.getDescription());

			// on affiche la liste des employeurs CAP
			ArrayList<EmployeurCap> listeEmpCap = getEmployeurCapDao().listerEmployeurCapParCap(getCapCourant().getIdCap());
			ArrayList<Employeur> listeTempEmp = new ArrayList<Employeur>();
			for (int i = 0; i < listeEmpCap.size(); i++) {
				EmployeurCap empCap = listeEmpCap.get(i);
				Employeur emp = getEmployeurDao().chercherEmployeur(empCap.getIdEmployeur());
				listeTempEmp.add(emp);
			}
			setListeEmployeurCap(listeTempEmp);

			// on affiche la liste des repr�sentant CAP
			ArrayList<RepresentantCap> listeRepreCap = getRepresentantCapDao().listerRepresentantCapParCap(getCapCourant().getIdCap());
			ArrayList<Representant> listeTempRepre = new ArrayList<Representant>();
			for (int i = 0; i < listeRepreCap.size(); i++) {
				RepresentantCap reprCap = listeRepreCap.get(i);
				Representant rep = getRepresentantDao().chercherRepresentant(reprCap.getIdRepresentant());
				listeTempRepre.add(rep);
			}
			setListeRepresentantCap(listeTempRepre);

			// on affiche la liste des corps CAP
			ArrayList<CorpsCap> listeCorpsCap = getCorpsCapDao().listerCorpsCapParCap(getCapCourant().getIdCap());
			ArrayList<GradeGenerique> listeTempCorps = new ArrayList<GradeGenerique>();
			for (int i = 0; i < listeCorpsCap.size(); i++) {
				CorpsCap corpsCap = listeCorpsCap.get(i);
				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), corpsCap.getCodeSpgeng());
				listeTempCorps.add(gg);
			}
			setListeCorpsCap(listeTempCorps);

			addZone(getNOM_ST_ACTION_CAP(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "cap"));
		}

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_CAP Date de cr�ation
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_CAP() {
		return "NOM_PB_VALIDER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_CAP(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_CAP() != null && getVAL_ST_ACTION_CAP() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_CAP().equals(ACTION_CREATION) || getVAL_ST_ACTION_CAP().equals(ACTION_MODIFICATION)) {

				// on alimente la liste des corps
				setListeCorpsCap(null);
				ArrayList<GradeGenerique> listeCorps = new ArrayList<GradeGenerique>();
				for (int i = 0; i < getListeCorps().size(); i++) {
					// on recup�re la ligne concern�e
					GradeGenerique gg = (GradeGenerique) getListeCorps().get(i);
					if (getVAL_CK_SELECT_LIGNE_CORPS(i).equals(getCHECKED_ON())) {
						listeCorps.add(gg);
					}
				}
				setListeCorpsCap(listeCorps);

				if (!performControlerSaisieCap(request))
					return false;

				if (!performControlerRegleGestionCap(request))
					return false;

				if (getVAL_ST_ACTION_CAP().equals(ACTION_CREATION)) {
					setCapCourant(new Cap());

				} else {
					// modification
				}

				// on recupere le type de d�liberation
				int indicetypeCap = (Services.estNumerique(getVAL_LB_TYPE_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_CAP_SELECT()) : -1);
				String typeCap = Const.CHAINE_VIDE;
				if (indicetypeCap > -1) {
					typeCap = getListeTypeCap().get(indicetypeCap);
				}

				getCapCourant().setCodeCap(getVAL_EF_CODE_CAP());
				getCapCourant().setRefCap(getVAL_EF_REF_CAP());
				getCapCourant().setDescription(getVAL_EF_DESCRIPTION_CAP());
				getCapCourant().setTypeCap(typeCap);
				Cap capAjoute = null;
				if (getVAL_ST_ACTION_CAP().equals(ACTION_CREATION)) {
					getCapDao().creerCap(getCapCourant().getCodeCap(), getCapCourant().getRefCap(), getCapCourant().getDescription(),
							getCapCourant().getTypeCap());
					capAjoute = getCapDao().chercherCap(getCapCourant().getCodeCap(), getCapCourant().getRefCap());
				} else {
					// modification
					capAjoute = getCapCourant();
					getCapDao().modifierCap(getCapCourant().getIdCap(), getCapCourant().getCodeCap(), getCapCourant().getRefCap(),
							getCapCourant().getDescription(), getCapCourant().getTypeCap());

					// on supprime les corps li�s
					getCorpsCapDao().supprimerCorpsCapParCap(getCapCourant().getIdCap());

					// on supprime les employeurs li�s
					getEmployeurCapDao().supprimerEmployeurCapParCap(getCapCourant().getIdCap());

					// on supprime les employeurs li�s
					getRepresentantCapDao().supprimerRepresentantCapParCap(getCapCourant().getIdCap());
				}

				// on ajoute les corps CAP
				for (int i = 0; i < getListeCorpsCap().size(); i++) {
					getCorpsCapDao().creerCorpsCap(getListeCorpsCap().get(i).getCdgeng(), capAjoute.getIdCap());
				}
				setListeCorpsCap(null);

				// on ajoute les employeurs CAP
				for (int i = 0; i < getListeEmployeurCap().size(); i++) {
					getEmployeurCapDao().creerEmployeurCap(getListeEmployeurCap().get(i).getIdEmployeur(), capAjoute.getIdCap(), i);
				}
				setListeEmployeurCap(null);

				// on ajoute les repr�sentants CAP
				for (int i = 0; i < getListeRepresentantCap().size(); i++) {
					getRepresentantCapDao().creerRepresentantCap(getListeRepresentantCap().get(i).getIdRepresentant(), capAjoute.getIdCap(), i);
				}
				setListeRepresentantCap(null);

			} else if (getVAL_ST_ACTION_CAP().equals(ACTION_SUPPRESSION)) {
				// on supprime les corps li�s
				getCorpsCapDao().supprimerCorpsCapParCap(getCapCourant().getIdCap());

				// on supprime les employeurs li�s
				getEmployeurCapDao().supprimerEmployeurCapParCap(getCapCourant().getIdCap());

				// on supprime les employeurs li�s
				getRepresentantCapDao().supprimerRepresentantCapParCap(getCapCourant().getIdCap());

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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_CAP() {
		return "NOM_EF_CODE_CAP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_CODE_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_CAP() {
		return getZone(getNOM_EF_CODE_CAP());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_CAP Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_REF_CAP() {
		return "NOM_EF_REF_CAP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_REF_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_REF_CAP() {
		return getZone(getNOM_EF_REF_CAP());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DESCRIPTION_CAP
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_DESCRIPTION_CAP() {
		return "NOM_EF_DESCRIPTION_CAP";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DESCRIPTION_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_DESCRIPTION_CAP() {
		return getZone(getNOM_EF_DESCRIPTION_CAP());
	}

	/**
	 * Contr�le les zones saisies d'une cap Date de cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieCap(HttpServletRequest request) throws Exception {
		// Verification reference cap not null
		if (getZone(getNOM_EF_REF_CAP()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "r�f�rence"));
			setFocus(getDefaultFocus());
			return false;
		}
		// Verification code cap not null
		if (getZone(getNOM_EF_CODE_CAP()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			setFocus(getDefaultFocus());
			return false;
		}
		// Verification description not null
		if (getZone(getNOM_EF_DESCRIPTION_CAP()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			setFocus(getDefaultFocus());
			return false;
		}

		// **********************************
		// Verification Employeur
		// **********************************
		if (getListeEmployeurCap() == null || getListeEmployeurCap().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Employeurs"));
			setFocus(getDefaultFocus());
			return false;
		}

		// **********************************
		// Verification Representant
		// **********************************
		if (getListeRepresentantCap() == null || getListeRepresentantCap().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Repr�sentants"));
			setFocus(getDefaultFocus());
			return false;
		}

		// **********************************
		// Verification Corps
		// **********************************
		if (getListeCorpsCap() == null || getListeCorpsCap().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Corps"));
			setFocus(getDefaultFocus());
			return false;
		}
		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'une cap Date de cr�ation : (14/09/11)
	 */
	private boolean performControlerRegleGestionCap(HttpServletRequest request) throws Exception {

		// V�rification des contraintes d'unicit� de la cap
		if (getVAL_ST_ACTION_CAP().equals(ACTION_CREATION)) {

			for (Cap cap : getListeCap()) {
				if (cap.getCodeCap().equals(getVAL_EF_CODE_CAP().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une CAP", "ce code"));
					setFocus(getDefaultFocus());
					return false;
				}
			}
		}

		// V�rification des contraintes d'unicit� de la cap
		if (getVAL_ST_ACTION_CAP().equals(ACTION_MODIFICATION)) {
			for (Cap cap : getListeCap()) {
				if (cap.getCodeCap().equals(getVAL_EF_CODE_CAP().toUpperCase()) && (!cap.equals(getCapCourant()))) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une CAP", "ce code"));
					setFocus(getDefaultFocus());
					return false;
				}
			}
		}

		return true;
	}

	public EmployeurCapDao getEmployeurCapDao() {
		return employeurCapDao;
	}

	public void setEmployeurCapDao(EmployeurCapDao employeurCapDao) {
		this.employeurCapDao = employeurCapDao;
	}

	public ArrayList<Employeur> getListeEmployeurCap() {
		return listeEmployeurCap == null ? new ArrayList<Employeur>() : listeEmployeurCap;
	}

	public void setListeEmployeurCap(ArrayList<Employeur> listeEmployeurCap) {
		this.listeEmployeurCap = listeEmployeurCap;
	}

	public RepresentantCapDao getRepresentantCapDao() {
		return representantCapDao;
	}

	public void setRepresentantCapDao(RepresentantCapDao representantCapDao) {
		this.representantCapDao = representantCapDao;
	}

	public ArrayList<Representant> getListeRepresentantCap() {
		return listeRepresentantCap == null ? new ArrayList<Representant>() : listeRepresentantCap;
	}

	public void setListeRepresentantCap(ArrayList<Representant> listeRepresentantCap) {
		this.listeRepresentantCap = listeRepresentantCap;
	}

	public ArrayList<GradeGenerique> getListeCorpsCap() {
		return listeCorpsCap == null ? new ArrayList<GradeGenerique>() : listeCorpsCap;
	}

	public void setListeCorpsCap(ArrayList<GradeGenerique> listeCorpsCap) {
		this.listeCorpsCap = listeCorpsCap;
	}

	public ArrayList<GradeGenerique> getListeCorps() {
		return listeCorps == null ? new ArrayList<GradeGenerique>() : listeCorps;
	}

	public void setListeCorps(ArrayList<GradeGenerique> listeCorps) {
		this.listeCorps = listeCorps;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CORPS Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_CORPS() {
		if (LB_CORPS == null)
			LB_CORPS = initialiseLazyLB();
		return LB_CORPS;
	}

	/**
	 * Setter de la liste: LB_CORPS Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_CORPS(String[] newLB_CORPS) {
		LB_CORPS = newLB_CORPS;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CORPS Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_CORPS() {
		return "NOM_LB_CORPS";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_CORPS_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_CORPS_SELECT() {
		return "NOM_LB_CORPS_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_CORPS Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_CORPS() {
		return getLB_CORPS();
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_SELECT_LIGNE_CORPS Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_CORPS(int i) {
		return "NOM_CK_SELECT_LIGNE_CORPS_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_SELECT_LIGNE_CORPS Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_CORPS(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_CORPS(i));
	}

	public CorpsCapDao getCorpsCapDao() {
		return corpsCapDao;
	}

	public void setCorpsCapDao(CorpsCapDao corpsCapDao) {
		this.corpsCapDao = corpsCapDao;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_CAP Date de
	 * cr�ation : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_CAP() {
		return "NOM_PB_MODIFIER_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_CAP(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_CAP_SELECT()) : -1);

		if (indice != -1 && indice < getListeCap().size()) {
			Cap cap = getListeCap().get(indice);
			setCapCourant(cap);
			addZone(getNOM_EF_CODE_CAP(), cap.getCodeCap());
			addZone(getNOM_EF_REF_CAP(), cap.getRefCap());
			addZone(getNOM_EF_DESCRIPTION_CAP(), cap.getDescription());

			int ligneTypeCap = getListeTypeCap().indexOf(cap.getTypeCap());
			addZone(getNOM_LB_TYPE_CAP_SELECT(), String.valueOf(ligneTypeCap));

			// on affiche la liste des employeurs CAP
			ArrayList<EmployeurCap> listeEmpCap = getEmployeurCapDao().listerEmployeurCapParCap(getCapCourant().getIdCap());
			ArrayList<Employeur> listeTempEmp = new ArrayList<Employeur>();
			for (int i = 0; i < listeEmpCap.size(); i++) {
				EmployeurCap empCap = listeEmpCap.get(i);
				Employeur emp = getEmployeurDao().chercherEmployeur(empCap.getIdEmployeur());
				listeTempEmp.add(emp);
			}
			setListeEmployeurCap(listeTempEmp);
			// Afiichage des employeurs deja pr�sent
			if (getListeEmployeurCap().size() != 0) {
				int tailles[] = { 70 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator list = getListeEmployeurCap().listIterator(); list.hasNext();) {
					Employeur emp = (Employeur) list.next();
					String ligne[] = { emp.getLibEmployeur() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_EMP_CAP_MULTI(aFormat.getListeFormatee());
			} else {
				setLB_EMP_CAP_MULTI(null);
			}

			// on affiche la liste des repr�sentant CAP
			ArrayList<RepresentantCap> listeRepreCap = getRepresentantCapDao().listerRepresentantCapParCap(getCapCourant().getIdCap());
			ArrayList<Representant> listeTempRepre = new ArrayList<Representant>();
			for (int i = 0; i < listeRepreCap.size(); i++) {
				RepresentantCap reprCap = listeRepreCap.get(i);
				Representant rep = getRepresentantDao().chercherRepresentant(reprCap.getIdRepresentant());
				listeTempRepre.add(rep);
			}
			setListeRepresentantCap(listeTempRepre);
			// Afiichage des repr�sentants deja pr�sent
			if (getListeRepresentantCap().size() != 0) {
				int tailles[] = { 70 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator list = getListeRepresentantCap().listIterator(); list.hasNext();) {
					Representant repre = (Representant) list.next();
					TypeRepresentant typeRepre = getTypeRepresentantDao().chercherTypeRepresentant(repre.getIdTypeRepresentant());
					String ligne[] = { repre.getNomRepresentant() + " " + repre.getPrenomRepresentant() + "( " + typeRepre.getLibTypeRepresentant()
							+ " )" };

					aFormat.ajouteLigne(ligne);
				}
				setLB_REPRE_CAP_MULTI(aFormat.getListeFormatee());
			} else {
				setLB_REPRE_CAP_MULTI(null);
			}

			// on affiche la liste des corps CAP
			ArrayList<CorpsCap> listeCorpsCap = getCorpsCapDao().listerCorpsCapParCap(getCapCourant().getIdCap());
			ArrayList<GradeGenerique> listeTempCorps = new ArrayList<GradeGenerique>();
			for (int i = 0; i < listeCorpsCap.size(); i++) {
				CorpsCap corpsCap = listeCorpsCap.get(i);
				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), corpsCap.getCodeSpgeng());
				listeTempCorps.add(gg);
			}
			setListeCorpsCap(listeTempCorps);
			// Afiichage des corps deja pr�sent
			for (int i = 0; i < getListeCorps().size(); i++) {
				GradeGenerique gg = getListeCorps().get(i);
				addZone(getNOM_CK_SELECT_LIGNE_CORPS(i), getCHECKED_OFF());
				for (int j = 0; j < getListeCorpsCap().size(); j++) {
					GradeGenerique ggCap = getListeCorpsCap().get(j);
					if (gg.getCdgeng().toString().equals(ggCap.getCdgeng().toString())) {
						addZone(getNOM_CK_SELECT_LIGNE_CORPS(i), getCHECKED_ON());
						break;
					}
				}
			}

			addZone(getNOM_ST_ACTION_CAP(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "CAP"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_CAP Date de cr�ation
	 * : (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_TYPE_CAP() {
		if (LB_TYPE_CAP == null)
			LB_TYPE_CAP = initialiseLazyLB();
		return LB_TYPE_CAP;
	}

	/**
	 * Setter de la liste: LB_TYPE_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_TYPE_CAP(String[] newLB_TYPE_CAP) {
		LB_TYPE_CAP = newLB_TYPE_CAP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_CAP Date de cr�ation
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_CAP() {
		return "NOM_LB_TYPE_CAP";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_CAP_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_TYPE_CAP_SELECT() {
		return "NOM_LB_TYPE_CAP_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_TYPE_CAP() {
		return getLB_TYPE_CAP();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_CAP Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_TYPE_CAP_SELECT() {
		return getZone(getNOM_LB_TYPE_CAP_SELECT());
	}

	public ArrayList<String> getListeTypeCap() {
		return listeTypeCap == null ? new ArrayList<String>() : listeTypeCap;
	}

	public void setListeTypeCap(ArrayList<String> listeTypeCap) {
		this.listeTypeCap = listeTypeCap;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REPRESENTANT_CAP
	 * Date de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REPRESENTANT_CAP() {
		return "NOM_PB_SUPPRIMER_REPRESENTANT_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_REPRESENTANT_CAP(HttpServletRequest request) throws Exception {

		// Suppression du dernier representant de la liste

		if (getListeRepresentantCap() != null && getListeRepresentantCap().size() != 0) {
			Representant niv = (Representant) getListeRepresentantCap().get(getListeRepresentantCap().size() - 1);
			getListeRepresentantCap().remove(niv);

			if (getListeRepresentantCap().size() != 0) {
				int tailles[] = { 70 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator list = getListeRepresentantCap().listIterator(); list.hasNext();) {
					Representant repre = (Representant) list.next();
					TypeRepresentant typeRepre = getTypeRepresentantDao().chercherTypeRepresentant(repre.getIdTypeRepresentant());
					String ligne[] = { repre.getNomRepresentant() + " " + repre.getPrenomRepresentant() + "( " + typeRepre.getLibTypeRepresentant()
							+ " )" };

					aFormat.ajouteLigne(ligne);
				}
				setLB_REPRE_CAP_MULTI(aFormat.getListeFormatee());
			} else {
				setLB_REPRE_CAP_MULTI(null);
			}

		}

		setFocus(getDefaultFocus());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_REPRESENTANT_CAP
	 * Date de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_REPRESENTANT_CAP() {
		return "NOM_PB_AJOUTER_REPRESENTANT_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_REPRESENTANT_CAP(HttpServletRequest request) throws Exception {
		// R�cup�ration du niveau d'�tude � ajouter

		int indiceRepr = (Services.estNumerique(getVAL_LB_REPRE_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_REPRE_CAP_SELECT()) : -1);

		if (indiceRepr > 0) {
			Representant n = (Representant) getListeRepresentant().get(indiceRepr - 1);

			if (n != null) {
				ArrayList<Representant> existant = getListeRepresentantCap();
				boolean exist = false;
				for (int i = 0; i < existant.size(); i++) {
					Representant reprExist = existant.get(i);
					if (reprExist.getIdRepresentant().equals(n.getIdRepresentant())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					existant.add(n);
				}
				setListeRepresentantCap(existant);
			}
		}
		if (getListeRepresentantCap().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeRepresentantCap().listIterator(); list.hasNext();) {
				Representant repre = (Representant) list.next();
				TypeRepresentant typeRepre = getTypeRepresentantDao().chercherTypeRepresentant(repre.getIdTypeRepresentant());
				String ligne[] = { repre.getNomRepresentant() + " " + repre.getPrenomRepresentant() + "( " + typeRepre.getLibTypeRepresentant()
						+ " )" };

				aFormat.ajouteLigne(ligne);
			}
			setLB_REPRE_CAP_MULTI(aFormat.getListeFormatee());
		} else {
			setLB_REPRE_CAP_MULTI(null);
		}

		addZone(getNOM_LB_REPRE_CAP_SELECT(), Const.ZERO);
		setFocus(getDefaultFocus());
		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REPRE_CAP Date de
	 * cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_REPRE_CAP() {
		if (LB_REPRE_CAP == null)
			LB_REPRE_CAP = initialiseLazyLB();
		return LB_REPRE_CAP;
	}

	/**
	 * Setter de la liste: LB_REPRE_CAP Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private void setLB_REPRE_CAP(String[] newLB_REPRE_CAP) {
		LB_REPRE_CAP = newLB_REPRE_CAP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REPRE_CAP Date de
	 * cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_REPRE_CAP() {
		return "NOM_LB_REPRE_CAP";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_REPRE_CAP_SELECT Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_REPRE_CAP_SELECT() {
		return "NOM_LB_REPRE_CAP_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_REPRE_CAP Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_REPRE_CAP() {
		return getLB_REPRE_CAP();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_REPRE_CAP Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_REPRE_CAP_SELECT() {
		return getZone(getNOM_LB_REPRE_CAP_SELECT());
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_REPRE_CAP_MULTI Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_REPRE_CAP_MULTI_SELECT() {
		return getZone(getNOM_LB_REPRE_CAP_MULTI_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_REPRE_CAP_MULTI_SELECT Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_REPRE_CAP_MULTI_SELECT() {
		return "NOM_LB_REPRE_CAP_MULTI_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REPRE_CAP_MULTI Date de
	 * cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_REPRE_CAP_MULTI() {
		return "NOM_LB_REPRE_CAP_MULTI";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_REPRE_CAP_MULTI Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_REPRE_CAP_MULTI() {
		return getLB_REPRE_CAP_MULTI();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REPRE_CAP_MULTI Date de
	 * cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_REPRE_CAP_MULTI() {
		if (LB_REPRE_CAP_MULTI == null)
			LB_REPRE_CAP_MULTI = initialiseLazyLB();
		return LB_REPRE_CAP_MULTI;
	}

	/**
	 * Setter de la liste: LB_REPRE_CAP_MULTI Date de cr�ation : (08/07/11
	 * 09:13:07)
	 * 
	 * 
	 */
	private void setLB_REPRE_CAP_MULTI(String[] newLB_REPRE_CAP_MULTI) {
		LB_REPRE_CAP_MULTI = newLB_REPRE_CAP_MULTI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_EMP_CAP Date de cr�ation
	 * : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_EMP_CAP() {
		return "NOM_LB_EMP_CAP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_EMPLOYEUR_CAP Date
	 * de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_EMPLOYEUR_CAP() {
		return "NOM_PB_SUPPRIMER_EMPLOYEUR_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_EMPLOYEUR_CAP(HttpServletRequest request) throws Exception {

		// Suppression du dernier employeur de la liste

		if (getListeEmployeurCap() != null && getListeEmployeurCap().size() != 0) {
			Employeur niv = (Employeur) getListeEmployeurCap().get(getListeEmployeurCap().size() - 1);
			getListeEmployeurCap().remove(niv);

			if (getListeEmployeurCap().size() != 0) {
				int tailles[] = { 70 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator list = getListeEmployeurCap().listIterator(); list.hasNext();) {
					Employeur emp = (Employeur) list.next();
					String ligne[] = { emp.getLibEmployeur() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_EMP_CAP_MULTI(aFormat.getListeFormatee());
			} else {
				setLB_EMP_CAP_MULTI(null);
			}

		}

		setFocus(getDefaultFocus());
		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_EMP_CAP Date de cr�ation
	 * : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_EMP_CAP() {
		if (LB_EMP_CAP == null)
			LB_EMP_CAP = initialiseLazyLB();
		return LB_EMP_CAP;
	}

	/**
	 * Setter de la liste: LB_EMP_CAP Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private void setLB_EMP_CAP(String[] newLB_EMP_CAP) {
		LB_EMP_CAP = newLB_EMP_CAP;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_EMPLOYEUR_CAP Date
	 * de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_EMPLOYEUR_CAP() {
		return "NOM_PB_AJOUTER_EMPLOYEUR_CAP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_EMPLOYEUR_CAP(HttpServletRequest request) throws Exception {
		// R�cup�ration de l'employeur � ajouter

		int indiceEmp = (Services.estNumerique(getVAL_LB_EMP_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_EMP_CAP_SELECT()) : -1);

		if (indiceEmp > 0) {
			Employeur n = (Employeur) getListeEmployeur().get(indiceEmp - 1);

			if (n != null) {
				ArrayList<Employeur> existant = getListeEmployeurCap();
				boolean exist = false;
				for (int i = 0; i < existant.size(); i++) {
					Employeur empExist = existant.get(i);
					if (empExist.getIdEmployeur().equals(n.getIdEmployeur())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					existant.add(n);
				}
				setListeEmployeurCap(existant);
			}
		}
		if (getListeEmployeurCap().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeEmployeurCap().listIterator(); list.hasNext();) {
				Employeur emp = (Employeur) list.next();
				String ligne[] = { emp.getLibEmployeur() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_EMP_CAP_MULTI(aFormat.getListeFormatee());
		} else {
			setLB_EMP_CAP_MULTI(null);
		}

		addZone(getNOM_LB_EMP_CAP_SELECT(), Const.ZERO);
		setFocus(getDefaultFocus());
		return true;
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_EMP_CAP Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_EMP_CAP_SELECT() {
		return getZone(getNOM_LB_EMP_CAP_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_EMP_CAP_SELECT Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_EMP_CAP_SELECT() {
		return "NOM_LB_EMP_CAP_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_EMP_CAP Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_EMP_CAP() {
		return getLB_EMP_CAP();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_EMP_CAP_MULTI Date de
	 * cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_EMP_CAP_MULTI() {
		if (LB_EMP_CAP_MULTI == null)
			LB_EMP_CAP_MULTI = initialiseLazyLB();
		return LB_EMP_CAP_MULTI;
	}

	/**
	 * Setter de la liste: LB_EMP_CAP_MULTI Date de cr�ation : (08/07/11
	 * 09:13:07)
	 * 
	 * 
	 */
	private void setLB_EMP_CAP_MULTI(String[] newLB_EMP_CAP_MULTI) {
		LB_EMP_CAP_MULTI = newLB_EMP_CAP_MULTI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_EMP_CAP_MULTI Date de
	 * cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_EMP_CAP_MULTI() {
		return "NOM_LB_EMP_CAP_MULTI";
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_EMP_CAP_MULTI Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_EMP_CAP_MULTI_SELECT() {
		return getZone(getNOM_LB_EMP_CAP_MULTI_SELECT());
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_EMP_CAP_MULTI_SELECT Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_EMP_CAP_MULTI_SELECT() {
		return "NOM_LB_EMP_CAP_MULTI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_EMP_CAP_MULTI Date de cr�ation : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_EMP_CAP_MULTI() {
		return getLB_EMP_CAP_MULTI();
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
		return getNOM_PB_ANNULER_CAP();
	}

	/**
	 * @param focus
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}
}
