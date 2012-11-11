<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		
		<TITLE>Historique du suivi médical</TITLE>	

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
			
	<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 

		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.OeSMHistorique" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Historique des visites médicales du travail</legend>
			<span class="sigp2" style="width:35px">Mois : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOIS() %>" style="width=90px;">
				<%=process.forComboHTML(process.getVAL_LB_MOIS(), process.getVAL_LB_MOIS_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:45px;margin-left:20px;">Année : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=60px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<INPUT  style="margin-left:20px;" type="submit" class="sigp2-Bouton-100" value="Rechercher" name="<%=process.getNOM_PB_RECHERCHER()%>">
		</FIELDSET>	
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;height:580px;">
		    <legend class="sigp2Legend">Historique</legend>
			<BR/>
			<div>
				<table class="display" id="tabHistoSuiviMed">
					<thead>
						<tr>
							<th>NumSuiviMed</th>
							<th>Matr.</th>
							<th>Agent</th>
							<th>Statut</th>
							<th>Serv.</th>
							<th>Motif</th>
							<th>Medecin</th>
							<th>Date RDV</th>
							<th>Heure RDV</th>
							<th>Avis</th>
							<th>Effectuée</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeHistoSuiviMed()!=null){
						for (int indiceSM = 0;indiceSM<process.getListeHistoSuiviMed().size();indiceSM++){
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_SM(indiceSM)%></td>
								<td><%=process.getVAL_ST_MATR(indiceSM)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceSM)%></td>
								<td><%=process.getVAL_ST_STATUT(indiceSM)%></td>
								<td><%=process.getVAL_ST_SERVICE(indiceSM)%></td>						
								<td><%=process.getVAL_ST_MOTIF(indiceSM)%></td>	
				    			<td><%=process.getVAL_ST_MEDECIN(indiceSM)%></td>					
								<td><%=process.getVAL_ST_DATE_RDV(indiceSM)%></td>					
								<td><%=process.getVAL_ST_HEURE_RDV(indiceSM)%></td>					
								<td><%=process.getVAL_ST_AVIS(indiceSM)%></td>					
								<td><%=process.getVAL_ST_EFFECTUE(indiceSM)%></td>	
							</tr>
					<%
						}
					}
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabHistoSuiviMed').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,null,null,null,null,null,null,null,null],
							"sDom": '<"H"fl>t<"F"iT>',
							"bPaginate": false,
							"oTableTools": {
								"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"historiqueSuiviMedical","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
								"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
							}
					    });
					} );
				</script>
			</div>
			<BR/>
		</FIELDSET>
	</FORM>
	</BODY>
</HTML>