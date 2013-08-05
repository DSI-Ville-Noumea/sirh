package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.agent.PrimeAgent;
import nc.mairie.metier.paye.Matricule;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Process OeAGENTPrime Date de création : (05/08/11 10:06:07)
 * 
 */
public class OeAGENTPrime extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private ArrayList<Prime> listePrimes;
	private ArrayList<Rubrique> listeRubriques;
	private ArrayList<Rubrique> listeRubriquesTotales;

	private Hashtable<String, Rubrique> hashRubriques;

	private AgentNW agentCourant;
	private Prime primeCourante;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche Prime.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche Prime.";
	private String ACTION_MODIFICATION = "Modification d'une fiche Prime.";
	private String ACTION_CREATION = "Création d'une fiche Prime.";

	private static QSYSObjectPathName CALC_PATH = new QSYSObjectPathName((String) ServletAgent.getMesParametres().get("DTAARA_SCHEMA"),
			(String) ServletAgent.getMesParametres().get("DTAARA_NAME"), "DTAARA");
	public static CharacterDataArea DTAARA_CALC = new CharacterDataArea(new AS400((String) ServletAgent.getMesParametres().get("HOST_SGBD_PAYE"),
			(String) ServletAgent.getMesParametres().get("HOST_SGBD_ADMIN"), (String) ServletAgent.getMesParametres().get("HOST_SGBD_PWD")),
			CALC_PATH.getPath());
	private String calculPaye;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (05/08/11 10:06:07)
	 * 
	 * RG_AG_PR_C01 RG_AG_PR_C04 RG_AG_PR_C02
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

		// SI CALCUL PAYE EN COURS
		String percou = DTAARA_CALC.read().toString();
		if (!percou.trim().equals(Const.CHAINE_VIDE)) {
			setCalculPaye(percou);
		} else {
			setCalculPaye(Const.CHAINE_VIDE);
		}

		// Si hashtable des rubriques vide
		// RG_AG_PR_C01
		// RG_AG_PR_C04
		// RG_AG_PR_C02

		if (getHashRubriques().size() == 0) {
			ArrayList<Rubrique> listeRubTot = Rubrique.listerRubriqueAvecTypeRubrAvecInactives(getTransaction(), "P");
			setListeRubriquesTotales(listeRubTot);

			ArrayList<Rubrique> listeRubrique = Rubrique.listerRubriqueAvecTypeRubr(getTransaction(), "P");
			// remplissage de la hashTable
			setListeRubriques(new ArrayList<Rubrique>());
			for (Rubrique r : listeRubrique) {
				// RG_AG_PR_C04
				if (!r.typePrime.equals("I") && !r.typePrime.equals("J")) {
					getListeRubriques().add(r);
					getHashRubriques().put(r.getNumRubrique(), r);
				}
			}
		}

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListePrimes(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

	}

	/**
	 * fonction qui permet de savoir si une paye est en cours.
	 * 
	 * @return calculPaye
	 */
	public String getCalculPaye() {
		return calculPaye;
	}

	private void setCalculPaye(String calculPaye) {
		this.calculPaye = calculPaye;
	}

	/**
	 * Initialisation de la liste des primes de l'agent courant Date de création
	 * : (04/08/11)
	 * 
	 */
	private void initialiseListePrimes(HttpServletRequest request) throws Exception {
		// Recherche des primes de l'agent
		ArrayList<Prime> listePrimes = Prime.listerPrimeAvecAgent(getTransaction(), getAgentCourant());
		setListePrimes(listePrimes);

		int indicePrime = 0;
		if (getListePrimes() != null) {
			for (int i = 0; i < getListePrimes().size(); i++) {
				Prime p = (Prime) getListePrimes().get(i);
				Rubrique r = Rubrique.chercherRubrique(getTransaction(), p.getNoRubr());

				addZone(getNOM_ST_CODE_RUBR(indicePrime), r == null || r.getNumRubrique().equals(Const.CHAINE_VIDE) ? "&nbsp;" : r.getNumRubrique());
				addZone(getNOM_ST_LIB_RUBR(indicePrime), r == null || r.getLibRubrique().equals(Const.CHAINE_VIDE) ? "&nbsp;" : r.getLibRubrique());
				addZone(getNOM_ST_REF_ARR(indicePrime), p.getRefArr().equals(Const.CHAINE_VIDE) ? "&nbsp;" : p.getRefArr());
				addZone(getNOM_ST_DATE_ARR(indicePrime),
						p.getDateArrete() == null || p.getDateArrete().equals(Const.DATE_NULL) ? "&nbsp;" : p.getDateArrete());
				addZone(getNOM_ST_MONTANT(indicePrime), p.getMtPri().equals(Const.CHAINE_VIDE) ? "&nbsp;" : p.getMtPri());
				addZone(getNOM_ST_DATE_DEBUT(indicePrime), p.getDatDeb());
				addZone(getNOM_ST_DATE_FIN(indicePrime), p.getDatFin() == null || p.getDatFin().equals(Const.DATE_NULL) ? "&nbsp;" : p.getDatFin());

				indicePrime++;
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTPrime. Date de création : (05/08/11
	 * 10:06:07)
	 * 
	 */
	public OeAGENTPrime() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de la visite courante
		setPrimeCourante(new Prime());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'une prime
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_EF_DATE_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MONTANT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RUBRIQUE(), Const.CHAINE_VIDE);
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'une prime
	 * Date de création : 11/08/05
	 * 
	 */
	private boolean initialisePrimeCourante(HttpServletRequest request) throws Exception {
		Rubrique r = Rubrique.chercherRubrique(getTransaction(), getPrimeCourante().getNoRubr());

		// Alim zones
		addZone(getNOM_ST_RUBRIQUE(), r.getLibRubrique());
		addZone(getNOM_EF_RUBRIQUE(), r.getNumRubrique() + " " + r.getLibRubrique());

		addZone(getNOM_EF_REF_ARR(), getPrimeCourante().getRefArr());
		addZone(getNOM_EF_MONTANT(), getPrimeCourante().getMtPri());
		addZone(getNOM_EF_DATE_DEBUT(), getPrimeCourante().getDatDeb());
		addZone(getNOM_EF_DATE_FIN(), getPrimeCourante().getDatFin());
		addZone(getNOM_EF_DATE_ARR(), getPrimeCourante().getDateArrete());

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_PR_A02 RG_AG_PR_C03
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// RG_AG_PR_A02

		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_RUBRIQUE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "rubrique"));
			return false;
		}

		// montant obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_MONTANT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "montant"));
			return false;
		}

		// reference arrete
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_REF_ARR())) && !Services.estNumerique(getZone(getNOM_EF_REF_ARR()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Ref. arrêté"));
			return false;
		}

		// date de l'arrêté en format date
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_ARR())) && !Services.estUneDate(getZone(getNOM_EF_DATE_ARR()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de l'arrêté"));
			return false;
		}

		// montant numerique
		if (!Services.estNumerique(getZone(getNOM_EF_MONTANT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "montant"));
			return false;
		}

		// date de début de la prime
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
			return false;
		}

		// date de fin format date
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN())) && !Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin"));
			return false;
		}

		// date de fin > date de début
		// RG_AG_PR_C03
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN()))
				&& Services.compareDates(getZone(getNOM_EF_DATE_FIN()), getZone(getNOM_EF_DATE_DEBUT())) <= 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "de début"));
			return false;
		}

		return true;
	}

	/**
	 * Verifie la regle de gestion du chevauchement des primes
	 * 
	 * @return RG_AG_PR_A03 RG_AG_PR_C06 RG_AG_PR_C05
	 */
	public boolean checkGestion(Rubrique rubrique) throws Exception {
		// RG_AG_PR_A03
		// RG_AG_PR_C06
		// verification des regles de gestions
		ArrayList<Prime> listePrime = getListePrimes();
		for (int i = 0; i < listePrime.size(); i++) {
			Prime p = (Prime) listePrime.get(i);

			if (p != getPrimeCourante() && p.getNoRubr().equals(rubrique.getNumRubrique())) {

				if (Services.compareDates(p.getDatDeb(), getPrimeCourante().getDatDeb()) >= 0) {
					// dateDebCur < dateDeb
					if (getPrimeCourante().getDatFin() == null || getPrimeCourante().getDatFin().equals(Const.CHAINE_VIDE)) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR100"));
						return false;
					} else if (Services.compareDates(getPrimeCourante().getDatFin(), p.getDatDeb()) > 0) {
						// dateFinCur >= dateDeb
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR100"));
						return false;
					}
				} else {
					// dateDebCur >= dateDEb
					if (p.getDatFin() == null || p.getDatFin().equals(Const.CHAINE_VIDE)) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR060"));
						return false;
					} else if (Services.compareDates(p.getDatFin(), getPrimeCourante().getDatDeb()) > 0) {
						// dateFin >= dateDebCur
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR060"));
						return false;
					}
				}
			}
		}

		// verification des date pour type de prime = 'M' ou 'E'
		// RG_AG_PR_C05
		if (rubrique.typePrime.equals("M") || rubrique.typePrime.equals("E")) {
			if (!getPrimeCourante().getDatDeb().substring(0, 2).equals("01")) {
				// Le jour de la date de début doit etre égal au premier jour du
				// mois
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR061"));
				return false;
			}

			// date de fin format date
			if ((Const.CHAINE_VIDE).equals(getPrimeCourante().getDatFin())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
				return false;
			}
			if (getPrimeCourante().getDatFin() != null && !getPrimeCourante().getDatFin().equals(Const.CHAINE_VIDE)
					&& !getPrimeCourante().getDatFin().substring(0, 2).equals("01")) {
				// Le jour de la date de fin doit etre égal au premier jour du
				// mois
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR062"));
				return false;
			}
			/*
			 * if (getPrimeCourante().getDatFin() != null &&
			 * Services.ajouteJours(getPrimeCourante().getDatFin(), 1) != null
			 * && !Services.ajouteJours(getPrimeCourante().getDatFin(),
			 * 1).substring(0, 2).equals("01")) { //Le jour de la date de fin
			 * doit etre égal au dernier jour du mois
			 * getTransaction().declarerErreur
			 * (MessageUtils.getMessage("ERR062")); return false; }
			 */
		}

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 10:06:07)
	 * 
	 * RG_AG_PR_A01
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

			// Suppression du lien
			PrimeAgent primeAgent = PrimeAgent.chercherPrimeAgent(getTransaction(), getAgentCourant().idAgent, getAgentCourant().getNoMatricule(),
					getPrimeCourante().getNoRubr(), getPrimeCourante().getDatDeb());
			primeAgent.supprimerPrimeAgent(getTransaction());
			if (getTransaction().isErreur())
				return false;

			// suppression
			UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
			getPrimeCourante().supprimerPrime(getTransaction(), user);
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// vérification de la validité du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			// récupération des informations remplies dans les zones de saisie
			String refArr = getZone(getNOM_EF_REF_ARR());
			String dateArr = getZone(getNOM_EF_DATE_ARR());
			String montant = getZone(getNOM_EF_MONTANT());
			String dateFin = getZone(getNOM_EF_DATE_FIN());
			String dateDebut = getZone(getNOM_EF_DATE_DEBUT());

			Rubrique r = getSelectedRubrique();
			if (r == null) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "rubriques"));
				return false;
			}
			// Création de l'objet prime à créer/modifier
			AgentNW agentCourant = getAgentCourant();
			getPrimeCourante().setNoMatr(agentCourant.getNoMatricule());
			getPrimeCourante().setNoRubr(r.getNumRubrique());
			getPrimeCourante().setRefArr(refArr.equals(Const.CHAINE_VIDE) ? Const.ZERO : refArr);
			getPrimeCourante().setDateArrete(dateArr.equals(Const.CHAINE_VIDE) ? Const.ZERO : dateArr);
			getPrimeCourante().setMtPri(montant);
			getPrimeCourante().setDatDeb(dateDebut);
			getPrimeCourante().setDatFin(dateFin);

			// règles de gestions
			if (!checkGestion(r))
				return false;

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
				getPrimeCourante().modifierPrime(getTransaction(), getAgentCourant(), user);
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

				getPrimeCourante().creerPrime(getTransaction(), user);

				PrimeAgent primeAgent = new PrimeAgent(getAgentCourant().getIdAgent(), getAgentCourant().getNoMatricule(), getPrimeCourante()
						.getNoRubr(), getPrimeCourante().getDatDeb());
				primeAgent.creerPrimeAgent(getTransaction());
			}
			// RG_AG_PR_A01
			Matricule.updateMatricule(getTransaction(), getAgentCourant(), getPrimeCourante().getDatDeb());
			if (getTransaction().isErreur())
				return false;
		}

		initialiseListePrimes(request);

		if (getTransaction().isErreur())
			return false;

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien passé
		commitTransaction();

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RUBRIQUE Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_ST_RUBRIQUE() {
		return "NOM_ST_RUBRIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RUBRIQUE Date
	 * de création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_ST_RUBRIQUE() {
		return getZone(getNOM_ST_RUBRIQUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARR Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_EF_DATE_ARR() {
		return "NOM_EF_DATE_ARR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARR Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_EF_DATE_ARR() {
		return getZone(getNOM_EF_DATE_ARR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_EF_MONTANT() {
		return "NOM_EF_MONTANT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_EF_MONTANT() {
		return getZone(getNOM_EF_MONTANT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_ARR Date de
	 * création : (05/08/11 10:06:07)
	 * 
	 */
	public String getNOM_EF_REF_ARR() {
		return "NOM_EF_REF_ARR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_ARR Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public String getVAL_EF_REF_ARR() {
		return getZone(getNOM_EF_REF_ARR());
	}

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	private Hashtable<String, Rubrique> getHashRubriques() {
		if (hashRubriques == null)
			hashRubriques = new Hashtable<String, Rubrique>();
		return hashRubriques;
	}

	public ArrayList<Prime> getListePrimes() {
		return listePrimes;
	}

	private void setListePrimes(ArrayList<Prime> listePrimes) {
		this.listePrimes = listePrimes;
	}

	public ArrayList<Rubrique> getListeRubriques() {
		return listeRubriques;
	}

	private void setListeRubriques(ArrayList<Rubrique> listeRubriques) {
		this.listeRubriques = listeRubriques;
	}

	private Prime getPrimeCourante() {
		return primeCourante;
	}

	private void setPrimeCourante(Prime primeCourante) {
		this.primeCourante = primeCourante;
	}

	public String getNomEcran() {
		return "ECR-AG-ELTSAL-PRIMES";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (05/08/11 10:06:07)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListePrimes().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListePrimes().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListePrimes().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (18/10/11 11:32:27)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTPrime.jsp";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RUBRIQUE Date de
	 * création : (30/08/11 10:25:41)
	 * 
	 */
	public String getNOM_EF_RUBRIQUE() {
		return "NOM_EF_RUBRIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_RUBRIQUE Date de création : (30/08/11 10:25:41)
	 * 
	 */
	public String getVAL_EF_RUBRIQUE() {
		return getZone(getNOM_EF_RUBRIQUE());
	}

	/**
	 * Récupere la rubrique selectionnée
	 * 
	 * @return
	 * @throws Exception
	 */
	private Rubrique getSelectedRubrique() throws Exception {
		// récupération de la rubrique et vérification de son existence.
		String idRubrique = Const.CHAINE_VIDE;
		// pour les rubriques actives
		for (int i = 0; i < getListeRubriques().size(); i++) {
			Rubrique r = (Rubrique) getListeRubriques().get(i);
			String textRubr = r.getNumRubrique() + " " + r.getLibRubrique();
			if (textRubr.equals(getVAL_EF_RUBRIQUE())) {
				idRubrique = r.getNumRubrique();
				break;
			}
		}
		// tests sur rubriques inactives
		for (int i = 0; i < getListeRubriquesTotales().size(); i++) {
			Rubrique r = (Rubrique) getListeRubriquesTotales().get(i);
			String textRubr = r.getNumRubrique() + " " + r.getLibRubrique();
			if (textRubr.equals(getVAL_EF_RUBRIQUE())) {
				idRubrique = r.getNumRubrique();
				break;
			}
		}

		if (idRubrique.equals(Const.CHAINE_VIDE)) {
			return null;
		}
		return Rubrique.chercherRubrique(getTransaction(), idRubrique);
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_RUBR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE_RUBR(int i) {
		return "NOM_ST_CODE_RUBR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_RUBR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE_RUBR(int i) {
		return getZone(getNOM_ST_CODE_RUBR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_RUBR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_RUBR(int i) {
		return "NOM_ST_LIB_RUBR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_RUBR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_RUBR(int i) {
		return getZone(getNOM_ST_LIB_RUBR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REF_ARR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REF_ARR(int i) {
		return "NOM_ST_REF_ARR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REF_ARR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REF_ARR(int i) {
		return getZone(getNOM_ST_REF_ARR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ARR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_ARR(int i) {
		return "NOM_ST_DATE_ARR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ARR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_ARR(int i) {
		return getZone(getNOM_ST_DATE_ARR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MONTANT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MONTANT(int i) {
		return "NOM_ST_MONTANT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MONTANT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MONTANT(int i) {
		return getZone(getNOM_ST_MONTANT(i));
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

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de la prime courante
		Prime primeCourante = (Prime) getListePrimes().get(indiceEltAModifier);
		setPrimeCourante(primeCourante);

		// init prime courant
		if (!initialisePrimeCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
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

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de la prime courante
		Prime primeCourante = (Prime) getListePrimes().get(indiceEltAConsulter);
		setPrimeCourante(primeCourante);

		// init prime courant
		if (!initialisePrimeCourante(request))
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de la prime courante
		Prime primeCourante = (Prime) getListePrimes().get(indiceEltASuprimer);
		setPrimeCourante(primeCourante);

		// init prime courant
		if (!initialisePrimeCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<Rubrique> getListeRubriquesTotales() {
		return listeRubriquesTotales;
	}

	public void setListeRubriquesTotales(ArrayList<Rubrique> listeRubriquesTotales) {
		this.listeRubriquesTotales = listeRubriquesTotales;
	}
}
