package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.carriere.HistoCarriereDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
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
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IAvancementService;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctCarrieres extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private String[] LB_ANNEE;
	private String[] LB_FILIERE;

	private String[] listeAnnee;
	private String anneeSelect;
	private ArrayList<FiliereGrade> listeFiliere;

	private ArrayList<AvancementFonctionnaires> listeAvct;

	public String agentEnErreur = Const.CHAINE_VIDE;

	private AutreAdministrationDao autreAdministrationDao;
	private AvisCapDao avisCapDao;
	private AvancementFonctionnairesDao avancementFonctionnairesDao;
	private HistoCarriereDao histoCarriereDao;
	private AgentDao agentDao;

	private IAdsService adsService;
	private IAvancementService avancementService;

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
		}

		// Si liste avancements vide alors initialisation.
		if (getListeAvct().size() == 0) {
			agentEnErreur = Const.CHAINE_VIDE;
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getAutreAdministrationDao() == null) {
			setAutreAdministrationDao(new AutreAdministrationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvisCapDao() == null) {
			setAvisCapDao(new AvisCapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementFonctionnairesDao() == null) {
			setAvancementFonctionnairesDao(new AvancementFonctionnairesDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoCarriereDao() == null) {
			setHistoCarriereDao(new HistoCarriereDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == avancementService) {
			avancementService = (IAvancementService) context.getBean("avancementService");
		}
	}

	/**
	 * Initialisation des liste deroulantes de l'écran Avancement des
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

		// Si liste medecins vide alors affectation
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
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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

			// Si clic sur le bouton PB_MAJ_DATE_AVCT
			if (testerParametre(request, getNOM_PB_MAJ_DATE_AVCT())) {
				return performPB_MAJ_DATE_AVCT(request);
			}

			// Si clic sur le bouton PB_AFFECTER
			if (testerParametre(request, getNOM_PB_AFFECTER())) {
				return performPB_AFFECTER(request);
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

			// Si clic sur le bouton PB_RAFRAICHIR
			for (int i = 0; i < getListeAvct().size(); i++) {
				AvancementFonctionnaires avct = getListeAvct().get(i);
				if (testerParametre(request, getNOM_PB_RAFRAICHIR(avct.getIdAvct()))) {
					return performPB_RAFRAICHIR(request, avct.getIdAvct());
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
	public OeAVCTFonctCarrieres() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctCarrieres.jsp";
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		setAnneeSelect(annee);

		// Recuperation filiere
		FiliereGrade filiere = null;
		int indiceFiliere = (Services.estNumerique(getVAL_LB_FILIERE_SELECT()) ? Integer.parseInt(getVAL_LB_FILIERE_SELECT()) : -1);
		if (indiceFiliere > 0) {
			filiere = (FiliereGrade) getListeFiliere().get(indiceFiliere - 1);
		}

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		// recuperation du service
		List<String> listeSousService = null;
		if (getVAL_ST_ID_SERVICE_ADS().length() != 0) {
			// on recupere les sous-service du service selectionne
			listeSousService = adsService.getListSiglesWithEnfantsOfEntite(new Integer(getVAL_ST_ID_SERVICE_ADS()));
		}

		String reqEtat = " and (ETAT='" + EnumEtatAvancement.ARRETE.getValue() + "' ) and (ID_AVIS_EMP!= 5  or ID_AVIS_EMP is null ) ";
		setListeAvct(getAvancementFonctionnairesDao().listerAvancementAvecAnneeEtat(Integer.valueOf(annee), reqEtat, filiere == null ? null : filiere.getLibFiliere(),
				agent == null ? null : agent.getIdAgent(), listeSousService, null, null,"non"));

		afficheListeAvancement();
		return true;
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
			addZone(getNOM_ST_DIRECTION(i), Services.estNumerique(av.getDirectionService()) ? getAutreAdministrationDao().chercherAutreAdministration(Integer.valueOf(av.getDirectionService()))
					.getLibAutreAdmin() : av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i), (av.getCdcadr() == null ? "&nbsp;" : av.getCdcadr()) + " <br> " + av.getFiliere());
			PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
			addZone(getNOM_ST_PA(i), pa.getLiPAdm());
			addZone(getNOM_ST_DATE_CAP(i), sdfFormatDate.format(av.getDateCap()));
			addZone(getNOM_ST_GRADE_ANCIEN(i), av.getGrade());
			addZone(getNOM_ST_GRADE_NOUVEAU(i), (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct().toString());

			if (av.getIdAvisEmp() != null) {
				String idAvisEmp = getAvisCapDao().chercherAvisCap(av.getIdAvisEmp()).getLibCourtAvisCap().toUpperCase();
				Date dateAvctFinale = null;
				if (idAvisEmp.equals("MIN")) {
					dateAvctFinale = av.getDateAvctMini();
				} else if (idAvisEmp.equals("MOY")) {
					dateAvctFinale = av.getDateAvctMoy();
				} else if (idAvisEmp.equals("MAX")) {
					dateAvctFinale = av.getDateAvctMaxi();
				} else if (idAvisEmp.equals("FAV")) {
					dateAvctFinale = av.getDateAvctMoy();
				}
				addZone(getNOM_ST_DATE_AVCT(i), dateAvctFinale == null ? Const.CHAINE_VIDE : sdfFormatDate.format(dateAvctFinale));
			} else {
				if (av.getIdMotifAvct() == 3) {
					addZone(getNOM_ST_DATE_AVCT(i), sdfFormatDate.format(av.getDateAvctMoy()));
				} else {
					addZone(getNOM_ST_DATE_AVCT(i), "&nbsp;");
				}
			}

			addZone(getNOM_ST_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_ST_DATE_ARRETE(i), av.getDateArrete() == null ? Const.CHAINE_VIDE : sdfFormatDate.format(av.getDateArrete()));
			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null || av.getCarriereSimu().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : "oui");
			addZone(getNOM_CK_MAJ_DATE_AVCT(i), getCHECKED_OFF());

		}
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// format date de debut
		if (!getVAL_ST_DATE_ARR_GLOBALE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_ARR_GLOBALE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'arrêté"));
			return false;
		}
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concerné
					Agent agent = getAgentDao().chercherAgent(avct.getIdAgent());
					// on recupere la derniere carriere dans l'année
					Carriere carr = Carriere.chercherDerniereCarriereAvecAgentEtAnnee(getTransaction(), agent.getNomatr(), avct.getAnnee().toString());
					// on check il y a une carriere deja saisie
					if (!avancementService.isCarriereFonctionnaireSimu(carr)) {
						// pourla date d'avancement
						Date dateAvctFinale = null;
						String idAvisEmp = null;
						if (avct.getIdAvisEmp() != null) {
							idAvisEmp = getAvisCapDao().chercherAvisCap(avct.getIdAvisEmp()).getLibCourtAvisCap().toUpperCase();
							if (idAvisEmp.equals("MIN")) {
								dateAvctFinale = avct.getDateAvctMini();
							} else if (idAvisEmp.equals("MOY")) {
								dateAvctFinale = avct.getDateAvctMoy();
							} else if (idAvisEmp.equals("MAX")) {
								dateAvctFinale = avct.getDateAvctMaxi();
							} else if (idAvisEmp.equals("FAV")) {
								dateAvctFinale = avct.getDateAvctMoy();
							} else {
								agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
								continue;
							}
						} else {
							if (avct.getIdMotifAvct() == 3) {
								dateAvctFinale = avct.getDateAvctMoy();
								idAvisEmp = "MOY";
							} else {
								agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
								continue;
							}
						}

						// il faut faire attention qu'il n'y a pas de carriere
						// de simu deja en cours
						@SuppressWarnings("unused")
						Carriere carrSimu = Carriere.chercherCarriereSuperieurOuEgaleDate(getTransaction(), agent,
								Services.convertitDate(sdfFormatDate.format(dateAvctFinale), "dd/MM/yyyy", "yyyyMMdd"));
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
							// on met un 'S' dans son avancement
							avct.setCarriereSimu("S");
							getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(),
									avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(),
									avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
									avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(),
									avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(),
									avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
									avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
									avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(), avct.getUserVerifArrImpr(),
									avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());
							continue;
						}

						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());
						// on traite le numero et la date d'arrete
						String dateArr = getVAL_ST_DATE_ARR_GLOBALE();
						avct.setDateArrete(dateArr.equals(Const.CHAINE_VIDE) ? null : sdfFormatDate.parse(dateArr));
						avct.setNumArrete(getVAL_ST_NUM_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NUM_ARRETE(i));

						// on recalcul la date d'avancement
						if (avct.getIdAvisEmp() != null) {
							idAvisEmp = getAvisCapDao().chercherAvisCap(avct.getIdAvisEmp()).getLibCourtAvisCap().toUpperCase();
							if (idAvisEmp.equals("MIN")) {
								dateAvctFinale = avct.getDateAvctMini();
							} else if (idAvisEmp.equals("MOY")) {
								dateAvctFinale = avct.getDateAvctMoy();
							} else if (idAvisEmp.equals("MAX")) {
								dateAvctFinale = avct.getDateAvctMaxi();
							} else if (idAvisEmp.equals("FAV")) {
								dateAvctFinale = avct.getDateAvctMoy();
							} else {
								agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
								continue;
							}
						} else {
							if (avct.getIdMotifAvct() == 3) {
								dateAvctFinale = avct.getDateAvctMoy();
								idAvisEmp = "MOY";
							} else {
								agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
								continue;
							}
						}
						// on crée un nouvelle carriere
						Carriere nouvelleCarriere = avancementService.getNewCarriereFonctionnaire(getTransaction(), agent, avct, carr, getAvancementFonctionnairesDao(), idAvisEmp, dateAvctFinale);

						// on ferme cette carriere
						carr.setDateFin(dateAvctFinale == null ? Const.ZERO : sdfFormatDate.format(dateAvctFinale));
						// RG_AG_CA_A03
						HistoCarriere histo = new HistoCarriere(carr);
						getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
						carr.modifierCarriere(getTransaction(), agent, user);

						HistoCarriere histo2 = new HistoCarriere(nouvelleCarriere);
						getHistoCarriereDao().creerHistoCarriere(histo2, user, EnumTypeHisto.CREATION);
						nouvelleCarriere.creerCarriere(getTransaction(), agent, user);

						// on enregistre

						if (getTransaction().isErreur()) {
							return false;
						} else {
							nbAgentAffectes += 1;
						}
					} else {
						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste a l'ecran
						agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
						// on met un 'S' dans son avancement
						avct.setCarriereSimu("S");
						getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(),
								avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(),
								avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(),
								avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
								avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(),
								avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(),
								avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(),
								avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
								avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());

					}
				}
			}
		}

		// on valide les modifis
		commitTransaction();
		afficheListeAvancement();

		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// format date de debut
		if (!getVAL_ST_DATE_ARR_GLOBALE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_ARR_GLOBALE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'arrêté"));
			return false;
		}
		// on sauvegarde l'etat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.ARRETE.getValue());
				}
				// on traite le numero et la date d'arrete
				String dateArr = getVAL_ST_DATE_ARR_GLOBALE();
				avct.setDateArrete(dateArr.equals(Const.CHAINE_VIDE) ? null : sdfFormatDate.parse(dateArr));
				avct.setNumArrete(getVAL_ST_NUM_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NUM_ARRETE(i));

			}
			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
					avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(),
					avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(),
					avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
					avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(),
					avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(),
					avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
					avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
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
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_DATE_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_ARRETE(int i) {
		return "NOM_ST_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_DATE_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */

	public String getVAL_ST_DATE_ARRETE(int i) {
		return getZone(getNOM_ST_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ST_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_ARRETE(int i) {
		return "NOM_ST_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_NUM_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_ARRETE(int i) {
		return getZone(getNOM_ST_NUM_ARRETE(i));
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
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_AFFECTER(int i) {
		return "NOM_CK_AFFECTER_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
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
		return "ECR-AVCT-FONCT-CARRIERES";
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
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
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
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_VALID_CARRIERE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_VALID_CARRIERE(int i) {
		return "NOM_ST_USER_VALID_CARRIERE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_USER_VALID_CARRIERE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_CARRIERE(int i) {
		return getZone(getNOM_ST_USER_VALID_CARRIERE(i));
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

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

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
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
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
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
	 * Retourne le nom d'un bouton pour la JSP : PB_RAFRAICHIR Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_RAFRAICHIR(int i) {
		return "NOM_PB_RAFRAICHIR_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_RAFRAICHIR(HttpServletRequest request, Integer idAvct) throws Exception {
		AvancementFonctionnaires avct = getAvancementFonctionnairesDao().chercherAvancement(idAvct);
		Agent agent = getAgentDao().chercherAgent(avct.getIdAgent());
		Carriere carrEnCours = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);

		// on regarde si l'agent a une carriere de simulation deja
		// saisie
		// autrement dis si la carriere actuelle a pour datfin 0
		if (carrEnCours.getDateFin() == null || carrEnCours.getDateFin().equals(Const.ZERO)) {
			avct.setCarriereSimu(null);
		} else {
			avct.setCarriereSimu("S");
		}
		getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
				avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(),
				avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(),
				avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
				avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(),
				avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(),
				avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
				avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());
		commitTransaction();

		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ARR Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_ARR_GLOBALE() {
		return "NOM_ST_DATE_ARR_GLOBALE_";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ARR Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_ARR_GLOBALE() {
		return getZone(getNOM_ST_DATE_ARR_GLOBALE());
	}

	public String getNOM_CK_MAJ_DATE_AVCT(int i) {
		return "NOM_CK_MAJ_DATE_AVCT_" + i;
	}

	public String getVAL_CK_MAJ_DATE_AVCT(int i) {
		return getZone(getNOM_CK_MAJ_DATE_AVCT(i));
	}

	public String getNOM_PB_MAJ_DATE_AVCT() {
		return "NOM_PB_MAJ_DATE_AVCT";
	}

	public boolean performPB_MAJ_DATE_AVCT(HttpServletRequest request) throws Exception {
		// on recupere les lignes qui sont cochées pour date carriere
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_MAJ_DATE_AVCT(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concerné
					try {
						Agent agent = getAgentDao().chercherAgent(avct.getIdAgent());
						// on recupere la carriere en cours
						Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
						if (getTransaction().isErreur()) {
							return false;
						}

						// si la carriere est bien la derniere de la liste
						if (!avancementService.isCarriereFonctionnaireSimu(carr)) {
							// pourla date d'avancement
							Date dateAvctFinale = null;
							String idAvisEmp = null;
							if (avct.getIdAvisEmp() != null) {
								try {
									idAvisEmp = getAvisCapDao().chercherAvisCap(avct.getIdAvisEmp()).getLibCourtAvisCap().toUpperCase();
								} catch (Exception e) {
									return false;
								}
								if (idAvisEmp.equals("MIN")) {
									dateAvctFinale = avct.getDateAvctMini();
								} else if (idAvisEmp.equals("MOY")) {
									dateAvctFinale = avct.getDateAvctMoy();
								} else if (idAvisEmp.equals("MAX")) {
									dateAvctFinale = avct.getDateAvctMaxi();
								} else if (idAvisEmp.equals("FAV")) {
									dateAvctFinale = avct.getDateAvctMoy();
								} else {
									agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
									continue;
								}
							} else {
								if (avct.getIdMotifAvct() == 3) {
									dateAvctFinale = avct.getDateAvctMoy();
									idAvisEmp = "MOY";
								} else {
									agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
									continue;
								}
							}

							// il faut faire attention qu'il n'y a pas de
							// carriere
							// de simu deja en cours
							@SuppressWarnings("unused")
							Carriere carrSimu = Carriere.chercherCarriereSuperieurOuEgaleDate(getTransaction(), agent,
									Services.convertitDate(sdfFormatDate.format(dateAvctFinale), "dd/MM/yyyy", "yyyyMMdd"));
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							} else {
								agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
								continue;
							}

							// on calcul Grade - ACC/BM en fonction de l'avis
							// CAP
							// il est different du resultat affiché dans le
							// tableau
							// si AVIS_CAP != MOY
							// car pour la simulation on prenait comme ref de
							// calcul
							// la duree MOY
							if ((carr.getCodeCategorie().equals("2") || carr.getCodeCategorie().equals("18")) && avct.getPeriodeStandard() == 12) {
							} else {
								avancementService.calculAccBmFonctionnaire(getTransaction(), avct, carr, null, idAvisEmp, getAvancementFonctionnairesDao());
							}

							if (getTransaction().isErreur()) {
								return false;
							}
							commitTransaction();
						} else {
							// si ce n'est pas la derniere carriere du tableau
							// ie :
							// si datfin!=0
							// on met l'agent dans une variable et on affiche
							// cette
							// liste a l'ecran
							agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr() + "); ";
						}
					} catch (Exception e) {
						return false;
					}
				}
			}
		}
		performPB_FILTRER(request);
		return true;
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

	public HistoCarriereDao getHistoCarriereDao() {
		return histoCarriereDao;
	}

	public void setHistoCarriereDao(HistoCarriereDao histoCarriereDao) {
		this.histoCarriereDao = histoCarriereDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}