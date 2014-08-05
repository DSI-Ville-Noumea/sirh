<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit" %>
<%@page import="nc.mairie.utils.MairieUtils" %>
<%@page import="nc.mairie.gestionagent.pointage.dto.HeureSupDto" %>
<%@page import="nc.mairie.gestionagent.absence.dto.AbsenceDto" %>
<%@page import="nc.mairie.gestionagent.pointage.dto.PrimeDto" %>
<%@page import="nc.mairie.gestionagent.process.pointage.EtatPointageEnum" %>
<%@page import="nc.mairie.gestionagent.process.pointage.TypeSaisieEnum" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.List" %>
<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
        <LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGSaisie" id="process" scope="session"></jsp:useBean>
        <TITLE>Saisie des pointages</TITLE>		
    	<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>   <!--  -->
        <SCRIPT type="text/javascript">
            function suppr(id) {
                if (document.getElementById("NOM_CK_" + id) !== null) {  document.getElementById("NOM_CK_" + id).checked = false;  }
                if (document.getElementById("NOM_nbr_" + id) !== null) {  document.getElementById("NOM_nbr_" + id).value = '';       }
                document.getElementById("NOM_motif_" + id).value = '';
                document.getElementById("NOM_comm_" + id).value = '';
                if (document.getElementById("NOM_time_" + id + "_D") !== null) {document.getElementById("NOM_time_" + id + "_D").value = '';}
                if (document.getElementById("NOM_time_" + id + "_F") !== null) {document.getElementById("NOM_time_" + id + "_F").value = '';}
                if (document.getElementById("NOM_typeAbs_" + id ) !== null) {document.getElementById("NOM_typeAbs_" + id ).value = '';}
            }

            function testClickEnrigistrer(){
            	if(event.keyCode == 13){
            		executeBouton('NOM_PB_VALIDATION');
            	}
            }
            function executeBouton(nom)
            {
            	alert("ici "+nom);
            	alert("ici2 "+document.formu.elements[nom]);
                document.formu.elements[nom].click();
            }

            function setfocus(nom)
            {
                if (document.formu.elements[nom] != null)
                    document.formu.elements[nom].focus();
            }
        </SCRIPT>		
        <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </HEAD>
    <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%=process.getFocus()%>')">
        <%@ include file="BanniereErreur.jsp" %>
        <FORM onkeypress="testClickEnrigistrer();" name="formu" method="POST" class="sigp2-titre">		
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend" style="font-size: 11px;"> Saisie des pointages pour l'agent <%=process.getIdAgent()%> semaine <%=process.getWeekYear()%>
                    <img onkeydown="" onkeypress="" onkeyup="" src="images/annuler.png" height="16px" width="55px" title="Retourner à l'écran de visualisation" onClick="executeBouton('<%=process.getNOM_PB_BACK()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">	
               		<INPUT height="16px" width="68px" type="submit" title="Enregister et Retourner à l'écran de visualisation"  value="Enregistrer" name="<%=process.getNOM_PB_VALIDATION()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">	
                 </legend>
                 <BR/>
                <table cellpadding="0" cellspacing="0" border="0" class="display" id="SaisiePointageList"> 
                    <thead>
                    	<tr>
                    		<%	for (int i = 0; i < 7; i++) { %>
                    				<th><%=process.getDateLundiStr(i) %></th>
                    		<%	} %>
                        </tr>
                    </thead>
                    <tbody>
     <!-- ------------------------------------------------ PRIMES ----------------------------------------------------------------- -->
                        <%
						if (process.getPrimess().size() > 0) { 
							if (process.getPrimess().get(0).size() > 0) { %>
								<tr bgcolor="#009ACD">
									<TD colspan="8" align="center">
										<b><H4>Primes</H4></b>
									</TD>
								</tr>
						<%	}
				
							for (int i = 0; i < process.getPrimess().get(0).size(); i++) { %>
								<tr>
							<%	int jour = 0;
								for (List<PrimeDto> pl : process.getPrimess()) {
									
									String id = "PRIME:" + pl.get(i).getNumRubrique() + ":" + pl.get(i).getIdRefPrime() + ":" + jour;
									String motif = pl.get(i).getMotif() != null ? pl.get(i).getMotif() : "";
									String titre = pl.get(i).getTitre() != null ? pl.get(i).getTitre() : "";
									String commentaire = pl.get(i).getCommentaire() != null ? pl.get(i).getCommentaire() : "";
									String qte = pl.get(i).getQuantite() != null ? "" + pl.get(i).getQuantite() : "";
									String checked = qte.equals("1") ? "CHECKED_ON" : "CHECKED_OFF"; 
									int nbrMinsTot = qte == "" ? 0 : Integer.parseInt(qte);
									int idRefEtat = pl.get(i).getIdRefEtat() != null ? pl.get(i).getIdRefEtat() : 0;
									int idPtg = pl.get(i).getIdPointage() != null ? pl.get(i).getIdPointage() : 0;
									String status = pl.get(i).getIdRefEtat() != null && !motif.equals("") ? EtatPointageEnum.getDisplayableEtatPointageEnum(idRefEtat) : "";
									%>
										<td>
											<table style="vertical-align: top;"  cellpadding="0" cellspacing="0" border="0" class="display" id="Type0TabCell<%=id %>">
												<tr>
													<td>
														<CENTER><b><%=titre %></b></CENTER>
													</td>
												</tr>
												<tr>
													<td height="20px">
														<CENTER>
														<%=status %> 
														<% if(status.equals("Saisi")){ %>
															 <img src="images/suppression.gif" height="16px" width="16px" onClick="suppr('<%=id %>')">
														<% } %>
														</CENTER>
													</td>
												</tr>
												<tr bgcolor="#BFEFFF">
													<td>
												<%	switch (TypeSaisieEnum.valueOf(pl.get(i).getTypeSaisie())) {
														case CASE_A_COCHER: %>
															<INPUT type="checkbox" <%= process.forCheckBoxHTML("NOM_CK_"+id, checked)%>> accordée
													<%		break;
														case NB_INDEMNITES: %>
															Nbre d'indemnités
															<input type="text" size="4" name="NOM_nbr_<%=id %>" value="<%=qte %>">
														<%	process.addZone("nbr_" + id, "" + qte);
															break;
														case NB_HEURES: 
															int nbr = nbrMinsTot / 60;
															int mins = nbrMinsTot - nbr * 60;
															%>
															Nbre d'heures
															<input type="text" size="2" name="NOM_nbr_<%=id %>" value="<%=nbr %>">h
															<select name="NOM_mins_<%=id %>"><%=process.getMinsCombo(mins) %></select>
														<%	 process.addZone("nbr_" + id, "" + nbr);
															break;
														case PERIODE_HEURES: %>
															Heure début  -->  Heure fin <br>
															<select name='NOM_time_<%=id %>_D' ><%=process.getTimeCombo(pl.get(i).getHeureDebut()) %></select>  / 
															<select name='NOM_time_<%=id %>_F'><%=process.getTimeFinCombo(pl.get(i).getHeureFin()) %></select>
														<%	break;
														default:
															break;
													} %>
														</td>
													</tr>
													<tr bgcolor="#BFEFFF">
														<td>
															<INPUT type="text" class="sigp2-saisie" size="22px" name="NOM_motif_<%=id %>" value="<%=motif %>" title="Zone de saisie du motif" />
														</td>
													</tr>
													<tr bgcolor="#BFEFFF">
														<td>
															<textarea  cols="15" rows="3" name="NOM_comm_<%=id %>" title="Zone de saisie du commentaire"><%=commentaire %></textarea>
															
															<input type="hidden" size="1" name="NOM_idptg_<%=id %>" value="<%=idPtg %>" />
															<input type="hidden" size="1" name="NOM_idrefetat_<%=id %>" value="<%=idRefEtat %>" />
														</td>
													</tr>
												</table>
											</td>
							<%		jour++;
								} %>
								</tr>
					<%		}
						} %>
	 <!-- ------------------------------------------------ PRIMES ----------------------------------------------------------------- -->					
						
     <!-- --------------------------------------- HEURES SUPPLEMENTAIRES ----------------------------------------------------------------- -->
                        <tr bgcolor="#009ACD">
							<TD colspan="8" align="center">
								<b><H4>Heures Supplémentaires</H4></b>
							</TD>
						</tr>
					    <%
					    for (int j = 0; j < 2; j++) { %>
					    <tr>
					    	<%
					    	for (int i = 0; i < 7; i++) {
					    		
					    		String dateIndex = process.getDateLundiStr(i); 
								HeureSupDto hs = null;
								String status = "";
								String id = "HS:" + i + ":" + j;
								Date heureDebut = null;
								Date heureFin = null;
								boolean checkedRecupere = false;
								String motif = "";
								String commentaire = "";
								Integer idPtg = 0;
								Integer idRefEtat = 0;
								
								if (process.getHsups().containsKey(dateIndex)
										&& process.getHsups().get(dateIndex).size() > j) {
									hs = process.getHsups().get(dateIndex).get(j);
								}
								
								if(null != hs) {
									status = hs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(hs.getIdRefEtat()) : "";
									heureDebut = hs.getHeureDebut();
									heureFin = hs.getHeureFin(); 
									checkedRecupere = hs.getRecuperee() != null ? hs.getRecuperee() : false;
									motif = hs.getMotif();
									commentaire = hs.getCommentaire();
									idPtg = hs.getIdPointage();
									idRefEtat = hs.getIdRefEtat();
								}
								
								%>
								
								<td>
									<table cellpadding="0" cellspacing="0" border="0" class="display" id="Type1-2TabCell<%=id %>">
										<tr>
											<td height="20px">
												<CENTER>
												<%=status %> 
												<% if(status.equals("Saisi")){ %>
													 <img src="images/suppression.gif" height="16px" width="16px" onClick="suppr('<%=id %>')">
												<% } %>
												</CENTER>
											</TD>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td> 
												Heure début  -->  Heure fin <br />
												<select name="NOM_time_<%=id %>_D"><%=process.getTimeCombo(heureDebut) %></select>  /  
												<select name="NOM_time_<%=id %>_F"><%=process.getTimeFinCombo(heureFin) %></select>
											</td>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td>
												<input type="checkbox" name="NOM_CK_<%=id %>" <% if(checkedRecupere){ %> checked <% } %> /> A récupérer
											</td>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td>
												<INPUT type="text" size="22px" class="sigp2-saisie" name="NOM_motif_<%=id %>" value="<%=motif %>" title="Zone de saisie du motif" />
											</td>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td>
												<textarea  cols="15" rows="3" name="NOM_comm_<%=id %>" title="Zone de saisie du commentaire"><%=commentaire %></textarea>
												
												<input type="hidden" size="1" name="NOM_idptg_<%=id %>" value="<%=idPtg %>" />
												<input type="hidden" size="1" name="NOM_idrefetat_<%=id %>" value="<%=idRefEtat %>" />
											</td>
										</tr>
									</table>
								</td>
							<% 
					    	}
					    	%>
					    </tr>
					 <% } %>
		<!-- --------------------------------------- HEURES SUPPLEMENTAIRES ----------------------------------------------------------------- -->			 
						 
        <!-- ----------------------------------------------- ABSENCES ----------------------------------------------------------------- -->
                        <tr bgcolor="#009ACD">
	                        <TD colspan="8" align="center">
	                        	<b><H4>Absences</H4></b>
	                        </TD>
                        </tr>
 
						<% 
						for (int j = 0; j < 2; j++) { %>
					    <tr>
					    	<%
					    	for (int i = 0; i < 7; i++) { 
					    		
					    		String dateIndex = process.getDateLundiStr(i); 
					    		AbsenceDto abs = null;
					    		String id = "ABS:" + i + ":" + j;
					    		String status = "";
								Date heureDebut = null;
								Date heureFin = null;
								String motif = "";
								String commentaire = "";
								Integer idPtg = 0;
								Integer idRefEtat = 0;
								Integer idTypeAbs = 0;
					    		
								if (process.getAbsences().containsKey(dateIndex) && process.getAbsences().get(dateIndex).size() > j) {
									abs = process.getAbsences().get(dateIndex).get(j);
								}
								
								if(null != abs) {
									status = abs.getIdRefEtat() != null ? EtatPointageEnum.getDisplayableEtatPointageEnum(abs.getIdRefEtat()) : "";
									heureDebut = abs.getHeureDebut();
									heureFin = abs.getHeureFin();
									motif = abs.getMotif();
									commentaire = abs.getCommentaire();
									idPtg = abs.getIdPointage();
									idRefEtat = abs.getIdRefEtat();
									idTypeAbs = abs.getIdRefTypeAbsence();
								}
								%>
								<td>
									<table cellpadding="0" cellspacing="0" border="0" class="display" id="Type1-2TabCell<%=id %>">
										<tr>
											<td height="20px">
												<CENTER>
												<%=status %> 
												<% if(status.equals("Saisi")){ %>
													 <img src="images/suppression.gif" height="16px" width="16px" onClick="suppr('<%=id %>')">
												<% } %>
												</CENTER>
											</TD>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td> Heure début  -->  Heure fin <br />
												<select name="NOM_time_<%=id %>_D"><%=process.getTimeCombo(heureDebut) %></select>  /  
												<select name="NOM_time_<%=id %>_F"><%=process.getTimeFinCombo(heureFin) %></select>
											</td>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td>
												<select name="NOM_typeAbs_<%=id %>"><%=process.getTypeAbsenceCombo(idTypeAbs) %></select>
											</td>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td>
												<INPUT type="text" class="sigp2-saisie" size="22px" name="NOM_motif_<%=id %>" value="<%=motif %>" title="Zone de saisie du motif" />
											</td>
										</tr>
										<tr bgcolor="#BFEFFF">
											<td>
												<textarea  cols="15" rows="3" name="NOM_comm_<%=id %>" title="Zone de saisie du commentaire"><%=commentaire %></textarea>
												
												<input type="hidden" size="1" name="NOM_idptg_<%=id %>" value="<%=idPtg %>" />
												<input type="hidden" size="1" name="NOM_idrefetat_<%=id %>" value="<%=idRefEtat %>" />
											</td>
										</tr>
									</table>
								</td>
						<%	} %>
							</tr>
					<%	}  %>
		 <!-- ------------------------------------------- ABSENCES ----------------------------------------------------------------- -->
                    </tbody>
                </table>
                <BR/>
               <img onkeydown="" onkeypress="" onkeyup="" src="images/annuler.png" height="16px" width="55px" title="Retourner à l'écran de visualisation" onClick="executeBouton('<%=process.getNOM_PB_BACK()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">	
               <INPUT height="16px" width="68px" type="submit" title="Enregister et Retourner à l'écran de visualisation"  value="Enregistrer" name="<%=process.getNOM_PB_VALIDATION()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">	
                 	
        	</FIELDSET>
        	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_BACK()%>" value="BACK">
        </FORM>
    </BODY>
</HTML>
