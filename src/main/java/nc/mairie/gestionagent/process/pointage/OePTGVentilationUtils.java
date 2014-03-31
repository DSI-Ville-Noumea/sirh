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

import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.pointage.dto.VentilAbsenceDto;
import nc.mairie.gestionagent.pointage.dto.VentilErreurDto;
import nc.mairie.gestionagent.pointage.dto.VentilHSupDto;
import nc.mairie.gestionagent.pointage.dto.VentilPrimeDto;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.carriere.BaseHoraire;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.Transaction;

/**
 *
 *
 */
public class OePTGVentilationUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public static String getTabVisu(Transaction aTransaction, int date, int typePointage, boolean titulaire,
			String agentsJson) throws Exception {

		SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
		SimpleDateFormat annee = new SimpleDateFormat("yyyy");
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
					sb.append("<th>Abs</th>");
					sb.append("<th>Base</th>");
					sb.append("<th>Total</th>");
					sb.append("<th>Complémentaires</th>");
					sb.append("<th>Simples</th>");
					sb.append("<th>Composées</th>");
					sb.append("<th>Nuit</th>");
					sb.append("<th>DJF</th>");
					sb.append("<th>Mai</th>");
					sb.append("<th>&nbsp;</th>");
					sb.append("</tr></thead>");
					List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, date, typePointage,
							agentsJson);
					sb.append("<tbody>");
					for (VentilHSupDto hsup : rep) {
						greg.setTime(hsup.getDateLundi());
						agent = AgentNW.chercherAgent(aTransaction, String.valueOf(hsup.getId_agent()));
						Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
						BaseHoraire baseHoraire = BaseHoraire.chercherBaseHoraire(aTransaction, carr.getCodeBase());
						Horaire tempsTravail = Horaire.chercherHoraire(aTransaction, carr.getCodeBaseHoraire2());
						double weekBase = Double.valueOf(baseHoraire.getNbashh().replace(",", "."))
								* Double.valueOf(tempsTravail.getCdTaux());
						sb.append("<tr>");
						sb.append("<td>" + agent.getNoMatricule() + "</td>");
						sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
						sb.append("<td>" + moisAnnee.format(hsup.getDateLundi()) + "</td>");
						sb.append("<td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getMabs()) + "</td>");
						sb.append("<td>" + roundDecimal(weekBase, 2) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmHorsContrat()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmNormales()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmSimples()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmComposees()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmNuit()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmDjf()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getM1Mai()) + "</td>");
						sb.append("<td><INPUT title='Editer le pointage correspondant' type='image' class='<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, \"\")%>' src='images/modifier.gif' height='16px' width='16px' name='JMP_SAISIE:"
								+ greg.get(Calendar.WEEK_OF_YEAR)
								+ ":"
								+ annee.format(hsup.getDateLundi())
								+ ":"
								+ agent.getNoMatricule() + "'></td>");
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
					sb.append("<th>Abs</th>");
					sb.append("<th>Base</th>");
					sb.append("<th>Total</th>");
					sb.append("<th>Complémentaires</th>");
					sb.append("<th>25%</th>");
					sb.append("<th>50%</th>");
					sb.append("<th>Nuit</th>");
					sb.append("<th>DJF</th>");
					sb.append("<th>Mai</th>");
					sb.append("<th>&nbsp;</th>");
					sb.append("</tr></thead>");
					List<VentilHSupDto> rep = consum.getVentilations(VentilHSupDto.class, date, typePointage,
							agentsJson);
					sb.append("<tbody>");
					for (VentilHSupDto hsup : rep) {
						greg.setTime(hsup.getDateLundi());
						agent = AgentNW.chercherAgent(aTransaction, String.valueOf(hsup.getId_agent()));
						Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(aTransaction, agent);
						BaseHoraire baseHoraire = BaseHoraire.chercherBaseHoraire(aTransaction, carr.getCodeBase());
						Horaire tempsTravail = Horaire.chercherHoraire(aTransaction, carr.getCodeBaseHoraire2());
						double weekBase = Double.valueOf(baseHoraire.getNbashh().replace(",", "."))
								* Double.valueOf(tempsTravail.getCdTaux());
						sb.append("<tr>");
						sb.append("<td>" + agent.getNoMatricule() + "</td>");
						sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
						sb.append("<td>" + moisAnnee.format(hsup.getDateLundi()) + "</td>");
						sb.append("<td>" + greg.get(Calendar.WEEK_OF_YEAR) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getMabs()) + "</td>");
						sb.append("<td>" + roundDecimal(weekBase, 2) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmHorsContrat()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmComplementaires()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmSup25()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmSup50()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmNuit()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getmDjf()) + "</td>");
						sb.append("<td>" + getHeureMinute(hsup.getM1Mai()) + "</td>");
						sb.append("<td><INPUT title='Editer le pointage correspondant' type='image' class='<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, \"\")%>' src='images/modifier.gif' height='16px' width='16px' name='JMP_SAISIE:"
								+ greg.get(Calendar.WEEK_OF_YEAR)
								+ ":"
								+ annee.format(hsup.getDateLundi())
								+ ":"
								+ agent.getNoMatricule() + "'></td>");
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
				sb.append("<th>Abs concertées</th>");
				sb.append("<th>Abs non concertées</th>");
				sb.append("<th>Abs immédiates</th>");
				sb.append("<th>Total</th>");
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
					sb.append("<td>" + getHeureMinute(abs.getMinutesConcertees()) + "</td>");
					sb.append("<td>" + getHeureMinute(abs.getMinutesNonConcertees()) + "</td>");
					sb.append("<td>" + getHeureMinute(abs.getMinutesImmediates()) + "</td>");
					sb.append("<td>"
							+ getHeureMinute(abs.getMinutesConcertees() + abs.getMinutesNonConcertees()
									+ abs.getMinutesImmediates()) + "</td>");
					sb.append("<td><INPUT title='Editer le pointage correspondant' type='image' class='<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, \"\")%>' src='images/modifier.gif' height='16px' width='16px' name='JMP_SAISIE:"
							+ greg.get(Calendar.WEEK_OF_YEAR)
							+ ":"
							+ annee.format(abs.getDateLundi())
							+ ":"
							+ agent.getNoMatricule() + "'></td>");
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
				sb.append("<th>Primes</th>");
				sb.append("<th>Nombre</th>");
				sb.append("</tr></thead>");
				List<VentilPrimeDto> rep = consum.getVentilations(VentilPrimeDto.class, date, typePointage, agentsJson);
				sb.append("<tbody>");
				for (VentilPrimeDto prime : rep) {
					RefPrimeDto primeDetail = consum.getPrimeDetailFromRefPrime(prime.getIdRefPrime());
					agent = AgentNW.chercherAgent(aTransaction, String.valueOf(prime.getId_agent()));
					sb.append("<tr>");
					sb.append("<td>" + agent.getNoMatricule() + "</td>");
					sb.append("<td>" + agent.getNomAgent() + " " + agent.getPrenomAgent() + "</td>");
					sb.append("<td>" + moisAnnee.format(prime.getDateDebutMois()) + "</td>");
					sb.append("<td>" + primeDetail.getLibelle() + "</td>");
					sb.append("<td>" + prime.getQuantite() + "</td>");
					sb.append("</tr>");
				}
				sb.append("</tbody>");
				break;
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	private static String getHeureMinute(int nombreMinute) {
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

	public static String getMondayFromWeekNumberAndYear(int week, int year) {
		GregorianCalendar cal = new GregorianCalendar();
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

	private static double roundDecimal(double nombre, int precision) {
		double tmp = Math.pow(10, precision);
		return Math.round(nombre * tmp) / tmp;
	}

}
