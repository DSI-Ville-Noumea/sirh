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
		sb.append("<table id='VentilationTable'>");

		AgentNW agent;

		switch (RefTypePointageEnum.getRefTypePointageEnum(typePointage)) {
			case H_SUP: {
				if (titulaire) {
					sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>ABS</th><th>CH</th><th>HCO</th><th>HS</th><th>HSDJF</th><th>HSJO</th><th>HSI</th><th>HSNU</th><th>loupe</th></tr></thead>");
					List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, date, typePointage,
							agentsJson);
					sb.append("<tbody>");
					for (VentilHSupDto hsup : rep) {
						greg.setTime(hsup.getDateLundi());
						agent = AgentNW.chercherAgent(aTransaction, String.valueOf(hsup.getId_agent()));
						sb.append("<tr><td>" + hsup.getId_agent() + "</td><td>" + agent.getNomAgent() + " "
								+ agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(hsup.getDateLundi())
								+ "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + hsup.getMabs()
								+ "</td><td>" + "contingent h hebdo" + "</td><td>" + hsup.getmHorsContrat()
								+ "</td><td>" + hsup.getmComposees() + "</td><td>" + hsup.getmNormales() + "</td><td>"
								+ hsup.getmSup() + "</td><td>" + hsup.getmDjf() + "</td><td>" + hsup.getmSup()
								+ "</td><td>" + hsup.getmSimples() + "</td><td>" + hsup.getmNuit()
								+ "</td><td>loupe</td></tr>");
					}
					sb.append("</tbody>");
					break;
				} else {
					sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>ABS</th><th>CH</th><th>HC</th><th>HCE</th><th>HDJF</th><th>HDJF25</th><th>HDJF50</th><th>HMAI</th><th>HNU</th><th>HS</th><th>HS25</th><th>HS50</th><th>loupe</th></tr></thead>");
					List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, date, typePointage,
							agentsJson);
					sb.append("<tbody>");
					for (VentilHSupDto hsup : rep) {
						greg.setTime(hsup.getDateLundi());
						agent = AgentNW.chercherAgent(aTransaction, String.valueOf(hsup.getId_agent()));
						sb.append("<tr><td>" + hsup.getId_agent() + "</td><td>" + agent.getNomAgent() + " "
								+ agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(hsup.getDateLundi())
								+ "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + hsup.getMabs()
								+ "</td><td>" + "contingent h hebdo" + "</td><td>" + hsup.getmHorsContrat()
								+ "</td><td>" + hsup.getmComplementaires() + "</td><td>" + hsup.getmDjf() + "</td><td>"
								+ hsup.getmDjf25() + "</td><td>" + hsup.getmDjf50() + "</td><td>" + hsup.getM1Mai()
								+ "</td><td>" + hsup.getmNuit() + "</td><td>" + hsup.getmSup() + "</td><td>"
								+ hsup.getmSup25() + "</td><td>" + hsup.getmSup50() + "</td><td>loupe</td></tr>");
					}
					sb.append("</tbody>");
					break;
				}
			}
			case ABSENCE: {
				sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>Minutes concertées</th><th>Minutes non concertées</th><th>loupe</th></tr></thead>");
				List<VentilAbsenceDto> rep = consum.getVentilations(VentilAbsenceDto.class, date, typePointage,
						agentsJson);
				sb.append("<tbody>");
				for (VentilAbsenceDto abs : rep) {
					greg.setTime(abs.getDateLundi());
					agent = AgentNW.chercherAgent(aTransaction, String.valueOf(abs.getId_agent()));
					sb.append("<tr><td>" + abs.getId_agent() + "</td><td>" + agent.getNomAgent() + " "
							+ agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(abs.getDateLundi()) + "</td><td>"
							+ greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + abs.getMinutesConcertees() + "</td><td>"
							+ abs.getMinutesNonConcertees() + "</td><td>loupe</td></tr>");
				}
				sb.append("</tbody>");
				break;
			}
			case PRIME: {
				sb.append("<thead><tr><th>Matricule Agent</th><th>Nom prénom</th><th>Mois-année</th><th>Semaine</th><th>Primes</th><th>Nombre</th><th>loupe</th></tr></thead>");
				List<VentilPrimeDto> rep = consum.getVentilations(VentilPrimeDto.class, date, typePointage, agentsJson);
				sb.append("<tbody>");
				for (VentilPrimeDto prime : rep) {
					greg.setTime(prime.getDateDebutMois());
					RefPrimeDto primeDetail = consum.getPrimeDetailFromRefPrime(prime.getIdRefPrime());
					agent = AgentNW.chercherAgent(aTransaction, String.valueOf(prime.getId_agent()));
					sb.append("<tr><td>" + prime.getId_agent() + "</td><td>" + agent.getNomAgent() + " "
							+ agent.getPrenomAgent() + "</td><td>" + moisAnnee.format(prime.getDateDebutMois())
							+ "</td><td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td><td>" + primeDetail.getLibelle()
							+ "</td><td>" + prime.getQuantite() + "</td><td>loupe</td></tr>");
				}
				sb.append("</tbody>");
				break;
			}
		}
		// <INPUT title="Editer le pointage" type="image"
		// class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
		// src="images/modifier.gif" height="16px" width="16px"
		// title="Voir l'historique du pointage"
		// name="<%=process.getSAISIE_PTG(indicePtg)%>">
		// <%=process.getVAL_ST_AGENT(indicePtg)%> <img src="images/loupe.gif"
		// height="16px" width="16px"
		// onClick="loadPointageHistory('<%=process.getValHistory(indicePtg)%>', '<%=process.getHistory(indicePtg)%>')">
		sb.append("</table>");
		// return "tab for contractuel:" +
		// RefTypePointageEnum.getRefTypePointageEnum(typePointage);
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
