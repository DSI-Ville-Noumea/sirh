<%@page import="nc.mairie.gestionagent.dto.AgentDto"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTRechercheDroitKiosqueAgentApprobateur" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Sélection d'agents</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
			//afin de sélectionner un élément dans une liste
			function executeBouton(nom)
			{
			document.formu.elements[nom].click();
			}
			
			//function pour changement couleur arriere plan ligne du tableau
			function SelectLigne(id,tailleTableau)
			{
				for (i=0; i<tailleTableau; i++){
			 		document.getElementById(i).className="";
				} 
			 document.getElementById(id).className="selectLigne";
			}

			
			function activeAgent() {						
				<%
				for (int j = 0;j<process.getListeAgents().size();j++){
					AgentDto ag = (AgentDto) process.getListeAgents().get(j);
					Integer i = ag.getIdAgent();
				%>
				var box = document.formu.elements['NOM_CK_AGENT_' + <%=i%>];
				if (document.formu.elements['CHECK_ALL_AGENT'].checked) {
					if (box != null && !box.disabled) {
						box.checked = true;
					}
				} else {
					if (box != null && !box.disabled) {
						box.checked = false;
					}
				}
				<%}%>
			}
		</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'un agent">
				<LEGEND class="sigp2Legend">Sélection d'agents</LEGEND>
				<BR/><BR/>
               	<%if(process.getListeAgents()!= null && process.getListeAgents().size()>0){ %>      
						<div style="overflow: auto;height: 250px;width:700px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:680px;">
								<tr>
									<td style="text-align: center;"><INPUT type="checkbox" name="CHECK_ALL_AGENT" onClick='activeAgent()'></td>
									<td><span>Matricule</span></td>
									<td><span>Nom</span></td>
									<td><span>Prenom</span></td>					
								</tr>
								<%
								if (process.getListeAgents()!=null){
									for (int i = 0;i<process.getListeAgents().size();i++){
										int indiceAgent = process.getListeAgents().get(i).getIdAgent();
								%>
									<tr id="<%=indiceAgent%>" onmouseover="SelectLigne(<%=indiceAgent%>,<%=process.getListeAgents().size()%>)" >
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: center;">
											<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AGENT(indiceAgent),process.getVAL_CK_AGENT(indiceAgent))%> >
										</td>	
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: left;"><%=process.getVAL_ST_MATR(indiceAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_NOM(indiceAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_PRENOM(indiceAgent)%></td>
									</tr>
										<%
									}
								}%>
							</table>	
						</div>
	            <%} else{ %>
				    <span>Aucun agent disponible</span>	  
	            	<BR>          
	            <%} %>
	            <BR/><BR/>
	            <TABLE border="0" style="text-align : center;" cellpadding="0" cellspacing="0">
					<TBODY>
						<TR>
							<TD>
								<INPUT type="submit" value="Valider" class="sigp2-Bouton-100" accesskey="A" name="<%=process.getNOM_PB_OK()%>">
								<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" accesskey="B" name="<%=process.getNOM_PB_ANNULER()%>">
							</TD>
						</TR>
					</TBODY>
	            </TABLE>
	            <BR>
			</FIELDSET>
		</FORM>
	</BODY>
</HTML>