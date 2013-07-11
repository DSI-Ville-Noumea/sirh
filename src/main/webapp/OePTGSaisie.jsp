<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		<TITLE>Droits des pointages</TITLE>		


<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
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

		// afin d'afficher la hiérarchie des services
		function agrandirHierarchy() {

			hier = 	document.getElementById('treeHierarchy');

			if (hier.style.display!='none') {
				reduireHierarchy();
			} else {
				hier.style.display='block';
			}
		}

		// afin de cacher la hiérarchie des services
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
			<span class="sigp2Mandatory" style="width:80px">Date début : </span>
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
		
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>" value="RECHERCHERAGENTMIN">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>" value="SUPPRECHERCHERAGENTMIN">	
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>" value="RECHERCHERAGENTMAX">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>" value="SUPPRECHERCHERAGENTMAX">	
	</FORM>
	</BODY>
</HTML>