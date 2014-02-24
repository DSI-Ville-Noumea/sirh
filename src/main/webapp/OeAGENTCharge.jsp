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
			res+= "   \""+((Rubrique)listeRubriques.get(i)).getNumRubrique()+" "+((Rubrique)listeRubriques.get(i)).getLibRubrique()+"\"";
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
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:65px;">
				    <%if(process.getCalculPaye().equals("")){ %>
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
					<%}%>
					</span>
				    <span style="position:relative;width:45px;text-align: center;">Code rubrique</span>
					<span style="position:relative;width:250px;text-align: left;">Libellé rubrique</span>
					<span style="position:relative;width:90px;text-align: center;">Matr. charge agent</span>
					<span style="position:relative;width:200px;text-align: left;">Libellé charge</span>
					<span style="position:relative;width:60px;text-align: center;">Taux</span>
					<span style="position:relative;width:55px;text-align: center;">Montant</span>
					<span style="position:relative;width:90px;text-align: center;">Date début</span>
					<span style="position:relative;text-align: center;">Date fin</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
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
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indiceCharge)%></td>
									</tr>
									<%
									indiceCharge++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		<!-- Boutons cachés -->
		<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_SELECT_RUBRIQUE()%>">	
		<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_SELECT_CODE_CHARGE()%>">			

		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<div>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:160px;">Rubrique :</span>
			<INPUT id="listeRubriques" class="sigp2-saisie" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>
				name="<%= process.getNOM_EF_RUBRIQUE() %>"  style="margin-right:10px;width:450px" type="text" value="<%= process.getVAL_EF_RUBRIQUE() %>">
			
			<BR/><BR/>
			
			<% if (process.showCreancier){ %>
		
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:160px;">Creancier :</span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CREANCIER() %>">
				<%=process.forComboHTML(process.getVAL_LB_CREANCIER(), process.getVAL_LB_CREANCIER_SELECT()) %>
			</SELECT>

			<BR/><BR/>
			
			<% } %>
			
			<% if (process.showCodeCharge){ %>
		
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:160px;">Charge :</span>
			<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_CODE_CHARGE() %>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_CODE_CHARGE()%>")'>
				<%=process.forComboHTML(process.getVAL_LB_CODE_CHARGE(), process.getVAL_LB_CODE_CHARGE_SELECT()) %>
			</SELECT>
			<span class="sigp2-saisie"><%= process.getVAL_ST_INFO_CODE_CHARGE()%></span>
			<BR/><BR/>
			<% } %>
			
			<% if (process.showMontant){ %>
		
			<span class="<%= process.montantObligatoire ? "sigp2Mandatory" : "sigp2" %> " style="margin-left:20px;position:relative;width:160px;">Montant forfait :</span>
			<INPUT class="sigp2-saisie" maxlength="9" name="<%= process.getNOM_EF_MONTANT() %>" size="9" type="text" value="<%= process.getVAL_EF_MONTANT() %>">

			<BR/><BR/>
			
			<% } %>
			
			<% if (process.showMatriculeCharge){ %>
			
			<span class="<%= process.matriculeChargeObligatoire ? "sigp2Mandatory" : "sigp2" %> " style="margin-left:20px;position:relative;width:160px;" >Matricule charge employé :</span>
			<INPUT class="sigp2-saisie" maxlength="15" <%= process.matriculeChargeEditable ? "" : "readonly='readonly'" %>  name="<%= process.getNOM_EF_MAT_CHARGE() %>" size="15" type="text" value="<%= process.getVAL_EF_MAT_CHARGE() %>">
			<BR/><BR/>
			
			<% } %>
			
			<span class="sigp2" style="margin-left:20px;position:relative;width:160px;">Taux :</span>
			<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_TAUX() %>" size="5" type="text" value="<%= process.getVAL_EF_TAUX() %>">

			<BR/><BR/>

			<span class="sigp2Mandatory"  style="margin-left:20px;position:relative;width:160px;">Date de début :</span>
			<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">
			<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">

			<BR/><BR/>
			
			<span class="sigp2"  style="margin-left:20px;position:relative;width:160px;">Date de fin :</span>
			<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_FIN() %>">
			<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');">
			
			</div>
			
			<% } else{ %>
			<div>
			<%if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
		    	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
		    <span class="sigp2" style="width:160px">Rubrique : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_RUBRIQUE()%></span>
			<BR/>
		    <span class="sigp2" style="width:160px">Créancier : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_CREANCIER()%></span>
			<BR/>
		    <span class="sigp2" style="width:160px">Code charge : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_CODE_CHARGE()%></span>
			<BR/>
			<span class="sigp2" style="width:160px">Montant : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_MONTANT()%></span>
			<BR/>
			<span class="sigp2" style="width:160px">Matricule charge employé : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_MAT_CHARGE()%></span>
			<BR/>
			<span class="sigp2" style="width:160px">Taux : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_TAUX()%></span>
			<BR/>
			<span class="sigp2" style="width:160px">Date de début : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>
			<BR/>
			<span class="sigp2" style="width:160px">Date de fin : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
		</div>
		<%} %>
			<BR/>
			<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
			<TBODY>
				<TR>
					<%if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
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