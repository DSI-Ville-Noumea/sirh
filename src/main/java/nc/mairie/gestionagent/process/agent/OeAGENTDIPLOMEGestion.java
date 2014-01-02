package nc.mairie.gestionagent.process.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.SpecialiteDiplomeNW;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.diplome.FormationAgentDao;
import nc.mairie.spring.dao.metier.diplome.PermisAgentDao;
import nc.mairie.spring.dao.metier.parametrage.CentreFormationDao;
import nc.mairie.spring.dao.metier.parametrage.TitreFormationDao;
import nc.mairie.spring.dao.metier.parametrage.TitrePermisDao;
import nc.mairie.spring.domain.metier.diplome.FormationAgent;
import nc.mairie.spring.domain.metier.diplome.PermisAgent;
import nc.mairie.spring.domain.metier.parametrage.CentreFormation;
import nc.mairie.spring.domain.metier.parametrage.TitreFormation;
import nc.mairie.spring.domain.metier.parametrage.TitrePermis;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAGENTDIPLOMEGestion Date de création : (11/02/03 14:20:31)
 * 
 */
public class OeAGENTDIPLOMEGestion extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private Logger logger = LoggerFactory.getLogger(OeAGENTDIPLOMEGestion.class);

	private String[] LB_TITRE_DIPLOME;
	private String[] LB_SPECIALITE_DIPLOME;
	private String[] LB_UNITE_DUREE;

	private String[] LB_TITRE_PERMIS;

	public String ACTION_SUPPRESSION_DIPLOME = "Suppression d'un diplôme.";
	public String ACTION_MODIFICATION_DIPLOME = "Modification d'un diplôme.";
	public String ACTION_CREATION_DIPLOME = "Création d'un diplôme.";
	public String ACTION_CONSULTATION_DIPLOME = "Consultation d'un diplôme.";

	public String ACTION_SUPPRESSION_FORMATION = "Suppression d'une formation.";
	public String ACTION_MODIFICATION_FORMATION = "Modification d'une formation.";
	public String ACTION_CREATION_FORMATION = "Création d'une formation.";
	public String ACTION_CONSULTATION_FORMATION = "Consultation d'une formation.";

	public String ACTION_SUPPRESSION_PERMIS = "Suppression d'un permis.";
	public String ACTION_MODIFICATION_PERMIS = "Modification d'un permis.";
	public String ACTION_CREATION_PERMIS = "Création d'un permis.";
	public String ACTION_CONSULTATION_PERMIS = "Consultation d'un permis.";

	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document.";
	private ArrayList<Document> listeDocuments;
	private Document documentCourant;
	private LienDocumentAgent lienDocument;
	private String urlFichier;
	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

	private AgentNW AgentCourant;
	private Hashtable<String, TitreDiplome> hashTitreDiplome;
	private Hashtable<String, SpecialiteDiplomeNW> hashSpeDiplome;
	private ArrayList<DiplomeAgent> listeDiplomesAgent;
	private ArrayList<TitreDiplome> listeTitreDiplome;
	private ArrayList<SpecialiteDiplomeNW> listeSpeDiplome;
	private DiplomeAgent diplomeAgentCourant;
	private TitreDiplome titreDiplomeCourant;
	private ArrayList<DiplomeAgent> listeEcoles;
	private ArrayList<String> listeUniteDuree;

	private ArrayList<FormationAgent> listeFormationsAgent;
	private Hashtable<Integer, TitreFormation> hashTitreFormation;
	private Hashtable<Integer, CentreFormation> hashCentreFormation;
	private FormationAgent formationAgentCourant;
	private ArrayList<TitreFormation> listeTitreFormation;
	private ArrayList<CentreFormation> listeCentreFormation;

	private ArrayList<PermisAgent> listePermisAgent;
	private PermisAgent permisAgentCourant;
	private ArrayList<TitrePermis> listeTitrePermis;

	public String focus = null;

	private TitreFormationDao titreFormationDao;
	private CentreFormationDao centreFormationDao;
	private FormationAgentDao formationAgentDao;
	private TitrePermisDao titrePermisDao;
	private PermisAgentDao permisAgentDao;

	/**
	 * Insérez la description de la méthode ici. Date de création : (11/02/2003
	 * 15:15:56)
	 * 
	 * @return nc.mairie.metier.agent.Agent
	 */
	public AgentNW getAgentCourant() {
		return AgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 11:01:39)
	 * 
	 * @return nc.mairie.metier.agent.Contact
	 */
	private DiplomeAgent getDiplomeAgentCourant() {
		return diplomeAgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (11/06/2003
	 * 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, TitreDiplome> getHashTitreDiplome() {
		if (hashTitreDiplome == null) {
			hashTitreDiplome = new Hashtable<String, TitreDiplome>();
		}
		return hashTitreDiplome;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_DIPLOME Date de
	 * création : (11/02/03 14:20:32)
	 * 
	 */
	private String[] getLB_TITRE_DIPLOME() {
		if (LB_TITRE_DIPLOME == null)
			LB_TITRE_DIPLOME = initialiseLazyLB();
		return LB_TITRE_DIPLOME;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:38:02)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<DiplomeAgent> getListeDiplomesAgent() {
		if (listeDiplomesAgent == null) {
			listeDiplomesAgent = new ArrayList<DiplomeAgent>();
		}
		return listeDiplomesAgent;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:47:43)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<TitreDiplome> getListeTitreDiplome() {
		if (listeTitreDiplome == null) {
			listeTitreDiplome = new ArrayList<TitreDiplome>();
		}
		return listeTitreDiplome;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_OBTENTION_DIPLOME Date de création : (11/06/03 15:05:22)
	 * 
	 */
	public String getNOM_EF_DATE_OBTENTION_DIPLOME() {
		return "NOM_EF_DATE_OBTENTION_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME Date de création
	 * : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_DIPLOME() {
		return "NOM_LB_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_SELECT Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_DIPLOME_SELECT() {
		return "NOM_LB_DIPLOME_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_DIPLOME Date de
	 * création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_TITRE_DIPLOME() {
		return "NOM_LB_TITRE_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_DIPLOME_SELECT Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_TITRE_DIPLOME_SELECT() {
		return "NOM_LB_TITRE_DIPLOME_SELECT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER_DIPLOME() {
		return "NOM_PB_ANNULER_DIPLOME";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_CREER_DIPLOME() {
		return "NOM_PB_CREER_DIPLOME";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DIPLOME Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_DIPLOME() {
		return "NOM_PB_DIPLOME";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_TITRE_DIPLOME Date de
	 * création : (11/06/03 15:05:22)
	 * 
	 */
	public String getNOM_PB_TITRE_DIPLOME() {
		return "NOM_PB_TITRE_DIPLOME";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_DIPLOME Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER_DIPLOME() {
		return "NOM_PB_VALIDER_DIPLOME";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DIPLOME Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_ACTION_DIPLOME() {
		return "NOM_ST_ACTION_DIPLOME";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIPLOME Date de
	 * création : (11/06/03 15:05:22)
	 * 
	 */
	public String getNOM_ST_DIPLOME() {
		return "NOM_ST_DIPLOME";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NIVEAU Date de
	 * création : (11/06/03 15:05:22)
	 * 
	 */
	public String getNOM_ST_NIVEAU() {
		return "NOM_ST_NIVEAU";
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (12/06/2003
	 * 10:46:00)
	 * 
	 * @return nc.mairie.metier.diplome.TitreDiplome
	 */
	private TitreDiplome getTitreDiplomeCourant() {
		return titreDiplomeCourant;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_OBTENTION Date de création : (11/06/03 15:05:22)
	 * 
	 */
	public String getVAL_EF_DATE_OBTENTION_DIPLOME() {
		return getZone(getNOM_EF_DATE_OBTENTION_DIPLOME());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_DIPLOME Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String[] getVAL_LB_TITRE_DIPLOME() {
		return getLB_TITRE_DIPLOME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE_DIPLOME Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getVAL_LB_TITRE_DIPLOME_SELECT() {
		return getZone(getNOM_LB_TITRE_DIPLOME_SELECT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_DIPLOME
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_ACTION_DIPLOME() {
		return getZone(getNOM_ST_ACTION_DIPLOME());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIPLOME Date
	 * de création : (11/06/03 15:05:22)
	 * 
	 */
	public String getVAL_ST_DIPLOME() {
		return getZone(getNOM_ST_DIPLOME());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NIVEAU Date de
	 * création : (11/06/03 15:05:22)
	 * 
	 */
	public String getVAL_ST_NIVEAU() {
		return getZone(getNOM_ST_NIVEAU());
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	private boolean initialiseDiplomeCourant(HttpServletRequest request) throws Exception {
		DiplomeAgent d = getDiplomeAgentCourant();
		TitreDiplome t = (TitreDiplome) getHashTitreDiplome().get(d.getIdTitreDiplome());
		setTitreDiplomeCourant(t);
		SpecialiteDiplomeNW s = (SpecialiteDiplomeNW) getHashSpeDiplome().get(d.getIdSpecialiteDiplome());
		// setSpeDiplomeCourant(s);

		// Alim zones
		// Titre diplome
		int ligneTitre = getListeTitreDiplome().indexOf(t);
		addZone(getNOM_LB_TITRE_DIPLOME_SELECT(), String.valueOf(ligneTitre + 1));
		// Niveau
		addZone(getNOM_ST_NIVEAU(), t.getNiveauEtude());

		// Specialite
		int ligneSpecialite = getListeSpeDiplome().indexOf(s);
		addZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT(), String.valueOf(ligneSpecialite + 1));
		addZone(getNOM_ST_SPECIALITE_DIPLOME(), s.getLibSpeDiplome());

		// Last addZone
		addZone(getNOM_EF_DATE_OBTENTION_DIPLOME(), d.getDateObtention());
		addZone(getNOM_EF_NOM_ECOLE(), d.getNomEcole());

		return true;
	}

	private boolean initialiseDiplomeSuppression(HttpServletRequest request) throws Exception {

		// Récup du Diplome courant
		DiplomeAgent d = getDiplomeAgentCourant();
		TitreDiplome t = (TitreDiplome) getHashTitreDiplome().get(d.getIdTitreDiplome());
		setTitreDiplomeCourant(t);
		SpecialiteDiplomeNW s = (SpecialiteDiplomeNW) getHashSpeDiplome().get(d.getIdSpecialiteDiplome());

		if (getTransaction().isErreur())
			return false;

		// Alim zone Niveau
		addZone(getNOM_ST_NIVEAU(), t.getNiveauEtude());

		// Alim zone Specialite
		addZone(getNOM_ST_SPECIALITE_DIPLOME(), s.getLibSpeDiplome());

		// Last addZone
		addZone(getNOM_EF_DATE_OBTENTION_DIPLOME(), d.getDateObtention());
		addZone(getNOM_ST_TITRE(), t.getLibTitreDiplome());
		addZone(getNOM_EF_NOM_ECOLE(), d.getNomEcole());

		return true;
	}

	/**
	 * Initialisation de la liste des formation
	 * 
	 */
	private void initialiseListeFormationsAgent(HttpServletRequest request) throws Exception {

		// Recherche des formations de l'agent
		ArrayList<FormationAgent> a = getFormationAgentDao().listerFormationAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()));
		setListeFormationsAgent(a);

		int indiceFormation = 0;
		if (getListeFormationsAgent() != null) {
			for (int i = 0; i < getListeFormationsAgent().size(); i++) {
				FormationAgent d = (FormationAgent) getListeFormationsAgent().get(i);
				TitreFormation t = getTitreFormationDao().chercherTitreFormation(d.getIdTitreFormation());

				addZone(getNOM_ST_FORMATION(indiceFormation), t.getLibTitreFormation());
				addZone(getNOM_ST_ANNEE(indiceFormation), d.getAnneeFormation().toString());

				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(),
						getAgentCourant(), "DONNEES PERSONNELLES", "FORM", d.getIdFormation().toString());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}
				addZone(getNOM_ST_NB_DOC_FORMATION(indiceFormation), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceFormation++;
			}
		}
	}

	/**
	 * Initialisation de la liste des diplomes
	 * 
	 */
	private void initialiseListeDiplomesAgent(HttpServletRequest request) throws Exception {

		// Recherche des diplomes de l'agent
		ArrayList<DiplomeAgent> a = DiplomeAgent.listerDiplomeAgentAvecAgent(getTransaction(), getAgentCourant());
		setListeDiplomesAgent(a);

		int indiceDiplome = 0;
		if (getListeDiplomesAgent() != null) {
			for (int i = 0; i < getListeDiplomesAgent().size(); i++) {
				DiplomeAgent d = (DiplomeAgent) getListeDiplomesAgent().get(i);
				TitreDiplome t = (TitreDiplome) getHashTitreDiplome().get(d.getIdTitreDiplome());
				String titre = t.getLibTitreDiplome();
				SpecialiteDiplomeNW spec = SpecialiteDiplomeNW.chercherSpecialiteDiplomeNW(getTransaction(),
						d.getIdSpecialiteDiplome());

				addZone(getNOM_ST_TITRE_DIPLOME(indiceDiplome), titre);
				addZone(getNOM_ST_SPE_DIPLOME(indiceDiplome), spec.getLibSpeDiplome());
				addZone(getNOM_ST_NIVEAU(indiceDiplome),
						t.getNiveauEtude().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getNiveauEtude());

				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(),
						getAgentCourant(), "DONNEES PERSONNELLES", "DIP", d.getIdDiplome());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}
				addZone(getNOM_ST_NB_DOC_DIPLOME(indiceDiplome), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceDiplome++;
			}
		}

		addZone(getNOM_LB_TITRE_DIPLOME_SELECT(), Const.ZERO);
		addZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT(), Const.ZERO);
		addZone(getNOM_EF_NOM_ECOLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_OBTENTION_DIPLOME(), Const.CHAINE_VIDE);
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		initialiseListeDeroulante();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				addZone(getNOM_ST_ACTION_FORMATION(), Const.CHAINE_VIDE);
				addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);
				addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
				addZone(getNOM_ST_ACTION_PERMIS(), Const.CHAINE_VIDE);
				setAgentCourant(aAgent);
				initialiseListeDiplomesAgent(request);
				initialiseListeFormationsAgent(request);
				initialiseListePermisAgent(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseListeDeroulante() throws Exception {
		// liste des ecoles
		if (getListeEcoles().size() == 0) {
			ArrayList<DiplomeAgent> listeEcole = DiplomeAgent.listerEcolesDiplomeAgent(getTransaction());
			setListeEcoles(listeEcole);
		}

		// Si liste des titres vide
		if (getLB_TITRE_DIPLOME() == LBVide) {
			ArrayList<TitreDiplome> a = TitreDiplome.listerTitreDiplome(getTransaction());
			setListeTitreDiplome(a);

			int[] tailles = { 70 };
			String[] champs = { "libTitreDiplome" };
			setLB_TITRE_DIPLOME(new FormateListe(tailles, a, champs).getListeFormatee(true));
		}

		// Si liste des spécialités vide
		if (getLB_SPECIALITE_DIPLOME() == LBVide) {
			ArrayList<SpecialiteDiplomeNW> a = SpecialiteDiplomeNW.listerSpecialiteDiplomeNW(getTransaction());
			setListeSpeDiplome(a);

			int[] tailles = { 70 };
			String[] champs = { "libSpeDiplome" };
			setLB_SPECIALITE_DIPLOME(new FormateListe(tailles, a, champs).getListeFormatee(true));
		}

		// Si hashtable des titres vide ou statut gestion diplomes
		if (getHashTitreDiplome().size() == 0) {
			ArrayList<TitreDiplome> a = TitreDiplome.listerTitreDiplome(getTransaction());
			setListeTitreDiplome(a);
			// remplissage de la hashTable
			for (int i = 0; i < a.size(); i++) {
				TitreDiplome aTitreDiplome = (TitreDiplome) a.get(i);
				getHashTitreDiplome().put(aTitreDiplome.getIdTitreDiplome(), aTitreDiplome);
			}
		}

		// Si hashtable des specialites vide
		if (getHashSpeDiplome().size() == 0) {
			ArrayList<SpecialiteDiplomeNW> a = SpecialiteDiplomeNW.listerSpecialiteDiplomeNW(getTransaction());
			setListeSpeDiplome(a);
			// remplissage de la hashTable
			for (int i = 0; i < a.size(); i++) {
				SpecialiteDiplomeNW aSpeDiplome = (SpecialiteDiplomeNW) a.get(i);
				getHashSpeDiplome().put(aSpeDiplome.getIdSpeDiplome(), aSpeDiplome);
			}
		}

		// Si liste titre formation vide alors affectation
		if (getListeTitreFormation() == null || getListeTitreFormation().size() == 0) {
			ArrayList<TitreFormation> listeTitreFormation = getTitreFormationDao().listerTitreFormation();
			setListeTitreFormation(listeTitreFormation);
		}

		// Si hashtable des titres formation vide
		if (getHashTitreFormation().size() == 0) {
			// remplissage de la hashTable
			for (int i = 0; i < getListeTitreFormation().size(); i++) {
				TitreFormation aTitreFormation = (TitreFormation) getListeTitreFormation().get(i);
				getHashTitreFormation().put(aTitreFormation.getIdTitreFormation(), aTitreFormation);
			}
		}

		// Si liste centre formation vide alors affectation
		if (getListeCentreFormation() == null || getListeCentreFormation().size() == 0) {
			ArrayList<CentreFormation> listeCentreFormation = getCentreFormationDao().listerCentreFormation();
			setListeCentreFormation(listeCentreFormation);
		}

		// Si hashtable des centres formation vide
		if (getHashCentreFormation().size() == 0) {
			// remplissage de la hashTable
			for (int i = 0; i < getListeCentreFormation().size(); i++) {
				CentreFormation aCentreFormation = (CentreFormation) getListeCentreFormation().get(i);
				getHashCentreFormation().put(aCentreFormation.getIdCentreFormation(), aCentreFormation);
			}
		}

		// Si liste unité durée vide
		if (getLB_UNITE_DUREE() == LBVide) {
			ArrayList<String> listeUniteDuree = new ArrayList<String>();
			listeUniteDuree.add("heures");
			listeUniteDuree.add("jours");
			listeUniteDuree.add("mois");
			setListeUniteDuree(listeUniteDuree);
			int[] tailles = { 10 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeUniteDuree.listIterator(); list.hasNext();) {
				String unite = (String) list.next();
				String ligne[] = { unite };
				aFormat.ajouteLigne(ligne);
			}
			setLB_UNITE_DUREE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_UNITE_DUREE_SELECT(), Const.ZERO);
		}
		// Si liste titre permis vide alors affectation
		if (getLB_TITRE_PERMIS() == LBVide) {
			ArrayList<TitrePermis> listeTitrePermis = getTitrePermisDao().listerTitrePermis();
			setListeTitrePermis(listeTitrePermis);
			int[] tailles = { 25 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<TitrePermis> list = listeTitrePermis.listIterator(); list.hasNext();) {
				TitrePermis permis = (TitrePermis) list.next();
				String ligne[] = { permis.getLibTitrePermis() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_TITRE_PERMIS(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_TITRE_PERMIS_SELECT(), Const.ZERO);
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTitreFormationDao() == null) {
			setTitreFormationDao((TitreFormationDao) context.getBean("titreFormationDao"));
		}
		if (getCentreFormationDao() == null) {
			setCentreFormationDao((CentreFormationDao) context.getBean("centreFormationDao"));
		}
		if (getFormationAgentDao() == null) {
			setFormationAgentDao((FormationAgentDao) context.getBean("formationAgentDao"));
		}
		if (getTitrePermisDao() == null) {
			setTitrePermisDao((TitrePermisDao) context.getBean("titrePermisDao"));
		}
		if (getPermisAgentDao() == null) {
			setPermisAgentDao((PermisAgentDao) context.getBean("permisAgentDao"));
		}
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER_DIPLOME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);
		performPB_RESET(request);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_CREER_DIPLOME(HttpServletRequest request) throws Exception {

		// On vide la zone de saisie
		addZone(getNOM_LB_TITRE_DIPLOME_SELECT(), "-1");
		addZone(getNOM_ST_DIPLOME(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NIVEAU(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT(), "-1");
		addZone(getNOM_EF_NOM_ECOLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_OBTENTION_DIPLOME(), Const.CHAINE_VIDE);
		setListeDocuments(null);

		// init du diplome courant
		setDiplomeAgentCourant(new DiplomeAgent());

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_CREATION_DIPLOME);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_TITRE_DIPLOME(HttpServletRequest request) throws Exception {

		// Test si ligne sélectionnée
		int numligne = (Services.estNumerique(getZone(getNOM_LB_TITRE_DIPLOME_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_TITRE_DIPLOME_SELECT())) : -1);
		if (numligne == -1 || getListeTitreDiplome().size() == 0 || numligne > getListeTitreDiplome().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
			return false;
		}

		// Récup du titre sélectionné
		TitreDiplome aTitre = (numligne > 0 ? (TitreDiplome) getListeTitreDiplome().get(numligne - 1) : null);
		setTitreDiplomeCourant(aTitre);

		// init du niveau
		if (getTitreDiplomeCourant() == null) {
			addZone(getNOM_ST_NIVEAU(), Const.CHAINE_VIDE);
		} else {
			addZone(getNOM_ST_NIVEAU(), aTitre.getNiveauEtude());
			if (getTransaction().isErreur())
				return false;
		}

		setStatut(STATUT_MEME_PROCESS, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_DIPLOME(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION_DIPLOME()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Récup des zones saisies
		String newNomEcole = getZone(getNOM_EF_NOM_ECOLE());
		String newDateObt = getZone(getNOM_EF_DATE_OBTENTION_DIPLOME());

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION_DIPLOME()).equals(ACTION_SUPPRESSION_DIPLOME)) {
			// Suppression
			getDiplomeAgentCourant().supprimerDiplomeAgent(getTransaction());
			if (getTransaction().isErreur())
				return false;

			// il faut supprimer les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document d = getListeDocuments().get(i);
				LienDocumentAgent lien = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(),
						getAgentCourant().getIdAgent(), d.getIdDocument());
				// suppression dans table DOCUMENT_AGENT
				lien.supprimerLienDocumentAgent(getTransaction());
				// Suppression dans la table DOCUMENT_ASSOCIE
				d.supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = d.getLienDocument().replace("/", "\\");
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.error("Erreur suppression physique du fichier : " + e.toString());
				}
			}

		} else {
			if (!performControlerChampsDiplome(request)) {
				return false;
			}
			if (!performControlerRGDiplome(request)) {
				return false;
			}
			// Recup du titre
			int numligneTitre = (Services.estNumerique(getZone(getNOM_LB_TITRE_DIPLOME_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TITRE_DIPLOME_SELECT())) : -1);
			if (numligneTitre == -1 || getListeTitreDiplome().size() == 0
					|| numligneTitre > getListeTitreDiplome().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
				return false;
			}
			TitreDiplome newTitre = (numligneTitre > 0 ? (TitreDiplome) getListeTitreDiplome().get(numligneTitre - 1)
					: null);

			// Recup de la spécialité
			int numligneSpe = (Services.estNumerique(getZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT())) : -1);
			if (numligneSpe == -1 || getListeSpeDiplome().size() == 0 || numligneSpe > getListeSpeDiplome().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Spécialités"));
				return false;
			}
			SpecialiteDiplomeNW newSpec = (numligneSpe > 0 ? (SpecialiteDiplomeNW) getListeSpeDiplome().get(
					numligneSpe - 1) : null);

			// Affectation des attributs
			getDiplomeAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
			getDiplomeAgentCourant().setDateObtention(newDateObt);
			getDiplomeAgentCourant().setIdSpecialiteDiplome(newSpec.getIdSpeDiplome());
			getDiplomeAgentCourant().setNomEcole(newNomEcole.toUpperCase());
			getDiplomeAgentCourant().setIdTitreDiplome(newTitre.getIdTitreDiplome());
			if (getZone(getNOM_ST_ACTION_DIPLOME()).equals(ACTION_MODIFICATION_DIPLOME)) {
				// Modification
				getDiplomeAgentCourant().modifierDiplomeAgent(getTransaction());
			} else if (getZone(getNOM_ST_ACTION_DIPLOME()).equals(ACTION_CREATION_DIPLOME)) {
				// Création
				getDiplomeAgentCourant().creerDiplomeAgent(getTransaction());
			}

			// pour le nom des ecoles
			setListeEcoles(null);

			if (getTransaction().isErreur())
				return false;

			// on fait la gestion des documents
			performPB_VALIDER_DOCUMENT_DIPLOME_CREATION(request,
					Integer.valueOf(getDiplomeAgentCourant().getIdDiplome()));
		}

		// Tout s'est bien passé
		commitTransaction();
		initialiseListeDiplomesAgent(request);
		setTitreDiplomeCourant(new TitreDiplome());

		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);

		return true;
	}

	private boolean performControlerRGDiplome(HttpServletRequest request) {
		// Vérification des contraintes d'unicité du diplome
		if (getVAL_ST_ACTION_DIPLOME().equals(ACTION_CREATION_DIPLOME)) {

			// Recup du titre
			int numligneTitre = (Services.estNumerique(getZone(getNOM_LB_TITRE_DIPLOME_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TITRE_DIPLOME_SELECT())) : -1);
			if (numligneTitre == -1 || getListeTitreDiplome().size() == 0
					|| numligneTitre > getListeTitreDiplome().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
				return false;
			}
			TitreDiplome newTitre = (numligneTitre > 0 ? (TitreDiplome) getListeTitreDiplome().get(numligneTitre - 1)
					: null);

			// Recup de la spécialité
			int numligneSpe = (Services.estNumerique(getZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT())) : -1);
			if (numligneSpe == -1 || getListeSpeDiplome().size() == 0 || numligneSpe > getListeSpeDiplome().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Spécialités"));
				return false;
			}
			SpecialiteDiplomeNW newSpec = (numligneSpe > 0 ? (SpecialiteDiplomeNW) getListeSpeDiplome().get(
					numligneSpe - 1) : null);

			for (DiplomeAgent diplome : getListeDiplomesAgent()) {
				if (diplome.getIdAgent().equals(getAgentCourant().getIdAgent())
						&& diplome.getDateObtention().equals(Services.formateDate(getVAL_EF_DATE_OBTENTION_DIPLOME()))
						&& diplome.getIdSpecialiteDiplome().equals(newSpec.getIdSpeDiplome())
						&& diplome.getNomEcole().equals(getVAL_EF_NOM_ECOLE().toUpperCase())
						&& diplome.getIdTitreDiplome().equals(newTitre.getIdTitreDiplome())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un diplôme", "ces valeurs"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 * @param idDiplomeAgent
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_DIPLOME_CREATION(HttpServletRequest request, Integer idDiplomeAgent)
			throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {

			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());

				// Contrôle des champs
				if (!performControlerSaisieDocument(request))
					return false;

				if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_CREATION)) {
					if (!creeDocumentDiplome(request, idDiplomeAgent)) {
						return false;
					}
				}
			}
		} else {
			if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_SUPPRESSION)) {
				// suppression dans table DOCUMENT_AGENT
				getLienDocument().supprimerLienDocumentAgent(getTransaction());
				// Suppression dans la table DOCUMENT_ASSOCIE
				getDocumentCourant().supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = getDocumentCourant().getLienDocument();
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.error("Erreur suppression physique du fichier : " + e.toString());
				}

				// tout s'est bien passé
				commitTransaction();

			}

		}

		initialiseListeDocumentsDiplome(request);
		return true;
	}

	private boolean creeDocumentDiplome(HttpServletRequest request, Integer idDiplomeAgent) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupère le type de document
		String codTypeDoc = "DIP";
		TypeDocument td = TypeDocument.chercherTypeDocumentByCod(getTransaction(), codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + idDiplomeAgent + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf"))
			upload = uploadFichierPDF(fichierUpload, nom, codTypeDoc);
		else
			upload = uploadFichier(fichierUpload, nom, codTypeDoc);

		if (!upload)
			return false;

		// on crée le document en base de données
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomDocument(nom);
		getDocumentCourant().setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		getDocumentCourant().creerDocument(getTransaction());

		setLienDocument(new LienDocumentAgent());
		getLienDocument().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocument().setIdDocument(getDocumentCourant().getIdDocument());
		getLienDocument().creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private boolean creeDocumentFormation(HttpServletRequest request, Integer idFormationAgent) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupère le type de document
		String codTypeDoc = "FORM";
		TypeDocument td = TypeDocument.chercherTypeDocumentByCod(getTransaction(), codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + idFormationAgent + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf"))
			upload = uploadFichierPDF(fichierUpload, nom, codTypeDoc);
		else
			upload = uploadFichier(fichierUpload, nom, codTypeDoc);

		if (!upload)
			return false;

		// on crée le document en base de données
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomDocument(nom);
		getDocumentCourant().setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		getDocumentCourant().creerDocument(getTransaction());

		setLienDocument(new LienDocumentAgent());
		getLienDocument().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocument().setIdDocument(getDocumentCourant().getIdDocument());
		getLienDocument().creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private boolean creeDocumentPermis(HttpServletRequest request, Integer idPermisAgent) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupère le type de document
		String codTypeDoc = "PERM";
		TypeDocument td = TypeDocument.chercherTypeDocumentByCod(getTransaction(), codTypeDoc);
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String nom = codTypeDoc.toUpperCase() + "_" + idPermisAgent + "_" + dateJour + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf"))
			upload = uploadFichierPDF(fichierUpload, nom, codTypeDoc);
		else
			upload = uploadFichier(fichierUpload, nom, codTypeDoc);

		if (!upload)
			return false;

		// on crée le document en base de données
		getDocumentCourant().setLienDocument(codTypeDoc + "/" + nom);
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomDocument(nom);
		getDocumentCourant().setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		getDocumentCourant().creerDocument(getTransaction());

		setLienDocument(new LienDocumentAgent());
		getLienDocument().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocument().setIdDocument(getDocumentCourant().getIdDocument());
		getLienDocument().creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...)
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 *             RG_AG_DI_C01 RG_AG_DI_C02
	 */
	public boolean performControlerChampsDiplome(HttpServletRequest request) throws Exception {

		// titre du diplôme
		int indice = (Services.estNumerique(getVAL_LB_TITRE_DIPLOME_SELECT()) ? Integer
				.parseInt(getVAL_LB_TITRE_DIPLOME_SELECT()) : -1);
		if (indice < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Titre du diplôme"));
			setFocus(getNOM_LB_TITRE_DIPLOME());
			return false;
		}

		// spécialité obligatoire
		int indiceSpe = (Services.estNumerique(getVAL_LB_SPECIALITE_DIPLOME_SELECT()) ? Integer
				.parseInt(getVAL_LB_SPECIALITE_DIPLOME_SELECT()) : -1);
		if (indiceSpe < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Spécialité du diplôme"));
			setFocus(getNOM_LB_SPECIALITE_DIPLOME());
			return false;
		}

		// date obtention
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_OBTENTION_DIPLOME())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date d'obtention"));
			return false;
		}
		if (!Services.estUneDate(getVAL_EF_DATE_OBTENTION_DIPLOME())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date d'obtention"));
			setFocus(getNOM_EF_DATE_OBTENTION_DIPLOME());
			return false;
		} else if (Services.compareDates(getAgentCourant().getDateNaissance(), getVAL_EF_DATE_OBTENTION_DIPLOME()) > -1
				|| Services.compareDates(getVAL_EF_DATE_OBTENTION_DIPLOME(), Services.dateDuJour()) > 0) {
			// "ERR202",
			// "La date @ doit être comprise entre la date @ et la date @."
			// RG_AG_DI_C01
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR202", "d'obtention", "de naissance de l'agent", "du jour"));
			setFocus(getNOM_EF_DATE_OBTENTION_DIPLOME());
			return false;
		}

		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (11/02/2003
	 * 15:15:56)
	 * 
	 * @param newAgentCourant
	 *            nc.mairie.metier.agent.Agent
	 */
	private void setAgentCourant(AgentNW newAgentCourant) {
		AgentCourant = newAgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 11:01:39)
	 * 
	 * @param newContactCourant
	 *            nc.mairie.metier.agent.Contact
	 */
	private void setDiplomeAgentCourant(DiplomeAgent newDiplomeAgentCourant) {
		diplomeAgentCourant = newDiplomeAgentCourant;
	}

	/**
	 * Setter de la liste: LB_TITRE_DIPLOME Date de création : (11/02/03
	 * 14:20:32)
	 * 
	 */
	private void setLB_TITRE_DIPLOME(String[] newLB_TITRE_DIPLOME) {
		LB_TITRE_DIPLOME = newLB_TITRE_DIPLOME;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:38:02)
	 * 
	 * @param newListeDiplomesAgent
	 *            ArrayList
	 */
	private void setListeDiplomesAgent(ArrayList<DiplomeAgent> newListeDiplomesAgent) {
		listeDiplomesAgent = newListeDiplomesAgent;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:47:43)
	 * 
	 * @param newListeTitreDiplome
	 *            ArrayList
	 */
	private void setListeTitreDiplome(ArrayList<TitreDiplome> newListeTitreDiplome) {
		listeTitreDiplome = newListeTitreDiplome;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (12/06/2003
	 * 10:46:00)
	 * 
	 * @param newTitreDiplomeCourant
	 *            nc.mairie.metier.diplome.TitreDiplome
	 */
	private void setTitreDiplomeCourant(TitreDiplome newTitreDiplomeCourant) {
		titreDiplomeCourant = newTitreDiplomeCourant;
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

	public String getDefaultFocus() {
		return getNOM_EF_DATE_OBTENTION_DIPLOME();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATEOBT Date de
	 * création : (29/09/08 10:18:46)
	 * 
	 */
	public String getNOM_ST_DATEOBT() {
		return "NOM_ST_DATEOBT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATEOBT Date
	 * de création : (29/09/08 10:18:46)
	 * 
	 */
	public String getVAL_ST_DATEOBT() {
		return getZone(getNOM_ST_DATEOBT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATEVAL Date de
	 * création : (29/09/08 10:18:46)
	 * 
	 */
	public String getNOM_ST_DATEVAL() {
		return "NOM_ST_DATEVAL";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATEVAL Date
	 * de création : (29/09/08 10:18:46)
	 * 
	 */
	public String getVAL_ST_DATEVAL() {
		return getZone(getNOM_ST_DATEVAL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (29/09/08 10:18:46)
	 * 
	 */
	public String getNOM_ST_TITRE() {
		return "NOM_ST_TITRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (29/09/08 10:18:46)
	 * 
	 */
	public String getVAL_ST_TITRE() {
		return getZone(getNOM_ST_TITRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_ECOLE Date de
	 * création : (09/05/11 16:27:58)
	 * 
	 */
	public String getNOM_EF_NOM_ECOLE() {
		return "NOM_EF_NOM_ECOLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_ECOLE Date de création : (09/05/11 16:27:58)
	 * 
	 */
	public String getVAL_EF_NOM_ECOLE() {
		return getZone(getNOM_EF_NOM_ECOLE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_ECOLE Date de
	 * création : (10/05/11 14:47:09)
	 * 
	 */
	public String getNOM_ST_NOM_ECOLE() {
		return "NOM_ST_NOM_ECOLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_ECOLE Date
	 * de création : (10/05/11 14:47:09)
	 * 
	 */
	public String getVAL_ST_NOM_ECOLE() {
		return getZone(getNOM_ST_NOM_ECOLE());
	}

	/**
	 * Retourne la liste des spécialités des diplomes.
	 * 
	 * @return listeSpeDiplome ArrayList d'objets SpecialiteDiplomeNW
	 */
	private ArrayList<SpecialiteDiplomeNW> getListeSpeDiplome() {
		return listeSpeDiplome;
	}

	/**
	 * Met à jour la liste des specialites des diplomes
	 * 
	 * @param listeSpeDiplome
	 *            listeSpeDiplome à définir
	 */
	private void setListeSpeDiplome(ArrayList<SpecialiteDiplomeNW> listeSpeDiplome) {
		this.listeSpeDiplome = listeSpeDiplome;
	}

	/**
	 * Retourne les specialites dans une table de hashage Date de création :
	 * (11/06/2003 15:37:08)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<String, SpecialiteDiplomeNW> getHashSpeDiplome() {
		if (hashSpeDiplome == null) {
			hashSpeDiplome = new Hashtable<String, SpecialiteDiplomeNW>();
		}
		return hashSpeDiplome;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_SPECIALITE_DIPLOME Date
	 * de création : (15/06/11 13:49:36)
	 * 
	 */
	private String[] getLB_SPECIALITE_DIPLOME() {
		if (LB_SPECIALITE_DIPLOME == null)
			LB_SPECIALITE_DIPLOME = initialiseLazyLB();
		return LB_SPECIALITE_DIPLOME;
	}

	/**
	 * Setter de la liste: LB_SPECIALITE_DIPLOME Date de création : (15/06/11
	 * 13:49:36)
	 * 
	 */
	private void setLB_SPECIALITE_DIPLOME(String[] newLB_SPECIALITE_DIPLOME) {
		LB_SPECIALITE_DIPLOME = newLB_SPECIALITE_DIPLOME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_SPECIALITE_DIPLOME Date
	 * de création : (15/06/11 13:49:36)
	 * 
	 */
	public String getNOM_LB_SPECIALITE_DIPLOME() {
		return "NOM_LB_SPECIALITE_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_SPECIALITE_DIPLOME_SELECT Date de création : (15/06/11 13:49:36)
	 * 
	 */
	public String getNOM_LB_SPECIALITE_DIPLOME_SELECT() {
		return "NOM_LB_SPECIALITE_DIPLOME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_SPECIALITE_DIPLOME Date de création : (15/06/11 13:49:36)
	 * 
	 */
	public String[] getVAL_LB_SPECIALITE_DIPLOME() {
		return getLB_SPECIALITE_DIPLOME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_SPECIALITE_DIPLOME Date de création : (15/06/11 13:49:36)
	 * 
	 */
	public String getVAL_LB_SPECIALITE_DIPLOME_SELECT() {
		return getZone(getNOM_LB_SPECIALITE_DIPLOME_SELECT());
	}

	/**
	 * Constructeur du process OeAGENTDIPLOMEGestion. Date de création :
	 * (15/06/11 14:04:18)
	 * 
	 */
	public OeAGENTDIPLOMEGestion() {
		super();
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SPECIALITE_DIPLOME
	 * Date de création : (15/06/11 14:04:19)
	 * 
	 */
	public String getNOM_ST_SPECIALITE_DIPLOME() {
		return "NOM_ST_SPECIALITE_DIPLOME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_SPECIALITE_DIPLOME Date de création : (15/06/11 14:04:19)
	 * 
	 */
	public String getVAL_ST_SPECIALITE_DIPLOME() {
		return getZone(getNOM_ST_SPECIALITE_DIPLOME());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		String JSP = null;
		if (null == request.getParameter("JSP")) {
			if (multi != null) {
				JSP = multi.getParameter("JSP");
			}
		} else {
			JSP = request.getParameter("JSP");
		}

		// Si on arrive de la JSP alors on traite le get
		if (JSP != null && JSP.equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER_DIPLOME
			if (testerParametre(request, getNOM_PB_ANNULER_DIPLOME())) {
				return performPB_ANNULER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_CREER_DIPLOME
			if (testerParametre(request, getNOM_PB_CREER_DIPLOME())) {
				return performPB_CREER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_TITRE_DIPLOME
			if (testerParametre(request, getNOM_PB_TITRE_DIPLOME())) {
				return performPB_TITRE_DIPLOME(request);
			}

			// Si clic sur le bouton PB_VALIDER_DIPLOME
			if (testerParametre(request, getNOM_PB_VALIDER_DIPLOME())) {
				return performPB_VALIDER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_MODIFIER_DIPLOME
			for (int i = 0; i < getListeDiplomesAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_DIPLOME(i))) {
					return performPB_MODIFIER_DIPLOME(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER_DIPLOME
			for (int i = 0; i < getListeDiplomesAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_DIPLOME(i))) {
					return performPB_CONSULTER_DIPLOME(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DIPLOME
			for (int i = 0; i < getListeDiplomesAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DIPLOME(i))) {
					return performPB_SUPPRIMER_DIPLOME(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_FORMATION
			if (testerParametre(request, getNOM_PB_CREER_FORMATION())) {
				return performPB_CREER_FORMATION(request);
			}

			// Si clic sur le bouton PB_MODIFIER_FORMATION
			for (int i = 0; i < getListeFormationsAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_FORMATION(i))) {
					return performPB_MODIFIER_FORMATION(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER_FORMATION
			for (int i = 0; i < getListeFormationsAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_FORMATION(i))) {
					return performPB_CONSULTER_FORMATION(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_FORMATION
			for (int i = 0; i < getListeFormationsAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_FORMATION(i))) {
					return performPB_SUPPRIMER_FORMATION(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_FORMATION
			if (testerParametre(request, getNOM_PB_VALIDER_FORMATION())) {
				return performPB_VALIDER_FORMATION(request);
			}

			// Si clic sur le bouton PB_ANNULER_FORMATION
			if (testerParametre(request, getNOM_PB_ANNULER_FORMATION())) {
				return performPB_ANNULER_FORMATION(request);
			}

			// Si clic sur le bouton PB_CREER_PERMIS
			if (testerParametre(request, getNOM_PB_CREER_PERMIS())) {
				return performPB_CREER_PERMIS(request);
			}

			// Si clic sur le bouton PB_MODIFIER_PERMIS
			for (int i = 0; i < getListePermisAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_PERMIS(i))) {
					return performPB_MODIFIER_PERMIS(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER_PERMIS
			for (int i = 0; i < getListePermisAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_PERMIS(i))) {
					return performPB_CONSULTER_PERMIS(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_PERMIS
			for (int i = 0; i < getListePermisAgent().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_PERMIS(i))) {
					return performPB_SUPPRIMER_PERMIS(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_PERMIS
			if (testerParametre(request, getNOM_PB_VALIDER_PERMIS())) {
				return performPB_VALIDER_PERMIS(request);
			}

			// Si clic sur le bouton PB_ANNULER_PERMIS
			if (testerParametre(request, getNOM_PB_ANNULER_PERMIS())) {
				return performPB_ANNULER_PERMIS(request);
			}
			// GESTION DES DOCS
			// Si clic sur le bouton PB_CREER_DOC_DIPLOME
			if (testerParametre(request, getNOM_PB_CREER_DOC_DIPLOME())) {
				return performPB_CREER_DOC_DIPLOME(request);
			}
			// Si clic sur le bouton PB_CREER_DOC_FORMATION
			if (testerParametre(request, getNOM_PB_CREER_DOC_FORMATION())) {
				return performPB_CREER_DOC_FORMATION(request);
			}
			// Si clic sur le bouton PB_CREER_DOC_PERMIS
			if (testerParametre(request, getNOM_PB_CREER_DOC_PERMIS())) {
				return performPB_CREER_DOC_PERMIS(request);
			}

			// Si clic sur le bouton PB_CONSULTER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_DOC(i))) {
					return performPB_CONSULTER_DOC(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(i))) {
					return performPB_SUPPRIMER_DOC(request, i);
				}
			}

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}

		}
		// Si pas de retour définit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non géré par le process");
		return false;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 10:36:22)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTDIPLOMEGestion.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-AG-DP-DIPLOMES";
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_DIPLOME Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TITRE_DIPLOME(int i) {
		return "NOM_ST_TITRE_DIPLOME" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIPLOME Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TITRE_DIPLOME(int i) {
		return getZone(getNOM_ST_TITRE_DIPLOME(i));
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_DIPLOME Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SPE_DIPLOME(int i) {
		return "NOM_ST_SPE_DIPLOME" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIPLOME Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SPE_DIPLOME(int i) {
		return getZone(getNOM_ST_SPE_DIPLOME(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_NIVEAU Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NIVEAU(int i) {
		return "NOM_ST_NIVEAU" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NIVEAU Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NIVEAU(int i) {
		return getZone(getNOM_ST_NIVEAU(i));
	}

	public String getNOM_PB_MODIFIER_DIPLOME(int i) {
		return "NOM_PB_MODIFIER_DIPLOME" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_DIPLOME(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		DiplomeAgent d = (DiplomeAgent) getListeDiplomesAgent().get(indiceEltAModifier);
		setDiplomeAgentCourant(d);

		// init du diplome courant
		if (!initialiseDiplomeCourant(request))
			return false;

		initialiseListeDocumentsDiplome(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_MODIFICATION_DIPLOME);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION_DIPLOME Date
	 * de création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_DIPLOME(int i) {
		return "NOM_PB_CONSULTER_DIPLOME" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_DIPLOME(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		DiplomeAgent d = (DiplomeAgent) getListeDiplomesAgent().get(indiceEltAConsulter);
		setDiplomeAgentCourant(d);

		// init du diplome courant
		if (!initialiseDiplomeSuppression(request))
			return false;

		initialiseListeDocumentsDiplome(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_CONSULTATION_DIPLOME);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_DIPLOME Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DIPLOME(int i) {
		return "NOM_PB_SUPPRIMER_DIPLOME" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DIPLOME(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		DiplomeAgent d = (DiplomeAgent) getListeDiplomesAgent().get(indiceEltASuprimer);
		setDiplomeAgentCourant(d);

		// init du diplome courant
		if (!initialiseDiplomeSuppression(request))
			return false;

		initialiseListeDocumentsDiplome(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DIPLOME(), ACTION_SUPPRESSION_DIPLOME);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_FORMATION
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_FORMATION(int i) {
		return "NOM_ST_FORMATION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FORMATION Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_FORMATION(int i) {
		return getZone(getNOM_ST_FORMATION(i));
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_ANNEE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	public ArrayList<FormationAgent> getListeFormationsAgent() {
		if (listeFormationsAgent == null)
			return new ArrayList<FormationAgent>();
		return listeFormationsAgent;
	}

	public void setListeFormationsAgent(ArrayList<FormationAgent> listeFormationsAgent) {
		this.listeFormationsAgent = listeFormationsAgent;
	}

	public FormationAgent getFormationAgentCourant() {
		return formationAgentCourant;
	}

	public void setFormationAgentCourant(FormationAgent formationAgentCourant) {
		this.formationAgentCourant = formationAgentCourant;
	}

	public ArrayList<TitreFormation> getListeTitreFormation() {
		if (listeTitreFormation == null)
			return new ArrayList<TitreFormation>();
		return listeTitreFormation;
	}

	public void setListeTitreFormation(ArrayList<TitreFormation> listeTitreFormation) {
		this.listeTitreFormation = listeTitreFormation;
	}

	public ArrayList<CentreFormation> getListeCentreFormation() {
		if (listeCentreFormation == null)
			return new ArrayList<CentreFormation>();
		return listeCentreFormation;
	}

	public void setListeCentreFormation(ArrayList<CentreFormation> listeCentreFormation) {
		this.listeCentreFormation = listeCentreFormation;
	}

	public TitreFormationDao getTitreFormationDao() {
		return titreFormationDao;
	}

	public void setTitreFormationDao(TitreFormationDao titreFormationDao) {
		this.titreFormationDao = titreFormationDao;
	}

	public CentreFormationDao getCentreFormationDao() {
		return centreFormationDao;
	}

	public void setCentreFormationDao(CentreFormationDao centreFormationDao) {
		this.centreFormationDao = centreFormationDao;
	}

	public FormationAgentDao getFormationAgentDao() {
		return formationAgentDao;
	}

	public void setFormationAgentDao(FormationAgentDao formationAgentDao) {
		this.formationAgentDao = formationAgentDao;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_CREER_FORMATION() {
		return "NOM_PB_CREER_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_CREER_FORMATION(HttpServletRequest request) throws Exception {

		// On vide la zone de saisie
		addZone(getNOM_ST_DUREE_FORMATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ANNEE_FORMATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_TITRE_FORM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TITRE_FORM(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_CENTRE_FORM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CENTRE_FORM(), Const.CHAINE_VIDE);
		setListeDocuments(null);

		// init du diplome courant
		setFormationAgentCourant(new FormationAgent());

		// On nomme l'action
		addZone(getNOM_ST_ACTION_FORMATION(), ACTION_CREATION_FORMATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE_FORMATION Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_DUREE_FORMATION() {
		return "NOM_ST_DUREE_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_DUREE_FORMATION Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_DUREE_FORMATION() {
		return getZone(getNOM_ST_DUREE_FORMATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE_FORMATION Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_ANNEE_FORMATION() {
		return "NOM_ST_ANNEE_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ANNEE_FORMATION Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_ANNEE_FORMATION() {
		return getZone(getNOM_ST_ANNEE_FORMATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_FORMATION
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_ACTION_FORMATION() {
		return "NOM_ST_ACTION_FORMATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_FORMATION Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_ACTION_FORMATION() {
		return getZone(getNOM_ST_ACTION_FORMATION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_MODIFIER_FORMATION(int i) {
		return "NOM_PB_MODIFIER_FORMATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_FORMATION(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_FORMATION(), Const.CHAINE_VIDE);

		// Récup de la formation courante
		FormationAgent f = (FormationAgent) getListeFormationsAgent().get(indiceEltAModifier);
		setFormationAgentCourant(f);

		// init de la formation courant
		if (!initialiseFormationCourant(request))
			return false;

		initialiseListeDocumentsFormation(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_FORMATION(), ACTION_MODIFICATION_FORMATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	private boolean initialiseFormationCourant(HttpServletRequest request) throws Exception {
		FormationAgent f = getFormationAgentCourant();
		TitreFormation titre = (TitreFormation) getHashTitreFormation().get(f.getIdTitreFormation());
		CentreFormation centre = (CentreFormation) getHashCentreFormation().get(f.getIdCentreFormation());

		// Alim zones
		// Titre formation
		addZone(getNOM_ST_TITRE_FORM(), titre.getLibTitreFormation());
		addZone(getNOM_EF_TITRE_FORM(), titre.getLibTitreFormation());

		// Centre formation
		addZone(getNOM_ST_CENTRE_FORM(), centre.getLibCentreFormation());
		addZone(getNOM_EF_CENTRE_FORM(), centre.getLibCentreFormation());

		// Duree
		addZone(getNOM_ST_DUREE_FORMATION(), f.getDureeFormation().toString());
		// Annee
		addZone(getNOM_ST_ANNEE_FORMATION(), f.getAnneeFormation().toString());
		// Unite
		int ligneUnite = getListeUniteDuree().indexOf(f.getUniteDuree());
		addZone(getNOM_LB_UNITE_DUREE_SELECT(), String.valueOf(ligneUnite));

		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION_FORMATION Date
	 * de création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_FORMATION(int i) {
		return "NOM_PB_CONSULTER_FORMATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_FORMATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_FORMATION(), Const.CHAINE_VIDE);

		// Récup de la formation courante
		FormationAgent f = (FormationAgent) getListeFormationsAgent().get(indiceEltAConsulter);
		setFormationAgentCourant(f);

		// init du diplome courant
		if (!initialiseFormationCourant(request))
			return false;

		initialiseListeDocumentsFormation(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_FORMATION(), ACTION_CONSULTATION_FORMATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_FORMATION Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_FORMATION(int i) {
		return "NOM_PB_SUPPRIMER_FORMATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_FORMATION(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);

		// Récup de la formation courante
		FormationAgent f = (FormationAgent) getListeFormationsAgent().get(indiceEltASuprimer);
		setFormationAgentCourant(f);

		// init du diplome courant
		if (!initialiseFormationCourant(request))
			return false;

		initialiseListeDocumentsFormation(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_FORMATION(), ACTION_SUPPRESSION_FORMATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_FORMATION Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER_FORMATION() {
		return "NOM_PB_VALIDER_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_FORMATION(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION_FORMATION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Récup des zones saisies
		String dureeForm = getZone(getNOM_ST_DUREE_FORMATION());
		String annee = getZone(getNOM_ST_ANNEE_FORMATION());

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION_FORMATION()).equals(ACTION_SUPPRESSION_FORMATION)) {

			// Suppression
			getFormationAgentDao().supprimerFormationAgent(getFormationAgentCourant().getIdFormation());

			// il faut supprimer les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document d = getListeDocuments().get(i);
				LienDocumentAgent lien = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(),
						getAgentCourant().getIdAgent(), d.getIdDocument());
				// suppression dans table DOCUMENT_AGENT
				lien.supprimerLienDocumentAgent(getTransaction());
				// Suppression dans la table DOCUMENT_ASSOCIE
				d.supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = d.getLienDocument().replace("/", "\\");
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.error("Erreur suppression physique du fichier : " + e.toString());
				}
			}

		} else {
			if (!performControlerChampsFormation(request)) {
				return false;
			}
			if (!performControlerRGFormation(request)) {
				return false;
			}
			// Recup du titre de formation
			TitreFormation titreForm = getSelectedTitreFormation();

			// Recup du centre
			CentreFormation centreForm = getSelectedCentreFormation();

			// Recup de l'unite
			int numligneUnite = (Services.estNumerique(getZone(getNOM_LB_UNITE_DUREE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_UNITE_DUREE_SELECT())) : -1);
			if (numligneUnite == -1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "unité durée"));
				return false;
			}
			String uniteDuree = (numligneUnite >= 0 ? (String) getListeUniteDuree().get(numligneUnite) : null);

			// Affectation des attributs
			getFormationAgentCourant().setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			getFormationAgentCourant().setIdTitreFormation(titreForm.getIdTitreFormation());
			getFormationAgentCourant().setIdCentreFormation(centreForm.getIdCentreFormation());
			getFormationAgentCourant().setDureeFormation(Integer.valueOf(dureeForm));
			getFormationAgentCourant().setUniteDuree(uniteDuree);
			getFormationAgentCourant().setAnneeFormation(Integer.valueOf(annee));

			Integer idFormationAgent = null;
			if (getZone(getNOM_ST_ACTION_FORMATION()).equals(ACTION_MODIFICATION_FORMATION)) {
				// Modification
				getFormationAgentDao().modifierFormationAgent(getFormationAgentCourant().getIdFormation(),
						getFormationAgentCourant().getIdTitreFormation(),
						getFormationAgentCourant().getIdCentreFormation(), getFormationAgentCourant().getIdAgent(),
						getFormationAgentCourant().getDureeFormation(), getFormationAgentCourant().getUniteDuree(),
						getFormationAgentCourant().getAnneeFormation());
				idFormationAgent = getFormationAgentCourant().getIdFormation();
			} else if (getZone(getNOM_ST_ACTION_FORMATION()).equals(ACTION_CREATION_FORMATION)) {
				// Création
				idFormationAgent = getFormationAgentDao().creerFormationAgent(
						getFormationAgentCourant().getIdTitreFormation(),
						getFormationAgentCourant().getIdCentreFormation(), getFormationAgentCourant().getIdAgent(),
						getFormationAgentCourant().getDureeFormation(), getFormationAgentCourant().getUniteDuree(),
						getFormationAgentCourant().getAnneeFormation());
			}

			// on fait la gestion des documents
			performPB_VALIDER_DOCUMENT_FORMATION_CREATION(request, idFormationAgent);
		}

		// Tout s'est bien passé
		initialiseListeFormationsAgent(request);

		addZone(getNOM_ST_ACTION_FORMATION(), Const.CHAINE_VIDE);

		return true;
	}

	private CentreFormation getSelectedCentreFormation() throws Exception {
		Integer idCentre = null;
		for (int i = 0; i < getListeCentreFormation().size(); i++) {
			CentreFormation centre = (CentreFormation) getListeCentreFormation().get(i);
			String textCentre = centre.getLibCentreFormation();
			if (textCentre.equals(getVAL_EF_CENTRE_FORM())) {
				idCentre = centre.getIdCentreFormation();
				break;
			}
		}
		if (idCentre == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Centres"));
		}

		return getCentreFormationDao().chercherCentreFormation(idCentre);
	}

	private TitreFormation getSelectedTitreFormation() throws Exception {
		Integer idTitre = null;
		for (int i = 0; i < getListeTitreFormation().size(); i++) {
			TitreFormation titre = (TitreFormation) getListeTitreFormation().get(i);
			String textTitre = titre.getLibTitreFormation();
			if (textTitre.equals(getVAL_EF_TITRE_FORM())) {
				idTitre = titre.getIdTitreFormation();
				break;
			}
		}
		if (idTitre == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
		}

		return getTitreFormationDao().chercherTitreFormation(idTitre);
	}

	private boolean performControlerRGFormation(HttpServletRequest request) throws Exception {

		// Vérification des contraintes d'unicité de la formation
		if (getVAL_ST_ACTION_FORMATION().equals(ACTION_CREATION_FORMATION)) {

			// Recup du titre
			Integer idTitre = null;
			for (int i = 0; i < getListeTitreFormation().size(); i++) {
				TitreFormation titre = (TitreFormation) getListeTitreFormation().get(i);
				String textTitre = titre.getLibTitreFormation();
				if (textTitre.equals(getVAL_EF_TITRE_FORM())) {
					idTitre = titre.getIdTitreFormation();
					break;
				}
			}
			if (idTitre == null) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
				return false;
			}
			TitreFormation titreForm = getTitreFormationDao().chercherTitreFormation(idTitre);
			// Recup du centre
			Integer idCentre = null;
			for (int i = 0; i < getListeCentreFormation().size(); i++) {
				CentreFormation centre = (CentreFormation) getListeCentreFormation().get(i);
				String textCentre = centre.getLibCentreFormation();
				if (textCentre.equals(getVAL_EF_CENTRE_FORM())) {
					idCentre = centre.getIdCentreFormation();
					break;
				}
			}
			if (idCentre == null) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Centres"));
				return false;
			}
			CentreFormation centreForm = getCentreFormationDao().chercherCentreFormation(idCentre);
			// Recup de l'unite
			int numligneUnite = (Services.estNumerique(getZone(getNOM_LB_UNITE_DUREE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_UNITE_DUREE_SELECT())) : -1);
			if (numligneUnite == -1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "unité durée"));
				return false;
			}
			String uniteDuree = (numligneUnite >= 0 ? (String) getListeUniteDuree().get(numligneUnite) : null);
			for (FormationAgent formation : getListeFormationsAgent()) {
				if (formation.getAnneeFormation().toString().equals(getVAL_ST_ANNEE_FORMATION())
						&& formation.getIdAgent().toString().equals(getAgentCourant().getIdAgent())
						&& formation.getIdTitreFormation().toString()
								.equals(titreForm.getIdTitreFormation().toString())
						&& formation.getIdCentreFormation().toString()
								.equals(centreForm.getIdCentreFormation().toString())
						&& formation.getDureeFormation().toString().equals(getVAL_ST_DUREE_FORMATION())
						&& formation.getUniteDuree().equals(uniteDuree)) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une formation", "ces valeurs"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 * @param idFormationAgent
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_FORMATION_CREATION(HttpServletRequest request, Integer idFormationAgent)
			throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {

			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());

				// Contrôle des champs
				if (!performControlerSaisieDocument(request))
					return false;

				if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_CREATION)) {
					if (!creeDocumentFormation(request, idFormationAgent)) {
						return false;
					}
				}
			}
		} else {
			if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_SUPPRESSION)) {
				// suppression dans table DOCUMENT_AGENT
				getLienDocument().supprimerLienDocumentAgent(getTransaction());
				// Suppression dans la table DOCUMENT_ASSOCIE
				getDocumentCourant().supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = getDocumentCourant().getLienDocument();
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.error("Erreur suppression physique du fichier : " + e.toString());
				}

				// tout s'est bien passé
				commitTransaction();

			}

		}

		initialiseListeDocumentsFormation(request);
		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...)
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 */
	public boolean performControlerChampsFormation(HttpServletRequest request) throws Exception {

		// durée formation
		if (getZone(getNOM_ST_DUREE_FORMATION()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée de la formation"));
			setFocus(getNOM_ST_DUREE_FORMATION());
			return false;
		}
		if (!Services.estNumerique(getVAL_ST_DUREE_FORMATION())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "durée de la formation"));
			setFocus(getNOM_ST_DUREE_FORMATION());
			return false;
		}

		// annee formation
		if (getZone(getNOM_ST_ANNEE_FORMATION()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année de la formation"));
			setFocus(getNOM_ST_ANNEE_FORMATION());
			return false;
		}
		if (!Services.estNumerique(getVAL_ST_ANNEE_FORMATION())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année de la formation"));
			setFocus(getNOM_ST_ANNEE_FORMATION());
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER_FORMATION() {
		return "NOM_PB_ANNULER_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER_FORMATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_FORMATION(), Const.CHAINE_VIDE);
		performPB_RESET(request);

		return true;
	}

	public ArrayList<DiplomeAgent> getListeEcoles() {
		if (listeEcoles == null)
			return new ArrayList<DiplomeAgent>();
		return listeEcoles;
	}

	public void setListeEcoles(ArrayList<DiplomeAgent> listeEcoles) {
		this.listeEcoles = listeEcoles;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_UNITE_DUREE Date de
	 * création : (28/11/11)
	 * 
	 */
	private String[] getLB_UNITE_DUREE() {
		if (LB_UNITE_DUREE == null)
			LB_UNITE_DUREE = initialiseLazyLB();
		return LB_UNITE_DUREE;
	}

	/**
	 * Setter de la liste: LB_UNITE_DUREE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_UNITE_DUREE(String[] newLB_UNITE_DUREE) {
		LB_UNITE_DUREE = newLB_UNITE_DUREE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_UNITE_DUREE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_UNITE_DUREE() {
		return "NOM_LB_UNITE_DUREE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_UNITE_DUREE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_UNITE_DUREE_SELECT() {
		return "NOM_LB_UNITE_DUREE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_UNITE_DUREE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_UNITE_DUREE() {
		return getLB_UNITE_DUREE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_UNITE_DUREE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_UNITE_DUREE_SELECT() {
		return getZone(getNOM_LB_UNITE_DUREE_SELECT());
	}

	public ArrayList<String> getListeUniteDuree() {
		return listeUniteDuree;
	}

	public void setListeUniteDuree(ArrayList<String> listeUniteDuree) {
		this.listeUniteDuree = listeUniteDuree;
	}

	public ArrayList<PermisAgent> getListePermisAgent() {
		if (listePermisAgent == null)
			return new ArrayList<PermisAgent>();
		return listePermisAgent;
	}

	public void setListePermisAgent(ArrayList<PermisAgent> listePermisAgent) {
		this.listePermisAgent = listePermisAgent;
	}

	public PermisAgent getPermisAgentCourant() {
		return permisAgentCourant;
	}

	public void setPermisAgentCourant(PermisAgent permisAgentCourant) {
		this.permisAgentCourant = permisAgentCourant;
	}

	public ArrayList<TitrePermis> getListeTitrePermis() {
		if (listeTitrePermis == null)
			return new ArrayList<TitrePermis>();
		return listeTitrePermis;
	}

	public void setListeTitrePermis(ArrayList<TitrePermis> listeTitrePermis) {
		this.listeTitrePermis = listeTitrePermis;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_PERMIS Date de
	 * création : (28/11/11)
	 * 
	 */
	private String[] getLB_TITRE_PERMIS() {
		if (LB_TITRE_PERMIS == null)
			LB_TITRE_PERMIS = initialiseLazyLB();
		return LB_TITRE_PERMIS;
	}

	/**
	 * Setter de la liste: LB_TITRE_PERMIS Date de création : (28/11/11)
	 * 
	 */
	private void setLB_TITRE_PERMIS(String[] newLB_TITRE_PERMIS) {
		LB_TITRE_PERMIS = newLB_TITRE_PERMIS;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_PERMIS Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_TITRE_PERMIS() {
		return "NOM_LB_TITRE_PERMIS";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_PERMIS_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_TITRE_PERMIS_SELECT() {
		return "NOM_LB_TITRE_PERMIS_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_PERMIS Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_TITRE_PERMIS() {
		return getLB_TITRE_PERMIS();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE_PERMIS Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_TITRE_PERMIS_SELECT() {
		return getZone(getNOM_LB_TITRE_PERMIS_SELECT());
	}

	public TitrePermisDao getTitrePermisDao() {
		return titrePermisDao;
	}

	public void setTitrePermisDao(TitrePermisDao titrePermisDao) {
		this.titrePermisDao = titrePermisDao;
	}

	public PermisAgentDao getPermisAgentDao() {
		return permisAgentDao;
	}

	public void setPermisAgentDao(PermisAgentDao permisAgentDao) {
		this.permisAgentDao = permisAgentDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_PERMIS Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_ACTION_PERMIS() {
		return "NOM_ST_ACTION_PERMIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_PERMIS
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_ACTION_PERMIS() {
		return getZone(getNOM_ST_ACTION_PERMIS());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_MODIFIER_PERMIS(int i) {
		return "NOM_PB_MODIFIER_PERMIS" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_PERMIS(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_PERMIS(), Const.CHAINE_VIDE);

		// Récup du permis courant
		PermisAgent p = (PermisAgent) getListePermisAgent().get(indiceEltAModifier);
		setPermisAgentCourant(p);

		// init de la formation courant
		if (!initialisePermisCourant(request))
			return false;

		initialiseListeDocumentsPermis(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_PERMIS(), ACTION_MODIFICATION_PERMIS);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	private boolean initialisePermisCourant(HttpServletRequest request) throws Exception {
		PermisAgent p = getPermisAgentCourant();
		TitrePermis titre = getTitrePermisDao().chercherTitrePermis(p.getIdPermis());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Alim zones
		// Titre formation
		int ligneTitre = getListeTitrePermis().indexOf(titre);
		addZone(getNOM_LB_TITRE_PERMIS_SELECT(), String.valueOf(ligneTitre + 1));

		// Duree
		addZone(getNOM_ST_DUREE_PERMIS(), p.getDureePermis().toString());
		addZone(getNOM_EF_DATE_OBTENTION_PERMIS(), sdf.format(p.getDateObtention()));
		// unite
		int ligneUnite = getListeUniteDuree().indexOf(p.getUniteDuree());
		addZone(getNOM_LB_UNITE_DUREE_SELECT(), String.valueOf(ligneUnite));

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION_PERMIS Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_PERMIS(int i) {
		return "NOM_PB_CONSULTER_PERMIS" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_PERMIS(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_PERMIS(), Const.CHAINE_VIDE);

		// Récup du permis courant
		PermisAgent p = (PermisAgent) getListePermisAgent().get(indiceEltAConsulter);
		setPermisAgentCourant(p);

		// init du diplome courant
		if (!initialisePermisCourant(request))
			return false;

		initialiseListeDocumentsPermis(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_PERMIS(), ACTION_CONSULTATION_PERMIS);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_PERMIS Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_PERMIS(int i) {
		return "NOM_PB_SUPPRIMER_PERMIS" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_PERMIS(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);

		// Récup du permis courant
		PermisAgent p = (PermisAgent) getListePermisAgent().get(indiceEltASuprimer);
		setPermisAgentCourant(p);

		// init du diplome courant
		if (!initialisePermisCourant(request))
			return false;

		initialiseListeDocumentsPermis(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION_PERMIS(), ACTION_SUPPRESSION_PERMIS);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_DIPLOME(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_FORMATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_PERMIS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;
		setURLFichier(null);
		setListeDocuments(null);
		setDocumentCourant(null);
		setLienDocument(null);
		multi = null;
		fichierUpload = null;
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER_PERMIS() {
		return "NOM_PB_VALIDER_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_PERMIS(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION_PERMIS()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Récup des zones saisies
		String dureePermis = getZone(getNOM_ST_DUREE_PERMIS());
		String dateObtention = getZone(getNOM_EF_DATE_OBTENTION_PERMIS());

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION_PERMIS()).equals(ACTION_SUPPRESSION_PERMIS)) {

			// Suppression
			getPermisAgentDao().supprimerPermisAgent(getPermisAgentCourant().getIdPermisAgent());

			// il faut supprimer les documents
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document d = getListeDocuments().get(i);
				LienDocumentAgent lien = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(),
						getAgentCourant().getIdAgent(), d.getIdDocument());
				// suppression dans table DOCUMENT_AGENT
				lien.supprimerLienDocumentAgent(getTransaction());
				// Suppression dans la table DOCUMENT_ASSOCIE
				d.supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = d.getLienDocument().replace("/", "\\");
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.error("Erreur suppression physique du fichier : " + e.toString());
				}
			}

		} else {
			if (!performControlerChampsPermis(request)) {
				return false;
			}
			if (!performControlerRGPermis(request)) {
				return false;
			}
			// Recup du titre
			int numligneTitre = (Services.estNumerique(getZone(getNOM_LB_TITRE_PERMIS_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TITRE_PERMIS_SELECT())) : -1);
			if (numligneTitre == -1 || getListeTitrePermis().size() == 0
					|| numligneTitre > getListeTitrePermis().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
				return false;
			}
			TitrePermis titrePermis = (numligneTitre >= 0 ? (TitrePermis) getListeTitrePermis().get(numligneTitre)
					: null);

			// Recup de l'unite
			int numligneUnite = (Services.estNumerique(getZone(getNOM_LB_UNITE_DUREE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_UNITE_DUREE_SELECT())) : -1);
			if (numligneUnite == -1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "unité durée"));
				return false;
			}
			String uniteDuree = (numligneUnite >= 0 ? (String) getListeUniteDuree().get(numligneUnite) : null);

			// Affectation des attributs
			getPermisAgentCourant().setIdPermis(titrePermis.getIdTitrePermis());
			getPermisAgentCourant().setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			getPermisAgentCourant().setDureePermis(Integer.valueOf(dureePermis));
			getPermisAgentCourant().setUniteDuree(uniteDuree);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			getPermisAgentCourant().setDateObtention(sdf.parse(dateObtention));

			Integer idPermisAgent = null;
			if (getZone(getNOM_ST_ACTION_PERMIS()).equals(ACTION_MODIFICATION_PERMIS)) {
				// Modification
				getPermisAgentDao().modifierPermisAgent(getPermisAgentCourant().getIdPermisAgent(),
						getPermisAgentCourant().getIdPermis(), getPermisAgentCourant().getIdAgent(),
						getPermisAgentCourant().getDureePermis(), getPermisAgentCourant().getUniteDuree(),
						getPermisAgentCourant().getDateObtention());
				idPermisAgent = getPermisAgentCourant().getIdPermisAgent();
			} else if (getZone(getNOM_ST_ACTION_PERMIS()).equals(ACTION_CREATION_PERMIS)) {
				// Création
				idPermisAgent = getPermisAgentDao().creerPermisAgent(getPermisAgentCourant().getIdPermis(),
						getPermisAgentCourant().getIdAgent(), getPermisAgentCourant().getDureePermis(),
						getPermisAgentCourant().getUniteDuree(), getPermisAgentCourant().getDateObtention());
			}

			// on fait la gestion des documents
			performPB_VALIDER_DOCUMENT_PERMIS_CREATION(request, idPermisAgent);
		}

		// Tout s'est bien passé
		initialiseListePermisAgent(request);

		addZone(getNOM_ST_ACTION_PERMIS(), Const.CHAINE_VIDE);

		return true;
	}

	private boolean performControlerRGPermis(HttpServletRequest request) throws ParseException {

		// Vérification des contraintes d'unicité de la formation
		if (getVAL_ST_ACTION_PERMIS().equals(ACTION_CREATION_PERMIS)) {

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			// Recup du titre
			int numligneTitre = (Services.estNumerique(getZone(getNOM_LB_TITRE_PERMIS_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TITRE_PERMIS_SELECT())) : -1);
			if (numligneTitre == -1 || getListeTitrePermis().size() == 0
					|| numligneTitre > getListeTitrePermis().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Titres"));
				return false;
			}
			TitrePermis titrePermis = (numligneTitre >= 0 ? (TitrePermis) getListeTitrePermis().get(numligneTitre)
					: null);

			// Recup de l'unite
			int numligneUnite = (Services.estNumerique(getZone(getNOM_LB_UNITE_DUREE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_UNITE_DUREE_SELECT())) : -1);
			if (numligneUnite == -1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "unité durée"));
				return false;
			}
			String uniteDuree = (numligneUnite >= 0 ? (String) getListeUniteDuree().get(numligneUnite) : null);
			for (PermisAgent permis : getListePermisAgent()) {
				if (permis.getIdPermis().toString().equals(titrePermis.getIdTitrePermis().toString())
						&& permis.getIdAgent().toString().equals(getAgentCourant().getIdAgent())
						&& permis.getDureePermis().toString().equals(getVAL_ST_DUREE_PERMIS())
						&& permis.getDateObtention().toString()
								.equals(sdf.parse(getVAL_EF_DATE_OBTENTION_PERMIS()).toString())
						&& permis.getUniteDuree().equals(uniteDuree)) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "un permis", "ces valeurs"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Vérifie les règles de gestion de saisie (champs obligatoires, ...)
	 * 
	 * @param request
	 * @return true si les règles de gestion sont respectées. false sinon.
	 * @throws Exception
	 */
	public boolean performControlerChampsPermis(HttpServletRequest request) throws Exception {
		// durée permis
		if (getZone(getNOM_ST_DUREE_PERMIS()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée de validité"));
			setFocus(getNOM_ST_DUREE_PERMIS());
			return false;
		}
		if (!Services.estNumerique(getZone(getNOM_ST_DUREE_PERMIS()))) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "durée de validité"));
			setFocus(getNOM_ST_DUREE_FORMATION());
			return false;
		}

		// date obtention
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_OBTENTION_PERMIS())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date d'obtention"));
			return false;
		}
		if (!Services.estUneDate(getVAL_EF_DATE_OBTENTION_PERMIS())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date d'obtention"));
			setFocus(getNOM_EF_DATE_OBTENTION_PERMIS());
			return false;
		} else if (Services.compareDates(getAgentCourant().getDateNaissance(), getVAL_EF_DATE_OBTENTION_PERMIS()) > -1
				|| Services.compareDates(getVAL_EF_DATE_OBTENTION_PERMIS(), Services.dateDuJour()) > 0) {
			// "ERR202",
			// "La date @ doit être comprise entre la date @ et la date @."
			// RG_AG_DI_C01
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR202", "d'obtention", "de naissance de l'agent", "du jour"));
			setFocus(getVAL_EF_DATE_OBTENTION_PERMIS());
			return false;
		}

		return true;
	}

	/**
	 * Initialisation de la liste des permis
	 * 
	 */
	private void initialiseListePermisAgent(HttpServletRequest request) throws Exception {
		// Recherche des permis de l'agent
		ArrayList<PermisAgent> a = getPermisAgentDao().listerPermisAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()));
		setListePermisAgent(a);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int indicePermis = 0;
		if (getListePermisAgent() != null) {
			for (int i = 0; i < getListePermisAgent().size(); i++) {
				PermisAgent p = (PermisAgent) getListePermisAgent().get(i);
				TitrePermis t = getTitrePermisDao().chercherTitrePermis(p.getIdPermis());

				addZone(getNOM_ST_PERMIS(indicePermis), t.getLibTitrePermis());
				String dateLimite = "&nbsp;";
				if (p.getUniteDuree().equals("heures")) {
					dateLimite = sdf.format(p.getDateObtention());
				} else if (p.getUniteDuree().equals("jours")) {
					dateLimite = Services.ajouteJours(sdf.format(p.getDateObtention()), p.getDureePermis());
				} else if (p.getUniteDuree().equals("mois")) {
					dateLimite = Services.ajouteMois(sdf.format(p.getDateObtention()), p.getDureePermis());
				}

				addZone(getNOM_ST_LIMITE_PERMIS(indicePermis), dateLimite);

				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(),
						getAgentCourant(), "DONNEES PERSONNELLES", "PERM", p.getIdPermisAgent().toString());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}
				addZone(getNOM_ST_NB_DOC_PERMIS(indicePermis), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indicePermis++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_PERMIS Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER_PERMIS() {
		return "NOM_PB_ANNULER_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER_PERMIS(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_PERMIS(), Const.CHAINE_VIDE);
		performPB_RESET(request);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_CREER_PERMIS() {
		return "NOM_PB_CREER_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_CREER_PERMIS(HttpServletRequest request) throws Exception {
		// On vide la zone de saisie
		addZone(getNOM_LB_TITRE_PERMIS_SELECT(), Const.ZERO);
		addZone(getNOM_LB_UNITE_DUREE_SELECT(), Const.ZERO);
		addZone(getNOM_ST_DUREE_PERMIS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_OBTENTION_PERMIS(), Const.CHAINE_VIDE);
		setListeDocuments(null);

		// init du diplome courant
		setPermisAgentCourant(new PermisAgent());

		// On nomme l'action
		addZone(getNOM_ST_ACTION_PERMIS(), ACTION_CREATION_PERMIS);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_PERMIS Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PERMIS(int i) {
		return "NOM_ST_PERMIS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PERMIS Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PERMIS(int i) {
		return getZone(getNOM_ST_PERMIS(i));
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_PERMIS Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIMITE_PERMIS(int i) {
		return "NOM_ST_LIMITE_PERMIS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PERMIS Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIMITE_PERMIS(int i) {
		return getZone(getNOM_ST_LIMITE_PERMIS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DUREE_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_DUREE_PERMIS() {
		return "NOM_ST_DUREE_PERMIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DUREE_PERMIS
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_DUREE_PERMIS() {
		return getZone(getNOM_ST_DUREE_PERMIS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_OBTENTION_PERMIS Date de création : (11/06/03 15:05:22)
	 * 
	 */
	public String getNOM_EF_DATE_OBTENTION_PERMIS() {
		return "NOM_EF_DATE_OBTENTION_PERMIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_OBTENTION_PERMIS Date de création : (11/06/03 15:05:22)
	 * 
	 */
	public String getVAL_EF_DATE_OBTENTION_PERMIS() {
		return getZone(getNOM_EF_DATE_OBTENTION_PERMIS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC_DIPLOME Date de
	 * création : (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER_DOC_DIPLOME() {
		return "NOM_PB_CREER_DOC_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC_DIPLOME(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
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
	 * Retourne le doc en cours.
	 * 
	 * @return documentCourant
	 */
	private Document getDocumentCourant() {
		return documentCourant;
	}

	/**
	 * Met à jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getVAL_ST_DATE_DOC() {
		return getZone(getNOM_ST_DATE_DOC());
	}

	public String getNOM_ST_DATE_DOC() {
		return "NOM_ST_DATE_DOC";
	}

	public String getVAL_ST_COMMENTAIRE_DOC() {
		return getZone(getNOM_ST_COMMENTAIRE_DOC());
	}

	public String getNOM_ST_COMMENTAIRE_DOC() {
		return "NOM_ST_COMMENTAIRE_DOC";
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT Date
	 * de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	private boolean uploadFichierPDF(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		File newFile = new File(repPartage + codTypeDoc + "/" + nomFichier);

		FileInputStream in = new FileInputStream(f);

		try {
			FileOutputStream out = new FileOutputStream(newFile);
			try {
				byte[] byteBuffer = new byte[in.available()];
				int s = in.read(byteBuffer);
				out.write(byteBuffer);
				out.flush();
				resultat = true;
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

		return resultat;
	}

	private boolean uploadFichier(File f, String nomFichier, String codTypeDoc) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		// on verifie que les repertoires existent
		verifieRepertoire(codTypeDoc);

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.toFileObject(f);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(repPartage + codTypeDoc + "/" + nomFichier);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		try {
			while ((ligne = in.readLine()) != null) {
				out.write(ligne);
			}
			resultat = true;
		} catch (Exception e) {
			logger.error("erreur d'execution " + e.toString());
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

		return resultat;
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

	private boolean performControlerSaisieDocument(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), multi.getParameter(getNOM_EF_COMMENTAIRE()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	/**
	 * Méthode qui teste si un paramètre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null || (multi != null && multi
				.getParameter(param) != null));
	}

	/**
	 * Process incoming requests for information*
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 */
	public boolean recupererPreControles(HttpServletRequest request) throws Exception {
		String type = request.getHeader("Content-Type");
		String repTemp = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			request.setCharacterEncoding("UTF-8");
			multi = new MultipartRequest(request, repTemp, 10 * 1024 * 1024, "UTF-8");
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @param newListeDocuments
	 *            ArrayList
	 */
	private void setListeDocuments(ArrayList<Document> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<Document>();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des documents pour le diplome
	 * 
	 */
	private void initialiseListeDocumentsDiplome(HttpServletRequest request) throws Exception {
		// Recherche des documents du diplome
		ArrayList<Document> listeDoc = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(),
				getAgentCourant(), "DONNEES PERSONNELLES", "DIP", getDiplomeAgentCourant().getIdDiplome());
		setListeDocuments(listeDoc);

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getNomDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), doc.getDateDocument());
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());

				indiceActeVM++;
			}
		}

	}

	/**
	 * Initialisation de la liste des documents pour la formation
	 * 
	 */
	private void initialiseListeDocumentsFormation(HttpServletRequest request) throws Exception {
		// Recherche des documents de la formation
		if (getFormationAgentCourant().getIdFormation() != null) {
			ArrayList<Document> listeDoc = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(),
					getAgentCourant(), "DONNEES PERSONNELLES", "FORM", getFormationAgentCourant().getIdFormation()
							.toString());
			setListeDocuments(listeDoc);
		}

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getNomDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), doc.getDateDocument());
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());

				indiceActeVM++;
			}
		}

	}

	/**
	 * Initialisation de la liste des documents pour la formation
	 * 
	 */
	private void initialiseListeDocumentsPermis(HttpServletRequest request) throws Exception {
		// Recherche des documents du permis
		if (getPermisAgentCourant().getIdPermisAgent() != null) {
			ArrayList<Document> listeDoc = LienDocumentAgent.listerLienDocumentAgentTYPE(getTransaction(),
					getAgentCourant(), "DONNEES PERSONNELLES", "PERM", getPermisAgentCourant().getIdPermisAgent()
							.toString());
			setListeDocuments(listeDoc);
		}

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getNomDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), doc.getDateDocument());
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());

				indiceActeVM++;
			}
		}

	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT Date
	 * de création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION_DOCUMENT() {
		return "NOM_ST_ACTION_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT Date de création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION_DOCUMENT() {
		return getZone(getNOM_ST_ACTION_DOCUMENT());
	}

	public LienDocumentAgent getLienDocument() {
		return lienDocument;
	}

	public void setLienDocument(LienDocumentAgent lienDocument) {
		this.lienDocument = lienDocument;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_DOC Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_DOC(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");

		// Récup du document courant
		Document d = (Document) getListeDocuments().get(indiceEltAConsulter);
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + d.getLienDocument()));

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_DOC Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		// Récup du Diplome courant
		Document d = (Document) getListeDocuments().get(indiceEltASuprimer);
		setDocumentCourant(d);

		// init des documents courant
		if (!initialiseDocumentSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request) throws Exception {

		// Récup du Diplome courant
		Document d = getDocumentCourant();

		LienDocumentAgent lda = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant()
				.getIdAgent(), getDocumentCourant().getIdDocument());
		setLienDocument(lda);

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_DATE_DOC(), d.getDateDocument());
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC_FORMATION Date de
	 * création : (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER_DOC_FORMATION() {
		return "NOM_PB_CREER_DOC_FORMATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC_FORMATION(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC_PERMIS Date de
	 * création : (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER_DOC_PERMIS() {
		return "NOM_PB_CREER_DOC_PERMIS";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC_PERMIS(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 * @param idPermisAgent
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_PERMIS_CREATION(HttpServletRequest request, Integer idPermisAgent)
			throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {

			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());

				// Contrôle des champs
				if (!performControlerSaisieDocument(request))
					return false;

				if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_CREATION)) {
					if (!creeDocumentPermis(request, idPermisAgent)) {
						return false;
					}
				}
			}
		} else {
			if (getZone(getNOM_ST_ACTION_DOCUMENT()).equals(ACTION_DOCUMENT_SUPPRESSION)) {
				// suppression dans table DOCUMENT_AGENT
				getLienDocument().supprimerLienDocumentAgent(getTransaction());
				// Suppression dans la table DOCUMENT_ASSOCIE
				getDocumentCourant().supprimerDocument(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// on supprime le fichier physiquement sur le serveur
				String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
				String cheminDoc = getDocumentCourant().getLienDocument();
				File fichierASupp = new File(repertoireStockage + cheminDoc);
				try {
					fichierASupp.delete();
				} catch (Exception e) {
					logger.error("Erreur suppression physique du fichier : " + e.toString());
				}

				// tout s'est bien passé
				commitTransaction();

			}

		}

		initialiseListeDocumentsPermis(request);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_DOC_PERMIS Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NB_DOC_PERMIS(int i) {
		return "NOM_ST_NB_DOC_PERMIS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NB_DOC_PERMIS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NB_DOC_PERMIS(int i) {
		return getZone(getNOM_ST_NB_DOC_PERMIS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_DOC_FORMATION
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NB_DOC_FORMATION(int i) {
		return "NOM_ST_NB_DOC_FORMATION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_NB_DOC_FORMATION Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NB_DOC_FORMATION(int i) {
		return getZone(getNOM_ST_NB_DOC_FORMATION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NB_DOC_DIPLOME Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NB_DOC_DIPLOME(int i) {
		return "NOM_ST_NB_DOC_DIPLOME" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NB_DOC_DIPLOME
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NB_DOC_DIPLOME(int i) {
		return getZone(getNOM_ST_NB_DOC_DIPLOME(i));
	}

	public Hashtable<Integer, TitreFormation> getHashTitreFormation() {
		if (hashTitreFormation == null) {
			hashTitreFormation = new Hashtable<Integer, TitreFormation>();
		}
		return hashTitreFormation;
	}

	public void setHashTitreFormation(Hashtable<Integer, TitreFormation> hashTitreFormation) {
		this.hashTitreFormation = hashTitreFormation;
	}

	public Hashtable<Integer, CentreFormation> getHashCentreFormation() {
		if (hashCentreFormation == null) {
			hashCentreFormation = new Hashtable<Integer, CentreFormation>();
		}
		return hashCentreFormation;
	}

	public void setHashCentreFormation(Hashtable<Integer, CentreFormation> hashCentreFormation) {
		this.hashCentreFormation = hashCentreFormation;
	}

	public String getNOM_EF_TITRE_FORM() {
		return "NOM_EF_TITRE_FORM";
	}

	public String getVAL_EF_TITRE_FORM() {
		return getZone(getNOM_EF_TITRE_FORM());
	}

	public String getNOM_ST_TITRE_FORM() {
		return "NOM_ST_TITRE_FORM";
	}

	public String getVAL_ST_TITRE_FORM() {
		return getZone(getNOM_ST_TITRE_FORM());
	}

	public String getNOM_EF_CENTRE_FORM() {
		return "NOM_EF_CENTRE_FORM";
	}

	public String getVAL_EF_CENTRE_FORM() {
		return getZone(getNOM_EF_CENTRE_FORM());
	}

	public String getNOM_ST_CENTRE_FORM() {
		return "NOM_ST_CENTRE_FORM";
	}

	public String getVAL_ST_CENTRE_FORM() {
		return getZone(getNOM_ST_CENTRE_FORM());
	}
}
