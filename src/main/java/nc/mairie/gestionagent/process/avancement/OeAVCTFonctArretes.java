package nc.mairie.gestionagent.process.avancement;

import java.io.BufferedOutputStream;
import java.io.File;
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
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.parametrage.Cap;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.parametrage.CapDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhWSConsumer;
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
import org.springframework.context.ApplicationContext;

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
	private String[] LB_CAP;
	private String[] LB_CATEGORIE;
	private String[] LB_VERIF_SGC;
	private String[] LB_AVIS_CAP_AD;
	private String[] LB_AVIS_CAP_CLASSE;
	private String[] LB_AVIS_EMP_AD;
	private String[] LB_AVIS_EMP_CLASSE;

	private String[] listeAnnee;
	private String anneeSelect;
	private ArrayList<FiliereGrade> listeFiliere;
	private ArrayList<Cap> listeCap;
	private ArrayList<Grade> listeCategorie;
	private ArrayList<String> listeVerifSGC;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private ArrayList<AvancementFonctionnaires> listeAvct;
	private ArrayList<AvisCap> listeAvisCAPMinMoyMax;
	private ArrayList<AvisCap> listeAvisCAPFavDefav;

	private Hashtable<Integer, AvisCap> hashAvisCAP;

	private CapDao capDao;

	private ArrayList<String> listeDocuments;
	private String urlFichier;

	private MotifAvancementDao motifAvancementDao;
	private AutreAdministrationDao autreAdministrationDao;
	private AvisCapDao avisCapDao;
	private AvancementFonctionnairesDao avancementFonctionnairesDao;
	private AgentDao agentDao;
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

		initialiseListeService();

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
		}

		// Initialisation de la liste des documents suivi medicaux
		if (getListeDocuments() == null || getListeDocuments().size() == 0) {
			setListeDocuments(listerDocumentsArretes());
			afficheListeDocuments();

		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getCapDao() == null) {
			setCapDao(new CapDao((SirhDao) context.getBean("sirhDao")));
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
			Integer i = av.getIdAvct();
			Agent agent = getAgentDao().chercherAgent(av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DIRECTION(i),
					Services.estNumerique(av.getDirectionService()) ? getAutreAdministrationDao()
							.chercherAutreAdministration(Integer.valueOf(av.getDirectionService())).getLibAutreAdmin()
							: av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i),
					(av.getCdcadr() == null ? "&nbsp;" : av.getCdcadr()) + " <br> " + av.getFiliere());
			PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
			addZone(getNOM_ST_PA(i), pa.getLiPAdm());
			addZone(getNOM_ST_GRADE_ANCIEN(i), av.getGrade());
			addZone(getNOM_ST_GRADE_NOUVEAU(i),
					(av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct().toString());
			addZone(getNOM_ST_DATE_AVCT(i),
					(av.getDateAvctMini() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateAvctMini())) + " <br> "
							+ sdfFormatDate.format(av.getDateAvctMoy()) + " <br> "
							+ (av.getDateAvctMaxi() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateAvctMaxi())));
			// motif avancement
			MotifAvancement motifVDN = null;
			if (av.getIdMotifAvct() != null) {
				motifVDN = getMotifAvancementDao().chercherMotifAvancement(av.getIdMotifAvct());
			}
			// avis SHD
			String avisSHD = av.getAvisShd() == null ? "&nbsp;" : av.getAvisShd();

			// avis VDN
			String avisVDN = av.getIdAvisCap() == null ? "&nbsp;" : getAvisCapDao().chercherAvisCap(av.getIdAvisCap())
					.getLibCourtAvisCap().toUpperCase();
			addZone(getNOM_ST_MOTIF_AVCT(i), (motifVDN == null ? "&nbsp;" : motifVDN.getCode()) + " <br> " + avisSHD
					+ " <br> " + avisVDN);

			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_OBSERVATION(i), av.getObservationArr() == null ? "&nbsp;" : av.getObservationArr());
			addZone(getNOM_CK_REGUL_ARR_IMPR(i), av.isRegularisation() ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_VALID_ARR(i), av.getEtat().equals(EnumEtatAvancement.ARRETE.getValue()) ? getCHECKED_ON()
					: getCHECKED_OFF());

			if (av.getIdAvisArr() == null) {
				if (av.getIdAvisCap() != null) {
					if (av.getIdAvisCap() == 3) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
								String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(3))));
					} else if (av.getIdAvisCap() == 2) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
								String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
					} else if (av.getIdAvisCap() == 1) {
						addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
								String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(1))));
					} else if (av.getIdAvisCap() == 4) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i),
								String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
					} else if (av.getIdAvisCap() == 5) {
						addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i),
								String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(5))));
					}

				} else {
					addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
							String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
					addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i),
							String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
				}

			} else {
				addZone(getNOM_LB_AVIS_CAP_AD_SELECT(i),
						av.getIdAvisArr() == null ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPMinMoyMax()
								.indexOf(getHashAvisCAP().get(av.getIdAvisArr()))));
				addZone(getNOM_LB_AVIS_CAP_CLASSE_SELECT(i),
						av.getIdAvisArr() == null ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPFavDefav()
								.indexOf(getHashAvisCAP().get(av.getIdAvisArr()))));
			}
			if (av.getIdAvisEmp() == null) {
				if (av.getIdAvisCap() != null) {
					if (av.getIdAvisCap() == 3) {
						addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i),
								String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(3))));
					} else if (av.getIdAvisCap() == 2) {
						addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i),
								String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
					} else if (av.getIdAvisCap() == 1) {
						addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i),
								String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(1))));
					} else if (av.getIdAvisCap() == 4) {
						addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i),
								String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
					} else if (av.getIdAvisCap() == 5) {
						addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i),
								String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(5))));
					}

				} else {
					addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i),
							String.valueOf(getListeAvisCAPMinMoyMax().indexOf(getHashAvisCAP().get(2))));
					addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i),
							String.valueOf(getListeAvisCAPFavDefav().indexOf(getHashAvisCAP().get(4))));
				}

			} else {
				addZone(getNOM_LB_AVIS_EMP_AD_SELECT(i),
						av.getIdAvisEmp() == null ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPMinMoyMax()
								.indexOf(getHashAvisCAP().get(av.getIdAvisEmp()))));
				addZone(getNOM_LB_AVIS_EMP_CLASSE_SELECT(i),
						av.getIdAvisEmp() == null ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAPFavDefav()
								.indexOf(getHashAvisCAP().get(av.getIdAvisEmp()))));
			}

			String user = av.getUserVerifArrImpr() == null ? "&nbsp;" : av.getUserVerifArrImpr();
			String heure = av.getHeureVerifArrImpr() == null ? "&nbsp;" : av.getHeureVerifArrImpr();
			String date = av.getDateVerifArrImpr() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateVerifArrImpr());
			addZone(getNOM_ST_USER_VALID_ARR_IMPR(i), user + " <br> " + date + " <br> " + heure);

			// date de la cap
			addZone(getNOM_ST_DATE_CAP(i), av.getDateCap() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateCap()));

			// date avct
			if (av.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
				if (av.getIdMotifAvct() == 7) {
					// on récupere l'avis Emp
					int indiceAvisCapMinMoyMaxEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_AD_SELECT(i)) ? Integer
							.parseInt(getVAL_LB_AVIS_EMP_AD_SELECT(i)) : -1);
					if (indiceAvisCapMinMoyMaxEmp != -1) {
						String idAvisEmp = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxEmp))
								.getLibCourtAvisCap().toUpperCase();
						Date dateAvctFinale = null;
						if (idAvisEmp.equals("MIN")) {
							dateAvctFinale = av.getDateAvctMini();
						} else if (idAvisEmp.equals("MOY")) {
							dateAvctFinale = av.getDateAvctMoy();
						} else if (idAvisEmp.equals("MAX")) {
							dateAvctFinale = av.getDateAvctMaxi();
						}
						addZone(getNOM_ST_DATE_AVCT_FINALE(i), dateAvctFinale == null ? Const.CHAINE_VIDE
								: sdfFormatDate.format(dateAvctFinale));
					}
				} else if (av.getIdMotifAvct() == 6) {
					addZone(getNOM_ST_DATE_AVCT_FINALE(i), sdfFormatDate.format(av.getDateAvctMoy()));
				} else {
					// on récupere l'avis Emp
					int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(i)) ? Integer
							.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(i)) : -1);
					if (indiceAvisCapFavDefavEmp != -1) {
						String idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp))
								.getLibCourtAvisCap().toUpperCase();
						Date dateAvctFinale = null;
						if (idAvisEmp.equals("FAV")) {
							dateAvctFinale = av.getDateAvctMoy();
						}
						addZone(getNOM_ST_DATE_AVCT_FINALE(i), dateAvctFinale == null ? Const.CHAINE_VIDE
								: sdfFormatDate.format(dateAvctFinale));
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
			String anneeCourante = (String) ServletAgent.getMesParametres().get("ANNEE_AVCT");
			setListeAnnee(new String[1]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
			setAnneeSelect(String.valueOf(Integer.parseInt(anneeCourante) + 1));
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
		// Si liste cap vide alors affectation
		if (getListeCap() == null || getListeCap().size() == 0) {
			setListeCap(getCapDao().listerCap());

			int tailles[] = { 10 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Cap> list = getListeCap().listIterator(); list.hasNext();) {
				Cap cap = (Cap) list.next();
				String ligne[] = { cap.getCodeCap() };

				aFormat.ajouteLigne(ligne);
			}
			String ligneSans[] = { "SANS" };
			aFormat.ajouteLigne(ligneSans);
			setLB_CAP(aFormat.getListeFormatee(true));

		}

		// Si liste categorie vide alors affectation
		if (getListeCategorie() == null || getListeCategorie().size() == 0) {
			setListeCategorie(Grade.listerCategorieGrade(getTransaction()));

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (Grade list : getListeCategorie()) {
				String ligne[] = { list.getCodeCadre() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_CATEGORIE(aFormat.getListeFormatee(true));

		}

		// Si liste verif SGC vide alors affectation
		if (getLB_VERIF_SGC() == LBVide) {
			ArrayList<String> verif = new ArrayList<String>();
			verif.add("oui");
			verif.add("non");
			setListeVerifSGC(verif);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = verif.listIterator(); list.hasNext();) {
				String ligne[] = { list.next() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_VERIF_SGC(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_VERIF_SGC_SELECT(), Const.ZERO);
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAPMinMoyMax() == null || getListeAvisCAPMinMoyMax().size() == 0) {
			ArrayList<AvisCap> avis = getAvisCapDao().listerAvisCapMinMoyMax();
			setListeAvisCAPMinMoyMax(avis);

			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvisCap> list = getListeAvisCAPMinMoyMax().listIterator(); list.hasNext();) {
				AvisCap av = list.next();
				String ligne[] = { av.getLibLongAvisCap() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_AVIS_CAP_AD(aFormat.getListeFormatee(false));
			setLB_AVIS_EMP_AD(aFormat.getListeFormatee(false));

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

			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvisCap> list = getListeAvisCAPFavDefav().listIterator(); list.hasNext();) {
				AvisCap av = list.next();
				String ligne[] = { av.getLibLongAvisCap() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_AVIS_CAP_CLASSE(aFormat.getListeFormatee(false));
			setLB_AVIS_CAP_CLASSE_EMP(aFormat.getListeFormatee(false));

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAPFavDefav().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAPFavDefav().get(i);
				getHashAvisCAP().put(ac.getIdAvisCap(), ac);
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
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT())
				: -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		setAnneeSelect(annee);

		// Recuperation filiere
		FiliereGrade filiere = null;
		int indiceFiliere = (Services.estNumerique(getVAL_LB_FILIERE_SELECT()) ? Integer
				.parseInt(getVAL_LB_FILIERE_SELECT()) : -1);
		if (indiceFiliere > 0) {
			filiere = (FiliereGrade) getListeFiliere().get(indiceFiliere - 1);
		}

		// Recuperation CAP
		String idCap = null;
		int indiceCap = (Services.estNumerique(getVAL_LB_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_CAP_SELECT()) : -1);
		if (indiceCap > getListeCap().size()) {
			idCap = "null";
		} else if (indiceCap > 0) {
			Cap cap = (Cap) getListeCap().get(indiceCap - 1);
			idCap = cap.getIdCap().toString();
		}

		// Recuperation categorie
		String categorie = null;
		int indiceCat = (Services.estNumerique(getVAL_LB_CATEGORIE_SELECT()) ? Integer
				.parseInt(getVAL_LB_CATEGORIE_SELECT()) : -1);
		if (indiceCat > 0) {
			categorie = getListeCategorie().get(indiceCat - 1).getCodeCadre();
		}

		// Recuperation verifSGC
		String verifSGC = null;
		int indiceVerifSGC = (Services.estNumerique(getVAL_LB_VERIF_SGC_SELECT()) ? Integer
				.parseInt(getVAL_LB_VERIF_SGC_SELECT()) : -1);
		if (indiceVerifSGC > 0) {
			verifSGC = (String) getListeVerifSGC().get(indiceVerifSGC - 1);
		}

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		// recuperation du service
		ArrayList<String> listeSousService = null;
		if (getVAL_ST_CODE_SERVICE().length() != 0) {
			// on recupere les sous-service du service selectionne
			Service serv = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			listeSousService = Service.listSousServiceBySigle(getTransaction(), serv.getSigleService());
		}

		String reqEtat = " and (ETAT='" + EnumEtatAvancement.SEF.getValue() + "' or ETAT='"
				+ EnumEtatAvancement.ARRETE.getValue() + "')";
		if (verifSGC != null) {
			if (verifSGC.equals("oui")) {
				reqEtat = " and (ETAT='" + EnumEtatAvancement.ARRETE.getValue() + "')";
			} else if (verifSGC.equals("non")) {
				reqEtat = " and (ETAT='" + EnumEtatAvancement.SEF.getValue() + "' )";
			}

		}
		setListeAvct(getAvancementFonctionnairesDao().listerAvancementAvecAnneeEtat(Integer.valueOf(annee), reqEtat,
				filiere == null ? null : filiere.getLibFiliere(), agent == null ? null : agent.getIdAgent(),
				listeSousService, categorie, idCap));

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
		ArrayList<Integer> listeImpressionChangementClasse = new ArrayList<Integer>();
		ArrayList<Integer> listeImpressionAvancementDiff = new ArrayList<Integer>();

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());

		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer idAvct = avct.getIdAvct();
			if (getVAL_CK_VALID_ARR_IMPR(idAvct).equals(getCHECKED_ON())) {
				if (avct.getIdMotifAvct() == 4) {
					// on fait une liste des arretes changement classe
					listeImpressionChangementClasse.add(avct.getIdAgent());
				} else if (avct.getIdMotifAvct() == 7 || avct.getIdMotifAvct() == 6 || avct.getIdMotifAvct() == 3) {
					// on fait une liste des arretes avancement diffé
					listeImpressionAvancementDiff.add(avct.getIdAgent());
				} else {
					continue;
				}
				// si la ligne est cochée
				// on regarde si l'etat est deja ARR
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat à ARR et on met à jour le user
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

					getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(),
							avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(),
							avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(),
							avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(),
							avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(),
							avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(),
							avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(),
							avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(),
							avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(),
							avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
							avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
							avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
							avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
							avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
							avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
							avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
							avct.getCodePa());
				} catch (Exception e) {
					getTransaction()
							.declarerErreur(
									"Une erreur est survenue dans la sauvegarde des avancements. Merci de contacter le responsable du projet.");
					return false;
				}
				commitTransaction();
			}
			addZone(getNOM_CK_VALID_ARR_IMPR(idAvct), getCHECKED_OFF());

		}
		if (listeImpressionChangementClasse.size() > 0) {

			try {
				byte[] fileAsBytes  = new SirhWSConsumer().downloadArrete(
						listeImpressionChangementClasse.toString().replace("[", "").replace("]", "").replace(" ", ""),
						true, Integer.valueOf(getAnneeSelect()), false);

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
				byte[] fileAsBytes = new SirhWSConsumer().downloadArrete(listeImpressionAvancementDiff.toString()
						.replace("[", "").replace("]", "").replace(" ", ""), false, Integer.valueOf(getAnneeSelect()),
						false);
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
		afficheListeAvancement();
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

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
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
			logger.error(String.format("An error occured while writing the report file to the following path  : "
					+ chemin + filename + " : " + e));
			return false;
		}
		return true;
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
		// on verifie si la date de CAP est remplie
		// date de debut obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_DATE_CAP_GLOBALE())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de cap"));
			return false;
		}

		// format date de debut
		if (!Services.estUneDate(getVAL_ST_DATE_CAP_GLOBALE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de cap"));
			return false;
		}

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		// on sauvegarde l'état du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementFonctionnaires avctDepart = (AvancementFonctionnaires) getListeAvct().get(j);
			AvancementFonctionnaires avct = getAvancementFonctionnairesDao().chercherAvancement(avctDepart.getIdAvct());
			Integer idAvct = avct.getIdAvct();
			// on fait les modifications
			// on traite l'etat
			if (getVAL_CK_VALID_ARR(idAvct).equals(getCHECKED_ON())) {
				avct = getAvancementFonctionnairesDao().chercherAvancement(avctDepart.getIdAvct());

				// si la ligne est cochée
				// on regarde si l'etat est deja ARR
				// --> oui on ne modifie pas le user
				// --> non on passe l'etat à ARR et on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.SEF.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifArr(user.getUserName());
					avct.setDateVerifArr(new Date());
					avct.setHeureVerifArr(heureAction);
					avct.setEtat(EnumEtatAvancement.ARRETE.getValue());

					performPB_SET_DATE_AVCT(request, avct, idAvct);
				}
			} else {
				// si la ligne n'est pas cochée
				// on regarde quel etat son etat
				// --> si ARR alors on met à jour le user
				if (avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())) {
					// on sauvegarde qui a fait l'action
					avct.setUserVerifArr(user.getUserName());
					avct.setDateVerifArr(new Date());
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

			if (avct.getIdMotifAvct() == 7) {
				// on traite l'avis CAP
				int indiceAvisCapMinMoyMaxCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_AD_SELECT(idAvct)) : -1);
				if (indiceAvisCapMinMoyMaxCap != -1) {
					Integer idAvisArr = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxCap))
							.getIdAvisCap();
					avct.setIdAvisArr(idAvisArr);
				}
				// on traite l'avis Emp
				int indiceAvisCapMinMoyMaxEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_AD_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_AD_SELECT(idAvct)) : -1);
				if (indiceAvisCapMinMoyMaxEmp != -1) {
					Integer idAvisEmp = ((AvisCap) getListeAvisCAPMinMoyMax().get(indiceAvisCapMinMoyMaxEmp))
							.getIdAvisCap();
					avct.setIdAvisEmp(idAvisEmp);
				}
			} else if (avct.getIdMotifAvct() == 6) {
				// on traite l'avis CAP
				avct.setIdAvisArr(2);
				// on traite l'avis Emp
				avct.setIdAvisEmp(2);

			} else {
				// on traite l'avis CAP
				int indiceAvisCapFavDefavCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_CLASSE_SELECT(idAvct)) : -1);
				if (indiceAvisCapFavDefavCap != -1) {
					Integer idAvisArr = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavCap))
							.getIdAvisCap();
					avct.setIdAvisArr(idAvisArr);
				}
				// on traite l'avis Emp
				int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) : -1);
				if (indiceAvisCapFavDefavEmp != -1) {
					Integer idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp))
							.getIdAvisCap();
					avct.setIdAvisEmp(idAvisEmp);
				}
			}
			avct.setObservationArr(getVAL_ST_OBSERVATION(idAvct));

			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(),
					avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
					avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(),
					avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
					avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
					avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
					avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
					avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(),
					avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(),
					avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(),
					avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
					avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
					avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
					avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
					avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
					avct.getCodePa());

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
	private Hashtable<Integer, AvisCap> getHashAvisCAP() {
		if (hashAvisCAP == null)
			hashAvisCAP = new Hashtable<Integer, AvisCap>();
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_SET_DATE_AVCT(HttpServletRequest request, AvancementFonctionnaires avct,
			Integer indiceElemen) throws Exception {
		if (getVAL_CK_VALID_ARR(indiceElemen).equals(getCHECKED_ON())) {

			if (avct.getIdMotifAvct() == 7) {
				// on récupere l'avis Emp
				if (avct.getIdAvisEmp() != null) {
					String idAvisEmp = getAvisCapDao().chercherAvisCap(avct.getIdAvisEmp()).getLibCourtAvisCap()
							.toUpperCase();
					Date dateAvctFinale = null;
					if (idAvisEmp.equals("MIN")) {
						dateAvctFinale = avct.getDateAvctMini();
					} else if (idAvisEmp.equals("MOY")) {
						dateAvctFinale = avct.getDateAvctMoy();
					} else if (idAvisEmp.equals("MAX")) {
						dateAvctFinale = avct.getDateAvctMaxi();
					}
					addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), dateAvctFinale == null ? Const.CHAINE_VIDE
							: sdfFormatDate.format(dateAvctFinale));
				}
			} else if (avct.getIdMotifAvct() == 6) {
				addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), sdfFormatDate.format(avct.getDateAvctMoy()));
			} else {
				// on récupere l'avis Emp
				if (avct.getIdAvisEmp() != null) {
					String idAvisEmp = getAvisCapDao().chercherAvisCap(avct.getIdAvisEmp()).getLibCourtAvisCap()
							.toUpperCase();
					Date dateAvctFinale = null;
					if (idAvisEmp.equals("FAV")) {
						dateAvctFinale = avct.getDateAvctMoy();
					}
					addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), dateAvctFinale == null ? Const.CHAINE_VIDE
							: sdfFormatDate.format(dateAvctFinale));
				}
			}
			// on met la date de CAP
			String dateCAP = getVAL_ST_DATE_CAP_GLOBALE();
			addZone(getNOM_ST_DATE_CAP(indiceElemen), dateCAP);
			avct.setDateCap(sdfFormatDate.parse(dateCAP));

			if (getTransaction().isErreur())
				return false;
			// on enregistre
			commitTransaction();

		} else {

			addZone(getNOM_ST_DATE_AVCT_FINALE(indiceElemen), Const.CHAINE_VIDE);
			addZone(getNOM_ST_DATE_CAP(indiceElemen), Const.CHAINE_VIDE);
			avct.setDateCap(null);
			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(),
					avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
					avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(),
					avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
					avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
					avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
					avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
					avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(),
					avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(),
					avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(),
					avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
					avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
					avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
					avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
					avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
					avct.getCodePa());

			if (getTransaction().isErreur())
				return false;
			// on enregistre
			commitTransaction();
		}

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
		try {
			AvancementFonctionnaires avct = getAvancementFonctionnairesDao().chercherAvancement(idAvct);

			if (avct.getIdMotifAvct() == 4) {
				// on traite l'avis Emp
				int indiceAvisCapFavDefavEmp = (Services.estNumerique(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) ? Integer
						.parseInt(getVAL_LB_AVIS_EMP_CLASSE_SELECT(idAvct)) : -1);
				if (indiceAvisCapFavDefavEmp != -1) {
					String idAvisEmp = ((AvisCap) getListeAvisCAPFavDefav().get(indiceAvisCapFavDefavEmp))
							.getLibCourtAvisCap();
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
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CATEGORIE Date de
	 * création : (28/11/11)
	 * 
	 */
	private String[] getLB_CATEGORIE() {
		if (LB_CATEGORIE == null)
			LB_CATEGORIE = initialiseLazyLB();
		return LB_CATEGORIE;
	}

	/**
	 * Setter de la liste: LB_CATEGORIE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_CATEGORIE(String[] newLB_CATEGORIE) {
		LB_CATEGORIE = newLB_CATEGORIE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CATEGORIE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_CATEGORIE() {
		return "NOM_LB_CATEGORIE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CATEGORIE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_CATEGORIE_SELECT() {
		return "NOM_LB_CATEGORIE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CATEGORIE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_CATEGORIE() {
		return getLB_CATEGORIE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CATEGORIE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_CATEGORIE_SELECT() {
		return getZone(getNOM_LB_CATEGORIE_SELECT());
	}

	public ArrayList<Grade> getListeCategorie() {
		return listeCategorie == null ? new ArrayList<Grade>() : listeCategorie;
	}

	public void setListeCategorie(ArrayList<Grade> listeCategorie) {
		this.listeCategorie = listeCategorie;
	}

	public ArrayList<String> getListeVerifSGC() {
		return listeVerifSGC == null ? new ArrayList<String>() : listeVerifSGC;
	}

	public void setListeVerifSGC(ArrayList<String> listeVerifSGC) {
		this.listeVerifSGC = listeVerifSGC;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_VERIF_SGC Date de
	 * création : (28/11/11)
	 * 
	 */
	private String[] getLB_VERIF_SGC() {
		if (LB_VERIF_SGC == null)
			LB_VERIF_SGC = initialiseLazyLB();
		return LB_VERIF_SGC;
	}

	/**
	 * Setter de la liste: LB_VERIF_SGC Date de création : (28/11/11)
	 * 
	 */
	private void setLB_VERIF_SGC(String[] newLB_VERIF_SGC) {
		LB_VERIF_SGC = newLB_VERIF_SGC;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_VERIF_SGC Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_VERIF_SGC() {
		return "NOM_LB_VERIF_SGC";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_VERIF_SGC_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_VERIF_SGC_SELECT() {
		return "NOM_LB_VERIF_SGC_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_VERIF_SGC Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_VERIF_SGC() {
		return getLB_VERIF_SGC();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_VERIF_SGC Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_VERIF_SGC_SELECT() {
		return getZone(getNOM_LB_VERIF_SGC_SELECT());
	}

	public ArrayList<Cap> getListeCap() {
		return listeCap == null ? new ArrayList<Cap>() : listeCap;
	}

	public void setListeCap(ArrayList<Cap> listeCap) {
		this.listeCap = listeCap;
	}

	public CapDao getCapDao() {
		return capDao;
	}

	public void setCapDao(CapDao capDao) {
		this.capDao = capDao;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CAP Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_CAP() {
		if (LB_CAP == null)
			LB_CAP = initialiseLazyLB();
		return LB_CAP;
	}

	/**
	 * Setter de la liste: LB_CAP Date de création : (28/11/11)
	 * 
	 */
	private void setLB_CAP(String[] newLB_CAP) {
		LB_CAP = newLB_CAP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CAP Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_CAP() {
		return "NOM_LB_CAP";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CAP_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_CAP_SELECT() {
		return "NOM_LB_CAP_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CAP Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_CAP() {
		return getLB_CAP();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_CAP Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_CAP_SELECT() {
		return getZone(getNOM_LB_CAP_SELECT());
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
}