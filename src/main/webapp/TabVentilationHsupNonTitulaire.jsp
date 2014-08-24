<%@page import="nc.mairie.metier.carriere.BaseHoraire"%>
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
									<th>Nom pr�nom</th>
									<th>Mois- ann�e</th>
									<th>Total Abs</th>
									<th>Abs</th>
									<th>Abs (AS400)</th>
									<th>Base</th>
									<th>Total</th>
									<th>Compl</th>
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
								int minutesComplementaires = 0;					
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
									minutesHorsContrat += t.getmHorsContrat();									
									minutesComplementaires += t.getmComplementaires();					
									minutes25 += t.getmSup25();			
									minutes50 += t.getmSup50();	
									minutesNuit += t.getmNuit();
									minutesDJF += t.getmDjf();
									minutesMai += t.getM1Mai();									
														agent = process.getAgent(t.getId_agent());
														nomatr = agent.getNomatr().toString();
									prenom = agent.getPrenomAgent();
									nom = agent.getNomAgent();
									affMois = moisAnnee.format(t.getDateLundi());
									Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(process.getTransaction(), agent);
									BaseHoraire baseHoraire = BaseHoraire.chercherBaseHoraire(process.getTransaction(), carr.getCodeBase());
									weekBase = Double.valueOf(baseHoraire.getNbashh().replace(",", "."));
								}
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
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesComplementaires)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutes25)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutes50)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesNuit)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesDJF)%></td>
									<td><%= OePTGVentilationUtils.getHeureMinute(minutesMai)%></td>
									<td>
										<img  src="images/loupe.gif" height="16px" width="16px" title="Voir le d�tail du mois" onClick="loadVentilationHsupHistory('<%=process.getValHistoryHsup(moisAnneeR,idAgent)%>', '<%=process.getHistoryHsup(moisAnneeR,idAgent)%>')">
									</td>			
								</tr>
								<%} %>
							</tbody>
	                    </table>