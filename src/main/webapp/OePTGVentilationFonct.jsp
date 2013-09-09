<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.process.pointage.OePTGVentilationUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>

    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
        <TITLE>Ventilation des fonctionnaires</TITLE>		

		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
        <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

        <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>  
        <SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
        <SCRIPT language="JavaScript">

            $(document).ready(function() {
                $('#VentilationTable').dataTable();
            });
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
        </SCRIPT>		
        <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </HEAD>
    <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGVentilationFonct" id="process" scope="session"></jsp:useBean>
        <BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">

            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <div style="margin-left:10px;margin-top:20px;text-align:left;width:1030px" align="left">
                <% if (process.onglet.equals("ONGLET1")) {%>
                <span id="titreOngletVentilation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET1');">&nbsp;Ventilation&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletVentilation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET1');">&nbsp;Ventilation&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET2")) {%>
                <span id="titreOngletHS" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET2');">&nbsp;Heures suppl�mentaires&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletHS" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET2');">&nbsp;Heures suppl�mentaires&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET3")) {%>
                <span id="titreOngletPrimes" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET3');">&nbsp;Primes&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletPrimes" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET3');">&nbsp;Primes&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET4")) {%>
                <span id="titreOngletAbs" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET4');">&nbsp;Absences&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletAbs" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET4');">&nbsp;Absences&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET5")) {%>
                <span id="titreOngletValidation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET5');">&nbsp;Validation&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletValidation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET5');">&nbsp;Validation&nbsp;</span>&nbsp;&nbsp;
                <% }%>
            </div>


            <% if (process.onglet.equals("ONGLET1")) {%>
            <div id="corpsOngletVentilation" title="Ventilation" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                <% } else {%>
                <div id="corpsOngletVentilation" title="Ventilation" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                    <% }%>
                    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                        <legend class="sigp2Legend">Ventilation des pointages des fonctionnaires</legend>
						<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Date :</span>
						<%if(process.ventilationExist()){ %>
							<input class="sigp2-saisie" disabled="disabled"  maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">				
						<%}else{ %>
							<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">
							<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
						<%} %>
                		<span class="sigp2Mandatory" style="width:50px;margin-left:50px;">Type :</span>
                        <INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_HS())%>>Heures suppl�mentaires
						<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_PRIME())%>>Primes
						<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_ABS())%>>Absences
						<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_TOUT())%>>Tout
						<BR/><BR/>
                		<span class="sigp2" style="width:50px;margin-left:20px;">Agents :</span>
				        <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT()%>">
				       <br/>
			            <%if(process.getListeAgentsVentil()!=null && process.getListeAgentsVentil().size()>0){ %>
							<div style="overflow: auto;height: 120px;width:1000px;margin-left:20px;">
								<table class="sigp2NewTab" style="text-align:left;width:980px;">
								<%
								int indiceAgent = 0;
									for (int i = 0;i<process.getListeAgentsVentil().size();i++){
								%>
										<tr id="<%=indiceAgent%>" onmouseover="SelectLigne(<%=indiceAgent%>,<%=process.getListeAgentsVentil().size()%>)" >
											<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">											
												<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT(indiceAgent)%>">
											</td>
											<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_AGENT(indiceAgent)%></td>
										</tr>
										<%
										indiceAgent++;
									}
								%>
								</table>	
							</div>
						<br/>
						<%} %>
						<BR/><BR/>
						<%if( OePTGVentilationUtils.canProcessVentilation("F")){ %>
						<INPUT type="submit" class="sigp2-Bouton-100" value="Ventiler" name="<%=process.getNOM_PB_VENTILER()%>">
						<%} %>		
                    </FIELDSET>
                </div>


                <% if (process.onglet.equals("ONGLET2")) {%>
                <div id="corpsOngletHS" title="Heures suppl�mentaires" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                <% } else {%>
                <div id="corpsOngletHS" title="Heures suppl�mentaires" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                <% }%>
                	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                            <legend class="sigp2Legend">Filtres des heures suppl�mentaires</legend>		
			                <span class="sigp2" style="width:100px">Agent min :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
							<span class="sigp2" style="width:100px">Agent max :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER_VENTIL(2)%>">
                 	</FIELDSET>
                    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                            <legend class="sigp2Legend">Visualisation de la ventilation des heures suppl�mentaires</legend>	
                            <%=process.getTabVisu()%>		
                    </FIELDSET>
                    </div>


                 <% if (process.onglet.equals("ONGLET3")) {%>
                 <div id="corpsOngletPrimes" title="Primes" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                 <% } else {%>
                 <div id="corpsOngletPrimes" title="Primes" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                 <% }%>
                	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                            <legend class="sigp2Legend">Filtres des primes</legend>		
			                <span class="sigp2" style="width:100px">Agent min :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
							<span class="sigp2" style="width:100px">Agent max :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER_VENTIL(3)%>">
                 	</FIELDSET>
                 	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                    	<legend class="sigp2Legend">Visualisation de la ventilation des primes</legend>	
                        <%=process.getTabVisu()%>		
                    </FIELDSET>
                 </div>


                <% if (process.onglet.equals("ONGLET4")) {%>
                <div id="corpsOngletAbs" title="Absences" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                <% } else {%>
                <div id="corpsOngletAbs" title="Absences" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                <% }%>
                	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                            <legend class="sigp2Legend">Filtres des absences</legend>		
			                <span class="sigp2" style="width:100px">Agent min :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
							<span class="sigp2" style="width:100px">Agent max :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER_VENTIL(1)%>">
                 	</FIELDSET>
	                <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
	                	<legend class="sigp2Legend">Visualisation de la ventilation des absences</legend>	
	                    <%=process.getTabVisu()%>		
	                </FIELDSET>
                </div>


                            <% if (process.onglet.equals("ONGLET5")) {%>
                            <div id="corpsOngletValidation" title="Validation" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                                <% } else {%>
                                <div id="corpsOngletValidation" title=""Validation"" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                                     <% }%>
                                     <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                                        <legend class="sigp2Legend">Validation de la ventilation des pointages</legend>	
                                        <%=process.getValid()%>		
                                    </FIELDSET>
                                </div>


                                <INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">

            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>" value="RECHERCHERAGENTMIN">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>" value="SUPPRECHERCHERAGENTMIN">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>" value="RECHERCHERAGENTMAX">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>" value="SUPPRECHERCHERAGENTMAX">	
	</FORM>
</BODY>
</HTML>