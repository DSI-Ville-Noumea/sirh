<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeGroupeAbsence"%>
<%@page import="nc.mairie.gestionagent.absence.dto.TypeAbsenceDto"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
		<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des absences</TITLE>


        <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
        <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
        <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
            
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
		
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#CongesExcep').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSortable":false,"bSearchable":false},null,null,null],
							"sDom": '<"H"fl>t<"F"i>',
							"sScrollY": "575px",
							"aaSorting": [[ 1, "asc" ]],
							"bPaginate": false
					    });
					} );
				</script>
		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsenceConges" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<table width="1030px;">
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Gestion des congés exceptionnels</legend>
							<table cellpadding="0" cellspacing="0" border="0" class="display" id="CongesExcep"> 
			                    <thead>
			                        <tr>
			                            <th>
			                            	<img src="images/ajout.gif" height="16px" width="16px" title="Creer une absence" onClick="executeBouton('<%=process.getNOM_PB_AJOUTER_CONGES()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
			            				</th>  
			                            <th>Type</th>
			                            <th>Unite de décompte</th>	
			                            <th>Info</th>	                            
			                        </tr>
			                    </thead>
			                    <tbody>
			                        <%for (TypeAbsenceDto abs : process.getListeTypeAbsence()) {
			                			if (abs.getGroupeAbsence() == null
			                					|| abs.getGroupeAbsence().getIdRefGroupeAbsence() != EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
			                				continue;
			                			}
			                        	int indiceAbs = abs.getIdRefTypeAbsence();
			                        %>
			                        <tr>
			                            <td align="center">
			                            	<INPUT title="Modifiere" type="image" src="images/modifier.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_CONGES(indiceAbs)%>">
                           				</td>                            
			                            <td><%=process.getVAL_ST_TYPE_CONGE(indiceAbs)%></td> 
			                            <td><%=process.getVAL_ST_UNITE(indiceAbs)%></td>  
			                            <td><%=process.getVAL_ST_INFO(indiceAbs)%></td>  
			                        </tr>
			                        <%}%>
			                    </tbody>
			                </table>
						</FIELDSET>	
					</td>					
				</tr>
			</table>
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_CONGES()%>" value="AJOUTERCONGES">        
		</FORM>
	</BODY>
</HTML>