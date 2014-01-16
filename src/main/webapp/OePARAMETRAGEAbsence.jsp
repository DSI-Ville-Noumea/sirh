<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des absences</TITLE>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsence" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Motifs de refus de l'approbateur</legend>
				<span class="sigp2-titre">
				<span class="sigp2-saisie" style="position:relative;width:290px;">Libellé</span>
				<span class="sigp2-saisie" style="position:relative;">Type Absence</span>
				<SELECT name="<%= process.getNOM_LB_MOTIF_REFUS() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_MOTIF_REFUS(), process.getVAL_LB_MOTIF_REFUS_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MOTIF_REFUS()%>">
    	        	<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_MOTIF_REFUS()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_MOTIF_REFUS()!= null && !process.getVAL_ST_ACTION_MOTIF_REFUS().equals("")) {%>
            		<br>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF_REFUS() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF_REFUS() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<span class="sigp2Mandatory" style="width:110px">Famille d'absence : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE_REFUS() %>" style="width=150px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE_REFUS(), process.getVAL_LB_TYPE_ABSENCE_REFUS_SELECT()) %>
					</SELECT>					
					<br /><br />
				    <div Style="width:100%" align="center">
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF_REFUS()%>"></span>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF_REFUS()%>"></span>
					</div>
				<% } %>
			</FIELDSET>		
			
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Motifs d'alimentation manuelle des compteurs</legend>
				<span class="sigp2-titre">
				<span class="sigp2-saisie" style="position:relative;width:290px;">Libellé</span>
				<span class="sigp2-saisie" style="position:relative;">Type Absence</span>
				<SELECT name="<%= process.getNOM_LB_MOTIF_COMPTEUR() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_MOTIF_COMPTEUR(), process.getVAL_LB_MOTIF_COMPTEUR_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MOTIF_COMPTEUR()%>">
    	        	<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_MOTIF_COMPTEUR()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_MOTIF_COMPTEUR()!= null && !process.getVAL_ST_ACTION_MOTIF_COMPTEUR().equals("")) {%>
            		<br>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF_COMPTEUR() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF_COMPTEUR() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<span class="sigp2Mandatory" style="width:110px">Famille d'absence : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE_COMPTEUR() %>" style="width=150px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE_COMPTEUR(), process.getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) %>
					</SELECT>					
					<br /><br />
				    <div Style="width:100%" align="center">
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF_COMPTEUR()%>"></span>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF_COMPTEUR()%>"></span>
					</div>
				<% } %>
				
			</FIELDSET>		
			</div>	
		</FORM>
	</BODY>
</HTML>