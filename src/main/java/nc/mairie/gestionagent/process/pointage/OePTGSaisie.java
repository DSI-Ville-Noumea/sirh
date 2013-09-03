package nc.mairie.gestionagent.process.pointage;

import com.sun.jersey.api.client.ClientResponse;
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
import nc.mairie.gestionagent.process.avancement.OeAVCTCampagneGestionEAE;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import static nc.mairie.technique.BasicProcess.STATUT_PROCESS_APPELANT;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final String VALIDATION = "validation page saisie";
    public static final String BACK = "back sur page saisie";
    public static final String DATE_FORMAT = "EEEE dd MMMM yyyy";
    private AgentNW loggedAgent;
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, new Locale("fr", "FR"));
    	private Logger logger = LoggerFactory.getLogger(OeAVCTCampagneGestionEAE.class);

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
        listeFichePointage = t.getSaisiePointage(idAgent, Services.convertitDate(getDateLundiStr(0), DATE_FORMAT, "yyyyMMdd"));
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
    public void initialiseZones(HttpServletRequest request) throws Exception { // POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
        VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
        if (MairieUtils.estInterdit(request, getNomEcran())) { // "ERR190", "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
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
        List<JourPointageDto> newList = new ArrayList<>();

        int nbrPrime = primess.get(0).size();

        int i = 0;
        for (JourPointageDto jour : listeFichePointage.getSaisies()) {
            JourPointageDto temp = new JourPointageDto();
            temp.setDate(jour.getDate());
            for (int j = 0; j < 2; j++) {
                AbsenceDto dto = getAbsence(temp.getDate(), "ABS:" + i + ":" + j);
                if (dto != null) {
                    temp.getAbsences().add(dto);
                }
                HeureSupDto hsdto = getHS(temp.getDate(), "HS:" + i + ":" + j);
                if (hsdto != null) {
                    temp.getHeuresSup().add(hsdto);
                }
            }
            //TODO for liste des primes
            for (int prim = 0; prim < nbrPrime; prim++) {
                PrimeDto ptemp = primess.get(i).get(prim);
                temp.getPrimes().add(getPrime(temp.getDate(), "PRIME:" + ptemp.getNumRubrique() + ":" + ptemp.getIdRefPrime() + ":" + i, ptemp.getTitre(), ptemp.getTypeSaisie()));
            }
            newList.add(temp);
            i++;
        }
        listeFichePointage.setSaisies(newList);
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();
        if (loggedAgent == null) {
            logger.debug("OePTGSaisie.Java : Agent complètement nul!");// getDateLundi(0).getTime()+"+"+ getDateLundi(0).getTimezoneOffset()    Services.convertitDate(getDateLundiStr(0), DATE_FORMAT, "yyyyMMdd")
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
        ret = new PrimeDto();
        ret.setMotif(data.getMotif());
        if (!ret.getMotif().equals("")) {
            if (data.getNbr() != null && !"".equals(data.getNbr())) {
                ret.setQuantite(Integer.parseInt("0" + data.getNbr().trim()));
            } else {
                ret.setQuantite(data.getChk().equals("on") ? 1 : 0);
            }
            ret.setNumRubrique(Integer.parseInt(id.split(":")[1]));
            ret.setIdRefPrime(Integer.parseInt(id.split(":")[2]));
            ret.setHeureDebut(data.getTimeD());
            ret.setHeureFin(data.getTimeF());
            ret.setCommentaire(data.getComment());
            ret.setTitre(title);
            ret.setTypeSaisie(typesaisie);
        }
        logger.debug("Prime " + id);
        return ret;
    }

    private AbsenceDto getAbsence(Date d, String id) {
        AbsenceDto ret = null;
        DataContainer data = getData(id, d);
        if (!data.getMotif().equals("")) {
            ret = new AbsenceDto();
            ret.setConcertee(data.getChk().equals("on"));
            ret.setHeureDebut(data.getTimeD());
            ret.setHeureFin(data.getTimeF());
            ret.setCommentaire(data.getComment());
            ret.setMotif(data.getMotif());
            logger.debug("Absence " + id);
        }
        return ret;
    }

    private HeureSupDto getHS(Date d, String id) {
        HeureSupDto ret = null;
        DataContainer data = getData(id, d);
        if (!data.getMotif().equals("")) {
            ret = new HeureSupDto();
            ret.setRecuperee(data.getChk().equals("on"));
            ret.setHeureDebut(data.getTimeD());
            ret.setHeureFin(data.getTimeF());
            ret.setCommentaire(data.getComment());
            ret.setMotif(data.getMotif());
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
        ret.setTimeD(getDateFromTimeCombo(d, getZone("NOM_time_" + id + "_D"), Integer.parseInt(id.split(":")[1])));
        ret.setTimeF(getDateFromTimeCombo(d, getZone("NOM_time_" + id + "_F"), Integer.parseInt(id.split(":")[1])));
        return ret;
    }

    @Override
    public boolean recupererStatut(HttpServletRequest request) throws Exception {
        if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) { // Si on arrive de la JSP alors on traite le get
            if (testerParametre(request, VALIDATION)) {
                save();
                setStatut(STATUT_PROCESS_APPELANT);
                return true;
            }
            if (testerParametre(request, BACK)) {
                setStatut(STATUT_PROCESS_APPELANT);
                return true;
            }
        }
        setStatut(STATUT_MEME_PROCESS);// Si TAG INPUT non géré par le process
        return true;
    }

    public String getIdAgent() {
        return idAgent;
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
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, new Locale("fr", "FR"));
       return formatter.format(getDateLundi(inc));
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
        ret.append("<tr>");//<th>Intitulé</th>");
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
                    String status = hs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(hs.getIdRefEtat()) : "";
                   ret.append(getType3TabCell(id + i + ":" + j, "A récupérer", hs.getRecuperee(), hs.getHeureDebut(), hs.getHeureFin(), hs.getMotif(), hs.getCommentaire(), status, ""));
                } else {
                    ret.append(getType3TabCell(id + i + ":" + j, "A récupérer", false, bidon, bidon, "", "", "", ""));
                }
            }
            ret.append("</tr>");
        }
        return ret.toString();
        // return getType3TabLine("HS0", "Heures Supplémentaires", "A récupérer") + getType3TabLine("HS1", "Heures Supplémentaires", "A récupérer");
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
                    String status = abs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(abs.getIdRefEtat()) : "";
                    boolean chk = abs.getConcertee() != null ? abs.getConcertee() : false;
                    ret.append(getType3TabCell(id + i + ":" + j, "Concertée", chk, abs.getHeureDebut(), abs.getHeureFin(), abs.getMotif(), abs.getCommentaire(), status, ""));
                } else {
                    ret.append(getType3TabCell(id + i + ":" + j, "Concertée", false, bidon, bidon, "", "", "", ""));
                }
            }
            ret.append("</tr>");
        }
        return ret.toString();
    }

    /**
     * private String getLibelleCell(PrimeDto p) { return
     * getLibelleCell(p.getTypeSaisie(), p.getTitre()); }*
     */
    private String getCell(PrimeDto p, int i) {
        String id = "PRIME:" + p.getNumRubrique() + ":" + p.getIdRefPrime() + ":" + i; //+ ":" + p.getIdPointage()
        String motif = p.getMotif() != null ? p.getMotif() : "";
        String commentaire = p.getCommentaire() != null ? p.getCommentaire() : "";
        String qte = p.getQuantite() != null ? "" + p.getQuantite() : "";
        int idref = p.getIdRefEtat() != null ? p.getIdRefEtat() : 0;
        String status = p.getIdRefEtat() != null && !motif.equals("") ? EtatPointageEnum.getDisplayableEtatPointageEnum(idref) : "";
        switch (TypeSaisieEnum.valueOf(p.getTypeSaisie())) {
            case CASE_A_COCHER:
                return getType0TabCell(id, qte.equals("1"), motif, commentaire, status, p.getTitre());
            case NB_HEURES:
                return getType12TabCell(id, qte, motif, commentaire, status, "Nombre d'heures :", p.getTitre());
            case NB_INDEMNITES:
                return getType12TabCell(id, qte, motif, commentaire, status, "Nombre d'indemnités :", p.getTitre());
            case PERIODE_HEURES:
                return getType3TabCell(id, "check", qte.equals("1"), p.getHeureDebut(), p.getHeureFin(), motif, commentaire, status + "<br>", p.getTitre());
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
        ret.append("<tr><td><CENTER>" + status + " " + (status.equals("Saisi") ? " <img src='images/suppression.gif' height='16px' width='16px' onClick=\"suppr('" + id + "')\">" : "<img src='images/vide.png' height='16px' width='16px'>") + "</CENTER></TD></tr>");
        return ret.toString();
    }

    /**
     * private String getLibelleCell(String type, String title) { switch
     * (TypeSaisieEnum.valueOf(type)) { case CASE_A_COCHER: return
     * getLibelleTypCell(title, "Accordée ?"); case NB_HEURES: return
     * getLibelleTypCell(title, "Nombre d'indemnités"); case NB_INDEMNITES:
     * return getLibelleTypCell(title, "Nombre d'heures"); case PERIODE_HEURES:
     * return getLibelleTypCell(title, "Début / Fin"); } return "type indéfini";
     * }
     *
     * private String getLibelleTypCell(String title, String spc) {
     * StringBuilder ret = new StringBuilder(); ret.append("<td><table
     * cellpadding='0' cellspacing='0' border='0' class='display'>");
     * ret.append("<tr bgcolor='#20B2AA'><td><b>" + title + "</td></b></tr>");
     * ret.append("<tr bgcolor='#00CDCD'><td>" + spc + "</td></tr>");
     * ret.append("<tr bgcolor='#B2DFEE'><td><input type='text' length='50px'
     * disabled value='Motif'></td></tr>"); ret.append("<tr
     * bgcolor='#BFEFFF'><td><input type='text' length='50px' disabled
     * value='Commentaire'></td></tr>"); ret.append("</table></td>"); return
     * ret.toString(); }*
     */
    private String getType0TabCell(String id, boolean check, String motif, String comment, String status, String title) {
        StringBuilder ret = new StringBuilder();
        ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type0TabCell" + id + "'>");
        ret.append(getHead(id, status, title));
        ret.append("<tr bgcolor='#BFEFFF'><td><input type='checkbox' name='NOM_chk_" + id + "'" + (check ? "checked" : "") + "> accordée</td></tr>");
        ret.append(commonFields(id, motif, comment));
        ret.append("</table></td>");

        addZone("acc_" + id, "" + check);
        return ret.toString();
    }

    private String getType12TabCell(String id, String nbr, String motif, String comment, String status, String label, String title) {
        StringBuilder ret = new StringBuilder();
        ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id + "'>");
        ret.append(getHead(id, status, title));
        ret.append("<tr bgcolor='#BFEFFF'><td>" + label + "<input type='text' size='4' name='NOM_nbr_" + id + "' value='" + nbr + "'></td></tr>");
        ret.append(commonFields(id, motif, comment));
        ret.append("</table></td>");
        addZone("nbr_" + id, "" + nbr);
        return ret.toString();
    }

    private String getType3TabCell(String id, String checkname, boolean check, Date heureDebut, Date heureFin, String motif, String comment, String status, String title) {
        StringBuilder ret = new StringBuilder();
        ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id + "'>");
        ret.append(getHead(id, status, title));
        ret.append("<tr bgcolor='#BFEFFF'><td> Heure début  -->   Heure fin <br><select name='NOM_time_" + id + "_D" + "'>" + getTimeCombo(heureDebut) + " </select>  /  <select name='NOM_time_" + id + "_F" + "'>" + getTimeCombo(heureFin) + " </select></td></tr>");
        ret.append("<tr bgcolor='#BFEFFF'><td><input type='checkbox' name='NOM_chk_" + id + "'" + (check ? "checked" : "") + ">" + checkname + "</td></tr>");
        ret.append(commonFields(id, motif, comment));
        ret.append("</table></td>");
        return ret.toString();
    }

    private String commonFields(String id, String motif, String comment) {
        StringBuilder ret = new StringBuilder();
        motif = motif.equals("null") ? "" : motif;
        comment = comment.equals("null") ? "" : comment;
        ret.append("<tr bgcolor='#BFEFFF'><td><INPUT type='text' class=\"sigp2-saisie\"  length='50px' name='NOM_motif_" + id + "' value='" + motif + "' title='Zone de saisie du motif'></td></tr>");
        ret.append("<tr bgcolor='#BFEFFF'><td><textarea  cols='15' rows='3' name='NOM_comm_" + id + "' title='Zone de saisie du commentaire'>" + comment + "</textarea></td></tr>");
        return ret.toString();
    }

    private String getTimeCombo(Date heure) {
        StringBuilder ret = new StringBuilder();
        heure = heure == null ? new Date() : heure;
        ret.append("<option value=''></option>");
        DateFormat df = new SimpleDateFormat("HH:mm");
        String seleted = df.format(heure);
        String val = "";
        for (int hours = 5; hours <= 23; hours++) {
            for (int min = 0; min < 60; min += 15) {
                val = hours + ":" + min;
                if (min == 0) {
                    val += "0";
                }
                if (hours < 10) {
                    val = "0" + val;
                }
                ret.append("<option value='" + val + "'" + (seleted.equals(val) ? "selected" : "") + ">" + val + "</option>");
            }
        }
        return ret.toString();
    }
}
