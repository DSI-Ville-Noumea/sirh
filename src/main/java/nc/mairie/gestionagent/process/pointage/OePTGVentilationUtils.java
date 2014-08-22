/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;
import nc.mairie.gestionagent.pointage.dto.VentilPrimeDto;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.Transaction;

/**
 *
 *
 */
public class OePTGVentilationUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public static String getTabVisu(Transaction aTransaction, int date, int typePointage, String agentsJson,
			AgentDao agentDao) throws Exception {

		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");

		StringBuilder sb = new StringBuilder();
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		sb.append("<table  class=\"display\"  id=\"VentilationTable\">");

		Agent agent;
		// sb.append("<td><INPUT title='Editer le pointage' type='image' class='<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, \"\")%>' src='images/modifier.gif' height='16px' width='16px' name='<%=process.getSAISIE_PTG(indicePtg)%>'></td>");

		switch (RefTypePointageEnum.getRefTypePointageEnum(typePointage)) {
			case PRIME: {
				sb.append("<thead><tr>");
				sb.append("<th>Matricule Agent</th>");
				sb.append("<th>Nom prénom</th>");
				sb.append("<th>Mois-année</th>");
				sb.append("<th>Primes</th>");
				sb.append("<th>Nombre</th>");
				sb.append("</tr></thead>");
				List<VentilPrimeDto> rep = consum.getVentilations(VentilPrimeDto.class, date, typePointage, agentsJson);
				sb.append("<tbody>");
				for (VentilPrimeDto prime : rep) {
					RefPrimeDto primeDetail = consum.getPrimeDetailFromRefPrime(prime.getIdRefPrime());
					agent = agentDao.chercherAgent(prime.getId_agent());
					sb.append("<tr>");
					sb.append("<td>" + agent.getNomatr() + "</td>");
					sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
					sb.append("<td>" + moisAnnee.format(prime.getDateDebutMois()) + "</td>");
					sb.append("<td>" + primeDetail.getLibelle() + "</td>");
					sb.append("<td>" + prime.getQuantite() + "</td>");
					sb.append("</tr>");
				}
				sb.append("</tbody>");
				break;
			}
			default:
				break;
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String getHeureMinute(int nombreMinute) {
		int heure = nombreMinute / 60;
		int minute = nombreMinute % 60;
		String res = "";
		if (heure > 0)
			res += heure + "h";
		if (minute > 0)
			res += minute + "m";

		return res;
	}

	public static boolean canProcessDeversementPaie(String statut) {
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		return consum.isValidAvailable(statut);
	}

	public static boolean canProcessVentilation(String statut) {
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		return consum.isVentilAvailable(statut);
	}

	public static boolean isDeversementEnCours(String statut) {
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		return consum.isValidEnCours(statut);
	}

	public static boolean isVentilationEnCours(String statut) {
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();
		return consum.isVentilEnCours(statut);
	}

	public static String getMondayFromWeekNumberAndYear(int week, int year) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
		// cal.setTime(listePointage.get(idPtg).getDate());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week); // back to previous week
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // jump to next monday.
		return sdf.format(cal.getTime());
	}

	public static String getTabErreurVentil(String type) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		StringBuilder sb = new StringBuilder();
		SirhPtgWSConsumer consum = new SirhPtgWSConsumer();

		ArrayList<VentilErreurDto> listeErreurs = (ArrayList<VentilErreurDto>) consum.getErreursVentilation(type);
		sb.append("<table class=\"sigp2NewTab\" style=\"text-align:left;width:980px;\"> ");

		sb.append("<tr>");
		sb.append("<td class=\"sigp2NewTab-liste\" style=\"width:100px;\" > Agent en erreur </td>");
		sb.append("<td class=\"sigp2NewTab-liste\" style=\"width:90px;\" > Date ventilation </td>");
		sb.append("<td class=\"sigp2NewTab-liste\" >Erreur</td>");
		sb.append("</tr>");

		for (VentilErreurDto dto : listeErreurs) {
			sb.append("<tr>");
			sb.append("<td class=\"sigp2NewTab-liste\" style=\"width:100px;\" >"
					+ dto.getIdAgent().toString().substring(3, dto.getIdAgent().toString().length()) + "</td>");
			sb.append("<td class=\"sigp2NewTab-liste\" style=\"width:90px;\" >" + sdf.format(dto.getDateCreation())
					+ "</td>");
			sb.append("<td class=\"sigp2NewTab-liste\">" + dto.getTaskStatus() + "</td>");
			sb.append("</tr>");
		}

		sb.append("</table>");
		return sb.toString();

	}

	public static double roundDecimal(double nombre, int precision) {
		double tmp = Math.pow(10, precision);
		return Math.round(nombre * tmp) / tmp;
	}
}
