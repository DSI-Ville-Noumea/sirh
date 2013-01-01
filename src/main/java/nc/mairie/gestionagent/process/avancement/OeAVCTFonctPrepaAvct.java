package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumCategorieAgent;
import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.metier.Const;
import nc.mairie.metier.avancement.Avancement;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.utils.ApplicationContextProvider;
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
public class OeAVCTFonctPrepaAvct extends nc.mairie.technique.BasicProcess {

	private static Logger logger = Logger.getLogger(OeAVCTFonctPrepaAvct.class.getName());

	private String[] LB_ANNEE;

	private String[] listeAnnee;
	private String anneeSelect;

	private ArrayList<Avancement> listeAvct;

	public String agentEnErreur = Const.CHAINE_VIDE;

	private EAEDao eaeDao;

	public EAEDao getEAEDao() {
		return eaeDao;
	}

	public void setEAEDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	private CampagneEAEDao campagneEAEDao;

	public CampagneEAEDao getCampagneEAEDao() {
		return campagneEAEDao;
	}

	public void setCampagneEAEDao(CampagneEAEDao campagneEAEDao) {
		this.campagneEAEDao = campagneEAEDao;
	}

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

		initialiseDao();

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// Si liste avancements vide alors initialisation.
		if (getListeAvct() == null || getListeAvct().size() == 0) {
			agentEnErreur = Const.CHAINE_VIDE;
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			String annee = (String) getListeAnnee()[indiceAnnee];
			String reqEtat = " and (ETAT='" + EnumEtatAvancement.TRAVAIL.getValue() + "' or ETAT='" + EnumEtatAvancement.SGC.getValue() + "')";
			setListeAvct(Avancement.listerAvancementAvecCategorieAnneeEtat(getTransaction(), EnumCategorieAgent.FONCTIONNAIRE.getLibLong(), annee,
					reqEtat));

			for (int i = 0; i < getListeAvct().size(); i++) {
				Avancement av = (Avancement) getListeAvct().get(i);

				addZone(getNOM_ST_AGENT(i), av.getNomAgent() + " <br> " + av.getPrenomAgent() + " <br> " + av.getMatrAgent());
				addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
				addZone(getNOM_ST_CATEGORIE(i), (av.getCodeCadre() == null ? "&nbsp;" : av.getCodeCadre()) + " <br> " + av.getFiliere());
				addZone(getNOM_ST_DATE_DEBUT(i), av.getDateGrade());
				addZone(getNOM_ST_IBA(i),
						(av.getIba() == null ? "&nbsp;" : av.getIba()) + " <br> " + (av.getNouvIBA() == null ? "&nbsp;" : av.getNouvIBA()));
				addZone(getNOM_ST_INM(i),
						(av.getInm() == null ? "&nbsp;" : av.getInm()) + " <br> " + (av.getNouvINM() == null ? "&nbsp;" : av.getNouvINM()));
				addZone(getNOM_ST_INA(i),
						(av.getIna() == null ? "&nbsp;" : av.getIna()) + " <br> " + (av.getNouvINA() == null ? "&nbsp;" : av.getNouvINA()));
				addZone(getNOM_ST_BM_A(i), av.getBMAnnee() + " <br> " + av.getNouvBMAnnee());
				addZone(getNOM_ST_BM_M(i), av.getBMMois() + " <br> " + av.getNouvBMMois());
				addZone(getNOM_ST_BM_J(i), av.getBMJour() + " <br> " + av.getNouvBMJour());
				addZone(getNOM_ST_ACC_A(i), av.getACCAnnee() + " <br> " + av.getNouvACCAnnee());
				addZone(getNOM_ST_ACC_M(i), av.getACCMois() + " <br> " + av.getNouvACCMois());
				addZone(getNOM_ST_ACC_J(i), av.getACCJour() + " <br> " + av.getNouvACCJour());
				addZone(getNOM_ST_GRADE(i),
						av.getGrade() + " <br> "
								+ (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
				String libGrade = av.getLibelleGrade().equals(Const.CHAINE_VIDE) ? "&nbsp;" : av.getLibelleGrade();
				String libNouvGrade = av.getLibNouvGrade().equals(Const.CHAINE_VIDE) ? "&nbsp;" : av.getLibNouvGrade();
				addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

				addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct());
				addZone(getNOM_ST_PERIODE_STD(i), av.getDureeStandard());
				addZone(getNOM_ST_DATE_AVCT(i), (av.getDateAvctMini() == null ? "&nbsp;" : av.getDateAvctMini()) + " <br> " + av.getDateAvctMoy()
						+ " <br> " + (av.getDateAvctMaxi() == null ? "&nbsp;" : av.getDateAvctMaxi()));

				addZone(getNOM_CK_VALID_SGC(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());

				addZone(getNOM_ST_ETAT(i), av.getEtat());
				addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
				String user = av.getUserVerifSGC() == null ? "&nbsp;" : av.getUserVerifSGC();
				String heure = av.getHeureVerifSGC() == null ? "&nbsp;" : av.getHeureVerifSGC();
				String date = av.getDateVerifSGC() == null ? "&nbsp;" : av.getDateVerifSGC();
				addZone(getNOM_ST_USER_VALID_SGC(i), user + " <br> " + date + " <br> " + heure);

			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getEAEDao() == null) {
			setEAEDao((EAEDao) context.getBean("eaeDao"));
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
	public OeAVCTFonctPrepaAvct() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctPrepaAvct.jsp";
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
			if (getVAL_CK_VALID_SGC(i).equals(getCHECKED_ON())) {
				// si la ligne est cochée
				// on regarde si l'etat est deja SGC
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat à SGC et on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSGC(user.getUserName());
					avct.setDateVerifSGC(dateJour);
					avct.setHeureVerifSGC(heureAction);
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
					avct.modifierAvancement(getTransaction());
				}
				// RG-EAE-25
				// on regarde si il y a une campagne pour l'année en cours de
				// l'avancement
				try {
					CampagneEAE campagne = getCampagneEAEDao().chercherCampagneEAEAnnee(Integer.valueOf(avct.getAnnee()));
					// on regarde si il y a une ligne dans EAE
					try {
						EAE eaeAgentAnne = getEAEDao().chercherEAEAgent(Integer.valueOf(avct.getIdAgent()), campagne.getIdCampagneEAE());
						if (!eaeAgentAnne.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())) {
							// si oui alors on flag CAP à true;
							getEAEDao().modifierCAP(eaeAgentAnne.getIdEAE(), true);
						}
					} catch (Exception e) {
						// il n'y a pas de ligne dans EAE
						logger.info("Erreur dans la recherche de l'eae : " + e);
					}
				} catch (Exception e) {
					// il n'y a pas de campagne pour l'année d'avancement
					logger.info("Erreur dans la recherche de la campagne : " + e);
				}
			} else {
				// si la ligne n'est pas cochée
				// on regarde quel etat son etat
				// --> si SGC alors on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSGC(user.getUserName());
					avct.setDateVerifSGC(dateJour);
					avct.setHeureVerifSGC(heureAction);
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
					avct.modifierAvancement(getTransaction());
				}
				// RG-EAE-25
				// on regarde si il y a une campagne pour l'année en cours de
				// l'avancement
				try {
					CampagneEAE campagne = getCampagneEAEDao().chercherCampagneEAEAnnee(Integer.valueOf(avct.getAnnee()));
					// on regarde si il y a une ligne dans EAE
					try {
						EAE eaeAgentAnne = getEAEDao().chercherEAEAgent(Integer.valueOf(avct.getIdAgent()), campagne.getIdCampagneEAE());
						if (!eaeAgentAnne.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())) {
							// si oui alors on flag CAP à true;
							getEAEDao().modifierCAP(eaeAgentAnne.getIdEAE(), false);
						}
					} catch (Exception e) {
						// il n'y a pas de ligne dans EAE
					}
				} catch (Exception e) {
					// il n'y a pas de campagne pour l'année d'avancement
				}
			}
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
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_A(int i) {
		return "NOM_ST_ACC_A_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACC_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_A(int i) {
		return getZone(getNOM_ST_ACC_A(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_J(int i) {
		return "NOM_ST_ACC_J_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACC_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_J(int i) {
		return getZone(getNOM_ST_ACC_J(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_M(int i) {
		return "NOM_ST_ACC_M_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACC_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_M(int i) {
		return getZone(getNOM_ST_ACC_M(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_A(int i) {
		return "NOM_ST_BM_A_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BM_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_A(int i) {
		return getZone(getNOM_ST_BM_A(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_J(int i) {
		return "NOM_ST_BM_J_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BM_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_J(int i) {
		return getZone(getNOM_ST_BM_J(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_M(int i) {
		return "NOM_ST_BM_M_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BM_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_M(int i) {
		return getZone(getNOM_ST_BM_M(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_IBA Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_IBA(int i) {
		return "NOM_ST_IBA_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_IBA Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_IBA(int i) {
		return getZone(getNOM_ST_IBA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INM Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_INM(int i) {
		return "NOM_ST_INM_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INM Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_INM(int i) {
		return getZone(getNOM_ST_INM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INA Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_INA(int i) {
		return "NOM_ST_INA_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INA Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_INA(int i) {
		return getZone(getNOM_ST_INA(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_PERIODE_STD Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_PERIODE_STD(int i) {
		return "NOM_ST_PERIODE_STD_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PERIODE_STD
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_PERIODE_STD(int i) {
		return getZone(getNOM_ST_PERIODE_STD(i));
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
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_SGC Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_SGC(int i) {
		return "NOM_CK_VALID_SGC_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_SGC(int i) {
		return getZone(getNOM_CK_VALID_SGC(i));
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<Avancement> getListeAvct() {
		if (listeAvct == null) {
			return new ArrayList<Avancement>();
		}
		return listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<Avancement> listeAvct) {
		this.listeAvct = listeAvct;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-FONCT-PREPA-AVCT";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_VALID_SGC Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_VALID_SGC(int i) {
		return "NOM_ST_USER_VALID_SGC_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_USER_VALID_SGC
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_SGC(int i) {
		return getZone(getNOM_ST_USER_VALID_SGC(i));
	}
}