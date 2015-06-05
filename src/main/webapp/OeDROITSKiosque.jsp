<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des droits du kiosque</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>

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

// afin d'afficher la hiérarchie des services
function agrandirHierarchy() {

	hier = 	document.getElementById('treeHierarchy');

	if (hier.style.display!='none') {
		reduireHierarchy();
	} else {
		hier.style.display='block';
	}
}

// afin de cacher la hiérarchie des services
function reduireHierarchy() {
	hier = 	document.getElementById('treeHierarchy');
	hier.style.display='none';
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.droits.process.OeDROITSKiosque"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Filtres pour l'affichage des approbateurs</legend>
				<span class="sigp2" style="width:70px;">Service :</span>
				<INPUT id="service" class="sigp2-saisie" readonly="readonly"
					name="<%= process.getNOM_EF_SERVICE() %>" style="margin-right:10px;width:100px"
					type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"
					height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
          		<INPUT type="hidden" id="codeservice" size="4" name="<%=process.getNOM_ST_CODE_SERVICE() %>" 
					value="<%=process.getVAL_ST_CODE_SERVICE() %>" class="sigp2-saisie">
				<div id="treeHierarchy" style="display: none; height: 340; width: 500; overflow:auto; background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;">
					<script type="text/javascript">
						d = new dTree('d');
						d.add(0,-1,"Services");
						
						<%
						String serviceSaisi = process.getVAL_EF_SERVICE().toUpperCase();
						int theNode = 0;
						for (int i =1; i <  process.getListeServices().size(); i++) {
							Service serv = (Service)process.getListeServices().get(i);
							String code = serv.getCodService();
							TreeHierarchy tree = (TreeHierarchy)process.getHTree().get(code);
							if (theNode ==0 && serviceSaisi.equals(tree.getService().getSigleService())) {
								theNode=tree.getIndex();
							}
						%>
						<%=tree.getJavaScriptLine()%>
						<%}%>
						document.write(d);
				
						d.closeAll();
						<% if (theNode !=0) { %>
							d.openTo(<%=theNode%>,true);
						<%}%>
					</script>
				</div>
				<span class="sigp2" style="width:60px">Par agent :</span>
				<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
				<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          		<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          		<BR/><BR/><BR/>
				<INPUT type="submit" value="Afficher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_AFFICHER()%>">
				<BR><BR>
		</FIELDSET>
			
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Liste des approbateurs des pointages / absences</legend>
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
			<table class="sigp2NewTab" style="text-align:left;width:980px;">
				<tr>
					<td><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>"></td>
					<td><span><INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")'<%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_AGENT())%>> Agent</span></td>
					<td><span><INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")'<%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_SERVICE())%>> Service</span></td>
					<td align="center"><span>PTG</span></td>
					<td align="center"><span>Droit <br> PTG</span></td>
					<td align="center"><span>Délég <br> PTG</span></td>
					<td align="center"><span>ABS</span></td>
					<td align="center"><span>Droit <br> ABS</span></td>
					<td align="center"><span>Délég <br> ABS</span></td>					
				</tr>
				<%
				for (int indice = 0;indice<process.getListeApprobateurs().size();indice++){
					int i = process.getListeApprobateurs().get(indice).getApprobateur().getIdAgent();
				%>
					<tr>
						<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
				    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(i)%>">
				    	</td>
						<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_AGENT(i)%></td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_SERVICE(i)%></td>
						<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: center;">
							<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_DROIT_PTG(i),process.getVAL_CK_DROIT_PTG(i))%> onClick='executeBouton("<%=process.getNOM_PB_SET_APPROBATEUR_PTG(i)%>")'>
							<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SET_APPROBATEUR_PTG(i)%>" value="DATE">
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: center;">
							<%if(process.peutModifierDelegatairePTG(i)){ %>
								<INPUT title="Gérer les droits des pointages" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_GERER_DROIT_PTG(i)%>">
							<%} %>		    										
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: center; min-height:15px;"><%=process.getVAL_ST_DELEGATAIRE_PTG(i)%>
							<%if(process.peutModifierDelegatairePTG(i)){ %>
							<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DELEGATAIRE_PTG(i)%>">
				    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATAIRE_PTG(i)%>">
				    		<%} %>			    										
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: center;">
							<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_DROIT_ABS(i),process.getVAL_CK_DROIT_ABS(i))%> onClick='executeBouton("<%=process.getNOM_PB_SET_APPROBATEUR_ABS(i)%>")'>
							<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SET_APPROBATEUR_ABS(i)%>" value="DATE">
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: center;">
							<%if(process.peutModifierDelegataireABS(i)){ %>
							<INPUT title="Gérer les droits des absences" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_GERER_DROIT_ABS(i)%>">
							<%} %>		    										
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: center; min-height:15px;"><%=process.getVAL_ST_DELEGATAIRE_ABS(i)%>
							<%if(process.peutModifierDelegataireABS(i)){ %>
							<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DELEGATAIRE_ABS(i)%>">
				    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATAIRE_ABS(i)%>">				    										
							<%} %>		    										
						</td>
					</tr>
				<%}%>
				</table>	
			</div>	
        </FIELDSET>
        
        <%if(process.getVAL_ST_ACTION().equals(process.ACTION_GERER_DROIT_ABS)){ %>
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
	            <legend class="sigp2Legend"><%=process.ACTION_GERER_DROIT_ABS %> <%=process.getApprobateurCourant().getNom() %></legend>
		            <fieldset>
		            	<legend>Agents à approuver par l'approbateur</legend>
		            	<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr>
								<td>
									<INPUT title="Ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT_APPRO_ABS()%>">
									<INPUT title="Agents mairie" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_ABS()%>">
							    </td>
								<td><span>Agent</span></td>										
							</tr>
							<%
							for (int indice = 0;indice<process.getListeAgentsApprobateurAbs().size();indice++){
								int i = process.getListeAgentsApprobateurAbs().get(indice).getIdAgent();
							%>
								<tr>
									<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
							    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT_APPRO_ABS(i)%>">
							    	</td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_AGENT_APPRO(i)%></td>
								</tr>
							<%}%>
						</table>	
		            </fieldset>
			        <BR/><BR/>
		            <fieldset>
		            	<legend>Opérateurs de l'approbateur</legend>
		            	<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr>
								<td><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT_OPE_ABS()%>"></td>
								<td><span>Agent</span></td>										
							</tr>
							<%
							for (int indice = 0;indice<process.getListeAgentsOperateurAbs().size();indice++){
								int i = process.getListeAgentsOperateurAbs().get(indice).getIdAgent();
							%>
								<tr>
									<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
							    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT_OPE_ABS(i)%>">
							    		<INPUT title="Gérer les agents de l'opérateur" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_GERER_AGENT_OPE_APPRO_ABS(i)%>">
							    	</td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_AGENT_OPE(i)%></td>
								</tr>
							<%}%>
						</table>
		            </fieldset>
			        <BR/><BR/>
		            <fieldset>
		            	<legend>Viseurs de l'approbateur</legend>
		            	<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr>
								<td><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT_VISEUR_ABS()%>"></td>
								<td><span>Agent</span></td>										
							</tr>
							<%
							for (int indice = 0;indice<process.getListeAgentsViseurAbs().size();indice++){
								int i = process.getListeAgentsViseurAbs().get(indice).getIdAgent();
							%>
								<tr>
									<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
							    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT_VISEUR_ABS(i)%>">
							    		<INPUT title="Gérer les agents du viseur" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_GERER_AGENT_VISEUR_APPRO_ABS(i)%>">
							    	</td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_AGENT_VISEUR(i)%></td>
								</tr>
							<%}%>
						</table>
		            </fieldset>		            
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>
	            </FIELDSET>
        <%} else if(process.getVAL_ST_ACTION().equals(process.ACTION_GERER_DROIT_PTG)){ %>
             <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
	            <legend class="sigp2Legend"><%=process.ACTION_GERER_DROIT_PTG %> <%=process.getApprobateurCourant().getNom() %></legend>
		            <fieldset>
		            	<legend>Agents à approuver par l'approbateur</legend>
		            	<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr>
								<td>
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT_APPRO_PTG()%>">
									<INPUT title="Agents mairie" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_AJOUTER_AGENT_MAIRIE_APPRO_PTG()%>">
								</td>
								<td><span>Agent</span></td>										
							</tr>
							<%
							for (int indice = 0;indice<process.getListeAgentsApprobateurPtg().size();indice++){
								int i = process.getListeAgentsApprobateurPtg().get(indice).getIdAgent();
							%>
								<tr>
									<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
							    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT_APPRO_PTG(i)%>">
							    	</td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_AGENT_APPRO(i)%></td>
								</tr>
							<%}%>
						</table>	
		            </fieldset>
			        <BR/><BR/>
		            <fieldset>
		            	<legend>Opérateurs de l'approbateur</legend>
		            	<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr>
								<td><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT_OPE_PTG()%>"></td>
								<td><span>Agent</span></td>										
							</tr>
							<%
							for (int indice = 0;indice<process.getListeAgentsOperateurPtg().size();indice++){
								int i = process.getListeAgentsOperateurPtg().get(indice).getIdAgent();
							%>
								<tr>
									<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
							    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT_OPE_PTG(i)%>">
							    		<INPUT title="Gérer les agents de l'opérateur" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_GERER_AGENT_OPE_APPRO_PTG(i)%>">
							    	</td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_AGENT_OPE(i)%></td>
								</tr>
							<%}%>
						</table>
		            </fieldset>		            
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>
	            </FIELDSET>
        <%} %>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_TRI()%>" value="TRI">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">
	</FORM>
</BODY>
</HTML>