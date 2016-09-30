package nc.mairie.gestionagent.process.pointage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.ConsultPointageDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.pointage.dto.RefTypePointageDto;
import nc.mairie.gestionagent.pointage.dto.VentilDateDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.PtgService;

import org.codehaus.plexus.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGVisualisation extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT_CREATE = 4;
	public static final int STATUT_RECHERCHER_AGENT_MAX = 2;
	public static final int STATUT_RECHERCHER_AGENT_MIN = 1;
	public static final int STATUT_SAISIE_PTG = 3;
	private String[] LB_ETAT;
	private String[] LB_TYPE;
	private String[] LB_TYPE_HS;
	private String[] LB_POPULATION;
	private ArrayList<String> listeTypeHS;
	private ArrayList<String> listePopulation;
	private ArrayList<RefEtatDto> listeEtats;
	private HashMap<Integer, ConsultPointageDto> listePointage;
	private ArrayList<RefTypePointageDto> listeTypes;
	private HashMap<Integer, List<ConsultPointageDto>> history = new HashMap<>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat hrs = new SimpleDateFormat("HH:mm");
	private Agent loggedAgent;
	public String status = "VISU";
	public String focus = null;
	private Logger logger = LoggerFactory.getLogger(OePTGVisualisation.class);

	private AffectationDao affectationDao;
	private AgentDao agentDao;

	private IAdsService adsService;

	private IRadiService radiService;

	private IPtgService ptgService;

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(
				null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	private void afficheListePointages() {
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		greg.setFirstDayOfWeek(Calendar.MONDAY);

		for (ConsultPointageDto ptg : getListePointage().values()) {
			greg.setTime(ptg.getDate());
			Integer i = ptg.getIdPointage();
			AgentDto agtPtg = ptg.getAgent();

			addZone(getNOM_ST_MATR(i),
					agtPtg.getIdAgent().toString().substring(3, agtPtg.getIdAgent().toString().length()));
			addZone(getNOM_ST_AGENT(i), agtPtg.getNom() + " " + agtPtg.getPrenom());
			addZone(getNOM_ST_TYPE(i), ptg.getTypePointage());
			addZone(getNOM_ST_RECUP(i), ptg.isHeuresSupRappelEnService() ? "RS" : ptg.isHeuresSupRecuperees() ? "R"
					: Const.CHAINE_VIDE);
			addZone(getNOM_ST_SEMAINE(i), String.valueOf(greg.get(Calendar.WEEK_OF_YEAR)));

			addZone(getNOM_ST_DATE(i), sdf.format(ptg.getDate()));

			addZone(getNOM_ST_DATE_DEB(i), hrs.format(ptg.getDebut()));
			if (ptg.getFin() != null) {
				addZone(getNOM_ST_DATE_FIN(i), hrs.format(ptg.getFin()));
			}
			addZone(getNOM_ST_DUREE(i), ptg.getQuantite());

			String strMotif = "";
			if (null != ptg.getMotif() && !Const.CHAINE_VIDE.equals(ptg.getMotif())) {
				strMotif = ptg.getMotif();
			}
			if (null != ptg.getMotif() && !Const.CHAINE_VIDE.equals(ptg.getMotif()) && null != ptg.getCommentaire()
					&& !Const.CHAINE_VIDE.equals(ptg.getCommentaire())) {
				strMotif += " - ";
			}
			if (null != ptg.getCommentaire() && !Const.CHAINE_VIDE.equals(ptg.getCommentaire())) {
				strMotif += ptg.getCommentaire();
			}

			addZone(getNOM_ST_MOTIF(i), strMotif);

			AgentDto opPtg = ptg.getOperateur();
			addZone(getNOM_ST_OPERATEUR(i), opPtg.getNom() + " " + opPtg.getPrenom() + " ("
					+ opPtg.getIdAgent().toString().substring(3, opPtg.getIdAgent().toString().length()) + ")");

			addZone(getNOM_ST_ETAT(i), EtatPointageEnum.getEtatPointageEnum(ptg.getIdRefEtat()).name());
			addZone(getNOM_ST_DATE_SAISIE(i), sdf.format(ptg.getDateSaisie()) + " à  " + hrs.format(ptg.getDateSaisie()));
		}
	}

	@Override
	public String getJSP() {
		return "OePTGVisualisation.jsp";
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_PB_FILTRER();
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ETAT() {
		if (LB_ETAT == null) {
			LB_ETAT = initialiseLazyLB();
		}
		return LB_ETAT;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_TYPE() {
		if (LB_TYPE == null) {
			LB_TYPE = initialiseLazyLB();
		}
		return LB_TYPE;
	}

	private String[] getLB_TYPE_HS() {
		if (LB_TYPE_HS == null) {
			LB_TYPE_HS = initialiseLazyLB();
		}
		return LB_TYPE_HS;
	}

	public ArrayList<RefEtatDto> getListeEtats() {
		return listeEtats;
	}

	public HashMap<Integer, ConsultPointageDto> getListePointage() {
		return listePointage == null ? new HashMap<Integer, ConsultPointageDto>() : listePointage;
	}

	public ArrayList<RefTypePointageDto> getListeTypes() {
		return listeTypes;
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
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_TYPE() {
		return "NOM_LB_TYPE";
	}

	public String getNOM_LB_TYPE_HS() {
		return "NOM_LB_TYPE_HS";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_TYPE_SELECT() {
		return "NOM_LB_TYPE_SELECT";
	}

	public String getNOM_LB_TYPE_HS_SELECT() {
		return "NOM_LB_TYPE_HS_SELECT";
	}

	public String getNOM_PB_CREATE() {
		return "NOM_PB_CREATE";
	}

	public String getNOM_PB_CREATE_CANCEL() {
		return "NOM_PB_CREATE_CANCEL";
	}

	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	public String getNOM_ST_DATE_CREATE() {
		return "NOM_ST_DATE_CREATE";
	}

	public String getVAL_ST_DATE_CREATE() {
		return getZone(getNOM_ST_DATE_CREATE());
	}

	public String getNOM_ST_AGENT_CREATE() {
		return "NOM_ST_AGENT_CREATE";
	}

	public String getVAL_ST_AGENT_CREATE() {
		return getZone(getNOM_ST_AGENT_CREATE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT_MAX Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT_MAX() {
		return "NOM_PB_RECHERCHER_AGENT_MAX";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT_MIN Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT_MIN() {
		return "NOM_PB_RECHERCHER_AGENT_MIN";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_RECHERCHER_AGENT_MAX Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX";
	}

	public String getNOM_PB_RECHERCHER_AGENT_CREATE() {
		return "NOM_PB_RECHERCHER_AGENT_CREATE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_RECHERCHER_AGENT_MIN Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN";
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
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	public String getNOM_ST_MATR(int i) {
		return "NOM_ST_MATR_" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT_MAX Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT_MAX() {
		return "NOM_ST_AGENT_MAX";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT_MIN Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT_MIN() {
		return "NOM_ST_AGENT_MIN";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE_" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEB(int i) {
		return "getNOM_ST_DATE_DEB" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "getNOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_MAX() {
		return "NOM_ST_DATE_MAX";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_ETAT() {
		return "NOM_ST_DATE_ETAT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_MIN() {
		return "NOM_ST_DATE_MIN";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique
	 * 
	 */
	public String getNOM_ST_DATE_SAISIE(int i) {
		return "getNOM_ST_DATE_SAISIE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique
	 * 
	 */
	public String getNOM_ST_DUREE(int i) {
		return "getNOM_ST_DUREE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "getNOM_ST_ETAT" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique
	 * 
	 */
	public String getNOM_ST_MOTIF(int i) {
		return "getNOM_ST_MOTIF" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE_" + i;
	}

	public String getNOM_ST_RECUP(int i) {
		return "NOM_ST_RECUP_" + i;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-VISU";
	}

	public String getVal_Del(int i) {
		return "Delete_F" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getVal_DelAll() {
		return "Delete_All";
	}

	public String getVal_Delay(int i) {
		return "Delay_F" + i;
	}

	public String getVal_DelayAll() {
		return "Delay_All";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_ETAT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_TYPE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_TYPE() {
		return getLB_TYPE();
	}

	public String[] getVAL_LB_TYPE_HS() {
		return getLB_TYPE_HS();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_TYPE_SELECT() {
		return getZone(getNOM_LB_TYPE_SELECT());
	}

	public String getVAL_LB_TYPE_HS_SELECT() {
		return getZone(getNOM_LB_TYPE_HS_SELECT());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	public String getVAL_ST_MATR(int i) {
		return getZone(getNOM_ST_MATR(i));
	}

	/**
     *
     */
	public String getSAISIE_PTG(int i) {
		return "getSAISIE_PTG_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT_MAX Date
	 * de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT_MAX() {
		return getZone(getNOM_ST_AGENT_MAX());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT_MIN Date
	 * de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT_MIN() {
		return getZone(getNOM_ST_AGENT_MIN());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone
	 * 
	 */
	public String getVAL_ST_DATE_DEB(int i) {
		return getZone(getNOM_ST_DATE_DEB(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_MAX() {
		return getZone(getNOM_ST_DATE_MAX());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_ETAT() {
		return getZone(getNOM_ST_DATE_ETAT());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_MIN() {
		return getZone(getNOM_ST_DATE_MIN());
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone
	 * 
	 */
	public String getVAL_ST_DATE_SAISIE(int i) {
		return getZone(getNOM_ST_DATE_SAISIE(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone
	 * 
	 */
	public String getVAL_ST_DUREE(int i) {
		return getZone(getNOM_ST_DUREE(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone
	 * 
	 */
	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TYPE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	public String getVAL_ST_RECUP(int i) {
		return getZone(getNOM_ST_RECUP(i));
	}

	public String getVal_Valid(int i) {
		return "Valid_" + i;
	}

	public String getVal_ValidAll() {
		return "Valid_All";
	}

	public String getValHistory(int id) {
		return "History_" + id;
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			List<RefEtatDto> etats = ptgService.getEtatsPointage();
			setListeEtats((ArrayList<RefEtatDto>) etats);
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (RefEtatDto etat : etats) {
				String ligne[] = { etat.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);

		}
		// Si liste type vide alors affectation
		if (getLB_TYPE() == LBVide) {
			List<RefTypePointageDto> types = ptgService.getTypesPointage();
			setListeTypes((ArrayList<RefTypePointageDto>) types);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (RefTypePointageDto type : types) {
				String ligne[] = { type.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_TYPE_SELECT(), Const.ZERO);

		}
		// Si liste type vide alors affectation
		if (getLB_TYPE_HS() == LBVide) {
			ArrayList<String> listeTypeHS = new ArrayList<String>();
			listeTypeHS.add("Payées");
			listeTypeHS.add("Récupérées");
			listeTypeHS.add("Rappel en service");
			setListeTypeHS(listeTypeHS);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (String pop : listeTypeHS) {
				String ligne[] = { pop };
				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_HS(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_TYPE_HS_SELECT(), Const.ZERO);

		}
		// Si liste population vide alors affectation
		if (getLB_POPULATION() == LBVide) {
			ArrayList<String> listeStatut = new ArrayList<String>();
			listeStatut.add("Fonctionnaire");
			listeStatut.add("Convention collective");
			listeStatut.add("Contractuel");
			setListePopulation(listeStatut);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (String pop : listeStatut) {
				String ligne[] = { pop };
				aFormat.ajouteLigne(ligne);
			}
			setLB_POPULATION(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_POPULATION_SELECT(), Const.ZERO);

		}
	}

	@Override
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

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		// Initialisation des infos sur la ventilation
		initialiseInfoVentilation();

		if (etatStatut() == STATUT_SAISIE_PTG) {
			performPB_FILTRER();
			if(getTransaction().isErreur()) {
				getTransaction().setErreur(false);
			}
			String infos = (String) VariableGlobale.recuperer(request, "MESSAGE_INFO_SAISIE_POINTAGE");
			if(null != infos
					&& !"".equals(infos.trim())) {
				getTransaction().declarerErreur("INFO : " + infos);
				VariableGlobale.enlever(request, "MESSAGE_INFO_SAISIE_POINTAGE");
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_MIN) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_MIN(), agt.getNomatr().toString());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_MAX) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_MAX(), agt.getNomatr().toString());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATE(), agt.getNomatr().toString());
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
	}

	private void initialiseInfoVentilation() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_ST_DATE_VENTIL_CC(), "Aucune");
		addZone(getNOM_ST_DATE_VENTIL_F_C(), "Aucune");

		VentilDateDto ventilEnCoursCC = getInfoVentilation("CC");
		if (ventilEnCoursCC != null) {
			addZone(getNOM_ST_DATE_VENTIL_CC(), sdf.format(ventilEnCoursCC.getDateVentil()));
		}

		VentilDateDto ventilEnCoursF = getInfoVentilation("F");
		if (ventilEnCoursF != null) {
			addZone(getNOM_ST_DATE_VENTIL_F_C(), sdf.format(ventilEnCoursF.getDateVentil()));
		}

	}

	private VentilDateDto getInfoVentilation(String statut) {
		VentilDateDto dto = ptgService.getVentilationEnCours(statut);
		return dto;
	}

	private boolean performControlerFiltres() throws Exception {
		String dateDeb = getVAL_ST_DATE_MIN();
		if (dateDeb.equals(Const.CHAINE_VIDE)) {
			// "ERR500",
			// "Le champ date de début est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR500"));
			return false;
		}

		// on controle que le service saisie est bien un service
		String sigleService = getVAL_EF_SERVICE().toUpperCase();
		if (!sigleService.equals(Const.CHAINE_VIDE)) {

			// on cherche le code service associé
			EntiteDto serv = adsService.getEntiteBySigle(sigleService);
			if (null == serv || 0 == serv.getIdEntite()) {
				// ERR502", "Le sigle service saisie ne permet pas de trouver le
				// service associé."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR502"));
				return false;
			}
		}

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean performPB_FILTRER() throws Exception {
		if (!performControlerFiltres()) {
			return false;
		}

		String dateDeb = getVAL_ST_DATE_MIN();
		String dateMin = Services.convertitDate(dateDeb, "dd/MM/yyyy", "yyyyMMdd");

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = null;
		String dateEtat = getVAL_ST_DATE_ETAT().equals(Const.CHAINE_VIDE)?null : Services.convertitDate(getVAL_ST_DATE_ETAT(), "dd/MM/yyyy", "yyyyMMdd");
		if (!dateFin.equals(Const.CHAINE_VIDE)) {
			dateMax = Services.convertitDate(dateFin, "dd/MM/yyyy", "yyyyMMdd");
		} else {
			// dateMax = new SimpleDateFormat("yyyyMMdd").format(new Date());
			dateMax = dateMin;
			addZone(getNOM_ST_DATE_MAX(), getVAL_ST_DATE_MIN());
		}
		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
		RefEtatDto etat = null;
		if (numEtat != -1 && numEtat != 0) {
			etat = (RefEtatDto) getListeEtats().get(numEtat - 1);
		}
		// type
		int numType = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);
		RefTypePointageDto type = null;
		if (numType != -1 && numType != 0) {
			type = (RefTypePointageDto) getListeTypes().get(numType - 1);
		}
		// type heures sup
		int numTypeHS = (Services.estNumerique(getZone(getNOM_LB_TYPE_HS_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_TYPE_HS_SELECT())) : -1);
		String typeHS = null;
		if (numTypeHS != -1 && numTypeHS != 0) {
			String libTypeHS = getListeTypeHS().get(numTypeHS - 1);
			if (libTypeHS.equals("Récupérées")) {
				typeHS = "R";
			} else if (libTypeHS.equals("Rappel en service")) {
				typeHS = "RS";
			} else if (libTypeHS.equals("Payées")) {
				typeHS = "AUTRE";
			}
		}

		if (getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MAX(), getVAL_ST_AGENT_MIN());
		}
		if (getVAL_ST_AGENT_MIN().equals(Const.CHAINE_VIDE) && !getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT_MIN(), getVAL_ST_AGENT_MAX());
		}

		Integer idAgentMin = getVAL_ST_AGENT_MIN().equals(Const.CHAINE_VIDE) ? null : Integer.valueOf("900"
				+ getVAL_ST_AGENT_MIN());
		Integer idAgentMax = getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE) ? null : Integer.valueOf("900"
				+ getVAL_ST_AGENT_MAX());

		// si superieur a 1000 alors on bloque
		boolean filtreAgent = false;

		Collection<String> idAgentPopulation = new ArrayList<>();
		// population
		int numPopulation = (Services.estNumerique(getZone(getNOM_LB_POPULATION_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_POPULATION_SELECT())) : -1);
		String population = null;
		if (numPopulation != -1 && numPopulation != 0) {
			filtreAgent = true;
			population = getListePopulation().get(numPopulation - 1);
			ArrayList<Agent> listAgent = getAgentDao().listerAgentWithStatut(population);
			ArrayList<Integer> listeTempAgent = new ArrayList<Integer>();
			for (Agent ag : listAgent) {
				listeTempAgent.add(ag.getIdAgent());
			}
			// on receupere tous les agents qui ont des pointages
			// on regarde si il sont du type de population choisi
			ArrayList<Integer> listIdAgentPtg = (ArrayList<Integer>) ptgService.getListeIdAgentPointage();
			for (Integer idAgentPtg : listIdAgentPtg) {
				if (listeTempAgent.contains(idAgentPtg) && !idAgentPopulation.contains(idAgentPtg.toString())) {
					idAgentPopulation.add(idAgentPtg.toString());
				}
			}
		}

		// Agent Min et/ou MAx
		Collection<String> idAgentMinMax = new ArrayList<>();
		if (idAgentMin != null && idAgentMax == null) {
			filtreAgent = true;
			if (!idAgentMinMax.contains(idAgentMin)) {
				idAgentMinMax.add(idAgentMin.toString());
			}
		} else if (idAgentMin != null && idAgentMax != null) {
			filtreAgent = true;
			ArrayList<Agent> listAgent = getAgentDao().listerAgentEntreDeuxIdAgent(idAgentMin, idAgentMax);
			for (Agent ag : listAgent) {
				if (!idAgentMinMax.contains(ag.getIdAgent().toString())) {
					idAgentMinMax.add(ag.getIdAgent().toString());
				}
			}
		}

		// SERVICE
		Collection<String> idAgentService = new ArrayList<String>();
		String sigle = getVAL_EF_SERVICE().toUpperCase();
		String idServiceAds = getVAL_ST_ID_SERVICE_ADS().toUpperCase();

		if (!sigle.equals(Const.CHAINE_VIDE)) {
			EntiteDto service = adsService.getEntiteBySigle(sigle);
			idServiceAds = service.getIdEntite().toString();
		}

		if (!idServiceAds.equals(Const.CHAINE_VIDE)) {
			filtreAgent = true;

			// Récupération des agents
			// on recupere les sous-service du service selectionne
			List<Integer> listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));

			ArrayList<Agent> listAgent = getAgentDao().listerAgentAvecServicesETMatricules(listeSousService,
					idAgentMin, idAgentMax);
			// on supprime les doublons
			for (Agent ag : listAgent) {
				if (!idAgentService.contains(ag.getIdAgent().toString())) {
					idAgentService.add(ag.getIdAgent().toString());
				}
			}
		}

		// on fait l'intersection des listes d'agent
		Collection<String> intersectionCollection = new ArrayList<>();
		if (idAgentPopulation.size() > 0) {
			intersectionCollection = idAgentPopulation;
			if (idAgentMinMax.size() > 0) {
				intersectionCollection = CollectionUtils.intersection(intersectionCollection, idAgentMinMax);
				if (idAgentService.size() > 0) {
					intersectionCollection = CollectionUtils.intersection(intersectionCollection, idAgentService);
				}
			} else {
				if (idAgentService.size() > 0) {
					intersectionCollection = CollectionUtils.intersection(intersectionCollection, idAgentService);
				}
			}
		} else {
			if (idAgentMinMax.size() > 0) {
				intersectionCollection = idAgentMinMax;
				if (idAgentService.size() > 0) {
					intersectionCollection = CollectionUtils.intersection(intersectionCollection, idAgentService);
				}
			} else {
				if (idAgentService.size() > 0) {
					intersectionCollection = idAgentService;
				}
			}
		}

		if (intersectionCollection.size() == 0) {
			if (!filtreAgent) {
				intersectionCollection = null;
			}
		} else if (intersectionCollection.size() >= 1000) {
			// "ERR501",
			// "La sélection des filtres engendre plus de 1000 agents. Merci de reduire la sélection."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR501"));
			return false;
		}

		List<ConsultPointageDto> _listePointage = ptgService.getVisualisationPointage(dateMin, dateMax,
				(List<String>) intersectionCollection, etat != null ? etat.getIdRefEtat() : null,
				type != null ? type.getIdRefTypePointage() : null, typeHS,dateEtat);
		setListePointage((ArrayList<ConsultPointageDto>) _listePointage);
		// loadHistory();

		afficheListePointages();

		return true;
	}

	public boolean performPB_VALID(HttpServletRequest request) throws Exception {
		changeState(request, getListePointage().values(), EtatPointageEnum.APPROUVE);
		return true;
	}

	public boolean performPB_VALID(HttpServletRequest request, int i) throws Exception {
		changeState(request, getListePointage().get(i), EtatPointageEnum.APPROUVE);
		return true;
	}

	public boolean performPB_DEL(HttpServletRequest request) throws Exception {
		changeState(request, getListePointage().values(), EtatPointageEnum.REJETE);
		return true;
	}

	public boolean performPB_DEL(HttpServletRequest request, int i) throws Exception {
		changeState(request, getListePointage().get(i), EtatPointageEnum.REJETE);
		return true;
	}

	public boolean performPB_DELAY(HttpServletRequest request) throws Exception {
		changeState(request, getListePointage().values(), EtatPointageEnum.EN_ATTENTE);
		return true;
	}

	public boolean performPB_DELAY(HttpServletRequest request, int i) throws Exception {
		changeState(request, getListePointage().get(i), EtatPointageEnum.EN_ATTENTE);
		return true;
	}

	private void changeState(HttpServletRequest request, ConsultPointageDto ptg, EtatPointageEnum state)
			throws Exception {
		ArrayList<ConsultPointageDto> param = new ArrayList<>();
		param.add(ptg);
		changeState(request, param, state);
	}

	private void changeState(HttpServletRequest request, Collection<ConsultPointageDto> ptg, EtatPointageEnum state)
			throws Exception {
		ArrayList<Integer> ids = new ArrayList<>();
		for (ConsultPointageDto pt : ptg) {
			ids.add(pt.getIdPointage());
			refreshHistory(pt.getIdPointage());
		}

		if (!verifieDroitAgents(request)) {
			logger.debug("Agent nul dans jsp visualisation");
		} else {

			try {

				ReturnMessageDto message = ptgService.setPtgState(ids, state.ordinal(), loggedAgent.getIdAgent());
				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur("ERREUR : " + err);
				}
				performPB_FILTRER();
			} catch (Exception e) {
				logger.debug("Exception in performPB_FILTRER");
			}
		}
	}

	private boolean verifieDroitAgents(HttpServletRequest request) throws Exception {

		if (null == loggedAgent) {
			// on recupere l'agent connecte
			UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
			// on fait la correspondance entre le login et l'agent via RADI

			LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
			if (user == null) {
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				loggedAgent = getAgentDao().chercherAgentParMatricule(
						radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_MAX, true);
		return true;
	}

	public boolean performPB_RECHERCHER_AGENT_CREATE(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_CREATE, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_MIN, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_MAX(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_MIN(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			setFocus(getDefaultFocus());

			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER();
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_MIN
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_MIN())) {
				return performPB_RECHERCHER_AGENT_MIN(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_MIN
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_MAX
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_MAX())) {
				return performPB_RECHERCHER_AGENT_MAX(request);
			}

			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATE())) {
				return performPB_RECHERCHER_AGENT_CREATE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_MAX
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(request);
			}

			if (testerParametre(request, getVal_ValidAll())) {
				return performPB_VALID(request);
			}
			if (testerParametre(request, getNOM_PB_CREATE_BOX())) {
				addZone(getNOM_ST_DATE_CREATE(), Const.CHAINE_VIDE);
				addZone(getNOM_ST_AGENT_CREATE(), Const.CHAINE_VIDE);
				status = "CREATION";
				setFocus(getNOM_PB_CREATE());
				setStatut(STATUT_MEME_PROCESS);
				return true;
			}

			if (testerParametre(request, getVal_DelayAll())) {
				return performPB_DELAY(request);
			}

			if (testerParametre(request, getVal_DelAll())) {
				return performPB_DEL(request);
			}

			for (int i : getListePointage().keySet()) {
				if (testerParametre(request, getVal_Del(i))) {
					return performPB_DEL(request, i);
				}
				if (testerParametre(request, getVal_Valid(i))) {
					return performPB_VALID(request, i);
				}
				if (testerParametre(request, getVal_Delay(i))) {
					return performPB_DELAY(request, i);
				}
				if (testerParametre(request, getSAISIE_PTG(i))) {
					VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_PTG,
							Integer.valueOf(getVAL_ST_MATR(i)));
					VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LUNDI_PTG, getLundi(i));
					setStatut(STATUT_SAISIE_PTG, true);
					return true;
				}
			}

			if (testerParametre(request, getNOM_PB_CREATE())) {
				// on verifie que l'agent exist et qu'il est affecté
				if (!performControlerSaisie(getVAL_ST_DATE_CREATE(), getVAL_ST_AGENT_CREATE())) {
					return false;
				}

				status = "EDIT";
				VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_PTG,
						Integer.valueOf(getVAL_ST_AGENT_CREATE()));
				VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LUNDI_PTG,
						getLundiCreate(getVAL_ST_DATE_CREATE()));
				setStatut(STATUT_SAISIE_PTG, true);
				return true;
			}

			if (testerParametre(request, getNOM_PB_CREATE_CANCEL())) {
				status = "EDIT";
				setStatut(STATUT_MEME_PROCESS);
				return true;
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean performControlerSaisie(String dateCreation, String idAgent) throws Exception {
		if (dateCreation.equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date"));
			return false;
		}
		if (idAgent.equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		}
		try {
			@SuppressWarnings("unused")
			Agent agent = getAgentDao().chercherAgent(Integer.valueOf("900" + idAgent));
		} catch (Exception e) {
			// "ERR503",
			// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
			return false;
		}

		// on transforme la date
		if (Services.estNumerique(dateCreation)) {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			SimpleDateFormat sdfSortie = new SimpleDateFormat("dd/MM/yyyy");
			dateCreation = sdfSortie.format(sdf.parse(dateCreation));
		}
		// on cherche l'affectation en cours
		try {
			@SuppressWarnings("unused")
			Affectation affEnCours = getAffectationDao().chercherAffectationAgentPourDate(
					Integer.valueOf("900" + idAgent), sdf.parse(dateCreation));
		} catch (Exception e) {
			// "ERR504",
			// "L'agent @ n'est affecté a aucun poste le @. Aucun pointage ne peut être saisi pour cette date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR504", idAgent, dateCreation));
			return false;
		}
		return true;
	}

	/**
	 * Setter de la liste: LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ETAT(String[] newLB_ETAT) {
		LB_ETAT = newLB_ETAT;
	}

	/**
	 * Setter de la liste: LB_TYPE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_TYPE(String[] newLB_TYPE) {
		LB_TYPE = newLB_TYPE;
	}

	private void setLB_TYPE_HS(String[] newLB_TYPE_HS) {
		LB_TYPE_HS = newLB_TYPE_HS;
	}

	public void setListeEtats(ArrayList<RefEtatDto> listeEtats) {
		this.listeEtats = listeEtats;
	}

	public void setListePointage(ArrayList<ConsultPointageDto> _listePointage) {
		listePointage = new HashMap<>();
		for (ConsultPointageDto ptg : _listePointage) {
			listePointage.put(ptg.getIdPointage(), ptg);
		}
	}

	public void setListeTypes(ArrayList<RefTypePointageDto> listeTypes) {
		this.listeTypes = listeTypes;
	}

	public Agent getLoggedAgent() {
		return loggedAgent;
	}

	public int getHistorySize() {
		return history.size();
	}

	private void refreshHistory(int ptgId) {
		history.remove(ptgId);
		history.put(ptgId, ptgService.getVisualisationHistory(ptgId));
	}

	public String getHistory(int ptgId) {

		List<ConsultPointageDto> data = getPtgService().getVisualisationHistory(ptgId);
		int numParams = 8;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		for (ConsultPointageDto p : data) {
			ret[index][0] = formatDate(p.getDate());
			ret[index][1] = formatHeure(p.getDebut()).equals("00:00") ? "&nbsp;" : formatHeure(p.getDebut());
			ret[index][2] = formatHeure(p.getFin()).equals("00:00") ? "&nbsp;" : formatHeure(p.getFin());
			ret[index][3] = Const.CHAINE_VIDE + p.getQuantite();
			ret[index][4] = (null == p.getMotif() ? "" : p.getMotif()) + (null != p.getMotif() && null != p.getCommentaire() ? " - " : "") + (null == p.getCommentaire() ? "" : p.getCommentaire());
			AgentDto opPtg = p.getOperateur();
			ret[index][5] = opPtg.getNom() + " " + opPtg.getPrenom() + " ("
					+ opPtg.getIdAgent().toString().substring(3, opPtg.getIdAgent().toString().length()) + ")";
			ret[index][6] = EtatPointageEnum.getEtatPointageEnum(p.getIdRefEtat()).name();
			ret[index][7] = formatDate(p.getDateSaisie()) + " a " + formatHeure(p.getDateSaisie());
			index++;
		}

		StringBuilder strret = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			// strret.append("[");
			for (int j = 0; j < numParams; j++) {
				strret.append(ret[i][j]).append(",");
			}
			strret.deleteCharAt(strret.lastIndexOf(","));
			strret.append("|");
		}
		strret.deleteCharAt(strret.lastIndexOf("|"));
		return strret.toString();
	}

	private String formatDate(Date d) {
		if (d != null) {
			return sdf.format(d);
		} else {
			return Const.CHAINE_VIDE;
		}
	}

	private String formatHeure(Date d) {
		if (d != null) {
			return hrs.format(d);
		} else {
			return "00:00";
		}
	}

	public String getLundi(int idPtg) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		cal.setTime(listePointage.get(idPtg).getDate());
		cal.set(Calendar.DAY_OF_WEEK, -6); // back to previous week
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // jump to next monday.
		return sdf.format(cal.getTime());
	}

	public String getLundiCreate(String dateCreation) throws ParseException {
		// on transforme la date
		if (Services.estNumerique(dateCreation)) {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			SimpleDateFormat sdfSortie = new SimpleDateFormat("dd/MM/yyyy");
			dateCreation = sdfSortie.format(sdf.parse(dateCreation));
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		try {
			cal.setTimeInMillis(sdf.parse(dateCreation).getTime());
		} catch (ParseException ex) {
			cal.setTime(new Date());
		}
		cal.set(Calendar.DAY_OF_WEEK, -6); // back to previous week
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // jump to next monday.
		return sdf.format(cal.getTime());
	}

	public List<ConsultPointageDto> getHistoryTable(int ptgId) {
		return history.get(ptgId);
	}

	private String[] getLB_POPULATION() {
		if (LB_POPULATION == null) {
			LB_POPULATION = initialiseLazyLB();
		}
		return LB_POPULATION;
	}

	private void setLB_POPULATION(String[] newLB_POPULATION) {
		LB_POPULATION = newLB_POPULATION;
	}

	public String getNOM_LB_POPULATION() {
		return "NOM_LB_POPULATION";
	}

	public String getNOM_LB_POPULATION_SELECT() {
		return "NOM_LB_POPULATION_SELECT";
	}

	public String[] getVAL_LB_POPULATION() {
		return getLB_POPULATION();
	}

	public String getVAL_LB_POPULATION_SELECT() {
		return getZone(getNOM_LB_POPULATION_SELECT());
	}

	public ArrayList<String> getListePopulation() {
		return listePopulation;
	}

	public void setListePopulation(ArrayList<String> listePopulation) {
		this.listePopulation = listePopulation;
	}

	public String getNOM_ST_DATE_VENTIL_CC() {
		return "NOM_ST_DATE_VENTIL_CC";
	}

	public String getVAL_ST_DATE_VENTIL_CC() {
		return getZone(getNOM_ST_DATE_VENTIL_CC());
	}

	public String getNOM_ST_DATE_VENTIL_F_C() {
		return "NOM_ST_DATE_VENTIL_F_C";
	}

	public String getVAL_ST_DATE_VENTIL_F_C() {
		return getZone(getNOM_ST_DATE_VENTIL_F_C());
	}

	public String getNOM_PB_CREATE_BOX() {
		return "NOM_PB_CREATE_BOX";
	}

	public String getVAL_ST_SEMAINE(int i) {
		return getZone(getNOM_ST_SEMAINE(i));
	}

	public String getNOM_ST_SEMAINE(int i) {
		return "NOM_ST_SEMAINE_" + i;
	}

	public String getNOM_ST_OPERATEUR(int i) {
		return "getNOM_ST_OPERATEUR" + i;
	}

	public String getVAL_ST_OPERATEUR(int i) {
		return getZone(getNOM_ST_OPERATEUR(i));
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

	public ArrayList<String> getListeTypeHS() {
		return listeTypeHS;
	}

	public void setListeTypeHS(ArrayList<String> listeTypeHS) {
		this.listeTypeHS = listeTypeHS;
	}

	public IPtgService getPtgService() {

		if (null == ptgService) {
			ApplicationContext context = ApplicationContextProvider.getContext();
			ptgService = (PtgService) context.getBean("ptgService");
		}
		return ptgService;
	}

	public void setPtgService(IPtgService ptgService) {
		this.ptgService = ptgService;
	}
	
}
