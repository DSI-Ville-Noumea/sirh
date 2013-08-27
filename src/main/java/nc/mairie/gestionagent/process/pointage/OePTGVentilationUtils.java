/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import nc.mairie.gestionagent.dto.VentilAbsenceDto;
import nc.mairie.gestionagent.dto.VentilHSupDto;
import nc.mairie.gestionagent.dto.VentilPrimeDto;
import static nc.mairie.gestionagent.process.pointage.RefTypePointageEnum.ABSENCE;
import static nc.mairie.gestionagent.process.pointage.RefTypePointageEnum.H_SUP;
import static nc.mairie.gestionagent.process.pointage.RefTypePointageEnum.PRIME;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.ws.SirhPtgWSConsumer;

/**
 *
 *
 */
public class OePTGVentilationUtils {

    public static String getTabVisu(Map<Integer, AgentNW> agents, int date, int typePointage) {


        SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");

        GregorianCalendar greg = new GregorianCalendar();

        StringBuilder agentsCsv = new StringBuilder();
        for (Integer ag : agents.keySet()) {
            agentsCsv.append(ag).append(",");
        }

        if (agents.size() > 0) {
            agentsCsv.deleteCharAt(agentsCsv.length() - 1);
        }
        StringBuilder sb = new StringBuilder();
        SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
        sb.append("<table cellpadding='0' cellspacing='0' border='0' class='display' id='VentilationTable'>");


        AgentNW agent;
        switch (RefTypePointageEnum.getRefTypePointageEnum(typePointage)) {
            case H_SUP: {
                sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>A récupérer</th><th>Heures complémentairees</th><th>HS 25%</th><th>HS 50%</th><th>HNU</th><th>HDJF 20%</th><th>HDJF 50%</th><th>HMAI</th><th>loupe</th></tr></thead>");
                List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, agentsCsv.toString(), date, typePointage);
               // System.out.println("VentilHSupDto rep.size()=" + rep.size());
                sb.append("<tbody>");
                for (VentilHSupDto hsup : rep) {
                    greg.setTime(hsup.getDate_lundi());
                    agent = agents.get(hsup.getId_agent());
                    sb.append("<tr><td>" + hsup.getId_agent() + "</td><td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(hsup.getDate_lundi()) + "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + (hsup.getM_normales() - hsup.getM_recuperees()) + "</td><td>" + hsup.getM_complementaires() + "</td><td>" + hsup.getM_sup_25() + "</td><td>" + hsup.getM_sup_50() + "</td><td>" + hsup.getM_nuit() + "</td><td>" + hsup.getM_djf_25() + "</td><td>" + hsup.getM_djf_50() + "</td><td>" + hsup.getM_1_mai() + "</td><td>loupe</td></tr>");
                }
                sb.append("</tbody>");
                break;
            }
            case ABSENCE: {
                sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>Minutes concertées</th><th>Minutes non concertées</th><th>loupe</th></tr></thead>");
                List<VentilAbsenceDto> rep = consum.getVentilations(VentilAbsenceDto.class, agentsCsv.toString(), date, typePointage);
            //    System.out.println("VentilAbsenceDto rep.size()=" + rep.size());
                sb.append("<tbody>");
                for (VentilAbsenceDto abs : rep) {
                    greg.setTime(abs.getDate_lundi());
                    agent = agents.get(abs.getId_agent());
                    sb.append("<tr><td>" + abs.getId_agent() + "</td><td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(abs.getDate_lundi()) + "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + abs.getMinutes_concertees() + "</td><td>" + abs.getMinutes_non_concertees() + "</td><td>loupe</td></tr>");
                }
                sb.append("</tbody>");
                break;
            }
            case PRIME: {
                sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>Primes</th><th><th>Nombre</th><th>loupe</th></tr></thead>");
                List<VentilPrimeDto> rep = consum.getVentilations(VentilPrimeDto.class, agentsCsv.toString(), date, typePointage);
             //   System.out.println("VentilPrimeDto rep.size()=" + rep.size());
                sb.append("<tbody>");
                for (VentilPrimeDto prime : rep) {
                    greg.setTime(prime.getDate_debut_mois());
                    agent = agents.get(prime.getId_agent());
                    sb.append("<tr><td>" + prime.getId_agent() + "</td><td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(prime.getDate_debut_mois()) + "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + prime.getId_ref_prime() + "</td><td>" + prime.getQuantite() + "</td><td>loupe</td></tr>");
                }
                sb.append("</tbody>");
                break;
            }
        }
        //<INPUT title="Editer le pointage" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/modifier.gif" height="16px" width="16px" title="Voir l'historique du pointage" name="<%=process.getSAISIE_PTG(indicePtg)%>"> <%=process.getVAL_ST_AGENT(indicePtg)%>    <img	src="images/loupe.gif" height="16px" width="16px"	onClick="loadPointageHistory('<%=process.getValHistory(indicePtg)%>', '<%=process.getHistory(indicePtg)%>')">          
        sb.append("</table>");
        //    return "tab for contractuel:" + RefTypePointageEnum.getRefTypePointageEnum(typePointage);
        return sb.toString();
    }
}
