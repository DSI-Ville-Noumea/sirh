package nc.mairie.gestionagent.process.pointage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AbsenceDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;
import nc.mairie.gestionagent.dto.FichePointageDto;
import nc.mairie.gestionagent.dto.HeureSupDto;
import nc.mairie.gestionagent.dto.JourPointageDto;
import nc.mairie.gestionagent.dto.PrimeDto;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

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
    private HashMap<String, List<AbsenceDto>> absences = new HashMap<>();
    private HashMap<String, List<HeureSupDto>> hsups = new HashMap<>();
    public static final String VALIDATION = "validation page saisie";
    public static final String BACK = "back sur page saisie";
    public static final String DATE_FORMAT = "EEEE dd MMMM yyyy";
    private AgentNW loggedAgent;
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public String getJSP() {
        return "OePTGSaisie.jsp";
    }

    /**
     * Getter du nom de l'�cran (pour la gestion des droits)
     */
    public String getNomEcran() {
        return "ECR-PTG-SAISIE";
    }

    public void initParams() {
        setIdAgent((String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_PTG));
        setDateLundi((String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LUNDI_PTG));
    }

    /**
     * Initialisation des donn�es.
     */
    private void initialiseDonnees() throws Exception {
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();
        FichePointageDto listeFichePointage = t.getSaisiePointage(idAgent, Services.convertitDate(getDateLundiStr(0), DATE_FORMAT, "yyyyMMdd"));
        absences.clear();
        primess.clear();
        hsups.clear();
        for (JourPointageDto jour : listeFichePointage.getSaisies()) {
            primess.add(jour.getPrimes());
            absences.put(sdf.format(jour.getDate()), jour.getAbsences());
            hsups.put(sdf.format(jour.getDate()), jour.getHeuresSup());
        }
        // TODO
    }

    @Override
    public void initialiseZones(HttpServletRequest request) throws Exception { // POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
        VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
        // ----------------------------------//
        // V�rification des droits d'acc�s. //
        // ----------------------------------//
        if (MairieUtils.estInterdit(request, getNomEcran())) { // "ERR190",
            // "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
            getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
            throw new Exception();
        }
        initParams();
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

    public boolean performPB_VALID(HttpServletRequest request) throws Exception {
        // TODO
        return true;
    }

    public boolean performPB_BACK(HttpServletRequest request) throws Exception {
        setStatut(STATUT_PROCESS_APPELANT);
        return true;
    }

    private void changeState(ConsultPointageDto ptg, EtatPointageEnum state) {
        ArrayList<ConsultPointageDto> param = new ArrayList<>();
        param.add(ptg);
        changeState(param, state);
    }

    private void changeState(Collection<ConsultPointageDto> ptg, EtatPointageEnum state) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (ConsultPointageDto pt : ptg) {
            ids.add(pt.getIdPointage());
        }
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();
        if (loggedAgent == null) {
            System.out.println("Agent compl�tement nul!");
        } else {
            t.setPtgState(ids, state.ordinal(), loggedAgent.getIdAgent());
            // refresh?
        }
    }

    @Override
    public boolean recupererStatut(HttpServletRequest request) throws Exception {
        // Si on arrive de la JSP alors on traite le get
        if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
            if (testerParametre(request, VALIDATION)) {
                return performPB_VALID(request);
            }

            if (testerParametre(request, BACK)) {
                return performPB_BACK(request);
            }
        }
        // Si TAG INPUT non g�r� par le process
        setStatut(STATUT_MEME_PROCESS);
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

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
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
            System.out.println("ParseException in OePTGSaisie setDateLundi");
        }
    }

    public String getHeaderTable() {
        StringBuilder ret = new StringBuilder();
        ret.append("<tr>");//<th>Intitul�</th>");
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
            //  ret.append(getLibelleCell(primess.get(0).get(i)));
            for (List<PrimeDto> pl : primess) {
                ret.append(getCell(pl.get(i)));
            }
            ret.append("</tr>");
        }
        return ret.toString();
    }

    public String getHSTab() {
        StringBuilder ret = new StringBuilder();
        String id = "HS";
        ret.append(getLineTitle("Heures Suppl�mentaires"));
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
                    System.out.println("hs:" + id + i + ":" + j + "   - " + hs.getHeureDebut() + " " + hs.getHeureFin() + " " + hs.getMotif() + " " + hs.getCommentaire());
                    ret.append(getType3TabCell(id + i + ":" + j, "A r�cup�rer", hs.getRecuperee(), hs.getHeureDebut(), hs.getHeureFin(), hs.getMotif(), hs.getCommentaire(), status, ""));
                } else {
                    ret.append(getType3TabCell(id + i + ":" + j, "A r�cup�rer", false, bidon, bidon, "", "", "", ""));
                }
            }
            ret.append("</tr>");
        }
        return ret.toString();
        // return getType3TabLine("HS0", "Heures Suppl�mentaires", "A r�cup�rer") + getType3TabLine("HS1", "Heures Suppl�mentaires", "A r�cup�rer");
    }

    public String getAbsTab() {
        StringBuilder ret = new StringBuilder();
        String id = "ABS";
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
                    System.out.println("abs:" + id + i + ":" + j + "   - " + abs.getHeureDebut() + " " + abs.getHeureFin() + " " + abs.getMotif() + " " + abs.getCommentaire());
                    String status = abs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(abs.getIdRefEtat()) : "";
                    ret.append(getType3TabCell(id + i + ":" + j, "Concert�e", abs.getConcertee(), abs.getHeureDebut(), abs.getHeureFin(), abs.getMotif(), abs.getCommentaire(), status, ""));
                } else {
                    ret.append(getType3TabCell(id + i + ":" + j, "Concert�e", false, bidon, bidon, "", "", "", ""));
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
    private String getCell(PrimeDto p) {
        String id = p.getNumRubrique() + ":" + p.getIdPointage();
        String motif = p.getMotif() != null ? p.getMotif() : "";
        String commentaire = p.getCommentaire() != null ? p.getCommentaire() : "";
        String qte = p.getQuantite() != null ? "" + p.getQuantite() : "";
        int idref = p.getIdRefEtat() != null ? p.getIdRefEtat() : 0;
        String status = p.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(idref) : "";
        switch (TypeSaisieEnum.valueOf(p.getTypeSaisie())) {
            case CASE_A_COCHER:
                return getType0TabCell(id, qte.equals("1"), motif, commentaire, status, p.getTitre());
            case NB_HEURES:
                return getType12TabCell(id, qte, motif, commentaire, status, "Nombre d'heures :", p.getTitre());
            case NB_INDEMNITES:
                return getType12TabCell(id, qte, motif, commentaire, status, "Nombre d'indemnit�s :", p.getTitre());
            case PERIODE_HEURES:
                return getType3TabCell(id, "check", false, p.getHeureDebut(), p.getHeureFin(), motif, commentaire, status + "<br>", p.getTitre());
            default:
        }
        return "failcell:" + id;
    }

    private String getLineTitle(String title) {
        return "<tr bgcolor='#009ACD'><TD colspan=8 align=center><b><H4>" + title + "<H3></b></TD></tr>";
    }

    private String getHead(String id, String status, String title) {
        StringBuilder ret = new StringBuilder();
        ret.append("<TR> <td ><CENTER><b>" + title + " </b>" + status + "<br>" + (status.equals("Saisi") ? " <img src='images/suppression.gif' height='16px' width='16px' onClick=\"suppr('" + id + "')\">" : "") + "</CENTER></td></TR> ");
        return ret.toString();
    }

    /**
     * private String getLibelleCell(String type, String title) { switch
     * (TypeSaisieEnum.valueOf(type)) { case CASE_A_COCHER: return
     * getLibelleTypCell(title, "Accord�e ?"); case NB_HEURES: return
     * getLibelleTypCell(title, "Nombre d'indemnit�s"); case NB_INDEMNITES:
     * return getLibelleTypCell(title, "Nombre d'heures"); case PERIODE_HEURES:
     * return getLibelleTypCell(title, "D�but / Fin"); } return "type ind�fini";
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
        //System.out.println("cell:" + id + " " + check + " " + motif + " " + comment);
        StringBuilder ret = new StringBuilder();
        ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type0TabCell" + id + "'>");
        ret.append(getHead(id, status, title + "<br>"));
        ret.append("<tr bgcolor='#5CACEE'><td><input type='checkbox' name='acc_" + id + "'" + (check ? "checked" : "") + "> accord�e</td></tr>");
        ret.append(commonFields(id, motif, comment));
        ret.append("</table></td>");
        return ret.toString();
    }

    private String getType12TabCell(String id, String nbr, String motif, String comment, String status, String label, String title) {
        StringBuilder ret = new StringBuilder();
        ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id + "'>");
        ret.append(getHead(id, status, title + "<br>"));
        ret.append("<tr bgcolor='#5CACEE'><td>" + label + "<input type='text' size='4' name='nbr_" + id + "' value='" + nbr + "'></td></tr>");
        ret.append(commonFields(id, motif, comment));
        ret.append("</table></td>");
        return ret.toString();
    }

    private String getType3TabCell(String id, String checkname, boolean check, Date heureDebut, Date heureFin, String motif, String comment, String status, String title) {
        StringBuilder ret = new StringBuilder();
        ret.append("<td><table cellpadding='0' cellspacing='0' border='0' class='display' id='Type1-2TabCell" + id + "'>");
        ret.append(getHead(id, status, title));
        ret.append("<tr bgcolor='#5CACEE'><td> Heure d�but  -->   Heure fin <br><select name='TIME_" + id + "_D" + "'>" + getTimeCombo(heureDebut) + " </select>  /  <select name='TIME_" + id + "_F" + "'>" + getTimeCombo(heureFin) + " </select></td></tr>");
        ret.append("<tr bgcolor='#5CBBEE'><td><input type='checkbox' name='chk_" + id + "'" + (check ? "checked" : "") + ">" + checkname + "</td></tr>");
        ret.append(commonFields(id, motif, comment));
        ret.append("</table></td>");
        return ret.toString();
    }

    private String commonFields(String id, String motif, String comment) {
        StringBuilder ret = new StringBuilder();
        motif = motif.equals("null") ? "" : motif;
        comment = comment.equals("null") ? "" : comment;
        ret.append("<tr bgcolor='#B2DFEE'><td><input type='text' length='50px' name='motif_" + id + "' value='" + motif + "' title='Zone de saisie du motif'></td></tr>");
        ret.append("<tr bgcolor='#BFEFFF'><td><textarea  cols='15' rows='3' name='comm_" + id + "' title='Zone de saisie du commentaire'>" + comment + "</textarea></td></tr>");
        return ret.toString();
    }

    private String getTimeCombo(Date heure) {
        StringBuilder ret = new StringBuilder();
        ret.append("<option value=''></option>");
        DateFormat df = new SimpleDateFormat("HH:mm");
        String seleted = df.format(heure);
        String val = "";
        for (int hours = 5; hours <= 22; hours++) {
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
        ret.append("<option value='23:00'>23:00</option>");
        return ret.toString();
    }
}
