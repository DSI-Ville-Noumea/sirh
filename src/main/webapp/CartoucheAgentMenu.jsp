<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nc.mairie.technique.VariableGlobale"%>
<%@page import="nc.mairie.metier.agent.Agent"%>
<%@page import="nc.mairie.technique.BasicProcess"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
	<META http-equiv="Content-Style-Type" content="text/css">
	<title>Référence de l'agent</title>
	<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
	<SCRIPT language="javascript" src="js/GestionMenuHaut.js"></SCRIPT>
	<SCRIPT language="JavaScript" src="js/GestionCartoucheAgent.js"></SCRIPT>
	<LINK rel="stylesheet" href="theme/menu.css" type="text/css">
</head>
<BODY>
	<FORM name="formu" method="POST" target="Main" action="GestionAgentServlet">
		<div id="refAgent" class="refAgent">
			<span style="text-decoration: underline;">Agent :</span>
			<nobr>
            <IMG name="AgentRecherche" title="Recherche d'un agent" src="images/loupe.gif" height="20" width="20" onclick='executeBouton(this);'>
            <br>
			<%
				Agent agent = (Agent)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_AGENT_MAIRIE);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			%>
			<%String serviceAgent = (String)VariableGlobale.recuperer(request,"SERVICE_AGENT");%>
			<%String res = serviceAgent; %>
            <%if (agent != null) {%>
             	<span class="sigp2" style="font-size : 11px;font-family : Arial;font-weight : bold;color : #555555;text-align: center;">
             		<%= agent.getNomUsage()%>
             		<%= agent.getPrenom()%>
             		<br>
             		mat. <%= agent.getNomatr()%>
             		<br>
             		(<%= sdf.format(agent.getDateNaissance()) %>)
             		<br>
             		<% if(serviceAgent!=null){%>
		             	<%if(serviceAgent.length()>30){
		             		res = serviceAgent.substring(0,30)+"<br/>";
		             		res += serviceAgent.substring(30,serviceAgent.length());		             	        		
		             	} %>
	             	<%} %>
	             	<%= res%>
             	</span>
	        <%}%>
	        <br>
			<%BasicProcess processMemorise = (BasicProcess)VariableGlobale.recuperer(request, "PROCESS_MEMORISE");
			String jsp="";
			if(processMemorise!=null){
			jsp = processMemorise.getJSP();
			}
			%>
	        <INPUT type="submit" style="visibility : hidden;" name="ACTION" value="OK" height="0">
			<%if(!jsp.equals("")){ %>
	        <INPUT type="hidden" name="JSP" value="<%=jsp%>" height="0">
	        <%}else{ %>
			<INPUT type="hidden" name="ACTIVITE" value="AgentRecherche">
	        <%} %>
		</nobr>
		</div>
	</FORM>
</BODY>
</html>