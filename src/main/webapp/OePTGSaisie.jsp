<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.dto.ConsultPointageDto"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		<TITLE>Visualisation des pointages</TITLE>		

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>


<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>

<SCRIPT language="JavaScript">
		//afin de s�lectionner un �l�ment dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone pr�cise
		function setfocus(nom)
		{
			if (document.formu.elements[nom] != null)
			document.formu.elements[nom].focus();
		}

		// afin d'afficher la hi�rarchie des services
		function agrandirHierarchy() {

			hier = 	document.getElementById('treeHierarchy');

			if (hier.style.display!='none') {
				reduireHierarchy();
			} else {
				hier.style.display='block';
			}
		}

		// afin de cacher la hi�rarchie des services
		function reduireHierarchy() {
			hier = 	document.getElementById('treeHierarchy');
			hier.style.display='none';
		}
		
		</SCRIPT>		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGSaisie" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Filtres pour l'affichage</legend>
			<span class="sigp2Mandatory" style="width:80px">Date d�but : </span>
			<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MIN() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MIN() %>" >
			<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MIN()%>', 'dd/mm/y');">
			<span class="sigp2Mandatory" style="width:80px"></span>
			<span class="sigp2" style="width:100px">Agent min :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MIN() %>" style="margin-right:10px;">
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
          	<span class="sigp2Mandatory" style="width:80px"></span>
			<span class="sigp2" style="width:40px">Etat : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT() %>" style="width=150px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ETAT(), process.getVAL_LB_ETAT_SELECT()) %>
			</SELECT>
			<BR/><BR/>
			<span class="sigp2" style="width:80px">Date fin : </span>
			<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MAX() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MAX() %>" >
			<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MAX()%>', 'dd/mm/y');">
			<span class="sigp2Mandatory" style="width:80px"></span>
			<span class="sigp2" style="width:100px">Agent max :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MAX() %>" style="margin-right:10px;">
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
          	<span class="sigp2Mandatory" style="width:80px"></span>
			<span class="sigp2" style="width:40px">Type : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE() %>" style="width=150px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_TYPE(), process.getVAL_LB_TYPE_SELECT()) %>
			</SELECT>
			<BR/><BR/>
			<span class="sigp2" style="width:75px;">Service :</span>
				<INPUT tabindex="" id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
          		<INPUT type="hidden" id="codeservice" size="4" name="<%=process.getNOM_ST_CODE_SERVICE() %>" 
					value="<%=process.getVAL_ST_CODE_SERVICE() %>" class="sigp2-saisie">
				<div id="treeHierarchy" style="display: none;margin-left:100px;margin-top:20px; height: 340; width: 500; overflow:auto; background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;">
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
			<BR/><BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>">		
			<BR/><BR/>				
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des pointages</legend>
			<BR/>
			<table class="display" id="tabPTG">
				<thead>
					<tr>
						<th>&nbsp;</th>
						<th>Nom <br> Pr�nom <br> Matr</th>
						<th>Type </th>
						<th>Date</th>
					</tr>
				</thead>
				<tbody>
				<%for (int i = 0;i<process.getListePointage().size();i++){
					ConsultPointageDto ptg = process.getListePointage().get(i);
					Integer indicePtg = ptg.getIdPointage();
				%>
						<tr>
							<td>&nbsp;</td>
							<td><%=process.getVAL_ST_AGENT(indicePtg)%></td>
							<td><%=process.getVAL_ST_TYPE(indicePtg)%></td>
							<td><%=process.getVAL_ST_DATE(indicePtg)%></td>							
						</tr>
				<%}%>
				</tbody>
			</table>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#"tabPTG"').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [null,null,null,null],
						"sDom": '<"H"fl>t<"F"iT>',
						"sScrollY": "375px",
						"bPaginate": false,
						"oTableTools": {
							"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"pointages","sFileName":"*.xls"}], //OU : "mColumns":[1,2,3,4]
							"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
						}
				    });
				} );
			</script>
			<BR/>	
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des fonctionnaires</legend>
			<BR/>
				<table class="display" id="tabAvctFonct">
					<thead>
						<tr>
							<th rowspan="2">V�rifi� par</th>
						</tr>
					</thead>
					<tbody>
							<tr>
								<td>&nbsp;</td>
						    </tr>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonct').dataTable({			    						    
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [null],
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
		
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>" value="RECHERCHERAGENTMIN">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>" value="SUPPRECHERCHERAGENTMIN">	
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>" value="RECHERCHERAGENTMAX">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>" value="SUPPRECHERCHERAGENTMAX">	
	</FORM>
	</BODY>
</HTML>