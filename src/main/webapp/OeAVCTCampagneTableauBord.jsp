<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des avancements des conventions collectives</TITLE>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
<script type="text/javascript" src="js/avancementConvCol.js"></script>
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
 class="nc.mairie.gestionagent.process.avancement.OeAVCTCampagneTableauBord" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Calcul du tableau</legend>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
				</SELECT>
				<BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Calculer" name="<%=process.getNOM_PB_CALCULER()%>">			
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Tableau de bord</legend>
			<BR/>
			<table class="display" id="tabAvctCampTB">
				<thead>
					<tr>
						<th>Direction</th>
						<th>Service</th>
						<th>Section</th>
						<th>Sans évaluateur</th>
						<th>Non débuté</th>
						<th>Crée</th>
						<th>En cours</th>
						<th>Finalisé</th>
						<th>Contrôlé</th>
						<th>Total EAE</th>
						<th>CAP</th>
						<th>Non défini</th>
						<th>Mini</th>
						<th>Moy</th>
						<th>Maxi</th>
						<th>Chgt classe</th>
					</tr>
				</thead>
				<tbody>
				<%for (int indiceAvct = 0;indiceAvct<process.getListeTableauBord().size();indiceAvct++){%>
						<tr>
							<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
							<td><%=process.getVAL_ST_SERVICE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_SECTION(indiceAvct)%></td>
							<td><%=process.getVAL_ST_NON_AFF(indiceAvct)%></td>
							<td><%=process.getVAL_ST_NON_DEB(indiceAvct)%></td>
							<td><%=process.getVAL_ST_CREE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_EN_COURS(indiceAvct)%></td>
							<td><%=process.getVAL_ST_FINALISE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_CONTROLE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_TOTAL_EAE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_PASSAGE_CAP(indiceAvct)%></td>
							<td><%=process.getVAL_ST_NON_DEFINI(indiceAvct)%></td>
							<td><%=process.getVAL_ST_MINI(indiceAvct)%></td>
							<td><%=process.getVAL_ST_MOY(indiceAvct)%></td>
							<td><%=process.getVAL_ST_MAXI(indiceAvct)%></td>
							<td><%=process.getVAL_ST_CHANGEMENT_CLASSE(indiceAvct)%></td>
						</tr>
				<%}%>
				</tbody>
				<tfoot>
						<tr>
							<th></th>
							<th>Total</th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
				</tfoot>
			</table>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabAvctCampTB').dataTable({
						"fnFooterCallback": function ( nRow, aaData, iStart, iEnd, aiDisplay ) {
							/*
							 * Calculate the total market share for all browsers in this table (ie inc. outside
							 * the pagination)
							 */
							var totalNonAff = 0;
							var totalNonDeb = 0;
							var totalCree = 0;
							var totalEnCours = 0;
							var totalFinalise = 0;
							var totalControle = 0;
							var totalEAE = 0;
							var totalCAP = 0;
							var totalNonDefini = 0;
							var totalMini = 0;
							var totalMoy = 0;
							var totalMaxi = 0;
							var totalChangement = 0;
							for ( var i=0 ; i<aaData.length ; i++ )
							{
								if(isNaN(aaData[i][2])==false){
									totalNonAff +=  parseInt(aaData[i][2]);
								}
								if(isNaN(aaData[i][3])==false){
									totalNonDeb +=  parseInt(aaData[i][3]);
								}
								if(isNaN(aaData[i][4])==false){
									totalCree +=  parseInt(aaData[i][4]);
								}
								if(isNaN(aaData[i][5])==false){
									totalEnCours +=  parseInt(aaData[i][5]);
								}
								if(isNaN(aaData[i][6])==false){
									totalFinalise +=  parseInt(aaData[i][6]);
								}
								if(isNaN(aaData[i][7])==false){
									totalControle +=  parseInt(aaData[i][7]);
								}
								if(isNaN(aaData[i][8])==false){
									totalEAE +=  parseInt(aaData[i][8]);
								}
								if(isNaN(aaData[i][9])==false){
									totalCAP +=  parseInt(aaData[i][9]);
								}
								if(isNaN(aaData[i][10])==false){
									totalNonDefini +=  parseInt(aaData[i][10]);
								}
								if(isNaN(aaData[i][11])==false){
									totalMini +=  parseInt(aaData[i][11]);
								}
								if(isNaN(aaData[i][12])==false){
									totalMoy +=  parseInt(aaData[i][12]);
								}
								if(isNaN(aaData[i][13])==false){
									totalMaxi +=  parseInt(aaData[i][13]);
								}
								if(isNaN(aaData[i][14])==false){
									totalChangement +=  parseInt(aaData[i][14]);
								}
							}
							
							/* Calculate the market share for browsers on this page */
							var totalPageNonAff = 0;
							var totalPageNonDeb = 0;
							var totalPageCree = 0;
							var totalPageEnCours = 0;
							var totalPageFinalise = 0;
							var totalPageControle = 0;
							var totalPageTotalEAE = 0;
							var totalPageCAP = 0;
							var totalPageNonDefini = 0;
							var totalPageMini = 0;
							var totalPageMoy = 0;
							var totalPageMaxi = 0;
							var totalPageChangement = 0;
							for ( var i=iStart ; i<iEnd ; i++ )
							{
								if(isNaN(aaData[i][2])==false){
									totalPageNonAff += parseInt(aaData[i][2]);
								}
								if(isNaN(aaData[i][3])==false){
									totalPageNonDeb += parseInt(aaData[i][3]);
								}
								if(isNaN(aaData[i][4])==false){
									totalPageCree += parseInt(aaData[i][4]);
								}
								if(isNaN(aaData[i][5])==false){
									totalPageEnCours += parseInt(aaData[i][5]);
								}
								if(isNaN(aaData[i][6])==false){
									totalPageFinalise += parseInt(aaData[i][6]);
								}
								if(isNaN(aaData[i][7])==false){
									totalPageControle += parseInt(aaData[i][7]);
								}
								if(isNaN(aaData[i][8])==false){
									totalPageTotalEAE += parseInt(aaData[i][8]);
								}
								if(isNaN(aaData[i][9])==false){
									totalPageCAP += parseInt(aaData[i][9]);
								}
								if(isNaN(aaData[i][10])==false){
									totalPageNonDefini += parseInt(aaData[i][10]);
								}
								if(isNaN(aaData[i][11])==false){
									totalPageMini += parseInt(aaData[i][11]);
								}
								if(isNaN(aaData[i][12])==false){
									totalPageMoy += parseInt(aaData[i][12]);
								}
								if(isNaN(aaData[i][13])==false){
									totalPageMaxi += parseInt(aaData[i][13]);
								}
								if(isNaN(aaData[i][14])==false){
									totalPageChangement += parseInt(aaData[i][14]);
								}
							}
							
							/* Modify the footer row to match what we want */
							var nCells = nRow.getElementsByTagName('th');
							nCells[2].innerHTML = parseInt(totalPageNonAff) +	' ('+ parseInt(totalNonAff ) +')';
							nCells[3].innerHTML = parseInt(totalPageNonDeb) +	' ('+ parseInt(totalNonDeb ) +')';
							nCells[4].innerHTML = parseInt(totalPageCree) +	' ('+ parseInt(totalCree ) +')';
							nCells[5].innerHTML = parseInt(totalPageEnCours) +	' ('+ parseInt(totalEnCours ) +')';
							nCells[6].innerHTML = parseInt(totalPageFinalise) +	' ('+ parseInt(totalFinalise ) +')';
							nCells[7].innerHTML = parseInt(totalPageControle) +	' ('+ parseInt(totalControle ) +')';
							nCells[8].innerHTML = parseInt(totalPageTotalEAE) +	' ('+ parseInt(totalEAE ) +')';
							nCells[9].innerHTML = parseInt(totalPageCAP) +	' ('+ parseInt(totalCAP ) +')';
							nCells[10].innerHTML = parseInt(totalPageNonDefini) +	' ('+ parseInt(totalNonDefini ) +')';
							nCells[11].innerHTML = parseInt(totalPageMini) +	' ('+ parseInt(totalMini ) +')';
							nCells[12].innerHTML = parseInt(totalPageMoy) +	' ('+ parseInt(totalMoy ) +')';
							nCells[13].innerHTML = parseInt(totalPageMaxi) +	' ('+ parseInt(totalMaxi ) +')';
							nCells[14].innerHTML = parseInt(totalPageChangement) +	' ('+ parseInt(totalChangement ) +')';
							
						},
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [null,null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false}],
						"sDom": '<"H"fl>t<"F"iT>',
						"oTableTools": {
							"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"tableauBordCampagne","sFileName":"*.xls"}], //OU : "mColumns":[1,2,3,4]
							"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
						},
						"sScrollY": "375px",
						"bPaginate": false
						
				    });
				} );
			</script>
			<BR/>
		</FIELDSET>
	</FORM>
</BODY>
</HTML>