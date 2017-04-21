package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeDto;
import nc.mairie.gestionagent.eae.dto.EaeEvaluationDto;
import nc.mairie.gestionagent.eae.dto.FormRehercheGestionEae;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.avancement.AvancementCapPrintJob;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.Cap;
import nc.mairie.metier.parametrage.CorpsCap;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementCapPrintJobDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.parametrage.CadreEmploiDao;
import nc.mairie.spring.dao.metier.parametrage.CapDao;
import nc.mairie.spring.dao.metier.parametrage.CorpsCapDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IEaeService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctPrepaCAP extends BasicProcess {

	/**
	 * 
	 */
	private static final long						serialVersionUID		= 1L;

	private Logger									logger					= LoggerFactory.getLogger(OeAVCTFonctPrepaCAP.class);

	public static final int							STATUT_RECHERCHER_AGENT	= 1;

	private String[]								LB_ANNEE;
	private String[]								LB_AVIS_CAP_AD;
	private String[]								LB_AVIS_CAP_CLASSE;
	private String[]								LB_FILIERE;

	private ArrayList<FiliereGrade>					listeFiliere;

	private Hashtable<Integer, AvisCap>				hashAvisCAP;

	private String[]								listeAnnee;
	private String									anneeSelect;
	private String									urlFichier;

	private ArrayList<AvancementFonctionnaires>		listeAvct;
	private ArrayList<AvisCap>						listeAvisCAPMinMoyMax;
	private ArrayList<AvisCap>						listeAvisCAPFavDefav;

	private CapDao									capDao;
	private CorpsCapDao								corpsCapDao;
	private AvancementCapPrintJobDao				avancementCapPrintJobDao;
	private CadreEmploiDao							cadreEmploiDao;
	private MotifAvancementDao						motifAvancementDao;

	private Hashtable<Cap, ArrayList<CadreEmploi>>	hashListeImpression;
	ArrayList<CadreEmploi>							listeImpression			= new ArrayList<CadreEmploi>();

	private ArrayList<AvancementCapPrintJob>		listeAvancementCapPrintJob;

	private AutreAdministrationDao					autreAdministrationDao;
	private AvisCapDao								avisCapDao;
	private AvancementFonctionnairesDao				avancementFonctionnairesDao;
	private AgentDao								agentDao;

	private IAdsService								adsService;

	private IRadiService							radiService;

	private ISirhService							sirhService;

	private IEaeService								eaeService;

	private SimpleDateFormat						sdfFormatDate			= new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
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

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
		}

		initialiseTableauImpression();
		initialiseTableauImpressionJob();
	}

	private void initialiseTableauImpressionJob() throws Exception {
		setListeAvancementCapPrintJob(getAvancementCapPrintJobDao().listerAvancementCapPrintJob());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for (int i = 0; i < getListeAvancementCapPrintJob().size(); i++) {
			AvancementCapPrintJob job = (AvancementCapPrintJob) getListeAvancementCapPrintJob().get(i);
			addZone(getNOM_ST_CODE_CAP_JOB(i), job.getCodeCap());
			addZone(getNOM_ST_CADRE_EMPLOI_JOB(i), job.getLibCadreEmploi());
			addZone(getNOM_ST_USER_JOB(i), job.getLogin());
			addZone(getNOM_ST_DATE_JOB(i), sdf.format(job.getDateSubmission()));
			addZone(getNOM_ST_ETAT_JOB(i), job.getStatut() == null ? "en attente" : job.getStatut());
			addZone(getNOM_ST_TYPE_JOB(i), job.isEaes() ? "oui" : Const.CHAINE_VIDE);
			addZone(getNOM_ST_JOB_ID(i), job.getJobId() == null ? Const.CHAINE_VIDE : job.getJobId());
		}
	}

	private void afficheListeAvancement(HttpServletRequest request) throws Exception {

		CampagneEaeDto campagneEAE = null;
		List<EaeDto> listEae = new ArrayList<>();
		try {
			campagneEAE = eaeService.getCampagneAnneePrecedenteLight(getAgentConnecte(request).getIdAgent(), Integer.valueOf(getAnneeSelect()));
			if (campagneEAE != null && campagneEAE.getIdCampagneEae() != null) {
				// on cherche l'eae correspondant ainsi que l'eae evaluation
				FormRehercheGestionEae form = new FormRehercheGestionEae();
				form.setIdCampagneEae(campagneEAE.getIdCampagneEae());
				listEae = eaeService.getListeEaeDtoLight(getAgentConnecte(request).getIdAgent(), form);
			}
		} catch (Exception e) {
			// pas de campagne pour cette année
		}

		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires av = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = av.getIdAvct();
			Agent agent = getAgentDao().chercherAgent(av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DIRECTION(i),
					Services.estNumerique(av.getDirectionService())
							? getAutreAdministrationDao().chercherAutreAdministration(Integer.valueOf(av.getDirectionService())).getLibAutreAdmin()
							: av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i), (av.getCdcadr() == null ? "&nbsp;" : av.getCdcadr()) + " <br> " + av.getFiliere());
			PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
			addZone(getNOM_ST_PA(i), pa.getLiPAdm());
			addZone(getNOM_ST_DATE_DEBUT(i), sdfFormatDate.format(av.getDateGrade()));
			// cf redmine #13988
			Date dateAvctAncienMin = getAvancementFonctionnairesDao().getDateAvancementsMinimaleAncienne(av.getIdAgent());
			addZone(getNOM_ST_DATE_AVCT_MIN_ANCIEN(i), dateAvctAncienMin != null ? sdfFormatDate.format(dateAvctAncienMin) : "&nbsp;");
			addZone(getNOM_ST_GRADE_ANCIEN(i), av.getGrade());
			addZone(getNOM_ST_GRADE_NOUVEAU(i), (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct().toString());
			addZone(getNOM_ST_DATE_AVCT(i),
					(av.getDateAvctMini() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateAvctMini())) + " <br> "
							+ sdfFormatDate.format(av.getDateAvctMoy()) + " <br> "
							+ (av.getDateAvctMaxi() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateAvctMaxi())));

			addZone(getNOM_CK_VALID_SEF(i), av.getEtat().equals(EnumEtatAvancement.SEF.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			String user = av.getUserVerifSef() == null ? "&nbsp;" : av.getUserVerifSef();
			String heure = av.getHeureVerifSef() == null ? "&nbsp;" : av.getHeureVerifSef();
			String date = av.getDateVerifSef() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateVerifSef());
			addZone(getNOM_ST_USER_VALID_SEF(i), user + " <br> " + date + " <br> " + heure);
			addZone(getNOM_EF_ORDRE_MERITE(i), av.getOrdreMerite().equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : av.getOrdreMerite());

			// avis SHD
			// on cherche la camapagne correspondante
			String avisSHD = Const.CHAINE_VIDE;
			MotifAvancement motif = null;
			if (campagneEAE != null) {
				EaeDto eaeAgent = eaeService.getEaeDtoByIdAgent(listEae, av.getIdAgent());

				if (eaeAgent.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) || eaeAgent.getEtat().equals(EnumEtatEAE.FINALISE.getCode())) {
					EaeEvaluationDto eaeEvaluation = eaeAgent.getEvaluation();
					if (String.valueOf(av.getIdMotifAvct()).equals("7")) {
						avisSHD = eaeEvaluation.getPropositionAvancement() == null ? Const.CHAINE_VIDE
								: eaeEvaluation.getPropositionAvancement().getCourant();
					} else if (String.valueOf(av.getIdMotifAvct()).equals("5")) {
						avisSHD = eaeEvaluation.getAvisRevalorisation() == null ? Const.CHAINE_VIDE
								: eaeEvaluation.getAvisRevalorisation() ? "FAV" : "DEFAV";
					} else if (String.valueOf(av.getIdMotifAvct()).equals("4")) {
						avisSHD = eaeEvaluation.getAvisChangementClasse() == null ? Const.CHAINE_VIDE
								: eaeEvaluation.getAvisChangementClasse() ? "FAV" : "DEFAV";
					} else if (String.valueOf(av.getIdMotifAvct()).equals("6")) {
						avisSHD = "MOY";
					}
				}
			}
			// motif Avct
			if (av.getIdMotifAvct() != null) {
				motif = getMotifAvancementDao().chercherMotifAvancement(av.getIdMotifAvct());
			}
			addZone(getNOM_ST_MOTIF_AVCT(i), (motif == null ? Const.CHAINE_VIDE : motif.getCode()) + "<br/>" + avisSHD);

			// duree VDN
			if (av.getIdAvisCap() == null) {
				if (!avisSHD.equals(Const.CHAINE_VIDE)) {
					if (avisSHD.equals("MAXI")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(3))));
					} else if (avisSHD.equals("MOY")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
					} else if (avisSHD.equals("MINI")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(1))));
					} else if (avisSHD.equals("FAV")) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
					} else if (avisSHD.equals("DEFAV")) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(5))));
					}

				} else {
					addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
					addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
				}

			} else {
				// si la SEF a deja coche
				if (av.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
					addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), av.getIdAvisCap() == null ? Const.CHAINE_VIDE
							: String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(av.getIdAvisCap()))));
					addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), av.getIdAvisCap() == null ? Const.CHAINE_VIDE
							: String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(av.getIdAvisCap()))));
				} else {
					if (!avisSHD.equals(Const.CHAINE_VIDE)) {
						if (avisSHD.equals("MAXI")) {
							addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(3))));
						} else if (avisSHD.equals("MOY")) {
							addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
						} else if (avisSHD.equals("MINI")) {
							addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(1))));
						} else if (avisSHD.equals("FAV")) {
							addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
						} else if (avisSHD.equals("DEFAV")) {
							addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(5))));
						}

					} else {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), av.getIdAvisCap() == null ? Const.CHAINE_VIDE
								: String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(av.getIdAvisCap()))));
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), av.getIdAvisCap() == null ? Const.CHAINE_VIDE
								: String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(av.getIdAvisCap()))));
					}
				}
			}

		}
	}

	private void initialiseTableauImpression() throws Exception {
		for (int i = 0; i < getListeImpression().size(); i++) {
			addZone(getNOM_CK_TAB_SHD(i), getCHECKED_OFF());
			addZone(getNOM_CK_EAE_SHD(i), getCHECKED_OFF());
			addZone(getNOM_CK_TAB_VDN(i), getCHECKED_OFF());
			addZone(getNOM_CK_EAE_VDN(i), getCHECKED_OFF());
		}

		// Si liste impression
		if (getListeImpression().size() == 0) {
			getHashListeImpression().clear();

			ArrayList<Cap> listeCap = new ArrayList<Cap>();
			listeCap.addAll(getCapDao().listerCap());

			for (int i = 0; i < listeCap.size(); i++) {
				Cap cap = listeCap.get(i);
				ArrayList<CorpsCap> listeCorps = getCorpsCapDao().listerCorpsCapParCap(cap.getIdCap());
				ArrayList<CadreEmploi> listeCadreEmploi = new ArrayList<CadreEmploi>();
				for (CorpsCap corps : listeCorps) {
					// on cherche le cdgeng correspondant
					GradeGenerique gradeWithCadreEmploi = GradeGenerique.chercherGradeGenerique(getTransaction(), corps.getCdgeng());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (gradeWithCadreEmploi.getIdCadreEmploi() != null) {
							try {
								CadreEmploi cadreEmp = getCadreEmploiDao()
										.chercherCadreEmploi(Integer.valueOf(gradeWithCadreEmploi.getIdCadreEmploi()));
								if (!listeCadreEmploi.contains(cadreEmp)) {
									listeCadreEmploi.add(cadreEmp);
								}
							} catch (Exception e) {

							}
						}
					}

				}
				getHashListeImpression().put(cap, listeCadreEmploi);
			}

			Enumeration<Cap> nb = getHashListeImpression().keys();
			ArrayList<Cap> listeTemp = new ArrayList<Cap>();
			while (nb.hasMoreElements()) {
				Cap cap = (Cap) nb.nextElement();
				listeTemp.add(cap);
			}
			setListeImpression(null);
			ArrayList<CadreEmploi> listeTempCadreEmp = new ArrayList<CadreEmploi>();
			int k = 0;
			for (int i = 0; i < listeTemp.size(); i++) {
				Cap cap = listeTemp.get(i);
				ArrayList<CadreEmploi> cadre = (ArrayList<CadreEmploi>) getHashListeImpression().get(cap);
				for (int j = 0; j < cadre.size(); j++) {
					CadreEmploi cadreEmp = cadre.get(j);
					listeTempCadreEmp.add(cadreEmp);
					addZone(getNOM_ST_CODE_CAP(k), cap.getCodeCap());
					addZone(getNOM_ST_CADRE_EMPLOI(k), cadreEmp.getLibCadreEmploi());
					addZone(getNOM_CK_TAB_SHD(k), getCHECKED_OFF());
					addZone(getNOM_CK_EAE_SHD(k), getCHECKED_OFF());
					addZone(getNOM_CK_TAB_VDN(k), getCHECKED_OFF());
					addZone(getNOM_CK_EAE_VDN(k), getCHECKED_OFF());
					k++;
				}

			}
			setListeImpression(listeTempCadreEmp);

		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getCapDao() == null) {
			setCapDao(new CapDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCorpsCapDao() == null) {
			setCorpsCapDao(new CorpsCapDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getAvancementCapPrintJobDao() == null) {
			setAvancementCapPrintJobDao(new AvancementCapPrintJobDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCadreEmploiDao() == null) {
			setCadreEmploiDao(new CadreEmploiDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getMotifAvancementDao() == null) {
			setMotifAvancementDao(new MotifAvancementDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationDao() == null) {
			setAutreAdministrationDao(new AutreAdministrationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvisCapDao() == null) {
			setAvisCapDao(new AvisCapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementFonctionnairesDao() == null) {
			setAvancementFonctionnairesDao(new AvancementFonctionnairesDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (null == eaeService) {
			eaeService = (IEaeService) context.getBean("eaeService");
		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	/**
	 * Initialisation des liste deroulantes de l'écran Avancement des
	 * fonctionnaires.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) ServletAgent.getMesParametres().get("ANNEE_AVCT");
			setListeAnnee(new String[1]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
			setAnneeSelect(String.valueOf(Integer.parseInt(anneeCourante) + 1));
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAPMinMoyMax() == null || getListeAvisCAPMinMoyMax().size() == 0) {
			ArrayList<AvisCap> avis = getAvisCapDao().listerAvisCapMinMoyMax();
			setListeAvisCAPMinMoyMax(avis);

			int[] tailles = { 7 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvisCap> list = getListeAvisCAPMinMoyMax().listIterator(); list.hasNext();) {
				AvisCap fili = (AvisCap) list.next();
				String ligne[] = { fili.getLibLongAvisCap() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_AVIS_CAP_AD(aFormat.getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPMinMoyMax().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPMinMoyMax().get(i);
				getHashAvisCAP().put(ac.getIdAvisCap(), ac);
			}
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAPFavDefav() == null || getListeAvisCAPFavDefav().size() == 0) {
			ArrayList<AvisCap> avis = getAvisCapDao().listerAvisCapFavDefav();
			setListeAvisCAPFavDefav(avis);

			int[] tailles = { 7 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvisCap> list = getListeAvisCAPFavDefav().listIterator(); list.hasNext();) {
				AvisCap fili = (AvisCap) list.next();
				String ligne[] = { fili.getLibLongAvisCap() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_AVIS_CAP_CLASSE(aFormat.getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPFavDefav().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPFavDefav().get(i);
				getHashAvisCAP().put(ac.getIdAvisCap(), ac);
			}

		}

		// Si liste filiere vide alors affectation
		if (getListeFiliere() == null || getListeFiliere().size() == 0) {
			setListeFiliere(FiliereGrade.listerFiliereGrade(getTransaction()));

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<FiliereGrade> list = getListeFiliere().listIterator(); list.hasNext();) {
				FiliereGrade fili = (FiliereGrade) list.next();
				String ligne[] = { fili.getLibFiliere() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FILIERE(aFormat.getListeFormatee(true));

		}
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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

			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}

			// Si clic sur le bouton PB_IMPRIMER
			if (testerParametre(request, getNOM_PB_IMPRIMER())) {
				return performPB_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_RAFRAICHIR_IMPRIMER
			if (testerParametre(request, getNOM_PB_RAFRAICHIR_IMPRIMER())) {
				return performPB_RAFRAICHIR_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_CONSULTER_TABLEAU
			for (int i = 0; i < getListeImpression().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_TABLEAU_AVIS_VDN(i, getVAL_ST_CODE_CAP(i), getVAL_ST_CADRE_EMPLOI(i)))) {
					return performPB_CONSULTER_TABLEAU_AVIS_VDN(request, getVAL_ST_CODE_CAP(i), getVAL_ST_CADRE_EMPLOI(i));
				}
			}

			// Si clic sur le bouton PB_CONSULTER_TABLEAU_AVIS_SHD
			for (int i = 0; i < getListeImpression().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_TABLEAU_AVIS_SHD(i, getVAL_ST_CODE_CAP(i), getVAL_ST_CADRE_EMPLOI(i)))) {
					return performPB_CONSULTER_TABLEAU_AVIS_SHD(request, getVAL_ST_CODE_CAP(i), getVAL_ST_CADRE_EMPLOI(i));
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		setAnneeSelect(annee);

		// Recuperation filiere
		FiliereGrade filiere = null;
		int indiceFiliere = (Services.estNumerique(getVAL_LB_FILIERE_SELECT()) ? Integer.parseInt(getVAL_LB_FILIERE_SELECT()) : -1);
		if (indiceFiliere > 0) {
			filiere = (FiliereGrade) getListeFiliere().get(indiceFiliere - 1);
		}

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		// recuperation du service
		List<String> listeSousService = null;
		if (getVAL_ST_ID_SERVICE_ADS().length() != 0) {
			// on recupere les sous-service du service selectionne
			listeSousService = adsService.getListSiglesWithEnfantsOfEntite(new Integer(getVAL_ST_ID_SERVICE_ADS()));
		}

		String reqEtat = " and (ETAT='" + EnumEtatAvancement.SGC.getValue() + "' or ETAT='" + EnumEtatAvancement.SEF.getValue() + "')";
		setListeAvct(getAvancementFonctionnairesDao().listerAvancementAvecAnneeEtat(Integer.valueOf(annee), reqEtat,
				filiere == null ? null : filiere.getLibFiliere().trim(), agent == null ? null : agent.getIdAgent(), listeSousService, null, null, "non"));

		afficheListeAvancement(request);
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agent = null;
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto ag = radiService.getAgentCompteADByLogin(user.getUserName());
		if (ag == null) {
			// "ERR183",
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(ag.getEmployeeNumber()));

		for (int i = 0; i < getListeImpression().size(); i++) {
			if (getVAL_CK_TAB_SHD(i).equals(getCHECKED_ON())) {
				try {
					Cap cap = getCapDao().chercherCapByCodeCap(getVAL_ST_CODE_CAP(i));
					CadreEmploi cadre = getCadreEmploiDao().chercherCadreEmploiByLib(getVAL_ST_CADRE_EMPLOI(i));
					// on crée l'entrée dans la table du job
					getAvancementCapPrintJobDao().creerAvancementCapPrintJob(agent.getIdAgent(), user.getUserName(), cap.getIdCap(), cap.getCodeCap(),
							cadre.getIdCadreEmploi(), cadre.getLibCadreEmploi(), false, true);
				} catch (Exception e) {
					getTransaction().traiterErreur();
					// "ERR182",
					// "Une erreur est survenue dans la génération du tableau.
					// Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
					return false;
				}

			}
			if (getVAL_CK_EAE_SHD(i).equals(getCHECKED_ON())) {
				try {
					Cap cap = getCapDao().chercherCapByCodeCap(getVAL_ST_CODE_CAP(i));
					CadreEmploi cadre = getCadreEmploiDao().chercherCadreEmploiByLib(getVAL_ST_CADRE_EMPLOI(i));
					// on crée l'entrée dans la table du job
					getAvancementCapPrintJobDao().creerAvancementCapPrintJob(agent.getIdAgent(), user.getUserName(), cap.getIdCap(), cap.getCodeCap(),
							cadre.getIdCadreEmploi(), cadre.getLibCadreEmploi(), true, true);
				} catch (Exception e) {
					// "ERR182",
					// "Une erreur est survenue dans la génération du tableau.
					// Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
					return false;
				}

			}
			if (getVAL_CK_TAB_VDN(i).equals(getCHECKED_ON())) {
				try {
					Cap cap = getCapDao().chercherCapByCodeCap(getVAL_ST_CODE_CAP(i));
					CadreEmploi cadre = getCadreEmploiDao().chercherCadreEmploiByLib(getVAL_ST_CADRE_EMPLOI(i));
					// on crée l'entrée dans la table du job
					getAvancementCapPrintJobDao().creerAvancementCapPrintJob(agent.getIdAgent(), user.getUserName(), cap.getIdCap(), cap.getCodeCap(),
							cadre.getIdCadreEmploi(), cadre.getLibCadreEmploi(), false, false);
				} catch (Exception e) {
					// "ERR182",
					// "Une erreur est survenue dans la génération du tableau.
					// Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
					return false;
				}

			}
			if (getVAL_CK_EAE_VDN(i).equals(getCHECKED_ON())) {
				try {
					Cap cap = getCapDao().chercherCapByCodeCap(getVAL_ST_CODE_CAP(i));
					CadreEmploi cadre = getCadreEmploiDao().chercherCadreEmploiByLib(getVAL_ST_CADRE_EMPLOI(i));
					// on crée l'entrée dans la table du job
					getAvancementCapPrintJobDao().creerAvancementCapPrintJob(agent.getIdAgent(), user.getUserName(), cap.getIdCap(), cap.getCodeCap(),
							cadre.getIdCadreEmploi(), cadre.getLibCadreEmploi(), true, false);
				} catch (Exception e) {
					// "ERR182",
					// "Une erreur est survenue dans la génération du tableau.
					// Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
					return false;
				}

			}
		}
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		// on sauvegarde l'etat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer idAvct = avct.getIdAvct();
			// on fait les modifications
			// on traite l'etat
			if (getVAL_CK_VALID_SEF(idAvct).equals(getCHECKED_ON())) {
				// si la ligne est cochée
				// on regarde si l'etat est deja SEF
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat a SEF et on met a jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSef(user.getUserName());
					avct.setDateVerifSef(new Date());
					avct.setHeureVerifSef(heureAction);
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				}
				// on met egalement a jour la CAP à  laquelle appartient l'agent
				// on recupere la categorie de l'agent
				String type = avct.getCodeCategorie() == 1 || avct.getCodeCategorie() == 2 ? "COMMUNAL"
						: (avct.getCodeCategorie() == 18 || avct.getCodeCategorie() == 20) ? "TERRITORIAL" : null;
				if (type != null) {
					logger.debug("Recherche CAP : [idAvct = " + avct.getIdAvct() + ", idAgent=" + avct.getIdAgent() + ",type=" + type + "]");
					try {
						Cap capAgent = getCapDao().chercherCapByAgent(avct.getIdAgent(), type, avct.getAnnee());
						if (capAgent != null & capAgent.getIdCap() != null) {
							avct.setIdCap(capAgent.getIdCap());
						}
					} catch (Exception e) {
						// aucune CAP trouvée
					}
				}

			} else {
				// si la ligne n'est pas cochée
				// on regarde quel etat son etat
				// --> si SEF alors on met a jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSef(user.getUserName());
					avct.setDateVerifSef(new Date());
					avct.setHeureVerifSef(heureAction);
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				}
			}

			if (avct.getIdMotifAvct() != null) {
				if (String.valueOf(avct.getIdMotifAvct()).equals("7")) {
					// on traite l'avis CAP
					int indiceAvisCapMinMoyMax = (Services.estNumerique(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct))
							? Integer.parseInt(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) : -1);
					if (indiceAvisCapMinMoyMax != -1) {
						Integer idAvisCap = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMax)).getIdAvisCap();
						avct.setIdAvisCap(idAvisCap);
						// on traite l'odre de merite
						// on test si "moyenne" choisi alors on remete a vide
						// ordre du
						// mérite
						if (indiceAvisCapMinMoyMax == 0) {
							String ordre = getVAL_EF_ORDRE_MERITE(idAvct);
							if (!ordre.equals(Const.CHAINE_VIDE)) {
								avct.setOrdreMerite(ordre);
							} else {
								avct.setOrdreMerite(null);
							}
						} else {
							avct.setOrdreMerite(null);
						}
					}
				} else if (String.valueOf(avct.getIdMotifAvct()).equals("6") || String.valueOf(avct.getIdMotifAvct()).equals("3")) {
					avct.setIdAvisCap(2);
					avct.setOrdreMerite(null);
				} else {
					int indiceAvisCapFavDefav = (Services.estNumerique(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct))
							? Integer.parseInt(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) : -1);
					if (indiceAvisCapFavDefav != -1) {
						Integer idAvisCap = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefav)).getIdAvisCap();
						avct.setIdAvisCap(idAvisCap);
					}
					avct.setOrdreMerite(null);
				}
			} else {
				avct.setIdAvisCap(null);
				avct.setOrdreMerite(null);
			}

			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(),
					avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(),
					avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(),
					avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(),
					avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
					avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(),
					avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
					avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(),
					avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
					avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
					avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(),
					avct.getIdCap(), avct.getCodePa(), avct.isAutre());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		// on remet la liste a vide afin qu'elle soit de nouveau initialisee
		performPB_FILTRER(request);
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CATEGORIE
	 * Date de création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_AVCT
	 * Date de création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_DEBUT
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DIRECTION
	 * Date de création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_GRADE_LIB
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NUM_AVCT Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ETAT Date de
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
	public ArrayList<AvancementFonctionnaires> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementFonctionnaires>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementFonctionnaires> listeAvct) {
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
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_ANNEE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
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
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP :
	 * CK_VALID_SEF Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_SEF(int i) {
		return "NOM_CK_VALID_SEF_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case a cocher : CK_VALID_SEF Date de création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_USER_VALID_SEF Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_SEF(int i) {
		return getZone(getNOM_ST_USER_VALID_SEF(i));
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP_AD(int i) {
		if (LB_AVIS_CAP_AD == null)
			LB_AVIS_CAP_AD = initialiseLazyLB();
		return LB_AVIS_CAP_AD;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP_AD Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP_AD(String[] newLB_AVIS_CAP_AD) {
		LB_AVIS_CAP_AD = newLB_AVIS_CAP_AD;
	}

	/**
	 * Getter de la HashTable AvisCAP.
	 * 
	 * @return Hashtable<String, AvisCap>
	 */
	private Hashtable<Integer, AvisCap> getHashAvisCAP() {
		if (hashAvisCAP == null)
			hashAvisCAP = new Hashtable<Integer, AvisCap>();
		return hashAvisCAP;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVIS_CAP_AD_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_AD_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_AD_" + i + "_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_CAP_AD_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_CAP_AD_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_AD(int i) {
		return "NOM_LB_AVIS_CAP_AD_" + i;
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP_AD(int i) {
		return getLB_AVIS_CAP_AD(i);
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
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF_AVCT(int i) {
		return getZone(getNOM_ST_MOTIF_AVCT(i));
	}

	public CapDao getCapDao() {
		return capDao;
	}

	public void setCapDao(CapDao capDao) {
		this.capDao = capDao;
	}

	public CorpsCapDao getCorpsCapDao() {
		return corpsCapDao;
	}

	public void setCorpsCapDao(CorpsCapDao corpsCapDao) {
		this.corpsCapDao = corpsCapDao;
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP : CK_TAB
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_TAB_SHD(int i) {
		return "NOM_CK_TAB_SHD_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case a cocher : CK_TAB Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_TAB_SHD(int i) {
		return getZone(getNOM_CK_TAB_SHD(i));
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP : CK_EAE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_EAE_SHD(int i) {
		return "NOM_CK_EAE_SHD_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case a cocher : CK_EAE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_EAE_SHD(int i) {
		return getZone(getNOM_CK_EAE_SHD(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CODE_CAP(int i) {
		return "NOM_ST_CODE_CAP_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CODE_CAP(int i) {
		return getZone(getNOM_ST_CODE_CAP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI(int i) {
		return "NOM_ST_CADRE_EMPLOI_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CADRE_EMPLOI
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI(int i) {
		return getZone(getNOM_ST_CADRE_EMPLOI(i));
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_TABLEAU Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_CONSULTER_TABLEAU_AVIS_VDN(int i, String indiceCap, String indiceCadreEmp) {
		return "NOM_PB_CONSULTER_TABLEAU_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_CONSULTER_TABLEAU_AVIS_VDN(HttpServletRequest request, String indiceCap, String indiceCadreEmploi) throws Exception {

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		String nomFichier = "tabAvctCap_" + user.getUserName() + ".pdf";

		// on recupere la CAP et le cadre Emploi
		Cap cap = getCapDao().chercherCapByCodeCap(indiceCap);
		CadreEmploi cadre = getCadreEmploiDao().chercherCadreEmploiByLib(indiceCadreEmploi);

		String url = "PrintDocument?fromPage=" + this.getClass().getName() + "&nomFichier=" + nomFichier + "&idCap=" + cap.getIdCap()
				+ "&idCadreEmploi=" + cadre.getIdCadreEmploi() + "&idAgent=" + getAgentConnecte(request).getIdAgent() + "&isAvisShd=false";
		setURLFichier(getScriptOuverture(url));

		return true;
	}

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	/**
	 * Getter de la HashTable hashListeImpression.
	 * 
	 * @return Hashtable<Cap, CadreEmploi>
	 */
	private Hashtable<Cap, ArrayList<CadreEmploi>> getHashListeImpression() {
		if (hashListeImpression == null)
			hashListeImpression = new Hashtable<Cap, ArrayList<CadreEmploi>>();
		return hashListeImpression;
	}

	public ArrayList<CadreEmploi> getListeImpression() {
		return listeImpression == null ? new ArrayList<CadreEmploi>() : listeImpression;
	}

	public void setListeImpression(ArrayList<CadreEmploi> listeImpression) {
		this.listeImpression = listeImpression;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVIS_CAP_CLASSE_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_CLASSE_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_CLASSE_" + i + "_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_CAP_CLASSE_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_CLASSE(int i) {
		return "NOM_LB_AVIS_CAP_CLASSE_" + i;
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP_CLASSE(int i) {
		return getLB_AVIS_CAP_CLASSE(i);
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP_CLASSE(int i) {
		if (LB_AVIS_CAP_CLASSE == null)
			LB_AVIS_CAP_CLASSE = initialiseLazyLB();
		return LB_AVIS_CAP_CLASSE;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP_CLASSE Date de création : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP_CLASSE(String[] newLB_AVIS_CAP_CLASSE) {
		LB_AVIS_CAP_CLASSE = newLB_AVIS_CAP_CLASSE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_FILIERE Date de création
	 * : (28/11/11)
	 * 
	 */
	private String[] getLB_FILIERE() {
		if (LB_FILIERE == null)
			LB_FILIERE = initialiseLazyLB();
		return LB_FILIERE;
	}

	/**
	 * Setter de la liste: LB_FILIERE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_FILIERE(String[] newLB_FILIERE) {
		LB_FILIERE = newLB_FILIERE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_FILIERE Date de création
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_LB_FILIERE() {
		return "NOM_LB_FILIERE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_FILIERE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_FILIERE_SELECT() {
		return "NOM_LB_FILIERE_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_FILIERE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_FILIERE() {
		return getLB_FILIERE();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_FILIERE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_FILIERE_SELECT() {
		return getZone(getNOM_LB_FILIERE_SELECT());
	}

	public ArrayList<FiliereGrade> getListeFiliere() {
		return listeFiliere == null ? new ArrayList<FiliereGrade>() : listeFiliere;
	}

	public void setListeFiliere(ArrayList<FiliereGrade> listeFiliere) {
		this.listeFiliere = listeFiliere;
	}

	public ArrayList<AvisCap> getListeAvisCAPMinMoyMax() {
		return listeAvisCAPMinMoyMax;
	}

	public void setListeAvisCAPMinMoyMax(ArrayList<AvisCap> listeAvisCAPMinMoyMax) {
		this.listeAvisCAPMinMoyMax = listeAvisCAPMinMoyMax;
	}

	public ArrayList<AvisCap> getListeAvisCAPFavDefav() {
		return listeAvisCAPFavDefav;
	}

	public void setListeAvisCAPFavDefav(ArrayList<AvisCap> listeAvisCAPFavDefav) {
		this.listeAvisCAPFavDefav = listeAvisCAPFavDefav;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (13/09/11 11:47:15)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	public AvancementCapPrintJobDao getAvancementCapPrintJobDao() {
		return avancementCapPrintJobDao;
	}

	public void setAvancementCapPrintJobDao(AvancementCapPrintJobDao avancementCapPrintJobDao) {
		this.avancementCapPrintJobDao = avancementCapPrintJobDao;
	}

	public ArrayList<AvancementCapPrintJob> getListeAvancementCapPrintJob() {
		return listeAvancementCapPrintJob == null ? new ArrayList<AvancementCapPrintJob>() : listeAvancementCapPrintJob;
	}

	public void setListeAvancementCapPrintJob(ArrayList<AvancementCapPrintJob> listeAvancementCapPrintJob) {
		this.listeAvancementCapPrintJob = listeAvancementCapPrintJob;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_CAP_JOB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CODE_CAP_JOB(int i) {
		return "NOM_ST_CODE_CAP_JOB_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_CAP_JOB
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CODE_CAP_JOB(int i) {
		return getZone(getNOM_ST_CODE_CAP_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI_JOB
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI_JOB(int i) {
		return "NOM_ST_CADRE_EMPLOI_JOB_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_CADRE_EMPLOI_JOB Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI_JOB(int i) {
		return getZone(getNOM_ST_CADRE_EMPLOI_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_JOB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_JOB(int i) {
		return "NOM_ST_USER_JOB_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_USER_JOB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_JOB(int i) {
		return getZone(getNOM_ST_USER_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_JOB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_JOB(int i) {
		return "NOM_ST_DATE_JOB_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_JOB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_JOB(int i) {
		return getZone(getNOM_ST_DATE_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT_JOB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT_JOB(int i) {
		return "NOM_ST_ETAT_JOB_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ETAT_JOB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT_JOB(int i) {
		return getZone(getNOM_ST_ETAT_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_JOB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_TYPE_JOB(int i) {
		return "NOM_ST_TYPE_JOB_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TYPE_JOB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_TYPE_JOB(int i) {
		return getZone(getNOM_ST_TYPE_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_JOB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_JOB_ID(int i) {
		return "NOM_ST_JOB_ID_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TYPE_JOB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_JOB_ID(int i) {
		return getZone(getNOM_ST_JOB_ID(i));
	}

	public String getNOM_PB_RAFRAICHIR_IMPRIMER() {
		return "NOM_PB_RAFRAICHIR_IMPRIMER";
	}

	private boolean performPB_RAFRAICHIR_IMPRIMER(HttpServletRequest request) throws Exception {
		initialiseTableauImpressionJob();

		return true;
	}

	public String getNOM_PB_CONSULTER_TABLEAU_AVIS_SHD(int i, String indiceCap, String indiceCadreEmp) {
		return "NOM_PB_CONSULTER_TABLEAU_AVIS_SHD_" + i;
	}

	public boolean performPB_CONSULTER_TABLEAU_AVIS_SHD(HttpServletRequest request, String indiceCap, String indiceCadreEmploi) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		String nomFichier = "tabAvctCap_" + user.getUserName() + ".pdf";

		// on recupere la CAP et le cadre Emploi
		Cap cap = getCapDao().chercherCapByCodeCap(indiceCap);
		CadreEmploi cadre = getCadreEmploiDao().chercherCadreEmploiByLib(indiceCadreEmploi);

		String url = "PrintDocument?fromPage=" + this.getClass().getName() + "&nomFichier=" + nomFichier + "&idCap=" + cap.getIdCap()
				+ "&idCadreEmploi=" + cadre.getIdCadreEmploi() + "&idAgent=" + getAgentConnecte(request).getIdAgent() + "&isAvisShd=true";
		setURLFichier(getScriptOuverture(url));

		return true;
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP : CK_TAB
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_TAB_VDN(int i) {
		return "NOM_CK_TAB_VDN_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case a cocher : CK_TAB Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_TAB_VDN(int i) {
		return getZone(getNOM_CK_TAB_VDN(i));
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP : CK_EAE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_EAE_VDN(int i) {
		return "NOM_CK_EAE_VDN_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case a cocher : CK_EAE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_EAE_VDN(int i) {
		return getZone(getNOM_CK_EAE_VDN(i));
	}

	public String getNOM_ST_PA(int i) {
		return "NOM_ST_PA_" + i;
	}

	public String getVAL_ST_PA(int i) {
		return getZone(getNOM_ST_PA(i));
	}

	public String getNOM_ST_GRADE_ANCIEN(int i) {
		return "NOM_ST_GRADE_ANCIEN_" + i;
	}

	public String getVAL_ST_GRADE_ANCIEN(int i) {
		return getZone(getNOM_ST_GRADE_ANCIEN(i));
	}

	public String getNOM_ST_GRADE_NOUVEAU(int i) {
		return "NOM_ST_GRADE_NOUVEAU_" + i;
	}

	public String getVAL_ST_GRADE_NOUVEAU(int i) {
		return getZone(getNOM_ST_GRADE_NOUVEAU(i));
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public CadreEmploiDao getCadreEmploiDao() {
		return cadreEmploiDao;
	}

	public void setCadreEmploiDao(CadreEmploiDao cadreEmploiDao) {
		this.cadreEmploiDao = cadreEmploiDao;
	}

	public MotifAvancementDao getMotifAvancementDao() {
		return motifAvancementDao;
	}

	public void setMotifAvancementDao(MotifAvancementDao motifAvancementDao) {
		this.motifAvancementDao = motifAvancementDao;
	}

	public AutreAdministrationDao getAutreAdministrationDao() {
		return autreAdministrationDao;
	}

	public void setAutreAdministrationDao(AutreAdministrationDao autreAdministrationDao) {
		this.autreAdministrationDao = autreAdministrationDao;
	}

	public AvisCapDao getAvisCapDao() {
		return avisCapDao;
	}

	public void setAvisCapDao(AvisCapDao avisCapDao) {
		this.avisCapDao = avisCapDao;
	}

	public AvancementFonctionnairesDao getAvancementFonctionnairesDao() {
		return avancementFonctionnairesDao;
	}

	public void setAvancementFonctionnairesDao(AvancementFonctionnairesDao avancementFonctionnairesDao) {
		this.avancementFonctionnairesDao = avancementFonctionnairesDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_ST_DATE_AVCT_MIN_ANCIEN(int i) {
		return "NOM_ST_DATE_AVCT_MIN_ANCIEN_" + i;
	}

	public String getVAL_ST_DATE_AVCT_MIN_ANCIEN(int i) {
		return getZone(getNOM_ST_DATE_AVCT_MIN_ANCIEN(i));
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre
					// identifiant. Merci de contacter le responsable du
					// projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}
}