package nc.mairie.gestionagent.process.agent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.ActeursDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.FiltreSoldeDto;
import nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto;
import nc.mairie.gestionagent.absence.dto.MoisAlimAutoCongesAnnuelsDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.RestitutionMassiveDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.absence.dto.SoldeSpecifiqueDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.RadiService;

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

	public String ACTION_VISUALISATION = "Consultation de l'historique d'alimentation manuelle du compteur de %s";

	private Agent agentCourant;
	private ArrayList<TypeAbsenceDto> listeTypeAbsence;
	private ArrayList<HistoriqueSoldeDto> listeHistorique;
	private ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimAuto;
	private ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimPaie;
	private ArrayList<RestitutionMassiveDto> listeHistoriqueRestitutionMassive;
	private ArrayList<DemandeDto> listeDemandeCA;

	private boolean afficheSoldeAsaA52;
	private boolean afficheSoldeAsaA48;
	private boolean afficheSoldeAsaA54;
	private boolean afficheSoldeAsaA55;
	private boolean afficheSoldeAsaAmicale;
	private ArrayList<SoldeSpecifiqueDto> listeSoldeCongesExcep;
	private OrganisationSyndicaleDto organisationAgent;
	private boolean agentReposComp;
	private ActeursDto acteursDto;

	private ArrayList<String> listeAnnee;
	private String[] LB_ANNEE;

	private DecimalFormat df = new DecimalFormat("0");

	private AgentDao agentDao;
	public String focus = null;

	private IAbsService absService;

	private IRadiService radiService;

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
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
		if (null == radiService) {
			radiService = (RadiService) context.getBean("radiService");
		}
	}

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
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

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
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
				initialiseActeursAbsenceAgent(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		// si l'agent n'est pas contractuel ou convention collectives, alors il
		// n'a pas le droit au repos compensateur
		Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), getAgentCourant());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			// "ERR136", "Cet agent n'a aucune carriere active."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR136"));
			return;
		}

		if (!(carr.getCodeCategorie().equals("4") || carr.getCodeCategorie().equals("7"))) {
			setAgentReposComp(false);
		} else {
			setAgentReposComp(true);
		}

	}

	private void initialiseListeDeroulante() {
		if (getListeTypeAbsence().size() == 0) {
			setListeTypeAbsence((ArrayList<TypeAbsenceDto>) absService.getListeRefTypeAbsenceDto(null));
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
		FiltreSoldeDto dto = new FiltreSoldeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateFin(dateFin);
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		// Solde depuis SIRH-ABS-WS
		SoldeDto soldeGlobal = absService.getSoldeAgent(getAgentCourant().getIdAgent(), json);

		// solde congés
		addZone(getNOM_ST_SOLDE_CONGE(), soldeGlobal.getSoldeCongeAnnee() == 0 ? "&nbsp;" : soldeGlobal.getSoldeCongeAnnee().toString() + " j");
		addZone(getNOM_ST_SOLDE_CONGE_PREC(), soldeGlobal.getSoldeCongeAnneePrec() == 0 ? "&nbsp;" : soldeGlobal.getSoldeCongeAnneePrec().toString() + " j");
		addZone(getNOM_ST_SAMEDI_OFFERT_SOLDE_CONGE(), soldeGlobal.isSamediOffert() ? "pris" : "non pris");

		// Solde recup
		int soldeRecup = soldeGlobal.getSoldeRecup().intValue();
		String soldeRecupHeure = (soldeRecup / 60) == 0 ? Const.CHAINE_VIDE : soldeRecup / 60 + "h ";
		String soldeRecupMinute = (soldeRecup % 60) == 0 ? "&nbsp;" : soldeRecup % 60 + "m";
		addZone(getNOM_ST_SOLDE_RECUP(), soldeRecupHeure + soldeRecupMinute);

		// Solde repos comp
		int soldeReposComp = soldeGlobal.getSoldeReposCompAnnee().intValue();
		String soldeReposCompHeure = (soldeReposComp / 60) == 0 ? Const.CHAINE_VIDE : soldeReposComp / 60 + "h ";
		String soldeReposCompMinute = (soldeReposComp % 60) == 0 ? "&nbsp;" : soldeReposComp % 60 + "m";
		addZone(getNOM_ST_SOLDE_REPOS_COMP(), soldeReposCompHeure + soldeReposCompMinute);

		int soldeReposCompPrec = soldeGlobal.getSoldeReposCompAnneePrec().intValue();
		String soldeReposCompPrecHeure = (soldeReposCompPrec / 60) == 0 ? Const.CHAINE_VIDE : soldeReposCompPrec / 60 + "h ";
		String soldeReposCompPrecMinute = (soldeReposCompPrec % 60) == 0 ? "&nbsp;" : soldeReposCompPrec % 60 + "m";
		addZone(getNOM_ST_SOLDE_REPOS_COMP_PREC(), soldeReposCompPrecHeure + soldeReposCompPrecMinute);

		// Solde ASA A48
		setAfficheSoldeAsaA48(soldeGlobal.isAfficheSoldeAsaA48());
		addZone(getNOM_ST_SOLDE_ASA_A48(), soldeGlobal.getSoldeAsaA48() == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaA48().toString() + " j");

		// Solde ASA A54
		setAfficheSoldeAsaA54(soldeGlobal.isAfficheSoldeAsaA54());
		addZone(getNOM_ST_SOLDE_ASA_A54(), soldeGlobal.getSoldeAsaA54() == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaA54().toString() + " j");

		// Solde ASA A55
		setAfficheSoldeAsaA55(soldeGlobal.isAfficheSoldeAsaA55());
		if (soldeGlobal != null) {
			String soldeAsaA55Heure = (soldeGlobal.getSoldeAsaA55().intValue() / 60) == 0 ? Const.CHAINE_VIDE : soldeGlobal.getSoldeAsaA55().intValue() / 60 + "h ";
			String soldeAsaA55Minute = (soldeGlobal.getSoldeAsaA55().intValue() % 60) == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaA55().intValue() % 60 + "m";
			addZone(getNOM_ST_SOLDE_ASA_A55(), soldeAsaA55Heure + soldeAsaA55Minute);
		}

		// Solde ASA AMICALE
		setAfficheSoldeAsaAmicale(soldeGlobal.isAfficheSoldeAsaAmicale());
		if (soldeGlobal != null) {
			String soldeAsaAmicaleHeure = (soldeGlobal.getSoldeAsaAmicale().intValue() / 60) == 0 ? Const.CHAINE_VIDE : soldeGlobal.getSoldeAsaAmicale().intValue() / 60 + "h ";
			String soldeAsaAmicaleMinute = (soldeGlobal.getSoldeAsaAmicale().intValue() % 60) == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaAmicale().intValue() % 60 + "m";
			addZone(getNOM_ST_SOLDE_ASA_AMICALE(), soldeAsaAmicaleHeure + soldeAsaAmicaleMinute);
		}

		// Solde ASA A52
		setAfficheSoldeAsaA52(soldeGlobal.isAfficheSoldeAsaA52());
		if (soldeGlobal != null) {
			setOrganisationAgent(soldeGlobal.getOrganisationA52());

			String soldeAsaA52Heure = (soldeGlobal.getSoldeAsaA52().intValue() / 60) == 0 ? Const.CHAINE_VIDE : soldeGlobal.getSoldeAsaA52().intValue() / 60 + "h ";
			String soldeAsaA52Minute = (soldeGlobal.getSoldeAsaA52().intValue() % 60) == 0 ? "&nbsp;" : soldeGlobal.getSoldeAsaA52().intValue() % 60 + "m";
			addZone(getNOM_ST_SOLDE_ASA_A52(), soldeAsaA52Heure + soldeAsaA52Minute);
		}

		df.setRoundingMode(RoundingMode.DOWN);
		setListeSoldeCongesExcep((ArrayList<SoldeSpecifiqueDto>) soldeGlobal.getListeSoldeCongesExcep());
		for (int i = 0; i < getListeSoldeCongesExcep().size(); i++) {
			SoldeSpecifiqueDto soldeSpecifiqueDto = getListeSoldeCongesExcep().get(i);
			if ("minutes".equals(soldeSpecifiqueDto.getUniteDecompte())) {
				String soldeCongesExcepHeure = "0".equals(df.format(soldeSpecifiqueDto.getSolde().intValue() / 60)) ? Const.CHAINE_VIDE : df.format(soldeSpecifiqueDto.getSolde().intValue() / 60) + "h ";
				String soldeCongesExcepMinute = "0".equals(df.format(soldeSpecifiqueDto.getSolde().intValue() % 60)) ? "&nbsp;" : df.format(soldeSpecifiqueDto.getSolde().intValue() % 60) + "m";
				addZone(getNOM_ST_SOLDE_CONGES_EXCEP(i), soldeCongesExcepHeure + soldeCongesExcepMinute);
			}
			if ("jours".equals(soldeSpecifiqueDto.getUniteDecompte())) {
				String soldeCongesExcepHeure = soldeSpecifiqueDto.getSolde() + " j";
				addZone(getNOM_ST_SOLDE_CONGES_EXCEP(i), soldeCongesExcepHeure);
			}

			addZone(getNOM_ST_TYPE_CONGES_EXCEP(i), soldeSpecifiqueDto.getLibelle());
		}
	}

	private void initialiseActeursAbsenceAgent(HttpServletRequest request) {
		ActeursDto acteursDto = absService.getListeActeurs(getAgentCourant().getIdAgent());

		setActeursDto(acteursDto);
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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

	public String getNOM_ST_SOLDE_ASA_A55() {
		return "NOM_ST_SOLDE_ASA_A55";
	}

	public String getVAL_ST_SOLDE_ASA_A55() {
		return getZone(getNOM_ST_SOLDE_ASA_A55());
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

		SimpleDateFormat sdfDateEtHeure = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		SimpleDateFormat sdfDateMonth = new SimpleDateFormat("MM/yyyy");

		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ANNEE_SELECT())) : -1);

		if (numAnnee < 0 || getListeAnnee().size() == 0 || numAnnee > getListeAnnee().size())
			return false;

		String annee = getListeAnnee().get(numAnnee);

		Date dateDeb = new DateTime(Integer.valueOf(annee), 1, 1, 0, 0, 0).toDate();
		Date dateFin = new DateTime(Integer.valueOf(annee), 12, 31, 23, 59, 59).toDate();
		FiltreSoldeDto dto = new FiltreSoldeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateFin(dateFin);
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		// Liste depuis SIRH-ABS-WS
		ArrayList<HistoriqueSoldeDto> listeHistorique = (ArrayList<HistoriqueSoldeDto>) absService.getHistoriqueCompteurAgent(getAgentCourant().getIdAgent(), codeTypeAbsence, json);
		setListeHistorique(listeHistorique);

		for (int i = 0; i < getListeHistorique().size(); i++) {

			HistoriqueSoldeDto histo = (HistoriqueSoldeDto) getListeHistorique().get(i);
			if(null != histo.getIdAgentModification()) {
				Agent ag = getAgentDao().chercherAgent(histo.getIdAgentModification());
	
				addZone(getNOM_ST_DATE(i), sdfDate.format(histo.getDateModifcation()) + "<br/>" + sdfHeure.format(histo.getDateModifcation()));
				addZone(getNOM_ST_PAR(i), ag.getNomAgent() + " " + ag.getPrenomAgent());
				addZone(getNOM_ST_MOTIF(i), histo.getMotif() == null ? Const.CHAINE_VIDE : histo.getMotif().getLibelle());
				addZone(getNOM_ST_OPERATION(i), histo.getTextModification());
			}
		}

		setListeHistoriqueAlimAuto(null);
		setListeHistoriqueAlimPaie(null);
		setListeDemandeCA(null);
		if (EnumTypeAbsence.getRefTypeAbsenceEnum(codeTypeAbsence) == EnumTypeAbsence.CONGE) {
			// #15146 : dans les congés annuels, on charge aussi l'historique
			// des
			// alim auto de fin de mois
			// Liste depuis SIRH-ABS-WS
			ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimAuto = (ArrayList<MoisAlimAutoCongesAnnuelsDto>) absService
					.getHistoriqueAlimAutoCongeAnnuelAgent(getAgentCourant().getIdAgent());
			setListeHistoriqueAlimAuto(listeHistoriqueAlimAuto);

			// evol #30837
			List<DemandeDto> listDemandeCA = absService.getListeDemandeCAWhichAddOrRemoveOnCounterAgent(getAgentConnecte(request).getIdAgent(), getAgentCourant().getIdAgent());
			setListeDemandeCA((ArrayList<DemandeDto>) listDemandeCA);
			
			for (int i = 0; i < getListeHistoriqueAlimAuto().size(); i++) {
				MoisAlimAutoCongesAnnuelsDto histo = (MoisAlimAutoCongesAnnuelsDto) getListeHistoriqueAlimAuto().get(i);
				// evol #30837
				addZone(getNOM_ST_MOIS(i), histo.getDateMois() == null ? Const.CHAINE_VIDE : sdfDateMonth.format(histo.getDateMois()));
				addZone(getNOM_ST_DATE_MODIF_HISTO_ALIM_AUTO(i), 
						histo.getDateModification() == null 
							? Const.CHAINE_VIDE : 
									sdfDate.format(histo.getDateModification())
										+ "<br/>" + sdfHeure.format(histo.getDateModification()));
				addZone(getNOM_ST_NB_JOUR(i), histo.getNbJours().toString());
				addZone(getNOM_ST_COMMENTAIRE(i), histo.getStatus());
			}

			// #15599 : dans les congés annuels, on charge aussi l'historique
			// des restitutions massives de CA
			// Liste depuis SIRH-ABS-WS
			ArrayList<RestitutionMassiveDto> listeHistoriquerestitutionMassive = (ArrayList<RestitutionMassiveDto>) absService.getHistoRestitutionMassiveByIdAgent(getAgentCourant().getIdAgent());
			setListeHistoriqueRestitutionMassive(listeHistoriquerestitutionMassive);

			for (int i = 0; i < getListeHistoriqueRestitutionMassive().size(); i++) {
				RestitutionMassiveDto histo = (RestitutionMassiveDto) getListeHistoriqueRestitutionMassive().get(i);

				addZone(getNOM_ST_RESTITUTION_JOURS(i), sdfDate.format(histo.getDateRestitution()));
				addZone(getNOM_ST_RESTITUTION_MOTIF(i), histo.getMotif());
				addZone(getNOM_ST_RESTITUTION_NB_JOUR(i), histo.getListHistoAgents().get(0).getJours().toString());
			}
		} else if (EnumTypeAbsence.getRefTypeAbsenceEnum(codeTypeAbsence) == EnumTypeAbsence.RECUP) {
			// #15479 : dans les recups/repos comp, on charge aussi l'historique
			// des alim auto de la paie
			// Liste depuis SIRH-ABS-WS
			ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimAuto = (ArrayList<MoisAlimAutoCongesAnnuelsDto>) absService.getHistoriqueAlimAutoRecupAgent(getAgentCourant().getIdAgent());
			setListeHistoriqueAlimPaie(listeHistoriqueAlimAuto);

			for (int i = 0; i < getListeHistoriqueAlimPaie().size(); i++) {
				MoisAlimAutoCongesAnnuelsDto histo = (MoisAlimAutoCongesAnnuelsDto) getListeHistoriqueAlimPaie().get(i);

				addZone(getNOM_ST_MOIS(i), histo.getDateMois() == null ? Const.CHAINE_VIDE : sdfDate.format(histo.getDateMois()));

				int soldeRecup = histo.getNbJours().intValue();
				String soldeRecupHeure = (soldeRecup / 60) == 0 ? Const.CHAINE_VIDE : soldeRecup / 60 + "h ";
				String soldeRecupMinute = (soldeRecup % 60) == 0 ? Const.CHAINE_VIDE : soldeRecup % 60 + "m";
				addZone(getNOM_ST_NB_JOUR(i), (soldeRecupHeure + soldeRecupMinute).equals(Const.CHAINE_VIDE) ? "0" : (soldeRecupHeure + soldeRecupMinute));
				addZone(getNOM_ST_COMMENTAIRE(i), histo.getStatus());
				addZone(getNOM_ST_DATE_MODIF(i), sdfDateEtHeure.format(histo.getDateModification()));
			}
			
		} else if (EnumTypeAbsence.getRefTypeAbsenceEnum(codeTypeAbsence) == EnumTypeAbsence.REPOS_COMP) {
			// #15479 : dans les recups/repos comp, on charge aussi l'historique
			// des alim auto de la paie
			// Liste depuis SIRH-ABS-WS
			ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimAuto = (ArrayList<MoisAlimAutoCongesAnnuelsDto>) absService.getHistoriqueAlimAutoReposCompAgent(getAgentCourant().getIdAgent());
			setListeHistoriqueAlimPaie(listeHistoriqueAlimAuto);

			for (int i = 0; i < getListeHistoriqueAlimPaie().size(); i++) {
				MoisAlimAutoCongesAnnuelsDto histo = (MoisAlimAutoCongesAnnuelsDto) getListeHistoriqueAlimPaie().get(i);

				addZone(getNOM_ST_MOIS(i), histo.getDateMois() == null ? Const.CHAINE_VIDE : sdfDate.format(histo.getDateMois()));

				int soldeReposComp = histo.getNbJours().intValue();
				String soldeReposCompHeure = (soldeReposComp / 60) == 0 ? Const.CHAINE_VIDE : soldeReposComp / 60 + "h ";
				String soldeReposCompMinute = (soldeReposComp % 60) == 0 ? Const.CHAINE_VIDE : soldeReposComp % 60 + "m";
				addZone(getNOM_ST_NB_JOUR(i), (soldeReposCompHeure + soldeReposCompMinute).equals(Const.CHAINE_VIDE) ? "0" : (soldeReposCompHeure + soldeReposCompMinute));
				addZone(getNOM_ST_COMMENTAIRE(i), histo.getStatus());
			}
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), String.format(ACTION_VISUALISATION, EnumTypeAbsence.getRefTypeAbsenceEnum(codeTypeAbsence).getValue()));
		setFocus(getNOM_PB_ANNULER());

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
		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ANNEE_SELECT())) : -1);

		if (numAnnee < 0 || getListeAnnee().size() == 0 || numAnnee > getListeAnnee().size())
			return false;

		String annee = getListeAnnee().get(numAnnee);
		initialiseSoldesAgent(request, Integer.valueOf(annee));
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

	public String getNOM_ST_SOLDE_ASA_A52() {
		return "NOM_ST_SOLDE_ASA_A52";
	}

	public String getVAL_ST_SOLDE_ASA_A52() {
		return getZone(getNOM_ST_SOLDE_ASA_A52());
	}

	public boolean isAfficheSoldeAsaA52() {
		return afficheSoldeAsaA52;
	}

	public void setAfficheSoldeAsaA52(boolean afficheSoldeAsaA52) {
		this.afficheSoldeAsaA52 = afficheSoldeAsaA52;
	}

	public OrganisationSyndicaleDto getOrganisationAgent() {
		return organisationAgent;
	}

	public void setOrganisationAgent(OrganisationSyndicaleDto organisationAgent) {
		this.organisationAgent = organisationAgent;
	}

	public void setAgentReposComp(boolean agentReposComp) {
		this.agentReposComp = agentReposComp;
	}

	public boolean isAgentReposComp() {
		return agentReposComp;
	}

	public String getNOM_ST_SAMEDI_OFFERT_SOLDE_CONGE() {
		return "NOM_ST_SAMEDI_OFFERT_SOLDE_CONGE";
	}

	public String getVAL_ST_SAMEDI_OFFERT_SOLDE_CONGE() {
		return getZone(getNOM_ST_SAMEDI_OFFERT_SOLDE_CONGE());
	}

	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_PB_ANNULER();
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public boolean isAfficheSoldeAsaA48() {
		return afficheSoldeAsaA48;
	}

	public void setAfficheSoldeAsaA48(boolean afficheSoldeAsaA48) {
		this.afficheSoldeAsaA48 = afficheSoldeAsaA48;
	}

	public boolean isAfficheSoldeAsaA54() {
		return afficheSoldeAsaA54;
	}

	public void setAfficheSoldeAsaA54(boolean afficheSoldeAsaA54) {
		this.afficheSoldeAsaA54 = afficheSoldeAsaA54;
	}

	public boolean isAfficheSoldeAsaA55() {
		return afficheSoldeAsaA55;
	}

	public void setAfficheSoldeAsaA55(boolean afficheSoldeAsaA55) {
		this.afficheSoldeAsaA55 = afficheSoldeAsaA55;
	}

	public ActeursDto getActeursDto() {
		return acteursDto;
	}

	public void setActeursDto(ActeursDto acteursDto) {
		this.acteursDto = acteursDto;
	}

	public ArrayList<MoisAlimAutoCongesAnnuelsDto> getListeHistoriqueAlimAuto() {
		return listeHistoriqueAlimAuto == null ? new ArrayList<MoisAlimAutoCongesAnnuelsDto>() : listeHistoriqueAlimAuto;
	}

	public void setListeHistoriqueAlimAuto(ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimAuto) {
		this.listeHistoriqueAlimAuto = listeHistoriqueAlimAuto;
	}

	public String getNOM_ST_MOIS(int i) {
		return "NOM_ST_MOIS" + i;
	}

	public String getVAL_ST_MOIS(int i) {
		return getZone(getNOM_ST_MOIS(i));
	}
	
	public String getNOM_ST_DATE_MODIF(int i) {
		return "NOM_ST_DATE_MODIF" + i;
	}

	public String getVAL_ST_DATE_MODIF(int i) {
		return getZone(getNOM_ST_DATE_MODIF(i));
	}

	public String getNOM_ST_DATE_MODIF_HISTO_ALIM_AUTO(int i) {
		return "NOM_ST_DATE_MODIF_HISTO_ALIM_AUTO" + i;
	}

	public String getVAL_ST_DATE_MODIF_HISTO_ALIM_AUTO(int i) {
		return getZone(getNOM_ST_DATE_MODIF_HISTO_ALIM_AUTO(i));
	}

	public String getNOM_ST_NB_JOUR(int i) {
		return "NOM_ST_NB_JOUR" + i;
	}

	public String getVAL_ST_NB_JOUR(int i) {
		return getZone(getNOM_ST_NB_JOUR(i));
	}

	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	public String getNOM_ST_RESTITUTION_JOURS(int i) {
		return "NOM_ST_RESTITUTION_JOURS" + i;
	}

	public String getVAL_ST_RESTITUTION_JOURS(int i) {
		return getZone(getNOM_ST_RESTITUTION_JOURS(i));
	}

	public String getNOM_ST_RESTITUTION_MOTIF(int i) {
		return "NOM_ST_RESTITUTION_MOTIF" + i;
	}

	public String getVAL_ST_RESTITUTION_MOTIF(int i) {
		return getZone(getNOM_ST_RESTITUTION_MOTIF(i));
	}

	public String getNOM_ST_RESTITUTION_NB_JOUR(int i) {
		return "NOM_ST_RESTITUTION_NB_JOUR" + i;
	}

	public String getVAL_ST_RESTITUTION_NB_JOUR(int i) {
		return getZone(getNOM_ST_RESTITUTION_NB_JOUR(i));
	}

	public ArrayList<MoisAlimAutoCongesAnnuelsDto> getListeHistoriqueAlimPaie() {
		return listeHistoriqueAlimPaie == null ? new ArrayList<MoisAlimAutoCongesAnnuelsDto>() : listeHistoriqueAlimPaie;
	}

	public void setListeHistoriqueAlimPaie(ArrayList<MoisAlimAutoCongesAnnuelsDto> listeHistoriqueAlimPaie) {
		this.listeHistoriqueAlimPaie = listeHistoriqueAlimPaie;
	}

	public ArrayList<RestitutionMassiveDto> getListeHistoriqueRestitutionMassive() {
		return listeHistoriqueRestitutionMassive == null ? new ArrayList<RestitutionMassiveDto>() : listeHistoriqueRestitutionMassive;
	}

	public void setListeHistoriqueRestitutionMassive(ArrayList<RestitutionMassiveDto> listeHistoriqueRestitutionMassive) {
		this.listeHistoriqueRestitutionMassive = listeHistoriqueRestitutionMassive;
	}

	public String getNOM_ST_SOLDE_ASA_AMICALE() {
		return "NOM_ST_SOLDE_ASA_AMICALE";
	}

	public String getVAL_ST_SOLDE_ASA_AMICALE() {
		return getZone(getNOM_ST_SOLDE_ASA_AMICALE());
	}

	public boolean isAfficheSoldeAsaAmicale() {
		return afficheSoldeAsaAmicale;
	}

	public void setAfficheSoldeAsaAmicale(boolean afficheSoldeAsaAmicale) {
		this.afficheSoldeAsaAmicale = afficheSoldeAsaAmicale;
	}

	public ArrayList<DemandeDto> getListeDemandeCA() {
		return listeDemandeCA;
	}

	public void setListeDemandeCA(ArrayList<DemandeDto> listeDemandeCA) {
		this.listeDemandeCA = listeDemandeCA;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

}
