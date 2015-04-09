<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.metier.agent.PositionAdm"%>
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTPosAdm" id="process" scope="session"></jsp:useBean>	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des positions administratives</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></script>
		
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
		<%
		ArrayList<PositionAdm> listePA = process.getListePA();
		
		String res = 	"<script language=\"javascript\">\n"+
				"var availablePA = new Array(\n";
		
		for (int i = 0; i < listePA.size(); i++){
			res+= "   \""+((PositionAdm)listePA.get(i)).getCdpadm()+" "+((PositionAdm)listePA.get(i)).getLiPAdm()+"\"";
			if (i+1 < listePA.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listePA").autocomplete({source:availablePA
				});
			});		
		</SCRIPT>
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des positions administratives de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td>
								    <%if(process.getCalculPaye().equals("")){ %>											
								    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
								    <%}%>
								</td>
								<td width="45px;" align="center">Code PA</td>
								<td width="250px;">Libellé PA</td>
								<td width="75px;" align="center">Ref. arrêté</td>
								<td width="85px;" align="center">Date de l'arrêté</td>
								<td width="85px;" align="center">Date de début</td>
								<td>Date de fin</td>
							</tr>
							<%
							int indicePAAgent = 0;
							if (process.getListePAAgent()!=null){
								for (int i = 0;i<process.getListePAAgent().size();i++){
							%>
									<tr id="<%=indicePAAgent%>" onmouseover="SelectLigne(<%=indicePAAgent%>,<%=process.getListePAAgent().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indicePAAgent)%>">	
											<%if(process.getCalculPaye().equals("")){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indicePAAgent)%>">
											<%}%>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indicePAAgent)%>">				
										<%if(indicePAAgent==process.getListePAAgent().size()-1){ %>
											<%if(process.getCalculPaye().equals("")){ %>
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indicePAAgent)%>">
											<%}%>
										<%} %>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_POSA(indicePAAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align:left;"><%=process.getVAL_ST_LIB_POSA(indicePAAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: right;"><%=process.getVAL_ST_REF_ARR(indicePAAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_DATE_ARR(indicePAAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indicePAAgent)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left">&nbsp;<%=process.getVAL_ST_DATE_FIN(indicePAAgent)%></td>
									</tr>
									<%
									indicePAAgent++;
								}
							}%>
						</table>	
						</div>		
				</FIELDSET>
		
		
		
		</FORM>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<table>
				<tr>
					<td width="150px;">
						<span class="sigp2Mandatory">Position administrative :</span>
					</td>
					<td>
						<INPUT size="100" id="listePA" class="sigp2-saisie" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_POSA() %>" type="text" value="<%= process.getVAL_EF_POSA() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Ref. arrêté :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="6" name="<%= process.getNOM_EF_REF_ARR() %>" size="6" type="text" value="<%= process.getVAL_EF_REF_ARR() %>">
						<span class="sigp2"  style="margin-left:20px;"">Date d'arrêté :</span>
						<input id="<%=process.getNOM_EF_DATE_ARR()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_ARR() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_ARR() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_ARR()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" <%= !process.DateDebutEditable ? "disabled='disabled'" : Const.CHAINE_VIDE %> maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">
						<%if(process.DateDebutEditable){ %>
							<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
						<%} %>
					</td>
				</tr>
			</table>			
			<% } else{ %>
			<div>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
		    	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
			    <span class="sigp2" style="width:150px">Position administrative : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_POSA()%></span>
				<BR/>
			    <span class="sigp2" style="width:150px">Ref. arrêté : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_REF_ARR()%></span>
				<BR/>
				<span class="sigp2" style="width:150px">Date arrêté : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_ARR()%></span>
				<BR/>
				<span class="sigp2" style="width:150px">Date de début : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>
				<BR/>
				<span class="sigp2" style="width:150px">Date de fin : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
			</div>
		<%} %>
			<BR/>
			<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
			<TBODY>
				<TR>
					<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
		    		<TD width="31"><INPUT type="submit"
						class="sigp2-Bouton-100" value="Valider"
						name="<%=process.getNOM_PB_VALIDER()%>"></TD>
					<TD height="18" width="15"></TD>
					<% } %>
					<TD class="sigp2" style="text-align : center;"
						height="18" width="23"><INPUT type="submit"
						class="sigp2-Bouton-100" value="Annuler"
						name="<%=process.getNOM_PB_ANNULER()%>"></TD>
				</TR>
			</TBODY>
		</TABLE>
		</FIELDSET>
		</FORM>
		<% } %>
<%} %>
	</BODY>
</HTML>