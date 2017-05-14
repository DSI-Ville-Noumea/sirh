package nc.mairie.gestionagent.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.enums.EnumMotifVisiteMed;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IAbsService;

public class OeSMCalcul extends BasicProcess {

	/**
	 * 
	 */
	private static final long	serialVersionUID		= 1L;
	private String[]			LB_MOIS;
	private String[]			LB_ANNEE;

	private List<Integer>		listeMois				= new ArrayList<>();
	private List<Integer>		listeAnnee				= new ArrayList<>();;

	public String				ACTION_CALCUL			= "Calcul";

	private Logger				logger					= LoggerFactory.getLogger(OeSMCalcul.class);

	private SuiviMedicalDao				suiviMedDao;
	private MedecinDao					medecinDao;
	private VisiteMedicaleDao			visiteMedicaleDao;
	private FichePosteDao				fichePosteDao;
	private AffectationDao				affectationDao;
	private AgentDao					agentDao;
	private IAbsService					absService;

	public static final int		STATUT_RECHERCHER_AGENT	= 1;

	private SimpleDateFormat			sdfyyyyMMdd				= new SimpleDateFormat("yyyyMMdd");

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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
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
			setSuiviMedDao(new SuiviMedicalDao((SirhDao) context.getBean("sirhDao")));

		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getVisiteMedicaleDao() == null) {
			setVisiteMedicaleDao(new VisiteMedicaleDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == absService) {
			absService = (IAbsService) context.getBean("absService");
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
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
		if (getLB_ANNEE() == LBVide) {
			Integer anneeCourante = Integer.parseInt(Services.dateDuJour().substring(6, 10));
			getListeAnnee().clear();
			getListeAnnee().add(anneeCourante + 2);
			getListeAnnee().add(anneeCourante + 1);
			getListeAnnee().add(anneeCourante);
			getListeAnnee().add(anneeCourante - 1);
			getListeAnnee().add(anneeCourante - 2);

			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (Integer anne : getListeAnnee()) {
				String ligne[] = { anne.toString() };
				aFormat.ajouteLigne(ligne);
			}

			setLB_ANNEE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_SELECT(), "2");
		}

		// Si liste mois vide alors affectation
		if (getLB_MOIS() == LBVide) {
			Integer moisCourant = Integer.parseInt(Services.dateDuJour().substring(3, 5));
			getListeMois().clear();

			for (int i = 1; i < 13; i++) {
				getListeMois().add(i);
			}

			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (Integer mois : getListeMois()) {
				String ligne[] = { mois.toString() };
				aFormat.ajouteLigne(ligne);
			}

			setLB_MOIS(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_MOIS_SELECT(), moisCourant.toString());
		}
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
	public boolean performPB_CALCULER(HttpServletRequest request) throws Exception {
		// Mise à  jour de l'action menee
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		if (indiceMois != -1 && indiceAnnee != -1) {
			Integer anneeChoisi = getListeAnnee().get(indiceAnnee);
			Integer moisChoisi = getListeMois().get(indiceMois);

			// Suppression des suivi medicaux a l'etat 'Travail' en fonction du
			// mois et de l'année
			try {
				getSuiviMedDao().supprimerSuiviMedicalTravailAvecMoisetAnnee(EnumEtatSuiviMed.TRAVAIL.getCode(), moisChoisi, anneeChoisi);
			} catch (Exception e) {
				logger.error("Problème dans la suppression des suivi medicaux" + new Date());
			}

			// Lancement du calcul des suivi medicaux
			performCalculSuiviMedical(moisChoisi, anneeChoisi);

		} else {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mois/année"));
			return false;
		}

		// "INF400","Calcul effectue"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF400"));

		return true;
	}

	private void performCalculSuiviMedical(Integer moisChoisi, Integer anneeChoisi) throws Exception {

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

	private void performCalculCas9(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 9 : AGENT SANS VISITES MEDICALES
		// on liste tous les agents sans visites medicales
		// avec une PA active à  la date du jour
		ArrayList<PositionAdmAgent> listeAgentActivite = PositionAdmAgent.listerPositionAdmAgentEnActivite(getTransaction());
		String listeNomatr = Const.CHAINE_VIDE;
		for (PositionAdmAgent pa : listeAgentActivite) {
			listeNomatr += pa.getNomatr() + ",";
		}
		if (!listeNomatr.equals(Const.CHAINE_VIDE)) {
			listeNomatr = listeNomatr.substring(0, listeNomatr.length() - 1);
		}
		ArrayList<Agent> listeSMCas9 = getAgentDao().listerAgentSansVMPAEnCours(listeNomatr);
		int nbCas9 = 0;

		for (int i = 0; i < listeSMCas9.size(); i++) {
			Agent agent = listeSMCas9.get(i);
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), agent.getNomatr());
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
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(agent.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse("15/" + moisChoisi + "/" + anneeChoisi);
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
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(), moisChoisi, anneeChoisi);
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
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite().toString();
					if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
			nbCas9++;
		}
		logger.info("Nb de cas 9 : " + nbCas9);
	}

	private void performCalculCas5(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 5 : Longue maladie
		// on liste toutes les absences pour le mois et l'année donnée
		int nbCas5 = 0;

		DateTime dateDeb = new DateTime().withYear(anneeChoisi).withMonthOfYear(moisChoisi).withDayOfMonth(1).withMillisOfDay(0);
		DateTime dateFin = new DateTime().withYear(anneeChoisi).withMonthOfYear(moisChoisi).dayOfMonth().withMaximumValue().withMillisOfDay(0);

		String dateMin = sdfyyyyMMdd.format(dateDeb.toDate());
		String dateMax = sdfyyyyMMdd.format(dateFin.toDate());

		try {
			ArrayList<DemandeDto> listMaladie = (ArrayList<DemandeDto>) absService.getListeDemandes(dateMin, dateMax,
					Arrays.asList(EnumEtatAbsence.VALIDEE.getCode(), EnumEtatAbsence.PRISE.getCode()).toString().replace("[", "").replace("]", "")
							.replace(" ", ""),
					EnumTypeAbsence.MALADIES_MALADIE.getCode(), null, EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

			ArrayList<DemandeDto> listLongueMaladie = (ArrayList<DemandeDto>) absService.getListeDemandes(dateMin, dateMax,
					Arrays.asList(EnumEtatAbsence.VALIDEE.getCode(), EnumEtatAbsence.PRISE.getCode()).toString().replace("[", "").replace("]", "")
							.replace(" ", ""),
					EnumTypeAbsence.MALADIES_LONGUE_MALADIE.getCode(), null, EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

			List<DemandeDto> listeToutesMaladies = new ArrayList<DemandeDto>();
			listeToutesMaladies.addAll(listMaladie);
			listeToutesMaladies.addAll(listLongueMaladie);

			// on liste les matricules
			List<Integer> listIdAgent = new ArrayList<Integer>();
			if (null != listMaladie) {
				for (DemandeDto maladie : listeToutesMaladies) {
					if (!listIdAgent.contains(maladie.getAgentWithServiceDto().getIdAgent())) {
						listIdAgent.add(maladie.getAgentWithServiceDto().getIdAgent());
					}
				}
			}

			// pour chaque matricule trouvé on va cherche la liste de ses
			// absences
			// on regarde si il se suivent, que le nombre de jours est > 90
			for (int i = 0; i < listIdAgent.size(); i++) {
				Integer idAgent = listIdAgent.get(i);
				Integer nomatrAgent = getNoMatr(idAgent);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), nomatrAgent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}

				List<DemandeDto> listeMaladieAgent = getListDemandeDtoByAgent(idAgent, listeToutesMaladies);
				Double compteurJoursMA = 0.0;
				DemandeDto dernierAM = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeMaladieAgent.size(); j++) {
					DemandeDto lignePrecedente = listeMaladieAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursMA += lignePrecedente.getDuree();
					}
					dernierAM = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeMaladieAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale a datFin de la precdente + 1
						DemandeDto ligneSuivante = listeMaladieAgent.get(j + 1);
						dernierAM = ligneSuivante;
						DateTime dateDebLigneSuiv = new DateTime(ligneSuivante.getDateDebut()).withMillisOfDay(0).minusDays(1);
						DateTime dateFinLignePrec = new DateTime(lignePrecedente.getDateFin()).withMillisOfDay(0);
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursMA += ligneSuivante.getDuree();
							dejaComptabilise = true;
						} else {
							compteurJoursMA = 0.0;
							dejaComptabilise = false;
						}
					}
				}
				if (90 < compteurJoursMA) {
					// on crée la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(nomatrAgent);
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
					Date datePrev = new DateTime(dernierAM.getDateFin()).withMillisOfDay(0).plusDays(2).toDate();
					sm.setDatePrevisionVisite(datePrev);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_CONGE_LONGUE_MALADIE.getCode());
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
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(), moisChoisi, anneeChoisi);
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
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
									"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite().toString();
							if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(),
							sm.getIdServi(), sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
					nbCas5++;

				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 5.");
		}
		logger.info("Nb cas 5 : " + nbCas5);
	}

	private void performCalculCas4(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 4 : Maladie > 1 mois
		// on liste toutes les absences pour le mois et l'année donnée
		int nbCas4 = 0;
		try {
			DateTime dateDeb = new DateTime().withYear(anneeChoisi).withMonthOfYear(moisChoisi).withDayOfMonth(1).withMillisOfDay(0);
			DateTime dateFin = new DateTime().withYear(anneeChoisi).withMonthOfYear(moisChoisi).dayOfMonth().withMaximumValue().withMillisOfDay(0);

			String dateMin = sdfyyyyMMdd.format(dateDeb);
			String dateMax = sdfyyyyMMdd.format(dateFin);

			ArrayList<DemandeDto> listMaladie = (ArrayList<DemandeDto>) absService.getListeDemandes(dateMin, dateMax,
					Arrays.asList(EnumEtatAbsence.VALIDEE.getCode(), EnumEtatAbsence.PRISE.getCode()).toString().replace("[", "").replace("]", "")
							.replace(" ", ""),
					EnumTypeAbsence.MALADIES_MALADIE.getCode(), null, EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

			// on liste les matricules
			List<Integer> listIdAgent = new ArrayList<Integer>();
			if (null != listMaladie) {
				for (DemandeDto maladie : listMaladie) {
					if (!listIdAgent.contains(maladie.getAgentWithServiceDto().getIdAgent())) {
						listIdAgent.add(maladie.getAgentWithServiceDto().getIdAgent());
					}
				}
			}

			// pour chaque matricule trouvé on va cherche la liste de ses
			// absences
			// on regarde si il se suivent, que le nombre de jours est > 30
			for (int i = 0; i < listIdAgent.size(); i++) {
				Integer idAgent = listIdAgent.get(i);
				Integer nomatrAgent = getNoMatr(idAgent);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), nomatrAgent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}

				List<DemandeDto> listeMaladieAgent = getListDemandeDtoByAgent(idAgent, listMaladie);

				Double compteurJoursMA = 0.0;
				DemandeDto dernierAM = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeMaladieAgent.size(); j++) {
					DemandeDto lignePrecedente = listeMaladieAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursMA = compteurJoursMA + lignePrecedente.getDuree();
					}
					dernierAM = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeMaladieAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale a datFin de la precdente + 1
						DemandeDto ligneSuivante = listeMaladieAgent.get(j + 1);
						dernierAM = ligneSuivante;
						DateTime dateDebLigneSuiv = new DateTime(ligneSuivante.getDateDebut()).withMillisOfDay(0).minusDays(1);
						DateTime dateFinLignePrec = new DateTime(lignePrecedente.getDateFin()).withMillisOfDay(0);
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursMA += ligneSuivante.getDuree();
							dejaComptabilise = true;
						} else {
							compteurJoursMA = 0.0;
							dejaComptabilise = false;
						}
					}
				}
				if (90 > compteurJoursMA && compteurJoursMA > 30) {
					// on crée la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(nomatrAgent);
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
					Date datePrev = new DateTime(dernierAM.getDateFin()).withMillisOfDay(0).plusDays(2).toDate();
					sm.setDatePrevisionVisite(datePrev);
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
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(), moisChoisi, anneeChoisi);
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
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
									"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite().toString();
							if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(),
							sm.getIdServi(), sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
					nbCas4++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 4");
		}
		logger.info("Nb de cas 4 : " + nbCas4);
	}

	private void performCalculCas3(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 3 : AT avec ITT>15jours
		// on liste toutes les absences pour le mois et l'année donnée
		int nbCas3 = 0;
		try {
			DateTime dateDeb = new DateTime().withYear(anneeChoisi).withMonthOfYear(moisChoisi).withDayOfMonth(1).withMillisOfDay(0);
			DateTime dateFin = new DateTime().withYear(anneeChoisi).withMonthOfYear(moisChoisi).dayOfMonth().withMaximumValue().withMillisOfDay(0);

			String dateMin = sdfyyyyMMdd.format(dateDeb);
			String dateMax = sdfyyyyMMdd.format(dateFin);

			ArrayList<DemandeDto> listMaladie = (ArrayList<DemandeDto>) absService.getListeDemandes(dateMin, dateMax,
					Arrays.asList(EnumEtatAbsence.VALIDEE.getCode(), EnumEtatAbsence.PRISE.getCode()).toString().replace("[", "").replace("]", "")
							.replace(" ", ""),
					EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode(), null, EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

			// on liste les matricules
			List<Integer> listIdAgent = new ArrayList<Integer>();
			if (null != listMaladie) {
				for (DemandeDto maladie : listMaladie) {
					if (!listIdAgent.contains(maladie.getAgentWithServiceDto().getIdAgent())) {
						listIdAgent.add(maladie.getAgentWithServiceDto().getIdAgent());
					}
				}
			}

			// pour chaque matricule trouvé on va cherche la liste de ses
			// absences
			// on regarde si il se suivent, que le nombre de jours est > 15
			for (int i = 0; i < listIdAgent.size(); i++) {
				Integer idAgent = listIdAgent.get(i);
				Integer nomatrAgent = getNoMatr(idAgent);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), nomatrAgent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}

				List<DemandeDto> listeATAgent = getListDemandeDtoByAgent(idAgent, listMaladie);
				Double compteurJoursITT = 0.0;
				DemandeDto dernierAT = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeATAgent.size(); j++) {
					DemandeDto lignePrecedente = listeATAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursITT = compteurJoursITT + lignePrecedente.getDuree();
					}
					dernierAT = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeATAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale a datFin de la precdente + 1
						DemandeDto ligneSuivante = listeATAgent.get(j + 1);
						dernierAT = ligneSuivante;

						DateTime dateDebLigneSuiv = new DateTime(ligneSuivante.getDateDebut()).withMillisOfDay(0).minusDays(1);
						DateTime dateFinLignePrec = new DateTime(lignePrecedente.getDateFin()).withMillisOfDay(0);
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursITT = compteurJoursITT + ligneSuivante.getDuree();
							dejaComptabilise = true;
						} else {
							compteurJoursITT = 0.0;
							dejaComptabilise = false;
						}
					}
				}
				if (compteurJoursITT > 15) {
					// on crée la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(nomatrAgent);
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
					Date datePrev = new DateTime(dernierAT.getDateFin()).plusDays(1).toDate();
					sm.setDatePrevisionVisite(datePrev);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_AT_ITT_15JOURS.getCode());
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
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(), moisChoisi, anneeChoisi);
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
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
									"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite().toString();
							if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(),
							sm.getIdServi(), sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
					nbCas3++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 3");
		}
		logger.info("Nb de cas 3 : " + nbCas3);
	}

	private void performCalculCas1(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 1 : A la demande de l'agent ou du service
		// on liste toutes les visites medicales du type "a la demande..."
		Medecin m = getMedecinDao().chercherMedecinARenseigner("A", "RENSEIGNER");
		ArrayList<VisiteMedicale> listeSMCas1 = getVisiteMedicaleDao().listerVisiteMedicalePourSMCas1(EnumMotifVisiteMed.VM_DEMANDE_AGENT.getCode(),
				EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getCode(), m.getIdMedecin());
		int nbCas1 = 0;
		for (int i = 0; i < listeSMCas1.size(); i++) {
			VisiteMedicale vm = listeSMCas1.get(i);
			// on crée la nouvelle ligne
			SuiviMedical sm = new SuiviMedical();
			Agent agent = getAgentDao().chercherAgent(vm.getIdAgent());
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}

			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}

			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(agent.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse("15/" + moisChoisi + "/" + anneeChoisi);
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
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(), moisChoisi, anneeChoisi);
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
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite().toString();
					if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
			nbCas1++;
		}
		logger.info("Nb de cas 1 : " + nbCas1);
	}

	private void performCalculCas8(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 8 : CONVOCATION NON EXECUTEE
		// on liste tous les suivi médicaux de type "CONVOQUE" du mois précédent
		int nbCas8 = 0;
		try {
			ArrayList<SuiviMedical> listeSMCas8 = getSuiviMedDao().listerSuiviMedicalNonEffectue(moisChoisi, anneeChoisi,
					EnumEtatSuiviMed.PLANIFIE.getCode());
			for (int i = 0; i < listeSMCas8.size(); i++) {
				// on crée une nouvelle ligne avec les memes informations
				// sauf pour le statut et le service on le remet a jour
				SuiviMedical smAncien = listeSMCas8.get(i);
				// on crée la nouvelle ligne
				SuiviMedical sm = new SuiviMedical();
				Agent agent = getAgentDao().chercherAgentParMatricule(smAncien.getNomatr());
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), agent.getNomatr());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
						continue;
					}
				}
				Affectation aff = null;
				try {
					aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
				} catch (Exception e) {

				}
				FichePoste fp = null;
				if (aff != null && aff.getIdFichePoste() != null) {
					try {
						fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
					} catch (Exception e) {

					}
				}
				sm.setIdAgent(smAncien.getIdAgent());
				sm.setNomatr(smAncien.getNomatr());
				sm.setAgent(smAncien.getAgent());
				sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
				sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
				sm.setIdServi(fp != null ? fp.getIdServi() : null);
				VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
				sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
				sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
				sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
				sm.setDatePrevisionVisite(smAncien.getDatePrevisionVisite());
				sm.setIdMotifVm(smAncien.getIdMotifVm());
				// ATTENTION : si mois de la date de prochainRDV < moisChoisi
				// alors
				// il faut incrémenter le compteur de 1 pour NbVisiteRatées
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				if (smAncien.getDateProchaineVisite() != null
						&& Integer.valueOf(sdf.format(smAncien.getDateProchaineVisite()).toString().substring(5, 7)) < moisChoisi) {
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
					SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(smAncien.getIdAgent(), moisChoisi, anneeChoisi);
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
						String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy");
						String datePrevision = sm.getDatePrevisionVisite().toString();
						if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
							continue;
						} else {
							getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
						}
					} else {
						continue;
					}
				} catch (Exception e) {
					// aucune ligne n'a été trouvée alors on continue
				}
				getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
						sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
						sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(),
						sm.getIdServi(), sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
				nbCas8++;
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 8");
		}
		logger.info("Nb de cas 8 : " + nbCas8);
	}

	private void performCalculCas7(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 7 : Changement de PA
		// on liste toutes les PA hors-effectif
		// on Vérifie qu'il y a bien une PA normale apres cette PA hors-effectif
		ArrayList<PositionAdmAgent> listePACas7 = PositionAdmAgent.listerPositionAdmAgentHorsEffectif(getTransaction(), moisChoisi, anneeChoisi);
		int nbCas7 = 0;
		for (int i = 0; i < listePACas7.size(); i++) {
			// on regarde pour cette liste de PA si il en existe une qui suit en
			// NORMALE 01
			PositionAdmAgent paHorsEffectif = listePACas7.get(i);
			PositionAdmAgent paSuivante = PositionAdmAgent.chercherPositionAdmAgent(getTransaction(), paHorsEffectif.getNomatr(),
					paHorsEffectif.getDatfin());
			if (paSuivante != null && paSuivante.getCdpadm() != null) {
				if (paSuivante.getCdpadm().equals("01")) {
					// si c'est bon alors on crée le suiviMedical
					SuiviMedical sm = new SuiviMedical();
					Agent agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(paSuivante.getNomatr()));
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = null;
					try {
						aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
					} catch (Exception e) {

					}
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						try {
							fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
						} catch (Exception e) {

						}
					}
					sm.setIdAgent(agent.getIdAgent());
					sm.setNomatr(agent.getNomatr());
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
					sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
					sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
					sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
					Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(Services.enleveJours(paSuivante.getDatdeb(), 15));
					sm.setDatePrevisionVisite(d2);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_CHANGEMENT_PA.getCode());
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
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentNomatrMoisetAnnee(Integer.valueOf(paHorsEffectif.getNomatr()),
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
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd",
									"dd/MM/yyyy");
							String datePrevision = sm.getDatePrevisionVisite().toString();
							if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
								continue;
							} else {
								getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
							}
						} else {
							continue;
						}
					} catch (Exception e) {
						// aucune ligne n'a été trouvée alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(),
							sm.getIdServi(), sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
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

	private void performCalculCas6(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 6 : Visite Nouvel arrivant
		// on liste tous les nouveaux arrivant
		ArrayList<Agent> listeAgentCas6 = getAgentDao().listerAgentNouveauxArrivant(moisChoisi, anneeChoisi);
		int nbCas6 = 0;
		for (int i = 0; i < listeAgentCas6.size(); i++) {
			SuiviMedical sm = new SuiviMedical();
			Agent agent = listeAgentCas6.get(i);
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(agent.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
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
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(agent.getIdAgent(), moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur a
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite().toString();
					if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
			nbCas6++;
		}
		logger.info("Nb de cas 6 : " + nbCas6);
	}

	private void performCalculCas2(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N 2 : Visite Réguliere
		// on liste toutes les visites medicales
		// dont la dateVM + durée validitéVM = mois et année choisie du calcul
		ArrayList<VisiteMedicale> listeVMCas2 = getVisiteMedicaleDao().listerVisiteMedicalePourSMCas2(moisChoisi, anneeChoisi);
		int nbCas2 = 0;
		for (int i = 0; i < listeVMCas2.size(); i++) {
			VisiteMedicale vm = listeVMCas2.get(i);

			SuiviMedical sm = new SuiviMedical();
			Agent agent = getAgentDao().chercherAgent(vm.getIdAgent());
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(), agent.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				continue;
			} else {
				if (pa == null) {
					continue;
				}
			}
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
					continue;
				}
			}
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
			} catch (Exception e) {

			}
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				try {
					fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
				} catch (Exception e) {

				}
			}
			sm.setIdAgent(vm.getIdAgent());
			sm.setNomatr(agent.getNomatr());
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServiceAds(fp != null ? fp.getIdServiceAds() : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			VisiteMedicale derniereVisite = getVisiteMedicaleDao().chercherDerniereVisiteMedicale(agent.getIdAgent());
			sm.setDateDerniereVisite(derniereVisite == null ? null : derniereVisite.getDateDerniereVisite());
			sm.setIdRecommandationDerniereVisite(derniereVisite == null ? null : derniereVisite.getIdRecommandation());
			sm.setCommentaireDerniereViste(derniereVisite == null ? null : derniereVisite.getCommentaire());
			Date d2 = new SimpleDateFormat("dd/MM/yyyy")
					.parse(Services.ajouteMois(new SimpleDateFormat("dd/MM/yyyy").format(vm.getDateDerniereVisite()), vm.getDureeValidite()));
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
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(vm.getIdAgent(), moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur a
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy");
					String datePrevision = sm.getDatePrevisionVisite().toString();
					if (Services.compareDates(dateExistePrevision, datePrevision) > 0) {
						continue;
					} else {
						getSuiviMedDao().supprimerSuiviMedicalById(smTemp.getIdSuiviMed());
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				// aucune ligne n'a été trouvée alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(), sm.getDateDerniereVisite(),
					sm.getDatePrevisionVisite(), sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance(), sm.getIdServiceAds(), sm.getIdServi(),
					sm.getIdRecommandationDerniereVisite(), sm.getCommentaireDerniereViste());
			nbCas2++;
		}
		logger.info("Nb de cas 2 : " + nbCas2);
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
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_MOIS Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MOIS() {
		return getLB_MOIS();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MOIS Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_MOIS_SELECT() {
		return getZone(getNOM_LB_MOIS_SELECT());
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date de
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

	private Integer getNoMatr(Integer idAgent) {
		if (null != idAgent) {
			return idAgent - 9000000;
		}
		return idAgent;
	}

	private List<DemandeDto> getListDemandeDtoByAgent(Integer idAgent, List<DemandeDto> listMaladies) {

		List<DemandeDto> result = new ArrayList<DemandeDto>();

		if (null != listMaladies) {
			for (DemandeDto demande : listMaladies) {
				if (demande.getAgentWithServiceDto().getIdAgent().equals(idAgent)) {
					result.add(demande);
				}
			}
		}

		Collections.sort(result, new Comparator<DemandeDto>() {
			@Override
			public int compare(DemandeDto obj1, DemandeDto obj2) {
				return obj1.getDateDebut().compareTo(obj2.getDateDebut());
			}
		});

		return result;
	}

	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	private void setLB_ANNEE(String[] listeANNEE) {
		LB_ANNEE = listeANNEE;
	}

	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	public List<Integer> getListeMois() {
		return listeMois;
	}

	public void setListeMois(List<Integer> listeMois) {
		this.listeMois = listeMois;
	}

	public List<Integer> getListeAnnee() {
		return listeAnnee;
	}

	public void setListeAnnee(List<Integer> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

}
