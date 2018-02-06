<%@page import="nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurTaskDto"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurDto"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>

<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGTitreRepasEtatPayeur" id="process" scope="session"></jsp:useBean>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Edition du payeur des titres repas</TITLE>		


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

function startCompteurDeversement(duree){
	<%if(process.isEtatPayeurEnCours()){%>
	var o=document.getElementById("boxDeversement");
	if(duree >= 0) {
		//on format les minutes et les secondes
		var minutes = Math.floor(duree/60) +"m";
		var secondes = Math.floor(duree%60)+"s";
		o.innerHTML = "Début de la génération de l'état du payeur dans "+ minutes +secondes;
		setTimeout("startCompteurDeversement("+duree+"-1)", 1000);
	} else {
		o.innerHTML ="Génération en cours.";					
	}	
	<%}%>
}

function ConfirmMessage(nom) {
    if (confirm("Attention, les saisies ne seront plus possibles sur ce mois. Confirmez la génération?")) { // Clic sur OK
		executeBouton(nom);
    }
}
</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();startCompteurDeversement('300');" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		
		<br />
		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Filtres pour l'affichage</legend>
                		
				<%if(process.isEtatPayeurEnCours()){%>						
                	<span style="color: red;">Une état est en cours. Vous ne pouvez effectuer de génération.</span>
					<INPUT type="button" class="sigp2-Bouton-100" value="Générer" disabled="disabled">
                	<INPUT type="submit" class="sigp2-Bouton-200" value="En cours, rafraichîr" name="<%=process.getNOM_PB_RAFRAICHIR()%>">
		        <%}else{ %>
					<span class="sigp2Mandatory">Fichier issu de tickets restaurants : </span> 
					<% if(process.fichierUpload == null){ %>
						<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
					<%}else{ %>
						<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
					<% }%>
					<br />
					<br />
					<span class="sigp2Mandatory">Générer l'état du payeur et le fichier prestataire pour le mois en cours</span>
                	<INPUT type="button" class="sigp2-Bouton-100" value="Générer" onclick="ConfirmMessage('<%=process.getNOM_PB_GENERER()%>');">                	
				<%} %>
                <br/>
                
                
                <INPUT type="submit"  style="display:none;" value="Générer les états payeurs" name="<%=process.getNOM_PB_GENERER()%>">                
				<span style="color: red;" id="boxDeversement"></span>
             </FIELDSET>
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Historique des éditions des titres repas</legend>			
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:650px;">
				<thead>
                        <tr>
                            <th width="210px" align="center">Imprimé le à <br> PAR</th>  
                            <th width="290px" align="center">Mois</th>  
                            <th width="290px" align="center">Consulter etat payeur</th>  
                            <th width="290px" align="center">Consulter etat prestataire</th>                             
                        </tr>
                    </thead>
					<%
						for (int i = 0; i < process.getListEtatsPayeurDto().size(); i++){
							TitreRepasEtatPayeurDto etatPayeur = process.getListEtatsPayeurDto().get(i);
							%>
						<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>, <%=process.getListEtatsPayeurDto().size()%>)">
							
							<td class="sigp2NewTab-liste" style="position:relative;width:210px;text-align: center;"><%=process.getVAL_ST_USER_DATE_EDITION(i) %></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:290px;text-align: center;"><%=process.getVAL_ST_LIBELLE_EDITION(i) %></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: center;" align="center">
								<a class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" href="<%=etatPayeur.getUrlAlfresco() %>" title="<%=etatPayeur.getLabel() %>" target="_blank" >
									<img onkeydown="" onkeypress="" onkeyup="" src="images/oeil.gif" height="16px" width="16px" title="Voir l'état du payeur" />
								</a>
				    		</td>
				    		<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: center;" align="center">
								<a class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" href="<%=etatPayeur.getUrlAlfrescoPrestataire() %>" title="<%=etatPayeur.getLabelPrestataire() %>" target="_blank" >
									<img onkeydown="" onkeypress="" onkeyup="" src="images/oeil.gif" height="16px" width="16px" title="Voir l'etat prestataire" />
								</a>
				    		</td>
						</tr>
					<% } %>
				</table>
			</div>
		
		</FIELDSET>	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Historique des erreurs sur les éditions du payeur des titres repas</legend>			
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:650px;">
				<thead>
                        <tr>
                            <th width="210px" align="center">le à <br> PAR</th>  
                            <th width="290px" align="center">Mois</th>  
                            <th width="290px" align="center">Erreur</th>                             
                        </tr>
                    </thead>
					<%
						for (int i = 0; i < process.getListErreurTaskEtatsPayeurDto().size(); i++){
							TitreRepasEtatPayeurTaskDto etatPayeurTask = process.getListErreurTaskEtatsPayeurDto().get(i);
							%>
						<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>, <%=process.getListErreurTaskEtatsPayeurDto().size()%>)">
							
							<td class="sigp2NewTab-liste" style="position:relative;width:210px;text-align: center;"><%=process.getVAL_ST_USER_DATE_EDITION_TASK(i) %></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:290px;text-align: center;"><%=process.getVAL_ST_LIBELLE_EDITION_TASK(i) %></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: center;" align="center"><%=process.getVAL_ST_ERREUR_TASK(i) %></td>
						</tr>
					<% } %>
				</table>
			</div>
		
		</FIELDSET>	
		
	</FORM>
	</BODY>
</HTML>