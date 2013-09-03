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
import nc.mairie.gestionagent.dto.RefPrimeDto;
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

    public static String getTabVisu(Map<Integer, AgentNW> agents, int date, int typePointage, boolean titulaire) {


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
        sb.append("<table id='VentilationTable'>");


        AgentNW agent;

        switch (RefTypePointageEnum.getRefTypePointageEnum(typePointage)) {
            case H_SUP: {
                if (titulaire) {
                    sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>ABS</th><th>CH</th><th>HCO</th><th>HS</th><th>HSDJF</th><th>HSJO</th><th>HSI</th><th>HSNU</th><th>loupe</th></tr></thead>");
                    List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, agentsCsv.toString(), date, typePointage);
                    sb.append("<tbody>");
                    for (VentilHSupDto hsup : rep) {
                        greg.setTime(hsup.getDate_lundi());
                        agent = agents.get(hsup.getId_agent());
                        sb.append("<tr><td>" + hsup.getId_agent() + "</td><td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(hsup.getDate_lundi()) + "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + hsup.getMabs() + "</td><td>" + "contingent h hebdo" + "</td><td>" + hsup.getM_hors_contrat() + "</td><td>" + hsup.getM_composees() + "</td><td>" + hsup.getM_normales() + "</td><td>" + hsup.getM_sup() + "</td><td>" + hsup.getM_djf() + "</td><td>" + hsup.getM_sup() + "</td><td>" + hsup.getM_simples() + "</td><td>" + hsup.getM_nuit() + "</td><td>loupe</td></tr>");
                    }
                    sb.append("</tbody>");
                    break;
                } else {
                    sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>ABS</th><th>CH</th><th>HC</th><th>HCE</th><th>HDJF</th><th>HDJF25</th><th>HDJF50</th><th>HMAI</th><th>HNU</th><th>HS</th><th>HS25</th><th>HS50</th><th>loupe</th></tr></thead>");
                    List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, agentsCsv.toString(), date, typePointage);
                    sb.append("<tbody>");
                    for (VentilHSupDto hsup : rep) {
                        greg.setTime(hsup.getDate_lundi());
                        agent = agents.get(hsup.getId_agent());
                        sb.append("<tr><td>" + hsup.getId_agent() + "</td><td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(hsup.getDate_lundi()) + "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + hsup.getMabs() + "</td><td>" + "contingent h hebdo" + "</td><td>" + hsup.getM_hors_contrat() + "</td><td>" + hsup.getM_complementaires() + "</td><td>" + hsup.getM_djf() + "</td><td>" + hsup.getM_djf_25() + "</td><td>" + hsup.getM_djf_50() + "</td><td>" + hsup.getM_1_mai() + "</td><td>" + hsup.getM_nuit() + "</td><td>" + hsup.getM_sup() + "</td><td>" + hsup.getM_sup_25() + "</td><td>" + hsup.getM_sup_50() + "</td><td>loupe</td></tr>");
                    }
                    sb.append("</tbody>");
                    break;
                }
            }
            case ABSENCE: {
                sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>Minutes concertées</th><th>Minutes non concertées</th><th>loupe</th></tr></thead>");
                List<VentilAbsenceDto> rep = consum.getVentilations(VentilAbsenceDto.class, agentsCsv.toString(), date, typePointage);
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
                sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>Primes</th><th>Nombre</th><th>loupe</th></tr></thead>");
                List<VentilPrimeDto> rep = consum.getVentilations(VentilPrimeDto.class, agentsCsv.toString(), date, typePointage);
                sb.append("<tbody>");
                for (VentilPrimeDto prime : rep) {
                    greg.setTime(prime.getDate_debut_mois());
                    RefPrimeDto primeDetail = consum.getPrimeDetailFromRefPrime(prime.getId_ref_prime());
                    agent = agents.get(prime.getId_agent());
                    sb.append("<tr><td>" + prime.getId_agent() + "</td><td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(prime.getDate_debut_mois()) + "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + primeDetail.getLibelle() + "</td><td>" + prime.getQuantite() + "</td><td>loupe</td></tr>");
                }
                sb.append("</tbody>");
                break;
            }
        }
        //<INPUT title="Editer le pointage" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/modifier.gif" height="16px" width="16px" title="Voir l'historique du pointage" name="<%=process.getSAISIE_PTG(indicePtg)%>"> <%=process.getVAL_ST_AGENT(indicePtg)%>    <img	src="images/loupe.gif" height="16px" width="16px"	onClick="loadPointageHistory('<%=process.getValHistory(indicePtg)%>', '<%=process.getHistory(indicePtg)%>')">          
        sb.append(
                "</table>");
        //    return "tab for contractuel:" + RefTypePointageEnum.getRefTypePointageEnum(typePointage);
        return sb.toString();
    }

    public static String getTabValid() {
        return "      <INPUT type=\"submit\" class=\"sigp2-Bouton-100\" value=\"Deverser\" onclick='unavailable()'>";
    }

    public static String getTabVentil() {
        return "      <INPUT type=\"submit\" class=\"sigp2-Bouton-100\" value=\"Ventiler\" onclick='unavailable()'>";
    }
}
