package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumCategorieAgent;
import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.metier.Const;
import nc.mairie.metier.avancement.Avancement;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctPrepaCAP extends nc.mairie.technique.BasicProcess {
	private String[] LB_ANNEE;
	private String[] LB_AVIS_CAP;

	private Hashtable<String, AvisCap> hashAvisCAP;

	private String[] listeAnnee;
	private String anneeSelect;

	private ArrayList listeAvct;
	private ArrayList listeAvisCAP;

	public String agentEnErreur = Const.CHAINE_VIDE;

	private EAEDao eaeDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private CampagneEAEDao campagneEAEDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
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

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		initialiseDao();

		// Si liste avancements vide alors initialisation.
		if (getListeAvct() == null || getListeAvct().size() == 0) {
			agentEnErreur = Const.CHAINE_VIDE;
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			String annee = (String) getListeAnnee()[indiceAnnee];
			String reqEtat = " and (ETAT='" + EnumEtatAvancement.SGC.getValue() + "' or ETAT='" + EnumEtatAvancement.SEF.getValue() + "')";
			setListeAvct(Avancement.listerAvancementAvecCategorieAnneeEtat(getTransaction(), EnumCategorieAgent.FONCTIONNAIRE.getLibLong(), annee,
					reqEtat));

			for (int i = 0; i < getListeAvct().size(); i++) {
				Avancement av = (Avancement) getListeAvct().get(i);

				addZone(getNOM_ST_AGENT(i), av.getNomAgent() + " <br> " + av.getPrenomAgent() + " <br> " + av.getMatrAgent());
				addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
				addZone(getNOM_ST_CATEGORIE(i), (av.getCodeCadre() == null ? "&nbsp;" : av.getCodeCadre()) + " <br> " + av.getFiliere());
				addZone(getNOM_ST_DATE_DEBUT(i), av.getDateGrade());
				addZone(getNOM_ST_GRADE(i),
						av.getGrade() + " <br> "
								+ (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
				String libGrade = av.getLibelleGrade() == null ? "&nbsp;" : av.getLibelleGrade();
				String libNouvGrade = av.getLibNouvGrade() == null ? "&nbsp;" : av.getLibNouvGrade();
				addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

				addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct());
				addZone(getNOM_ST_DATE_AVCT(i), (av.getDateAvctMini() == null ? "&nbsp;" : av.getDateAvctMini()) + " <br> " + av.getDateAvctMoy()
						+ " <br> " + (av.getDateAvctMaxi() == null ? "&nbsp;" : av.getDateAvctMaxi()));


				addZone(getNOM_CK_VALID_SEF(i), av.getEtat().equals(EnumEtatAvancement.SEF.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());
				addZone(getNOM_ST_ETAT(i), av.getEtat());
				addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
				String user = av.getUserVerifSEF() == null ? "&nbsp;" : av.getUserVerifSEF();
				String heure = av.getHeureVerifSEF() == null ? "&nbsp;" : av.getHeureVerifSEF();
				String date = av.getDateVerifSEF() == null ? "&nbsp;" : av.getDateVerifSEF();
				addZone(getNOM_ST_USER_VALID_SEF(i), user + " <br> " + date + " <br> " + heure);
				addZone(getNOM_EF_ORDRE_MERITE(i), av.getOrdreMerite().equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : av.getOrdreMerite());

				// avis SHD
				// on cherche la camapagne correspondante
				String avisSHD = "";
				MotifAvancement motif = null;
				try {
					CampagneEAE campagneEAE = getCampagneEAEDao().chercherCampagneEAEAnnee(Integer.valueOf(annee));
					// on cherche l'eae correspondant ainsi que l'eae evaluation
					EAE eaeAgent = getEaeDao().chercherEAEAgent(Integer.valueOf(av.getIdAgent()), campagneEAE.getIdCampagneEAE());
					if (eaeAgent.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) || eaeAgent.getEtat().equals(EnumEtatEAE.FINALISE.getCode())) {
						EaeEvaluation eaeEvaluation = getEaeEvaluationDao().chercherEaeEvaluation(eaeAgent.getIdEAE());
						if (av.getIdMotifAvct().equals("7")) {
							avisSHD = eaeEvaluation.getPropositionAvancement() == null ? "" : eaeEvaluation.getPropositionAvancement();
						}
						// motif Avct
						if (av.getIdMotifAvct() != null) {
							motif = MotifAvancement.chercherMotifAvancement(getTransaction(), av.getIdMotifAvct());
						}
					}
				} catch (Exception e) {
					// pas de campagne pour cette année
				}
				addZone(getNOM_ST_MOTIF_AVCT(i), (motif == null ? Const.CHAINE_VIDE : motif.getCodeMotifAvct()) + "<br/>" + avisSHD);

				// duree VDN
				if (av.getIdAvisCAP() == null) {
					if (!avisSHD.equals("")) {
						if (avisSHD.equals("MAXI")) {
							addZone(getNOM_LB_AVIS_CAP_SELECT(i), String.valueOf(getListeAvisCAP().indexOf(getHashAvisCAP().get("3"))));
						} else if (avisSHD.equals("MOY")) {
							addZone(getNOM_LB_AVIS_CAP_SELECT(i), String.valueOf(getListeAvisCAP().indexOf(getHashAvisCAP().get("2"))));
						} else if (avisSHD.equals("MINI")) {
							addZone(getNOM_LB_AVIS_CAP_SELECT(i), String.valueOf(getListeAvisCAP().indexOf(getHashAvisCAP().get("1"))));
						}
					} else {
						addZone(getNOM_LB_AVIS_CAP_SELECT(i), String.valueOf(getListeAvisCAP().indexOf(getHashAvisCAP().get("2"))));
					}

				} else {
					addZone(getNOM_LB_AVIS_CAP_SELECT(i),
							av.getIdAvisCAP() == null || av.getIdAvisCAP().length() == 0 ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAP()
									.indexOf(getHashAvisCAP().get(av.getIdAvisCAP()))));
				}

			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getEaeDao() == null) {
			setEaeDao((EAEDao) context.getBean("eaeDao"));
		}

		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao((EaeEvaluationDao) context.getBean("eaeEvaluationDao"));
		}

		if (getCampagneEAEDao() == null) {
			setCampagneEAEDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}
	}

	/**
	 * Initialisation des liste déroulantes de l'écran Avancement des
	 * fonctionnaires.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT);
			if (anneeCourante == null || anneeCourante.length() == 0)
				anneeCourante = Services.dateDuJour().substring(6, 10);
			setListeAnnee(new String[5]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			// TODO
			// changement de l'année pour faire au mieux.
			// getListeAnnee()[0] =
			// String.valueOf(Integer.parseInt(anneeCourante) + 1);
			getListeAnnee()[1] = String.valueOf(Integer.parseInt(anneeCourante) + 2);
			getListeAnnee()[2] = String.valueOf(Integer.parseInt(anneeCourante) + 3);
			getListeAnnee()[3] = String.valueOf(Integer.parseInt(anneeCourante) + 4);
			getListeAnnee()[4] = String.valueOf(Integer.parseInt(anneeCourante) + 5);
			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
			setAnneeSelect(String.valueOf(Integer.parseInt(anneeCourante) + 1));
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAP() == null || getListeAvisCAP().size() == 0) {
			ArrayList avis = AvisCap.listerAvisCap(getTransaction());
			setListeAvisCAP(avis);

			int[] tailles = { 7 };
			String[] champs = { "libLongAvisCAP" };
			setLB_AVIS_CAP(new FormateListe(tailles, avis, champs).getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAP().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAP().get(i);
				getHashAvisCAP().put(ac.getIdAvisCAP(), ac);
			}
		}
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CHANGER_ANNEE
			if (testerParametre(request, getNOM_PB_CHANGER_ANNEE())) {
				return performPB_CHANGER_ANNEE(request);
			}

			// Si clic sur le bouton PB_IMPRIMER
			if (testerParametre(request, getNOM_PB_IMPRIMER())) {
				return performPB_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTFonctPrepaCAP() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctPrepaCAP.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_FILTRER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_CHANGER_ANNEE() {
		return "NOM_PB_CHANGER_ANNEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CHANGER_ANNEE(HttpServletRequest request) throws Exception {
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		if (!annee.equals(getAnneeSelect())) {
			setListeAvct(null);
			setAnneeSelect(annee);
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		String dateJour = Services.dateDuJour();
		// on sauvegarde l'état du tableau
		for (int i = 0; i < getListeAvct().size(); i++) {
			// on recupère la ligne concernée
			Avancement avct = (Avancement) getListeAvct().get(i);
			// on fait les modifications
			// on traite l'etat
			if (getVAL_CK_VALID_SEF(i).equals(getCHECKED_ON())) {
				// si la ligne est cochée
				// on regarde si l'etat est deja SEF
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat à SEF et on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSEF(user.getUserName());
					avct.setDateVerifSEF(dateJour);
					avct.setHeureVerifSEF(heureAction);
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				}
			} else {
				// si la ligne n'est pas cochée
				// on regarde quel etat son etat
				// --> si SEF alors on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSEF(user.getUserName());
					avct.setDateVerifSEF(dateJour);
					avct.setHeureVerifSEF(heureAction);
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				}
			}
			// on traite l'avis CAP
			int indiceAvisCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_SELECT(i)) ? Integer.parseInt(getVAL_LB_AVIS_CAP_SELECT(i)) : -1);
			if (indiceAvisCap != -1) {
				String idAvisCap = ((AvisCap) getListeAvisCAP().get(indiceAvisCap)).getIdAvisCAP();
				avct.setIdAvisCAP(idAvisCap);
			}
			// on traite l'odre de merite
			String ordre = getVAL_EF_ORDRE_MERITE(i);
			if (!ordre.equals(Const.CHAINE_VIDE)) {
				avct.setOrdreMerite(ordre);
			} else {
				avct.setOrdreMerite(null);
			}
			avct.modifierAvancement(getTransaction());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		// on remet la liste à vide afin qu'elle soit de nouveau initialisée
		setListeAvct(null);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CATEGORIE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CATEGORIE(int i) {
		return "NOM_ST_CATEGORIE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CATEGORIE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CATEGORIE(int i) {
		return getZone(getNOM_ST_CATEGORIE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_AVCT(int i) {
		return "NOM_ST_DATE_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_AVCT(int i) {
		return getZone(getNOM_ST_DATE_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_LIB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE_LIB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE(int i) {
		return "NOM_ST_GRADE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE(int i) {
		return getZone(getNOM_ST_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_AVCT(int i) {
		return "NOM_ST_NUM_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_AVCT(int i) {
		return getZone(getNOM_ST_NUM_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList getListeAvct() {
		return listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList listeAvct) {
		this.listeAvct = listeAvct;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-FONCT-PREPA-CAP";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ANNEE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Getter de la liste des années possibles.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des années possibles.
	 * 
	 * @param listeAnnee
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	/**
	 * Getter de l'annee sélectionnée.
	 * 
	 * @return anneeSelect
	 */
	public String getAnneeSelect() {
		return anneeSelect;
	}

	/**
	 * Setter de l'année sélectionnée
	 * 
	 * @param newAnneeSelect
	 */
	public void setAnneeSelect(String newAnneeSelect) {
		this.anneeSelect = newAnneeSelect;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_SEF Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_SEF(int i) {
		return "NOM_CK_VALID_SEF_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_VALID_SEF Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_SEF(int i) {
		return getZone(getNOM_CK_VALID_SEF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_VALID_SEF Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_VALID_SEF(int i) {
		return "NOM_ST_USER_VALID_SEF_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_USER_VALID_SEF
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_SEF(int i) {
		return getZone(getNOM_ST_USER_VALID_SEF(i));
	}

	/**
	 * Getter de la liste des avis CAP.
	 * 
	 * @return listeAvisCAP
	 */
	private ArrayList getListeAvisCAP() {
		return listeAvisCAP;
	}

	/**
	 * Setter de la liste des avis CAP.
	 * 
	 * @param listeAvisCAP
	 */
	private void setListeAvisCAP(ArrayList listeAvisCAP) {
		this.listeAvisCAP = listeAvisCAP;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP(int i) {
		if (LB_AVIS_CAP == null)
			LB_AVIS_CAP = initialiseLazyLB();
		return LB_AVIS_CAP;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP(String[] newLB_AVIS_CAP) {
		LB_AVIS_CAP = newLB_AVIS_CAP;
	}

	/**
	 * Getter de la HashTable AvisCAP.
	 * 
	 * @return Hashtable<String, AvisCap>
	 */
	private Hashtable<String, AvisCap> getHashAvisCAP() {
		if (hashAvisCAP == null)
			hashAvisCAP = new Hashtable<String, AvisCap>();
		return hashAvisCAP;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVIS_CAP_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_CAP_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_CAP_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP(int i) {
		return "NOM_LB_AVIS_CAP_" + i;
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP(int i) {
		return getLB_AVIS_CAP(i);
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ORDRE_MERITE Date
	 * de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_ORDRE_MERITE(int i) {
		return "NOM_EF_ORDRE_MERITE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ORDRE_MERITE Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_ORDRE_MERITE(int i) {
		return getZone(getNOM_EF_ORDRE_MERITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF_AVCT(int i) {
		return "NOM_ST_MOTIF_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF_AVCT(int i) {
		return getZone(getNOM_ST_MOTIF_AVCT(i));
	}

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
	}

	public CampagneEAEDao getCampagneEAEDao() {
		return campagneEAEDao;
	}

	public void setCampagneEAEDao(CampagneEAEDao campagneEAEDao) {
		this.campagneEAEDao = campagneEAEDao;
	}
}