<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
<script type="text/javascript" src="js/avancementPrepaCAP.js"></script>
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
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTFonctPrepaCAP"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Tri des avancements à afficher</legend>
			<span class="sigp2" style="width:75px">Année : </span>
			<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=70px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Changer" name="<%=process.getNOM_PB_CHANGER_ANNEE()%>">
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des fonctionnaires</legend>
			<BR/>
				<table class="display" id="tabAvctFonct">
					<thead>
						<tr>
							<th>NumAvct</th>
							<th>Dir. <br> Sect.</th>
							<th>Nom <br> Prénom <br> Matr</th>
							<th>Cat <br> Filière</th>
							<th>Carr Simu</th>
							<th>Code grade <br> Ancien <br> Nouveau</th>
							<th>Libellé grade <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ancien&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br> Nouveau</th>
							<th>Date début</th>
							<th>Date Avct Mini <br> Moy <br> Maxi</th>	
							<th>Motif Avct <br> <br> Avis SHD</th>	
							<th>Durée VDN</th>	
							<th>Class. Ordre mérite</th>	
							<th>Verif SEF <br> 							
								<INPUT type="checkbox" name="CHECK_ALL_SEF" onClick='activeSEF("<%=process.getListeAvct().size() %>")'>
							</th>
							<th>Vérifié par</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeAvct()!=null){
						for (int indiceAvct = 0;indiceAvct<process.getListeAvct().size();indiceAvct++){
							AvancementFonctionnaires avct = (AvancementFonctionnaires) process.getListeAvct().get(indiceAvct);
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CATEGORIE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CARRIERE_SIMU(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE_LIB(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_DEBUT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
								<% if (process.getVAL_CK_VALID_SEF(indiceAvct).equals(process.getCHECKED_ON())){ %>
								<td>
									<SELECT disabled="disabled"  name="<%= process.getNOM_LB_AVIS_CAP(indiceAvct) %>" class="sigp2-liste" >
											<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP(indiceAvct), process.getVAL_LB_AVIS_CAP_SELECT(indiceAvct)) %>
									</SELECT>
								</td>
								<td>
									<%if(avct.getIdAvisCAP()!=null && avct.getIdMotifAvct()!=null){%>
										<%if(avct.getIdMotifAvct().equals("7") && (avct.getIdAvisCAP().equals("1")||avct.getIdAvisCAP().equals("3"))){%>
										<INPUT disabled="disabled" class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
										<%}else{%>
										&nbsp;
										<%} %>
									<%}else{ %>
										&nbsp;
									<%} %>
								</td>								
								<%}else{%>
								<td>
									<SELECT onchange='activeOrdreMerite("<%=indiceAvct %>")' name="<%= process.getNOM_LB_AVIS_CAP(indiceAvct) %>" class="sigp2-liste" >
											<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP(indiceAvct), process.getVAL_LB_AVIS_CAP_SELECT(indiceAvct)) %>
									</SELECT>
								</td>
								<td>
									<%if(avct.getIdAvisCAP()!=null && avct.getIdMotifAvct()!=null){%>
										<%if(avct.getIdMotifAvct().equals("7") && (avct.getIdAvisCAP().equals("1")||avct.getIdAvisCAP().equals("3"))){%>
										<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
										<%}else{%>
										<INPUT style="visibility: hidden;" class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
										<%} %>
									<%}else{ %>
										<INPUT style="visibility: hidden;" class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
									<%} %>
								</td>							
								<%} %>
								
								<td><INPUT type="checkbox" onClick='validSEF("<%=indiceAvct %>")'  <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_SEF(indiceAvct),process.getVAL_CK_VALID_SEF(indiceAvct))%>></td>
								<td><%=process.getVAL_ST_USER_VALID_SEF(indiceAvct)%></td>
								
							</tr>
					<%
						}
					}
					%>
					</tbody>
				</table>
				<% if (!process.agentEnErreur.equals("")){ %>
					<span class="sigp2Mandatory">Agents en anomalies : <%=process.agentEnErreur %></span>
					<BR/><BR/>
					<span class="sigp2Mandatory">Pour ces agents une ligne de carrière n'a pu être crée car il y avait déjà une carrière suivante de saisie. Merci de corriger manuellement les carrières de ces agents.</span>
				<%} %>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonct').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,{"bSearchable":false},{"bSearchable":false},null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false}],
							"sDom": '<"H"fl>t<"F"iT>',
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
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>

		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Impression des tableaux d'avancement différencié et des EAE</legend>
		    <BR/>
				<table class="display" id="tabAvctFonctImpr">
					<thead>
						<tr>
							<th>Code CAP</th>
							<th>Cadre Emploi</th>
							<th>Consulter</th>
							<th>Tableau<br> 							
								<INPUT type="checkbox" name="CHECK_ALL_TAB" onClick='activeTab("<%=process.getListeImpression().size() %>")'>
							</th>
							<th>Tableau + EAEs<br> 							
								<INPUT type="checkbox" name="CHECK_ALL_EAE" onClick='activeEae("<%=process.getListeImpression().size() %>")'>
							</th>
							<th>Imprimé par</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeImpression()!=null){
						for (int indiceImpr = 0;indiceImpr<process.getListeImpression().size();indiceImpr++){
					%>
							<tr>
								<td><%=process.getVAL_ST_CODE_CAP(indiceImpr)%></td>
								<td><%=process.getVAL_ST_CADRE_EMPLOI(indiceImpr)%></td>
								<td align="center" ><INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_TABLEAU(indiceImpr)%>"></td>
								<td align="center" ><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_TAB(indiceImpr),process.getVAL_CK_TAB(indiceImpr))%>></td>
								<td align="center" ><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_EAE(indiceImpr),process.getVAL_CK_EAE(indiceImpr))%>></td>								
								<td><%=process.getVAL_ST_USER_IMPRESSION(indiceImpr)%></td>
								
							</tr>
					<%
						}
					}
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonctImpr').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [null,null,null,null,null,null],
							"sDom": '<"H"fl>t<"F"iT>',
							"bPaginate": false,
					    });
					} );
				</script>
			<BR/>
		</FIELDSET>
	</FORM>
</BODY>
</HTML>