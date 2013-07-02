package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Ecole;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.NFA;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OePARAMETRAGEFichePoste Date de cr�ation : (13/09/11 15:49:10)
 * 
 */
public class OePARAMETRAGEFichePoste extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_ENTITE_GEO;
	private String[] LB_ENTITE_ECOLE;
	private String[] LB_NATURE_AVANTAGE;
	private String[] LB_TITRE;
	private String[] LB_TYPE_AVANTAGE;
	private String[] LB_TYPE_DELEGATION;
	private String[] LB_TYPE_REGIME;
	private String[] LB_ECOLE;

	private ArrayList<EntiteGeo> listeEntite;
	private EntiteGeo entiteGeoCourante;

	private ArrayList<Ecole> listeEntiteEcole;
	private Hashtable<String, Ecole> hashEntiteEcole;

	private ArrayList<TitrePoste> listeTitrePoste;
	private TitrePoste titrePosteCourante;

	private ArrayList<TypeAvantage> listeTypeAvantage;
	private TypeAvantage typeAvantageCourant;

	private ArrayList<NatureAvantage> listeNatureAvantage;
	private NatureAvantage natureAvantageCourant;

	private ArrayList<TypeDelegation> listeTypeDelegation;
	private TypeDelegation typeDelegationCourant;

	private ArrayList<TypeRegIndemn> listeTypeRegime;
	private TypeRegIndemn typeRegimeCourant;

	private ArrayList<NFA> listeNFA;
	private NFA NFACourant;

	private ArrayList<Ecole> listeEcole;
	private Ecole EcoleCourante;

	public String ACTION_SUPPRESSION = "0";
	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (13/09/11 15:49:10)
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

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		initialiseListes(request);

		// Si hashtable des �coles vide
		if (getHashEntiteEcole().size() == 0) {
			ArrayList<Ecole> a = Ecole.listerEcole(getTransaction());
			setListeEntiteEcole(a);
			initialiseListeEntiteGeoEcole(request);
			// remplissage de la hashTable
			for (int i = 0; i < a.size(); i++) {
				Ecole aEcole = (Ecole) a.get(i);
				getHashEntiteEcole().put(aEcole.getCdecol(), aEcole);
			}
		}
	}

	/**
	 * Retourne les ecoles dans une table de hashage Date de cr�ation :
	 * (11/06/2003 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, Ecole> getHashEntiteEcole() {
		if (hashEntiteEcole == null) {
			hashEntiteEcole = new Hashtable<String, Ecole>();
		}
		return hashEntiteEcole;
	}

	/**
	 * Initialisation de la listes des entit�s g�ographiques Date de cr�ation :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeEntiteGeo(HttpServletRequest request) throws Exception {
		setListeEntite(EntiteGeo.listerEntiteGeo(getTransaction()));
		if (getListeEntite().size() != 0) {
			int tailles[] = { 5, 70 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EntiteGeo> list = getListeEntite().listIterator(); list.hasNext();) {
				EntiteGeo eg = (EntiteGeo) list.next();
				String ligne[] = { eg.getCdEcol(), eg.getLibEntiteGeo() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ENTITE_GEO(aFormat.getListeFormatee());
		} else {
			setLB_ENTITE_GEO(null);
		}
	}

	/**
	 * Initialisation de la listes des ecoles pour les entit�s g�ographiques
	 * Date de cr�ation : (14/09/11)
	 */
	private void initialiseListeEntiteGeoEcole(HttpServletRequest request) throws Exception {
		if (getListeEntiteEcole().size() != 0) {
			int tailles[] = { 6, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Ecole> list = getListeEntiteEcole().listIterator(); list.hasNext();) {
				Ecole ecole = (Ecole) list.next();
				String ligne[] = { ecole.getCdecol(), ecole.getLiecol() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ENTITE_GEO_ECOLE(aFormat.getListeFormatee(true));
		} else {
			setLB_ENTITE_GEO_ECOLE(null);
		}
	}

	/**
	 * Initialisation dede la liste des titres de poste Date de cr�ation :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTitre(HttpServletRequest request) throws Exception {
		setListeTitrePoste(TitrePoste.listerTitrePoste(getTransaction()));
		if (getListeTitrePoste().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TitrePoste> list = getListeTitrePoste().listIterator(); list.hasNext();) {
				TitrePoste tp = (TitrePoste) list.next();
				String ligne[] = { tp.getLibTitrePoste() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TITRE(aFormat.getListeFormatee());
		} else {
			setLB_TITRE(null);
		}
	}

	/**
	 * Initialisation dede la liste des types d'avantage en nature Date de
	 * cr�ation : (14/09/11)
	 * 
	 */
	private void initialiseListeTypeAvantage(HttpServletRequest request) throws Exception {
		setListeTypeAvantage(TypeAvantage.listerTypeAvantage(getTransaction()));
		if (getListeTypeAvantage().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeAvantage> list = getListeTypeAvantage().listIterator(); list.hasNext();) {
				TypeAvantage ta = (TypeAvantage) list.next();
				String ligne[] = { ta.getLibTypeAvantage() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_AVANTAGE(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_AVANTAGE(null);
		}
	}

	/**
	 * Initialisation dede la liste des natures d'avantage en nature Date de
	 * cr�ation : (14/09/11)
	 * 
	 */
	private void initialiseListeNatureAvantage(HttpServletRequest request) throws Exception {
		setListeNatureAvantage(NatureAvantage.listerNatureAvantage(getTransaction()));
		if (getListeNatureAvantage().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<NatureAvantage> list = getListeNatureAvantage().listIterator(); list.hasNext();) {
				NatureAvantage na = (NatureAvantage) list.next();
				String ligne[] = { na.getLibNatureAvantage() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_NATURE_AVANTAGE(aFormat.getListeFormatee());
		} else {
			setLB_NATURE_AVANTAGE(null);
		}
	}

	/**
	 * Initialisation dede la liste des types de d�l�gation Date de cr�ation :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeTypeDelegation(HttpServletRequest request) throws Exception {
		setListeTypeDelegation(TypeDelegation.listerTypeDelegation(getTransaction()));
		if (getListeTypeDelegation().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeDelegation> list = getListeTypeDelegation().listIterator(); list.hasNext();) {
				TypeDelegation td = (TypeDelegation) list.next();
				String ligne[] = { td.getLibTypeDelegation() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DELEGATION(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_DELEGATION(null);
		}
	}

	/**
	 * Initialisation dede la liste des types de r�gime indemnitaire Date de
	 * cr�ation : (14/09/11)
	 * 
	 */
	private void initialiseListeTypeRegime(HttpServletRequest request) throws Exception {
		setListeTypeRegime(TypeRegIndemn.listerTypeRegIndemn(getTransaction()));
		if (getListeTypeRegime().size() != 0) {
			int tailles[] = { 70 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeRegIndemn> list = getListeTypeRegime().listIterator(); list.hasNext();) {
				TypeRegIndemn tr = (TypeRegIndemn) list.next();
				String ligne[] = { tr.getLibTypeRegIndemn() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_REGIME(aFormat.getListeFormatee());
		} else {
			setLB_TYPE_REGIME(null);
		}
	}

	/**
	 * Initialisation dede la liste des NFAs Date de cr�ation : (04/11/11)
	 * 
	 */
	private void initialiseListeNFA(HttpServletRequest request) throws Exception {
		setListeNFA(NFA.listerNFA(getTransaction()));
		if (getListeNFA().size() != 0) {
			int tailles[] = { 6, 5 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<NFA> list = getListeNFA().listIterator(); list.hasNext();) {
				NFA nfa = (NFA) list.next();
				String ligne[] = { nfa.getCodeService(), nfa.getNFA() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_NFA(aFormat.getListeFormatee());
		} else {
			setLB_NFA(null);
		}
	}

	/**
	 * Initialisation dede la liste des ecoles Date de cr�ation : (04/11/11)
	 */
	private void initialiseListeEcole(HttpServletRequest request) throws Exception {
		setListeEcole(Ecole.listerEcole(getTransaction()));
		if (getListeEcole().size() != 0) {
			int tailles[] = { 6, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Ecole> list = getListeEcole().listIterator(); list.hasNext();) {
				Ecole ecole = (Ecole) list.next();
				String ligne[] = { ecole.getCdecol(), ecole.getLiecol() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ECOLE(aFormat.getListeFormatee());
		} else {
			setLB_ECOLE(null);
		}
	}

	/**
	 * Initialisation des listes de param�tres Date de cr�ation : (13/09/11)
	 * 
	 */
	private void initialiseListes(HttpServletRequest request) throws Exception {

		if (getListeEntite() == null) {
			// Recherche des entit�s g�ographiques
			setListeEntite(EntiteGeo.listerEntiteGeo(getTransaction()));
			initialiseListeEntiteGeo(request);
		}

		if (getListeTitrePoste() == null) {
			// Recherche des titres de poste
			setListeTitrePoste(TitrePoste.listerTitrePoste(getTransaction()));
			initialiseListeTitre(request);
		}

		if (getListeTypeAvantage() == null) {
			// Recherche des types d'avantage en nature
			setListeTypeAvantage(TypeAvantage.listerTypeAvantage(getTransaction()));
			initialiseListeTypeAvantage(request);
		}

		if (getListeNatureAvantage() == null) {
			// Recherche des natures d'avantage en nature
			setListeNatureAvantage(NatureAvantage.listerNatureAvantage(getTransaction()));
			initialiseListeNatureAvantage(request);
		}

		if (getListeTypeDelegation() == null) {
			// Recherche des types de d�l�gation
			setListeTypeDelegation(TypeDelegation.listerTypeDelegation(getTransaction()));
			initialiseListeTypeDelegation(request);
		}

		if (getListeTypeRegime() == null) {
			// Recherche des types de r�gime indemnitaires
			setListeTypeRegime(TypeRegIndemn.listerTypeRegIndemn(getTransaction()));
			initialiseListeTypeRegime(request);
		}

		if (getListeNFA() == null) {
			// Recherche des NFAs
			setListeNFA(NFA.listerNFA(getTransaction()));
			initialiseListeNFA(request);
		}

		if (getListeEcole() == null) {
			// Recherche des Ecoles
			setListeEcole(Ecole.listerEcole(getTransaction()));
			initialiseListeEcole(request);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEFichePoste. Date de cr�ation :
	 * (13/09/11 15:49:10)
	 * 
	 */
	public OePARAMETRAGEFichePoste() {
		super();
	}

	/**
	 * Retourne le nom de l'�cran (notamment pour d�terminer les droits
	 * associ�s).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-PE-FICHEPOSTE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_ENTITE_GEO() {
		return "NOM_PB_ANNULER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_ENTITE_GEO(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ENTITE_GEO(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_NATURE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_NATURE_AVANTAGE() {
		return "NOM_PB_ANNULER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TITRE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TITRE() {
		return "NOM_PB_ANNULER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TITRE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TITRE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_AVANTAGE() {
		return "NOM_PB_ANNULER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_DELEGATION Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_DELEGATION() {
		return "NOM_PB_ANNULER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_TYPE_REGIME Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_ANNULER_TYPE_REGIME() {
		return "NOM_PB_ANNULER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_ANNULER_TYPE_REGIME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_TYPE_REGIME(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_ENTITE_GEO() {
		return "NOM_PB_CREER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_ENTITE_GEO(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_ENTITE_GEO(), ACTION_CREATION);
		addZone(getNOM_EF_ENTITE_GEO(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT(), "0");

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_MODIFIER_ENTITE_GEO() {
		return "NOM_PB_MODIFIER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_ENTITE_GEO(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_SELECT()) ? Integer.parseInt(getVAL_LB_ENTITE_GEO_SELECT()) : -1);

		if (indice != -1 && indice < getListeEntite().size()) {
			EntiteGeo entiteGeo = getListeEntite().get(indice);
			setEntiteGeoCourante(entiteGeo);

			Ecole s = (Ecole) getHashEntiteEcole().get(entiteGeo.getCdEcol());
			int ligneEcoleEntitee = getListeEntiteEcole().indexOf(s);
			addZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT(), String.valueOf(ligneEcoleEntitee + 1));

			addZone(getNOM_EF_ENTITE_GEO(), entiteGeo.getLibEntiteGeo());
			addZone(getNOM_ST_ACTION_ENTITE_GEO(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "entit�s g�ographiques"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_NATURE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_NATURE_AVANTAGE() {
		return "NOM_PB_CREER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), ACTION_CREATION);
		addZone(getNOM_EF_NATURE_AVANTAGE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TITRE Date de cr�ation
	 * : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TITRE() {
		return "NOM_PB_CREER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TITRE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TITRE(), ACTION_CREATION);
		addZone(getNOM_EF_TITRE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_AVANTAGE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_AVANTAGE() {
		return "NOM_PB_CREER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_AVANTAGE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_DELEGATION Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_DELEGATION() {
		return "NOM_PB_CREER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_DELEGATION(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_TYPE_REGIME Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_CREER_TYPE_REGIME() {
		return "NOM_PB_CREER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_CREER_TYPE_REGIME(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_TYPE_REGIME(), ACTION_CREATION);
		addZone(getNOM_EF_TYPE_REGIME(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_ENTITE_GEO() {
		return "NOM_PB_SUPPRIMER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_ENTITE_GEO(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_SELECT()) ? Integer.parseInt(getVAL_LB_ENTITE_GEO_SELECT()) : -1);

		if (indice != -1 && indice < getListeEntite().size()) {
			EntiteGeo eg = getListeEntite().get(indice);
			setEntiteGeoCourante(eg);

			Ecole s = (Ecole) getHashEntiteEcole().get(eg.getCdEcol());
			int ligneEcoleEntitee = getListeEntiteEcole().indexOf(s);
			addZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT(), String.valueOf(ligneEcoleEntitee + 1));

			addZone(getNOM_EF_ENTITE_GEO(), eg.getLibEntiteGeo());
			addZone(getNOM_ST_ACTION_ENTITE_GEO(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "entit�s g�ographiques"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NATURE_AVANTAGE
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NATURE_AVANTAGE() {
		return "NOM_PB_SUPPRIMER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()) : -1);

		if (indice != -1 && indice < getListeNatureAvantage().size()) {
			NatureAvantage na = getListeNatureAvantage().get(indice);
			setNatureAvantageCourant(na);
			addZone(getNOM_EF_NATURE_AVANTAGE(), na.getLibNatureAvantage());
			addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "natures d'avantage en nature"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TITRE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TITRE() {
		return "NOM_PB_SUPPRIMER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TITRE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TITRE_SELECT()) ? Integer.parseInt(getVAL_LB_TITRE_SELECT()) : -1);

		if (indice != -1 && indice < getListeTitrePoste().size()) {
			TitrePoste tp = getListeTitrePoste().get(indice);
			setTitrePosteCourante(tp);
			addZone(getNOM_EF_TITRE(), tp.getLibTitrePoste());
			addZone(getNOM_ST_ACTION_TITRE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de poste"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_AVANTAGE() {
		return "NOM_PB_SUPPRIMER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeAvantage().size()) {
			TypeAvantage ta = getListeTypeAvantage().get(indice);
			setTypeAvantageCourant(ta);
			addZone(getNOM_EF_TYPE_AVANTAGE(), ta.getLibTypeAvantage());
			addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types d'avantage en nature"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_DELEGATION
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_DELEGATION() {
		return "NOM_PB_SUPPRIMER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeDelegation().size()) {
			TypeDelegation tg = getListeTypeDelegation().get(indice);
			setTypeDelegationCourant(tg);
			addZone(getNOM_EF_TYPE_DELEGATION(), tg.getLibTypeDelegation());
			addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types de d�l�gation"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_TYPE_REGIME Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_TYPE_REGIME() {
		return "NOM_PB_SUPPRIMER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_SUPPRIMER_TYPE_REGIME(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeRegime().size()) {
			TypeRegIndemn tr = getListeTypeRegime().get(indice);
			setTypeRegimeCourant(tr);
			addZone(getNOM_EF_TYPE_REGIME(), tr.getLibTypeRegIndemn());
			addZone(getNOM_ST_ACTION_TYPE_REGIME(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types de r�gime"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_ENTITE_GEO() {
		return "NOM_PB_VALIDER_ENTITE_GEO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_ENTITE_GEO(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieEntiteGeo(request))
			return false;

		if (!performControlerRegleGestionEntiteGeo(request))
			return false;

		if (getVAL_ST_ACTION_ENTITE_GEO() != null && getVAL_ST_ACTION_ENTITE_GEO() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_CREATION)) {
				// on recupere l'�cole (si elle est saisie !)
				setEntiteGeoCourante(new EntiteGeo());
				int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) ? Integer.parseInt(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) : -1);
				if (indice != 0) {
					String codeEcole = ((Ecole) getListeEntiteEcole().get(indice - 1)).getCdecol();
					getEntiteGeoCourante().setCdEcol(codeEcole);
				} else {
					getEntiteGeoCourante().setCdEcol(Const.ZERO);
				}

				getEntiteGeoCourante().setLibEntiteGeo(getVAL_EF_ENTITE_GEO());
				getEntiteGeoCourante().setRidet(Const.ZERO);
				getEntiteGeoCourante().creerEntiteGeo(getTransaction());

				if (!getTransaction().isErreur())
					getListeEntite().add(getEntiteGeoCourante());
			} else if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_SUPPRESSION)) {
				getEntiteGeoCourante().supprimerEntiteGeo(getTransaction());
				if (!getTransaction().isErreur())
					getListeEntite().remove(getEntiteGeoCourante());
				setEntiteGeoCourante(null);
			} else if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_MODIFICATION)) {
				// on recupere l'�cole (si elle est saisie !)
				int indice = (Services.estNumerique(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) ? Integer.parseInt(getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) : -1);
				if (indice != 0) {
					String codeEcole = ((Ecole) getListeEntiteEcole().get(indice - 1)).getCdecol();
					getEntiteGeoCourante().setCdEcol(codeEcole);

				} else {
					getEntiteGeoCourante().setCdEcol(Const.ZERO);
				}

				getEntiteGeoCourante().setLibEntiteGeo(getVAL_EF_ENTITE_GEO());
				getEntiteGeoCourante().modifierEntiteGeo(getTransaction());

			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeEntiteGeo(request);
			addZone(getNOM_ST_ACTION_ENTITE_GEO(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'une entit� g�ographique Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieEntiteGeo(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_ENTITE_GEO()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'une entit� g�ographique Date de cr�ation
	 * : (09/09/11)
	 */
	private boolean performControlerRegleGestionEntiteGeo(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une entit� g�ographique utilis�e sur
		// une fiche de poste
		if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_SUPPRESSION)
				&& FichePoste.listerFichePosteAvecEntiteGeo(getTransaction(), getEntiteGeoCourante()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche de poste", "cette entit� g�ographique"));
			return false;
		}

		// V�rification des contraintes d'unicit� de l'entit� g�ographique
		if (getVAL_ST_ACTION_ENTITE_GEO().equals(ACTION_CREATION)) {

			for (EntiteGeo entite : getListeEntite()) {
				if (entite.getLibEntiteGeo().equals(getVAL_EF_ENTITE_GEO().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une entit� g�ographique", "ce libell�"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_NATURE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_NATURE_AVANTAGE() {
		return "NOM_PB_VALIDER_NATURE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_NATURE_AVANTAGE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieNatureAvantage(request))
			return false;

		if (!performControlerRegleGestionNatureAvantage(request))
			return false;

		if (getVAL_ST_ACTION_NATURE_AVANTAGE() != null && getVAL_ST_ACTION_NATURE_AVANTAGE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_CREATION)) {
				setNatureAvantageCourant(new NatureAvantage());
				getNatureAvantageCourant().setLibNatureAvantage(getVAL_EF_NATURE_AVANTAGE());
				getNatureAvantageCourant().creerNatureAvantage(getTransaction());
				if (!getTransaction().isErreur())
					getListeNatureAvantage().add(getNatureAvantageCourant());
			} else if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_SUPPRESSION)) {
				getNatureAvantageCourant().supprimerNatureAvantage(getTransaction());
				if (!getTransaction().isErreur())
					getListeNatureAvantage().remove(getNatureAvantageCourant());
				setNatureAvantageCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeNatureAvantage(request);
			addZone(getNOM_ST_ACTION_NATURE_AVANTAGE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'une nature d'avantage en nature Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieNatureAvantage(HttpServletRequest request) throws Exception {

		// Verification lib nature avantage not null
		if (getZone(getNOM_EF_NATURE_AVANTAGE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'une nature d'avantage en nature Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerRegleGestionNatureAvantage(HttpServletRequest request) throws Exception {

		// Verification si suppression d'une nature d'avantage en nature
		// utilis�e sur un avantage en nature
		if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_SUPPRESSION)
				&& AvantageNature.listerAvantageNatureAvecNatureAvantage(getTransaction(), getNatureAvantageCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un avantage en nature", "cette nature d'avantage en nature"));
			return false;
		}

		// V�rification des contraintes d'unicit� de l'entit� g�ographique
		if (getVAL_ST_ACTION_NATURE_AVANTAGE().equals(ACTION_CREATION)) {

			for (NatureAvantage nature : getListeNatureAvantage()) {
				if (nature.getLibNatureAvantage().equals(getVAL_EF_NATURE_AVANTAGE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une nature d'avantage en nature", "ce libell�"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TITRE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TITRE() {
		return "NOM_PB_VALIDER_TITRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TITRE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTitre(request))
			return false;

		if (!performControlerRegleGestionTitre(request))
			return false;

		if (getVAL_ST_ACTION_TITRE() != null && getVAL_ST_ACTION_TITRE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TITRE().equals(ACTION_CREATION)) {
				setTitrePosteCourante(new TitrePoste());
				getTitrePosteCourante().setLibTitrePoste(getVAL_EF_TITRE());
				getTitrePosteCourante().creerTitrePoste(getTransaction());
				if (!getTransaction().isErreur())
					getListeTitrePoste().add(getTitrePosteCourante());
			} else if (getVAL_ST_ACTION_TITRE().equals(ACTION_SUPPRESSION)) {
				getTitrePosteCourante().supprimerTitrePoste(getTransaction());
				if (!getTransaction().isErreur())
					getListeTitrePoste().remove(getTitrePosteCourante());
				setTitrePosteCourante(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTitre(request);
			addZone(getNOM_ST_ACTION_TITRE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'un titre de poste Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTitre(HttpServletRequest request) throws Exception {

		// Verification libell� titre de poste not null
		if (getZone(getNOM_EF_TITRE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un titre de poste Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionTitre(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un titre de poste utilis� sur une fiche
		// de poste
		if (getVAL_ST_ACTION_TITRE().equals(ACTION_SUPPRESSION)
				&& FichePoste.listerFichePosteAvecTitrePoste(getTransaction(), getTitrePosteCourante()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une fiche de poste", "ce titre de poste"));
			return false;
		}

		// V�rification des contraintes d'unicit� du titre de poste
		if (getVAL_ST_ACTION_TITRE().equals(ACTION_CREATION)) {

			for (TitrePoste titre : getListeTitrePoste()) {
				if (titre.getLibTitrePoste().equals(getVAL_EF_TITRE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un titre de poste", "ce libell�"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_AVANTAGE() {
		return "NOM_PB_VALIDER_TYPE_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_AVANTAGE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeAvantage(request))
			return false;

		if (!performControlerRegleGestionTypeAvantage(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_AVANTAGE() != null && getVAL_ST_ACTION_TYPE_AVANTAGE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_CREATION)) {
				setTypeAvantageCourant(new TypeAvantage());
				getTypeAvantageCourant().setLibTypeAvantage(getVAL_EF_TYPE_AVANTAGE());
				getTypeAvantageCourant().creerTypeAvantage(getTransaction());
				if (!getTransaction().isErreur())
					getListeTypeAvantage().add(getTypeAvantageCourant());
			} else if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_SUPPRESSION)) {
				getTypeAvantageCourant().supprimerTypeAvantage(getTransaction());
				if (!getTransaction().isErreur())
					getListeTypeAvantage().remove(getTypeAvantageCourant());
				setTypeAvantageCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeAvantage(request);
			addZone(getNOM_ST_ACTION_TYPE_AVANTAGE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'un type d'avantage en nature Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieTypeAvantage(HttpServletRequest request) throws Exception {

		// Verification libell� type d'avantage not null
		if (getZone(getNOM_EF_TYPE_AVANTAGE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un type d'avantage en nature Date de
	 * cr�ation : (09/09/11)
	 */
	private boolean performControlerRegleGestionTypeAvantage(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type d'avantage utilis� sur un
		// avantage en nature
		if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_SUPPRESSION)
				&& AvantageNature.listerAvantageNatureAvecTypeAvantage(getTransaction(), getTypeAvantageCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un avantage en nature", "ce type d'avantage en nature"));
			return false;
		}

		// V�rification des contraintes d'unicit� du type d'avantage en nature
		if (getVAL_ST_ACTION_TYPE_AVANTAGE().equals(ACTION_CREATION)) {

			for (TypeAvantage type : getListeTypeAvantage()) {
				if (type.getLibTypeAvantage().equals(getVAL_EF_TYPE_AVANTAGE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un type d'avantage en nature", "ce libell�"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_DELEGATION Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_DELEGATION() {
		return "NOM_PB_VALIDER_TYPE_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_DELEGATION(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeDelegation(request))
			return false;

		if (!performControlerRegleGestionTypeDelegation(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_DELEGATION() != null && getVAL_ST_ACTION_TYPE_DELEGATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_CREATION)) {
				setTypeDelegationCourant(new TypeDelegation());
				getTypeDelegationCourant().setLibTypeDelegation(getVAL_EF_TYPE_DELEGATION());
				getTypeDelegationCourant().creerTypeDelegation(getTransaction());
				if (!getTransaction().isErreur())
					getListeTypeDelegation().add(getTypeDelegationCourant());
			} else if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_SUPPRESSION)) {
				getTypeDelegationCourant().supprimerTypeDelegation(getTransaction());
				if (!getTransaction().isErreur())
					getListeTypeDelegation().remove(getTypeDelegationCourant());
				setTypeDelegationCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeDelegation(request);
			addZone(getNOM_ST_ACTION_TYPE_DELEGATION(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'un type de d�l�gation Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerSaisieTypeDelegation(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_TYPE_DELEGATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un type de delegation Date de cr�ation :
	 * (14/09/11)
	 */
	private boolean performControlerRegleGestionTypeDelegation(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type de d�l�gation sur une
		// d�l�gation
		if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_SUPPRESSION)
				&& Delegation.listerDelegationAvecTypeDelegation(getTransaction(), getTypeDelegationCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une d�l�gation", "ce type de d�l�gation"));
			return false;
		}

		// V�rification des contraintes d'unicit� de l'entit� g�ographique
		if (getVAL_ST_ACTION_TYPE_DELEGATION().equals(ACTION_CREATION)) {

			for (TypeDelegation type : getListeTypeDelegation()) {
				if (type.getLibTypeDelegation().equals(getVAL_EF_TYPE_DELEGATION().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un type de d�l�gation", "ce libell�"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_TYPE_REGIME Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_PB_VALIDER_TYPE_REGIME() {
		return "NOM_PB_VALIDER_TYPE_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean performPB_VALIDER_TYPE_REGIME(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieTypeRegime(request))
			return false;

		if (!performControlerRegleGestionTypeRegime(request))
			return false;

		if (getVAL_ST_ACTION_TYPE_REGIME() != null && getVAL_ST_ACTION_TYPE_REGIME() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_CREATION)) {
				setTypeRegimeCourant(new TypeRegIndemn());
				getTypeRegimeCourant().setLibTypeRegIndemn(getVAL_EF_TYPE_REGIME());
				getTypeRegimeCourant().creerTypeRegIndemn(getTransaction());
				if (!getTransaction().isErreur())
					getListeTypeRegime().add(getTypeRegimeCourant());
			} else if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_SUPPRESSION)) {
				getTypeRegimeCourant().supprimerTypeRegIndemn(getTransaction());
				if (!getTransaction().isErreur())
					getListeTypeRegime().remove(getTypeRegimeCourant());
				setTypeRegimeCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeTypeRegime(request);
			addZone(getNOM_ST_ACTION_TYPE_REGIME(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'un type de regime indemnitaire Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieTypeRegime(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_TYPE_REGIME()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un type de r�gime idemnitaire Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerRegleGestionTypeRegime(HttpServletRequest request) throws Exception {

		// Verification si suppression d'un type de r�gime idemnitaire utilis�e
		// sur un r�gime idemnitaire
		if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_SUPPRESSION)
				&& RegimeIndemnitaire.listerRegimeIndemnitaireAvecTypeRegime(getTransaction(), getTypeRegimeCourant()).size() > 0) {

			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattach� � @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "un r�gime indemnitaire", "ce type de r�gime idemnitaire"));
			return false;
		}

		// V�rification des contraintes d'unicit� du type de r�gime idemnitaire
		if (getVAL_ST_ACTION_TYPE_REGIME().equals(ACTION_CREATION)) {

			for (TypeRegIndemn type : getListeTypeRegime()) {
				if (type.getLibTypeRegIndemn().equals(getVAL_EF_TYPE_REGIME().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un type de r�gime idemnitaire", "ce libell�"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_ENTITE_GEO
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_ENTITE_GEO() {
		return "NOM_ST_ACTION_ENTITE_GEO";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_ENTITE_GEO Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_ENTITE_GEO() {
		return getZone(getNOM_ST_ACTION_ENTITE_GEO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_NATURE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_NATURE_AVANTAGE() {
		return "NOM_ST_ACTION_NATURE_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_NATURE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_NATURE_AVANTAGE() {
		return getZone(getNOM_ST_ACTION_NATURE_AVANTAGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TITRE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TITRE() {
		return "NOM_ST_ACTION_TITRE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION_TITRE
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TITRE() {
		return getZone(getNOM_ST_ACTION_TITRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TYPE_AVANTAGE
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_AVANTAGE() {
		return "NOM_ST_ACTION_TYPE_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_AVANTAGE() {
		return getZone(getNOM_ST_ACTION_TYPE_AVANTAGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ACTION_TYPE_DELEGATION Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_DELEGATION() {
		return "NOM_ST_ACTION_TYPE_DELEGATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_DELEGATION Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_DELEGATION() {
		return getZone(getNOM_ST_ACTION_TYPE_DELEGATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_TYPE_REGIME
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_ST_ACTION_TYPE_REGIME() {
		return "NOM_ST_ACTION_TYPE_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_ACTION_TYPE_REGIME Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_ST_ACTION_TYPE_REGIME() {
		return getZone(getNOM_ST_ACTION_TYPE_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_ENTITE_GEO() {
		return "NOM_EF_ENTITE_GEO";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_ENTITE_GEO Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_ENTITE_GEO() {
		return getZone(getNOM_EF_ENTITE_GEO());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NATURE_AVANTAGE
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_NATURE_AVANTAGE() {
		return "NOM_EF_NATURE_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NATURE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_NATURE_AVANTAGE() {
		return getZone(getNOM_EF_NATURE_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TITRE() {
		return "NOM_EF_TITRE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_TITRE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TITRE() {
		return getZone(getNOM_EF_TITRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_AVANTAGE Date
	 * de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TYPE_AVANTAGE() {
		return "NOM_EF_TYPE_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TYPE_AVANTAGE() {
		return getZone(getNOM_EF_TYPE_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_DELEGATION
	 * Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TYPE_DELEGATION() {
		return "NOM_EF_TYPE_DELEGATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_DELEGATION Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TYPE_DELEGATION() {
		return getZone(getNOM_EF_TYPE_DELEGATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TYPE_REGIME Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_EF_TYPE_REGIME() {
		return "NOM_EF_TYPE_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_TYPE_REGIME Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_EF_TYPE_REGIME() {
		return getZone(getNOM_EF_TYPE_REGIME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_ENTITE_GEO() {
		if (LB_ENTITE_GEO == null)
			LB_ENTITE_GEO = initialiseLazyLB();
		return LB_ENTITE_GEO;
	}

	/**
	 * Setter de la liste: LB_ENTITE_GEO Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_ENTITE_GEO(String[] newLB_ENTITE_GEO) {
		LB_ENTITE_GEO = newLB_ENTITE_GEO;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ENTITE_GEO Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO() {
		return "NOM_LB_ENTITE_GEO";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_ENTITE_GEO_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO_SELECT() {
		return "NOM_LB_ENTITE_GEO_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_ENTITE_GEO Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_ENTITE_GEO() {
		return getLB_ENTITE_GEO();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_ENTITE_GEO Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_ENTITE_GEO_SELECT() {
		return getZone(getNOM_LB_ENTITE_GEO_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ENTITE_ECOLE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_ENTITE_ECOLE() {
		if (LB_ENTITE_ECOLE == null)
			LB_ENTITE_ECOLE = initialiseLazyLB();
		return LB_ENTITE_ECOLE;
	}

	/**
	 * Setter de la liste: LB_ENTITE_ECOLE Date de cr�ation : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_ENTITE_GEO_ECOLE(String[] newLB_ENTITE_ECOLE) {
		LB_ENTITE_ECOLE = newLB_ENTITE_ECOLE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ENTITE_GEO_ECOLE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO_ECOLE() {
		return "NOM_LB_ENTITE_GEO_ECOLE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_ENTITE_GEO_ECOLE_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_ENTITE_GEO_ECOLE_SELECT() {
		return "NOM_LB_ENTITE_GEO_ECOLE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_ENTITE_GEO_ECOLE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_ENTITE_GEO_ECOLE() {
		return getLB_ENTITE_ECOLE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_ENTITE_GEO_ECOLE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_ENTITE_GEO_ECOLE_SELECT() {
		return getZone(getNOM_LB_ENTITE_GEO_ECOLE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de cr�ation : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE Date de cr�ation :
	 * (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TITRE() {
		if (LB_TITRE == null)
			LB_TITRE = initialiseLazyLB();
		return LB_TITRE;
	}

	/**
	 * Setter de la liste: LB_TITRE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_TITRE(String[] newLB_TITRE) {
		LB_TITRE = newLB_TITRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE Date de cr�ation :
	 * (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TITRE() {
		return "NOM_LB_TITRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TITRE_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TITRE_SELECT() {
		return "NOM_LB_TITRE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TITRE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TITRE() {
		return getLB_TITRE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TITRE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TITRE_SELECT() {
		return getZone(getNOM_LB_TITRE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de cr�ation : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE() {
		return "NOM_LB_TYPE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de cr�ation : (13/09/11
	 * 15:49:10)
	 * 
	 */
	private void setLB_TYPE_DELEGATION(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION() {
		return "NOM_LB_TYPE_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT() {
		return "NOM_LB_TYPE_DELEGATION_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION() {
		return getLB_TYPE_DELEGATION();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private String[] getLB_TYPE_REGIME() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	private void setLB_TYPE_REGIME(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME() {
		return "NOM_LB_TYPE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT() {
		return "NOM_LB_TYPE_REGIME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME() {
		return getLB_TYPE_REGIME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public String getVAL_LB_TYPE_REGIME_SELECT() {
		return getZone(getNOM_LB_TYPE_REGIME_SELECT());
	}

	private EntiteGeo getEntiteGeoCourante() {
		return entiteGeoCourante;
	}

	private void setEntiteGeoCourante(EntiteGeo entiteGeoCourante) {
		this.entiteGeoCourante = entiteGeoCourante;
	}

	private ArrayList<EntiteGeo> getListeEntite() {
		return listeEntite;
	}

	private void setListeEntite(ArrayList<EntiteGeo> listeEntite) {
		this.listeEntite = listeEntite;
	}

	private ArrayList<Ecole> getListeEntiteEcole() {
		return listeEntiteEcole;
	}

	private void setListeEntiteEcole(ArrayList<Ecole> listeEntiteEcole) {
		this.listeEntiteEcole = listeEntiteEcole;
	}

	private ArrayList<NatureAvantage> getListeNatureAvantage() {
		return listeNatureAvantage;
	}

	private void setListeNatureAvantage(ArrayList<NatureAvantage> listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	private ArrayList<TitrePoste> getListeTitrePoste() {
		return listeTitrePoste;
	}

	private void setListeTitrePoste(ArrayList<TitrePoste> listeTitrePoste) {
		this.listeTitrePoste = listeTitrePoste;
	}

	private ArrayList<TypeAvantage> getListeTypeAvantage() {
		return listeTypeAvantage;
	}

	private void setListeTypeAvantage(ArrayList<TypeAvantage> listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	private ArrayList<TypeDelegation> getListeTypeDelegation() {
		return listeTypeDelegation;
	}

	private void setListeTypeDelegation(ArrayList<TypeDelegation> listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
	}

	private ArrayList<TypeRegIndemn> getListeTypeRegime() {
		return listeTypeRegime;
	}

	private void setListeTypeRegime(ArrayList<TypeRegIndemn> listeTypeRegime) {
		this.listeTypeRegime = listeTypeRegime;
	}

	private NatureAvantage getNatureAvantageCourant() {
		return natureAvantageCourant;
	}

	private void setNatureAvantageCourant(NatureAvantage natureAvantageCourant) {
		this.natureAvantageCourant = natureAvantageCourant;
	}

	private TitrePoste getTitrePosteCourante() {
		return titrePosteCourante;
	}

	private void setTitrePosteCourante(TitrePoste titrePosteCourante) {
		this.titrePosteCourante = titrePosteCourante;
	}

	private TypeAvantage getTypeAvantageCourant() {
		return typeAvantageCourant;
	}

	private void setTypeAvantageCourant(TypeAvantage typeAvantageCourant) {
		this.typeAvantageCourant = typeAvantageCourant;
	}

	private TypeDelegation getTypeDelegationCourant() {
		return typeDelegationCourant;
	}

	private void setTypeDelegationCourant(TypeDelegation typeDelegationCourant) {
		this.typeDelegationCourant = typeDelegationCourant;
	}

	private TypeRegIndemn getTypeRegimeCourant() {
		return typeRegimeCourant;
	}

	private void setTypeRegimeCourant(TypeRegIndemn typeRegimeCourant) {
		this.typeRegimeCourant = typeRegimeCourant;
	}

	private String[] LB_NFA;

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (13/09/11 15:49:10)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_ECOLE
			if (testerParametre(request, getNOM_PB_ANNULER_ECOLE())) {
				return performPB_ANNULER_ECOLE(request);
			}

			// Si clic sur le bouton PB_CREER_ECOLE
			if (testerParametre(request, getNOM_PB_CREER_ECOLE())) {
				return performPB_CREER_ECOLE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_ECOLE
			if (testerParametre(request, getNOM_PB_MODIFIER_ECOLE())) {
				return performPB_MODIFIER_ECOLE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ECOLE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ECOLE())) {
				return performPB_SUPPRIMER_ECOLE(request);
			}

			// Si clic sur le bouton PB_VALIDER_ECOLE
			if (testerParametre(request, getNOM_PB_VALIDER_ECOLE())) {
				return performPB_VALIDER_ECOLE(request);
			}

			// Si clic sur le bouton PB_ANNULER_NFA
			if (testerParametre(request, getNOM_PB_ANNULER_NFA())) {
				return performPB_ANNULER_NFA(request);
			}

			// Si clic sur le bouton PB_CREER_NFA
			if (testerParametre(request, getNOM_PB_CREER_NFA())) {
				return performPB_CREER_NFA(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NFA
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NFA())) {
				return performPB_SUPPRIMER_NFA(request);
			}

			// Si clic sur le bouton PB_VALIDER_NFA
			if (testerParametre(request, getNOM_PB_VALIDER_NFA())) {
				return performPB_VALIDER_NFA(request);
			}

			// Si clic sur le bouton PB_ANNULER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_ANNULER_ENTITE_GEO())) {
				return performPB_ANNULER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_ANNULER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_ANNULER_NATURE_AVANTAGE())) {
				return performPB_ANNULER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_ANNULER_TITRE
			if (testerParametre(request, getNOM_PB_ANNULER_TITRE())) {
				return performPB_ANNULER_TITRE(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_AVANTAGE())) {
				return performPB_ANNULER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_DELEGATION())) {
				return performPB_ANNULER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_ANNULER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_ANNULER_TYPE_REGIME())) {
				return performPB_ANNULER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_CREER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_CREER_ENTITE_GEO())) {
				return performPB_CREER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_MODIFIER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_MODIFIER_ENTITE_GEO())) {
				return performPB_MODIFIER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_CREER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_CREER_NATURE_AVANTAGE())) {
				return performPB_CREER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_CREER_TITRE
			if (testerParametre(request, getNOM_PB_CREER_TITRE())) {
				return performPB_CREER_TITRE(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_CREER_TYPE_AVANTAGE())) {
				return performPB_CREER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_CREER_TYPE_DELEGATION())) {
				return performPB_CREER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_CREER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_CREER_TYPE_REGIME())) {
				return performPB_CREER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ENTITE_GEO())) {
				return performPB_SUPPRIMER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NATURE_AVANTAGE())) {
				return performPB_SUPPRIMER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TITRE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TITRE())) {
				return performPB_SUPPRIMER_TITRE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_AVANTAGE())) {
				return performPB_SUPPRIMER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_DELEGATION())) {
				return performPB_SUPPRIMER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_TYPE_REGIME())) {
				return performPB_SUPPRIMER_TYPE_REGIME(request);
			}

			// Si clic sur le bouton PB_VALIDER_ENTITE_GEO
			if (testerParametre(request, getNOM_PB_VALIDER_ENTITE_GEO())) {
				return performPB_VALIDER_ENTITE_GEO(request);
			}

			// Si clic sur le bouton PB_VALIDER_NATURE_AVANTAGE
			if (testerParametre(request, getNOM_PB_VALIDER_NATURE_AVANTAGE())) {
				return performPB_VALIDER_NATURE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TITRE
			if (testerParametre(request, getNOM_PB_VALIDER_TITRE())) {
				return performPB_VALIDER_TITRE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_AVANTAGE
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_AVANTAGE())) {
				return performPB_VALIDER_TYPE_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_DELEGATION
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_DELEGATION())) {
				return performPB_VALIDER_TYPE_DELEGATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_TYPE_REGIME
			if (testerParametre(request, getNOM_PB_VALIDER_TYPE_REGIME())) {
				return performPB_VALIDER_TYPE_REGIME(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (04/11/11 11:33:54)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEFichePoste.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ECOLE Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_ANNULER_ECOLE() {
		return "NOM_PB_ANNULER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_ANNULER_ECOLE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_ECOLE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_ECOLE Date de cr�ation
	 * : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_CREER_ECOLE() {
		return "NOM_PB_CREER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_CREER_ECOLE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_ECOLE(), ACTION_CREATION);
		addZone(getNOM_EF_ECOLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ECOLE_CODE_ECOLE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_ECOLE Date de
	 * cr�ation : (14/09/11 15:57:59)
	 * 
	 */
	public String getNOM_PB_MODIFIER_ECOLE() {
		return "NOM_PB_MODIFIER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 15:57:59)
	 * 
	 */
	public boolean performPB_MODIFIER_ECOLE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ECOLE_SELECT()) ? Integer.parseInt(getVAL_LB_ECOLE_SELECT()) : -1);

		if (indice != -1 && indice < getListeEcole().size()) {
			Ecole ecole = getListeEcole().get(indice);
			setEcoleCourante(ecole);
			addZone(getNOM_EF_ECOLE_CODE_ECOLE(), ecole.getCdecol());
			addZone(getNOM_EF_ECOLE(), ecole.getLiecol());
			addZone(getNOM_ST_ACTION_ECOLE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "�coles"));
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ECOLE Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_ECOLE() {
		return "NOM_PB_SUPPRIMER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_SUPPRIMER_ECOLE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_ECOLE_SELECT()) ? Integer.parseInt(getVAL_LB_ECOLE_SELECT()) : -1);

		if (indice != -1 && indice < getListeEcole().size()) {
			Ecole ecole = getListeEcole().get(indice);
			setEcoleCourante(ecole);
			addZone(getNOM_EF_ECOLE_CODE_ECOLE(), ecole.getCdecol());
			addZone(getNOM_EF_ECOLE(), ecole.getLiecol());
			addZone(getNOM_ST_ACTION_ECOLE(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Ecoles"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_ECOLE Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_VALIDER_ECOLE() {
		return "NOM_PB_VALIDER_ECOLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_VALIDER_ECOLE(HttpServletRequest request) throws Exception {

		if (!performControlerSaisieEcole(request))
			return false;

		if (!performControlerRegleGestionEcole(request))
			return false;

		if (getVAL_ST_ACTION_ECOLE() != null && getVAL_ST_ACTION_ECOLE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_ECOLE().equals(ACTION_CREATION)) {
				setEcoleCourante(new Ecole());
				getEcoleCourante().setCdecol(getVAL_EF_ECOLE_CODE_ECOLE());
				getEcoleCourante().setLiecol(getVAL_EF_ECOLE());
				getEcoleCourante().setQuecol(Const.CHAINE_VIDE);
				getEcoleCourante().setSecter(Const.CHAINE_VIDE);
				getEcoleCourante().creerEcole(getTransaction());
				if (!getTransaction().isErreur())
					getListeEcole().add(getEcoleCourante());
			} else if (getVAL_ST_ACTION_ECOLE().equals(ACTION_SUPPRESSION)) {
				getEcoleCourante().supprimerEcole(getTransaction());
				if (!getTransaction().isErreur())
					getListeEcole().remove(getEcoleCourante());
				setEcoleCourante(null);
			} else if (getVAL_ST_ACTION_ECOLE().equals(ACTION_MODIFICATION)) {
				getEcoleCourante().setLiecol(getVAL_EF_ECOLE());
				getEcoleCourante().modifierEcole(getTransaction());
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeEcole(request);
			addZone(getNOM_ST_ACTION_ECOLE(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'un type de regime indemnitaire Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieEcole(HttpServletRequest request) throws Exception {
		// Verification code ecole numerique
		if (!Services.estNumerique(getVAL_EF_ECOLE_CODE_ECOLE())) {
			// "ERR992", "La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "code �cole"));
			return false;
		}
		// Verification code ecole not null
		if (getZone(getNOM_EF_ECOLE_CODE_ECOLE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�e"));
			return false;
		}
		// Verification lib ecole not null
		if (getZone(getNOM_EF_ECOLE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code ecole"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un type de r�gime idemnitaire Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerRegleGestionEcole(HttpServletRequest request) throws Exception {
		// V�rification des contraintes d'unicit� du code ecole
		if (getVAL_ST_ACTION_ECOLE().equals(ACTION_CREATION)) {
			for (Ecole ecole : getListeEcole()) {
				if (ecole.getCdecol().equals(getVAL_EF_ECOLE_CODE_ECOLE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une �cole", "ce code �cole"));
					return false;
				}
			}
		}
		// V�rification des contraintes d'unicit� du libell� ecole
		if (getVAL_ST_ACTION_ECOLE().equals(ACTION_CREATION)) {
			for (Ecole ecole : getListeEcole()) {
				if (ecole.getLiecol().equals(getVAL_EF_ECOLE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une �cole", "ce libell�"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_ECOLE Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_ST_ACTION_ECOLE() {
		return "NOM_ST_ACTION_ECOLE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION_ECOLE
	 * Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_ST_ACTION_ECOLE() {
		return getZone(getNOM_ST_ACTION_ECOLE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ECOLE Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_ECOLE() {
		return "NOM_EF_ECOLE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ECOLE_CODE_ECOLE
	 * Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_ECOLE_CODE_ECOLE() {
		return "NOM_EF_ECOLE_CODE_ECOLE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_ECOLE_CODE_ECOLE Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_ECOLE_CODE_ECOLE() {
		return getZone(getNOM_EF_ECOLE_CODE_ECOLE());
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_ECOLE Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_ECOLE() {
		return getZone(getNOM_EF_ECOLE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_NFA Date de cr�ation
	 * : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_ANNULER_NFA() {
		return "NOM_PB_ANNULER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_ANNULER_NFA(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_NFA(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_NFA Date de cr�ation :
	 * (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_CREER_NFA() {
		return "NOM_PB_CREER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_CREER_NFA(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_NFA(), ACTION_CREATION);
		addZone(getNOM_EF_NFA(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NFA_CODE_SERVICE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NFA Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NFA() {
		return "NOM_PB_SUPPRIMER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_SUPPRIMER_NFA(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_NFA_SELECT()) ? Integer.parseInt(getVAL_LB_NFA_SELECT()) : -1);

		if (indice != -1 && indice < getListeTypeRegime().size()) {
			NFA nfa = getListeNFA().get(indice);
			setNFACourant(nfa);
			addZone(getNOM_EF_NFA(), nfa.getCodeService());
			addZone(getNOM_EF_NFA(), nfa.getNFA());
			addZone(getNOM_ST_ACTION_NFA(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "NFAs"));
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_NFA Date de cr�ation
	 * : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_PB_VALIDER_NFA() {
		return "NOM_PB_VALIDER_NFA";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public boolean performPB_VALIDER_NFA(HttpServletRequest request) throws Exception {

		if (!performControlerSaisieNFA(request))
			return false;

		if (!performControlerRegleGestionNFA(request))
			return false;

		if (getVAL_ST_ACTION_NFA() != null && getVAL_ST_ACTION_NFA() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_NFA().equals(ACTION_CREATION)) {
				setNFACourant(new NFA());
				getNFACourant().setCodeService(getVAL_EF_NFA_CODE_SERVICE());
				getNFACourant().setNFA(getVAL_EF_NFA());
				getNFACourant().creerNFA(getTransaction());
				if (!getTransaction().isErreur())
					getListeNFA().add(getNFACourant());
			} else if (getVAL_ST_ACTION_NFA().equals(ACTION_SUPPRESSION)) {
				getNFACourant().supprimerNFA(getTransaction());
				if (!getTransaction().isErreur())
					getListeNFA().remove(getNFACourant());
				setNFACourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeNFA(request);
			addZone(getNOM_ST_ACTION_NFA(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'un type de regime indemnitaire Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerSaisieNFA(HttpServletRequest request) throws Exception {

		// Verification lib domaine d'activite not null
		if (getZone(getNOM_EF_NFA()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code service"));
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les r�gles de gestion d'un type de r�gime idemnitaire Date de
	 * cr�ation : (14/09/11)
	 */
	private boolean performControlerRegleGestionNFA(HttpServletRequest request) throws Exception {

		// V�rification des contraintes d'unicit� du NFA
		if (getVAL_ST_ACTION_NFA().equals(ACTION_CREATION)) {

			for (NFA nfa : getListeNFA()) {
				if (nfa.getCodeService().equals(getVAL_EF_NFA_CODE_SERVICE().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un NFA", "ce code service"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_NFA Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_ST_ACTION_NFA() {
		return "NOM_ST_ACTION_NFA";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION_NFA
	 * Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_ST_ACTION_NFA() {
		return getZone(getNOM_ST_ACTION_NFA());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NFA Date de
	 * cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_NFA() {
		return "NOM_EF_NFA";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie : EF_NFA
	 * Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_NFA() {
		return getZone(getNOM_EF_NFA());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NFA_CODE_SERVICE
	 * Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_EF_NFA_CODE_SERVICE() {
		return "NOM_EF_NFA_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NFA_CODE_SERVICE Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_EF_NFA_CODE_SERVICE() {
		return getZone(getNOM_EF_NFA_CODE_SERVICE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NFA Date de cr�ation :
	 * (04/11/11 11:33:55)
	 * 
	 */
	private String[] getLB_NFA() {
		if (LB_NFA == null)
			LB_NFA = initialiseLazyLB();
		return LB_NFA;
	}

	/**
	 * Setter de la liste: LB_NFA Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	private void setLB_NFA(String[] newLB_NFA) {
		LB_NFA = newLB_NFA;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NFA Date de cr�ation :
	 * (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_NFA() {
		return "NOM_LB_NFA";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_NFA_SELECT Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_NFA_SELECT() {
		return "NOM_LB_NFA_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_NFA Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String[] getVAL_LB_NFA() {
		return getLB_NFA();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_NFA Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_LB_NFA_SELECT() {
		return getZone(getNOM_LB_NFA_SELECT());
	}

	private NFA getNFACourant() {
		return NFACourant;
	}

	private void setNFACourant(NFA courant) {
		NFACourant = courant;
	}

	private ArrayList<NFA> getListeNFA() {
		return listeNFA;
	}

	private void setListeNFA(ArrayList<NFA> listeNFA) {
		this.listeNFA = listeNFA;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ECOLE Date de cr�ation :
	 * (04/11/11 11:33:55)
	 * 
	 */
	private String[] getLB_ECOLE() {
		if (LB_ECOLE == null)
			LB_ECOLE = initialiseLazyLB();
		return LB_ECOLE;
	}

	/**
	 * Setter de la liste: LB_ECOLE Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	private void setLB_ECOLE(String[] newLB_ECOLE) {
		LB_ECOLE = newLB_ECOLE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ECOLE Date de cr�ation :
	 * (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_ECOLE() {
		return "NOM_LB_ECOLE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_ECOLE_SELECT Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getNOM_LB_ECOLE_SELECT() {
		return "NOM_LB_ECOLE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_ECOLE Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String[] getVAL_LB_ECOLE() {
		return getLB_ECOLE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_ECOLE Date de cr�ation : (04/11/11 11:33:55)
	 * 
	 */
	public String getVAL_LB_ECOLE_SELECT() {
		return getZone(getNOM_LB_ECOLE_SELECT());
	}

	private Ecole getEcoleCourante() {
		return EcoleCourante;
	}

	private void setEcoleCourante(Ecole courant) {
		EcoleCourante = courant;
	}

	private ArrayList<Ecole> getListeEcole() {
		return listeEcole;
	}

	private void setListeEcole(ArrayList<Ecole> listeEcole) {
		this.listeEcole = listeEcole;
	}
}