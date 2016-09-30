package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.droits.Droit;
import nc.mairie.metier.droits.Element;
import nc.mairie.metier.droits.Groupe;
import nc.mairie.metier.droits.TypeDroit;
import nc.mairie.spring.dao.metier.droits.DroitDao;
import nc.mairie.spring.dao.metier.droits.ElementDao;
import nc.mairie.spring.dao.metier.droits.GroupeDao;
import nc.mairie.spring.dao.metier.droits.GroupeUtilisateurDao;
import nc.mairie.spring.dao.metier.droits.TypeDroitDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeDROITSGestion Date de création : (10/10/11 14:37:55)
 */
public class OeDROITSGestion extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_TYPE_DROIT;

	private ArrayList<Element> listeElement;
	private ArrayList<Groupe> listeGroupe;
	private ArrayList<Droit> listeDroits;
	private Hashtable<String, String> hashDroit;
	private ArrayList<TypeDroit> listeTypeDroits;
	private Hashtable<String, String> hashTypeDroit;

	public String focus = null;
	public String ACTION_CREATION_GROUPE = "Création d'un groupe.";
	public String ACTION_MODIFICATION_GROUPE = "Modification d'un groupe.";
	public String ACTION_SUPPRESSION_GROUPE = "Suppression d'un groupe.";
	public String ACTION_CREATION_ELEMENT = "Création d'un élément.";

	private Groupe groupeCourant;
	private TypeDroitDao typeDroitDao;
	private DroitDao droitDao;
	private ElementDao elementDao;
	private GroupeDao groupeDao;
	private GroupeUtilisateurDao groupeUtilisateurDao;

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_ELEMENT Date de
	 * création : (10/10/11 14:37:55)
	 * 
	 * @return String
	 */
	public String getNOM_PB_AJOUTER_ELEMENT() {
		return "NOM_PB_AJOUTER_ELEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 14:37:55)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 */
	public boolean performPB_AJOUTER_ELEMENT(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_CREATION_ELEMENT);
		setFocus(getNOM_EF_NOM_ELEMENT());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_GROUPE Date de
	 * création : (10/10/11 14:37:55)
	 * 
	 * @return String
	 */
	public String getNOM_PB_AJOUTER_GROUPE() {
		return "NOM_PB_AJOUTER_GROUPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 14:37:55)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 */
	public boolean performPB_AJOUTER_GROUPE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_CREATION_GROUPE);
		setFocus(getNOM_EF_NOM_GROUPE());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_ELEMENT Date de
	 * création : (10/10/11 14:37:55)
	 * 
	 * @return String
	 */
	public String getNOM_PB_SUPPRIMER_ELEMENT() {
		return "NOM_PB_SUPPRIMER_ELEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 14:37:55)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 */
	public boolean performPB_SUPPRIMER_ELEMENT(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 14:37:55)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param i
	 *            numero ligne
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 */
	public boolean performPB_SUPPRIMER_GROUPE(HttpServletRequest request, int i) throws Exception {
		setGroupeCourant((Groupe) getListeGroupe().get(i));
		addZone(getNOM_EF_NOM_GROUPE(), getGroupeCourant().getLibGroupe());
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION_GROUPE);
		return true;
	}

	/**
	 * Getter de la liste des éléments.
	 * 
	 * @return listeElement
	 */
	public ArrayList<Element> getListeElement() {
		return listeElement;
	}

	/**
	 * Setter de la liste des éléments.
	 * 
	 * @param listeElement
	 *            listeElement à  définir
	 */
	private void setListeElement(ArrayList<Element> listeElement) {
		this.listeElement = listeElement;
	}

	/**
	 * Getter de la liste des groupes.
	 * 
	 * @return listeGroupe
	 */
	public ArrayList<Groupe> getListeGroupe() {
		return listeGroupe;
	}

	/**
	 * Setter de la liste des groupes
	 * 
	 * @param listeGroupe
	 */
	private void setListeGroupe(ArrayList<Groupe> listeGroupe) {
		this.listeGroupe = listeGroupe;
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
		return getNOM_PB_VALIDER();
	}

	/**
	 * @param focus
	 *            focus à  définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (10/10/11 15:14:20)
	 * 
	 * @return String
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 15:14:20)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (10/10/11 15:14:20)
	 * 
	 * @return String
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 15:14:20)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Récupération des nouveaux droits
		for (int i = 0; i < getListeElement().size(); i++) {
			Element elt = (Element) getListeElement().get(i);
			for (int j = 0; j < getListeGroupe().size(); j++) {
				Groupe grpe = (Groupe) getListeGroupe().get(j);
				getHashDroit().put(elt.getIdElement() + "-" + grpe.getIdGroupe(), getVAL_LB_TYPE_DROIT_SELECT(i, j));
			}
		}

		// Sauvegarde de la matrice
		for (int i = 0; i < getListeDroits().size(); i++) {
			Droit d = (Droit) getListeDroits().get(i);
			String newIdTypeDroit = (String) getHashDroit().get(d.getIdElement() + "-" + d.getIdGroupe());
			d.setIdTypeDroit(newIdTypeDroit.equals("0") ? null : Integer.valueOf(newIdTypeDroit));
			Droit droitExistant = getDroitDao().chercherDroit(d.getIdElement(), d.getIdGroupe());
			if (droitExistant == null) {
				getDroitDao().creerDroit(d.getIdElement(), d.getIdGroupe(), d.getIdTypeDroit());
			} else {
				getDroitDao().modifierDroit(d.getIdElement(), d.getIdGroupe(), d.getIdTypeDroit());
			}
		}

		commitTransaction();
		return true;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTypeDroitDao() == null) {
			setTypeDroitDao(new TypeDroitDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDroitDao() == null) {
			setDroitDao(new DroitDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getElementDao() == null) {
			setElementDao(new ElementDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getGroupeDao() == null) {
			setGroupeDao(new GroupeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getGroupeUtilisateurDao() == null) {
			setGroupeUtilisateurDao(new GroupeUtilisateurDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (10/10/11 16:15:05)
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();
		// Si liste éléments vide alors affectation
		if (getListeElement() == null || getListeElement().size() == 0) {
			ArrayList<Element> elt = getElementDao().listerElement();
			setListeElement(elt);
		}
		// Si liste groupes vide alors affectation
		if (getListeGroupe() == null || getListeGroupe().size() == 0) {
			ArrayList<Groupe> gr = getGroupeDao().listerGroupe();
			setListeGroupe(gr);
		}

		// Initialisation des droits
		if (getListeDroits() == null || getListeDroits().size() == 0) {
			setListeDroits(getDroitDao().listerDroit());

			// Remplissage de la hashtable des droits.
			for (ListIterator<Droit> list = getListeDroits().listIterator(); list.hasNext();) {
				Droit aDroit = (Droit) list.next();
				getHashDroit().put(aDroit.getIdElement() + "-" + aDroit.getIdGroupe(),
						aDroit.getIdTypeDroit() == null ? "0" : aDroit.getIdTypeDroit().toString());
			}
		}

		// Initialisation des types de droits
		if (getListeTypeDroits() == null || getListeTypeDroits().size() == 0) {
			setListeTypeDroits(getTypeDroitDao().listerTypeDroit());

			if (getListeTypeDroits().size() != 0) {
				int[] tailles = { 15 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TypeDroit> list = getListeTypeDroits().listIterator(); list.hasNext();) {
					TypeDroit de = (TypeDroit) list.next();
					String ligne[] = { de.getLibTypeDroit() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_DROIT(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE_DROIT(null);
			}

			// Remplissage de la hashtable des types de droits (idType, index
			// dans la liste).
			for (ListIterator<TypeDroit> list = getListeTypeDroits().listIterator(); list.hasNext();) {
				TypeDroit aTypeDroit = (TypeDroit) list.next();
				// indice +1 pour gerer la ligne vide
				getHashTypeDroit().put(aTypeDroit.getIdTypeDroit().toString(),
						String.valueOf(getListeTypeDroits().indexOf(aTypeDroit) + 1));
			}
		}

		// Initialisation de la matrice
		for (int i = 0; i < getListeElement().size(); i++) {
			Element elt = (Element) getListeElement().get(i);
			for (int j = 0; j < getListeGroupe().size(); j++) {
				Groupe grpe = (Groupe) getListeGroupe().get(j);
				String idTypeDroit = (String) getHashDroit().get(elt.getIdElement() + "-" + grpe.getIdGroupe());

				if (idTypeDroit != null) {
					addZone(getNOM_LB_TYPE_DROIT_SELECT(i, j), getHashTypeDroit().get(idTypeDroit));
				} else
					addZone(getNOM_LB_TYPE_DROIT_SELECT(i, j), String.valueOf(0));
			}
		}
	}

	/**
	 * Retourne le nom de l'ecran utilise par la gestion des droits
	 * 
	 * @return String
	 */
	public String getNomEcran() {
		return "ECR-DROIT-GROUPE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (10/10/11 16:15:06)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (10/10/11 16:15:06)
	 * 
	 * @return String
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_ELEMENT Date de
	 * création : (10/10/11 16:15:06)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_EF_NOM_ELEMENT() {
		return "NOM_EF_NOM_ELEMENT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_NOM_ELEMENT Date de création : (10/10/11 16:15:06)
	 * 
	 * @return String
	 * 
	 */
	public String getVAL_EF_NOM_ELEMENT() {
		return getZone(getNOM_EF_NOM_ELEMENT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_GROUPE Date de
	 * création : (10/10/11 16:15:06)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_EF_NOM_GROUPE() {
		return "NOM_EF_NOM_GROUPE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_NOM_GROUPE Date de création : (10/10/11 16:15:06)
	 * 
	 * @return String
	 * 
	 */
	public String getVAL_EF_NOM_GROUPE() {
		return getZone(getNOM_EF_NOM_GROUPE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AJOUT Date de
	 * création : (10/10/11 16:22:57)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_PB_VALIDER_AJOUT() {
		return "NOM_PB_VALIDER_AJOUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (10/10/11 16:22:57)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean performPB_VALIDER_AJOUT(HttpServletRequest request) throws Exception {
		if (ACTION_CREATION_ELEMENT.equals(getVAL_ST_ACTION())) {
			Element elt = new Element(getVAL_EF_NOM_ELEMENT());
			Integer id = getElementDao().creerElement(elt.getLibElement());
			elt.setIdElement(id);
			getListeElement().add(elt);
			for (int i = 0; i < getListeGroupe().size(); i++) {
				Groupe gr = (Groupe) getListeGroupe().get(i);
				Droit d = new Droit(elt.getIdElement(), gr.getIdGroupe());
				getDroitDao().creerDroit(d.getIdElement(), d.getIdGroupe(), d.getIdTypeDroit());
				getListeDroits().add(d);
				getHashDroit().put(elt.getIdElement() + "-" + gr.getIdGroupe(), "0");
			}
		} else if (ACTION_CREATION_GROUPE.equals(getVAL_ST_ACTION())) {
			Groupe gr = new Groupe(getVAL_EF_NOM_GROUPE());
			Integer id = getGroupeDao().creerGroupe(gr.getLibGroupe());
			gr.setIdGroupe(id);
			getListeGroupe().add(gr);
			for (int i = 0; i < getListeElement().size(); i++) {
				Element elt = (Element) getListeElement().get(i);
				Droit d = new Droit(elt.getIdElement(), gr.getIdGroupe());
				getDroitDao().creerDroit(d.getIdElement(), d.getIdGroupe(), d.getIdTypeDroit());
				getListeDroits().add(d);
				getHashDroit().put(elt.getIdElement() + "-" + gr.getIdGroupe(), "0");
			}
		}
		commitTransaction();
		viderFormulaire();
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Vide le formulaire de saisie.
	 * 
	 * @throws Exception
	 *             Exception
	 */
	private void viderFormulaire() throws Exception {
		addZone(getNOM_EF_NOM_ELEMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NOM_GROUPE(), Const.CHAINE_VIDE);
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DROIT Date de
	 * création : (11/10/11 11:39:06)
	 * 
	 * @return String[]
	 * 
	 */
	private String[] getLB_TYPE_DROIT(int i, int j) {
		if (LB_TYPE_DROIT == null)
			LB_TYPE_DROIT = initialiseLazyLB();
		return LB_TYPE_DROIT;
	}

	/**
	 * Setter de la liste: LB_TYPE_DROIT Date de création : (11/10/11 11:39:06)
	 * 
	 */
	private void setLB_TYPE_DROIT(String[] newLB_TYPE_DROIT) {
		LB_TYPE_DROIT = newLB_TYPE_DROIT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DROIT Date de
	 * création : (11/10/11 11:39:06)
	 * 
	 * @param i
	 *            ligne
	 * @param j
	 *            colonne
	 * @return String
	 * 
	 */
	public String getNOM_LB_TYPE_DROIT(int i, int j) {
		return "NOM_LB_TYPE_DROIT_" + i + "_" + j;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DROIT_SELECT Date de création : (11/10/11 11:39:06)
	 * 
	 * @param i
	 *            ligne
	 * @param j
	 *            colonne
	 * @return String
	 * 
	 */
	public String getNOM_LB_TYPE_DROIT_SELECT(int i, int j) {
		return "NOM_LB_TYPE_DROIT_" + i + "_" + j + "_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_TYPE_DROIT Date de création : (11/10/11 11:39:06)
	 * 
	 * @param i
	 *            ligne
	 * @param j
	 *            colonne
	 * @return String[]
	 * 
	 */
	public String[] getVAL_LB_TYPE_DROIT(int i, int j) {
		return getLB_TYPE_DROIT(i, j);
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_DROIT Date de création : (11/10/11 11:39:06)
	 * 
	 * @param i
	 *            ligne
	 * @param j
	 *            colonne
	 * @return String
	 * 
	 */
	public String getVAL_LB_TYPE_DROIT_SELECT(int i, int j) {
		return getZone(getNOM_LB_TYPE_DROIT_SELECT(i, j));
	}

	/**
	 * Getter de la liste des droits.
	 * 
	 * @return listeDroits
	 */
	private ArrayList<Droit> getListeDroits() {
		return listeDroits;
	}

	/**
	 * Setter de la liste des droits.
	 * 
	 * @param listeDroits
	 */
	private void setListeDroits(ArrayList<Droit> listeDroits) {
		this.listeDroits = listeDroits;
	}

	/**
	 * Getter de la liste des types de droits.
	 * 
	 * @return listeTypeDroits
	 */
	private ArrayList<TypeDroit> getListeTypeDroits() {
		return listeTypeDroits;
	}

	/**
	 * Setter de la liste des types de droits.
	 * 
	 * @param listeTypeDroits
	 */
	private void setListeTypeDroits(ArrayList<TypeDroit> listeTypeDroits) {
		this.listeTypeDroits = listeTypeDroits;
	}

	/**
	 * Getter de la HashTable des types de droits.
	 * 
	 * @return hashTypeDroit
	 */
	private Hashtable<String, String> getHashTypeDroit() {
		if (hashTypeDroit == null) {
			hashTypeDroit = new Hashtable<String, String>();
		}
		return hashTypeDroit;
	}

	/**
	 * Getter de la HashTable des droits.
	 * 
	 * @return hashDroit
	 */
	private Hashtable<String, String> getHashDroit() {
		if (hashDroit == null) {
			hashDroit = new Hashtable<String, String>();
		}
		return hashDroit;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_GROUPE Date de
	 * création : (20/10/11 10:25:32)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getNOM_PB_MODIFIER_GROUPE(int i) {
		return "NOM_PB_MODIFIER_GROUPE_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (20/10/11 10:25:32)
	 * 
	 * @param i
	 *            id
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return String
	 * 
	 */
	public boolean performPB_MODIFIER_GROUPE(HttpServletRequest request, int i) throws Exception {
		setGroupeCourant((Groupe) getListeGroupe().get(i));
		addZone(getNOM_EF_NOM_GROUPE(), getGroupeCourant().getLibGroupe());
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_GROUPE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_GROUPE Date de
	 * création : (20/10/11 10:25:32)
	 * 
	 * @param i
	 *            id
	 * @return String
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_GROUPE(int i) {
		return "NOM_PB_SUPPRIMER_GROUPE_" + i;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (10/10/11 14:37:55)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER_MODIFICATION_GRPE
			if (testerParametre(request, getNOM_PB_VALIDER_MODIFICATION_GRPE())) {
				return performPB_VALIDER_MODIFICATION_GRPE(request);
			}

			// Si clic sur le bouton PB_VALIDER_SUPPRESSION_GRPE
			if (testerParametre(request, getNOM_PB_VALIDER_SUPPRESSION_GRPE())) {
				return performPB_VALIDER_SUPPRESSION_GRPE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_GROUPE
			for (int i = 0; i < getListeGroupe().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_GROUPE(i))) {
					return performPB_MODIFIER_GROUPE(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_AJOUT
			if (testerParametre(request, getNOM_PB_VALIDER_AJOUT())) {
				return performPB_VALIDER_AJOUT(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_ELEMENT
			if (testerParametre(request, getNOM_PB_AJOUTER_ELEMENT())) {
				return performPB_AJOUTER_ELEMENT(request);
			}

			// Si clic sur le bouton PB_AJOUTER_GROUPE
			if (testerParametre(request, getNOM_PB_AJOUTER_GROUPE())) {
				return performPB_AJOUTER_GROUPE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_ELEMENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_ELEMENT())) {
				return performPB_SUPPRIMER_ELEMENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_GROUPE
			for (int i = 0; i < getListeGroupe().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_GROUPE(i))) {
					return performPB_SUPPRIMER_GROUPE(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeDROITSGestion. Date de création : (20/10/11
	 * 11:05:27)
	 * 
	 */
	public OeDROITSGestion() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (20/10/11 11:05:27)
	 * 
	 */
	public String getJSP() {
		return "OeDROITSGestion.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MODIFICATION_GRPE
	 * Date de création : (20/10/11 11:05:27)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_PB_VALIDER_MODIFICATION_GRPE() {
		return "NOM_PB_VALIDER_MODIFICATION_GRPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (20/10/11 11:05:27)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean performPB_VALIDER_MODIFICATION_GRPE(HttpServletRequest request) throws Exception {
		getGroupeCourant().setLibGroupe(getVAL_EF_NOM_GROUPE());
		getGroupeDao().modifierGroupe(getGroupeCourant().getIdGroupe(), getGroupeCourant().getLibGroupe());
		commitTransaction();
		setGroupeCourant(null);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_SUPPRESSION_GRPE
	 * Date de création : (20/10/11 11:05:27)
	 * 
	 * @return String
	 * 
	 */
	public String getNOM_PB_VALIDER_SUPPRESSION_GRPE() {
		return "NOM_PB_VALIDER_SUPPRESSION_GRPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (20/10/11 11:05:27)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 * @return boolean
	 * 
	 */
	public boolean performPB_VALIDER_SUPPRESSION_GRPE(HttpServletRequest request) throws Exception {
		// Suppression des droits en BD
		getDroitDao().supprimerDroitAvecGroupe(getGroupeCourant().getIdGroupe());
		// Suppression des liens du groupe supprimé avec les utilisateurs en BD
		getGroupeUtilisateurDao().supprimerGroupeUtilisateurAvecGroupe(getGroupeCourant().getIdGroupe());
		// Suppression du groupe en BD
		getGroupeDao().supprimerGroupe(getGroupeCourant().getIdGroupe());
		commitTransaction();

		// Mise à  jour de la liste de droits
		ArrayList<Droit> droitsASupprimer = new ArrayList<Droit>();
		for (int i = 0; i < getListeDroits().size(); i++) {
			Droit d = (Droit) getListeDroits().get(i);
			if (d.getIdGroupe() == getGroupeCourant().getIdGroupe())
				droitsASupprimer.add(d);
		}
		getListeDroits().removeAll(droitsASupprimer);

		// Mise à  jour de la liste des groupes.
		getListeGroupe().remove(getGroupeCourant());

		setGroupeCourant(null);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Getter du groupe courant.
	 * 
	 * @return groupeCourant
	 */
	private Groupe getGroupeCourant() {
		return groupeCourant;
	}

	/**
	 * Setter du groupe courant.
	 * 
	 * @param groupeCourant
	 *            groupeCourant à  définir
	 */
	private void setGroupeCourant(Groupe groupeCourant) {
		this.groupeCourant = groupeCourant;
	}

	public TypeDroitDao getTypeDroitDao() {
		return typeDroitDao;
	}

	public void setTypeDroitDao(TypeDroitDao typeDroitDao) {
		this.typeDroitDao = typeDroitDao;
	}

	public DroitDao getDroitDao() {
		return droitDao;
	}

	public void setDroitDao(DroitDao droitDao) {
		this.droitDao = droitDao;
	}

	public ElementDao getElementDao() {
		return elementDao;
	}

	public void setElementDao(ElementDao elementDao) {
		this.elementDao = elementDao;
	}

	public GroupeDao getGroupeDao() {
		return groupeDao;
	}

	public void setGroupeDao(GroupeDao groupeDao) {
		this.groupeDao = groupeDao;
	}

	public GroupeUtilisateurDao getGroupeUtilisateurDao() {
		return groupeUtilisateurDao;
	}

	public void setGroupeUtilisateurDao(GroupeUtilisateurDao groupeUtilisateurDao) {
		this.groupeUtilisateurDao = groupeUtilisateurDao;
	}
}
