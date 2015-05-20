<%@ page contentType="text/html; charset=UTF-8" %> 
<%@page import="nc.mairie.metier.carriere.Carriere"%>
<%@page import="nc.mairie.gestionagent.process.pointage.OePTGVentilationUtils"%>
<%@page import="nc.mairie.gestionagent.pointage.dto.VentilHSupDto"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nc.mairie.metier.agent.Agent"%>
						<table  class="display"  id="VentilationTableHsupNonTitulaire"  width="100%" >
							<thead>
								<tr>
									<th>Matricule Agent</th>
									<th>Nom prénom</th>
									<th>Mois- année</th>
									<th>Total Abs</th>
									<th>Abs Sans Titre</th>
									<th>Abs</th>
									<th>Base</th>
									<th>Total</th>
									<th>Normales</th>
									<th>25%</th>
									<th>50%</th>
									<th>Nuit</th>
									<th>DJF</th>
									<th>Mai</th>
									<th>&nbsp;</th>
								</tr>
							</thead>
							<tbody>
							<%
							SimpleDateFormat moisAnnee = new SimpleDateFormat("MM-yyyy");
							Enumeration<Hashtable<Integer, String>> e = process.getHashVentilHsup().keys();
							Agent agent;
							
							while (e.hasMoreElements()) {
								Hashtable<Integer, String> ag = e.nextElement();
								Enumeration<Integer> o = ag.keys();
								Integer idAgent = o.nextElement();
								Collection<String> r = ag.values();
								String moisAnneeR = r.iterator().next();
								List<VentilHSupDto> hsup = process.getHashVentilHsup().get(ag);
								int abs = 0;
								int absAs400 = 0;
								int minutesHorsContrat = 0;								
								int minutesNormales = 0;					
								int minutes25 = 0;				
								int minutes50 = 0;		
								int minutesNuit = 0;
								int minutesDJF = 0;
								int minutesMai = 0;
								String affMois = "";
								String nomatr = "";
								String prenom = "";
								String nom = "";
								double weekBase=0;
								for (VentilHSupDto t : hsup) {
									abs += t.getMabs();
									absAs400 += t.getMabsAs400();
									minutesHorsContrat += t.getmHorsContrat()-t.getmRecuperees();									
									minutesNormales += t.getmNormales() - t.getmNormalesR();					
									minutes25 += t.getmSup25() - t.getmSup25R();			
									minutes50 += t.getmSup50() - t.getmSup50R();	
									minutesNuit += t.getmNuit() - t.getmNuitR();
									minutesDJF += t.getmDjf() - t.getmDjfR();
									minutesMai += t.getM1Mai() - t.getM1maiR();
									affMois = moisAnnee.format(t.getDateLundi());
									
									if(minutesHorsContrat > 0) {
										agent = process.getAgent(t.getId_agent());
										nomatr = agent.getNomatr().toString();
										prenom = agent.getPrenomAgent();
										nom = agent.getNomAgent();
										weekBase = process.getWeekBase(agent, t.getDateLundi());
									}
								}
								
								if(minutesHorsContrat > 0) {
							%>
								<tr id="hsup_<%=moisAnneeR+"_"+idAgent%>">
									<td><%=nomatr%></td>
									<td><%=nom + " " + prenom %></td>
									<td><%= affMois %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(abs + absAs400) %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(abs) %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(absAs400) %></td>
									<td><%= OePTGVentilationUtils.roundDecimal(weekBase, 2) %></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesHorsContrat)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesNormales)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutes25)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutes50)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesNuit)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesDJF)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesMai)%></td>
									<td>
										<img  src="images/loupe.gif" height="16px" width="16px" title="Voir le détail du mois" onClick="loadVentilationHsupHistory('<%=process.getValHistoryHsup(moisAnneeR,idAgent)%>', '<%=process.getHistoryHsup(moisAnneeR,idAgent)%>')">
									</td>			
								</tr>
								<% }
								} %>
							</tbody>
	                    </table>