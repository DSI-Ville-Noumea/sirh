<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.agent.Contrat"%>
<%@page import="nc.mairie.technique.Services"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des contrats d'un agent</TITLE>

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
<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTContrat" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des contrats de l'agent</legend>
				    <br/>
				    <span style="margin-left: 5px;">
				    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
				    </span>
				    <span style="margin-left: 65px;">Numéro</span>
					<span style="margin-left: 40px;">Type</span>
					<span style="margin-left: 5px;">Avenant</span>
					<span style="margin-left: 5px;">Date de début</span>
					<span style="margin-left: 5px;">Fin période essai</span>
					<span style="margin-left: 15px;">Date de fin</span>
					<span style="margin-left: 15px;">Motif</span>
					<span style="margin-left: 175px;">Justification</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							int indiceContrat = 0;
							if (process.getListeContrat()!=null){
								for (int i = 0;i<process.getListeContrat().size();i++){
									Contrat c = (Contrat) process.getListeContrat().get(i);
							%>
									<tr id="<%=indiceContrat%>" onmouseover="SelectLigne(<%=indiceContrat%>,<%=process.getListeContrat().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceContrat)%>">
										<%if(c.getIdTypeContrat().equals("2") && c.getDateFin()==null){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceContrat)%>">
										<%}else if(c.getIdTypeContrat().equals("1") && Services.compareDates(sdf.format(c.getDateDebut()),Services.dateDuJour())<0 && (c.getDateFin()!=null ? Services.compareDates(Services.dateDuJour(),sdf.format(c.getDateFin()))<0 : true)) { %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceContrat)%>">
										<%}else{ %>
											<span style="width: 22px;"></span>
										<%} %>
										<%if(c.getIdTypeContrat().equals("1")){ %>
											<INPUT title="imprimer" type="image" src="images/imprimer.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_IMPRIMER(indiceContrat)%>">
										<%}else{ %>
											<span style="width: 22px;"></span>
										<%} %>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceContrat)%>">				
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceContrat)%>">		
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_NUM(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: center;"><%=process.getVAL_ST_TYPE(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: center;"><%=process.getVAL_ST_AVENANT(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_ESSAI(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_MOTIF(indiceContrat)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_JUSTIFICATION(indiceContrat)%></td>
									</tr>
									<%
									indiceContrat++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if (process.getVAL_ST_ACTION().equals(process.ACTION_IMPRESSION)){ %>
				<div title="Saisie contrat">
					<span class="sigp2-saisie"><%=process.getVAL_ST_WARNING()%></span>
					<BR/><BR/>
				</div>
				<div style="text-align: center">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_IMPRIMER()%>">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
				</div>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) && process.getVAL_ST_CHOIX_CONTRAT().equals(process.CHOIX_CONTRAT_O)){ %>
				<div title="Création contrat">
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:70px;">Avenant : </span>
					<INPUT class="sigp2" type="radio" name="<%=process.getNOM_RG_AVENANT()%>" onclick='executeBouton("<%=process.getNOM_PB_AVENANT()%>")' <%= process.forRadioHTML(process.getNOM_RG_AVENANT(),process.getNOM_RB_AVENANT_O())%>> Oui
					<INPUT class="sigp2" type="radio" name="<%=process.getNOM_RG_AVENANT()%>" onclick='executeBouton("<%=process.getNOM_PB_AVENANT()%>")' <%= process.forRadioHTML(process.getNOM_RG_AVENANT(),process.getNOM_RB_AVENANT_N())%>> Non
					<span style="width:20px"></span>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:110px;">Type de contrat : </span>
					<% if(process.getVAL_RG_AVENANT().equals(process.getNOM_RB_AVENANT_O())){ %>
						<SELECT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_LB_TYPE_CONTRAT() %>" style="width:120px">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_CONTRAT(), process.getVAL_LB_TYPE_CONTRAT_SELECT()) %>
						</SELECT>
						<span style="width:20px"></span>
						<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:130px;">Contrat de référence : </span>
						<span class="sigp2-saisie" style="width:60px"><%=process.getVAL_ST_NUM_CONTRAT_REF()%></span>
					<% } else { %>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_CONTRAT() %>" style="width:120px">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_CONTRAT(), process.getVAL_LB_TYPE_CONTRAT_SELECT()) %>
						</SELECT>
					<% } %>
					<BR/><BR/>
				</div>
				<div style="text-align: center">
					<INPUT type="submit" class="sigp2-Bouton-100" value="OK" name="<%=process.getNOM_PB_OK()%>">
					<span style="width:30px"></span>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
				</div>
			<%}else if (!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
				<div title="Saisie contrat">
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:70px;">Avenant : </span>
					<INPUT class="sigp2"type="radio"	disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_AVENANT(),process.getNOM_RB_AVENANT_O())%>> Oui
					<INPUT class="sigp2" type="radio"	disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_AVENANT(),process.getNOM_RB_AVENANT_N())%>> Non
					<span style="width:20px"></span>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:110px;">Type de contrat : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_CONTRAT() %>" style="width:120px" disabled="disabled">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_CONTRAT(), process.getVAL_LB_TYPE_CONTRAT_SELECT()) %>
					</SELECT>
					<%if(process.getVAL_RG_AVENANT().equals(process.getNOM_RB_AVENANT_O())){ %>
						<span style="width:20px"></span>
						<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:130px;">Contrat de référence : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NUM_CONTRAT_REF()%></span>
					<% } %>
					<BR/><HR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:130px;">Date de début : </span>
					
					<%if(process.getVAL_RG_AVENANT().equals(process.getNOM_RB_AVENANT_O())){ %>
						<%if(process.getVAL_LB_TYPE_CONTRAT_SELECT().equals("0")){ %>
							<span style="width:120px">
							<INPUT class="sigp2-saisie" disabled="disabled" maxlength="10" name="<%= process.getNOM_EF_DATE_DEB() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEB() %>">
							</span>
						<%} else{%>	
							<span style="width:120px">
							<INPUT id="<%=process.getNOM_EF_DATE_DEB()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DEB() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEB() %>">
							<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_DEB() %>', 'dd/mm/y');">
							</span>
						<%} %>
					<% } else { %>
						<span style="width:120px">
						<INPUT id="<%=process.getNOM_EF_DATE_DEB()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DEB() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEB() %>" onblur='executeBouton("<%=process.getNOM_PB_INIT_FIN_PERIODE_ESSAI()%>")'>
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_DEB() %>', 'dd/mm/y');">
						</span>
					<% } %>
					<%if(process.getVAL_LB_TYPE_CONTRAT_SELECT().equals("0")){ %>
						<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:110px;">Date de fin : </span>
						<INPUT id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN() %>" onblur='executeBouton("<%=process.getNOM_PB_INIT_FIN_PERIODE_ESSAI()%>")'>
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_FIN() %>', 'dd/mm/y');">
						<%}else{ %>
						<span class="sigp2" style="margin-left:20px;position:relative;width:110px;">Date de fin : </span>
						<INPUT id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_FIN() %>', 'dd/mm/y');">
						<%} %>
					<%if(!process.getVAL_RG_AVENANT().equals(process.getNOM_RB_AVENANT_O())){ %>					
						<%if(process.getVAL_LB_TYPE_CONTRAT_SELECT().equals("0")){ %>
							<BR/><BR/>
							<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:180px;">Date de fin de période d'essai : </span>
						<%}else{ %>
							<BR/><BR/>
							<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:180px;">Date de fin de période d'essai : </span>
						<%} %>
						<span style="width:120px">
							<INPUT id="<%=process.getNOM_EF_DATE_FIN_PERIODE_ESSAI()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN_PERIODE_ESSAI() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN_PERIODE_ESSAI() %>">
							<IMG src="images/calendrier.gif" hspace="5"  onclick="return showCalendar('<%= process.getNOM_EF_DATE_FIN_PERIODE_ESSAI() %>', 'dd/mm/y');">					
						</span>
					<%} %>
					
					<BR/><BR/>
					<%if(process.getVAL_RG_AVENANT().equals(process.getNOM_RB_AVENANT_O()) && process.getVAL_ST_TYPE_CONTRAT().equals("CDD")){ %>
						<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:130px;">Motif : </span>
						<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>" >
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
						<br/>
						<br/>
						<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:130px;">Justification : </span>
						<INPUT disabled="disabled" class="sigp2-saisiemajusculenongras" maxlength="100" name="<%= process.getNOM_EF_JUSTIFICATION() %>" size="100"
							type="text" value="<%= process.getVAL_EF_JUSTIFICATION() %>">
					<% } else { %>
						<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:130px;">Motif : </span>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>" >
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
						<br/>
						<br/>
						<span class="sigp2Mandatory" style="margin-left:10px;position:relative;width:130px;">Justification : </span>
						<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_JUSTIFICATION() %>" size="100"
							type="text" value="<%= process.getVAL_EF_JUSTIFICATION() %>">
					<% } %>
					<BR/><BR/>
				</div>
				<div style="text-align: center">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
				</div>
			<% } else { %>
			<div>
				<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
					<FONT color='red'><%=process.getVAL_ST_WARNING() %></FONT>
					<BR/><BR/>
				<% } %>
				<table>
					<tr>
						<td width="100px">
							<span class="sigp2">Numéro de contrat : </span>
						</td>
						<td width="100px">
							<span class="sigp2-saisie"><%=process.getVAL_ST_NUM_CONTRAT()%></span>
						</td>
						<td width="100px">
							<span class="sigp2">Type de contrat : </span>
						</td>
						<td colspan="2" width="200px">
							<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE_CONTRAT()%></span>
						</td>
					</tr>
					<tr>
						<td>
							<span class="sigp2">Avenant : </span>
						</td>
						<td>				
							<span class="sigp2">
								<INPUT type="radio"	disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_AVENANT(),process.getNOM_RB_AVENANT_O())%>><span class="sigp2">Oui</span>
								<INPUT type="radio"	disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_AVENANT(),process.getNOM_RB_AVENANT_N())%>><span class="sigp2">Non</span>
							</span>
						</td>
						<td>
							<span class="sigp2">Contrat de référence : </span>
						</td>
						<td colspan="2">
							<span class="sigp2-saisie"><%=process.getVAL_ST_NUM_CONTRAT_REF()%></span>
						</td>
					</tr>
					<tr>
						<td>
							<span class="sigp2" >Date de début : </span>
						</td>
						<td>			
							<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEB()%></span>
						</td>
						<td>
							<span class="sigp2">Date de fin de période d'essai : </span>
						</td>
						<td >
							<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN_PERIODE_ESSAI()%></span>
						</td>
						<td>
							<span class="sigp2">Date de fin : </span>
						</td>
						<td >
							<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
						</td>
					</tr>
					<tr>
						<td>
							<span class="sigp2" >Motif : </span>
						</td>
						<td>			
							<span class="sigp2-saisie"><%=process.getVAL_ST_MOTIF()%></span>
						</td>
						<td>
							<span class="sigp2">Justification : </span>
						</td>
						<td colspan="2">
							<span class="sigp2-saisie"><%=process.getVAL_EF_JUSTIFICATION()%></span>
						</td>
					</tr>
				</table>
			</div>
			<div style="text-align: center">
				<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
				<% } %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
			<%} %>
		</FIELDSET>
		<!-- Boutons cachés -->
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_INIT_FIN_PERIODE_ESSAI()%>" value="x">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AVENANT()%>" value="y">
<%} %>
<%=process.getUrlFichier()%>
</FORM>
<%} %>
</BODY>
</HTML>