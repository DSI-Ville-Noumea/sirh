package nc.mairie.gestionagent.process.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumImpressionAffectation;
import nc.mairie.enums.EnumTempsTravail;
import nc.mairie.gestionagent.dto.RefPrimeDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.parametrage.MotifAffectation;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.SpecialiteDiplomeNW;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Budget;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.AvantageNatureAFF;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.DelegationAFF;
import nc.mairie.metier.specificites.RegIndemnAFF;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.dao.metier.specificites.PrimePointageAffDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.domain.metier.specificites.PrimePointageAff;
import nc.mairie.spring.domain.metier.specificites.PrimePointageFP;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTEmploisAffectation Date de création : (04/08/11 15:20:56)
 * 
 */
public class OeAGENTEmploisAffectation extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_HISTORIQUE = 3;
	public static final int STATUT_RECHERCHE_FP = 1;
	public static final int STATUT_RECHERCHE_FP_SECONDAIRE = 4;
	public static final int STATUT_VISU_FP = 2;

	private final String ACTION_AJOUTER_SPEC = "Ajouter";
	public String ACTION_CONSULTATION = "Consultation d'une affectation";
	public String ACTION_CREATION = "Création d'une affectation";

	public String ACTION_IMPRESSION = "Impression des documents liés à une affectation";
	public String ACTION_MODIFICATION = "Modification d'une affectation";
	public String ACTION_SUPPRESSION = "Suppression d'une affectation";

	public final String ACTION_SUPPRIMER_SPEC = "Supprimer";
	private Affectation affectationCourant;
	private AgentNW agentCourant;
	private FichePoste fichePosteCourant;
	private FichePoste fichePosteSecondaireCourant;

	public String focus = null;
	private String[] LB_LISTE_IMPRESSION;
	private String[] LB_MOTIF_AFFECTATION;
	private String[] LB_NATURE_AVANTAGE;

	private String[] LB_RUBRIQUE_AVANTAGE;
	private String[] LB_RUBRIQUE_PRIME_POINTAGE;
	private String[] LB_RUBRIQUE_REGIME;
	private String[] LB_TEMPS_TRAVAIL;
	private String[] LB_TYPE_AVANTAGE;
	private String[] LB_TYPE_DELEGATION;

	private String[] LB_TYPE_REGIME;
	private ArrayList<Affectation> listeAffectation = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageAAjouter = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageAFF = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageASupprimer = new ArrayList<>();
	private ArrayList<AvantageNature> listeAvantageFP = new ArrayList<>();
	private ArrayList<Delegation> listeDelegationAAjouter = new ArrayList<>();

	private ArrayList<Delegation> listeDelegationAFF = new ArrayList<>();
	private ArrayList<Delegation> listeDelegationASupprimer = new ArrayList<>();
	private ArrayList<Delegation> listeDelegationFP = new ArrayList<>();
	private ArrayList<MotifAffectation> listeMotifAffectation = new ArrayList<>();
	private ArrayList<NatureAvantage> listeNatureAvantage = new ArrayList<>();

	private ArrayList<PrimePointageAff> listePrimePointageAFF;
	private ArrayList<PrimePointageAff> listePrimePointageAffAAjouter = new ArrayList<>();
	private ArrayList<PrimePointageAff> listePrimePointageAffASupprimer = new ArrayList<>();
	private ArrayList<PrimePointageFP> listePrimePointageFP = new ArrayList<>();

	private List<RefPrimeDto> listePrimes = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeAAjouter = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeAFF = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeASupprimer = new ArrayList<>();
	private ArrayList<RegimeIndemnitaire> listeRegimeFP = new ArrayList<>();
	private List<Rubrique> listeRubrique = new ArrayList<>();
	private String[] listeTempsTravail;

	private ArrayList<TypeAvantage> listeTypeAvantage = new ArrayList<>();
	private ArrayList<TypeDelegation> listeTypeDelegation = new ArrayList<>();
	private ArrayList<TypeRegIndemn> listeTypeRegIndemn = new ArrayList<>();
	private PrimePointageAffDao primePointageAffDao;
	private PrimePointageFPDao primePointageFPDao;
	public final String SPEC_AVANTAGE_NATURE_SPEC = "avantage en nature";

	public final String SPEC_DELEGATION_SPEC = "délégation";
	public final String SPEC_PRIME_POINTAGE_SPEC = "prime pointage";

	public final String SPEC_REG_INDEMN_SPEC = "régime indemnitaire";
	private String urlFichier;

	/**
	 * Constructeur du process OeAGENTEmploisAffectation. Date de création :
	 * (11/08/11 16:10:24)
	 * 
	 */
	public OeAGENTEmploisAffectation() {
		super();
	}

	private void creerModeleDocument(String modele, String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("NS");

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.resolveFile(modele);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		AgentNW a = getAgentCourant();
		Affectation aff = getAffectationCourant();
		FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
		TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
		Contrat c = Contrat.chercherContratAgentDateComprise(getTransaction(), a.getIdAgent(), Services.dateDuJour());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		}
		Service s = Service.chercherService(getTransaction(), fp.getIdServi());
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), fp.getIdEntiteGeo());

		// on recupere les champs qui nous interessent
		String prenom = a.getPrenomAgent().toLowerCase();
		String premLettre = prenom.substring(0, 1).toUpperCase();
		String restePrenom = prenom.substring(1, prenom.length()).toLowerCase();
		prenom = premLettre + restePrenom;
		String nom = a.getNomAgent().toUpperCase();
		String civilite = a.getCivilite().equals("0") ? "Monsieur" : a.getCivilite().equals("1") ? "Madame"
				: "Mademoiselle";
		String dateDebAffectation = aff.getDateDebutAff();
		String dateFinAffectation = aff.getDateFinAff() == null || aff.getDateFinAff().equals(Const.DATE_NULL) ? "Il n'y a pas de date de fin pour ce contrat !"
				: aff.getDateFinAff();
		String titrePoste = tp.getLibTitrePoste();
		String dureePeriodeEssai = Const.CHAINE_VIDE;
		String dateFinEssai = Const.CHAINE_VIDE;
		if (c != null) {
			if (c.getDateFinPeriodeEssai() == null || c.getDateFinPeriodeEssai().equals(Const.DATE_NULL)) {
				dureePeriodeEssai = "Il n'y a pas de date fin de periode d'essai pour ce contrat !";
			} else {
				dureePeriodeEssai = String.valueOf(Services.compteJoursEntreDates(c.getDateDebut(),
						c.getDateFinPeriodeEssai()));
			}
			dateFinEssai = c.getDateFinPeriodeEssai() == null || c.getDateFinPeriodeEssai().equals(Const.DATE_NULL) ? "Il n'y a pas de date de fin de periode d'essai pour ce contrat !"
					: c.getDateFinPeriodeEssai();
		}
		String interesse = a.getCivilite().equals("0") ? "interesse" : "interessee";
		String nomme = a.getCivilite().equals("0") ? "nomme" : "nommee";
		String affecte = a.getCivilite().equals("0") ? "affecte" : "affectee";
		String libService = s.getLibService();
		String lieuPoste = eg.getLibEntiteGeo();

		// tant qu'il y a des lignes
		while ((ligne = in.readLine()) != null) {
			// je fais mon traitement
			ligne = StringUtils.replace(ligne, "$_DATE_JOUR", new Date().toString());
			ligne = StringUtils.replace(ligne, "$_NOM", nom);
			ligne = StringUtils.replace(ligne, "$_PRENOM", prenom);
			ligne = StringUtils.replace(ligne, "$_CIVILITE", civilite);
			ligne = StringUtils.replace(ligne, "$_DATEDEBAFFECTATION", dateDebAffectation);
			ligne = StringUtils.replace(ligne, "$_DATEFINAFFECTATION", dateFinAffectation);
			ligne = StringUtils.replace(ligne, "$_TITRE_POSTE", titrePoste);
			ligne = StringUtils.replace(ligne, "$_DUREEESSAI", dureePeriodeEssai);
			ligne = StringUtils.replace(ligne, "$_DATEFINESSAI", dateFinEssai);
			ligne = StringUtils.replace(ligne, "$_INTERESSE", interesse);
			ligne = StringUtils.replace(ligne, "$_NOMME", nomme);
			ligne = StringUtils.replace(ligne, "$_AFFECTE", affecte);
			ligne = StringUtils.replace(ligne, "$_LIB_SERVICE", libService);
			ligne = StringUtils.replace(ligne, "$_LIEU_POSTE", lieuPoste);
			out.write(ligne);
		}

		// FERMETURE DES FLUX
		in.close();
		inR.close();
		is.close();
		fo.close();

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();

		destination = destination.substring(destination.lastIndexOf("/"), destination.length());
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		setURLFichier(getScriptOuverture(repertoireStockage + "NS" + destination));
	}

	private void creerModeleDocumentFP(String repertoire, String modele, String destination, String idFichePoste)
			throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire(repertoire);

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.resolveFile(modele);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		AgentNW a = getAgentCourant();
		FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), idFichePoste);
		// Carriere
		String gradeTitulaire = Const.CHAINE_VIDE;
		ArrayList<Carriere> carrieres = Carriere.listerCarriereAvecAgent(getTransaction(), a);
		if (carrieres.size() > 0) {
			Grade grade = Grade.chercherGrade(getTransaction(), carrieres.get(carrieres.size() - 1).getCodeGrade());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			gradeTitulaire += grade.getGrade().replace("°", "eme");

		}
		// on recupère les champs liés à la FP
		FichePoste fpResponsable = FichePoste.chercherFichePoste(getTransaction(), fp.getIdResponsable());
		TitrePoste tpResponsable = TitrePoste.chercherTitrePoste(getTransaction(), fpResponsable.getIdTitrePoste());
		TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), fp.getIdEntiteGeo());
		Service s = Service.chercherService(getTransaction(), fp.getIdServi());
		Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
		String titrePoste = tp.getLibTitrePoste();
		String lieuPoste = eg.getLibEntiteGeo();
		String libService = s.getLibService();
		String missions = fp.getMissions();
		String grade = g.getGrade();
		String responsable = tpResponsable.getLibTitrePoste();
		// Liste Diplomes FP
		String formationRequise = Const.CHAINE_VIDE;
		ArrayList<DiplomeGenerique> dg = DiplomeGenerique.listerDiplomeGeneriqueAvecFP(getTransaction(), fp);
		for (DiplomeGenerique d : dg) {
			formationRequise += d.getLibDiplomeGenerique();
		}
		String tachesPrincipales = Const.CHAINE_VIDE;
		// activites principales
		ArrayList<Activite> lActi = Activite.listerActiviteAvecFP(getTransaction(), fp);
		for (Activite acti : lActi) {
			tachesPrincipales += acti.getNomActivite();
		}
		String budget = fp.getIdBudget() == null ? Const.CHAINE_VIDE : Budget.chercherBudget(getTransaction(),
				fp.getIdBudget()).getLibBudget();
		String reglementaire = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorReg()).getLibHor();
		String budgete = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorBud()).getLibHor();

		// on recupère les champs liés à l'agent
		String prenom = a.getPrenomAgent().toLowerCase();
		String premLettre = prenom.substring(0, 1).toUpperCase();
		String restePrenom = prenom.substring(1, prenom.length()).toLowerCase();
		prenom = premLettre + restePrenom;
		String nom = a.getNomAgent().toUpperCase();
		String civilite = a.getCivilite().equals("0") ? "Monsieur" : a.getCivilite().equals("1") ? "Madame"
				: "Mademoiselle";
		String dateNaiss = a.getCivilite().equals("0") ? "ne le " + a.getDateNaissance() : "nee le "
				+ a.getDateNaissance();
		String embauche = a.getCivilite().equals("0") ? "embauche le " + a.getDateDerniereEmbauche() : "embauchee le "
				+ a.getDateDerniereEmbauche() + ".";
		String titulaire = civilite + " " + prenom + " " + nom + " (" + a.getNoMatricule() + ") " + dateNaiss + " "
				+ embauche;

		// on récupère les diplomes de l'agent
		ArrayList<DiplomeAgent> diplomesAgent = DiplomeAgent.listerDiplomeAgentAvecAgent(getTransaction(), a);
		String listeDiplome = Const.CHAINE_VIDE;
		for (Iterator<DiplomeAgent> iter = diplomesAgent.iterator(); iter.hasNext();) {
			DiplomeAgent da = (DiplomeAgent) iter.next();
			TitreDiplome td = TitreDiplome.chercherTitreDiplome(getTransaction(), da.getIdTitreDiplome());
			SpecialiteDiplomeNW sd = SpecialiteDiplomeNW.chercherSpecialiteDiplomeNW(getTransaction(),
					da.getIdSpecialiteDiplome());
			listeDiplome += td.getLibTitreDiplome() + " " + sd.getLibSpeDiplome() + ",";
		}
		if (!listeDiplome.equals(Const.CHAINE_VIDE)) {
			listeDiplome = listeDiplome.substring(0, listeDiplome.length() - 1);
		}

		// tant qu'il y a des lignes
		while ((ligne = in.readLine()) != null) {
			// je fais mon traitement
			ligne = StringUtils.replace(ligne, "$_NUMERO_FP", fp.getNumFP());
			ligne = StringUtils.replace(ligne, "$_BUDGET_POSTE", budget);
			ligne = StringUtils.replace(ligne, "$_ANNEE", fp.getAnneeCreation());
			ligne = StringUtils.replace(ligne, "$_NFA", fp.getNFA());
			ligne = StringUtils.replace(ligne, "$_OPI", fp.getOPI() == null ? Const.CHAINE_VIDE : fp.getOPI());
			ligne = StringUtils.replace(ligne, "$_REGLEMENTAIRE", reglementaire);
			ligne = StringUtils.replace(ligne, "$_BUDGETE", budgete);
			ligne = StringUtils.replace(ligne, "$_TITRE_POSTE", titrePoste);
			ligne = StringUtils.replace(ligne, "$_LIEU_POSTE", lieuPoste);
			ligne = StringUtils.replace(ligne, "$_SERVICE", libService.replace("&", "et"));
			ligne = StringUtils.replace(ligne, "$_MISSION", missions);
			ligne = StringUtils.replace(ligne, "$_GRADE_POSTE", grade);
			ligne = StringUtils.replace(ligne, "$_RESPONSABLE", responsable);
			ligne = StringUtils.replace(ligne, "$_TITULAIRE", titulaire);
			ligne = StringUtils.replace(ligne, "$_DIPLOMES", listeDiplome);
			ligne = StringUtils.replace(ligne, "$_FORMATION_REQUISE", formationRequise);
			ligne = StringUtils.replace(ligne, "$_TACHES_PRINCIPALES", tachesPrincipales);
			ligne = StringUtils.replace(ligne, "$_GRADE_TITULAIRE", gradeTitulaire);
			ligne = StringUtils.replace(ligne, "$_DATE_JOUR", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

			out.write(ligne);
		}

		// FERMETURE DES FLUX
		in.close();
		inR.close();
		is.close();
		fo.close();

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();

		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		}
	}

	/**
	 * Retourne l'affectation en cours.
	 * 
	 * @return affectationCourant
	 */
	public Affectation getAffectationCourant() {
		return affectationCourant;
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	public String getDefaultFocus() {
		return getNOM_EF_REF_ARRETE();
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return fichePosteCourant
	 */
	private FichePoste getFichePosteCourant() {
		return fichePosteCourant;
	}

	/**
	 * Retourne la fiche de poste secondaire courante.
	 * 
	 * @return fichePosteSecondaireCourant
	 */
	private FichePoste getFichePosteSecondaireCourant() {
		return fichePosteSecondaireCourant;
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 16:45:31)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEmploisAffectation.jsp";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_LISTE_IMPRESSION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	private String[] getLB_LISTE_IMPRESSION() {
		if (LB_LISTE_IMPRESSION == null)
			LB_LISTE_IMPRESSION = initialiseLazyLB();
		return LB_LISTE_IMPRESSION;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF_AFFECTATION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	private String[] getLB_MOTIF_AFFECTATION() {
		if (LB_MOTIF_AFFECTATION == null)
			LB_MOTIF_AFFECTATION = initialiseLazyLB();
		return LB_MOTIF_AFFECTATION;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_AVANTAGE() {
		if (LB_RUBRIQUE_AVANTAGE == null)
			LB_RUBRIQUE_AVANTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_PRIME_POINTAGE Date
	 * de création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_PRIME_POINTAGE() {
		if (LB_RUBRIQUE_PRIME_POINTAGE == null)
			LB_RUBRIQUE_PRIME_POINTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_REGIME() {
		if (LB_RUBRIQUE_REGIME == null)
			LB_RUBRIQUE_REGIME = initialiseLazyLB();
		return LB_RUBRIQUE_REGIME;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TEMPS_TRAVAIL Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	private String[] getLB_TEMPS_TRAVAIL() {
		if (LB_TEMPS_TRAVAIL == null)
			LB_TEMPS_TRAVAIL = initialiseLazyLB();
		return LB_TEMPS_TRAVAIL;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION_spec() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_REGIME_spec() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Retourne la liste des affectations de l'agent
	 * 
	 * @return listeAffectation
	 */
	public ArrayList<Affectation> getListeAffectation() {
		return listeAffectation;
	}

	/**
	 * Retourne la liste des avantages en nature à ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	public ArrayList<AvantageNature> getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null)
			listeAvantageAAjouter = new ArrayList<AvantageNature>();
		return listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des AvantageNature de l'affectation.
	 * 
	 * @return listeAvantageAFF
	 */
	public ArrayList<AvantageNature> getListeAvantageAFF() {
		return listeAvantageAFF;
	}

	/**
	 * Retourne la liste des avantages en nature à supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList<AvantageNature> getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null)
			listeAvantageASupprimer = new ArrayList<AvantageNature>();
		return listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des AvantageNature de la fiche de poste.
	 * 
	 * @return listeAvantageFP
	 */
	public ArrayList<AvantageNature> getListeAvantageFP() {
		return listeAvantageFP;
	}

	/**
	 * Retourne la liste des délégations à ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	public ArrayList<Delegation> getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null)
			listeDelegationAAjouter = new ArrayList<Delegation>();
		return listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des Delegation de l'affectation.
	 * 
	 * @return listeDelegationAFF
	 */
	public ArrayList<Delegation> getListeDelegationAFF() {
		return listeDelegationAFF;
	}

	/**
	 * Retourne la liste des délégations à supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList<Delegation> getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null)
			listeDelegationASupprimer = new ArrayList<Delegation>();
		return listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des Delegation de la fiche de poste.
	 * 
	 * @return listeDelegationFP
	 */
	public ArrayList<Delegation> getListeDelegationFP() {
		return listeDelegationFP;
	}

	/**
	 * Retourne la liste des motifs d'affectation.
	 * 
	 * @return listeMotifAffectation
	 */
	private ArrayList<MotifAffectation> getListeMotifAffectation() {
		return listeMotifAffectation;
	}

	/**
	 * Retourne la liste des natures d'avantage en nature.
	 * 
	 * @return listeNatureAvantage
	 */
	private ArrayList<NatureAvantage> getListeNatureAvantage() {
		return listeNatureAvantage;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire de l'affectation.
	 * 
	 * @return listePrimePointageAFF
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAFF() {
		return listePrimePointageAFF;
	}

	/**
	 * Retourne la liste des régimes indemnitaires à ajouter.
	 * 
	 * @return listePrimePointageAAjouter
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAffAAjouter() {
		if (listePrimePointageAffAAjouter == null)
			listePrimePointageAffAAjouter = new ArrayList<PrimePointageAff>();
		return listePrimePointageAffAAjouter;
	}

	/**
	 * Retourne la liste des régimes indemnitaires à supprimer.
	 * 
	 * @return listePrimePointageASupprimer
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAffASupprimer() {
		if (listePrimePointageAffASupprimer == null)
			listePrimePointageAffASupprimer = new ArrayList<PrimePointageAff>();
		return listePrimePointageAffASupprimer;
	}

	/**
	 * Retourne la liste des PrimePointage de la fiche de poste.
	 * 
	 * @return listePrimePointageFP
	 */
	public ArrayList<PrimePointageFP> getListePrimePointageFP() {
		return listePrimePointageFP == null ? new ArrayList<PrimePointageFP>() : listePrimePointageFP;
	}

	/**
	 * Retourne la liste des primes.
	 * 
	 * @return listeRubrique
	 */
	private List<RefPrimeDto> getListePrimes() {
		return listePrimes;
	}

	/**
	 * Retourne la liste des régimes indemnitaires à ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null)
			listeRegimeAAjouter = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire de l'affectation.
	 * 
	 * @return listeRegimeAFF
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegimeAFF() {
		return listeRegimeAFF;
	}

	/**
	 * Retourne la liste des régimes indemnitaires à supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null)
			listeRegimeASupprimer = new ArrayList<RegimeIndemnitaire>();
		return listeRegimeASupprimer;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire de la fiche de poste.
	 * 
	 * @return listeRegimeFP
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegimeFP() {
		return listeRegimeFP;
	}

	/**
	 * Retourne la liste des rubriques.
	 * 
	 * @return listeRubrique
	 */
	private List<Rubrique> getListeRubrique() {
		return listeRubrique;
	}

	public ArrayList<Integer> getListeRubs() {
		ArrayList<Integer> ret = new ArrayList<>();

		if (getListePrimePointageAFF() != null) {
			for (PrimePointageAff p : getListePrimePointageAFF()) {
				ret.add(p.getNumRubrique());
			}
		}
		if (getListePrimePointageAffAAjouter() != null) {
			for (PrimePointageAff p : getListePrimePointageAffAAjouter()) {
				ret.add(p.getNumRubrique());

			}
		}
		return ret;
	}

	/**
	 * Retourne la liste des temps de travail (exprimés en pourcentage).
	 * 
	 * @return listeTempsTravail
	 */
	private String[] getListeTempsTravail() {
		return listeTempsTravail;
	}

	/**
	 * Retourne la liste des types d'avantage en nature.
	 * 
	 * @return listeTypeAvantage
	 */
	private ArrayList<TypeAvantage> getListeTypeAvantage() {
		return listeTypeAvantage;
	}

	/**
	 * Retourne la liste des TypeDelegation.
	 * 
	 * @return listeTypeDelegation
	 */
	private ArrayList<TypeDelegation> getListeTypeDelegation_spec() {
		return listeTypeDelegation;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeTypeRegIndemn
	 */
	private ArrayList<TypeRegIndemn> getListeTypeRegIndemn() {
		return listeTypeRegIndemn;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENT_DELEGATION
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_COMMENT_DELEGATION_spec() {
		return "NOM_EF_COMMENT_DELEGATION_spec";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENTAIRE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE() {
		return "NOM_EF_DATE_ARRETE";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_FORFAIT_REGIME Date
	 * de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_FORFAIT_REGIME_spec() {
		return "NOM_EF_FORFAIT_REGIME_spec";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_AVANTAGE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_MONTANT_AVANTAGE_spec() {
		return "NOM_EF_MONTANT_AVANTAGE_spec";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_POINTS_REGIME
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_NB_POINTS_REGIME_spec() {
		return "NOM_EF_NB_POINTS_REGIME_spec";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_ARRETE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_EF_REF_ARRETE() {
		return "NOM_EF_REF_ARRETE";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_LISTE_IMPRESSION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_LISTE_IMPRESSION() {
		return "NOM_LB_LISTE_IMPRESSION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_LISTE_IMPRESSION_SELECT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_LISTE_IMPRESSION_SELECT() {
		return "NOM_LB_LISTE_IMPRESSION_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF_AFFECTATION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_MOTIF_AFFECTATION() {
		return "NOM_LB_MOTIF_AFFECTATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_AFFECTATION_SELECT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_MOTIF_AFFECTATION_SELECT() {
		return "NOM_LB_MOTIF_AFFECTATION_SELECT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT_spec() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT_spec";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE() {
		return "NOM_LB_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_AVANTAGE_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_AVANTAGE_SELECT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_PRIME_POINTAGE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_PRIME_POINTAGE() {
		return "NOM_LB_RUBRIQUE_PRIME_POINTAGE";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME() {
		return "NOM_LB_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_RUBRIQUE_REGIME_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME_SELECT() {
		return "NOM_LB_RUBRIQUE_REGIME_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TEMPS_TRAVAIL Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_TEMPS_TRAVAIL() {
		return "NOM_LB_TEMPS_TRAVAIL";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TEMPS_TRAVAIL_SELECT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_LB_TEMPS_TRAVAIL_SELECT() {
		return "NOM_LB_TEMPS_TRAVAIL_SELECT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT_spec() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT_spec";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_spec() {
		return "NOM_LB_TYPE_AVANTAGE_spec";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT_spec() {
		return "NOM_LB_TYPE_DELEGATION_SELEC_spec";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_spec() {
		return "NOM_LB_TYPE_DELEGATION_spec";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT_spec() {
		return "NOM_LB_TYPE_REGIME_SELECT_spec";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_spec() {
		return "NOM_LB_TYPE_REGIME_spec";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_AVANTAGE_spec() {
		return "NOM_PB_AJOUTER_AVANTAGE_SPEC";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DELEGATION_spec() {
		return "NOM_PB_AJOUTER_DELEGATION_SPEC";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_PRIME_POINTAGE Date
	 * de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_PRIME_POINTAGE_spec() {
		return "NOM_PB_AJOUTER_PRIME_POINTAGE_SPEC";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_REGIME_spec() {
		return "NOM_PB_AJOUTER_REGIME_SPEC";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_SPECIFICITE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_CHANGER_SPECIFICITE_spec() {
		return "NOM_PB_CHANGER_SPECIFICITE_SPEC";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_HISTORIQUE Date de création
	 * : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_HISTORIQUE() {
		return "NOM_PB_HISTORIQUE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER
	 */
	public String getNOM_PB_IMPRIMER(int i) {
		return "NOM_PB_IMPRIMER" + i;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP Date de
	 * création : (05/08/11 13:35:40)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP() {
		return "NOM_PB_RECHERCHER_FP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FP_SECONDAIRE
	 * Date de création : (05/08/11 13:35:40)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FP_SECONDAIRE() {
		return "NOM_PB_RECHERCHER_FP_SECONDAIRE";
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
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AVANTAGE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AVANTAGE_spec(int i) {
		return "NOM_PB_SUPPRIMER_AVANTAGE_SPEC" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DELEGATION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELEGATION_spec(int i) {
		return "NOM_PB_SUPPRIMER_DELEGATION_SPEC" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_PRIME_POINTAGE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_PRIME_POINTAGE_spec(int i) {
		return "NOM_PB_SUPPRIMER_PRIME_POINTAGE_spec" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REGIME Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REGIME_spec(int i) {
		return "NOM_PB_SUPPRIMER_REGIME_SPEC" + i;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AJOUT Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_VALIDER_AJOUT_spec() {
		return "NOM_PB_VALIDER_AJOUT_spec";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_AN Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_AN_spec() {
		return "NOM_RB_SPECIFICITE_AN_spec";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_D Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_D_spec() {
		return "NOM_RB_SPECIFICITE_D_spec";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_PP Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_PP_spec() {
		return "NOM_RB_SPECIFICITE_PP_spec";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_RI Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_RI_spec() {
		return "NOM_RB_SPECIFICITE_RI_spec";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_SPECIFICITE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RG_SPECIFICITE_spec() {
		return "NOM_RG_SPECIFICITE_spec";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (04/08/11 15:42:46)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_ST_ACTION_spec() {
		return "NOM_ST_ACTION_spec";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIR Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DIR(int i) {
		return "NOM_ST_DIR" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_DIRECTION() {
		return "NOM_ST_DIRECTION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_DIRECTION_SECONDAIRE() {
		return "NOM_ST_DIRECTION_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIEU_FP Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_LIEU_FP() {
		return "NOM_ST_LIEU_FP";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIEU_FP_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_LIEU_FP_SECONDAIRE() {
		return "NOM_ST_LIEU_FP_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_MONTANT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_MONTANT_spec(int i) {
		return "NOM_ST_LST_AVANTAGE_MONTANT_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_NATURE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_NATURE_spec(int i) {
		return "NOM_ST_LST_AVANTAGE_NATURE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_AVANTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_RUBRIQUE_spec(int i) {
		return "NOM_ST_LST_AVANTAGE_RUBRIQUE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_TYPE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_TYPE_spec(int i) {
		return "NOM_ST_LST_AVANTAGE_TYPE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_DELEGATION_COMMENT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_DELEGATION_COMMENT_spec(int i) {
		return "NOM_ST_LST_DELEGATION_COMMENT_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_DELEGATION_TYPE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_DELEGATION_TYPE_spec(int i) {
		return "NOM_ST_LST_DELEGATION_TYPE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_PRIME_POINTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(int i) {
		return "NOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_FORFAIT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_FORFAIT_spec(int i) {
		return "NOM_ST_LST_REGINDEMN_FORFAIT_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_NB_POINTS Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_NB_POINTS_spec(int i) {
		return "NOM_ST_LST_REGINDEMN_NB_POINTS_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_RUBRIQUE_spec(int i) {
		return "NOM_ST_LST_REGINDEMN_RUBRIQUE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_REGINDEMN_TYPE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_TYPE_spec(int i) {
		return "NOM_ST_LST_REGINDEMN_TYPE_spec" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FICHE_POSTE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_NUM_FICHE_POSTE() {
		return "NOM_ST_NUM_FICHE_POSTE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FICHE_POSTE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE() {
		return "NOM_ST_NUM_FICHE_POSTE_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FP Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM_FP(int i) {
		return "NOM_ST_NUM_FP" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERV Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SERV(int i) {
		return "NOM_ST_SERV" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SERVICE_SECONDAIRE() {
		return "NOM_ST_SERVICE_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SPECIFICITE Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_ST_SPECIFICITE_spec() {
		return "NOM_ST_SPECIFICITE_spec";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SUBDIVISION Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SUBDIVISION() {
		return "NOM_ST_SUBDIVISION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_SUBDIVISION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_SUBDIVISION_SECONDAIRE() {
		return "NOM_ST_SUBDIVISION_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TITRE(int i) {
		return "NOM_ST_TITRE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_FP Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TITRE_FP() {
		return "NOM_ST_TITRE_FP";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_FP_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TITRE_FP_SECONDAIRE() {
		return "NOM_ST_TITRE_FP_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_REG Date de
	 * création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TPS_REG() {
		return "NOM_ST_TPS_REG";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_REG_SECONDAIRE
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getNOM_ST_TPS_REG_SECONDAIRE() {
		return "NOM_ST_TPS_REG_SECONDAIRE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_WARNING Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	public String getNomEcran() {
		return "ECR-AG-EMPLOIS-AFFECTATIONS";
	}

	public String getVAL_PB_SET_PRIME_POINTAGE_spec(int i) {
		return getZone(getNOM_PB_SET_PRIME_POINTAGE_spec(i));
	}

	public String getNOM_PB_SET_PRIME_POINTAGE_spec(int i) {
		return "NOM_PB_SET_PRIME_POINTAGE_spec_" + i;
	}

	/***
	 * methods called by jsp
	 * 
	 * @return
	 */
	public String getPosteCourantTitle() {
		return fichePosteCourant.getNumFP();
	}

	public PrimePointageAffDao getPrimePointageAffDao() {
		return primePointageAffDao;
	}

	public PrimePointageFPDao getPrimePointageFPDao() {
		return primePointageFPDao;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENT_DELEGATION Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_COMMENT_DELEGATION_spec() {
		return getZone(getNOM_EF_COMMENT_DELEGATION_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENTAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE() {
		return getZone(getNOM_EF_DATE_ARRETE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_FORFAIT_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_FORFAIT_REGIME_spec() {
		return getZone(getNOM_EF_FORFAIT_REGIME_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_MONTANT_AVANTAGE_spec() {
		return getZone(getNOM_EF_MONTANT_AVANTAGE_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NB_POINTS_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_NB_POINTS_REGIME_spec() {
		return getZone(getNOM_EF_NB_POINTS_REGIME_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_ARRETE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_EF_REF_ARRETE() {
		return getZone(getNOM_EF_REF_ARRETE());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_LISTE_IMPRESSION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_LISTE_IMPRESSION() {
		return getLB_LISTE_IMPRESSION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_LISTE_IMPRESSION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_LISTE_IMPRESSION_SELECT() {
		return getZone(getNOM_LB_LISTE_IMPRESSION_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF_AFFECTATION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_MOTIF_AFFECTATION() {
		return getLB_MOTIF_AFFECTATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF_AFFECTATION Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_MOTIF_AFFECTATION_SELECT() {
		return getZone(getNOM_LB_MOTIF_AFFECTATION_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT_spec() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT_spec());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE_spec() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_AVANTAGE() {
		return getLB_RUBRIQUE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_PRIME_POINTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_PRIME_POINTAGE() {
		return getLB_RUBRIQUE_PRIME_POINTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_REGIME() {
		return getLB_RUBRIQUE_REGIME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_REGIME_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_REGIME_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TEMPS_TRAVAIL Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String[] getVAL_LB_TEMPS_TRAVAIL() {
		return getLB_TEMPS_TRAVAIL();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TEMPS_TRAVAIL Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_LB_TEMPS_TRAVAIL_SELECT() {
		return getZone(getNOM_LB_TEMPS_TRAVAIL_SELECT());
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT_spec() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT_spec());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE_spec() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT_spec() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT_spec());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION_spec() {
		return getLB_TYPE_DELEGATION_spec();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_REGIME_SELECT_spec() {
		return getZone(getNOM_LB_TYPE_REGIME_SELECT_spec());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME_spec() {
		return getLB_TYPE_REGIME_spec();
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_SPECIFICITE Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_RG_SPECIFICITE_spec() {
		return getZone(getNOM_RG_SPECIFICITE_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (04/08/11 15:42:46)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_ST_ACTION_spec() {
		return getZone(getNOM_ST_ACTION_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DIR(int i) {
		return getZone(getNOM_ST_DIR(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_DIRECTION() {
		return getZone(getNOM_ST_DIRECTION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DIRECTION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_DIRECTION_SECONDAIRE() {
		return getZone(getNOM_ST_DIRECTION_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIEU_FP Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_LIEU_FP() {
		return getZone(getNOM_ST_LIEU_FP());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIEU_FP_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_LIEU_FP_SECONDAIRE() {
		return getZone(getNOM_ST_LIEU_FP_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_MONTANT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_MONTANT_spec(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_MONTANT_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_NATURE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_NATURE_spec(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_NATURE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_RUBRIQUE_spec(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_TYPE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_TYPE_spec(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_TYPE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_DELEGATION_COMMENT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_DELEGATION_COMMENT_spec(int i) {
		return getZone(getNOM_ST_LST_DELEGATION_COMMENT_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_DELEGATION_TYPE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_DELEGATION_TYPE_spec(int i) {
		return getZone(getNOM_ST_LST_DELEGATION_TYPE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_PRIME_POINTAGE_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(int i) {
		return getZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_FORFAIT Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_FORFAIT_spec(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_FORFAIT_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_NB_POINTS Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_NB_POINTS_spec(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_NB_POINTS_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_RUBRIQUE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_RUBRIQUE_spec(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_TYPE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_TYPE_spec(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_TYPE_spec(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_FICHE_POSTE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_NUM_FICHE_POSTE() {
		return getZone(getNOM_ST_NUM_FICHE_POSTE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NUM_FICHE_POSTE_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE() {
		return getZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_FP Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM_FP(int i) {
		return getZone(getNOM_ST_NUM_FP(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERV Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERV(int i) {
		return getZone(getNOM_ST_SERV(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_SERVICE_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SERVICE_SECONDAIRE() {
		return getZone(getNOM_ST_SERVICE_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SPECIFICITE
	 * Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_ST_SPECIFICITE() {
		return getZone(getNOM_ST_SPECIFICITE_spec());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SUBDIVISION
	 * Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SUBDIVISION() {
		return getZone(getNOM_ST_SUBDIVISION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_SUBDIVISION_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_SUBDIVISION_SECONDAIRE() {
		return getZone(getNOM_ST_SUBDIVISION_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TITRE(int i) {
		return getZone(getNOM_ST_TITRE(i));
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE_FP Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TITRE_FP() {
		return getZone(getNOM_ST_TITRE_FP());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TITRE_FP_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TITRE_FP_SECONDAIRE() {
		return getZone(getNOM_ST_TITRE_FP_SECONDAIRE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TPS_REG Date
	 * de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TPS_REG() {
		return getZone(getNOM_ST_TPS_REG());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TPS_REG_SECONDAIRE Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public String getVAL_ST_TPS_REG_SECONDAIRE() {
		return getZone(getNOM_ST_TPS_REG_SECONDAIRE());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	private boolean imprimeModele(HttpServletRequest request, String nomDoc) throws Exception {
		Affectation aff = getAffectationCourant();
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destination = "NS/NS_" + aff.getIdAffectation() + "_" + nomDoc.substring(3, nomDoc.length());
		// si le fichier existe alors on supprime l'entrée où il y a le fichier
		// f
		if (verifieExistFichier(aff.getIdAffectation(), nomDoc)) {
			String nomSansExtension = nomDoc.substring(0, nomDoc.indexOf("."));
			Document d = Document.chercherDocumentByContainsNom(getTransaction(), "NS_" + aff.getIdAffectation() + "_"
					+ nomSansExtension.substring(3, nomSansExtension.length()));
			LienDocumentAgent l = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant()
					.getIdAgent(), d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			l.supprimerLienDocumentAgent(getTransaction());
			d.supprimerDocument(getTransaction());
		}

		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_AFFECTATIONS");
		String modele = repModeles + nomDoc;

		// Tout s'est bien passé
		// on crée le document en base de données
		Document d = new Document();
		d.setIdTypeDocument("3");
		d.setLienDocument(destination);
		d.setNomDocument("NS_" + aff.getIdAffectation() + "_" + nomDoc.substring(3, nomDoc.length()));
		d.setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		d.setCommentaire("Document généré par l'application");
		d.creerDocument(getTransaction());

		LienDocumentAgent lda = new LienDocumentAgent();
		lda.setIdAgent(getAgentCourant().getIdAgent());
		lda.setIdDocument(d.getIdDocument());
		lda.creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		creerModeleDocument(modele, repPartage + destination);
		commitTransaction();

		return true;
	}

	/**
	 * Initialise les zones de l'affectation courant RG_AG_AF_A04 RG_AG_AF_A12
	 */
	private boolean initialiseAffectationCourante(HttpServletRequest request) throws Exception {

		if (getAffectationCourant() == null || getAffectationCourant().getIdAffectation() == null) {
			if (getFichePosteCourant() == null || getFichePosteCourant().getIdFichePoste() == null) {
				initialiseAffectationVide();
			}
			// Init Fiche de poste
			// RG_AG_AF_A04
			// RG_AG_AF_A12
			if (etatStatut() == STATUT_RECHERCHE_FP) {
				FichePoste fp = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fp != null) {
					setFichePosteCourant(fp);
					initialiserFichePoste();
				} else if (getFichePosteSecondaireCourant() == null
						&& getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePoste()));
					initialiserFichePoste();
				}
			}
			// Init Fiche de poste secondaire
			if (etatStatut() == STATUT_RECHERCHE_FP_SECONDAIRE) {
				FichePoste fpSecondaire = (FichePoste) VariablesActivite.recuperer(this,
						VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fpSecondaire != null) {
					setFichePosteSecondaireCourant(fpSecondaire);
					initialiserFichePosteSecondaire();
				} else if (getFichePosteSecondaireCourant() == null
						&& getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(),
							getAffectationCourant().getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			}
		} else {
			// Init Fiche de poste
			// RG_AG_AF_A04
			// RG_AG_AF_A12
			if (etatStatut() == STATUT_RECHERCHE_FP) {
				FichePoste fp = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fp != null) {
					setFichePosteCourant(fp);
					initialiserFichePoste();
				} else if (getFichePosteSecondaireCourant() == null
						&& getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePoste()));
					initialiserFichePoste();
				}
			} else {
				if (getAffectationCourant().getIdFichePoste() != null) {
					FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePoste());

					setFichePosteCourant(fp);
					initialiserFichePoste();
				}
			}
			// Init Fiche de poste secondaire
			if (etatStatut() == STATUT_RECHERCHE_FP_SECONDAIRE) {
				FichePoste fpSecondaire = (FichePoste) VariablesActivite.recuperer(this,
						VariablesActivite.ACTIVITE_FICHE_POSTE);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
				if (fpSecondaire != null) {
					setFichePosteSecondaireCourant(fpSecondaire);
					initialiserFichePosteSecondaire();
				} else if (getFichePosteSecondaireCourant() == null
						&& getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(),
							getAffectationCourant().getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			} else {
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(),
							getAffectationCourant().getIdFichePosteSecondaire()));
					initialiserFichePosteSecondaire();
				}
			}

			// Récup du motif d'affectation et temps de travail
			if (getAffectationCourant().getIdMotifAffectation() != null) {
				MotifAffectation ma = (MotifAffectation) MotifAffectation.chercherMotifAffectation(getTransaction(),
						getAffectationCourant().getIdMotifAffectation());
				addZone(getNOM_LB_MOTIF_AFFECTATION_SELECT(), String.valueOf(getListeMotifAffectation().indexOf(ma)));
				for (int i = 0; i < getListeTempsTravail().length; i++) {
					String tpsT = getListeTempsTravail()[i];
					if (tpsT.equals(getAffectationCourant().getTempsTravail()))
						addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), String.valueOf(i));
				}
			}

		}
		return true;
	}

	/**
	 * Initialise à vide les zones de l'affectation.
	 */
	private void initialiseAffectationVide() {
		addZone(getNOM_ST_DIRECTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SUBDIVISION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TPS_REG(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_NUM_FICHE_POSTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TITRE_FP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIEU_FP(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_DIRECTION_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SERVICE_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SUBDIVISION_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TPS_REG_SECONDAIRE(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TITRE_FP_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIEU_FP_SECONDAIRE(), Const.CHAINE_VIDE);

		addZone(getNOM_EF_REF_ARRETE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_ARRETE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		addZone(getNOM_LB_MOTIF_AFFECTATION_SELECT(), Const.ZERO);
		addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), Const.ZERO);

	}

	private void initialiseAvantageNature_spec() throws Exception {
		// Avantages en nature
		if (getListeAvantageFP() == null && getFichePosteCourant() != null
				&& getFichePosteCourant().getIdFichePoste() != null) {
			setListeAvantageFP(AvantageNature.listerAvantageNatureAvecFP(getTransaction(), getFichePosteCourant()
					.getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeAvantageFP().addAll(
						AvantageNature.listerAvantageNatureAvecFP(getTransaction(), getFichePosteSecondaireCourant()
								.getIdFichePoste()));
			}
		}
		if (getListeAvantageAFF() == null && getFichePosteCourant() != null
				&& getFichePosteCourant().getIdFichePoste() != null
				&& getAffectationCourant().getIdAffectation() != null) {
			setListeAvantageAFF(AvantageNature.listerAvantageNatureAvecAFF(getTransaction(), getAffectationCourant()
					.getIdAffectation()));
		}
		int indiceAvNat = 0;
		if (getListeAvantageFP() != null && getListeAvantageFP().size() != 0) {
			for (int i = 0; i < getListeAvantageFP().size(); i++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageFP().get(i);
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage
							.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage
							.chercherNatureAvantage(getTransaction(), aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : Rubrique.chercherRubrique(
							getTransaction(), aAvNat.getNumRubrique());

					addZone(getNOM_ST_LST_AVANTAGE_TYPE_spec(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT_spec(indiceAvNat), aAvNat.getMontant());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE_spec(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE_spec(indiceAvNat), rubr.getLibRubrique());
					indiceAvNat++;
				}
			}
		}
		if (getListeAvantageAFF() != null && getListeAvantageAFF().size() != 0) {
			for (int j = 0; j < getListeAvantageAFF().size(); j++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageAFF().get(j);
				if (aAvNat != null && !getListeAvantageFP().contains(aAvNat)) {
					TypeAvantage typAv = TypeAvantage
							.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage
							.chercherNatureAvantage(getTransaction(), aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : Rubrique.chercherRubrique(
							getTransaction(), aAvNat.getNumRubrique());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE_spec(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT_spec(indiceAvNat), aAvNat.getMontant());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE_spec(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE_spec(indiceAvNat), rubr.getLibRubrique());
					indiceAvNat++;
				}
			}
		}

		if (getListeAvantageAAjouter() != null && getListeAvantageAAjouter().size() != 0) {
			for (int k = 0; k < getListeAvantageAAjouter().size(); k++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageAAjouter().get(k);
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage
							.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage
							.chercherNatureAvantage(getTransaction(), aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : Rubrique.chercherRubrique(
							getTransaction(), aAvNat.getNumRubrique());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE_spec(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT_spec(indiceAvNat), aAvNat.getMontant());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE_spec(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE_spec(indiceAvNat), rubr.getLibRubrique());
					indiceAvNat++;
				}
			}
		}
	}

	private void initialiseDao_spec() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getPrimePointageAffDao() == null) {
			setPrimePointageAffDao((PrimePointageAffDao) context.getBean("primePointageAffDao"));
		}
		if (getPrimePointageFPDao() == null) {
			setPrimePointageFPDao((PrimePointageFPDao) context.getBean("primePointageFPDao"));
		}
	}

	private void initialiseDelegation_spec() throws Exception {
		// Délégations
		if (getListeDelegationFP() == null && getFichePosteCourant() != null
				&& getFichePosteCourant().getIdFichePoste() != null) {
			setListeDelegationFP(Delegation.listerDelegationAvecFP(getTransaction(), getFichePosteCourant()
					.getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeDelegationFP().addAll(
						Delegation.listerDelegationAvecFP(getTransaction(), getFichePosteSecondaireCourant()
								.getIdFichePoste()));
			}
		}
		if (getListeDelegationAFF() == null && getFichePosteCourant() != null
				&& getFichePosteCourant().getIdFichePoste() != null && getAffectationCourant() != null
				&& getAffectationCourant().getIdAffectation() != null) {
			setListeDelegationAFF(Delegation.listerDelegationAvecAFF(getTransaction(), getAffectationCourant()
					.getIdAffectation()));
		}
		int indiceDel = 0;
		if (getListeDelegationFP() != null && getListeDelegationFP().size() != 0) {
			for (int i = 0; i < getListeDelegationFP().size(); i++) {
				Delegation aDel = (Delegation) getListeDelegationFP().get(i);
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(),
							aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE_spec(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT_spec(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}
		if (getListeDelegationAFF() != null && getListeDelegationAFF().size() != 0) {
			for (int j = 0; j < getListeDelegationAFF().size(); j++) {
				Delegation aDel = (Delegation) getListeDelegationAFF().get(j);
				if (aDel != null && !getListeDelegationFP().contains(aDel)) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(),
							aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE_spec(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT_spec(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}
		if (getListeDelegationAAjouter() != null && getListeDelegationAAjouter().size() != 0) {
			for (int k = 0; k < getListeDelegationAAjouter().size(); k++) {
				Delegation aDel = (Delegation) getListeDelegationAAjouter().get(k);
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(),
							aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE_spec(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT_spec(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}

	}

	/**
	 * Initialisation de la liste des affectations. RG_AG_AF_A09
	 */
	private void initialiseListeAffectation(HttpServletRequest request) throws Exception {

		// Recherche des affectations de l'agent
		ArrayList<Affectation> aff = Affectation.listerAffectationAvecAgent(getTransaction(), getAgentCourant());
		setListeAffectation(aff);

		boolean affectationActive = false;

		int indiceAff = 0;
		if (getListeAffectation() != null) {
			for (int i = 0; i < getListeAffectation().size(); i++) {
				Affectation a = (Affectation) getListeAffectation().get(i);
				affectationActive = affectationActive || a.isActive();
				FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), a.getIdFichePoste());
				HistoFichePoste hfp = null;
				ArrayList<HistoFichePoste> listeHistoFP = new ArrayList<HistoFichePoste>();
				if (a.getDateFinAff() != null && !a.getDateFinAff().equals(Const.DATE_NULL)
						&& !a.getDateFinAff().equals(Const.CHAINE_VIDE)) {
					// on cherche la FDP dans histo_fiche_poste
					listeHistoFP = HistoFichePoste.listerHistoFichePosteDansDate(getTransaction(), a.getIdFichePoste(),
							Services.convertitDate(Services.formateDate(a.getDateDebutAff()), "dd/MM/yyyy",
									"yyyy-MM-dd"), Services.convertitDate(Services.formateDate(a.getDateFinAff()),
									"dd/MM/yyyy", "yyyy-MM-dd"));
				}
				// si il n'y en a pas on prend les infos dans la table
				// FICHE_POSTE
				if (listeHistoFP == null || listeHistoFP.size() == 0) {
					fp = FichePoste.chercherFichePoste(getTransaction(), a.getIdFichePoste());
				} else {
					// si il y en a plusieurs on prend la date la plus recente
					hfp = (HistoFichePoste) listeHistoFP.get(0);
				}
				String titreFichePoste = Const.CHAINE_VIDE;
				String numFP = Const.CHAINE_VIDE;
				Service direction = null;
				Service service = null;
				if (hfp != null) {
					titreFichePoste = hfp.getIdTitrePoste() == null ? "&nbsp;" : TitrePoste.chercherTitrePoste(
							getTransaction(), hfp.getIdTitrePoste()).getLibTitrePoste();
					// Service
					direction = Service.getDirection(getTransaction(), hfp.getIdServi());
					service = Service.getSection(getTransaction(), hfp.getIdServi());
					if (service == null)
						service = Service.getDivision(getTransaction(), hfp.getIdServi());
					if (service == null)
						service = Service.getDirection(getTransaction(), hfp.getIdServi());
					if (service == null)
						service = Service.chercherService(getTransaction(), hfp.getIdServi());

					numFP = hfp.getNumFp();
					if (a.getIdFichePosteSecondaire() != null)
						numFP = hfp.getNumFp() + " *";

				} else {
					titreFichePoste = fp.getIdTitrePoste() == null ? "&nbsp;" : TitrePoste.chercherTitrePoste(
							getTransaction(), fp.getIdTitrePoste()).getLibTitrePoste();
					// Service
					direction = Service.getDirection(getTransaction(), fp.getIdServi());
					service = Service.getSection(getTransaction(), fp.getIdServi());
					if (service == null)
						service = Service.getDivision(getTransaction(), fp.getIdServi());
					if (service == null)
						service = Service.getDirection(getTransaction(), fp.getIdServi());
					if (service == null)
						service = Service.chercherService(getTransaction(), fp.getIdServi());

					numFP = fp.getNumFP();
					if (a.getIdFichePosteSecondaire() != null)
						numFP = fp.getNumFP() + " *";

				}

				addZone(getNOM_ST_DIR(indiceAff), direction != null ? direction.getCodService() : "&nbsp;");
				addZone(getNOM_ST_SERV(indiceAff),
						service != null ? service.getLibService() + " ( " + service.getCodService() + " )" : "&nbsp;");
				addZone(getNOM_ST_DATE_DEBUT(indiceAff), a.getDateDebutAff());
				addZone(getNOM_ST_DATE_FIN(indiceAff),
						a.getDateFinAff() == null || a.getDateFinAff().equals(Const.CHAINE_VIDE)
								|| a.getDateFinAff().equals(Const.DATE_NULL) ? "&nbsp;" : a.getDateFinAff());
				addZone(getNOM_ST_NUM_FP(indiceAff), numFP.equals(Const.CHAINE_VIDE) ? "&nbsp;" : numFP);
				addZone(getNOM_ST_TITRE(indiceAff), titreFichePoste.equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: titreFichePoste);

				indiceAff++;
			}
		}

		if (!affectationActive) {
			// Messages informatifs
			// RG_AG_AF_A09
			if (getTransaction().isErreur())
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + "<BR/>"
								+ MessageUtils.getMessage("INF003", getAgentCourant().getNoMatricule()));
			else
				getTransaction().declarerErreur(MessageUtils.getMessage("INF003", getAgentCourant().getNoMatricule()));

			setFocus(getNOM_PB_AJOUTER());
		}
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * 
	 * @throws Exception
	 *             RG_AG_AF_C06 RG_AG_AF_C02
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste motif affectation vide alors affectation
		// RG_AG_AF_C06
		if (getLB_MOTIF_AFFECTATION() == LBVide) {
			ArrayList<MotifAffectation> motifAff = MotifAffectation.listerMotifAffectation(getTransaction());
			setListeMotifAffectation(motifAff);

			int[] tailles = { 30 };
			String[] champs = { "libMotifAffectation" };
			setLB_MOTIF_AFFECTATION(new FormateListe(tailles, motifAff, champs).getListeFormatee());
		}

		// Si liste pourcentages Temps de travail alors affectation
		// RG_AG_AF_C02
		if (getLB_TEMPS_TRAVAIL() == LBVide) {
			setListeTempsTravail(EnumTempsTravail.getValues());
			setLB_TEMPS_TRAVAIL(getListeTempsTravail());
			addZone(getNOM_LB_TEMPS_TRAVAIL_SELECT(), Const.ZERO);
		}
	}

	/**
	 * Initialise les listes déroulantes de l'écran. Date de création :
	 * (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante_spec() throws Exception {
		// Si liste type avantage vide alors affectation
		if (getLB_TYPE_AVANTAGE() == LBVide) {
			ArrayList<TypeAvantage> typeAvantage = TypeAvantage.listerTypeAvantage(getTransaction());
			setListeTypeAvantage(typeAvantage);

			int[] tailles = { 50 };
			String[] champs = { "libTypeAvantage" };
			setLB_TYPE_AVANTAGE(new FormateListe(tailles, typeAvantage, champs).getListeFormatee());
		}

		// Si liste nature avantage vide alors affectation
		if (getLB_NATURE_AVANTAGE() == LBVide) {
			ArrayList<NatureAvantage> natureAvantage = NatureAvantage.listerNatureAvantage(getTransaction());
			NatureAvantage natAvVide = new NatureAvantage();
			natureAvantage.add(0, natAvVide);
			setListeNatureAvantage(natureAvantage);

			int[] tailles = { 50 };
			String[] champs = { "libNatureAvantage" };
			setLB_NATURE_AVANTAGE_spec(new FormateListe(tailles, natureAvantage, champs).getListeFormatee());
		}

		// Si liste rubrique vide alors affectation
		if (getLB_RUBRIQUE_AVANTAGE() == LBVide || getLB_RUBRIQUE_REGIME() == LBVide) {
			ArrayList<Rubrique> rubrique = Rubrique.listerRubrique7000(getTransaction());
			setListeRubrique(rubrique);

			if (getListeRubrique() != null && getListeRubrique().size() != 0) {
				int taillesRub[] = { 68 };
				FormateListe aFormatRub = new FormateListe(taillesRub);
				for (ListIterator<Rubrique> list = getListeRubrique().listIterator(); list.hasNext();) {
					Rubrique aRub = (Rubrique) list.next();
					if (aRub != null) {
						String ligne[] = { aRub.getNumRubrique() + " - " + aRub.getLibRubrique() };
						aFormatRub.ajouteLigne(ligne);
					}
				}
				setLB_RUBRIQUE_AVANTAGE(aFormatRub.getListeFormatee(true));
				setLB_RUBRIQUE_REGIME(aFormatRub.getListeFormatee(true));
			} else {
				setLB_RUBRIQUE_AVANTAGE(null);
				setLB_RUBRIQUE_REGIME(null);
			}
		}

		if (getLB_RUBRIQUE_PRIME_POINTAGE() == LBVide) {
			setListePrimes(initialiseListeDeroulantePrimes_spec());
			if (getListePrimes() != null) {
				String[] content = new String[getListePrimes().size()];
				for (int i = 0; i < getListePrimes().size(); i++) {
					content[i] = getListePrimes().get(i).getNumRubrique() + " - "
							+ getListePrimes().get(i).getLibelle();
				}
				setLB_RUBRIQUE_PRIME_POINTAGE_spec(content);
			}
		}

		// Si liste type délégation vide alors affectation
		if (getLB_TYPE_DELEGATION_spec() == LBVide) {
			ArrayList<TypeDelegation> typeDelegation = TypeDelegation.listerTypeDelegation(getTransaction());
			setListeTypeDelegation_spec(typeDelegation);

			int[] tailles = { 30 };
			String[] champs = { "libTypeDelegation" };
			setLB_TYPE_DELEGATION_spec(new FormateListe(tailles, typeDelegation, champs).getListeFormatee());
		}

		// Si liste type régime vide alors affectation
		if (getLB_TYPE_REGIME_spec() == LBVide) {
			ArrayList<TypeRegIndemn> typeRegime = TypeRegIndemn.listerTypeRegIndemn(getTransaction());
			setListeTypeRegIndemn(typeRegime);
			int[] tailles = { 20 };
			String[] champs = { "libTypeRegIndemn" };
			setLB_TYPE_REGIME_spec(new FormateListe(tailles, typeRegime, champs).getListeFormatee());
		}
	}

	/**
	 * CLV #3264 Initialisation de la liste déroulantes des primes.
	 * 
	 * @throws Exception
	 */
	private List<RefPrimeDto> initialiseListeDeroulantePrimes_spec() throws Exception {
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		List<RefPrimeDto> primes = new ArrayList<RefPrimeDto>();
		if (agentCourant != null) {
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agentCourant);
			try {
				primes = t.getPrimes(carr.getStatutCarriere(carr.getCodeCategorie()));
			} catch (Exception e) {
				// TODO a supprimer quand les pointages seront en prod
			}
		}
		return primes;
	}

	/**
	 * Initialise la liste déroulantes des impressions.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeImpression() throws Exception {
		// Si liste impressions vide alors affectation
		if (getLB_LISTE_IMPRESSION() == LBVide) {
			setLB_LISTE_IMPRESSION(EnumImpressionAffectation.getValues());
			addZone(getNOM_LB_LISTE_IMPRESSION_SELECT(), "0");
		}
	}

	/**
	 * Initialise les listes de spécificités. Date de création : (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeSpecificites_spec() throws Exception {
		initialiseAvantageNature_spec();
		initialiseDelegation_spec();
		initialiseRegime_spec();
		initialisePrimePointage_spec();
	}

	/**
	 * fin CLV #3264
	 */

	private void initialisePrimePointage_spec() throws Exception {

		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		// Primes pointages
		if (getListePrimePointageFP().size() == 0 && getFichePosteCourant() != null
				&& getFichePosteCourant().getIdFichePoste() != null) {
			setListePrimePointageFP(getPrimePointageFPDao().listerPrimePointageFP(
					Integer.valueOf(getFichePosteCourant().getIdFichePoste())));
			if (getFichePosteSecondaireCourant() != null) {
				getListePrimePointageFP().addAll(
						getPrimePointageFPDao().listerPrimePointageFP(
								Integer.valueOf(getFichePosteSecondaireCourant().getIdFichePoste())));
			}
		}

		if (getListePrimePointageAFF() == null && getFichePosteCourant() != null
				&& getAffectationCourant().getIdAffectation() != null) {
			setListePrimePointageAFF(getPrimePointageAffDao().listerPrimePointageAff(
					Integer.valueOf(getAffectationCourant().getIdAffectation())));
		}
		int indicePrime = 0;
		if (getListePrimePointageFP() != null && getListePrimePointageFP().size() != 0) {
			for (int i = 0; i < getListePrimePointageFP().size(); i++) {
				PrimePointageFP prime = (PrimePointageFP) getListePrimePointageFP().get(i);
				if (prime != null) {
					try {
						RefPrimeDto rubr = t.getPrimeDetail(prime.getNumRubrique());
						if (rubr != null && rubr.getNumRubrique() != null)
							addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrime), rubr.getNumRubrique()
									+ " : " + rubr.getLibelle());
					} catch (Exception e) {
						// TODO a supprimer quand les pointages seront en prod
						addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrime),
								"L'application des pointages n'est pas disponible.");
					}

					indicePrime++;
				}
			}
		}
		if (getListePrimePointageAFF() != null && getListePrimePointageAFF().size() != 0) {
			for (int j = 0; j < getListePrimePointageAFF().size(); j++) {
				PrimePointageAff prime = (PrimePointageAff) getListePrimePointageAFF().get(j);
				if (prime != null && !getListePrimePointageFP().contains(prime)) {
					try {
						RefPrimeDto rubr = t.getPrimeDetail(prime.getNumRubrique());
						if (rubr != null && rubr.getNumRubrique() != null)
							addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrime), rubr.getNumRubrique()
									+ " : " + rubr.getLibelle());
					} catch (Exception e) {
						// TODO a supprimer quand les pointages seront en prod
						addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrime),
								"L'application des pointages n'est pas disponible.");
					}
					indicePrime++;
				}
			}
		}
		if (getListePrimePointageAffAAjouter() != null && getListePrimePointageAffAAjouter().size() != 0) {
			for (PrimePointageAff prime : getListePrimePointageAffAAjouter()) {
				if (prime != null) {
					try {
						RefPrimeDto rubr = t.getPrimeDetail(prime.getNumRubrique());
						if (rubr != null && rubr.getNumRubrique() != null)
							addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrime), rubr.getNumRubrique()
									+ " : " + rubr.getLibelle());
					} catch (Exception e) {
						// TODO a supprimer quand les pointages seront en prod
						addZone(getNOM_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrime),
								"L'application des pointages n'est pas disponible.");
					}
					indicePrime++;
				}
			}
		}
	}

	private void initialiseRegime_spec() throws Exception {
		// Régimes indemnitaires
		if (getListeRegimeFP() == null && getFichePosteCourant() != null
				&& getFichePosteCourant().getIdFichePoste() != null) {
			setListeRegimeFP(RegimeIndemnitaire.listerRegimeIndemnitaireAvecFP(getTransaction(), getFichePosteCourant()
					.getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeRegimeFP().addAll(
						RegimeIndemnitaire.listerRegimeIndemnitaireAvecFP(getTransaction(),
								getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeRegimeAFF() == null && getAffectationCourant() != null
				&& getAffectationCourant().getIdAffectation() != null) {
			setListeRegimeAFF(RegimeIndemnitaire.listerRegimeIndemnitaireAvecAFF(getTransaction(),
					getAffectationCourant().getIdAffectation()));
		}
		int indiceReg = 0;
		if (getListeRegimeFP() != null && getListeRegimeFP().size() != 0) {
			for (int i = 0; i < getListeRegimeFP().size(); i++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeFP().get(i);
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(),
							aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(),
							aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE_spec(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT_spec(indiceReg), aReg.getForfait());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS_spec(indiceReg), aReg.getNombrePoints());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE_spec(indiceReg), rubr.getLibRubrique());
					indiceReg++;
				}
			}
		}
		if (getListeRegimeAFF() != null && getListeRegimeAFF().size() != 0) {
			for (int j = 0; j < getListeRegimeAFF().size(); j++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeAFF().get(j);
				if (aReg != null && !getListeRegimeFP().contains(aReg)) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(),
							aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(),
							aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE_spec(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT_spec(indiceReg), aReg.getForfait());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS_spec(indiceReg), aReg.getNombrePoints());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE_spec(indiceReg), rubr.getLibRubrique());
					indiceReg++;
				}
			}
		}
		if (getListeRegimeAAjouter() != null && getListeRegimeAAjouter().size() != 0) {
			for (int k = 0; k < getListeRegimeAAjouter().size(); k++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeAAjouter().get(k);
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(),
							aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(),
							aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE_spec(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT_spec(indiceReg), aReg.getForfait());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS_spec(indiceReg), aReg.getNombrePoints());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE_spec(indiceReg), rubr.getLibRubrique());
					indiceReg++;
				}
			}
		}

	}

	/**
	 * Initialise les champs de la fiche de poste courante liée à l'affectation.
	 * 
	 * @throws Exception
	 */
	private void initialiserFichePoste() throws Exception {
		// Titre
		String titreFichePoste = getFichePosteCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : TitrePoste
				.chercherTitrePoste(getTransaction(), getFichePosteCourant().getIdTitrePoste()).getLibTitrePoste();
		// Service
		Service srv = Service.chercherService(getTransaction(), getFichePosteCourant().getIdServi());
		String direction = Const.CHAINE_VIDE;
		String division = Const.CHAINE_VIDE;
		String section = Const.CHAINE_VIDE;
		if (Services.estAlphabetique(srv.getCodService())) {
			if (Service.isSection(srv.getCodService()))
				section = srv.getLibService();
			if (!srv.getCodService().substring(2, 3).equals("A"))
				division = Service.getDivision(getTransaction(), srv.getCodService().substring(0, 3) + "A")
						.getLibService();
			if (!srv.getCodService().substring(1, 2).equals("A"))
				direction = Service.getDirection(getTransaction(), srv.getCodService().substring(0, 2) + "AA")
						.getLibService();
		} else {
			division = srv.getLibService();
		}
		if (division == null) {
			Service serv = Service.chercherService(getTransaction(), getFichePosteCourant().getIdServi());
			division = serv.getLibService();
		}
		// temps reglementaire de travail
		Horaire hor = Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg());
		// Lieu
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo());

		addZone(getNOM_ST_DIRECTION(), direction);
		addZone(getNOM_ST_SERVICE(), division);
		addZone(getNOM_ST_SUBDIVISION(), section);
		addZone(getNOM_ST_NUM_FICHE_POSTE(), getFichePosteCourant().getNumFP());
		addZone(getNOM_ST_TITRE_FP(), titreFichePoste);
		addZone(getNOM_ST_TPS_REG(), hor.getLibHor());
		addZone(getNOM_ST_LIEU_FP(), eg.getLibEntiteGeo());
	}

	/**
	 * Initialise les champs de la fiche de poste secondaire courante liée à
	 * l'affectation.
	 * 
	 * @throws Exception
	 */
	private void initialiserFichePosteSecondaire() throws Exception {
		// Titre
		String titreFichePoste = getFichePosteSecondaireCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE
				: TitrePoste.chercherTitrePoste(getTransaction(), getFichePosteSecondaireCourant().getIdTitrePoste())
						.getLibTitrePoste();
		// Service
		Service srv = Service.chercherService(getTransaction(), getFichePosteSecondaireCourant().getIdServi());
		String direction = Const.CHAINE_VIDE;
		String division = Const.CHAINE_VIDE;
		String section = Const.CHAINE_VIDE;
		if (Services.estAlphabetique(srv.getCodService())) {
			if (Service.isSection(srv.getCodService()))
				section = srv.getLibService();
			if (!srv.getCodService().substring(2, 3).equals("A"))
				division = Service.getDivision(getTransaction(), srv.getCodService().substring(0, 3) + "A")
						.getLibService();
			if (!srv.getCodService().substring(1, 2).equals("A"))
				direction = Service.getDirection(getTransaction(), srv.getCodService().substring(0, 2) + "AA")
						.getLibService();
		} else {
			division = srv.getLibService();
		}
		// temps reglementaire de travail
		Horaire hor = Horaire.chercherHoraire(getTransaction(), getFichePosteSecondaireCourant().getIdCdthorReg());
		// Lieu
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteSecondaireCourant().getIdEntiteGeo());

		addZone(getNOM_ST_DIRECTION_SECONDAIRE(), direction);
		addZone(getNOM_ST_SERVICE_SECONDAIRE(), division);
		addZone(getNOM_ST_SUBDIVISION_SECONDAIRE(), section);
		addZone(getNOM_ST_NUM_FICHE_POSTE_SECONDAIRE(), getFichePosteSecondaireCourant().getNumFP());
		addZone(getNOM_ST_TITRE_FP_SECONDAIRE(), titreFichePoste);
		addZone(getNOM_ST_TPS_REG_SECONDAIRE(), hor.getLibHor());
		addZone(getNOM_ST_LIEU_FP_SECONDAIRE(), eg.getLibEntiteGeo());
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (04/08/11 15:42:45)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeAffectation(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		initialiseAffectationCourante(request);

		// Init Motifs affectation et Tps de travail
		initialiseListeDeroulante();

		initialiseDao_spec();
		initialiseListeDeroulante_spec();

		initialiseListeSpecificites_spec();

		// Si pas d'affectation en cours
		if (getFichePosteCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			ArrayList<Affectation> affActives = Affectation.listerAffectationActiveAvecAgent(getTransaction(),
					getAgentCourant());
			if (affActives.size() == 1) {
				setAffectationCourant((Affectation) affActives.get(0));
				// Recherche des informations à afficher
				setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
						.getIdFichePoste()));
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(),
							getAffectationCourant().getIdFichePosteSecondaire()));
				}
			} else if (affActives.size() == 0) {
				/*
				 * getTransaction().declarerErreur(MessageUtils.getMessage("ERR083"
				 * )); return;
				 */
			} else if (affActives.size() > 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR082"));
				return;
			}
		}
		if (getVAL_RG_SPECIFICITE_spec() == null || getVAL_RG_SPECIFICITE_spec().length() == 0) {
			addZone(getNOM_RG_SPECIFICITE_spec(), getNOM_RB_SPECIFICITE_PP_spec());
			addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_PRIME_POINTAGE_SPEC);
		}

	}

	/**
	 * Vérifie les règles de gestion métier
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean performControlerChoixImpression() throws Exception {
		// Si pas de document sélectionné alors erreur
		if (Integer.parseInt(getZone(getNOM_LB_LISTE_IMPRESSION_SELECT())) == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "documents à imprimer"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Vérifie les règles de gestion métier
	 * 
	 * @return boolean
	 * @throws Exception
	 *             RG_AG_AF_A11
	 */
	public boolean performControlerRG() throws Exception {

		// Vérification du non-chevauchement des dates des affectations
		for (ListIterator<Affectation> list = getListeAffectation().listIterator(); list.hasNext();) {
			Affectation aAff = (Affectation) list.next();
			if (!aAff.getIdAffectation().equals(getAffectationCourant().getIdAffectation())) {
				if (aAff.getDateFinAff() != null && !aAff.getDateFinAff().equals(Const.DATE_NULL)) {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), aAff.getDateFinAff()) <= 0) {
							// "ERR201",
							// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						}
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aAff.getDateDebutAff()) >= 0
								&& Services.compareDates(getVAL_EF_DATE_DEBUT(), aAff.getDateFinAff()) <= 0) {
							// "ERR201",
							// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						}
					}
				} else {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						// "ERR201",
						// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
						setFocus(getNOM_PB_AJOUTER());
						return false;
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aAff.getDateDebutAff()) >= 0) {
							// "ERR201",
							// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						}
					}
				}
			}
		}
		// verification pas 2 fois la même fiche de poste mise
		if (getFichePosteSecondaireCourant() != null) {
			if (getFichePosteCourant().getIdFichePoste().equals(getFichePosteSecondaireCourant().getIdFichePoste())) {
				// "ERR117",
				// "La fiche de poste @ doit être différente de la fiche courante."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR117", "secondaire"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		// Vérification de la non-affectation de la Fiche de poste choisie dans
		// les dates choisies
		ArrayList<Affectation> listeAffFP = Affectation.listerAffectationAvecFP(getTransaction(),
				getFichePosteCourant());
		for (Affectation aff : listeAffFP) {
			if (!aff.getIdAffectation().equals(getAffectationCourant().getIdAffectation())) {
				if (aff.getDateFinAff() != null && !aff.getDateFinAff().equals(Const.DATE_NULL)) {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						}
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0
								&& Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						}
					}
				} else {
					if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
						// "ERR085",
						// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
						setFocus(getNOM_PB_AJOUTER());
						return false;
					} else {
						if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						}
					}
				}
			}
		}

		// Vérification de la non-affectation de la Fiche de poste secondaire
		// choisie dans les dates choisies
		if (getFichePosteSecondaireCourant() != null) {
			ArrayList<Affectation> listeAffFPSecondaire = Affectation.listerAffectationAvecFP(getTransaction(),
					getFichePosteSecondaireCourant());
			for (Affectation aff : listeAffFPSecondaire) {
				if (!aff.getIdAffectation().equals(getAffectationCourant().getIdAffectation())) {
					if (aff.getDateFinAff() != null && !aff.getDateFinAff().equals(Const.DATE_NULL)) {
						if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
							if (Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
								// "ERR085",
								// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
								setFocus(getNOM_PB_AJOUTER());
								return false;
							}
						} else {
							if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0
									&& Services.compareDates(getVAL_EF_DATE_DEBUT(), aff.getDateFinAff()) <= 0) {
								// "ERR085",
								// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
								setFocus(getNOM_PB_AJOUTER());
								return false;
							}
						}
					} else {
						if (Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN())) {
							// "ERR085",
							// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
							setFocus(getNOM_PB_AJOUTER());
							return false;
						} else {
							if (Services.compareDates(getVAL_EF_DATE_FIN(), aff.getDateDebutAff()) >= 0) {
								// "ERR085",
								// "Cette Fiche de poste est déjà affectée à un autre agent aux dates données."
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR085"));
								setFocus(getNOM_PB_AJOUTER());
								return false;
							}
						}
					}
				}
			}
		}

		// Verification des temps reglementaires des 2 fiches de postes < 100%
		// RG_AG_AF_A11
		if (getFichePosteSecondaireCourant() != null) {
			Horaire horFDP1 = Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg());
			Horaire horFDP2 = Horaire.chercherHoraire(getTransaction(), getFichePosteSecondaireCourant()
					.getIdCdthorReg());
			// calcul du taux que ca donne
			Float res = Float.valueOf(horFDP1.getCdTaux()) + Float.valueOf(horFDP2.getCdTaux());
			if (res > 1) {
				// "ERR104",
				// "Le temps de travail réglementaire des deux fiche de poste dépasse 100%."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR080"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, dates bien
	 * formatées, ...)
	 * 
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 *             RG_AG_AF_A06
	 */
	public boolean performControlerSaisie() throws Exception {
		// RG_AG_AF_A06

		// **********************************************************
		// RG_AG_AFF_C01 : Vérification des champs obligatoires
		// **********************************************************
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date arrêté"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		if (!Services.estUneDate(getVAL_EF_DATE_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "arrêté"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// Vérification Date début et date fin (non null et dans le bon ordre.
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			// format de date
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
				// ERR007 : La date @ est incorrecte. Elle doit être au format
				// date.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "début"));
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
				if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
					// ERR007 : La date @ est incorrecte. Elle doit être au
					// format date.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "fin"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				} else if (Services.compareDates(getZone(getNOM_EF_DATE_DEBUT()), getZone(getNOM_EF_DATE_FIN())) > 0) {
					// ERR200 : La date @ doit être supérieure ou égale à la
					// date @.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR200", "fin", "début"));
					setFocus(getNOM_PB_AJOUTER());
					return false;
				}
			}
		} else {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date début"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		if ((Const.CHAINE_VIDE).equals(getVAL_ST_NUM_FICHE_POSTE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Fiche de poste"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// **********************************************************
		// Vérification Formats
		// **********************************************************
		// "ERR992", "La zone @ doit être numérique."
		if (!Const.CHAINE_VIDE.equals(getVAL_EF_REF_ARRETE()) && !Services.estNumerique(getVAL_EF_REF_ARRETE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Réf. arrêté"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un avantage en nature. Date de création :
	 * (28/07/11)
	 */
	private boolean performControlerSaisieAvNat_spec(HttpServletRequest request) throws Exception {

		// type avantage obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT_spec()) ? Integer
				.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT_spec()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type avantage"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// ****************************************
		// Verification Montant OU Nature renseigné
		// ****************************************
		if (getVAL_EF_MONTANT_AVANTAGE_spec().length() == 0
				&& ((NatureAvantage) getListeNatureAvantage().get(
						Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT_spec()))).getIdNatureAvantage() == null) {
			// "ERR979","Au moins une des 2 zones suivantes doit être renseignée : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Nature avantage", "Montant"));

			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// ********************
		// Verification Montant
		// ********************
		if (getVAL_EF_MONTANT_AVANTAGE_spec().length() != 0
				&& !Services.estNumerique(getVAL_EF_MONTANT_AVANTAGE_spec())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Montant"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'une délégation. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieDel_spec(HttpServletRequest request) throws Exception {
		// type obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT_spec()) ? Integer
				.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT_spec()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		return true;
	}

	/**
	 * Contrôle les zones saisies d'un régime indemnitaire. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisiePrimePointage_spec(HttpServletRequest request) throws Exception {

		// rubrique obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer
				.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "rubrique"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les zones saisies d'un régime indemnitaire. Date de création :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieRegIndemn_spec(HttpServletRequest request) throws Exception {
		// type obligatoire
		int indiceRubr = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT_spec()) ? Integer
				.parseInt(getVAL_LB_TYPE_REGIME_SELECT_spec()) : -1);
		if (indiceRubr < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// *******************************************
		// Verification Forfait OU Nb points renseigné
		// *******************************************
		if (getVAL_EF_FORFAIT_REGIME_spec().length() == 0 && getVAL_EF_NB_POINTS_REGIME_spec().length() == 0) {
			// "ERR979","Au moins une des 2 zones suivantes doit être renseignée : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Forfait", "Nb points"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// ********************
		// Verification Forfait
		// ********************
		if (getVAL_EF_FORFAIT_REGIME_spec().length() != 0 && !Services.estNumerique(getVAL_EF_FORFAIT_REGIME_spec())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Forfait"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// **********************
		// Verification Nb points
		// **********************
		if (getVAL_EF_NB_POINTS_REGIME_spec().length() != 0
				&& !Services.estNumerique(getVAL_EF_NB_POINTS_REGIME_spec())) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Nb points"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {

		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de l'affectation courante
		setAffectationCourant(new Affectation());

		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		setFichePosteSecondaireCourant(null);
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_AVANTAGE_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_DELEGATION_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);
		addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_DELEGATION_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_PRIME_POINTAGE_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);
		addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_PRIME_POINTAGE_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_REGIME_spec(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_spec(), ACTION_AJOUTER_SPEC);
		addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_REG_INDEMN_SPEC);

		setFocus(getNOM_EF_REF_ARRETE());
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		setListeAvantageAFF(null);
		setListeAvantageFP(null);
		getListeAvantageAAjouter().clear();
		getListeAvantageASupprimer().clear();
		setListeDelegationAFF(null);
		setListeDelegationFP(null);
		getListeDelegationAAjouter().clear();
		getListeDelegationASupprimer().clear();
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);
		getListePrimePointageAffAAjouter().clear();
		getListePrimePointageAffASupprimer().clear();
		setListeRegimeAFF(null);
		setListeRegimeFP(null);
		getListeRegimeAAjouter().clear();
		getListeRegimeASupprimer().clear();
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_CHANGER_SPECIFICITE_spec(HttpServletRequest request) throws Exception {
		if (getVAL_RG_SPECIFICITE_spec().equals(getNOM_RB_SPECIFICITE_AN_spec()))
			addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_AVANTAGE_NATURE_SPEC);
		else if (getVAL_RG_SPECIFICITE_spec().equals(getNOM_RB_SPECIFICITE_D_spec()))
			addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_DELEGATION_SPEC);
		else if (getVAL_RG_SPECIFICITE_spec().equals(getNOM_RB_SPECIFICITE_RI_spec()))
			addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_REG_INDEMN_SPEC);
		else if (getVAL_RG_SPECIFICITE_spec().equals(getNOM_RB_SPECIFICITE_PP_spec()))
			addZone(getNOM_ST_SPECIFICITE_spec(), SPEC_PRIME_POINTAGE_SPEC);

		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		// On supprime la fiche de poste secondaire
		setFichePosteSecondaireCourant(null);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAConsulter);
		setAffectationCourant(aff);
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);

		if (initialiseAffectationCourante(request)) {

			// Alim zones
			addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
			addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete());
			addZone(getNOM_EF_DATE_DEBUT(), getAffectationCourant().getDateDebutAff());
			addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff());
			addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean performPB_HISTORIQUE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_HISTORIQUE, true);
		return true;
	}

	public boolean performPB_IMPRIMER(HttpServletRequest request, int indiceEltAImprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAImprimer);
		setAffectationCourant(aff);
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);

		if (getAffectationCourant().isActive()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

			if (initialiseAffectationCourante(request)) {
				initialiseListeImpression();
				// On nomme l'action
				addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
			}
		} else {
			// "ERR081",
			// "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimée.")
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR081"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du processs - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// On vide les zones de saisie
		initialiseAffectationVide();
		// On supprime la fiche de poste
		setFichePosteCourant(null);
		setFichePosteSecondaireCourant(null);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltAModifier);
		setAffectationCourant(aff);
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);

		if (initialiseAffectationCourante(request)) {

			// Alim zones
			addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
			addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete());
			addZone(getNOM_EF_DATE_DEBUT(), getAffectationCourant().getDateDebutAff());
			addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff());
			addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 13:35:40)
	 * 
	 * RG_AG_AF_C05 RG_AG_AF_C07
	 */
	public boolean performPB_RECHERCHER_FP(HttpServletRequest request) throws Exception {
		// RG_AG_AF_C05
		// RG_AG_AF_C07
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_AFFECTATION, Boolean.TRUE);
		setStatut(STATUT_RECHERCHE_FP, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/08/11 13:35:40)
	 * 
	 * RG_AG_AF_C05 RG_AG_AF_C07
	 */
	public boolean performPB_RECHERCHER_FP_SECONDAIRE(HttpServletRequest request) throws Exception {
		// RG_AG_AF_C05
		// RG_AG_AF_C07
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_SECONDAIRE_AFFECTATION, Boolean.TRUE);
		setStatut(STATUT_RECHERCHE_FP_SECONDAIRE, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_AF_A02
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			setFocus(getNOM_PB_AJOUTER());
			return false;
		}

		// Récup du contrat courant
		Affectation aff = (Affectation) getListeAffectation().get(indiceEltASuprimer);
		setAffectationCourant(aff);
		setListePrimePointageAFF(null);
		setListePrimePointageFP(null);

		if (getAffectationCourant().isActive()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);

			if (initialiseAffectationCourante(request)) {

				// Alim zones
				addZone(getNOM_EF_REF_ARRETE(), getAffectationCourant().getRefArreteAff());
				addZone(getNOM_EF_DATE_ARRETE(), getAffectationCourant().getDateArrete());
				addZone(getNOM_EF_DATE_DEBUT(), getAffectationCourant().getDateDebutAff());
				addZone(getNOM_EF_DATE_FIN(), getAffectationCourant().getDateFinAff());
				addZone(getNOM_EF_COMMENTAIRE(), getAffectationCourant().getCommentaire());

				// On nomme l'action
				addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
			}
		} else {
			// RG_AG_AF_A02
			// "ERR081",
			// "Cette affectation est inactive, elle ne peut être ni supprimée,ni imprimée.")
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR081"));
			setFocus(getNOM_PB_AJOUTER());
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
			setAffectationCourant(null);
			return false;
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AVANTAGE_spec(HttpServletRequest request, int indiceEltASupprimer)
			throws Exception {
		// Calcul du nombre d'Avantages en nature sélectionnés par l'utilisateur
		// parmi ceux issus de la fiche de poste
		int nbAvNatFPSelected = 0;
		for (int i = 0; i < getListeAvantageAFF().size(); i++) {
			if (getListeAvantageFP().contains(getListeAvantageAFF().get(i)))
				nbAvNatFPSelected++;
		}
		// Si la spécificité à supprimer est déjà en base
		if (indiceEltASupprimer - getListeAvantageFP().size() + nbAvNatFPSelected < getListeAvantageAFF().size()) {
			AvantageNature avNatASupprimer = (AvantageNature) getListeAvantageAFF().get(
					indiceEltASupprimer - getListeAvantageFP().size() + nbAvNatFPSelected);
			if (avNatASupprimer != null) {
				getListeAvantageAFF().remove(avNatASupprimer);
				getListeAvantageASupprimer().add(avNatASupprimer);
			}

		}
		// Si la spécificité à supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			AvantageNature avNatASupprimer = (AvantageNature) getListeAvantageAAjouter().get(
					indiceEltASupprimer - getListeAvantageFP().size() - getListeAvantageAFF().size()
							+ nbAvNatFPSelected);
			if (avNatASupprimer != null) {
				getListeAvantageAAjouter().remove(avNatASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DELEGATION_spec(HttpServletRequest request, int indiceEltASupprimer)
			throws Exception {
		// Calcul du nombre de Delegation sélectionnées par l'utilisateur parmi
		// ceux issus de la fiche de poste
		int nbDelFPSelected = 0;
		for (int i = 0; i < getListeDelegationAFF().size(); i++) {
			if (getListeDelegationFP().contains(getListeDelegationAFF().get(i)))
				nbDelFPSelected++;
		}
		// Si la spécificité à supprimer est déjà en base
		if (indiceEltASupprimer - getListeDelegationFP().size() + nbDelFPSelected < getListeDelegationAFF().size()) {
			Delegation delASupprimer = (Delegation) getListeDelegationAFF().get(
					indiceEltASupprimer - getListeDelegationFP().size() + nbDelFPSelected);
			if (delASupprimer != null) {
				getListeDelegationAFF().remove(delASupprimer);
				getListeDelegationASupprimer().add(delASupprimer);
			}

		}
		// Si la spécificité à supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			Delegation delASupprimer = (Delegation) getListeDelegationAAjouter().get(
					indiceEltASupprimer - getListeDelegationFP().size() - getListeDelegationAFF().size()
							+ nbDelFPSelected);
			if (delASupprimer != null) {
				getListeDelegationAAjouter().remove(delASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_PRIME_POINTAGE_spec(HttpServletRequest request, int indiceEltASupprimer)
			throws Exception {
		// Calcul du nombre de Prime Pointage sélectionnés par l'utilisateur
		// parmi ceux issus de la fiche de poste

		int ppAffSize = 0;
		if (getListePrimePointageAFF() != null) {
			ppAffSize = getListePrimePointageAFF().size();
		}
		// Si la spécificité à supprimer est déjà en base
		if (indiceEltASupprimer - getListePrimePointageFP().size() < ppAffSize) {
			PrimePointageAff primePointageASupprimer = (PrimePointageAff) getListePrimePointageAFF().get(
					indiceEltASupprimer - getListePrimePointageFP().size());
			if (primePointageASupprimer != null) {
				getListePrimePointageAFF().remove(primePointageASupprimer);
				getListePrimePointageAffASupprimer().add(primePointageASupprimer);
			}

		}
		// Si la spécificité à supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			PrimePointageAff primePointageASupprimer = (PrimePointageAff) getListePrimePointageAffAAjouter().get(
					indiceEltASupprimer - getListePrimePointageFP().size() - ppAffSize);
			if (primePointageASupprimer != null) {
				getListePrimePointageAffAAjouter().remove(primePointageASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_REGIME_spec(HttpServletRequest request, int indiceEltASupprimer)
			throws Exception {
		// Calcul du nombre de RegimeIndemnitaire sélectionnés par l'utilisateur
		// parmi ceux issus de la fiche de poste
		int nbRegIndemnFPSelected = 0;
		for (int i = 0; i < getListeRegimeAFF().size(); i++) {
			if (getListeRegimeFP().contains(getListeRegimeAFF().get(i)))
				nbRegIndemnFPSelected++;
		}
		// Si la spécificité à supprimer est déjà en base
		if (indiceEltASupprimer - getListeRegimeFP().size() + nbRegIndemnFPSelected < getListeRegimeAFF().size()) {
			RegimeIndemnitaire regIndemnASupprimer = (RegimeIndemnitaire) getListeRegimeAFF().get(
					indiceEltASupprimer - getListeRegimeFP().size() + nbRegIndemnFPSelected);
			if (regIndemnASupprimer != null) {
				getListeRegimeAFF().remove(regIndemnASupprimer);
				getListeRegimeASupprimer().add(regIndemnASupprimer);
			}

		}
		// Si la spécificité à supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			RegimeIndemnitaire regIndemnASupprimer = (RegimeIndemnitaire) getListeRegimeAAjouter().get(
					indiceEltASupprimer - getListeRegimeFP().size() - getListeRegimeAFF().size()
							+ nbRegIndemnFPSelected);
			if (regIndemnASupprimer != null) {
				getListeRegimeAAjouter().remove(regIndemnASupprimer);
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/08/11 15:20:56)
	 * 
	 * RG_AG_AF_A10 RG_AG_AF_A07
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Si Action Suppression
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
			// ON SUPPRIME LES SPECIFICITES DE L'AFFECTATION LIE
			getPrimePointageAffDao().supprimerToutesPrimePointageAff(getAffectationCourant().getIdAffectation());
			// Suppression
			getAffectationCourant().supprimerAffectation(getTransaction(), user, getAgentCourant());
			if (getTransaction().isErreur())
				return false;

			setFocus(getNOM_PB_AJOUTER());
		} else if (getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
			if (performControlerChoixImpression()) {
				// recup du document à imprimer
				String nomDocument = EnumImpressionAffectation.getCodeImpressionAffectation(Integer
						.parseInt(getZone(getNOM_LB_LISTE_IMPRESSION_SELECT())));
				// Récup affectation courante
				Affectation aff = getAffectationCourant();
				if (getVAL_ST_WARNING().equals(Const.CHAINE_VIDE)) {
					// on verifie si il existe dejà un fichier pour cette
					// affectation dans la BD
					if (verifieExistFichier(aff.getIdAffectation(), nomDocument)) {
						// alors on affiche un message
						// :" Attention un fichier existe déjà pour ce contrat. Etes-vous sûr de vouloir écraser la version précédente ?"
						addZone(getNOM_ST_WARNING(),
								"Attention un fichier du même type existe déjà pour cette affectation. Etes-vous sûr de vouloir écraser la version précédente ?");
					} else {
						imprimeModele(request, nomDocument);
						addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
						addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
						addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
					}
				} else {
					imprimeModele(request, nomDocument);
					addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
					addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
					addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
				}
			}
		} else {
			if (!performControlerSaisie()) {
				return false;
			}

			if (!performControlerRG()) {
				return false;
			}

			// Récup des zones saisies
			String newIndMotifAffectation = getZone(getNOM_LB_MOTIF_AFFECTATION_SELECT());
			MotifAffectation newMotifAffectation = (MotifAffectation) getListeMotifAffectation().get(
					Integer.parseInt(newIndMotifAffectation));

			// pour recupere le codeEcole
			EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo());

			// Affectation des attributs
			getAffectationCourant().setIdAgent(getAgentCourant().getIdAgent());
			getAffectationCourant().setIdFichePoste(getFichePosteCourant().getIdFichePoste());
			getAffectationCourant().setRefArreteAff(
					getVAL_EF_REF_ARRETE().length() == 0 ? null : getVAL_EF_REF_ARRETE());
			getAffectationCourant().setDateArrete(Services.formateDate(getVAL_EF_DATE_ARRETE()));
			getAffectationCourant().setDateDebutAff(Services.formateDate(getVAL_EF_DATE_DEBUT()));
			getAffectationCourant().setDateFinAff(
					getVAL_EF_DATE_FIN().equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : Services
							.formateDate(getVAL_EF_DATE_FIN()));
			getAffectationCourant().setIdMotifAffectation(newMotifAffectation.getIdMotifAffectation());
			getAffectationCourant().setTempsTravail(
					getListeTempsTravail()[Integer.parseInt(getVAL_LB_TEMPS_TRAVAIL_SELECT())]);
			getAffectationCourant().setCodeEcole(eg.getCdEcol());
			getAffectationCourant().setIdFichePosteSecondaire(
					getFichePosteSecondaireCourant() != null ? getFichePosteSecondaireCourant().getIdFichePoste()
							: null);
			getAffectationCourant().setCommentaire(
					getVAL_EF_COMMENTAIRE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_COMMENTAIRE());

			if (getVAL_ST_ACTION().equals(ACTION_MODIFICATION)) {
				// Modification
				getAffectationCourant().modifierAffectation(getTransaction(), user, getAgentCourant(),
						getFichePosteCourant());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création Affectation
				if (!getAffectationCourant().creerAffectation(getTransaction(), user, getAgentCourant())) {
					return false;
				}

				// on sauvegarde les FDP au moment de la creation d'une
				// affectation
				// RG_AG_AF_A07
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					if (!sauvegardeFDP(getAffectationCourant().getIdFichePosteSecondaire())) {
						return false;
					}
				}
				if (!sauvegardeFDP(getAffectationCourant().getIdFichePoste())) {
					return false;
				}
			}
			if (getTransaction().isErreur())
				return false;
		}

		commitTransaction();

		if (getAffectationCourant().getIdAffectation() == null)
			return false;

		// Sauvegarde des nouveaux avantages nature et suppression des anciens
		for (int i = 0; i < getListeAvantageAAjouter().size(); i++) {
			AvantageNature avNat = (AvantageNature) getListeAvantageAAjouter().get(i);
			avNat.creerAvantageNature(getTransaction());
			AvantageNatureAFF avNatAFF = new AvantageNatureAFF(getAffectationCourant().getIdAffectation(),
					avNat.getIdAvantage());
			avNatAFF.creerAvantageNatureAFF(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu être créé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (int i = 0; i < getListeAvantageASupprimer().size(); i++) {
			AvantageNature avNat = (AvantageNature) getListeAvantageASupprimer().get(i);
			AvantageNatureAFF avNatAFF = AvantageNatureAFF.chercherAvantageNatureAFF(getTransaction(),
					getAffectationCourant().getIdAffectation(), avNat.getIdAvantage());
			avNatAFF.supprimerAvantageNatureAFF(getTransaction());
			avNat.supprimerAvantageNature(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu être supprimé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		// Sauvegarde des nouvelles Delegation et suppression des anciennes
		for (int i = 0; i < getListeDelegationAAjouter().size(); i++) {
			Delegation deleg = (Delegation) getListeDelegationAAjouter().get(i);
			deleg.creerDelegation(getTransaction());
			DelegationAFF delAFF = new DelegationAFF(getAffectationCourant().getIdAffectation(),
					deleg.getIdDelegation());
			delAFF.creerDelegationAFF(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins une Delegation n'a pu être créée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (int i = 0; i < getListeDelegationASupprimer().size(); i++) {
			Delegation deleg = (Delegation) getListeDelegationASupprimer().get(i);
			DelegationAFF delAFF = DelegationAFF.chercherDelegationAFF(getTransaction(), getAffectationCourant()
					.getIdAffectation(), deleg.getIdDelegation());
			delAFF.supprimerDelegationAFF(getTransaction());
			deleg.supprimerDelegation(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins une Delegation n'a pu être supprimée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		// Sauvegarde des nouveaux RegimeIndemnitaire et suppression des anciens
		for (int i = 0; i < getListeRegimeAAjouter().size(); i++) {
			RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeAAjouter().get(i);
			regIndemn.creerRegimeIndemnitaire(getTransaction());
			RegIndemnAFF riAFF = new RegIndemnAFF(getAffectationCourant().getIdAffectation(),
					regIndemn.getIdRegIndemn());
			riAFF.creerRegIndemnAFF(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu être créé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (int i = 0; i < getListeRegimeASupprimer().size(); i++) {
			RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeASupprimer().get(i);
			RegIndemnAFF riAFF = RegIndemnAFF.chercherRegIndemnAFF(getTransaction(), getAffectationCourant()
					.getIdAffectation(), regIndemn.getIdRegIndemn());
			riAFF.supprimerRegIndemnAFF(getTransaction());
			regIndemn.supprimerRegimeIndemnitaire(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu être supprimé.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		for (PrimePointageAff prime : getListePrimePointageAffAAjouter()) {
			try {
				getPrimePointageAffDao().creerPrimePointageAff(prime.getNumRubrique(),
						Integer.valueOf(getAffectationCourant().getIdAffectation()));

			} catch (Exception e) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins une prime de pointage n'a pu être créée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}
		for (PrimePointageAff prime : getListePrimePointageAffASupprimer()) {
			try {
				getPrimePointageAffDao().supprimerPrimePointageAff(
						Integer.valueOf(getAffectationCourant().getIdAffectation()), prime.getNumRubrique());
			} catch (Exception e) {
				getTransaction().declarerErreur(
						getTransaction().traiterErreur() + " Au moins une prime de pointage n'a pu être supprimée.");
				setFocus(getNOM_PB_AJOUTER());
				return false;
			}
		}

		if (getListePrimePointageAFF() == null && getListePrimePointageAffAAjouter().size() > 0) {
			setListePrimePointageAFF(new ArrayList<PrimePointageAff>());
		}
		for (PrimePointageAff prime : getListePrimePointageAffAAjouter()) {
			getListePrimePointageAFF().add(prime);
		}
		getListePrimePointageAffAAjouter().clear();
		getListePrimePointageAffASupprimer().clear();

		if (!getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
			// Tout s'est bien passé
			commitTransaction();

			// Réinitialisation
			initialiseListeAffectation(request);
			// init de l'affectation courante
			setAffectationCourant(null);
			// On vide les zones de saisie
			initialiseAffectationVide();
			// On supprime la fiche de poste
			setFichePosteCourant(null);

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VALIDER_AJOUT_spec(HttpServletRequest request) throws Exception {

		if (getVAL_ST_SPECIFICITE().equals(SPEC_AVANTAGE_NATURE_SPEC)) {
			// Contrôle des champs
			if (!performControlerSaisieAvNat_spec(request))
				return false;

			// Alimentation de l'objet
			AvantageNature avNat = new AvantageNature();

			avNat.setMontant(getVAL_EF_MONTANT_AVANTAGE_spec());

			int indiceTypeAvantage = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT_spec()) ? Integer
					.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT_spec()) : -1);
			avNat.setIdTypeAvantage(((TypeAvantage) getListeTypeAvantage().get(indiceTypeAvantage)).getIdTypeAvantage());
			int indiceNatAvantage = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT_spec()) ? Integer
					.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT_spec()) : -1);
			avNat.setIdNatureAvantage(((NatureAvantage) getListeNatureAvantage().get(indiceNatAvantage))
					.getIdNatureAvantage());
			int indiceRubAvantage = (Services.estNumerique(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) : -1);
			if (indiceRubAvantage > 0)
				avNat.setNumRubrique(getListeRubrique().get(indiceRubAvantage - 1).getNumRubrique());

			if (getListeAvantageAFF() == null)
				setListeAvantageAFF(new ArrayList<AvantageNature>());

			if (!getListeAvantageAFF().contains(avNat) && !getListeAvantageFP().contains(avNat)
					&& !getListeAvantageAAjouter().contains(avNat)) {
				if (getListeAvantageASupprimer().contains(avNat)) {
					getListeAvantageASupprimer().remove(avNat);
					getListeAvantageAFF().add(avNat);
				} else {
					getListeAvantageAAjouter().add(avNat);
				}
			}
			// Réinitialisation des champs de saisie
			viderAvantageNature_spec();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_DELEGATION_SPEC)) {
			// Contrôle des champs
			if (!performControlerSaisieDel_spec(request))
				return false;

			// Alimentation de l'objet
			Delegation deleg = new Delegation();

			deleg.setLibDelegation(getVAL_EF_COMMENT_DELEGATION_spec());

			int indiceTypeDelegation = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT_spec()) ? Integer
					.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT_spec()) : -1);
			deleg.setIdTypeDelegation(((TypeDelegation) getListeTypeDelegation_spec().get(indiceTypeDelegation))
					.getIdTypeDelegation());

			if (getListeDelegationAFF() == null)
				setListeDelegationAFF(new ArrayList<Delegation>());

			if (!getListeDelegationAFF().contains(deleg) && !getListeDelegationFP().contains(deleg)
					&& !getListeDelegationAAjouter().contains(deleg)) {
				if (getListeDelegationASupprimer().contains(deleg)) {
					getListeDelegationASupprimer().remove(deleg);
					getListeDelegationAFF().add(deleg);
				} else {
					getListeDelegationAAjouter().add(deleg);
				}
			}
			// Réinitialisation des champs de saisie
			viderDelegation_spec();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_REG_INDEMN_SPEC)) {
			// Contrôle des champs
			if (!performControlerSaisieRegIndemn_spec(request))
				return false;

			// Alimentation de l'objet
			RegimeIndemnitaire regIndemn = new RegimeIndemnitaire();

			regIndemn.setForfait(getVAL_EF_FORFAIT_REGIME_spec());
			regIndemn.setNombrePoints(getVAL_EF_NB_POINTS_REGIME_spec());

			int indiceRegIndemn = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT_spec()) ? Integer
					.parseInt(getVAL_LB_TYPE_REGIME_SELECT_spec()) : -1);
			regIndemn.setIdTypeRegIndemn(((TypeRegIndemn) getListeTypeRegIndemn().get(indiceRegIndemn))
					.getIdTypeRegIndemn());
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_REGIME_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_REGIME_SELECT()) : -1);
			if (indiceRub > 0)
				regIndemn.setNumRubrique(getListeRubrique().get(indiceRub - 1).getNumRubrique());

			if (getListeRegimeAFF() == null)
				setListeRegimeAFF(new ArrayList<RegimeIndemnitaire>());

			if (!getListeRegimeAFF().contains(regIndemn) && !getListeRegimeFP().contains(regIndemn)
					&& !getListeRegimeAAjouter().contains(regIndemn)) {
				if (getListeRegimeASupprimer().contains(regIndemn)) {
					getListeRegimeASupprimer().remove(regIndemn);
					getListeRegimeAFF().add(regIndemn);
				} else {
					getListeRegimeAAjouter().add(regIndemn);
				}
			}

			// Réinitialisation des champs de saisie
			viderRegIndemn_spec();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_PRIME_POINTAGE_SPEC)) {
			// Contrôle des champs
			if (!performControlerSaisiePrimePointage_spec(request))
				return false;

			int indiceRub = getListePrimes().get(
					Services.estNumerique(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) ? Integer
							.parseInt(getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) : -1).getNumRubrique();
			// Alimentation de l'objet
			if (!getListeRubs().contains(indiceRub)) {
				PrimePointageAff prime = new PrimePointageAff();
				prime.setNumRubrique(indiceRub);

				if (getListePrimePointageAFF() == null)
					setListePrimePointageAFF(new ArrayList<PrimePointageAff>());

				if (getListePrimePointageAffASupprimer().contains(prime)) {
					getListePrimePointageAffASupprimer().remove(prime);
					getListePrimePointageAFF().add(prime);
				} else {
					getListePrimePointageAffAAjouter().add(prime);
				}
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR088"));
				setFocus(getNOM_PB_AJOUTER());
			}

			// Réinitialisation des champs de saisie
			viderPrimePointage();
		}

		addZone(getNOM_ST_ACTION_spec(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (04/08/11 15:20:56)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER_FP
			if (testerParametre(request, getNOM_PB_RECHERCHER_FP())) {
				return performPB_RECHERCHER_FP(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_FP_SECONDAIRE
			if (testerParametre(request, getNOM_PB_RECHERCHER_FP_SECONDAIRE())) {
				return performPB_RECHERCHER_FP_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_HISTORIQUE
			if (testerParametre(request, getNOM_PB_HISTORIQUE())) {
				return performPB_HISTORIQUE(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_IMPRIMER
			for (int i = 0; i < getListeAffectation().size(); i++) {
				if (testerParametre(request, getNOM_PB_IMPRIMER(i))) {
					return performPB_IMPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_AJOUTER_AVANTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_AVANTAGE_spec())) {
				return performPB_AJOUTER_AVANTAGE_spec(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DELEGATION
			if (testerParametre(request, getNOM_PB_AJOUTER_DELEGATION_spec())) {
				return performPB_AJOUTER_DELEGATION_spec(request);
			}

			// Si clic sur le bouton PB_AJOUTER_REGIME
			if (testerParametre(request, getNOM_PB_AJOUTER_REGIME_spec())) {
				return performPB_AJOUTER_REGIME_spec(request);
			}

			// Si clic sur le bouton PB_AJOUTER_PRIME_POINTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_PRIME_POINTAGE_spec())) {
				return performPB_AJOUTER_PRIME_POINTAGE_spec(request);
			}

			// Si clic sur le bouton AJOUTER PRIME POINTAGE FP
			for (int i = 0; i < getListePrimePointageFP().size(); i++) {
				if (testerParametre(request, getNOM_PB_SET_PRIME_POINTAGE_spec(i))) {
					getListePrimePointageAffAAjouter().add(
							new PrimePointageAff(getListePrimePointageFP().get(i),
									getAffectationCourant().idAffectation));
					return true;
				}
			}

			// Si clic sur le bouton PB_CHANGER_SPECIFICITE
			if (testerParametre(request, getNOM_PB_CHANGER_SPECIFICITE_spec())) {
				return performPB_CHANGER_SPECIFICITE_spec(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AVANTAGE
			for (int i = getListeAvantageFP().size(); i < getListeAvantageFP().size() + getListeAvantageAFF().size()
					+ getListeAvantageAAjouter().size() - getListeAvantageASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AVANTAGE_spec(i))) {
					return performPB_SUPPRIMER_AVANTAGE_spec(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DELEGATION
			for (int i = getListeDelegationFP().size(); i < getListeDelegationFP().size()
					+ getListeDelegationAFF().size() + getListeDelegationAAjouter().size()
					- getListeDelegationASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DELEGATION_spec(i))) {
					return performPB_SUPPRIMER_DELEGATION_spec(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_REGIME
			for (int i = getListeRegimeFP().size(); i < getListeRegimeFP().size() + getListeRegimeAFF().size()
					+ getListeRegimeAAjouter().size() - getListeRegimeASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_REGIME_spec(i))) {
					return performPB_SUPPRIMER_REGIME_spec(request, i);
				}
			}

			if (getListePrimePointageAFF() == null)
				setListePrimePointageAFF(new ArrayList<PrimePointageAff>());

			// Si clic sur le bouton PB_SUPPRIMER_PRIME_POINTAGE
			for (int i = 0; i < getListePrimePointageFP().size() + getListePrimePointageAFF().size()
					+ getListePrimePointageAffAAjouter().size(); i++) {

				if (testerParametre(request, getNOM_PB_SUPPRIMER_PRIME_POINTAGE_spec(i))) {
					return performPB_SUPPRIMER_PRIME_POINTAGE_spec(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_AJOUT
			if (testerParametre(request, getNOM_PB_VALIDER_AJOUT_spec())) {
				return performPB_VALIDER_AJOUT_spec(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean sauvegardeFDP(String idFichePoste) throws Exception {

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String destination = "SauvegardeFDP/SauvFP_" + idFichePoste + "_" + dateJour + ".xml";

		String modele = "ModeleFP.xml";
		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_FICHEPOSTE");

		creerModeleDocumentFP("SauvegardeFDP", repModeles + modele, repPartage + destination, idFichePoste);

		// Tout s'est bien passé
		// on crée le document en base de données
		Document d = new Document();
		d.setIdTypeDocument("1");
		d.setLienDocument(destination);
		d.setNomDocument("SauvFP_" + idFichePoste + "_" + dateJour + ".xml");
		d.setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		d.setCommentaire("Sauvegarde automatique lors création affectation.");
		d.creerDocument(getTransaction());

		LienDocumentAgent lda = new LienDocumentAgent();
		lda.setIdAgent(getAgentCourant().getIdAgent());
		lda.setIdDocument(d.getIdDocument());
		lda.creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		commitTransaction();
		return true;
	}

	/**
	 * Met à jour l'affectation en cours.
	 * 
	 * @param affectationCourant
	 *            Nouvelle affectation en cours
	 */
	private void setAffectationCourant(Affectation affectationCourant) {
		this.affectationCourant = affectationCourant;
	}

	/**
	 * Met à jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Met à jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteCourant(FichePoste fichePosteCourant) {
		this.fichePosteCourant = fichePosteCourant;
	}

	/**
	 * Met à jour la fiche de poste secondaire courante.
	 * 
	 * @param fichePosteSecondaireCourant
	 */
	private void setFichePosteSecondaireCourant(FichePoste fichePosteSecondaireCourant) {
		this.fichePosteSecondaireCourant = fichePosteSecondaireCourant;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Setter de la liste: LB_LISTE_IMPRESSION Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_LISTE_IMPRESSION(String[] newLB_LISTE_IMPRESSION) {
		LB_LISTE_IMPRESSION = newLB_LISTE_IMPRESSION;
	}

	/**
	 * Setter de la liste: LB_MOTIF_AFFECTATION Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_MOTIF_AFFECTATION(String[] newLB_MOTIF_AFFECTATION) {
		LB_MOTIF_AFFECTATION = newLB_MOTIF_AFFECTATION;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE_spec(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_AVANTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_AVANTAGE(String[] newLB_RUBRIQUE_AVANTAGE) {
		LB_RUBRIQUE_AVANTAGE = newLB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_PRIME_POINTAGE Date de création :
	 * (16/08/11 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_PRIME_POINTAGE_spec(String[] newLB_RUBRIQUE_PRIME_POINTAGE) {
		LB_RUBRIQUE_PRIME_POINTAGE = newLB_RUBRIQUE_PRIME_POINTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_REGIME Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_REGIME(String[] newLB_RUBRIQUE_REGIME) {
		LB_RUBRIQUE_REGIME = newLB_RUBRIQUE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TEMPS_TRAVAIL Date de création : (04/08/11
	 * 15:20:56)
	 * 
	 */
	private void setLB_TEMPS_TRAVAIL(String[] newLB_TEMPS_TRAVAIL) {
		LB_TEMPS_TRAVAIL = newLB_TEMPS_TRAVAIL;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de création : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_TYPE_DELEGATION_spec(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de création : (16/08/11 15:48:02)
	 * 
	 */
	private void setLB_TYPE_REGIME_spec(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * @param listeAffectation
	 *            listeAffectation à définir
	 */
	private void setListeAffectation(ArrayList<Affectation> listeAffectation) {
		this.listeAffectation = listeAffectation;
	}

	/**
	 * Met à jour la liste des AvantageNature de l'affectation.
	 * 
	 * @param listeAvantageAFF
	 */
	private void setListeAvantageAFF(ArrayList<AvantageNature> listeAvantageAFF) {
		this.listeAvantageAFF = listeAvantageAFF;
	}

	/**
	 * Met à jour la liste des AvantageNature de la fiche de poste.
	 * 
	 * @param listeAvantageFP
	 *            listeAvantageFP à définir
	 */
	private void setListeAvantageFP(ArrayList<AvantageNature> listeAvantageFP) {
		this.listeAvantageFP = listeAvantageFP;
	}

	/**
	 * Met à jour la liste des Delegation de l'affectation.
	 * 
	 * @param listeDelegationAFF
	 */
	private void setListeDelegationAFF(ArrayList<Delegation> listeDelegationAFF) {
		this.listeDelegationAFF = listeDelegationAFF;
	}

	/**
	 * Met à jour la liste des Delegation de la fiche de poste.
	 * 
	 * @param listeDelegationFP
	 *            listeDelegationFP à définir
	 */
	private void setListeDelegationFP(ArrayList<Delegation> listeDelegationFP) {
		this.listeDelegationFP = listeDelegationFP;
	}

	/**
	 * Met à jour la liste des motifs d'affectation.
	 * 
	 * @param listeMotifAffectation
	 *            Nouvelle liste des motifs d'affectations
	 */
	private void setListeMotifAffectation(ArrayList<MotifAffectation> listeMotifAffectation) {
		this.listeMotifAffectation = listeMotifAffectation;
	}

	/**
	 * Met à jour la liste des natures d'avantage en nature.
	 * 
	 * @param listeNatureAvantage
	 */
	private void setListeNatureAvantage(ArrayList<NatureAvantage> listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	/**
	 * Met à jour la liste des PrimePointageIndemnitaire de l'affectation.
	 * 
	 * @param listePrimePointageAFF
	 */
	private void setListePrimePointageAFF(ArrayList<PrimePointageAff> listePrimePointageAFF) {
		this.listePrimePointageAFF = listePrimePointageAFF;
	}

	/**
	 * Met à jour la liste des PrimePointage de la fiche de poste.
	 * 
	 * @param listePrimePointageFP
	 */
	private void setListePrimePointageFP(ArrayList<PrimePointageFP> listePrimePointageFP) {
		this.listePrimePointageFP = listePrimePointageFP;
	}

	/**
	 * Met à jour la liste des primes.
	 * 
	 * @param listeRubrique
	 */
	private void setListePrimes(List<RefPrimeDto> listePrimes) {
		this.listePrimes = listePrimes;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire de l'affectation.
	 * 
	 * @param listeRegimeAFF
	 */
	private void setListeRegimeAFF(ArrayList<RegimeIndemnitaire> listeRegimeAFF) {
		this.listeRegimeAFF = listeRegimeAFF;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire de la fiche de poste.
	 * 
	 * @param listeRegimeFP
	 *            listeRegimeFP à définir
	 */
	private void setListeRegimeFP(ArrayList<RegimeIndemnitaire> listeRegimeFP) {
		this.listeRegimeFP = listeRegimeFP;
	}

	/**
	 * Met à jour la liste des rubriques.
	 * 
	 * @param listeRubrique
	 */
	private void setListeRubrique(List<Rubrique> listeRubrique) {
		this.listeRubrique = listeRubrique;
	}

	/**
	 * Met à jour la liste des temps de travail.
	 * 
	 * @param listeTempsTravail
	 *            Liste des temps de travail
	 */
	private void setListeTempsTravail(String[] listeTempsTravail) {
		this.listeTempsTravail = listeTempsTravail;
	}

	/**
	 * Met à jour la liste des types d'avantage en nature.
	 * 
	 * @param listeTypeAvantage
	 */
	private void setListeTypeAvantage(ArrayList<TypeAvantage> listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	/**
	 * Met à jour la liste des TypeDelegation.
	 * 
	 * @param listeTypeDelegation
	 */
	private void setListeTypeDelegation_spec(ArrayList<TypeDelegation> listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeTypeRegIndemn
	 */
	private void setListeTypeRegIndemn(ArrayList<TypeRegIndemn> listeTypeRegIndemn) {
		this.listeTypeRegIndemn = listeTypeRegIndemn;
	}

	public void setPrimeFP_AFF(int indiceRub) {

		// Alimentation de l'objet
		PrimePointageAff prime = new PrimePointageAff();
		prime.setNumRubrique(getListePrimePointageFP().get(indiceRub).getNumRubrique());

		if (getListePrimePointageAFF() == null)
			setListePrimePointageAFF(new ArrayList<PrimePointageAff>());

		if (!getListePrimePointageAFF().contains(prime) && !getListePrimePointageFP().contains(prime)
				&& !getListePrimePointageAffAAjouter().contains(prime)) {
			if (getListePrimePointageAffASupprimer().contains(prime)) {
				getListePrimePointageAffASupprimer().remove(prime);
				getListePrimePointageAFF().add(prime);
			} else {
				getListePrimePointageAffAAjouter().add(prime);
			}
		}

	}

	/**
	 * public int getListPosteCourant_IdRub(int index) { return
	 * getPrimePointageFPDao
	 * ().listerPrimePointageFP(Integer.parseInt(fichePosteCourant
	 * .getIdFichePoste())).get(index).getNumRubrique(); }
	 * 
	 * public int getListAffCourant_IdRub(int index) { return
	 * getPrimePointageAffDao
	 * ().listerPrimePointageAff(Integer.parseInt(affectationCourant
	 * .getIdAffectation())).get(index).getNumRubrique(); }
	 **/

	public void setPrimePointageAffDao(PrimePointageAffDao primePointageAffDao) {
		this.primePointageAffDao = primePointageAffDao;
	}

	public void setPrimePointageFPDao(PrimePointageFPDao primePointageFPDao) {
		this.primePointageFPDao = primePointageFPDao;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	// public String getNomEcran() {
	// return "ECR-AG-EMPLOIS-SPECIFICITES";
	// }

	private boolean verifieExistFichier(String idAffectation, String nomDoc) throws Exception {
		// on regarde si le fichier existe
		String nomSansExtension = nomDoc.substring(0, nomDoc.indexOf("."));
		Document.chercherDocumentByContainsNom(getTransaction(),
				"NS_" + idAffectation + "_" + nomSansExtension.substring(3, nomSansExtension.length()));
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
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
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderAvantageNature_spec() throws Exception {
		addZone(getNOM_LB_TYPE_AVANTAGE_SELECT_spec(), "0");
		addZone(getNOM_LB_NATURE_AVANTAGE_SELECT_spec(), "0");
		addZone(getNOM_EF_MONTANT_AVANTAGE_spec(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT(), "0");
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderDelegation_spec() throws Exception {
		addZone(getNOM_LB_TYPE_DELEGATION_SELECT_spec(), "0");
		addZone(getNOM_EF_COMMENT_DELEGATION_spec(), Const.CHAINE_VIDE);
	}

	/**
	 * Vide les champs de saisie des primes pointage.
	 * 
	 * @throws Exception
	 */
	private void viderPrimePointage() throws Exception {
		addZone(getNOM_LB_RUBRIQUE_PRIME_POINTAGE_SELECT(), Const.ZERO);
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderRegIndemn_spec() throws Exception {
		addZone(getNOM_LB_TYPE_REGIME_SELECT_spec(), "0");
		addZone(getNOM_EF_FORFAIT_REGIME_spec(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NB_POINTS_REGIME_spec(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT(), "0");
	}

	public boolean isPrimeModifiable() throws Exception {
		// TODO a supprimer quand les PTG-WS seront en prod
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		try {
			List<RefPrimeDto> primes = t.getPrimes();
		} catch (Exception e) {
			// TODO A SUPPRIMER QUAND PTG-WS SERA EN PROD
			return false;
		}
		return true;
	}

	public boolean isPrimeSupprimable(int indiceEltASupprimer) throws Exception {
		// TODO a supprimer quand les PTG-WS seront en prod
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();

		PrimePointageAff primePointage = null;
		// Si la spécificité à supprimer est déjà en base
		if (indiceEltASupprimer - getListePrimePointageFP().size() < getListePrimePointageAFF().size()) {
			primePointage = (PrimePointageAff) getListePrimePointageAFF().get(
					indiceEltASupprimer - getListePrimePointageFP().size());
		}
		// Si la spécificité à supprimer n'est pas encore en base mais vient
		// d'être ajoutée par l'utilisateur
		else {
			primePointage = (PrimePointageAff) getListePrimePointageAffAAjouter().get(
					indiceEltASupprimer - getListePrimePointageFP().size() - getListePrimePointageAFF().size());
		}
		if (primePointage == null) {
			return false;
		}
		// on interroge le WS pour savoir si la prime est utilisée sur un
		// pointage donné.
		try {
			return t.isPrimeUtilPointage(primePointage.getNumRubrique(),
					Integer.valueOf(getAgentCourant().getIdAgent()));
		} catch (Exception e) {
			// TODO A SUPPRIMER QUAND PTG-WS SERA EN PROD
			return false;
		}

	}

}