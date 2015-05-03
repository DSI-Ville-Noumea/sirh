<%@page import="nc.mairie.gestionagent.absence.dto.MoisAlimAutoCongesAnnuelsDto"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
		<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.absence.OeABSAlimentationMensuelle" id="process" scope="session"></jsp:useBean>
            <TITLE>Alimentation mensuelle des congés annuels</TITLE>		


            <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
			<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
			<script type="text/javascript" src="js/jquery.dataTables.js"></script>
			<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

            <SCRIPT type="text/javascript">
                function executeBouton(nom)
                {
                    document.formu.elements[nom].click();
                }
                function setfocus(nom)
                {
                    if (document.formu.elements[nom] != null)
                        document.formu.elements[nom].focus();
                }
            </SCRIPT>		
            <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </HEAD>
        <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%=process.getFocus()%>')">	
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Alimentation mensuelle des congés annuels</legend>   
				<span class="sigp2Mandatory">Retrouver ici toutes les alimentations automatiques de congés annuels.</span>      
				<br/><br/>   
				<span class="sigp2" style="width:100px">Choisissez le mois à afficher : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOIS_ALIM_AUTO() %>" style="width:100px;">
					<%=process.forComboHTML(process.getVAL_LB_MOIS_ALIM_AUTO(), process.getVAL_LB_MOIS_ALIM_AUTO_SELECT()) %>
				</SELECT>  
	            <BR/><BR/>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher tout" name="<%=process.getNOM_PB_AFFICHER()%>">  
				<INPUT type="submit" class="sigp2-Bouton-200" value="Afficher les erreurs" name="<%=process.getNOM_PB_AFFICHER_ERREUR()%>">   
	            <BR/><BR/>		 
						<table class="display" id="tabAlimHisto">
							<thead>
								<tr>
									<th>Matricule</th>
									<th>Agent</th>
									<th>Erreur</th>
									<th>Info</th>			
								</tr>
							</thead>
							<tbody>
							<%
								for (int i = 0;i<process.getListeAlimAuto().size();i++){
									MoisAlimAutoCongesAnnuelsDto histo = process.getListeAlimAuto().get(i);
							%>
									<tr>
										<td><%=process.getVAL_ST_NOMATR_AGENT(i)%></td>
										<td><%=process.getVAL_ST_LIB_AGENT(i)%></td>
										<td><%=process.getVAL_ST_STATUT(i)%></td>
										<td><%=process.getVAL_ST_INFO(i)%></td>								
									</tr>
							<%
								}
							%>
							</tbody>
						</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAlimHisto').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [null,null,null,null],
							"sDom": '<"H"fl>t<"F"iT>',
							"sScrollY": "375px",
							"bPaginate": false,
							"oTableTools": {
								"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"alimAuto","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
								"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
							}
					    });
					} );
				</script>       
				
             </FIELDSET>             
            
            
        </FORM>
    </BODY>
</HTML>