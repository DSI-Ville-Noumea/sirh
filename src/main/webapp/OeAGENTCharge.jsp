<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.specificites.Rubrique"%>
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTCharge" id="process" scope="session"></jsp:useBean>	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des charges</TITLE>
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
		//afin de s�lectionner un �l�ment dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone pr�cise
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
				},{select: function(event, ui) { test(ui); }});
			});		
			function test(ui){
   			document.getElementById('listeRubriques').value=ui.item.value;   			
   			executeBouton('NOM_PB_SELECT_RUBRIQUE');
			}	
		</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des charges de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td>
								    <%if(process.getCalculPaye().equals("")){ %>
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
									<%}%>
								</td>
								<td width="45px;" align="center">Code rubrique</td>
								<td width="250px;">Libell� rubrique</td>
								<td width="90px;" align="center">Matr. charge agent</td>
								<td width="200px;">Libell� charge</td>
								<td width="60px;" align="center">Taux</td>
								<td width="55px;" align="center">Montant</td>
								<td width="90px;" align="center">Date d�but</td>
								<td align="center">Date fin</td>
							</tr>
							<%
							int indiceCharge = 0;
							if (process.getListeCharges()!=null){
								for (int i = 0;i<process.getListeCharges().size();i++){
							%>
									<tr id="<%=indiceCharge%>" onmouseover="SelectLigne(<%=indiceCharge%>,<%=process.getListeCharges().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceCharge)%>">
											<%if(process.getCalculPaye().equals("")){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceCharge)%>">
											<%}%>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceCharge)%>">				
											<%if(process.getCalculPaye().equals("")){ %>
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceCharge)%>">
											<%}%>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_CODE_RUBR(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: left;"><%=process.getVAL_ST_RUBRIQUE(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:75px;text-align: center;"><%=process.getVAL_ST_MAT_CHARGE(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_LIB_CHARGE(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:55px;text-align: center;"><%=process.getVAL_ST_TAUX(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:55px;text-align: right;"><%=process.getVAL_ST_MONTANT(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:85px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceCharge)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indiceCharge)%></td>
									</tr>
									<%
									indiceCharge++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		<!-- Boutons cach�s -->
		<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_SELECT_RUBRIQUE()%>">	
		<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_SELECT_CODE_CHARGE()%>">			

		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<table>
				<tr>
					<td width="160px;">
						<span class="sigp2Mandatory">Rubrique :</span>
					</td>
					<td>
						<INPUT id="listeRubriques" class="sigp2-saisie" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_EF_RUBRIQUE() %>"  style="margin-right:10px;width:450px" type="text" value="<%= process.getVAL_EF_RUBRIQUE() %>">
					</td>
				</tr>
				<% if (process.showCreancier){ %>
				<tr>
					<td>
						<span class="sigp2Mandatory">Creancier :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CREANCIER() %>">
							<%=process.forComboHTML(process.getVAL_LB_CREANCIER(), process.getVAL_LB_CREANCIER_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<% } %>
				<% if (process.showCodeCharge){ %>
				<tr>
					<td>
						<span class="sigp2Mandatory">Charge :</span>
					</td>
					<td>
						<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_CODE_CHARGE() %>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_CODE_CHARGE()%>")'>
							<%=process.forComboHTML(process.getVAL_LB_CODE_CHARGE(), process.getVAL_LB_CODE_CHARGE_SELECT()) %>
						</SELECT>
						<span class="sigp2-saisie"><%= process.getVAL_ST_INFO_CODE_CHARGE()%></span>
					</td>
				</tr>
				<% } %>
				<% if (process.showMontant){ %>				
				<tr>
					<td>
						<span class="<%= process.montantObligatoire ? "sigp2Mandatory" : "sigp2" %> ">Montant forfait :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="9" name="<%= process.getNOM_EF_MONTANT() %>" size="9" type="text" value="<%= process.getVAL_EF_MONTANT() %>">
					</td>
				</tr>
				<% } %>				
				<% if (process.showMatriculeCharge){ %>
				<tr>
					<td>
						<span class=<%= process.matriculeChargeObligatoire ? "sigp2Mandatory" : "sigp2" %>>Matricule charge employ� :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="15" <%= process.matriculeChargeEditable ? "" : "readonly='readonly'" %>  name="<%= process.getNOM_EF_MAT_CHARGE() %>" size="15" type="text" value="<%= process.getVAL_EF_MAT_CHARGE() %>">
					</td>
				</tr>
				<% } %>
				<tr>
					<td>
						<span class="sigp2">Taux :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_TAUX() %>" size="5" type="text" value="<%= process.getVAL_EF_TAUX() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de d�but :</span>
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
			<%if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
		    	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
		    <table>
		    	<tr>
		    		<td width="160px;">
		   				<span class="sigp2">Rubrique : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_RUBRIQUE()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
		    			<span class="sigp2">Cr�ancier : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_CREANCIER()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
		    			<span class="sigp2">Code charge : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_CODE_CHARGE()%></span>
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
						<span class="sigp2">Matricule charge employ� : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_MAT_CHARGE()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">Taux : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_TAUX()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">Date de d�but : </span>
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
					<%if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
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