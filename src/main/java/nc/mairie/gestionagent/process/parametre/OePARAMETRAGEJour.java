package nc.mairie.gestionagent.process.parametre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.spring.dao.metier.parametrage.JourFerieDao;
import nc.mairie.spring.dao.metier.parametrage.TypeJourFerieDao;
import nc.mairie.spring.domain.metier.parametrage.JourFerie;
import nc.mairie.spring.domain.metier.parametrage.TypeJourFerie;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
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
public class OePARAMETRAGEJour extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	public String ACTION_VISUALISATION = "Consultation d'une année.";
	public String ACTION_MODIFICATION = "Modification d'une année.";
	public String ACTION_CREATION_JOUR = "Création d'un jour.";
	public String ACTION_MODIFICATION_JOUR = "Modification d'un jour.";
	public String ACTION_SUPPRESSION_JOUR = "Suppression d'un jour.";

	private Hashtable<Integer, TypeJourFerie> hashTypeJour;

	private String[] LB_TYPE_JOUR;

	private ArrayList<String> listeAnnee;
	private ArrayList<JourFerie> listeJourFerie;
	private ArrayList<TypeJourFerie> listeTypeJourFerie;
	private JourFerie jourFerieCourant;
	private JourFerieDao jourFerieDao;
	private TypeJourFerieDao typeJourFerieDao;

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

		initialiseListeDeroulante();

		initialiseAnnee();

	}

	private void initialiseListeDeroulante() {
		// si liste typeJour
		if (getHashTypeJour().size() == 0) {
			ArrayList<TypeJourFerie> listeType = getTypeJourFerieDao().listerTypeJour();
			setListeTypeJourFerie(listeType);

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (TypeJourFerie list : listeType) {
				String ligne[] = { list.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_JOUR(aFormat.getListeFormatee());

			// remplissage de la hashTable
			for (TypeJourFerie sc : listeType)
				getHashTypeJour().put(sc.getIdTypeJour(), sc);
		}

	}

	private void initialiseAnnee() {
		setListeAnnee(getJourFerieDao().listerAnnee());
		afficheListeAnnee();
	}

	private void afficheListeAnnee() {
		for (int i = 0; i < getListeAnnee().size(); i++) {
			String annee = getListeAnnee().get(i);
			addZone(getNOM_ST_ANNEE(i), annee);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getJourFerieDao() == null) {
			setJourFerieDao((JourFerieDao) context.getBean("jourFerieDao"));
		}
		if (getTypeJourFerieDao() == null) {
			setTypeJourFerieDao((TypeJourFerieDao) context.getBean("typeJourFerieDao"));
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAvancement. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEJour() {
		super();
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAnnee().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_VISUALISATION
			for (int i = 0; i < getListeAnnee().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_ANNEE
			if (testerParametre(request, getNOM_PB_CREER_ANNEE())) {
				return performPB_CREER_ANNEE(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_MODIFIER_JOUR
			for (int i = 0; i < getListeJourFerie().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_JOUR(i))) {
					return performPB_MODIFIER_JOUR(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_JOUR
			for (int i = 0; i < getListeJourFerie().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_JOUR(i))) {
					return performPB_SUPPRIMER_JOUR(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_JOUR
			if (testerParametre(request, getNOM_PB_AJOUTER_JOUR())) {
				return performPB_AJOUTER_JOUR(request);
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
		return "OePARAMETRAGEJour.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-JOUR";
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
		return "";
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public ArrayList<String> getListeAnnee() {
		return listeAnnee == null ? new ArrayList<String>() : listeAnnee;
	}

	public void setListeAnnee(ArrayList<String> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	public JourFerieDao getJourFerieDao() {
		return jourFerieDao;
	}

	public void setJourFerieDao(JourFerieDao jourFerieDao) {
		this.jourFerieDao = jourFerieDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_JOUR(), Const.CHAINE_VIDE);
		setJourFerieCourant(null);

		String annee = getListeAnnee().get(indiceEltAConsulter);

		setListeJourFerie(getJourFerieDao().listerJourByAnnee(annee));
		// init du calendrier courant
		if (!afficheCalendrier(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean afficheCalendrier(HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (int i = 0; i < getListeJourFerie().size(); i++) {
			JourFerie jour = getListeJourFerie().get(i);
			addZone(getNOM_ST_DATE_JOUR(i), sdf.format(jour.getDateJour()));
			addZone(getNOM_ST_TYPE_JOUR(i), jour.getIdTypeJour().toString().equals("1") ? "Férié" : "Chômé");
			addZone(getNOM_ST_DESCRIPTION_JOUR(i), jour.getDescription());
		}
		return true;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_JOUR(), Const.CHAINE_VIDE);
		setJourFerieCourant(null);

		String annee = getListeAnnee().get(indiceEltAModifier);

		setListeJourFerie(getJourFerieDao().listerJourByAnnee(annee));
		// init du calendrier courant
		if (!afficheCalendrier(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_CREER_ANNEE() {
		return "NOM_PB_CREER_ANNEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_CREER_ANNEE(HttpServletRequest request) throws Exception {
		// on duplique tous les jours fériés pour l'année suivante
		// on recupere la derniere année
		String annee = getListeAnnee().get(0);

		// on cherche tous les jours fériés de cette année là
		TypeJourFerie typeJour = getTypeJourFerieDao().chercherTypeJourByLibelle("Férié");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<JourFerie> listeExistante = getJourFerieDao().listerJourByAnneeWithType(annee, typeJour.getIdTypeJour());
		for (JourFerie jour : listeExistante) {
			JourFerie newJour = new JourFerie();
			newJour.setIdTypeJour(typeJour.getIdTypeJour());
			newJour.setDescription(jour.getDescription());
			newJour.setDateJour(sdf.parse(Services.ajouteAnnee(sdf.format(jour.getDateJour()), 1)));
			getJourFerieDao().creerJourFerie(newJour.getIdTypeJour(), newJour.getDateJour(), newJour.getDescription());
		}

		// on re initialise l'affichage du tableau
		performPB_ANNULER(request);
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public ArrayList<JourFerie> getListeJourFerie() {
		return listeJourFerie == null ? new ArrayList<JourFerie>() : listeJourFerie;
	}

	public void setListeJourFerie(ArrayList<JourFerie> listeJourFerie) {
		this.listeJourFerie = listeJourFerie;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_JOUR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_JOUR(int i) {
		return "NOM_ST_DATE_JOUR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_JOUR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_JOUR(int i) {
		return getZone(getNOM_ST_DATE_JOUR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_JOUR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_JOUR(int i) {
		return "NOM_ST_TYPE_JOUR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_JOUR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_JOUR(int i) {
		return getZone(getNOM_ST_TYPE_JOUR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DESCRIPTION_JOUR
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DESCRIPTION_JOUR(int i) {
		return "NOM_ST_DESCRIPTION_JOUR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DESCRIPTION_JOUR Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DESCRIPTION_JOUR(int i) {
		return getZone(getNOM_ST_DESCRIPTION_JOUR(i));
	}

	public String getNOM_PB_MODIFIER_JOUR(int i) {
		return "NOM_PB_MODIFIER_JOUR" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_JOUR(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		JourFerie jour = getListeJourFerie().get(indiceEltAModifier);
		setJourFerieCourant(jour);

		TypeJourFerie statut = (TypeJourFerie) getHashTypeJour().get(getJourFerieCourant().getIdTypeJour());
		// Alim zones
		if (statut != null) {
			int ligneStatut = getListeTypeJourFerie().indexOf(statut);
			addZone(getNOM_LB_TYPE_JOUR_SELECT(), String.valueOf(ligneStatut));
		}

		addZone(getNOM_ST_DATE_JOUR(), new SimpleDateFormat("dd/MM/yyyy").format(jour.getDateJour()));
		addZone(getNOM_ST_DESCRIPTION(), jour.getDescription());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		addZone(getNOM_ST_ACTION_JOUR(), ACTION_MODIFICATION_JOUR);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setListeAnnee(null);
		setListeJourFerie(null);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_JOUR(), Const.CHAINE_VIDE);
		setJourFerieCourant(null);
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_DATE_JOUR Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_DATE_JOUR() {
		return "NOM_ST_DATE_JOUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_DATE_JOUR Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_DATE_JOUR() {
		return getZone(getNOM_ST_DATE_JOUR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_DESCRIPTION Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_ST_DESCRIPTION() {
		return "NOM_ST_DESCRIPTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_DESCRIPTION Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_ST_DESCRIPTION() {
		return getZone(getNOM_ST_DESCRIPTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_JOUR Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION_JOUR() {
		return "NOM_ST_ACTION_JOUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_JOUR
	 * Date de création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION_JOUR() {
		return getZone(getNOM_ST_ACTION_JOUR());
	}

	public String getNOM_PB_SUPPRIMER_JOUR(int i) {
		return "NOM_PB_SUPPRIMER_JOUR" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_JOUR(HttpServletRequest request, int indiceEltASupprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		JourFerie jour = getListeJourFerie().get(indiceEltASupprimer);
		setJourFerieCourant(jour);

		TypeJourFerie statut = (TypeJourFerie) getHashTypeJour().get(getJourFerieCourant().getIdTypeJour());
		// Alim zones
		if (statut != null) {
			int ligneStatut = getListeTypeJourFerie().indexOf(statut);
			addZone(getNOM_LB_TYPE_JOUR_SELECT(), String.valueOf(ligneStatut));
		}

		addZone(getNOM_ST_DATE_JOUR(), new SimpleDateFormat("dd/MM/yyyy").format(jour.getDateJour()));
		addZone(getNOM_ST_DESCRIPTION(), jour.getDescription());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		addZone(getNOM_ST_ACTION_JOUR(), ACTION_SUPPRESSION_JOUR);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION_JOUR()).equals(ACTION_SUPPRESSION_JOUR)) {

			if (getJourFerieCourant() == null) {
				// "ERR144", "Impossible de modifier/supprimer le jour."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR144"));
				return false;
			}
			getJourFerieDao().supprimerJourFerie(getJourFerieCourant().getIdJourFerie());
		} else if (getZone(getNOM_ST_ACTION_JOUR()).equals(ACTION_MODIFICATION_JOUR)) {

			if (getJourFerieCourant() == null) {
				// "ERR144", "Impossible de modifier/supprimer le jour."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR144"));
				return false;
			}

			// vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			String dateJour = getVAL_ST_DATE_JOUR();

			// type de jour
			int numType = (Services.estNumerique(getZone(getNOM_LB_TYPE_JOUR_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_TYPE_JOUR_SELECT()))
					: -1);
			TypeJourFerie type = (TypeJourFerie) getListeTypeJourFerie().get(numType);

			getJourFerieCourant().setIdTypeJour(type.getIdTypeJour());
			getJourFerieCourant().setDateJour(new SimpleDateFormat("dd/MM/yyyy").parse(dateJour));
			getJourFerieCourant().setDescription(getVAL_ST_DESCRIPTION());

			getJourFerieDao().modifierJourFerie(getJourFerieCourant().getIdJourFerie(), getJourFerieCourant().getIdTypeJour(),
					getJourFerieCourant().getDateJour(), getJourFerieCourant().getDescription());
		} else if (getZone(getNOM_ST_ACTION_JOUR()).equals(ACTION_CREATION_JOUR)) {
			setJourFerieCourant(new JourFerie());

			// vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			String dateJour = getVAL_ST_DATE_JOUR();

			// type de jour
			int numType = (Services.estNumerique(getZone(getNOM_LB_TYPE_JOUR_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_TYPE_JOUR_SELECT()))
					: -1);
			TypeJourFerie type = (TypeJourFerie) getListeTypeJourFerie().get(numType);

			getJourFerieCourant().setIdTypeJour(type.getIdTypeJour());
			getJourFerieCourant().setDateJour(new SimpleDateFormat("dd/MM/yyyy").parse(dateJour));
			getJourFerieCourant().setDescription(getVAL_ST_DESCRIPTION());

			getJourFerieDao().creerJourFerie(getJourFerieCourant().getIdTypeJour(), getJourFerieCourant().getDateJour(),
					getJourFerieCourant().getDescription());

		}
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_JOUR(), Const.CHAINE_VIDE);
		setJourFerieCourant(null);
		setListeJourFerie(null);
		return true;
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		// date de debut obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_DATE_JOUR())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date du jour"));
			return false;
		}

		// format date de debut
		if (!Services.estUneDate(getVAL_ST_DATE_JOUR())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "du jour"));
			return false;
		}
		return true;
	}

	public JourFerie getJourFerieCourant() {
		return jourFerieCourant;
	}

	public void setJourFerieCourant(JourFerie jourFerieCourant) {
		this.jourFerieCourant = jourFerieCourant;
	}

	private Hashtable<Integer, TypeJourFerie> getHashTypeJour() {
		if (hashTypeJour == null)
			hashTypeJour = new Hashtable<Integer, TypeJourFerie>();
		return hashTypeJour;
	}

	public TypeJourFerieDao getTypeJourFerieDao() {
		return typeJourFerieDao;
	}

	public void setTypeJourFerieDao(TypeJourFerieDao typeJourFerieDao) {
		this.typeJourFerieDao = typeJourFerieDao;
	}

	public ArrayList<TypeJourFerie> getListeTypeJourFerie() {
		return listeTypeJourFerie;
	}

	public void setListeTypeJourFerie(ArrayList<TypeJourFerie> listeTypeJourFerie) {
		this.listeTypeJourFerie = listeTypeJourFerie;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_JOUR Date de
	 * création : (05/09/11 14:28:45)
	 * 
	 */
	private String[] getLB_TYPE_JOUR() {
		if (LB_TYPE_JOUR == null)
			LB_TYPE_JOUR = initialiseLazyLB();
		return LB_TYPE_JOUR;
	}

	/**
	 * Setter de la liste: LB_TYPE_JOUR Date de création : (05/09/11 14:28:45)
	 * 
	 */
	private void setLB_TYPE_JOUR(String[] newLB_TYPE_JOUR) {
		LB_TYPE_JOUR = newLB_TYPE_JOUR;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_JOUR Date de
	 * création : (05/09/11 14:28:45)
	 * 
	 */
	public String getNOM_LB_TYPE_JOUR() {
		return "NOM_LB_TYPE_JOUR";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_JOUR_SELECT Date de création : (05/09/11 14:28:45)
	 * 
	 */
	public String getNOM_LB_TYPE_JOUR_SELECT() {
		return "NOM_LB_TYPE_JOUR_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_JOUR Date de création : (05/09/11 14:28:45)
	 * 
	 */
	public String[] getVAL_LB_TYPE_JOUR() {
		return getLB_TYPE_JOUR();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_JOUR Date de création : (05/09/11 14:28:45)
	 * 
	 */
	public String getVAL_LB_TYPE_JOUR_SELECT() {
		return getZone(getNOM_LB_TYPE_JOUR_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_JOUR Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER_JOUR() {
		return "NOM_PB_AJOUTER_JOUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AJOUTER_JOUR(HttpServletRequest request) throws Exception {
		viderZoneSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION_JOUR(), ACTION_CREATION_JOUR);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void viderZoneSaisie() {
		addZone(getNOM_LB_TYPE_JOUR_SELECT(), Const.ZERO);
		addZone(getNOM_ST_DATE_JOUR(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DESCRIPTION(), Const.CHAINE_VIDE);
		setJourFerieCourant(null);

	}
}
