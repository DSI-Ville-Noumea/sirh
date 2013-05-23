package nc.mairie.gestionagent.process.avancement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.technique.BasicProcess;
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
import org.springframework.http.HttpStatus;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctArretes extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OeAVCTFonctArretes.class);

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_ANNEE;
	private String[] LB_FILIERE;
	private String[] LB_AVIS_CAP_AD;
	private String[] LB_AVIS_CAP_CLASSE;
	private String[] LB_AVIS_EMP_AD;
	private String[] LB_AVIS_EMP_CLASSE;

	private String[] listeAnnee;
	private String anneeSelect;
	private ArrayList<FiliereGrade> listeFiliere;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private ArrayList<AvancementFonctionnaires> listeAvct;
	private ArrayList<AvisCap> listeAvisCAPMinMoyMax;
	private ArrayList<AvisCap> listeAvisCAPFavDefav;

	private Hashtable<String, AvisCap> hashAvisCAP;

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

		initialiseListeService();

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNoMatricule());
		}

		// Initialisation de la liste des documents suivi medicaux
		if (getListeDocuments() == null || getListeDocuments().size() == 0) {
			setListeDocuments(listerDocumentsArretes());
			afficheListeDocuments();

		}
	}

	private ArrayList<String> listerDocumentsArretes() throws ParseException {
		ArrayList<String> res = new ArrayList<String>();
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		String docuArreteChangementClasse = repPartage + "Avancement/arretesChangementClasse.doc";
		String docuArreteAvctDiff = repPartage + "Avancement/arretesAvancementDiff.doc";

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
			addZone(getNOM_ST_GRADE(i),
					av.getGrade() + " <br> " + (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct());
			addZone(getNOM_ST_DATE_AVCT(i), (av.getDateAvctMini() == null ? "&nbsp;" : av.getDateAvctMini()) + " <br> " + av.getDateAvctMoy()
					+ " <br> " + (av.getDateAvctMaxi() == null ? "&nbsp;" : av.getDateAvctMaxi()));
			// motif avancement
			MotifAvancement motifVDN = MotifAvancement.chercherMotifAvancement(getTransaction(), av.getIdMotifAvct());
			// avis SHD
			String avisSHD = av.getAvisSHD() == null ? "&nbsp;" : av.getAvisSHD();

			// avis VDN
			String avisVDN = av.getIdAvisCAP() == null ? "&nbsp;" : AvisCap.chercherAvisCap(getTransaction(), av.getIdAvisCAP()).getLibCourtAvisCAP()
					.toUpperCase();
			addZone(getNOM_ST_MOTIF_AVCT(i), (motifVDN == null ? "&nbsp;" : motifVDN.getCodeMotifAvct()) + " <br> " + avisSHD + " <br> " + avisVDN);

			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_OBSERVATION(i), av.getObservationArr() == null ? "&nbsp;" : av.getObservationArr());
			addZone(getNOM_CK_REGUL_ARR_IMPR(i), av.isRegularisation() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_VALID_ARR(i), av.getEtat().equals(EnumEtatAvancement.ARRETE.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());

			if (av.getIdAvisArr() == null) {
				if (!av.getIdAvisCAP().equals(Const.CHAINE_VIDE)) {
					if (av.getIdAvisCAP().equals("3")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("3"))));
					} else if (av.getIdAvisCAP().equals("2")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("2"))));
					} else if (av.getIdAvisCAP().equals("1")) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("1"))));
					} else if (av.getIdAvisCAP().equals("4")) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("4"))));
					} else if (av.getIdAvisCAP().equals("5")) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("5"))));
					}

				} else {
					addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("2"))));
					addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("4"))));
				}

			} else {
				addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
						av.getIdAvisArr() == null || av.getIdAvisArr().length() == 0 ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPMinMoyMax()
								.indexOf(getHashAvisCAP().get(av.getIdAvisArr()))));
				addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i), av.getIdAvisArr() == null || av.getIdAvisArr().length() == 0 ? Const.CHAINE_VIDE
						: String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(av.getIdAvisArr()))));
			}
			if (av.getIdAvisEmp() == null) {
				if (!av.getIdAvisCAP().equals(Const.CHAINE_VIDE)) {
					if (av.getIdAvisCAP().equals("3")) {
						addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("3"))));
					} else if (av.getIdAvisCAP().equals("2")) {
						addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("2"))));
					} else if (av.getIdAvisCAP().equals("1")) {
						addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("1"))));
					} else if (av.getIdAvisCAP().equals("4")) {
						addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("4"))));
					} else if (av.getIdAvisCAP().equals("5")) {
						addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("5"))));
					}

				} else {
					addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i), String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get("2"))));
					addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i), String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get("4"))));
				}

			} else {
				addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i),
						av.getIdAvisEmp() == null || av.getIdAvisEmp().length() == 0 ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPMinMoyMax()
								.indexOf(getHashAvisCAP().get(av.getIdAvisEmp()))));
				addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i), av.getIdAvisEmp() == null || av.getIdAvisEmp().length() == 0 ? Const.CHAINE_VIDE
						: String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(av.getIdAvisEmp()))));
			}

			String user = av.getUserVerifArrImpr() == null ? "&nbsp;" : av.getUserVerifArrImpr();
			String heure = av.getHeureVerifArrImpr() == null ? "&nbsp;" : av.getHeureVerifArrImpr();
			String date = av.getDateVerifArrImpr() == null ? "&nbsp;" : av.getDateVerifArrImpr();
			addZone(getNOM_ST_USER_VALID_ARR_IMPR(i), user + " <br> " + date + " <br> " + heure);

			// date de la cap
			addZone(getNOM_ST_DATE_CAP(i), av.getDateCap() == null ? "&nbps;" : av.getDateCap());

			// date avct
			if (av.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
				if (av.getIdMotifAvct().equals("7")) {
					// on récupere l'avis Emp
					int indiceAvisCapMinMoyMaxEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_AD_SELECT(i)) ? Integer
							.parseInt(getVAL_LB_AVIS_EMP_AD_SELECT(i)) : -1);
					if (indiceAvisCapMinMoyMaxEmp != -1) {
						String idAvisEmp = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxEmp)).getLibCourtAvisCAP().toUpperCase();
						String dateAvctFinale = Const.CHAINE_VIDE;
						if (idAvisEmp.equals("MIN")) {
							dateAvctFinale = av.getDateAvctMini();
						} else if (idAvisEmp.equals("MOY")) {
							dateAvctFinale = av.getDateAvctMoy();
						} else if (idAvisEmp.equals("MAX")) {
							dateAvctFinale = av.getDateAvctMaxi();
						}
						addZone(getNOM_ST_DATE_AVCT_FINALE(i), dateAvctFinale);
					}
				} else if (av.getIdMotifAvct().equals("6")) {
					addZone(getNOM_ST_DATE_AVCT_FINALE(i), av.getDateAvctMoy());
				} else {
					// on récupere l'avis Emp
					int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(i)) ? Integer
							.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(i)) : -1);
					if (indiceAvisCapFavDefavEmp != -1) {
						String idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp)).getLibCourtAvisCAP().toUpperCase();
						String dateAvctFinale = Const.CHAINE_VIDE;
						if (idAvisEmp.equals("FAV")) {
							dateAvctFinale = av.getDateAvctMoy();
						}
						addZone(getNOM_ST_DATE_AVCT_FINALE(i), dateAvctFinale);
					}
				}
			} else {
				addZone(getNOM_ST_DATE_AVCT_FINALE(i), Const.CHAINE_VIDE);
			}

		}
	}

	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList<Service> services = Service.listerServiceActif(getTransaction());
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

				// recherche du supérieur
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

		// Si liste medecins vide alors affectation
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

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAPMinMoyMax() == null || getListeAvisCAPMinMoyMax().size() == 0) {
			ArrayList<AvisCap> avis = AvisCap.listerAvisCapMinMoyMax(getTransaction());
			setListeAvisCAPMinMoyMax(avis);

			int[] tailles = { 15 };
			String[] champs = { "libLongAvisCAP" };
			setLB_AVIS_CAP_AD(new FormateListe(tailles, avis, champs).getListeFormatee(false));
			setLB_AVIS_EMP_AD(new FormateListe(tailles, avis, champs).getListeFormatee(false));

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPMinMoyMax().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPMinMoyMax().get(i);
				getHashAvisCAP().put(ac.getIdAvisCAP(), ac);
			}
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAPFavDefav() == null || getListeAvisCAPFavDefav().size() == 0) {
			ArrayList<AvisCap> avis = AvisCap.listerAvisCapFavDefav(getTransaction());
			setListeAvisCAPFavDefav(avis);

			int[] tailles = { 15 };
			String[] champs = { "libLongAvisCAP" };
			setLB_AVIS_CAP_CLASSE(new FormateListe(tailles, avis, champs).getListeFormatee(false));
			setLB_AVIS_CAP_CLASSE_EMP(new FormateListe(tailles, avis, champs).getListeFormatee(false));

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPFavDefav().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPFavDefav().get(i);
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

			// Si clic sur le bouton PB_CONSULTER_TABLEAU
			for (int i = 0; i < getListeAvct().size(); i++) {
				AvancementFonctionnaires avct = getListeAvct().get(i);
				if (testerParametre(request, getNOM_PB_SET_DATE_AVCT(Integer.valueOf(avct.getIdAvct())))) {
					return performPB_SET_DATE_AVCT(request, avct.getIdAvct());
				}
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
	public OeAVCTFonctArretes() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctArretes.jsp";
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
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
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

		String reqEtat = " and (ETAT='" + EnumEtatAvancement.SEF.getValue() + "' or ETAT='" + EnumEtatAvancement.ARRETE.getValue() + "')";
		setListeAvct(AvancementFonctionnaires.listerAvancementAvecAnneeEtat(getTransaction(), annee, reqEtat, filiere, agent, listeSousService));

		afficheListeAvancement();
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

		verifieRepertoire("Avancement");
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		// on supprime les documents existants
		String docuChangementClasse = "Avancement/arretesChangementClasse.doc";
		String docuAvctDiff = "Avancement/arretesAvancementDiff.doc";
		// on verifie l'existance de chaque fichier
		File chgtClasse = new File(repPartage.substring(8, repPartage.length()) + docuChangementClasse);
		if (chgtClasse.exists()) {
			chgtClasse.delete();
		}
		File avctDiff = new File(repPartage.substring(8, repPartage.length()) + docuAvctDiff);
		if (avctDiff.exists()) {
			avctDiff.delete();
		}

		// on sauvegarde l'état du tableau afin de sauvegarder les
		// regularisations
		if (!performPB_VALIDER(request)) {
			// "ERR184",
			// "Une erreur est survenue dans la sauvegarde du tableau. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR184"));
			return false;
		}
		ArrayList<Integer> listeImpressionChangementClasse = new ArrayList<Integer>();
		ArrayList<Integer> listeImpressionAvancementDiff = new ArrayList<Integer>();

		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer idAvct = Integer.valueOf(avct.getIdAvct());
			if (getVAL_CK_VALID_ARR_IMPR(idAvct).equals(getCHECKED_ON())) {
				if (avct.getIdMotifAvct().equals("4")) {
					// on fait une liste des arretes changement classe
					listeImpressionChangementClasse.add(Integer.valueOf(avct.getIdAgent()));
				} else if (avct.getIdMotifAvct().equals("7") || avct.getIdMotifAvct().equals("6")) {
					// on fait une liste des arretes avancement diffé
					listeImpressionAvancementDiff.add(Integer.valueOf(avct.getIdAgent()));
				} else {
					continue;
				}
			}
			addZone(getNOM_CK_VALID_ARR_IMPR(idAvct), getCHECKED_OFF());

		}
		if (listeImpressionChangementClasse.size() > 0) {

			try {
				byte[] fileAsBytes = getArretesReportAsByteArray(listeImpressionChangementClasse.toString().replace("[", "").replace("]", "")
						.replace(" ", ""), true, Integer.valueOf(getAnneeSelect()));

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

				byte[] fileAsBytes = getArretesReportAsByteArray(
						listeImpressionAvancementDiff.toString().replace("[", "").replace("]", "").replace(" ", ""), false,
						Integer.valueOf(getAnneeSelect()));
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

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
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

	public byte[] getArretesReportAsByteArray(String csvAgents, boolean isChangementClasse, int anneeAvct) throws Exception {

		ClientResponse response = createAndFireRequest(csvAgents, isChangementClasse, anneeAvct);

		return readResponseAsByteArray(response);
	}

	public ClientResponse createAndFireRequest(String csvAgents, boolean isChangementClasse, int anneeAvct) {
		String urlWSArretes = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL_ARRETES_AVCT") + "?isChangementClasse=" + isChangementClasse
				+ "&csvIdAgents=" + csvAgents + "&annee=" + anneeAvct;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return response;
	}

	public byte[] readResponseAsByteArray(ClientResponse response) throws Exception {

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
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementFonctionnaires avctDepart = (AvancementFonctionnaires) getListeAvct().get(j);
			AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancement(getTransaction(), avctDepart.getIdAvct());
			Integer idAvct = Integer.valueOf(avct.getIdAvct());
			// on fait les modifications
			// on traite l'etat
			if (getVAL_CK_VALID_ARR(idAvct).equals(getCHECKED_ON())) {
				// si la ligne est cochée
				// on regarde si l'etat est deja ARR
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat à ARR et on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifArr(user.getUserName());
					avct.setDateVerifArr(dateJour);
					avct.setHeureVerifArr(heureAction);
					avct.setEtat(EnumEtatAvancement.ARRETE.getValue());
				}
				if (getVAL_CK_VALID_ARR_IMPR(idAvct).equals(getCHECKED_ON())) {
					// si la ligne est cochée
					// on regarde si l'etat est deja ARR
					// --> oui on ne modifie pas le user
					// --> non on passe l'etat à ARR et on met à jour le user
					if (avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
						// on sauvegarde qui a fait l'action
						avct.setUserVerifArrImpr(user.getUserName());
						avct.setDateVerifArrImpr(dateJour);
						avct.setHeureVerifArrImpr(heureAction);
					}
				}
			} else {
				// si la ligne n'est pas cochée
				// on regarde quel etat son etat
				// --> si ARR alors on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifArr(user.getUserName());
					avct.setDateVerifArr(dateJour);
					avct.setHeureVerifArr(heureAction);
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				}

			}
			// on traite la regularisation
			if (getVAL_CK_REGUL_ARR_IMPR(idAvct).equals(getCHECKED_ON())) {
				avct.setRegularisation(true);
			} else {
				avct.setRegularisation(false);
			}

			if (avct.getIdMotifAvct().equals("7")) {
				// on traite l'avis CAP
				int indiceAvisCapMinMoyMaxCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) : -1);
				if (indiceAvisCapMinMoyMaxCap != -1) {
					String idAvisArr = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxCap)).getIdAvisCAP();
					avct.setIdAvisArr(idAvisArr);
				}
				// on traite l'avis Emp
				int indiceAvisCapMinMoyMaxEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_AD_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_AD_SELECT(idAvct)) : -1);
				if (indiceAvisCapMinMoyMaxEmp != -1) {
					String idAvisEmp = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxEmp)).getIdAvisCAP();
					avct.setIdAvisEmp(idAvisEmp);
				}
			} else if (avct.getIdMotifAvct().equals("6")) {
				// on traite l'avis CAP
				avct.setIdAvisArr("2");
				// on traite l'avis Emp
				avct.setIdAvisEmp("2");

			} else {
				// on traite l'avis CAP
				int indiceAvisCapFavDefavCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) : -1);
				if (indiceAvisCapFavDefavCap != -1) {
					String idAvisArr = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavCap)).getIdAvisCAP();
					avct.setIdAvisArr(idAvisArr);
				}
				// on traite l'avis Emp
				int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) : -1);
				if (indiceAvisCapFavDefavEmp != -1) {
					String idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp)).getIdAvisCAP();
					avct.setIdAvisEmp(idAvisEmp);
				}
			}
			avct.setObservationArr(getVAL_ST_OBSERVATION(idAvct));

			avct.modifierAvancement(getTransaction());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		// on remet la liste à vide afin qu'elle soit de nouveau initialisée
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
		return "ECR-AVCT-FONCT-ARRETES";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enlève le service selectionnée
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
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
	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	/**
	 * Met à jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Retourne une hashTable de la hiérarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_FILIERE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_FILIERE() {
		return getLB_FILIERE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_ARR(int i) {
		return "NOM_CK_VALID_ARR_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_VALID_SGC_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_ARR(int i) {
		return getZone(getNOM_CK_VALID_ARR(i));
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
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
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVIS_CAP_AD_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_AD_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_AD_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP_AD(int i) {
		return getLB_AVIS_CAP_AD(i);
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
	 * NOM_LB_AVIS_CAP_CLASSE_EMP_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_EMP_CLASSE_SELECT(int i) {
		return "NOM_LB_AVIS_EMP_CLASSE_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_EMP_CLASSE_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_EMP_CLASSE(int i) {
		return "NOM_LB_AVIS_EMP_CLASSE_" + i;
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_EMP_CLASSE(int i) {
		return getLB_AVIS_EMP_CLASSE(i);
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_EMP_CLASSE(int i) {
		if (LB_AVIS_EMP_CLASSE == null)
			LB_AVIS_EMP_CLASSE = initialiseLazyLB();
		return LB_AVIS_EMP_CLASSE;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP_CLASSE Date de création : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP_CLASSE_EMP(String[] newLB_AVIS_EMP_CLASSE) {
		LB_AVIS_EMP_CLASSE = newLB_AVIS_EMP_CLASSE;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVIS_CAP_AD_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_EMP_AD_SELECT(int i) {
		return "NOM_LB_AVIS_EMP_AD_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_EMP_AD_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_EMP_AD_SELECT(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_EMP_AD(int i) {
		return "NOM_LB_AVIS_EMP_AD_" + i;
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_EMP_AD(int i) {
		return getLB_AVIS_EMP_AD(i);
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_EMP_AD(int i) {
		if (LB_AVIS_EMP_AD == null)
			LB_AVIS_EMP_AD = initialiseLazyLB();
		return LB_AVIS_EMP_AD;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP_AD Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_AVIS_EMP_AD(String[] newLB_AVIS_EMP_AD) {
		LB_AVIS_EMP_AD = newLB_AVIS_EMP_AD;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_OBSERVATION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_OBSERVATION(int i) {
		return "NOM_ST_OBSERVATION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_OBSERVATION
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_OBSERVATION(int i) {
		return getZone(getNOM_ST_OBSERVATION(i));
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
	 * à cocher : CK_VALID_SGC_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_ARR_IMPR(int i) {
		return getZone(getNOM_CK_VALID_ARR_IMPR(i));
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
	 * à cocher : CK_REGUL_SGC_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_REGUL_ARR_IMPR(int i) {
		return getZone(getNOM_CK_REGUL_ARR_IMPR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_VALID_SGC_ARR
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_VALID_ARR_IMPR(int i) {
		return "NOM_ST_USER_VALID_ARR_IMPR_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_USER_VALID_SGC_ARR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_ARR_IMPR(int i) {
		return getZone(getNOM_ST_USER_VALID_ARR_IMPR(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SET_DATE_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_SET_DATE_AVCT(int i) {
		return "NOM_PB_SET_DATE_AVCT_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_SET_DATE_AVCT(HttpServletRequest request, String idAvct) throws Exception {
		Integer indiceElemen = Integer.valueOf(idAvct);
		if (getVAL_CK_VALID_ARR(indiceElemen).equals(getCHECKED_ON())) {
			// on verifie si la date de CAP est remplie
			// date de debut obligatoire
			if ((Const.CHAINE_VIDE).equals(getVAL_ST_DATE_CAP_GLOBALE())) {
				addZone(getNOM_CK_VALID_ARR(indiceElemen), getCHECKED_OFF());
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de cap"));
				return false;
			}

			// format date de debut
			if (!Services.estUneDate(getVAL_ST_DATE_CAP_GLOBALE())) {
				addZone(getNOM_CK_VALID_ARR(indiceElemen), getCHECKED_OFF());
				// "ERR007",
				// "La date @ est incorrecte. Elle doit être au format date."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de cap"));
				return false;
			}
			// on sauvegarde la ligne
			if (!performPB_VALIDER(request, indiceElemen))
				return false;

			AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancement(getTransaction(), idAvct);
			if (avct.getIdMotifAvct().equals("7")) {
				// on récupere l'avis Emp
				if (avct.getIdAvisEmp() != null) {
					String idAvisEmp = AvisCap.chercherAvisCap(getTransaction(), avct.getIdAvisEmp()).getLibCourtAvisCAP().toUpperCase();
					String dateAvctFinale = Const.CHAINE_VIDE;
					if (idAvisEmp.equals("MIN")) {
						dateAvctFinale = avct.getDateAvctMini();
					} else if (idAvisEmp.equals("MOY")) {
						dateAvctFinale = avct.getDateAvctMoy();
					} else if (idAvisEmp.equals("MAX")) {
						dateAvctFinale = avct.getDateAvctMaxi();
					}
					addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), dateAvctFinale);
				}
			} else if (avct.getIdMotifAvct().equals("6")) {
				addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), avct.getDateAvctMoy());
			} else {
				// on récupere l'avis Emp
				if (avct.getIdAvisEmp() != null) {
					String idAvisEmp = AvisCap.chercherAvisCap(getTransaction(), avct.getIdAvisEmp()).getLibCourtAvisCAP().toUpperCase();
					String dateAvctFinale = Const.CHAINE_VIDE;
					if (idAvisEmp.equals("FAV")) {
						dateAvctFinale = avct.getDateAvctMoy();
					}
					addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), dateAvctFinale);
				}
			}
			// on met la date de CAP
			String dateCAP = getVAL_ST_DATE_CAP_GLOBALE();
			addZone(getNOM_ST_DATE_CAP(indiceElemen), dateCAP);
			avct.setDateCap(dateCAP);
			avct.modifierAvancement(getTransaction());

			if (getTransaction().isErreur())
				return false;
			// on enregistre
			commitTransaction();

		} else {
			// on sauvegarde la ligne
			if (!performPB_VALIDER(request, indiceElemen))
				return false;

			AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancement(getTransaction(), idAvct);
			addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), Const.CHAINE_VIDE);
			addZone(getNOM_ST_DATE_CAP(indiceElemen), Const.CHAINE_VIDE);
			avct.setDateCap(Const.DATE_NULL);
			avct.modifierAvancement(getTransaction());

			if (getTransaction().isErreur())
				return false;
			// on enregistre
			commitTransaction();
		}

		return true;
	}

	private boolean performPB_VALIDER(HttpServletRequest request, Integer indiceElemen) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		String dateJour = Services.dateDuJour();
		// on sauvegarde l'état du tableau
		// on recupère la ligne concernée
		AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancement(getTransaction(), indiceElemen.toString());
		// on fait les modifications
		// on traite l'etat
		if (getVAL_CK_VALID_ARR(indiceElemen).equals(getCHECKED_ON())) {
			// si la ligne est cochée
			// on regarde si l'etat est deja ARR
			// --> oui on ne modifie pas le user
			// --> non on passe l'etat à ARR et on met à jour le user
			if (avct.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
				// on sauvegarde qui a fait l'action
				avct.setUserVerifArr(user.getUserName());
				avct.setDateVerifArr(dateJour);
				avct.setHeureVerifArr(heureAction);
				avct.setEtat(EnumEtatAvancement.ARRETE.getValue());
			}
			if (getVAL_CK_VALID_ARR_IMPR(indiceElemen).equals(getCHECKED_ON())) {
				// si la ligne est cochée
				// on regarde si l'etat est deja ARR
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat à ARR et on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifArrImpr(user.getUserName());
					avct.setDateVerifArrImpr(dateJour);
					avct.setHeureVerifArrImpr(heureAction);
				}
			}
		} else {
			// si la ligne n'est pas cochée
			// on regarde quel etat son etat
			// --> si ARR alors on met à jour le user
			if (avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
				// on sauvegarde qui a fait l'action
				avct.setUserVerifArr(user.getUserName());
				avct.setDateVerifArr(dateJour);
				avct.setHeureVerifArr(heureAction);
				avct.setEtat(EnumEtatAvancement.SEF.getValue());
			}

		}
		// on traite la regularisation
		if (getVAL_CK_REGUL_ARR_IMPR(indiceElemen).equals(getCHECKED_ON())) {
			avct.setRegularisation(true);
		} else {
			avct.setRegularisation(false);
		}

		if (getVAL_CK_VALID_ARR(indiceElemen).equals(getCHECKED_ON())) {
			if (avct.getIdMotifAvct().equals("7")) {
				// on traite l'avis CAP
				int indiceAvisCapMinMoyMaxCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_AD_SELECT(indiceElemen)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_AD_SELECT(indiceElemen)) : -1);
				if (indiceAvisCapMinMoyMaxCap != -1) {
					String idAvisArr = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxCap)).getIdAvisCAP();
					avct.setIdAvisArr(idAvisArr);
				}
				// on traite l'avis Emp
				int indiceAvisCapMinMoyMaxEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_AD_SELECT(indiceElemen)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_AD_SELECT(indiceElemen)) : -1);
				if (indiceAvisCapMinMoyMaxEmp != -1) {
					String idAvisEmp = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxEmp)).getIdAvisCAP();
					avct.setIdAvisEmp(idAvisEmp);
				}
			} else if (avct.getIdMotifAvct().equals("6")) {
				// on traite l'avis CAP
				avct.setIdAvisArr("2");
				// on traite l'avis Emp
				avct.setIdAvisEmp("2");

			} else {
				// on traite l'avis CAP
				int indiceAvisCapFavDefavCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_CLASSE_SELECT(indiceElemen)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_CLASSE_SELECT(indiceElemen)) : -1);
				if (indiceAvisCapFavDefavCap != -1) {
					String idAvisArr = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavCap)).getIdAvisCAP();
					avct.setIdAvisArr(idAvisArr);
				}
				// on traite l'avis Emp
				int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(indiceElemen)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(indiceElemen)) : -1);
				if (indiceAvisCapFavDefavEmp != -1) {
					String idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp)).getIdAvisCAP();
					avct.setIdAvisEmp(idAvisEmp);
				}
			}
			avct.setObservationArr(getVAL_ST_OBSERVATION(indiceElemen));
		}

		avct.modifierAvancement(getTransaction());
		if (getTransaction().isErreur())
			return false;

		// on enregistre
		commitTransaction();
		// on remet la liste à vide afin qu'elle soit de nouveau initialisée
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_AVCT_FINALE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_AVCT_FINALE(int i) {
		return "NOM_ST_DATE_AVCT_FINALE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DATE_AVCT_FINALE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_AVCT_FINALE(int i) {
		return getZone(getNOM_ST_DATE_AVCT_FINALE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_CAP_GLOBALE() {
		return "NOM_ST_DATE_CAP_GLOBALE_";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_CAP_GLOBALE() {
		return getZone(getNOM_ST_DATE_CAP_GLOBALE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_CAP(int i) {
		return "NOM_ST_DATE_CAP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_CAP(int i) {
		return getZone(getNOM_ST_DATE_CAP(i));
	}

	private ArrayList<String> listeDocuments;

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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

	public boolean isDefavorable(Integer idAvct) throws Exception {
		AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancement(getTransaction(), idAvct.toString());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			return true;
		}

		if (avct.getIdMotifAvct().equals("4")) {
			// on traite l'avis Emp
			int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) ? Integer
					.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) : -1);
			if (indiceAvisCapFavDefavEmp != -1) {
				String idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp)).getLibCourtAvisCAP();
				if (idAvisEmp.toUpperCase().equals("DEF")) {
					return true;
				} else {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}