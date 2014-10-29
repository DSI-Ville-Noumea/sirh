package nc.mairie.gestionagent.process.agent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.FiltreSoldeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.absence.dto.SoldeMonthDto;
import nc.mairie.gestionagent.absence.dto.SoldeSpecifiqueDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 * Process OeAGENTAbsences Date de création : (05/09/11 11:31:37)
 * 
 */
public class OeAGENTAbsencesSolde extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	public String ACTION_VISUALISATION = "Consultation de l'historique d'un compteur";

	private Agent agentCourant;
	private ArrayList<TypeAbsenceDto> listeTypeAbsence;
	private ArrayList<HistoriqueSoldeDto> listeHistorique;
	private ArrayList<SoldeMonthDto> listeSoldeA55;
	private ArrayList<SoldeSpecifiqueDto> listeSoldeCongesExcep;

	private ArrayList<String> listeAnnee;
	private String[] LB_ANNEE;

	private DecimalFormat df = new DecimalFormat("0");

	private AgentDao agentDao;

	/**
	 * Constructeur du process OeAGENTAbsences. Date de création : (05/09/11
	 * 11:39:24)
	 * 
	 */
	public OeAGENTAbsencesSolde() {
		super();
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (05/09/11 11:39:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
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

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				Integer annee = cal.get(Calendar.YEAR);
				initialiseSoldesAgent(request, annee);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseListeDeroulante() {
		if (getListeTypeAbsence().size() == 0) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeTypeAbsence((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto(null));
		}
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			Integer annee = cal.get(Calendar.YEAR);

			String[] list = { String.valueOf(annee), String.valueOf(annee + 1), String.valueOf(annee + 2) };
			ArrayList<String> arrayList = new ArrayList<String>();
			for (String an : list)
				arrayList.add(an);

			setListeAnnee(arrayList);

			setLB_ANNEE(list);
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
	}

	private void initialiseSoldesAgent(HttpServletRequest request, Integer annee) {
		Date dateDeb = new DateTime(annee, 1, 1, 0, 0, 0).toDate();
		Date dateFin = new DateTime(annee, 12, 31, 23, 59, 59).toDate();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		FiltreSoldeDto dto = new FiltreSoldeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateFin(dateFin);
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		// Solde depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		SoldeDto soldeGlobal = consuAbs.getSoldeAgent(getAgentCourant().getIdAgent(), json);

		// solde congés
		addZone(getNOM_ST_SOLDE_CONGE(), soldeGlobal.getSoldeCongeAnnee() == 0 ? "&nbsp;" : soldeGlobal
				.getSoldeCongeAnnee().toString() + " j");
		addZone(getNOM_ST_SOLDE_CONGE_PREC(), soldeGlobal.getSoldeCongeAnneePrec() == 0 ? "&nbsp;" : soldeGlobal
				.getSoldeCongeAnneePrec().toString() + " j");

		// Solde recup
		int soldeRecup = soldeGlobal.getSoldeRecup().intValue();
		String soldeRecupHeure = (soldeRecup / 60) == 0 ? "" : soldeRecup / 60 + "h ";
		String soldeRecupMinute = (soldeRecup % 60) == 0 ? "&nbsp;" : soldeRecup % 60 + "m";
		addZone(getNOM_ST_SOLDE_RECUP(), soldeRecupHeure + soldeRecupMinute);

		// Solde repos comp
		int soldeReposComp = soldeGlobal.getSoldeReposCompAnnee().intValue();
		String soldeReposCompHeure = (soldeReposComp / 60) == 0 ? "" : soldeReposComp / 60 + "h ";
		String soldeReposCompMinute = (soldeReposComp % 60) == 0 ? "&nbsp;" : soldeReposComp % 60 + "m";
		addZone(getNOM_ST_SOLDE_REPOS_COMP(), soldeReposCompHeure + soldeReposCompMinute);

		int soldeReposCompPrec = soldeGlobal.getSoldeReposCompAnneePrec().intValue();
		String soldeReposCompPrecHeure = (soldeReposCompPrec / 60) == 0 ? "" : soldeReposCompPrec / 60 + "h ";
		String soldeReposCompPrecMinute = (soldeReposCompPrec % 60) == 0 ? "&nbsp;" : soldeReposCompPrec % 60 + "m";
		addZone(getNOM_ST_SOLDE_REPOS_COMP_PREC(), soldeReposCompPrecHeure + soldeReposCompPrecMinute);

		// Solde ASA A48
		addZone(getNOM_ST_SOLDE_ASA_A48(), soldeGlobal.getSoldeAsaA48() == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaA48()
				.toString() + " j");

		// Solde ASA A54
		addZone(getNOM_ST_SOLDE_ASA_A54(), soldeGlobal.getSoldeAsaA54() == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaA54()
				.toString() + " j");

		// Solde ASA A55
		setListeSoldeA55((ArrayList<SoldeMonthDto>) soldeGlobal.getListeSoldeAsaA55());
		for (int i = 0; i < getListeSoldeA55().size(); i++) {
			SoldeMonthDto monthDto = getListeSoldeA55().get(i);
			String soldeAsaA55Heure = (monthDto.getSoldeAsaA55() / 60) == 0 ? "" : monthDto.getSoldeAsaA55() / 60
					+ "h ";
			String soldeAsaA55Minute = (monthDto.getSoldeAsaA55() % 60) == 0 ? "&nbsp;" : monthDto.getSoldeAsaA55()
					% 60 + "m";
			addZone(getNOM_ST_SOLDE_ASA_A55(i), soldeAsaA55Heure + soldeAsaA55Minute);
			addZone(getNOM_ST_DEBUT_ASA_A55(i), sdf.format(monthDto.getDateDebut()));
			addZone(getNOM_ST_FIN_ASA_A55(i), sdf.format(monthDto.getDateFin()));
		}

		df.setRoundingMode(RoundingMode.DOWN);
		setListeSoldeCongesExcep((ArrayList<SoldeSpecifiqueDto>) soldeGlobal.getListeSoldeCongesExcep());
		for (int i = 0; i < getListeSoldeCongesExcep().size(); i++) {
			SoldeSpecifiqueDto soldeSpecifiqueDto = getListeSoldeCongesExcep().get(i);
			if ("minutes".equals(soldeSpecifiqueDto.getUniteDecompte())) {
				String soldeCongesExcepHeure = "0".equals(df.format(soldeSpecifiqueDto.getSolde() / 60)) ? "" : df
						.format(soldeSpecifiqueDto.getSolde() / 60) + "h ";
				String soldeCongesExcepMinute = "0".equals(df.format(soldeSpecifiqueDto.getSolde() % 60)) ? "&nbsp;"
						: df.format(soldeSpecifiqueDto.getSolde() % 60) + "m";
				addZone(getNOM_ST_SOLDE_CONGES_EXCEP(i), soldeCongesExcepHeure + soldeCongesExcepMinute);
			}
			if ("jours".equals(soldeSpecifiqueDto.getUniteDecompte())) {
				String soldeCongesExcepHeure = soldeSpecifiqueDto.getSolde() + " j";
				addZone(getNOM_ST_SOLDE_CONGES_EXCEP(i), soldeCongesExcepHeure);
			}

			addZone(getNOM_ST_TYPE_CONGES_EXCEP(i), soldeSpecifiqueDto.getLibelle());
		}
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
			// Si clic sur le bouton PB_HISTORIQUE
			for (int i = 0; i < getListeTypeAbsence().size(); i++) {
				Integer code = getListeTypeAbsence().get(i).getIdRefTypeAbsence();
				if (testerParametre(request, getNOM_PB_HISTORIQUE(code))) {
					return performPB_HISTORIQUE(request, code);
				}
			}

			// Si clic sur le bouton PB_ANNEE
			if (testerParametre(request, getNOM_PB_ANNEE())) {
				return performPB_ANNEE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (29/09/11 10:03:37)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTAbsencesSolde.jsp";
	}

	public String getNomEcran() {
		return "ECR-AG-ABS";
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	public String getNOM_ST_SOLDE_CONGE_PREC() {
		return "NOM_ST_SOLDE_CONGE_PREC";
	}

	public String getVAL_ST_SOLDE_CONGE_PREC() {
		return getZone(getNOM_ST_SOLDE_CONGE_PREC());
	}

	public String getNOM_ST_SOLDE_CONGE() {
		return "NOM_ST_SOLDE_CONGE";
	}

	public String getVAL_ST_SOLDE_CONGE() {
		return getZone(getNOM_ST_SOLDE_CONGE());
	}

	public String getNOM_ST_SOLDE_RECUP() {
		return "NOM_ST_SOLDE_RECUP";
	}

	public String getVAL_ST_SOLDE_RECUP() {
		return getZone(getNOM_ST_SOLDE_RECUP());
	}

	public String getNOM_ST_SOLDE_REPOS_COMP_PREC() {
		return "NOM_ST_SOLDE_REPOS_COMP_PREC";
	}

	public String getVAL_ST_SOLDE_REPOS_COMP_PREC() {
		return getZone(getNOM_ST_SOLDE_REPOS_COMP_PREC());
	}

	public String getNOM_ST_SOLDE_REPOS_COMP() {
		return "NOM_ST_SOLDE_REPOS_COMP";
	}

	public String getVAL_ST_SOLDE_REPOS_COMP() {
		return getZone(getNOM_ST_SOLDE_REPOS_COMP());
	}

	public String getNOM_ST_SOLDE_ASA_A48() {
		return "NOM_ST_SOLDE_ASA_A48";
	}

	public String getVAL_ST_SOLDE_ASA_A48() {
		return getZone(getNOM_ST_SOLDE_ASA_A48());
	}

	public String getNOM_ST_SOLDE_ASA_A54() {
		return "NOM_ST_SOLDE_ASA_A54";
	}

	public String getVAL_ST_SOLDE_ASA_A54() {
		return getZone(getNOM_ST_SOLDE_ASA_A54());
	}

	public String getNOM_ST_SOLDE_ASA_A55(int i) {
		return "NOM_ST_SOLDE_ASA_A55_" + i;
	}

	public String getVAL_ST_SOLDE_ASA_A55(int i) {
		return getZone(getNOM_ST_SOLDE_ASA_A55(i));
	}

	public String getNOM_ST_DEBUT_ASA_A55(int i) {
		return "NOM_ST_DEBUT_ASA_A55_" + i;
	}

	public String getVAL_ST_DEBUT_ASA_A55(int i) {
		return getZone(getNOM_ST_DEBUT_ASA_A55(i));
	}

	public String getNOM_ST_FIN_ASA_A55(int i) {
		return "NOM_ST_FIN_ASA_A55_" + i;
	}

	public String getVAL_ST_FIN_ASA_A55(int i) {
		return getZone(getNOM_ST_FIN_ASA_A55(i));
	}

	public String getNOM_PB_HISTORIQUE(int i) {
		return "NOM_PB_HISTORIQUE" + i;
	}

	public String getNOM_ST_SOLDE_CONGES_EXCEP(int i) {
		return "NOM_ST_SOLDE_CONGES_EXCEP_" + i;
	}

	public String getVAL_ST_SOLDE_CONGES_EXCEP(int i) {
		return getZone(getNOM_ST_SOLDE_CONGES_EXCEP(i));
	}

	public String getNOM_ST_TYPE_CONGES_EXCEP(int i) {
		return "NOM_ST_TYPE_CONGES_EXCEP_" + i;
	}

	public String getVAL_ST_TYPE_CONGES_EXCEP(int i) {
		return getZone(getNOM_ST_TYPE_CONGES_EXCEP(i));
	}

	public boolean performPB_HISTORIQUE(HttpServletRequest request, Integer codeTypeAbsence) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ANNEE_SELECT())) : -1);

		if (numAnnee < 0 || getListeAnnee().size() == 0 || numAnnee > getListeAnnee().size())
			return false;

		String annee = getListeAnnee().get(numAnnee);

		Date dateDeb = new DateTime(Integer.valueOf(annee), 1, 1, 0, 0, 0).toDate();
		Date dateFin = new DateTime(Integer.valueOf(annee), 12, 31, 23, 59, 59).toDate();
		FiltreSoldeDto dto = new FiltreSoldeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateFin(dateFin);
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		// Liste depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ArrayList<HistoriqueSoldeDto> listeHistorique = (ArrayList<HistoriqueSoldeDto>) consuAbs
				.getHistoriqueCompteurAgent(getAgentCourant().getIdAgent(), codeTypeAbsence, json);
		setListeHistorique(listeHistorique);

		for (int i = 0; i < getListeHistorique().size(); i++) {

			HistoriqueSoldeDto histo = (HistoriqueSoldeDto) getListeHistorique().get(i);
			Agent ag = getAgentDao().chercherAgent(histo.getIdAgentModification());

			addZone(getNOM_ST_DATE(i),
					sdfDate.format(histo.getDateModifcation()) + "<br/>" + sdfHeure.format(histo.getDateModifcation()));
			addZone(getNOM_ST_PAR(i), ag.getNomAgent() + " " + ag.getPrenomAgent());
			addZone(getNOM_ST_MOTIF(i), histo.getMotif().getLibelle());
			addZone(getNOM_ST_OPERATION(i), histo.getTextModification());

		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setListeHistorique(null);
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE" + i;
	}

	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	public String getNOM_ST_PAR(int i) {
		return "NOM_ST_PAR" + i;
	}

	public String getVAL_ST_PAR(int i) {
		return getZone(getNOM_ST_PAR(i));
	}

	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	public String getNOM_ST_OPERATION(int i) {
		return "NOM_ST_OPERATION" + i;
	}

	public String getVAL_ST_OPERATION(int i) {
		return getZone(getNOM_ST_OPERATION(i));
	}

	public ArrayList<TypeAbsenceDto> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<TypeAbsenceDto>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<TypeAbsenceDto> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public ArrayList<HistoriqueSoldeDto> getListeHistorique() {
		return listeHistorique == null ? new ArrayList<HistoriqueSoldeDto>() : listeHistorique;
	}

	public void setListeHistorique(ArrayList<HistoriqueSoldeDto> listeHistorique) {
		this.listeHistorique = listeHistorique;
	}

	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	public ArrayList<String> getListeAnnee() {
		return listeAnnee;
	}

	public void setListeAnnee(ArrayList<String> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	public String getNOM_PB_ANNEE() {
		return "NOM_PB_ANNEE";
	}

	public boolean performPB_ANNEE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ANNEE_SELECT())) : -1);

		if (numAnnee < 0 || getListeAnnee().size() == 0 || numAnnee > getListeAnnee().size())
			return false;

		String annee = getListeAnnee().get(numAnnee);
		initialiseSoldesAgent(request, Integer.valueOf(annee));
		return true;
	}

	public ArrayList<SoldeMonthDto> getListeSoldeA55() {
		return listeSoldeA55 == null ? new ArrayList<SoldeMonthDto>() : listeSoldeA55;
	}

	public void setListeSoldeA55(ArrayList<SoldeMonthDto> listeSoldeA55) {
		this.listeSoldeA55 = listeSoldeA55;
	}

	public boolean isAgentReposComp() throws Exception {
		// si l'agent n'est pas contractuel ou convention collectives, alors il
		// n'a pas le droit au repos compensateur
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), getAgentCourant());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			// "ERR136", "Cet agent n'a aucune carrière active."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR136"));
			return false;
		}

		if (!(carr.getCodeCategorie().equals("4") || carr.getCodeCategorie().equals("7"))) {
			// "ERR802",
			// "Cet agent n'est ni contractuel ni convention collective, il ne peut avoir de repos compensateur."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR802"));
			return false;
		}

		return true;
	}

	public ArrayList<SoldeSpecifiqueDto> getListeSoldeCongesExcep() {
		return listeSoldeCongesExcep;
	}

	public void setListeSoldeCongesExcep(ArrayList<SoldeSpecifiqueDto> listeSoldeCongesExcep) {
		this.listeSoldeCongesExcep = listeSoldeCongesExcep;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
