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
		<jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGSaisie" id="process" scope="request"></jsp:useBean>
		<TITLE>Saisie des pointages</TITLE>		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
		
		<!-- <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>  -->
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
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
	
		function back(){
			document.location.href = './OePTGVisualisation.jsp';
		}
		
        /**
         * Build the table containing the detail of the pointage
         */
        function buildDetailTable(data) {

            var detailContainer = $(document.createElement("div"));

            // Build the new table that will handle the detail (append the thead and all tr, th)
            var detailTable = $(document.createElement("table"))
                              .addClass("detailContent")
                              .addClass("subDataTable")
                              .attr("cellpadding", "0")
                              .attr("cellspacing", "0")
                              .css({ width: "100%" })
                              .append($(document.createElement("thead"))
                              .append($(document.createElement("tr"))
                              .append($(document.createElement("th")).html("Date"))
                              .append($(document.createElement("th")).html("D&eacute;but"))
                              .append($(document.createElement("th")).html("Fin"))
                              .append($(document.createElement("th")).html("Dur&eacute;e<br />Quantit&eacute;"))
                              .append($(document.createElement("th")).html("Motif<br />Commentaire"))
                              .append($(document.createElement("th")).html("Etat"))
                              .append($(document.createElement("th")).html("Date de saisie"))
                      ));

            // Append the tbody element
            var tbody = $(document.createElement("tbody")).appendTo(detailTable);

           
            var pointages=data.split("|");
            
          //  alert(" pointages.length " +pointages.length);
            // Loop on data
            for (var i = 0; i < pointages.length; i++) {
                var donnees= pointages[i].split(",");
                tbody.append($(document.createElement("tr"))
                             .addClass(i % 2 == 0 ? "even" : "odd")
                             .append($(document.createElement("td")).html(donnees[0]))
                             .append($(document.createElement("td")).html(donnees[1]))
                             .append($(document.createElement("td")).html(donnees[2]))
                             .append($(document.createElement("td")).html(donnees[3]))
                             .append($(document.createElement("td")).html(donnees[4]))
                             .append($(document.createElement("td")).html(donnees[5]))
                             .append($(document.createElement("td")).html(donnees[6]))
                             );
            }

            // Append the detail table into the detail container
            detailContainer.append(detailTable);
            // Finally return the table
            return detailContainer;
        }
	    </SCRIPT>		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<% process.setIdAgent(request.getParameter("idAgent")); 
		   process.setDateLundi(request.getParameter("dateLundi"));
		   %>
	</HEAD>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		 <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Saisie des pointages pour l'agent <%=process.getIdAgent() %> semaine <%=process.getWeekYear() %></legend>
			<BR/>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationPointageList"> 
   				 <thead>
					<tr>
						<th><%=process.getDateLundiStr(0) %> </th>
						<th><%=process.getDateLundiStr(1) %></th>
						<th><%=process.getDateLundiStr(2) %></th>
						<th><%=process.getDateLundiStr(3) %></th>
						<th><%=process.getDateLundiStr(4) %></th>
						<th><%=process.getDateLundiStr(5) %></th>
						<th><%=process.getDateLundiStr(6) %></th>
					</tr>
				</thead>
				<tbody>
	
				</tbody>
			</table>
			<BR/>	
		</FIELDSET>
	</FORM>
	<img src="images/fleche-gauche.png" height="25px" width="25px"	onClick="back()">
	</BODY>
</HTML>