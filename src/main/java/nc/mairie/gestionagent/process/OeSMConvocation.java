package nc.mairie.gestionagent.process;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.enums.EnumMotifVisiteMed;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.SPABSEN;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.suiviMedical.MotifVisiteMed;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.MairieDao;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.SPABSENDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
import nc.mairie.spring.dao.metier.suiviMedical.MotifVisiteMedDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
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
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeSMConvocation extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_MOIS;
	private String[] LB_MEDECIN;
	private String[] LB_HEURE_RDV;
	private String[] LB_RELANCE;
	private String[] LB_MOTIF;
	private String[] LB_ETAT;
	private String[] LB_STATUT;

	private String[] listeMois;
	private Hashtable<String, String> hashMois;

	private ArrayList<SuiviMedical> listeSuiviMed;
	private ArrayList<String> listeDocuments;

	private ArrayList<Medecin> listeMedecin;
	private Hashtable<Integer, Medecin> hashMedecin;

	private ArrayList<String> listeHeureRDV;

	public String ACTION_CALCUL = "Calcul";
	public String ACTION_RECHERCHE = "Recherche";
	public String ACTION_MODIFICATION = "Modification";
	public String ACTION_SUPPRESSION = "Suppression";

	private Logger logger = LoggerFactory.getLogger(OeSMConvocation.class);

	public String convocationsEnErreur = Const.CHAINE_VIDE;
	private String urlFichier;

	private SuiviMedicalDao suiviMedDao;
	private MotifVisiteMedDao motifVisiteMedDao;
	private SPABSENDao spabsenDao;
	private MedecinDao medecinDao;
	private VisiteMedicaleDao visiteMedicaleDao;

	public VisiteMedicaleDao getVisiteMedicaleDao() {
		return visiteMedicaleDao;
	}

	public void setVisiteMedicaleDao(VisiteMedicaleDao visiteMedicaleDao) {
		this.visiteMedicaleDao = visiteMedicaleDao;
	}

	public static final int STATUT_RECHERCHER_AGENT = 1;
	private ArrayList<Service> listeServices;
	private ArrayList<String> listeRelance;
	private ArrayList<String> listeStatut;
	private ArrayList<MotifVisiteMed> listeMotif;
	private ArrayList<EnumEtatSuiviMed> listeEnumEtatSuiviMed;
	public Hashtable<String, TreeHierarchy> hTree = null;

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		logger.info("Entrée initialiseZones");
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

		initialiseListeService();

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNoMatricule());
			performPB_RECHERCHER(request);
		}
		// Initialisation de la liste des documents suivi medicaux
		if (getListeDocuments() == null || getListeDocuments().size() == 0) {
			setListeDocuments(listerDocumentsSM());
			afficheListeDocuments();

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

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getSuiviMedDao() == null)
			setSuiviMedDao(new SuiviMedicalDao((SirhDao) context.getBean("sirhDao")));

		if (getMotifVisiteMedDao() == null)
			setMotifVisiteMedDao(new MotifVisiteMedDao((SirhDao) context.getBean("sirhDao")));

		if (getSpabsenDao() == null)
			setSpabsenDao(new SPABSENDao((MairieDao) context.getBean("mairieDao")));

		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getVisiteMedicaleDao() == null) {
			setVisiteMedicaleDao(new VisiteMedicaleDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void afficheListeDocuments() {
		for (int i = 0; i < getListeDocuments().size(); i++) {
			String nomDoc = getListeDocuments().get(i);
			addZone(getNOM_ST_NOM_DOC(i), nomDoc.substring(nomDoc.lastIndexOf("/") + 1, nomDoc.length()));
		}
	}

	private ArrayList<String> listerDocumentsSM() throws ParseException {
		ArrayList<String> res = new ArrayList<String>();
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT())
				: -1);
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		String docuConvocF = repPartage + "SuiviMedical/SM_Convocation_F_" + getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".doc";
		String docuConvocCC = repPartage + "SuiviMedical/SM_Convocation_CC_" + getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".doc";
		String docuAccompagnementF = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_F_"
				+ getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
		String docuAccompagnementCC = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_CC_"
				+ getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";

		// on verifie l'existance de chaque fichier
		boolean existsConvocF = new File(docuConvocF).exists();
		if (existsConvocF) {
			res.add(docuConvocF);
		}
		boolean existsConvocCC = new File(docuConvocCC).exists();
		if (existsConvocCC) {
			res.add(docuConvocCC);
		}
		boolean existsAccompF = new File(docuAccompagnementF).exists();
		if (existsAccompF) {
			res.add(docuAccompagnementF);
		}
		boolean existsAcompCC = new File(docuAccompagnementCC).exists();
		if (existsAcompCC) {
			res.add(docuAccompagnementCC);
		}
		return res;
	}

	private void afficheListeSuiviMed() throws ParseException, Exception {
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), sm.getIdAgent().toString());
			addZone(getNOM_ST_NUM_SM(i), sm.getIdSuiviMed().toString());
			addZone(getNOM_ST_MATR(i), sm.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), sm.getAgent());
			addZone(getNOM_ST_NUM_CAFAT(i), agent.getNumCafat() == null ? Const.CHAINE_VIDE : agent.getNumCafat()
					.trim());
			addZone(getNOM_ST_STATUT(i), sm.getStatut());
			Service serv = Service.chercherService(getTransaction(), sm.getIdServi());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			addZone(getNOM_ST_SERVICE(i),
					serv == null || serv.getLibService() == null ? "&nbsp;" : serv.getLibService());
			addZone(getNOM_ST_DATE_DERNIERE_VISITE(i),
					sm.getDateDerniereVisite() == null ? "&nbsp;" : Services.convertitDate(sm.getDateDerniereVisite()
							.toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			addZone(getNOM_ST_DATE_PREVISION_VISITE(i),
					sm.getDatePrevisionVisite() == null ? "&nbsp;" : Services.convertitDate(sm.getDatePrevisionVisite()
							.toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			addZone(getNOM_ST_MOTIF(i), getLibMotifVM(sm.getIdMotifVm()));
			addZone(getNOM_ST_NB_VISITES_RATEES(i), sm.getNbVisitesRatees().toString());
			addZone(getNOM_LB_MEDECIN_SELECT(i),
					sm.getIdMedecin() != null ? String.valueOf(getListeMedecin().indexOf(
							getHashMedecin().get(sm.getIdMedecin()))) : Const.ZERO);
			addZone(getNOM_ST_DATE_PROCHAIN_RDV(i),
					sm.getDateProchaineVisite() == null ? Const.CHAINE_VIDE : Services.convertitDate(sm
							.getDateProchaineVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			if (sm.getHeureProchaineVisite() != null) {
				Integer resHeure = getListeHeureRDV().indexOf(sm.getHeureProchaineVisite());
				addZone(getNOM_LB_HEURE_RDV_SELECT(i), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_RDV_SELECT(i), Const.ZERO);
			}
			addZone(getNOM_CK_A_IMPRIMER_CONVOC(i), sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getCode())
					|| sm.getEtat().equals(EnumEtatSuiviMed.ACCOMP.getCode()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_CK_A_IMPRIMER_ACCOMP(i),
					sm.getEtat().equals(EnumEtatSuiviMed.ACCOMP.getCode()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), sm.getEtat());
		}
	}

	/**
	 * Retourne le libellé associé au motif
	 * 
	 * @return String
	 */
	public String getLibMotifVM(Integer idMotif) {
		for (EnumMotifVisiteMed e : EnumMotifVisiteMed.values()) {
			if (e.getCode().toString().equals(idMotif.toString())) {
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}
			// Si clic sur le bouton PB_CALCULER
			if (testerParametre(request, getNOM_PB_CALCULER())) {
				return performPB_CALCULER(request);
			}
			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}
			// Si clic sur le bouton PB_IMPRIMER_CONVOCATIONS
			if (testerParametre(request, getNOM_PB_IMPRIMER_CONVOCATIONS())) {
				return performPB_IMPRIMER_CONVOCATIONS(request);
			}
			// Si clic sur le bouton PB_IMPRIMER_LISTE_VISITE
			if (testerParametre(request, getNOM_PB_IMPRIMER_LISTE_VISITE())) {
				return performPB_IMPRIMER_LISTE_VISITE(request);
			}
			// Si clic sur le bouton PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS
			if (testerParametre(request, getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS())) {
				return performPB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeSuiviMed().size(); i++) {
				SuiviMedical sm = getListeSuiviMed().get(i);
				if (testerParametre(request, getNOM_PB_MODIFIER(sm.getIdSuiviMed()))) {
					return performPB_MODIFIER(request, sm.getIdSuiviMed());
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeSuiviMed().size(); i++) {
				SuiviMedical sm = getListeSuiviMed().get(i);
				if (testerParametre(request, getNOM_PB_SUPPRIMER(sm.getIdSuiviMed()))) {
					return performPB_SUPPRIMER(request, sm.getIdSuiviMed());
				}
			}

			// Si clic sur le bouton PB_VISUALISER pour les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
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
	 * Initialisation des liste déroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {

		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			ArrayList<EnumEtatSuiviMed> listeEtat = EnumEtatSuiviMed.getValues();
			setListeEnumEtatSuiviMed(listeEtat);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<EnumEtatSuiviMed> list = listeEtat.listIterator(); list.hasNext();) {
				EnumEtatSuiviMed etat = (EnumEtatSuiviMed) list.next();
				String ligne[] = { etat.getValue() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);
		}

		// Si liste statut vide alors affectation
		if (getLB_STATUT() == LBVide) {
			ArrayList<String> listeStatut = new ArrayList<String>();
			listeStatut.add("F");
			listeStatut.add("C");
			listeStatut.add("CC");
			setListeStatut(listeStatut);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeStatut.listIterator(); list.hasNext();) {
				String statut = (String) list.next();
				String ligne[] = { statut };
				aFormat.ajouteLigne(ligne);
			}
			setLB_STATUT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_STATUT_SELECT(), Const.ZERO);
		}

		// Si liste motif vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			ArrayList<MotifVisiteMed> listeMotif = (ArrayList<MotifVisiteMed>) getMotifVisiteMedDao()
					.listerMotifVisiteMed();
			setListeMotif(listeMotif);
			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<MotifVisiteMed> list = listeMotif.listIterator(); list.hasNext();) {
				MotifVisiteMed motif = (MotifVisiteMed) list.next();
				String ligne[] = { motif.getLibMotifVm() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		}

		// Si liste relance vide alors affectation
		if (getLB_RELANCE() == LBVide) {
			ArrayList<String> listeRelance = new ArrayList<String>();
			listeRelance.add("oui");
			listeRelance.add("non");
			setListeRelance(listeRelance);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeRelance.listIterator(); list.hasNext();) {
				String relance = (String) list.next();
				String ligne[] = { relance };
				aFormat.ajouteLigne(ligne);
			}
			setLB_RELANCE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_RELANCE_SELECT(), Const.ZERO);
		}

		// Si liste mois vide alors affectation
		if (getLB_MOIS() == LBVide) {
			Integer moisCourant = Integer.parseInt(Services.dateDuJour().substring(3, 5)) - 1;
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
				getHashMois().put(moisAnneeFR[i] + " - " + anneeCourante, moisAnnee[i] + "/" + anneeCourante);
				j++;
			}
			// si moisCourant = juillet ou plus
			// alors il faudra afficher des mois de l'année suivante
			if (moisCourant >= 7) {
				String anneeSuivante = String.valueOf(Integer.valueOf(anneeCourante) + 1);
				int sauvJ = j;
				for (int i = 0; i < tailleTotal - sauvJ; i++) {
					getListeMois()[j] = moisAnneeFR[i] + " - " + anneeSuivante;
					getHashMois().put(moisAnneeFR[i] + " - " + anneeSuivante, moisAnnee[i] + "/" + anneeSuivante);
					j++;
				}
			}

			setLB_MOIS(getListeMois());
			addZone(getNOM_LB_MOIS_SELECT(), Const.ZERO);
		}

		// Si liste medecins vide alors affectation
		if (getListeMedecin() == null || getListeMedecin().size() == 0) {
			setListeMedecin(getMedecinDao().listerMedecin());

			int[] tailles = { 15 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Medecin> list = getListeMedecin().listIterator(); list.hasNext();) {
				Medecin m = (Medecin) list.next();
				String ligne[] = { m.getPrenomMedecin() + " " + m.getNomMedecin() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MEDECIN(aFormat.getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeMedecin().size(); i++) {
				Medecin m = (Medecin) getListeMedecin().get(i);
				getHashMedecin().put(m.getIdMedecin(), m);
			}
		}
		// si liste des heures de VM vid alors affectation
		if (getListeHeureRDV() == null || getListeHeureRDV().size() == 0) {
			setListeHeureRDV(new ArrayList<String>());
			int heureDeb = 7; // heures depart
			int minuteDeb = 0; // minutes debut
			int diffFinDeb = 9 * 60; // différence en minute entre le début et
										// la
										// fin
			int interval = 15; // interval en minute

			SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm"); // format
																			// de
																			// la
																			// date

			GregorianCalendar deb = new GregorianCalendar();
			if (heureDeb > 11) // gestion AM PM
				deb.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);
			else
				deb.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
			deb.set(GregorianCalendar.HOUR, heureDeb % 12);
			deb.set(GregorianCalendar.MINUTE, minuteDeb);

			GregorianCalendar fin = (GregorianCalendar) deb.clone();
			fin.set(GregorianCalendar.MINUTE, diffFinDeb);

			getListeHeureRDV().add(formatDate.format(deb.getTime()));
			Integer i = 1;
			while (deb.compareTo(fin) < 0) {
				deb.add(GregorianCalendar.MINUTE, interval);
				getListeHeureRDV().add(formatDate.format(deb.getTime()));
				i++;
			}
			String[] a = new String[37];
			for (int j = 0; j < getListeHeureRDV().size(); j++) {
				a[j] = getListeHeureRDV().get(j);
			}
			setLB_HEURE_RDV(a);
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
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		// Mise à jour de l'action menée
		addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);

		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT())
				: -1);
		if (indiceMois != -1) {
			// récupération de l'état
			int indiceEtat = (Services.estNumerique(getVAL_LB_ETAT_SELECT()) ? Integer
					.parseInt(getVAL_LB_ETAT_SELECT()) : -1);
			String etat = Const.CHAINE_VIDE;
			if (indiceEtat > 0) {
				etat = getListeEnumEtatSuiviMed().get(indiceEtat - 1).getCode();
			}

			// recupération motif
			int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer
					.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
			String motif = Const.CHAINE_VIDE;
			if (indiceMotif > 0) {
				motif = getListeMotif().get(indiceMotif - 1).getIdMotifVm().toString();
			}

			// recupération relance
			int indiceRelance = (Services.estNumerique(getVAL_LB_RELANCE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RELANCE_SELECT()) : -1);
			String relance = Const.CHAINE_VIDE;
			if (indiceRelance > 0) {
				relance = getListeRelance().get(indiceRelance - 1);
			}

			// recupération statut
			int indiceStatut = (Services.estNumerique(getVAL_LB_STATUT_SELECT()) ? Integer
					.parseInt(getVAL_LB_STATUT_SELECT()) : -1);
			String statut = Const.CHAINE_VIDE;
			if (indiceStatut > 0) {
				statut = getListeStatut().get(indiceStatut - 1);
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
				listeSousService = Service.listSousService(getTransaction(), serv.getSigleService());
			}

			setListeSuiviMed(getSuiviMedDao().listerSuiviMedicalAvecMoisetAnneeSansEffectue(
					getMoisSelectionne(indiceMois), getAnneeSelectionne(indiceMois), agent, listeSousService, relance,
					motif, etat, statut));
			afficheListeSuiviMed();
			// getSuiviMedDao().detruitDao();
			// pour les documents
			setListeDocuments(listerDocumentsSM());
			afficheListeDocuments();
		} else {
			setListeSuiviMed(null);
			setListeDocuments(null);
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		return true;
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CALCULER(HttpServletRequest request) throws Exception {
		// Mise à jour de l'action menée
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT())
				: -1);
		if (indiceMois != -1) {
			Integer moisChoisi = getMoisSelectionne(indiceMois);
			Integer anneeChoisi = getAnneeSelectionne(indiceMois);

			// Suppression des suivi medicaux à l'état 'Travail' en fonction du
			// mois et de l'année
			try {
				getSuiviMedDao().supprimerSuiviMedicalTravailAvecMoisetAnnee(EnumEtatSuiviMed.TRAVAIL.getCode(),
						moisChoisi, anneeChoisi);
			} catch (Exception e) {
				logger.error("Problème dans la suppression des suivi medicaux" + new Date());
			}

			// Lancement du calcul des suivi medicaux
			performCalculSuiviMedical(moisChoisi, anneeChoisi);

			// Affichage de la liste
			setListeSuiviMed(getSuiviMedDao().listerSuiviMedicalAvecMoisetAnneeSansEffectue(moisChoisi, anneeChoisi,
					null, null, Const.CHAINE_VIDE, Const.CHAINE_VIDE, Const.CHAINE_VIDE, Const.CHAINE_VIDE));
			logger.info("Affichage de la liste");
			afficheListeSuiviMed();
			// pour les documents
			setListeDocuments(listerDocumentsSM());
			afficheListeDocuments();
			// getSuiviMedDao().detruitDao();
		} else {
			setListeSuiviMed(null);
			setListeDocuments(null);
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		// "INF400","Calcul effectué"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF400"));

		return true;
	}

	private void performCalculSuiviMedical(Integer moisChoisi, Integer anneeChoisi) throws Exception {

		// CAS N°1 : A la demande de l'agent ou du service
		logger.info("Calcul cas1 : A la demande de l'agent ou du service");
		performCalculCas1(moisChoisi, anneeChoisi);

		// CAS N°2 : Visite Réguliere
		logger.info("Calcul cas2 : Visite Réguliere");
		performCalculCas2(moisChoisi, anneeChoisi);

		// CAS N°3 : AT avec ITT>15jours
		logger.info("Calcul cas3 :  AT avec ITT>15jours");
		performCalculCas3(moisChoisi, anneeChoisi);

		// CAS N°4 : Maladie > 1 mois
		logger.info("Calcul cas4 : Maladie > 1 mois");
		performCalculCas4(moisChoisi, anneeChoisi);

		// CAS N°5 : Longue maladie
		logger.info("Calcul cas5 : Longue maladie");
		performCalculCas5(moisChoisi, anneeChoisi);

		// CAS N°6 : Visite Nouvel arrivant
		logger.info("Calcul cas6 : Visite Nouvel arrivant");
		performCalculCas6(moisChoisi, anneeChoisi);

		// CAS N°7 : Changement de PA
		logger.info("Calcul cas7 : Changement de PA");
		performCalculCas7(moisChoisi, anneeChoisi);

		// CAS N°8 : CONVOCATION NON EXECUTEE
		logger.info("Calcul cas8 : CONVOCATION NON EXECUTEE");
		performCalculCas8(moisChoisi, anneeChoisi);

		// CAS N°9 : AGENT SANS VISITES MEDICALES
		logger.info("Calcul cas9 : AGENT SANS VISITES MEDICALES");
		performCalculCas9(moisChoisi, anneeChoisi);

		logger.info("FIN DES CALCULS");

	}

	private void performCalculCas9(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°9 : AGENT SANS VISITES MEDICALES
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
		ArrayList<AgentNW> listeSMCas9 = AgentNW.listerAgentSansVMPAEnCours(getTransaction(), listeNomatr);
		int nbCas9 = 0;

		for (int i = 0; i < listeSMCas9.size(); i++) {
			AgentNW agent = listeSMCas9.get(i);
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
					agent.getNoMatricule());
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
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
					carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(null);
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

			// on regarde la liste des SM pour ne pas réecrire une ligne
			// du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
						moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est
				// superieur à
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer
				// la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(),
							"yyyy-MM-dd", "dd/MM/yyyy");
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
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
					sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
			nbCas9++;
		}
		logger.info("Nb de cas 9 : " + nbCas9);
	}

	private void performCalculCas5(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°5 : Longue maladie
		// on liste toutes les absences (SPABSEN) de type MA pourle mois et
		// l'année donnée
		int nbCas5 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas5 = getSpabsenDao().listerMatriculeAbsencePourSMDoubleType("MA",
					"LM", moisChoisi, anneeChoisi);
			// pour chaque matricule trouvé on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 90
			for (int i = 0; i < listeMatriculeSMCas5.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas5.get(i);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
						nomatrAgent.toString());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				ArrayList<SPABSEN> listeSPABSENAgent = getSpabsenDao().listerAbsencePourAgentTypeEtMoisAnneeDoubleType(
						nomatrAgent, "MA", "LM", moisChoisi, anneeChoisi);
				Integer compteurJoursMA = 0;
				SPABSEN dernierAM = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeSPABSENAgent.size(); j++) {
					SPABSEN lignePrecedente = listeSPABSENAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursMA = compteurJoursMA + lignePrecedente.getNbJour();
					}
					dernierAM = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeSPABSENAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale à datFin de la precdente + 1
						SPABSEN ligneSuivante = listeSPABSENAgent.get(j + 1);
						dernierAM = ligneSuivante;
						String dateDebLigneSuiv = Services.enleveJours(
								Services.convertitDate(ligneSuivante.getDatDeb().toString(), "yyyyMMdd", "dd/MM/yyyy"),
								1);
						String dateFinLignePrec = Services.convertitDate(lignePrecedente.getDatFin().toString(),
								"yyyyMMdd", "dd/MM/yyyy");
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursMA = compteurJoursMA + ligneSuivante.getNbJour();
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
					AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), nomatrAgent.toString());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(),
							agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
							carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					String datePrev = Services.ajouteJours(
							Services.convertitDate(dernierAM.getDatFin().toString(), "yyyyMMdd", "dd/MM/yyyy"), 2);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_CONGE_LONGUE_MALADIE.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas réecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur à
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite()
									.toString(), "yyyy-MM-dd", "dd/MM/yyyy");
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
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
							sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
					nbCas5++;

				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 5.");
		}
		logger.info("Nb cas 5 : " + nbCas5);
	}

	private void performCalculCas4(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°4 : Maladie > 1 mois
		// on liste toutes les absences (SPABSEN) de type MA pourle mois et
		// l'année donnée
		int nbCas4 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas4 = getSpabsenDao().listerMatriculeAbsencePourSM("MA", moisChoisi,
					anneeChoisi);
			// pour chaque matricule trouvé on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 30
			for (int i = 0; i < listeMatriculeSMCas4.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas4.get(i);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
						nomatrAgent.toString());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				ArrayList<SPABSEN> listeSPABSENAgent = getSpabsenDao().listerAbsencePourAgentTypeEtMoisAnnee(
						nomatrAgent, "MA", moisChoisi, anneeChoisi);
				Integer compteurJoursMA = 0;
				SPABSEN dernierAM = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeSPABSENAgent.size(); j++) {
					SPABSEN lignePrecedente = listeSPABSENAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursMA = compteurJoursMA + lignePrecedente.getNbJour();
					}
					dernierAM = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeSPABSENAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale à datFin de la precdente + 1
						SPABSEN ligneSuivante = listeSPABSENAgent.get(j + 1);
						dernierAM = ligneSuivante;
						String dateDebLigneSuiv = Services.enleveJours(
								Services.convertitDate(ligneSuivante.getDatDeb().toString(), "yyyyMMdd", "dd/MM/yyyy"),
								1);
						String dateFinLignePrec = Services.convertitDate(lignePrecedente.getDatFin().toString(),
								"yyyyMMdd", "dd/MM/yyyy");
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursMA = compteurJoursMA + ligneSuivante.getNbJour();
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
					AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), nomatrAgent.toString());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(),
							agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
							carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					String datePrev = Services.ajouteJours(
							Services.convertitDate(dernierAM.getDatFin().toString(), "yyyyMMdd", "dd/MM/yyyy"), 2);
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

					// on regarde la liste des SM pour ne pas réecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur à
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite()
									.toString(), "yyyy-MM-dd", "dd/MM/yyyy");
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
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
							sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
					nbCas4++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 4");
		}
		logger.info("Nb de cas 4 : " + nbCas4);
	}

	private void performCalculCas3(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°3 : AT avec ITT>15jours
		// on liste toutes les absences (SPABSEN) de type AT pourle mois et
		// l'année donnée
		int nbCas3 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas3 = getSpabsenDao().listerMatriculeAbsencePourSM("AT", moisChoisi,
					anneeChoisi);
			// pour chaque matricule trouvé on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 15
			for (int i = 0; i < listeMatriculeSMCas3.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas3.get(i);
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
						nomatrAgent.toString());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				} else {
					if (pa == null) {
						continue;
					}
				}
				ArrayList<SPABSEN> listeSPABSENAgent = getSpabsenDao().listerAbsencePourAgentTypeEtMoisAnnee(
						nomatrAgent, "AT", moisChoisi, anneeChoisi);
				Integer compteurJoursITT = 0;
				SPABSEN dernierAT = null;
				boolean dejaComptabilise = false;
				for (int j = 0; j < listeSPABSENAgent.size(); j++) {
					SPABSEN lignePrecedente = listeSPABSENAgent.get(j);
					if (!dejaComptabilise) {
						compteurJoursITT = compteurJoursITT + lignePrecedente.getNbJour();
					}
					dernierAT = lignePrecedente;
					// on regarde si la ligne suivante existe
					if (listeSPABSENAgent.size() > j + 1) {
						// si elle existe on regarde que la date de debut de la
						// ligne suivante est egale à datFin de la precdente + 1
						SPABSEN ligneSuivante = listeSPABSENAgent.get(j + 1);
						dernierAT = ligneSuivante;
						String dateDebLigneSuiv = Services.enleveJours(
								Services.convertitDate(ligneSuivante.getDatDeb().toString(), "yyyyMMdd", "dd/MM/yyyy"),
								1);
						String dateFinLignePrec = Services.convertitDate(lignePrecedente.getDatFin().toString(),
								"yyyyMMdd", "dd/MM/yyyy");
						if (dateFinLignePrec.equals(dateDebLigneSuiv)) {
							compteurJoursITT = compteurJoursITT + ligneSuivante.getNbJour();
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
					AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), nomatrAgent.toString());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(),
							agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
							carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					String datePrev = Services.ajouteJours(
							Services.convertitDate(dernierAT.getDatFin().toString(), "yyyyMMdd", "dd/MM/yyyy"), 1);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_AT_ITT_15JOURS.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas réecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur à
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite()
									.toString(), "yyyy-MM-dd", "dd/MM/yyyy");
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
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
							sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
					nbCas3++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 3");
		}
		logger.info("Nb de cas 3 : " + nbCas3);
	}

	private void performCalculCas1(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°1 : A la demande de l'agent ou du service
		// on liste toutes les visites medicales du type "a la demande..."
		Medecin m = getMedecinDao().chercherMedecinARenseigner("A", "RENSEIGNER");
		ArrayList<VisiteMedicale> listeSMCas1 = getVisiteMedicaleDao().listerVisiteMedicalePourSMCas1(
				EnumMotifVisiteMed.VM_DEMANDE_AGENT.getCode(), EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getCode(),
				m.getIdMedecin());
		int nbCas1 = 0;
		for (int i = 0; i < listeSMCas1.size(); i++) {
			VisiteMedicale vm = listeSMCas1.get(i);
			// on crée la nouvelle ligne
			SuiviMedical sm = new SuiviMedical();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), vm.getIdAgent().toString());
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
					agent.getNoMatricule());
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

			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
					carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(null);
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

			// on regarde la liste des SM pour ne pas réecrire une ligne
			// du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
						moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est
				// superieur à
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer
				// la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(),
							"yyyy-MM-dd", "dd/MM/yyyy");
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
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
					sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
			nbCas1++;
		}
		logger.info("Nb de cas 1 : " + nbCas1);
	}

	private void performCalculCas8(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°8 : CONVOCATION NON EXECUTEE
		// on liste tous les suivi médicaux de type "CONVOQUE" du mois précédent
		int nbCas8 = 0;
		try {
			ArrayList<SuiviMedical> listeSMCas8 = getSuiviMedDao().listerSuiviMedicalNonEffectue(moisChoisi,
					anneeChoisi, EnumEtatSuiviMed.CONVOQUE.getCode());
			for (int i = 0; i < listeSMCas8.size(); i++) {
				// on crée une nouvelle ligne avec les memes informations
				// sauf pour le statut et le service on le remet à jour
				SuiviMedical smAncien = listeSMCas8.get(i);
				// on crée la nouvelle ligne
				SuiviMedical sm = new SuiviMedical();
				AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), smAncien.getNomatr().toString());
				// on regarde que la PA est active
				PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
						agent.getNoMatricule());
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
				Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				FichePoste fp = null;
				if (aff != null && aff.getIdFichePoste() != null) {
					fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
				}
				sm.setIdAgent(smAncien.getIdAgent());
				sm.setNomatr(smAncien.getNomatr());
				sm.setAgent(smAncien.getAgent());
				sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
						carr.getCodeCategorie()) : null);
				sm.setIdServi(fp != null ? fp.getIdServi() : null);
				sm.setDateDerniereVisite(smAncien.getDateDerniereVisite());
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
				// couleur différente à l'affichage
				sm.setRelance(1);

				// on regarde la liste des SM pour ne pas réecrire une ligne
				// du meme
				// agent
				try {
					SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(smAncien.getIdAgent(),
							moisChoisi, anneeChoisi);
					logger.debug("SM : " + smTemp.toString());
					// si une ligne existe deja
					// on regarde si etat Travail
					// si oui, on regarde si la date de prevision est
					// superieur à
					// celle existante
					// si oui alors on ne crée pas de nouvelle ligne
					// si non , on supprime la ligne existante pour recréer
					// la
					// nouvelle
					if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
						String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(),
								"yyyy-MM-dd", "dd/MM/yyyy");
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
				getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
						sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
						sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
						sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
				nbCas8++;
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 8");
		}
		logger.info("Nb de cas 8 : " + nbCas8);
	}

	private void performCalculCas7(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°7 : Changement de PA
		// on liste toutes les PA hors-effectif
		// on vérifie qu'il y a bien une PA normale apres cette PA hors-effectif
		ArrayList<PositionAdmAgent> listePACas7 = PositionAdmAgent.listerPositionAdmAgentHorsEffectif(getTransaction(),
				moisChoisi, anneeChoisi);
		int nbCas7 = 0;
		for (int i = 0; i < listePACas7.size(); i++) {
			// on regarde pour cette liste de PA si il en existe une qui suit en
			// NORMALE 01
			PositionAdmAgent paHorsEffectif = listePACas7.get(i);
			PositionAdmAgent paSuivante = PositionAdmAgent.chercherPositionAdmAgent(getTransaction(),
					paHorsEffectif.getNomatr(), paHorsEffectif.getDatfin());
			if (paSuivante != null && paSuivante.getCdpadm() != null) {
				if (paSuivante.getCdpadm().equals("01")) {
					// si c'est bon alors on crée le suiviMedical
					SuiviMedical sm = new SuiviMedical();
					AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), paSuivante.getNomatr());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						if (Carriere.isCarriereConseilMunicipal(carr.getCodeCategorie())) {
							continue;
						}
					}
					Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(),
							agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
					sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
							carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					Date d2 = new SimpleDateFormat("dd/MM/yyyy")
							.parse(Services.enleveJours(paSuivante.getDatdeb(), 15));
					sm.setDatePrevisionVisite(d2);
					sm.setIdMotifVm(EnumMotifVisiteMed.VM_CHANGEMENT_PA.getCode());
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas réecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentNomatrMoisetAnnee(
								Integer.valueOf(paHorsEffectif.getNomatr()), moisChoisi, anneeChoisi);
						logger.debug("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur à
						// celle existante
						// si oui alors on ne crée pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recréer
						// la
						// nouvelle
						if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
							String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite()
									.toString(), "yyyy-MM-dd", "dd/MM/yyyy");
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
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
							sm.getIdMotifVm(), sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
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
		// CAS N°6 : Visite Nouvel arrivant
		// on liste tous les nouveaux arrivant
		ArrayList<AgentNW> listeAgentCas6 = AgentNW.listerAgentNouveauxArrivant(getTransaction(), moisChoisi,
				anneeChoisi);
		int nbCas6 = 0;
		for (int i = 0; i < listeAgentCas6.size(); i++) {
			SuiviMedical sm = new SuiviMedical();
			AgentNW agent = listeAgentCas6.get(i);
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
					agent.getNoMatricule());
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
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
					carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(null);
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(agent.getDateDerniereEmbauche());
			sm.setDatePrevisionVisite(d2);
			sm.setIdMotifVm(EnumMotifVisiteMed.VM_NOUVEAU.getCode());
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas réecrire une ligne du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(
						Integer.valueOf(agent.getIdAgent()), moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur à
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(),
							"yyyy-MM-dd", "dd/MM/yyyy");
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
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
					sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
			nbCas6++;
		}
		logger.info("Nb de cas 6 : " + nbCas6);
	}

	private void performCalculCas2(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		// CAS N°2 : Visite Réguliere
		// on liste toutes les visites medicales
		// dont la dateVM + durée validitéVM = mois et année choisie du calcul
		ArrayList<VisiteMedicale> listeVMCas2 = getVisiteMedicaleDao().listerVisiteMedicalePourSMCas2(moisChoisi,
				anneeChoisi);
		int nbCas2 = 0;
		for (int i = 0; i < listeVMCas2.size(); i++) {
			VisiteMedicale vm = listeVMCas2.get(i);

			SuiviMedical sm = new SuiviMedical();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), vm.getIdAgent().toString());
			// on regarde que la PA est active
			PositionAdmAgent pa = PositionAdmAgent.chercherPositionAdmAgentActive(getTransaction(),
					agent.getNoMatricule());
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
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), vm.getIdAgent()
					.toString());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(vm.getIdAgent());
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao().getStatutSM(
					carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(vm.getDateDerniereVisite());
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(Services.ajouteMois(
					new SimpleDateFormat("dd/MM/yyyy").format(vm.getDateDerniereVisite()), vm.getDureeValidite()));
			sm.setDatePrevisionVisite(d2);
			sm.setIdMotifVm(EnumMotifVisiteMed.VM_REGULIERE.getCode());
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas réecrire une ligne du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao().chercherSuiviMedicalAgentMoisetAnnee(vm.getIdAgent(),
						moisChoisi, anneeChoisi);
				logger.debug("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur à
				// celle existante
				// si oui alors on ne crée pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recréer la
				// nouvelle
				if (smTemp.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
					String dateExistePrevision = Services.convertitDate(smTemp.getDatePrevisionVisite().toString(),
							"yyyy-MM-dd", "dd/MM/yyyy");
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
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(), sm.getAgent(), sm.getStatut(),
					sm.getIdServi(), sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(), sm.getIdMotifVm(),
					sm.getNbVisitesRatees(), sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(), sm.getAnnee(), sm.getRelance());
			nbCas2++;
		}
		logger.info("Nb de cas 2 : " + nbCas2);
	}

	private Integer getMoisSelectionne(int indiceMois) throws ParseException {
		if (getListeMois() != null && getListeMois().length > 0 && indiceMois != -1) {
			String test = getListeMois()[indiceMois];
			String test2 = getHashMois().get(test);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy", Locale.FRENCH);
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM", Locale.FRENCH);
			Date d = sdf.parse(test2);
			String mois = sdf2.format(d);
			return Integer.valueOf(mois);
		} else {
			return 0;
		}
	}

	private Integer getAnneeSelectionne(int indiceMois) throws ParseException {
		if (getListeMois() != null && getListeMois().length > 0 && indiceMois != -1) {
			String test = getListeMois()[indiceMois];
			String test2 = getHashMois().get(test);
			SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy", Locale.FRENCH);
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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

	public ArrayList<SuiviMedical> getListeSuiviMed() {
		if (listeSuiviMed == null)
			return new ArrayList<SuiviMedical>();
		return listeSuiviMed;
	}

	public void setListeSuiviMed(ArrayList<SuiviMedical> listeSuiviMed) {
		this.listeSuiviMed = listeSuiviMed;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_SM Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_SM(int i) {
		return "NOM_ST_NUM_SM_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_SM Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_SM(int i) {
		return getZone(getNOM_ST_NUM_SM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MATR Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MATR Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_CAFAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_CAFAT(int i) {
		return "NOM_ST_NUM_CAFAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_CAFAT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_CAFAT(int i) {
		return getZone(getNOM_ST_NUM_CAFAT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DERNIERE_VISITE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DERNIERE_VISITE(int i) {
		return "NOM_ST_DATE_DERNIERE_VISITE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DATE_DERNIERE_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DERNIERE_VISITE(int i) {
		return getZone(getNOM_ST_DATE_DERNIERE_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_DATE_PREVISION_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_PREVISION_VISITE(int i) {
		return "NOM_ST_DATE_PREVISION_VISITE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DATE_PREVISION_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_PREVISION_VISITE(int i) {
		return getZone(getNOM_ST_DATE_PREVISION_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_VISITES_RATEES
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NB_VISITES_RATEES(int i) {
		return "NOM_ST_NB_VISITES_RATEES_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NB_VISITES_RATEES Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NB_VISITES_RATEES(int i) {
		return getZone(getNOM_ST_NB_VISITES_RATEES(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_PROCHAIN_RDV
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_PROCHAIN_RDV(int i) {
		return "NOM_ST_DATE_PROCHAIN_RDV_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DATE_PROCHAIN_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_PROCHAIN_RDV(int i) {
		return getZone(getNOM_ST_DATE_PROCHAIN_RDV(i));
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
	 * Getter de la liste avec un lazy initialize : LB_MEDECIN Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_MEDECIN(int i) {
		if (LB_MEDECIN == null)
			LB_MEDECIN = initialiseLazyLB();
		return LB_MEDECIN;
	}

	/**
	 * Setter de la liste: LB_MEDECIN Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_MEDECIN(String[] newLB_MEDECIN) {
		LB_MEDECIN = newLB_MEDECIN;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MEDECIN Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_MEDECIN(int i) {
		return "NOM_LB_MEDECIN_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MEDECIN_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_MEDECIN_SELECT(int i) {
		return "NOM_LB_MEDECIN_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MEDECIN Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MEDECIN(int i) {
		return getLB_MEDECIN(i);
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MEDECIN Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_MEDECIN_SELECT(int i) {
		return getZone(getNOM_LB_MEDECIN_SELECT(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_A_IMPRIMER_CONVOC Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_A_IMPRIMER_CONVOC(int i) {
		return "NOM_CK_A_IMPRIMER_CONVOC_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_A_IMPRIMER_CONVOC Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_A_IMPRIMER_CONVOC(int i) {
		return getZone(getNOM_CK_A_IMPRIMER_CONVOC(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_A_IMPRIMER_ACCOMP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_A_IMPRIMER_ACCOMP(int i) {
		return "NOM_CK_A_IMPRIMER_ACCOMP_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_A_IMPRIMER_ACCOMP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_A_IMPRIMER_ACCOMP(int i) {
		return getZone(getNOM_CK_A_IMPRIMER_ACCOMP(i));
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
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int idSm) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		setStatut(STATUT_MEME_PROCESS);

		// on change l'etat juste pour l'affichage
		addZone(getNOM_ST_ETAT(idSm), EnumEtatSuiviMed.PLANIFIE.getCode());
		addZone(getVAL_CK_A_IMPRIMER_CONVOC(idSm), Const.CHAINE_VIDE);
		addZone(getVAL_CK_A_IMPRIMER_ACCOMP(idSm), Const.CHAINE_VIDE);

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
	 * RG_AG_CA_A08
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int idSm) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		setStatut(STATUT_MEME_PROCESS);

		// on change l'etat juste pour l'affichage
		addZone(getNOM_ST_ETAT(idSm), EnumEtatSuiviMed.TRAVAIL.getCode());
		addZone(getNOM_ST_DATE_PROCHAIN_RDV(idSm), Const.CHAINE_VIDE);
		addZone(getNOM_LB_HEURE_RDV_SELECT(idSm), Const.ZERO);
		addZone(getNOM_LB_MEDECIN_SELECT(idSm), Const.ZERO);
		addZone(getVAL_CK_A_IMPRIMER_CONVOC(idSm), Const.CHAINE_VIDE);
		addZone(getVAL_CK_A_IMPRIMER_ACCOMP(idSm), Const.CHAINE_VIDE);

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
		if (!performControlerSaisie()) {
			return false;
		}
		// on sauvegarder l'état du tableau
		sauvegardeTableau();
		// on remet la liste à vide afin qu'elle soit initialisée avec les
		// nouvelles valeurs
		setListeSuiviMed(null);
		setListeDocuments(null);
		performPB_RECHERCHER(request);
		return true;
	}

	private boolean performControlerSaisie() throws ParseException {
		// on controle les champs
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			SuiviMedical sm = getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			// si la ligne n'est pas en etat travail
			if (!getVAL_ST_ETAT(i).equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
				String dateRDV = getVAL_ST_DATE_PROCHAIN_RDV(i);
				String agentConcerne = getVAL_ST_MATR(i) + " ( " + getVAL_ST_AGENT(i) + " ) ";
				// si la date du prochain RDV est vide
				if (dateRDV == null || dateRDV.trim().equals(Const.CHAINE_VIDE)) {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR002", "date du prochain RDV pour la ligne " + i));
					return false;
				}
				// si la date du prochain RDV est inférieur au moins selectionné
				int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
						.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
				Integer moisChoisi = getMoisSelectionne(indiceMois);
				Integer moisRDV = Integer.valueOf(dateRDV.substring(3, 5));
				if (moisRDV < moisChoisi) {
					// "ERR300",
					// "La date du prochain RDV pour l'agent @ doit être supérieure ou égale au mois selectionné.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR300", agentConcerne));
					return false;
				}
				// Contrôle format date du prochain RDV
				if (!Services.estUneDate(dateRDV)) {
					// "ERR301",
					// "La date du prochain RDV est incorrecte pour l'agent @. Elle doit être au format date."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR301", agentConcerne));
					return false;
				} else {
					addZone(getNOM_ST_DATE_PROCHAIN_RDV(i), Services.formateDate(dateRDV));
				}

				// si la date du prochain RDV est inférieur à la date du jour
				if (!getVAL_ST_ETAT(i).equals(EnumEtatSuiviMed.CONVOQUE.getCode())
						&& !getVAL_ST_ETAT(i).equals(EnumEtatSuiviMed.ACCOMP.getCode())
						&& Services.compareDates(dateRDV, Services.dateDuJour()) < 0) {
					// "ERR302",
					// "La date du prochain RDV pour l'agent @ doit être supérieure ou égale à la date du jour"
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR302", agentConcerne));
					return false;
				}

			}
		}
		return true;

	}

	private ArrayList<Medecin> getListeMedecin() {
		return listeMedecin;
	}

	private void setListeMedecin(ArrayList<Medecin> listeMedecin) {
		this.listeMedecin = listeMedecin;
	}

	private Hashtable<Integer, Medecin> getHashMedecin() {
		if (hashMedecin == null)
			hashMedecin = new Hashtable<Integer, Medecin>();
		return hashMedecin;
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

	public ArrayList<String> getListeHeureRDV() {
		return listeHeureRDV;
	}

	private void setListeHeureRDV(ArrayList<String> listeHeureRDV) {
		this.listeHeureRDV = listeHeureRDV;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_RDV Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_RDV(int i) {
		if (LB_HEURE_RDV == null)
			LB_HEURE_RDV = initialiseLazyLB();
		return LB_HEURE_RDV;
	}

	/**
	 * Setter de la liste: LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_HEURE_RDV(String[] newLB_HEURE_RDV) {
		LB_HEURE_RDV = newLB_HEURE_RDV;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_RDV Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_RDV(int i) {
		return "NOM_LB_HEURE_RDV_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_HEURE_RDV_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_RDV_SELECT(int i) {
		return "NOM_LB_HEURE_RDV_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_RDV(int i) {
		return getLB_HEURE_RDV(i);
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_RDV_SELECT(int i) {
		return getZone(getNOM_LB_HEURE_RDV_SELECT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER_CONVOCATIONS Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER_LISTE_VISITE() {
		return "NOM_PB_IMPRIMER_LISTE_VISITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER_LISTE_VISITE(HttpServletRequest request) throws Exception {
		performPB_RECHERCHER(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER_CONVOCATIONS Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS() {
		return "NOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS(HttpServletRequest request) throws Exception {
		convocationsEnErreur = Const.CHAINE_VIDE;
		if (!performControlerSaisie()) {
			return false;
		}
		sauvegardeTableau();

		// on supprime les documents existants
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT())
				: -1);
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		String docuAccompagnementF = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_F_"
				+ getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
		String docuAccompagnementCC = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_CC_"
				+ getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
		// on verifie que les repertoires existent
		verifieRepertoire("SuiviMedical");
		// on verifie l'existance de chaque fichier
		File accompF = new File(docuAccompagnementF.substring(8, docuAccompagnementF.length()));
		if (accompF.exists()) {
			accompF.delete();
		}
		File accompCC = new File(docuAccompagnementCC.substring(8, docuAccompagnementCC.length()));
		if (accompCC.exists()) {
			accompCC.delete();
		}

		int nbConvocImpr = 0;
		// on recupere les lignes qui sont cochées pour imprimer
		ArrayList<Integer> smFonctionnaireAImprimer = new ArrayList<Integer>();
		ArrayList<Integer> smCCAImprimer = new ArrayList<Integer>();
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			// on recupère la ligne concernée
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			// si l'etat de la ligne est 'convoque'
			if (sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getCode())) {
				if (getVAL_CK_A_IMPRIMER_ACCOMP(i).equals(getCHECKED_ON())) {
					// RG-SVM-12.4
					if (sm.getStatut() != null && !sm.getStatut().equals(Const.CHAINE_VIDE)) {
						if (sm.getStatut().equals("F")) {
							// alors on edite EDIT_SVM-4
							smFonctionnaireAImprimer.add(sm.getIdSuiviMed());
						} else if (sm.getStatut().equals("CC") || sm.getStatut().equals("C")) {
							// alors on edite EDIT_SVM-5
							smCCAImprimer.add(sm.getIdSuiviMed());
						}
						sm.setEtat(EnumEtatSuiviMed.ACCOMP.getCode());
						getSuiviMedDao().modifierSuiviMedicalTravail(sm.getIdSuiviMed(), sm);
						nbConvocImpr++;
					} else {
						// cas vide on ne traite pas
						// mais on informe l'utilisateur que certaines lignes
						// n'ont pu être imprimées
						// on met l'agent dans une variable et on affiche cette
						// liste à l'ecran
						convocationsEnErreur += sm.getAgent() + " (" + sm.getNomatr() + "); ";
					}
				}
			}
		}

		// si aucune convocation selectionnée
		// RG_SVM-12.1
		if (nbConvocImpr == 0) {
			// "INF401",
			// "Attention, aucune convocation n'est sélectionnée pour cette impression."
			getTransaction().declarerErreur(MessageUtils.getMessage("INF401"));
			return false;
		} else {
			// on remet la liste à vide afin qu'elle soit initialisée avec les
			// nouvelles valeurs
			setListeSuiviMed(null);
			setListeDocuments(null);
		}

		// on imprime les 2 listes
		if (smFonctionnaireAImprimer.size() > 0) {
			String destination = "SuiviMedical/SM_Lettre_Accompagnement_F_" + getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".doc";

			byte[] fileAsBytes = getAccompagnementAsByteArray(smFonctionnaireAImprimer, "F",
					getMoisSelectionne(indiceMois), getAnneeSelectionne(indiceMois));

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

		}
		if (smCCAImprimer.size() > 0) {
			String destination = "SuiviMedical/SM_Lettre_Accompagnement_CC_" + getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".doc";

			byte[] fileAsBytes = getAccompagnementAsByteArray(smCCAImprimer, "CC", getMoisSelectionne(indiceMois),
					getAnneeSelectionne(indiceMois));

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}
		}
		performPB_RECHERCHER(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER_CONVOCATIONS Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER_CONVOCATIONS() {
		return "NOM_PB_IMPRIMER_CONVOCATIONS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER_CONVOCATIONS(HttpServletRequest request) throws Exception {
		convocationsEnErreur = Const.CHAINE_VIDE;
		if (!performControlerSaisie()) {
			return false;
		}
		sauvegardeTableau();

		// on supprime les documents existants
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT())
				: -1);
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		String docuConvocF = repPartage + "SuiviMedical/SM_Convocation_F_" + getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".doc";
		String docuConvocCC = repPartage + "SuiviMedical/SM_Convocation_CC_" + getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".doc";
		// on verifie que les repertoires existent
		verifieRepertoire("SuiviMedical");
		// on verifie l'existance de chaque fichier
		File convocF = new File(docuConvocF.substring(8, docuConvocF.length()));
		if (convocF.exists()) {
			convocF.delete();
		}
		File convocCC = new File(docuConvocCC.substring(8, docuConvocCC.length()));
		if (convocCC.exists()) {
			convocCC.delete();
		}

		int nbConvocImpr = 0;
		// on recupere les lignes qui sont cochées pour imprimer
		ArrayList<Integer> smFonctionnaireAImprimer = new ArrayList<Integer>();
		ArrayList<Integer> smCCAImprimer = new ArrayList<Integer>();
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			// on recupère la ligne concernée
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			// si l'etat de la ligne n'est pas deja 'imprimé' et que la colonne
			// imprimée est cochée
			if (!sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getCode())) {
				if (getVAL_CK_A_IMPRIMER_CONVOC(i).equals(getCHECKED_ON())) {
					// RG-SVM-10.3
					if (sm.getStatut() != null && !sm.getStatut().equals(Const.CHAINE_VIDE)) {
						if (sm.getStatut().equals("F")) {
							// alors on edite EDIT_SVM-1
							smFonctionnaireAImprimer.add(sm.getIdSuiviMed());
						} else if (sm.getStatut().equals("CC") || sm.getStatut().equals("C")) {
							// alors on edite EDIT_SVM-2
							smCCAImprimer.add(sm.getIdSuiviMed());
						}
						sm.setEtat(EnumEtatSuiviMed.CONVOQUE.getCode());
						getSuiviMedDao().modifierSuiviMedicalTravail(sm.getIdSuiviMed(), sm);
						nbConvocImpr++;
					} else {
						// cas vide on ne traite pas
						// mais on informe l'utilisateur que certaines lignes
						// n'ont pu être imprimées
						// on met l'agent dans une variable et on affiche cette
						// liste à l'ecran
						convocationsEnErreur += sm.getAgent() + " (" + sm.getNomatr() + "); ";
					}
				}
			}
		}

		// si aucune convocation selectionnée
		// RG_SVM-10.1
		if (nbConvocImpr == 0) {
			// "INF401",
			// "Attention, aucune convocation n'est sélectionnée cet impression.");
			getTransaction().declarerErreur(MessageUtils.getMessage("INF401"));
			return false;
		} else {
			// on remet la liste à vide afin qu'elle soit initialisée avec les
			// nouvelles valeurs
			setListeSuiviMed(null);
			setListeDocuments(null);
		}

		// on imprime les 2 listes
		if (smFonctionnaireAImprimer.size() > 0) {
			String destination = "SuiviMedical/SM_Convocation_F_" + getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".doc";

			byte[] fileAsBytes = getConvocationAsByteArray(smFonctionnaireAImprimer, "F",
					getMoisSelectionne(indiceMois), getAnneeSelectionne(indiceMois));

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}
		}
		if (smCCAImprimer.size() > 0) {
			String destination = "SuiviMedical/SM_Convocation_CC_" + getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".doc";

			byte[] fileAsBytes = getConvocationAsByteArray(smCCAImprimer, "CC", getMoisSelectionne(indiceMois),
					getAnneeSelectionne(indiceMois));

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}
		}
		performPB_RECHERCHER(request);
		return true;
	}

	public boolean saveFileToRemoteFileSystem(byte[] fileAsBytes, String chemin, String filename) throws Exception {

		BufferedOutputStream bos = null;
		FileObject docFile = null;

		try {
			FileSystemManager fsManager = VFS.getManager();
			docFile = fsManager.resolveFile(String.format("%s", chemin + filename));
			bos = new BufferedOutputStream(docFile.getContent().getOutputStream());
			IOUtils.write(fileAsBytes, bos);
			IOUtils.closeQuietly(bos);

			if (docFile != null) {
				try {
					docFile.close();
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

	public ClientResponse createAndFireRequestConvocation(String csvIdSuiviMedical, String typePopulation, String mois,
			String annee) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL_CONVOCATION_VM_SIRH")
				+ "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation + "&mois=" + mois
				+ "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWS);

		ClientResponse response = webResource.get(ClientResponse.class);

		return response;
	}

	public ClientResponse createAndFireRequestAccompagnement(String csvIdSuiviMedical, String typePopulation,
			String mois, String annee) {
		String urlWS = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL_ACCOMPAGNEMENT_VM_SIRH")
				+ "?csvIdSuiviMedical=" + csvIdSuiviMedical + "&typePopulation=" + typePopulation + "&mois=" + mois
				+ "&annee=" + annee;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWS);

		ClientResponse response = webResource.get(ClientResponse.class);

		return response;
	}

	private byte[] getConvocationAsByteArray(ArrayList<Integer> smFonctionnaireAImprimer, String typePopulation,
			Integer moisSelectionne, Integer anneeSelectionne) throws Exception {

		ClientResponse response = createAndFireRequestConvocation(smFonctionnaireAImprimer.toString().replace("[", "")
				.replace("]", "").replace(" ", ""), typePopulation, moisSelectionne.toString(),
				anneeSelectionne.toString());

		return readResponseAsByteArray(response);
	}

	private byte[] getAccompagnementAsByteArray(ArrayList<Integer> smFonctionnaireAImprimer, String typePopulation,
			Integer moisSelectionne, Integer anneeSelectionne) throws Exception {

		ClientResponse response = createAndFireRequestAccompagnement(
				smFonctionnaireAImprimer.toString().replace("[", "").replace("]", "").replace(" ", ""), typePopulation,
				moisSelectionne.toString(), anneeSelectionne.toString());

		return readResponseAsByteArray(response);
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

	private void sauvegardeTableau() throws Exception {
		// on sauvegarde l'état du tableau
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			// on recupère la ligne concernée
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			sm.setEtat(getVAL_ST_ETAT(i));
			if (getVAL_ST_ETAT(i).equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
				sm.setHeureProchaineVisite(null);
				sm.setDateProchaineVisite(null);
				sm.setIdMedecin(null);
			} else {
				sm.setHeureProchaineVisite(getListeHeureRDV().get(Integer.valueOf(getVAL_LB_HEURE_RDV_SELECT(i))));
				String dateProchainRDV = getVAL_ST_DATE_PROCHAIN_RDV(i);
				if (!dateProchainRDV.equals(Const.CHAINE_VIDE)) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date d = formatter.parse(dateProchainRDV);
					sm.setDateProchaineVisite(d);
				} else {
					sm.setDateProchaineVisite(null);
				}
				Medecin m = getListeMedecin().get(Integer.valueOf(getVAL_LB_MEDECIN_SELECT(i)));
				sm.setIdMedecin(m.getIdMedecin());
			}
			getSuiviMedDao().modifierSuiviMedicalTravail(sm.getIdSuiviMed(), sm);

		}
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

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		String docSelection = getListeDocuments().get(indiceEltAConsulter);
		String nomDoc = docSelection.substring(docSelection.lastIndexOf("/"), docSelection.length());

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		setURLFichier(getScriptOuverture(repertoireStockage + "SuiviMedical" + nomDoc));

		setStatut(STATUT_MEME_PROCESS);
		return true;
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

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
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
	 * Getter de la liste avec un lazy initialize : LB_RELANCE Date de création
	 * : (28/11/11)
	 * 
	 */
	private String[] getLB_RELANCE() {
		if (LB_RELANCE == null)
			LB_RELANCE = initialiseLazyLB();
		return LB_RELANCE;
	}

	/**
	 * Setter de la liste: LB_RELANCE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_RELANCE(String[] newLB_RELANCE) {
		LB_RELANCE = newLB_RELANCE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RELANCE Date de création
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_LB_RELANCE() {
		return "NOM_LB_RELANCE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RELANCE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_RELANCE_SELECT() {
		return "NOM_LB_RELANCE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RELANCE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_RELANCE() {
		return getLB_RELANCE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RELANCE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_RELANCE_SELECT() {
		return getZone(getNOM_LB_RELANCE_SELECT());
	}

	public ArrayList<String> getListeRelance() {
		return listeRelance == null ? new ArrayList<String>() : listeRelance;
	}

	public void setListeRelance(ArrayList<String> listeRelance) {
		this.listeRelance = listeRelance;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de création : (28/11/11)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	public ArrayList<MotifVisiteMed> getListeMotif() {
		return listeMotif;
	}

	public void setListeMotif(ArrayList<MotifVisiteMed> listeMotif) {
		this.listeMotif = listeMotif;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ETAT() {
		if (LB_ETAT == null)
			LB_ETAT = initialiseLazyLB();
		return LB_ETAT;
	}

	/**
	 * Setter de la liste: LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ETAT(String[] newLB_ETAT) {
		LB_ETAT = newLB_ETAT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_ETAT() {
		return "NOM_LB_ETAT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ETAT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ETAT_SELECT() {
		return "NOM_LB_ETAT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ETAT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	public ArrayList<EnumEtatSuiviMed> getListeEnumEtatSuiviMed() {
		return listeEnumEtatSuiviMed == null ? new ArrayList<EnumEtatSuiviMed>() : listeEnumEtatSuiviMed;
	}

	public void setListeEnumEtatSuiviMed(ArrayList<EnumEtatSuiviMed> listeEnumEtatSuiviMed) {
		this.listeEnumEtatSuiviMed = listeEnumEtatSuiviMed;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_STATUT() {
		if (LB_STATUT == null)
			LB_STATUT = initialiseLazyLB();
		return LB_STATUT;
	}

	/**
	 * Setter de la liste: LB_STATUT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_STATUT(String[] newLB_STATUT) {
		LB_STATUT = newLB_STATUT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUT Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_STATUT() {
		return "NOM_LB_STATUT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_STATUT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_STATUT_SELECT() {
		return "NOM_LB_STATUT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_STATUT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_STATUT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_STATUT_SELECT() {
		return getZone(getNOM_LB_STATUT_SELECT());
	}

	public ArrayList<String> getListeStatut() {
		return listeStatut == null ? new ArrayList<String>() : listeStatut;
	}

	public void setListeStatut(ArrayList<String> listeStatut) {
		this.listeStatut = listeStatut;
	}

	public SuiviMedicalDao getSuiviMedDao() {
		return suiviMedDao;
	}

	public void setSuiviMedDao(SuiviMedicalDao suiviMedDao) {
		this.suiviMedDao = suiviMedDao;
	}

	public MotifVisiteMedDao getMotifVisiteMedDao() {
		return motifVisiteMedDao;
	}

	public void setMotifVisiteMedDao(MotifVisiteMedDao motifVisiteMedDao) {
		this.motifVisiteMedDao = motifVisiteMedDao;
	}

	public SPABSENDao getSpabsenDao() {
		return spabsenDao;
	}

	public void setSpabsenDao(SPABSENDao spabsenDao) {
		this.spabsenDao = spabsenDao;
	}

	@Override
	public String getJSP() {
		return "OeSMConvocation.jsp";
	}

	public MedecinDao getMedecinDao() {
		return medecinDao;
	}

	public void setMedecinDao(MedecinDao medecinDao) {
		this.medecinDao = medecinDao;
	}

}
