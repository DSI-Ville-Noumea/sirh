package nc.mairie.gestionagent.process.avancement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.avancement.AvancementCapPrintJobDao;
import nc.mairie.spring.dao.metier.parametrage.CapDao;
import nc.mairie.spring.dao.metier.parametrage.CorpsCapDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.domain.metier.avancement.AvancementCapPrintJob;
import nc.mairie.spring.domain.metier.parametrage.Cap;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Process OeAVCTFonctionnaires Date de cr�ation : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctPrepaCAP extends nc.mairie.technique.BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OeAVCTFonctPrepaCAP.class);

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_ANNEE;
	private String[] LB_AVIS_CAP_AD;
	private String[] LB_AVIS_CAP_CLASSE;
	private String[] LB_FILIERE;

	private ArrayList<FiliereGrade> listeFiliere;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private Hashtable<String, AvisCap> hashAvisCAP;

	private String[] listeAnnee;
	private String anneeSelect;

	private ArrayList<AvancementFonctionnaires> listeAvct;
	private ArrayList listeAvisCAPMinMoyMax;
	private ArrayList listeAvisCAPFavDefav;

	public String agentEnErreur = Const.CHAINE_VIDE;

	private EAEDao eaeDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private CampagneEAEDao campagneEAEDao;
	private CapDao capDao;
	private CorpsCapDao corpsCapDao;
	private AvancementCapPrintJobDao avancementCapPrintJobDao;

	private Hashtable<Cap, ArrayList<CadreEmploi>> hashListeImpression;
	ArrayList<CadreEmploi> listeImpression = new ArrayList<CadreEmploi>();

	private ArrayList<AvancementCapPrintJob> listeAvancementCapPrintJob;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (21/11/11 09:55:36)
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

		// Initialisation des listes d�roulantes
		initialiseListeDeroulante();

		initialiseListeService();

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNoMatricule());
		}

		// Si liste avancements vide alors initialisation.
		if (getListeAvct() == null || getListeAvct().size() == 0) {
			agentEnErreur = Const.CHAINE_VIDE;
		}

		initialiseTableauImpression();
		initialiseTableauImpressionJob();
	}

	private void initialiseTableauImpressionJob() throws Exception {
		// TODO Auto-generated method stub
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

	private void afficheListeAvancement() throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires av = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(av.getIdAvct());
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent() + " <br> " + agent.getNoMatricule());
			addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i), (av.getCodeCadre() == null ? "&nbsp;" : av.getCodeCadre()) + " <br> " + av.getFiliere());
			addZone(getNOM_ST_DATE_DEBUT(i), av.getDateGrade());
			addZone(getNOM_ST_GRADE(i),
					av.getGrade() + " <br> " + (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
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
			String avisSHD = Const.CHAINE_VIDE;
			MotifAvancement motif = null;
			try {
				CampagneEAE campagneEAE = getCampagneEAEDao().chercherCampagneEAEAnnee(Integer.valueOf(getAnneeSelect()));
				// on cherche l'eae correspondant ainsi que l'eae evaluation
				EAE eaeAgent = getEaeDao().chercherEAEAgent(Integer.valueOf(av.getIdAgent()), campagneEAE.getIdCampagneEAE());
				if (eaeAgent.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) || eaeAgent.getEtat().equals(EnumEtatEAE.FINALISE.getCode())) {
					EaeEvaluation eaeEvaluation = getEaeEvaluationDao().chercherEaeEvaluation(eaeAgent.getIdEAE());
					if (av.getIdMotifAvct().equals("7")) {
						avisSHD = eaeEvaluation.getPropositionAvancement() == null ? Const.CHAINE_VIDE : eaeEvaluation.getPropositionAvancement();
					} else if (av.getIdMotifAvct().equals("5")) {
						avisSHD = eaeEvaluation.getAvisRevalorisation() == null ? Const.CHAINE_VIDE
								: eaeEvaluation.getAvisRevalorisation() == 1 ? "FAV" : "DEFAV";
					} else if (av.getIdMotifAvct().equals("4")) {
						avisSHD = eaeEvaluation.getAvisChangementClasse() == null ? Const.CHAINE_VIDE
								: eaeEvaluation.getAvisChangementClasse() == 1 ? "FAV" : "DEFAV";
					}
				}
			} catch (Exception e) {
				// pas de campagne pour cette ann�e
			}
			// motif Avct
			if (av.getIdMotifAvct() != null) {
				motif = MotifAvancement.chercherMotifAvancement(getTransaction(), av.getIdMotifAvct());
			}
			addZone(getNOM_ST_MOTIF_AVCT(i), (motif == null ? Const.CHAINE_VIDE : motif.getCodeMotifAvct()) + "<br/>" + avisSHD);

			// duree VDN
			if (av.getIdAvisCAP() == null) {
				if (!avisSHD.equals(Const.CHAINE_VIDE)) {
					if (avisSHD.equals("MAXI")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("3"))));
					} else if (avisSHD.equals("MOY")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("2"))));
					} else if (avisSHD.equals("MINI")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("1"))));
					} else if (avisSHD.equals("FAV")) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("4"))));
					} else if (avisSHD.equals("DEFAV")) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("5"))));
					}

				} else {
					addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("2"))));
					addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("4"))));
				}

			} else {
				addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
						av.getIdAvisCAP() == null || av.getIdAvisCAP().length() == 0 ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPMinMoyMax()
								.indexOf(getHashAvisCAP().get(av.getIdAvisCAP()))));
				addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), av.getIdAvisCAP() == null || av.getIdAvisCAP().length() == 0 ? Const.CHAINE_VIDE
						: String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(av.getIdAvisCAP()))));
			}

		}
	}

	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList services = Service.listerServiceActif(getTransaction());
			setListeServices(services);

			// Tri par codeservice
			Collections.sort(getListeServices(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Service s1 = (Service) o1;
					Service s2 = (Service) o2;
					return (s1.getCodService().compareTo(s2.getCodService()));
				}
			});

			// alim de la hTree
			hTree = new Hashtable<String, TreeHierarchy>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService()))
					continue;

				// recherche du sup�rieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");
				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

			}
		}
	}

	private void initialiseTableauImpression() throws Exception {
		for (int i = 0; i < getListeImpression().size(); i++) {
			addZone(getNOM_CK_TAB(i), getCHECKED_OFF());
			addZone(getNOM_CK_EAE(i), getCHECKED_OFF());
		}

		// Si liste impression
		if (getListeImpression().size() == 0) {
			getHashListeImpression().clear();

			ArrayList<Cap> listeCap = new ArrayList<Cap>();
			listeCap.addAll(getCapDao().listerCap());

			for (int i = 0; i < listeCap.size(); i++) {
				Cap cap = listeCap.get(i);
				ArrayList<CadreEmploi> listeCadreEmploiCap = CadreEmploi.listerCadreEmploiCap(getTransaction(), cap.getIdCap());
				getHashListeImpression().put(cap, listeCadreEmploiCap);

			}

			Enumeration nb = getHashListeImpression().keys();
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
					addZone(getNOM_CK_TAB(k), getCHECKED_OFF());
					addZone(getNOM_CK_EAE(k), getCHECKED_OFF());
					k++;
				}

			}
			setListeImpression(listeTempCadreEmp);

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

		if (getCapDao() == null) {
			setCapDao((CapDao) context.getBean("capDao"));
		}

		if (getCorpsCapDao() == null) {
			setCorpsCapDao((CorpsCapDao) context.getBean("corpsCapDao"));
		}

		if (getAvancementCapPrintJobDao() == null) {
			setAvancementCapPrintJobDao((AvancementCapPrintJobDao) context.getBean("avancementCapPrintJobDao"));
		}
	}

	/**
	 * Initialisation des liste d�roulantes de l'�cran Avancement des
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
			// changement de l'ann�e pour faire au mieux.
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
		if (getListeAvisCAPMinMoyMax() == null || getListeAvisCAPMinMoyMax().size() == 0) {
			ArrayList avis = AvisCap.listerAvisCapMinMoyMax(getTransaction());
			setListeAvisCAPMinMoyMax(avis);

			int[] tailles = { 7 };
			String[] champs = { "libLongAvisCAP" };
			setLB_AVIS_CAP_AD(new FormateListe(tailles, avis, champs).getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPMinMoyMax().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPMinMoyMax().get(i);
				getHashAvisCAP().put(ac.getIdAvisCAP(), ac);
			}
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAPFavDefav() == null || getListeAvisCAPFavDefav().size() == 0) {
			ArrayList avis = AvisCap.listerAvisCapFavDefav(getTransaction());
			setListeAvisCAPFavDefav(avis);

			int[] tailles = { 7 };
			String[] champs = { "libLongAvisCAP" };
			setLB_AVIS_CAP_CLASSE(new FormateListe(tailles, avis, champs).getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPFavDefav().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPFavDefav().get(i);
				getHashAvisCAP().put(ac.getIdAvisCAP(), ac);
			}

		}

		// Si liste medecins vide alors affectation
		if (getListeFiliere() == null || getListeFiliere().size() == 0) {
			setListeFiliere(FiliereGrade.listerFiliereGrade(getTransaction()));

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeFiliere().listIterator(); list.hasNext();) {
				FiliereGrade fili = (FiliereGrade) list.next();
				String ligne[] = { fili.getLibFiliere() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FILIERE(aFormat.getListeFormatee(true));

		}
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (21/11/11 09:55:36)
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

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_CONSULTER_TABLEAU
			for (int i = 0; i < getListeImpression().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_TABLEAU(i, getVAL_ST_CODE_CAP(i), getVAL_ST_CADRE_EMPLOI(i)))) {
					return performPB_CONSULTER_TABLEAU(request, getVAL_ST_CODE_CAP(i), getVAL_ST_CADRE_EMPLOI(i));
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
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTFonctPrepaCAP() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctPrepaCAP.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_FILTRER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
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
		AgentNW agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT());
		}

		// recuperation du service
		ArrayList<String> listeSousService = null;
		if (getVAL_ST_CODE_SERVICE().length() != 0) {
			// on recupere les sous-service du service selectionne
			Service serv = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			listeSousService = Service.listSousServiceBySigle(getTransaction(), serv.getSigleService());
		}

		String reqEtat = " and (ETAT='" + EnumEtatAvancement.SGC.getValue() + "' or ETAT='" + EnumEtatAvancement.SEF.getValue() + "')";
		setListeAvct(AvancementFonctionnaires.listerAvancementAvecAnneeEtat(getTransaction(), annee, reqEtat, filiere, agent, listeSousService));

		afficheListeAvancement();
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		AgentNW agent = null;
		if (!user.getUserName().equals("nicno85")) {
			// on recupere l'id de l'agent
			Siidma ag = Siidma.chercherSiidma(getTransaction(), user.getUserName());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "ERR183",
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), ag.getNomatr());
		} else {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}

		for (int i = 0; i < getListeImpression().size(); i++) {
			if (getVAL_CK_TAB(i).equals(getCHECKED_ON())) {
				Cap cap = getCapDao().chercherCapByCodeCap(getVAL_ST_CODE_CAP(i));
				CadreEmploi cadre = CadreEmploi.chercherCadreEmploiByLib(getTransaction(), getVAL_ST_CADRE_EMPLOI(i));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// "ERR182",
					// "Une erreur est survenue dans la g�n�ration du tableau. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
					return false;
				}
				// on cr�e l'entr�e dans la table du job
				getAvancementCapPrintJobDao().creerAvancementCapPrintJob(Integer.valueOf(agent.getIdAgent()), user.getUserName(), cap.getIdCap(),
						cap.getCodeCap(), Integer.valueOf(cadre.getIdCadreEmploi()), cadre.getLibCadreEmploi(), false);

			}
			if (getVAL_CK_EAE(i).equals(getCHECKED_ON())) {
				Cap cap = getCapDao().chercherCapByCodeCap(getVAL_ST_CODE_CAP(i));
				CadreEmploi cadre = CadreEmploi.chercherCadreEmploiByLib(getTransaction(), getVAL_ST_CADRE_EMPLOI(i));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// "ERR182",
					// "Une erreur est survenue dans la g�n�ration du tableau. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
					return false;
				}
				// on cr�e l'entr�e dans la table du job
				getAvancementCapPrintJobDao().creerAvancementCapPrintJob(Integer.valueOf(agent.getIdAgent()), user.getUserName(), cap.getIdCap(),
						cap.getCodeCap(), Integer.valueOf(cadre.getIdCadreEmploi()), cadre.getLibCadreEmploi(), true);

			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		String dateJour = Services.dateDuJour();
		// on sauvegarde l'�tat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recup�re la ligne concern�e
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer idAvct = Integer.valueOf(avct.getIdAvct());
			// on fait les modifications
			// on traite l'etat
			if (getVAL_CK_VALID_SEF(idAvct).equals(getCHECKED_ON())) {
				// si la ligne est coch�e
				// on regarde si l'etat est deja SEF
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat � SEF et on met � jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSEF(user.getUserName());
					avct.setDateVerifSEF(dateJour);
					avct.setHeureVerifSEF(heureAction);
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				}
			} else {
				// si la ligne n'est pas coch�e
				// on regarde quel etat son etat
				// --> si SEF alors on met � jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifSEF(user.getUserName());
					avct.setDateVerifSEF(dateJour);
					avct.setHeureVerifSEF(heureAction);
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				}
			}

			if (avct.getIdMotifAvct() != null) {
				if (avct.getIdMotifAvct().equals("7")) {
					// on traite l'avis CAP
					int indiceAvisCapMinMoyMax = (Services.estNumerique(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) ? Integer
							.parseInt(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) : -1);
					if (indiceAvisCapMinMoyMax != -1) {
						String idAvisCap = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMax)).getIdAvisCAP();
						avct.setIdAvisCAP(idAvisCap);
						// on traite l'odre de merite
						// on test si "moyenne" choisi alors on remete � vide
						// ordre du
						// m�rite
						if (indiceAvisCapMinMoyMax != 1) {
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
				} else {
					int indiceAvisCapFavDefav = (Services.estNumerique(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) ? Integer
							.parseInt(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) : -1);
					if (indiceAvisCapFavDefav != -1) {
						String idAvisCap = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefav)).getIdAvisCAP();
						avct.setIdAvisCAP(idAvisCap);
					}
					avct.setOrdreMerite(null);
				}
			} else {
				avct.setIdAvisCAP(null);
				avct.setOrdreMerite(null);
			}

			avct.modifierAvancement(getTransaction());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		// on remet la liste � vide afin qu'elle soit de nouveau initialis�e
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AGENT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CATEGORIE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CATEGORIE(int i) {
		return "NOM_ST_CATEGORIE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CATEGORIE Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CATEGORIE(int i) {
		return getZone(getNOM_ST_CATEGORIE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_AVCT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_AVCT(int i) {
		return "NOM_ST_DATE_AVCT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_AVCT Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_AVCT(int i) {
		return getZone(getNOM_ST_DATE_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_LIB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_GRADE_LIB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE(int i) {
		return "NOM_ST_GRADE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_GRADE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE(int i) {
		return getZone(getNOM_ST_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_AVCT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_AVCT(int i) {
		return "NOM_ST_NUM_AVCT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NUM_AVCT Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_AVCT(int i) {
		return getZone(getNOM_ST_NUM_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ETAT Date de
	 * cr�ation : (21/11/11 09:55:36)
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
	 * Getter du nom de l'�cran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-FONCT-PREPA-CAP";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de cr�ation :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de cr�ation : (28/11/11)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de cr�ation :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de cr�ation : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_ANNEE Date de cr�ation : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_ANNEE Date de cr�ation : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Getter de la liste des ann�es possibles.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des ann�es possibles.
	 * 
	 * @param listeAnnee
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	/**
	 * Getter de l'annee s�lectionn�e.
	 * 
	 * @return anneeSelect
	 */
	public String getAnneeSelect() {
		return anneeSelect;
	}

	/**
	 * Setter de l'ann�e s�lectionn�e
	 * 
	 * @param newAnneeSelect
	 */
	public void setAnneeSelect(String newAnneeSelect) {
		this.anneeSelect = newAnneeSelect;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_VALID_SEF Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_SEF(int i) {
		return "NOM_CK_VALID_SEF_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_VALID_SEF Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_SEF(int i) {
		return getZone(getNOM_CK_VALID_SEF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_VALID_SEF Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_VALID_SEF(int i) {
		return "NOM_ST_USER_VALID_SEF_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_USER_VALID_SEF
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_SEF(int i) {
		return getZone(getNOM_ST_USER_VALID_SEF(i));
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP_AD(int i) {
		if (LB_AVIS_CAP_AD == null)
			LB_AVIS_CAP_AD = initialiseLazyLB();
		return LB_AVIS_CAP_AD;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP_AD Date de cr�ation : (21/11/11 09:55:36)
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
	private Hashtable<String, AvisCap> getHashAvisCAP() {
		if (hashAvisCAP == null)
			hashAvisCAP = new Hashtable<String, AvisCap>();
		return hashAvisCAP;
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_AVIS_CAP_AD_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_AD_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_AD_" + i + "_SELECT_";
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_CAP_AD_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_CAP_AD_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_AD(int i) {
		return "NOM_LB_AVIS_CAP_AD_" + i;
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP_AD(int i) {
		return getLB_AVIS_CAP_AD(i);
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ORDRE_MERITE Date
	 * de cr�ation : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_ORDRE_MERITE(int i) {
		return "NOM_EF_ORDRE_MERITE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_ORDRE_MERITE Date de cr�ation : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_ORDRE_MERITE(int i) {
		return getZone(getNOM_EF_ORDRE_MERITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF_AVCT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF_AVCT(int i) {
		return "NOM_ST_MOTIF_AVCT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de cr�ation : (21/11/11 09:55:36)
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
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP : CK_TAB
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_TAB(int i) {
		return "NOM_CK_TAB_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_TAB Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_TAB(int i) {
		return getZone(getNOM_CK_TAB(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP : CK_EAE
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_EAE(int i) {
		return "NOM_CK_EAE_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_EAE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_EAE(int i) {
		return getZone(getNOM_CK_EAE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_CAP Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CODE_CAP(int i) {
		return "NOM_ST_CODE_CAP_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_CAP Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CODE_CAP(int i) {
		return getZone(getNOM_ST_CODE_CAP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI(int i) {
		return "NOM_ST_CADRE_EMPLOI_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CADRE_EMPLOI
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI(int i) {
		return getZone(getNOM_ST_CADRE_EMPLOI(i));
	}

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie d�j� que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_TABLEAU Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_CONSULTER_TABLEAU(int i, String indiceCap, String indiceCadreEmp) {
		return "NOM_PB_CONSULTER_TABLEAU_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_CONSULTER_TABLEAU(HttpServletRequest request, String indiceCap, String indiceCadreEmploi) throws Exception {
		verifieRepertoire("Avancement");
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		String destination = repPartage + "Avancement/tabAvctCap_" + user.getUserName() + ".pdf";
		// on receupere la CAP et le cadre Emploi
		Cap cap = getCapDao().chercherCapByCodeCap(indiceCap);
		CadreEmploi cadre = CadreEmploi.chercherCadreEmploiByLib(getTransaction(), indiceCadreEmploi);
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			// "ERR182",
			// "Une erreur est survenue dans la g�n�ration du tableau. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
			return false;
		}

		byte[] fileAsBytes = getTabAvctCapReportAsByteArray(cap.getIdCap(), Integer.valueOf(cadre.getIdCadreEmploi()), "PDF");
		if (!saveFileToRemoteFileSystem(fileAsBytes, destination)) {
			// "ERR182",
			// "Une erreur est survenue dans la g�n�ration du tableau. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR182"));
			return false;
		}
		return true;
	}

	public byte[] getTabAvctCapReportAsByteArray(int idCap, int idCadreEmploi, String format) throws Exception {

		ClientResponse response = createAndFireRequest(idCap, idCadreEmploi, format);

		return readResponseAsByteArray(response, format);
	}

	public byte[] readResponseAsByteArray(ClientResponse response, String format) throws Exception {

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new Exception(String.format("An error occured ", response.getStatus()));
		}

		byte[] reponseData = null;
		File reportFile = null;

		try {
			reportFile = response.getEntity(File.class);
			reponseData = IOUtils.toByteArray(new FileInputStream(reportFile));
		} catch (Exception e) {
			throw new Exception("An error occured while reading the downloaded report.", e);
		} finally {
			if (reportFile != null && reportFile.exists())
				reportFile.delete();
		}

		return reponseData;
	}

	public ClientResponse createAndFireRequest(int idCap, int idCadreEmploi, String format) {
		String urlWSTableauAvctCAP = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL_TABLEAU_AVCT_CAP") + "?idCap=" + idCap
				+ "&idCadreEmploi=" + idCadreEmploi;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSTableauAvctCAP);

		ClientResponse response = webResource.get(ClientResponse.class);

		return response;
	}

	public boolean saveFileToRemoteFileSystem(byte[] fileAsBytes, String filename) throws Exception {

		BufferedOutputStream bos = null;
		FileObject pdfFile = null;

		try {
			FileSystemManager fsManager = VFS.getManager();
			pdfFile = fsManager.resolveFile(String.format("%s", filename));
			bos = new BufferedOutputStream(pdfFile.getContent().getOutputStream());
			IOUtils.write(fileAsBytes, bos);
			IOUtils.closeQuietly(bos);

			if (pdfFile != null) {
				try {
					pdfFile.close();
					setURLFichier(getScriptOuverture(filename));
				} catch (FileSystemException e) {
					// ignore the exception
				}
			}
		} catch (Exception e) {
			setURLFichier(null);
			logger.error(String.format("An error occured while writing the report file to the following path  : " + filename + " : " + e));
			return false;
		}
		return true;
	}

	private String urlFichier;

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
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_AVIS_CAP_CLASSE_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_CLASSE_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_CLASSE_" + i + "_SELECT_";
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_CAP_CLASSE_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_CLASSE(int i) {
		return "NOM_LB_AVIS_CAP_CLASSE_" + i;
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP_CLASSE(int i) {
		return getLB_AVIS_CAP_CLASSE(i);
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP_CLASSE(int i) {
		if (LB_AVIS_CAP_CLASSE == null)
			LB_AVIS_CAP_CLASSE = initialiseLazyLB();
		return LB_AVIS_CAP_CLASSE;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP_CLASSE Date de cr�ation : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP_CLASSE(String[] newLB_AVIS_CAP_CLASSE) {
		LB_AVIS_CAP_CLASSE = newLB_AVIS_CAP_CLASSE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_FILIERE Date de cr�ation
	 * : (28/11/11)
	 * 
	 */
	private String[] getLB_FILIERE() {
		if (LB_FILIERE == null)
			LB_FILIERE = initialiseLazyLB();
		return LB_FILIERE;
	}

	/**
	 * Setter de la liste: LB_FILIERE Date de cr�ation : (28/11/11)
	 * 
	 */
	private void setLB_FILIERE(String[] newLB_FILIERE) {
		LB_FILIERE = newLB_FILIERE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_FILIERE Date de cr�ation
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_LB_FILIERE() {
		return "NOM_LB_FILIERE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_FILIERE_SELECT Date de cr�ation : (28/11/11)
	 * 
	 */
	public String getNOM_LB_FILIERE_SELECT() {
		return "NOM_LB_FILIERE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_FILIERE Date de cr�ation : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_FILIERE() {
		return getLB_FILIERE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_FILIERE Date de cr�ation : (28/11/11)
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

	public ArrayList getListeAvisCAPMinMoyMax() {
		return listeAvisCAPMinMoyMax;
	}

	public void setListeAvisCAPMinMoyMax(ArrayList listeAvisCAPMinMoyMax) {
		this.listeAvisCAPMinMoyMax = listeAvisCAPMinMoyMax;
	}

	public ArrayList getListeAvisCAPFavDefav() {
		return listeAvisCAPFavDefav;
	}

	public void setListeAvisCAPFavDefav(ArrayList listeAvisCAPFavDefav) {
		this.listeAvisCAPFavDefav = listeAvisCAPFavDefav;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * cr�ation : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AGENT Date de
	 * cr�ation : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * cr�ation : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activit�
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de cr�ation : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enl�ve l'agent selectionn�e
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * cr�ation : (13/09/11 11:47:15)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de cr�ation : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
	 * Date de cr�ation : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enl�ve le service selectionn�e
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * cr�ation : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de cr�ation : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	/**
	 * Retourne la liste des services.
	 * 
	 * @return listeServices
	 */
	public ArrayList getListeServices() {
		return listeServices;
	}

	/**
	 * Met � jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Retourne une hashTable de la hi�rarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
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
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CODE_CAP_JOB(int i) {
		return "NOM_ST_CODE_CAP_JOB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_CAP_JOB
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CODE_CAP_JOB(int i) {
		return getZone(getNOM_ST_CODE_CAP_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI_JOB
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI_JOB(int i) {
		return "NOM_ST_CADRE_EMPLOI_JOB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_CADRE_EMPLOI_JOB Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI_JOB(int i) {
		return getZone(getNOM_ST_CADRE_EMPLOI_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_JOB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_JOB(int i) {
		return "NOM_ST_USER_JOB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_USER_JOB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_JOB(int i) {
		return getZone(getNOM_ST_USER_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_JOB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_JOB(int i) {
		return "NOM_ST_DATE_JOB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_JOB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_JOB(int i) {
		return getZone(getNOM_ST_DATE_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT_JOB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT_JOB(int i) {
		return "NOM_ST_ETAT_JOB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ETAT_JOB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT_JOB(int i) {
		return getZone(getNOM_ST_ETAT_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_JOB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_TYPE_JOB(int i) {
		return "NOM_ST_TYPE_JOB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TYPE_JOB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_TYPE_JOB(int i) {
		return getZone(getNOM_ST_TYPE_JOB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_JOB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_JOB_ID(int i) {
		return "NOM_ST_JOB_ID_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TYPE_JOB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_JOB_ID(int i) {
		return getZone(getNOM_ST_JOB_ID(i));
	}
}