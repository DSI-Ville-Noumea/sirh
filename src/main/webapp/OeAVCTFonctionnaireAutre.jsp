<%@page import="nc.mairie.metier.avancement.AvancementFonctionnaires"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumEtatAvancement"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<lINK rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des avancements des fonctionnaires territoriaux ou AVCT = AUTO</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
<script type="text/javascript" src="js/avancementFonct.js"></script>

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
 class="nc.mairie.gestionagent.process.avancement.OeAVCTFonctionnaireAutre" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
	<script type="text/javascript">
		function activeDRH() {						
				<%
				for (int j = 0;j<process.getListeAvct().size();j++){
					AvancementFonctionnaires avct = process.getListeAvct().get(j);
					Integer i = avct.getIdAvct();
				%>
				var box = document.formu.elements['NOM_CK_VALID_DRH_'+<%=i%>];  		
		  		if(document.formu.elements['CHECK_ALL_DRH'].checked ){
		  			if(box!=null && !box.disabled){			
						box.checked=true; 
						validDRH(<%=i%>);  
					}			
		  		}else{
		  			if(box!=null && !box.disabled){		
						box.checked=false; 
						validDRH(<%=i%>);
					}
				}
				<%}%>
		}
		function activeProjet() {						
			<%
			for (int j = 0;j<process.getListeAvct().size();j++){
				AvancementFonctionnaires avct = process.getListeAvct().get(j);
				Integer i = avct.getIdAvct();
			%>
			var box = document.formu.elements['NOM_CK_PROJET_ARRETE_'+<%=i%>]; 
				var boxDRH = document.formu.elements['NOM_CK_VALID_DRH_'+<%=i%>];   		
				if(document.formu.elements['CHECK_ALL_PROJET'].checked && boxDRH!=null && boxDRH.checked){
					if(box!=null && !box.disabled){		
					box.checked=true; 
					validProjet(<%=i%>);
				}
				}else{
					if(box!=null && !box.disabled){	
					box.checked=false; 
					validProjet(<%=i%>);
				}
			}
			<%}%>
		}
		function activeAffecter() {						
			<%
			for (int j = 0;j<process.getListeAvct().size();j++){
				AvancementFonctionnaires avct = process.getListeAvct().get(j);
				Integer i = avct.getIdAvct();
			%>
			var box = document.formu.elements['NOM_CK_AFFECTER_'+<%=i%>];  
				var boxProjet = document.formu.elements['NOM_CK_PROJET_ARRETE_'+<%=i%>];  		
				if(document.formu.elements['CHECK_ALL_AFFECTER'].checked && boxProjet!=null && boxProjet.checked){
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
	</script>
	
<script type="text/javascript">
function activeImpr() {						
	<%
	for (int j = 0;j<process.getListeAvct().size();j++){
		AvancementFonctionnaires avct = (AvancementFonctionnaires) process.getListeAvct().get(j);
		Integer i = avct.getIdAvct();
	%>
	var box = document.formu.elements['NOM_CK_VALID_ARR_IMPR_' + <%=i%>];
	if (document.formu.elements['CHECK_ALL_IMPR'].checked) {
		if (box != null && !box.disabled) {
			box.checked = true;
		}
	} else {
		if (box != null && !box.disabled) {
			box.checked = false;
		}
	}
	<%}%>
}
function activeRegul() {						
	<%
	for (int j = 0;j<process.getListeAvct().size();j++){
		AvancementFonctionnaires avct = (AvancementFonctionnaires) process.getListeAvct().get(j);
		Integer i = avct.getIdAvct();
	%>
	var box = document.formu.elements['NOM_CK_REGUL_ARR_IMPR_' + <%=i%>];
	if (document.formu.elements['CHECK_ALL_REGUL'].checked) {
		if (box != null && !box.disabled) {
			box.checked = true;
		}
	} else {
		if (box != null && !box.disabled) {
			box.checked = false;
		}
	}
	<%}%>
}
</script>
	
	
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Tri des avancements à afficher</legend>
			<span class="sigp2" style="width:75px">Année : </span>
			<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=70px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER()%>">
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des détachés</legend>
			<BR/>
				<table class="display" id="tabAvctFonct">
					<thead>
						<tr>
							<th rowspan="2">NumAvct</th>
							<th rowspan="2">Dir. <br> Sect.</th>
							<th rowspan="2">Matr</th>
							<th rowspan="2">Nom <br> Prénom</th>
							<th rowspan="2">Cat <br> Filière</th>
							<th rowspan="2">PA</th>
							<th rowspan="2">Carr Simu</th>
							<th rowspan="2">Code grade <br> Ancien</th>
							<th rowspan="2">Code grade <br> Nouveau</th>
							<th rowspan="2">Libel. grade <br> Ancien <br> Nouveau</th>
							<th rowspan="2">Date début</th>
							<th colspan="3">BM <br> Ancien <br> Nouveau</th>
							<th colspan="3">ACC <br> Ancien <br> Nouveau</th>
							<th rowspan="2">Durée Std</th>
							<th rowspan="2">Date Avct Mini <br> Moy <br> Maxi</th>
							<th rowspan="2">Valid. DRH							
								<INPUT type="checkbox" name="CHECK_ALL_DRH" onClick='activeDRH()'>
							</th>
							<th rowspan="2">Motif Avct</th>
							<th rowspan="2">Avis CAP</th>
							<th rowspan="2">Projet Arrete
								<INPUT type="checkbox" name="CHECK_ALL_PROJET" onClick='activeProjet()'>
							</th>
							<th rowspan="2">Num Arrete</th>
							<th rowspan="2">Date Arrete</th>
							<th rowspan="2">Regul. <br> <INPUT type="checkbox" name="CHECK_ALL_REGUL" onClick='activeRegul()'></th>
							<th rowspan="2">A imprimer<br> <INPUT type="checkbox" name="CHECK_ALL_IMPR" onClick='activeImpr()'></th>
							<th rowspan="2">Affecter
								<INPUT type="checkbox" name="CHECK_ALL_AFFECTER" onClick='activeAffecter()'>
							</th>
						</tr>
						<tr>
							<th>a</th>
							<th>m</th>
							<th>j</th>
							<th>a</th>
							<th>m</th>
							<th>j</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeAvct()!=null){
						for (int j = 0;j<process.getListeAvct().size();j++){
							Integer indiceAvct = process.getListeAvct().get(j).getIdAvct();
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
								<td><%=process.getVAL_ST_MATRICULE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CATEGORIE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_PA(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CARRIERE_SIMU(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE_ANCIEN(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE_NOUVEAU(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE_LIB(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_DEBUT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_BM_A(indiceAvct)%></td>
								<td><%=process.getVAL_ST_BM_M(indiceAvct)%></td>
								<td><%=process.getVAL_ST_BM_J(indiceAvct)%></td>
								<td><%=process.getVAL_ST_ACC_A(indiceAvct)%></td>
								<td><%=process.getVAL_ST_ACC_M(indiceAvct)%></td>
								<td><%=process.getVAL_ST_ACC_J(indiceAvct)%></td>
								<td><%=process.getVAL_ST_PERIODE_STD(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_AVCT(indiceAvct)%></td>
								<% if (process.getVAL_CK_AFFECTER(indiceAvct).equals(process.getCHECKED_ON())){ %>
									<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>							
									<td>
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP(indiceAvct) %>" class="sigp2-liste" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP(indiceAvct), process.getVAL_LB_AVIS_CAP_SELECT(indiceAvct)) %>
										</SELECT>
									</td>
									<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT disabled="disabled" class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT disabled="disabled" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT disabled="disabled" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_REGUL_ARR_IMPR(indiceAvct),process.getVAL_CK_REGUL_ARR_IMPR(indiceAvct))%>>
									</td>										
									<td>	
										<INPUT disabled="disabled" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR_IMPR(indiceAvct),process.getVAL_CK_VALID_ARR_IMPR(indiceAvct))%>>
									</td>								
									<%if(process.getVAL_ST_ETAT(indiceAvct).equals("E")){ %>
										<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> ></td>																
									<%}else{ %>
										<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>																
									<%} %>
								<%}else if(process.getVAL_CK_PROJET_ARRETE(indiceAvct).equals(process.getCHECKED_ON())){ %>
									<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>							
									<td>
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP(indiceAvct) %>" class="sigp2-liste" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP(indiceAvct), process.getVAL_LB_AVIS_CAP_SELECT(indiceAvct)) %>
										</SELECT>
									</td>	
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>	
									<td>
										<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_REGUL_ARR_IMPR(indiceAvct),process.getVAL_CK_REGUL_ARR_IMPR(indiceAvct))%>>
									</td>									
									<td>	
										<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR_IMPR(indiceAvct),process.getVAL_CK_VALID_ARR_IMPR(indiceAvct))%>>
									</td>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>																
								<%}else if(process.getVAL_CK_VALID_DRH(indiceAvct).equals(process.getCHECKED_ON())){ %>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>							
									<td>
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP(indiceAvct) %>" class="sigp2-liste">
											<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP(indiceAvct), process.getVAL_LB_AVIS_CAP_SELECT(indiceAvct)) %>
										</SELECT>
									</td>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>	
									<td>
										<INPUT type="checkbox" style="visibility: hidden;" <%= process.forCheckBoxHTML(process.getNOM_CK_REGUL_ARR_IMPR(indiceAvct),process.getVAL_CK_REGUL_ARR_IMPR(indiceAvct))%>>
									</td>									
									<td>	
										<INPUT type="checkbox" style="visibility: hidden;" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR_IMPR(indiceAvct),process.getVAL_CK_VALID_ARR_IMPR(indiceAvct))%>>
									</td>
									<td><INPUT type="checkbox" style="visibility:hidden" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>								
								<%}else{ %>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>							
									<td>
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP(indiceAvct) %>" class="sigp2-liste" style="visibility:hidden">
											<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP(indiceAvct), process.getVAL_LB_AVIS_CAP_SELECT(indiceAvct)) %>
										</SELECT>
									</td>		
									<td><INPUT type="checkbox" style="visibility:hidden" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>	
									<td>
										<INPUT type="checkbox" style="visibility: hidden;" <%= process.forCheckBoxHTML(process.getNOM_CK_REGUL_ARR_IMPR(indiceAvct),process.getVAL_CK_REGUL_ARR_IMPR(indiceAvct))%>>
									</td>									
									<td>	
										<INPUT type="checkbox" style="visibility: hidden;" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR_IMPR(indiceAvct),process.getVAL_CK_VALID_ARR_IMPR(indiceAvct))%>>
									</td>
									<td><INPUT type="checkbox" style="visibility:hidden" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>
								<%} %>
							</tr>
					<%
						}
					}
					%>
					</tbody>
				</table>
				<% if (!process.agentEnErreur.equals("")){ %>
					<span style="color: red;" class="sigp2Mandatory">Agents en anomalies : <%=process.agentEnErreur %></span>
					<BR/><BR/>
					<span style="color: red;" class="sigp2Mandatory">Pour ces agents, aucun avancement n'a pu être calculé.</span>
				<%} %>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonct').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false},{"bSortable":false,"bSearchable":false}],
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
			<div align="center">			
				<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Générer" name="<%=process.getNOM_PB_AFFECTER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Imprimer" name="<%=process.getNOM_PB_IMPRIMER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
		</FIELDSET>
	<%=process.getUrlFichier()%>
		</FORM>
</BODY>
</HTML>