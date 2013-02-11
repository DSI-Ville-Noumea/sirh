package nc.mairie.gestionagent.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.enums.EnumMotifVisiteMed;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.hsct.SPABSENDao;
import nc.mairie.spring.dao.metier.suiviMedical.MotifVisiteMedDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.domain.metier.hsct.SPABSEN;
import nc.mairie.spring.domain.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTAccidentTravail Date de cr�ation : (30/06/11 13:56:32)
 * 
 */
public class OeSMConvocation extends nc.mairie.technique.BasicProcess {

	private String[] LB_MOIS;
	private String[] LB_MEDECIN;
	private String[] LB_HEURE_RDV;

	private String[] listeMois;
	private Hashtable<String, String> hashMois;

	private ArrayList<SuiviMedical> listeSuiviMed;
	private ArrayList<String> listeDocuments;

	private ArrayList<Medecin> listeMedecin;
	private Hashtable<String, Medecin> hashMedecin;

	private ArrayList<String> listeHeureRDV;

	public String ACTION_CALCUL = "Calcul";
	public String ACTION_RECHERCHE = "Recherche";
	public String ACTION_MODIFICATION = "Modification";
	public String ACTION_SUPPRESSION = "Suppression";

	private static Logger logger = Logger.getLogger(OeSMConvocation.class
			.getName());

	public String convocationsEnErreur = Const.CHAINE_VIDE;
	private String urlFichier;

	private SuiviMedicalDao suiviMedDao;

	private MotifVisiteMedDao motifVisiteMedDao;

	private SPABSENDao spabsenDao;

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

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		logger.info("Entr�e initialiseZones");
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

		// Initialisation de la liste de suivi medicaux
		if (getListeSuiviMed() == null || getListeSuiviMed().size() == 0) {
			int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
					.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
			setListeSuiviMed(getSuiviMedDao()
					.listerSuiviMedicalAvecMoisetAnneeSansEffectue(
							getMoisSelectionne(indiceMois),
							getAnneeSelectionne(indiceMois)));
			afficheListeSuiviMed();
			// pour les documents
			setListeDocuments(listerDocumentsSM());
			afficheListeDocuments();

		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getSuiviMedDao() == null)
			setSuiviMedDao((SuiviMedicalDao) context.getBean("suiviMedicalDao"));

		if (getMotifVisiteMedDao() == null)
			setMotifVisiteMedDao((MotifVisiteMedDao) context
					.getBean("motifVisiteMedDao"));

		if (getSpabsenDao() == null)
			setSpabsenDao((SPABSENDao) context.getBean("spabsenDao"));

	}

	private void afficheListeDocuments() {
		for (int i = 0; i < getListeDocuments().size(); i++) {
			String nomDoc = getListeDocuments().get(i);
			addZone(getNOM_ST_NOM_DOC(i),
					nomDoc.substring(nomDoc.lastIndexOf("/") + 1,
							nomDoc.length()));
		}
	}

	private ArrayList<String> listerDocumentsSM() throws ParseException {
		ArrayList<String> res = new ArrayList<String>();
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		String repPartage = (String) ServletAgent.getMesParametres().get(
				"REPERTOIRE_ROOT");
		String docuConvocF = repPartage + "SuiviMedical/SM_Convocation_F_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		String docuConvocCC = repPartage + "SuiviMedical/SM_Convocation_CC_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		String docuAccompagnementF = repPartage
				+ "SuiviMedical/SM_Lettre_Accompagnement_F_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		String docuAccompagnementCC = repPartage
				+ "SuiviMedical/SM_Lettre_Accompagnement_CC_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";

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
		for (int i = 0; i < getListeSuiviMed().size(); i++) {
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(i);
			addZone(getNOM_ST_NUM_SM(i), sm.getIdSuiviMed().toString());
			addZone(getNOM_ST_MATR(i), sm.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), sm.getAgent());
			addZone(getNOM_ST_STATUT(i), sm.getStatut());
			Service serv = Service.chercherService(getTransaction(),
					sm.getIdServi());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			addZone(getNOM_ST_SERVICE(i),
					serv == null || serv.getLibService() == null ? "&nbsp;"
							: serv.getLibService());
			addZone(getNOM_ST_DATE_DERNIERE_VISITE(i),
					sm.getDateDerniereVisite() == null ? "&nbsp;" : Services
							.convertitDate(sm.getDateDerniereVisite()
									.toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			addZone(getNOM_ST_DATE_PREVISION_VISITE(i),
					sm.getDatePrevisionVisite() == null ? "&nbsp;" : Services
							.convertitDate(sm.getDatePrevisionVisite()
									.toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			addZone(getNOM_ST_MOTIF(i), getLibMotifVM(sm.getIdMotifVM()
					.toString()));
			addZone(getNOM_ST_NB_VISITES_RATEES(i), sm.getNbVisitesRatees()
					.toString());
			addZone(getNOM_LB_MEDECIN_SELECT(i),
					sm.getIdMedecin() != null ? String
							.valueOf(getListeMedecin().indexOf(
									getHashMedecin().get(
											sm.getIdMedecin().toString())))
							: Const.ZERO);
			addZone(getNOM_ST_DATE_PROCHAIN_RDV(i),
					sm.getDateProchaineVisite() == null ? Const.CHAINE_VIDE
							: Services.convertitDate(sm
									.getDateProchaineVisite().toString(),
									"yyyy-MM-dd", "dd/MM/yyyy"));
			if (sm.getHeureProchaineVisite() != null) {
				Integer resHeure = getListeHeureRDV().indexOf(
						sm.getHeureProchaineVisite());
				addZone(getNOM_LB_HEURE_RDV_SELECT(i), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_RDV_SELECT(i), Const.ZERO);
			}
			addZone(getNOM_CK_A_IMPRIMER_CONVOC(i),
					sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getValue())
							|| sm.getEtat().equals(
									EnumEtatSuiviMed.ACCOMP.getValue()) ? getCHECKED_ON()
							: getCHECKED_OFF());
			addZone(getNOM_CK_A_IMPRIMER_ACCOMP(i),
					sm.getEtat().equals(EnumEtatSuiviMed.ACCOMP.getValue()) ? getCHECKED_ON()
							: getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), sm.getEtat());
		}
	}

	/**
	 * Retourne le libell� associ� au motif
	 * 
	 * @return String
	 */
	public String getLibMotifVM(String idMotif) {
		for (EnumMotifVisiteMed e : EnumMotifVisiteMed.values()) {
			if (e.getCode().equals(idMotif)) {
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null
				&& request.getParameter("JSP").equals(getJSP())) {

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
			if (testerParametre(request,
					getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS())) {
				return performPB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeSuiviMed().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeSuiviMed().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_VISUALISER pour les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}
		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste d�roulantes de l'�cran convocation du suivi
	 * m�dical.
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
			// alors il faudra afficher des mois de l'ann�e suivante
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

		// Si liste medecins vide alors affectation
		if (getListeMedecin() == null || getListeMedecin().size() == 0) {
			setListeMedecin(Medecin.listerMedecin(getTransaction()));

			int[] tailles = { 15 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator list = getListeMedecin().listIterator(); list
					.hasNext();) {
				Medecin m = (Medecin) list.next();
				String ligne[] = { m.getPrenomMedecin() + " "
						+ m.getNomMedecin() };

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
			int diffFinDeb = 9 * 60; // diff�rence en minute entre le d�but et
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
	 * Getter du nom de l'�cran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SM-CONVOCATION";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de cr�ation
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (28/11/11)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request)
			throws Exception {
		// Mise � jour de l'action men�e
		addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);

		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		if (indiceMois != -1) {
			// SuiviMedicalDao getSuiviMedDao() = new SuiviMedicalDao();
			setListeSuiviMed(getSuiviMedDao()
					.listerSuiviMedicalAvecMoisetAnneeSansEffectue(
							getMoisSelectionne(indiceMois),
							getAnneeSelectionne(indiceMois)));
			afficheListeSuiviMed();
			// getSuiviMedDao().detruitDao();
			// pour les documents
			setListeDocuments(listerDocumentsSM());
			afficheListeDocuments();
		} else {
			setListeSuiviMed(null);
			setListeDocuments(null);
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CALCULER Date de cr�ation :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_PB_CALCULER() {
		return "NOM_PB_CALCULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (28/11/11)
	 * 
	 */
	public boolean performPB_CALCULER(HttpServletRequest request)
			throws Exception {
		// Mise � jour de l'action men�e
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		if (indiceMois != -1) {
			Integer moisChoisi = getMoisSelectionne(indiceMois);
			Integer anneeChoisi = getAnneeSelectionne(indiceMois);

			// Suppression des suivi medicaux � l'�tat 'Travail' en fonction du
			// mois et de l'ann�e
			// SuiviMedicalDao getSuiviMedDao() = new SuiviMedicalDao();
			try {
				getSuiviMedDao().supprimerSuiviMedicalTravailAvecMoisetAnnee(
						EnumEtatSuiviMed.TRAVAIL.getValue(), moisChoisi,
						anneeChoisi);
			} catch (Exception e) {
				logger.warning("Probl�me dans la suppression des suivi medicaux"
						+ new Date());
			}

			// Lancement du calcul des suivi medicaux
			performCalculSuiviMedical(moisChoisi, anneeChoisi);

			// Affichage de la liste
			setListeSuiviMed(getSuiviMedDao()
					.listerSuiviMedicalAvecMoisetAnneeSansEffectue(moisChoisi,
							anneeChoisi));
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
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		// "INF400","Calcul effectu�"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF400"));

		return true;
	}

	private void performCalculSuiviMedical(Integer moisChoisi,
			Integer anneeChoisi) throws Exception {

		// CAS N�1 : A la demande de l'agent ou du service
		logger.info("Calcul cas1 : A la demande de l'agent ou du service");
		perfomrCalculCas1(moisChoisi, anneeChoisi);

		// CAS N�2 : Visite R�guliere
		logger.info("Calcul cas2 : Visite R�guliere");
		perfomrCalculCas2(moisChoisi, anneeChoisi);

		// CAS N�3 : AT avec ITT>15jours
		logger.info("Calcul cas3 :  AT avec ITT>15jours");
		perfomrCalculCas3(moisChoisi, anneeChoisi);

		// CAS N�4 : Maladie > 1 mois
		logger.info("Calcul cas4 : Maladie > 1 mois");
		perfomrCalculCas4(moisChoisi, anneeChoisi);

		// CAS N�5 : Longue maladie
		logger.info("Calcul cas5 : Longue maladie");
		perfomrCalculCas5(moisChoisi, anneeChoisi);

		// CAS N�6 : Visite Nouvel arrivant
		logger.info("Calcul cas6 : Visite Nouvel arrivant");
		perfomrCalculCas6(moisChoisi, anneeChoisi);

		// CAS N�7 : Changement de PA
		logger.info("Calcul cas7 : Changement de PA");
		perfomrCalculCas7(moisChoisi, anneeChoisi);

		// CAS N�8 : CONVOCATION NON EXECUTEE
		logger.info("Calcul cas8 : CONVOCATION NON EXECUTEE");
		perfomrCalculCas8(moisChoisi, anneeChoisi);

		// CAS N�9 : AGENT SANS VISITES MEDICALES
		logger.info("Calcul cas9 : AGENT SANS VISITES MEDICALES");
		perfomrCalculCas9(moisChoisi, anneeChoisi);

		logger.info("FIN DES CALCULS");

	}

	private void perfomrCalculCas9(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�9 : AGENT SANS VISITES MEDICALES
		// on liste tous les agents sans visites medicales
		// avec une PA active � la date du jour
		ArrayList<AgentNW> listeSMCas9 = AgentNW
				.listerAgentSansVMPAEnCours(getTransaction());
		int nbCas9 = 0;

		for (int i = 0; i < listeSMCas9.size(); i++) {
			AgentNW agent = listeSMCas9.get(i);
			// on cr�e la nouvelle ligne
			SuiviMedical sm = new SuiviMedical();
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(),
						aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(null);
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse("15/"
					+ moisChoisi + "/" + anneeChoisi);
			sm.setDatePrevisionVisite(d);
			sm.setIdMotifVM(Integer.valueOf(EnumMotifVisiteMed.VM_AGENT_SANS_VM
					.getCode()));
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas r�ecrire une ligne
			// du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
				logger.fine("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est
				// superieur �
				// celle existante
				// si oui alors on ne cr�e pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recr�er
				// la
				// nouvelle
				if (smTemp.getEtat()
						.equals(EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
				// aucune ligne n'a �t� trouv�e alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getIdServi(),
					sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
					sm.getIdMotifVM(), sm.getNbVisitesRatees(),
					sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(),
					sm.getAnnee(), sm.getRelance());
			nbCas9++;
		}
		logger.info("Nb de cas 9 : " + nbCas9);
	}

	private void perfomrCalculCas5(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�5 : Longue maladie
		// on liste toutes les absences (SPABSEN) de type MA pourle mois et
		// l'ann�e donn�e
		int nbCas5 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas5 = getSpabsenDao()
					.listerMatriculeAbsencePourSMDoubleType("MA", "LM",
							moisChoisi, anneeChoisi);
			// pour chaque matricule trouv� on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 90
			for (int i = 0; i < listeMatriculeSMCas5.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas5.get(i);
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
						// ligne suivante est egale � datFin de la precdente + 1
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
					// on cr�e la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					AgentNW agent = AgentNW.chercherAgentParMatricule(
							getTransaction(), nomatrAgent.toString());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					Affectation aff = Affectation
							.chercherAffectationActiveAvecAgent(
									getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(),
								aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					String datePrev = Services.ajouteJours(Services
							.convertitDate(dernierAM.getDatFin().toString(),
									"yyyyMMdd", "dd/MM/yyyy"), 2);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVM(Integer
							.valueOf(EnumMotifVisiteMed.VM_CONGE_LONGUE_MALADIE
									.getCode()));
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas r�ecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentMoisetAnnee(
										sm.getIdAgent(), moisChoisi,
										anneeChoisi);
						logger.fine("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur �
						// celle existante
						// si oui alors on ne cr�e pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recr�er
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
						// aucune ligne n'a �t� trouv�e alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVM(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance());
					nbCas5++;

				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 5.");
		}
		logger.info("Nb cas 5 : " + nbCas5);
	}

	private void perfomrCalculCas4(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�4 : Maladie > 1 mois
		// on liste toutes les absences (SPABSEN) de type MA pourle mois et
		// l'ann�e donn�e
		int nbCas4 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas4 = getSpabsenDao()
					.listerMatriculeAbsencePourSM("MA", moisChoisi, anneeChoisi);
			// pour chaque matricule trouv� on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 30
			for (int i = 0; i < listeMatriculeSMCas4.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas4.get(i);
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
						// ligne suivante est egale � datFin de la precdente + 1
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
					// on cr�e la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					AgentNW agent = AgentNW.chercherAgentParMatricule(
							getTransaction(), nomatrAgent.toString());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					Affectation aff = Affectation
							.chercherAffectationActiveAvecAgent(
									getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(),
								aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					String datePrev = Services.ajouteJours(Services
							.convertitDate(dernierAM.getDatFin().toString(),
									"yyyyMMdd", "dd/MM/yyyy"), 2);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVM(Integer
							.valueOf(EnumMotifVisiteMed.VM_MA_1MOIS.getCode()));
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas r�ecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentMoisetAnnee(
										sm.getIdAgent(), moisChoisi,
										anneeChoisi);
						logger.fine("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur �
						// celle existante
						// si oui alors on ne cr�e pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recr�er
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
						// aucune ligne n'a �t� trouv�e alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVM(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance());
					nbCas4++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 4");
		}
		logger.info("Nb de cas 4 : " + nbCas4);
	}

	private void perfomrCalculCas3(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�3 : AT avec ITT>15jours
		// on liste toutes les absences (SPABSEN) de type AT pourle mois et
		// l'ann�e donn�e
		int nbCas3 = 0;
		try {
			ArrayList<Integer> listeMatriculeSMCas3 = getSpabsenDao()
					.listerMatriculeAbsencePourSM("AT", moisChoisi, anneeChoisi);
			// pour chaque matricule trouv� on va cherche la liste de ses
			// SPABSEN et
			// on regarde si il se suivent, que le nombre de jours est > 15
			for (int i = 0; i < listeMatriculeSMCas3.size(); i++) {
				Integer nomatrAgent = listeMatriculeSMCas3.get(i);
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
						// ligne suivante est egale � datFin de la precdente + 1
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
					// on cr�e la nouvelle ligne
					SuiviMedical sm = new SuiviMedical();
					AgentNW agent = AgentNW.chercherAgentParMatricule(
							getTransaction(), nomatrAgent.toString());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					Affectation aff = Affectation
							.chercherAffectationActiveAvecAgent(
									getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(),
								aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					String datePrev = Services.ajouteJours(Services
							.convertitDate(dernierAT.getDatFin().toString(),
									"yyyyMMdd", "dd/MM/yyyy"), 1);
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(datePrev);
					sm.setDatePrevisionVisite(d);
					sm.setIdMotifVM(Integer
							.valueOf(EnumMotifVisiteMed.VM_AT_ITT_15JOURS
									.getCode()));
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas r�ecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentMoisetAnnee(
										sm.getIdAgent(), moisChoisi,
										anneeChoisi);
						logger.fine("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur �
						// celle existante
						// si oui alors on ne cr�e pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recr�er
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
						// aucune ligne n'a �t� trouv�e alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVM(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance());
					nbCas3++;
				}
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 3");
		}
		logger.info("Nb de cas 3 : " + nbCas3);
	}

	private void perfomrCalculCas1(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�1 : A la demande de l'agent ou du service
		// on liste toutes les visites medicales du type "a la demande..."
		Medecin m = Medecin.chercherMedecinByLib(getTransaction(),
				Const.CHAINE_VIDE, "A", "RENSEIGNER");
		ArrayList<VisiteMedicale> listeSMCas1 = VisiteMedicale
				.listerVisiteMedicalePourSMCas1(getTransaction(),
						EnumMotifVisiteMed.VM_DEMANDE_AGENT.getCode(),
						EnumMotifVisiteMed.VM_DEMANDE_SERVICE.getCode(),
						m.getIdMedecin());
		int nbCas1 = 0;
		for (int i = 0; i < listeSMCas1.size(); i++) {
			VisiteMedicale vm = listeSMCas1.get(i);
			// on cr�e la nouvelle ligne
			SuiviMedical sm = new SuiviMedical();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(),
					vm.getIdAgent());
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(),
						aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(null);
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse("15/"
					+ moisChoisi + "/" + anneeChoisi);
			sm.setDatePrevisionVisite(d);
			sm.setIdMotifVM(Integer.valueOf(vm.getIdMotif()));
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas r�ecrire une ligne
			// du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(sm.getIdAgent(),
								moisChoisi, anneeChoisi);
				logger.fine("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est
				// superieur �
				// celle existante
				// si oui alors on ne cr�e pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recr�er
				// la
				// nouvelle
				if (smTemp.getEtat()
						.equals(EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
				// aucune ligne n'a �t� trouv�e alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getIdServi(),
					sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
					sm.getIdMotifVM(), sm.getNbVisitesRatees(),
					sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(),
					sm.getAnnee(), sm.getRelance());
			nbCas1++;
		}
		logger.info("Nb de cas 1 : " + nbCas1);
	}

	private void perfomrCalculCas8(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�8 : CONVOCATION NON EXECUTEE
		// on liste tous les suivi m�dicaux de type "CONVOQUE" du mois pr�c�dent
		int nbCas8 = 0;
		try {
			ArrayList<SuiviMedical> listeSMCas8 = getSuiviMedDao()
					.listerSuiviMedicalNonEffectue(moisChoisi, anneeChoisi,
							EnumEtatSuiviMed.CONVOQUE.getValue());
			for (int i = 0; i < listeSMCas8.size(); i++) {
				// on cr�e une nouvelle ligne avec les memes informations
				// sauf pour le statut et le service on le remet � jour
				SuiviMedical smAncien = listeSMCas8.get(i);
				// on cr�e la nouvelle ligne
				SuiviMedical sm = new SuiviMedical();
				AgentNW agent = AgentNW.chercherAgentParMatricule(
						getTransaction(), smAncien.getNomatr().toString());
				Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
						getTransaction(), agent.getIdAgent());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				Affectation aff = Affectation
						.chercherAffectationActiveAvecAgent(getTransaction(),
								agent.getIdAgent());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				FichePoste fp = null;
				if (aff != null && aff.getIdFichePoste() != null) {
					fp = FichePoste.chercherFichePoste(getTransaction(),
							aff.getIdFichePoste());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
				}
				sm.setIdAgent(smAncien.getIdAgent());
				sm.setNomatr(smAncien.getNomatr());
				sm.setAgent(smAncien.getAgent());
				sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
						.getStatutSM(carr.getCodeCategorie()) : null);
				sm.setIdServi(fp != null ? fp.getIdServi() : null);
				sm.setDateDerniereVisite(smAncien.getDateDerniereVisite());
				sm.setDatePrevisionVisite(smAncien.getDatePrevisionVisite());
				sm.setIdMotifVM(smAncien.getIdMotifVM());
				// ATTENTION : si mois de la date de prochainRDV < moisChoisi
				// alors
				// il faut incr�menter le compteur de 1 pour NbVisiteRat�es
				if (smAncien.getDateProchaineVisite() != null
						&& Integer.valueOf(smAncien.getDateProchaineVisite()
								.toString().substring(5, 7)) < moisChoisi) {
					sm.setNbVisitesRatees(smAncien.getNbVisitesRatees() + 1);
				} else {
					sm.setNbVisitesRatees(smAncien.getNbVisitesRatees());
				}

				sm.setIdMedecin(null);
				sm.setDateProchaineVisite(null);
				sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
				sm.setMois(moisChoisi);
				sm.setAnnee(anneeChoisi);
				// on flag cette ligne en relance afin qu'on puisse la mettre de
				// couleur diff�rente � l'affichage
				sm.setRelance(1);

				// on regarde la liste des SM pour ne pas r�ecrire une ligne
				// du meme
				// agent
				try {
					SuiviMedical smTemp = getSuiviMedDao()
							.chercherSuiviMedicalAgentMoisetAnnee(
									smAncien.getIdAgent(), moisChoisi,
									anneeChoisi);
					logger.fine("SM : " + smTemp.toString());
					// si une ligne existe deja
					// on regarde si etat Travail
					// si oui, on regarde si la date de prevision est
					// superieur �
					// celle existante
					// si oui alors on ne cr�e pas de nouvelle ligne
					// si non , on supprime la ligne existante pour recr�er
					// la
					// nouvelle
					if (smTemp.getEtat().equals(
							EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
					// aucune ligne n'a �t� trouv�e alors on continue
				}
				getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
						sm.getNomatr(), sm.getAgent(), sm.getStatut(),
						sm.getIdServi(), sm.getDateDerniereVisite(),
						sm.getDatePrevisionVisite(), sm.getIdMotifVM(),
						sm.getNbVisitesRatees(), sm.getIdMedecin(),
						sm.getDateProchaineVisite(),
						sm.getHeureProchaineVisite(), sm.getEtat(),
						sm.getMois(), sm.getAnnee(), sm.getRelance());
				nbCas8++;
			}
		} catch (Exception e) {
			logger.info("Aucun resultat pour le cas 8");
		}
		logger.info("Nb de cas 8 : " + nbCas8);
	}

	private void perfomrCalculCas7(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�7 : Changement de PA
		// on liste toutes les PA hors-effectif
		// on v�rifie qu'il y a bien une PA normale apres cette PA hors-effectif
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
					// si c'est bon alors on cr�e le suiviMedical
					SuiviMedical sm = new SuiviMedical();
					AgentNW agent = AgentNW.chercherAgentParMatricule(
							getTransaction(), paSuivante.getNomatr());
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
							getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					Affectation aff = Affectation
							.chercherAffectationActiveAvecAgent(
									getTransaction(), agent.getIdAgent());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
					FichePoste fp = null;
					if (aff != null && aff.getIdFichePoste() != null) {
						fp = FichePoste.chercherFichePoste(getTransaction(),
								aff.getIdFichePoste());
						if (getTransaction().isErreur())
							getTransaction().traiterErreur();
					}
					sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
					sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
					sm.setAgent(agent.getNomAgent() + " "
							+ agent.getPrenomAgent());
					sm.setStatut(carr != null
							&& carr.getCodeCategorie() != null ? getSuiviMedDao()
							.getStatutSM(carr.getCodeCategorie()) : null);
					sm.setIdServi(fp != null ? fp.getIdServi() : null);
					sm.setDateDerniereVisite(null);
					Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(Services
							.enleveJours(paSuivante.getDatdeb(), 15));
					sm.setDatePrevisionVisite(d2);
					sm.setIdMotifVM(Integer
							.valueOf(EnumMotifVisiteMed.VM_CHANGEMENT_PA
									.getCode()));
					sm.setNbVisitesRatees(0);
					sm.setIdMedecin(null);
					sm.setDateProchaineVisite(null);
					sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
					sm.setMois(moisChoisi);
					sm.setAnnee(anneeChoisi);
					sm.setRelance(0);

					// on regarde la liste des SM pour ne pas r�ecrire une ligne
					// du meme
					// agent
					try {
						SuiviMedical smTemp = getSuiviMedDao()
								.chercherSuiviMedicalAgentNomatrMoisetAnnee(
										Integer.valueOf(paHorsEffectif
												.getNomatr()), moisChoisi,
										anneeChoisi);
						logger.fine("SM : " + smTemp.toString());
						// si une ligne existe deja
						// on regarde si etat Travail
						// si oui, on regarde si la date de prevision est
						// superieur �
						// celle existante
						// si oui alors on ne cr�e pas de nouvelle ligne
						// si non , on supprime la ligne existante pour recr�er
						// la
						// nouvelle
						if (smTemp.getEtat().equals(
								EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
						// aucune ligne n'a �t� trouv�e alors on continue
					}
					getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(),
							sm.getNomatr(), sm.getAgent(), sm.getStatut(),
							sm.getIdServi(), sm.getDateDerniereVisite(),
							sm.getDatePrevisionVisite(), sm.getIdMotifVM(),
							sm.getNbVisitesRatees(), sm.getIdMedecin(),
							sm.getDateProchaineVisite(),
							sm.getHeureProchaineVisite(), sm.getEtat(),
							sm.getMois(), sm.getAnnee(), sm.getRelance());
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

	private void perfomrCalculCas6(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�6 : Visite Nouvel arrivant
		// on liste tous les nouveaux arrivant
		ArrayList<AgentNW> listeAgentCas6 = AgentNW
				.listerAgentNouveauxArrivant(getTransaction(), moisChoisi,
						anneeChoisi);
		int nbCas6 = 0;
		for (int i = 0; i < listeAgentCas6.size(); i++) {
			SuiviMedical sm = new SuiviMedical();
			AgentNW agent = listeAgentCas6.get(i);
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(),
						aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(agent.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			sm.setDateDerniereVisite(null);
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(agent
					.getDateDerniereEmbauche());
			sm.setDatePrevisionVisite(d2);
			sm.setIdMotifVM(Integer.valueOf(EnumMotifVisiteMed.VM_NOUVEAU
					.getCode()));
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas r�ecrire une ligne du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(
								Integer.valueOf(agent.getIdAgent()),
								moisChoisi, anneeChoisi);
				logger.fine("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur �
				// celle existante
				// si oui alors on ne cr�e pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recr�er la
				// nouvelle
				if (smTemp.getEtat()
						.equals(EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
				// aucune ligne n'a �t� trouv�e alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getIdServi(),
					sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
					sm.getIdMotifVM(), sm.getNbVisitesRatees(),
					sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(),
					sm.getAnnee(), sm.getRelance());
			nbCas6++;
		}
		logger.info("Nb de cas 6 : " + nbCas6);
	}

	private void perfomrCalculCas2(Integer moisChoisi, Integer anneeChoisi)
			throws Exception {
		// CAS N�2 : Visite R�guliere
		// on liste toutes les visites medicales
		// dont la dateVM + dur�e validit�VM = mois et ann�e choisie du calcul
		ArrayList<VisiteMedicale> listeVMCas2 = VisiteMedicale
				.listerVisiteMedicalePourSMCas2(getTransaction(), moisChoisi,
						anneeChoisi);
		int nbCas2 = 0;
		for (int i = 0; i < listeVMCas2.size(); i++) {
			VisiteMedicale vm = listeVMCas2.get(i);

			SuiviMedical sm = new SuiviMedical();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(),
					vm.getIdAgent());
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(
					getTransaction(), agent.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(
					getTransaction(), vm.getIdAgent());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			FichePoste fp = null;
			if (aff != null && aff.getIdFichePoste() != null) {
				fp = FichePoste.chercherFichePoste(getTransaction(),
						aff.getIdFichePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}
			sm.setIdAgent(Integer.valueOf(vm.getIdAgent()));
			sm.setNomatr(Integer.valueOf(agent.getNoMatricule()));
			sm.setAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
			sm.setStatut(carr != null && carr.getCodeCategorie() != null ? getSuiviMedDao()
					.getStatutSM(carr.getCodeCategorie()) : null);
			sm.setIdServi(fp != null ? fp.getIdServi() : null);
			Date d = new SimpleDateFormat("dd/MM/yyyy").parse(vm
					.getDateDerniereVisite());
			sm.setDateDerniereVisite(d);
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(Services
					.ajouteMois(vm.getDateDerniereVisite(),
							Integer.valueOf(vm.getDureeValidite())));
			sm.setDatePrevisionVisite(d2);
			sm.setIdMotifVM(Integer.valueOf(EnumMotifVisiteMed.VM_REGULIERE
					.getCode()));
			sm.setNbVisitesRatees(0);
			sm.setIdMedecin(null);
			sm.setDateProchaineVisite(null);
			sm.setEtat(EnumEtatSuiviMed.TRAVAIL.getValue());
			sm.setMois(moisChoisi);
			sm.setAnnee(anneeChoisi);
			sm.setRelance(0);

			// on regarde la liste des SM pour ne pas r�ecrire une ligne du meme
			// agent
			try {
				SuiviMedical smTemp = getSuiviMedDao()
						.chercherSuiviMedicalAgentMoisetAnnee(
								Integer.valueOf(vm.getIdAgent()), moisChoisi,
								anneeChoisi);
				logger.fine("SM : " + smTemp.toString());
				// si une ligne existe deja
				// on regarde si etat Travail
				// si oui, on regarde si la date de prevision est superieur �
				// celle existante
				// si oui alors on ne cr�e pas de nouvelle ligne
				// si non , on supprime la ligne existante pour recr�er la
				// nouvelle
				if (smTemp.getEtat()
						.equals(EnumEtatSuiviMed.TRAVAIL.getValue())) {
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
				// aucune ligne n'a �t� trouv�e alors on continue
			}
			getSuiviMedDao().creerSuiviMedical(sm.getIdAgent(), sm.getNomatr(),
					sm.getAgent(), sm.getStatut(), sm.getIdServi(),
					sm.getDateDerniereVisite(), sm.getDatePrevisionVisite(),
					sm.getIdMotifVM(), sm.getNbVisitesRatees(),
					sm.getIdMedecin(), sm.getDateProchaineVisite(),
					sm.getHeureProchaineVisite(), sm.getEtat(), sm.getMois(),
					sm.getAnnee(), sm.getRelance());
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
	 * Getter de la liste avec un lazy initialize : LB_MOIS Date de cr�ation :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_MOIS() {
		if (LB_MOIS == null)
			LB_MOIS = initialiseLazyLB();
		return LB_MOIS;
	}

	/**
	 * Setter de la liste: LB_MOIS Date de cr�ation : (28/11/11)
	 * 
	 */
	private void setLB_MOIS(String[] listeMois) {
		LB_MOIS = listeMois;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOIS Date de cr�ation :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_MOIS() {
		return "NOM_LB_MOIS";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_MOIS_SELECT Date de cr�ation : (28/11/11)
	 * 
	 */
	public String getNOM_LB_MOIS_SELECT() {
		return "NOM_LB_MOIS_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_MOIS Date de cr�ation : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MOIS() {
		return getLB_MOIS();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_MOIS Date de cr�ation : (28/11/11)
	 * 
	 */
	public String getVAL_LB_MOIS_SELECT() {
		return getZone(getNOM_LB_MOIS_SELECT());
	}

	/**
	 * Getter de la liste des ann�es possibles.
	 * 
	 * @return listeMois
	 */
	private String[] getListeMois() {
		return listeMois;
	}

	/**
	 * Setter de la liste des ann�es possibles.
	 * 
	 * @param strings
	 */
	private void setListeMois(String[] strings) {
		this.listeMois = strings;
	}

	public ArrayList getListeSuiviMed() {
		if (listeSuiviMed == null)
			return new ArrayList();
		return listeSuiviMed;
	}

	public void setListeSuiviMed(ArrayList listeSuiviMed) {
		this.listeSuiviMed = listeSuiviMed;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_SM Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_SM(int i) {
		return "NOM_ST_NUM_SM_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NUM_SM Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_SM(int i) {
		return getZone(getNOM_ST_NUM_SM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MATR Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MATR Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_STATUT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_SERVICE Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DERNIERE_VISITE
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DERNIERE_VISITE(int i) {
		return "NOM_ST_DATE_DERNIERE_VISITE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_DATE_DERNIERE_VISITE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DERNIERE_VISITE(int i) {
		return getZone(getNOM_ST_DATE_DERNIERE_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_DATE_PREVISION_VISITE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_PREVISION_VISITE(int i) {
		return "NOM_ST_DATE_PREVISION_VISITE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_DATE_PREVISION_VISITE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_PREVISION_VISITE(int i) {
		return getZone(getNOM_ST_DATE_PREVISION_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOTIF Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_VISITES_RATEES
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NB_VISITES_RATEES(int i) {
		return "NOM_ST_NB_VISITES_RATEES_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_NB_VISITES_RATEES Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NB_VISITES_RATEES(int i) {
		return getZone(getNOM_ST_NB_VISITES_RATEES(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_PROCHAIN_RDV
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_PROCHAIN_RDV(int i) {
		return "NOM_ST_DATE_PROCHAIN_RDV_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_DATE_PROCHAIN_RDV Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_PROCHAIN_RDV(int i) {
		return getZone(getNOM_ST_DATE_PROCHAIN_RDV(i));
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
	 * Getter de la liste avec un lazy initialize : LB_MEDECIN Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_MEDECIN(int i) {
		if (LB_MEDECIN == null)
			LB_MEDECIN = initialiseLazyLB();
		return LB_MEDECIN;
	}

	/**
	 * Setter de la liste: LB_MEDECIN Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_MEDECIN(String[] newLB_MEDECIN) {
		LB_MEDECIN = newLB_MEDECIN;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MEDECIN Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_MEDECIN(int i) {
		return "NOM_LB_MEDECIN_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_MEDECIN_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_MEDECIN_SELECT(int i) {
		return "NOM_LB_MEDECIN_" + i + "_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_MEDECIN Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MEDECIN(int i) {
		return getLB_MEDECIN(i);
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_MEDECIN Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_MEDECIN_SELECT(int i) {
		return getZone(getNOM_LB_MEDECIN_SELECT(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_A_IMPRIMER_CONVOC Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_A_IMPRIMER_CONVOC(int i) {
		return "NOM_CK_A_IMPRIMER_CONVOC_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_A_IMPRIMER_CONVOC Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_A_IMPRIMER_CONVOC(int i) {
		return getZone(getNOM_CK_A_IMPRIMER_CONVOC(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_A_IMPRIMER_ACCOMP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_A_IMPRIMER_ACCOMP(int i) {
		return "NOM_CK_A_IMPRIMER_ACCOMP_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_A_IMPRIMER_ACCOMP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_A_IMPRIMER_ACCOMP(int i) {
		return getZone(getNOM_CK_A_IMPRIMER_ACCOMP(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request,
			int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		setStatut(STATUT_MEME_PROCESS);

		// on change l'etat juste pour l'affichage
		addZone(getNOM_ST_ETAT(indiceEltAModifier),
				EnumEtatSuiviMed.PLANIFIE.getValue());
		addZone(getVAL_CK_A_IMPRIMER_CONVOC(indiceEltAModifier),
				Const.CHAINE_VIDE);
		addZone(getVAL_CK_A_IMPRIMER_ACCOMP(indiceEltAModifier),
				Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de cr�ation
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 * RG_AG_CA_A08
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request,
			int indiceEltASuprimer) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		setStatut(STATUT_MEME_PROCESS);

		// on change l'etat juste pour l'affichage
		addZone(getNOM_ST_ETAT(indiceEltASuprimer),
				EnumEtatSuiviMed.TRAVAIL.getValue());
		addZone(getNOM_ST_DATE_PROCHAIN_RDV(indiceEltASuprimer),
				Const.CHAINE_VIDE);
		addZone(getNOM_LB_HEURE_RDV_SELECT(indiceEltASuprimer), Const.ZERO);
		addZone(getNOM_LB_MEDECIN_SELECT(indiceEltASuprimer), Const.ZERO);
		addZone(getVAL_CK_A_IMPRIMER_CONVOC(indiceEltASuprimer),
				Const.CHAINE_VIDE);
		addZone(getVAL_CK_A_IMPRIMER_ACCOMP(indiceEltASuprimer),
				Const.CHAINE_VIDE);

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
	public boolean performPB_VALIDER(HttpServletRequest request)
			throws Exception {
		if (!performControlerSaisie()) {
			return false;
		}
		// on sauvegarder l'�tat du tableau
		sauvegardeTableau();
		// on remet la liste � vide afin qu'elle soit initialis�e avec les
		// nouvelles valeurs
		setListeSuiviMed(null);
		setListeDocuments(null);
		return true;
	}

	private boolean performControlerSaisie() throws ParseException {
		// on controle les champs
		for (int i = 0; i < getListeSuiviMed().size(); i++) {
			// si la ligne n'est pas en etat travail
			if (!getVAL_ST_ETAT(i).equals(EnumEtatSuiviMed.TRAVAIL.getValue())) {
				String dateRDV = getVAL_ST_DATE_PROCHAIN_RDV(i);
				String agentConcerne = getVAL_ST_MATR(i) + " ( "
						+ getVAL_ST_AGENT(i) + " ) ";
				// si la date du prochain RDV est vide
				if (dateRDV == null || dateRDV.trim().equals(Const.CHAINE_VIDE)) {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR002",
									"date du prochain RDV pour la ligne " + i));
					return false;
				}
				// si la date du prochain RDV est inf�rieur au moins selectionn�
				int indiceMois = (Services
						.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
						.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
				Integer moisChoisi = getMoisSelectionne(indiceMois);
				Integer moisRDV = Integer.valueOf(dateRDV.substring(3, 5));
				if (moisRDV < moisChoisi) {
					// "ERR300",
					// "La date du prochain RDV pour l'agent @ doit �tre sup�rieure ou �gale au mois selectionn�.");
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR300", agentConcerne));
					return false;
				}
				// Contr�le format date du prochain RDV
				if (!Services.estUneDate(dateRDV)) {
					// "ERR301",
					// "La date du prochain RDV est incorrecte pour l'agent @. Elle doit �tre au format date."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR301", agentConcerne));
					return false;
				} else {
					addZone(getNOM_ST_DATE_PROCHAIN_RDV(i),
							Services.formateDate(dateRDV));
				}

				// si la date du prochain RDV est inf�rieur � la date du jour
				if (!getVAL_ST_ETAT(i).equals(
						EnumEtatSuiviMed.CONVOQUE.getValue())
						&& !getVAL_ST_ETAT(i).equals(
								EnumEtatSuiviMed.ACCOMP.getValue())
						&& Services
								.compareDates(dateRDV, Services.dateDuJour()) < 0) {
					// "ERR302",
					// "La date du prochain RDV pour l'agent @ doit �tre sup�rieure ou �gale � la date du jour"
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR302", agentConcerne));
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

	private Hashtable<String, Medecin> getHashMedecin() {
		if (hashMedecin == null)
			hashMedecin = new Hashtable<String, Medecin>();
		return hashMedecin;
	}

	private void setHashMedecin(Hashtable<String, Medecin> hashMedecin) {
		this.hashMedecin = hashMedecin;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (12/09/11 11:49:01)
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
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_RDV(int i) {
		if (LB_HEURE_RDV == null)
			LB_HEURE_RDV = initialiseLazyLB();
		return LB_HEURE_RDV;
	}

	/**
	 * Setter de la liste: LB_HEURE_RDV Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_HEURE_RDV(String[] newLB_HEURE_RDV) {
		LB_HEURE_RDV = newLB_HEURE_RDV;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_RDV Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_RDV(int i) {
		return "NOM_LB_HEURE_RDV_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_RDV_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_RDV_SELECT(int i) {
		return "NOM_LB_HEURE_RDV_" + i + "_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_RDV Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_RDV(int i) {
		return getLB_HEURE_RDV(i);
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_RDV Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_RDV_SELECT(int i) {
		return getZone(getNOM_LB_HEURE_RDV_SELECT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER_CONVOCATIONS Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER_LISTE_VISITE() {
		return "NOM_PB_IMPRIMER_LISTE_VISITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER_LISTE_VISITE(HttpServletRequest request)
			throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER_CONVOCATIONS Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS() {
		return "NOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS(
			HttpServletRequest request) throws Exception {
		convocationsEnErreur = Const.CHAINE_VIDE;
		if (!performControlerSaisie()) {
			return false;
		}
		sauvegardeTableau();

		// on supprime les documents existants
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		String repPartage = (String) ServletAgent.getMesParametres().get(
				"REPERTOIRE_ACTES");

		String docuAccompagnementF = repPartage
				+ "SuiviMedical/SM_Lettre_Accompagnement_F_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		String docuAccompagnementCC = repPartage
				+ "SuiviMedical/SM_Lettre_Accompagnement_CC_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		// on verifie l'existance de chaque fichier
		File accompF = new File(docuAccompagnementF.substring(8,
				docuAccompagnementF.length()));
		if (accompF.exists()) {
			accompF.delete();
		}
		File accompCC = new File(docuAccompagnementCC.substring(8,
				docuAccompagnementCC.length()));
		if (accompCC.exists()) {
			accompCC.delete();
		}

		int nbConvocImpr = 0;
		// on recupere les lignes qui sont coch�es pour imprimer
		ArrayList<SuiviMedical> smFonctionnaireAImprimer = new ArrayList<SuiviMedical>();
		ArrayList<SuiviMedical> smCCAImprimer = new ArrayList<SuiviMedical>();
		for (int i = 0; i < getListeSuiviMed().size(); i++) {
			// on recup�re la ligne concern�e
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(i);
			// si l'etat de la ligne est 'convoque'
			if (sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getValue())) {
				if (getVAL_CK_A_IMPRIMER_ACCOMP(i).equals(getCHECKED_ON())) {
					// RG-SVM-12.4
					if (sm.getStatut() != null
							&& !sm.getStatut().equals(Const.CHAINE_VIDE)) {
						if (sm.getStatut().equals("F")) {
							// alors on edite EDIT_SVM-4
							smFonctionnaireAImprimer.add(sm);
						} else if (sm.getStatut().equals("CC")) {
							// alors on edite EDIT_SVM-5
							smCCAImprimer.add(sm);
						}
						sm.setEtat(EnumEtatSuiviMed.ACCOMP.getValue());
						getSuiviMedDao().modifierSuiviMedicalTravail(
								sm.getIdSuiviMed(), sm);
						nbConvocImpr++;
					} else {
						// cas vide on ne traite pas
						// mais on informe l'utilisateur que certaines lignes
						// n'ont pu �tre imprim�es
						// on met l'agent dans une variable et on affiche cette
						// liste � l'ecran
						convocationsEnErreur += sm.getAgent() + " ("
								+ sm.getNomatr() + "); ";
					}
				}
			}
		}

		// si aucune convocation selectionn�e
		// RG_SVM-12.1
		if (nbConvocImpr == 0) {
			// "INF401",
			// "Attention, aucune convocation n'est s�lectionn�e pour cette impression."
			getTransaction().declarerErreur(MessageUtils.getMessage("INF401"));
			return false;
		} else {
			// on remet la liste � vide afin qu'elle soit initialis�e avec les
			// nouvelles valeurs
			setListeSuiviMed(null);
			setListeDocuments(null);
		}

		// on imprime les 2 listes
		String repModeles = (String) ServletAgent.getMesParametres().get(
				"REPERTOIRE_MODELES_SM");

		if (smFonctionnaireAImprimer.size() > 0) {
			String destination = repPartage
					+ "SuiviMedical/SM_Lettre_Accompagnement_F_"
					+ getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".xml";
			creerModeleDocumentSVM4(smFonctionnaireAImprimer, repModeles,
					destination);
		}
		if (smCCAImprimer.size() > 0) {
			String destination = repPartage
					+ "SuiviMedical/SM_Lettre_Accompagnement_CC_"
					+ getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".xml";
			creerModeleDocumentSVM5(smCCAImprimer, repModeles, destination);
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER_CONVOCATIONS Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER_CONVOCATIONS() {
		return "NOM_PB_IMPRIMER_CONVOCATIONS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER_CONVOCATIONS(HttpServletRequest request)
			throws Exception {
		convocationsEnErreur = Const.CHAINE_VIDE;
		if (!performControlerSaisie()) {
			return false;
		}
		sauvegardeTableau();

		// on supprime les documents existants
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
		String repPartage = (String) ServletAgent.getMesParametres().get(
				"REPERTOIRE_ACTES");

		String docuConvocF = repPartage + "SuiviMedical/SM_Convocation_F_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		String docuConvocCC = repPartage + "SuiviMedical/SM_Convocation_CC_"
				+ getMoisSelectionne(indiceMois) + "_"
				+ getAnneeSelectionne(indiceMois) + ".xml";
		// on verifie l'existance de chaque fichier
		File convocF = new File(docuConvocF.substring(8, docuConvocF.length()));
		if (convocF.exists()) {
			convocF.delete();
		}
		File convocCC = new File(docuConvocCC.substring(8,
				docuConvocCC.length()));
		if (convocCC.exists()) {
			convocCC.delete();
		}

		int nbConvocImpr = 0;
		// on recupere les lignes qui sont coch�es pour imprimer
		ArrayList<SuiviMedical> smFonctionnaireAImprimer = new ArrayList<SuiviMedical>();
		ArrayList<SuiviMedical> smCCAImprimer = new ArrayList<SuiviMedical>();
		for (int i = 0; i < getListeSuiviMed().size(); i++) {
			// on recup�re la ligne concern�e
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(i);
			// si l'etat de la ligne n'est pas deja 'imprim�' et que la colonne
			// imprim�e est coch�e
			if (!sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getValue())) {
				if (getVAL_CK_A_IMPRIMER_CONVOC(i).equals(getCHECKED_ON())) {
					// RG-SVM-10.3
					if (sm.getStatut() != null
							&& !sm.getStatut().equals(Const.CHAINE_VIDE)) {
						if (sm.getStatut().equals("F")) {
							// alors on edite EDIT_SVM-1
							smFonctionnaireAImprimer.add(sm);
						} else if (sm.getStatut().equals("CC")) {
							// alors on edite EDIT_SVM-2
							smCCAImprimer.add(sm);
						}
						sm.setEtat(EnumEtatSuiviMed.CONVOQUE.getValue());
						getSuiviMedDao().modifierSuiviMedicalTravail(
								sm.getIdSuiviMed(), sm);
						nbConvocImpr++;
					} else {
						// cas vide on ne traite pas
						// mais on informe l'utilisateur que certaines lignes
						// n'ont pu �tre imprim�es
						// on met l'agent dans une variable et on affiche cette
						// liste � l'ecran
						convocationsEnErreur += sm.getAgent() + " ("
								+ sm.getNomatr() + "); ";
					}
				}
			}
		}

		// si aucune convocation selectionn�e
		// RG_SVM-10.1
		if (nbConvocImpr == 0) {
			// "INF401",
			// "Attention, aucune convocation n'est s�lectionn�e cet impression.");
			getTransaction().declarerErreur(MessageUtils.getMessage("INF401"));
			return false;
		} else {
			// on remet la liste � vide afin qu'elle soit initialis�e avec les
			// nouvelles valeurs
			setListeSuiviMed(null);
			setListeDocuments(null);
		}

		// on imprime les 2 listes
		String repModeles = (String) ServletAgent.getMesParametres().get(
				"REPERTOIRE_MODELES_SM");

		if (smFonctionnaireAImprimer.size() > 0) {
			String destination = repPartage + "SuiviMedical/SM_Convocation_F_"
					+ getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".xml";
			creerModeleDocumentSVM1(smFonctionnaireAImprimer, repModeles,
					destination);
		}
		if (smCCAImprimer.size() > 0) {
			String destination = repPartage + "SuiviMedical/SM_Convocation_CC_"
					+ getMoisSelectionne(indiceMois) + "_"
					+ getAnneeSelectionne(indiceMois) + ".xml";
			creerModeleDocumentSVM2(smCCAImprimer, repModeles, destination);
		}
		return true;
	}

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie d�j� que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get(
				"REPERTOIRE_ACTES");

		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
	}

	private void creerModeleDocumentSVM5(ArrayList<SuiviMedical> smCCAImprimer,
			String modele, String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("SuiviMedical");

		FileSystemManager fsManager = VFS.getManager();

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		// on lit le fichier debut pour demarrer l'ecriture
		// LECTURE
		FileObject foDebut = fsManager.resolveFile(modele + "debut_SM5.xml");
		InputStream isDebut = foDebut.getContent().getInputStream();
		InputStreamReader inRDebut = new InputStreamReader(isDebut, "UTF8");
		BufferedReader inDebut = new BufferedReader(inRDebut);
		String ligneDebut;
		while ((ligneDebut = inDebut.readLine()) != null) {
			out.write(ligneDebut);
		}
		// FERMETURE DES FLUX
		inDebut.close();
		inRDebut.close();
		isDebut.close();
		foDebut.close();

		for (int i = 0; i < smCCAImprimer.size(); i++) {
			SuiviMedical sm = smCCAImprimer.get(i);
			// on recupere l'agent concern� pour connaitre sa civilit�
			// RG-SVM-30
			AgentNW agentSelectionne = AgentNW.chercherAgent(getTransaction(),
					sm.getIdAgent().toString());
			String nomPrenom = Const.CHAINE_VIDE;
			if (agentSelectionne != null
					&& agentSelectionne.getIdAgent() != null) {
				ArrayList<String> listePrenomAgent = new ArrayList<String>();
				String prenom = agentSelectionne.getPrenomAgent();
				if (prenom.contains("-")) {
					StringTokenizer st = new StringTokenizer(prenom, "-");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase();
						if (k != listePrenomAgent.size() - 1) {
							nomPrenom += "-";
						}
					}
				} else if (prenom.contains(" ")) {
					StringTokenizer st = new StringTokenizer(prenom, " ");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase() + " ";
					}
				} else {
					nomPrenom = prenom.substring(0, 1).toUpperCase()
							+ prenom.substring(1, prenom.length())
									.toLowerCase();
				}
				nomPrenom += " " + agentSelectionne.getNomAgent();
			} else {
				nomPrenom = sm.getAgent();
			}
			String agent = agentSelectionne.getSexe() != null ? agentSelectionne
					.getSexe().equals("M") ? "Monsieur " + nomPrenom
					: "Madame " + nomPrenom : Const.CHAINE_VIDE;

			// on recupere le rendez-vous
			String rendezVous = "Sans Rendez-vous";
			if (sm.getDateProchaineVisite() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEEE dd MMMM yyyy", Locale.FRENCH);
				rendezVous = "le " + sdf.format(sm.getDateProchaineVisite());
				rendezVous = rendezVous + " � "
						+ sm.getHeureProchaineVisite().replace(":", "h");
			}

			// on recupere le responsable
			// RG-SVM-26
			String responsable = "Sans Responsable";
			String serviceResponsable = "Sans Responsable";
			if (sm.getIdServi() != null
					&& !sm.getIdServi().equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(),
						sm.getIdServi());
				if (!getTransaction().isErreur()) {
					// si l'agent est chef de service alors on ne le prend pas
					// en compte
					if (serv.getCodService().endsWith("AA")) {
						continue;
					} else {
						String codeServResp = serv.getCodService().substring(0,
								serv.getCodService().length() - 1)
								+ "A";
						Service servResponsable = Service.chercherService(
								getTransaction(), codeServResp);
						responsable = servResponsable.getSignature();
						serviceResponsable = servResponsable.getLibService()
								.replace("&", " et ");
					}
				} else {
					getTransaction().traiterErreur();
				}
			}

			// LECTURE
			FileObject fo = fsManager.resolveFile(modele + "milieu_SM5.xml");
			InputStream is = fo.getContent().getInputStream();
			InputStreamReader inR = new InputStreamReader(is, "UTF8");
			BufferedReader in = new BufferedReader(inR);

			String ligne;

			// tant qu'il y a des lignes
			while ((ligne = in.readLine()) != null) {
				// je fais mon traitement
				// statut
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy",
						Locale.FRENCH);
				ligne = StringUtils.replace(ligne, "$_ANNEE", Services
						.dateDuJour().substring(6, 10));
				ligne = StringUtils.replace(ligne, "$_DATE",
						sdf.format(new Date()));
				ligne = StringUtils.replace(ligne, "$RESPONSABLE", responsable);
				ligne = StringUtils.replace(ligne, "$SERVICERESPONSABLE",
						serviceResponsable);
				ligne = StringUtils.replace(ligne, "$_AGENT", agent);
				ligne = StringUtils.replace(ligne, "$_RENDEZ_VOUS", rendezVous);

				out.write(ligne);
			}

			// saut de page sauf pour le dernier
			if (i < smCCAImprimer.size() - 1) {
				out.write("<w:br w:type=\"page\"/>");
			}

			// FERMETURE DES FLUX
			in.close();
			inR.close();
			is.close();
			fo.close();
		}

		// on lit le fichier fin pour finir l'ecriture
		// LECTURE
		FileObject foFin = fsManager.resolveFile(modele + "fin_SM5.xml");
		InputStream isFin = foFin.getContent().getInputStream();
		InputStreamReader inRFin = new InputStreamReader(isFin, "UTF8");
		BufferedReader inFin = new BufferedReader(inRFin);
		String ligneFin;
		while ((ligneFin = inFin.readLine()) != null) {
			out.write(ligneFin);
		}
		// FERMETURE DES FLUX
		inFin.close();
		inRFin.close();
		isFin.close();
		foFin.close();

		// FERMETURE DES FLUX

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();
	}

	private void creerModeleDocumentSVM4(
			ArrayList<SuiviMedical> smFonctionnaireAImprimer, String modele,
			String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("SuiviMedical");

		FileSystemManager fsManager = VFS.getManager();

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		// on lit le fichier debut pour demarrer l'ecriture
		// LECTURE
		FileObject foDebut = fsManager.resolveFile(modele + "debut_SM4.xml");
		InputStream isDebut = foDebut.getContent().getInputStream();
		InputStreamReader inRDebut = new InputStreamReader(isDebut, "UTF8");
		BufferedReader inDebut = new BufferedReader(inRDebut);
		String ligneDebut;
		while ((ligneDebut = inDebut.readLine()) != null) {
			out.write(ligneDebut);
		}
		// FERMETURE DES FLUX
		inDebut.close();
		inRDebut.close();
		isDebut.close();
		foDebut.close();

		for (int i = 0; i < smFonctionnaireAImprimer.size(); i++) {
			SuiviMedical sm = smFonctionnaireAImprimer.get(i);
			// on recupere l'agent concern� pour connaitre sa civilit�
			// RG-SVM-27
			AgentNW agentSelectionne = AgentNW.chercherAgent(getTransaction(),
					sm.getIdAgent().toString());
			String nomPrenom = Const.CHAINE_VIDE;
			if (agentSelectionne != null
					&& agentSelectionne.getIdAgent() != null) {
				ArrayList<String> listePrenomAgent = new ArrayList<String>();
				String prenom = agentSelectionne.getPrenomAgent();
				if (prenom.contains("-")) {
					StringTokenizer st = new StringTokenizer(prenom, "-");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase();
						if (k != listePrenomAgent.size() - 1) {
							nomPrenom += "-";
						}
					}
				} else if (prenom.contains(" ")) {
					StringTokenizer st = new StringTokenizer(prenom, " ");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase() + " ";
					}
				} else {
					nomPrenom = prenom.substring(0, 1).toUpperCase()
							+ prenom.substring(1, prenom.length())
									.toLowerCase();
				}
				nomPrenom += " " + agentSelectionne.getNomAgent();
			} else {
				nomPrenom = sm.getAgent();
			}
			String agent = agentSelectionne.getSexe() != null ? agentSelectionne
					.getSexe().equals("M") ? "Monsieur " + nomPrenom
					: "Madame " + nomPrenom : Const.CHAINE_VIDE;

			// on recupere le rendez-vous
			String rendezVous = Const.CHAINE_VIDE;
			if (sm.getDateProchaineVisite() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEEE dd MMMM yyyy", Locale.FRENCH);
				rendezVous = "le " + sdf.format(sm.getDateProchaineVisite());
				rendezVous = rendezVous + " � "
						+ sm.getHeureProchaineVisite().replace(":", "h");
			}

			// on recupere le nb de visites rat�es
			String visitesRatees = Const.CHAINE_VIDE;
			if (sm.getNbVisitesRatees() == 1) {
				visitesRatees = " - 1 �re relance";
			} else if (sm.getNbVisitesRatees() > 1) {
				visitesRatees = " - " + sm.getNbVisitesRatees()
						+ " �me relance";
			}

			// on recupere le motif
			String motif = Const.CHAINE_VIDE;
			if (sm.getIdMotifVM() != null) {
				motif = " - "
						+ getMotifVisiteMedDao().chercherMotif(
								sm.getIdMotifVM()).getLibMotifVM();
			}

			// on recupere le responsable
			// RG-SVM-26
			String responsable = "Sans Responsable";
			String serviceResponsable = "Sans Responsable";
			if (sm.getIdServi() != null
					&& !sm.getIdServi().equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(),
						sm.getIdServi());
				if (!getTransaction().isErreur()) {
					// si l'agent est chef de service alors on ne le prend pas
					// en compte
					if (serv.getCodService().endsWith("AA")) {
						continue;
					} else {
						String codeServResp = serv.getCodService().substring(0,
								serv.getCodService().length() - 1)
								+ "A";
						Service servResponsable = Service.chercherService(
								getTransaction(), codeServResp);
						responsable = servResponsable.getSignature();
						serviceResponsable = servResponsable.getLibService()
								.replace("&", " et ");
					}
				} else {
					getTransaction().traiterErreur();
				}
			}

			// LECTURE
			FileObject fo = fsManager.resolveFile(modele + "milieu_SM4.xml");
			InputStream is = fo.getContent().getInputStream();
			InputStreamReader inR = new InputStreamReader(is, "UTF8");
			BufferedReader in = new BufferedReader(inR);

			String ligne;

			// tant qu'il y a des lignes
			while ((ligne = in.readLine()) != null) {
				// je fais mon traitement
				// statut
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy",
						Locale.FRENCH);
				ligne = StringUtils.replace(ligne, "$_ANNEE", Services
						.dateDuJour().substring(6, 10));
				ligne = StringUtils.replace(ligne, "$_DATE",
						sdf.format(new Date()));
				ligne = StringUtils.replace(ligne, "$RESPONSABLE", responsable);
				ligne = StringUtils.replace(ligne, "$SERVICERESPONSABLE",
						serviceResponsable);
				ligne = StringUtils.replace(ligne, "$_AGENT", agent);
				ligne = StringUtils.replace(ligne, "$_RENDEZ_VOUS", rendezVous);
				ligne = StringUtils.replace(ligne, "$_VISITES_RATEES",
						visitesRatees);
				ligne = StringUtils.replace(ligne, "$_MOTIF", motif);

				out.write(ligne);
			}

			// saut de page sauf pour le dernier
			if (i < smFonctionnaireAImprimer.size() - 1) {
				out.write("<w:br w:type=\"page\"/>");
			}

			// FERMETURE DES FLUX
			in.close();
			inR.close();
			is.close();
			fo.close();
		}
		// on lit le fichier fin pour finir l'ecriture
		// LECTURE
		FileObject foFin = fsManager.resolveFile(modele + "fin_SM4.xml");
		InputStream isFin = foFin.getContent().getInputStream();
		InputStreamReader inRFin = new InputStreamReader(isFin, "UTF8");
		BufferedReader inFin = new BufferedReader(inRFin);
		String ligneFin;
		while ((ligneFin = inFin.readLine()) != null) {
			out.write(ligneFin);
		}
		// FERMETURE DES FLUX
		inFin.close();
		inRFin.close();
		isFin.close();
		foFin.close();

		// FERMETURE DES FLUX

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();
	}

	private void creerModeleDocumentSVM1(
			ArrayList<SuiviMedical> smFonctionnaireAImprimer, String modele,
			String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("SuiviMedical");

		FileSystemManager fsManager = VFS.getManager();

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		// on lit le fichier debut pour demarrer l'ecriture
		// LECTURE
		FileObject foDebut = fsManager.resolveFile(modele + "debut_SM1.xml");
		InputStream isDebut = foDebut.getContent().getInputStream();
		InputStreamReader inRDebut = new InputStreamReader(isDebut, "UTF8");
		BufferedReader inDebut = new BufferedReader(inRDebut);
		String ligneDebut;
		while ((ligneDebut = inDebut.readLine()) != null) {
			out.write(ligneDebut);
		}
		// FERMETURE DES FLUX
		inDebut.close();
		inRDebut.close();
		isDebut.close();
		foDebut.close();

		for (int i = 0; i < smFonctionnaireAImprimer.size(); i++) {
			SuiviMedical sm = smFonctionnaireAImprimer.get(i);
			// on recupere l'agent concern� pour connaitre sa civilit�
			// RG-SVM-22
			AgentNW agentSelectionne = AgentNW.chercherAgent(getTransaction(),
					sm.getIdAgent().toString());
			// on recupere le nb de visites rat�es pour connaitre le titre
			// RG-SVM-21
			String titre = Const.CHAINE_VIDE;
			if (sm.getNbVisitesRatees() == 0) {
				titre = "CONVOCATION";
			} else if (sm.getNbVisitesRatees() == 1) {
				titre = "CONVOCATION - 1 �re relance";
			} else {
				titre = "CONVOCATION - " + sm.getNbVisitesRatees()
						+ " �me relance";
			}
			// on recupere le service
			String service = Const.CHAINE_VIDE;
			if (sm.getIdServi() != null
					&& !sm.getIdServi().equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(),
						sm.getIdServi());
				if (!getTransaction().isErreur()) {
					service = serv.getLibService().replace("&", " et ");
				} else {
					getTransaction().traiterErreur();
				}
			}
			// on recupere le rendez-vous
			String rendezVous = Const.CHAINE_VIDE;
			if (sm.getDateProchaineVisite() != null) {
				// on determine convoqu� en fonction de la civilit�
				rendezVous = agentSelectionne.getSexe() != null ? agentSelectionne
						.getSexe().equals("M") ? "convoqu� " : "convoqu�e "
						: "convoqu� ";
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEEE dd MMMM yyyy", Locale.FRENCH);
				rendezVous = rendezVous + "le "
						+ sdf.format(sm.getDateProchaineVisite());
				rendezVous = rendezVous + " � "
						+ sm.getHeureProchaineVisite().replace(":", "h");
			}
			// on recupere le medecin
			String medecin = Const.CHAINE_VIDE;
			if (sm.getIdMedecin() != null) {
				Medecin m = Medecin.chercherMedecin(getTransaction(), sm
						.getIdMedecin().toString());
				if (!getTransaction().isErreur()) {
					String prenom = m.getPrenomMedecin();
					prenom = prenom.substring(0, 1).toUpperCase()
							+ prenom.substring(1, prenom.length())
									.toLowerCase();
					String titreMedecin = m.getTitreMedecin();
					titreMedecin = !titreMedecin.equals(Const.CHAINE_VIDE) ? titreMedecin
							.substring(0, 1).toUpperCase()
							+ titreMedecin.substring(1, titreMedecin.length())
									.toLowerCase() : Const.CHAINE_VIDE;
					medecin = titreMedecin + " " + prenom + " "
							+ m.getNomMedecin();
				} else {
					getTransaction().traiterErreur();
				}
			}
			String nomPrenom = Const.CHAINE_VIDE;
			if (agentSelectionne != null
					&& agentSelectionne.getIdAgent() != null) {
				ArrayList<String> listePrenomAgent = new ArrayList<String>();
				String prenom = agentSelectionne.getPrenomAgent();
				if (prenom.contains("-")) {
					StringTokenizer st = new StringTokenizer(prenom, "-");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase();
						if (k != listePrenomAgent.size() - 1) {
							nomPrenom += "-";
						}
					}
				} else if (prenom.contains(" ")) {
					StringTokenizer st = new StringTokenizer(prenom, " ");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase() + " ";
					}
				} else {
					nomPrenom = prenom.substring(0, 1).toUpperCase()
							+ prenom.substring(1, prenom.length())
									.toLowerCase();
				}
				nomPrenom += " " + agentSelectionne.getNomAgent();
			} else {
				nomPrenom = sm.getAgent();
			}
			String agent = agentSelectionne.getSexe() != null ? agentSelectionne
					.getSexe().equals("M") ? "Monsieur " + nomPrenom
					: "Madame " + nomPrenom : Const.CHAINE_VIDE;

			// LECTURE
			FileObject fo = fsManager.resolveFile(modele + "milieu_SM1.xml");
			InputStream is = fo.getContent().getInputStream();
			InputStreamReader inR = new InputStreamReader(is, "UTF8");
			BufferedReader in = new BufferedReader(inR);

			String ligne;

			// tant qu'il y a des lignes
			while ((ligne = in.readLine()) != null) {
				// je fais mon traitement
				// statut
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy",
						Locale.FRENCH);
				ligne = StringUtils.replace(ligne, "$_ANNEE", Services
						.dateDuJour().substring(6, 10));
				ligne = StringUtils.replace(ligne, "$_DATE",
						sdf.format(new Date()));
				ligne = StringUtils.replace(ligne, "$_TITRE", titre);
				ligne = StringUtils.replace(ligne, "$_AGENT", agent);
				ligne = StringUtils.replace(ligne, "$_FONCTION", service);
				ligne = StringUtils.replace(ligne, "$_RENDEZ_VOUS", rendezVous);
				ligne = StringUtils.replace(ligne, "$_MEDECIN", medecin);

				out.write(ligne);
			}

			// saut de page sauf pour le dernier
			if (i < smFonctionnaireAImprimer.size() - 1) {
				out.write("<w:br w:type=\"page\"/>");
			}

			// FERMETURE DES FLUX
			in.close();
			inR.close();
			is.close();
			fo.close();
		}

		// on lit le fichier fin pour finir l'ecriture
		// LECTURE
		FileObject foFin = fsManager.resolveFile(modele + "fin_SM1.xml");
		InputStream isFin = foFin.getContent().getInputStream();
		InputStreamReader inRFin = new InputStreamReader(isFin, "UTF8");
		BufferedReader inFin = new BufferedReader(inRFin);
		String ligneFin;
		while ((ligneFin = inFin.readLine()) != null) {
			out.write(ligneFin);
		}
		// FERMETURE DES FLUX
		inFin.close();
		inRFin.close();
		isFin.close();
		foFin.close();

		// FERMETURE DES FLUX

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();
	}

	private void creerModeleDocumentSVM2(ArrayList<SuiviMedical> smCCAImprimer,
			String modele, String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("SuiviMedical");

		FileSystemManager fsManager = VFS.getManager();

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		// on lit le fichier debut pour demarrer l'ecriture
		// LECTURE
		FileObject foDebut = fsManager.resolveFile(modele + "debut_SM2.xml");
		InputStream isDebut = foDebut.getContent().getInputStream();
		InputStreamReader inRDebut = new InputStreamReader(isDebut, "UTF8");
		BufferedReader inDebut = new BufferedReader(inRDebut);
		String ligneDebut;
		while ((ligneDebut = inDebut.readLine()) != null) {
			out.write(ligneDebut);
		}
		// FERMETURE DES FLUX
		inDebut.close();
		inRDebut.close();
		isDebut.close();
		foDebut.close();

		for (int i = 0; i < smCCAImprimer.size(); i++) {
			SuiviMedical sm = smCCAImprimer.get(i);
			// on recupere l'agent concern� pour connaitre sa civilit�
			// RG-SVM-24
			AgentNW agentSelectionne = AgentNW.chercherAgent(getTransaction(),
					sm.getIdAgent().toString());
			// on recupere le nb de visites rat�es pour connaitre le titre
			// RG-SVM-23
			String titre = Const.CHAINE_VIDE;
			if (sm.getNbVisitesRatees() == 0) {
				titre = "CONVOCATION";
			} else if (sm.getNbVisitesRatees() == 1) {
				titre = "CONVOCATION - 1 �re relance";
			} else {
				titre = "CONVOCATION - " + sm.getNbVisitesRatees()
						+ " �me relance";
			}
			// on recupere le service
			String service = Const.CHAINE_VIDE;
			if (sm.getIdServi() != null
					&& !sm.getIdServi().equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(),
						sm.getIdServi());
				if (!getTransaction().isErreur()) {
					service = serv.getLibService().replace("&", " et ");
				} else {
					getTransaction().traiterErreur();
				}
			}
			// on recupere le rendez-vous
			String rendezVous = Const.CHAINE_VIDE;
			if (sm.getDateProchaineVisite() != null) {
				// on determine convoqu� en fonction de la civilit�
				rendezVous = agentSelectionne.getSexe() != null ? agentSelectionne
						.getSexe().equals("M") ? "convoqu� " : "convoqu�e "
						: "convoqu� ";
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEEE dd MMMM yyyy", Locale.FRENCH);
				rendezVous = rendezVous + "le "
						+ sdf.format(sm.getDateProchaineVisite());
				rendezVous = rendezVous + " � "
						+ sm.getHeureProchaineVisite().replace(":", "h");
			}
			String nomPrenom = Const.CHAINE_VIDE;
			if (agentSelectionne != null
					&& agentSelectionne.getIdAgent() != null) {
				ArrayList<String> listePrenomAgent = new ArrayList<String>();
				String prenom = agentSelectionne.getPrenomAgent();
				if (prenom.contains("-")) {
					StringTokenizer st = new StringTokenizer(prenom, "-");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase();
						if (k != listePrenomAgent.size() - 1) {
							nomPrenom += "-";
						}
					}
				} else if (prenom.contains(" ")) {
					StringTokenizer st = new StringTokenizer(prenom, " ");
					while (st.hasMoreElements()) {
						listePrenomAgent.add((String) st.nextElement());
					}
					for (int k = 0; k < listePrenomAgent.size(); k++) {
						String prenomAgent = listePrenomAgent.get(k);
						nomPrenom += prenomAgent.substring(0, 1).toUpperCase()
								+ prenomAgent
										.substring(1, prenomAgent.length())
										.toLowerCase() + " ";
					}
				} else {
					nomPrenom = prenom.substring(0, 1).toUpperCase()
							+ prenom.substring(1, prenom.length())
									.toLowerCase();
				}
				nomPrenom += " " + agentSelectionne.getNomAgent();
			} else {
				nomPrenom = sm.getAgent();
			}
			String agent = agentSelectionne.getSexe() != null ? agentSelectionne
					.getSexe().equals("M") ? "Monsieur " + nomPrenom
					: "Madame " + nomPrenom : Const.CHAINE_VIDE;

			// LECTURE
			FileObject fo = fsManager.resolveFile(modele + "milieu_SM2.xml");
			InputStream is = fo.getContent().getInputStream();
			InputStreamReader inR = new InputStreamReader(is, "UTF8");
			BufferedReader in = new BufferedReader(inR);

			String ligne;

			// tant qu'il y a des lignes
			while ((ligne = in.readLine()) != null) {
				// je fais mon traitement
				// statut
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy",
						Locale.FRENCH);
				ligne = StringUtils.replace(ligne, "$_ANNEE", Services
						.dateDuJour().substring(6, 10));
				ligne = StringUtils.replace(ligne, "$_DATE",
						sdf.format(new Date()));
				ligne = StringUtils.replace(ligne, "$_TITRE", titre);
				ligne = StringUtils.replace(ligne, "$_AGENT", agent);
				ligne = StringUtils.replace(ligne, "$_FONCTION", service);
				ligne = StringUtils.replace(ligne, "$_RENDEZ_VOUS", rendezVous);

				out.write(ligne);
			}

			// FERMETURE DES FLUX
			in.close();
			inR.close();
			is.close();
			fo.close();
		}

		// on lit le fichier fin pour finir l'ecriture
		// LECTURE
		FileObject foFin = fsManager.resolveFile(modele + "fin_SM2.xml");
		InputStream isFin = foFin.getContent().getInputStream();
		InputStreamReader inRFin = new InputStreamReader(isFin, "UTF8");
		BufferedReader inFin = new BufferedReader(inRFin);
		String ligneFin;
		while ((ligneFin = inFin.readLine()) != null) {
			out.write(ligneFin);
		}
		// FERMETURE DES FLUX
		inFin.close();
		inRFin.close();
		isFin.close();
		foFin.close();

		// FERMETURE DES FLUX

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();
	}

	private void sauvegardeTableau() throws Exception {
		// on sauvegarde l'�tat du tableau
		for (int i = 0; i < getListeSuiviMed().size(); i++) {
			// on recup�re la ligne concern�e
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(i);
			sm.setEtat(getVAL_ST_ETAT(i));
			if (getVAL_ST_ETAT(i).equals(EnumEtatSuiviMed.TRAVAIL.getValue())) {
				sm.setHeureProchaineVisite(null);
				sm.setDateProchaineVisite(null);
				sm.setIdMedecin(null);
			} else {
				sm.setHeureProchaineVisite(getListeHeureRDV().get(
						Integer.valueOf(getVAL_LB_HEURE_RDV_SELECT(i))));
				String dateProchainRDV = getVAL_ST_DATE_PROCHAIN_RDV(i);
				if (!dateProchainRDV.equals(Const.CHAINE_VIDE)) {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"dd/MM/yyyy");
					Date d = formatter.parse(dateProchainRDV);
					sm.setDateProchaineVisite(d);
				} else {
					sm.setDateProchaineVisite(null);
				}
				Medecin m = getListeMedecin().get(
						Integer.valueOf(getVAL_LB_MEDECIN_SELECT(i)));
				sm.setIdMedecin(Integer.valueOf(m.getIdMedecin()));
			}
			getSuiviMedDao()
					.modifierSuiviMedicalTravail(sm.getIdSuiviMed(), sm);

		}
	}

	public ArrayList<String> getListeDocuments() {
		if (listeDocuments == null)
			return new ArrayList();
		return listeDocuments;
	}

	public void setListeDocuments(ArrayList<String> listeDocuments) {
		this.listeDocuments = listeDocuments;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request,
			int indiceEltAConsulter) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		String docSelection = getListeDocuments().get(indiceEltAConsulter);
		String nomDoc = docSelection.substring(docSelection.lastIndexOf("/"),
				docSelection.length());

		String repertoireStockage = (String) ServletAgent.getMesParametres()
				.get("REPERTOIRE_LECTURE");
		setURLFichier(getScriptOuverture(repertoireStockage + "SuiviMedical"
				+ nomDoc));

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer(
				"<script type=\"text/javascript\">");
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
}
