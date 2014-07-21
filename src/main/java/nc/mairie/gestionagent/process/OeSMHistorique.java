package nc.mairie.gestionagent.process;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatSuiviMed;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.hsct.Medecin;
import nc.mairie.metier.hsct.VisiteMedicale;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.suiviMedical.SuiviMedical;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.hsct.MedecinDao;
import nc.mairie.spring.dao.metier.suiviMedical.MotifVisiteMedDao;
import nc.mairie.spring.dao.metier.suiviMedical.SuiviMedicalDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeSMHistorique extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_ANNEE;
	private String[] listeAnnee;
	private String[] LB_MOIS;
	private String[] listeMois;

	private ArrayList<SuiviMedical> listeHistoSuiviMed;

	public String ACTION_RECHERCHE = "Recherche";

	private SuiviMedicalDao suiviMedDao;
	private MotifVisiteMedDao motifVisiteMedDao;
	private MedecinDao medecinDao;

	@Override
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

		// Initialisation de la liste de suivi medicaux
		if (getListeHistoSuiviMed() == null || getListeHistoSuiviMed().size() == 0) {
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer
					.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer
					.parseInt(getVAL_LB_MOIS_SELECT()) : -1);
			setListeHistoSuiviMed(getSuiviMedDao().listerHistoriqueSuiviMedical(getAnneeSelectionne(indiceAnnee),
					getMoisSelectionne(indiceMois), EnumEtatSuiviMed.CONVOQUE.getCode(),
					EnumEtatSuiviMed.ACCOMP.getCode(), EnumEtatSuiviMed.EFFECTUE.getCode()));
			afficheListeHistoSuiviMed();
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getSuiviMedDao() == null)
			setSuiviMedDao(new SuiviMedicalDao((SirhDao) context.getBean("sirhDao")));

		if (getMotifVisiteMedDao() == null)
			setMotifVisiteMedDao(new MotifVisiteMedDao((SirhDao) context.getBean("sirhDao")));

		if (getMedecinDao() == null) {
			setMedecinDao(new MedecinDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void afficheListeHistoSuiviMed() throws ParseException, Exception {
		for (int i = 0; i < getListeHistoSuiviMed().size(); i++) {
			SuiviMedical sm = (SuiviMedical) getListeHistoSuiviMed().get(i);
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

			addZone(getNOM_ST_MOTIF(i), getMotifVisiteMedDao().chercherMotif(sm.getIdMotifVm()).getLibMotifVm());
			// RG-SVM-15
			// si SM effectué alors on prend les infos de la VM
			if (sm.getEtat().equals(EnumEtatSuiviMed.EFFECTUE.getCode())) {
				VisiteMedicale vm = VisiteMedicale.chercherVisiteMedicaleLieeSM(getTransaction(), sm.getIdSuiviMed()
						.toString(), sm.getIdAgent().toString());
				Medecin medecin = null;
				if (vm.getIdMedecin() != null) {
					medecin = getMedecinDao().chercherMedecin(Integer.valueOf(vm.getIdMedecin()));
				}
				addZone(getNOM_ST_MEDECIN(i),
						medecin != null ? medecin.getTitreMedecin() + " " + medecin.getPrenomMedecin() + " "
								+ medecin.getNomMedecin() : Const.CHAINE_VIDE);
				addZone(getNOM_ST_DATE_RDV(i),
						vm.getDateDerniereVisite() == null ? Const.CHAINE_VIDE : vm.getDateDerniereVisite());
			} else {
				Medecin medecin = getMedecinDao().chercherMedecin(sm.getIdMedecin());
				addZone(getNOM_ST_MEDECIN(i),
						sm.getIdMedecin() != null ? medecin.getTitreMedecin() + " " + medecin.getPrenomMedecin() + " "
								+ medecin.getNomMedecin() : Const.CHAINE_VIDE);
				addZone(getNOM_ST_DATE_RDV(i),
						sm.getDateProchaineVisite() == null ? Const.CHAINE_VIDE : Services.convertitDate(sm
								.getDateProchaineVisite().toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
			}
			addZone(getNOM_ST_HEURE_RDV(i), sm.getHeureProchaineVisite() != null ? sm.getHeureProchaineVisite()
					: Const.CHAINE_VIDE);
			// on cherche si il y a une VM
			if (sm.getEtat().equals(EnumEtatSuiviMed.EFFECTUE.getCode())) {
				VisiteMedicale vm = VisiteMedicale.chercherVisiteMedicaleLieeSM(getTransaction(), sm.getIdSuiviMed()
						.toString(), sm.getIdAgent().toString());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				if (vm != null && vm.getIdVisite() != null) {
					addZone(getNOM_ST_AVIS(i), vm.getApte() == null ? "&nbsp;" : vm.getApte().equals("1") ? "Apte"
							: "Inapte");
				} else {
					addZone(getNOM_ST_AVIS(i), "&nbsp;");
				}
			} else {
				addZone(getNOM_ST_AVIS(i), "&nbsp;");
			}
			addZone(getNOM_ST_EFFECTUE(i), sm.getEtat().equals(EnumEtatSuiviMed.EFFECTUE.getCode()) ? "Oui" : "Non");
		}
	}

	private Integer getAnneeSelectionne(int indiceAnnee) throws ParseException {
		if (getListeAnnee() != null && getListeAnnee().length > 0 && indiceAnnee != -1) {
			return Integer.valueOf(getListeAnnee()[indiceAnnee]);
		} else {
			return 0;
		}
	}

	private Integer getMoisSelectionne(int indiceMois) throws ParseException {
		if (getListeMois() != null && getListeMois().length > 0 && indiceMois != -1) {
			return indiceMois + 1;
		} else {
			return 0;
		}
	}

	/**
	 * Initialisation des liste déroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = Services.dateDuJour().substring(6, 10);
			setListeAnnee(new String[6]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));
			getListeAnnee()[1] = String.valueOf(Integer.parseInt(anneeCourante) - 1);
			getListeAnnee()[2] = String.valueOf(Integer.parseInt(anneeCourante) - 2);
			getListeAnnee()[3] = String.valueOf(Integer.parseInt(anneeCourante) - 3);
			getListeAnnee()[4] = String.valueOf(Integer.parseInt(anneeCourante) - 4);
			getListeAnnee()[5] = String.valueOf(Integer.parseInt(anneeCourante) - 5);

			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
		// Si liste mois vide alors affectation
		if (getLB_MOIS() == LBVide) {
			Integer moisCourant = Integer.parseInt(Services.dateDuJour().substring(3, 5)) - 1;
			DateFormatSymbols dfsFR = new DateFormatSymbols(Locale.FRENCH);
			String[] moisAnneeFR = dfsFR.getMonths();
			int j = 0;
			int tailleTotal = 12;
			setListeMois(new String[tailleTotal]);

			for (int i = 0; i < moisAnneeFR.length - 1; i++) {
				getListeMois()[j] = moisAnneeFR[i];
				j++;
			}

			setLB_MOIS(getListeMois());
			addZone(getNOM_LB_MOIS_SELECT(), moisCourant.toString());
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	@Override
	public String getJSP() {
		return "OeSMHistorique.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SM-HISTORIQUE";
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

		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT())
				: -1);
		if (indiceAnnee == -1) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			return false;
		}

		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_SELECT()) ? Integer.parseInt(getVAL_LB_MOIS_SELECT())
				: -1);
		if (indiceMois == -1) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}
		// on remet la liste à null afin qu'elle soit initialiser dans la
		// methode initializeZone
		setListeHistoSuiviMed(null);

		return true;
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
	private void setLB_ANNEE(String[] listeAnnees) {
		LB_ANNEE = listeAnnees;
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
	 * NOM_LB_MOIS_SELECT Date de création : (28/11/11)
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
	 * @return listeAnnées
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des années possibles.
	 * 
	 * @param strings
	 */
	private void setListeAnnee(String[] strings) {
		this.listeAnnee = strings;
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
	private void setLB_MOIS(String[] listeMOISs) {
		LB_MOIS = listeMOISs;
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
	 * Setter de la liste des mois possibles.
	 * 
	 * @param strings
	 */
	private void setListeMois(String[] strings) {
		this.listeMois = strings;
	}

	public ArrayList<SuiviMedical> getListeHistoSuiviMed() {
		if (listeHistoSuiviMed == null)
			return new ArrayList<SuiviMedical>();
		return listeHistoSuiviMed;
	}

	public void setListeHistoSuiviMed(ArrayList<SuiviMedical> listeHistoSuiviMed) {
		this.listeHistoSuiviMed = listeHistoSuiviMed;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_MEDECIN Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MEDECIN(int i) {
		return "NOM_ST_MEDECIN_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MEDECIN Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MEDECIN(int i) {
		return getZone(getNOM_ST_MEDECIN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_RDV Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_RDV(int i) {
		return "NOM_ST_DATE_RDV_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_RDV Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_RDV(int i) {
		return getZone(getNOM_ST_DATE_RDV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_HEURE_RDV Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_HEURE_RDV(int i) {
		return "NOM_ST_HEURE_RDV_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_HEURE_RDV Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_HEURE_RDV(int i) {
		return getZone(getNOM_ST_HEURE_RDV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AVIS(int i) {
		return "NOM_ST_AVIS_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AVIS(int i) {
		return getZone(getNOM_ST_AVIS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EFFECTUE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_EFFECTUE(int i) {
		return "NOM_ST_EFFECTUE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EFFECTUE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_EFFECTUE(int i) {
		return getZone(getNOM_ST_EFFECTUE(i));
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

	public MedecinDao getMedecinDao() {
		return medecinDao;
	}

	public void setMedecinDao(MedecinDao medecinDao) {
		this.medecinDao = medecinDao;
	}
}
