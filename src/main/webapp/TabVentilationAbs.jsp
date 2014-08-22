<%@page import="nc.mairie.gestionagent.process.pointage.OePTGVentilationUtils"%>
<%@page import="nc.mairie.gestionagent.pointage.dto.VentilAbsenceDto"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nc.mairie.metier.agent.Agent"%>
						<table  class="display"  id="VentilationTableAbs">
							<thead>
								<tr>
									<th width="70px">Matricule Agent</th>
									<th width="150px">Nom prénom</th>
									<th width="70px">Mois-année</th>
									<th width="70px">Abs concertées</th>
									<th width="70px">Abs non concertées</th>
									<th width="70px">Abs immédiates</th>
									<th width="70px">Total</th>
									<th width="30px">&nbsp</th>
								</tr>
							</thead>
							<tbody>
							<%
								SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
												Enumeration<Hashtable<Integer, String>> e = process.getHashVentilAbs().keys();
												Agent agent;
												while (e.hasMoreElements()) {
													Hashtable<Integer, String> ag = e.nextElement();
													Enumeration<Integer> o = ag.keys();
													Integer idAgent = o.nextElement();
													Collection<String> r = ag.values();
													String moisAnneeR = r.iterator().next();
													List<VentilAbsenceDto> abs = process.getHashVentilAbs().get(ag);
													int minutesConcertees = 0;
													int minutesNonConcertees = 0;
													int minutesImmediates = 0;
													String affMois = "";
													String nomatr = "";
													String prenom = "";
													String nom = "";
													for (VentilAbsenceDto t : abs) {
														minutesConcertees += t.getMinutesConcertees();
														minutesNonConcertees += t.getMinutesNonConcertees();
														minutesImmediates += t.getMinutesImmediates();
														agent = process.getAgent(t.getId_agent());
														nomatr = agent.getNomatr().toString();
														prenom = agent.getPrenomAgent();
														nom = agent.getNomAgent();
														affMois = moisAnnee.format(t.getDateLundi());
													}
							%>
								<tr id="abs_<%=moisAnneeR+"_"+idAgent%>">
									<td><%=nomatr%></td>
									<td><%=nom + " " + prenom %></td>
									<td><%= affMois %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesConcertees) %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesNonConcertees) %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesImmediates)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesConcertees + minutesNonConcertees + minutesImmediates)%></td>
									<td>
										<img  src="images/loupe.gif" height="16px" width="16px" title="Voir le détail du mois" onClick="loadVentilationAbsHistory('<%=process.getValHistoryAbs(moisAnneeR,idAgent)%>', '<%=process.getHistoryAbs(moisAnneeR,idAgent)%>')">
									</td>			
								</tr>
								<%} %>
							</tbody>
	                    </table>