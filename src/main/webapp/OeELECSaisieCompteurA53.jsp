<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des compteurs ASA</TITLE>
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
<jsp:useBean
 class="nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA53" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Formation syndicale (A53)</legend>			
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="50px;">
								    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
								</td>
								<td width="300px;">Organisation syndicale</td>
								<td align="center" width="50px;">Année</td>
								<td align="center">Nb jours</td>
							</tr>
							<%
							if (process.getListeCompteur()!=null){
								for (int i = 0;i<process.getListeCompteur().size();i++){
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeCompteur().size()%>)">
										<td class="sigp2NewTab-liste" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(i)%>">
				    						<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(i)%>">
				    						<%if(process.peutModifierCompteur(i)){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(i)%>">
											<%} %>				
										</td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_OS(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_ANNEE(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_JOURS(i)%></td>
									</tr>
									<%
								}
							}%>
						</table>	
				</div>	
		</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
			
			<table>
				<tr>
					<td width="135px;">
						<span class="sigp2Mandatory">Organisation syndicale :</span>
					</td>
					<td>
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_OS() %>">
								<%=process.forComboHTML(process.getVAL_LB_OS(), process.getVAL_LB_OS_SELECT()) %>
							</SELECT>
                        <%}else{ %>
							<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_OS() %>">
								<%=process.forComboHTML(process.getVAL_LB_OS(), process.getVAL_LB_OS_SELECT()) %>
							</SELECT>
                        <%} %>
                    </td>
				</tr>
				<tr>
					<td width="70px;">
						<span class="sigp2Mandatory">Année :</span>
					</td>
					<td>
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
								<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
							</SELECT>
                        <%}else{ %>
							<INPUT class="sigp2-saisie" disabled="disabled" maxlength="4" name="<%= process.getNOM_ST_ANNEE() %>" size="4" type="text"  value="<%= process.getVAL_ST_ANNEE() %>">
                        <%} %>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Nb jours :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_NB_JOURS() %>" size="4" type="text"  value="<%= process.getVAL_ST_NB_JOURS() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Motif :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>">
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
							<INPUT type="submit" class="sigp2-Bouton-100" value="Creer" name="<%=process.getNOM_PB_VALIDER()%>">
						<%}else{ %>
							<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER()%>">
						<%} %>	 
                        <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">	
					</td>
				</tr>
			</table>
			<%}else{ %>
			
			<table>
				<tr>
					<td width="135px;">
						<span class="sigp2Mandatory">Organisation syndicale :</span>
					</td>
					<td>
						<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_OS() %>">
							<%=process.forComboHTML(process.getVAL_LB_OS(), process.getVAL_LB_OS_SELECT()) %>
						</SELECT>
                    </td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Année :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" disabled="disabled" maxlength="4" name="<%= process.getNOM_ST_ANNEE() %>" size="4" type="text"  value="<%= process.getVAL_ST_ANNEE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Nb jours :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" disabled="disabled" maxlength="4" name="<%= process.getNOM_ST_NB_JOURS() %>" size="4" type="text"  value="<%= process.getVAL_ST_NB_JOURS() %>">
					</td>
				</tr>
				<tr>
					<td colspan="2">
                        <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">	
					</td>
				</tr>
			</table>
			<%} %>
		</FIELDSET>
		<%} %>

	</FORM>
</BODY>
</HTML>