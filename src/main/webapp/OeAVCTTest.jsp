<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Test</TITLE>
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
 class="nc.mairie.gestionagent.process.avancement.OeAVCTTest" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Test json simple</legend>		
			<span class="sigp2" style="width:60px"><%=process.getJsonSimple() %></span>
		</FIELDSET>
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Test json liste</legend>	
		    <table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							if (process.getJsonListe()!=null){
								for (int i = 0;i<process.getJsonListe().size();i++){
									String t = process.getJsonListe().get(i);
							%>
									<tr>										
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=t %></td>
									</tr>
									<%
								}
							}%>
			</table>	
		</FIELDSET>
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Test json tableau</legend>		
		    <table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							if (process.getJsonTable()!=null){
								for (int i = 0;i<process.getJsonTable().size();i++){
									String t = process.getJsonTable().get(i);
							%>
									<tr>										
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=t%></td>
									</tr>
									<%
								}
							}%>
			</table>
		</FIELDSET>
		
	    
	</FORM>
</BODY>
</HTML>