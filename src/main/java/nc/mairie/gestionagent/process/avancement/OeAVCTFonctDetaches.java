package nc.mairie.gestionagent.process.avancement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementDetachesDao;
import nc.mairie.spring.dao.metier.carriere.HistoCarriereDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IAvancementService;
import nc.noumea.spring.service.ISirhService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctDetaches extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private Logger logger = LoggerFactory.getLogger(OeAVCTFonctDetaches.class);

	private String[] LB_ANNEE;

	private String[] listeAnnee;
	private String anneeSelect;

	private Hashtable<String, MotifAvancement> hashMotifAvct;
	private ArrayList<MotifAvancement> listeMotifAvct;

	private ArrayList<AvancementDetaches> listeAvct;
	public String agentEnErreur = Const.CHAINE_VIDE;

	private ArrayList<String> listeDocuments;
	private String urlFichier;

	private MotifAvancementDao motifAvancementDao;
	private AutreAdministrationDao autreAdministrationDao;
	private AvancementDetachesDao avancementDetachesDao;
	private HistoCarriereDao histoCarriereDao;
	private AgentDao agentDao;

	private ISirhService sirhService;
	private IAvancementService avancementService;

	private SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");

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
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
		initialiseListeDeroulante();

		// Initialisation de la liste des documents suivi medicaux
		if (getListeDocuments() == null || getListeDocuments().size() == 0) {
			setListeDocuments(listerDocumentsArretes());
			afficheListeDocuments();

		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getMotifAvancementDao() == null) {
			setMotifAvancementDao(new MotifAvancementDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationDao() == null) {
			setAutreAdministrationDao(new AutreAdministrationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementDetachesDao() == null) {
			setAvancementDetachesDao(new AvancementDetachesDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoCarriereDao() == null) {
			setHistoCarriereDao(new HistoCarriereDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (null == avancementService) {
			avancementService = (IAvancementService) context.getBean("avancementService");
		}
	}

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
		// Si liste motifs avancement vide alors affectation
		if (getListeMotifAvct() == null || getListeMotifAvct().size() == 0) {
			ArrayList<MotifAvancement> motif = getMotifAvancementDao().listerMotifAvancementSansRevalo();
			setListeMotifAvct(motif);

			// remplissage de la hashTable
			for (int i = 0; i < getListeMotifAvct().size(); i++) {
				MotifAvancement m = (MotifAvancement) getListeMotifAvct().get(i);
				getHashMotifAvancement().put(m.getIdMotifAvct().toString(), m);
			}
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

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AFFECTER
			if (testerParametre(request, getNOM_PB_AFFECTER())) {
				return performPB_AFFECTER(request);
			}

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

			// Si clic sur le bouton PB_VISUALISER pour les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
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
	public OeAVCTFonctDetaches() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctDetaches.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-DETACHE";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Getter de la liste des années possibles de simulation.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des années possibles de simulation.
	 * 
	 * @param listeAnnee
	 *            listeAnnee à définir
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
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

	private void afficherListeAvct(HttpServletRequest request) throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementDetaches av = (AvancementDetaches) getListeAvct().get(j);
			Integer i = av.getIdAvct();
			Agent agent = getAgentDao().chercherAgent(av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DIRECTION(i), Services.estNumerique(av.getDirectionService()) ? getAutreAdministrationDao().chercherAutreAdministration(Integer.valueOf(av.getDirectionService()))
					.getLibAutreAdmin() : av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i), (av.getCdcadr() == null ? "&nbsp;" : av.getCdcadr()) + " <br> " + av.getFiliere());
			PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
			addZone(getNOM_ST_PA(i), pa.getLiPAdm());
			addZone(getNOM_ST_DATE_DEBUT(i), sdfFormatDate.format(av.getDateGrade()));
			addZone(getNOM_ST_BM_A(i), av.getBmAnnee() + " <br> " + av.getNouvBmAnnee());
			addZone(getNOM_ST_BM_M(i), av.getBmMois() + " <br> " + av.getNouvBmMois());
			addZone(getNOM_ST_BM_J(i), av.getBmJour() + " <br> " + av.getNouvBmJour());
			addZone(getNOM_ST_ACC_A(i), av.getAccAnnee() + " <br> " + av.getNouvAccAnnee());
			addZone(getNOM_ST_ACC_M(i), av.getAccMois() + " <br> " + av.getNouvAccMois());
			addZone(getNOM_ST_ACC_J(i), av.getAccJour() + " <br> " + av.getNouvAccJour());
			addZone(getNOM_ST_GRADE_ANCIEN(i), av.getGrade());
			addZone(getNOM_ST_GRADE_NOUVEAU(i), (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct().toString());
			addZone(getNOM_ST_PERIODE_STD(i), av.getPeriodeStandard().toString());
			addZone(getNOM_ST_DATE_AVCT(i), sdfFormatDate.format(av.getDateAvctMoy()));

			addZone(getNOM_CK_VALID_DRH(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_ST_MOTIF_AVCT(i), av.getIdMotifAvct() == null ? "&nbsp;" : getHashMotifAvancement().get(av.getIdMotifAvct().toString()).getLibMotifAvct());
			addZone(getNOM_CK_PROJET_ARRETE(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) || av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF()
					: getCHECKED_ON());
			addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_EF_DATE_ARRETE(i), av.getDateArrete() == null ? Const.CHAINE_VIDE : sdfFormatDate.format(av.getDateArrete()));

			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue()) || av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());

			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
			addZone(getNOM_CK_VALID_ARR_IMPR(i), getCHECKED_OFF());
			addZone(getNOM_CK_REGUL_ARR_IMPR(i), av.isRegularisation() ? getCHECKED_ON() : getCHECKED_OFF());

		}
	}

	/**
	 * Getter de la HashTable MotifAvancement.
	 * 
	 * @return Hashtable<String, MotifAvancement>
	 */
	private Hashtable<String, MotifAvancement> getHashMotifAvancement() {
		if (hashMotifAvct == null)
			hashMotifAvct = new Hashtable<String, MotifAvancement>();
		return hashMotifAvct;
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<AvancementDetaches> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementDetaches>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementDetaches> listeAvct) {
		this.listeAvct = listeAvct;
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
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_AFFECTER(int i) {
		return "NOM_CK_AFFECTER_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
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

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE(int i) {
		return "NOM_EF_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE(int i) {
		return getZone(getNOM_EF_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_NUM_ARRETE(int i) {
		return "NOM_EF_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_NUM_ARRETE(int i) {
		return getZone(getNOM_EF_NUM_ARRETE(i));
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
	 * CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_PROJET_ARRETE(int i) {
		return "NOM_CK_PROJET_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_PROJET_ARRETE(int i) {
		return getZone(getNOM_CK_PROJET_ARRETE(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_DRH(int i) {
		return "NOM_CK_VALID_DRH_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_DRH(int i) {
		return getZone(getNOM_CK_VALID_DRH(i));
	}

	/**
	 * Getter de la liste des motifs d'avancement.
	 * 
	 * @return listeMotifAvct
	 */
	private ArrayList<MotifAvancement> getListeMotifAvct() {
		return listeMotifAvct;
	}

	/**
	 * Setter de la liste des motifs d'avancement.
	 * 
	 * @param listeMotifAvct
	 */
	private void setListeMotifAvct(ArrayList<MotifAvancement> listeMotifAvct) {
		this.listeMotifAvct = listeMotifAvct;
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
		// on sauvegarde l'etat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementDetaches avct = (AvancementDetaches) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else if (getVAL_CK_PROJET_ARRETE(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				} else if (getVAL_CK_VALID_DRH(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
				}
				// on traite le numero et la date d'arrete
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdfFormatDate.parse(getVAL_EF_DATE_ARRETE(i)));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));

				// on traite la regularisation
				if (getVAL_CK_REGUL_ARR_IMPR(i).equals(getCHECKED_ON())) {
					avct.setRegularisation(true);
				} else {
					avct.setRegularisation(false);
				}
			}
			getAvancementDetachesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(),
					avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
					avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
					avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMoy(), avct.getNumArrete(),
					avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
					avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getObservationArr(),
					avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getCodePa());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		afficherListeAvct(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementDetaches avct = (AvancementDetaches) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concerné
					Agent agent = getAgentDao().chercherAgent(avct.getIdAgent());
					// on recupere la derniere carriere dans l'année
					Carriere carr = Carriere.chercherDerniereCarriereAvecAgentEtAnnee(getTransaction(), agent.getNomatr(), avct.getAnnee().toString());
					// on check il y a une carriere deja saisie
					if (avancementService.isCarriereDetacheSimu(getTransaction(), agent, avct, carr)) {

						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());

						// on traite le numero et la date d'arrete
						avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdfFormatDate.parse(getVAL_EF_DATE_ARRETE(i)));
						avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
						// avct.modifierAvancement(getTransaction());

						// on regarde l'avis CAP selectionné pour determinà la
						// date de debut de carriere et la date de fin de la
						// precedente

						String dateAvct = sdfFormatDate.format(avct.getDateAvctMoy());

						// on ferme cette carriere
						carr.setDateFin(dateAvct);
						// RG_AG_CA_A03
						HistoCarriere histo = new HistoCarriere(carr);
						getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
						carr.modifierCarriere(getTransaction(), agent, user);

						// on crée un nouvelle carriere
						Carriere nouvelleCarriere = avancementService.getNewCarriereDetache(getTransaction(), agent, avct, carr, dateAvct);

						HistoCarriere histo2 = new HistoCarriere(nouvelleCarriere);
						getHistoCarriereDao().creerHistoCarriere(histo2, user, EnumTypeHisto.CREATION);
						nouvelleCarriere.creerCarriere(getTransaction(), agent, user);

						getAvancementDetachesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
								avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(),
								avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(),
								avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
								avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMoy(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(),
								avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(),
								avct.getHeureVerifSef(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getObservationArr(), avct.getUserVerifArrImpr(),
								avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getCodePa());

						// on enregistre

						if (getTransaction().isErreur()) {
							return false;
						} else {
							nbAgentAffectes += 1;
						}
					} else {
						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste a l'ecran
						agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
						// on met un 'S' dans son avancement
						avct.setCarriereSimu("S");
						getAvancementDetachesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
								avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(),
								avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(),
								avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
								avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMoy(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(),
								avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(),
								avct.getHeureVerifSef(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getObservationArr(), avct.getUserVerifArrImpr(),
								avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getCodePa());

					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();

		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		return true;
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
		setListeAvct(new ArrayList<AvancementDetaches>());
		afficherListeAvct(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		setAnneeSelect(annee);

		setListeAvct(getAvancementDetachesDao().listerAvancementAvecAnneeEtat(Integer.valueOf(annee), null, null, null, null, null));

		afficherListeAvct(request);
		return true;
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_ARR_IMPR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_ARR_IMPR(int i) {
		return "NOM_CK_VALID_ARR_IMPR_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_VALID_SGC_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_ARR_IMPR(int i) {
		return getZone(getNOM_CK_VALID_ARR_IMPR(i));
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

		verifieRepertoire("Avancement");
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		// on supprime les documents existants
		String docuChangementClasse = "Avancement/arretesChangementClasseDetaches.doc";
		String docuAvctDiff = "Avancement/arretesAvancementDiffDetaches.doc";
		// on verifie l'existance de chaque fichier
		File chgtClasse = new File(repPartage.substring(8, repPartage.length()) + docuChangementClasse);
		if (chgtClasse.exists()) {
			chgtClasse.delete();
		}
		File avctDiff = new File(repPartage.substring(8, repPartage.length()) + docuAvctDiff);
		if (avctDiff.exists()) {
			avctDiff.delete();
		}
		ArrayList<Integer> listeImpressionChangementClasse = new ArrayList<Integer>();
		ArrayList<Integer> listeImpressionAvancementDiff = new ArrayList<Integer>();

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());

		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementDetaches avct = (AvancementDetaches) getListeAvct().get(j);
			Integer idAvct = avct.getIdAvct();
			if (getVAL_CK_VALID_ARR_IMPR(idAvct).equals(getCHECKED_ON())) {
				if (avct.getIdMotifAvct() == 4) {
					// on fait une liste des arretes changement classe
					listeImpressionChangementClasse.add(avct.getIdAgent());
				} else if (avct.getIdMotifAvct() == 7 || avct.getIdMotifAvct() == 6 || avct.getIdMotifAvct() == 3) {
					// on fait une liste des arretes avancement diffe
					listeImpressionAvancementDiff.add(avct.getIdAgent());
				} else {
					continue;
				}
				// si la ligne est cochée
				// on regarde si l'etat est deja ARR
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat a ARR et on met a jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifArrImpr(user.getUserName());
					avct.setDateVerifArrImpr(new Date());
					avct.setHeureVerifArrImpr(heureAction);
				}

				// on sauvegarde les regularisations

				if (getVAL_CK_REGUL_ARR_IMPR(idAvct).equals(getCHECKED_ON())) {
					avct.setRegularisation(true);
				} else {
					avct.setRegularisation(false);
				}
				try {

					getAvancementDetachesDao()
							.modifierAvancement(avct.getIdAvct(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(), avct.getGrade(),
									avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
									avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(),
									avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(),
									avct.getDateAvctMoy(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(),
									avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getUserVerifArr(),
									avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(),
									avct.isRegularisation(), avct.isAgentVdn(), avct.getCodePa());
				} catch (Exception e) {
					getTransaction().declarerErreur("Une erreur est survenue dans la sauvegarde des avancements. Merci de contacter le responsable du projet.");
					return false;
				}
				commitTransaction();
			}
			addZone(getNOM_CK_VALID_ARR_IMPR(idAvct), getCHECKED_OFF());

		}
		if (listeImpressionChangementClasse.size() > 0) {

			try {
				byte[] fileAsBytes = sirhService.downloadArrete(listeImpressionChangementClasse.toString().replace("[", "").replace("]", "").replace(" ", ""), true, Integer.valueOf(getAnneeSelect()),
						true);

				if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, docuChangementClasse)) {
					// "ERR185",
					// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
					return false;
				}
			} catch (Exception e) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

		}
		if (listeImpressionAvancementDiff.size() > 0) {
			try {
				byte[] fileAsBytes = sirhService.downloadArrete(listeImpressionAvancementDiff.toString().replace("[", "").replace("]", "").replace(" ", ""), false, Integer.valueOf(getAnneeSelect()),
						true);
				if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, docuAvctDiff)) {
					// "ERR185",
					// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
					return false;
				}
			} catch (Exception e) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

		}
		setListeDocuments(null);
		afficherListeAvct(request);
		return true;
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

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie déjà que le repertoire source existe
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

	public boolean saveFileToRemoteFileSystem(byte[] fileAsBytes, String chemin, String filename) throws Exception {

		BufferedOutputStream bos = null;
		FileObject pdfFile = null;

		try {
			FileSystemManager fsManager = VFS.getManager();
			pdfFile = fsManager.resolveFile(String.format("%s", chemin + filename));
			bos = new BufferedOutputStream(pdfFile.getContent().getOutputStream());
			IOUtils.write(fileAsBytes, bos);
			IOUtils.closeQuietly(bos);

			if (pdfFile != null) {
				try {
					pdfFile.close();
				} catch (FileSystemException e) {
					// ignore the exception
				}
			}
		} catch (Exception e) {
			logger.error(String.format("An error occured while writing the report file to the following path  : " + chemin + filename + " : " + e));
			return false;
		}
		return true;
	}

	public ArrayList<String> getListeDocuments() {
		if (listeDocuments == null)
			return new ArrayList<String>();
		return listeDocuments;
	}

	public void setListeDocuments(ArrayList<String> listeDocuments) {
		this.listeDocuments = listeDocuments;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		String docSelection = getListeDocuments().get(indiceEltAConsulter);
		String nomDoc = docSelection.substring(docSelection.lastIndexOf("/"), docSelection.length());

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		setURLFichier(getScriptOuverture(repertoireStockage + "Avancement" + nomDoc));

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void afficheListeDocuments() {
		for (int i = 0; i < getListeDocuments().size(); i++) {
			String nomDoc = getListeDocuments().get(i);
			addZone(getNOM_ST_NOM_DOC(i), nomDoc.substring(nomDoc.lastIndexOf("/") + 1, nomDoc.length()));
		}
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
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

	private ArrayList<String> listerDocumentsArretes() throws ParseException {
		ArrayList<String> res = new ArrayList<String>();
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		String docuArreteChangementClasse = repPartage + "Avancement/arretesChangementClasseDetaches.doc";
		String docuArreteAvctDiff = repPartage + "Avancement/arretesAvancementDiffDetaches.doc";

		// on verifie l'existance de chaque fichier
		boolean existsDocuArreteChangementClasse = new File(docuArreteChangementClasse).exists();
		if (existsDocuArreteChangementClasse) {
			res.add(docuArreteChangementClasse);
		}
		boolean existsDocuArreteAvctDiff = new File(docuArreteAvctDiff).exists();
		if (existsDocuArreteAvctDiff) {
			res.add(docuArreteAvctDiff);
		}
		return res;
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_REGUL_ARR_IMPR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_REGUL_ARR_IMPR(int i) {
		return "NOM_CK_REGUL_ARR_IMPR_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_REGUL_SGC_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_REGUL_ARR_IMPR(int i) {
		return getZone(getNOM_CK_REGUL_ARR_IMPR(i));
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

	public AvancementDetachesDao getAvancementDetachesDao() {
		return avancementDetachesDao;
	}

	public void setAvancementDetachesDao(AvancementDetachesDao avancementDetachesDao) {
		this.avancementDetachesDao = avancementDetachesDao;
	}

	public HistoCarriereDao getHistoCarriereDao() {
		return histoCarriereDao;
	}

	public void setHistoCarriereDao(HistoCarriereDao histoCarriereDao) {
		this.histoCarriereDao = histoCarriereDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}