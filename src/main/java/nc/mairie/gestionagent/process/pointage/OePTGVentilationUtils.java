/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import nc.mairie.gestionagent.dto.RefPrimeDto;
import nc.mairie.gestionagent.dto.VentilAbsenceDto;
import nc.mairie.gestionagent.dto.VentilHSupDto;
import nc.mairie.gestionagent.dto.VentilPrimeDto;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.Transaction;

/**
 *
 *
 */
public class OePTGVentilationUtils {

	public static String getTabVisu(Transaction aTransaction, int date, int typePointage, boolean titulaire,
			String agentsJson) throws Exception {

		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
		GregorianCalendar greg = new GregorianCalendar();

		StringBuilder sb = new StringBuilder();
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		sb.append("<table  class=\"display\"  id=\"VentilationTable\">");

		AgentNW agent;
		// sb.append("<td><INPUT title='Editer le pointage' type='image' class='<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, \"\")%>' src='images/modifier.gif' height='16px' width='16px' name='<%=process.getSAISIE_PTG(indicePtg)%>'></td>");

		switch (RefTypePointageEnum.getRefTypePointageEnum(typePointage)) {
			case H_SUP: {
				if (titulaire) {
					sb.append("<thead><tr>");
					sb.append("<th>Matricule Agent</th>");
					sb.append("<th>Nom prénom</th>");
					sb.append("<th>Mois-année</th>");
					sb.append("<th>Semaine</th>");
					sb.append("<th>ABS</th>");
					sb.append("<th>CH</th>");
					sb.append("<th>HC</th>");
					sb.append("<th>HCO</th>");
					sb.append("<th>HNO</th>");
					sb.append("<th>HS</th>");
					sb.append("<th>HSDJF</th>");
					sb.append("<th>HSJO</th>");
					sb.append("<th>HSI</th>");
					sb.append("<th>HSNU</th>");
					sb.append("<th>&nbsp;</th>");
					sb.append("</tr></thead>");
					List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, date, typePointage,
							agentsJson);
					sb.append("<tbody>");
					for (VentilHSupDto hsup : rep) {
						greg.setTime(hsup.getDateLundi());
						agent = AgentNW.chercherAgent(aTransaction, String.valueOf(hsup.getId_agent()));
						sb.append("<tr>");
						sb.append("<td>" + agent.getNoMatricule() + "</td>");
						sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
						sb.append("<td>" + moisAnnee.format(hsup.getDateLundi()) + "</td>");
						sb.append("<td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td>");
						sb.append("<td>" + hsup.getMabs() + "</td>");
						sb.append("<td>" + "contingent h hebdo" + "</td>");
						sb.append("<td>" + hsup.getmHorsContrat() + "</td>");
						sb.append("<td>" + hsup.getmComposees() + "</td>");
						sb.append("<td>" + hsup.getmNormales() + "</td>");
						sb.append("<td>" + hsup.getmSup() + "</td>");
						sb.append("<td>" + hsup.getmDjf() + "</td>");
						sb.append("<td>" + hsup.getmSup() + "</td>");
						sb.append("<td>" + hsup.getmSimples() + "</td>");
						sb.append("<td>" + hsup.getmNuit() + "</td>");
						sb.append("<td><img border=\"0\" src=\"images/loupe.gif\" width=\"16px\" height=\"16px\" style=\"cursor : pointer;\"></td>");
						sb.append("</tr>");
					}
					sb.append("</tbody>");
					break;
				} else {
					sb.append("<thead><tr>");
					sb.append("<th>Matricule Agent</th>");
					sb.append("<th>Nom prénom</th>");
					sb.append("<th>Mois-année</th>");
					sb.append("<th>Semaine</th>");
					sb.append("<th>ABS</th>");
					sb.append("<th>CH</th>");
					sb.append("<th>HC</th>");
					sb.append("<th>HCE</th>");
					sb.append("<th>HDJF</th>");
					sb.append("<th>HDJF25</th>");
					sb.append("<th>HDJF50</th>");
					sb.append("<th>HMAI</th>");
					sb.append("<th>HNU</th>");
					sb.append("<th>HS</th>");
					sb.append("<th>HS25</th>");
					sb.append("<th>HS50</th>");
					sb.append("<th>&nbsp;</th>");
					sb.append("</tr></thead>");
					List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, date, typePointage,
							agentsJson);
					sb.append("<tbody>");
					for (VentilHSupDto hsup : rep) {
						greg.setTime(hsup.getDateLundi());
						agent = AgentNW.chercherAgent(aTransaction, String.valueOf(hsup.getId_agent()));
						sb.append("<tr>");
						sb.append("<td>" + agent.getNoMatricule() + "</td>");
						sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
						sb.append("<td>" + moisAnnee.format(hsup.getDateLundi()) + "</td>");
						sb.append("<td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td>");
						sb.append("<td>" + hsup.getMabs() + "</td>");
						sb.append("<td>" + "contingent h hebdo" + "</td>");
						sb.append("<td>" + hsup.getmHorsContrat() + "</td>");
						sb.append("<td>" + hsup.getmComplementaires() + "</td>");
						sb.append("<td>" + hsup.getmDjf() + "</td>");
						sb.append("<td>" + hsup.getmDjf25() + "</td>");
						sb.append("<td>" + hsup.getmDjf50() + "</td>");
						sb.append("<td>" + hsup.getM1Mai() + "</td>");
						sb.append("<td>" + hsup.getmNuit() + "</td>");
						sb.append("<td>" + hsup.getmSup() + "</td>");
						sb.append("<td>" + hsup.getmSup25() + "</td>");
						sb.append("<td>" + hsup.getmSup50() + "</td>");
						sb.append("<td><img border=\"0\" src=\"images/loupe.gif\" width=\"16px\" height=\"16px\" style=\"cursor : pointer;\"></td>");
						sb.append("</tr>");
					}
					sb.append("</tbody>");
					break;
				}
			}
			case ABSENCE: {
				sb.append("<thead><tr>");
				sb.append("<th>Matricule Agent</th>");
				sb.append("<th>Nom prénom</th>");
				sb.append("<th>Mois-année</th>");
				sb.append("<th>Semaine</th>");
				sb.append("<th>Minutes concertées</th>");
				sb.append("<th>Minutes non concertées</th>");
				sb.append("<th>&nbsp;</th>");
				sb.append("</tr></thead>");
				List<VentilAbsenceDto> rep = consum.getVentilations(VentilAbsenceDto.class, date, typePointage,
						agentsJson);
				sb.append("<tbody>");
				for (VentilAbsenceDto abs : rep) {
					greg.setTime(abs.getDateLundi());
					agent = AgentNW.chercherAgent(aTransaction, String.valueOf(abs.getId_agent()));
					sb.append("<tr>");
					sb.append("<td>" + agent.getNoMatricule() + "</td>");
					sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
					sb.append("<td>" + moisAnnee.format(abs.getDateLundi()) + "</td>");
					sb.append("<td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td>");
					sb.append("<td>" + abs.getMinutesConcertees() + "</td>");
					sb.append("<td>" + abs.getMinutesNonConcertees() + "</td>");
					sb.append("<td><img border=\"0\" src=\"images/loupe.gif\" width=\"16px\" height=\"16px\" style=\"cursor : pointer;\"></td>");
					sb.append("</tr>");
				}
				sb.append("</tbody>");
				break;
			}
			case PRIME: {
				sb.append("<thead><tr>");
				sb.append("<th>Matricule Agent</th>");
				sb.append("<th>Nom prénom</th>");
				sb.append("<th>Mois-année</th>");
				sb.append("<th>Semaine</th>");
				sb.append("<th>Primes</th>");
				sb.append("<th>Nombre</th>");
				sb.append("<th>&nbsp;</th>");
				sb.append("</tr></thead>");
				List<VentilPrimeDto> rep = consum.getVentilations(VentilPrimeDto.class, date, typePointage, agentsJson);
				sb.append("<tbody>");
				for (VentilPrimeDto prime : rep) {
					greg.setTime(prime.getDateDebutMois());
					RefPrimeDto primeDetail = consum.getPrimeDetailFromRefPrime(prime.getIdRefPrime());
					agent = AgentNW.chercherAgent(aTransaction, String.valueOf(prime.getId_agent()));
					sb.append("<tr>");
					sb.append("<td>" + agent.getNoMatricule() + "</td>");
					sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
					sb.append("<td>" + moisAnnee.format(prime.getDateDebutMois()) + "</td>");
					sb.append("<td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td>");
					sb.append("<td>" + primeDetail.getLibelle() + "</td>");
					sb.append("<td>" + prime.getQuantite() + "</td>");
					sb.append("<td><img border=\"0\" src=\"images/loupe.gif\" width=\"16px\" height=\"16px\" style=\"cursor : pointer;\"></td>");
					sb.append("</tr>");
				}
				sb.append("</tbody>");
				break;
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String getTabValid(String statut) {
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		boolean ok = consum.isValidAvailable(statut);
		String method = ok ? "'available()'" : "'unavailable()'";
		return "      <INPUT type=\"submit\" class=\"sigp2-Bouton-100\" value=\"Valider\" onclick=" + method + ">";
	}

	public static boolean canProcessVentilation(String statut) {
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		return consum.isVentilAvailable(statut);
	}
}
