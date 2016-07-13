package nc.mairie.gestionagent.process;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.enums.EnumMotifVisiteMed;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.SPABSEN;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.SPABSENDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class OeSMCalcul extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_MOIS;

	private String[] listeMois;
	private Hashtable<String, String> hashMois;

	public String ACTION_CALCUL = "Calcul";

	private Logger logger = LoggerFactory.getLogger(OeSMCalcul.class);

	private SuiviMedicalDao suiviMedDao;
	private SPABSENDao spabsenDao;
	private MedecinDao medecinDao;
	private VisiteMedicaleDao visiteMedicaleDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		logger.info("entrée initialiseZones");
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getSuiviMedDao() == null)
			setSuiviMedDao(new SuiviMedicalDao(
					(SirhDao) context.getBean("sirhDao")));

		if (getSpabsenDao() == null)
			setSpabsenDao(new SPABSENDao(
					(MairieDao) context.getBean("mairieDao")));

		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getVisiteMedicaleDao() == null) {
			setVisiteMedicaleDao(new VisiteMedicaleDao(
					(SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao(
					(SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao(
					(SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null
				&& request.getParameter("JSP").equals(getJSP())) {
			// Si clic sur le bouton PB_CALCULER
			if (testerParametre(request, getNOM_PB_CALCULER())) {
				return performPB_CALCULER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {

		// Si liste mois vide alors affectation
		if (getLB_MOIS() == LBVide) {
			Integer moisCourant = Integer.parseInt(Services.dateDuJour()
					.substring(3, 5)) - 1;
			String anneeCourante = Services.dateDuJour().substring(6, 10);
			DateFormatSymbols dfsFR = new DateFormatSymbols(Locale.FRENCH);
			String[] moisAnneeFR = dfsFR.getMonths();
			String[] moisAnnee = dfsFR.getShortMonths();
			getHashMois().clear();
			int j = 0;
			int tailleTotal = 6;
			setListeMois(new String[tailleTotal]);

			for (int i = moisCourant; i < moisAnneeFR.length - 1; i++) {
				if (j >= tailleTotal) {
					break;
				}
				getListeMois()[j] = moisAnneeFR[i] + " - " + anneeCourante;
				getHashMois().put(moisAnneeFR[i] + " - " + anneeCourante,
						moisAnnee[i] + "/" + anneeCourante);
				j++;
			}
			// si moisCourant = juillet ou plus
			// alors il faudra afficher des mois de l'année suivante
			if (moisCourant >= 7) {
				String anneeSuivante = String.valueOf(Integer
						.valueOf(anneeCourante) + 1);
				int sauvJ = j;
				for (int i = 0; i < tailleTotal - sauvJ; i++) {
					getListeMois()[j] = moisAnneeFR[i] + " - " + anneeSuivante;
					getHashMois().put(moisAnneeFR[i] + " - " + anneeSuivante,
							moisAnnee[i] + "/" + anneeSuivante);
					j++;
				}
			}

			setLB_MOIS(getListeMois());
			addZone(getNOM_LB_MOIS_SELECT(), Const.ZERO);
		}
	}

	/**
	 * Getter de la HashTable des mois.
	 * 
	 * @return hashMois
	 */
	private Hashtable<String, String> getHashMois() {
		if (hashMois == null) {
			hashMois = new Hashtable<String, String>();
		}
		return hashMois;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SM-CONVOCATION";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CALCULER Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_PB_CALCULER() {
		return "NOM_PB_CALCULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CALCULER(HttpServletRequest request)
			throws Exception {
		// Mise à jour de l'action menee
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		if (indiceMois != -1) {
			Integer moisChoisi = getMoisSelectionne(indiceMois);
			Integer anneeChoisi = getAnneeSelectionne(indiceMois);

			// Suppression des suivi medicaux a l'etat 'Travail' en fonction du
			// mois et de l'année
			try {
				getSuiviMedDao().supprimerSuiviMedicalTravailAvecMoisetAnnee(
						EnumEtatSuiviMed.TRAVAIL.getCode(), moisChoisi,
						anneeChoisi);
			} catch (Exception e) {
				logger.error("Problème dans la suppression des suivi medicaux"
						+ new Date());
			}

			// Lancement du calcul des suivi medicaux
			performCalculSuiviMedical(moisChoisi, anneeChoisi);

		} else {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		// "INF400","Calcul effectue"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF400"));

		return true;
	}

	private void performCalculSuiviMedical(Integer moisChoisi,
			Integer anneeChoisi) throws Exception {

		// CAS N 1 : A la demande de l'agent ou du service
		logger.info("Calcul cas1 : A la demande de l'agent ou du service");
		performCalculCas1(moisChoisi, anneeChoisi);

		// CAS N 2 : Visite Réguliere
		logger.info("Calcul cas2 : Visite Réguliere");
		performCalculCas2(moisChoisi, anneeChoisi);

		// CAS N 3 : AT avec ITT>15jours
		logger.info("Calcul cas3 :  AT avec ITT>15jours");
		performCalculCas3(moisChoisi, anneeChoisi);

		// CAS N 4 : Maladie > 1 mois
		logger.info("Calcul cas4 : Maladie > 1 mois");
		performCalculCas4(moisChoisi, anneeChoisi);

		// CAS N 5 : Longue maladie
		logger.info("Calcul cas5 : Longue maladie");
		performCalculCas5(moisChoisi, anneeChoisi);

		// CAS N 6 : Visite Nouvel arrivant
		logger.info("Calcul cas6 : Visite Nouvel arrivant");
		performCalculCas6(moisChoisi, anneeChoisi);

		// CAS N 7 : Changement de PA
		logger.info("Calcul cas7 : Changement de PA");
		performCalculCas7(moisChoisi, anneeChoisi);

		// CAS N 8 : CONVOCATION NON EXECUTEE
		logger.info("Calcul cas8 : CONVOCATION NON EXECUTEE");
		performCalculCas8(moisChoisi, anneeChoisi);

		// CAS N 9 : AGENT SANS VISITES MEDICALES
		logger.info("Calcul cas9 : AGENT SANS VISITES MEDICALES");
		performCalculCas9(moisChoisi, anneeChoisi);

		logger.info("FIN DES CALCULS");

	}

	private void performCalculCas9(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 9 : AGENT SANS VISITES MEDICALES
		// on liste tous les agents sans visites medicales
		// avec une PA active à la date du jour
		ArrayList<PositionAdmAgent> listeAgentActivite = PositionAdmAgent
				.listerPositionAdmAgentEnActivite(getTransaction());
		String listeNomatr = Const.CHAINE_VIDE;
		for (PositionAdmAgent pa : listeAgentActivite) {
			listeNomatr += pa.getNomatr() + ",";
		}
		if (!listeNomatr.equals(Const.CHAINE_VIDE)) {
			listeNomatr = listeNomatr.substring(0, listeNomatr.length() - 1);
		}
		ArrayList<Agent> listeSMCas9 = getAgentDao()
				.listerAgentSansVMPAEnCours(listeNomatr);
		int nbCas9 = 0;

		for (int i = 0; i < listeSMCas9.size(); i++) {
			Agent agent = listeSMCas9.get(i);
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent
					.chercherPositionAdmAgentActive(getTransaction(),
							agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}
			// on crée la nouvelle ligne
			SuiviMedical sm = new SuiviMedical();
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere
						.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(
						agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(
							aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(agent.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao()
					.chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null
					: derniereVisite.getCommentaire());
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse("15/"
					+ moisChoisi + "/" + anneeChoisi);
			sm.setDatePrevisionVisite(d);
			sm.setIdMotifVm(EnumMotifVisiteMed.VM_AGENT_SANS_VM.getCode());
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas reecrire une ligne
			// du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est
				// superieur a
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer
				// la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp
							.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
							"dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite()
							.toString();
					if (Services.compareDates(dateExistePrevision,
							datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(
								smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(),
					sm.getDateProchaineVisite(), sm.getHeureProchaineVisite(),
					sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(),
					sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(),
					sm.getCommentaireDerniereViste());
			nbCas9++;
		}
		logger.info("Nb de cas 9 : " + nbCas9);
	}

	private void performCalculCas5(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 5 : Longue maladie
		// on liste toutes les absences (SPABSEN) de type MA pourle mois et
		// l'année donnée
		int nbCas5 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas5 = getSpabsenDao()
					.listerMatriculeAbsencePourSMDoubleType("MA", "LM",
							moisChoisi, anneeChoisi);
			// pour chaque matricule trouvé on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 90
			for (int i = 0; i < listeMatriculeSMCas5.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas5.get(i);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent
						.chercherPositionAdmAgentActive(getTransaction(),
								nomatrAgent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				ArrayList<SPABSEN> listeSPABSENAgent = getSpabsenDao()
						.listerAbsencePourAgentTypeEtMoisAnneeDoubleType(
								nomatrAgent, "MA", "LM", moisChoisi,
								anneeChoisi);
				Integer compteurJoursMA = 0;
				SPABSEN dernierAM = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeSPABSENAgent.size(); j++) {
					SPABSEN lignePrecedente = listeSPABSENAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursMA = compteurJoursMA
								+ lignePrecedente.getNbJour();
					}
					dernierAM = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeSPABSENAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale a datFin de la precdente + 1
						SPABSEN ligneSuivante = listeSPABSENAgent.get(j + 1);
						dernierAM = ligneSuivante;
						String dateDebLigneSuiv = Services.enleveJours(Services
								.convertitDate(ligneSuivante.getDatDeb()
										.toString(), "yyyyMMdd", "dd/MM/yyyy"),
								1);
						String dateFinLignePrec = Services.convertitDate(
								lignePrecedente.getDatFin().toString(),
								"yyyyMMdd", "dd/MM/yyyy");
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursMA = compteurJoursMA
									+ ligneSuivante.getNbJour();
							dejaComptabilise = true;
						} else {
							compteurJoursMA = 0;
							dejaComptabilise = false;
						}
					}
				}
				if (90 < compteurJoursMA) {
					// on crée la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(
							nomatrAgent);
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr
								.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao()
								.chercherAffectationActiveAvecAgent(
										agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(
									aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao()
							.chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null
							: derniereVisite.getCommentaire());
					String datePrev = Services.ajouteJours(Services
							.convertitDate(dernierAM.getDatFin().toString(),
									"yyyyMMdd", "dd/MM/yyyy"), 2);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_CONGE_LONGUE_MALADIE
							.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas reecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentMoisetAnnee(
										sm.getIdAgent(), moisChoisi,
										anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur a
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services
									.convertitDate(smTemp
											.getDatePrevisionVisite()
											.toString(), "yyyy-MM-dd",
											"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite()
									.toString();
							if (Services.compareDates(dateExistePrevision,
									datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(
										smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance(),
							sm.getIdServiceAds(), sm.getIdServi(),
							sm.getIdRecommandationDerniereVisite(),
							sm.getCommentaireDerniereViste());
					nbCas5++;

				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 5.");
		}
		logger.info("Nb cas 5 : " + nbCas5);
	}

	private void performCalculCas4(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 4 : Maladie > 1 mois
		// on liste toutes les absences (SPABSEN) de type MA pourle mois et
		// l'année donnée
		int nbCas4 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas4 = getSpabsenDao()
					.listerMatriculeAbsencePourSM("MA", moisChoisi, anneeChoisi);
			// pour chaque matricule trouvé on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 30
			for (int i = 0; i < listeMatriculeSMCas4.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas4.get(i);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent
						.chercherPositionAdmAgentActive(getTransaction(),
								nomatrAgent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				ArrayList<SPABSEN> listeSPABSENAgent = getSpabsenDao()
						.listerAbsencePourAgentTypeEtMoisAnnee(nomatrAgent,
								"MA", moisChoisi, anneeChoisi);
				Integer compteurJoursMA = 0;
				SPABSEN dernierAM = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeSPABSENAgent.size(); j++) {
					SPABSEN lignePrecedente = listeSPABSENAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursMA = compteurJoursMA
								+ lignePrecedente.getNbJour();
					}
					dernierAM = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeSPABSENAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale a datFin de la precdente + 1
						SPABSEN ligneSuivante = listeSPABSENAgent.get(j + 1);
						dernierAM = ligneSuivante;
						String dateDebLigneSuiv = Services.enleveJours(Services
								.convertitDate(ligneSuivante.getDatDeb()
										.toString(), "yyyyMMdd", "dd/MM/yyyy"),
								1);
						String dateFinLignePrec = Services.convertitDate(
								lignePrecedente.getDatFin().toString(),
								"yyyyMMdd", "dd/MM/yyyy");
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursMA = compteurJoursMA
									+ ligneSuivante.getNbJour();
							dejaComptabilise = true;
						} else {
							compteurJoursMA = 0;
							dejaComptabilise = false;
						}
					}
				}
				if (90 > compteurJoursMA && compteurJoursMA > 30) {
					// on crée la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(
							nomatrAgent);
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr
								.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao()
								.chercherAffectationActiveAvecAgent(
										agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(
									aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao()
							.chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null
							: derniereVisite.getCommentaire());
					String datePrev = Services.ajouteJours(Services
							.convertitDate(dernierAM.getDatFin().toString(),
									"yyyyMMdd", "dd/MM/yyyy"), 2);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_MA_1MOIS.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas reecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentMoisetAnnee(
										sm.getIdAgent(), moisChoisi,
										anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur a
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services
									.convertitDate(smTemp
											.getDatePrevisionVisite()
											.toString(), "yyyy-MM-dd",
											"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite()
									.toString();
							if (Services.compareDates(dateExistePrevision,
									datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(
										smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance(),
							sm.getIdServiceAds(), sm.getIdServi(),
							sm.getIdRecommandationDerniereVisite(),
							sm.getCommentaireDerniereViste());
					nbCas4++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 4");
		}
		logger.info("Nb de cas 4 : " + nbCas4);
	}

	private void performCalculCas3(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 3 : AT avec ITT>15jours
		// on liste toutes les absences (SPABSEN) de type AT pourle mois et
		// l'année donnée
		int nbCas3 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas3 = getSpabsenDao()
					.listerMatriculeAbsencePourSM("AT", moisChoisi, anneeChoisi);
			// pour chaque matricule trouvé on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 15
			for (int i = 0; i < listeMatriculeSMCas3.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas3.get(i);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent
						.chercherPositionAdmAgentActive(getTransaction(),
								nomatrAgent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				ArrayList<SPABSEN> listeSPABSENAgent = getSpabsenDao()
						.listerAbsencePourAgentTypeEtMoisAnnee(nomatrAgent,
								"AT", moisChoisi, anneeChoisi);
				Integer compteurJoursITT = 0;
				SPABSEN dernierAT = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeSPABSENAgent.size(); j++) {
					SPABSEN lignePrecedente = listeSPABSENAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursITT = compteurJoursITT
								+ lignePrecedente.getNbJour();
					}
					dernierAT = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeSPABSENAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale a datFin de la precdente + 1
						SPABSEN ligneSuivante = listeSPABSENAgent.get(j + 1);
						dernierAT = ligneSuivante;
						String dateDebLigneSuiv = Services.enleveJours(Services
								.convertitDate(ligneSuivante.getDatDeb()
										.toString(), "yyyyMMdd", "dd/MM/yyyy"),
								1);
						String dateFinLignePrec = Services.convertitDate(
								lignePrecedente.getDatFin().toString(),
								"yyyyMMdd", "dd/MM/yyyy");
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursITT = compteurJoursITT
									+ ligneSuivante.getNbJour();
							dejaComptabilise = true;
						} else {
							compteurJoursITT = 0;
							dejaComptabilise = false;
						}
					}
				}
				if (compteurJoursITT > 15) {
					// on crée la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(
							nomatrAgent);
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr
								.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao()
								.chercherAffectationActiveAvecAgent(
										agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(
									aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao()
							.chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null
							: derniereVisite.getCommentaire());
					String datePrev = Services.ajouteJours(Services
							.convertitDate(dernierAT.getDatFin().toString(),
									"yyyyMMdd", "dd/MM/yyyy"), 1);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_AT_ITT_15JOURS
							.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas reecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentMoisetAnnee(
										sm.getIdAgent(), moisChoisi,
										anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur a
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services
									.convertitDate(smTemp
											.getDatePrevisionVisite()
											.toString(), "yyyy-MM-dd",
											"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite()
									.toString();
							if (Services.compareDates(dateExistePrevision,
									datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(
										smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance(),
							sm.getIdServiceAds(), sm.getIdServi(),
							sm.getIdRecommandationDerniereVisite(),
							sm.getCommentaireDerniereViste());
					nbCas3++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 3");
		}
		logger.info("Nb de cas 3 : " + nbCas3);
	}

	private void performCalculCas1(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 1 : A la demande de l'agent ou du service
		// on liste toutes les visites medicales du type "a la demande..."
		Medecin m = getMedecinDao().chercherMedecinARenseigner("A",
				"RENSEIGNER");
		ArrayList<VisiteMedicale> listeSMCas1 = getVisiteMedicaleDao()
				.listerVisiteMedicalePourSMCas1(
						EnumMotifVisiteMed.VM_DEMANDE_AGENT.getCode(),
						EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getCode(),
						m.getIdMedecin());
		int nbCas1 = 0;
		for (int i = 0; i < listeSMCas1.size(); i++) {
			VisiteMedicale vm = listeSMCas1.get(i);
			// on crée la nouvelle ligne
			SuiviMedical sm = new SuiviMedical();
			Agent agent = getAgentDao().chercherAgent(vm.getIdAgent());
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent
					.chercherPositionAdmAgentActive(getTransaction(),
							agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}

			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere
						.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}

			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(
						agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(
							aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(agent.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao()
					.chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null
					: derniereVisite.getCommentaire());
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse("15/"
					+ moisChoisi + "/" + anneeChoisi);
			sm.setDatePrevisionVisite(d);
			sm.setIdMotifVm(vm.getIdMotifVm());
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas reecrire une ligne
			// du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est
				// superieur a
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer
				// la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp
							.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
							"dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite()
							.toString();
					if (Services.compareDates(dateExistePrevision,
							datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(
								smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(),
					sm.getDateProchaineVisite(), sm.getHeureProchaineVisite(),
					sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(),
					sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(),
					sm.getCommentaireDerniereViste());
			nbCas1++;
		}
		logger.info("Nb de cas 1 : " + nbCas1);
	}

	private void performCalculCas8(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 8 : CONVOCATION NON EXECUTEE
		// on liste tous les suivi médicaux de type "CONVOQUE" du mois précédent
		int nbCas8 = 0;
		try {
			ArrayList<SuiviMedical> listeSMCas8 = getSuiviMedDao()
					.listerSuiviMedicalNonEffectue(moisChoisi, anneeChoisi,
							EnumEtatSuiviMed.PLANIFIE.getCode());
			for (int i = 0; i < listeSMCas8.size(); i++) {
				// on crée une nouvelle ligne avec les memes informations
				// sauf pour le statut et le service on le remet a jour
				SuiviMedical smAncien = listeSMCas8.get(i);
				// on crée la nouvelle ligne
				SuiviMedical sm = new SuiviMedical();
				Agent agent = getAgentDao().chercherAgentParMatricule(
						smAncien.getNomatr());
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent
						.chercherPositionAdmAgentActive(getTransaction(),
								agent.getNomatr());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
						getTransaction(), agent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					if (Carriere.isCarriereConseilMunicipal(carr
							.getCodeCategorie())) {
						continue;
					}
				}
				Affectation aff = null;
				try {
					aff = getAffectationDao()
							.chercherAffectationActiveAvecAgent(
									agent.getIdAgent());
				} catch (Exception e) {

				}
				FichePoste fp = null;
				if (aff != null && aff.getIdFichePoste() != null) {
					try {
						fp = getFichePosteDao().chercherFichePoste(
								aff.getIdFichePoste());
					} catch (Exception e) {

					}
				}
				sm.setIdAgent(smAncien.getIdAgent());
				sm.setNomatr(smAncien.getNomatr());
				sm.setAgent(smAncien.getAgent());
				sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
						.getStatutSM(carr.getCodeCategorie()) : null);
				sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
				sm.setIdServi(fp != null ? fp.getIdServi() : null);
				VisiteMedicale derniereVisite = getVisiteMedicaleDao()
						.chercherDerniereVisiteMedicale(agent.getIdAgent());
				sm.setDateDerniereVisite(derniereVisite == null ? null
						: derniereVisite.getDateDerniereVisite());
				sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
						: derniereVisite.getIdRecommandation());
				sm.setCommentaireDerniereViste(derniereVisite == null ? null
						: derniereVisite.getCommentaire());
				sm.setDatePrevisionVisite(smAncien.getDatePrevisionVisite());
				sm.setIdMotifVm(smAncien.getIdMotifVm());
				// ATTENTION : si mois de la date de prochainRDV < moisChoisi
				// alors
				// il faut incrémenter le compteur de 1 pour NbVisiteRatées
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				if (smAncien.getDateProchaineVisite() != null
						&& Integer.valueOf(sdf
								.format(smAncien.getDateProchaineVisite())
								.toString().substring(5, 7)) < moisChoisi) {
					sm.setNbVisitesRatees(smAncien.getNbVisitesRatees() + 1);
				} else {
					sm.setNbVisitesRatees(smAncien.getNbVisitesRatees());
				}

				sm.setIdMedecin(null);
				sm.setDateProchaineVisite(null);
				sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
				sm.setMois(moisChoisi);
				sm.setAnnee(anneeChoisi);
				// on flag cette ligne en relance afin qu'on puisse la mettre de
				// couleur differente a l'affichage
				sm.setRelance(1);

				// on regarde la liste des SM pour ne pas reecrire une ligne
				// du meme
				// agent
				try {
					SuiviMedical smTemp = getSuiviMedDao()
							.chercherSuiviMedicalAgentMoisetAnnee(
									smAncien.getIdAgent(), moisChoisi,
									anneeChoisi);
					logger.debug("SM : " + smTemp.toString());
					// si une ligne existe deja
					// on regarde si etat Travail
					// si oui, on regarde si la date de prevision est
					// superieur a
					// celle existante
					// si oui alors on ne crée pas de nouvelle ligne
					// si non , on supprime la ligne existante pour recréer
					// la
					// nouvelle
					if (smTemp.getEtat().equals(
							EnumEtatSuiviMed.TRAVAIL.getCode())) {
						String dateExistePrevision = Services.convertitDate(
								smTemp.getDatePrevisionVisite().toString(),
								"yyyy-MM-dd", "dd/MM/yyyy");
						String datePrevision = sm.getDatePrevisionVisite()
								.toString();
						if (Services.compareDates(dateExistePrevision,
								datePrevision) > 0) {
							continue;
						} else {
							getSuiviMedDao().supprimerSuiviMedicalById(
									smTemp.getIdSuiviMed());
						}
					} else {
						continue;
					}
				} catch (Exception e) {
					// aucune ligne n'a été trouvée alors on continue
				}
				getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
						sm.getNomatr(), sm.getAgent(), sm.getStatut(),
						sm.getDateDerniereVisite(),
						sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
						sm.getNbVisitesRatees(), sm.getIdMedecin(),
						sm.getDateProchaineVisite(),
						sm.getHeureProchaineVisite(), sm.getEtat(),
						sm.getMois(), sm.getAnnee(), sm.getRelance(),
						sm.getIdServiceAds(), sm.getIdServi(),
						sm.getIdRecommandationDerniereVisite(),
						sm.getCommentaireDerniereViste());
				nbCas8++;
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 8");
		}
		logger.info("Nb de cas 8 : " + nbCas8);
	}

	private void performCalculCas7(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 7 : Changement de PA
		// on liste toutes les PA hors-effectif
		// on Vérifie qu'il y a bien une PA normale apres cette PA hors-effectif
		ArrayList<PositionAdmAgent> listePACas7 = PositionAdmAgent
				.listerPositionAdmAgentHorsEffectif(getTransaction(),
						moisChoisi, anneeChoisi);
		int nbCas7 = 0;
		for (int i = 0; i < listePACas7.size(); i++) {
			// on regarde pour cette liste de PA si il en existe une qui suit en
			// NORMALE 01
			PositionAdmAgent paHorsEffectif = listePACas7.get(i);
			PositionAdmAgent paSuivante = PositionAdmAgent
					.chercherPositionAdmAgent(getTransaction(),
							paHorsEffectif.getNomatr(),
							paHorsEffectif.getDatfin());
			if (paSuivante != null && paSuivante.getCdpadm() != null) {
				if (paSuivante.getCdpadm().equals("01")) {
					// si c'est bon alors on crée le suiviMedical
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(
							Integer.valueOf(paSuivante.getNomatr()));
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr
								.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao()
								.chercherAffectationActiveAvecAgent(
										agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(
									aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao()
							.chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
							: derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null
							: derniereVisite.getCommentaire());
					Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(Services
							.enleveJours(paSuivante.getDatdeb(), 15));
					sm.setDatePrevisionVisite(d2);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_CHANGEMENT_PA
							.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas reecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentNomatrMoisetAnnee(
										Integer.valueOf(paHorsEffectif
												.getNomatr()), moisChoisi,
										anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur a
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services
									.convertitDate(smTemp
											.getDatePrevisionVisite()
											.toString(), "yyyy-MM-dd",
											"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite()
									.toString();
							if (Services.compareDates(dateExistePrevision,
									datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(
										smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance(),
							sm.getIdServiceAds(), sm.getIdServi(),
							sm.getIdRecommandationDerniereVisite(),
							sm.getCommentaireDerniereViste());
					nbCas7++;
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		logger.info("Nb de cas 7 : " + nbCas7);
	}

	private void performCalculCas6(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 6 : Visite Nouvel arrivant
		// on liste tous les nouveaux arrivant
		ArrayList<Agent> listeAgentCas6 = getAgentDao()
				.listerAgentNouveauxArrivant(moisChoisi, anneeChoisi);
		int nbCas6 = 0;
		for (int i = 0; i < listeAgentCas6.size(); i++) {
			SuiviMedical sm = new SuiviMedical();
			Agent agent = listeAgentCas6.get(i);
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent
					.chercherPositionAdmAgentActive(getTransaction(),
							agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere
						.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(
						agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(
							aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(agent.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao()
					.chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null
					: derniereVisite.getCommentaire());
			sm.setDatePrevisionVisite(agent.getDateDerniereEmbauche());
			sm.setIdMotifVm(EnumMotifVisiteMed.VM_NOUVEAU.getCode());
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas reecrire une ligne du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(
								agent.getIdAgent(), moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur a
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp
							.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
							"dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite()
							.toString();
					if (Services.compareDates(dateExistePrevision,
							datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(
								smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(),
					sm.getDateProchaineVisite(), sm.getHeureProchaineVisite(),
					sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(),
					sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(),
					sm.getCommentaireDerniereViste());
			nbCas6++;
		}
		logger.info("Nb de cas 6 : " + nbCas6);
	}

	private void performCalculCas2(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N 2 : Visite Réguliere
		// on liste toutes les visites medicales
		// dont la dateVM + durée validitéVM = mois et année choisie du calcul
		ArrayList<VisiteMedicale> listeVMCas2 = getVisiteMedicaleDao()
				.listerVisiteMedicalePourSMCas2(moisChoisi, anneeChoisi);
		int nbCas2 = 0;
		for (int i = 0; i < listeVMCas2.size(); i++) {
			VisiteMedicale vm = listeVMCas2.get(i);

			SuiviMedical sm = new SuiviMedical();
			Agent agent = getAgentDao().chercherAgent(vm.getIdAgent());
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent
					.chercherPositionAdmAgentActive(getTransaction(),
							agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere
						.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(
						agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(
							aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(vm.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao()
					.chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null
					: derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null
					: derniereVisite.getCommentaire());
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(Services
					.ajouteMois(new SimpleDateFormat("dd/MM/yyyy").format(vm
							.getDateDerniereVisite()), vm.getDureeValidite()));
			sm.setDatePrevisionVisite(d2);
			sm.setIdMotifVm(EnumMotifVisiteMed.VM_REGULIERE.getCode());
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas reecrire une ligne du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(vm.getIdAgent(),
								moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur a
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp
							.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
							"dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite()
							.toString();
					if (Services.compareDates(dateExistePrevision,
							datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(
								smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(),
					sm.getDateProchaineVisite(), sm.getHeureProchaineVisite(),
					sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(),
					sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(),
					sm.getCommentaireDerniereViste());
			nbCas2++;
		}
		logger.info("Nb de cas 2 : " + nbCas2);
	}

	private Integer getMoisSelectionne(int indiceMois) throws ParseException {
		if (getListeMois() != null && getListeMois().length > 0
				&& indiceMois != -1) {
			String test = getListeMois()[indiceMois];
			String test2 = getHashMois().get(test);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy",
					Locale.FRENCH);
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM", Locale.FRENCH);
			Date d = sdf.parse(test2);
			String mois = sdf2.format(d);
			return Integer.valueOf(mois);
		} else {
			return 0;
		}
	}

	private Integer getAnneeSelectionne(int indiceMois) throws ParseException {
		if (getListeMois() != null && getListeMois().length > 0
				&& indiceMois != -1) {
			String test = getListeMois()[indiceMois];
			String test2 = getHashMois().get(test);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy",
					Locale.FRENCH);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy", Locale.FRENCH);
			Date d = sdf.parse(test2);
			String annee = sdf2.format(d);
			return Integer.valueOf(annee);
		} else {
			return 0;
		}
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOIS Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_MOIS() {
		if (LB_MOIS == null)
			LB_MOIS = initialiseLazyLB();
		return LB_MOIS;
	}

	/**
	 * Setter de la liste: LB_MOIS Date de création : (28/11/11)
	 * 
	 */
	private void setLB_MOIS(String[] listeMois) {
		LB_MOIS = listeMois;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOIS Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_MOIS() {
		return "NOM_LB_MOIS";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOIS_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_MOIS_SELECT() {
		return "NOM_LB_MOIS_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOIS Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MOIS() {
		return getLB_MOIS();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MOIS Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_MOIS_SELECT() {
		return getZone(getNOM_LB_MOIS_SELECT());
	}

	/**
	 * Getter de la liste des années possibles.
	 * 
	 * @return listeMois
	 */
	private String[] getListeMois() {
		return listeMois;
	}

	/**
	 * Setter de la liste des années possibles.
	 * 
	 * @param strings
	 */
	private void setListeMois(String[] strings) {
		this.listeMois = strings;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public SuiviMedicalDao getSuiviMedDao() {
		return suiviMedDao;
	}

	public void setSuiviMedDao(SuiviMedicalDao suiviMedDao) {
		this.suiviMedDao = suiviMedDao;
	}

	public SPABSENDao getSpabsenDao() {
		return spabsenDao;
	}

	public void setSpabsenDao(SPABSENDao spabsenDao) {
		this.spabsenDao = spabsenDao;
	}

	@Override
	public String getJSP() {
		return "OeSMCalcul.jsp";
	}

	public MedecinDao getMedecinDao() {
		return medecinDao;
	}

	public void setMedecinDao(MedecinDao medecinDao) {
		this.medecinDao = medecinDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public VisiteMedicaleDao getVisiteMedicaleDao() {
		return visiteMedicaleDao;
	}

	public void setVisiteMedicaleDao(VisiteMedicaleDao visiteMedicaleDao) {
		this.visiteMedicaleDao = visiteMedicaleDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
