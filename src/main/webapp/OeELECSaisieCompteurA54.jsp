<%@page import="nc.mairie.metier.Const"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des compteurs</TITLE>
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

function testClickEnrigistrer(){
	if(event.keyCode == 13){
		executeBouton('NOM_PB_VALIDER');
	}
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA54" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM onkeypress="testClickEnrigistrer();" name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Congrès et conseil syndical</legend>	
				<span class="sigp2" style="width:40px">Année : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE_FILTRE() %>" style="width:100px;margin-right:20px;">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE_FILTRE(), process.getVAL_LB_ANNEE_FILTRE_SELECT()) %>
				</SELECT>
	          	<INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER()%>">
	          	<% if(process.isDuplicationPossible()){ %>
	          		<INPUT type="submit" class="sigp2-Bouton-200" value="Dupliquer sur l'année suivante" name="<%=process.getNOM_PB_DUPLIQUER()%>">
	          	<% }%>
	          	<BR/><BR/>
					
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="50px;">
								    <img title="ajouter" border="0" src="images/ajout.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_AJOUTER()%>');" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								</td>
								<td align="center" width="60px;">Matricule</td>
								<td width="300px;"><INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_AGENT())%>>Agent</TD>
								<td width="100px;"><INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_OS())%>>OS</TD>
								<td align="center" width="50px;">Année</td>
								<td align="center" width="90px;">Nb jours</td>
								<td align="center" width="20px;">Actif</td>
								<td align="center">Motif</td>
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
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_MATRICULE(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT_OS(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_ANNEE(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_JOURS(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_ACTIF(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_MOTIF(i)%></td>
									</tr>
									<%
								}
							}%>
						</table>	
				</div>	
		</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) && (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)|| process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
			
			<table>
				<tr>
					<td width="70px;">
						<span class="sigp2Mandatory">Agent :</span>
					</td>
					<td>
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>">
                        	<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>');">
                        <%}else{ %>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" disabled="disabled" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>">
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
					<td>
						<span class="sigp2Mandatory">Actif :</span>
					</td>
					<td>
						<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_AGENT_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
						<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_AGENT_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>					
								
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
					<td width="70px;">
						<span class="sigp2Mandatory">Agent :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" disabled="disabled" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>">
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
					<td>
						<span class="sigp2Mandatory">Motif :</span>
					</td>
					<td>
						<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>">
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Actif :</span>
					</td>
					<td>
						<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_AGENT_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
						<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_AGENT_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>					
								
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
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Groupement par OS</legend>			
				<div style="overflow: auto;height: 250px;width:1000px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="100px;">Sigle</td>
								<td width="700px;">Organisation syndicale</td>
								<td>Représentants</td>
							</tr>
							<%
								for (int j = 0;j<process.getListeOrganisationSyndicale().size();j++){
									Integer i = process.getListeOrganisationSyndicale().get(j).getIdOrganisation();
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeOrganisationSyndicale().size()%>)">
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_SIGLE_OS(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_OS(i)%></td>
										<td class="sigp2NewTab-liste">										
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISU_REPRESENTANT(i)%>">
				    						<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISU_REPRESENTANT(i)%>">
				    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_REPRESENTANT(i)%>">
										</td>
									</tr>
									<%
								}
							%>
						</table>	
						<BR/><BR/>
						
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_VISU_REPRESENTANT)){ %>
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="300px" >Agent</td>
							</tr>
							<%
							if (process.getListeRepresentant()!=null){
								for (int j = 0;j<process.getListeRepresentant().size();j++){
									Integer i = process.getListeRepresentant().get(j).getIdAgent();
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeRepresentant().size()%>)">
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT_REPRESENTANT(i)%></td>
									</tr>
									<%
								}
							}%>
						</table>			
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_REPRESENTANT)){ %>			
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="20px;">
									<img title="ajouter" border="0" src="images/ajout.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_AJOUTER_REPRESENTANT()%>');" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								</td>
								<td width="300px">Agent</td>
							</tr>
							<%
							if (process.getListeRepresentant()!=null){
								for (int j = 0;j<process.getListeRepresentant().size();j++){
									Integer i = process.getListeRepresentant().get(j).getIdAgent();
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeRepresentant().size()%>)">
										<td class="sigp2NewTab-liste" align="center">
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_REPRESENTANT(i)%>">
										</td>
										<td class="sigp2NewTab-liste" width="300px;"><%=process.getVAL_ST_AGENT_REPRESENTANT(i)%></td>
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
							<% if(process.getVAL_ST_ACTION_REPRESENTANT().equals(process.ACTION_CREATION_REPRESENTANT)){ %>
								<span class="sigp2Mandatory" style="width:70px">Agent :</span>
	                       		<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>" style="margin-right:10px;">
	                        	<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>');">                        
								<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-200" value="Ajouter à la liste" name="<%=process.getNOM_PB_CREATE()%>">	 
		                    <%} %>
		                </fieldset>
						<%} %>
			<%}%>
				</div>	
		</FIELDSET>

    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER()%>" value="AJOUTER">
    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_REPRESENTANT()%>" value="AJOUTERREPRESENTANT">
    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>" value="RECHERCHERAGENTCREATE">
	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_TRI()%>" value="TRI">
	</FORM>
</BODY>
</HTML>