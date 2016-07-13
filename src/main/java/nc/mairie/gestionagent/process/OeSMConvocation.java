package nc.mairie.gestionagent.process;

import java.io.BufferedOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.enums.EnumMotifVisiteMed;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.hsct.RecommandationDao;
import nc.mairie.spring.dao.metier.hsct.SPABSENDao;
import nc.mairie.spring.dao.metier.hsct.VisiteMedicaleDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.suiviMedical.MotifVisiteMedDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.ISirhService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeSMConvocation extends BasicProcess {

	/**
	 * 
	 */
	private static final long			serialVersionUID						= 1L;
	private String[]					LB_MEDECIN;
	private String[]					LB_HEURE_RDV;
	private String[]					LB_STATUT;

	private ArrayList<SuiviMedical>		listeSuiviMed;
	private ArrayList<String>			listeDocuments;

	private ArrayList<Medecin>			listeMedecin;
	private Hashtable<Integer, Medecin>	hashMedecin;

	private ArrayList<String>			listeHeureRDV;

	public String						ACTION_CALCUL							= "Calcul";
	public String						ACTION_RECHERCHE						= "Recherche";
	public String						ACTION_MODIFICATION						= "Modification";
	public String						ACTION_SUPPRESSION						= "Suppression";

	private Logger						logger									= LoggerFactory.getLogger(OeSMConvocation.class);

	public String						convocationsEnErreur					= Const.CHAINE_VIDE;
	private String						urlFichier;

	private SuiviMedicalDao				suiviMedDao;
	private MotifVisiteMedDao			motifVisiteMedDao;
	private SPABSENDao					spabsenDao;
	private MedecinDao					medecinDao;
	private FichePosteDao				fichePosteDao;
	private AffectationDao				affectationDao;
	private AgentDao					agentDao;
	private RecommandationDao			recommandationDao;
	private VisiteMedicaleDao visiteMedicaleDao;

	private IAdsService					adsService;

	private ISirhService				sirhService;

	public static final int				STATUT_RECHERCHER_AGENT					= 1;
	public static final int				STATUT_RECHERCHER_AGENT_HIERARCHIQUE	= 2;
	private ArrayList<String>			listeStatut;

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

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
		} else if (etatStatut() == STATUT_RECHERCHER_AGENT_HIERARCHIQUE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT_HIERARCHIQUE(), agt.getNomatr().toString());
		}
		// Initialisation de la liste des documents suivi medicaux
		if (getListeDocuments() == null || getListeDocuments().size() == 0) {
			setListeDocuments(listerDocumentsSM());
			afficheListeDocuments();

		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
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
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRecommandationDao() == null) {
			setRecommandationDao(new RecommandationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getVisiteMedicaleDao() == null) {
			setVisiteMedicaleDao(new VisiteMedicaleDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
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
//		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
//		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
//		String docuConvocF = repPartage + "SuiviMedical/SM_Convocation_F_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//		String docuConvocCC = repPartage + "SuiviMedical/SM_Convocation_CC_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//		String docuAccompagnementF = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_F_" + getMoisSelectionne(indiceMois) + "_"
//				+ getAnneeSelectionne(indiceMois) + ".doc";
//		String docuAccompagnementCC = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_CC_" + getMoisSelectionne(indiceMois) + "_"
//				+ getAnneeSelectionne(indiceMois) + ".doc";
//
//		// on verifie l'existance de chaque fichier
//		boolean existsConvocF = new File(docuConvocF).exists();
//		if (existsConvocF) {
//			res.add(docuConvocF);
//		}
//		boolean existsConvocCC = new File(docuConvocCC).exists();
//		if (existsConvocCC) {
//			res.add(docuConvocCC);
//		}
//		boolean existsAccompF = new File(docuAccompagnementF).exists();
//		if (existsAccompF) {
//			res.add(docuAccompagnementF);
//		}
//		boolean existsAcompCC = new File(docuAccompagnementCC).exists();
//		if (existsAcompCC) {
//			res.add(docuAccompagnementCC);
//		}
		return res;
	}

	private void afficheListeSuiviMed() throws ParseException, Exception {
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			Agent agent = getAgentDao().chercherAgent(sm.getIdAgent());
			addZone(getNOM_ST_NUM_SM(i), sm.getIdSuiviMed().toString());
			addZone(getNOM_ST_MATR(i), sm.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), sm.getAgent());
			addZone(getNOM_ST_NUM_CAFAT(i), agent.getNumCafat() == null ? Const.CHAINE_VIDE : agent.getNumCafat().trim());
			addZone(getNOM_ST_STATUT(i), sm.getStatut());
			// #16233
			EntiteDto serv = adsService.getEntiteByIdEntite(sm.getIdServiceAds());
			EntiteDto direction = adsService.getAffichageDirection(sm.getIdServiceAds());
			addZone(getNOM_ST_SERVICE(i), serv == null || serv.getLabel() == null ? "&nbsp;" : serv.getLabel());
			addZone(getNOM_ST_DIRECTION(i), direction != null ? direction.getSigle() : "&nbsp;");
			addZone(getNOM_ST_DATE_DERNIERE_VISITE(i),
					sm.getDateDerniereVisite() == null ? "&nbsp;" : Services.convertitDate(sm.getDateDerniereVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			addZone(getNOM_ST_RESULTAT_DERNIERE_VISITE(i), sm.getIdRecommandationDerniereVisite()==null ? "&nbsp;" : getRecommandationDao().chercherRecommandation(sm.getIdRecommandationDerniereVisite()).getDescRecommandation());
			addZone(getNOM_ST_COMMENTAIRE_DERNIERE_VISITE(i), sm.getCommentaireDerniereViste()==null ? "&nbsp;" : sm.getCommentaireDerniereViste());
			addZone(getNOM_ST_DATE_PREVISION_VISITE(i),
					sm.getDatePrevisionVisite() == null ? "&nbsp;" : Services.convertitDate(sm.getDatePrevisionVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			addZone(getNOM_ST_MOTIF(i), getLibMotifVM(sm.getIdMotifVm()));
			addZone(getNOM_ST_NB_VISITES_RATEES(i), sm.getNbVisitesRatees().toString());
			Medecin med = sm.getIdMedecin() != null ? getListeMedecin().get(getListeMedecin().indexOf(getHashMedecin().get(sm.getIdMedecin()))) : null;
			addZone(getNOM_LB_MEDECIN_SELECT(i), med != null ? String.valueOf(med) : Const.ZERO);
			if (sm.getEtat().equals(EnumEtatSuiviMed.EFFECTUE.getCode())) {
				VisiteMedicale vm = null;
				try {
					vm = getVisiteMedicaleDao().chercherVisiteMedicaleLieeSM(sm.getIdSuiviMed(),
						sm.getIdAgent());
				} catch(EmptyResultDataAccessException e) {
					
				}
				Medecin medecin = null;
				if (null != vm && vm.getIdMedecin() != null) {
					medecin = getMedecinDao().chercherMedecin(vm.getIdMedecin());
				}
				addZone(getNOM_ST_MEDECIN(i),
						medecin != null ?  medecin.getPrenomMedecin() + " "
								+ medecin.getNomMedecin() : Const.CHAINE_VIDE);
				addZone(getNOM_ST_DATE_RDV(i),
						 sm.getDateProchaineVisite() == null ? Const.CHAINE_VIDE : Services.convertitDate(sm.getDateProchaineVisite().toString(), "yyyy-MM-dd",
									"dd/MM/yyyy"));
				addZone(getNOM_ST_HEURE_RDV(i),sm.getHeureProchaineVisite());			

			}else{
				addZone(getNOM_ST_MEDECIN(i),Const.CHAINE_VIDE);
				addZone(getNOM_ST_DATE_RDV(i),Const.CHAINE_VIDE);
				addZone(getNOM_ST_HEURE_RDV(i),Const.CHAINE_VIDE);
			}
			addZone(getNOM_ST_DATE_PROCHAIN_RDV(i),
					sm.getDateProchaineVisite() == null ? Const.CHAINE_VIDE : Services.convertitDate(sm.getDateProchaineVisite().toString(), "yyyy-MM-dd",
							"dd/MM/yyyy"));
			if (sm.getHeureProchaineVisite() != null) {
				Integer resHeure = getListeHeureRDV().indexOf(sm.getHeureProchaineVisite());
				addZone(getNOM_LB_HEURE_RDV_SELECT(i), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_RDV_SELECT(i), Const.ZERO);
			}
//			addZone(getNOM_CK_A_IMPRIMER_CONVOC(i),
//					sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getCode()) || sm.getEtat().equals(EnumEtatSuiviMed.ACCOMP.getCode()) ? getCHECKED_ON()
//							: getCHECKED_OFF());
//			addZone(getNOM_CK_A_IMPRIMER_ACCOMP(i), sm.getEtat().equals(EnumEtatSuiviMed.ACCOMP.getCode()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), sm.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())?"&nbsp" : sm.getEtat() );
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
			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}
//			// Si clic sur le bouton PB_IMPRIMER_CONVOCATIONS
//			if (testerParametre(request, getNOM_PB_IMPRIMER_CONVOCATIONS())) {
//				return performPB_IMPRIMER_CONVOCATIONS(request);
//			}
//			// Si clic sur le bouton PB_IMPRIMER_LISTE_VISITE
//			if (testerParametre(request, getNOM_PB_IMPRIMER_LISTE_VISITE())) {
//				return performPB_IMPRIMER_LISTE_VISITE(request);
//			}
//			// Si clic sur le bouton PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS
//			if (testerParametre(request, getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS())) {
//				return performPB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS(request);
//			}

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

			// Si clic sur le bouton PB_RECHERCHER_AGENT_HIERARCHIQUE
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_HIERARCHIQUE())) {
				return performPB_RECHERCHER_AGENT_HIERARCHIQUE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi médical.
	 */
	private void initialiseListeDeroulante() throws Exception {

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
			int diffFinDeb = 9 * 60; // difference en minute entre le début et
										// la
										// fin
			int interval = 15; // interval en minute

			SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm"); // format
																			// de
																			// la
																			// date

			GregorianCalendar deb = new GregorianCalendar();
			deb.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
			if (heureDeb > 11) // gestion AM PM
				deb.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);
			else
				deb.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
			deb.set(GregorianCalendar.HOUR, heureDeb % 12);
			deb.set(GregorianCalendar.MINUTE, minuteDeb);

			GregorianCalendar fin = (GregorianCalendar) deb.clone();
			fin.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
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
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SM-CONVOCATION";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	private boolean performControlerFiltres() throws Exception {
		String dateDeb = getVAL_ST_DATE_MIN();
		if (dateDeb.equals(Const.CHAINE_VIDE)) {
			// "ERR500",
			// "Le champ date de début est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR500"));
			return false;
		}

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		// Mise à jour de l'action menee
		addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);

		if (!performControlerFiltres()) {
			setListeSuiviMed(null);
			setListeDocuments(null);
			return false;
		}

		// récupération dates
		String dateDeb = getVAL_ST_DATE_MIN();
		Date dateDebut = new SimpleDateFormat("dd/MM/yyyy").parse(dateDeb);

		String dateF = getVAL_ST_DATE_MAX();
		Date dateFin = null;
		if (dateF.equals(Const.CHAINE_VIDE)) {
			dateFin = dateDebut;
			addZone(getNOM_ST_DATE_MAX(), getVAL_ST_DATE_MIN());
		} else {
			dateFin = new SimpleDateFormat("dd/MM/yyyy").parse(dateF);
		}

		// recupération statut
		int indiceStatut = (Services.estNumerique(getVAL_LB_STATUT_SELECT()) ? Integer.parseInt(getVAL_LB_STATUT_SELECT()) : -1);
		String statut = Const.CHAINE_VIDE;
		if (indiceStatut > 0) {
			statut = getListeStatut().get(indiceStatut - 1);
		}

		List<Integer> listeAgent = new ArrayList<>();

		// recuperation agent
		if (getVAL_ST_AGENT().length() != 0) {
			Agent agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
			listeAgent.add(agent.getIdAgent());
		}

		// recuperation supérieur hiérarchique
		if (getVAL_ST_AGENT_HIERARCHIQUE().length() != 0) {
			listeAgent = new ArrayList<>();
			Agent agentSuperieur = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT_HIERARCHIQUE()));
			List<AgentDto> agentSousHierarchique = sirhService.getAgentsSubordonnes(agentSuperieur.getIdAgent());
			for (AgentDto dto : agentSousHierarchique) {
				Agent agent = getAgentDao().chercherAgent(dto.getIdAgent());
				listeAgent.add(agent.getIdAgent());
			}
		}

		// recuperation du service
		List<Integer> listeSousService = null;
		if (getVAL_ST_ID_SERVICE_ADS().length() != 0) {
			// #16233 on recupere les sous-service du service selectionne
			listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(getVAL_ST_ID_SERVICE_ADS()));
		}

		// coche CDD
		boolean isCocheCDD = getVAL_CK_AGENT_CDD().equals(getCHECKED_ON());

		// #31345 : on ne cherche plus sur etat/relance/motif
		setListeSuiviMed(getSuiviMedDao().listerSuiviMedicalAvecMoisetAnneeBetweenDate(dateDebut, dateFin, listeAgent, listeSousService, statut,
				isCocheCDD));
		afficheListeSuiviMed();
		// getSuiviMedDao().detruitDao();
		// pour les documents
		setListeDocuments(listerDocumentsSM());
		afficheListeDocuments();

		return true;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_DOC Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_SM Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_SM(int i) {
		return "NOM_ST_NUM_SM_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_SM Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_SM(int i) {
		return getZone(getNOM_ST_NUM_SM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MATR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MATR Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_CAFAT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_CAFAT(int i) {
		return "NOM_ST_NUM_CAFAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_CAFAT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_CAFAT(int i) {
		return getZone(getNOM_ST_NUM_CAFAT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DERNIERE_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DERNIERE_VISITE(int i) {
		return "NOM_ST_DATE_DERNIERE_VISITE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DERNIERE_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DERNIERE_VISITE(int i) {
		return getZone(getNOM_ST_DATE_DERNIERE_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_PREVISION_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_PREVISION_VISITE(int i) {
		return "NOM_ST_DATE_PREVISION_VISITE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_PREVISION_VISITE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_PREVISION_VISITE(int i) {
		return getZone(getNOM_ST_DATE_PREVISION_VISITE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_VISITES_RATEES Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NB_VISITES_RATEES(int i) {
		return "NOM_ST_NB_VISITES_RATEES_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NB_VISITES_RATEES Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NB_VISITES_RATEES(int i) {
		return getZone(getNOM_ST_NB_VISITES_RATEES(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_PROCHAIN_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_PROCHAIN_RDV(int i) {
		return "NOM_ST_DATE_PROCHAIN_RDV_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_PROCHAIN_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_PROCHAIN_RDV(int i) {
		return getZone(getNOM_ST_DATE_PROCHAIN_RDV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETAT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MEDECIN Date de création : (21/11/11 09:55:36)
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
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MEDECIN Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_MEDECIN(int i) {
		return "NOM_LB_MEDECIN_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP : NOM_LB_MEDECIN_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_MEDECIN_SELECT(int i) {
		return "NOM_LB_MEDECIN_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la JSP : LB_MEDECIN Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_MEDECIN(int i) {
		return getLB_MEDECIN(i);
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de la JSP : LB_MEDECIN Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_MEDECIN_SELECT(int i) {
		return getZone(getNOM_LB_MEDECIN_SELECT(i));
	}

//	/**
//	 * Retourne le nom de la case à cocher sélectionnée pour la JSP : CK_A_IMPRIMER_CONVOC Date de création : (21/11/11 09:55:36)
//	 * 
//	 */
//	public String getNOM_CK_A_IMPRIMER_CONVOC(int i) {
//		return "NOM_CK_A_IMPRIMER_CONVOC_" + i;
//	}
//
//	/**
//	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case a cocher : CK_A_IMPRIMER_CONVOC Date de création : (21/11/11 09:55:36)
//	 * 
//	 */
//	public String getVAL_CK_A_IMPRIMER_CONVOC(int i) {
//		return getZone(getNOM_CK_A_IMPRIMER_CONVOC(i));
//	}
//
//	/**
//	 * Retourne le nom de la case à cocher sélectionnée pour la JSP : CK_A_IMPRIMER_ACCOMP Date de création : (21/11/11 09:55:36)
//	 * 
//	 */
//	public String getNOM_CK_A_IMPRIMER_ACCOMP(int i) {
//		return "NOM_CK_A_IMPRIMER_ACCOMP_" + i;
//	}
//
//	/**
//	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case a cocher : CK_A_IMPRIMER_ACCOMP Date de création : (21/11/11 09:55:36)
//	 * 
//	 */
//	public String getVAL_CK_A_IMPRIMER_ACCOMP(int i) {
//		return getZone(getNOM_CK_A_IMPRIMER_ACCOMP(i));
//	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int idSm) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		setStatut(STATUT_MEME_PROCESS);
		
		SuiviMedical sm =  getSuiviMedDao().chercherSuiviMedical(idSm);		
		getListeSuiviMed().get(getListeSuiviMed().indexOf(sm)).setEtat(EnumEtatSuiviMed.PLANIFIE.getCode());

		// on change l'etat juste pour l'affichage
//		addZone(getNOM_ST_ETAT(idSm), EnumEtatSuiviMed.PLANIFIE.getCode());
//		addZone(getVAL_CK_A_IMPRIMER_CONVOC(idSm), Const.CHAINE_VIDE);
//		addZone(getVAL_CK_A_IMPRIMER_ACCOMP(idSm), Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_CA_A08
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int idSm) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		setStatut(STATUT_MEME_PROCESS);
		
		SuiviMedical sm =  getSuiviMedDao().chercherSuiviMedical(idSm);		
		getListeSuiviMed().get(getListeSuiviMed().indexOf(sm)).setEtat(EnumEtatSuiviMed.TRAVAIL.getCode());

		// on change l'etat juste pour l'affichage
//		addZone(getNOM_ST_ETAT(idSm), EnumEtatSuiviMed.TRAVAIL.getCode());
		addZone(getNOM_ST_DATE_PROCHAIN_RDV(idSm), Const.CHAINE_VIDE);
		addZone(getNOM_LB_HEURE_RDV_SELECT(idSm), Const.ZERO);
		addZone(getNOM_LB_MEDECIN_SELECT(idSm), Const.ZERO);
//		addZone(getVAL_CK_A_IMPRIMER_CONVOC(idSm), Const.CHAINE_VIDE);
//		addZone(getVAL_CK_A_IMPRIMER_ACCOMP(idSm), Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		if (!performControlerSaisie()) {
			return false;
		}
		// on sauvegarder l'etat du tableau
		sauvegardeTableau();
		// on remet la liste a vide afin qu'elle soit initialisee avec les
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
			if (!sm.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
				String dateRDV = getVAL_ST_DATE_PROCHAIN_RDV(i);
				String agentConcerne = getVAL_ST_MATR(i) + " ( " + getVAL_ST_AGENT(i) + " ) ";
				// si la date du prochain RDV est vide
				if (dateRDV == null || dateRDV.trim().equals(Const.CHAINE_VIDE)) {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date du prochain RDV pour l'agent " + agentConcerne));
					return false;
				}
				
				// Controle format date du prochain RDV
				if (!Services.estUneDate(dateRDV)) {
					// "ERR301",
					// "La date du prochain RDV est incorrecte pour l'agent @. Elle doit être au format date."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR301", agentConcerne));
					return false;
				} else {
					addZone(getNOM_ST_DATE_PROCHAIN_RDV(i), Services.formateDate(dateRDV));
				}

				// si la date du prochain RDV est inferieur à la date du jour
				if (sm.getEtat().equals(EnumEtatSuiviMed.PLANIFIE.getCode())
						&& Services.compareDates(dateRDV, Services.dateDuJour()) < 0) {
					// "ERR302",
					// "La date du prochain RDV pour l'agent @ doit être supérieure ou egale à la date du jour"
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
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de création : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de création : (12/09/11 11:49:01)
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
	 * Getter de la liste avec un lazy initialize : LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
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
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_RDV(int i) {
		return "NOM_LB_HEURE_RDV_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP : NOM_LB_HEURE_RDV_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_RDV_SELECT(int i) {
		return "NOM_LB_HEURE_RDV_" + i + "_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la JSP : LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_RDV(int i) {
		return getLB_HEURE_RDV(i);
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de la JSP : LB_HEURE_RDV Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_RDV_SELECT(int i) {
		return getZone(getNOM_LB_HEURE_RDV_SELECT(i));
	}

//	public String getNOM_PB_IMPRIMER_LISTE_VISITE() {
//		return "NOM_PB_IMPRIMER_LISTE_VISITE";
//	}
//
//	public boolean performPB_IMPRIMER_LISTE_VISITE(HttpServletRequest request) throws Exception {
//		performPB_RECHERCHER(request);
//		return true;
//	}
//	
//	public String getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS() {
//		return "NOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS";
//	}
//
//	public boolean performPB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS(HttpServletRequest request) throws Exception {
//		convocationsEnErreur = Const.CHAINE_VIDE;
//		if (!performControlerSaisie()) {
//			return false;
//		}
//		sauvegardeTableau();
//
//		// on supprime les documents existants
//		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
//		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
//
//		String docuAccompagnementF = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_F_" + getMoisSelectionne(indiceMois) + "_"
//				+ getAnneeSelectionne(indiceMois) + ".doc";
//		String docuAccompagnementCC = repPartage + "SuiviMedical/SM_Lettre_Accompagnement_CC_" + getMoisSelectionne(indiceMois) + "_"
//				+ getAnneeSelectionne(indiceMois) + ".doc";
//		// on verifie que les repertoires existent
//		verifieRepertoire("SuiviMedical");
//		// on verifie l'existance de chaque fichier
//		File accompF = new File(docuAccompagnementF.substring(8, docuAccompagnementF.length()));
//		if (accompF.exists()) {
//			accompF.delete();
//		}
//		File accompCC = new File(docuAccompagnementCC.substring(8, docuAccompagnementCC.length()));
//		if (accompCC.exists()) {
//			accompCC.delete();
//		}
//
//		int nbConvocImpr = 0;
//		// on recupere les lignes qui sont cochées pour imprimer
//		ArrayList<Integer> smFonctionnaireAImprimer = new ArrayList<Integer>();
//		ArrayList<Integer> smCCAImprimer = new ArrayList<Integer>();
//		for (int j = 0; j < getListeSuiviMed().size(); j++) {
//			// on recupere la ligne concernée
//			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
//			Integer i = sm.getIdSuiviMed();
//			// si l'etat de la ligne est 'convoque'
//			if (sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getCode())) {
//				if (getVAL_CK_A_IMPRIMER_ACCOMP(i).equals(getCHECKED_ON())) {
//					// RG-SVM-12.4
//					if (sm.getStatut() != null && !sm.getStatut().equals(Const.CHAINE_VIDE)) {
//						if (sm.getStatut().equals("F")) {
//							// alors on edite EDIT_SVM-4
//							smFonctionnaireAImprimer.add(sm.getIdSuiviMed());
//						} else if (sm.getStatut().equals("CC") || sm.getStatut().equals("C")) {
//							// alors on edite EDIT_SVM-5
//							smCCAImprimer.add(sm.getIdSuiviMed());
//						}
//						sm.setEtat(EnumEtatSuiviMed.ACCOMP.getCode());
//						getSuiviMedDao().modifierSuiviMedicalTravail(sm.getIdSuiviMed(), sm);
//						nbConvocImpr++;
//					} else {
//						// cas vide on ne traite pas
//						// mais on informe l'utilisateur que certaines lignes
//						// n'ont pu être imprimees
//						// on met l'agent dans une variable et on affiche cette
//						// liste a l'ecran
//						convocationsEnErreur += sm.getAgent() + " (" + sm.getNomatr() + "); ";
//					}
//				}
//			}
//		}
//
//		// si aucune convocation selectionnée
//		// RG_SVM-12.1
//		if (nbConvocImpr == 0) {
//			// "INF401",
//			// "Attention, aucune convocation n'est sélectionnée pour cette impression."
//			getTransaction().declarerErreur(MessageUtils.getMessage("INF401"));
//			return false;
//		} else {
//			// on remet la liste a vide afin qu'elle soit initialisee avec les
//			// nouvelles valeurs
//			setListeSuiviMed(null);
//			setListeDocuments(null);
//		}
//
//		// on imprime les 2 listes
//		if (smFonctionnaireAImprimer.size() > 0) {
//			String destination = "SuiviMedical/SM_Lettre_Accompagnement_F_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//
//			byte[] fileAsBytes = sirhService.downloadAccompagnement(smFonctionnaireAImprimer.toString().replace("[", "").replace("]", "").replace(" ", ""),
//					"F", getMoisSelectionne(indiceMois).toString(), getAnneeSelectionne(indiceMois).toString());
//
//			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
//				// "ERR185",
//				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
//				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
//				return false;
//			}
//
//		}
//		if (smCCAImprimer.size() > 0) {
//			String destination = "SuiviMedical/SM_Lettre_Accompagnement_CC_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//
//			byte[] fileAsBytes = sirhService.downloadAccompagnement(smCCAImprimer.toString().replace("[", "").replace("]", "").replace(" ", ""), "CC",
//					getMoisSelectionne(indiceMois).toString(), getAnneeSelectionne(indiceMois).toString());
//
//			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
//				// "ERR185",
//				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
//				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
//				return false;
//			}
//		}
//		performPB_RECHERCHER(request);
//		return true;
//	}

//	public String getNOM_PB_IMPRIMER_CONVOCATIONS() {
//		return "NOM_PB_IMPRIMER_CONVOCATIONS";
//	}
//
//	public boolean performPB_IMPRIMER_CONVOCATIONS(HttpServletRequest request) throws Exception {
//		convocationsEnErreur = Const.CHAINE_VIDE;
//		if (!performControlerSaisie()) {
//			return false;
//		}
//		sauvegardeTableau();
//
//		// on supprime les documents existants
//		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
//		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
//
//		String docuConvocF = repPartage + "SuiviMedical/SM_Convocation_F_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//		String docuConvocCC = repPartage + "SuiviMedical/SM_Convocation_CC_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//		// on verifie que les repertoires existent
//		verifieRepertoire("SuiviMedical");
//		// on verifie l'existance de chaque fichier
//		File convocF = new File(docuConvocF.substring(8, docuConvocF.length()));
//		if (convocF.exists()) {
//			convocF.delete();
//		}
//		File convocCC = new File(docuConvocCC.substring(8, docuConvocCC.length()));
//		if (convocCC.exists()) {
//			convocCC.delete();
//		}
//
//		int nbConvocImpr = 0;
//		// on recupere les lignes qui sont cochées pour imprimer
//		ArrayList<Integer> smFonctionnaireAImprimer = new ArrayList<Integer>();
//		ArrayList<Integer> smCCAImprimer = new ArrayList<Integer>();
//		for (int j = 0; j < getListeSuiviMed().size(); j++) {
//			// on recupere la ligne concernée
//			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
//			Integer i = sm.getIdSuiviMed();
//			// si l'etat de la ligne n'est pas deja 'imprime' et que la colonne
//			// imprimee est cochée
//			if (!sm.getEtat().equals(EnumEtatSuiviMed.CONVOQUE.getCode())) {
//				if (getVAL_CK_A_IMPRIMER_CONVOC(i).equals(getCHECKED_ON())) {
//					// RG-SVM-10.3
//					if (sm.getStatut() != null && !sm.getStatut().equals(Const.CHAINE_VIDE)) {
//						if (sm.getStatut().equals("F")) {
//							// alors on edite EDIT_SVM-1
//							smFonctionnaireAImprimer.add(sm.getIdSuiviMed());
//						} else if (sm.getStatut().equals("CC") || sm.getStatut().equals("C")) {
//							// alors on edite EDIT_SVM-2
//							smCCAImprimer.add(sm.getIdSuiviMed());
//						}
//						sm.setEtat(EnumEtatSuiviMed.CONVOQUE.getCode());
//						getSuiviMedDao().modifierSuiviMedicalTravail(sm.getIdSuiviMed(), sm);
//						nbConvocImpr++;
//					} else {
//						// cas vide on ne traite pas
//						// mais on informe l'utilisateur que certaines lignes
//						// n'ont pu être imprimees
//						// on met l'agent dans une variable et on affiche cette
//						// liste a l'ecran
//						convocationsEnErreur += sm.getAgent() + " (" + sm.getNomatr() + "); ";
//					}
//				}
//			}
//		}
//
//		// si aucune convocation selectionnée
//		// RG_SVM-10.1
//		if (nbConvocImpr == 0) {
//			// "INF401",
//			// "Attention, aucune convocation n'est sélectionnée cet impression.");
//			getTransaction().declarerErreur(MessageUtils.getMessage("INF401"));
//			return false;
//		} else {
//			// on remet la liste a vide afin qu'elle soit initialisee avec les
//			// nouvelles valeurs
//			setListeSuiviMed(null);
//			setListeDocuments(null);
//		}
//
//		// on imprime les 2 listes
//		if (smFonctionnaireAImprimer.size() > 0) {
//			String destination = "SuiviMedical/SM_Convocation_F_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//
//			byte[] fileAsBytes = sirhService.downloadConvocation(smFonctionnaireAImprimer.toString().replace("[", "").replace("]", "").replace(" ", ""), "F",
//					getMoisSelectionne(indiceMois).toString(), getAnneeSelectionne(indiceMois).toString());
//
//			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
//				// "ERR185",
//				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
//				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
//				return false;
//			}
//		}
//		if (smCCAImprimer.size() > 0) {
//			String destination = "SuiviMedical/SM_Convocation_CC_" + getMoisSelectionne(indiceMois) + "_" + getAnneeSelectionne(indiceMois) + ".doc";
//
//			byte[] fileAsBytes = sirhService.downloadConvocation(smCCAImprimer.toString().replace("[", "").replace("]", "").replace(" ", ""), "CC",
//					getMoisSelectionne(indiceMois).toString(), getAnneeSelectionne(indiceMois).toString());
//
//			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destination)) {
//				// "ERR185",
//				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
//				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
//				return false;
//			}
//		}
//		performPB_RECHERCHER(request);
//		return true;
//	}

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
			logger.error(String.format("An error occured while writing the report file to the following path  : " + chemin + filename + " : " + e));
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

	private void sauvegardeTableau() throws Exception {
		// on sauvegarde l'etat du tableau
		for (int j = 0; j < getListeSuiviMed().size(); j++) {
			// on recupere la ligne concernée
			SuiviMedical sm = (SuiviMedical) getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
			if (sm.getEtat().equals(EnumEtatSuiviMed.TRAVAIL.getCode())) {
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
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
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
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les regles de gestion du process - Positionne un statut en fonction de ces regles :
	 * setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUT Date de création : (28/11/11)
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
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_STATUT() {
		return "NOM_LB_STATUT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP : NOM_LB_STATUT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_STATUT_SELECT() {
		return "NOM_LB_STATUT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la JSP : LB_STATUT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de la JSP : LB_STATUT Date de création : (28/11/11)
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

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_ST_DATE_MIN() {
		return "NOM_ST_DATE_MIN";
	}

	public String getVAL_ST_DATE_MIN() {
		return getZone(getNOM_ST_DATE_MIN());
	}

	public String getNOM_ST_DATE_MAX() {
		return "NOM_ST_DATE_MAX";
	}

	public String getVAL_ST_DATE_MAX() {
		return getZone(getNOM_ST_DATE_MAX());
	}

	public String getNOM_ST_AGENT_HIERARCHIQUE() {
		return "NOM_ST_AGENT_HIERARCHIQUE";
	}

	public String getVAL_ST_AGENT_HIERARCHIQUE() {
		return getZone(getNOM_ST_AGENT_HIERARCHIQUE());
	}

	public String getNOM_PB_RECHERCHER_AGENT_HIERARCHIQUE() {
		return "NOM_PB_RECHERCHER_AGENT_HIERARCHIQUE";
	}

	public boolean performPB_RECHERCHER_AGENT_HIERARCHIQUE(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT_HIERARCHIQUE, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_HIERARCHIQUE(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_CK_AGENT_CDD() {
		return "NOM_CK_AGENT_CDD";
	}

	public String getVAL_CK_AGENT_CDD() {
		return getZone(getNOM_CK_AGENT_CDD());
	}
	public String getNOM_ST_RESULTAT_DERNIERE_VISITE(int i) {
		return "NOM_ST_RESULTAT_DERNIERE_VISITE_" + i;
	}

	public String getVAL_ST_RESULTAT_DERNIERE_VISITE(int i) {
		return getZone(getNOM_ST_RESULTAT_DERNIERE_VISITE(i));
	}

	public RecommandationDao getRecommandationDao() {
		return recommandationDao;
	}

	public void setRecommandationDao(RecommandationDao recommandationDao) {
		this.recommandationDao = recommandationDao;
	}
	public String getNOM_ST_COMMENTAIRE_DERNIERE_VISITE(int i) {
		return "NOM_ST_COMMENTAIRE_DERNIERE_VISITE_" + i;
	}

	public String getVAL_ST_COMMENTAIRE_DERNIERE_VISITE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE_DERNIERE_VISITE(i));
	}
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}
	public String getNOM_ST_MEDECIN(int i) {
		return "NOM_ST_MEDECIN_" + i;
	}

	public String getVAL_ST_MEDECIN(int i) {
		return getZone(getNOM_ST_MEDECIN(i));
	}
	public String getNOM_ST_HEURE_RDV(int i) {
		return "NOM_ST_HEURE_RDV_" + i;
	}

	public String getVAL_ST_HEURE_RDV(int i) {
		return getZone(getNOM_ST_HEURE_RDV(i));
	}
	public String getNOM_ST_DATE_RDV(int i) {
		return "NOM_ST_DATE_RDV_" + i;
	}

	public String getVAL_ST_DATE_RDV(int i) {
		return getZone(getNOM_ST_DATE_RDV(i));
	}

	public VisiteMedicaleDao getVisiteMedicaleDao() {
		return visiteMedicaleDao;
	}

	public void setVisiteMedicaleDao(VisiteMedicaleDao visiteMedicaleDao) {
		this.visiteMedicaleDao = visiteMedicaleDao;
	}

}
