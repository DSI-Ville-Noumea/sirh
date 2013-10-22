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

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AbsenceDto;
import nc.mairie.gestionagent.dto.FichePointageDto;
import nc.mairie.gestionagent.dto.HeureSupDto;
import nc.mairie.gestionagent.dto.JourPointageDto;
import nc.mairie.gestionagent.dto.PrimeDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;

/**
 *
 */
public class OePTGSaisie extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private String idAgent = "";
	private Date dateLundi = new Date();
	private List<List<PrimeDto>> primess = new ArrayList<>();
	private FichePointageDto listeFichePointage;
	private HashMap<String, List<AbsenceDto>> absences = new HashMap<>();
	private HashMap<String, List<HeureSupDto>> hsups = new HashMap<>();
	private AgentNW loggedAgent;
	private SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", new Locale("fr", "FR"));
	private SimpleDateFormat wsdf = new SimpleDateFormat("yyyyMMdd", new Locale("fr", "FR"));
	private Logger logger = LoggerFactory.getLogger(OePTGSaisie.class);

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
		listeFichePointage = t.getSaisiePointage(idAgent, date);
		absences.clear();
		primess.clear();
		hsups.clear();
		for (JourPointageDto jour : listeFichePointage.getSaisies()) {
			primess.add(jour.getPrimes());
			absences.put(sdf.format(jour.getDate()), jour.getAbsences());
			hsups.put(sdf.format(jour.getDate()), jour.getHeuresSup());
		}
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MairieUtils.estInterdit(request, getNomEcran())) { // "ERR190",
																// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		setIdAgent((String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_PTG));
		setDateLundi((String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LUNDI_PTG));
		initialiseDonnees();
		UserAppli uuser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		if (!uuser.getUserName().equals("nicno85") && !uuser.getUserName().equals("levch80")) {
			Siidma user = Siidma.chercherSiidma(getTransaction(), uuser.getUserName().toUpperCase());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (user != null && user.getNomatr() != null) {
				loggedAgent = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
			}
		} else {
			loggedAgent = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
	}

	private boolean save() throws Exception {

		// compare date lundi avec aujourd'hui pour savoir si autorisé à
		// enregister
		if (((new Date().getTime() - getDateLundi(0).getTime()) / (1000l * 60 * 60 * 24 * 30)) >= 3) {
			logger.debug("\nTentative de sauvegarde d'un pointage de plus de 3 mois");
			getTransaction().declarerErreur("La semaine sélectionnée est trop ancienne pour être modifiée");
			return false;
		}

		List<JourPointageDto> newList = new ArrayList<>();

		int nbrPrime = primess.get(0).size();

		int i = 0;
		for (JourPointageDto jour : listeFichePointage.getSaisies()) {
			JourPointageDto temp = new JourPointageDto();
			temp.setDate(jour.getDate());
			for (int j = 0; j < 2; j++) {
				AbsenceDto dto = getAbsence(temp.getDate(), "ABS:" + i + ":" + j);
				if (dto != null) {
					// vérification date debut>date fin
					if (dto.getHeureDebut().getTime() >= dto.getHeureFin().getTime()) {
						logger.debug("\nTentative de sauvegarde d'une absence de durée nulle ou négative");
						getTransaction().declarerErreur(
								"L'absence saisie le " + sdf.format(jour.getDate())
										+ " est de durée  nulle ou négative.");
						return false;
					}
					// vérification motif obligatoire
					if (dto.getMotif().equals(Const.CHAINE_VIDE)) {
						logger.debug("\nTentative de sauvegarde d'une absence sans motif.");
						getTransaction().declarerErreur(
								"L'absence saisie le " + sdf.format(jour.getDate()) + " n'a pas de motif.");
						return false;
					}
					temp.getAbsences().add(dto);
				}
				HeureSupDto hsdto = getHS(temp.getDate(), "HS:" + i + ":" + j);
				if (hsdto != null) {
					// vérification date debut>date fin
					if (hsdto.getHeureDebut().getTime() >= hsdto.getHeureFin().getTime()) {
						logger.debug("\nTentative de sauvegarde d'une heure supplémentaire de durée nulle ou négative");
						getTransaction().declarerErreur(
								"L'heure supplémentaire saisie le " + sdf.format(jour.getDate())
										+ " est de durée nulle ou négative.");
						return false;
					}
					// vérification motif obligatoire
					if (hsdto.getMotif().equals(Const.CHAINE_VIDE)) {
						logger.debug("\nTentative de sauvegarde d'une heure supplémentaire sans motif");
						getTransaction()
								.declarerErreur(
										"L'heure supplémentaire saisie le " + sdf.format(jour.getDate())
												+ " n'a pas de motif.");
						return false;
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
					// vérification motif obligatoire
					if (primeAajouter.getMotif().equals(Const.CHAINE_VIDE)) {
						logger.debug("\nTentative de sauvegarde d'une prime sans motif");
						getTransaction().declarerErreur(
								"La prime " + ptemp.getTitre() + " saisie le " + sdf.format(jour.getDate())
										+ " n'a pas de motif.");
						return false;
					}
					// verification si prime 7704 que le nombre ne soit pas
					// supérieur à 2
					if (primeAajouter.getNumRubrique() == 7704 && primeAajouter.getQuantite() > 2) {
						logger.debug("\nTentative de sauvegarde de la prime 7704 avec une quantité supérieure à 2");
						getTransaction().declarerErreur(
								"La prime " + ptemp.getTitre() + " saisie le " + sdf.format(jour.getDate())
										+ " ne peut pas excéder 2 en quantité.");
						return false;
					}

					temp.getPrimes().add(primeAajouter);
				}
			}

			newList.add(temp);
			i++;
		}

		listeFichePointage.setSaisies(newList);
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		if (loggedAgent == null) {
			logger.debug("OePTGSaisie.Java : Objet Agent nul");
		} else {
			ClientResponse res = t.setSaisiePointage(loggedAgent.idAgent, listeFichePointage);
			if (res.getStatus() != 200) {
				String rep = res.getEntity(String.class).toString();
				logger.debug("response :" + res.toString() + "\n" + rep);
				rep = (rep.indexOf("[") > -1) ? rep.substring(rep.indexOf("[") + 1) : rep;
				rep = (rep.indexOf("]") > -1) ? rep.substring(0, rep.indexOf("]")) : rep;
				getTransaction().declarerErreur(rep);
			}
		}

		return true;
	}

	private PrimeDto getPrime(Date d, String id, String title, String typesaisie) {
		PrimeDto ret = null;
		DataContainer data = getData(id, d);
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
						&& data.getChk().equals("")) {
					saisie = false;
				}
				break;
			default:
				saisie = false;
				break;
		}
		if (saisie) {
			ret = new PrimeDto();
			ret.setMotif(data.getMotif());
			if (typesaisie.equals("NB_INDEMNITES")) {
				ret.setQuantite(Integer.parseInt("0" + data.getNbr().trim()));
			} else {
				if (data.getNbr() != null && !"".equals(data.getNbr())) {
					ret.setQuantite(Integer.parseInt("0" + data.getNbr().trim()) * 60
							+ (Integer.parseInt(data.getMins())));
				} else {
					ret.setQuantite(data.getChk().equals("on") ? 1 : 0);
				}
			}
			ret.setNumRubrique(Integer.parseInt(id.split(":")[1]));
			ret.setIdRefPrime(Integer.parseInt(id.split(":")[2]));
			ret.setHeureDebut(data.getTimeD());
			ret.setHeureFin(data.getTimeF());
			ret.setCommentaire(data.getComment());
			ret.setIdPointage(data.getIdPtg());
			ret.setIdRefEtat(data.getIdRefEtat());
			ret.setTitre(title);
			ret.setTypeSaisie(typesaisie);
			logger.debug("Prime " + id);
		}
		return ret;
	}

	private AbsenceDto getAbsence(Date d, String id) {
		AbsenceDto ret = null;
		DataContainer data = getData(id, d);
		boolean saisie = true;
		if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
				&& data.getTimeD().getTime() == d.getTime() && data.getTimeF().getTime() == d.getTime()) {
			saisie = false;
		}
		if (saisie) {
			ret = new AbsenceDto();
			ret.setConcertee(data.getChk().equals("on"));
			ret.setHeureDebut(data.getTimeD());
			ret.setHeureFin(data.getTimeF());
			ret.setCommentaire(data.getComment());
			ret.setMotif(data.getMotif());
			ret.setIdRefEtat(data.getIdRefEtat());
			ret.setIdPointage(data.getIdPtg());
			logger.debug("Absence " + id);
		}
		return ret;
	}

	private HeureSupDto getHS(Date d, String id) {
		HeureSupDto ret = null;
		DataContainer data = getData(id, d);
		boolean saisie = true;
		if (data.getComment().equals(Const.CHAINE_VIDE) && data.getMotif().equals(Const.CHAINE_VIDE)
				&& data.getTimeD().getTime() == d.getTime() && data.getTimeF().getTime() == d.getTime()) {
			saisie = false;
		}
		if (saisie) {
			ret = new HeureSupDto();
			ret.setRecuperee(data.getChk().equals("on"));
			ret.setHeureDebut(data.getTimeD());
			ret.setHeureFin(data.getTimeF());
			ret.setCommentaire(data.getComment());
			ret.setMotif(data.getMotif());
			ret.setIdPointage(data.getIdPtg());
			ret.setIdRefEtat(data.getIdRefEtat());
			logger.debug("Heure sup " + id);
		}
		return ret;
	}

	private DataContainer getData(String id, Date d) {
		DataContainer ret = new DataContainer();
		ret.setChk(getZone("NOM_chk_" + id));
		ret.setMotif(getZone("NOM_motif_" + id));
		ret.setComment(getZone("NOM_comm_" + id));
		ret.setNbr(getZone("NOM_nbr_" + id));
		ret.setMins(getZone("NOM_mins_" + id));
		ret.setTimeD(getDateFromTimeCombo(d, getZone("NOM_time_" + id + "_D"), Integer.parseInt(id.split(":")[1])));
		ret.setTimeF(getDateFromTimeCombo(d, getZone("NOM_time_" + id + "_F"), Integer.parseInt(id.split(":")[1])));
		ret.setIdPtg(Integer.parseInt("0" + getZone("NOM_idptg_" + id)));
		ret.setIdRefEtat(Integer.parseInt("0" + getZone("NOM_idrefetat_" + id)));
		return ret;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			if (testerParametre(request, getNOM_PB_VALIDATION())) {
				if (!save())
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
		AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), idAgent);
		String service = "";
		Affectation affAgent = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agent.getIdAgent());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		} else {
			if (affAgent.getIdFichePoste() != null) {
				FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), affAgent.getIdFichePoste());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					service = fp.getIdServi();
				}
			}
		}
		return agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + idAgent + ")"
				+ (service.equals("") ? "" : " - " + service);
	}

	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateLundi(int inc) {
		GregorianCalendar calendar = new java.util.GregorianCalendar();
		calendar.setTime(dateLundi);
		calendar.add(Calendar.DATE, inc);
		return calendar.getTime();
	}

	public String getDateLundiStr(int inc) {
		return sdf.format(getDateLundi(inc));
	}

	public int getWeekYear() {
		GregorianCalendar calendar = new java.util.GregorianCalendar();
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
		if (h.equals("")) {
			return ret;
		}
		GregorianCalendar calendar = new java.util.GregorianCalendar();
		calendar.setTime(ret);
		calendar.add(GregorianCalendar.HOUR, Integer.parseInt(h.substring(0, h.indexOf(":"))));
		calendar.add(GregorianCalendar.MINUTE, Integer.parseInt(h.substring(h.indexOf(":") + 1)));
		return calendar.getTime();
	}

	public String getHeaderTable() {
		StringBuilder ret = new StringBuilder();
		ret.append("<tr>");// <th>Intitulé</th>");
		for (int i = 0; i < 7; i++) {
			ret.append("<TH>");
			ret.append(getDateLundiStr(i));
			ret.append("</TH>");
		}
		ret.append("</tr>");
		return ret.toString();
	}

	public String getPrimesTab() {
		StringBuilder ret = new StringBuilder();
		if (primess.size() > 0) {
			int nbrPrime = primess.get(0).size();
			if (nbrPrime > 0) {
				ret.append(getLineTitle("Primes"));
			}

			for (int i = 0; i < nbrPrime; i++) {
				ret.append("<tr>");
				int jour = 0;
				for (List<PrimeDto> pl : primess) {
					ret.append(getCell(pl.get(i), jour));
					jour++;
				}
				ret.append("</tr>");
			}
		} else {
			ret.append("");
		}
		return ret.toString();
	}

	public String getHSTab() {
		StringBuilder ret = new StringBuilder();
		String id = "HS:";
		ret.append(getLineTitle("Heures Supplémentaires"));
		String dateIndex = "";
		Date bidon = new Date();
		bidon.setHours(0);
		bidon.setMinutes(0);
		for (int j = 0; j < 2; j++) {
			ret.append("<tr>");
			for (int i = 0; i < 7; i++) {
				dateIndex = getDateLundiStr(i);
				if (hsups.containsKey(dateIndex) && hsups.get(dateIndex).size() > j) {
					HeureSupDto hs = hsups.get(dateIndex).get(j);
					String status = hs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(hs
							.getIdRefEtat()) : "";
					ret.append(getTypeHSCell(id + i + ":" + j, "A récupérer", hs.getRecuperee(), hs.getHeureDebut(),
							hs.getHeureFin(), hs.getMotif(), hs.getCommentaire(), status, "", hs.getIdPointage(),
							hs.getIdRefEtat()));
				} else {
					ret.append(getTypeHSCell(id + i + ":" + j, "A récupérer", false, bidon, bidon, "", "", "", "", 0, 0));
				}
			}
			ret.append("</tr>");
		}
		return ret.toString();
		// return getType3TabLine("HS0", "Heures Supplémentaires",
		// "A récupérer") + getType3TabLine("HS1", "Heures Supplémentaires",
		// "A récupérer");
	}

	public String getAbsTab() {
		StringBuilder ret = new StringBuilder();
		String id = "ABS:";
		ret.append(getLineTitle("Absences"));
		Date bidon = new Date();
		bidon.setHours(0);
		bidon.setMinutes(0);
		String dateIndex = "";

		for (int j = 0; j < 2; j++) {
			ret.append("<tr>");
			for (int i = 0; i < 7; i++) {
				dateIndex = getDateLundiStr(i);
				if (absences.containsKey(dateIndex) && absences.get(dateIndex).size() > j) {
					AbsenceDto abs = absences.get(dateIndex).get(j);
					String status = abs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(abs
							.getIdRefEtat()) : "";
					boolean chk = abs.getConcertee() != null ? abs.getConcertee() : false;
					ret.append(getTypeAbsCell(id + i + ":" + j, "Concertée", chk, abs.getHeureDebut(),
							abs.getHeureFin(), abs.getMotif(), abs.getCommentaire(), status, "", abs.getIdPointage(),
							abs.getIdRefEtat()));
				} else {
					ret.append(getTypeAbsCell(id + i + ":" + j, "Concertée", false, bidon, bidon, "", "", "", "", 0, 0));
				}
			}
			ret.append("</tr>");
		}
		return ret.toString();
	}

	private String getCell(PrimeDto p, int i) {
		String id = "PRIME:" + p.getNumRubrique() + ":" + p.getIdRefPrime() + ":" + i;
		String motif = p.getMotif() != null ? p.getMotif() : "";
		String titre = p.getTitre() != null ? p.getTitre() : "";
		String commentaire = p.getCommentaire() != null ? p.getCommentaire() : "";
		String qte = p.getQuantite() != null ? "" + p.getQuantite() : "";
		int nbrMinsTot = qte == "" ? 0 : Integer.parseInt(qte);
		int idref = p.getIdRefEtat() != null ? p.getIdRefEtat() : 0;
		int idptg = p.getIdPointage() != null ? p.getIdPointage() : 0;
		String status = p.getIdRefEtat() != null && !motif.equals("") ? EtatPointageEnum
				.getDisplayableEtatPointageEnum(idref) : "";
		switch (TypeSaisieEnum.valueOf(p.getTypeSaisie())) {
			case CASE_A_COCHER:
				return getType0TabCell(id, qte.equals("1"), motif, commentaire, status, titre, idptg, idref);
			case NB_INDEMNITES:
				return getType1TabCell(id, qte, motif, commentaire, status, "Nbre d'indemnités", titre, idptg, idref);
			case NB_HEURES:
				return getType2TabCell(id, nbrMinsTot, motif, commentaire, status, "Nbre d'heures", titre, idptg, idref);
			case PERIODE_HEURES:
				return getType3TabCell(id, qte.equals("1"), p.getHeureDebut(), p.getHeureFin(), motif, commentaire,
						status + "<br>", titre, idptg, idref);
			default:
		}
		return "failcell:" + id;
	}

	private String getLineTitle(String title) {
		return "<tr bgcolor='#009ACD'><TD colspan=8 align=center><b><H4>" + title + "<H3></b></TD></tr>";
	}

	private String getHead(String id, String status, String title) {
		StringBuilder ret = new StringBuilder();
		if (!"".equals(title)) {
			ret.append("<tr><td><CENTER><b>" + title + " </b></CENTER></td></tr>");
		}
		ret.append("<tr><td><CENTER>"
				+ status
				+ " "
				+ (status.equals("Saisi") ? " <img src='images/suppression.gif' height='16px' width='16px' onClick=\"suppr('"
						+ id + "')\">"
						: "<img src='images/vide.png' height='16px' width='16px'>") + "</CENTER></TD></tr>");
		return ret.toString();
	}

	private String getType0TabCell(String id, boolean check, String motif, String comment, String status, String title,
			int idptg, int idrefetat) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type0TabCell" + id + "'>");
		ret.append(getHead(id, status, title));
		ret.append("<tr bgcolor='#BFEFFF'><td><input type='checkbox' name='NOM_chk_" + id + "'"
				+ (check ? "checked" : "") + "> accordée</td></tr>");
		ret.append(commonFields(id, motif, comment, idptg, idrefetat));
		ret.append("</table></td>");

		addZone("acc_" + id, "" + check);
		return ret.toString();
	}

	private String getType1TabCell(String id, String nbr, String motif, String comment, String status, String label,
			String title, int idptg, int idrefetat) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1TabCell" + id + "'>");
		ret.append(getHead(id, status, title));
		ret.append("<tr bgcolor='#BFEFFF'><td>" + label + "<input type='text' size='4' name='NOM_nbr_" + id
				+ "' value='" + nbr + "'></td></tr>");
		ret.append(commonFields(id, motif, comment, idptg, idrefetat));
		ret.append("</table></td>");
		addZone("nbr_" + id, "" + nbr);
		return ret.toString();
	}

	private String getType2TabCell(String id, int nbrMins, String motif, String comment, String status, String label,
			String title, int idptg, int idrefetat) {

		int nbr = nbrMins / 60;
		int mins = nbrMins - nbr * 60;
		StringBuilder ret = new StringBuilder();
		ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type2TabCell" + id + "'>");
		ret.append(getHead(id, status, title));
		ret.append("<tr bgcolor='#BFEFFF'><td>" + label + "<input type='text' size='2' name='NOM_nbr_" + id
				+ "' value='" + nbr + "'>h<select name='NOM_mins_" + id + "'>" + getMinsCombo(mins)
				+ " </select></td></tr>");
		ret.append(commonFields(id, motif, comment, idptg, idrefetat));
		ret.append("</table></td>");
		addZone("nbr_" + id, "" + nbr);
		return ret.toString();
	}

	private String getType3TabCell(String id, boolean check, Date heureDebut, Date heureFin, String motif,
			String comment, String status, String title, int idptg, int idrefetat) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id
				+ "'>");
		ret.append(getHead(id, status, title));
		ret.append("<tr bgcolor='#BFEFFF'><td> Heure début  -->   Heure fin <br><select name='NOM_time_" + id + "_D"
				+ "'>" + getTimeCombo(heureDebut) + " </select>  /  <select name='NOM_time_" + id + "_F" + "'>"
				+ getTimeCombo(heureFin) + " </select></td></tr>");
		ret.append(commonFields(id, motif, comment, idptg, idrefetat));
		ret.append("</table></td>");
		return ret.toString();
	}

	private String getTypeAbsCell(String id, String checkname, boolean check, Date heureDebut, Date heureFin,
			String motif, String comment, String status, String title, int idptg, int idrefetat) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id
				+ "'>");
		ret.append(getHead(id, status, title));
		ret.append("<tr bgcolor='#BFEFFF'><td> Heure début  -->   Heure fin <br><select name='NOM_time_" + id + "_D"
				+ "'>" + getTimeCombo(heureDebut) + " </select>  /  <select name='NOM_time_" + id + "_F" + "'>"
				+ getTimeCombo(heureFin) + " </select></td></tr>");
		ret.append("<tr bgcolor='#BFEFFF'><td><input type='checkbox' name='NOM_chk_" + id + "'"
				+ (check ? "checked" : "") + ">" + checkname + "</td></tr>");
		ret.append(commonFields(id, motif, comment, idptg, idrefetat));
		ret.append("</table></td>");
		return ret.toString();
	}

	private String getTypeHSCell(String id, String checkname, boolean check, Date heureDebut, Date heureFin,
			String motif, String comment, String status, String title, int idptg, int idrefetat) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id
				+ "'>");
		ret.append(getHead(id, status, title));
		ret.append("<tr bgcolor='#BFEFFF'><td> Heure début  -->   Heure fin <br><select name='NOM_time_" + id + "_D"
				+ "'>" + getTimeCombo(heureDebut) + " </select>  /  <select name='NOM_time_" + id + "_F" + "'>"
				+ getTimeCombo(heureFin) + " </select></td></tr>");
		ret.append("<tr bgcolor='#BFEFFF'><td><input type='checkbox' name='NOM_chk_" + id + "'"
				+ (check ? "checked" : "") + ">" + checkname + "</td></tr>");
		ret.append(commonFields(id, motif, comment, idptg, idrefetat));
		ret.append("</table></td>");
		return ret.toString();
	}

	private String commonFields(String id, String motif, String comment, int idptg, int idrefetat) {
		StringBuilder ret = new StringBuilder();
		motif = motif.equals("null") ? "" : motif;
		comment = comment.equals("null") ? "" : comment;
		ret.append("<tr bgcolor='#BFEFFF'><td><INPUT type='text' class=\"sigp2-saisie\"  length='50px' name='NOM_motif_"
				+ id + "' value='" + motif + "' title='Zone de saisie du motif'></td></tr>");
		ret.append("<tr bgcolor='#BFEFFF'><td><textarea  cols='15' rows='3' name='NOM_comm_" + id
				+ "' title='Zone de saisie du commentaire'>" + comment + "</textarea></td></tr>");
		ret.append("<textarea  cols='10' rows='1'  style='visibility: hidden' name='NOM_idptg_" + id
				+ "' title='Zone cachee idptg'>" + idptg + "</textarea>");
		ret.append("<textarea  cols='10' rows='1' style='visibility: hidden' name='NOM_idrefetat_" + id
				+ "' title='Zone cachee idrefetat'>" + idrefetat + "</textarea>");

		return ret.toString();
	}

	private String getTimeCombo(Date heure) {
		StringBuilder ret = new StringBuilder();
		heure = heure == null ? new Date() : heure;
		ret.append("<option value=''></option>");
		DateFormat df = new SimpleDateFormat("HH:mm");
		String seleted = df.format(heure);
		String val = "";
		for (int hours = 0; hours <= 23; hours++) {
			for (int min = 0; min < 60; min += 15) {
				val = hours + ":" + min;
				if (min == 0) {
					val += "0";
				}
				if (hours < 10) {
					val = "0" + val;
				}
				ret.append("<option value='" + val + "'" + (seleted.equals(val) ? "selected" : "") + ">" + val
						+ "</option>");
			}
		}
		return ret.toString();
	}

	private String getMinsCombo(int ival) {
		StringBuilder ret = new StringBuilder();
		String selected = "" + ival;
		if (ival == 0) {
			selected = "00";
		}
		String val = "";
		for (int min = 0; min < 60; min += 15) {
			val = "" + min;
			if (min == 0) {
				val += "0";
			}
			ret.append("<option value='" + val + "'" + (selected.equals(val) ? "selected" : "") + ">" + val
					+ "</option>");
		}
		return ret.toString();
	}

	public String getNOM_PB_BACK() {
		return "NOM_PB_BACK";
	}

	public String getNOM_PB_VALIDATION() {
		return "NOM_PB_VALIDATION";
	}
}
