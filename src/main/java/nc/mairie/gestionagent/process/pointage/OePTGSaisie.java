package nc.mairie.gestionagent.process.pointage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.AbsenceDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.FichePointageDto;
import nc.mairie.gestionagent.pointage.dto.HeureSupDto;
import nc.mairie.gestionagent.pointage.dto.JourPointageDto;
import nc.mairie.gestionagent.pointage.dto.MotifHeureSupDto;
import nc.mairie.gestionagent.pointage.dto.PrimeDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.RadiWSConsumer;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class OePTGSaisie extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private Integer idAgent = null;
	private Date dateLundi = new Date();
	private boolean isDPM;
	private boolean isINASuperieur315;
	private List<List<PrimeDto>> primess = new ArrayList<>();
	private FichePointageDto listeFichePointage;
	private HashMap<String, List<AbsenceDto>> absences = new HashMap<>();
	private HashMap<String, List<HeureSupDto>> hsups = new HashMap<>();
	private Agent loggedAgent;
	private SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", new Locale("fr", "FR"));
	private SimpleDateFormat wsdf = new SimpleDateFormat("yyyyMMdd", new Locale("fr", "FR"));
	private Logger logger = LoggerFactory.getLogger(OePTGSaisie.class);
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	public String focus = null;

	private List<MotifHeureSupDto> listMotifHsup;

	@Override
	public String getJSP() {
		return "OePTGSaisie.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-SAISIE";
	}

	/**
	 * Initialisation des données.
	 */
	private void initialiseDonnees() throws Exception {
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		String date = wsdf.format(getDateLundi(0));
		FichePointageDto dto = t.getSaisiePointage(idAgent, date);
		setListeFichePointage(dto);

		setListMotifHsup(t.getListeMotifHeureSup());

		setINASuperieur315(dto.isINASuperieur315());
		setDPM(dto.isDPM());
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190","Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();
		setFocus(getDefaultFocus());
		setIdAgent((Integer) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_PTG));
		setDateLundi((String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LUNDI_PTG));
		if (!getTransaction().isErreur()) {
			initialiseDonnees();
		}
	}

	public String getDefaultFocus() {
		return getNOM_PB_VALIDATION();
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

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private boolean save(HttpServletRequest request) throws Exception {
		// on recupere l ensemble des donnees
		setJourPointageSaisi(getListData());

		// on controle les donnees saisies
		if (!controlSaisie(listeFichePointage.getSaisies()))
			return false;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		RadiWSConsumer radiConsu = new RadiWSConsumer();
		LightUserDto user = radiConsu.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
			try {
				loggedAgent = getAgentDao().chercherAgentParMatricule(
						radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
			} catch (Exception e) {
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
		}

		// on enregistre les pointages
		if (loggedAgent == null) {
			logger.debug("OePTGSaisie.Java : Objet Agent nul");
		} else {
			SirhPtgWSConsumer t = new SirhPtgWSConsumer();
			ReturnMessageDto message = t.setSaisiePointage(loggedAgent.getIdAgent(), listeFichePointage);
			if (message.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
				return false;
			}
		}

		return true;
	}

	private List<JourPointageDto> getListData() {

		List<JourPointageDto> newList = new ArrayList<>();

		int nbrPrime = primess.get(0).size();
		int i = 0;
		for (JourPointageDto jour : listeFichePointage.getSaisies()) {
			JourPointageDto temp = new JourPointageDto();
			temp.setDate(jour.getDate());
			for (int j = 0; j < 2; j++) {
				AbsenceDto dto = getAbsence(temp.getDate(), "ABS:" + i + ":" + j);
				if (dto != null) {
					// si heurefin < heuredeb alors J+1
					if (dto.getHeureDebut().getTime() > dto.getHeureFin().getTime()) {
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, dto.getHeureFin().getYear());
						cal.setTime(dto.getHeureFin());
						cal.add(Calendar.DATE, 1);

						dto.setHeureFin(cal.getTime());
					}
					temp.getAbsences().add(dto);
				}
				HeureSupDto hsdto = getHS(temp.getDate(), "HS:" + i + ":" + j);
				if (hsdto != null) {
					// si heurefin < heuredeb alors J+1
					if (hsdto.getHeureDebut().getTime() > hsdto.getHeureFin().getTime()) {
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, hsdto.getHeureFin().getYear());
						cal.setTime(hsdto.getHeureFin());
						cal.add(Calendar.DATE, 1);

						hsdto.setHeureFin(cal.getTime());
					}
					temp.getHeuresSup().add(hsdto);
				}
			}

			for (int prim = 0; prim < nbrPrime; prim++) {
				PrimeDto ptemp = primess.get(i).get(prim);
				PrimeDto primeAajouter = getPrime(temp.getDate(),
						"PRIME:" + ptemp.getNumRubrique() + ":" + ptemp.getIdRefPrime() + ":" + i, ptemp.getTitre(),
						ptemp.getTypeSaisie());
				if (primeAajouter != null) {
					// HEURE INDEMNITE ROULEMENT DPM
					// Vérification date debut > date fin
					if (primeAajouter.getNumRubrique() == 7715 && primeAajouter.getHeureDebut() != null) {
						if (primeAajouter.getHeureDebut().getTime() > primeAajouter.getHeureFin().getTime()) {
							Calendar cal = Calendar.getInstance();
							cal.set(Calendar.YEAR, primeAajouter.getHeureFin().getYear());
							cal.setTime(primeAajouter.getHeureFin());
							cal.add(Calendar.DATE, 1);

							primeAajouter.setHeureFin(cal.getTime());
						}
					}

					temp.getPrimes().add(primeAajouter);
				}
			}

			newList.add(temp);
			i++;
		}

		return newList;
	}

	private boolean controlSaisie(List<JourPointageDto> newList) {

		for (JourPointageDto jour : listeFichePointage.getSaisies()) {

			for (AbsenceDto absDto : jour.getAbsences()) {
				if (absDto != null) {
					// Vérification motif obligatoire
					if (absDto.getMotif().equals(Const.CHAINE_VIDE)) {
						getTransaction().traiterErreur();
						logger.debug("Tentative de sauvegarde d'une absence sans motif.");
						getTransaction().declarerErreur(
								"L'absence saisie le " + sdf.format(jour.getDate()) + " n'a pas de motif.");
						return false;
					}
					// Vérification type Absence obligatoire
					if (absDto.getIdRefTypeAbsence() == 0) {
						getTransaction().traiterErreur();
						logger.debug("Tentative de sauvegarde d'une absence sans type.");
						getTransaction().declarerErreur(
								"L'absence saisie le " + sdf.format(jour.getDate())
										+ " n'a pas de type (concertée, non concertée, immédiate).");
						return false;
					}
				}
			}

			for (HeureSupDto hsdto : jour.getHeuresSup()) {
				if (hsdto != null) {
					// Vérification motif obligatoire
					if (hsdto.getIdMotifHsup() == null) {
						getTransaction().traiterErreur();
						logger.debug("Tentative de sauvegarde d'une heure supplémentaire sans motif");
						getTransaction()
								.declarerErreur(
										"L'heure supplémentaire saisie le " + sdf.format(jour.getDate())
												+ " n'a pas de motif.");
						return false;
					}
				}
			}

			for (PrimeDto primeDto : jour.getPrimes()) {
				if (primeDto != null && null != primeDto.getMotif() && null != primeDto.getQuantite()) {
					// verification si prime 7704 que le nombre ne soit pas
					// supérieur a 2
					if (primeDto.getNumRubrique() == 7704 && primeDto.getQuantite() > 2) {
						getTransaction().traiterErreur();
						logger.debug("Tentative de sauvegarde de la prime 7704 avec une quantité supérieure a 2");
						getTransaction().declarerErreur(
								"La prime " + primeDto.getTitre() + " saisie le " + sdf.format(jour.getDate())
										+ " ne peut pas excéder 2 en quantité.");
						return false;
					}
				}
			}
		}

		return true;
	}

	private PrimeDto getPrime(Date d, String id, String title, String typesaisie) {
		PrimeDto ret = new PrimeDto();
		ret.setNumRubrique(Integer.parseInt(id.split(":")[1]));
		ret.setIdRefPrime(Integer.parseInt(id.split(":")[2]));
		ret.setTypeSaisie(typesaisie);
		ret.setTitre(title);

		DataContainer data = getData(id, d, false, false);
		boolean saisie = true;
		switch (typesaisie) {
			case "PERIODE_HEURES":
				if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
						&& data.getTimeD().getTime() == d.getTime() && data.getTimeF().getTime() == d.getTime()) {
					saisie = false;
				}
				break;
			case "NB_INDEMNITES":
				if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
						&& data.getNbr().equals(Const.CHAINE_VIDE)) {
					saisie = false;
				}
				break;
			case "NB_HEURES":
				if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
						&& data.getNbr().equals("0") && data.getMins().equals("00")) {
					saisie = false;
				}
				break;
			case "CASE_A_COCHER":
				if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
						&& (data.getChk().equals(Const.CHAINE_VIDE) || data.getChk().equals("CHECKED_OFF"))) {
					saisie = false;
				}
				break;
			default:
				saisie = false;
				break;
		}
		if (saisie) {
			ret.setMotif(data.getMotif());
			if (typesaisie.equals("NB_INDEMNITES")) {
				ret.setQuantite(Integer.parseInt("0" + data.getNbr().trim()));
			} else {
				if (data.getNbr() != null && !Const.CHAINE_VIDE.equals(data.getNbr())) {
					ret.setQuantite(Integer.parseInt("0" + data.getNbr().trim()) * 60
							+ (Integer.parseInt(data.getMins())));
				} else {
					ret.setQuantite(data.getChk().equals("CHECKED_ON") ? 1 : 0);
				}
			}

			ret.setHeureDebut(data.getTimeD());
			ret.setHeureFin(data.getTimeF());
			ret.setCommentaire(data.getComment());
			ret.setIdPointage(data.getIdPtg());
			ret.setIdRefEtat(data.getIdRefEtat());
			logger.debug("Prime " + id);
		}
		return ret;
	}

	private AbsenceDto getAbsence(Date d, String id) {
		AbsenceDto ret = null;
		DataContainer data = getData(id, d, true, false);
		boolean saisie = true;

		if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
				&& data.getTimeD().getTime() == d.getTime() && data.getTimeF().getTime() == d.getTime()) {
			saisie = false;
		}
		if (saisie) {
			ret = new AbsenceDto();
			ret.setHeureDebut(data.getTimeD());
			ret.setHeureFin(data.getTimeF());
			ret.setCommentaire(data.getComment());
			ret.setMotif(data.getMotif());
			ret.setIdRefEtat(data.getIdRefEtat());
			ret.setIdPointage(data.getIdPtg());
			ret.setIdRefTypeAbsence(data.getIdTypeAbsence());
			logger.debug("Absence " + id);
		}
		return ret;
	}

	private HeureSupDto getHS(Date d, String id) {
		HeureSupDto ret = null;
		DataContainer data = getData(id, d, false, true);
		boolean saisie = true;

		if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
				&& data.getTimeD().getTime() == d.getTime() && data.getTimeF().getTime() == d.getTime()) {
			saisie = false;
		}
		if (saisie) {
			ret = new HeureSupDto();
			ret.setRecuperee(getListeFichePointage().isDPM() || getListeFichePointage().isINASuperieur315() ? true
					: data.getChk().equals("CHECKED_ON"));
			ret.setRappelService(getListeFichePointage().isDPM() ? data.getChkRappelService().equals("CHECKED_ON")
					: false);
			ret.setHeureDebut(data.getTimeD());
			ret.setHeureFin(data.getTimeF());
			ret.setCommentaire(data.getComment());
			ret.setIdMotifHsup(data.getIdMotifHsup());
			ret.setMotif(data.getMotif());
			ret.setIdPointage(data.getIdPtg());
			ret.setIdRefEtat(data.getIdRefEtat());
			logger.debug("Heure sup " + id);
		}
		return ret;
	}

	private DataContainer getData(String id, Date d, boolean isAbsence, boolean isHeureSup) {
		DataContainer ret = new DataContainer();
		ret.setComment(getZone("NOM_comm_" + id));
		ret.setNbr(getZone("NOM_nbr_" + id));
		ret.setMins(getZone("NOM_mins_" + id));
		ret.setTimeD(getDateFromTimeCombo(d, getZone("NOM_time_" + id + "_D"), Integer.parseInt(id.split(":")[1])));
		ret.setTimeF(getDateFromTimeCombo(d, getZone("NOM_time_" + id + "_F"), Integer.parseInt(id.split(":")[1])));
		ret.setIdPtg(Integer.parseInt("0" + getZone("NOM_idptg_" + id)));
		ret.setIdRefEtat(Integer.parseInt("0" + getZone("NOM_idrefetat_" + id)));
		if (isAbsence) {
			ret.setIdTypeAbsence(Integer.parseInt("0" + getZone("NOM_typeAbs_" + id)));
		} else if (isHeureSup) {
			ret.setChk(getZone("NOM_CK_RECUP_" + id));
			ret.setChkRappelService(getZone("NOM_CK_RAPPEL_" + id));
			ret.setIdMotifHsup(Integer.parseInt("0" + getZone("NOM_motif_" + id)));
		} else {
			ret.setChk(getZone("NOM_CK_" + id));
		}
		return ret;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			if (testerParametre(request, getNOM_PB_VALIDATION())) {
				if (!save(request))
					return false;
				setStatut(STATUT_PROCESS_APPELANT);
				return true;
			}
			if (testerParametre(request, getNOM_PB_BACK())) {
				setStatut(STATUT_PROCESS_APPELANT);
				return true;
			}
		}
		setStatut(STATUT_MEME_PROCESS);// Si TAG INPUT non géré par le process
		return true;
	}

	public String getIdAgent() throws Exception {
		Agent agent = getAgentDao().chercherAgentParMatricule(idAgent);
		String service = Const.CHAINE_VIDE;
		try {
			Affectation affAgent = getAffectationDao().chercherAffectationActiveAvecAgent(agent.getIdAgent());
			if (affAgent.getIdFichePoste() != null) {
				try {
					FichePoste fp = getFichePosteDao().chercherFichePoste(affAgent.getIdFichePoste());
					Service serviceAgent = Service.chercherService(getTransaction(), fp.getIdServi());
					service = serviceAgent.getLibService();
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {

		}
		return agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + idAgent + ")"
				+ (service.equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : " - " + service);
	}

	public Date getDateLundi(int inc) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		calendar.setTime(dateLundi);
		calendar.add(Calendar.DATE, inc);
		return calendar.getTime();
	}

	public String getDateLundiStr(int inc) {
		return sdf.format(getDateLundi(inc));
	}

	public int getWeekYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		calendar.setTime(dateLundi);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public void setDateLundi(String _dateLundi) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			this.dateLundi = formatter.parse(_dateLundi);
		} catch (ParseException e) {
			logger.debug("ParseException in OePTGSaisie setDateLundi");
		}
	}

	private Date getDateFromTimeCombo(Date d, String h, int i) {
		Date ret = d;
		if (h.equals(Const.CHAINE_VIDE)) {
			return ret;
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		calendar.setTime(ret);
		calendar.add(GregorianCalendar.HOUR, Integer.parseInt(h.substring(0, h.indexOf(":"))));
		calendar.add(GregorianCalendar.MINUTE, Integer.parseInt(h.substring(h.indexOf(":") + 1)));
		return calendar.getTime();
	}

	// /////////////////////////////////// COMBOX BOX POUR LA JSP
	// //////////////////////////////////////////////////////
	public String getTimeCombo(Date heure) {

		String selected = Const.CHAINE_VIDE;
		if (null != heure) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			selected = df.format(heure);
		}
		String val = Const.CHAINE_VIDE;

		StringBuilder ret = new StringBuilder();
		ret.append("<option value=''></option>");
		for (int hours = 0; hours <= 23; hours++) {
			for (int min = 0; min < 60; min += 15) {
				val = hours + ":" + min;
				if (min == 0) {
					val += "0";
				}
				if (hours < 10) {
					val = "0" + val;
				}
				ret.append("<option value='" + val + "'" + (selected.equals(val) ? "selected" : "") + ">" + val
						+ "</option>");
			}
		}
		return ret.toString();
	}

	public String getTimeFinCombo(Date heure) {

		String selected = Const.CHAINE_VIDE;
		if (null != heure) {
			DateFormat df = new SimpleDateFormat("HH:mm");
			selected = df.format(heure);
		}
		String val = Const.CHAINE_VIDE;
		StringBuilder ret = new StringBuilder();
		ret.append("<option value=''></option>");

		for (int hours = 0; hours <= 24; hours++) {

			int minMin = 0;
			int maxMin = 60;
			if (hours == 0) {
				minMin = 0;
				maxMin = 60;
			}
			if (hours == 24) {
				minMin = 0;
				maxMin = 15;
			}

			for (int min = minMin; min < maxMin; min += 15) {
				val = hours + ":" + min;
				if (min == 0) {
					val += "0";
				}
				if (hours < 10) {
					val = "0" + val;
				}
				ret.append("<option value='" + val + "'" + (selected.equals(val) ? "selected" : "") + ">" + val
						+ "</option>");
			}
		}
		return ret.toString();
	}

	public String getMinsCombo(int ival) {
		StringBuilder ret = new StringBuilder();
		String selected = Const.CHAINE_VIDE + ival;
		if (ival == 0) {
			selected = "00";
		}
		String val = Const.CHAINE_VIDE;
		for (int min = 0; min < 60; min += 15) {
			val = Const.CHAINE_VIDE + min;
			if (min == 0) {
				val += "0";
			}
			ret.append("<option value='" + val + "'" + (selected.equals(val) ? "selected" : "") + ">" + val
					+ "</option>");
		}
		return ret.toString();
	}

	public String getTypeAbsenceCombo(Integer idTypeAbs) {

		Integer selected = 0;
		if (null != idTypeAbs) {
			selected = idTypeAbs;
		}

		StringBuilder ret = new StringBuilder();
		ret.append("<option value=''></option>");
		// on recupere la liste des types d'absence absence
		SirhPtgWSConsumer consu = new SirhPtgWSConsumer();
		ArrayList<TypeAbsenceDto> listeTypeAbs = (ArrayList<TypeAbsenceDto>) consu.getListeRefTypeAbsence();

		for (TypeAbsenceDto typeAbs : listeTypeAbs) {
			ret.append("<option value='" + typeAbs.getIdRefTypeAbsence() + "'"
					+ (selected == typeAbs.getIdRefTypeAbsence() ? "selected" : "") + ">" + typeAbs.getLibelle()
					+ "</option>");

		}
		return ret.toString();
	}

	public String getMotifHsup(Integer idMotifHsup) {

		Integer selected = 0;
		if (null != idMotifHsup) {
			selected = idMotifHsup;
		}

		StringBuilder ret = new StringBuilder();
		ret.append("<option value=''></option>");

		for (MotifHeureSupDto motif : getListMotifHsup()) {
			ret.append("<option value='" + motif.getIdMotifHsup() + "'"
					+ (selected == motif.getIdMotifHsup() ? "selected" : "") + " title='" + motif.getLibelle() + "' >"
					+ motif.getLibelle() + "</option>");

		}
		return ret.toString();
	}

	// /////////////////////////////////// FIN COMBOX BOX POUR LA JSP
	// //////////////////////////////////////////////////////

	public void addZone(String zone, String valeur) {
		super.addZone(zone, valeur);
	}

	public String getNOM_PB_BACK() {
		return "NOM_PB_BACK";
	}

	public String getNOM_PB_VALIDATION() {
		return "NOM_PB_VALIDATION";
	}

	// ////////////////////GETTER SETTER /////////////////////

	public List<List<PrimeDto>> getPrimess() {
		return primess;
	}

	public void setPrimess(List<List<PrimeDto>> primess) {
		this.primess = primess;
	}

	public HashMap<String, List<AbsenceDto>> getAbsences() {
		return absences;
	}

	public void setAbsences(HashMap<String, List<AbsenceDto>> absences) {
		this.absences = absences;
	}

	public HashMap<String, List<HeureSupDto>> getHsups() {
		return hsups;
	}

	public void setHsups(HashMap<String, List<HeureSupDto>> hsups) {
		this.hsups = hsups;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public FichePointageDto getListeFichePointage() {
		return listeFichePointage;
	}

	public void setListeFichePointage(FichePointageDto listeFichePointage) {
		this.listeFichePointage = listeFichePointage;

		absences.clear();
		primess.clear();
		hsups.clear();
		for (JourPointageDto jour : listeFichePointage.getSaisies()) {
			primess.add(jour.getPrimes());
			absences.put(sdf.format(jour.getDate()), jour.getAbsences());
			hsups.put(sdf.format(jour.getDate()), jour.getHeuresSup());
		}
	}

	public void setJourPointageSaisi(List<JourPointageDto> listJourPtg) {
		this.listeFichePointage.setSaisies(listJourPtg);

		absences.clear();
		primess.clear();
		hsups.clear();
		for (JourPointageDto jour : listJourPtg) {
			primess.add(jour.getPrimes());
			absences.put(sdf.format(jour.getDate()), jour.getAbsences());
			hsups.put(sdf.format(jour.getDate()), jour.getHeuresSup());
		}
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

	public boolean isDPM() {
		return isDPM;
	}

	public void setDPM(boolean isDPM) {
		this.isDPM = isDPM;
	}

	public boolean isINASuperieur315() {
		return isINASuperieur315;
	}

	public void setINASuperieur315(boolean isINASuperieur315) {
		this.isINASuperieur315 = isINASuperieur315;
	}

	public List<MotifHeureSupDto> getListMotifHsup() {
		return listMotifHsup;
	}

	public void setListMotifHsup(List<MotifHeureSupDto> listMotifHsup) {
		this.listMotifHsup = listMotifHsup;
	}

}
