<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<TITLE>Gestion des compteurs</TITLE>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 

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


function testClickEnrigistrer(){
	if(event.keyCode == 13){
		executeBouton('NOM_PB_VALIDER');
	}
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA52" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM onkeypress="testClickEnrigistrer();" name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Décharge de service CTP</legend>			
				<div style="overflow: auto;height: 250px;width:1000px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="50px;">
									<img title="ajouter" border="0" src="images/ajout.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_AJOUTER()%>');" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								</td>
								<td width="300px;">Organisation syndicale</td>
								<td align="center" width="90px;">Début</td>
								<td align="center" width="90px;">Fin</td>
								<td align="center" width="90px;">Nb heures</td>
								<td align="center" width="90px;">Motif</td>
								<td>Représentants</td>
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
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_FIN(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_HEURES(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_MOTIF(i)%></td>
										<td class="sigp2NewTab-liste">										
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISU_REPRESENTANT(i)%>">
				    						<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISU_REPRESENTANT(i)%>">
				    						<%if(process.peutModifierCompteur(i)){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_REPRESENTANT(i)%>">
											<%} %>				
										</td>
									</tr>
									<%
								}
							}%>
						</table>	
				</div>	
		</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%> <%=process.getOrganisationCourante().getSigle() %></legend>
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
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEBUT() %>">
						<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN() %>">
						<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Nb heures :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_NB_HEURES() %>" size="4" type="text"  value="<%= process.getVAL_ST_NB_HEURES() %>">
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
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)){ %>
			
			<table>
				<tr>
					<td width="135px;">
						<span class="sigp2Mandatory">Oragnisation syndicale :</span>
					</td>
					<td>
						<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_OS() %>">
							<%=process.forComboHTML(process.getVAL_LB_OS(), process.getVAL_LB_OS_SELECT()) %>
						</SELECT>
                    </td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_ST_DATE_DEBUT() %>" size="10" type="text"  value="<%= process.getVAL_ST_DATE_DEBUT()%>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de fin :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_ST_DATE_FIN() %>" size="10" type="text"  value="<%= process.getVAL_ST_DATE_FIN() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Nb heures :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_ST_NB_HEURES() %>" size="10" type="text"  value="<%= process.getVAL_ST_NB_HEURES() %>">
					</td>
				</tr>				
				<tr>
					<td width="135px;">
						<span class="sigp2Mandatory">Motif :</span>
					</td>
					<td>
						<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>">
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
                    </td>
				</tr>
				<tr>
					<td colspan="2">
                        <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">	
					</td>
				</tr>
			</table>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_REPRESENTANT)){ %>			
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="50px;">
									<img title="ajouter" border="0" src="images/ajout.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_AJOUTER_REPRESENTANT()%>');" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								</td>
								<td width="300px">Agent</td>
								<td>Actif</td>
							</tr>
							<%
							if (process.getListeRepresentant()!=null){
								for (int j = 0;j<process.getListeRepresentant().size();j++){
									Integer i = process.getListeRepresentant().get(j).getIdAgent();
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeRepresentant().size()%>)">
										<td class="sigp2NewTab-liste" align="center">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_REPRE(i)%>">
											<%if(process.peutSupprimerAgent(i)){ %>
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_REPRE(i)%>">
											<%} %>
										</td>
										<td class="sigp2NewTab-liste" width="300px;"><%=process.getVAL_ST_AGENT_REPRESENTANT(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT_REPRESENTANT_ACTIF(i)%></td>
									</tr>
									<%
								}
							}%>
						</table>
						<br/><br/>
						<INPUT type="submit" class="sigp2-Bouton-200" value="Valider les modifications" name="<%=process.getNOM_PB_VALIDER_REPRESENTANT()%>">
                        <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">	
						<%if(!process.getVAL_ST_ACTION_REPRESENTANT().equals(Const.CHAINE_VIDE)){ %>
						<br/><br/><br/>
						<fieldset  class="sigp2Fieldset" >
							<% if(process.getVAL_ST_ACTION_REPRESENTANT().equals(process.ACTION_CREATION_REPRE)){ %>
								<span class="sigp2Mandatory" style="width:70px">Agent :</span>
	                       		<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>" style="margin-right:10px;">
	                        	<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>');">                        
								<span class="sigp2Mandatory" style="width:50px">Actif : </span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_REPRESENTANT_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_REPRESENTANT_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>					
								<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-200" value="Ajouter à la liste" name="<%=process.getNOM_PB_CREATE()%>">	 
		                    <%}else if(process.getVAL_ST_ACTION_REPRESENTANT().equals(process.ACTION_MODIFICATION_REPRE)){ %>
								<span class="sigp2Mandatory" style="width:70px">Agent :</span>
	                       		<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_ST_AGENT_CREATE()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>" style="margin-right:10px;">
	                        	<span class="sigp2Mandatory" style="width:50px">Actif : </span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_REPRESENTANT_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_REPRESENTANT_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>					
								<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-200" value="Modifier l'entrée" name="<%=process.getNOM_PB_CREATE()%>">	 
		                    <%} %>
		                </fieldset>
						<%} %>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_VISU_REPRESENTANT)){ %>
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="300px" >Agent</td>
								<td>Actif</td>
							</tr>
							<%
							if (process.getListeRepresentant()!=null){
								for (int j = 0;j<process.getListeRepresentant().size();j++){
									Integer i = process.getListeRepresentant().get(j).getIdAgent();
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeRepresentant().size()%>)">
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT_REPRESENTANT(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT_REPRESENTANT_ACTIF(i)%></td>
									</tr>
									<%
								}
							}%>
						</table>
			<%} %>
		</FIELDSET>
		<%} %>

    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER()%>" value="AJOUTER">
    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_REPRESENTANT()%>" value="AJOUTERREPRESENTANT">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>" value="RECHERCHERAGENTCREATE">
	</FORM>
</BODY>
</HTML>