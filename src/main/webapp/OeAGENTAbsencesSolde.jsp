<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeAbsence"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesSolde" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des absences</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
		//afin de sélectionner un élément dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone précise
		function setfocus(nom)
		{
			if (document.formu.elements[nom] != null)
			document.formu.elements[nom].focus();
		}
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
		}
		
		</SCRIPT>	
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>');">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Filtres pour *</legend>
				<BR/>
				<SELECT onchange='executeBouton("<%=process.getNOM_PB_ANNEE() %>")'  class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
				</SELECT>
			</FIELDSET>
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des soldes de l'agent</legend>
				    <table>
				    	<tr>
				    		<td width="50%">
							    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">Congés</legend>
									<table class="sigp2NewTab" style="text-align:left;width:400px;">
										<tr bgcolor="#EFEFEF">
											<td width="100px;" align="center">année prec.</td>
											<td width="120px;" align="center">année</td>
											<td width="80px;">Historique</td>
											<td>Samedi offert</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGE_PREC()%></td>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGE()%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.CONGE.getCode())%>"></td>
											<td style="text-align: center"><%=process.getVAL_ST_SAMEDI_OFFERT_SOLDE_CONGE()%></td>
										</tr>
									</table>				    
							    </FIELDSET>
				    		</td>
				    		<td>
							    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">Récupérations</legend>
									<table class="sigp2NewTab" style="text-align:left;width:200px;">
										<tr bgcolor="#EFEFEF">
											<td width="200px;" align="center">En cours</td>
											<td>Historique</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_RECUP()%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.RECUP.getCode())%>"></td>
										</tr>
									</table>				    
							    </FIELDSET>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Réunion des membres du bureau directeur</legend>
									<table class="sigp2NewTab" style="text-align:left;width:200px;">
										<tr bgcolor="#EFEFEF">
											<td width="200px;" align="center">En cours</td>
											<td>Historique</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A48()%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.ASA_A48.getCode())%>"></td>
										</tr>
									</table>				    
							    </FIELDSET>	
				    		</td>
				    		<td>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Congrès et conseil syndical</legend>
									<table class="sigp2NewTab" style="text-align:left;width:200px;">
										<tr bgcolor="#EFEFEF">
											<td width="200px;" align="center">En cours</td>
											<td>Historique</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A54()%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.ASA_A54.getCode())%>"></td>
										</tr>
									</table>				    
							    </FIELDSET>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Délégation DP</legend>
									<table class="sigp2NewTab" style="text-align:left;width:400px;">
										<tr bgcolor="#EFEFEF">
											<td width="150px;" align="center">En cours</td>
											<td width="90px;" align="center">Début</td>
											<td width="90px;" align="center">Fin</td>
											<td>Historique</td>
										</tr>
										<% for(int i = 0; i< process.getListeSoldeA55().size();i++){ %>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A55(i)%></td>
											<td style="text-align: center"><%=process.getVAL_ST_DEBUT_ASA_A55(i)%></td>
											<td style="text-align: center"><%=process.getVAL_ST_FIN_ASA_A55(i)%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.ASA_A55.getCode())%>"></td>
										</tr>
										<% } %>
									</table>				    
							    </FIELDSET>
				    		</td>
				    		<td>
					    		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Décharge de service CTP</legend>
							    	
								    <%if(process.isAfficheSoldeAsaA52()){ %>
										<table class="sigp2NewTab" style="text-align:left;width:400px;">
											<tr bgcolor="#EFEFEF">
											<%if(process.getOrganisationAgent()!=null){%>
												<td width="150px;" align="center">En cours pour <%=process.getOrganisationAgent().getSigle() %></td>
											<%}else{ %>
												<td width="150px;" align="center">En cours pourOS</td>
											<%} %>
												<td width="90px;" align="center">Début</td>
												<td width="90px;" align="center">Fin</td>
												<td>Historique</td>
											</tr>
											<% for(int i = 0; i< process.getListeSoldeA52().size();i++){ %>
											<tr>
												<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A52(i)%></td>
												<td style="text-align: center"><%=process.getVAL_ST_DEBUT_ASA_A52(i)%></td>
												<td style="text-align: center"><%=process.getVAL_ST_FIN_ASA_A52(i)%></td>
												<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.ASA_A52.getCode())%>"></td>
											</tr>
											<% } %>
										</table>	
								    <%}else{ %>	
								    <span>Agent non concerné</span>
								    <%} %>		    
							    </FIELDSET>
				    		</td>
				    	</tr>
				    </table>
				    
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
				    	<legend class="sigp2Legend">Repos compensateurs</legend>
					    <%if(process.isAgentReposComp()){ %>
							<table class="sigp2NewTab" style="text-align:left;width:200px;">
								<tr bgcolor="#EFEFEF">
									<td width="100px;" align="center">année prec.</td>
									<td width="100px;" align="center">année</td>
									<td>Historique</td>
								</tr>
								<tr>
									<td style="text-align: center"><%=process.getVAL_ST_SOLDE_REPOS_COMP_PREC()%></td>
									<td style="text-align: center"><%=process.getVAL_ST_SOLDE_REPOS_COMP()%></td>
									<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.REPOS_COMP.getCode())%>"></td>
								</tr>
							</table>	
					    <%}else{ %>
					    <span>Agent non concerné</span>
					    <%} %>			    
				    </FIELDSET>
				    <BR/>	
				    
				     <FIELDSET class="sigp2Fieldset" style="text-align:left;width:950px;">
				    	<legend class="sigp2Legend">* Congés Exceptionnels</legend>
						<table class="sigp2NewTab" style="text-align:left;width:900px;">
							<tr bgcolor="#EFEFEF">
								<td width="750px;">Type</td>
								<td width="150px;" align="center">Congés déjà pris ou en cours</td>
							</tr>
							<% for(int i = 0; i< process.getListeSoldeCongesExcep().size();i++){ %>
							<tr>
								<td width="750px;"><%=process.getVAL_ST_TYPE_CONGES_EXCEP(i)%></td>
								<td width="150px;" style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGES_EXCEP(i)%></td>
							</tr>
							<% } %>
						</table>
				    </FIELDSET>
				    <BR/>
				</FIELDSET>
				<BR/>
				
				<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td align="center" width="90px;">Le <br/> à</td>
								<td width="180px;">Par</td>
								<td width="180px;">Motif</td>
								<td>Opération</td>
							</tr>
							<%
							for (int i = 0;i<process.getListeHistorique().size();i++){
							%>
								<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeHistorique().size()%>)">
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_PAR(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_MOTIF(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_OPERATION(i)%></td>
								</tr>
							<%}%>
						</table>	
						</div>
						<BR/><BR/>
						<div style="text-align: center;">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Fermer" name="<%=process.getNOM_PB_ANNULER()%>">
						</div>	
				</FIELDSET>
				<%} %>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_ANNEE()%>" value="x">
		</FORM>
<%} %>	
	</BODY>
</HTML>