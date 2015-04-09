<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.metier.specificites.Rubrique"%>
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTPrime" id="process" scope="session"></jsp:useBean>	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des primes</TITLE>
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
		ArrayList<Rubrique> listeRubriques = process.getListeRubriques();
		
		String res = 	"<script language=\"javascript\">\n"+
				"var availableRubriques = new Array(\n";
		
		for (int i = 0; i < listeRubriques.size(); i++){
			res+= "   \""+((Rubrique)listeRubriques.get(i)).getNorubr()+" "+((Rubrique)listeRubriques.get(i)).getLirubr()+"\"";
			if (i+1 < listeRubriques.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listeRubriques").autocomplete({source:availableRubriques
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
				    <legend class="sigp2Legend">Liste des primes de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td>
								    <%if(process.getCalculPaye().equals("")){ %>
								    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
								    <%}%>
								</td>
								<td width="45px;" align="center">Code rubrique</td>
								<td width="300px;">Libellé rubrique</td>
								<td width="75px;">Ref. arrêté</td>
								<td width="90px;">Date de l'arrêté</td>
								<td width="55px;" align="center">Montant</td>
								<td width="90px;" align="center">Date début</td>
								<td align="center">Date fin</td>
							</tr>
							<%
							int indicePrime = 0;
							if (process.getListePrimes()!=null){
								for (int i = 0;i<process.getListePrimes().size();i++){
							%>
									<tr id="<%=indicePrime%>" onmouseover="SelectLigne(<%=indicePrime%>,<%=process.getListePrimes().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indicePrime)%>">											
				    						<%if(process.getCalculPaye().equals("")){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indicePrime)%>">
											<%}%>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indicePrime)%>">				
											<%if(process.getCalculPaye().equals("")){ %>
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indicePrime)%>">
											<%}%>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_CODE_RUBR(indicePrime)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: left;"><%=process.getVAL_ST_LIB_RUBR(indicePrime)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:75px;text-align: right;"><%=process.getVAL_ST_REF_ARR(indicePrime)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_DATE_ARR(indicePrime)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:55px;text-align: right;"><%=process.getVAL_ST_MONTANT(indicePrime)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indicePrime)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indicePrime)%></td>
									</tr>
									<%
									indicePrime++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>			
				
	</FORM>
		<%if ( !Const.CHAINE_VIDE.equals(process.getVAL_ST_ACTION()) ) {%>
	<FORM method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<table>
				<tr>
					<td width="130px;">
						<span class="sigp2Mandatory">Rubrique :</span>
					</td>
					<td>
						<INPUT id="listeRubriques" class="sigp2-saisie" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_RUBRIQUE() %>"  style="margin-right:10px;width:450px" type="text" value="<%= process.getVAL_EF_RUBRIQUE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Ref. arrêté :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="6" name="<%= process.getNOM_EF_REF_ARR() %>" size="6" type="text" value="<%= process.getVAL_EF_REF_ARR() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date d'arrêté :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_ARR()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_ARR() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_ARR() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_ARR()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Montant :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="7" name="<%= process.getNOM_EF_MONTANT() %>" size="7" type="text" value="<%= process.getVAL_EF_MONTANT() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_FIN() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');">
					</td>
				</tr>
			</table>
			<% } else{ %>
			<div>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)) { %>
		    	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
		    <table>
		    	<tr>
		    		<td width="130px;">
		    			<span class="sigp2">Rubrique : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_RUBRIQUE()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
		    			<span class="sigp2">Ref. arrêté : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_REF_ARR()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">Date arrêté : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_ARR()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
		    			<span class="sigp2">Montant : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_MONTANT()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">Date de début : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">Date de fin : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
		    		</td>
		    	</tr>
		    </table>
		</div>
		<%} %>
			<BR/>
			<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
			<TBODY>
				<TR>
					<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)) { %>
		  			<TD width="31"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>"></TD>
					<TD height="18" width="15"></TD>
					<% } %>
					<TD class="sigp2" style="text-align : center;" height="18" width="23"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>"></TD>
				</TR>
			</TBODY>
		</TABLE>
		</FIELDSET>
		</FORM>
		<% } %>
<%} %>
	</BODY>
</HTML>