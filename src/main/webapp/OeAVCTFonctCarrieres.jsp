<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.avancement.AvancementFonctionnaires"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des avancements des fonctionnaires</TITLE>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
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
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTFonctCarrieres"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
<script type="text/javascript">
function activeMajDateAvct() {						
	<%
	for (int j = 0;j<process.getListeAvct().size();j++){
		AvancementFonctionnaires avct = (AvancementFonctionnaires) process.getListeAvct().get(j);
		Integer i = avct.getIdAvct();
	%>
	var box = document.formu.elements['NOM_CK_MAJ_DATE_AVCT_'+<%=i%>];  		
		if(document.formu.elements['CHECK_ALL_MAJ_DATE_AVCT'].checked){
			if(box!=null && !box.disabled){	
			box.checked=true;  
		}			
  	}else{
			if(box!=null && !box.disabled){	
			box.checked=false;	 
		}		
	}
	<%}%>
}
function activeAffecter() {						
	<%
	for (int j = 0;j<process.getListeAvct().size();j++){
		AvancementFonctionnaires avct = (AvancementFonctionnaires) process.getListeAvct().get(j);
		Integer i = avct.getIdAvct();
	%>
	var box = document.formu.elements['NOM_CK_AFFECTER_'+<%=i%>];  		
		if(document.formu.elements['CHECK_ALL_AFFECTER'].checked){
			if(box!=null && !box.disabled){	
			box.checked=true;  
			validAffecter(<%=i%>);
		}			
  	}else{
			if(box!=null && !box.disabled){	
			box.checked=false;	 
			validAffecter(<%=i%>);
		}		
	}
	<%}%>
}
	function validAffecter(indice) {	
		  var box = document.formu.elements['NOM_CK_AFFECTER_'+indice];
		  if (box != null && box.checked)
		  {
			document.formu.elements['NOM_ST_NUM_ARRETE_'+indice].disabled = true; 
		  }
		  else
		  { 
			document.formu.elements['NOM_ST_NUM_ARRETE_'+indice].disabled = false; 
		  }
}
</script>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Tri des avancements à afficher</legend>
			<span class="sigp2" style="width:75px">Année : </span>
			<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=70px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:75px">Filière : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FILIERE() %>" style="width=200px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_FILIERE(), process.getVAL_LB_FILIERE_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:75px">Par agent :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" >
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          	<span class="sigp2" style="width:75px;">Service :</span>
				<INPUT id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
          		<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" 
					value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">
				
	             	<div style="margin-left:500px;" class="sigp2">
	             	<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
					<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
					<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
					</div>
          	<BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER()%>">
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Date de l'arrêté</legend>
          	<span class="sigp2" style="width:100px">Date de l'arrêté : </span>
			<input id="<%=process.getNOM_ST_DATE_ARR_GLOBALE()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_ARR_GLOBALE() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_ARR_GLOBALE() %>" >
			<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_ARR_GLOBALE()%>', 'dd/mm/y');">
			<BR/><BR/>
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des fonctionnaires</legend>
		    
				<% if (!process.agentEnErreur.equals("")){ %>
					<span style="color: red;" class="sigp2Mandatory">Agents en anomalies : <%=process.agentEnErreur %></span>
					<BR/><BR/> 
					<span style="color: red;" class="sigp2Mandatory">Pour ces agents, aucun avancement n'a pu être calculé.</span>
				<%} %>
				
			<BR/><BR/><BR/>
				<table class="display" id="tabAvctFonct">
					<thead>
						<tr>
							<th>NumAvct</th>
							<th>Dir. <br> Sect.</th>
							<th>Matr</th>
							<th>Nom <br> Prénom</th>
							<th>Cat <br> Filière</th>
							<th>PA</th>
							<th>Code grade <br> Ancien</th>
							<th>Code grade <br> Nouveau</th>
							<th>Libel. grade <br> Ancien <br> Nouveau</th>
							<th>Date CAP</th>
							<th>Date AVCT</th>
							<th>Num Arrete</th>
							<th>Date Arrete</th>
							<th>Carrière de <br> simulation</th>
							<th>MAJ<br>date avct<br><INPUT type="checkbox" name="CHECK_ALL_MAJ_DATE_AVCT" onClick='activeMajDateAvct()'></th>
							<th> A affecter
								<br><INPUT type="checkbox" name="CHECK_ALL_AFFECTER" onClick='activeAffecter()'>
							</th>
							<th>Affecté <br> le <br> par</th>
						</tr>
					</thead>
					<tbody>
					<%
						for (int i = 0;i<process.getListeAvct().size();i++){
							AvancementFonctionnaires avct = process.getListeAvct().get(i);
							Integer indiceAvct = avct.getIdAvct();
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
								<td><%=process.getVAL_ST_MATRICULE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CATEGORIE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_PA(indiceAvct)%></td>
								<td align="center" ><%=process.getVAL_ST_GRADE_ANCIEN(indiceAvct)%></td>
								<td align="center" ><%=process.getVAL_ST_GRADE_NOUVEAU(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE_LIB(indiceAvct)%></td>
								<td align="center" ><%=process.getVAL_ST_DATE_CAP(indiceAvct)%></td>
								<td align="center" ><%=process.getVAL_ST_DATE_AVCT(indiceAvct)%></td>
								<td align="center" >
									<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_NUM_ARRETE(indiceAvct) %>" size="10"  type="text" value="<%= process.getVAL_ST_NUM_ARRETE(indiceAvct) %>" >
								</td>
								<td align="center" ><%=process.getVAL_ST_DATE_ARRETE(indiceAvct)%></td>
								<td align="center" ><%=process.getVAL_ST_CARRIERE_SIMU(indiceAvct)%></td>
								<td align="center" >
									<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_MAJ_DATE_AVCT(indiceAvct),process.getVAL_CK_MAJ_DATE_AVCT(indiceAvct))%>>									
								</td>
								<td align="center" >
								<%if(avct.getCarriereSimu()==null || !avct.getCarriereSimu().toUpperCase().equals("S")){ %>
								<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'>
								<%}else{ %>
								<INPUT title="rafraichir" type="image" src="images/refresh.png" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_RAFRAICHIR(indiceAvct)%>">
								<%} %>
								</td>
								<td><%=process.getVAL_ST_USER_VALID_CARRIERE(indiceAvct)%></td>
							</tr>
					<%
						}
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonct').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,null,{"bSearchable":false},{"bSearchable":false},null,null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false,"bSortable":false},{"bSearchable":false,"bSortable":false},{"bSearchable":false}],
							"sDom": '<"H"l>t<"F"iT>',
							"sScrollY": "375px",
							"bPaginate": false,
							"oTableTools": {
								"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"avctFonctionnaires","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
								"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
							}
					    });
					} );
				</script>
			<BR/>
		</FIELDSET>

		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Mettre à jour" name="<%=process.getNOM_PB_MAJ_DATE_AVCT()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Générer" name="<%=process.getNOM_PB_AFFECTER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENTEVALUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENTEVALUE">
	</FORM>
</BODY>
</HTML>