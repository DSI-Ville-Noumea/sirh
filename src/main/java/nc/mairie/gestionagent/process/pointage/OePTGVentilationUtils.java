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
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.Transaction;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.PtgService;

import org.springframework.context.ApplicationContext;

/**
 *
 *
 */
public class OePTGVentilationUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private static IPtgService ptgService;

	public static String getTabVisu(Transaction aTransaction, int date, int typePointage, String agentsJson,
			AgentDao agentDao, boolean allVentilation, IPtgService ptgService) throws Exception {

		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");

		StringBuilder sb = new StringBuilder();
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
				List<VentilPrimeDto> rep = ptgService.getVentilations(VentilPrimeDto.class, date, typePointage,
						agentsJson, allVentilation);
				sb.append("<tbody>");
				
				List<RefPrimeDto> listRefPrimeDto = new ArrayList<RefPrimeDto>();
				for (VentilPrimeDto prime : rep) {
					RefPrimeDto primeDetail = ptgService.getPrimeDetailFromRefPrimeOptimise(listRefPrimeDto, prime.getIdRefPrime());
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
		String res = Const.CHAINE_VIDE;
		if (heure > 0)
			res += heure + "h";
		if (minute > 0)
			res += minute + "m";

		return res;
	}
	
	/**
	 * #19323 
	 * @param nombreMinute int
	 * @return String
	 */
	public static String getHeureMinuteWithAffichageZero(int nombreMinute) {
		int heure = nombreMinute / 60;
		int minute = nombreMinute % 60;
		String res = Const.CHAINE_VIDE;
		if (heure > 0)
			res += heure + "h";
		if (minute > 0)
			res += minute + "m";
		
		if(Const.CHAINE_VIDE.equals(res))
			res = "0h";
		
		return res;
	}

	public static boolean canProcessDeversementPaie(String statut) {
		return getPtgService().isValidAvailable(statut);
	}

	public static boolean canProcessVentilation(String statut) {
		return getPtgService().isVentilAvailable(statut);
	}

	public static boolean isDeversementEnCours(String statut) {
		return getPtgService().isValidEnCours(statut);
	}

	public static boolean isVentilationEnCours(String statut) {
		return getPtgService().isVentilEnCours(statut);
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

	public static String getTabErreurVentil(String type, IPtgService ptgService) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		StringBuilder sb = new StringBuilder();

		ArrayList<VentilErreurDto> listeErreurs = (ArrayList<VentilErreurDto>) ptgService.getErreursVentilation(type);
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

	public static IPtgService getPtgService() {
		if (null == ptgService) {
			ApplicationContext context = ApplicationContextProvider.getContext();
			ptgService = (PtgService) context.getBean("ptgService");
		}
		return ptgService;
	}
}
